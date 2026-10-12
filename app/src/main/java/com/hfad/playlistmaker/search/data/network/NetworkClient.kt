package com.hfad.playlistmaker.search.data.network

import com.hfad.playlistmaker.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}