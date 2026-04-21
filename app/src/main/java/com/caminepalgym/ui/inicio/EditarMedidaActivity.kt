package com.caminepalgym.ui.inicio

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caminepalgym.R
import android.widget.ImageView

class EditarMedidaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_medida)

        val btnBack = findViewById<ImageView>(R.id.ivBack)

        btnBack.setOnClickListener {
            finish()
        }
    }
}