package com.hfad.playlistmaker.player.ui

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.playlistmaker.search.domain.models.Track
import com.hfad.playlistmaker.util.TimeFormatter
import kotlinx.coroutines.Runnable

class PlayerViewModel : ViewModel() {

    private val _state = MutableLiveData<PlayerState>(PlayerState.Default)
    val state: LiveData<PlayerState> = _state

    private val _currentTime = MutableLiveData("00:00")
    val currentTime: LiveData<String> = _currentTime

    private var mediaPlayer = MediaPlayer()
    private var currentTrack: Track? = null

    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (_state.value == PlayerState.Playing) {
                updateTimer()
                handler.postDelayed(this, TIMER_UPDATE_DELAY)
            }
        }
    }

    fun preparePlayer(track: Track) {
        currentTrack = track
        val url = track.previewUrl

        if (url.isNullOrEmpty()) {
            _state.value = PlayerState.Error("Трек недоступен для воспроизведения")
            return
        }

        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()

            mediaPlayer.setOnPreparedListener {
                _state.value = PlayerState.Prepared
            }

            mediaPlayer.setOnCompletionListener {
                _state.value = PlayerState.Completed
                stopTimer()
                _currentTime.value = "00:00"
            }

            mediaPlayer.setOnErrorListener { _, _, _ ->
                _state.value = PlayerState.Error("Ошибка воспроизведения")
                true
            }

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

    fun onPause() {
        if (_state.value == PlayerState.Playing) {
            pausePlayer()
        }
    }


    override fun onCleared() {
        super.onCleared()
        stopTimer()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()

    }

    private fun startPlayer() {
        mediaPlayer.start()
        _state.value = PlayerState.Playing
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        _state.value = PlayerState.Paused
        stopTimer()
    }

    private fun startTimer() {
        handler.removeCallbacks(timerRunnable)
        handler.post(timerRunnable)
    }

    private fun stopTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun updateTimer() {
        try {
            val currentPosition = mediaPlayer.currentPosition
            _currentTime.value = TimeFormatter.formatTime(currentPosition)
        } catch (e: Exception) {
            _currentTime.value = "00:00"
        }
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }
}