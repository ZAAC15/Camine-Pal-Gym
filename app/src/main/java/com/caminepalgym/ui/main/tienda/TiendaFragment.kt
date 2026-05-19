package com.caminepalgym.ui.main.tienda

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R
import com.caminepalgym.data.ProductoData
import com.caminepalgym.data.ProductoRepository
import com.caminepalgym.ui.main.productos.ProductAdapter
import kotlinx.coroutines.launch

class TiendaFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var adapter: ProductAdapter
    private var todosLosProductos = listOf<ProductoData>()
    private var categoriaActual = "Todos"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_tienda, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ── RecyclerView ───────────────────────────────────────────────
        recycler = view.findViewById(R.id.recycler_tienda)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = ProductAdapter(emptyList())
        recycler.adapter = adapter

        // ── Carrito ────────────────────────────────────────────────────
        view.findViewById<ImageButton>(R.id.btnCarrito).setOnClickListener {
            startActivity(Intent(requireContext(), CarritoActivity::class.java))
        }

        // ── Tiendas cercanas ───────────────────────────────────────────
        val irATiendas = {
            startActivity(Intent(requireContext(), TiendasCercanasActivity::class.java))
        }
        view.findViewById<Button>(R.id.btnConsultarTiendas).setOnClickListener { irATiendas() }
        view.findViewById<ImageButton>(R.id.btnUbicacion).setOnClickListener { irATiendas() }

        // ── Buscador ───────────────────────────────────────────────────
        view.findViewById<EditText>(R.id.etBuscarTienda)
            .addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { filtrarLista(s.toString()) }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

        // ── Botones de categoría ───────────────────────────────────────
        val btnTodos       = view.findViewById<Button>(R.id.btnCatTodos)
        val btnSuplementos = view.findViewById<Button>(R.id.btnCatSuplementos)
        val btnEquipo      = view.findViewById<Button>(R.id.btnCatEquipo)
        val btnRopa        = view.findViewById<Button>(R.id.btnCatRopa)

        btnTodos.setOnClickListener       { seleccionarCategoria("Todos",       btnTodos,       listOf(btnSuplementos, btnEquipo, btnRopa)) }
        btnSuplementos.setOnClickListener { seleccionarCategoria("Suplementos", btnSuplementos, listOf(btnTodos, btnEquipo, btnRopa)) }
        btnEquipo.setOnClickListener      { seleccionarCategoria("Equipo",      btnEquipo,      listOf(btnTodos, btnSuplementos, btnRopa)) }
        btnRopa.setOnClickListener        { seleccionarCategoria("Ropa",        btnRopa,        listOf(btnTodos, btnSuplementos, btnEquipo)) }

        seleccionarCategoria("Todos", btnTodos, listOf(btnSuplementos, btnEquipo, btnRopa))

        cargarProductos()
    }

    private fun cargarProductos() {
        lifecycleScope.launch {
            try {
                todosLosProductos = ProductoRepository.obtenerProductos()
                filtrarLista("")
            } catch (e: Exception) {
                Toast.makeText(requireContext(),
                    "Error cargando productos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun seleccionarCategoria(categoria: String, btnActivo: Button, btnOtros: List<Button>) {
        categoriaActual = categoria
        btnActivo.backgroundTintList = requireContext().getColorStateList(R.color.ColorPrincipal)
        btnActivo.setTextColor(requireContext().getColor(R.color.background))
        btnOtros.forEach { btn ->
            btn.backgroundTintList = requireContext().getColorStateList(R.color.FondoGris)
            btn.setTextColor(requireContext().getColor(android.R.color.white))
        }
        filtrarLista(
            try { requireView().findViewById<EditText>(R.id.etBuscarTienda).text.toString() }
            catch (e: Exception) { "" }
        )
    }

    private fun filtrarLista(query: String) {
        val porCategoria = if (categoriaActual == "Todos") todosLosProductos
        else todosLosProductos.filter { it.categoria == categoriaActual }
        val resultado = if (query.isBlank()) porCategoria
        else porCategoria.filter {
            it.nombre.contains(query, ignoreCase = true) ||
                    it.descripcion?.contains(query, ignoreCase = true) == true
        }
        adapter.actualizarLista(resultado)
    }

    override fun onResume() {
        super.onResume()
        cargarProductos()
    }
}