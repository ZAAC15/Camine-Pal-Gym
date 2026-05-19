package com.caminepalgym.ui.main.entrenamiento

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R
import com.caminepalgym.data.RutinaRepository
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class EntrenamientoFragment : Fragment() {

    private lateinit var recyclerEntrenamientos: RecyclerView
    private lateinit var fabAgregar: ExtendedFloatingActionButton
    private lateinit var adapter: EntrenamientoAdapter

    private lateinit var chipTodos: Button
    private lateinit var chipEmpuje: Button
    private lateinit var chipJalon: Button
    private lateinit var chipPierna: Button
    private lateinit var chipAbdomen: Button

    private var listaCompleta = listOf<Ejercicio>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_entrenamiento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerEntrenamientos = view.findViewById(R.id.recyclerEntrenamientos)
        recyclerEntrenamientos.layoutManager = LinearLayoutManager(requireContext())

        adapter = EntrenamientoAdapter(
            lista = emptyList(),
            onCompletar = { ejercicio ->
            // Callback cuando se marca como completado
            lifecycleScope.launch {
                try {
                    RutinaRepository.marcarCompletado(ejercicio.id)
                    cargarRutina()
                    Toast.makeText(requireContext(), "¡Ejercicio completado! 🔥", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        )
        recyclerEntrenamientos.adapter = adapter

        // FAB
        fabAgregar = view.findViewById(R.id.fabAgregar)
        fabAgregar.setOnClickListener {
            startActivity(Intent(requireContext(), AgregarEjercicioActivity::class.java))
        }

        // Chips
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

        seleccionarChip("Todos")
        cargarRutina()
    }

    override fun onResume() {
        super.onResume()
        cargarRutina()
    }
    private var rutinaGenerada = false

    private fun cargarRutina() {
        if (rutinaGenerada) {
            // Solo recargar sin generar
            lifecycleScope.launch {
                val rutina = RutinaRepository.obtenerRutinaHoy()
                listaCompleta = rutina.map {
                    Ejercicio(
                        id = it.id,
                        nombre = it.nombre,
                        series = it.series,
                        repeticiones = it.repeticiones,
                        peso = it.peso,
                        categoria = it.categoria,
                        completado = it.completado
                    )
                }
                requireActivity().runOnUiThread {
                    adapter.actualizarLista(listaCompleta)
                }
            }
            return
        }

        lifecycleScope.launch {
            try {
                RutinaRepository.generarRutinaAutomatica()
                rutinaGenerada = true

                val rutina = RutinaRepository.obtenerRutinaHoy()
                listaCompleta = rutina.map {
                    Ejercicio(
                        id = it.id,
                        nombre = it.nombre,
                        series = it.series,
                        repeticiones = it.repeticiones,
                        peso = it.peso,
                        categoria = it.categoria,
                        completado = it.completado
                    )
                }
                requireActivity().runOnUiThread {
                    adapter.actualizarLista(listaCompleta)
                }
            } catch (e: Exception) {
                requireActivity().runOnUiThread {
                    adapter.actualizarLista(emptyList())
                }
            }
        }
    }

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

        val filtrada = if (categoria == "Todos") listaCompleta
        else listaCompleta.filter { it.categoria == categoria }
        adapter.actualizarLista(filtrada)
    }
}