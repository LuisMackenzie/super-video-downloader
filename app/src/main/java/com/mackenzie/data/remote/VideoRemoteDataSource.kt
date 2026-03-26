package com.mackenzie.data.remote

import com.mackenzie.data.local.room.entity.VideoInfo
import com.mackenzie.data.remote.service.VideoService
import com.mackenzie.data.remote.service.VideoServiceSuperX
import com.mackenzie.data.repository.VideoRepository
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRemoteDataSource @Inject constructor(
    private val videoService: VideoService,
    private val videoServiceSuperX: VideoServiceSuperX
) : VideoRepository {

    override fun getVideoInfoBySuperXDetector(
        url: Request,
        isM3u8: Boolean,
        isMpd: Boolean,
        isAudioCheck: Boolean
    ): VideoInfo? {
        return videoServiceSuperX.getVideoInfo(url, isM3u8, isMpd, isAudioCheck)?.videoInfo
    }

    override fun getVideoInfo(
        url: Request,
        isM3u8OrMpd: Boolean,
        isAudioCheck: Boolean
    ): VideoInfo? {
        return videoService.getVideoInfo(url, isM3u8OrMpd, isM3u8OrMpd, isAudioCheck)?.videoInfo
    }

    override fun saveVideoInfo(videoInfo: VideoInfo) {
    }
}