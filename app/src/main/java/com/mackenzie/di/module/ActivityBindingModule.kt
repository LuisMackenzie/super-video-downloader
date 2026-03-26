package com.mackenzie.di.module

import com.mackenzie.di.ActivityScoped
import com.mackenzie.ui.main.home.MainActivity
import com.mackenzie.di.module.activity.MainModule
import com.mackenzie.ui.main.player.VideoPlayerActivity
import com.mackenzie.di.module.activity.VideoPlayerModule
import com.mackenzie.ui.main.splash.SplashActivity
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