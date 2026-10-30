package com.hfad.playlistmaker.media.ui.playlists.adapter

import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.core.domain.models.Playlist
import java.io.File

class PlaylistBottomSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistImage: ImageView = itemView.findViewById(R.id.playlist_image)
    private val placeholderImage: ImageView = itemView.findViewById(R.id.placeholder_image)
    private val name: TextView = itemView.findViewById(R.id.playlist_name)
    private val trackCounter: TextView = itemView.findViewById(R.id.playlist_track_count)

    fun bind(playlist: Playlist) {
        name.text = playlist.name
        trackCounter.text = "${playlist.tracksCount} треков"

        if (playlist.coverPath != null) {
            playlistImage.setImageURI(Uri.fromFile(File(playlist.coverPath)))
            playlistImage.visibility = View.VISIBLE
            placeholderImage.visibility = View.GONE
        } else {
            playlistImage.visibility = View.GONE
            placeholderImage.visibility = View.VISIBLE
        }
    }
}