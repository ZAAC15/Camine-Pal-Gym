package com.caminepalgym.data

import android.annotation.SuppressLint
import com.caminepalgym.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



object UsuarioRepository {

    @SuppressLint("UnsafeOptInUsageError")
    @Serializable
    data class UsuarioData(
        val id: String = "",
        val usuario: String = "",
        val nombre: String = "",
        val apellidos: String = "",
        val correo: String = "",
        val celular: String? = null,
        @SerialName("foto_url") val fotoUrl: String? = null,
        val rol: String = "cliente",
        val biografia: String? = null,
        @SerialName("fecha_cumpleanos") val fechaCumpleanos: String? = null,
        val instagram: String? = null,
        val twitter: String? = null
    )
    suspend fun insertarUsuario(
        usuario: String,
        nombre: String,
        apellidos: String,
        correo: String,
        celular: String
    ) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return
        SupabaseClient.client.postgrest["Usuarios"].insert(
            UsuarioData(
                id = userId,
                usuario = usuario,
                nombre = nombre,
                apellidos = apellidos,
                correo = correo,
                celular = celular
            )
        )
    }

    suspend fun obtenerUsuarioActual(): UsuarioData? {
        return try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return null
            SupabaseClient.client.postgrest["Usuarios"]
                .select { filter { eq("id", userId) } }
                .decodeList<UsuarioData>()
                .firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun obtenerRolActual(): String {
        return try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return "cliente"
            val resultado = SupabaseClient.client.postgrest["Usuarios"]
                .select { filter { eq("id", userId) } }
                .decodeList<UsuarioData>()
            resultado.firstOrNull()?.rol ?: "cliente"
        } catch (e: Exception) {
            "cliente"
        }
    }

    // — Medidas —

    suspend fun guardarMedidas(medidas: MedidasData) {
        SupabaseClient.client.postgrest["MedidasUsuario"].upsert(medidas)
    }

    suspend fun obtenerMedidas(): MedidasData? {
        return try {
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return null
            SupabaseClient.client.postgrest["MedidasUsuario"]
                .select { filter { eq("id", userId) } }
                .decodeList<MedidasData>()
                .firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun tieneMedidas(): Boolean {
        return obtenerMedidas() != null
    }
}