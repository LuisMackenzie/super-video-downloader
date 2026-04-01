package com.mackenzie.downhub.usecases.list

import arrow.core.Either
import com.mackenzie.downhub.data.repos.VideoHubRepository
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.domain.video.VideoListItem
import javax.inject.Inject

class GetVideoListUseCase @Inject constructor(private val repo: VideoHubRepository) {

    suspend operator fun invoke(
        page: Int?,
        thumbsize: String?,
        search: String?,
        tags: List<String>?,
        stars: List<String>?,
        category: String?,
        ordering: String?,
        period: String?
    ):  Either<Error, VideoListItem> = repo.requestVideoList(page, thumbsize, search, tags, stars, category, ordering, period)

}