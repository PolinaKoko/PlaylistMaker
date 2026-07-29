package com.hfad.playlistmaker.search.domain.models

import com.hfad.playlistmaker.util.TimeFormatter
import java.io.Serializable

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String? = null,
    val releaseDate: String? = null,
    val primaryGenreName: String? = null,
    val country: String? = null,
    val previewUrl: String? = null
) : Serializable {

    fun getTrackTime(): String {
        return TimeFormatter.formatTime(trackTimeMillis)
    }

    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }
}