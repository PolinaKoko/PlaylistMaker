package com.hfad.playlistmaker.search.ui

import com.hfad.playlistmaker.search.domain.models.Track

sealed class SearchState {
    object Initial : SearchState()
    object Loading : SearchState()
    object Empty : SearchState()
    data class Content(val tracks: List<Track>) : SearchState()
    data class Error(val message: String) : SearchState()
}
