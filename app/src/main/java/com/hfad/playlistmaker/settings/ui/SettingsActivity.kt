package com.hfad.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.hfad.playlistmaker.App
import com.hfad.playlistmaker.Creator
import com.hfad.playlistmaker.R


class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PlaylistMaker_Settings)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = ViewModelProvider(
            this, SettingsViewModelFactory(
                (applicationContext as App).let { app ->
                    val sharedPrefs = app.getSharedPreferences(App.PREFS_NAME, MODE_PRIVATE)
                    Creator.provideSettingsInteractor(sharedPrefs)
                },
                Creator.provideSharingInteractor(this)
            )
        ).get(SettingsViewModel::class.java)

        findViewById<ImageView>(R.id.back_button).setOnClickListener {
            finish()
        }

        val themeSwitch = findViewById<SwitchMaterial>(R.id.theme_switch)

        viewModel.themeState.observe(this) { settings ->
            themeSwitch.isChecked = settings.isDarkTheme
        }


        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeChanged(isChecked)
            (applicationContext as App).applyTheme()
            window.decorView.invalidate()
        }

        findViewById<LinearLayout>(R.id.share_button).setOnClickListener {
            viewModel.shareApp()
        }

        findViewById<LinearLayout>(R.id.support_button).setOnClickListener {
            viewModel.openSupport()
        }

        findViewById<LinearLayout>(R.id.terms_button).setOnClickListener {
            viewModel.openTerms()
        }
    }
}