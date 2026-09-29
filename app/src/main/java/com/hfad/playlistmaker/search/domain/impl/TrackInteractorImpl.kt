package com.hfad.playlistmaker.search.domain.impl

import com.hfad.playlistmaker.search.domain.TrackInteractor
import com.hfad.playlistmaker.search.domain.TrackRepository
import com.hfad.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow


class TrackInteractorImpl(
    private val repository: TrackRepository
) : TrackInteractor {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> {
        return repository.searchTracks(query)
    }
}