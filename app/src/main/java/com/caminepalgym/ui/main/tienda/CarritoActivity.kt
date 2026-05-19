package com.caminepalgym.ui.main.tienda

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R

class CarritoActivity : AppCompatActivity() {

    private lateinit var adapter: CarritoAdapter
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvVacio: TextView
    private lateinit var tvDescuento: TextView

    private var descuentoAplicado = 0.0
    // Códigos válidos para demostración
    private val codigosValidos = mapOf("CPG10" to 0.10, "CPG20" to 0.20, "GYM15" to 0.15)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        tvSubtotal  = findViewById(R.id.tvSubtotalValor)
        tvTotal     = findViewById(R.id.tvTotalValor)
        tvVacio     = findViewById(R.id.tvCarritoVacio)
        tvDescuento = findViewById(R.id.tvDescuentoValor)

        // ── RecyclerView ───────────────────────────────────────────────
        val recycler = findViewById<RecyclerView>(R.id.recyclerCarrito)
        recycler.layoutManager = LinearLayoutManager(this)
        adapter = CarritoAdapter(CarritoManager.obtenerItems()) { actualizarResumen() }
        recycler.adapter = adapter

        // ── Código de descuento ────────────────────────────────────────
        findViewById<Button>(R.id.btnAplicar).setOnClickListener {
            val codigo = findViewById<EditText>(R.id.inputCodigo).text.toString().trim().uppercase()
            val pct = codigosValidos[codigo]
            if (pct != null) {
                descuentoAplicado = pct
                Toast.makeText(this, "Descuento del ${(pct * 100).toInt()}% aplicado ✓", Toast.LENGTH_SHORT).show()
                actualizarResumen()
            } else {
                descuentoAplicado = 0.0
                Toast.makeText(this, "Código no válido", Toast.LENGTH_SHORT).show()
            }
        }

        // ── Pagar ──────────────────────────────────────────────────────
        findViewById<Button>(R.id.btnPagar).setOnClickListener {
            if (CarritoManager.isEmpty()) {
                Toast.makeText(this, "Tu carrito está vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Confirmar pedido")
                .setMessage("Total a pagar: ${tvTotal.text}\n\nLos pagos en línea estarán disponibles próximamente.\n\n¡Gracias por tu interés!")
                .setPositiveButton("Entendido") { d, _ -> d.dismiss() }
                .show()
                .apply {
                    getButton(AlertDialog.BUTTON_POSITIVE)
                        ?.setTextColor(getColor(R.color.ColorPrincipal))
                }
        }

        // ── Botón atrás ────────────────────────────────────────────────
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        actualizarResumen()
    }

    private fun actualizarResumen() {
        val subtotal = CarritoManager.subtotal()
        val descuento = subtotal * descuentoAplicado
        val total = subtotal - descuento

        tvSubtotal.text = "$ ${String.format("%,.0f", subtotal)}"
        tvDescuento.text = if (descuentoAplicado > 0)
            "- $ ${String.format("%,.0f", descuento)}"
        else "$ 0"
        tvTotal.text = "$ ${String.format("%,.0f", total)}"

        // Mostrar/ocultar carrito vacío
        val vacio = CarritoManager.isEmpty()
        tvVacio.visibility = if (vacio) View.VISIBLE else View.GONE
        findViewById<RecyclerView>(R.id.recyclerCarrito).visibility =
            if (vacio) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        adapter.actualizar()
        actualizarResumen()
    }
}