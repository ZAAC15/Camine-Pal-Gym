package com.caminepalgym.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.caminepalgym.MainActivity
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import com.caminepalgym.data.UsuarioRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val WEB_CLIENT_ID = "391549653007-2ql21ndrfg0ull8b1c3m81udcdldiki5.apps.googleusercontent.com"
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001

    // SharedPreferences para guardar credenciales para biometría
    private val PREFS_AUTH = "auth_prefs"
    private val KEY_EMAIL  = "saved_email"
    private val KEY_PASS   = "saved_pass"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val edtCorreo   = findViewById<EditText>(R.id.edtCorreo)
        val edtPassword = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.input_password)
        val btnIngresar = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.btnIngresar)
        val btnGoogle   = findViewById<android.widget.ImageView>(R.id.btnGoogle)
        val imgHuella   = findViewById<android.widget.ImageView>(R.id.imgHuella)
        val imgFacial   = findViewById<android.widget.ImageView>(R.id.imgFacial)
        val txtRegistro = findViewById<TextView>(R.id.txtRegistro)
        val txtOlvido   = findViewById<TextView>(R.id.txtOlvido)

        // ── Login con correo y contraseña ──
        btnIngresar.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val pass   = edtPassword.text.toString()

            if (correo.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    SupabaseClient.client.auth.signInWith(Email) {
                        email    = correo
                        password = pass
                    }
                    // Guardar credenciales para futuros logins biométricos
                    guardarCredenciales(correo, pass)
                    irAMain()
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity,
                            "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // ── Login con Google ──
        btnGoogle.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                startActivityForResult(googleSignInClient.signInIntent, RC_SIGN_IN)
            }
        }

        // ── Biometría ──
        imgHuella.setOnClickListener { mostrarBiometria() }
        imgFacial.setOnClickListener { mostrarBiometria() }

        // ── Navegación ──
        txtRegistro.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        txtOlvido?.setOnClickListener {
            startActivity(Intent(this, RecuperarContrasenaActivity::class.java))
        }
    }

    // ── Google Sign In result ──
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken ?: run {
                    Toast.makeText(this, "No se obtuvo el token de Google", Toast.LENGTH_SHORT).show()
                    return
                }

                lifecycleScope.launch {
                    try {
                        // 1. Autenticar con Supabase
                        SupabaseClient.client.auth.signInWith(IDToken) {
                            this.idToken = idToken
                            provider = io.github.jan.supabase.auth.providers.Google
                        }

                        val user         = SupabaseClient.client.auth.currentUserOrNull()
                        val userId       = user?.id ?: return@launch
                        val correoGoogle = user.email ?: ""
                        val nombreGoogle = account.displayName ?: "Usuario"

                        // 2. Solo insertar en Usuarios si NO existe ya
                        val usuarioExistente = UsuarioRepository.obtenerUsuarioActual()
                        if (usuarioExistente == null) {
                            // Primera vez que inicia con Google → crear perfil
                            val partes = nombreGoogle.trim().split(" ")
                            val nombre    = partes.firstOrNull() ?: nombreGoogle
                            val apellidos = partes.drop(1).joinToString(" ")

                            try {
                                UsuarioRepository.insertarUsuario(
                                    usuario   = correoGoogle.substringBefore("@"),
                                    nombre    = nombre,
                                    apellidos = apellidos,
                                    correo    = correoGoogle,
                                    celular   = ""
                                )
                            } catch (e: Exception) {
                                // Si falla el insert (ej. duplicado), ignorar y continuar
                            }
                        }
                        // Si ya existe → no hace nada, va directo al main
                        irAMain()

                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(this@LoginActivity,
                                "Error Google: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }

            } catch (e: ApiException) {
                Toast.makeText(this,
                    "Error Google: ${e.statusCode} - ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ── Biometría ──
    private fun mostrarBiometria() {
        val biometricManager = BiometricManager.from(this)
        val disponible = biometricManager.canAuthenticate(BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL)

        if (disponible != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this, "Biometría no disponible en este dispositivo", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar si hay credenciales guardadas
        val prefs  = getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE)
        val correo = prefs.getString(KEY_EMAIL, null)
        val pass   = prefs.getString(KEY_PASS, null)

        if (correo == null || pass == null) {
            Toast.makeText(this,
                "Primero inicia sesión una vez con correo o Google",
                Toast.LENGTH_LONG).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(this)

        val prompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    // Biometría confirmada → autenticar con Supabase usando credenciales guardadas
                    lifecycleScope.launch {
                        try {
                            // Intentar restaurar sesión existente primero
                            val sesionActiva = SupabaseClient.client.auth.currentSessionOrNull()
                            if (sesionActiva != null) {
                                irAMain()
                                return@launch
                            }
                            // Si no hay sesión, hacer login con correo guardado
                            SupabaseClient.client.auth.signInWith(Email) {
                                email    = correo
                                password = pass
                            }
                            irAMain()
                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity,
                                    "Error al restaurar sesión: ${e.message}",
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    Toast.makeText(this@LoginActivity,
                        "Biometría no reconocida", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                        errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        Toast.makeText(this@LoginActivity,
                            "Error: $errString", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Camine Pal Gym")
            .setSubtitle("Confirma tu identidad para ingresar")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
            .build()

        prompt.authenticate(promptInfo)
    }

    // ── Guardar credenciales para biometría ──
    private fun guardarCredenciales(correo: String, pass: String) {
        getSharedPreferences(PREFS_AUTH, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_EMAIL, correo)
            .putString(KEY_PASS, pass)
            .apply()
    }

    // ── Ir a Main o Onboarding ──
    private fun irAMain() {
        lifecycleScope.launch {
            try {
                val tieneMedidas = UsuarioRepository.tieneMedidas()
                runOnUiThread {
                    if (tieneMedidas) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    } else {
                        startActivity(Intent(this@LoginActivity, OnboardingActivity::class.java))
                    }
                    finish()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}