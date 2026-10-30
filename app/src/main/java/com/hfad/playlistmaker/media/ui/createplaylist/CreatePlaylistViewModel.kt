package com.hfad.playlistmaker.media.ui.createplaylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.playlistmaker.core.domain.models.Playlist
import com.hfad.playlistmaker.media.domain.db.PlaylistInteractor
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    fun createPlaylist(
        name: String,
        description: String?,
        coverPath: String?,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            val playlist = Playlist(
                id = 0,
                name = name,
                description = description,
                coverPath = coverPath,
                tracksCount = 0
            )
            playlistInteractor.addPlaylist(playlist)
            onSuccess(name)
        }
    }
}