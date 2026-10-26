package com.hfad.playlistmaker.media.ui.favorites

import com.hfad.playlistmaker.core.domain.models.Track

sealed interface FavoritesState {

    object Loading : FavoritesState

    data class Content(val tracks: List<Track>) : FavoritesState

    object Empty : FavoritesState
}