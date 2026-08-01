package com.hfad.playlistmaker.settings.domain

import com.hfad.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}