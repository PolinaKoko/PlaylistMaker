package com.hfad.playlistmaker.search.domain

import com.hfad.playlistmaker.search.domain.models.Track


interface TrackRepository {
    fun searchTracks(query: String): Result<List<Track>>
}