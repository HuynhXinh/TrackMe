package com.example.trackme.ui

import android.view.View

class TrackingStateHandler(
    private var state: State = State.Record,
    private val record: View,
    private val pause: View,
    private val resume: View,
    private val stop: View
) {
    var onClickRecord: (() -> Unit)? = null
    var onClickPause: (() -> Unit)? = null
    var onClickResume: (() -> Unit)? = null
    var onClickStop: (() -> Unit)? = null

    init {
        updateUi()

        record.setOnClickListener {
            state = State.Pause
            updateUi()

            onClickRecord?.invoke()
        }

        pause.setOnClickListener {
            state = State.Resume

            updateUi()

            onClickPause?.invoke()
        }

        resume.setOnClickListener {
            state = State.Pause

            updateUi()

            onClickResume?.invoke()
        }

        stop.setOnClickListener {
            state = State.Record

            updateUi()

            onClickStop?.invoke()
        }
    }

    private fun updateUi() {
        state.updateUi(record = record, pause = pause, resume = resume, stop = stop)
    }

    fun toRecord() {
        state = State.Record
        updateUi()
    }
}

sealed class State {
    abstract fun updateUi(record: View, pause: View, resume: View, stop: View)

    object Record : State() {
        override fun updateUi(record: View, pause: View, resume: View, stop: View) {
            record.show()
            pause.invisible()
            resume.invisible()
            stop.invisible()
        }
    }

    object Pause : State() {
        override fun updateUi(record: View, pause: View, resume: View, stop: View) {
            record.invisible()
            pause.show()
            resume.invisible()
            stop.invisible()
        }
    }

    object Resume : State() {
        override fun updateUi(record: View, pause: View, resume: View, stop: View) {
            record.invisible()
            pause.invisible()
            resume.show()
            stop.show()
        }
    }
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}