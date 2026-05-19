package com.caminepalgym.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.caminepalgym.MainActivity
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import com.caminepalgym.data.UsuarioRepository
import com.google.android.material.textfield.TextInputEditText
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etUsuario    = findViewById<EditText>(R.id.edtUsuario)
        val etNombre     = findViewById<EditText>(R.id.edtNombre)
        val etApellidos  = findViewById<EditText>(R.id.edtApellidos)
        val etCorreo     = findViewById<EditText>(R.id.edtCorreo)
        val etCelular    = findViewById<EditText>(R.id.edtCelular)
        val etPassword   = findViewById<TextInputEditText>(R.id.edtPassword)
        val etPassword2  = findViewById<TextInputEditText>(R.id.edtPassword2)
        val checkTyC     = findViewById<CheckBox>(R.id.checkTyC)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val txtIrLogin   = findViewById<TextView>(R.id.txtIrLogin)

        btnRegistrar.setOnClickListener {
            val usuario   = etUsuario.text.toString().trim()
            val nombre    = etNombre.text.toString().trim()
            val apellidos = etApellidos.text.toString().trim()
            val correo    = etCorreo.text.toString().trim()
            val celular   = etCelular.text.toString().trim()
            val password  = etPassword.text.toString()
            val password2 = etPassword2.text.toString()

            if (usuario.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != password2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener mínimo 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!checkTyC.isChecked) {
                Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signUpWith(Email) {
                        email = correo
                        this.password = password
                    }

                    UsuarioRepository.insertarUsuario(
                        usuario = usuario,
                        nombre = nombre,
                        apellidos = apellidos,
                        correo = correo,
                        celular = celular
                    )

                    runOnUiThread {
                        Toast.makeText(
                            this@RegisterActivity,
                            "¡Registro exitoso! Bienvenido a Camine Pal Gym",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(Intent(this@RegisterActivity, OnboardingActivity::class.java))
                        finish()
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        txtIrLogin.setOnClickListener { finish() }
    }
}