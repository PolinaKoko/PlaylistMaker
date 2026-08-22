package com.hfad.playlistmaker.di

import android.content.Context
import com.google.gson.Gson
import com.hfad.playlistmaker.search.data.network.ITunesApi
import com.hfad.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.hfad.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.hfad.playlistmaker.search.domain.SearchHistoryRepository
import com.hfad.playlistmaker.search.domain.TrackRepository
import com.hfad.playlistmaker.settings.data.SettingsRepositoryImpl
import com.hfad.playlistmaker.settings.domain.SettingsRepository
import com.hfad.playlistmaker.settings.ui.ExternalNavigator
import com.hfad.playlistmaker.sharing.domain.SharingNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    factory { Gson() }

    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<ITunesApi> {
        get<Retrofit>().create(ITunesApi::class.java)
    }

    single {
        androidContext().getSharedPreferences(
            "playlist_maker_prefs",
            Context.MODE_PRIVATE
        )
    }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(), get())
    }

    factory<TrackRepository> {
        TrackRepositoryImpl(get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    factory<SharingNavigator> {
        ExternalNavigator(get())
    }
}
