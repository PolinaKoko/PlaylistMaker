package com.hfad.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hfad.playlistmaker.settings.domain.SettingsInteractor
import com.hfad.playlistmaker.settings.domain.model.ThemeSettings
import com.hfad.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _themeState = MutableLiveData<ThemeSettings>()
    val themeState: LiveData<ThemeSettings> = _themeState

    init {
        _themeState.value = settingsInteractor.getThemeSettings()
    }

    fun onThemeChanged(isDark: Boolean) {
        val newSettings = ThemeSettings(isDark)
        settingsInteractor.updateThemeSettings(newSettings)
        _themeState.value = newSettings
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
}