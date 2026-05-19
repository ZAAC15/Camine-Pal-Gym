package com.caminepalgym.data

import com.caminepalgym.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

object ProductoRepository {

    // ── Obtener todos los productos activos ────────────────────────────
    suspend fun obtenerProductos(): List<ProductoData> {
        return try {
            SupabaseClient.client.postgrest["Productos"]
                .select { filter { eq("activo", true) } }
                .decodeList<ProductoData>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Obtener por categoría ──────────────────────────────────────────
    suspend fun obtenerPorCategoria(categoria: String): List<ProductoData> {
        return try {
            SupabaseClient.client.postgrest["Productos"]
                .select {
                    filter {
                        eq("activo", true)
                        eq("categoria", categoria)
                    }
                }
                .decodeList<ProductoData>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Obtener TODOS (incluyendo inactivos) — para el admin ───────────
    suspend fun obtenerTodosAdmin(): List<ProductoData> {
        return try {
            SupabaseClient.client.postgrest["Productos"]
                .select()
                .decodeList<ProductoData>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Crear producto ─────────────────────────────────────────────────
    @InternalSerializationApi
    suspend fun crearProducto(
        nombre: String,
        descripcion: String,
        precio: Double,
        categoria: String,
        insignia: String?,
        imagenUrl: String?,
        stock: Int
    ) {
        @Serializable
        data class NuevoProducto(
            val nombre: String,
            val descripcion: String,
            val precio: Double,
            val categoria: String,
            val insignia: String?,
            val imagen_url: String?,
            val stock: Int
        )

        SupabaseClient.client.postgrest["Productos"].insert(
            NuevoProducto(
                nombre      = nombre,
                descripcion = descripcion,
                precio      = precio,
                categoria   = categoria,
                insignia    = insignia?.ifBlank { null },
                imagen_url  = imagenUrl?.ifBlank { null },
                stock       = stock
            )
        )
    }

    // ── Eliminar (desactivar) producto — soft delete ───────────────────
    @InternalSerializationApi
    suspend fun desactivarProducto(productoId: String) {
        @Serializable
        data class ActivoUpdate(val activo: Boolean)

        SupabaseClient.client.postgrest["Productos"]
            .update(ActivoUpdate(activo = false)) {
                filter { eq("id", productoId) }
            }
    }

    // ── Eliminar permanente ────────────────────────────────────────────
    suspend fun eliminarProducto(productoId: String) {
        SupabaseClient.client.postgrest["Productos"]
            .delete { filter { eq("id", productoId) } }
    }

    // ── Stats para el panel admin ──────────────────────────────────────
    suspend fun obtenerStats(): Triple<Int, Int, Double> {
        return try {
            val todos = obtenerTodosAdmin()
            val total     = todos.size
            val insignias = todos.count { !it.insignia.isNullOrBlank() }
            val promedio  = if (todos.isEmpty()) 0.0
            else todos.sumOf { it.precio } / todos.size
            Triple(total, insignias, promedio)
        } catch (e: Exception) {
            Triple(0, 0, 0.0)
        }
    }
}