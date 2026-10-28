package com.hfad.playlistmaker.media.data.db.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hfad.playlistmaker.core.domain.models.Playlist
import com.hfad.playlistmaker.media.data.db.entity.PlaylistEntity

class PlaylistDbConvertor(private val gson: Gson) {

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = fromJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackIds = toJson(playlist.trackIds),
            tracksCount = playlist.tracksCount
        )
    }

    private fun toJson(ids: List<Long>): String {
        return gson.toJson(ids)
    }

    private fun fromJson(json: String): List<Long> {
        val type = object : TypeToken<List<Long>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
}