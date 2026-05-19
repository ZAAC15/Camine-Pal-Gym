package com.caminepalgym.ui.inicio

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import com.caminepalgym.data.MedidasData
import com.caminepalgym.data.UsuarioRepository
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class EditarMedidaActivity : AppCompatActivity() {

    private var sexoSeleccionado: String? = null
    private var objetivoSeleccionado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_medida)

        // Views
        val ivBack          = findViewById<android.widget.ImageView>(R.id.ivBack)
        val etAltura        = findViewById<TextInputEditText>(R.id.etAltura)
        val etPeso          = findViewById<TextInputEditText>(R.id.etPeso)
        val etEdad          = findViewById<TextInputEditText>(R.id.etEdad)
        val etGrasa         = findViewById<TextInputEditText>(R.id.etGrasa)
        val btnMasculino    = findViewById<Button>(R.id.btnMasculino)
        val btnFemenino     = findViewById<Button>(R.id.btnFemenino)
        val actvNivel       = findViewById<AutoCompleteTextView>(R.id.actvNivel)
        val actvTipoCuerpo  = findViewById<AutoCompleteTextView>(R.id.actvTipoCuerpo)
        val btnGanarPeso    = findViewById<Button>(R.id.btnGanarPeso)
        val btnPerderPeso   = findViewById<Button>(R.id.btnPerderPeso)
        val btnMantenerse   = findViewById<Button>(R.id.btnMantenerse)
        val actvFrecuencia  = findViewById<AutoCompleteTextView>(R.id.actvFrecuencia)
        val btnGuardar      = findViewById<Button>(R.id.btnGuardarMedidas)

        ivBack.setOnClickListener { finish() }

        // Dropdowns
        val niveles = listOf("Principiante", "Intermedio", "Avanzado")
        actvNivel.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, niveles)
        )

        val tiposCuerpo = listOf("Ectomorfo", "Mesomorfo", "Endomorfo")
        actvTipoCuerpo.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, tiposCuerpo)
        )

        val frecuencias = listOf("1 día", "2 días", "3 días", "4 días", "5 días", "6 días", "7 días")
        actvFrecuencia.setAdapter(
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, frecuencias)
        )

        // Sexo
        btnMasculino.setOnClickListener {
            sexoSeleccionado = "Masculino"
            seleccionarBoton(btnMasculino, btnFemenino)
        }
        btnFemenino.setOnClickListener {
            sexoSeleccionado = "Femenino"
            seleccionarBoton(btnFemenino, btnMasculino)
        }

        // Objetivo
        val botonesObjetivo = listOf(
            btnGanarPeso  to "Ganar masa muscular",
            btnPerderPeso to "Perder peso",
            btnMantenerse to "Mantenerme"
        )
        botonesObjetivo.forEach { (btn, valor) ->
            btn.setOnClickListener {
                objetivoSeleccionado = valor
                botonesObjetivo.forEach { (b, _) -> deseleccionarBoton(b) }
                seleccionarBotonObjetivo(btn)
            }
        }

        // Cargar datos existentes
        cargarDatos(
            etAltura, etPeso, etEdad, etGrasa,
            btnMasculino, btnFemenino,
            actvNivel, actvTipoCuerpo, actvFrecuencia,
            botonesObjetivo
        )

        // Guardar
        btnGuardar.setOnClickListener {
            guardarDatos(
                etAltura, etPeso, etEdad, etGrasa,
                actvNivel, actvTipoCuerpo, actvFrecuencia
            )
        }
    }

    private fun cargarDatos(
        etAltura: TextInputEditText,
        etPeso: TextInputEditText,
        etEdad: TextInputEditText,
        etGrasa: TextInputEditText,
        btnMasculino: Button,
        btnFemenino: Button,
        actvNivel: AutoCompleteTextView,
        actvTipoCuerpo: AutoCompleteTextView,
        actvFrecuencia: AutoCompleteTextView,
        botonesObjetivo: List<Pair<Button, String>>
    ) {
        lifecycleScope.launch {
            try {
                val medidas = UsuarioRepository.obtenerMedidas() ?: return@launch

                runOnUiThread {
                    // Campos de texto
                    medidas.altura?.let { etAltura.setText(
                        if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
                    )}
                    medidas.peso?.let { etPeso.setText(
                        if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
                    )}
                    medidas.edad?.let { etEdad.setText(it.toString()) }

                    // Sexo
                    when (medidas.sexo) {
                        "Masculino" -> {
                            sexoSeleccionado = "Masculino"
                            seleccionarBoton(btnMasculino, btnFemenino)
                        }
                        "Femenino" -> {
                            sexoSeleccionado = "Femenino"
                            seleccionarBoton(btnFemenino, btnMasculino)
                        }
                    }

                    // Nivel
                    medidas.nivelCondicion?.let { actvNivel.setText(it, false) }

                    // Tipo cuerpo (opcional, puede ser null)
                    medidas.tipoCuerpo?.let { actvTipoCuerpo.setText(it, false) }

                    // Objetivo
                    medidas.objetivo?.let { obj ->
                        objetivoSeleccionado = obj
                        botonesObjetivo.forEach { (btn, valor) ->
                            if (valor == obj) seleccionarBotonObjetivo(btn)
                            else deseleccionarBoton(btn)
                        }
                    }

                    // Frecuencia
                    medidas.frecuenciaActividad?.let { freq ->
                        val texto = if (freq == 1) "1 día" else "$freq días"
                        actvFrecuencia.setText(texto, false)
                    }
                }
            } catch (e: Exception) {
                // Si no hay datos previos se queda en blanco
            }
        }
    }

    private fun guardarDatos(
        etAltura: TextInputEditText,
        etPeso: TextInputEditText,
        etEdad: TextInputEditText,
        etGrasa: TextInputEditText,
        actvNivel: AutoCompleteTextView,
        actvTipoCuerpo: AutoCompleteTextView,
        actvFrecuencia: AutoCompleteTextView
    ) {
        val alturaStr = etAltura.text.toString().trim()
        val pesoStr   = etPeso.text.toString().trim()
        val edadStr   = etEdad.text.toString().trim()
        val nivel     = actvNivel.text.toString().trim()
        val frecStr   = actvFrecuencia.text.toString().trim()

        if (alturaStr.isEmpty() || pesoStr.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Altura, peso y edad son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        if (sexoSeleccionado == null) {
            Toast.makeText(this, "Selecciona tu sexo biológico", Toast.LENGTH_SHORT).show()
            return
        }
        if (objetivoSeleccionado == null) {
            Toast.makeText(this, "Selecciona tu objetivo", Toast.LENGTH_SHORT).show()
            return
        }
        if (nivel.isEmpty()) {
            Toast.makeText(this, "Selecciona tu nivel de condición", Toast.LENGTH_SHORT).show()
            return
        }
        if (frecStr.isEmpty()) {
            Toast.makeText(this, "Selecciona tu frecuencia", Toast.LENGTH_SHORT).show()
            return
        }

        val altura    = alturaStr.toDoubleOrNull() ?: 0.0
        val peso      = pesoStr.toDoubleOrNull() ?: 0.0
        val edad      = edadStr.toIntOrNull() ?: 0
        val grasa     = etGrasa.text.toString().trim().toDoubleOrNull()
        val tipoCuerpo = actvTipoCuerpo.text.toString().trim().ifEmpty { null }
        val frecuencia = frecStr.replace(" día", "").replace("s", "").toIntOrNull() ?: 1

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
                        tipoCuerpo = tipoCuerpo,
                        objetivo = objetivoSeleccionado,
                        frecuenciaActividad = frecuencia,
                        bmi = bmiRedondeado,
                        porcentajeGrasa = grasa
                    )
                )

                runOnUiThread {
                    Toast.makeText(this@EditarMedidaActivity, "¡Datos guardados!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@EditarMedidaActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun seleccionarBoton(seleccionado: Button, deseleccionado: Button) {
        seleccionado.backgroundTintList = ContextCompat.getColorStateList(this, R.color.ColorPrincipal)
        seleccionado.setTextColor(ContextCompat.getColor(this, R.color.black))
        deseleccionado.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Gris2)
        deseleccionado.setTextColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun seleccionarBotonObjetivo(btn: Button) {
        btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.ColorPrincipal)
        btn.setTextColor(ContextCompat.getColor(this, R.color.black))
    }

    private fun deseleccionarBoton(btn: Button) {
        btn.backgroundTintList = ContextCompat.getColorStateList(this, R.color.Gris2)
        btn.setTextColor(ContextCompat.getColor(this, R.color.white))
    }
}