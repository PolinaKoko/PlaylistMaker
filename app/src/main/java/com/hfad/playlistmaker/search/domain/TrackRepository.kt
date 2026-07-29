package com.hfad.playlistmaker.search.domain

import com.hfad.playlistmaker.search.domain.models.Track
import com.hfad.playlistmaker.util.Resource


interface TrackRepository {
    fun searchTracks(query: String): Resource<List<Track>>
}