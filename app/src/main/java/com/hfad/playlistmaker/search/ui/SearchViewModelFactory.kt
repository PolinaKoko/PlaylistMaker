package com.hfad.playlistmaker.search.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.playlistmaker.search.domain.SearchHistoryInteractor
import com.hfad.playlistmaker.search.domain.TrackInteractor

class SearchViewModelFactory(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(trackInteractor, searchHistoryInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}