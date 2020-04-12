package com.xinh.location

import android.content.Context
import android.os.Looper
import com.google.android.gms.location.*
import com.xinh.domain.model.MyLocation

interface FusedLocationHelper {

    fun startDetectLocation(callback: ((MyLocation) -> Unit)? = null)

    fun stop()
}

const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 2000

class FusedLocationHelperImpl(private val context: Context) : FusedLocationHelper {
    companion object {
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS
    }

    private val locationRequest = LocationRequest().apply {
        interval = UPDATE_INTERVAL_IN_MILLISECONDS
        fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    override fun startDetectLocation(callback: ((MyLocation) -> Unit)?) {
       fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                callback?.invoke(
                    MyLocation(
                        lat = locationResult.lastLocation.latitude,
                        lng = locationResult.lastLocation.longitude
                    )
                )
            }
        }

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun stop() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
    }
}