package com.hfad.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

class ExternalNavigator(
    private val context: Context
) {

    fun shareLink(link: String, title: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, link)
        }
        context.startActivity(Intent.createChooser(intent, title))
    }

    fun openEmail(emailData: Triple<String, String, String>) {
        val (email, subject, body) = emailData
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        context.startActivity(intent)
    }

    fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        context.startActivity(intent)
    }
}