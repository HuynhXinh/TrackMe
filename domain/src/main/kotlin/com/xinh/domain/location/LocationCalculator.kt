package com.xinh.domain.location

import com.xinh.domain.model.MyLocation

interface LocationCalculator {
    fun getDistance(prev: MyLocation?, curr: MyLocation): Float

    fun getSpeed(distance: Float, seconds: Long): Float

    fun getSpeed(distance: Float): Float
}