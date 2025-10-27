package com.example.barberroom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.barberroom.databinding.CarruselItemBinding

class CarruselAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<CarruselAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: CarruselItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Int) = binding.carruselImageView.setImageResource(image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(CarruselItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(images[position])

    override fun getItemCount() = images.size
}