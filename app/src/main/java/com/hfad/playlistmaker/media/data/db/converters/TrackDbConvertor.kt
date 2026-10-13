package com.hfad.playlistmaker.media.data.db.converters

import com.hfad.playlistmaker.core.domain.models.Track
import com.hfad.playlistmaker.media.data.db.entity.TrackEntity

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            id = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            addedAt = System.currentTimeMillis()
        )
    }

    fun map(track: TrackEntity): Track {
        return Track(
            trackId = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }
}