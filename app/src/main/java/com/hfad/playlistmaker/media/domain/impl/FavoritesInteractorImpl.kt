package com.hfad.playlistmaker.media.domain.impl

import com.hfad.playlistmaker.core.domain.models.Track
import com.hfad.playlistmaker.media.domain.db.FavoritesInteractor
import com.hfad.playlistmaker.media.domain.db.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addTrack(track: Track) {
        favoritesRepository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        favoritesRepository.removeTrack(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoriteTracks()
    }

    override suspend fun getFavoriteIds(): List<Long> {
        return favoritesRepository.getFavoriteIds()
    }
}