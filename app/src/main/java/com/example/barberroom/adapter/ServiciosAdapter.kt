package com.example.barberroom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.barberroom.R
import com.example.barberroom.data.Servicio
import com.example.barberroom.databinding.ServicioItemBinding

class ServiciosAdapter :
    ListAdapter<Servicio, ServiciosAdapter.ViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ServicioItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(h: ViewHolder, pos: Int) =
        h.bind(getItem(pos))

    inner class ViewHolder(private val b: ServicioItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(s: Servicio) {
            b.tvServicioNombre.text = s.nombre
            b.tvServicioPrecio.text = "S/ %.2f".format(s.precio)
            b.tvServicioDuracion.text = "${s.duracionMin} min"

            Glide.with(b.ivServicioFoto.context)
                .load(s.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(b.ivServicioFoto)
        }
    }

    private class Diff : DiffUtil.ItemCallback<Servicio>() {
        override fun areItemsTheSame(o: Servicio, n: Servicio) = o.id == n.id
        override fun areContentsTheSame(o: Servicio, n: Servicio) = o == n
    }
}