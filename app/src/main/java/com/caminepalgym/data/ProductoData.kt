package com.caminepalgym.data

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class ProductoData(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String? = null,
    val precio: Double = 0.0,
    val categoria: String = "Suplementos",
    val insignia: String? = null,
    @SerialName("imagen_url") val imagenUrl: String? = null,
    val rating: Double = 4.0,
    val stock: Int = 0,
    val activo: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)