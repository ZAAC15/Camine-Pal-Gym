package com.caminepalgym.ui.main.productos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.caminepalgym.R
import com.caminepalgym.data.ProductoData
import com.caminepalgym.ui.main.tienda.CarritoManager

class ProductAdapter(
    private var lista: List<ProductoData>
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imagen:     ImageView = view.findViewById(R.id.img_producto)
        val nombre:     TextView  = view.findViewById(R.id.producto_nombre)
        val precio:     TextView  = view.findViewById(R.id.producto_precio)
        val rating:     RatingBar = view.findViewById(R.id.producto_rating)
        val btnAgregar: Button    = view.findViewById(R.id.boton_agregar_producto)
        val tvInsignia: TextView  = view.findViewById(R.id.tvInsigniaProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = lista[position]

        holder.nombre.text = producto.nombre
        holder.precio.text = "$ ${String.format("%,.0f", producto.precio)}"
        holder.rating.rating = producto.rating.toFloat()

        // Imagen desde URL con Coil
        if (!producto.imagenUrl.isNullOrBlank()) {
            holder.imagen.load(producto.imagenUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
                transformations(RoundedCornersTransformation(8f))
            }
        } else {
            holder.imagen.setImageResource(R.drawable.ic_launcher_background)
        }

        // Badge insignia
        if (!producto.insignia.isNullOrBlank()) {
            holder.tvInsignia.visibility = View.VISIBLE
            holder.tvInsignia.text = producto.insignia
        } else {
            holder.tvInsignia.visibility = View.GONE
        }

        // Agregar al carrito
        holder.btnAgregar.setOnClickListener {
            CarritoManager.agregar(producto)
            Toast.makeText(
                holder.itemView.context,
                "${producto.nombre} agregado al carrito 🛒",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun actualizarLista(nuevaLista: List<ProductoData>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}