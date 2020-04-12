package com.xinh.location.rx

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.xinh.domain.model.MyLocation
import com.xinh.location.rx.RxFusedLocation.Companion.locationRequest
import io.reactivex.Observable
import io.reactivex.Single

class RxFusedLocationImpl(val context: Context) : RxFusedLocation {

    @SuppressLint("MissingPermission")
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
    override fun getCurrentLocation(): Observable<LocationResult> {
        return getCurrentLocation(context)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(context: Context): Observable<LocationResult> {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        return Observable.create {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    if (!it.isDisposed) {
                        it.onNext(locationResult)
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

            it.setCancellable { fusedLocationClient.removeLocationUpdates(locationCallback) }
        }
    }
}
