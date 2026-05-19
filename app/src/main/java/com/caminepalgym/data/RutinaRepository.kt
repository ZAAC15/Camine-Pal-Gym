package com.caminepalgym.data

import com.caminepalgym.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import java.text.SimpleDateFormat
import java.util.*

object RutinaRepository {

    private fun fechaHoy(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun diaSemanaHoy(): Int {
        val cal = Calendar.getInstance()
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY    -> 0
            Calendar.TUESDAY   -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY  -> 3
            Calendar.FRIDAY    -> 4
            Calendar.SATURDAY  -> 5
            else               -> 6
        }
    }

    private fun generarRutinasDia(objetivo: String?, nivel: String?): List<Triple<String, String, String>> {
        val esPrincipiante = nivel == "Principiante"
        val series = if (esPrincipiante) 3 else 4

        return when (diaSemanaHoy()) {
            0 -> listOf(
                Triple("Press de banca",       "Empuje", "$series × 10 × ${if (esPrincipiante) "60kg" else "80kg"}"),
                Triple("Press inclinado",       "Empuje", "$series × 10 × ${if (esPrincipiante) "40kg" else "60kg"}"),
                Triple("Extensión de trícep",   "Empuje", "$series × 12 × 20kg"),
                Triple("Fondos en paralelas",   "Empuje", "$series × 10 × peso corporal")
            )
            1 -> listOf(
                Triple("Dominadas",     "Jalón", "$series × 8 × peso corporal"),
                Triple("Jalón al pecho","Jalón", "$series × 10 × ${if (esPrincipiante) "40kg" else "60kg"}"),
                Triple("Remo con barra","Jalón", "$series × 10 × ${if (esPrincipiante) "60kg" else "80kg"}"),
                Triple("Curl de bíceps","Jalón", "$series × 12 × 15kg")
            )
            2 -> listOf(
                Triple("Sentadillas",     "Pierna", "$series × 10 × ${if (esPrincipiante) "60kg" else "100kg"}"),
                Triple("Prensa de pierna","Pierna", "$series × 12 × ${if (esPrincipiante) "80kg" else "120kg"}"),
                Triple("Zancadas",        "Pierna", "$series × 10 × 40kg"),
                Triple("Curl femoral",    "Pierna", "$series × 12 × 35kg")
            )
            3 -> listOf(
                Triple("Press militar",         "Empuje", "$series × 10 × ${if (esPrincipiante) "30kg" else "50kg"}"),
                Triple("Elevaciones laterales", "Empuje", "$series × 12 × 10kg"),
                Triple("Pájaros",               "Jalón",  "$series × 12 × 8kg"),
                Triple("Encogimientos",         "Jalón",  "$series × 12 × 20kg")
            )
            4 -> listOf(
                Triple("Peso muerto",   "Jalón",  "$series × 6 × ${if (esPrincipiante) "80kg" else "120kg"}"),
                Triple("Press de banca","Empuje", "$series × 8 × ${if (esPrincipiante) "60kg" else "80kg"}"),
                Triple("Sentadillas",   "Pierna", "$series × 8 × ${if (esPrincipiante) "60kg" else "90kg"}"),
                Triple("Dominadas",     "Jalón",  "$series × 6 × peso corporal")
            )
            5 -> listOf(
                Triple("Plancha",              "Abdomen", "4 × 1 × 60 seg"),
                Triple("Crunch",               "Abdomen", "3 × 20 × peso corporal"),
                Triple("Elevación de piernas", "Abdomen", "3 × 15 × peso corporal"),
                Triple("Russian twist",        "Abdomen", "3 × 20 × 10kg")
            )
            else -> listOf(
                Triple("Plancha",       "Abdomen", "3 × 1 × 45 seg"),
                Triple("Crunch",        "Abdomen", "3 × 15 × peso corporal"),
                Triple("Zancadas",      "Pierna",  "3 × 10 × peso corporal"),
                Triple("Curl de bíceps","Jalón",   "3 × 12 × 10kg")
            )
        }
    }

    suspend fun generarRutinaAutomatica() {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        val rutinaExistente = obtenerRutinaHoy()
        if (rutinaExistente.isNotEmpty()) return

        val medidas = UsuarioRepository.obtenerMedidas()
        val ejercicios = generarRutinasDia(medidas?.objetivo, medidas?.nivelCondicion)

        ejercicios.forEach { (nombre, categoria, detalle) ->
            val partes = detalle.split(" × ")
            val series = partes.getOrNull(0)?.toIntOrNull() ?: 3
            val reps   = partes.getOrNull(1)?.toIntOrNull() ?: 10
            val peso   = partes.getOrNull(2) ?: "peso corporal"

            SupabaseClient.client.postgrest["RutinaUsuario"].insert(
                RutinaData(
                    usuarioId = userId,
                    nombre = nombre,
                    categoria = categoria,
                    series = series,
                    repeticiones = reps,
                    peso = peso,
                    fecha = fechaHoy()
                )
            )
        }
    }

    suspend fun obtenerRutinaHoy(): List<RutinaData> {
        return try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return emptyList()
            SupabaseClient.client.postgrest["RutinaUsuario"]
                .select {
                    filter {
                        eq("usuario_id", userId)
                        eq("fecha", fechaHoy())
                    }
                }
                .decodeList<RutinaData>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun agregarEjercicio(
        nombre: String,
        categoria: String,
        series: Int,
        repeticiones: Int,
        peso: String
    ) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        SupabaseClient.client.postgrest["RutinaUsuario"].insert(
            RutinaData(
                usuarioId = userId,
                nombre = nombre,
                categoria = categoria,
                series = series,
                repeticiones = repeticiones,
                peso = peso,
                fecha = fechaHoy()
            )
        )
    }

    suspend fun marcarCompletado(ejercicioId: String) {
        SupabaseClient.client.postgrest["RutinaUsuario"]
            .update({ set("completado", true) }) {
                filter { eq("id", ejercicioId) }
            }
        registrarRachaHoy()
    }

    private suspend fun registrarRachaHoy() {
        try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
            SupabaseClient.client.postgrest["RachaUsuario"].upsert(
                RachaData(
                    usuarioId = userId,
                    fecha = fechaHoy(),
                    completado = true
                )
            ) {
                onConflict = "usuario_id,fecha"
            }
        } catch (_: Exception) { }
    }

    suspend fun obtenerRachaActual(): Int {
        return try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return 0

            val registros = SupabaseClient.client.postgrest["RachaUsuario"]
                .select { filter { eq("usuario_id", userId) } }
                .decodeList<RachaData>()

            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fechas = registros
                .map { formato.parse(it.fecha) }
                .filterNotNull()
                .sortedDescending()

            if (fechas.isEmpty()) return 0

            var racha = 0
            val hoy = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            var referencia = hoy.timeInMillis

            for (fecha in fechas) {
                val calFecha = Calendar.getInstance().apply {
                    time = fecha
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val diff = ((referencia - calFecha.timeInMillis) / (1000 * 60 * 60 * 24)).toInt()
                if (diff <= 1) {
                    racha++
                    referencia = calFecha.timeInMillis
                } else break
            }
            racha
        } catch (e: Exception) {
            0
        }
    }

    suspend fun obtenerRachaSemana(): List<Boolean> {
        return try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
                ?: return List(7) { false }

            val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // Calcular el lunes de esta semana correctamente
            val hoy = Calendar.getInstance()
            val diaSemana = hoy.get(Calendar.DAY_OF_WEEK)
            val diasDesdeElLunes = when (diaSemana) {
                Calendar.MONDAY    -> 0
                Calendar.TUESDAY   -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY  -> 3
                Calendar.FRIDAY    -> 4
                Calendar.SATURDAY  -> 5
                Calendar.SUNDAY    -> 6
                else               -> 0
            }

            // Clonar hoy y restar días para llegar al lunes
            val lunes = hoy.clone() as Calendar
            lunes.add(Calendar.DAY_OF_YEAR, -diasDesdeElLunes)
            lunes.set(Calendar.HOUR_OF_DAY, 0)
            lunes.set(Calendar.MINUTE, 0)
            lunes.set(Calendar.SECOND, 0)
            lunes.set(Calendar.MILLISECOND, 0)

            // Generar L-M-M-J-V-S-D
            val diasSemana = (0..6).map { i ->
                val dia = lunes.clone() as Calendar
                dia.add(Calendar.DAY_OF_YEAR, i)
                formato.format(dia.time)
            }

            val registros = SupabaseClient.client.postgrest["RachaUsuario"]
                .select { filter { eq("usuario_id", userId) } }
                .decodeList<RachaData>()

            val fechasCompletadas = registros.map { it.fecha }.toSet()
            diasSemana.map { it in fechasCompletadas }

        } catch (e: Exception) {
            List(7) { false }
        }
    }
    suspend fun contarEntrenamientosCompletados(): Int {
        return try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return 0
            SupabaseClient.client.postgrest["RutinaUsuario"]
                .select {
                    filter {
                        eq("usuario_id", userId)
                        eq("completado", true)
                    }
                }
                .decodeList<RutinaData>()
                .size
        } catch (e: Exception) {
            0
        }
    }
    suspend fun eliminarEjercicio(ejercicioId: String) {
        SupabaseClient.client.postgrest["RutinaUsuario"]
            .delete { filter { eq("id", ejercicioId) } }
    }
}