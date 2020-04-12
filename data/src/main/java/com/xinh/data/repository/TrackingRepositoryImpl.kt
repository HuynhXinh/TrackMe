package com.xinh.data.repository

import com.github.ajalt.timberkt.Timber
import com.xinh.data.repository.dao.TrackingInfoDao
import com.xinh.data.repository.dao.TrackingInfoEntity
import com.xinh.domain.interactor.GetHistory
import com.xinh.domain.location.LocationFormat
import com.xinh.domain.model.ItemHistory
import com.xinh.domain.model.TrackingInfo
import com.xinh.domain.repository.TrackingRepository
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class TrackingRepositoryImpl(
    private val locationFormat: LocationFormat,
    private val trackingInfoDao: TrackingInfoDao
) : TrackingRepository {

    override fun save(trackingInfo: TrackingInfo): Observable<Long> {
        return trackingInfoDao.insert(trackingInfo.toEntity()).toObservable()
    }

    override fun getLastTrackingInfo(): Observable<TrackingInfo> {
        return trackingInfoDao.getLast().map { it.toTrackingInfo() }.toObservable()
    }

    override fun getHistory(limit: Int, offset: Int): Observable<List<ItemHistory>> {
        Timber.d { "getHistory: limit=$limit - offset=$offset" }
        return trackingInfoDao.getHistories(limit, offset).map {
            it.toHistories(locationFormat)
        }.delay(2, TimeUnit.SECONDS)
    }
}

fun List<TrackingInfoEntity>.toHistories(locationFormat: LocationFormat): List<ItemHistory> {
    return this.map { it.toHistory(locationFormat) }
}

fun TrackingInfoEntity.toHistory(locationFormat: LocationFormat): ItemHistory {
    return ItemHistory(
        id = this.id,
        staticMap = StaticMapBuilder(this.routes ?: emptyList()).build(),
        distance = locationFormat.formatDistance(this.total_distance),
        avgSpeed = locationFormat.formatSpeed(this.avg_speed),
        time = locationFormat.formatTime(this.total_time)
    )
}