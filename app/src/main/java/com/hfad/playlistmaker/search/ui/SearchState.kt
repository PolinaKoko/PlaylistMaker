package com.hfad.playlistmaker.search.ui

import com.hfad.playlistmaker.search.domain.models.Track

sealed interface SearchState {
    object Initial : SearchState
    object Loading : SearchState
    object Empty : SearchState
    data class Content(
        val tracks: List<Track>,
        val history: List<Track> = emptyList()
    ) : SearchState

    data class Error(val message: String) : SearchState
}
