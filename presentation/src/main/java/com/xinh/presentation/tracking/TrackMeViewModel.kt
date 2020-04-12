package com.xinh.presentation.tracking

import androidx.lifecycle.MutableLiveData
import com.github.ajalt.timberkt.Timber
import com.xinh.domain.exception.Failure
import com.xinh.domain.interactor.GetHistory
import com.xinh.domain.interactor.SaveTrackingInfo
import com.xinh.domain.location.LocationCalculator
import com.xinh.domain.location.LocationFormat
import com.xinh.domain.model.ItemHistory
import com.xinh.domain.model.MyLocation
import com.xinh.domain.model.TrackingInfo
import com.xinh.domain.model.TrackingState
import com.xinh.location.distanceTo
import com.xinh.presentation.BaseViewModel
import com.xinh.presentation.SingleLiveEvent
import java.util.concurrent.atomic.AtomicBoolean

abstract class TrackMeViewModel(
    saveTrackingInfo: SaveTrackingInfo,
    getHistory: GetHistory
) : BaseViewModel(saveTrackingInfo, getHistory) {
    abstract fun onTracking(location: MyLocation)

    abstract fun pauseTracking()

    abstract fun resumeTracking()

    abstract fun stopTracking()

    abstract fun startTimer()

    abstract fun saveCurrentTrackingInfo()

    abstract fun loadCurrentState()

    abstract fun setServiceRunning(isServiceRunning: Boolean)

    abstract fun isServiceRunning(): Boolean

    abstract fun getRoute()

    abstract fun getHistory()

    abstract fun loadMoreHistory()

    var onDistance = SingleLiveEvent<String>()
    var onSpeed = SingleLiveEvent<String>()
    var onTime = SingleLiveEvent<String>()
    var onCurrentStateChange = MutableLiveData<TrackingState>()

    var onStartTracking = SingleLiveEvent<MyLocation>()
    var onRoutes = SingleLiveEvent<List<MyLocation>>()
    var onAppendRoute = SingleLiveEvent<Pair<MyLocation, MyLocation>>()

    var onShowLoadingHistory = SingleLiveEvent<Boolean>()
    var onShowLoadMoreHistory = SingleLiveEvent<Boolean>()
    var onGetHistory = SingleLiveEvent<List<ItemHistory>>()
    var onLoadMoreHistory = SingleLiveEvent<List<ItemHistory>>()
}

class TrackMeViewModelIml(
    private val locationCalculator: LocationCalculator,
    private val locationFormat: LocationFormat,
    private var trackingTimer: TrackingTimer,

    private val saveTrackingInfo: SaveTrackingInfo,
    private val getHistory: GetHistory

) : TrackMeViewModel(saveTrackingInfo, getHistory) {

    private var trackingInfo = TrackingInfo()
    private var distance = 0F
    private val tempRoutes = mutableListOf<MyLocation>()
    private val tempSpeed = mutableListOf<Float>()

    private val isServiceRunning = AtomicBoolean(false)

    private var page = 1

    override fun onTracking(location: MyLocation) {
        trackingInfo.state = TrackingState.Start

        distance = locationCalculator.getDistance(trackingInfo.prevLocation, location)
        val speed = locationCalculator.getSpeed(distance)
        tempSpeed.add(speed)

        checkShowStartTracking(location)
        tempRoutes.add(location)
        appendRoute(trackingInfo.prevLocation, location)

        trackingInfo.prevLocation = location
        trackingInfo.totalDistance += distance

        onDistance.value = locationFormat.formatDistance(trackingInfo.totalDistance)

        onSpeed.value = locationFormat.formatSpeed(speed)
    }

    private fun checkShowStartTracking(location: MyLocation) {
        if (tempRoutes.isEmpty()) {
            onStartTracking.value = location
        }
    }

    private fun appendRoute(prev: MyLocation?, location: MyLocation) {
        prev ?: return
        Timber.d { "Prev: $prev - Nex: $location - Distance: ${prev.distanceTo(location)}" }

        onAppendRoute.value = Pair(prev, location)
    }

    override fun pauseTracking() {
        trackingInfo.state = TrackingState.Pause
        trackingTimer.pause()
    }

    override fun resumeTracking() {
        trackingInfo.state = TrackingState.Start

        trackingTimer.resume {
            onTime.postValue(locationFormat.formatTime(it))
        }
    }

    override fun stopTracking() {
        trackingTimer.stop()
        trackingInfo.state = TrackingState.Done

        saveCurrentTrackingInfo()

        resetTrackingState()
    }

    private fun resetTrackingState() {
        trackingInfo = TrackingInfo()

        tempRoutes.clear()
        tempSpeed.clear()

        onDistance.value = locationFormat.formatDistance(trackingInfo.totalDistance)
        onSpeed.value = locationFormat.formatSpeed(locationCalculator.getSpeed(0F))
        onTime.value = locationFormat.formatTime(trackingInfo.totalTime)
    }

    override fun startTimer() {
        trackingTimer.start(trackingInfo.totalTime) {
            onTime.postValue(locationFormat.formatTime(it))
        }
    }

    override fun saveCurrentTrackingInfo() {
        trackingInfo.apply {
            routes = tempRoutes
            totalTime = trackingTimer.getSeconds()
            avgSpeed = getAvgSpeed()
        }

        Timber.d { "Tracking info: $trackingInfo" }

        saveTrackingInfo(trackingInfo) {
            it.fold(::handleSaveTrackingInfoFailure, ::handleSaveTrackingInfoSuccess)
        }
    }

    private fun getAvgSpeed(): Float {
        return tempSpeed.sum() / tempSpeed.size.toFloat()
    }

    private fun handleSaveTrackingInfoSuccess(id: Long) {
        Timber.d { "Save tracking info success id: $id" }
    }

    private fun handleSaveTrackingInfoFailure(failure: Failure) {
        handleFailure(failure)
    }

    override fun loadCurrentState() {
        trackingInfo.totalTime = trackingTimer.getSeconds()

        onDistance.value = locationFormat.formatDistance(trackingInfo.totalDistance)

        onSpeed.value = locationFormat.formatSpeed(locationCalculator.getSpeed(distance))

        onTime.postValue(locationFormat.formatTime(trackingInfo.totalTime))

        onCurrentStateChange.value = trackingInfo.state
    }

    override fun setServiceRunning(isServiceRunning: Boolean) {
        this.isServiceRunning.set(isServiceRunning)

        onCurrentStateChange.value =
            if (isServiceRunning) TrackingState.Start else TrackingState.Done
    }

    override fun isServiceRunning(): Boolean {
        return isServiceRunning.get()
    }

    override fun getRoute() {
        if (isShowRoute() && tempRoutes.isNotEmpty()) {
            onStartTracking.value = tempRoutes[0]

            onRoutes.value = tempRoutes
        }
    }

    private fun isShowRoute(): Boolean {
        return trackingInfo.state != TrackingState.Done
    }

    override fun getHistory() {
        page = 1

        onShowLoadingHistory.value = true

        getHistory(GetHistory.Params(page)) {

            onShowLoadingHistory.value = false

            it.fold(::handleGetHistoryFailure, ::handGetHistorySuccess)
        }
    }

    private fun handGetHistorySuccess(items: List<ItemHistory>) {
        onGetHistory.value = items
    }

    private fun handleGetHistoryFailure(failure: Failure) {
        handleFailure(failure)
    }

    private fun handleFailure(failure: Failure) {
        when (failure) {
            is Failure.Unknown -> Timber.e { "${failure.e.message}" }
        }
    }

    override fun loadMoreHistory() {
        page++

        onShowLoadMoreHistory.value = true

        getHistory(GetHistory.Params(page)) {

            onShowLoadMoreHistory.value = false

            it.fold(::handleLoadMoreHistoryFailure, ::handLoadMoreHistorySuccess)
        }
    }

    private fun handLoadMoreHistorySuccess(list: List<ItemHistory>) {
        onLoadMoreHistory.value = list
    }

    private fun handleLoadMoreHistoryFailure(failure: Failure) {
        handleFailure(failure)
    }
}