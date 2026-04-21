package com.caminepalgym.ui.main.entrenamiento

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class EntrenamientoFragment : Fragment() {

    private lateinit var recyclerEntrenamientos: RecyclerView
    private lateinit var fabAgregar: ExtendedFloatingActionButton

    private lateinit var chipTodos: Button
    private lateinit var chipEmpuje: Button
    private lateinit var chipJalon: Button
    private lateinit var chipPierna: Button
    private lateinit var chipAbdomen: Button

    // ── Lista completa de ejercicios (datos de ejemplo) ──────────
    private val listaCompleta = listOf(
        Ejercicio("Press de banca",  4, 10, "80kg",          "Empuje"),
        Ejercicio("Sentadillas",     4, 10, "120kg",         "Pierna"),
        Ejercicio("Peso muerto",     6,  6, "140kg",         "Jalón"),
        Ejercicio("Dominadas",       4, 12, "peso corporal", "Jalón"),
        Ejercicio("Press militar",   3, 10, "40kg",          "Empuje"),
        Ejercicio("Curl de bíceps",  3, 12, "15kg",          "Jalón"),
        Ejercicio("Extensión tricep",3, 12, "20kg",          "Empuje"),
        Ejercicio("Zancadas",        3, 10, "60kg",          "Pierna"),
        Ejercicio("Plancha",         4,  1, "60 seg",        "Abdomen"),
        Ejercicio("Crunch",          3, 20, "peso corporal", "Abdomen")
    )

    private lateinit var adapter: EntrenamientoAdapter

    // ─────────────────────────────────────────────────────────────
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_entrenamiento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView + adaptador
        recyclerEntrenamientos = view.findViewById(R.id.recyclerEntrenamientos)
        recyclerEntrenamientos.layoutManager = LinearLayoutManager(requireContext())
        adapter = EntrenamientoAdapter(listaCompleta)
        recyclerEntrenamientos.adapter = adapter

        // FAB
        fabAgregar = view.findViewById(R.id.fabAgregar)
        fabAgregar.setOnClickListener {
            // aquí abres el dialog o activity para agregar entrenamiento
        }

        // Chips / filtros
        chipTodos   = view.findViewById(R.id.chipTodos)
        chipEmpuje  = view.findViewById(R.id.chipEmpuje)
        chipJalon   = view.findViewById(R.id.chipJalon)
        chipPierna  = view.findViewById(R.id.chipPierna)
        chipAbdomen = view.findViewById(R.id.chipAbdomen)

        chipTodos.setOnClickListener   { seleccionarChip("Todos") }
        chipEmpuje.setOnClickListener  { seleccionarChip("Empuje") }
        chipJalon.setOnClickListener   { seleccionarChip("Jalón") }
        chipPierna.setOnClickListener  { seleccionarChip("Pierna") }
        chipAbdomen.setOnClickListener { seleccionarChip("Abdomen") }

        // Seleccionar "Todos" por defecto
        seleccionarChip("Todos")
    }

    // ── Lógica de chips ──────────────────────────────────────────
    private fun seleccionarChip(categoria: String) {
        val chips      = listOf(chipTodos, chipEmpuje, chipJalon, chipPierna, chipAbdomen)
        val categorias = listOf("Todos", "Empuje", "Jalón", "Pierna", "Abdomen")

        chips.forEachIndexed { index, chip ->
            if (categorias[index] == categoria) {
                chip.setBackgroundColor(requireContext().getColor(R.color.ColorPrincipal))
                chip.setTextColor(requireContext().getColor(R.color.background))
            } else {
                chip.setBackgroundColor(requireContext().getColor(R.color.FondoGris))
                chip.setTextColor(requireContext().getColor(R.color.white))
            }
        }

        filtrarPorCategoria(categoria)
    }

    private fun filtrarPorCategoria(categoria: String) {
        val filtrada = if (categoria == "Todos") {
            listaCompleta
        } else {
            listaCompleta.filter { it.categoria == categoria }
        }
        adapter.actualizarLista(filtrada)
    }
}