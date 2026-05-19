package com.caminepalgym.data

import android.annotation.SuppressLint
import com.caminepalgym.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Data classes para INSERT/UPDATE — fuera de las funciones ──────────
@SuppressLint("UnsafeOptInUsageError")
@Serializable
private data class NuevoPost(
    val usuario_id: String,
    val nombre: String,
    val titulo: String?,
    val contenido: String,
    val imagen_url: String?,
    val etiqueta: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
private data class LikeInsert(
    val post_id: String,
    val usuario_id: String
)

@SuppressLint("UnsafeOptInUsageError")
@Serializable
private data class LikeCountUpdate(
    val likes: Int
)

// ─────────────────────────────────────────────────────────────────────

object PostRepository {

    // ── Obtener posts ─────────────────────────────────────────────────
    suspend fun obtenerPosts(etiqueta: String? = null): List<PostData> {
        return try {
            SupabaseClient.client.postgrest["Posts"]
                .select {
                    if (etiqueta != null && etiqueta != "Tendencias") {
                        filter { eq("etiqueta", etiqueta) }
                    }
                }
                .decodeList<PostData>()
                .sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Crear post ────────────────────────────────────────────────────
    suspend fun crearPost(
        titulo: String?,
        contenido: String,
        imagenUrl: String?,
        etiqueta: String,
        nombre: String
    ) {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
            ?: throw Exception("Usuario no autenticado")

        SupabaseClient.client.postgrest["Posts"].insert(
            NuevoPost(
                usuario_id = userId,
                nombre     = nombre,
                titulo     = titulo?.ifBlank { null },
                contenido  = contenido,
                imagen_url = imagenUrl?.ifBlank { null },
                etiqueta   = etiqueta
            )
        )
    }

    // ── Toggle like ───────────────────────────────────────────────────
    suspend fun toggleLike(postId: String): Boolean {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
            ?: return false

        return try {
            val likes = SupabaseClient.client.postgrest["PostLikes"]
                .select { filter { eq("post_id", postId); eq("usuario_id", userId) } }
                .decodeList<Map<String, String>>()

            val post = SupabaseClient.client.postgrest["Posts"]
                .select { filter { eq("id", postId) } }
                .decodeList<PostData>().firstOrNull()

            if (likes.isEmpty()) {
                // Dar like
                SupabaseClient.client.postgrest["PostLikes"]
                    .insert(LikeInsert(post_id = postId, usuario_id = userId))
                post?.let {
                    SupabaseClient.client.postgrest["Posts"]
                        .update(LikeCountUpdate(it.likes + 1)) {
                            filter { eq("id", postId) }
                        }
                }
                true
            } else {
                // Quitar like
                SupabaseClient.client.postgrest["PostLikes"]
                    .delete { filter { eq("post_id", postId); eq("usuario_id", userId) } }
                post?.let {
                    val nuevo = if (it.likes > 0) it.likes - 1 else 0
                    SupabaseClient.client.postgrest["Posts"]
                        .update(LikeCountUpdate(nuevo)) {
                            filter { eq("id", postId) }
                        }
                }
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // ── Eliminar post ─────────────────────────────────────────────────
    suspend fun eliminarPost(postId: String) {
        SupabaseClient.client.postgrest["Posts"]
            .delete { filter { eq("id", postId) } }
    }

    // ── Verificar like ────────────────────────────────────────────────
    suspend fun tieneLike(postId: String): Boolean {
        val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return false
        return try {
            val resultado = SupabaseClient.client.postgrest["PostLikes"]
                .select { filter { eq("post_id", postId); eq("usuario_id", userId) } }
                .decodeList<Map<String, String>>()
            resultado.isNotEmpty()
        } catch (e: Exception) { false }
    }
}