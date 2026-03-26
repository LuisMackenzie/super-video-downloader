package com.mackenzie.di.module

import androidx.work.WorkerFactory
import com.mackenzie.data.repository.ProgressRepository
import com.mackenzie.util.FileUtil
import com.mackenzie.util.NotificationsHelper
import com.mackenzie.util.SharedPrefHelper
import com.mackenzie.util.downloaders.generic_downloader.DaggerWorkerFactory
import com.mackenzie.util.proxy_utils.CustomProxyController
import com.mackenzie.util.proxy_utils.OkHttpProxyClient
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class MyWorkerModule {
    @Provides
    @Singleton
    fun workerFactory(
        progressRepository: ProgressRepository,
        fileUtil: FileUtil,
        notificationsHelper: NotificationsHelper,
        proxyController: CustomProxyController,
        okHttpProxyClient: OkHttpProxyClient,
        sharedPrefHelper: SharedPrefHelper
    ): WorkerFactory {
        return DaggerWorkerFactory(
            progressRepository,
            fileUtil,
            notificationsHelper,
            proxyController,
            okHttpProxyClient,
            sharedPrefHelper
        )
    }
}

