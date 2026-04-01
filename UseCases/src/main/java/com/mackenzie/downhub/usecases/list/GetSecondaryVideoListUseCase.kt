package com.mackenzie.downhub.usecases.list

import arrow.core.Either
import com.mackenzie.downhub.data.repos.SecondaryRepository
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.domain.video.VideoListItem
import javax.inject.Inject

class GetSecondaryVideoListUseCase @Inject constructor(private val repo: SecondaryRepository) {

    suspend operator fun invoke(serverId:Int, serverUrl: String):  Either<Error, VideoListItem> = repo.secondaryVideoScrapper(serverId, serverUrl)

}