package com.xinh.data.repository.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xinh.domain.model.MyLocation
import com.xinh.domain.model.TrackingState

@Entity(tableName = "tracking_info")
class TrackingInfoEntity(
    @PrimaryKey
    var id: String,
    var total_distance: Float = 0F,
    var avg_speed: Float = 0F,
    var total_time: Long = 0,
    var prev_location: MyLocation? = null,
    var routes: List<MyLocation>? = null,
    var state: TrackingState = TrackingState.Start,
    var create_date: Long,
    var update_date: Long
)