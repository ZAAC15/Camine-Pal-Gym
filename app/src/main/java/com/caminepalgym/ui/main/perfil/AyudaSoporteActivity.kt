package com.caminepalgym.ui.main.perfil

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caminepalgym.R

class AyudaSoporteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ayuda_soporte)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnContactarSoporte).setOnClickListener {
            // lógica para contactar soporte
        }

        findViewById<Button>(R.id.btnForo).setOnClickListener {
            // lógica para ir al foro
        }

        // FAQs expandibles
        setupFaq(R.id.faq1)
        setupFaq(R.id.faq2)
        setupFaq(R.id.faq3)
        setupFaq(R.id.faq4)
        setupFaq(R.id.faq5)
    }

    private fun setupFaq(faqId: Int) {
        val faq = findViewById<LinearLayout>(faqId)
        // El faq1 tiene una respuesta visible por defecto, los demás se pueden expandir
        faq.setOnClickListener {
            // aquí puedes agregar lógica de expandir/colapsar
        }
    }
}