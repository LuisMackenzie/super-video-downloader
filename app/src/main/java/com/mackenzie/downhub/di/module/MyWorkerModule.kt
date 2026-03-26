package com.mackenzie.downhub.di.module

import androidx.work.WorkerFactory
import com.mackenzie.downhub.data.repository.ProgressRepository
import com.mackenzie.downhub.util.FileUtil
import com.mackenzie.downhub.util.NotificationsHelper
import com.mackenzie.downhub.util.SharedPrefHelper
import com.mackenzie.downhub.util.downloaders.generic_downloader.DaggerWorkerFactory
import com.mackenzie.downhub.util.proxy_utils.CustomProxyController
import com.mackenzie.downhub.util.proxy_utils.OkHttpProxyClient
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

