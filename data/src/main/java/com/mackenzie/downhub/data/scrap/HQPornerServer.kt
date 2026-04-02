package com.mackenzie.downhub.data.scrap

import com.mackenzie.downhub.data.map.createVideoItem
import com.mackenzie.downhub.domain.video.VideoDomainItem
import org.jsoup.nodes.Document
import kotlin.text.ifEmpty
import kotlin.text.isEmpty
import kotlin.text.startsWith

internal fun scrapeHQPorner(serverId: Int, baseUrl: String, doc: Document, videos: MutableList<VideoDomainItem>) {
    println("HQPorner: Analizando HTML...")

    // HQPorner usa enlaces directos: /hdporn/{id}-{slug}.html
    // Ejemplo: /hdporn/125518-some_title_here.html
    val videoElements = doc.select("a[href*='/hdporn/']")

    println("HQPorner: Enlaces encontrados: ${videoElements.size}")

    videoElements.forEach { element ->
        try {
            val href = element.attr("href")
            if (href.isEmpty() || !href.contains("/hdporn/")) return@forEach

            // Extraer ID del URL: /hdporn/125518-slug.html -> 125518
            val videoId = Regex("/hdporn/(\\d+)-").find(href)?.groupValues?.get(1)
            if (videoId.isNullOrEmpty()) return@forEach

            // Título - está en el texto del enlace o en el atributo title
            val title = element.attr("title")
                .ifEmpty { element.text().trim() }
                .ifEmpty { "Video $videoId" }

            // Thumbnail - buscar img dentro del enlace o en estructura adyacente
            // HQPorner usa imgs con patrón _main.jpg y preload arrays
            val img = element.select("img").firstOrNull()
            val thumb = img?.let {
                val src = it.attr("data-src")
                    .ifEmpty { it.attr("data-original") }
                    .ifEmpty { it.attr("src") }
                    .ifEmpty { it.attr("data-preview") }

                when {
                    src.startsWith("//") -> "https:$src"
                    src.startsWith("/") -> "https://hqporner.com$src"
                    src.isEmpty() -> ""
                    else -> src
                }
            } ?: ""

            // Construir URL completa
            val fullUrl = when {
                href.startsWith("http") -> href
                href.startsWith("/") -> "https://hqporner.com$href"
                else -> "https://hqporner.com/$href"
            }

            // Duración - buscar en elementos cercanos (.duration, .time, o texto con formato Xh Xm Xs)
            val parent = element.parent()
            val duration = element.select(".duration, .time").text().trim()
                .ifEmpty { parent?.select(".duration, .time")?.text()?.trim() ?: "" }
                .ifEmpty {
                    // Buscar patrón de duración en el texto adyacente (ej: "24m 51s", "2h 14m")
                    val parentText = parent?.text() ?: ""
                    val durationMatch = Regex("(\\d+h\\s*)?(\\d+m\\s*)?(\\d+s)?").find(parentText)
                    durationMatch?.groupValues?.get(0)?.trim() ?: ""
                }

            // Vistas - buscar en elementos cercanos
            val views = parent?.select(".views, .view-count")?.text()?.trim() ?: ""

            // Rating - HQPorner a veces tiene ratings
            val rating = parent?.select(".rating, .percent")?.text()?.replace("%", "")?.trim() ?: ""

            videos.add(
                createVideoItem(serverId, videoId, title, thumb, fullUrl, baseUrl, duration, views, rating)
            )
        } catch (e: Exception) {
            println("HQPorner: Error parseando elemento: ${e.message}")
        }
    }

    // Estrategia alternativa: Buscar por imágenes con preload
    if (videos.isEmpty()) {
        println("HQPorner: Buscando estrategia alternativa...")

        // Buscar scripts con preload arrays
        val scripts = doc.select("script:not([src])")
        scripts.forEach { script ->
            val content = script.html()

            // Patrón: var preload_125518=["//domain/imgs/..._1.jpg",...]
            val preloadPattern = Regex("""var\s+preload_(\d+)\s*=\s*\[(.*?)\]""")
            preloadPattern.findAll(content).forEach { match ->
                val vidId = match.groupValues[1]
                val thumbsArray = match.groupValues[2]

                // Extraer primera thumbnail del array
                val firstThumb = Regex("//[^\"']+").find(thumbsArray)?.groupValues?.get(0) ?: ""
                val thumb = if (firstThumb.isNotEmpty()) "https:$firstThumb" else ""

                // Buscar título en elemento HTML cercano
                val title = "Video $vidId"

                val fullUrl = "https://hqporner.com/hdporn/$vidId.html"

                videos.add(
                    createVideoItem(serverId, vidId, title, thumb, fullUrl, baseUrl)
                )
            }
        }
    }

    val deduped = videos.distinctBy { it.video.videoId.ifBlank { it.video.url } }
    println("Videos después de deduplicar: ${deduped.size}, scrapedVideos.size= ${videos.size}")
    println("HQPorner: Total videos extraídos: ${videos.size}")
}