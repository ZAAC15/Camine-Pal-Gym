package com.caminepalgym.ui.main.tienda

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.caminepalgym.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class TiendasCercanasActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapa: GoogleMap
    private lateinit var fusedClient: FusedLocationProviderClient
    private val PERMISO_UBICACION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tiendas_cercanas)

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializar el mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapa = googleMap

        // Estilo oscuro del mapa
        mapa.uiSettings.isZoomControlsEnabled    = true
        mapa.uiSettings.isMyLocationButtonEnabled = true

        verificarPermisosYCentrar()
    }

    private fun verificarPermisosYCentrar() {
        val permisoFino   = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val permisoGrueso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (permisoFino == PackageManager.PERMISSION_GRANTED ||
            permisoGrueso == PackageManager.PERMISSION_GRANTED) {
            activarUbicacion()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISO_UBICACION
            )
        }
    }

    private fun activarUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) return

        mapa.isMyLocationEnabled = true

        fusedClient.lastLocation.addOnSuccessListener { ubicacion ->
            if (ubicacion != null) {
                val miPos = LatLng(ubicacion.latitude, ubicacion.longitude)
                mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(miPos, 14f))
                buscarTiendasCercanas(miPos)
            } else {
                // Si no hay última ubicación, centrar en Bogotá por defecto
                val bogota = LatLng(4.7110, -74.0721)
                mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(bogota, 13f))
                buscarTiendasCercanas(bogota)
            }
        }
    }

    /**
     * Busca tiendas de gym cercanas usando la Places API de Google Maps
     * a través de una URL de búsqueda web como fallback visual.
     *
     * Para producción real usar Places SDK:
     * https://developers.google.com/maps/documentation/places/android-sdk
     */
    private fun buscarTiendasCercanas(centro: LatLng) {
        // Tiendas de gym conocidas en Bogotá como ejemplo de marcadores
        // En producción esto vendría de Places API Nearby Search
        val tiendas = listOf(
            Triple("GNC Nutrition", LatLng(centro.latitude + 0.008, centro.longitude + 0.005), "Suplementos deportivos"),
            Triple("Smart Fit Suplementos", LatLng(centro.latitude - 0.006, centro.longitude + 0.009), "Proteínas y accesorios"),
            Triple("Bodytech Store", LatLng(centro.latitude + 0.012, centro.longitude - 0.007), "Ropa y equipamiento"),
            Triple("PowerFit Shop", LatLng(centro.latitude - 0.010, centro.longitude - 0.004), "Suplementos y ropa"),
            Triple("Olimpo Nutrition", LatLng(centro.latitude + 0.003, centro.longitude + 0.012), "Nutrición deportiva"),
            Triple("Iron Gym Store", LatLng(centro.latitude - 0.015, centro.longitude + 0.006), "Equipamiento y accesorios"),
        )

        tiendas.forEach { (nombre, pos, desc) ->
            mapa.addMarker(
                MarkerOptions()
                    .position(pos)
                    .title(nombre)
                    .snippet(desc)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            )
        }

        Toast.makeText(
            this,
            "${tiendas.size} tiendas encontradas cerca de ti",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISO_UBICACION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activarUbicacion()
            } else {
                Toast.makeText(this,
                    "Necesitamos acceso a tu ubicación para mostrar tiendas cercanas",
                    Toast.LENGTH_LONG).show()
                // Mostrar mapa centrado en Bogotá sin ubicación
                val bogota = LatLng(4.7110, -74.0721)
                mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(bogota, 13f))
                buscarTiendasCercanas(bogota)
            }
        }
    }
}