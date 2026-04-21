package com.caminepalgym.ui.main.entrenamiento

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R
// ── Modelo de datos ──────────────────────────────────────────────
data class Ejercicio(
    val nombre: String,
    val series: Int,
    val repeticiones: Int,
    val peso: String,        // p.ej. "80kg" o "peso corporal"
    val categoria: String    // "Empuje", "Jalón", "Pierna", "Abdomen"
)

// ── Adaptador ────────────────────────────────────────────────────
class EntrenamientoAdapter(
    private var lista: List<Ejercicio>
) : RecyclerView.Adapter<EntrenamientoAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView    = itemView.findViewById(R.id.tvNombreEjercicio)
        val tvDetalle: TextView   = itemView.findViewById(R.id.tvDetalleEjercicio)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaEjercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entrenamiento, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = lista[position]
        holder.tvNombre.text    = e.nombre
        holder.tvDetalle.text   = "${e.series} series × ${e.repeticiones} repeticiones × ${e.peso}"
        holder.tvCategoria.text = e.categoria
    }

    override fun getItemCount() = lista.size

    /** Actualiza la lista (usado al filtrar por categoría) */
    fun actualizarLista(nuevaLista: List<Ejercicio>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}
