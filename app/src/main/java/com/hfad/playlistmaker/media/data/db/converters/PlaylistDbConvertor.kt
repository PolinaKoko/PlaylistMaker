package com.hfad.playlistmaker.media.data.db.converters

import com.hfad.playlistmaker.core.domain.models.Playlist
import com.hfad.playlistmaker.media.data.db.entity.PlaylistEntity

class PlaylistDbConvertor {

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            tracksCount = playlist.tracksCount
        )
    }

}