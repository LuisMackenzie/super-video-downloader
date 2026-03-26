package com.mackenzie.di.module

import com.mackenzie.util.proxy_utils.ProxyService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeProxyService(): ProxyService
}