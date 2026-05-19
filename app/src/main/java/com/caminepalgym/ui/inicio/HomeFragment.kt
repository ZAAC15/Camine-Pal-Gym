package com.caminepalgym.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.caminepalgym.MainActivity
import com.caminepalgym.R
import com.caminepalgym.data.RutinaRepository
import com.caminepalgym.data.UsuarioRepository
import com.caminepalgym.ui.main.entrenamiento.EstadoCorporalActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.tvUserAvatar).setOnClickListener {
            (requireActivity() as? MainActivity)?.irAPerfil()
        }

        view.findViewById<View>(R.id.cardPlanHoy).setOnClickListener {
            (requireActivity() as? MainActivity)?.irAEntrenamiento()
        }
        view.findViewById<View>(R.id.cardMetricas).setOnClickListener {
            startActivity(Intent(requireContext(), EditarMedidaActivity::class.java))
        }
        view.findViewById<View>(R.id.cardEstadoCorporal).setOnClickListener {
            startActivity(Intent(requireContext(), EstadoCorporalActivity::class.java))
        }

        val tvFecha = view.findViewById<TextView>(R.id.tvFecha)
        val formatoFecha = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "CO"))
        tvFecha.text = formatoFecha.format(Date()).replaceFirstChar { it.uppercase() }

        cargarDatosUsuario(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { cargarDatosUsuario(it) }
    }

    private fun cargarDatosUsuario(view: View) {
        val tvSaludo        = view.findViewById<TextView>(R.id.tvSaludo)
        val tvAvatar        = view.findViewById<TextView>(R.id.tvUserAvatar)
        val tvPeso          = view.findViewById<TextView>(R.id.tvPeso)
        val tvAltura        = view.findViewById<TextView>(R.id.tvAltura)
        val tvBMI           = view.findViewById<TextView>(R.id.tvBMI)
        val tvBMICat        = view.findViewById<TextView>(R.id.tvBMICategoria)
        val tvStreak        = view.findViewById<TextView>(R.id.tvStreak)
        val tvNombrePlan    = view.findViewById<TextView>(R.id.tvNombrePlan)
        val tvNumEjercicios = view.findViewById<TextView>(R.id.tvNumEjercicios)

        lifecycleScope.launch {
            try {
                val usuario     = UsuarioRepository.obtenerUsuarioActual()
                val nombre      = usuario?.nombre ?: "Usuario"
                val hora        = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val saludo      = when {
                    hora < 12 -> "Buenos días"
                    hora < 18 -> "Buenas tardes"
                    else      -> "Buenas noches"
                }
                val iniciales = buildString {
                    nombre.split(" ").take(2).forEach {
                        append(it.firstOrNull()?.uppercaseChar() ?: "")
                    }
                }.ifEmpty { "U" }

                val medidas     = UsuarioRepository.obtenerMedidas()
                val racha       = RutinaRepository.obtenerRachaActual()
                val rachaSemana = RutinaRepository.obtenerRachaSemana()
                val rutinaHoy   = RutinaRepository.obtenerRutinaHoy()

                val nombreRutina = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                    Calendar.MONDAY    -> "Pecho y Trícep"
                    Calendar.TUESDAY   -> "Espalda y Bícep"
                    Calendar.WEDNESDAY -> "Día de Pierna"
                    Calendar.THURSDAY  -> "Día de Hombros"
                    Calendar.FRIDAY    -> "Full Body"
                    Calendar.SATURDAY  -> "Abdomen y Cardio"
                    else               -> "Recuperación Activa"
                }
                val completados = rutinaHoy.count { it.completado }
                val total       = rutinaHoy.size

                // Verificar que el fragment sigue activo
                if (!isAdded || context == null) return@launch

                tvSaludo.text = "$saludo, $nombre"
                tvAvatar.text = iniciales
                tvNombrePlan.text    = nombreRutina
                tvNumEjercicios.text = if (total > 0) "$completados/$total ejercicios"
                else "Sin ejercicios aún"

                if (medidas != null) {
                    val peso = medidas.peso
                    tvPeso.text = if (peso != null) {
                        if (peso % 1.0 == 0.0) peso.toInt().toString()
                        else String.format("%.1f", peso)
                    } else "--"

                    val altura = medidas.altura
                    tvAltura.text = if (altura != null) {
                        if (altura % 1.0 == 0.0) altura.toInt().toString()
                        else String.format("%.1f", altura)
                    } else "--"

                    val bmi = medidas.bmi
                    if (bmi != null && bmi > 0) {
                        tvBMI.text = String.format("%.1f", bmi)
                        val (categoria, color) = when {
                            bmi < 18.5 -> "Bajo peso" to "#2196F3"
                            bmi < 25.0 -> "Normal"    to "#4CAF50"
                            bmi < 30.0 -> "Sobrepeso" to "#FF9800"
                            else       -> "Obesidad"  to "#F44336"
                        }
                        tvBMICat.text = categoria
                        tvBMICat.setTextColor(android.graphics.Color.parseColor(color))
                    } else {
                        tvBMI.text    = "--"
                        tvBMICat.text = ""
                    }
                } else {
                    tvPeso.text   = "--"
                    tvAltura.text = "--"
                    tvBMI.text    = "--"
                    tvBMICat.text = ""
                }

                tvStreak.text = when (racha) {
                    0    -> "Sin racha"
                    1    -> "1 día de racha 🔥"
                    else -> "$racha días de racha 🔥"
                }

                actualizarDiasSemana(view, rachaSemana)

            } catch (e: Exception) {
                if (!isAdded || context == null) return@launch
                tvSaludo.text = "Hola, Usuario"
                tvAvatar.text = "U"
            }
        }
    }

    private fun actualizarDiasSemana(view: View, rachaSemana: List<Boolean>) {
        val ids = listOf(
            R.id.diaL, R.id.diaM1, R.id.diaM2,
            R.id.diaJ, R.id.diaV, R.id.diaS, R.id.diaD
        )
        ids.forEachIndexed { index, id ->
            view.findViewById<View>(id)?.setBackgroundResource(
                if (rachaSemana.getOrElse(index) { false }) R.drawable.bg_day_active
                else R.drawable.bg_day_inactive
            )
        }
    }
}