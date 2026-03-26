package com.mackenzie.downhub.di.module

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import com.mackenzie.downhub.DLApplication
import com.mackenzie.downhub.util.AppUtil
import com.mackenzie.downhub.util.FileUtil
import com.mackenzie.downhub.util.IntentUtil
import com.mackenzie.downhub.util.NotificationsHelper
import com.mackenzie.downhub.util.SharedPrefHelper
import com.mackenzie.downhub.util.SystemUtil
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilModule {

    @Singleton
    @Provides
    fun bindDownloadManager(application: Application): DownloadManager =
        application.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    @Singleton
    @Provides
    fun bindFileUtil() = FileUtil()

    @Singleton
    @Provides
    fun bindSystemUtil() = SystemUtil()

    @Singleton
    @Provides
    fun bindIntentUtil(fileUtil: FileUtil) = IntentUtil(fileUtil)

    @Singleton
    @Provides
    fun bindAppUtil() = AppUtil()

    @Singleton
    @Provides
    fun provideNotificationsHelper(dlApplication: DLApplication): NotificationsHelper {
        return NotificationsHelper(dlApplication.applicationContext)
    }

    @Singleton
    @Provides
    fun provideSharedPrefHelper(dlApplication: DLApplication, appUtil: AppUtil): SharedPrefHelper {
        return SharedPrefHelper(dlApplication.applicationContext, appUtil)
    }
}