package com.mackenzie.naughtyhub.usecases.list

import arrow.core.Either
import com.mackenzie.naughtyhub.data.repositories.VideoHubRepository
import com.mackenzie.naughtyhub.domain.Error
import com.mackenzie.naughtyhub.domain.video.VideoListItem
import javax.inject.Inject

class GetVideoDefaultListUseCase @Inject constructor(private val repo: VideoHubRepository) {

    suspend operator fun invoke():  Either<Error, VideoListItem> = repo.requestDefaultVideoList()

}