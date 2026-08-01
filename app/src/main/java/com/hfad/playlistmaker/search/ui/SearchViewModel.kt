package com.hfad.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.playlistmaker.search.domain.SearchHistoryInteractor
import com.hfad.playlistmaker.search.domain.TrackInteractor
import com.hfad.playlistmaker.search.domain.models.Track
import com.hfad.playlistmaker.util.SingleLiveEvent

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state


    private val _navigateToPlayer = SingleLiveEvent<Track>()
    val navigateToPlayer: LiveData<Track> = _navigateToPlayer

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var lastQuery = ""
    private var isClickAllowed = true


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
        handler.removeCallbacksAndMessages(null)
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
        handler.removeCallbacksAndMessages(null)
    }

    private fun searchDebounce(query: String) {
        searchRunnable?.let { handler.removeCallbacks(it) }
        searchRunnable = Runnable { performSearch(query) }
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun performSearch(query: String) {
        lastQuery = query
        _state.value = SearchState.Loading

        trackInteractor.searchTracks(query) { result ->
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

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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