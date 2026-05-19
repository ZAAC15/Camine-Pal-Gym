package com.caminepalgym.ui.main.tienda

import com.caminepalgym.data.ProductoData

object CarritoManager {

    data class ItemCarrito(
        val producto: ProductoData,
        var cantidad: Int = 1
    )

    private val items = mutableListOf<ItemCarrito>()

    fun agregar(producto: ProductoData) {
        val existente = items.find { it.producto.id == producto.id }
        if (existente != null) existente.cantidad++
        else items.add(ItemCarrito(producto))
    }

    fun incrementar(productoId: String) {
        items.find { it.producto.id == productoId }?.cantidad++
    }

    fun decrementar(productoId: String) {
        val item = items.find { it.producto.id == productoId } ?: return
        if (item.cantidad > 1) item.cantidad-- else items.remove(item)
    }

    fun eliminar(productoId: String) {
        items.removeAll { it.producto.id == productoId }
    }

    fun obtenerItems(): List<ItemCarrito> = items.toList()
    fun totalItems(): Int = items.sumOf { it.cantidad }
    fun subtotal(): Double = items.sumOf { it.producto.precio * it.cantidad }
    fun limpiar() = items.clear()
    fun isEmpty(): Boolean = items.isEmpty()
}