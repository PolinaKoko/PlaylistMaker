package com.hfad.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.hfad.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel()

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupListeners()

        if (savedInstanceState != null) {
            viewModel.restoreState(savedInstanceState)
        } else {
            val track = getTrackFromArguments()
            if (track == null) {
                Toast.makeText(requireContext(), "Трек не найден", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
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

    private fun getTrackFromArguments(): Track? {
        return arguments?.getSerializable(ARG_TRACK) as? Track
    }

    private fun setupObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }
        viewModel.currentTime.observe(viewLifecycleOwner) { time ->
            binding.tvCurrentTime.text = time
        }
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            bindUiState(uiState)
        }
    }

    private fun setupListeners() {
        binding.btnPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
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

        Glide.with(requireContext())
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
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val ARG_TRACK = "track"

        fun newInstance(track: Track): AudioPlayerFragment {
            return AudioPlayerFragment().apply {
                arguments = bundleOf(ARG_TRACK to track)
            }
        }
    }

}