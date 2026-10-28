package com.hfad.playlistmaker.media.ui.playlists

import com.hfad.playlistmaker.core.domain.models.Playlist

sealed interface PlaylistsState {

    data class Content(val playlists: List<Playlist>) : PlaylistsState
    object Empty : PlaylistsState

}