package com.hfad.playlistmaker.media.data.repository

import com.hfad.playlistmaker.core.domain.models.Track
import com.hfad.playlistmaker.media.data.db.AppDatabase
import com.hfad.playlistmaker.media.data.db.converters.TrackDbConvertor
import com.hfad.playlistmaker.media.domain.db.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesRepository {

    override suspend fun addTrack(track: Track) {
        android.util.Log.d("FavoritesRepo", "addTrack: ${track.trackId}")
        val entity = trackDbConvertor.map(track)
        appDatabase.favoriteDao().addTrack(entity)
        android.util.Log.d("FavoritesRepo", "addTrack done")
    }

    override suspend fun removeTrack(track: Track) {
        val entity = trackDbConvertor.map(track)
        appDatabase.favoriteDao().removeTrack(entity)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return appDatabase.favoriteDao().getAllTracks()
            .map { entities ->
                entities.map { trackDbConvertor.map(it) }
            }
    }

    override suspend fun getFavoriteIds(): List<Long> {
        return appDatabase.favoriteDao().getFavoriteIds()
    }

}