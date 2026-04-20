package com.caminepalgym.ui.main.perfil

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caminepalgym.R

class NotificacionesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notificaciones)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        val switchRecordatorios = findViewById<Switch>(R.id.switchRecordatorios)
        val switchInforme = findViewById<Switch>(R.id.switchInforme)
        val switchLogros = findViewById<Switch>(R.id.switchLogros)
        val switchComunidad = findViewById<Switch>(R.id.switchComunidad)
        val switchPush = findViewById<Switch>(R.id.switchPush)
        val switchEmail = findViewById<Switch>(R.id.switchEmail)

        switchRecordatorios.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia recordatorios
        }

        switchInforme.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia informe
        }

        switchLogros.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia logros
        }

        switchComunidad.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia comunidad
        }

        switchPush.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia push
        }

        switchEmail.setOnCheckedChangeListener { _, isChecked ->
            // guardar preferencia email
        }
    }
}