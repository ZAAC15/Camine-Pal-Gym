package com.caminepalgym.ui.main.perfil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import com.caminepalgym.data.RutinaRepository
import com.caminepalgym.data.UsuarioRepository
import com.caminepalgym.ui.auth.LoginActivity
import com.caminepalgym.ui.main.admin.PanelAdminActivity
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class PerfilFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_perfil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnEditarPerfil).setOnClickListener {
            startActivity(Intent(requireContext(), EditarPerfilActivity::class.java))
        }
        view.findViewById<View>(R.id.itemNotificaciones).setOnClickListener {
            startActivity(Intent(requireContext(), NotificacionesActivity::class.java))
        }
        view.findViewById<View>(R.id.itemPrivacidad).setOnClickListener {
            startActivity(Intent(requireContext(), PrivacidadActivity::class.java))
        }
        view.findViewById<View>(R.id.itemTema).setOnClickListener {
            startActivity(Intent(requireContext(), TemaActivity::class.java))
        }
        view.findViewById<View>(R.id.itemSuscripcion).setOnClickListener {
            startActivity(Intent(requireContext(), SuscripcionActivity::class.java))
        }
        view.findViewById<View>(R.id.itemAyuda).setOnClickListener {
            startActivity(Intent(requireContext(), AyudaSoporteActivity::class.java))
        }
        view.findViewById<View>(R.id.id_administrativo).setOnClickListener {
            startActivity(Intent(requireContext(), PanelAdminActivity::class.java))
        }
        view.findViewById<View>(R.id.itemCerrarSesion).setOnClickListener {
            lifecycleScope.launch {
                try { SupabaseClient.client.auth.signOut() } catch (_: Exception) { }
                if (!isAdded || context == null) return@launch
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        cargarPerfil(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { cargarPerfil(it) }
    }

    private fun cargarPerfil(view: View) {
        val tvNombre   = view.findViewById<TextView>(R.id.tvNombrePerfil)
        val tvAvatar   = view.findViewById<TextView>(R.id.tvAvatarPerfil)
        val tvMiembro  = view.findViewById<TextView>(R.id.tvMiembroDesde)
        val tvRacha    = view.findViewById<TextView>(R.id.tvRachaPerfil)
        val tvNivel    = view.findViewById<TextView>(R.id.tvNivelPerfil)
        val tvEntrenam = view.findViewById<TextView>(R.id.tvEntrenamientosPerfil)
        val tvBadgeRol = view.findViewById<TextView>(R.id.tvBadgeRol)
        val itemAdmin  = view.findViewById<View>(R.id.id_administrativo)

        lifecycleScope.launch {
            try {
                val usuario             = UsuarioRepository.obtenerUsuarioActual()
                val medidas             = UsuarioRepository.obtenerMedidas()
                val racha               = RutinaRepository.obtenerRachaActual()
                val totalEntrenamientos = RutinaRepository.contarEntrenamientosCompletados()

                if (!isAdded || context == null) return@launch

                val nombre = "${usuario?.nombre ?: ""} ${usuario?.apellidos ?: ""}".trim()
                    .ifEmpty { "Usuario" }

                val iniciales = buildString {
                    nombre.split(" ").take(2).forEach {
                        append(it.firstOrNull()?.uppercaseChar() ?: "")
                    }
                }.ifEmpty { "U" }

                tvNombre?.text  = nombre
                tvAvatar?.text  = iniciales
                tvMiembro?.text = "Miembro de Camine Pal Gym"
                tvBadgeRol?.text = when (usuario?.rol) {
                    "administrador" -> "Administrador"
                    "pro"           -> "Membresía · Pro"
                    else            -> "Membresía · Básico"
                }
                tvRacha?.text    = racha.toString()
                tvNivel?.text    = medidas?.nivelCondicion ?: "--"
                tvEntrenam?.text = totalEntrenamientos.toString()
                itemAdmin?.visibility =
                    if (usuario?.rol == "administrador") View.VISIBLE else View.GONE

            } catch (e: Exception) {
                if (!isAdded || context == null) return@launch
                tvNombre?.text        = "Usuario"
                tvAvatar?.text        = "U"
                tvBadgeRol?.text      = "Membresía · Básico"
                itemAdmin?.visibility = View.GONE
            }
        }
    }
}