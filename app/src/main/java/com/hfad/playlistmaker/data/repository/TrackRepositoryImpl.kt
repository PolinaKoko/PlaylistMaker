package com.hfad.playlistmaker.data.repository

import com.hfad.playlistmaker.data.network.RetrofitClient
import com.hfad.playlistmaker.domain.api.TrackRepository
import com.hfad.playlistmaker.domain.models.Track

class TrackRepositoryImpl : TrackRepository {
    override fun searchTracks(query: String): List<Track> {
        try {
            val response = RetrofitClient.api.searchTracks(query).execute()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    return body.results.map { dto ->
                        Track(
                            trackId = dto.trackId,
                            trackName = dto.trackName,
                            artistName = dto.artistName,
                            trackTimeMillis = dto.trackTimeMillis,
                            artworkUrl100 = dto.artworkUrl100,
                            collectionName = dto.collectionName,
                            releaseDate = dto.releaseDate,
                            primaryGenreName = dto.primaryGenreName,
                            country = dto.country,
                            previewUrl = dto.previewUrl

                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return emptyList()
    }
}