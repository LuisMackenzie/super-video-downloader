package com.mackenzie.downhub.di.module

import android.app.Application
import android.app.DownloadManager
import android.content.Context
import com.mackenzie.downhub.util.AppUtil
import com.mackenzie.downhub.util.FileUtil
import com.mackenzie.downhub.util.IntentUtil
import com.mackenzie.downhub.util.NotificationsHelper
import com.mackenzie.downhub.util.SharedPrefHelper
import com.mackenzie.downhub.util.SystemUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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
    fun provideNotificationsHelper(@ApplicationContext context: Context): NotificationsHelper {
        return NotificationsHelper(context)
    }

    @Singleton
    @Provides
    fun provideSharedPrefHelper(@ApplicationContext context: Context, appUtil: AppUtil): SharedPrefHelper {
        return SharedPrefHelper(context, appUtil)
    }
}
