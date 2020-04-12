package com.example.trackme.mapper

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.xinh.domain.model.MyLocation
import com.xinh.location.safe


fun MyLocation.toLatLng(): LatLng {
    return LatLng(this.lat.safe(), this.lng.safe())
}

fun List<MyLocation>.toLatLngs(): List<LatLng> {
    return this.map { it.toLatLng() }
}

fun empPolylineOptions(): PolylineOptions {
    return PolylineOptions()
            .width(5F)
            .color(Color.RED)
            .geodesic(true)
}

fun List<MyLocation>.toPolylineOptions(): PolylineOptions {
    return empPolylineOptions().addAll(this.toLatLngs())
}

