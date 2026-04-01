package com.mackenzie.naughtyhub.usecases.list

import arrow.core.Either
import com.mackenzie.naughtyhub.data.repositories.SecondaryRepository
import com.mackenzie.naughtyhub.domain.Error
import com.mackenzie.naughtyhub.domain.video.VideoListItem
import javax.inject.Inject

class GetSecondaryVideoListUseCase @Inject constructor(private val repo: SecondaryRepository) {

    suspend operator fun invoke(serverId:Int, serverUrl: String):  Either<Error, VideoListItem> = repo.secondaryVideoScrapper(serverId, serverUrl)

}