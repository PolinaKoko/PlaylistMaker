package com.hfad.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hfad.playlistmaker.media.data.db.dao.FavoriteDao
import com.hfad.playlistmaker.media.data.db.dao.PlaylistDao
import com.hfad.playlistmaker.media.data.db.dao.PlaylistTrackDao
import com.hfad.playlistmaker.media.data.db.entity.PlaylistEntity
import com.hfad.playlistmaker.media.data.db.entity.PlaylistTrackEntity
import com.hfad.playlistmaker.media.data.db.entity.TrackEntity

@Database(
    version = 4,
    entities = [TrackEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class],
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistTrackDao(): PlaylistTrackDao
}