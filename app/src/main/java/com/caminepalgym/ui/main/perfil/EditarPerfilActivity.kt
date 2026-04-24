package com.caminepalgym.ui.main.perfil

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caminepalgym.R

class EditarPerfilActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_perfil)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageButton>(R.id.btnGuardarHeader).setOnClickListener {
            guardarCambios()
        }

        findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener {
            guardarCambios()
        }
    }

    private fun guardarCambios() {
        finish()
    }
}