package com.hfad.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.playlistmaker.databinding.ActivitySearchBinding
import com.hfad.playlistmaker.player.ui.AudioPlayerActivity
import com.hfad.playlistmaker.search.ui.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    private val viewModel: SearchViewModel by viewModel()

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private var lastState: SearchState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setupAdapters()
        setupListeners()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        updateHistoryVisibility(binding.searchEditText.hasFocus())
    }

    private fun initViews() {
        binding.backButton.setOnClickListener { finish() }
        binding.clearHistoryButton.setOnClickListener { viewModel.onClearHistoryClicked() }
        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }
    }


    private fun setupAdapters() {
        adapter = TrackAdapter { track -> viewModel.onTrackClicked(track) }
        binding.rvTracks.layoutManager = LinearLayoutManager(this)
        binding.rvTracks.adapter = adapter

        historyAdapter = TrackAdapter { track -> viewModel.onTrackClicked(track) }
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
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
        viewModel.state.observe(this) { state ->
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

        viewModel.navigateToPlayer.observe(this) { track ->
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
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
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }
}