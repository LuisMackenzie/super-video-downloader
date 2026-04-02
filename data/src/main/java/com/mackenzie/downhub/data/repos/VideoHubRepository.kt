package com.mackenzie.downhub.data.repos

import arrow.core.Either
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.data.datasources.VideoHubRemoteDataSource
import com.mackenzie.downhub.domain.video.VideoListItem
import javax.inject.Inject

class VideoHubRepository @Inject constructor(
    // val localDataSource: VideoHubLocalDataSource,
    val remoteDataSource: VideoHubRemoteDataSource
) {

    suspend fun requestVideoList(
        page: Int?,
        thumbsize: String?,
        search: String?,
        tags: List<String>?,
        stars: List<String>?,
        category: String?,
        ordering: String?,
        period: String?
    ):  Either<Error, VideoListItem> {
        return remoteDataSource.searchVideos(page, thumbsize, search, tags, stars, category, ordering, period)
    }

    suspend fun requestDefaultVideoList():  Either<Error, VideoListItem> {

        return remoteDataSource.searchVideos()
    }
}