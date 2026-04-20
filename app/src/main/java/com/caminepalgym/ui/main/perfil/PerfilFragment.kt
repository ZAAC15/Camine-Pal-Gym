package com.caminepalgym.ui.main.perfil

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caminepalgym.R
import com.caminepalgym.ui.main.admin.PanelAdminActivity
import kotlin.jvm.java

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
            // aquí va tu lógica de cerrar sesión
        }
    }
}