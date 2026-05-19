package com.caminepalgym.ui.main.entrenamiento

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R

data class Ejercicio(
    val id: String = "",
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val peso: String,
    val categoria: String,
    val completado: Boolean = false
)

class EntrenamientoAdapter(
    private var lista: List<Ejercicio>,
    private val onCompletar: (Ejercicio) -> Unit
) : RecyclerView.Adapter<EntrenamientoAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView    = itemView.findViewById(R.id.tvNombreEjercicio)
        val tvDetalle: TextView   = itemView.findViewById(R.id.tvDetalleEjercicio)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaEjercicio)
        val btnCompletar: Button  = itemView.findViewById(R.id.btnCompletarEjercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entrenamiento, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = lista[position]
        holder.tvNombre.text  = e.nombre
        holder.tvDetalle.text = "${e.series} series × ${e.repeticiones} reps × ${e.peso}"
        holder.tvCategoria.text = e.categoria

        if (e.completado) {
            holder.btnCompletar.text = "✓ Completado"
            holder.btnCompletar.backgroundTintList =
                ContextCompat.getColorStateList(holder.itemView.context, R.color.ColorVerde)
            holder.btnCompletar.isEnabled = false
            holder.tvNombre.paintFlags = holder.tvNombre.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.btnCompletar.text = "Completar"
            holder.btnCompletar.backgroundTintList =
                ContextCompat.getColorStateList(holder.itemView.context, R.color.ColorPrincipal)
            holder.btnCompletar.isEnabled = true
            holder.tvNombre.paintFlags = holder.tvNombre.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.btnCompletar.setOnClickListener { onCompletar(e) }
        }
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<Ejercicio>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}