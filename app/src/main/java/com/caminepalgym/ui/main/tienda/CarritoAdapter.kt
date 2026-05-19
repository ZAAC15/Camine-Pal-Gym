package com.caminepalgym.ui.main.tienda

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.caminepalgym.R

class CarritoAdapter(
    private var items: List<CarritoManager.ItemCarrito>,
    private val onCambio: () -> Unit
) : RecyclerView.Adapter<CarritoAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val imagen:    ImageView    = view.findViewById(R.id.imgCarritoProducto)
        val nombre:    TextView     = view.findViewById(R.id.tvCarritoNombre)
        val precio:    TextView     = view.findViewById(R.id.tvCarritoPrecio)
        val cantidad:  TextView     = view.findViewById(R.id.tvCarritoCantidad)
        val btnMas:    ImageButton  = view.findViewById(R.id.btnCarritoMas)
        val btnMenos:  ImageButton  = view.findViewById(R.id.btnCarritoMenos)
        val btnElim:   ImageButton  = view.findViewById(R.id.btnCarritoEliminar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrito, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val p = item.producto

        holder.nombre.text   = p.nombre
        holder.precio.text   = "$ ${String.format("%,.0f", p.precio * item.cantidad)}"
        holder.cantidad.text = item.cantidad.toString()

        if (!p.imagenUrl.isNullOrBlank()) {
            holder.imagen.load(p.imagenUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
            }
        } else {
            holder.imagen.setImageResource(R.drawable.ic_launcher_background)
        }

        holder.btnMas.setOnClickListener {
            CarritoManager.incrementar(p.id)
            actualizar()
            onCambio()
        }
        holder.btnMenos.setOnClickListener {
            CarritoManager.decrementar(p.id)
            actualizar()
            onCambio()
        }
        holder.btnElim.setOnClickListener {
            CarritoManager.eliminar(p.id)
            actualizar()
            onCambio()
        }
    }

    fun actualizar() {
        items = CarritoManager.obtenerItems()
        notifyDataSetChanged()
    }
}