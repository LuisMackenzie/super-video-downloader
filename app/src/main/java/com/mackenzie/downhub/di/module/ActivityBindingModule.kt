package com.mackenzie.downhub.di.module

import com.mackenzie.downhub.di.ActivityScoped
import com.mackenzie.downhub.ui.main.home.MainActivity
import com.mackenzie.downhub.di.module.activity.MainModule
import com.mackenzie.downhub.ui.main.player.VideoPlayerActivity
import com.mackenzie.downhub.di.module.activity.VideoPlayerModule
import com.mackenzie.downhub.ui.main.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun bindSplashActivity(): SplashActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun bindMainActivity(): MainActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [VideoPlayerModule::class])
    internal abstract fun bindVideoPlayerActivity(): VideoPlayerActivity
}