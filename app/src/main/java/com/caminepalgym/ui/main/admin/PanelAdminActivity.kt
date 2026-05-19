package com.caminepalgym.ui.main.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import com.caminepalgym.data.ProductoData
import com.caminepalgym.data.ProductoRepository
import com.caminepalgym.data.UsuarioRepository
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.InternalSerializationApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class PanelAdminActivity : AppCompatActivity() {

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var tabUsuarios: TextView
    private lateinit var tabAgregarUsuario: TextView
    private lateinit var tabProductos: TextView

    // ── Usuarios ───────────────────────────────────────────────────────
    private var listaUsuarios = listOf<UsuarioRepository.UsuarioData>()
    private lateinit var adapterUsuarios: UsuariosAdminAdapter
    private var rolSeleccionado = "cliente"
    private lateinit var btnRolBasico: Button
    private lateinit var btnRolPro: Button
    private lateinit var btnRolAdmin: Button
    private lateinit var tvDescRol: TextView

    // ── Productos ──────────────────────────────────────────────────────
    private var listaProductos = listOf<ProductoData>()
    private lateinit var adapterProductos: ProductosAdminAdapter

    private val SERVICE_ROLE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZweHJ6c2RheWZzbHZodGxscXJ5Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3ODcxMjIwNCwiZXhwIjoyMDk0Mjg4MjA0fQ.qZuVTamPKZH9_DB3XQkhEq3R5T_HzA-BzUg3hMMm8lc"
    private val SUPABASE_URL = "https://vpxrzsdayfslvhtllqry.supabase.co"

    @OptIn(InternalSerializationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_panel_admin)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        viewFlipper       = findViewById(R.id.viewFlipper)
        tabUsuarios       = findViewById(R.id.tabUsuarios)
        tabAgregarUsuario = findViewById(R.id.tabAgregarUsuario)
        tabProductos      = findViewById(R.id.tabProductos)

        tabUsuarios.setOnClickListener       { cambiarTab(0) }
        tabAgregarUsuario.setOnClickListener { cambiarTab(1) }
        tabProductos.setOnClickListener      { cambiarTab(2) }

        setupTabUsuarios()
        setupTabAgregarUsuario()
        setupTabProductos()

        cargarUsuarios()
        cargarProductos()
    }

    // ══════════════════════════════════════════════════════════════════
    //  TAB 1 — USUARIOS
    // ══════════════════════════════════════════════════════════════════
    @OptIn(InternalSerializationApi::class)
    private fun setupTabUsuarios() {
        val recyclerUsuarios = findViewById<RecyclerView>(R.id.recyclerUsuarios)
        recyclerUsuarios.layoutManager = LinearLayoutManager(this)
        adapterUsuarios = UsuariosAdminAdapter(emptyList()) { usuario, nuevoRol ->
            cambiarRolUsuario(usuario, nuevoRol)
        }
        recyclerUsuarios.adapter = adapterUsuarios

        findViewById<EditText>(R.id.etBuscarUsuario)
            .addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    val q = s.toString().lowercase()
                    val filtrada = listaUsuarios.filter {
                        it.nombre.lowercase().contains(q) ||
                                it.correo.lowercase().contains(q) ||
                                it.usuario.lowercase().contains(q)
                    }
                    adapterUsuarios.actualizarLista(filtrada)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
    }

    private fun cargarUsuarios() {
        lifecycleScope.launch {
            try {
                val usuarios = SupabaseClient.client.postgrest["Usuarios"]
                    .select { }.decodeList<UsuarioRepository.UsuarioData>()
                listaUsuarios = usuarios
                val total  = usuarios.size
                val pro    = usuarios.count { it.rol == "pro" }
                val basic  = usuarios.count { it.rol == "cliente" }
                val admins = usuarios.count { it.rol == "administrador" }
                runOnUiThread {
                    adapterUsuarios.actualizarLista(usuarios)
                    findViewById<TextView>(R.id.tvTotalUsuarios).text =
                        "$total usuario${if (total != 1) "s" else ""} registrado${if (total != 1) "s" else ""}"
                    findViewById<TextView>(R.id.tvStatTotal).text  = total.toString()
                    findViewById<TextView>(R.id.tvStatPro).text    = pro.toString()
                    findViewById<TextView>(R.id.tvStatBasic).text  = basic.toString()
                    findViewById<TextView>(R.id.tvStatAdmin).text  = admins.toString()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@PanelAdminActivity,
                        "Error cargando usuarios: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  TAB 2 — AGREGAR USUARIO
    // ══════════════════════════════════════════════════════════════════
    private fun setupTabAgregarUsuario() {
        btnRolBasico = findViewById(R.id.btnRolBasico)
        btnRolPro    = findViewById(R.id.btnRolPro)
        btnRolAdmin  = findViewById(R.id.btnRolAdmin)
        tvDescRol    = findViewById(R.id.tvDescripcionRol)

        btnRolBasico.setOnClickListener { seleccionarRol("cliente") }
        btnRolPro.setOnClickListener    { seleccionarRol("pro") }
        btnRolAdmin.setOnClickListener  { seleccionarRol("administrador") }
        seleccionarRol("cliente")

        findViewById<Button>(R.id.btnCrearUsuario).setOnClickListener { crearUsuario() }
        findViewById<TextView>(R.id.tvCancelar).setOnClickListener { cambiarTab(0) }
    }

    private fun seleccionarRol(rol: String) {
        rolSeleccionado = rol
        val activo   = getColor(R.color.ColorPrincipal)
        val inactivo = getColor(R.color.background)
        listOf(btnRolBasico to "cliente", btnRolPro to "pro", btnRolAdmin to "administrador")
            .forEach { (btn, valor) ->
                btn.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    if (rol == valor) activo else inactivo)
                btn.setTextColor(if (rol == valor) getColor(R.color.black) else getColor(R.color.white))
            }
        tvDescRol.text = when (rol) {
            "cliente"       -> "Acceso estándar con funciones limitadas"
            "pro"           -> "Acceso completo a todas las funciones"
            "administrador" -> "Control total de la plataforma"
            else -> ""
        }
    }

    private fun crearUsuario() {
        val nombre   = findViewById<EditText>(R.id.etNuevoNombre).text.toString().trim()
        val apellido = findViewById<EditText>(R.id.etNuevoApellido).text.toString().trim()
        val email    = findViewById<EditText>(R.id.etNuevoEmail).text.toString().trim()
        val password = findViewById<EditText>(R.id.etNuevoPassword).text.toString()

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa nombre, correo y contraseña", Toast.LENGTH_SHORT).show()
            return
        }
        if (password.length < 6) {
            Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val userId = withContext(Dispatchers.IO) {
                    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                    val jsonBody = """{"email":"$email","password":"$password","email_confirm":true}"""
                    val body = okhttp3.RequestBody.create(mediaType, jsonBody)
                    val request = okhttp3.Request.Builder()
                        .url("$SUPABASE_URL/auth/v1/admin/users")
                        .addHeader("Authorization", "Bearer $SERVICE_ROLE_KEY")
                        .addHeader("apikey", SERVICE_ROLE_KEY)
                        .post(body).build()
                    val response = okhttp3.OkHttpClient().newCall(request).execute()
                    val responseText = response.body?.string() ?: throw Exception("Sin respuesta")
                    if (!response.isSuccessful) throw Exception(responseText)
                    org.json.JSONObject(responseText).getString("id")
                }

                SupabaseClient.client.postgrest["Usuarios"].insert(
                    UsuarioRepository.UsuarioData(
                        id = userId, usuario = email.substringBefore("@"),
                        nombre = nombre, apellidos = apellido,
                        correo = email, rol = rolSeleccionado
                    )
                )
                runOnUiThread {
                    Toast.makeText(this@PanelAdminActivity,
                        "Usuario creado con rol: $rolSeleccionado", Toast.LENGTH_SHORT).show()
                    cambiarTab(0); cargarUsuarios()
                    listOf(R.id.etNuevoNombre, R.id.etNuevoApellido,
                        R.id.etNuevoEmail, R.id.etNuevoPassword)
                        .forEach { findViewById<EditText>(it).text.clear() }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@PanelAdminActivity,
                        "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  TAB 3 — PRODUCTOS
    // ══════════════════════════════════════════════════════════════════
    private fun setupTabProductos() {
        val recyclerProductos = findViewById<RecyclerView>(R.id.recyclerProductosAdmin)
        recyclerProductos.layoutManager = LinearLayoutManager(this)

        adapterProductos = ProductosAdminAdapter(emptyList()) { producto ->
            eliminarProducto(producto)
        }
        recyclerProductos.adapter = adapterProductos

        findViewById<Button>(R.id.btnAnadirProducto).setOnClickListener {
            anadirProducto()
        }
    }

    private fun cargarProductos() {
        lifecycleScope.launch {
            try {
                val productos = ProductoRepository.obtenerTodosAdmin()
                listaProductos = productos

                val total     = productos.size
                val insignias = productos.count { !it.insignia.isNullOrBlank() }
                val promedio  = if (productos.isEmpty()) 0.0
                else productos.sumOf { it.precio } / productos.size

                runOnUiThread {
                    adapterProductos.actualizarLista(productos)
                    // Stats exclusivas del tab Productos (IDs propios)
                    findViewById<TextView>(R.id.tvProdStatTotal).text    = total.toString()
                    findViewById<TextView>(R.id.tvProdStatInsignia).text = insignias.toString()
                    findViewById<TextView>(R.id.tvProdStatPromedio).text =
                        "$ ${String.format("%,.0f", promedio)}"
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@PanelAdminActivity,
                        "Error cargando productos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @OptIn(InternalSerializationApi::class)
    private fun anadirProducto() {
        val nombre      = findViewById<EditText>(R.id.etNombreProducto).text.toString().trim()
        val precioStr   = findViewById<EditText>(R.id.etPrecioProducto).text.toString().trim()
        val categoria   = findViewById<EditText>(R.id.etCategoriaProducto).text.toString().trim()
        val insignia    = findViewById<EditText>(R.id.etInsigniaProducto).text.toString().trim()
        val descripcion = findViewById<EditText>(R.id.etDescripcionProducto).text.toString().trim()

        // El campo de URL de imagen se agrega en el XML como etImagenUrl
        val imagenUrl = try {
            findViewById<EditText>(R.id.etImagenUrl).text.toString().trim()
        } catch (_: Exception) { "" }

        if (nombre.isEmpty() || precioStr.isEmpty() || categoria.isEmpty()) {
            Toast.makeText(this, "Nombre, precio y categoría son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }
        val precio = precioStr.toDoubleOrNull() ?: run {
            Toast.makeText(this, "El precio debe ser un número", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                ProductoRepository.crearProducto(
                    nombre      = nombre,
                    descripcion = descripcion,
                    precio      = precio,
                    categoria   = categoria.replaceFirstChar { it.uppercase() },
                    insignia    = insignia,
                    imagenUrl   = imagenUrl,
                    stock       = 0
                )
                runOnUiThread {
                    Toast.makeText(this@PanelAdminActivity,
                        "Producto \"$nombre\" añadido ✓", Toast.LENGTH_SHORT).show()
                    // Limpiar campos
                    listOf(R.id.etNombreProducto, R.id.etPrecioProducto,
                        R.id.etCategoriaProducto, R.id.etInsigniaProducto,
                        R.id.etDescripcionProducto)
                        .forEach { try { findViewById<EditText>(it).text.clear() } catch (_: Exception) {} }
                    try { findViewById<EditText>(R.id.etImagenUrl).text.clear() } catch (_: Exception) {}
                    cargarProductos()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@PanelAdminActivity,
                        "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun eliminarProducto(producto: ProductoData) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Eliminar \"${producto.nombre}\"? Esta acción no se puede deshacer.")
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    try {
                        ProductoRepository.eliminarProducto(producto.id)
                        runOnUiThread {
                            Toast.makeText(this@PanelAdminActivity,
                                "\"${producto.nombre}\" eliminado", Toast.LENGTH_SHORT).show()
                            cargarProductos()
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@PanelAdminActivity,
                                "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .show()
            .apply {
                getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(getColor(R.color.ColorRojo))
                getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(getColor(R.color.GrisTexto))
            }
    }

    // ══════════════════════════════════════════════════════════════════
    //  Cambiar rol usuario
    // ══════════════════════════════════════════════════════════════════
    @InternalSerializationApi
    private fun cambiarRolUsuario(usuario: UsuarioRepository.UsuarioData, nuevoRol: String) {
        lifecycleScope.launch {
            try {
                @kotlinx.serialization.Serializable
                data class RolUpdate(val rol: String)
                SupabaseClient.client.postgrest["Usuarios"]
                    .update(RolUpdate(rol = nuevoRol)) { filter { eq("id", usuario.id) } }
                runOnUiThread {
                    Toast.makeText(this@PanelAdminActivity,
                        "Rol de ${usuario.nombre} → $nuevoRol", Toast.LENGTH_SHORT).show()
                    cargarUsuarios()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@PanelAdminActivity,
                        "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════
    //  Cambiar tab
    // ══════════════════════════════════════════════════════════════════
    private fun cambiarTab(index: Int) {
        viewFlipper.displayedChild = index
        val activo   = getColor(R.color.ColorPrincipal)
        val inactivo = getColor(R.color.GrisTexto)
        tabUsuarios.setTextColor(if (index == 0) activo else inactivo)
        tabAgregarUsuario.setTextColor(if (index == 1) activo else inactivo)
        tabProductos.setTextColor(if (index == 2) activo else inactivo)
        // Recargar productos al entrar al tab
        if (index == 2) cargarProductos()
    }
}