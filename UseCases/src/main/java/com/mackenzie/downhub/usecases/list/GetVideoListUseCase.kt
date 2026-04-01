package com.mackenzie.naughtyhub.usecases.list

import arrow.core.Either
import com.mackenzie.naughtyhub.data.repositories.VideoHubRepository
import com.mackenzie.naughtyhub.domain.Error
import com.mackenzie.naughtyhub.domain.video.VideoListItem
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