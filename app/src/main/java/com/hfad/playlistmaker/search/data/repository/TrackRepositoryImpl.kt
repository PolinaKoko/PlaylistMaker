package com.hfad.playlistmaker.search.data.repository


import com.hfad.playlistmaker.search.data.dto.TrackResponseDto
import com.hfad.playlistmaker.search.data.dto.TrackSearchRequest
import com.hfad.playlistmaker.search.data.network.NetworkClient
import com.hfad.playlistmaker.search.domain.TrackRepository
import com.hfad.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl(
    private val networkClient: NetworkClient
) : TrackRepository {

    override fun searchTracks(query: String): Flow<Result<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(query))
        when (response.resultCode) {
            -1 -> emit(Result.failure(Exception("Проверьте подключение к интернету")))
            200 -> {
                with(response as TrackResponseDto) {
                    val tracks = results.map { dto ->
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
                    emit(Result.success(tracks))
                }
            }

            400 -> emit(Result.failure(Exception("Некорректный запрос")))
            500 -> emit(Result.failure(Exception("Ошибка сервера")))
            else -> emit(Result.failure(Exception("Что-то пошло не так")))

        }
    }
}