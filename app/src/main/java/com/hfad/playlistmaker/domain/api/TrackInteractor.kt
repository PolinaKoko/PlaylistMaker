package com.hfad.playlistmaker.domain.api

import com.hfad.playlistmaker.domain.models.Track


interface TrackInteractor {
    fun searchTracks(query: String, consumer: TrackConsumer)
    interface TrackConsumer {
        fun consume(tracks: List<Track>)
    }
}