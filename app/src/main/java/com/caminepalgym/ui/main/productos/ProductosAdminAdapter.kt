package com.caminepalgym.ui.main.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.caminepalgym.R
import com.caminepalgym.data.ProductoData

class ProductosAdminAdapter(
    private var lista: List<ProductoData>,
    private val onEliminar: (ProductoData) -> Unit
) : RecyclerView.Adapter<ProductosAdminAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre:    TextView    = view.findViewById(R.id.tvNombreProductoAdmin)
        val tvCategoria: TextView    = view.findViewById(R.id.tvCategoriaProductoAdmin)
        val tvPrecio:    TextView    = view.findViewById(R.id.tvPrecioProductoAdmin)
        val tvInsignia:  TextView    = view.findViewById(R.id.tvInsigniaProductoAdmin)
        val btnEliminar: ImageButton = view.findViewById(R.id.btnEliminarProductoAdmin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto_admin, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = lista[position]
        holder.tvNombre.text    = p.nombre
        holder.tvCategoria.text = p.categoria
        holder.tvPrecio.text    = "$ ${String.format("%,.0f", p.precio)}"
        holder.tvInsignia.text  = p.insignia ?: ""
        holder.tvInsignia.visibility = if (p.insignia.isNullOrBlank()) View.GONE else View.VISIBLE
        holder.btnEliminar.setOnClickListener { onEliminar(p) }
    }

    fun actualizarLista(nuevaLista: List<ProductoData>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}