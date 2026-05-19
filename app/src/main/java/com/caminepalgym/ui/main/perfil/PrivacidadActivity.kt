package com.caminepalgym.ui.main.perfil

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class PrivacidadActivity : AppCompatActivity() {

    private val PREFS_NAME = "privacidad_prefs"

    private val KEY_PERFIL_PUBLICO    = "switch_perfil_publico"
    private val KEY_ACTIVIDAD_AMIGOS  = "switch_actividad_amigos"
    private val KEY_DATOS_ANALISIS    = "switch_datos_analisis"
    private val KEY_UBICACION         = "switch_ubicacion"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacidad)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // ── Referencias ──
        val switchPerfilPublico   = findViewById<Switch>(R.id.switchPerfilPublico)
        val switchActividadAmigos = findViewById<Switch>(R.id.switchActividadAmigos)
        val switchDatosAnalisis   = findViewById<Switch>(R.id.switchDatosAnalisis)
        val switchUbicacion       = findViewById<Switch>(R.id.switchUbicacion)

        // ── Restaurar estado guardado ──
        switchPerfilPublico.isChecked   = prefs.getBoolean(KEY_PERFIL_PUBLICO,   true)
        switchActividadAmigos.isChecked = prefs.getBoolean(KEY_ACTIVIDAD_AMIGOS, true)
        switchDatosAnalisis.isChecked   = prefs.getBoolean(KEY_DATOS_ANALISIS,   false)
        switchUbicacion.isChecked       = prefs.getBoolean(KEY_UBICACION,        false)

        // ── Aplicar colores iniciales ──
        actualizarEstiloSwitch(switchPerfilPublico)
        actualizarEstiloSwitch(switchActividadAmigos)
        actualizarEstiloSwitch(switchDatosAnalisis)
        actualizarEstiloSwitch(switchUbicacion)

        // ── Listeners switches ──
        switchPerfilPublico.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_PERFIL_PUBLICO, isChecked).apply()
            actualizarEstiloSwitch(switchPerfilPublico)
            mostrarMensaje(isChecked, "Perfil público")
        }

        switchActividadAmigos.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_ACTIVIDAD_AMIGOS, isChecked).apply()
            actualizarEstiloSwitch(switchActividadAmigos)
            mostrarMensaje(isChecked, "Mostrar actividad a amigos")
        }

        switchDatosAnalisis.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_DATOS_ANALISIS, isChecked).apply()
            actualizarEstiloSwitch(switchDatosAnalisis)
            mostrarMensaje(isChecked, "Compartir datos para análisis")
        }

        switchUbicacion.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_UBICACION, isChecked).apply()
            actualizarEstiloSwitch(switchUbicacion)
            mostrarMensaje(isChecked, "Acceso a ubicación")
        }

        // ── Botón descargar datos ──
        findViewById<Button>(R.id.btnDescargarDatos).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Descargar mis datos")
                .setMessage("Próximamente podrás exportar todos tus datos en formato JSON.\n\n¡Esta función estará disponible en la siguiente versión!")
                .setPositiveButton("Entendido") { dialog, _ -> dialog.dismiss() }
                .show()
                .aplicarEstiloDialog()
        }

        // ── Botón borrar cuenta ──
        findViewById<Button>(R.id.btnBorrarCuenta).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("⚠️ Borrar cuenta")
                .setMessage("Esta acción es permanente. Se eliminarán todos tus datos, rutinas, medidas y progreso.\n\n¿Estás seguro de que deseas continuar?")
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .setPositiveButton("Sí, borrar") { _, _ -> confirmarBorradoCuenta() }
                .show()
                .aplicarEstiloDialog()
        }

        // ── Botón atrás ──
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun confirmarBorradoCuenta() {
        // Segundo dialog de confirmación por seguridad
        AlertDialog.Builder(this)
            .setTitle("Confirmación final")
            .setMessage("¿Seguro? No hay vuelta atrás.")
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Borrar definitivamente") { _, _ ->
                lifecycleScope.launch {
                    try {
                        SupabaseClient.client.auth.signOut()
                        runOnUiThread {
                            Toast.makeText(
                                this@PrivacidadActivity,
                                "Cuenta cerrada. Contacta soporte para eliminación completa.",
                                Toast.LENGTH_LONG
                            ).show()
                            finishAffinity() // cierra todas las activities
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(
                                this@PrivacidadActivity,
                                "Error: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            .show()
            .aplicarEstiloDialog()
    }

    private fun actualizarEstiloSwitch(switch: Switch) {
        if (switch.isChecked) {
            switch.thumbTintList = getColorStateList(R.color.background)
            switch.trackTintList = getColorStateList(R.color.ColorPrincipal)
        } else {
            switch.thumbTintList = getColorStateList(R.color.GrisTexto)
            switch.trackTintList = getColorStateList(R.color.FondoGris)
        }
    }

    private fun mostrarMensaje(activo: Boolean, nombre: String) {
        val estado = if (activo) "activado" else "desactivado"
        Toast.makeText(this, "$nombre $estado", Toast.LENGTH_SHORT).show()
    }

    // Extensión para darle estilo oscuro al AlertDialog
    private fun AlertDialog.aplicarEstiloDialog(): AlertDialog {
        this.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(getColor(R.color.ColorPrincipal))
        this.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(getColor(R.color.GrisTexto))
        return this
    }
}