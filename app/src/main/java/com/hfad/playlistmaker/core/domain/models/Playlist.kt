package com.hfad.playlistmaker.core.domain.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String?,
    val coverPath: String?,
    val trackIds: List<Long>,
    val tracksCount: Int
)