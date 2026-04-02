package com.mackenzie.downhub.usecases.list


import arrow.core.Either
import com.mackenzie.downhub.data.repos.BeegRepository
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.domain.video.VideoListItem
import okhttp3.OkHttpClient
import javax.inject.Inject

class GetBeegVideoListUseCase @Inject constructor(private val repo: BeegRepository) {

    suspend operator fun invoke(serverId: Int, serverUrl: String, okHttpProxyClient: OkHttpClient):  Either<Error, VideoListItem> = repo.beegVideoScrapper( serverUrl, okHttpProxyClient)

}