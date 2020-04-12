package com.xinh.domain.model

import java.util.*

data class TrackingInfo(
    var id: String = UUID.randomUUID().toString(),
    var totalDistance: Float = 0F,
    var avgSpeed: Float = 0F,
    var totalTime: Long = 0,
    var prevLocation: MyLocation? = null,
    var routes: List<MyLocation>? = null,
    var state: TrackingState = TrackingState.Done
)

sealed class TrackingState {
    object Start : TrackingState()
    object Pause : TrackingState()
    object Done : TrackingState()
}