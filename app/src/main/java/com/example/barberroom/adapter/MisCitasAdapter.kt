package com.example.barberroom.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.barberroom.R
import com.example.barberroom.data.Cita
import com.example.barberroom.databinding.MisCitasItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MisCitasAdapter :
    ListAdapter<Cita, MisCitasAdapter.CitaViewHolder>(CitaDiffCallback()) {

    var onCancelClickListener: ((Cita) -> Unit)? = null
    var onRateClickListener: ((Cita, Float) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CitaViewHolder(
            MisCitasItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        holder.bind(getItem(position), onCancelClickListener, onRateClickListener)
    }

    inner class CitaViewHolder(private val b: MisCitasItemBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(
            c: Cita,
            cancel: ((Cita) -> Unit)?,
            rate: ((Cita, Float) -> Unit)?
        ) {
            b.tvServicioCita.text = c.servicioNombre
            b.tvBarberoCita.text = "con ${c.barberoNombre}"

            b.tvFechaCita.text =
                SimpleDateFormat("dd 'de' MMMM, yyyy - hh:mm a", Locale.getDefault())
                    .format(c.fecha.toDate())

            b.chipEstadoCita.text = c.estado
            b.chipEstadoCita.chipBackgroundColor =
                ColorStateList.valueOf(
                    when (c.estado) {
                        "Confirmada" -> R.color.color_confirmada
                        "Pendiente" -> R.color.color_pendiente
                        "Rechazada" -> R.color.color_rechazada
                        "Completada" -> R.color.color_completada
                        "Cancelada" -> R.color.color_cancelada
                        else -> android.R.color.darker_gray
                    }.let { ContextCompat.getColor(b.root.context, it) }
                )

            b.btnCancelarCita.visibility = View.GONE
            b.ratingSection.visibility = View.GONE

            when (c.estado) {
                "Confirmada", "Pendiente" -> {
                    b.btnCancelarCita.visibility = View.VISIBLE
                    b.btnCancelarCita.setOnClickListener { cancel?.invoke(c) }
                }

                "Completada" -> {
                    b.ratingSection.visibility = View.VISIBLE
                    b.rbCitaRating.rating = c.calificacion.toFloat()

                    val rated = c.calificacion > 0
                    b.rbCitaRating.setIsIndicator(rated)
                    b.btnCalificarCita.visibility = if (rated) View.GONE else View.VISIBLE

                    if (!rated) {
                        b.btnCalificarCita.setOnClickListener {
                            val r = b.rbCitaRating.rating
                            if (r > 0) rate?.invoke(c, r)
                            else Toast.makeText(b.root.context, "Selecciona una calificación", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    private class CitaDiffCallback : DiffUtil.ItemCallback<Cita>() {
        override fun areItemsTheSame(o: Cita, n: Cita) = o.id == n.id
        override fun areContentsTheSame(o: Cita, n: Cita) =
            o.estado == n.estado && o.calificacion == n.calificacion
    }
}
