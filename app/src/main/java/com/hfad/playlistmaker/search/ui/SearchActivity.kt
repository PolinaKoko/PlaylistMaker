package com.hfad.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.playlistmaker.App
import com.hfad.playlistmaker.Creator
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.player.ui.AudioPlayerActivity
import com.hfad.playlistmaker.search.ui.adapter.TrackAdapter

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    // Views
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderEmpty: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var tvErrorMessage: TextView
    private lateinit var retryButton: Button
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViewModel()
        initViews()
        setupAdapters()
        setupListeners()
        observeViewModel()
    }

    private fun initViewModel() {
        val sharedPrefs = getSharedPreferences(App.PREFS_NAME, MODE_PRIVATE)
        val factory = SearchViewModelFactory(
            Creator.provideTrackInteractor(),
            Creator.provideSearchHistoryInteractor(sharedPrefs)
        )
        viewModel = ViewModelProvider(this, factory)[SearchViewModel::class.java]
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.rvTracks)
        placeholderEmpty = findViewById(R.id.placeholderEmpty)
        placeholderError = findViewById(R.id.placeholderError)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)
        retryButton = findViewById(R.id.retryButton)
        historyContainer = findViewById(R.id.historyContainer)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        progressBar = findViewById(R.id.progressBar)

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.onClearHistoryClicked()
        }

        retryButton.setOnClickListener {
            viewModel.onRetryClicked()
        }
    }

    private fun setupAdapters() {
        adapter = TrackAdapter { track ->
            viewModel.onTrackClicked(track)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        historyAdapter = TrackAdapter { track ->
            viewModel.onTrackClicked(track)
        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {
        searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text?.toString().orEmpty()
            clearButton.visibility = if (query.isEmpty()) View.GONE else View.VISIBLE

            if (query.isEmpty()) {
                viewModel.onClearQuery()
            } else {
                viewModel.onQueryChanged(query)
            }
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryVisibility(hasFocus)
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is SearchState.Initial -> {
                    hideLoading()
                    showResults(false)
                    showError(false)
                    showEmpty(false)
                    adapter.updateTracks(emptyList())
                }

                is SearchState.Loading -> {
                    showLoading()
                }

                is SearchState.Content -> {
                    hideLoading()
                    showResults(true)
                    showError(false)
                    showEmpty(false)
                    adapter.updateTracks(state.tracks)
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
                    showError(true, state.message)
                    showEmpty(false)
                }
            }
        }

        viewModel.history.observe(this) { history ->
            historyAdapter.updateTracks(history)
            updateHistoryVisibility(searchEditText.hasFocus())
        }

        viewModel.navigateToPlayer.observe(this) { track ->
            val intent = Intent(this, AudioPlayerActivity::class.java)
            intent.putExtra("track", track)
            startActivity(intent)
        }
    }

    private fun updateHistoryVisibility(hasFocus: Boolean) {
        val query = searchEditText.text.toString()
        viewModel.history.value?.let { history ->
            val shouldShow = hasFocus && query.isEmpty() && history.isNotEmpty()
            historyContainer.visibility = if (shouldShow) View.VISIBLE else View.GONE
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.GONE
        historyContainer.visibility = View.GONE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showResults(show: Boolean) {
        recyclerView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEmpty(show: Boolean) {
        placeholderEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(show: Boolean, message: String = "") {
        placeholderError.visibility = if (show) View.VISIBLE else View.GONE
        if (show && message.isNotEmpty()) {
            tvErrorMessage.text = message
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}