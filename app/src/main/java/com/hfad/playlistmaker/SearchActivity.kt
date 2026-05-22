package com.hfad.playlistmaker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
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
    private lateinit var adapter: TrackAdapter
    private var lastQuery = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<ImageView>(R.id.back_button)
        searchEditText = findViewById(R.id.search_edit_text)
        clearButton = findViewById(R.id.clear_button)
        recyclerView = findViewById(R.id.rvTracks)
        placeholderEmpty = findViewById(R.id.placeholderEmpty)
        placeholderError = findViewById(R.id.placeholderError)
        retryButton = findViewById(R.id.retryButton)

        adapter = TrackAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        backButton.setOnClickListener {
            finish()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                if (s.isNullOrEmpty()) {
                    adapter.updateTracks(emptyList())
                    showResults(false)
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

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
        }

        retryButton.setOnClickListener {
            if (lastQuery.isNotEmpty()) {
                searchTracks(lastQuery)
            }
        }
    }

    private fun searchTracks(query: String) {
        lastQuery = query

        RetrofitClient.api.searchTracks(query).enqueue(object : Callback<TrackResponse> {
            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                when (response.code()) {
                    200 -> {
                        val tracks = response.body()?.results ?: emptyList()
                        if (tracks.isEmpty()) {
                            showEmpty(true)
                            adapter.updateTracks(emptyList())
                        } else {
                            showResults(true)
                            adapter.updateTracks(tracks)
                        }
                    }

                    else -> showError(true)
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
