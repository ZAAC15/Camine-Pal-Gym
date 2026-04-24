package com.caminepalgym.ui.main.foro

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caminepalgym.R

class CrearPostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_post)

        // BOTON VOLVER
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        // VISTAS
        val tabTexto = findViewById<TextView>(R.id.tabTexto)
        val tabFoto = findViewById<TextView>(R.id.tabFoto)

        val boxImagen = findViewById<LinearLayout>(R.id.boxImagen)
        val inputContenido = findViewById<EditText>(R.id.inputContenido)

        val btnPublicar = findViewById<Button>(R.id.btnPublicar)

        // TAB TEXTO
        tabTexto.setOnClickListener {
            tabTexto.setBackgroundResource(R.drawable.bg_chip_selected)
            tabTexto.setTextColor(getColor(android.R.color.black))

            tabFoto.setBackgroundResource(android.R.color.transparent)
            tabFoto.setTextColor(getColor(R.color.white))

            boxImagen.visibility = View.GONE
        }

        // TAB FOTO
        tabFoto.setOnClickListener {
            tabFoto.setBackgroundResource(R.drawable.bg_chip_selected)
            tabFoto.setTextColor(getColor(android.R.color.black))

            tabTexto.setBackgroundResource(android.R.color.transparent)
            tabTexto.setTextColor(getColor(R.color.white))

            boxImagen.visibility = View.VISIBLE
        }

        // BOTON PUBLICAR
        btnPublicar.setOnClickListener {
            Toast.makeText(this, "Post creado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}