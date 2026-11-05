package com.hfad.playlistmaker.media.ui.playlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.core.domain.models.Playlist
import com.hfad.playlistmaker.core.domain.models.Track
import com.hfad.playlistmaker.core.ui.adapter.TrackAdapter
import com.hfad.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistFragment : Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()

    private val playlistId: Long by lazy {
        PlaylistFragmentArgs.fromBundle(requireArguments()).playlistId
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter(
            onItemClick = { track ->
                findNavController().navigate(PlaylistFragmentDirections.actionPlaylistToPlayer(track))
            },
            onLongItemClick = { track ->
                showDeleteDialog(track)
            }
        )

        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = adapter

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnShare.setOnClickListener {
            val currentState = viewModel.state.value
            if (currentState is PlaylistState.Content) {
                sharePlaylist(currentState.playlist, currentState.tracks)
            } else {
                Toast.makeText(
                    requireContext(),
                    "В этом плейлисте нет списка треков, которым можно поделиться",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val menuBottomSheet = view.findViewById<LinearLayout>(R.id.menuBottomSheet)
        val menuBehavior = BottomSheetBehavior.from(menuBottomSheet)

        binding.btnMore.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.menuShare.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val state = viewModel.state.value
            if (state is PlaylistState.Content) {
                sharePlaylist(state.playlist, state.tracks)
            } else {
                Toast.makeText(
                    requireContext(),
                    "В этом плейлисте нет списка треков, которым можно поделиться",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.menuEdit.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val bundle = Bundle().apply {
                putLong("playlistId", playlistId)
            }

            findNavController().navigate(
                R.id.action_playlist_to_editPlaylist,
                bundle
            )
        }

        binding.menuDelete.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
        viewModel.loadPlaylist(playlistId)

        viewModel.navigateBack.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Вы уверены, что хотите удалить этот плейлист?")
            .setNegativeButton("НЕТ") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("ДА") { dialog, _ ->
                viewModel.deletePlaylist(playlistId)
                dialog.dismiss()
            }
            .show()
    }

    private fun sharePlaylist(
        playlist: Playlist,
        tracks: List<Track>
    ) {
        val textToShare = viewModel.getShareText(playlist, tracks)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textToShare)
        }
        startActivity(Intent.createChooser(intent, "Поделиться плейлистом"))
    }

    private fun showDeleteDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage("Хотите удалить трек?")
            .setNegativeButton("НЕТ") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("ДА") { dialog, _ ->
                viewModel.removeTrackFromPlaylist(playlistId, track.trackId)
                dialog.dismiss()
            }
            .show()
    }

    private fun render(state: PlaylistState) {
        val playlist = when (state) {
            is PlaylistState.Content -> state.playlist
            is PlaylistState.Empty -> state.playlist
        }
        val coverPath = playlist.coverPath

        if (coverPath != null) {
            binding.coverCard.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), android.R.color.white)
            )

            Glide.with(this).clear(binding.ivCover)
            Glide.with(this)
                .load(File(playlist.coverPath))
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.ivCover)
        } else {
            binding.coverCard.setCardBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.light_gray)
            )
            binding.ivCover.setImageResource(R.drawable.ic_placeholder)
        }

        when (state) {
            is PlaylistState.Content -> {
                binding.tvPlaylistName.text = state.playlist.name
                if (state.playlist.description.isNullOrEmpty()) {
                    binding.tvDescription.visibility = View.GONE
                } else {
                    binding.tvDescription.visibility = View.VISIBLE
                    binding.tvDescription.text = state.playlist.description
                }

                binding.tvTotalDuration.text = "${state.totalDuration} минут"
                binding.tvTracksCount.text = "${state.tracks.size} треков"

                adapter.updateTracks(state.tracks)

                updateMenu(state.playlist, state.tracks.size)
            }

            is PlaylistState.Empty -> {
                binding.tvPlaylistName.text = state.playlist.name
                binding.tvDescription.text = state.playlist.description
                binding.tvTotalDuration.text = "${state.totalDuration} минут"
                binding.tvTracksCount.text = "0 треков"

                adapter.updateTracks(emptyList())

                updateMenu(state.playlist, 0)
            }
        }
    }

    private fun updateMenu(playlist: Playlist, trackCount: Int) {

        binding.menuPlaylistName.text = playlist.name
        binding.menuTracksCount.text = "$trackCount треков"

        if (playlist.coverPath != null) {
            Glide.with(this)
                .load(File(playlist.coverPath))
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.menuCover)
        } else {

            binding.menuCover.setImageResource(R.drawable.ic_placeholder)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}