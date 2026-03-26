package com.mackenzie.downhub.di.module.activity

import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.mackenzie.downhub.di.FragmentScoped
import com.mackenzie.downhub.ui.main.player.VideoPlayerFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VideoPlayerModule {

    @OptIn(UnstableApi::class)
    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun bindVideoPlayerFragment(): VideoPlayerFragment
}