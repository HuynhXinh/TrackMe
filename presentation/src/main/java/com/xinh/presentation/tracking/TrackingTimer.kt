package com.xinh.presentation.tracking

import com.github.ajalt.timberkt.Timber
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.schedule

interface TrackingTimer {
    fun getSeconds(): Long
    fun start(init: Long, block: ((Long) -> Unit)? = null)
    fun pause()
    fun resume(block: ((Long) -> Unit)? = null)
    fun stop()
}

class TrackingTimerImpl : TrackingTimer {
    private var seconds = 0L
    private var timer: Timer? = null
    private val isStarting = AtomicBoolean(false)

    private val lock = Any()

    override fun getSeconds(): Long {
        return seconds
    }

    override fun start(init: Long, block: ((Long) -> Unit)?) {
        synchronized(lock) {
            if (isStarting.get()) return
            isStarting.set(true)

            Timber.d { "start..." }

            seconds = init

            timer = Timer()
            timer?.schedule(0, 1000) {
                seconds++
                block?.invoke(seconds)
            }
        }
    }

    override fun pause() {
        Timber.d { "pause..." }
        stop()
    }

    override fun resume(block: ((Long) -> Unit)?) {
        Timber.d { "resume..." }
        start(seconds) {
            block?.invoke(it)
        }
    }

    override fun stop() {
        Timber.d { "stop..." }
        timer?.cancel()
        timer?.purge()
        timer = null

        isStarting.set(false)
    }
}