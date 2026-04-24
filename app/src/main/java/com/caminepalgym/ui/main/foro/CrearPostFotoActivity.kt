package com.caminepalgym.ui.main.foro

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caminepalgym.R
import android.content.Intent
import android.widget.ImageButton
import android.widget.TextView

class CrearPostFotoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_post)

        // Boton volver
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // TAB FOTO abrir otro activity
        val tabFoto = findViewById<TextView>(R.id.tabFoto)
        tabFoto.setOnClickListener {
            val intent = Intent(this, CrearPostFotoActivity::class.java)
            startActivity(intent)
            finish()
        }

        // TAB TEXTO
        val tabTexto = findViewById<TextView>(R.id.tabTexto)
        tabTexto.setOnClickListener {
        }

        // BOTON PUBLICAR
        val btnPublicar = findViewById<android.widget.Button>(R.id.btnPublicar)
        btnPublicar.setOnClickListener {
        }
    }
}