package com.example.barberroom.ui.usuario

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.barberroom.R
import com.example.barberroom.adapter.MisCitasAdapter
import com.example.barberroom.data.Barbero
import com.example.barberroom.data.Cita
import com.example.barberroom.databinding.FragmentMisCitasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class MisCitasFragment : Fragment() {

    private var _binding: FragmentMisCitasBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var misCitasAdapter: MisCitasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMisCitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        misCitasAdapter = MisCitasAdapter().apply {
            onCancelClickListener = { cancelarCita(it) }
            onRateClickListener = { cita, rating -> calificarCita(cita, rating) }
        }

        binding.misCitasRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = misCitasAdapter
        }

        cargarCitasFirebase()
    }

    private fun cargarCitasFirebase() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            misCitasAdapter.submitList(emptyList())
            return
        }

        listenerRegistration = db.collection("citas")
            .whereEqualTo("userId", user.uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                val citas = snap?.documents?.mapNotNull { d ->
                    d.toObject(Cita::class.java)?.copy(id = d.id)
                } ?: emptyList()
                if (isAdded) misCitasAdapter.submitList(citas)
            }
    }

    private fun cancelarCita(cita: Cita) {
        db.collection("citas").document(cita.id)
            .update("estado", "Cancelada")
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Cita cancelada", Toast.LENGTH_SHORT).show()
            }
    }

    private fun calificarCita(cita: Cita, rating: Float) {
        val cRef = db.collection("citas").document(cita.id)
        val bRef = db.collection("barberos").document(cita.barberoId)

        db.runTransaction { t ->
            val b = t.get(bRef).toObject(Barbero::class.java)
                ?: throw FirebaseFirestoreException("", FirebaseFirestoreException.Code.NOT_FOUND)

            val newReviews = b.totalReviews + 1
            val newRating = ((b.rating * b.totalReviews) + rating) / newReviews

            t.update(cRef, mapOf("calificacion" to rating, "estado" to "Completada"))
            t.update(bRef, mapOf("rating" to newRating, "totalReviews" to newReviews))
        }.addOnSuccessListener {
            Toast.makeText(requireContext(), "Cita calificada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        listenerRegistration?.remove()
        _binding = null
        super.onDestroyView()
    }
}