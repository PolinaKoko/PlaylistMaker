package com.hfad.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.playlistmaker.search.domain.models.Track
import com.hfad.playlistmaker.util.TimeFormatter

class PlayerViewModel : ViewModel() {

    private val _state = MutableLiveData<PlayerState>(PlayerState.Default)
    val state: LiveData<PlayerState> = _state

    private val _currentTime = MutableLiveData("00:00")
    val currentTime: LiveData<String> = _currentTime

    private val _uiState = MutableLiveData<PlayerUiState>()
    val uiState: LiveData<PlayerUiState> = _uiState

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrack: Track? = null

    private var pendingPosition: Long = 0L
    private var pendingPlayAfterPrepared: Boolean = false

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (_state.value == PlayerState.Playing) {
                updateTimer()
                handler.postDelayed(this, TIMER_UPDATE_DELAY)
            }
        }
    }


    fun saveState(outState: Bundle) {
        outState.putLong(KEY_POSITION, getCurrentPosition())
        outState.putString(KEY_STATE, getCurrentState())
        val track = getCurrentTrack()
        if (track != null) {
            outState.putSerializable(KEY_TRACK, track)
        }
    }

    fun restoreState(bundle: Bundle) {
        val position = bundle.getLong(KEY_POSITION, 0L)
        val state = bundle.getString(KEY_STATE, "Default") ?: "Default"
        val track = bundle.getSerializable(KEY_TRACK) as? Track

        if (track != null) {
            setTrack(track, position, state)
        } else if (position > 0) {
            pendingPosition = position
            _currentTime.value = TimeFormatter.formatTime(position)
        }
    }

    fun getCurrentTrack(): Track? = currentTrack

    fun getCurrentPosition(): Long {
        return try {
            mediaPlayer?.currentPosition?.toLong() ?: pendingPosition
        } catch (e: Exception) {
            pendingPosition
        }
    }

    fun getCurrentState(): String {
        return when (_state.value) {
            is PlayerState.Playing -> "Playing"
            is PlayerState.Paused -> "Paused"
            else -> "Default"
        }
    }

    fun restorePosition(position: Long) {
        pendingPosition = position
        val player = mediaPlayer
        if (player != null && position > 0) {
            try {
                player.seekTo(position.toInt())
                _currentTime.value = TimeFormatter.formatTime(position)
            } catch (e: Exception) {

            }
        }
    }

    fun restoreState(state: String) {
        when (state) {
            "Playing" -> {
                val currentState = _state.value
                if (currentState == PlayerState.Prepared ||
                    currentState == PlayerState.Paused
                ) {
                    startPlayer()
                } else {
                    pendingPlayAfterPrepared = true
                }
            }

            "Paused" -> {
                if (_state.value == PlayerState.Playing) {
                    pausePlayer()
                }
            }

            else -> {
                //ничего не делаем
            }
        }
    }

    fun setTrack(track: Track, position: Long = 0L, state: String = "Default") {
        currentTrack = track
        pendingPosition = position
        updateUiState(track)

        if (mediaPlayer != null && _state.value !is PlayerState.Error) {
            restorePosition(position)
            restoreState(state)
            return
        }

        preparePlayer(track)
    }

    fun onResume() {
        if (_state.value == PlayerState.Playing) {
            startTimer()
        }
    }

    fun onPause() {
        stopTimer()
    }

    fun onStop() {
        stopTimer()
        if (_state.value == PlayerState.Playing) {
            pausePlayer()
        }
    }

    private fun updateUiState(track: Track) {
        _uiState.value = PlayerUiState(
            trackName = track.trackName,
            artistName = track.artistName,
            albumName = track.collectionName,
            duration = track.getTrackTime(),
            year = track.releaseDate?.take(4),
            genre = track.primaryGenreName ?: "",
            country = track.country ?: "",
            coverUrl = track.getCoverArtwork(),
            showAlbum = !track.collectionName.isNullOrEmpty(),
            showYear = !track.releaseDate.isNullOrEmpty()
        )
    }

    private fun preparePlayer(track: Track) {
        val url = track.previewUrl
        if (url.isNullOrEmpty()) {
            _state.value = PlayerState.Error("Трек недоступен для воспроизведения")
            return
        }

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(url)
                setOnPreparedListener {
                    if (pendingPosition > 0) {
                        try {
                            seekTo(pendingPosition.toInt())
                            _currentTime.value = TimeFormatter.formatTime(pendingPosition)
                        } catch (e: Exception) { /* ignore */
                        }
                    }
                    _state.value = PlayerState.Prepared

                    if (pendingPlayAfterPrepared) {
                        pendingPlayAfterPrepared = false
                        startPlayer()
                    }
                }
                setOnCompletionListener {
                    _state.value = PlayerState.Completed
                    stopTimer()
                    _currentTime.value = "00:00"
                }
                setOnErrorListener { _, _, _ ->
                    _state.value = PlayerState.Error("Ошибка воспроизведения")
                    true
                }
                prepareAsync()
            }
            _state.value = PlayerState.Default
        } catch (e: Exception) {
            _state.value = PlayerState.Error("Ошибка воспроизведения")
        }
    }

    fun onPlayButtonClicked() {
        when (_state.value) {
            PlayerState.Playing -> pausePlayer()
            PlayerState.Prepared, PlayerState.Paused, PlayerState.Completed -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        val player = mediaPlayer ?: run {
            _state.value = PlayerState.Error("Плеер не инициализирован")
            return
        }
        try {
            if (pendingPosition > 0) {
                player.seekTo(pendingPosition.toInt())
                _currentTime.value = TimeFormatter.formatTime(pendingPosition)
                pendingPosition = 0L
            }
            player.start()
            _state.value = PlayerState.Playing
            startTimer()
        } catch (e: Exception) {
            _state.value = PlayerState.Error("Ошибка воспроизведения")
        }
    }

    private fun pausePlayer() {
        val player = mediaPlayer ?: return
        try {
            player.pause()
            _state.value = PlayerState.Paused
            stopTimer()
        } catch (e: Exception) {
            _state.value = PlayerState.Error("Ошибка воспроизведения")
        }
    }

    private fun startTimer() {
        handler.removeCallbacks(timerRunnable)
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun updateTimer() {
        val player = mediaPlayer ?: return
        try {
            val currentPosition = player.currentPosition
            _currentTime.value = TimeFormatter.formatTime(currentPosition)
        } catch (e: Exception) {
            _currentTime.value = "00:00"
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
        private const val KEY_POSITION = "key_position"
        private const val KEY_STATE = "key_state"
        private const val KEY_TRACK = "key_track"
    }
}