package com.hfad.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.hfad.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerActivity : AppCompatActivity() {

    private val viewModel: PlayerViewModel by viewModel()

    private lateinit var binding: ActivityAudioPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun setupObservers() {
        viewModel.state.observe(this) { state ->
            renderState(state)
        }
        viewModel.currentTime.observe(this) { time ->
            binding.tvCurrentTime.text = time
        }
        viewModel.uiState.observe(this) { uiState ->
            bindUiState(uiState)
        }
    }

    private fun setupListeners() {
        binding.btnPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun bindUiState(uiState: PlayerUiState) {
        binding.tvTrackName.text = uiState.trackName
        binding.tvArtistName.text = uiState.artistName
        binding.tvDuration.text = uiState.duration
        binding.tvGenre.text = uiState.genre
        binding.tvCountry.text = uiState.country

        if (uiState.showAlbum) {
            binding.tvAlbumName.text = uiState.albumName
            binding.tvAlbumName.visibility = View.VISIBLE
            binding.tvAlbumLabel.visibility = View.VISIBLE
        } else {
            binding.tvAlbumName.visibility = View.GONE
            binding.tvAlbumLabel.visibility = View.GONE
        }

        if (uiState.showYear) {
            binding.tvYear.text = uiState.year
            binding.tvYear.visibility = View.VISIBLE
            binding.tvYearLabel.visibility = View.VISIBLE
        } else {
            binding.tvYear.visibility = View.GONE
            binding.tvYearLabel.visibility = View.GONE
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
            .into(binding.ivCoverArtwork)
    }

    private fun renderState(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> {
                binding.btnPlay.isEnabled = false
                binding.btnPlay.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Prepared -> {
                binding.btnPlay.isEnabled = true
                binding.btnPlay.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Playing -> {
                binding.btnPlay.isEnabled = true
                binding.btnPlay.setImageResource(R.drawable.ic_pause)
            }

            is PlayerState.Paused -> {
                binding.btnPlay.isEnabled = true
                binding.btnPlay.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Completed -> {
                binding.btnPlay.isEnabled = true
                binding.btnPlay.setImageResource(R.drawable.ic_play)
            }

            is PlayerState.Error -> {
                binding.btnPlay.isEnabled = false
                binding.btnPlay.setImageResource(R.drawable.ic_play)
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }
        }
    }

}