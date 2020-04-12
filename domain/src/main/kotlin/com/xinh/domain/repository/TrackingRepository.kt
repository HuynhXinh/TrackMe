package com.xinh.domain.repository

import com.xinh.domain.model.ItemHistory
import com.xinh.domain.model.TrackingInfo
import io.reactivex.Observable

interface TrackingRepository {
    fun save(trackingInfo: TrackingInfo): Observable<Long>

    fun getLastTrackingInfo(): Observable<TrackingInfo>

    fun getHistory(limit: Int, offset: Int): Observable<List<ItemHistory>>
}