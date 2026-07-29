package com.hfad.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.playlistmaker.search.domain.SearchHistoryInteractor
import com.hfad.playlistmaker.search.domain.TrackInteractor
import com.hfad.playlistmaker.search.domain.models.Track
import com.hfad.playlistmaker.util.Resource
import com.hfad.playlistmaker.util.SingleLiveEvent

class SearchViewModel(
    private val trackInteractor: TrackInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _state = MutableLiveData<SearchState>()
    val state: LiveData<SearchState> = _state

    private val _history = MutableLiveData<List<Track>>()
    val history: LiveData<List<Track>> = _history

    private val _navigateToPlayer = SingleLiveEvent<Track>()
    val navigateToPlayer: LiveData<Track> = _navigateToPlayer

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private var lastQuery = ""
    private var isClickAllowed = true


    init {
        loadHistory()
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
        loadHistory()
        _navigateToPlayer.value = track
    }


    fun onClearHistoryClicked() {
        searchHistoryInteractor.clearHistory()
        _history.value = emptyList()
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

        trackInteractor.searchTracks(query) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val tracks = resource.data ?: emptyList()
                    if (tracks.isEmpty()) {
                        _state.postValue(SearchState.Empty)
                    } else {
                        _state.postValue(SearchState.Content(tracks))
                    }
                }

                is Resource.Error -> {
                    val message = resource.message ?: "Неизвестная ошибка"
                    _state.postValue(SearchState.Error(message))
                }
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

    fun loadHistory() {
        _history.value = searchHistoryInteractor.getHistory()
    }

    private fun clearState() {
        _state.value = SearchState.Initial
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}