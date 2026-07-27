package com.hfad.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false

    fun isDarkTheme(): Boolean = darkTheme

    override fun onCreate() {
        super.onCreate()

        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(THEME_KEY, false)
        switchTheme(darkTheme)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        val sharedPrefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(THEME_KEY, darkThemeEnabled)
            .apply()
    }

    companion object {
        const val PREFS_NAME = "playlist_maker_prefs"
        const val THEME_KEY = "dark_theme"
    }
}