package com.hfad.playlistmaker.di

import com.hfad.playlistmaker.search.domain.SearchHistoryInteractor
import com.hfad.playlistmaker.search.domain.TrackInteractor
import com.hfad.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.hfad.playlistmaker.search.domain.impl.TrackInteractorImpl
import com.hfad.playlistmaker.settings.domain.SettingsInteractor
import com.hfad.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.hfad.playlistmaker.sharing.domain.SharingInteractor
import com.hfad.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val domainModule = module {

    single<TrackInteractor> {
        TrackInteractorImpl(get())
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(
            get(),
            androidContext().getString(com.hfad.playlistmaker.R.string.share_app_message),
            androidContext().getString(com.hfad.playlistmaker.R.string.share_app),
            Triple(
                androidContext().getString(com.hfad.playlistmaker.R.string.email),
                androidContext().getString(com.hfad.playlistmaker.R.string.support_email_subject),
                androidContext().getString(com.hfad.playlistmaker.R.string.support_email_body)
            ),
            androidContext().getString(com.hfad.playlistmaker.R.string.terms_url)
        )
    }
}