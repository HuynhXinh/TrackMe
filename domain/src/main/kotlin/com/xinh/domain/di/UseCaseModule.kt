package com.xinh.domain.di

import com.xinh.domain.interactor.GetHistory
import com.xinh.domain.interactor.SaveTrackingInfo
import org.koin.dsl.module

val useCaseModule = module {
    factory { SaveTrackingInfo(trackingRepository = get(), schedulerProvider = get()) }

    factory {
        GetHistory(trackingRepository = get(), schedulerProvider = get())
    }
}