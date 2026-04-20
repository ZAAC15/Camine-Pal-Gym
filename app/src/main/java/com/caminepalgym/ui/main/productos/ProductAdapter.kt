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
import com.caminepalgym.R

class ProductAdapter(
    private val lista: List<Producto>
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){

        val imagen: ImageView = view.findViewById(R.id.img_producto)
        val nombre: TextView = view.findViewById(R.id.producto_nombre)
        val precio: TextView = view.findViewById(R.id.producto_precio)
        val btn_agregar: Button = itemView.findViewById(R.id.boton_agregar_producto)

        val rating: RatingBar = view.findViewById(R.id.producto_rating)

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
        holder.precio.text = "$${producto.precio}"
        holder.imagen.setImageResource(producto.imagenRes)
        holder.rating.rating = producto.rating
        holder.btn_agregar.setOnClickListener {
            Toast.makeText(
                holder.itemView.context,
                "${producto.nombre} agregado al carrito",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}