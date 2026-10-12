package com.hfad.playlistmaker.search.domain

import com.hfad.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


interface TrackRepository {
    fun searchTracks(query: String): Flow<Result<List<Track>>>
}