package com.xinh.data.repository

import android.net.Uri
import com.xinh.domain.model.MyLocation
import java.lang.StringBuilder

class StaticMapBuilder(private val routes: List<MyLocation>) {
    private val uri = Uri.parse("https://maps.googleapis.com/maps/api/staticmap?")

    companion object {
        private const val PARAMETER_KEY = "key"
        private const val PARAMETER_SIZE = "size"
        private const val PARAMETER_SCALE = "scale"
        private const val PARAMETER_ZOOM = "zoom"
        private const val PARAMETER_SENSOR = "sensor"
        private const val PARAMETER_CENTER = "center"
        private const val PARAMETER_MARKERS = "markers"
        private const val PARAMETER_PATH = "path"
    }

    fun build(): String {
        return uri.buildUpon()
            .appendQueryParameter(PARAMETER_KEY, "AIzaSyCL1rYXqtuCDZvIsYlRi4L7GzMu_UG1VCM")
            .appendQueryParameter(PARAMETER_SIZE, "640x640")
            .appendQueryParameter(PARAMETER_SCALE, "2")
            .appendQueryParameter(PARAMETER_ZOOM, "14")
            .appendQueryParameter(PARAMETER_SENSOR, "false")
            .appendQueryParameter(PARAMETER_CENTER, getCenterMap())
            .appendQueryParameter(PARAMETER_MARKERS, getMarkerStart())
            .appendQueryParameter(PARAMETER_MARKERS, getMarkerEnd())
            .appendQueryParameter(PARAMETER_PATH, getPath())
            .build()
            .toString()
    }

    private fun getPath(): String? {
        if (routes.isEmpty()) return null
        val stringBuilder = StringBuilder()
            .append("color:0xff0000ff")
            .append("|weight:5")

        routes.forEach {
            stringBuilder.append("|${it.lat},${it.lng}")
        }

        return stringBuilder.toString()
    }

    private fun getMarkerEnd(): String? {
        return routes.lastOrNull()?.let {
            "color:blue|label:E|${it.lat},${it.lng}"
        }
    }

    private fun getMarkerStart(): String? {
        return routes.firstOrNull()?.let {
            "color:red|label:S|${it.lat},${it.lng}"
        }
    }

    private fun getCenterMap(): String? {
        if (routes.isEmpty()) return null

        val center = routes[routes.size / 2]

        return "${center.lat},${center.lng}"
    }
}