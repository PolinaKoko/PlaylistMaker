package com.hfad.playlistmaker.search.domain

import com.hfad.playlistmaker.search.domain.models.Track

interface TrackInteractor {
    fun searchTracks(query: String, consumer: TrackConsumer)
    fun interface TrackConsumer {
        fun consume(result: Result<List<Track>>)
    }
}