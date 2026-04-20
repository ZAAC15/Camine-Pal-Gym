package com.caminepalgym.ui.main.tienda

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R
import com.caminepalgym.ui.main.productos.ProductAdapter
import com.caminepalgym.ui.main.productos.Producto


class TiendaFragment : Fragment() {

    private lateinit var recycler: RecyclerView

    private lateinit var recyclerCategorias: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_tienda, container, false)

        recycler = view.findViewById(R.id.recycler_tienda)

        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        val listaProductos = listOf(

            Producto("Proteina Whey", 190.990, R.drawable.whey, 4.8f),
            Producto("Creatina", 100.990, R.drawable.creatina, 4.6f),
            Producto("Bandas resistencia", 59.990, R.drawable.bandas, 4.3f),
            Producto("Guantes gym", 39.990, R.drawable.guantes, 4.1f)

        )

        recycler.adapter = ProductAdapter(listaProductos)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCarrito = view.findViewById<ImageButton>(R.id.btnCarrito)

        btnCarrito.setOnClickListener {

            startActivity(Intent(requireContext(), CarritoActivity::class.java))

        }
    }
}