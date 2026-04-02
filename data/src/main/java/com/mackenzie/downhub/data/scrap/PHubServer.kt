package com.mackenzie.downhub.data.scrap

import com.mackenzie.downhub.data.map.createVideoItem
import com.mackenzie.downhub.domain.video.TagDomainInfo
import com.mackenzie.downhub.domain.video.VideoDomainItem
import org.jsoup.Jsoup

internal fun scrapePHubServer(serverId: Int, baseUrl: String, videos: MutableList<VideoDomainItem>) {

    try {

        // Hacer la petición HTTP y obtener el documento HTML
        val doc = Jsoup.connect(baseUrl)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            .referrer("https://www.google.com")
            .timeout(15000)
            .followRedirects(true)
            .get()

        println( "Scrapeando $baseUrl - Título de la página: ${doc.title()}" )

        // Scraping de videos - Pornhub usa diferentes selectores dependiendo del layout
        var videoElements = doc.select("li[data-video-vkey]")

        if (videoElements.isEmpty()) {
            videoElements = doc.select("div.videoBox, div.pcVideoListItem")
        }

        if (videoElements.isEmpty()) {
            videoElements = doc.select("div[class*=videoblock], li[class*=pcVideoListItem]")
        }

        println( "Elementos de video encontrados: ${videoElements.size}" )

        val scrapedVideos = videoElements.mapNotNull { element ->
            try {
                // Extraer videoId con múltiples estrategias
                val videoId = element.attr("data-video-vkey").ifEmpty {
                    val href = element.select("a").attr("href")
                    when {
                        href.contains("viewkey=") -> href.substringAfter("viewkey=").substringBefore("&")
                        href.contains("/view_video.php") -> href.substringAfter("viewkey=").substringBefore("&")
                        href.startsWith("/video/") -> href.substringAfter("/video/")
                        else -> ""
                    }
                }

                // Extraer título con múltiples estrategias
                val titleElement = element.select("a[title]").firstOrNull()
                    ?: element.select(".title a").firstOrNull()
                    ?: element.select("span.title").firstOrNull()
                    ?: element.select("a").firstOrNull()

                val title = titleElement?.attr("title")?.ifEmpty { titleElement.text() }
                    ?: titleElement?.text()
                    ?: ""

                // Extraer URL
                val url = element.select("a").attr("href").let { href ->
                    when {
                        href.startsWith("http") -> href
                        href.startsWith("/") -> "https://es.pornhub.com$href"
                        else -> "https://es.pornhub.com/$href"
                    }
                }

                // Extraer thumbnail con múltiples estrategias
                val imgElement = element.select("img").firstOrNull()
                val thumb = imgElement?.attr("data-src")?.ifEmpty {
                    imgElement.attr("src")
                }?.ifEmpty {
                    imgElement.attr("data-thumb_url")
                }?.ifEmpty {
                    imgElement.attr("data-mediabook")
                } ?: ""

                // Extraer duración
                val duration = element.select(".duration").text().ifEmpty {
                    element.select(".marker-overlays var").text().ifEmpty {
                        element.select("var.duration").text()
                    }
                }

                // Extraer vistas
                val views = element.select(".views").text().ifEmpty {
                    element.select(".videoDetailsBlock var").text().ifEmpty {
                        element.select("span.views").text()
                    }
                }

                // Extraer rating
                val rating = element.select(".value").text().ifEmpty {
                    element.select(".percent").text().ifEmpty {
                        element.select(".rating-container .value").text()
                    }
                }

                // Extraer tags si están disponibles
                val tags = element.select(".videoTagsBlock a, .pstar-list-btn").mapNotNull { tag ->
                    val tagText = tag.text().trim()
                    if (tagText.isNotEmpty()) TagDomainInfo(tagName = tagText) else null
                }

                // Validar que tengamos datos mínimos necesarios
                if (title.isNotEmpty()) {
                    createVideoItem(
                        serverId,
                        videoId.ifEmpty { System.currentTimeMillis().toString() },
                        title,
                        thumb,
                        url,
                        baseUrl,
                        duration,
                        views,
                        rating,
                        tags.takeIf { it.isNotEmpty() }
                    )
                } else {
                    println("Video ignorado - título vacío. URL: $url, videoId: $videoId" )
                    null
                }
            } catch (e: Exception) {
                println("Error al parsear video individual: ${e.message}")
                null
            }
        }

        println( "Videos scrapeados exitosamente: ${scrapedVideos.size}" )

        if (scrapedVideos.isNotEmpty()) {
            videos.addAll(scrapedVideos)
        }

        val deduped = videos.distinctBy { it.video.videoId.ifBlank { it.video.url } }
        println("Videos después de deduplicar: ${deduped.size}, scrapedVideos.size= ${videos.size}")
        println("PHub: Total videos extraídos: ${videos.size}")

    } catch (e: Exception) {
        println("Error al scrapear PHubServer: ${e.message}")
        // devolver lista vacia o el error para manejarlo en el ViewModel
        return
    }
}