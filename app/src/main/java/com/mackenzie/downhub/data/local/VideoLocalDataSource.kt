package com.mackenzie.downhub.data.local

import com.mackenzie.downhub.data.local.room.dao.VideoDao
import com.mackenzie.downhub.data.local.room.entity.VideoInfo
import com.mackenzie.downhub.data.repository.VideoRepository
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoLocalDataSource @Inject constructor(
    private val videoDao: VideoDao
) : VideoRepository {
    override fun getVideoInfoBySuperXDetector(
        url: Request,
        isM3u8: Boolean,
        isMpd: Boolean,
        isAudioCheck: Boolean
    ): VideoInfo? {
        return videoDao.getVideoById(url.url.toString()).toSingle().blockingGet()
    }

    override fun getVideoInfo(
        url: Request,
        isM3u8OrMpd: Boolean,
        isAudioCheck: Boolean
    ): VideoInfo? {
        return videoDao.getVideoById(url.url.toString()).toSingle().blockingGet()
    }

    override fun saveVideoInfo(videoInfo: VideoInfo) {
        videoDao.insertVideo(videoInfo)
    }

}