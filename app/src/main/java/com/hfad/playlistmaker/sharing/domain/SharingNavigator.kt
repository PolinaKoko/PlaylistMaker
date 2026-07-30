package com.hfad.playlistmaker.sharing.domain

interface SharingNavigator {
    fun shareLink(link: String, title: String)
    fun openEmail(emailData: Triple<String, String, String>)
    fun openLink(url: String)
}