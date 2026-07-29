package com.hfad.playlistmaker.main.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.player.ui.MediaActivity
import com.hfad.playlistmaker.search.ui.SearchActivity
import com.hfad.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_PlaylistMaker_Main)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.button_search)
        val mediaButton = findViewById<Button>(R.id.button_media)
        val settingButton = findViewById<Button>(R.id.button_setting)

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        mediaButton.setOnClickListener {
            startActivity(Intent(this, MediaActivity::class.java))
        }

        settingButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}