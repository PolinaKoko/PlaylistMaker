package com.hfad.playlistmaker.media.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.databinding.FragmentPlaylistsBinding
import com.hfad.playlistmaker.media.ui.playlists.adapter.PlaylistAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private val viewModel: PlaylistsViewModel by viewModel()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = PlaylistAdapter {}

        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRecyclerView.adapter = adapter

        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_media_to_createPlaylist)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }

        viewModel.loadPlaylists()
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Content -> {
                binding.playlistsRecyclerView.visibility = View.VISIBLE
                binding.placeholderEmpty.visibility = View.GONE
                adapter.updatePlaylists(state.playlists)
            }

            is PlaylistsState.Empty -> {
                binding.playlistsRecyclerView.visibility = View.GONE
                binding.placeholderEmpty.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}