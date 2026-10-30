package com.hfad.playlistmaker.player.ui

sealed interface AddToPlaylistStatus {

    data class Added(val playlistName: String) : AddToPlaylistStatus
    data class AlreadyExists(val playlistName: String) : AddToPlaylistStatus
}