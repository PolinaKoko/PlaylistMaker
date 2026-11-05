package com.hfad.playlistmaker.media.ui.playlist

import com.hfad.playlistmaker.core.domain.models.Playlist
import com.hfad.playlistmaker.core.domain.models.Track

sealed interface PlaylistState {
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val totalDuration: String
    ) : PlaylistState

    data class Empty(
        val playlist: Playlist,
        val totalDuration: String
    ) : PlaylistState
}