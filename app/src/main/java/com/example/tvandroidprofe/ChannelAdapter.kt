package com.example.tvandroidprofe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ChannelAdapter(
    private val channels: List<Channel>,
    private val onItemClick: (Channel) -> Unit,
    private val onItemEdit: (Channel, Int) -> Unit,
    private val onItemDelete: (Int) -> Unit
) : RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        val channel = channels[position]
        holder.bind(channel)

        holder.itemView.setOnClickListener {
            onItemClick(channel) // Reproducir canal al hacer clic
        }

        // Editar canal al hacer clic en el botón de editar
        holder.editButton.setOnClickListener {
            onItemEdit(channel, position)
        }

        // Eliminar canal al hacer clic en el botón de eliminar
        holder.deleteButton.setOnClickListener {
            onItemDelete(position)
        }
    }

    override fun getItemCount(): Int = channels.size

    class ChannelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val logoImageView: ImageView = itemView.findViewById(R.id.logoImageView)
        val editButton: ImageView = itemView.findViewById(R.id.editButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)

        fun bind(channel: Channel) {
            nameTextView.text = channel.name
            // Aquí puedes cargar la imagen del logo usando Glide o cualquier otra librería
            Glide.with(logoImageView.context).load(channel.logo).into(logoImageView)
        }
    }
}
