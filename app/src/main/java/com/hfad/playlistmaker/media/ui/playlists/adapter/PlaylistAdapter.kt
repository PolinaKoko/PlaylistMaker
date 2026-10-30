package com.hfad.playlistmaker.media.ui.playlists.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.core.domain.models.Playlist

class PlaylistAdapter(
    private val onItemClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder>() {

    private var playlists: List<Playlist> = emptyList()

    fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists = newPlaylists
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener {
            onItemClick(playlist)
        }
    }

    override fun getItemCount(): Int = playlists.size
}