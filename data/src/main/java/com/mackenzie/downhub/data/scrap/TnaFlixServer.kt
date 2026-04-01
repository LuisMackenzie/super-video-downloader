package com.mackenzie.downhub.data.scrap

import com.mackenzie.downhub.data.map.createVideoItem
import com.mackenzie.downhub.domain.video.VideoDomainItem
import org.jsoup.nodes.Document
import kotlin.text.ifEmpty

internal fun scrapeTnaflix(serverId: Int, baseUrl: String, doc: Document, videos: MutableList<VideoDomainItem>) {

    val videoElements = doc.select("a[href*='/video'], div.video-item, div.thumb-item, article.video")

    val allLinks = if (videoElements.isEmpty()) {
        doc.select("a[href*='video'], a[href*='/v/']")
    } else {
        videoElements
    }

    allLinks.forEach { element ->
        try {
            val href = element.attr("href")

            // Extraer ID del URL: video25233922
            val videoId = when {
                href.contains("video") -> {
                    val match = Regex("video(\\d+)").find(href)
                    match?.groupValues?.get(1) ?: ""
                }
                href.contains("/v/") -> href.substringAfter("/v/").substringBefore("/").substringBefore("?")
                else -> ""
            }

            if (videoId.isEmpty()) return@forEach

            val title = element.select("img").attr("alt")
                .ifEmpty { element.attr("title") }
                .ifEmpty { element.text().trim() }
                .ifEmpty { "Video $videoId" }

            val img = element.select("img").firstOrNull()
            val thumb = img?.let {
                it.attr("data-src")
                    .ifEmpty { it.attr("data-original") }
                    .ifEmpty { it.attr("src") }
            }?.let {
                when {
                    it.startsWith("//") -> "https:$it"
                    it.startsWith("/") -> baseUrl + it
                    else -> it
                }
            } ?: ""

            val fullUrl = when {
                href.startsWith("http") -> href
                href.startsWith("/") -> baseUrl + href
                else -> "$baseUrl/$href"
            }

            val duration = element.select(".duration, .time, .video-duration").text().trim()
                .ifEmpty {
                    // Buscar duración en texto del padre o elemento contenedor
                    val parentText = element.parent()?.text() ?: ""
                    val durationMatch = Regex("(\\d{1,2}:\\d{2}(?::\\d{2})?)").find(parentText)
                    durationMatch?.groupValues?.get(1) ?: ""
                }

            videos.add(
                createVideoItem(serverId, videoId, title, thumb, fullUrl, baseUrl, duration)
            )
        } catch (e: Exception) {
            println("Error parseando elemento TNaflix: ${e.message}")
        }
    }
    val deduped = videos.distinctBy { it.video.videoId.ifBlank { it.video.url } }
    println( "Videos después de deduplicar: ${deduped.size}, scrapedVideos.size= ${videos.size}")
    println("TNAFlix: Total videos extraídos: ${videos.size}")
}