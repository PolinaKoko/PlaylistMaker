package com.hfad.playlistmaker.data.network

import com.hfad.playlistmaker.data.dto.TrackResponseDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApi {
    @GET("/search?entity=song")
    fun searchTracks(
        @Query("term") query: String
    ): Call<TrackResponseDto>
}