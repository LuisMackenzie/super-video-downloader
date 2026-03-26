package com.mackenzie.downhub.di.module

import com.mackenzie.downhub.util.proxy_utils.ProxyService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeProxyService(): ProxyService
}