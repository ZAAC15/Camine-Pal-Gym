package com.caminepalgym.ui.main.entrenamiento

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R
import com.caminepalgym.data.RutinaRepository
import kotlinx.coroutines.launch

class AgregarEjercicioActivity : AppCompatActivity() {

    private val ejerciciosPredeterminados = listOf(
        // Empuje
        Ejercicio("", "Press de banca",        4, 10, "80kg",          "Empuje"),
        Ejercicio("", "Press inclinado",        3, 10, "60kg",          "Empuje"),
        Ejercicio("", "Press militar",          3, 10, "40kg",          "Empuje"),
        Ejercicio("", "Fondos en paralelas",    3, 12, "peso corporal", "Empuje"),
        Ejercicio("", "Extensión de trícep",    3, 12, "20kg",          "Empuje"),
        Ejercicio("", "Aperturas con mancuerna",3, 12, "15kg",          "Empuje"),
        // Jalón
        Ejercicio("", "Dominadas",              4, 8,  "peso corporal", "Jalón"),
        Ejercicio("", "Jalón al pecho",         4, 10, "60kg",          "Jalón"),
        Ejercicio("", "Remo con barra",         4, 8,  "80kg",          "Jalón"),
        Ejercicio("", "Curl de bíceps",         3, 12, "15kg",          "Jalón"),
        Ejercicio("", "Peso muerto",            4, 6,  "120kg",         "Jalón"),
        Ejercicio("", "Remo en polea",          3, 12, "50kg",          "Jalón"),
        // Pierna
        Ejercicio("", "Sentadillas",            4, 10, "100kg",         "Pierna"),
        Ejercicio("", "Prensa de pierna",       4, 12, "120kg",         "Pierna"),
        Ejercicio("", "Zancadas",               3, 10, "60kg",          "Pierna"),
        Ejercicio("", "Extensión de cuádriceps",3, 15, "40kg",          "Pierna"),
        Ejercicio("", "Curl femoral",           3, 12, "35kg",          "Pierna"),
        Ejercicio("", "Elevación de pantorrilla",4, 20, "60kg",         "Pierna"),
        // Abdomen
        Ejercicio("", "Plancha",                4, 1,  "60 seg",        "Abdomen"),
        Ejercicio("", "Crunch",                 3, 20, "peso corporal", "Abdomen"),
        Ejercicio("", "Elevación de piernas",   3, 15, "peso corporal", "Abdomen"),
        Ejercicio("", "Russian twist",          3, 20, "10kg",          "Abdomen"),
        Ejercicio("", "Rueda abdominal",        3, 10, "peso corporal", "Abdomen")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_ejercicio)

        findViewById<android.widget.ImageView>(R.id.ivBack).setOnClickListener { finish() }

        val recycler = findViewById<RecyclerView>(R.id.recyclerEjerciciosPredeterminados)
        recycler.layoutManager = LinearLayoutManager(this)

        val adapter = AgregarEjercicioAdapter(ejerciciosPredeterminados) { ejercicio ->
            lifecycleScope.launch {
                try {
                    RutinaRepository.agregarEjercicio(
                        nombre = ejercicio.nombre,
                        categoria = ejercicio.categoria,
                        series = ejercicio.series,
                        repeticiones = ejercicio.repeticiones,
                        peso = ejercicio.peso
                    )
                    runOnUiThread {
                        Toast.makeText(this@AgregarEjercicioActivity,
                            "${ejercicio.nombre} agregado", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@AgregarEjercicioActivity,
                            "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        recycler.adapter = adapter
    }
}