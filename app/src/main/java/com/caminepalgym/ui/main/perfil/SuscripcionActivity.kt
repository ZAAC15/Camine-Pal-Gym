package com.caminepalgym.ui.main.perfil

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.caminepalgym.R

class SuscripcionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suscripcion)

        // ── Botón atrás ───────────────────────────────────────────────
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // ── Botón suscribirse anual ────────────────────────────────────
        findViewById<Button>(R.id.btnSuscribirAnual).setOnClickListener {
            mostrarProximamente(
                titulo  = "Plan Anual — \$108.000",
                detalle = "Ahorra 40% con el plan anual.\n\nLos pagos en línea estarán disponibles próximamente. ¡Gracias por tu interés!"
            )
        }

        // ── Cancelar suscripción ───────────────────────────────────────
        findViewById<TextView>(R.id.tvCancelarSuscripcion).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cancelar suscripción")
                .setMessage("¿Deseas cancelar tu plan PRO?\n\nEsta función estará disponible próximamente. Por ahora, contacta a soporte para gestionar tu suscripción.")
                .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
                .show()
                .colorearBoton()
        }
    }

    private fun mostrarProximamente(titulo: String, detalle: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(detalle)
            .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
            .show()
            .colorearBoton()
    }

    private fun AlertDialog.colorearBoton(): AlertDialog {
        getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(getColor(R.color.ColorPrincipal))
        return this
    }
}