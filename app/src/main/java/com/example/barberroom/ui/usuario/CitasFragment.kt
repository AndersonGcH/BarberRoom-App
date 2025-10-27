package com.example.barberroom.ui.usuario

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.barberroom.R
import com.example.barberroom.data.Barbero
import com.example.barberroom.data.Cita
import com.example.barberroom.data.Servicio
import com.example.barberroom.databinding.FragmentCitasBinding
import com.example.barberroom.ui.activity.MainActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CitasFragment : Fragment() {

    private var _binding: FragmentCitasBinding? = null
    private val binding get() = _binding!!
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val auth by lazy { FirebaseAuth.getInstance() }
    private var barberosList = listOf<Barbero>()
    private var serviciosList = listOf<Servicio>()
    private var selectedBarbero: Barbero? = null
    private var selectedServicio: Servicio? = null
    private val selectedDateTime = Calendar.getInstance()
    private var isDateSelected = false
    private var isTimeSelected = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCitasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadBarberos()
        loadServicios()
        binding.btnSeleccionarFecha.setOnClickListener { showDatePicker() }
        binding.btnSeleccionarHora.setOnClickListener { showTimePicker() }
        binding.btnConfirmarCita.setOnClickListener { saveCita() }
    }

    private fun loadBarberos() {
        db.collection("barberos").get().addOnSuccessListener { documents ->
            barberosList = documents.mapNotNull { doc ->
                doc.toObject(Barbero::class.java)?.copy(id = doc.id)
            }
            val nombres = barberosList.map { it.nombre }
            binding.actvBarbero.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres))
            binding.actvBarbero.setOnItemClickListener { _, _, pos, _ -> selectedBarbero = barberosList[pos] }
        }.addOnFailureListener { Log.e("CitasFragment", "Error al cargar barberos", it) }
    }

    private fun loadServicios() {
        db.collection("servicios").get().addOnSuccessListener { documents ->
            serviciosList = documents.mapNotNull { doc ->
                doc.toObject(Servicio::class.java)?.copy(id = doc.id)
            }
            val nombres = serviciosList.map { "${it.nombre} - S/ ${it.precio}" }
            binding.actvServicio.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, nombres))
            binding.actvServicio.setOnItemClickListener { _, _, pos, _ -> selectedServicio = serviciosList[pos] }
        }.addOnFailureListener { Log.e("CitasFragment", "Error al cargar servicios", it) }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, y, m, d ->
            selectedDateTime.set(Calendar.YEAR, y)
            selectedDateTime.set(Calendar.MONTH, m)
            selectedDateTime.set(Calendar.DAY_OF_MONTH, d)
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.tvFechaSeleccionada.text = "Fecha: ${format.format(selectedDateTime.time)}"
            binding.tvFechaSeleccionada.visibility = View.VISIBLE
            isDateSelected = true
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
            show()
        }
    }

    private fun showTimePicker() {
        val c = Calendar.getInstance()
        TimePickerDialog(requireContext(), { _, h, min ->
            selectedDateTime.set(Calendar.HOUR_OF_DAY, h)
            selectedDateTime.set(Calendar.MINUTE, min)
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            binding.tvHoraSeleccionada.text = "Hora: ${format.format(selectedDateTime.time)}"
            binding.tvHoraSeleccionada.visibility = View.VISIBLE
            isTimeSelected = true
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }

    private fun saveCita() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "Debes iniciar sesión para registrar una cita", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedBarbero == null || selectedServicio == null) {
            Toast.makeText(requireContext(), "Selecciona un barbero y un servicio", Toast.LENGTH_SHORT).show()
            return
        }
        if (!isDateSelected || !isTimeSelected) {
            Toast.makeText(requireContext(), "Selecciona una fecha y una hora", Toast.LENGTH_LONG).show()
            return
        }
        
        val nuevaCita = Cita(
            userId = user.uid,
            barberoId = selectedBarbero!!.id,
            barberoNombre = selectedBarbero!!.nombre,
            servicioId = selectedServicio!!.id,
            servicioNombre = selectedServicio!!.nombre,
            fecha = Timestamp(selectedDateTime.time),
            estado = "Pendiente"
        )

        db.collection("citas").add(nuevaCita).addOnSuccessListener {
            Toast.makeText(requireContext(), "Cita registrada con éxito.", Toast.LENGTH_LONG).show()
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, MisCitasFragment())
                .commit()
            (activity as? MainActivity)?.updateBottomNavSelection(R.id.navegacion_miscita)
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Error al registrar la cita", Toast.LENGTH_LONG).show()
            Log.e("CitasFragment", "Error al guardar cita", it)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}