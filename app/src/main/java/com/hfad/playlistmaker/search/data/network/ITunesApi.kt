package com.hfad.playlistmaker.search.data.network

import com.hfad.playlistmaker.search.data.dto.TrackResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    suspend fun searchTracks(
        @Query("term") query: String
    ): TrackResponseDto
}