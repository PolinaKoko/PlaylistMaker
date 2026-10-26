package com.hfad.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hfad.playlistmaker.media.data.db.dao.FavoriteDao
import com.hfad.playlistmaker.media.data.db.entity.TrackEntity

@Database(version = 1, entities = [TrackEntity::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao
}