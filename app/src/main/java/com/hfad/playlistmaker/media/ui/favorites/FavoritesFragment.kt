package com.hfad.playlistmaker.media.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.playlistmaker.databinding.FragmentFavoritesBinding
import com.hfad.playlistmaker.media.ui.MediaFragmentDirections
import com.hfad.playlistmaker.search.ui.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private val viewModel: FavoritesViewModel by viewModel()

    private var _binding: FragmentFavoritesBinding? = null

    private val binding get() = _binding!!
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter { track ->
            findNavController().navigate(
                MediaFragmentDirections.actionMediaToPlayer(track)
            )
        }

        binding.rvFavorites.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorites.adapter = adapter

        viewModel.state.observe(viewLifecycleOwner) { state ->
            render(state)
        }
    }

    private fun render(state: FavoritesState) {
        when (state) {
            is FavoritesState.Loading -> {
                binding.rvFavorites.visibility = View.GONE
                binding.placeholderEmpty.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }

            is FavoritesState.Content -> {
                binding.rvFavorites.visibility = View.VISIBLE
                binding.placeholderEmpty.visibility = View.GONE
                binding.progressBar.visibility = View.GONE
                adapter.updateTracks(state.tracks)
            }

            is FavoritesState.Empty -> {
                binding.rvFavorites.visibility = View.GONE
                binding.placeholderEmpty.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}