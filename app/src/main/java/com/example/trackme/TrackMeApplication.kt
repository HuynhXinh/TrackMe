package com.example.trackme

import android.app.Application
import com.example.di.appModule
import com.github.ajalt.timberkt.Timber
import com.xinh.data.di.databaseModule
import com.xinh.data.di.repositoryModule
import com.xinh.domain.di.useCaseModule
import com.xinh.location.di.locationModule
import com.xinh.presentation.di.presentationModule
import com.xinh.share.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class TrackMeApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(timber.log.Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(this@TrackMeApplication)
            modules(allModules())
        }
    }

    private fun allModules(): List<Module> {
        return listOf(
            appModule,
            databaseModule,
            repositoryModule,
            useCaseModule,
            presentationModule,
            locationModule
        )
    }
}