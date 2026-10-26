package com.hfad.playlistmaker.media.domain.db

import com.hfad.playlistmaker.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {

    suspend fun addTrack(track: Track)

    suspend fun removeTrack(track: Track)

    fun getFavoriteTracks(): Flow<List<Track>>

    suspend fun getFavoriteIds(): List<Long>
}