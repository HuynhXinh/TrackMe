package com.example.trackme.ui.tracking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.example.trackme.R
import com.example.trackme.mapper.empPolylineOptions
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*


interface MapHelper {
    fun init(fragment: SupportMapFragment?, onMapReady: () -> Unit)

    fun waitMapReady(function: GoogleMap.() -> Unit)

    fun enableMyLocation(enable: Boolean)

    fun showStartMarker(context: Context, latLng: LatLng)

    fun showRoutes(latLngs: List<LatLng>)

    fun appendRoute(prev: LatLng, next: LatLng)

    fun clear()

    fun zoom(target: LatLng, zoomLevel: Float)
}

class MapHelperImpl : MapHelper {
    private val queue = arrayListOf<GoogleMap.() -> Unit>()
    private var googleMap: GoogleMap? = null
    private var context: Context? = null

    private var polylineOptions = empPolylineOptions()

    override fun init(fragment: SupportMapFragment?, onMapReady: () -> Unit) {
        fragment ?: return

        context = fragment.context
        fragment.getMapAsync {
            googleMap = it

            initSetting()

            notifyMapReady()

            onMapReady.invoke()
        }
    }

    private fun notifyMapReady() {
        queue.forEach { it(googleMap!!) }
        queue.clear()
    }

    private fun initSetting() {
        googleMap?.uiSettings?.apply {
            isZoomControlsEnabled = false
            isMapToolbarEnabled = false
            isCompassEnabled = false
            isMyLocationButtonEnabled = false
        }
    }

    override fun waitMapReady(function: GoogleMap.() -> Unit) {
        if (googleMap != null) function(googleMap!!)
        else queue.add(function)
    }

    override fun enableMyLocation(enable: Boolean) {
        waitMapReady {
            this.apply {
                isMyLocationEnabled = enable
                uiSettings.isMyLocationButtonEnabled = enable
            }
        }
    }

    override fun showStartMarker(context: Context, latLng: LatLng) {
        waitMapReady {
            addMarker(context, latLng = latLng, icMarker = R.drawable.ic_marker_start_tracking)
        }
    }

    private fun addMarker(context: Context, latLng: LatLng, @DrawableRes icMarker: Int): Marker? {
        val markerOptions =
            MarkerOptions()
                .icon(createIconMarker(context, icMarker))
                .position(latLng)

        return googleMap?.addMarker(markerOptions)
    }

    private fun createIconMarker(context: Context, @DrawableRes resId: Int): BitmapDescriptor? {
        val vectorDrawable: Drawable = ContextCompat.getDrawable(context, resId) ?: return null

        vectorDrawable.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    override fun showRoutes(latLngs: List<LatLng>) {
        waitMapReady {
            polylineOptions.addAll(latLngs)
            addPolyline(polylineOptions)
        }
    }

    override fun appendRoute(prev: LatLng, next: LatLng) {
        waitMapReady {
            polylineOptions
                .add(prev)
                .add(next)
            addPolyline(polylineOptions)
        }
    }

    override fun zoom(target: LatLng, zoomLevel: Float) {
        waitMapReady {
            moveCamera(toCameraUpdate(target, zoomLevel))
        }
    }

    private fun toCameraUpdate(target: LatLng, zoomLevel: Float): CameraUpdate {
        val cameraPosition = CameraPosition.Builder()
            .target(target)
            .zoom(zoomLevel)
            .build()

        return CameraUpdateFactory.newCameraPosition(cameraPosition)
    }

    override fun clear() {
        queue.clear()

        googleMap?.clear()
        polylineOptions = empPolylineOptions()
    }
}