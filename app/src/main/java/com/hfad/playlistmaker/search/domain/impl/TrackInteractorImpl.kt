package com.hfad.playlistmaker.search.domain.impl

import com.hfad.playlistmaker.search.domain.TrackInteractor
import com.hfad.playlistmaker.search.domain.TrackRepository


class TrackInteractorImpl(
    private val repository: TrackRepository
) : TrackInteractor {

    override fun searchTracks(query: String, consumer: TrackInteractor.TrackConsumer) {
        Thread {
            val result = repository.searchTracks(query)
            consumer.consume(result)
        }.start()
    }
}