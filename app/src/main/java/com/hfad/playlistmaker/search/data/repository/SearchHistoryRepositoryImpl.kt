package com.hfad.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import com.hfad.playlistmaker.search.domain.SearchHistoryRepository
import com.hfad.playlistmaker.search.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson
) :
    SearchHistoryRepository {

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null)
        return if (json != null) {
            gson.fromJson(json, TRACK_LIST_TYPE)
        } else {
            emptyList()
        }
    }

    override fun addTrack(track: Track) {
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }

        currentHistory.add(0, track)

        val updatedHistory = if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.subList(0, MAX_HISTORY_SIZE)
        } else {
            currentHistory
        }
        saveHistory(updatedHistory)
    }

    override fun clearHistory() {
        saveHistory(emptyList())
    }

    private fun saveHistory(history: List<Track>) {
        val json = gson.toJson(history)
        sharedPreferences.edit().putString(HISTORY_KEY, json).apply()
    }

    companion object {
        private val TRACK_LIST_TYPE = object : TypeToken<List<Track>>() {}.type
        private const val HISTORY_KEY = "search_history"
        private const val MAX_HISTORY_SIZE = 10
    }
}