package com.xinh.location

import android.location.Location
import com.xinh.domain.location.LocationCalculator
import com.xinh.domain.location.LocationFormat
import com.xinh.domain.model.MyLocation
import java.math.BigDecimal
import java.math.RoundingMode

const val ONE_KM = 1000F
const val ONE_HOUR_SECONDS = 1 * 60 * 60
const val KM_UNIT = "km"
const val SPEED_UNIT = "km/h"

class LocationCalculatorImpl : LocationCalculator {
    override fun getDistance(prev: MyLocation?, curr: MyLocation): Float {
        prev ?: return 0F

        return curr.distanceToKm(prev)
    }

    override fun getSpeed(distance: Float, seconds: Long): Float {
        return distance / seconds.toHour()
    }

    override fun getSpeed(distance: Float): Float {
        return distance / UPDATE_INTERVAL_IN_MILLISECONDS.toHour()
    }
}

fun Long.toHour(): Float {
    return (this.toFloat() / ONE_HOUR_SECONDS.toFloat())
}

fun MyLocation.toLocation(name: String): Location {
    return Location(name).also {
        it.latitude = this.lat.safe()
        it.longitude = this.lng.safe()
    }
}


fun Double?.safe(default: Double = 0.0): Double {
    return this ?: default
}

fun MyLocation.distanceTo(to: MyLocation?): Float {
    to ?: return 0f
    return this.toLocation("A").distanceTo(to.toLocation("B"))
}

fun MyLocation.distanceToKm(to: MyLocation?): Float {
    return this.distanceTo(to) / ONE_KM
}

class LocationFormatImpl : LocationFormat {
    override fun formatDistance(km: Float): String {
        if (km == 0f) return "-- $KM_UNIT"

        return "${km.round()} $KM_UNIT"
    }

    override fun formatSpeed(speed: Float): String {
        if (speed == 0F) return "-- $SPEED_UNIT"

        return "${speed.round()} $SPEED_UNIT"
    }

    override fun formatTime(seconds: Long): String {
        val h = seconds / 3600
        val secondsLeft = seconds - h * 3600
        val m = secondsLeft / 60
        val s = secondsLeft - m * 60

        return String.format("%02d:%02d:%02d", h, m, s)
    }
}

fun Float.round(): Float {
    return BigDecimal(this.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toFloat()
}