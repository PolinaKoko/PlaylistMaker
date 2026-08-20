package com.hfad.playlistmaker.settings.ui

import android.app.Activity
import android.content.Intent
import androidx.core.net.toUri
import com.hfad.playlistmaker.sharing.domain.SharingNavigator

class ExternalNavigator(
    private val activity: Activity
) : SharingNavigator {

    override fun shareLink(link: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        activity.startActivity(Intent.createChooser(intent, title))
    }

    override fun openEmail(emailData: Triple<String, String, String>) {
        val (email, subject, body) = emailData
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        activity.startActivity(intent)
    }

    override fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
        }
        activity.startActivity(intent)
    }
}