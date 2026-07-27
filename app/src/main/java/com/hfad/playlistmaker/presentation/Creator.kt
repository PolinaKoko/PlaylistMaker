package com.hfad.playlistmaker.presentation

import android.content.SharedPreferences
import com.hfad.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.hfad.playlistmaker.data.repository.TrackRepositoryImpl
import com.hfad.playlistmaker.domain.api.SearchHistoryInteractor
import com.hfad.playlistmaker.domain.api.SearchHistoryRepository
import com.hfad.playlistmaker.domain.api.TrackInteractor
import com.hfad.playlistmaker.domain.api.TrackRepository
import com.hfad.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.hfad.playlistmaker.domain.impl.TrackInteractorImpl

object Creator {
    private fun getTrackRepository(): TrackRepository {
        return TrackRepositoryImpl()
    }

    private fun getHistoryRepository(sharedPreferences: SharedPreferences): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(sharedPreferences)
    }

    fun provideTrackInteractor(): TrackInteractor {
        return TrackInteractorImpl(getTrackRepository())
    }

    fun provideSearchHistoryInteractor(sharedPreferences: SharedPreferences): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getHistoryRepository(sharedPreferences))
    }

}