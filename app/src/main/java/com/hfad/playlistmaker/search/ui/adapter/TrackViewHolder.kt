package com.hfad.playlistmaker.search.ui.adapter

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.search.domain.models.Track


class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val ivArtwork: ImageView = itemView.findViewById(R.id.ivArtwork)
    private val tvTrackName: TextView = itemView.findViewById(R.id.tvTrackName)
    private val tvArtistName: TextView = itemView.findViewById(R.id.tvArtistName)
    private val tvTrackTime: TextView = itemView.findViewById(R.id.tvTrackTime)

    fun bind(track: Track) {
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName
        tvTrackTime.text = track.getTrackTime()

        val cornerRadiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            2f,
            itemView.context.resources.displayMetrics
        ).toInt()

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(cornerRadiusPx)))
            .into(ivArtwork)
    }
}