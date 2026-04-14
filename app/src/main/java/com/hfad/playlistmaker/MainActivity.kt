package com.hfad.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

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