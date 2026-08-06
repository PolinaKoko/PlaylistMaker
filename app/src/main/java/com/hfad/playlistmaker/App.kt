package com.hfad.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.hfad.playlistmaker.di.dataModule
import com.hfad.playlistmaker.di.domainModule
import com.hfad.playlistmaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                domainModule,
                viewModelModule
            )
        }

        applyTheme()
    }

    fun applyTheme() {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val isDark = sharedPrefs.getBoolean(THEME_KEY, false)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    companion object {
        const val PREFS_NAME = "playlist_maker_prefs"
        const val THEME_KEY = "dark_theme"
    }
}