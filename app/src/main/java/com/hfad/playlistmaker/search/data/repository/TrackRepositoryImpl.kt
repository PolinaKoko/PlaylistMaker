package com.hfad.playlistmaker.search.data.repository


import com.hfad.playlistmaker.search.data.network.RetrofitClient
import com.hfad.playlistmaker.search.domain.TrackRepository
import com.hfad.playlistmaker.search.domain.models.Track
import com.hfad.playlistmaker.util.Resource
import java.io.IOException

class TrackRepositoryImpl : TrackRepository {

    override fun searchTracks(query: String): Resource<List<Track>> {
        return try {
            val response = RetrofitClient.api.searchTracks(query).execute()

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
                    Resource.Success(tracks)
                } else {
                    Resource.Success(emptyList())
                }
            } else {
                Resource.Error("Ошибка сервера: ${response.code()}")
            }
        } catch (e: IOException) {
            Resource.Error("Проверьте подключение к интернету")
        } catch (e: Exception) {
            Resource.Error("Что-то пошло не так")
        }
    }
}