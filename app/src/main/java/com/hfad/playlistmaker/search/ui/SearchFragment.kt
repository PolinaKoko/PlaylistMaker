package com.hfad.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.databinding.FragmentSearchBinding
import com.hfad.playlistmaker.search.ui.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private val viewModel: SearchViewModel by viewModel()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var lastState: SearchState? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        setupAdapters()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        updateHistoryVisibility(binding.searchEditText.hasFocus())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        binding.clearHistoryButton.setOnClickListener { viewModel.onClearHistoryClicked() }
        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }
    }


    private fun setupAdapters() {
        adapter = TrackAdapter { track -> viewModel.onTrackClicked(track) }
        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTracks.adapter = adapter

        historyAdapter = TrackAdapter { track -> viewModel.onTrackClicked(track) }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString().orEmpty()
            binding.clearButton.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE

            if (query.isEmpty()) {
                viewModel.onClearQuery()
                updateHistoryVisibility(binding.searchEditText.hasFocus())
            } else {
                viewModel.onQueryChanged(query)
                binding.historyContainer.visibility = View.GONE
            }
        }

        binding.searchEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility(hasFocus)
        }

        binding.clearButton.setOnClickListener {
            binding.searchEditText.text.clear()
            hideKeyboard()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            lastState = state

            when (state) {
                is SearchState.Initial -> {
                    hideAllContainers()
                    adapter.updateTracks(emptyList())
                    updateHistoryVisibility(binding.searchEditText.hasFocus())
                }

                is SearchState.Loading -> {
                    showLoading()
                }

                is SearchState.Content -> {
                    hideLoading()
                    showError(false)
                    showEmpty(false)

                    val hasTracks = state.tracks.isNotEmpty()
                    showResults(hasTracks)

                    adapter.updateTracks(state.tracks)
                    historyAdapter.updateTracks(state.history)
                    updateHistoryVisibility(binding.searchEditText.hasFocus())
                }

                is SearchState.Empty -> {
                    hideLoading()
                    showResults(false)
                    showError(false)
                    showEmpty(true)
                    adapter.updateTracks(emptyList())
                }

                is SearchState.Error -> {
                    hideLoading()
                    showResults(false)
                    showEmpty(false)
                    showError(true, state.message)
                }
            }
        }

        viewModel.navigateToPlayer.observe(viewLifecycleOwner) { track ->
            val bundle = bundleOf("track" to track)
            findNavController().navigate(R.id.action_search_to_player, bundle)
        }
    }

    private fun updateHistoryVisibility(hasFocus: Boolean) {
        val query = binding.searchEditText.text.toString()
        val history = (lastState as? SearchState.Content)?.history ?: emptyList()

        val shouldShow = hasFocus && query.isEmpty() && history.isNotEmpty()
        binding.historyContainer.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    private fun hideAllContainers() {
        binding.progressBar.visibility = View.GONE
        binding.rvTracks.visibility = View.GONE
        binding.placeholderEmpty.visibility = View.GONE
        binding.placeholderError.visibility = View.GONE
    }

    private fun showLoading() {
        hideAllContainers()
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showResults(show: Boolean) {
        if (show) {
            hideAllContainers()
            binding.rvTracks.visibility = View.VISIBLE
        } else {
            binding.rvTracks.visibility = View.GONE
        }
    }

    private fun showEmpty(show: Boolean) {
        if (show) {
            hideAllContainers()
            binding.placeholderEmpty.visibility = View.VISIBLE
        } else {
            binding.placeholderEmpty.visibility = View.GONE
        }
    }

    private fun showError(show: Boolean, message: String = "") {
        if (show) {
            hideAllContainers()
            binding.placeholderError.visibility = View.VISIBLE
            if (message.isNotEmpty()) {
                binding.tvErrorMessage.text = message
            }
        } else {
            binding.placeholderError.visibility = View.GONE
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(InputMethodManager::class.java)
        imm?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}