package com.mackenzie.downhub.usecases.list

import arrow.core.Either
import com.mackenzie.downhub.data.repos.PrimaryRepository
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.domain.video.VideoListItem
import okhttp3.OkHttpClient
import javax.inject.Inject

class GetPrimaryVideoListUseCase @Inject constructor(private val repo: PrimaryRepository) {

    suspend operator fun invoke(serverId: Int, serverUrl: String, okHttpProxyClient: OkHttpClient):  Either<Error, VideoListItem> = repo.primaryVideoScrapper(serverId, serverUrl, okHttpProxyClient)

}