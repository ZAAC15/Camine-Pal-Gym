package com.caminepalgym.data

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class MedidasData(
    val id: String,
    val altura: Double? = null,
    val peso: Double? = null,
    val edad: Int? = null,
    val sexo: String? = null,
    @SerialName("nivel_condicion") val nivelCondicion: String? = null,
    @SerialName("tipo_cuerpo") val tipoCuerpo: String? = null,
    val objetivo: String? = null,
    @SerialName("frecuencia_actividad") val frecuenciaActividad: Int? = null,
    @SerialName("porcentaje_grasa") val porcentajeGrasa: Double? = null,
    val bmi: Double? = null
)