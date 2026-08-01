package com.hfad.playlistmaker.settings.data

import android.content.SharedPreferences
import com.hfad.playlistmaker.settings.domain.SettingsRepository
import com.hfad.playlistmaker.settings.domain.model.ThemeSettings

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        val isDark = sharedPreferences.getBoolean(THEME_KEY, false)
        return ThemeSettings(isDark)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(THEME_KEY, settings.isDarkTheme)
            .apply()
    }

    companion object {
        private const val THEME_KEY = "dark_theme"
    }
}