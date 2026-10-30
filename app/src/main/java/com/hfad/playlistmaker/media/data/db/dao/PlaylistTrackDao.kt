package com.hfad.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.hfad.playlistmaker.media.data.db.entity.PlaylistTrackEntity

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)
}