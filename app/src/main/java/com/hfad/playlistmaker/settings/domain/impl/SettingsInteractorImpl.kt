package com.hfad.playlistmaker.settings.domain.impl

import com.hfad.playlistmaker.settings.domain.SettingsInteractor
import com.hfad.playlistmaker.settings.domain.SettingsRepository
import com.hfad.playlistmaker.settings.domain.model.ThemeSettings

class SettingsInteractorImpl(private val repository: SettingsRepository) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        repository.updateThemeSettings(settings)
    }
}