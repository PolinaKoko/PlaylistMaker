package com.hfad.playlistmaker.media.data.db.converters

import com.hfad.playlistmaker.core.domain.models.Track
import com.hfad.playlistmaker.media.data.db.entity.PlaylistTrackEntity

class PlaylistTrackDbConvertor {

    fun map(playlist_track: PlaylistTrackEntity): Track {
        return Track(
            trackId = playlist_track.trackId,
            trackName = playlist_track.trackName,
            artistName = playlist_track.artistName,
            trackTimeMillis = playlist_track.trackTimeMillis,
            artworkUrl100 = playlist_track.artworkUrl100,
            collectionName = playlist_track.collectionName,
            releaseDate = playlist_track.releaseDate,
            primaryGenreName = playlist_track.primaryGenreName,
            country = playlist_track.country,
            previewUrl = playlist_track.previewUrl
        )
    }
}