package com.hfad.playlistmaker.domain.impl

import com.hfad.playlistmaker.domain.api.SearchHistoryInteractor
import com.hfad.playlistmaker.domain.api.SearchHistoryRepository
import com.hfad.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {
    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}