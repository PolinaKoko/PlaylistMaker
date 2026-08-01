package com.hfad.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.search.domain.models.Track

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel: PlayerViewModel by viewModels()

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

        initViews()
        setupObservers()
        setupListeners()

        if (savedInstanceState != null) {
            viewModel.restoreState(savedInstanceState)
        } else {
            val track = getTrackFromIntent()
            if (track == null) {
                Toast.makeText(this, "Трек не найден", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            viewModel.setTrack(track)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("PlayerDebug", "onSaveInstanceState")
        viewModel.saveState(outState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onPause()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun getTrackFromIntent(): Track? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("track") as? Track
        }
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

    private fun setupObservers() {
        viewModel.state.observe(this) { state ->
            renderState(state)
        }
        viewModel.currentTime.observe(this) { time ->
            tvCurrentTime.text = time
        }
        viewModel.uiState.observe(this) { uiState ->
            bindUiState(uiState)
        }
    }

    private fun setupListeners() {
        btnPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun bindUiState(uiState: PlayerUiState) {
        tvTrackName.text = uiState.trackName
        tvArtistName.text = uiState.artistName
        tvDuration.text = uiState.duration
        tvGenre.text = uiState.genre
        tvCountry.text = uiState.country

        if (uiState.showAlbum) {
            tvAlbumName.text = uiState.albumName
            tvAlbumName.visibility = View.VISIBLE
            tvAlbumLabel.visibility = View.VISIBLE
        } else {
            tvAlbumName.visibility = View.GONE
            tvAlbumLabel.visibility = View.GONE
        }

        if (uiState.showYear) {
            tvYear.text = uiState.year
            tvYear.visibility = View.VISIBLE
            tvYearLabel.visibility = View.VISIBLE
        } else {
            tvYear.visibility = View.GONE
            tvYearLabel.visibility = View.GONE
        }

        loadCover(uiState.coverUrl)
    }

    private fun loadCover(url: String) {
        val cornerRadiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            8f,
            resources.displayMetrics
        ).toInt()

        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(RoundedCorners(cornerRadiusPx))
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