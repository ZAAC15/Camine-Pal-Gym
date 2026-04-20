package com.caminepalgym.ui.main.foro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caminepalgym.R
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ForoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_foro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.rvForo)

        val lista = listOf(
            Post("Juan Rojas", "Empecé en 95kg, ahora 78kg 🔥", R.drawable.post1),
            Post("Brayan Cuervo", "Nuevo PR en peso muerto 💪", R.drawable.post2),
            Post("Santiago", "La constancia es la clave 🚀", R.drawable.post3)
        )

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = PostAdapter(lista)

        val fab = view.findViewById<FloatingActionButton>(R.id.fabAgregar)

        fab.setOnClickListener {
            val intent = Intent(requireContext(), CrearPostActivity::class.java)
            startActivity(intent)
        }
    }
}
