package com.hfad.playlistmaker.search.domain

import com.hfad.playlistmaker.search.domain.models.Track
import com.hfad.playlistmaker.util.Resource

interface TrackInteractor {
    fun searchTracks(query: String, consumer: TrackConsumer)
    fun interface TrackConsumer {
        fun consume(resource: Resource<List<Track>>)
    }
}