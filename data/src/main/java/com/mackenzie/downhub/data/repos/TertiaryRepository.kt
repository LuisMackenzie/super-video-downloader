package com.mackenzie.downhub.data.repos

import arrow.core.Either
import com.mackenzie.downhub.data.embed.fetchHtml
import com.mackenzie.downhub.data.scrap.*
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.domain.providers.getNameById
import com.mackenzie.downhub.domain.video.VideoDomainItem
import com.mackenzie.downhub.domain.video.VideoListItem
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import java.net.URI
import javax.inject.Inject

class TertiaryRepository @Inject constructor(
    val okHttpClient: OkHttpClient,
    val moshi: Moshi
) {

    suspend fun tertiaryVideoScrapper(serverId: Int, serverUrl: String) : Either<Error, VideoListItem> = withContext(Dispatchers.IO) {
        val serverName = getNameById(serverId)
        val baseUri = URI(serverUrl)
        val baseUrl = "${baseUri.scheme ?: "https"}://${baseUri.host ?: serverUrl.substringAfter("://").substringBefore("/")}"

        println("Scrapeando $serverName ($serverId): $serverUrl")

        // Fetch HTML vía OkHttp
        val (html, httpErr) = fetchHtml(serverUrl, okHttpClient)
        if (httpErr != null && html.isBlank()) {
            return@withContext Either.Left(Error.Unknown(httpErr))
        }

        val doc = Jsoup.parse(html, baseUrl)
        val scrapedVideos = mutableListOf<VideoDomainItem>()

        when (serverId) {
            1, 66 -> scrapePHubServer(serverId, baseUrl, scrapedVideos)
            5 -> scrapeTube8(serverId, baseUrl, doc, scrapedVideos)
            8 -> scrapeYouPorn(serverId, baseUrl, doc, scrapedVideos)
            11 -> scrapeTnaflix(serverId, baseUrl, doc, scrapedVideos)
            15 -> scrapeHQPorner(serverId, baseUrl, doc, scrapedVideos)
            6, 7, 12, 16, 17, 50, 51 -> scrapeXHamsterServer(serverId, baseUrl, html, doc, scrapedVideos)
            else -> scrapeGeneric(serverId, baseUrl, doc, scrapedVideos)
        }

        println("Videos encontrados en $serverName: ${scrapedVideos.size}")
        return@withContext if (scrapedVideos.isEmpty()) {
            Either.Left(Error.Unknown("No se encontraron videos en $serverName"))
        } else {
            Either.Right(VideoListItem(videos = scrapedVideos.distinctBy { it.video.videoId }))
        }
    }
}