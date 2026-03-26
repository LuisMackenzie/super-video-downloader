package com.mackenzie.di.module

import android.app.Application
import android.content.Context
import com.mackenzie.DLApplication
import com.mackenzie.di.qualifier.ApplicationContext
import com.mackenzie.util.downloaders.NotificationReceiver
import com.mackenzie.util.scheduler.BaseSchedulers
import com.mackenzie.util.scheduler.BaseSchedulersImpl
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

@Module
abstract class AppModule {

    @Binds
    @ApplicationContext
    abstract fun bindApplicationContext(application: DLApplication): Context

    @Binds
    abstract fun bindApplication(application: DLApplication): Application

    @Singleton
    @Binds
    abstract fun bindBaseSchedulers(baseSchedulers: BaseSchedulersImpl): BaseSchedulers

    @ContributesAndroidInjector
    abstract fun contributesNotificationReceiver(): NotificationReceiver
}