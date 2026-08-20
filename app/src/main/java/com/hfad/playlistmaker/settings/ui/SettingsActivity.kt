package com.hfad.playlistmaker.settings.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hfad.playlistmaker.App
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.databinding.ActivitySettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel {
        parametersOf(this)
    }

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PlaylistMaker_Settings)
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val themeSwitch = binding.themeSwitch

        viewModel.themeState.observe(this) { settings ->
            themeSwitch.isChecked = settings.isDarkTheme
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeChanged(isChecked)
            (applicationContext as App).applyTheme()
            window.decorView.invalidate()
        }

        binding.shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        binding.supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        binding.termsButton.setOnClickListener {
            viewModel.openTerms()
        }
    }
}