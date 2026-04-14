package com.hfad.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PlaylistMaker_Settings)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val backButton = findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val shareButton = findViewById<LinearLayout>(R.id.share_button)
        shareButton.setOnClickListener {
            shareApp()
        }

        val supportButton = findViewById<LinearLayout>(R.id.support_button)
        supportButton.setOnClickListener {
            sendSupportEmail()
        }

        val termsButton = findViewById<LinearLayout>(R.id.terms_button)
        termsButton.setOnClickListener {
            openTerms()
        }
    }

    private fun shareApp() {
        val message = getString(R.string.share_app_message)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
    }

    private fun sendSupportEmail() {
        val email = getString(R.string.email)
        val subject = getString(R.string.support_email_subject)
        val body = getString(R.string.support_email_body)

        val supportIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:".toUri()
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(supportIntent)
    }

    private fun openTerms() {
        val url = getString(R.string.terms_url)
        val termsIntent = Intent(Intent.ACTION_VIEW, url.toUri())
        startActivity(termsIntent)
    }
}