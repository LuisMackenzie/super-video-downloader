package com.mackenzie.downhub.data.repos

import arrow.core.Either
import com.mackenzie.downhub.data.embed.extractVideoIdFromHref
import com.mackenzie.downhub.data.embed.fetchHtml
import com.mackenzie.downhub.data.map.createVideoItem
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
import kotlin.text.ifEmpty
import kotlin.text.isEmpty

class PrimaryRepository @Inject constructor(
    val okHttpClient: OkHttpClient,
    val moshi: Moshi
) {

    suspend fun primaryVideoScrapper(serverId: Int, serverUrl: String): Either<Error, VideoListItem> = withContext(Dispatchers.IO) {
        val serverName = getNameById(serverId)
        val baseUri = URI(serverUrl)
        val baseUrl = "${baseUri.scheme ?: "https"}://${baseUri.host ?: "www.beeg.com"}"

        println("Scrapeando $serverName ($serverId): $serverUrl")

        // Estrategia 2: Fetch HTML vía OkHttp y extraer datos de scripts JavaScript
        val (html, httpErr) = fetchHtml(serverUrl, okHttpClient)
        if (httpErr != null && html.isBlank()) {
            return@withContext Either.Left(Error.Unknown(httpErr))
        }

        // Parse HTML con JSoup
        val doc = Jsoup.parse(html, baseUrl)
        val scrapedVideos = mutableListOf<VideoDomainItem>()

        // Estrategia 3: Extraer datos de scripts embebidos (JSON en JavaScript)
        val scripts = doc.select("script:not([src])")
        scripts.forEach { script ->
            val content = script.html()

            // Buscar patrones comunes de datos de videos en JavaScript
            // Patrón 1: Array de videos en formato JSON
            if (content.contains("videos") || content.contains("\"id\"") || content.contains("videoList")) {
                try {
                    // Extraer IDs de video
                    val idPattern = """"(?:id|video_id|videoId)"\s*:\s*"?(\d+)"?""".toRegex()
                    val titlePattern = """"(?:title|name|videoTitle)"\s*:\s*"([^"]+)"""".toRegex()
                    val thumbPattern = """"(?:thumb|thumbnail|preview|img)"\s*:\s*"([^"]+)"""".toRegex()
                    val durationPattern = """"(?:duration|length|time)"\s*:\s*"?(\d+)"?""".toRegex()
                    val viewsPattern = """"(?:views|view_count)"\s*:\s*"?(\d+)"?""".toRegex()

                    val ids = idPattern.findAll(content).map { it.groupValues[1] }.toList()
                    val titles = titlePattern.findAll(content).map { it.groupValues[1] }.toList()
                    val thumbs = thumbPattern.findAll(content).map { it.groupValues[1] }.toList()
                    val durations = durationPattern.findAll(content).map { it.groupValues[1] }.toList()
                    val views = viewsPattern.findAll(content).map { it.groupValues[1] }.toList()

                    ids.forEachIndexed { index, vidId ->
                        if (vidId.isNotEmpty()) {
                            val title = titles.getOrNull(index) ?: "Video $vidId"
                            val thumb = thumbs.getOrNull(index) ?: ""
                            val duration = durations.getOrNull(index) ?: ""
                            val viewCount = views.getOrNull(index) ?: ""

                            val videoUrl = "$baseUrl/$vidId"
                            val cleanThumb = when {
                                thumb.startsWith("//") -> "https:$thumb"
                                thumb.startsWith("/") -> baseUrl + thumb
                                thumb.startsWith("http") -> thumb
                                else -> ""
                            }

                            scrapedVideos.add(
                                createVideoItem(
                                    serverId = serverId,
                                    videoId = vidId,
                                    title = title.replace("\\u0027", "'").replace("\\\"", "\""),
                                    thumb = cleanThumb,
                                    url = videoUrl,
                                    baseUrl = baseUrl,
                                    duration = formatDuration(duration),
                                    views = viewCount,
                                    rating = ""
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    println("Error al parsear script JS: ${e.message}")
                }
            }
        }

        // Estrategia 4: Buscar elementos HTML estándar (similar a secondaryVideoScrapper)
        if (scrapedVideos.isEmpty()) {
            val videoElements = doc.select(
                "article.video, div.video-item, div.thumb-item, li.video, " +
                        "div[data-id], article[data-id], div[data-video-id], " +
                        "a[href*='/video/'], a[href*='/v/'], .video-block, .video-card"
            )

            videoElements.forEach { element ->
                try {
                    val videoId = element.attr("data-id")
                        .ifEmpty { element.attr("data-video-id") }
                        .ifEmpty { extractVideoIdFromHref(element.select("a").attr("href")) }

                    if (videoId.isEmpty()) return@forEach

                    val titleElement = element.select("a[title], .title, h2, h3").firstOrNull()
                    val title = titleElement?.attr("title")?.ifEmpty { titleElement.text() }
                        ?: element.select("img").attr("alt")
                        ?: "Video $videoId"

                    val href = element.select("a").attr("href")
                    val fullUrl = when {
                        href.startsWith("http") -> href
                        href.startsWith("/") -> baseUrl + href
                        else -> "$baseUrl/$videoId"
                    }

                    val img = element.select("img").firstOrNull()
                    println("VideoHubRepository::img=$img")
                    val thumb = img?.let {
                        when (serverId) {
                            19, 28, 29 -> it.attr("data-original")
                                .ifEmpty { it.attr("src") }
                                .ifEmpty { it.attr("data-src") }
                            else -> it.attr("data-src")
                                .ifEmpty { it.attr("src") }
                                .ifEmpty { it.attr("data-original") }
                        }
                    }?.let {
                        when {
                            it.startsWith("//") -> "https:$it"
                            it.startsWith("/") -> baseUrl + it
                            else -> it
                        }
                    } ?: ""

                    val duration = element.select(".duration, .time, var").text()
                    val views = element.select(".views, .view-count").text()

                    scrapedVideos.add(
                        createVideoItem(serverId, videoId, title, thumb, fullUrl, baseUrl, duration, views)
                    )
                } catch (e: Exception) {
                    // Ignorar elementos mal formados
                }
            }
        }

        val deduped = scrapedVideos.distinctBy { it.video.title.ifBlank { it.video.url } }
        println( "Videos después de deduplicar: ${deduped.size}, Videos encontrados= ${scrapedVideos.size}")
        return@withContext if (scrapedVideos.isEmpty()) {
            Either.Left(Error.Unknown("No se encontraron videos en $serverName"))
        } else {
            Either.Right(VideoListItem(videos = deduped))
        }
    }

    // Función auxiliar para formatear duración (de segundos a mm:ss)
    private fun formatDuration(seconds: String): String {
        return try {
            val totalSeconds = seconds.toLongOrNull() ?: return seconds
            val minutes = totalSeconds / 60
            val secs = totalSeconds % 60
            String.format(java.util.Locale.US, "%d:%02d", minutes, secs)
        } catch (_: Exception) {
            seconds
        }
    }
}