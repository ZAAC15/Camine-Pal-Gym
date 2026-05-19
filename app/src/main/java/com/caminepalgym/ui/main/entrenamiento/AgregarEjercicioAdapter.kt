package com.caminepalgym.ui.main.entrenamiento

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R

class AgregarEjercicioAdapter(
    private val lista: List<Ejercicio>,
    private val onAgregar: (Ejercicio) -> Unit
) : RecyclerView.Adapter<AgregarEjercicioAdapter.VH>() {

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView    = itemView.findViewById(R.id.tvNombreEjercicio)
        val tvDetalle: TextView   = itemView.findViewById(R.id.tvDetalleEjercicio)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaEjercicio)
        val btnAgregar: Button    = itemView.findViewById(R.id.btnAgregarEjercicio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_agregar_ejercicio, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e = lista[position]
        holder.tvNombre.text    = e.nombre
        holder.tvDetalle.text   = "${e.series} series × ${e.repeticiones} reps × ${e.peso}"
        holder.tvCategoria.text = e.categoria
        holder.btnAgregar.setOnClickListener { onAgregar(e) }
    }

    override fun getItemCount() = lista.size
}