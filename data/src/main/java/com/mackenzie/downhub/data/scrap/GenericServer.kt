package com.mackenzie.downhub.data.scrap

import com.mackenzie.downhub.data.embed.extractVideoIdFromHref
import com.mackenzie.downhub.data.map.createVideoItem
import com.mackenzie.downhub.domain.video.VideoDomainItem
import org.jsoup.nodes.Document
import kotlin.text.ifEmpty
import kotlin.text.isEmpty
import kotlin.text.startsWith

internal fun scrapeGeneric(serverId: Int, baseUrl: String, doc: Document, videos: MutableList<VideoDomainItem>) {
    // Fallback genérico para otros sitios
    val videoElements = doc.select(
        "div.video-box, div.video-item, div.thumb-item, div.thumb-block, " +
                "article.video, article[data-id], div[data-id], div[data-video-id], " +
                "a[href*='/watch/'], a[href*='/video/']"
    )

    videoElements.forEach { element ->
        try {
            val videoId = element.attr("data-video-id")
                .ifEmpty { element.attr("data-id") }
                .ifEmpty { extractVideoIdFromHref(element.attr("href")) }
                .ifEmpty { extractVideoIdFromHref(element.select("a").attr("href")) }

            if (videoId.isEmpty() || videoId == "ID_NOT_FOUND") return@forEach

            val title = element.select("a[title]").attr("title")
                .ifEmpty { element.select(".title, h2, h3").text() }
                .ifEmpty { element.select("img").attr("alt") }
                .ifEmpty { "Video $videoId" }

            val href = element.select("a").attr("href").ifEmpty { element.attr("href") }
            val fullUrl = when {
                href.startsWith("http") -> href
                href.startsWith("/") -> baseUrl + href
                else -> "$baseUrl/$href"
            }

            val img = element.select("img").firstOrNull()
            val thumb = img?.attr("data-src")
                ?.ifEmpty { img.attr("src") }
                ?.ifEmpty { img.attr("data-original") }
                ?.let {
                    when {
                        it.startsWith("//") -> "https:$it"
                        it.startsWith("/") -> baseUrl + it
                        else -> it
                    }
                } ?: ""

            val duration = element.select(".duration, .time, .video-duration").text()
            val views = element.select(".views, .video-views").text()

            videos.add(
                createVideoItem(serverId, videoId, title, thumb, fullUrl, baseUrl, duration, views)
            )
        } catch (e: Exception) {
            // Ignorar elementos mal formados
        }
    }
    val deduped = videos.distinctBy { it.video.videoId.ifBlank { it.video.url } }
    println("Videos después de deduplicar: ${deduped.size}, scrapedVideos.size= ${videos.size}")
    println("Generic: Total videos extraídos: ${videos.size}")
}