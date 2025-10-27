package com.example.barberroom.data

data class Servicio(
    val id: String = "",
    val nombre: String = "",
    val tipo: String = "",
    val duracionMin: Int = 0,
    val precio: Double = 0.0,
    val imageUrl: String = ""
)