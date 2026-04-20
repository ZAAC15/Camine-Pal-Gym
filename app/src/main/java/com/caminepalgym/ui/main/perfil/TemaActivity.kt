package com.caminepalgym.ui.main.perfil

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caminepalgym.R

class TemaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tema)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<android.view.View>(R.id.colorDorado).setOnClickListener { }
        findViewById<android.view.View>(R.id.colorAzul).setOnClickListener { }
        findViewById<android.view.View>(R.id.colorVerde).setOnClickListener { }
        findViewById<android.view.View>(R.id.colorMorado).setOnClickListener { }
        findViewById<android.view.View>(R.id.colorNaranja).setOnClickListener { }
        findViewById<android.view.View>(R.id.colorRojo).setOnClickListener { }

        val btnSmall = findViewById<Button>(R.id.btnSmall)
        val btnMedium = findViewById<Button>(R.id.btnMedium)
        val btnLarge = findViewById<Button>(R.id.btnLarge)

        btnSmall.setOnClickListener { }
        btnMedium.setOnClickListener { }
        btnLarge.setOnClickListener { }
    }
}