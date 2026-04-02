package com.mackenzie.downhub.data.remote.datasource

import arrow.core.Either
import com.mackenzie.downhub.data.datasources.VideoHubRemoteDataSource
import com.mackenzie.downhub.data.remote.mapper.toDomainModel
import com.mackenzie.downhub.data.remote.models.RemoteVideoHubConnect
import com.mackenzie.downhub.data.remote.tryCall
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.domain.video.CategoriesItem
import com.mackenzie.downhub.domain.video.StarsDetailedItems
import com.mackenzie.downhub.domain.video.StarsItems
import com.mackenzie.downhub.domain.video.TagsResponseItem
import com.mackenzie.downhub.domain.video.VideoActiveItems
import com.mackenzie.downhub.domain.video.VideoByIdItem
import com.mackenzie.downhub.domain.video.VideoEmbedCodeItem
import com.mackenzie.downhub.domain.video.VideoListItem
import com.mackenzie.downhub.domain.video.VideosDeletedItem
import javax.inject.Inject

class VideoHubDataSource @Inject constructor(
    private val remoteService: RemoteVideoHubConnect
) : VideoHubRemoteDataSource {

    override suspend fun getVideoServer() = tryCall {
        // TODO subir la info de los servidores y recuperarla aqui
        remoteService.videoHubService
            .searchVideos()
            .toDomainModel()
    }

    override suspend fun searchVideos(
        page: Int?,
        thumbsize: String?,
        search: String?,
        tags: List<String>?,
        stars: List<String>?,
        category: String?,
        ordering: String?,
        period: String?
    ): Either<Error, VideoListItem> = tryCall {
        remoteService.videoHubService
            .searchVideos(
                page = page,
                thumbsize = thumbsize,
                search = search,
                tags = tags,
                stars = stars,
                category = category,
                ordering = ordering,
                period = period
            ).toDomainModel()
    }

    override suspend fun searchDefaultVideos(): Either<Error, VideoListItem> = tryCall {
        remoteService.videoHubService
            .searchVideos()
            .toDomainModel()
    }

    override suspend fun getCategories(): Either<Error, CategoriesItem> = tryCall {
        remoteService.videoHubService
            .getCategories()
            .toDomainModel()
    }

    override suspend fun getTags(): Either<Error, TagsResponseItem> = tryCall {
        remoteService.videoHubService
            .getTags()
            .toDomainModel()
    }

    override suspend fun getStars(): Either<Error, StarsItems> = tryCall {
        remoteService.videoHubService
            .getStars()
            .toDomainModel()
    }

    override suspend fun getStarDetailedList(): Either<Error, StarsDetailedItems> = tryCall {
        remoteService.videoHubService
            .getStarDetailedList()
            .toDomainModel()
    }

    override suspend fun isVideoActive(videoId: Int): Either<Error, VideoActiveItems> = tryCall {
        remoteService.videoHubService
            .isVideoActive(videoid = videoId)
            .toDomainModel()
    }

    override suspend fun getVideoById(videoId: Int, thumbsize: String?): Either<Error, VideoByIdItem> = tryCall {
        remoteService.videoHubService
            .getVideoById(videoid = videoId, thumbsize = thumbsize)
            .toDomainModel()
    }

    override suspend fun getVideoEmbedCode(videoId: Int): Either<Error, VideoEmbedCodeItem> = tryCall {
        remoteService.videoHubService
            .getVideoEmbedCode(videoid = videoId)
            .toDomainModel()
    }

    override suspend fun areVideosDeleted(videoIds: List<Int>): Either<Error, VideosDeletedItem> = tryCall {
        remoteService.videoHubService
            .areVideosDeleted(videoids = videoIds.joinToString(","))
            .toDomainModel()
    }
}

