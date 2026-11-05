package com.hfad.playlistmaker.media.ui.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.playlistmaker.core.domain.models.Playlist
import com.hfad.playlistmaker.core.domain.models.Track
import com.hfad.playlistmaker.media.domain.db.PlaylistInteractor
import com.hfad.playlistmaker.util.SingleLiveEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistState>()
    val state: LiveData<PlaylistState> = _state

    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()

    private val _navigateBack = SingleLiveEvent<Unit>()
    val navigateBack: LiveData<Unit> = _navigateBack

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(playlistId).collect { playlist ->
                currentPlaylist = playlist
                updateState()
            }
        }

        viewModelScope.launch {
            playlistInteractor.getTracksByPlaylistId(playlistId).collect { tracks ->
                currentTracks = tracks
                updateState()
            }
        }
    }

    fun getShareText(
        playlist: Playlist,
        tracks: List<Track>
    ): String {
        return buildString {
            append("${playlist.name}\n")
            if (!playlist.description.isNullOrEmpty()) {
                append("${playlist.description}\n")
            }
            append("${tracks.size} треков\n")
            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.getTrackTime()}) \n")
            }
        }
    }

    private fun updateState() {
        val playlist = currentPlaylist ?: return
        val duration = calculateTotalDuration(currentTracks)
        _state.value = if (currentTracks.isEmpty()) {
            PlaylistState.Empty(playlist, duration)
        } else {
            PlaylistState.Content(playlist, currentTracks, duration)
        }
    }

    private fun calculateTotalDuration(tracks: List<Track>): String {
        val sum = tracks.sumOf { it.trackTimeMillis }
        return SimpleDateFormat("mm", Locale.getDefault()).format(sum)
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            playlistInteractor.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlistId)
            _navigateBack.postValue(Unit)
        }
    }
}