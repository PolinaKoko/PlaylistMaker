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
        val shareLink = context.getString(R.string.share_app_message)
        val shareTitle = context.getString(R.string.share_app)
        val supportEmail = context.getString(R.string.email)
        val supportSubject = context.getString(R.string.support_email_subject)
        val supportBody = context.getString(R.string.support_email_body)
        val termsLink = context.getString(R.string.terms_url)

        val emailData = Triple(supportEmail, supportSubject, supportBody)

        return SharingInteractorImpl(
            navigator = ExternalNavigator(context),
            shareLink = shareLink,
            shareTitle = shareTitle,
            supportEmailData = emailData,
            termsLink = termsLink
        )
    }

}