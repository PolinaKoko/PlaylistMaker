package com.hfad.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.playlistmaker.search.domain.SearchHistoryInteractor
import com.hfad.playlistmaker.search.domain.TrackInteractor
import com.hfad.playlistmaker.search.domain.models.Track
import com.hfad.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state


    private val _navigateToPlayer = SingleLiveEvent<Track>()
    val navigateToPlayer: LiveData<Track> = _navigateToPlayer

    private var searchJob: Job? = null
    private var searchRequestJob: Job? = null
    private var clickJob: Job? = null
    private var lastQuery = ""


    init {
        val history = searchHistoryInteractor.getHistory()
        _state.value = SearchState.Content(emptyList(), history)
    }

    fun onQueryChanged(query: String) {
        if (query.isEmpty()) {
            clearState()
            return
        }
        searchDebounce(query)
    }

    fun onClearQuery() {
        searchJob?.cancel()
        clearState()
    }

    fun onTrackClicked(track: Track) {
        if (!clickDebounce()) return
        searchHistoryInteractor.addTrack(track)

        val currentState = _state.value
        if (currentState is SearchState.Content) {
            val history = searchHistoryInteractor.getHistory()
            _state.postValue(currentState.copy(history = history))
        } else {
            _state.postValue(SearchState.Content(emptyList(), searchHistoryInteractor.getHistory()))
        }
        _navigateToPlayer.value = track
    }

    fun onClearHistoryClicked() {
        searchHistoryInteractor.clearHistory()
        val currentState = _state.value
        if (currentState is SearchState.Content) {
            _state.postValue(currentState.copy(history = emptyList()))
        } else {
            _state.postValue(SearchState.Initial)
        }
    }

    fun onRetryClicked() {
        if (lastQuery.isNotEmpty()) {
            performSearch(lastQuery)
        }
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
        searchRequestJob?.cancel()
        clickJob?.cancel()
    }

    private fun searchDebounce(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        lastQuery = query
        _state.value = SearchState.Loading

        searchRequestJob?.cancel()
        searchRequestJob = viewModelScope.launch {
            trackInteractor.searchTracks(query)
                .collect { result ->
                    result
                        .onSuccess { tracks ->
                            if (tracks.isEmpty()) {
                                _state.postValue(SearchState.Empty)
                            } else {
                                _state.postValue(
                                    SearchState.Content(
                                        tracks,
                                        searchHistoryInteractor.getHistory()
                                    )
                                )
                            }
                        }
                        .onFailure { exception ->
                            val message = exception.message ?: "Неизвестная ошибка"
                            _state.postValue(SearchState.Error(message))
                        }
                }
        }
    }

    private fun clickDebounce(): Boolean {
        if (clickJob?.isActive == true) return false
        clickJob = viewModelScope.launch {
            delay(CLICK_DEBOUNCE_DELAY)
        }
        return true
    }

    fun clearState() {
        val history = searchHistoryInteractor.getHistory()
        _state.value = SearchState.Content(emptyList(), history)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}