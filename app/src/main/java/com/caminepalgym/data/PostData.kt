package com.caminepalgym.data

import android.annotation.SuppressLint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class PostData(
    val id: String = "",
    @SerialName("usuario_id") val usuarioId: String = "",
    val nombre: String = "",
    val titulo: String? = null,
    val contenido: String = "",
    @SerialName("imagen_url") val imagenUrl: String? = null,
    val etiqueta: String = "General",
    val likes: Int = 0,
    @SerialName("created_at") val createdAt: String? = null
)