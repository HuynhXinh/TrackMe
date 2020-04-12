
package com.example.di

import com.example.rx.JobExecutor
import com.example.rx.SchedulerProviderImpl
import com.xinh.domain.executor.SchedulerProvider
import com.xinh.presentation.rx.ThreadExecutor
import org.koin.dsl.module

val appModule = module {
    single<ThreadExecutor> { JobExecutor() }

    single<SchedulerProvider> { SchedulerProviderImpl(threadExecutor = get()) }
}