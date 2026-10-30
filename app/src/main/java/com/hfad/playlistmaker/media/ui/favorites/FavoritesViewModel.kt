package com.hfad.playlistmaker.media.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.playlistmaker.media.domain.db.FavoritesInteractor
import kotlinx.coroutines.launch


class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    fun loadFavorites() {
        _state.value = FavoritesState.Loading
        viewModelScope.launch {
            favoritesInteractor.getFavoriteTracks().collect { tracks ->
                android.util.Log.d("FavoritesVM", "tracks: ${tracks.size}")
                if (tracks.isEmpty()) {
                    _state.value = FavoritesState.Empty
                } else {
                    _state.value = FavoritesState.Content(tracks)
                }
            }
        }
    }
}