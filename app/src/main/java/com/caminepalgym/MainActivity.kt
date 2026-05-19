package com.caminepalgym

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.caminepalgym.ui.inicio.HomeFragment
import com.caminepalgym.ui.main.entrenamiento.EntrenamientoFragment
import com.caminepalgym.ui.main.foro.ForoFragment
import com.caminepalgym.ui.main.perfil.PerfilFragment
import com.caminepalgym.ui.main.tienda.TiendaFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Forzar colores de barras
        WindowCompat.setDecorFitsSystemWindows(window, true)
        val colorFondo = ContextCompat.getColor(this, R.color.background)
        window.statusBarColor = colorFondo
        window.navigationBarColor = colorFondo
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNav = findViewById(R.id.bottom_nav)

        // Colores del menú
        val colorAmarillo = ContextCompat.getColor(this, R.color.ColorPrincipal)
        val colorGris = ContextCompat.getColor(this, R.color.GrisTexto)

        val estadoColores = android.content.res.ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(colorAmarillo, colorGris)
        )

        bottomNav.itemIconTintList = estadoColores
        bottomNav.itemTextColor = estadoColores
        bottomNav.setBackgroundColor(colorFondo)

        // Fragment inicial
        cargarFragment(HomeFragment())
        bottomNav.selectedItemId = R.id.nav_home

        // Bottom Navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home                 -> cargarFragment(HomeFragment())
                R.id.nav_bottom_entrenamiento -> cargarFragment(EntrenamientoFragment())
                R.id.nav_bottom_tienda        -> cargarFragment(TiendaFragment())
                R.id.nav_bottom_foro          -> cargarFragment(ForoFragment())
                R.id.nav_bottom_perfil        -> cargarFragment(PerfilFragment())
            }
            true
        }
    }

    // Función pública para que los fragments puedan navegar al tab de entrenamiento
    fun irAPerfil() {
        bottomNav.selectedItemId = R.id.nav_bottom_perfil
    }

    fun irAEntrenamiento() {
        bottomNav.selectedItemId = R.id.nav_bottom_entrenamiento
    }

    private fun cargarFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}