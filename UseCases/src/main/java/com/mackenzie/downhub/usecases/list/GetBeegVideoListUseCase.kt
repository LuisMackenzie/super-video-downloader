package com.mackenzie.naughtyhub.usecases.list


import arrow.core.Either
import com.mackenzie.naughtyhub.data.repositories.BeegRepository
import com.mackenzie.naughtyhub.data.repositories.VideoHubRepository
import com.mackenzie.naughtyhub.domain.Error
import com.mackenzie.naughtyhub.domain.video.VideoListItem
import javax.inject.Inject

class GetBeegVideoListUseCase @Inject constructor(private val repo: BeegRepository) {

    suspend operator fun invoke(serverId: Int, serverUrl: String):  Either<Error, VideoListItem> = repo.beegVideoScrapper( serverUrl)

}