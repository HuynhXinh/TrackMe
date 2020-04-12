package com.xinh.data.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xinh.domain.model.ItemHistory
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface TrackingInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: TrackingInfoEntity): Maybe<Long>

    @Query("SELECT * FROM tracking_info WHERE id = :id LIMIT 1")
    fun getById(id: Long): TrackingInfoEntity

    @Query("SELECT * FROM tracking_info ORDER BY update_date DESC LIMIT 1")
    fun getLast(): Maybe<TrackingInfoEntity>

    @Query("SELECT * FROM tracking_info ORDER BY update_date DESC LIMIT :limit OFFSET :offset")
    fun getHistories(limit: Int, offset: Int): Observable<List<TrackingInfoEntity>>
}