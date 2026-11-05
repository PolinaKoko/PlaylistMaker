package com.hfad.playlistmaker.media.data.repository

import com.hfad.playlistmaker.core.domain.models.Playlist
import com.hfad.playlistmaker.core.domain.models.Track
import com.hfad.playlistmaker.media.data.db.converters.PlaylistDbConvertor
import com.hfad.playlistmaker.media.data.db.converters.PlaylistTrackDbConvertor
import com.hfad.playlistmaker.media.data.db.dao.PlaylistDao
import com.hfad.playlistmaker.media.data.db.dao.PlaylistTrackDao
import com.hfad.playlistmaker.media.data.db.entity.PlaylistTrackEntity
import com.hfad.playlistmaker.media.domain.db.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistTrackDao: PlaylistTrackDao,
    private val playlistConvertor: PlaylistDbConvertor,
    private val playlistTrackConvertor: PlaylistTrackDbConvertor
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist): Long {
        return playlistDao.insertPlaylist(playlistConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistConvertor.map(playlist))
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { playlistConvertor.map(it) }
        }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): Boolean {
        val playlistWithTracks = playlistDao.getPlaylistWithTracks(playlist.id)
        val alreadyExists = playlistWithTracks.tracks.any { it.trackId == track.trackId }
        if (alreadyExists) {
            return false
        }

        val trackEntity = PlaylistTrackEntity(
            playlistId = playlist.id,
            trackId = track.trackId,
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

        val newCount = playlistDao.getTracksCount(playlist.id) + 1
        playlistTrackDao.insertTrack(trackEntity)
        playlistDao.updateTracksCount(playlist.id, newCount)

        return true
    }

    override fun getPlaylistById(playlistId: Long): Flow<Playlist?> {
        return playlistDao.getPlaylistById(playlistId).map { entity ->
            entity?.let { playlistConvertor.map(it) }
        }
    }

    override fun getTracksByPlaylistId(playlistId: Long): Flow<List<Track>> {
        return playlistTrackDao.getTracksByPlaylistId(playlistId).map { entities ->
            entities.map { playlistTrackConvertor.map(it) }
        }
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistTrackDao.removeTrackFromPlaylist(playlistId, trackId)

        val newCount = playlistDao.getTracksCount(playlistId)
        playlistDao.updateTracksCount(playlistId, newCount)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }
}