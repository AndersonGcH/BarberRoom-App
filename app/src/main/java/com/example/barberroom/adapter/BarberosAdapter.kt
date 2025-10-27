package com.example.barberroom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.barberroom.R
import com.example.barberroom.data.Barbero
import com.example.barberroom.databinding.BarberoItemBinding

class BarberosAdapter :
    ListAdapter<Barbero, BarberosAdapter.BarberoViewHolder>(BarberoDiff()) {

    private var onItemClickListener: ((Barbero) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        BarberoViewHolder(
            BarberoItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: BarberoViewHolder, position: Int) {
        val barbero = getItem(position)
        holder.itemView.setOnClickListener { onItemClickListener?.invoke(barbero) }
        holder.bind(barbero)
    }

    inner class BarberoViewHolder(private val binding: BarberoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(barbero: Barbero) {
            binding.tvBarberoNombre.text = barbero.nombre
            binding.tvBarberoEspecializacion.text = "Especialista en: " + barbero.especializacion
            binding.tvBarberoRatingValue.text = "%.1f".format(barbero.rating)
            binding.rbBarberoRating.rating = barbero.rating.toFloat()
            binding.tvBarberoTotalReviews.text = "(${barbero.totalReviews})"

            Glide.with(binding.ivBarberoFoto.context)
                .load(barbero.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.ivBarberoFoto)
        }
    }

    private class BarberoDiff : DiffUtil.ItemCallback<Barbero>() {
        override fun areItemsTheSame(old: Barbero, new: Barbero) = old.id == new.id
        override fun areContentsTheSame(old: Barbero, new: Barbero) = old == new
    }
}