package com.caminepalgym.ui.main.foro
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.caminepalgym.R

class PostAdapter(private val lista: List<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val usuario: TextView = view.findViewById(R.id.tvUsuario)
        val contenido: TextView = view.findViewById(R.id.tvContenido)

        val avatar: TextView = view.findViewById(R.id.tvAvatar)
        val imagen: ImageView = view.findViewById(R.id.imgPost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = lista[position]
        holder.usuario.text = post.usuario
        holder.contenido.text = post.contenido
        holder.imagen.setImageResource(post.imagen)

        val iniciales = post.usuario
            .split(" ")
            .map { it.first() }
            .joinToString("")
            .uppercase()

        holder.avatar.text = iniciales
    }
}