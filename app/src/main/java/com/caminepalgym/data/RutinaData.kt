package com.caminepalgym.data

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class RutinaData(
    val id: String = "",
    @SerialName("usuario_id") val usuarioId: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val series: Int = 3,
    val repeticiones: Int = 10,
    val peso: String = "peso corporal",
    val fecha: String = "",
    val completado: Boolean = false
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class RachaData(
    val id: String = "",
    @SerialName("usuario_id") val usuarioId: String = "",
    val fecha: String = "",
    val completado: Boolean = true
)