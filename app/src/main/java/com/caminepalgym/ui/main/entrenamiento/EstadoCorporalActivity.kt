package com.caminepalgym.ui.main.entrenamiento

import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caminepalgym.R
import com.caminepalgym.data.RutinaRepository
import com.caminepalgym.data.UsuarioRepository
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class EstadoCorporalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estado_corporal)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        cargarDatos()
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            try {
                val medidas = UsuarioRepository.obtenerMedidas()
                val racha   = RutinaRepository.obtenerRachaActual()

                runOnUiThread {

                    // ── Overall Fitness Score ──
                    // BMI (40pts) + Racha (30pts) + Nivel condición (30pts)
                    var score = 0

                    // Puntos por BMI
                    val bmi = medidas?.bmi ?: 0.0
                    val puntoBmi = when {
                        bmi == 0.0  -> 0
                        bmi < 18.5  -> 20
                        bmi < 25.0  -> 40  // Normal = máximo
                        bmi < 30.0  -> 25
                        else        -> 10
                    }
                    score += puntoBmi

                    // Puntos por racha (máx 30 con 30 + días)
                    val puntoRacha = minOf(racha * 3, 30)
                    score += puntoRacha

                    // Puntos por nivel condición
                    val puntoNivel = when (medidas?.nivelCondicion) {
                        "Principiante" -> 10
                        "Intermedio"   -> 20
                        "Avanzado"     -> 30
                        else           -> 0
                    }
                    score += puntoNivel

                    findViewById<TextView>(R.id.tvScore).text         = score.toString()
                    findViewById<TextView>(R.id.tvScoreCirculo).text  = score.toString()

                    val puntosTexto = when {
                        racha == 0  -> "Sin racha aún"
                        racha == 1  -> "+3 pts hoy 🔥"
                        else        -> "+${minOf(racha * 3, 30)} pts de racha 🔥"
                    }
                    findViewById<TextView>(R.id.tvPuntosScore).text = puntosTexto

                    // ── Masa corporal ─────────────────────────────────────
                    val peso = medidas?.peso
                    val tvMasa = findViewById<TextView>(R.id.tvMasaCorporal)
                    if (peso != null && peso > 0) {
                        val pesoTexto = if (peso % 1.0 == 0.0) "${peso.toInt()} kg" else "${"%.1f".format(peso)} kg"
                        tvMasa.text = pesoTexto

                        // Categoría IMC para las barras
                        val categoriaBmi = when {
                            bmi < 18.5 -> 1
                            bmi < 25.0 -> 2
                            bmi < 30.0 -> 3
                            else       -> 4
                        }
                        actualizarBarrasBMI(categoriaBmi)
                    } else {
                        tvMasa.text = "-- kg"
                        actualizarBarrasBMI(0)
                    }

                    // ── Definición (basada en % grasa) ──
                    val grasa = medidas?.porcentajeGrasa
                    val tvDefCirculo = findViewById<TextView>(R.id.tvDefinicionCirculo)
                    val tvDefLabel   = findViewById<TextView>(R.id.tvDefLabel)
                    val tvDefSub     = findViewById<TextView>(R.id.tvDefSub)

                    if (grasa != null && grasa > 0) {
                        // Definición inversa al % grasa (menos grasa = más definición)
                        val definicion = when {
                            grasa < 10 -> 95
                            grasa < 15 -> 85
                            grasa < 20 -> 72
                            grasa < 25 -> 58
                            grasa < 30 -> 42
                            else       -> 25
                        }
                        tvDefCirculo.text = "$definicion%"
                        val (label, color) = when {
                            definicion >= 80 -> "Excelente" to getColor(R.color.ColorVerde)
                            definicion >= 60 -> "Bueno"     to getColor(R.color.ColorVerde)
                            definicion >= 40 -> "Regular"   to getColor(R.color.ColorPrincipal)
                            else             -> "Bajo"      to getColor(R.color.ColorRojo)
                        }
                        tvDefLabel.text = label
                        tvDefLabel.setTextColor(color)
                        tvDefSub.text = "${grasa.toInt()}% grasa corporal"
                    } else {
                        tvDefCirculo.text = "--"
                        tvDefLabel.text   = "Sin datos"
                        tvDefLabel.setTextColor(getColor(R.color.GrisTexto))
                        tvDefSub.text     = "Agrega % de grasa"
                    }

                    // ── Simetría ──
                    // Calculada con nivel condición + BMI normal
                    val simetria = when {
                        medidas == null -> 0
                        else -> {
                            var s = 50
                            if (bmi in 18.5..24.9) s += 20
                            s += when (medidas.nivelCondicion) {
                                "Principiante" -> 10
                                "Intermedio"   -> 20
                                "Avanzado"     -> 30
                                else           -> 0
                            }
                            minOf(s, 100)
                        }
                    }
                    findViewById<TextView>(R.id.tvSimetria).text = "$simetria%"
                    findViewById<ProgressBar>(R.id.progressSimetria).progress = simetria

                    // ── Volumen ──
                    // Estimado: peso * (1 - porcentajeGrasa/100) = masa magra
                    val tvVolumen = findViewById<TextView>(R.id.tvVolumen)
                    if (peso != null && peso > 0) {
                        val masaMagra = if (grasa != null && grasa > 0) {
                            peso * (1 - grasa / 100)
                        } else {
                            peso * 0.75 // estimado sin datos de grasa
                        }
                        tvVolumen.text = "${"%.1f".format(masaMagra)} kg"
                    } else {
                        tvVolumen.text = "-- kg"
                    }

                    // ── Consistencia / Racha ──
                    val tvConsistencia = findViewById<TextView>(R.id.tvConsistencia)
                    tvConsistencia.text = when (racha) {
                        0    -> "Sin racha aún"
                        1    -> "1 día de racha 🔥"
                        else -> "$racha días de racha 🔥"
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    findViewById<TextView>(R.id.tvScore).text        = "0"
                    findViewById<TextView>(R.id.tvScoreCirculo).text = "0"
                    findViewById<TextView>(R.id.tvConsistencia).text = "Sin datos"
                }
            }
        }
    }

    private fun actualizarBarrasBMI(categoria: Int) {
        val barras = listOf(
            R.id.barraBmi1, R.id.barraBmi2,
            R.id.barraBmi3, R.id.barraBmi4
        )
        barras.forEachIndexed { index, id ->
            val barra = findViewById<android.view.View>(id)
            barra?.setBackgroundColor(
                if (index < categoria) getColor(R.color.ColorPrincipal)
                else getColor(R.color.background)
            )
        }
    }
}