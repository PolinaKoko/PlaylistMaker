package com.hfad.playlistmaker.sharing.domain.impl

import com.hfad.playlistmaker.sharing.data.ExternalNavigator
import com.hfad.playlistmaker.sharing.domain.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator
) : SharingInteractor {

    override fun shareApp() {
        val link = "https://practicum.yandex.ru/android-developer/"
        val title = "Поделиться приложением"
        externalNavigator.shareLink(link, title)
    }

    override fun openSupport() {
        val emailData = Triple(
            "kovalevap2019@gmail.com",
            "Сообщение разработчикам и разработчицам приложения Playlist Maker",
            "Спасибо разработчикам и разработчицам за крутое приложение!"
        )
        externalNavigator.openEmail(emailData)
    }

    override fun openTerms() {
        externalNavigator.openLink("https://yandex.ru/legal/practicum_offer/ru/")
    }
}