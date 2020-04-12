package com.xinh.data.di

import com.xinh.data.preference.SharedPreferenceStorage
import org.koin.dsl.module

val preferenceModule = module {
    single { SharedPreferenceStorage.create(context = get()) }
}