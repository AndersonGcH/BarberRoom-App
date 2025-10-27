package com.example.barberroom.ui.barbero

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.barberroom.R
import com.example.barberroom.databinding.FragmentBarberoPerfilBinding
import com.example.barberroom.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BarberoPerfilFragment : Fragment() {
    private var _binding: FragmentBarberoPerfilBinding? = null
    private val binding get() = _binding!!
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarberoPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarPerfil()
        binding.btnCerrarSesionBarber.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    private fun cargarPerfil() {
        val id = auth.currentUser?.uid ?: return
        db.collection("barberos").document(id).get()
            .addOnSuccessListener { doc ->
                if (!isAdded || _binding == null || !doc.exists()) return@addOnSuccessListener
                val nombre = doc.getString("nombre")
                val especial = doc.getString("especializacion")
                val url = doc.getString("imageUrl")
                val rating = doc.getDouble("rating") ?: 0.0
                val reviews = doc.getLong("totalReviews") ?: 0

                binding.tvBarberName.text = nombre
                binding.tvBarberSpecialization.text = "Especialista en $especial"
                binding.rbBarberRating.rating = rating.toFloat()
                binding.tvBarberRatingValue.text = "%.1f".format(rating)
                binding.tvBarberTotalReviews.text = "($reviews Reseñas)"

                Glide.with(this).load(url)
                    .placeholder(R.drawable.ic_perfil)
                    .error(R.drawable.ic_perfil)
                    .into(binding.ivBarberProfile)
            }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}