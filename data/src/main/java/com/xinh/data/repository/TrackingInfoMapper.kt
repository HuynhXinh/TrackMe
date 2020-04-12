package com.xinh.data.repository

import com.xinh.data.repository.dao.TrackingInfoEntity
import com.xinh.domain.model.ItemHistory
import com.xinh.domain.model.MyLocation
import com.xinh.domain.model.TrackingInfo
import java.util.*

fun TrackingInfo.toEntity(
    createDate: Long = Calendar.getInstance().timeInMillis,
    updateDate: Long = Calendar.getInstance().timeInMillis
): TrackingInfoEntity {
    return TrackingInfoEntity(
        id = this.id,
        total_distance = this.totalDistance,
        avg_speed = this.avgSpeed,
        total_time = this.totalTime,
        prev_location = this.prevLocation,
        routes = this.routes?.toMutableList(),
        state = this.state,
        create_date = createDate,
        update_date = updateDate
    )
}

fun TrackingInfoEntity.toTrackingInfo(): TrackingInfo {
    return TrackingInfo(
        id = this.id,
        totalDistance = this.total_distance,
        avgSpeed = this.avg_speed,
        totalTime = this.total_time,
        prevLocation = this.prev_location,
        routes = this.routes,
        state = this.state
    )
}

fun List<TrackingInfoEntity>.toTrackingInfos(): List<TrackingInfo> {
    return this.map { it.toTrackingInfo() }
}