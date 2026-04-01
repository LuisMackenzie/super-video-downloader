package com.mackenzie.naughtyhub.usecases.list

import arrow.core.Either
import com.mackenzie.naughtyhub.data.repositories.TertiaryRepository
import com.mackenzie.naughtyhub.domain.Error
import com.mackenzie.naughtyhub.domain.video.VideoListItem
import javax.inject.Inject

class GetTertiaryVideoListUseCase @Inject constructor(private val repo: TertiaryRepository) {

    suspend operator fun invoke(serverId:Int, serverUrl: String):  Either<Error, VideoListItem> = repo.tertiaryVideoScrapper(serverId, serverUrl)

}