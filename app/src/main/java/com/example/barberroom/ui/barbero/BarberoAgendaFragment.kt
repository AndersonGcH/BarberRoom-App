package com.example.barberroom.ui.barbero

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barberroom.adapter.BarberoCitasAdapter
import com.example.barberroom.data.Cita
import com.example.barberroom.databinding.FragmentBarberoAgendaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class BarberoAgendaFragment : Fragment() {

    private var _binding: FragmentBarberoAgendaBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val adapter by lazy { BarberoCitasAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarberoAgendaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.barberCitasRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.barberCitasRecyclerView.adapter = adapter
        adapterEventos()
        cargarCitasBarbero()
    }

    private fun adapterEventos() {
        adapter.onAcceptClickListener = { cambiarEstado(it, "Confirmada") }
        adapter.onRejectClickListener = { cambiarEstado(it, "Rechazada") }
        adapter.onCompleteClickListener = { cambiarEstado(it, "Completada") }
        adapter.onCancelClickListener = { cambiarEstado(it, "Cancelada") }
    }

    private fun cambiarEstado(cita: Cita, estado: String) {
        if (cita.id.isEmpty()) return
        db.collection("citas").document(cita.id)
            .update("estado", estado)
            .addOnSuccessListener {
                val lista = adapter.currentList.toMutableList()
                val index = lista.indexOfFirst { it.id == cita.id }
                if (index != -1) {
                    lista[index] = lista[index].copy(estado = estado)
                    adapter.submitList(lista)
                }
                Toast.makeText(requireContext(), "Cita $estado", Toast.LENGTH_SHORT).show()
            }
    }

    private fun cargarCitasBarbero() {
        val barbero = auth.currentUser ?: return
        db.collection("citas")
            .whereEqualTo("barberoId", barbero.uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) return@addSnapshotListener
                adapter.submitList(
                    snap?.documents?.mapNotNull { it.toObject(Cita::class.java)?.copy(id = it.id) }
                )
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}