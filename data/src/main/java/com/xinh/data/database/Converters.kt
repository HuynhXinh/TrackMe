package com.xinh.data.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.xinh.domain.model.MyLocation
import com.xinh.domain.model.TrackingState
import java.lang.reflect.Type

class Converters {
    @TypeConverter
    fun locationFromString(value: String?): MyLocation? {
        value ?: return null

        return Gson().fromJson(value, MyLocation::class.java)
    }

    @TypeConverter
    fun locationToString(location: MyLocation?): String? {
        location ?: return null

        return Gson().toJson(location)
    }

    @TypeConverter
    fun locationsFromString(value: String?): List<MyLocation>? {
        if (value == null) {
            return null
        }
        val type :Type = object : TypeToken<List<MyLocation?>?>() {}.type
        return Gson().fromJson(value, type)
    }

    @TypeConverter
    fun locationsToString(locations: List<MyLocation>?): String? {
        if (locations == null) {
            return null
        }
        val type = object : TypeToken<List<MyLocation?>?>() {}.type
        return Gson().toJson(locations, type)
    }

    @TypeConverter
    fun stateFromString(value: String): TrackingState {
        return when (value) {
            "Start" -> TrackingState.Start
            "Pause" -> TrackingState.Pause
            "Done" -> TrackingState.Done
            else -> TrackingState.Done
        }
    }

    @TypeConverter
    fun stateToString(state: TrackingState): String {
        return when (state) {
            TrackingState.Start -> "Start"
            TrackingState.Pause -> "Pause"
            TrackingState.Done -> "Done"
        }
    }
}