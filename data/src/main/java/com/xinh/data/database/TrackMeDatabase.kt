package com.xinh.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xinh.data.repository.dao.TrackingInfoDao
import com.xinh.data.repository.dao.TrackingInfoEntity

const val DB_VERSION = 1
private const val DB_NAME = "trackme"

@Database(
    version = DB_VERSION,
    entities = [TrackingInfoEntity::class]
)
@TypeConverters(Converters::class)
abstract class TrackMeDatabase : RoomDatabase() {

    companion object {
        fun createDatabase(application: Application): TrackMeDatabase? {
            return Room.databaseBuilder(application, TrackMeDatabase::class.java, DB_NAME)
                .build()
        }
    }

    abstract fun getTrackingInfoDao(): TrackingInfoDao
}