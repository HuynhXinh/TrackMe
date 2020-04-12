package com.xinh.data.di

import com.xinh.data.database.TrackMeDatabase
import org.koin.dsl.module

val databaseModule = module {
    single { TrackMeDatabase.createDatabase(application = get()) }
}