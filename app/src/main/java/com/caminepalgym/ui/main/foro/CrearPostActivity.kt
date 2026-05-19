package com.caminepalgym.ui.main.foro

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import com.caminepalgym.data.PostRepository
import com.caminepalgym.data.UsuarioRepository
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.util.UUID

class CrearPostActivity : AppCompatActivity() {

    private var modoFoto = false
    private var imagenUri: Uri? = null
    private lateinit var imgPreview: ImageView
    private lateinit var btnSeleccionarFoto: Button
    private lateinit var tvSubiendo: TextView
    private lateinit var boxImagen: LinearLayout

    // Lanzador del selector de imágenes del sistema
    private val selectorImagen = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imagenUri = uri
            imgPreview.setImageURI(uri)
            imgPreview.visibility = View.VISIBLE
            btnSeleccionarFoto.text = "Cambiar imagen"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_post)

        val tabTexto       = findViewById<TextView>(R.id.tabTexto)
        val tabFoto        = findViewById<TextView>(R.id.tabFoto)
        boxImagen          = findViewById(R.id.boxImagen)
        imgPreview         = findViewById(R.id.imgPreview)
        btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto)
        val inputTitulo    = findViewById<EditText>(R.id.inputTitulo)
        val inputContenido = findViewById<EditText>(R.id.inputContenido)
        val spinnerEtiq    = findViewById<Spinner>(R.id.spinnerEtiqueta)
        val tvNombre       = findViewById<TextView>(R.id.tvNombreUsuario)
        val tvAvatar       = findViewById<TextView>(R.id.tvAvatarCrear)
        val btnPublicar    = findViewById<Button>(R.id.btnPublicar)
        tvSubiendo         = findViewById(R.id.tvSubiendo)

        // ── Cargar nombre del usuario ──────────────────────────────────
        lifecycleScope.launch {
            val usuario = UsuarioRepository.obtenerUsuarioActual()
            runOnUiThread {
                if (usuario != null) {
                    tvNombre.text = "${usuario.nombre} ${usuario.apellidos}".trim()
                    val iniciales = "${usuario.nombre.firstOrNull() ?: ""}${usuario.apellidos.firstOrNull() ?: ""}".uppercase()
                    tvAvatar.text = iniciales.ifEmpty { "U" }
                }
            }
        }

        // ── Spinner etiquetas ──────────────────────────────────────────
        val etiquetas = listOf("General", "Tendencias", "Transformaciones", "Técnica", "Nutrición")
        spinnerEtiq.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, etiquetas)
            .also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        // ── Tabs ───────────────────────────────────────────────────────
        tabTexto.setOnClickListener {
            modoFoto = false
            tabTexto.setBackgroundResource(R.drawable.bg_chip_selected)
            tabTexto.setTextColor(getColor(android.R.color.black))
            tabFoto.setBackgroundResource(android.R.color.transparent)
            tabFoto.setTextColor(getColor(R.color.white))
            boxImagen.visibility = View.GONE
        }

        tabFoto.setOnClickListener {
            modoFoto = true
            tabFoto.setBackgroundResource(R.drawable.bg_chip_selected)
            tabFoto.setTextColor(getColor(android.R.color.black))
            tabTexto.setBackgroundResource(android.R.color.transparent)
            tabTexto.setTextColor(getColor(R.color.white))
            boxImagen.visibility = View.VISIBLE
        }

        // ── Botón seleccionar foto ─────────────────────────────────────
        btnSeleccionarFoto.setOnClickListener {
            selectorImagen.launch("image/*")
        }

        // ── Publicar ───────────────────────────────────────────────────
        btnPublicar.setOnClickListener {
            val titulo    = inputTitulo.text.toString().trim()
            val contenido = inputContenido.text.toString().trim()
            val etiqueta  = spinnerEtiq.selectedItem?.toString() ?: "General"
            val nombre    = tvNombre.text?.toString() ?: "Usuario"

            if (contenido.isEmpty()) {
                Toast.makeText(this, "El contenido no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Deshabilitar botón mientras publica
            btnPublicar.isEnabled = false
            btnPublicar.text = "Publicando..."

            lifecycleScope.launch {
                try {
                    var imagenUrl: String? = null

                    // Si hay imagen, subirla primero a Supabase Storage
                    if (modoFoto && imagenUri != null) {
                        runOnUiThread { tvSubiendo.visibility = View.VISIBLE }
                        imagenUrl = subirImagenAStorage(imagenUri!!)
                        runOnUiThread { tvSubiendo.visibility = View.GONE }
                    }

                    PostRepository.crearPost(
                        titulo    = titulo,
                        contenido = contenido,
                        imagenUrl = imagenUrl,
                        etiqueta  = etiqueta,
                        nombre    = nombre
                    )

                    runOnUiThread {
                        Toast.makeText(this@CrearPostActivity,
                            "¡Publicación creada! 🎉", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        btnPublicar.isEnabled = true
                        btnPublicar.text = getString(R.string.post)
                        tvSubiendo.visibility = View.GONE
                        Toast.makeText(this@CrearPostActivity,
                            "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // ── Botón atrás ────────────────────────────────────────────────
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    /**
     * Sube la imagen al bucket "posts" de Supabase Storage
     * y devuelve la URL pública.
     */
    private suspend fun subirImagenAStorage(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw Exception("No se pudo leer la imagen")

        val bytes = inputStream.readBytes()
        inputStream.close()

        val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
        val extension = mimeType.substringAfter("/").replace("jpeg", "jpg")
        val nombreArchivo = "posts/${UUID.randomUUID()}.$extension"

        // Supabase Storage 3.x — upload con ByteArray directo
        SupabaseClient.client.storage.from("posts").upload(
            path = nombreArchivo,
            data = bytes
        )

        return SupabaseClient.client.storage.from("posts").publicUrl(nombreArchivo)
    }
}