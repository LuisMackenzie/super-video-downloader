package com.mackenzie.downhub.data.scrap

import com.mackenzie.downhub.data.map.createVideoItem
import com.mackenzie.downhub.domain.video.VideoDomainItem
import org.jsoup.nodes.Document
import kotlin.text.ifEmpty

internal fun scrapeYouPorn(serverId: Int, baseUrl: String, doc: Document, videos: MutableList<VideoDomainItem>) {
    // YouPorn: <a href="/watch/VIDEO_ID/">
    val videoElements = doc.select("a[href*='/watch/']")

    videoElements.forEach { element ->
        try {
            val href = element.attr("href")
            // Extraer ID del URL: /watch/12345678/
            val videoId = href.substringAfter("/watch/").substringBefore("/").substringBefore("?")
            if (videoId.isEmpty() || !videoId.matches(Regex("\\d+"))) return@forEach

            // Título
            val title = element.select("img").attr("alt")
                .ifEmpty { element.attr("title") }
                .ifEmpty { element.text().trim() }
                .ifEmpty { "Video $videoId" }

            // Thumbnail - YouPorn usa lazy loading con data-src o src
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

            // Duración - está en el texto del elemento
            val duration = element.select(".duration, .video-duration, time").text().trim()
                .ifEmpty {
                    // Buscar en el texto del elemento padre o siblings
                    element.parent()?.select(".duration, time, .video-duration")?.text()?.trim() ?: ""
                }

            // Vistas - formato "Vistas: 206K" o similar
            val parent = element.parent()
            val views = parent?.select(".views, .video-views")?.text()?.trim() ?: ""

            // Rating - formato "Calificación: 83%"
            val rating = parent?.select(".rating, .percent")?.text()?.replace("%", "")?.trim() ?: ""

            videos.add(
                createVideoItem(serverId, videoId, title, thumb, fullUrl, baseUrl, duration, views, rating)
            )
        } catch (e: Exception) {
            println("Error parseando elemento YouPorn: ${e.message}")
        }
    }

    val deduped = videos.distinctBy { it.video.videoId.ifBlank { it.video.url } }
    println("Videos después de deduplicar: ${deduped.size}, scrapedVideos.size= ${videos.size}")
    println("Youporn: Total videos extraídos: ${videos.size}")
}