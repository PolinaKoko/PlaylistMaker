package com.hfad.playlistmaker.player.ui

data class PlayerUiState(
    val trackName: String = "",
    val artistName: String = "",
    val albumName: String? = null,
    val duration: String = "",
    val year: String? = null,
    val genre: String = "",
    val country: String = "",
    val coverUrl: String = "",
    val showAlbum: Boolean = false,
    val showYear: Boolean = false
)