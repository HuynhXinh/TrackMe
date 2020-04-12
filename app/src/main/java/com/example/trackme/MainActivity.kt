package com.example.trackme

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.trackme.service.TrackMeService
import com.example.trackme.ui.history.HistoryTrackingFragment
import com.example.trackme.ui.OnPermissionApprovedListener
import com.example.trackme.ui.State
import com.example.trackme.ui.tracking.TrackingFragment
import com.example.trackme.ui.TrackingStateHandler
import com.github.ajalt.timberkt.Timber
import com.xinh.domain.model.TrackingState
import com.xinh.permission.PermissionEnum
import com.xinh.permission.PermissionManager
import com.xinh.permission.SimpleCallback
import com.xinh.presentation.tracking.TrackMeViewModel
import com.xinh.share.extension.observeExt
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val trackMeViewModel: TrackMeViewModel by viewModel()
    private lateinit var trackMeService: TrackMeService
    private var isBound: Boolean = false

    private var trackingStateHandler: TrackingStateHandler? = null

    private val history = HistoryTrackingFragment.newInstance()
    private val tracking = TrackingFragment.newInstance()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TrackMeService.TrackMeBinder
            trackMeService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initFragments()

        tryToBindService()

        trackMeViewModel.loadCurrentState()

        trackMeViewModel.apply {
            onDistance.observeExt(this@MainActivity) {
                Timber.d { "Distance: $it" }
                tvValueDistance.text = it
            }

            onSpeed.observeExt(this@MainActivity) {
                Timber.d { "Speed: $it" }
                tvValueSeep.text = it
            }

            onTime.observeExt(this@MainActivity) {
                Timber.d { "Time: $it" }
                tvTime.text = it
            }

            onCurrentStateChange.observeExt(this@MainActivity) {
                val state = getState(it)
                showFragment(state)
                initTrackingState(state)
            }
        }
    }

    private fun showFragment(state: State) {
        if (state == State.Record) {
            showHistory()
        } else {
            showTracking()
        }
    }

    private fun initFragments() {
        add(history, HistoryTrackingFragment.TAG)
        add(tracking, TrackingFragment.TAG)
    }

    private fun showTracking() {
        hide(history)
        show(tracking)
    }

    private fun showHistory() {
        hide(tracking)
        show(history)
    }

    private fun tryToBindService() {
        if (trackMeViewModel.isServiceRunning()) {
            Intent(this, TrackMeService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun initTrackingState(state: State) {
        trackingStateHandler = TrackingStateHandler(
            state = state,
            record = ivRecord,
            pause = ivPause,
            resume = ivResume,
            stop = ivStop
        ).apply {
            onClickRecord = {
                startTracking()
            }

            onClickPause = {
                pauseTracking()
            }

            onClickResume = {
                resumeTracking()
            }

            onClickStop = {
                stopTracking()
            }
        }
    }

    private fun getState(state: TrackingState): State {
        return when (state) {
            TrackingState.Start -> State.Pause
            TrackingState.Pause -> State.Resume
            TrackingState.Done -> State.Record
        }
    }

    private fun stopTracking() {
        stopAndUnBindService()
    }

    private fun resumeTracking() {
        trackMeService.resume()
    }

    private fun pauseTracking() {
        trackMeService.pause()
    }

    private fun startTracking() {
        checkPermission {
            startAndBindService()
        }
    }

    private fun startAndBindService() {
        Intent(this, TrackMeService::class.java).also { intent ->
            ContextCompat.startForegroundService(this, intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun stopAndUnBindService() {
        unbindService()

        Intent(this, TrackMeService::class.java).also { intent ->
            this.stopService(intent)
        }
    }

    private fun unbindService() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
    }

    private fun checkPermission(onApproved: () -> Unit) {
        PermissionManager.builder()
            .permission(PermissionEnum.ACCESS_COARSE_LOCATION, PermissionEnum.ACCESS_FINE_LOCATION)
            .callback(object : SimpleCallback {
                override fun result(allPermissionsGranted: Boolean) {
                    if (allPermissionsGranted) {
                        onApproved()
                        notifyPermissionApproved()
                    } else {

                        trackingStateHandler?.toRecord()

                        Toast.makeText(
                            this@MainActivity,
                            "Permission deny.....",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
            .ask(this)
    }

    private fun notifyPermissionApproved() {
        supportFragmentManager.fragments
            .filterIsInstance<OnPermissionApprovedListener>()
            .forEach { it.onPermissionApproved() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handleResult(this, requestCode, permissions, grantResults)
    }

    override fun onDestroy() {

        unbindService()

        super.onDestroy()
    }
}

fun AppCompatActivity.add(fragment: Fragment, tag: String) {
    this.supportFragmentManager.beginTransaction().add(R.id.container, fragment, tag).commit()
}

fun AppCompatActivity.show(fragment: Fragment) {
    this.supportFragmentManager.beginTransaction().show(fragment).commit()
}

fun AppCompatActivity.hide(fragment: Fragment) {
    this.supportFragmentManager.beginTransaction().hide(fragment).commit()
}
