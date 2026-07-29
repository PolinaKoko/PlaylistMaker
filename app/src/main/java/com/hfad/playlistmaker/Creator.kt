package com.hfad.playlistmaker

import android.content.Context
import android.content.SharedPreferences

import com.hfad.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.hfad.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.hfad.playlistmaker.search.domain.SearchHistoryInteractor
import com.hfad.playlistmaker.search.domain.SearchHistoryRepository
import com.hfad.playlistmaker.search.domain.TrackInteractor
import com.hfad.playlistmaker.search.domain.TrackRepository
import com.hfad.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.hfad.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.hfad.playlistmaker.settings.data.SettingsRepositoryImpl
import com.hfad.playlistmaker.settings.domain.SettingsInteractor
import com.hfad.playlistmaker.settings.domain.SettingsRepository
import com.hfad.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.hfad.playlistmaker.sharing.data.ExternalNavigator
import com.hfad.playlistmaker.sharing.domain.SharingInteractor
import com.hfad.playlistmaker.sharing.domain.impl.SharingInteractorImpl

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

    private fun getSettingsRepository(sharedPreferences: SharedPreferences): SettingsRepository {
        return SettingsRepositoryImpl(sharedPreferences)
    }

    fun provideSettingsInteractor(sharedPreferences: SharedPreferences): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(sharedPreferences))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigator(context))
    }

}