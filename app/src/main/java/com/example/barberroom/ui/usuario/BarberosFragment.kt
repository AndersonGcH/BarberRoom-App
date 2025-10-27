package com.example.barberroom.ui.usuario

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.barberroom.adapter.BarberosAdapter
import com.example.barberroom.data.Barbero
import com.example.barberroom.databinding.FragmentBarberosBinding
import com.google.firebase.firestore.FirebaseFirestore

class BarberosFragment : Fragment() {

    private var _binding: FragmentBarberosBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val adapter by lazy { BarberosAdapter() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarberosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.barbersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.barbersRecyclerView.adapter = adapter
        cargarBarberos()
    }

    private fun cargarBarberos() {
        db.collection("barberos")
            .addSnapshotListener { snap, error ->
                if (error != null) return@addSnapshotListener
                adapter.submitList(
                    snap?.documents?.mapNotNull { it.toObject(Barbero::class.java)?.copy(id = it.id) }
                )
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}