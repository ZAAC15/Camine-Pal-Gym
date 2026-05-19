package com.caminepalgym.ui.main.perfil

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.caminepalgym.R

class NotificacionesActivity : AppCompatActivity() {

    // Nombre del archivo de preferencias
    private val PREFS_NAME = "notificaciones_prefs"

    // Claves para cada switch
    private val KEY_RECORDATORIOS = "switch_recordatorios"
    private val KEY_INFORME       = "switch_informe"
    private val KEY_LOGROS        = "switch_logros"
    private val KEY_COMUNIDAD     = "switch_comunidad"
    private val KEY_PUSH          = "switch_push"
    private val KEY_EMAIL         = "switch_email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notificaciones)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // ── Referencias ────────────────────────────────────────────────
        val switchRecordatorios = findViewById<Switch>(R.id.switchRecordatorios)
        val switchInforme       = findViewById<Switch>(R.id.switchInforme)
        val switchLogros        = findViewById<Switch>(R.id.switchLogros)
        val switchComunidad     = findViewById<Switch>(R.id.switchComunidad)
        val switchPush          = findViewById<Switch>(R.id.switchPush)
        val switchEmail         = findViewById<Switch>(R.id.switchEmail)

        // ── Restaurar estado guardado (defaults según el XML) ──────────
        switchRecordatorios.isChecked = prefs.getBoolean(KEY_RECORDATORIOS, true)
        switchInforme.isChecked       = prefs.getBoolean(KEY_INFORME,       true)
        switchLogros.isChecked        = prefs.getBoolean(KEY_LOGROS,        true)
        switchComunidad.isChecked     = prefs.getBoolean(KEY_COMUNIDAD,     false)
        switchPush.isChecked          = prefs.getBoolean(KEY_PUSH,          true)
        switchEmail.isChecked         = prefs.getBoolean(KEY_EMAIL,         false)

        // ── Aplicar colores iniciales ──────────────────────────────────
        actualizarEstiloSwitch(switchRecordatorios)
        actualizarEstiloSwitch(switchInforme)
        actualizarEstiloSwitch(switchLogros)
        actualizarEstiloSwitch(switchComunidad)
        actualizarEstiloSwitch(switchPush)
        actualizarEstiloSwitch(switchEmail)

        // ── Listeners ─────────────────────────────────────────────────
        switchRecordatorios.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_RECORDATORIOS, isChecked).apply()
            actualizarEstiloSwitch(switchRecordatorios)
            mostrarMensaje(isChecked, "Recordatorios de entrenamiento")
        }

        switchInforme.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_INFORME, isChecked).apply()
            actualizarEstiloSwitch(switchInforme)
            mostrarMensaje(isChecked, "Informe semanal")
        }

        switchLogros.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_LOGROS, isChecked).apply()
            actualizarEstiloSwitch(switchLogros)
            mostrarMensaje(isChecked, "Alertas de logros")
        }

        switchComunidad.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_COMUNIDAD, isChecked).apply()
            actualizarEstiloSwitch(switchComunidad)
            mostrarMensaje(isChecked, "Actividad comunitaria")
        }

        switchPush.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_PUSH, isChecked).apply()
            actualizarEstiloSwitch(switchPush)
            mostrarMensaje(isChecked, "Notificaciones push")
        }

        switchEmail.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean(KEY_EMAIL, isChecked).apply()
            actualizarEstiloSwitch(switchEmail)
            mostrarMensaje(isChecked, "Notificaciones por correo")
        }

        // ── Botón atrás ───────────────────────────────────────────────
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }


    private fun actualizarEstiloSwitch(switch: Switch) {
        if (switch.isChecked) {
            switch.thumbTintList = getColorStateList(com.caminepalgym.R.color.background)
            switch.trackTintList = getColorStateList(com.caminepalgym.R.color.ColorPrincipal)
        } else {
            switch.thumbTintList = getColorStateList(com.caminepalgym.R.color.GrisTexto)
            switch.trackTintList = getColorStateList(com.caminepalgym.R.color.FondoGris)
        }
    }

    private fun mostrarMensaje(activo: Boolean, nombre: String) {
        val estado = if (activo) "activado" else "desactivado"
        Toast.makeText(this, "$nombre $estado", Toast.LENGTH_SHORT).show()
    }
}