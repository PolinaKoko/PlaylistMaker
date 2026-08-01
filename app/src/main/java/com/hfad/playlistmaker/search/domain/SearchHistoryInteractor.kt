package com.hfad.playlistmaker.search.domain

import com.hfad.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun getHistory(): List<Track>
    fun addTrack(track: Track)
    fun clearHistory()
}