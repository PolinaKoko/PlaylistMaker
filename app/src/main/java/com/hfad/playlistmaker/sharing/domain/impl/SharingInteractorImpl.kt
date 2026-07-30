package com.hfad.playlistmaker.sharing.domain.impl

import com.hfad.playlistmaker.sharing.domain.SharingInteractor
import com.hfad.playlistmaker.sharing.domain.SharingNavigator

class SharingInteractorImpl(
    private val navigator: SharingNavigator,
    private val shareLink: String,
    private val shareTitle: String,
    private val supportEmailData: Triple<String, String, String>,
    private val termsLink: String
) : SharingInteractor {

    override fun shareApp() {
        navigator.shareLink(shareLink, shareTitle)
    }

    override fun openSupport() {
        navigator.openEmail(supportEmailData)
    }

    override fun openTerms() {
        navigator.openLink(termsLink)
    }
}