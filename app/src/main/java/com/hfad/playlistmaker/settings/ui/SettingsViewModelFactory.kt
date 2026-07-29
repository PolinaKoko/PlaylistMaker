package com.hfad.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hfad.playlistmaker.settings.domain.SettingsInteractor
import com.hfad.playlistmaker.sharing.domain.SharingInteractor

class SettingsViewModelFactory(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(settingsInteractor, sharingInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}