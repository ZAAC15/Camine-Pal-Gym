package com.caminepalgym.ui.main.perfil

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caminepalgym.R

class TemaActivity : AppCompatActivity() {

    private val PREFS_NAME   = "tema_prefs"
    private val KEY_COLOR    = "color_seleccionado"
    private val KEY_FUENTE   = "fuente_seleccionada"  // "small" | "medium" | "large"

    // Todos los colores disponibles
    private val colores = listOf("dorado", "azul", "verde", "morado", "naranja", "rojo")

    private lateinit var vistas: Map<String, ImageView>
    private lateinit var btnSmall: Button
    private lateinit var btnMedium: Button
    private lateinit var btnLarge: Button

    private var colorActual  = "dorado"
    private var fuenteActual = "medium"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tema)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        colorActual  = prefs.getString(KEY_COLOR,  "dorado")  ?: "dorado"
        fuenteActual = prefs.getString(KEY_FUENTE, "medium")  ?: "medium"

        // ── Referencias colores ───────────────────────────────────────
        vistas = mapOf(
            "dorado"  to findViewById(R.id.colorDorado),
            "azul"    to findViewById(R.id.colorAzul),
            "verde"   to findViewById(R.id.colorVerde),
            "morado"  to findViewById(R.id.colorMorado),
            "naranja" to findViewById(R.id.colorNaranja),
            "rojo"    to findViewById(R.id.colorRojo)
        )

        // ── Referencias botones fuente ────────────────────────────────
        btnSmall  = findViewById(R.id.btnSmall)
        btnMedium = findViewById(R.id.btnMedium)
        btnLarge  = findViewById(R.id.btnLarge)

        // ── Aplicar estado inicial ────────────────────────────────────
        actualizarSeleccionColor(colorActual, guardar = false)
        actualizarSeleccionFuente(fuenteActual, guardar = false)

        // ── Listeners colores ─────────────────────────────────────────
        colores.forEach { color ->
            vistas[color]?.setOnClickListener {
                actualizarSeleccionColor(color, guardar = true)
                prefs.edit().putString(KEY_COLOR, color).apply()
                Toast.makeText(this,
                    "Color ${color.replaceFirstChar { it.uppercase() }} seleccionado\n" +
                            "(se aplicará en una próxima actualización)",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // ── Listeners fuente ──────────────────────────────────────────
        btnSmall.setOnClickListener {
            actualizarSeleccionFuente("small", guardar = true)
            prefs.edit().putString(KEY_FUENTE, "small").apply()
            Toast.makeText(this, "Fuente pequeña seleccionada", Toast.LENGTH_SHORT).show()
        }
        btnMedium.setOnClickListener {
            actualizarSeleccionFuente("medium", guardar = true)
            prefs.edit().putString(KEY_FUENTE, "medium").apply()
            Toast.makeText(this, "Fuente mediana seleccionada", Toast.LENGTH_SHORT).show()
        }
        btnLarge.setOnClickListener {
            actualizarSeleccionFuente("large", guardar = true)
            prefs.edit().putString(KEY_FUENTE, "large").apply()
            Toast.makeText(this, "Fuente grande seleccionada", Toast.LENGTH_SHORT).show()
        }

        // ── Botón atrás ───────────────────────────────────────────────
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }


    private fun actualizarSeleccionColor(color: String, guardar: Boolean) {
        colorActual = color
        colores.forEach { c ->
            val vista = vistas[c] ?: return@forEach
            if (c == color) {
                // Mostrar check y agrandar ligeramente
                vista.setImageResource(android.R.drawable.checkbox_on_background)
                vista.scaleX = 1.15f
                vista.scaleY = 1.15f
                // Borde blanco para indicar selección
                vista.alpha = 1.0f
            } else {
                // Sin check, tamaño normal, un poco transparente
                vista.setImageDrawable(null)
                vista.scaleX = 1.0f
                vista.scaleY = 1.0f
                vista.alpha = 0.65f
            }
        }
    }


    private fun actualizarSeleccionFuente(fuente: String, guardar: Boolean) {
        fuenteActual = fuente

        // Resetear los tres
        listOf(btnSmall, btnMedium, btnLarge).forEach { btn ->
            btn.setBackgroundColor(getColor(R.color.background))
            btn.setTextColor(getColor(R.color.white))
        }

        // Resaltar el seleccionado
        val btnSeleccionado = when (fuente) {
            "small"  -> btnSmall
            "large"  -> btnLarge
            else     -> btnMedium
        }
        btnSeleccionado.setBackgroundColor(getColor(R.color.ColorPrincipal))
        btnSeleccionado.setTextColor(getColor(R.color.background))
    }
}