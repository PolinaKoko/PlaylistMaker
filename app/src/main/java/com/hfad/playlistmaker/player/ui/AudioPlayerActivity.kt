package com.hfad.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.search.domain.models.Track

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var track: Track


    private lateinit var btnPlay: ImageButton
    private lateinit var tvCurrentTime: TextView
    private lateinit var backButton: ImageButton
    private lateinit var tvTrackName: TextView
    private lateinit var tvArtistName: TextView
    private lateinit var tvAlbumName: TextView
    private lateinit var tvAlbumLabel: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvYearLabel: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvCountry: TextView
    private lateinit var ivCoverArtwork: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("track") as? Track
        } ?: return

        initViews()
        fillData()

        viewModel.state.observe(this) { state ->
            renderState(state)
        }

        viewModel.currentTime.observe(this) { time ->
            tvCurrentTime.text = time
        }

        viewModel.preparePlayer(track)

        btnPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initViews() {
        btnPlay = findViewById(R.id.btnPlay)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        backButton = findViewById(R.id.backButton)
        tvTrackName = findViewById(R.id.tvTrackName)
        tvArtistName = findViewById(R.id.tvArtistName)
        tvAlbumName = findViewById(R.id.tvAlbumName)
        tvAlbumLabel = findViewById(R.id.tvAlbumLabel)
        tvDuration = findViewById(R.id.tvDuration)
        tvYear = findViewById(R.id.tvYear)
        tvYearLabel = findViewById(R.id.tvYearLabel)
        tvGenre = findViewById(R.id.tvGenre)
        tvCountry = findViewById(R.id.tvCountry)
        ivCoverArtwork = findViewById(R.id.ivCoverArtwork)
    }

    private fun fillData() {
        tvTrackName.text = track.trackName
        tvArtistName.text = track.artistName

        if (!track.collectionName.isNullOrEmpty()) {
            tvAlbumName.text = track.collectionName
            tvAlbumName.visibility = View.VISIBLE
            tvAlbumLabel.visibility = View.VISIBLE
        } else {
            tvAlbumName.visibility = View.GONE
            tvAlbumLabel.visibility = View.GONE
        }

        tvDuration.text = track.getTrackTime()

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

        tvGenre.text = track.primaryGenreName ?: ""
        tvCountry.text = track.country ?: ""

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(RoundedCorners((8 * resources.displayMetrics.density).toInt()))
            .into(ivCoverArtwork)
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                btnPlay.isEnabled = false
                btnPlay.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Prepared -> {
                btnPlay.isEnabled = true
                btnPlay.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Playing -> {
                btnPlay.isEnabled = true
                btnPlay.setImageResource(R.drawable.ic_pause)
            }

            is PlayerState.Paused -> {
                btnPlay.isEnabled = true
                btnPlay.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Completed -> {
                btnPlay.isEnabled = true
                btnPlay.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Error -> {
                btnPlay.isEnabled = false
                btnPlay.setImageResource(R.drawable.ic_play)
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }
        }
    }
}


