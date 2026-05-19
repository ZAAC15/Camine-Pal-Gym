package com.caminepalgym.ui.main.foro

// Esta clase ya no se usa directamente (reemplazada por PostData de Supabase)
// Se mantiene solo si hay referencias legacy en el proyecto
@Deprecated("Usar PostData de com.caminepalgym.data en su lugar")
data class Post(
    val usuario: String,
    val contenido: String,
    val imagen: Int
)