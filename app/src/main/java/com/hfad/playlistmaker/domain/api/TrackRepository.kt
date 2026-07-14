package com.hfad.playlistmaker.domain.api

import com.hfad.playlistmaker.domain.models.Track

interface TrackRepository {
    fun searchTracks(query: String): List<Track>
}