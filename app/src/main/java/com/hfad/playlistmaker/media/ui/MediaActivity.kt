package com.hfad.playlistmaker.media.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.hfad.playlistmaker.R
import com.hfad.playlistmaker.databinding.ActivityMediaBinding

class MediaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediaBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.viewPager.adapter = MediaPagerAdapter(
            fragmentManager = supportFragmentManager,
            lifecycle = lifecycle
        )

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.favorites)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        savedInstanceState?.getInt(CURRENT_TAB_KEY)?.let { savedPosition ->
            binding.viewPager.currentItem = savedPosition
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_TAB_KEY, binding.viewPager.currentItem)
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

    companion object {
        private const val CURRENT_TAB_KEY = "current_tab"
    }
}