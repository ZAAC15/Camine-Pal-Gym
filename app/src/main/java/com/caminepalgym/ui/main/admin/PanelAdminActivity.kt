package com.caminepalgym.ui.main.admin

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R

class PanelAdminActivity : AppCompatActivity() {

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var tabUsuarios: TextView
    private lateinit var tabAgregarUsuario: TextView
    private lateinit var tabProductos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_panel_admin)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        viewFlipper = findViewById(R.id.viewFlipper)
        tabUsuarios = findViewById(R.id.tabUsuarios)
        tabAgregarUsuario = findViewById(R.id.tabAgregarUsuario)
        tabProductos = findViewById(R.id.tabProductos)

        // Navegación entre tabs
        tabUsuarios.setOnClickListener { cambiarTab(0) }
        tabAgregarUsuario.setOnClickListener { cambiarTab(1) }
        tabProductos.setOnClickListener { cambiarTab(2) }

        // RecyclerView usuarios
        val recyclerUsuarios = findViewById<RecyclerView>(R.id.recyclerUsuarios)
        recyclerUsuarios.layoutManager = LinearLayoutManager(this)
        // recyclerUsuarios.adapter = UsuariosAdapter(listaUsuarios, ::onEditarUsuario, ::onBorrarUsuario)

        // RecyclerView productos admin
        val recyclerProductos = findViewById<RecyclerView>(R.id.recyclerProductosAdmin)
        recyclerProductos.layoutManager = LinearLayoutManager(this)
        // recyclerProductos.adapter = ProductosAdminAdapter(listaProductos, ::onBorrarProducto)

        // Botones tab agregar usuario
        findViewById<TextView>(R.id.tvCancelar).setOnClickListener { cambiarTab(0) }
        findViewById<android.widget.Button>(R.id.btnCrearUsuario).setOnClickListener {
            // lógica para crear usuario
        }

        // Botón añadir producto
        findViewById<android.widget.Button>(R.id.btnAnadirProducto).setOnClickListener {
            // lógica para añadir producto
        }
    }

    private fun cambiarTab(index: Int) {
        viewFlipper.displayedChild = index
        val colorActivo = getColor(R.color.ColorPrincipal)
        val colorInactivo = getColor(R.color.GrisTexto)
        tabUsuarios.setTextColor(if (index == 0) colorActivo else colorInactivo)
        tabAgregarUsuario.setTextColor(if (index == 1) colorActivo else colorInactivo)
        tabProductos.setTextColor(if (index == 2) colorActivo else colorInactivo)
    }

    // Llamado desde el adapter al presionar lápiz
    fun onEditarUsuario(posicion: Int) {
        // mostrar inline el formulario de edición en el recycler
    }

    // Llamado desde el adapter al presionar basura
    fun onBorrarUsuario(posicion: Int) {
        // mostrar confirmación inline en el recycler
    }

    fun onBorrarProducto(posicion: Int) {
        // lógica para borrar producto
    }
}