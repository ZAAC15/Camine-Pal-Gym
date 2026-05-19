package com.caminepalgym.ui.main.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R
import com.caminepalgym.data.UsuarioRepository

class UsuariosAdminAdapter(
    private var lista: List<UsuarioRepository.UsuarioData>,
    private val onCambiarRol: (UsuarioRepository.UsuarioData, String) -> Unit
) : RecyclerView.Adapter<UsuariosAdminAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre:  TextView = itemView.findViewById(R.id.tvNombreUsuarioAdmin)
        val tvCorreo:  TextView = itemView.findViewById(R.id.tvCorreoUsuarioAdmin)
        val tvRol:     TextView = itemView.findViewById(R.id.tvRolUsuarioAdmin)
        val tvAvatar:  TextView = itemView.findViewById(R.id.tvAvatarUsuarioAdmin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuario_admin, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val u = lista[position]
        holder.tvNombre.text = "${u.nombre} ${u.apellidos}".trim()
        holder.tvCorreo.text = u.correo

        val iniciales = buildString {
            "${u.nombre} ${u.apellidos}".split(" ").take(2).forEach {
                append(it.firstOrNull()?.uppercaseChar() ?: "")
            }
        }.ifEmpty { "U" }
        holder.tvAvatar.text = iniciales

        val (textoRol, colorRol) = when (u.rol) {
            "pro"           -> "PRO"           to R.color.ColorPrincipal
            "administrador" -> "ADMIN"          to R.color.ColorRojo
            else            -> "BÁSICO"         to R.color.GrisTexto
        }
        holder.tvRol.text = textoRol
        holder.tvRol.setTextColor(holder.itemView.context.getColor(colorRol))

        // Cambiar rol con popup
        holder.tvRol.setOnClickListener {
            val popup = PopupMenu(holder.itemView.context, holder.tvRol)
            popup.menu.add("Básico")
            popup.menu.add("Pro")
            popup.menu.add("Administrador")
            popup.setOnMenuItemClickListener { item ->
                val nuevoRol = when (item.title.toString()) {
                    "Pro"           -> "pro"
                    "Administrador" -> "administrador"
                    else            -> "cliente"
                }
                onCambiarRol(u, nuevoRol)
                true
            }
            popup.show()
        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<UsuarioRepository.UsuarioData>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}