package com.caminepalgym.ui.main.perfil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caminepalgym.R

class AyudaSoporteActivity : AppCompatActivity() {

    // Mapa de respuestas para cada FAQ
    private val respuestas = mapOf(
        R.id.faq1 to "Accede al Panel de Control para ver tu estado físico completo, incluyendo masa muscular, definición, simetría y actividad semanal. También puedes añadir mediciones desde la pantalla de inicio con el botón +.",
        R.id.faq2 to "Actualmente la sincronización con otras aplicaciones no está disponible. Estamos trabajando en integración con Google Fit y Apple Health para próximas versiones.",
        R.id.faq3 to "Ve a Perfil → Suscripción → Cancelar suscripción. El plan seguirá activo hasta el final del período ya pagado. Si tienes problemas, contacta con soporte técnico.",
        R.id.faq4 to "En la pantalla de inicio de sesión, toca '¿Olvidaste tu contraseña?'. Recibirás un correo electrónico con un enlace para restablecerla. Revisa también tu carpeta de spam.",
        R.id.faq5 to "Sí. Todos tus datos se almacenan de forma encriptada en servidores seguros. Nunca compartimos tu información personal con terceros. Puedes consultar nuestra política de privacidad para más detalles."
    )

    // Rastrear cuál FAQ está abierta (null = ninguna)
    private var faqAbierta: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ayuda_soporte)

        // ── Botón atrás ───────────────────────────────────────────────
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // ── FAQs ──────────────────────────────────────────────────────
        // faq1 ya viene con su TextView de respuesta en el XML (el único expandido por defecto)
        // Los demás (faq2-faq5) son LinearLayout horizontales sin TextView de respuesta,
        // así que se los añadimos dinámicamente al hacer clic.
        setupFaq(R.id.faq1, tieneRespuestaEnXml = true,  abrirPorDefecto = true)
        setupFaq(R.id.faq2, tieneRespuestaEnXml = false, abrirPorDefecto = false)
        setupFaq(R.id.faq3, tieneRespuestaEnXml = false, abrirPorDefecto = false)
        setupFaq(R.id.faq4, tieneRespuestaEnXml = false, abrirPorDefecto = false)
        setupFaq(R.id.faq5, tieneRespuestaEnXml = false, abrirPorDefecto = false)

        // ── Contactar soporte ─────────────────────────────────────────
        findViewById<Button>(R.id.btnContactarSoporte).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL,   arrayOf("soporte@caminepalgym.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Soporte técnico — Camine Pal Gym")
            }
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this,
                    "No tienes una app de correo configurada",
                    Toast.LENGTH_SHORT).show()
            }
        }

        // ── Ir al foro ────────────────────────────────────────────────
        findViewById<Button>(R.id.btnForo).setOnClickListener {
            // Navega directo a la pestaña del foro en MainActivity
            Toast.makeText(this, "Abre el Foro desde la barra de navegación", Toast.LENGTH_SHORT).show()
            finish() // vuelve al perfil para que el usuario navegue al foro
        }
    }

    private fun setupFaq(faqId: Int, tieneRespuestaEnXml: Boolean, abrirPorDefecto: Boolean) {
        val faqLayout = findViewById<LinearLayout>(faqId)
        val respuesta = respuestas[faqId] ?: return

        // Buscar o crear el TextView de respuesta
        val tvRespuesta: TextView = if (tieneRespuestaEnXml) {
            // faq1: el TextView de respuesta ya existe en el XML, es el último hijo
            faqLayout.getChildAt(faqLayout.childCount - 1) as TextView
        } else {
            // faq2-5: son horizontales, hay que convertirlos a vertical y añadir el TextView
            convertirAVerticalYAgregarRespuesta(faqLayout, respuesta)
        }

        // Buscar la flecha (ImageView) dentro del layout
        val flecha = encontrarFlecha(faqLayout)

        // Estado inicial
        if (abrirPorDefecto) {
            tvRespuesta.visibility = View.VISIBLE
            faqAbierta = faqId
            flecha?.rotation = 180f
        } else {
            tvRespuesta.visibility = View.GONE
            flecha?.rotation = 0f
        }

        // Click para expandir/colapsar
        faqLayout.setOnClickListener {
            val estaAbierta = tvRespuesta.visibility == View.VISIBLE

            // Cerrar la que estaba abierta (si es diferente)
            faqAbierta?.let { anteriorId ->
                if (anteriorId != faqId) {
                    val anteriorLayout = findViewById<LinearLayout>(anteriorId)
                    val anteriorRespuesta = encontrarTvRespuesta(anteriorLayout)
                    val anteriorFlecha = encontrarFlecha(anteriorLayout)
                    anteriorRespuesta?.visibility = View.GONE
                    anteriorFlecha?.animate()?.rotation(0f)?.setDuration(200)?.start()
                }
            }

            // Abrir o cerrar la actual
            if (estaAbierta) {
                tvRespuesta.visibility = View.GONE
                flecha?.animate()?.rotation(0f)?.setDuration(200)?.start()
                faqAbierta = null
            } else {
                tvRespuesta.visibility = View.VISIBLE
                flecha?.animate()?.rotation(180f)?.setDuration(200)?.start()
                faqAbierta = faqId
            }
        }
    }

    private fun convertirAVerticalYAgregarRespuesta(
        faqLayout: LinearLayout,
        respuesta: String
    ): TextView {
        // Cambiar orientación a vertical
        faqLayout.orientation = LinearLayout.VERTICAL

        // Crear un LinearLayout horizontal que contenga la pregunta + flecha
        val fila = LinearLayout(this).apply {
            orientation  = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Mover todos los hijos actuales (pregunta + flecha) al LinearLayout horizontal
        val hijos = mutableListOf<View>()
        for (i in 0 until faqLayout.childCount) hijos.add(faqLayout.getChildAt(i))
        faqLayout.removeAllViews()
        hijos.forEach { hijo ->
            (hijo.parent as? LinearLayout)?.removeView(hijo)
            fila.addView(hijo)
        }
        faqLayout.addView(fila)

        // Crear el TextView de respuesta
        val tvRespuesta = TextView(this).apply {
            text      = respuesta
            setTextColor(getColor(R.color.GrisTexto))
            textSize  = 13f
            setPadding(0, 24, 0, 0)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        faqLayout.addView(tvRespuesta)
        return tvRespuesta
    }

    private fun encontrarFlecha(layout: LinearLayout): ImageView? {
        for (i in 0 until layout.childCount) {
            val hijo = layout.getChildAt(i)
            if (hijo is ImageView) return hijo
            if (hijo is LinearLayout) {
                val resultado = encontrarFlecha(hijo)
                if (resultado != null) return resultado
            }
        }
        return null
    }

    private fun encontrarTvRespuesta(layout: LinearLayout): TextView? {
        // El TextView de respuesta siempre es el último hijo directo del faqLayout
        val ultimo = layout.getChildAt(layout.childCount - 1)
        return if (ultimo is TextView) ultimo else null
    }
}