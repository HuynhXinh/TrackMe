package com.xinh.domain.location

interface LocationFormat {
    fun formatDistance(km: Float): String

    fun formatSpeed(speed: Float): String

    fun formatTime(seconds: Long): String
}