package com.xinh.data.di

import com.xinh.data.database.TrackMeDatabase
import com.xinh.data.repository.TrackingRepositoryImpl
import com.xinh.domain.repository.TrackingRepository
import org.koin.dsl.module

val repositoryModule = module {

    single { get<TrackMeDatabase>().getTrackingInfoDao() }

    single<TrackingRepository> {
        TrackingRepositoryImpl(
            trackingInfoDao = get(),
            locationFormat = get()
        )
    }
}