package com.hfad.playlistmaker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var placeholderEmpty: LinearLayout
    private lateinit var placeholderError: LinearLayout
    private lateinit var retryButton: Button
    private lateinit var historyContainer: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: Button
    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistory: SearchHistory
    private var lastQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPrefs = getSharedPreferences("playlist_maker_prefs", MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)
        initViews()
        setupAdapters()
        setupListeners()
        updateHistoryVisibility()
    }

    override fun onResume() {
        super.onResume()
        updateHistoryVisibility()
    }

    private fun initViews() {
        val backButton = findViewById<ImageView>(R.id.back_button)

        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.rvTracks)
        placeholderEmpty = findViewById(R.id.placeholderEmpty)
        placeholderError = findViewById(R.id.placeholderError)
        retryButton = findViewById(R.id.retryButton)
        historyContainer = findViewById(R.id.historyContainer)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        backButton.setOnClickListener {
            finish()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            updateHistoryAdapter()
            updateHistoryVisibility()
        }
    }

    private fun setupAdapters() {

        adapter = TrackAdapter { track ->
            searchHistory.addTrack(track)
            updateHistoryAdapter()
            updateHistoryVisibility()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter


        historyAdapter = TrackAdapter { track ->
            searchHistory.addTrack(track)
            updateHistoryAdapter()
            updateHistoryVisibility()

        }
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter
    }

    private fun setupListeners() {
        searchEditText.doOnTextChanged { text, _, _, _ ->
            clearButton.visibility = if (text.isNullOrEmpty()) View.GONE else View.VISIBLE

            if (text.isNullOrEmpty()) {
                adapter.updateTracks(emptyList())
                showResults(false)
                showError(false)
                showEmpty(false)
                updateHistoryVisibility()
            } else {
                historyContainer.visibility = View.GONE
            }
        }

        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                searchEditText.post {
                    updateHistoryVisibility()
                }
            } else {
                historyContainer.visibility = View.GONE
            }
        }

        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString()
                if (query.isNotEmpty()) {
                    searchTracks(query)
                    hideKeyboard()
                }
                true
            } else false
        }

        clearButton.setOnClickListener {
            searchEditText.text.clear()
            hideKeyboard()
            showResults(false)
            showError(false)
            showEmpty(false)
            adapter.updateTracks(emptyList())
            updateHistoryVisibility()
        }

        retryButton.setOnClickListener {
            if (lastQuery.isNotEmpty()) {
                searchTracks(lastQuery)
            }
        }
    }

    private fun updateHistoryVisibility() {
        val query = searchEditText.text.toString()
        val hasFocus = searchEditText.hasFocus()
        val history = searchHistory.getHistory()
        val shouldShowHistory = hasFocus && query.isEmpty() && history.isNotEmpty()

        historyContainer.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE
        if (shouldShowHistory) {
            historyAdapter.updateTracks(history)
        }
    }

    private fun updateHistoryAdapter() {
        historyAdapter.updateTracks(searchHistory.getHistory())
    }

    private fun searchTracks(query: String) {
        lastQuery = query

        historyContainer.visibility = View.GONE

        RetrofitClient.api.searchTracks(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(
                call: Call<TrackResponse>,
                response: Response<TrackResponse>
            ) {

                if (response.isSuccessful) {
                    val tracks = response.body()?.results ?: emptyList()
                    if (tracks.isEmpty()) {
                        showEmpty(true)
                        adapter.updateTracks(emptyList())
                    } else {
                        showResults(true)
                        adapter.updateTracks(tracks)
                    }
                } else {
                    showError(true)
                }
            }

            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                showError(true)
            }
        })
    }

    private fun showResults(show: Boolean) {
        recyclerView.visibility = if (show) View.VISIBLE else View.GONE
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.GONE
    }

    private fun showEmpty(show: Boolean) {
        recyclerView.visibility = View.GONE
        placeholderEmpty.visibility = if (show) View.VISIBLE else View.GONE
        placeholderError.visibility = View.GONE
    }

    private fun showError(show: Boolean) {
        recyclerView.visibility = View.GONE
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }
}
