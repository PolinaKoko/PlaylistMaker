package com.hfad.playlistmaker.settings.domain

import com.hfad.playlistmaker.settings.domain.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
}