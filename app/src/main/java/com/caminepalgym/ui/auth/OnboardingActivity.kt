package com.caminepalgym.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.caminepalgym.MainActivity
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import com.caminepalgym.data.MedidasData
import com.caminepalgym.data.UsuarioRepository
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {

    private var sexoSeleccionado: String? = null
    private var objetivoSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val etAltura     = findViewById<TextInputEditText>(R.id.etAltura)
        val etPeso       = findViewById<TextInputEditText>(R.id.etPeso)
        val etEdad       = findViewById<TextInputEditText>(R.id.etEdad)
        val btnMasculino = findViewById<Button>(R.id.btnMasculino)
        val btnFemenino  = findViewById<Button>(R.id.btnFemenino)
        val actvNivel    = findViewById<AutoCompleteTextView>(R.id.actvNivel)
        val btnGanarPeso = findViewById<Button>(R.id.btnGanarPeso)
        val btnPerderPeso= findViewById<Button>(R.id.btnPerderPeso)
        val btnMantenerse= findViewById<Button>(R.id.btnMantenerse)
        val actvFrecuencia = findViewById<AutoCompleteTextView>(R.id.actvFrecuencia)
        val btnComenzar  = findViewById<Button>(R.id.btnComenzar)

        // Dropdown nivel
        val niveles = listOf("Principiante", "Intermedio", "Avanzado")
        actvNivel.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, niveles)
        )

        // Dropdown frecuencia
        val frecuencias = listOf("1 día", "2 días", "3 días", "4 días", "5 días", "6 días", "7 días")
        actvFrecuencia.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, frecuencias)
        )

        // Sexo
        btnMasculino.setOnClickListener {
            sexoSeleccionado = "Masculino"
            btnMasculino.backgroundTintList = ContextCompat.getColorStateList(this, R.color.ColorPrincipal)
            btnMasculino.setTextColor(ContextCompat.getColor(this, R.color.black))
            btnFemenino.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Gris2)
            btnFemenino.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        btnFemenino.setOnClickListener {
            sexoSeleccionado = "Femenino"
            btnFemenino.backgroundTintList = ContextCompat.getColorStateList(this, R.color.ColorPrincipal)
            btnFemenino.setTextColor(ContextCompat.getColor(this, R.color.black))
            btnMasculino.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Gris2)
            btnMasculino.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        // Objetivo
        val botonesObjetivo = listOf(
            btnGanarPeso to "Ganar masa muscular",
            btnPerderPeso to "Perder peso",
            btnMantenerse to "Mantenerme"
        )

        botonesObjetivo.forEach { (btn, valor) ->
            btn.setOnClickListener {
                objetivoSeleccionado = valor
                botonesObjetivo.forEach { (b, _) ->
                    b.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Gris2)
                    b.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
                btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.ColorPrincipal)
                btn.setTextColor(ContextCompat.getColor(this, R.color.black))
            }
        }

        // Guardar
        btnComenzar.setOnClickListener {
            val alturaStr  = etAltura.text.toString().trim()
            val pesoStr    = etPeso.text.toString().trim()
            val edadStr    = etEdad.text.toString().trim()
            val nivel      = actvNivel.text.toString().trim()
            val frecStr    = actvFrecuencia.text.toString().trim()

            if (alturaStr.isEmpty() || pesoStr.isEmpty() || edadStr.isEmpty()) {
                Toast.makeText(this, "Completa altura, peso y edad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (sexoSeleccionado == null) {
                Toast.makeText(this, "Selecciona tu sexo biológico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (objetivoSeleccionado == null) {
                Toast.makeText(this, "Selecciona tu objetivo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (nivel.isEmpty()) {
                Toast.makeText(this, "Selecciona tu nivel de condición", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (frecStr.isEmpty()) {
                Toast.makeText(this, "Selecciona tu frecuencia de entrenamiento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val altura = alturaStr.toDoubleOrNull() ?: 0.0
            val peso   = pesoStr.toDoubleOrNull() ?: 0.0
            val edad   = edadStr.toIntOrNull() ?: 0
            val frecuencia = frecStr.replace(" día", "").replace("s", "").toIntOrNull() ?: 1

            // Calcular BMI
            val alturaMetros = altura / 100.0
            val bmi = if (alturaMetros > 0) peso / (alturaMetros * alturaMetros) else 0.0
            val bmiRedondeado = Math.round(bmi * 10.0) / 10.0

            lifecycleScope.launch {
                try {
                    val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return@launch

                    UsuarioRepository.guardarMedidas(
                        MedidasData(
                            id = userId,
                            altura = altura,
                            peso = peso,
                            edad = edad,
                            sexo = sexoSeleccionado,
                            nivelCondicion = nivel,
                            objetivo = objetivoSeleccionado,
                            frecuenciaActividad = frecuencia,
                            bmi = bmiRedondeado
                        )
                    )

                    runOnUiThread {
                        startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
                        finish()
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@OnboardingActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}