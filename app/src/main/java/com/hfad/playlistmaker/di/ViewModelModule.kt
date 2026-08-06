package com.hfad.playlistmaker.di

import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.player.ui.PlayerViewModel
import com.hfad.playlistmaker.search.ui.SearchViewModel
import com.hfad.playlistmaker.settings.ui.ExternalNavigator
import com.hfad.playlistmaker.settings.ui.SettingsViewModel
import com.hfad.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(
            get(),
            get()
        )
    }

    viewModel { params ->
        SettingsViewModel(
            get(),
            SharingInteractorImpl(
                ExternalNavigator(params.get()),
                androidContext().getString(R.string.share_app_message),
                androidContext().getString(R.string.share_app),
                Triple(
                    androidContext().getString(R.string.email),
                    androidContext().getString(R.string.support_email_subject),
                    androidContext().getString(R.string.support_email_body)
                ),
                androidContext().getString(R.string.terms_url)
            )
        )
    }

    viewModel {
        PlayerViewModel()
    }

}