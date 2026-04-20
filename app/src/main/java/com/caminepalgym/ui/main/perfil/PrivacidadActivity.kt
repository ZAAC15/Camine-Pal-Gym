package com.caminepalgym.ui.main.perfil

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caminepalgym.R

class PrivacidadActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_privacidad)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val switchPerfilPublico = findViewById<Switch>(R.id.switchPerfilPublico)
        val switchActividadAmigos = findViewById<Switch>(R.id.switchActividadAmigos)
        val switchDatosAnalisis = findViewById<Switch>(R.id.switchDatosAnalisis)
        val switchUbicacion = findViewById<Switch>(R.id.switchUbicacion)

        switchPerfilPublico.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia perfil público
        }

        switchActividadAmigos.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia actividad amigos
        }

        switchDatosAnalisis.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia datos análisis
        }

        switchUbicacion.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia ubicación
        }

        findViewById<Button>(R.id.btnDescargarDatos).setOnClickListener {
            // lógica para descargar datos
        }

        findViewById<Button>(R.id.btnBorrarCuenta).setOnClickListener {
            // lógica para borrar cuenta
        }
    }
}