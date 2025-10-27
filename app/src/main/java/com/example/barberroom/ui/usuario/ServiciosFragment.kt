package com.example.barberroom.ui.usuario

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barberroom.adapter.ServiciosAdapter
import com.example.barberroom.data.Servicio
import com.example.barberroom.databinding.FragmentServiciosBinding
import com.google.firebase.firestore.FirebaseFirestore

class ServiciosFragment : Fragment() {

    private var _binding: FragmentServiciosBinding? = null
    private val binding get() = _binding!!

    private val db by lazy { FirebaseFirestore.getInstance() }
    private val adapter by lazy { ServiciosAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentServiciosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.serviciosRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.serviciosRecyclerView.adapter = adapter
        cargarServicios()
    }

    private fun cargarServicios() {
        db.collection("servicios")
            .addSnapshotListener { snap, error ->
                if (error != null) return@addSnapshotListener
                adapter.submitList(
                    snap?.documents?.mapNotNull { it.toObject(Servicio::class.java)?.copy(id = it.id) }
                )
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}