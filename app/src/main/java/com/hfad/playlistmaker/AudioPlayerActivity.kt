package com.hfad.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale


class AudioPlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_UPDATE_DELAY = 300L
    }

    private lateinit var track: Track
    private lateinit var btnPlay: ImageButton
    private lateinit var tvCurrentTime: TextView

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT


    private val handler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (playerState == STATE_PLAYING) {
                updateTimer()
                handler.postDelayed(this, TIMER_UPDATE_DELAY)
            }
        }
    }

    private fun preparePlayer() {

        try {
            val url = track.previewUrl
            if (url.isNullOrEmpty()) {
                btnPlay.isEnabled = false
                return
            }
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()

            mediaPlayer.setOnPreparedListener {
                btnPlay.isEnabled = true
                playerState = STATE_PREPARED
                btnPlay.setImageResource(R.drawable.ic_play)
            }

            mediaPlayer.setOnCompletionListener {
                btnPlay.setImageResource(R.drawable.ic_play)
                playerState = STATE_PREPARED
                stopTimer()
                tvCurrentTime.text = "00:00"
            }

        } catch (e: Exception) {
            e.printStackTrace()
            btnPlay.isEnabled = false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        btnPlay = findViewById(R.id.btnPlay)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("track") as? Track
        } ?: return

        fillData()
        preparePlayer()

        btnPlay.setOnClickListener {
            playbackControl()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        btnPlay.setImageResource(R.drawable.ic_pause)
        playerState = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        btnPlay.setImageResource(R.drawable.ic_play)
        playerState = STATE_PAUSED
        stopTimer()
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
            else -> {}
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
        try {
            val currentPosition = mediaPlayer.currentPosition
            val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(currentPosition)
            tvCurrentTime.text = formattedTime
        } catch (e: Exception) {
            tvCurrentTime.text = "00:00"
        }
    }

    override fun onPause() {
        super.onPause()
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun fillData() {

        findViewById<TextView>(R.id.tvTrackName).text = track.trackName
        findViewById<TextView>(R.id.tvArtistName).text = track.artistName

        val tvAlbumName = findViewById<TextView>(R.id.tvAlbumName)
        val tvAlbumLabel = findViewById<TextView>(R.id.tvAlbumLabel)

        if (!track.collectionName.isNullOrEmpty()) {
            tvAlbumName.text = track.collectionName
            tvAlbumName.visibility = View.VISIBLE
            tvAlbumLabel.visibility = View.VISIBLE
        } else {
            tvAlbumName.visibility = View.GONE
            tvAlbumLabel.visibility = View.GONE
        }

        findViewById<TextView>(R.id.tvDuration).text = track.getTrackTime()

        val tvYear = findViewById<TextView>(R.id.tvYear)
        val tvYearLabel = findViewById<TextView>(R.id.tvYearLabel)
        val releaseDate = track.releaseDate

        if (!releaseDate.isNullOrEmpty() && releaseDate.length >= 4) {
            val year = releaseDate.substring(0, 4)
            tvYear.text = year
            tvYear.visibility = View.VISIBLE
            tvYearLabel.visibility = View.VISIBLE
        } else {
            tvYear.visibility = View.GONE
            tvYearLabel.visibility = View.GONE
        }

        findViewById<TextView>(R.id.tvGenre).text = track.primaryGenreName ?: ""
        findViewById<TextView>(R.id.tvCountry).text = track.country ?: ""

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(RoundedCorners((8 * resources.displayMetrics.density).toInt()))
            .into(findViewById(R.id.ivCoverArtwork))

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
}