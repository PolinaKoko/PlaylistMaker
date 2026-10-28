package com.hfad.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.hfad.playlistmaker.media.data.db.AppDatabase
import com.hfad.playlistmaker.media.data.db.converters.PlaylistDbConvertor
import com.hfad.playlistmaker.media.data.db.converters.TrackDbConvertor
import com.hfad.playlistmaker.media.data.repository.FavoritesRepositoryImpl
import com.hfad.playlistmaker.media.data.repository.PlaylistRepositoryImpl
import com.hfad.playlistmaker.media.domain.db.FavoritesRepository
import com.hfad.playlistmaker.media.domain.db.PlaylistRepository
import com.hfad.playlistmaker.search.data.network.ITunesApi
import com.hfad.playlistmaker.search.data.network.NetworkClient
import com.hfad.playlistmaker.search.data.network.RetrofitNetworkClient
import com.hfad.playlistmaker.search.data.repository.SearchHistoryRepositoryImpl
import com.hfad.playlistmaker.search.data.repository.TrackRepositoryImpl
import com.hfad.playlistmaker.search.domain.SearchHistoryRepository
import com.hfad.playlistmaker.search.domain.TrackRepository
import com.hfad.playlistmaker.settings.data.SettingsRepositoryImpl
import com.hfad.playlistmaker.settings.domain.SettingsRepository
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

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single {
        androidContext().getSharedPreferences(
            "playlist_maker_prefs",
            Context.MODE_PRIVATE
        )
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    factory { TrackDbConvertor() }

    factory { PlaylistDbConvertor() }

    single { get<AppDatabase>().playlistTrackDao() }

    single { get<AppDatabase>().favoriteDao() }

    single { get<AppDatabase>().playlistDao() }

    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), get())
    }

    factory<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
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

}
