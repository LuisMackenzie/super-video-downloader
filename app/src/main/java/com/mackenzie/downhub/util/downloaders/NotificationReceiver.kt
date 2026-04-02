package com.mackenzie.downhub.util.downloaders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mackenzie.downhub.data.local.room.entity.ProgressInfo
import com.mackenzie.downhub.data.local.room.entity.VideoInfo
import com.mackenzie.downhub.data.repository.ProgressRepository
import com.mackenzie.downhub.util.AppLogger
import com.mackenzie.downhub.util.downloaders.custom_downloader.CustomRegularDownloader
import com.mackenzie.downhub.util.downloaders.super_x_downloader.SuperXDownloader
import com.mackenzie.downhub.util.downloaders.youtubedl_downloader.YoutubeDlDownloader
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {
    @Inject
    lateinit var progressRepository: ProgressRepository

    private val receiverScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.extras?.getString(TASK_ID)
        receiverScope.launch {
            val progressInfo = progressRepository.getProgressInfos().blockingFirst()
                .firstOrNull { it.id == taskId }

            AppLogger.Companion.d("-----------------------------------   $taskId  $progressInfo")

            if (progressInfo == null) {
                return@launch
            }

            when (intent.action) {
                ACTION_PAUSE -> {
                    handlePause(context, progressInfo)
                }

                ACTION_RESUME -> {
                    handleResume(context, progressInfo)
                }

                ACTION_CANCEL -> {
                    handleCancel(context, progressInfo)
                }

                else -> {
                    AppLogger.Companion.d("ACTION NOT SUPPORTED ${intent.action}")
                }
            }
        }
    }

    private fun handleCancel(context: Context, task: ProgressInfo) {
        AppLogger.Companion.d("HANDLE CANCEL $task")
        when (val downloaderType = getTaskType(task.videoInfo)) {
            DOWNLOADER_YOUTUBE_DL -> {
                YoutubeDlDownloader.cancelDownload(context, task, true)
                progressRepository.deleteProgressInfo(task)
            }

            DOWNLOADER_REGULAR -> {
                CustomRegularDownloader.cancelDownload(context, task, true)
                progressRepository.deleteProgressInfo(task)
            }

            DOWNLOADER_SUPER_XD -> {
                SuperXDownloader.cancelDownload(context, task, true)
            }

            else -> {
                AppLogger.Companion.d("Unexpected downloader type: $downloaderType")
            }
        }
    }

    private fun handleResume(context: Context, task: ProgressInfo) {
        AppLogger.Companion.d("HANDLE RESUME $task")
        when (val downloaderType = getTaskType(task.videoInfo)) {
            DOWNLOADER_YOUTUBE_DL -> {
                YoutubeDlDownloader.resumeDownload(context, task)
            }

            DOWNLOADER_REGULAR -> {
                CustomRegularDownloader.resumeDownload(context, task)
            }


            DOWNLOADER_SUPER_XD -> {
                SuperXDownloader.resumeDownload(context, task)
            }

            else -> {
                AppLogger.Companion.d("Unexpected downloader type: $downloaderType")
            }
        }
    }

    private fun handlePause(context: Context, task: ProgressInfo) {
        AppLogger.Companion.d("HANDLE PAUSE $task")
        when (val downloaderType = getTaskType(task.videoInfo)) {
            DOWNLOADER_YOUTUBE_DL -> {
                YoutubeDlDownloader.pauseDownload(context, task)
            }

            DOWNLOADER_REGULAR -> {
                CustomRegularDownloader.pauseDownload(context, task)
            }

            DOWNLOADER_SUPER_XD -> {
                SuperXDownloader.pauseDownload(context, task)
            }

            else -> {
                AppLogger.Companion.d("Unexpected downloader type: $downloaderType")
            }
        }
    }

    private fun getTaskType(videoInfo: VideoInfo): String {
        return if (videoInfo.isRegularDownload) {
            DOWNLOADER_REGULAR
        } else if (videoInfo.isDetectedBySuperX) {
            DOWNLOADER_SUPER_XD
        } else {
            DOWNLOADER_YOUTUBE_DL
        }
    }

    companion object {
        const val DOWNLOADER_SUPER_XD = "DOWNLOADER_SUPER_XD"
        const val DOWNLOADER_YOUTUBE_DL = "DOWNLOADER_YOUTUBE_DL"
        const val DOWNLOADER_REGULAR = "DOWNLOADER_REGULAR"
        const val TASK_ID = "TASK_ID"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val ACTION_RESUME = "ACTION_RESUME"
        const val ACTION_CANCEL = "ACTION_CANCEL"
    }
}
