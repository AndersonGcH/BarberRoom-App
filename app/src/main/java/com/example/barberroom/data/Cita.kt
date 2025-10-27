package com.example.barberroom.data

import com.google.firebase.Timestamp

data class Cita(
    val id: String = "",
    val userId: String = "",
    val barberoId: String = "",
    val barberoNombre: String = "",
    val servicioId: String = "",
    val servicioNombre: String = "",
    val fecha: Timestamp = Timestamp.now(),
    val estado: String = "Confirmada",
    val calificacion: Int = 0
)