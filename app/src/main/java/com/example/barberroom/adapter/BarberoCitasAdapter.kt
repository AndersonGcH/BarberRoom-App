package com.example.barberroom.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.barberroom.R
import com.example.barberroom.data.Cita
import com.example.barberroom.databinding.BarberoCitaItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class BarberoCitasAdapter : ListAdapter<Cita, BarberoCitasAdapter.BarberCitaViewHolder>(CitaDiffCallback()) {
    var onAcceptClickListener: ((Cita) -> Unit)? = null
    var onRejectClickListener: ((Cita) -> Unit)? = null
    var onCompleteClickListener: ((Cita) -> Unit)? = null
    var onCancelClickListener: ((Cita) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarberCitaViewHolder {
        val binding = BarberoCitaItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarberCitaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarberCitaViewHolder, position: Int) {
        val currentCita = getItem(position)
        holder.bind(currentCita, onAcceptClickListener, onRejectClickListener, onCompleteClickListener, onCancelClickListener)
    }

    class BarberCitaViewHolder(private val binding: BarberoCitaItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cita: Cita, onAccept: ((Cita) -> Unit)?, onReject: ((Cita) -> Unit)?, onComplete: ((Cita) -> Unit)?, onCancel: ((Cita) -> Unit)?) {
            binding.tvServicioCitaBarber.text = cita.servicioNombre
            val sdf = SimpleDateFormat("dd 'de' MMMM, yyyy - hh:mm a", Locale.getDefault())
            binding.tvFechaCitaBarber.text = sdf.format(cita.fecha.toDate())

            binding.chipEstadoCitaBarber.text = cita.estado

            val context = binding.root.context
            val color = when (cita.estado) {
                "Confirmada" -> ContextCompat.getColor(context, R.color.color_confirmada)
                "Pendiente" -> ContextCompat.getColor(context, R.color.color_pendiente)
                "Rechazada" -> ContextCompat.getColor(context, R.color.color_rechazada)
                "Completada" -> ContextCompat.getColor(context, R.color.color_completada)
                "Cancelada" -> ContextCompat.getColor(context, R.color.color_cancelada)
                else -> Color.GRAY
            }
            binding.chipEstadoCitaBarber.chipBackgroundColor = ColorStateList.valueOf(color)

            binding.llPendienteActions.visibility = View.GONE
            binding.llConfirmadaActions.visibility = View.GONE

            when (cita.estado) {
                "Pendiente" -> {
                    binding.llPendienteActions.visibility = View.VISIBLE
                    binding.btnAceptarCita.setOnClickListener { onAccept?.invoke(cita) }
                    binding.btnRechazarCita.setOnClickListener { onReject?.invoke(cita) }
                }
                "Confirmada" -> {
                    binding.llConfirmadaActions.visibility = View.VISIBLE
                    binding.btnCompletarCita.setOnClickListener { onComplete?.invoke(cita) }
                    binding.btnCancelarCitaBarber.setOnClickListener { onCancel?.invoke(cita) }
                }
            }
        }
    }

    private class CitaDiffCallback : DiffUtil.ItemCallback<Cita>() {
        override fun areItemsTheSame(oldItem: Cita, newItem: Cita): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cita, newItem: Cita): Boolean {
            return oldItem == newItem
        }
    }
}