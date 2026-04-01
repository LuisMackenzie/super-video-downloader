package com.mackenzie.downhub.data.scrap

import com.mackenzie.downhub.data.map.createVideoItem
import com.mackenzie.downhub.domain.video.VideoDomainItem
import org.jsoup.nodes.Document
import kotlin.text.ifEmpty
import kotlin.text.isEmpty

internal fun scrapeTube8(serverId: Int, baseUrl: String, doc: Document, videos: MutableList<VideoDomainItem>) {
    println("Tube8: Analizando HTML...")

    // Estrategia 1: Buscar elementos con clase video-box (sin js_video-box que puede ser dinámico)
    var videoElements = doc.select("div.video-box[data-video-id]")

    // Estrategia 2: Buscar por estructura alternativa
    if (videoElements.isEmpty()) {
        videoElements = doc.select("div[class*=video-box]")
        println("Tube8: Buscando con selector alternativo, encontrados: ${videoElements.size}")
    }

    // Estrategia 3: Buscar links de videos directamente
    if (videoElements.isEmpty()) {
        val videoLinks = doc.select("a[href*='/porn-video/'], a[href*='/video/']")
        println("Tube8: Buscando por links, encontrados: ${videoLinks.size}")

        videoLinks.forEach { link ->
            try {
                val href = link.attr("href")
                val videoId = Regex("(\\d+)/?$").find(href)?.groupValues?.get(1)
                    ?: href.substringAfterLast("/").substringBefore("?").substringBefore("/")

                if (videoId.isEmpty() || !videoId.matches(Regex("\\d+"))) return@forEach

                val title = link.attr("title")
                    .ifEmpty { link.text().trim() }
                    .ifEmpty { link.select("img").attr("alt") }
                    .ifEmpty { "Video $videoId" }

                val img = link.select("img").firstOrNull()
                val thumb = img?.let {
                    it.attr("data-src")
                        .ifEmpty { it.attr("data-original") }
                        .ifEmpty { it.attr("data-poster") }
                        .ifEmpty { it.attr("src") }
                }?.let {
                    when {
                        it.startsWith("//") -> "https:$it"
                        it.startsWith("/") && !it.startsWith("//") -> "https://www.tube8.com$it"
                        else -> it
                    }
                } ?: ""

                val fullUrl = when {
                    href.startsWith("http") -> href
                    href.startsWith("/") -> "https://www.tube8.com$href"
                    else -> "https://www.tube8.com/$href"
                }

                // Duración - buscar en elementos hermanos o padres
                val duration = link.parent()?.select(".video-duration, .duration, time")?.text()?.trim()
                    ?: link.select(".duration, .video-duration").text().trim()

                videos.add(
                    createVideoItem(serverId, videoId, title, thumb, fullUrl, baseUrl, duration)
                )
            } catch (e: Exception) {
                println("Tube8: Error parseando link: ${e.message}")
            }
        }
        return // Si usamos esta estrategia, salimos
    }

    // Procesar elementos encontrados con estrategia 1 o 2
    videoElements.forEach { element ->
        try {
            val videoId = element.attr("data-video-id")
                .ifEmpty { element.attr("data-id") }
                .ifEmpty { element.attr("id") }

            if (videoId.isEmpty()) {
                // Intentar extraer del href
                val href = element.select("a[href*='/porn-video/'], a[href*='/video/']").attr("href")
                val extractedId = Regex("(\\d+)").find(href)?.groupValues?.get(1)
                if (extractedId.isNullOrEmpty()) return@forEach
            }

            // Título
            val title = element.select(".video-title-text").text().trim()
                .ifEmpty { element.select("a[title]").attr("title") }
                .ifEmpty { element.select("img").attr("alt") }
                .ifEmpty { element.select("a").text().trim() }
                .ifEmpty { "Video $videoId" }

            // URL
            val href = element.select("a[href*='/porn-video/'], a[href*='/video/']").firstOrNull()?.attr("href") ?: ""
            val fullUrl = when {
                href.startsWith("http") -> href
                href.startsWith("/") -> "https://www.tube8.com$href"
                else -> "https://www.tube8.com/video/$videoId"
            }

            // Thumbnail
            val img = element.select("img").firstOrNull()
            val thumb = img?.let {
                it.attr("data-poster")
                    .ifEmpty { it.attr("data-mediabook") }
                    .ifEmpty { it.attr("data-src") }
                    .ifEmpty { it.attr("data-original") }
                    .ifEmpty { it.attr("src") }
            }?.let {
                when {
                    it.startsWith("//") -> "https:$it"
                    it.startsWith("/") -> "https://www.tube8.com$it"
                    else -> it
                }
            } ?: ""

            // Duración
            val duration = element.select(".video-duration, .duration").text().trim()

            // Vistas
            val views = element.select(".info-views, .views").text().trim()

            videos.add(
                createVideoItem(serverId, videoId, title, thumb, fullUrl, baseUrl, duration, views)
            )
        } catch (e: Exception) {
            println("Tube8: Error parseando elemento: ${e.message}")
        }
    }

    // Estrategia 4: Buscar datos JSON embebidos en scripts
    if (videos.isEmpty()) {
        val scripts = doc.select("script:not([src])")
        scripts.forEach { script ->
            val content = script.html()
            if (content.contains("video_id") || content.contains("data-video-id") || content.contains("\"id\"")) {
                try {
                    // Buscar patrones de IDs
                    val idPattern = Regex(""""(?:video_id|videoId|video_id|id)"\s*[:=]\s*"?(\d+)"?""")
                    val titlePattern = Regex(""""(?:title|video_title|videoTitle)"\s*[:=]\s*"([^"]+)"""")
                    val thumbPattern = Regex(""""(?:thumb|thumbnail|poster|image|img)"\s*[:=]\s*"([^"]+)"""")

                    val ids = idPattern.findAll(content).map { it.groupValues[1] }.toList()
                    val titles = titlePattern.findAll(content).map { it.groupValues[1] }.toList()
                    val thumbs = thumbPattern.findAll(content).map { it.groupValues[1] }.toList()

                    ids.forEachIndexed { index, vidId ->
                        if (vidId.isNotEmpty()) {
                            val title = titles.getOrNull(index)?.replace("\\u0027", "'") ?: "Video $vidId"
                            val thumb = thumbs.getOrNull(index)?.let {
                                when {
                                    it.startsWith("//") -> "https:$it"
                                    else -> it
                                }
                            } ?: ""

                            videos.add(
                                createVideoItem(
                                    serverId, vidId, title, thumb,
                                    "https://www.tube8.com/porn-video/$vidId/", baseUrl
                                )
                            )
                        }
                    }
                } catch (e: Exception) {
                    println("Tube8: Error parseando script: ${e.message}")
                }
            }
        }
    }

    val deduped = videos.distinctBy { it.video.videoId.ifBlank { it.video.url } }
    println( "Videos después de deduplicar: ${deduped.size}, scrapedVideos.size= ${videos.size}")
    println("Tube8: Total videos extraídos: ${videos.size}")
}