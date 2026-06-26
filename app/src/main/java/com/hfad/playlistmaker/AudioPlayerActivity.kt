package com.hfad.playlistmaker

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("track") as? Track
        } ?: return

        fillData()
    }

    private fun fillData() {

        findViewById<TextView>(R.id.tvTrackName).text = track.trackName
        findViewById<TextView>(R.id.tvArtistName).text = track.artistName

        val tvAlbumName = findViewById<TextView>(R.id.tvAlbumName)
        val tvAlbumLabel = findViewById<TextView>(R.id.tvAlbumLabel)

        if (!track.collectionName.isNullOrEmpty()) {
            tvAlbumName.text = track.collectionName
            tvAlbumName.visibility = View.VISIBLE
            tvAlbumLabel.visibility = View.VISIBLE
        } else {
            tvAlbumName.visibility = View.GONE
            tvAlbumLabel.visibility = View.GONE
        }

        findViewById<TextView>(R.id.tvDuration).text = track.getTrackTime()

        val tvYear = findViewById<TextView>(R.id.tvYear)
        val tvYearLabel = findViewById<TextView>(R.id.tvYearLabel)
        val releaseDate = track.releaseDate

        if (!releaseDate.isNullOrEmpty() && releaseDate.length >= 4) {
            val year = releaseDate.substring(0, 4)
            tvYear.text = year
            tvYear.visibility = View.VISIBLE
            tvYearLabel.visibility = View.VISIBLE
        } else {
            tvYear.visibility = View.GONE
            tvYearLabel.visibility = View.GONE
        }

        findViewById<TextView>(R.id.tvGenre).text = track.primaryGenreName ?: ""
        findViewById<TextView>(R.id.tvCountry).text = track.country ?: ""

        Glide.with(this)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .transform(RoundedCorners(8))
            .into(findViewById(R.id.ivCoverArtwork))

        findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
}