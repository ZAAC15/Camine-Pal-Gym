package com.caminepalgym.ui.main.perfil

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import com.caminepalgym.data.UsuarioRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import java.util.Calendar

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var etNombres: EditText
    private lateinit var etApellido: EditText
    private lateinit var etUsuario: EditText
    private lateinit var etBiografia: EditText
    private lateinit var etEmail: EditText
    private lateinit var etCelular: EditText
    private lateinit var tvFechaCumple: TextView
    private lateinit var etAltura: EditText
    private lateinit var etPeso: EditText
    private lateinit var spinnerMeta: Spinner
    private lateinit var etInstagram: EditText
    private lateinit var etTwitter: EditText
    private lateinit var tvAvatar: TextView

    private var fechaSeleccionada: String? = null

    @OptIn(InternalSerializationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        // Referencias
        etNombres     = findViewById(R.id.etNombres)
        etApellido    = findViewById(R.id.etApellido)
        etUsuario     = findViewById(R.id.etUsuario)
        etBiografia   = findViewById(R.id.etBiografia)
        etEmail       = findViewById(R.id.etEmail)
        etCelular     = findViewById(R.id.etCelular)
        tvFechaCumple = findViewById(R.id.tvFechaCumple)
        etAltura      = findViewById(R.id.etAltura)
        etPeso        = findViewById(R.id.etPeso)
        spinnerMeta   = findViewById(R.id.spinnerMeta)
        etInstagram   = findViewById(R.id.etInstagram)
        etTwitter     = findViewById(R.id.etTwitter)

        // Spinner de metas
        val metas = listOf(
            "Perder peso", "Ganar masa muscular",
            "Mejorar resistencia", "Mantenerse en forma",
            "Aumentar fuerza", "Otro"
        )
        val adapterSpinner = ArrayAdapter(this, android.R.layout.simple_spinner_item, metas)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMeta.adapter = adapterSpinner

        // Selector de fecha
        tvFechaCumple.setOnClickListener { mostrarDatePicker() }
        findViewById<ImageView>(R.id.btnCambiarFoto).setOnClickListener {
            Toast.makeText(this, "Próximamente", Toast.LENGTH_SHORT).show()
        }

        // Botones guardar
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.btnGuardarHeader).setOnClickListener { guardarCambios() }
        findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener { guardarCambios() }

        // Cargar datos actuales
        cargarDatos()
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            try {
                val usuario = UsuarioRepository.obtenerUsuarioActual()
                val medidas = UsuarioRepository.obtenerMedidas()

                if (usuario == null) return@launch

                runOnUiThread {
                    // Iniciales avatar
                    val nombre = "${usuario.nombre} ${usuario.apellidos}".trim()
                    val iniciales = buildString {
                        nombre.split(" ").take(2).forEach {
                            append(it.firstOrNull()?.uppercaseChar() ?: "")
                        }
                    }.ifEmpty { "U" }
                    findViewById<TextView>(R.id.tvAvatarEditar)?.text = iniciales

                    // Rellenar campos
                    etNombres.setText(usuario.nombre)
                    etApellido.setText(usuario.apellidos)
                    etUsuario.setText(usuario.usuario)
                    etEmail.setText(usuario.correo)
                    etCelular.setText(usuario.celular ?: "")
                    etBiografia.setText(usuario.biografia ?: "")
                    etInstagram.setText(usuario.instagram ?: "")
                    etTwitter.setText(usuario.twitter ?: "")

                    usuario.fechaCumpleanos?.let {
                        fechaSeleccionada = it
                        tvFechaCumple.text = it
                    }

                    // Medidas
                    medidas?.altura?.let { etAltura.setText(it.toInt().toString()) }
                    medidas?.peso?.let { etPeso.setText(it.toString()) }

                    // Spinner meta
                    val metas = listOf(
                        "Perder peso", "Ganar masa muscular",
                        "Mejorar resistencia", "Mantenerse en forma",
                        "Aumentar fuerza", "Otro"
                    )
                    val metaIndex = metas.indexOf(medidas?.objetivo ?: "")
                    if (metaIndex >= 0) spinnerMeta.setSelection(metaIndex)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@EditarPerfilActivity,
                        "Error cargando datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @InternalSerializationApi
    private fun guardarCambios() {
        val nombre    = etNombres.text.toString().trim()
        val apellido  = etApellido.text.toString().trim()
        val usuario   = etUsuario.text.toString().trim()
        val celular   = etCelular.text.toString().trim()
        val bio       = etBiografia.text.toString().trim()
        val insta     = etInstagram.text.toString().trim()
        val twitter   = etTwitter.text.toString().trim()
        val alturaStr = etAltura.text.toString().trim()
        val pesoStr   = etPeso.text.toString().trim()
        val meta      = spinnerMeta.selectedItem?.toString() ?: ""

        if (nombre.isEmpty() || apellido.isEmpty()) {
            Toast.makeText(this, "Nombre y apellido son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: return@launch

                // ── Actualizar tabla Usuarios ──
                @kotlinx.serialization.Serializable
                data class UsuarioUpdate(
                    val nombre: String,
                    val apellidos: String,
                    val usuario: String,
                    val celular: String,
                    val biografia: String,
                    val instagram: String,
                    val twitter: String,
                    @kotlinx.serialization.SerialName("fecha_cumpleanos")
                    val fechaCumpleanos: String?
                )

                SupabaseClient.client.postgrest["Usuarios"]
                    .update(UsuarioUpdate(
                        nombre          = nombre,
                        apellidos       = apellido,
                        usuario         = usuario,
                        celular         = celular,
                        biografia       = bio,
                        instagram       = insta,
                        twitter         = twitter,
                        fechaCumpleanos = fechaSeleccionada
                    )) {
                        filter { eq("id", userId) }
                    }

                // ── Actualizar MedidasUsuario si se llenaron campos ────────
                // La tabla MedidasUsuario usa "id" como FK hacia auth.users,
                // NO "usuario_id". Se hace upsert para insertar si no existe.
                if (alturaStr.isNotEmpty() || pesoStr.isNotEmpty()) {
                    val altura = alturaStr.toDoubleOrNull()
                    val peso   = pesoStr.toDoubleOrNull()

                    @kotlinx.serialization.Serializable
                    data class MedidasUpdate(
                        val id: String,          // clave primaria de MedidasUsuario
                        val altura: Double?,
                        val peso: Double?,
                        val objetivo: String
                    )

                    // Usamos upsert: si ya existe la fila la actualiza,
                    // si no existe la crea.
                    SupabaseClient.client.postgrest["MedidasUsuario"]
                        .upsert(MedidasUpdate(
                            id       = userId,   // ← mismo id del usuario
                            altura   = altura,
                            peso     = peso,
                            objetivo = meta
                        ))
                }

                runOnUiThread {
                    Toast.makeText(this@EditarPerfilActivity,
                        "Perfil actualizado ✓", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this@EditarPerfilActivity,
                        "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun mostrarDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                fechaSeleccionada = "$year-${(month + 1).toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
                tvFechaCumple.text = fechaSeleccionada
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}