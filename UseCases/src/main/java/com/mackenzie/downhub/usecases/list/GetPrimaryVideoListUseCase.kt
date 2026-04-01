package com.mackenzie.naughtyhub.usecases.list

import arrow.core.Either
import com.mackenzie.naughtyhub.data.repositories.PrimaryRepository
import com.mackenzie.naughtyhub.domain.Error
import com.mackenzie.naughtyhub.domain.video.VideoListItem
import javax.inject.Inject

class GetPrimaryVideoListUseCase @Inject constructor(private val repo: PrimaryRepository) {

    suspend operator fun invoke(serverId: Int, serverUrl: String):  Either<Error, VideoListItem> = repo.primaryVideoScrapper(serverId, serverUrl)

}