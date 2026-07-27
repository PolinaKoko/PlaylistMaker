package com.hfad.playlistmaker.domain.impl

import com.hfad.playlistmaker.domain.api.TrackInteractor
import com.hfad.playlistmaker.domain.api.TrackRepository

class TrackInteractorImpl(private val repository: TrackRepository) : TrackInteractor {

    override fun searchTracks(query: String, consumer: TrackInteractor.TrackConsumer) {
        Thread {
            val tracks = repository.searchTracks(query)
            consumer.consume(tracks)
        }.start()
    }
}