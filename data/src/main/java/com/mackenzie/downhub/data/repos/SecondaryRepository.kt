package com.mackenzie.downhub.data.repos

import arrow.core.Either
import com.mackenzie.downhub.data.embed.extractVideoIdFromHref
import com.mackenzie.downhub.data.map.createVideoItem
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.domain.providers.getNameById
import com.mackenzie.downhub.domain.video.TagDomainInfo
import com.mackenzie.downhub.domain.video.VideoDomainItem
import com.mackenzie.downhub.domain.video.VideoListItem
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import javax.inject.Inject
import kotlin.text.ifEmpty
import kotlin.text.isEmpty

class SecondaryRepository @Inject constructor(
    val moshi: Moshi
) {

    suspend fun secondaryVideoScrapper(serverId: Int, serverUrl: String): Either<Error, VideoListItem> =
        withContext(Dispatchers.IO) {
            val serverName = getNameById(serverId)
            // val baseUrl = getServerUrlById(serverId)

            // Log.d("VideoHubViewModel", "Scrapeando $serverName ($serverId): $serverUrl")
            println( "Scrapeando $serverName ($serverId): $serverUrl" )

            val doc = Jsoup.connect(serverUrl)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .referrer("https://www.google.com")
                .timeout(20000)
                .followRedirects(true)
                .get()

            val scrapedVideos = mutableListOf<VideoDomainItem>()

            // Lista amplia de selectores comunes en sitios de videos
            val videoElements = doc.select(
                "li[data-video-vkey], div.videoBox, div.pcVideoListItem, article.thumb-item, " +
                        "div.thumb-block, div.hvideo, div.video-wrapper, div.mozaique, " +
                        "article[data-id], div[data-id], .thumb-item, .video-item, .video-block, " +
                        "div[class*=videoblock], li[class*=pcVideoListItem]"
            )

            // Log.d("VideoHubViewModel", "Elementos HTML encontrados: ${videoElements.size}")

            videoElements.forEach { element ->
                try {
                    // Extraer ID
                    val videoId = element.attr("data-video-vkey")
                        .ifEmpty { element.attr("data-id") }
                        .ifEmpty { element.attr("data-video-id") }
                        .ifEmpty { extractVideoIdFromHref(element.select("a").attr("href")) }

                    if (videoId.isEmpty()) return@forEach

                    // Extraer Título
                    val titleElement = element.select("a[title], .title a, h2 a, h3 a, span.title").firstOrNull()
                        ?: element.select("a").firstOrNull()

                    val title = titleElement?.attr("title")?.ifEmpty { titleElement.text() }
                        ?: titleElement?.text()
                        ?: element.select("img").attr("alt")
                        ?: "Video $videoId"

                    // Extraer URL
                    val href = element.select("a").attr("href")
                    val fullUrl = when {
                        href.startsWith("http") -> href
                        href.startsWith("/") -> serverUrl + href
                        else -> "$serverUrl/$href"
                    }

                    // Extraer Thumbnail
                    val img = element.select("img").firstOrNull()
                    val thumb = img?.let {
                        it.attr("data-src").ifEmpty { it.attr("src") }
                            .ifEmpty { it.attr("data-original") }
                            .ifEmpty { it.attr("data-thumb_url") }
                            .ifEmpty { it.attr("data-mediabook") }
                    }?.let {
                        when {
                            it.startsWith("//") -> "https:$it"
                            it.startsWith("/") -> serverUrl + it
                            else -> it
                        }
                    } ?: ""

                    // Metadatos adicionales
                    val duration = element.select(".duration, .time, .video-duration, var").text()
                    val views = element.select(".views, .video-views, span.views").text()
                    val rating = element.select(".rating, .percent, .value, .rate, .rating-container .value").text()
                    val tags = element.select("a.tag, .tags a, .videoTagsBlock a").map { TagDomainInfo(it.text().trim()) }.takeIf { it.isNotEmpty() }

                    scrapedVideos.add(
                        createVideoItem(serverId, videoId, title, thumb, fullUrl, serverUrl, duration, views, rating, tags)
                    )
                } catch (e: Exception) { /* Omitir elementos mal formados */ }
            }

            val deduped = when (serverId) {
                9 -> scrapedVideos
                    .filter { it.video.videoId != "ID_NOT_FOUND" }
                    .distinctBy { it.video.title.ifBlank { it.video.url } }
                else -> scrapedVideos
                    .filter { it.video.videoId != "ID_NOT_FOUND" }
                    .distinctBy { it.video.videoId.ifBlank { it.video.url } }
            }

            println( "Videos después de deduplicar: ${deduped.size}, Videos encontrados= ${scrapedVideos.size}")
            return@withContext if (scrapedVideos.isEmpty()) {
                Either.Left(Error.Unknown("No se encontraron videos en $serverName"))
            } else {
                Either.Right(VideoListItem(videos = deduped))
            }
        }
}