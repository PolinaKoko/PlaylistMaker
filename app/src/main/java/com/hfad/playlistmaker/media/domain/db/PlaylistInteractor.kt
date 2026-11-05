package com.hfad.playlistmaker.media.domain.db

import com.hfad.playlistmaker.core.domain.models.Playlist
import com.hfad.playlistmaker.core.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    suspend fun addPlaylist(playlist: Playlist): Long

    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackPlaylist(playlist: Playlist, track: Track): Boolean

    fun getPlaylistById(playlistId: Long): Flow<Playlist?>
    fun getTracksByPlaylistId(playlistId: Long): Flow<List<Track>>

    suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long)

    suspend fun deletePlaylist(playlistId: Long)
}