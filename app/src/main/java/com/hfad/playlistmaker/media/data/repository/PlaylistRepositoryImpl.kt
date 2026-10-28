package com.hfad.playlistmaker.media.data.repository

import com.hfad.playlistmaker.core.domain.models.Playlist
import com.hfad.playlistmaker.core.domain.models.Track
import com.hfad.playlistmaker.media.data.db.converters.PlaylistDbConvertor
import com.hfad.playlistmaker.media.data.db.dao.PlaylistDao
import com.hfad.playlistmaker.media.data.db.dao.PlaylistTrackDao
import com.hfad.playlistmaker.media.data.db.entity.PlaylistTrackEntity
import com.hfad.playlistmaker.media.domain.db.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val convertor: PlaylistDbConvertor
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist): Long {
        return playlistDao.insertPlaylist(convertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(convertor.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { convertor.map(it) }
        }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        if (playlist.trackIds.contains(track.trackId)) {
            return false
        }

        val updatePlaylist = playlist.copy(
            trackIds = playlist.trackIds + track.trackId,
            tracksCount = playlist.tracksCount + 1
        )
        playlistDao.updatePlaylist(convertor.map(updatePlaylist))

        val trackEntity = PlaylistTrackEntity(
            id = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            addedAt = System.currentTimeMillis()
        )
        playlistTrackDao.insertTrack(trackEntity)

        return true
    }
}