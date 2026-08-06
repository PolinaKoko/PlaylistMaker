package com.hfad.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.databinding.ActivityMainBinding
import com.hfad.playlistmaker.player.ui.MediaActivity
import com.hfad.playlistmaker.search.ui.SearchActivity
import com.hfad.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_PlaylistMaker_Main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.buttonMedia.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        binding.buttonSetting.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}