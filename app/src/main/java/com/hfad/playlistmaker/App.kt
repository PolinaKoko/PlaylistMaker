package com.hfad.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    override fun onCreate() {
        super.onCreate()
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