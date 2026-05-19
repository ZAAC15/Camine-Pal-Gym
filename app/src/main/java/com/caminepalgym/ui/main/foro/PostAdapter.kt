package com.caminepalgym.ui.main.foro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.caminepalgym.R
import com.caminepalgym.data.PostData

class PostAdapter(
    private var lista: List<PostData>,
    private val usuarioActualId: String,
    private val onLike: (PostData, Int) -> Unit,
    private val onEliminar: (PostData) -> Unit
) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    // Rastrear qué posts ya tienen like del usuario actual
    private val likesLocales = mutableSetOf<String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar:    TextView    = view.findViewById(R.id.tvAvatar)
        val usuario:   TextView    = view.findViewById(R.id.tvUsuario)
        val tiempo:    TextView    = view.findViewById(R.id.tvTiempo)
        val etiqueta:  TextView    = view.findViewById(R.id.tvEtiqueta)
        val titulo:    TextView    = view.findViewById(R.id.tvTitulo)
        val contenido: TextView    = view.findViewById(R.id.tvContenido)
        val imagen:    ImageView   = view.findViewById(R.id.imgPost)
        val btnLike:   ImageButton = view.findViewById(R.id.btnLike)
        val tvLikes:   TextView    = view.findViewById(R.id.tvLikes)
        val btnElim:   ImageButton = view.findViewById(R.id.btnEliminarPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = lista[position]
        val ctx  = holder.itemView.context

        // Avatar con iniciales
        val iniciales = post.nombre.split(" ").take(2)
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }.joinToString("")
            .ifEmpty { "U" }
        holder.avatar.text   = iniciales
        holder.usuario.text  = post.nombre
        holder.tiempo.text   = formatearFecha(post.createdAt)
        holder.etiqueta.text = post.etiqueta

        // Título
        if (!post.titulo.isNullOrBlank()) {
            holder.titulo.visibility = View.VISIBLE
            holder.titulo.text = post.titulo
        } else {
            holder.titulo.visibility = View.GONE
        }

        holder.contenido.text = post.contenido

        // Imagen
        if (!post.imagenUrl.isNullOrBlank()) {
            holder.imagen.visibility = View.VISIBLE
            holder.imagen.load(post.imagenUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
            }
        } else {
            holder.imagen.visibility = View.GONE
        }

        // Likes
        val tienelike = likesLocales.contains(post.id)
        holder.tvLikes.text = post.likes.toString()
        holder.btnLike.setImageResource(
            if (tienelike) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )
        holder.btnLike.setColorFilter(
            ctx.getColor(if (tienelike) R.color.ColorPrincipal else R.color.GrisTexto)
        )

        holder.btnLike.setOnClickListener {
            val actualPos = holder.adapterPosition
            if (actualPos == RecyclerView.NO_ID.toInt()) return@setOnClickListener
            onLike(post, actualPos)
        }

        // Botón eliminar (solo para el autor)
        if (post.usuarioId == usuarioActualId) {
            holder.btnElim.visibility = View.VISIBLE
            holder.btnElim.setOnClickListener { onEliminar(post) }
        } else {
            holder.btnElim.visibility = View.GONE
        }
    }

    fun actualizarLista(nuevaLista: List<PostData>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }

    fun marcarLike(postId: String, tieneLike: Boolean) {
        if (tieneLike) likesLocales.add(postId)
        else likesLocales.remove(postId)
    }

    private fun formatearFecha(createdAt: String?): String {
        if (createdAt == null) return "ahora"
        return try {
            val formato = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            formato.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val fecha = formato.parse(createdAt.take(19)) ?: return "ahora"
            val diff = (System.currentTimeMillis() - fecha.time) / 1000
            when {
                diff < 60      -> "ahora mismo"
                diff < 3600    -> "hace ${diff / 60} min"
                diff < 86400   -> "hace ${diff / 3600} h"
                diff < 604800  -> "hace ${diff / 86400} días"
                else           -> "hace ${diff / 604800} sem"
            }
        } catch (e: Exception) { "ahora" }
    }
}