package com.hfad.playlistmaker.di

import com.hfad.playlistmaker.media.ui.favorites.FavoritesViewModel
import com.hfad.playlistmaker.media.ui.playlists.PlaylistsViewModel
import com.hfad.playlistmaker.player.ui.PlayerViewModel
import com.hfad.playlistmaker.search.ui.SearchViewModel
import com.hfad.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            get(),
            get()
        )
    }

    viewModel {
        SettingsViewModel(
            get(),
            get()
        )
    }

    viewModel {
        PlayerViewModel()
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

}