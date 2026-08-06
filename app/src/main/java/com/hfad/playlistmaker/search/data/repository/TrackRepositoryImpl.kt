package com.hfad.playlistmaker.search.data.repository


import com.hfad.playlistmaker.search.data.network.ITunesApi
import com.hfad.playlistmaker.search.domain.TrackRepository
import com.hfad.playlistmaker.search.domain.models.Track
import java.io.IOException

class TrackRepositoryImpl(private val api: ITunesApi) : TrackRepository {

    override fun searchTracks(query: String): Result<List<Track>> {
        return try {
            val response = api.searchTracks(query).execute()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null && !body.results.isNullOrEmpty()) {
                    val tracks = body.results.map { dto ->
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
                    Result.success(tracks)
                } else {
                    Result.success(emptyList())
                }
            } else {
                Result.failure(IOException("Ошибка сервера: ${response.code()}"))
            }
        } catch (e: IOException) {
            Result.failure(IOException("Проверьте подключение к интернету"))
        } catch (e: Exception) {
            Result.failure(Exception("Что-то пошло не так"))
        }
    }
}