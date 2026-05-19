package com.caminepalgym.ui.main.foro

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R
import com.caminepalgym.SupabaseClient
import com.caminepalgym.data.PostRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

class ForoFragment : Fragment() {

    private lateinit var adapter: PostAdapter
    private var usuarioActualId = ""
    private var chipActual = "Tendencias"

    // Chips y sus etiquetas
    private val chips = listOf("Tendencias", "Transformaciones", "Técnica", "Nutrición", "General")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_foro, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usuarioActualId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: ""

        // ── RecyclerView ──
        val recycler = view.findViewById<RecyclerView>(R.id.rvForo)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = PostAdapter(
            lista            = emptyList(),
            usuarioActualId  = usuarioActualId,
            onLike           = { post, pos -> darLike(post, pos) },
            onEliminar       = { post -> confirmarEliminar(post) }
        )
        recycler.adapter = adapter

        // ── Chips dinámicos ──
        val contenedorChips = view.findViewById<LinearLayout>(R.id.contenedorChips)
        contenedorChips.removeAllViews()

        chips.forEach { etiqueta ->
            val chip = TextView(requireContext()).apply {
                text = etiqueta
                setPadding(48, 24, 48, 24)
                textSize = 13f
                val lp = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                lp.marginEnd = 24
                layoutParams = lp
                setOnClickListener { seleccionarChip(etiqueta, contenedorChips) }
            }
            contenedorChips.addView(chip)
        }
        // Seleccionar Tendencias por defecto
        seleccionarChip("Tendencias", contenedorChips)

        // ── FAB crear post ──
        view.findViewById<FloatingActionButton>(R.id.fabAgregar).setOnClickListener {
            startActivity(Intent(requireContext(), CrearPostActivity::class.java))
        }
    }

    private fun seleccionarChip(etiqueta: String, contenedor: LinearLayout) {
        chipActual = etiqueta
        // Actualizar estilos
        for (i in 0 until contenedor.childCount) {
            val chip = contenedor.getChildAt(i) as? TextView ?: continue
            val activo = chip.text == etiqueta
            chip.setBackgroundResource(
                if (activo) R.drawable.bg_chip_selected else R.drawable.bg_chip
            )
            chip.setTextColor(requireContext().getColor(
                if (activo) R.color.black else R.color.white
            ))
        }
        cargarPosts()
    }

    private fun cargarPosts() {
        lifecycleScope.launch {
            try {
                val posts = PostRepository.obtenerPosts(chipActual)
                adapter.actualizarLista(posts)
            } catch (e: Exception) {
                Toast.makeText(requireContext(),
                    "Error cargando posts: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @OptIn(InternalSerializationApi::class)
    private fun darLike(post: com.caminepalgym.data.PostData, position: Int) {
        lifecycleScope.launch {
            try {
                val ahoraTieneLike = PostRepository.toggleLike(post.id)
                adapter.marcarLike(post.id, ahoraTieneLike)
                cargarPosts() // recargar para mostrar el contador actualizado
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmarEliminar(post: com.caminepalgym.data.PostData) {
        AlertDialog.Builder(requireContext())
            .setTitle("Eliminar post")
            .setMessage("¿Eliminar esta publicación? Esta acción no se puede deshacer.")
            .setNegativeButton("Cancelar") { d, _ -> d.dismiss() }
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    try {
                        PostRepository.eliminarPost(post.id)
                        cargarPosts()
                        Toast.makeText(requireContext(), "Post eliminado", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
            .apply {
                getButton(AlertDialog.BUTTON_POSITIVE)
                    ?.setTextColor(requireContext().getColor(R.color.ColorRojo))
                getButton(AlertDialog.BUTTON_NEGATIVE)
                    ?.setTextColor(requireContext().getColor(R.color.GrisTexto))
            }
    }

    override fun onResume() {
        super.onResume()
        cargarPosts()
    }
}