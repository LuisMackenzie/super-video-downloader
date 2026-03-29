package com.mackenzie.downhub.di.module

import com.mackenzie.downhub.util.scheduler.BaseSchedulers
import com.mackenzie.downhub.util.scheduler.BaseSchedulersImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun bindBaseSchedulers(baseSchedulers: BaseSchedulersImpl): BaseSchedulers
}
