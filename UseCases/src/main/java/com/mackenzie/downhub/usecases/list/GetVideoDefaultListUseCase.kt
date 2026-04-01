package com.mackenzie.downhub.usecases.list

import arrow.core.Either
import com.mackenzie.downhub.data.repos.VideoHubRepository
import com.mackenzie.downhub.domain.video.VideoListItem
import com.mackenzie.downhub.domain.Error
import javax.inject.Inject

class GetVideoDefaultListUseCase @Inject constructor(private val repo: VideoHubRepository) {

    suspend operator fun invoke():  Either<Error, VideoListItem> = repo.requestDefaultVideoList()

}