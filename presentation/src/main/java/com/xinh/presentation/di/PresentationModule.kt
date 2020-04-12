package com.xinh.presentation.di

import com.xinh.presentation.tracking.TrackMeViewModel
import com.xinh.presentation.tracking.TrackMeViewModelIml
import com.xinh.presentation.tracking.TrackingTimer
import com.xinh.presentation.tracking.TrackingTimerImpl
import org.koin.dsl.module

val presentationModule = module {

    factory<TrackingTimer> { TrackingTimerImpl() }

    single<TrackMeViewModel> {
        TrackMeViewModelIml(
            locationCalculator = get(),
            locationFormat = get(),
            trackingTimer = get(),
            saveTrackingInfo = get(),
            getHistory = get()
        )
    }
}