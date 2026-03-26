package com.mackenzie.downhub.di.module

import android.app.Application
import android.content.Context
import com.mackenzie.downhub.DLApplication
import com.mackenzie.downhub.di.qualifier.ApplicationContext
import com.mackenzie.downhub.util.downloaders.NotificationReceiver
import com.mackenzie.downhub.util.scheduler.BaseSchedulers
import com.mackenzie.downhub.util.scheduler.BaseSchedulersImpl
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