package com.hfad.playlistmaker.player.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.hfad.playlistmaker.media.ui.playlists.adapter.PlaylistBottomSheetAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerFragment : Fragment() {

    private val viewModel: PlayerViewModel by viewModel()

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: PlaylistBottomSheetAdapter

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
        setupBottomSheet()

        if (savedInstanceState != null) {
            viewModel.restoreState(savedInstanceState)
        } else {
            val track = AudioPlayerFragmentArgs.fromBundle(requireArguments()).track
            viewModel.setTrack(track)
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1f) / 2f
            }
        })

        adapter = PlaylistBottomSheetAdapter { playlist ->
            viewModel.addTrackToPlaylist(playlist)
        }

        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistsRecyclerView.adapter = adapter

        binding.createPlaylistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            findNavController().navigate(R.id.action_audioPlayer_to_createPlaylist)
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
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            binding.btnFavorite.setImageResource(
                if (isFav) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite
            )
        }
        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            adapter.updatePlaylists(playlists)
        }
        viewModel.addToPlaylistStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is AddToPlaylistStatus.Added -> {
                    Toast.makeText(
                        requireContext(),
                        "Добавлено в плейлист ${status.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    viewModel.clearAddToPlaylistStatus()
                }

                is AddToPlaylistStatus.AlreadyExists -> {
                    Toast.makeText(
                        requireContext(),
                        "Трек уже добавлен в плейлист ${status.playlistName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.clearAddToPlaylistStatus()
                }

                null -> {}
            }
        }
    }

    private fun setupListeners() {
        binding.btnPlay.setOnClickListener {
            viewModel.onPlayButtonClicked()
        }
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnFavorite.setOnClickListener {
            viewModel.onFavoriteClicked()
        }
        binding.btnAddToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
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


}