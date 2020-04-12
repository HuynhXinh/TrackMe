package com.example.trackme.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.trackme.MainActivity
import com.example.trackme.R
import com.github.ajalt.timberkt.Timber
import com.xinh.location.FusedLocationHelper
import com.xinh.presentation.tracking.TrackMeViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class TrackMeService : Service(), KoinComponent {
    private val binder = TrackMeBinder()

    private val fusedLocationHelper: FusedLocationHelper by inject()
    private val trackMeViewModel: TrackMeViewModel by inject()

    companion object {
        private const val CHANNEL_ID = "TrackMeService"
        private const val CHANNEL_NAME = "TrackMe Service"
    }

    override fun onCreate() {
        super.onCreate()

        trackMeViewModel.setServiceRunning(true)

        startTracking()
    }

    private fun startTracking() {
        trackMeViewModel.startTimer()
        startDetectLocation()
    }

    private fun startDetectLocation() {
        fusedLocationHelper.startDetectLocation {
            Timber.e { "My location: $it" }
            trackMeViewModel.onTracking(it)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initNotification()

        return START_STICKY
    }

    private fun initNotification() {
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking  Me")
            .setContentText("Tracking your location...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            NotificationManagerCompat.from(this).createNotificationChannel(channel)
        }
    }

    inner class TrackMeBinder : Binder() {
        fun getService(): TrackMeService = this@TrackMeService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        trackMeViewModel.setServiceRunning(false)
        fusedLocationHelper.stop()
        trackMeViewModel.stopTracking()

        super.onDestroy()
    }

    fun resume() {
        trackMeViewModel.resumeTracking()
        startDetectLocation()
    }

    fun pause() {
        fusedLocationHelper.stop()
        trackMeViewModel.pauseTracking()
    }

}