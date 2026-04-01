package com.mackenzie.downhub.usecases.list

import arrow.core.Either
import com.mackenzie.downhub.data.repos.TertiaryRepository
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.domain.video.VideoListItem
import javax.inject.Inject

class GetTertiaryVideoListUseCase @Inject constructor(private val repo: TertiaryRepository) {

    suspend operator fun invoke(serverId:Int, serverUrl: String):  Either<Error, VideoListItem> = repo.tertiaryVideoScrapper(serverId, serverUrl)

}