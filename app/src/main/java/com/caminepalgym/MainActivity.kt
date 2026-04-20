package com.caminepalgym

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)


        // Fragment inicial
        cargarFragment(HomeFragment())
        bottomNav.selectedItemId = R.id.nav_home

        // Bottom Navigation
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> cargarFragment(HomeFragment())

                R.id.nav_bottom_entrenamiento ->
                    cargarFragment(EntrenamientoFragment())

                R.id.nav_bottom_tienda ->
                    cargarFragment(TiendaFragment())

                R.id.nav_bottom_foro ->
                    cargarFragment(ForoFragment())

                R.id.nav_bottom_perfil ->
                    cargarFragment(PerfilFragment())
            }
            true
        }
    }

    private fun cargarFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}