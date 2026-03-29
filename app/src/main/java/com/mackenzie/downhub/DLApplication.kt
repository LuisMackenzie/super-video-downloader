package com.mackenzie.downhub

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Configuration
import androidx.work.WorkManager
import com.mackenzie.downhub.util.AppLogger
import com.mackenzie.downhub.util.ContextUtils
import com.mackenzie.downhub.util.FileUtil
import com.mackenzie.downhub.util.SharedPrefHelper
import com.mackenzie.downhub.util.downloaders.generic_downloader.DaggerWorkerFactory
import com.mackenzie.downhub.util.proxy_utils.ProxyService
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltAndroidApp
open class DLApplication : Application() {
    companion object {
        const val DEBUG_TAG: String = "YOUTUBE_DL_DEBUG_TAG"
        var isProxyServiceStarted = false
    }

    @Inject
    lateinit var workerFactory: DaggerWorkerFactory

    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper

    @Inject
    lateinit var fileUtil: FileUtil

    override fun onCreate() {
        super.onCreate()

        ContextUtils.initApplicationContext(applicationContext)

        initializeFileUtils()

        val file: File = fileUtil.folderDir
        val ctx = applicationContext

        WorkManager.initialize(
            ctx, Configuration.Builder().setWorkerFactory(workerFactory).build()
        )

        RxJavaPlugins.setErrorHandler { error: Throwable? ->
            AppLogger.Companion.e("RxJavaError unhandled $error")
        }

        CoroutineScope(Dispatchers.Default).launch {
            if (!file.exists()) {
                file.mkdirs()
            }

            initializeYoutubeDl()
            updateYoutubeDL()
        }
    }

    private fun initializeFileUtils() {
        val isExternal = sharedPrefHelper.getIsExternalUse()
        val isAppDir = sharedPrefHelper.getIsAppDirUse()

        FileUtil.Companion.IS_EXTERNAL_STORAGE_USE = isExternal
        FileUtil.Companion.IS_APP_DATA_DIR_USE = isAppDir
        FileUtil.Companion.INITIIALIZED = true
    }

    private fun initializeYoutubeDl() {
        try {
            YoutubeDL.getInstance().init(applicationContext)
            FFmpeg.getInstance().init(applicationContext)
        } catch (e: YoutubeDLException) {
            AppLogger.Companion.e("failed to initialize youtubedl-android $e")
        }
    }

    private fun updateYoutubeDL() {
        try {
            val status = YoutubeDL.getInstance()
                .updateYoutubeDL(applicationContext, YoutubeDL.UpdateChannel._STABLE)
            AppLogger.Companion.d("UPDATE_STATUS MASTER: $status")
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    fun startProxyService() {
        if (isProxyServiceStarted) {
            return
        }

        val serviceIntent = Intent(this, ProxyService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
            isProxyServiceStarted = true
        } catch (e: Exception) {
            AppLogger.Companion.e("Failed to start ProxyService: ${e.message}")
        }
    }
}
