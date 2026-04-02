package com.mackenzie.downhub.data.scrap

import com.mackenzie.downhub.data.embed.extractXhamsterIdFromUrl
import com.mackenzie.downhub.data.map.createVideoItem
import com.mackenzie.downhub.domain.video.VideoDomainItem
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URI

internal fun scrapeXHamsterServer(
    serverId: Int,
    serverUrl: String,
    html: String,
    doc: Document,
    videos: MutableList<VideoDomainItem>
) {

    try {

        val baseUri = URI(serverUrl)
        val baseUrl = "${baseUri.scheme ?: "https"}://${baseUri.host}"

        // 3) Detección de bloqueo (más conservadora para evitar falsos positivos)
        val htmlLower = html.lowercase()
        val titleLower = doc.title().lowercase()

        val looksLikeChallenge =
            listOf("captcha", "cloudflare", "access denied", "verify you are human", "enable javascript").any { token ->
                titleLower.contains(token) || htmlLower.contains(token)
            }

        // Si es un challenge, normalmente el HTML es corto y sin anchors de videos
        val videoAnchorsCount = doc.select("a[href*=/videos/]").size
        val extremelyShort = html.length < 10_000

        if (looksLikeChallenge && (videoAnchorsCount == 0 || extremelyShort)) {
            println("xHamster devolvió una página de verificación (anti-bot / requiere JavaScript).")
            return
        }

        // 1) Intento principal: cards de video por selectores comunes en xHamster
        val primarySelectors = listOf(
            "article",
            "div.video-thumb",
            "div.thumb-list__item",
            "div.video-item",
            "div[data-video-id]",
            "li[data-video-id]"
        )

        fun resolveUrlMaybeRelative(raw: String): String {
            if (raw.isBlank()) return ""
            return try {
                when {
                    raw.startsWith("http") -> raw
                    raw.startsWith("//") -> "https:$raw"
                    else -> baseUri.resolve(raw).toString()
                }
            } catch (_: Exception) {
                when {
                    raw.startsWith("//") -> "https:$raw"
                    raw.startsWith("/") -> baseUrl + raw
                    else -> "$baseUrl/$raw"
                }
            }
        }

        fun parseCard(element: Element): VideoDomainItem? {
            // URL principal
            val linkEl = element.selectFirst(
                "a[href*=/videos/], a[href*=/video/], a[href*=/porn/], a[href]"
            ) ?: return null

            val href = linkEl.attr("href")
            val fullUrl = resolveUrlMaybeRelative(href)
            if (fullUrl.isBlank()) return null

            // Evitar enlaces que no son videos (por ej. perfiles/categorías)
            if (!fullUrl.contains("/videos/")) {
                val looksLikeVideo = fullUrl.contains("/video") || fullUrl.contains("/porn")
                if (!looksLikeVideo) return null
            }

            // ID
            val videoId = element.attr("data-video-id")
                .ifEmpty { element.attr("data-id") }
                .ifEmpty { extractXhamsterIdFromUrl(fullUrl) }
                .trim()
            if (videoId.isBlank()) return null

            // Metadatos (los calculamos antes para poder validar el título)
            val duration = element.select(
                ".duration, .time, [class*=duration], [class*=time]"
            ).text().trim()

            val views = element.select(
                ".views, [class*=views]"
            ).text().trim()

            fun normalizeTitle(raw: String): String {
                return raw
                    .replace("\u00A0", " ")
                    .replace(Regex("\\s+"), " ")
                    .trim()
            }

            fun looksLikeDuration(text: String): Boolean {
                val t = text.trim()
                if (t.isBlank()) return false
                // Formatos típicos: 12:34, 1:02:03
                if (Regex("^\\d{1,2}:\\d{2}(?::\\d{2})?$").matches(t)) return true
                // A veces: "12 min" o "12m"
                if (Regex("^\\d+\\s*(min|mins|m)$", RegexOption.IGNORE_CASE).matches(t)) return true
                return false
            }

            fun candidateTitles(): List<String> {
                val candidates = mutableListOf<String>()

                // 1) Atributos comunes en el link
                candidates += linkEl.attr("title")
                candidates += linkEl.attr("aria-label")
                candidates += linkEl.attr("data-title")

                // 2) Títulos dentro de headings
                candidates += element.selectFirst("h1")?.text().orEmpty()
                candidates += element.selectFirst("h2")?.text().orEmpty()
                candidates += element.selectFirst("h3")?.text().orEmpty()

                // 3) Elementos con clases típicas de título
                candidates += element.selectFirst(".title")?.text().orEmpty()
                candidates += element.selectFirst("[class*=title]")?.text().orEmpty()

                // 4) Meta semántico
                candidates += element.selectFirst("meta[itemprop=name]")?.attr("content").orEmpty()
                candidates += element.selectFirst("[itemprop=name]")?.attr("content").orEmpty()
                candidates += element.selectFirst("[itemprop=name]")?.text().orEmpty()

                // 5) Fallback: alt de imagen
                candidates += element.selectFirst("img[alt]")?.attr("alt").orEmpty()

                // 6) Último recurso: texto del link (pero puede estar contaminado)
                candidates += linkEl.text()

                return candidates
                    .map(::normalizeTitle)
                    .filter { it.isNotBlank() }
                    .distinct()
            }

            val title = candidateTitles()
                .firstOrNull { cand ->
                    // No aceptamos títulos que claramente sean solo duración
                    if (looksLikeDuration(cand)) return@firstOrNull false

                    // Si coincide exactamente con duration, descartarlo
                    if (duration.isNotBlank() && cand.equals(duration, ignoreCase = true)) return@firstOrNull false

                    // Evitar títulos excesivamente cortos que suelen ser ruido (ej: "HD")
                    if (cand.length < 4) return@firstOrNull false

                    true
                }
                ?: run {
                    // Último fallback: si todo falla, usar el alt aunque sea corto
                    val alt = normalizeTitle(element.selectFirst("img[alt]")?.attr("alt").orEmpty())
                    if (alt.isNotBlank() && !looksLikeDuration(alt) && alt != duration) alt else ""
                }

            if (title.isBlank()) return null

            // Thumb
            val img = element.selectFirst("img")
            val thumbRaw = img?.attr("data-src")
                ?.ifEmpty { img.attr("data-original") }
                ?.ifEmpty { img.attr("data-lazy") }
                ?.ifEmpty { img.attr("src") }
                ?.ifEmpty { element.selectFirst("video[poster]")?.attr("poster") ?: "" }
                ?: ""

            val thumb = resolveUrlMaybeRelative(thumbRaw)

            println( "Video Found - ID: $videoId, Title: $title, Duration: $duration, URL: $fullUrl, Thumb: $thumb")

            return createVideoItem(
                serverId = serverId,
                videoId = videoId,
                title = title,
                thumb = thumb,
                url = fullUrl,
                baseUrl = baseUrl,
                duration = duration,
                views = views
            )
        }

        println( "scrapedVideos.size antes de primarySelectors= ${videos.size}")

        // 1a) Recogida por selectores principales
        // Procesamos TODOS los selectores para maximizar la cantidad de videos encontrados
        for (selector in primarySelectors) {
            val cards = doc.select(selector)
            println( "Selector '$selector' encontró ${cards.size} elementos.")
            if (cards.isEmpty()) continue

            cards.forEach { el ->
                try {
                    parseCard(el)?.let { video ->
                        // Evitar duplicados durante la recolección
                        val isDuplicate = videos.any {
                            it.video.videoId == video.video.videoId ||
                                    it.video.url == video.video.url
                        }
                        if (!isDuplicate) {
                            videos.add(video)
                        }
                    }
                } catch (_: Exception) {
                    // ignorar
                }
            }

            // Continuamos con todos los selectores en lugar de hacer break temprano
            // Esto permite recolectar videos de diferentes estructuras HTML en la misma página
        }
        println( "Videos scrapeados con selectores principales: ${videos.size}")

        // 2) Fallback: anchors directos a /videos/
        if (videos.isEmpty()) {
            val anchors = doc.select("a[href*=/videos/]")
            println( "Fallback anchors encontrados: ${anchors.size}")
            anchors.forEach { a ->
                try {
                    val container = a.parent() ?: a
                    val temp = parseCard(container)
                    parseCard(container)?.let {
                        videos.add(it)
                    } ?: run {
                        val href = resolveUrlMaybeRelative(a.attr("href"))
                        val videoId = extractXhamsterIdFromUrl(href)
                        val title = a.attr("title").ifEmpty { a.text() }.trim()
                        val thumb = container.selectFirst("img")?.attr("data-original") ?: ""
                        println( "Fallback Anchor - ID: $videoId, Title: $title, Href: $href, Thumb: $thumb")
                        if (videoId.isNotBlank() && title.isNotBlank()) {
                            videos.add(
                                createVideoItem(
                                    serverId = serverId,
                                    videoId = videoId,
                                    title = title,
                                    thumb = thumb,
                                    url = href,
                                    baseUrl = baseUrl
                                )
                            )
                        }
                    }
                } catch (_: Exception) {
                    // ignorar
                }
            }
        }

        val deduped = videos.distinctBy { it.video.videoId.ifBlank { it.video.url } }
        println( "Videos después de deduplicar: ${deduped.size}, scrapedVideos.size= ${videos.size}")
        println( "DEBUG INFO::anchors= ${doc.select("a[href*=/videos/]").size}")


    } catch (e: Exception) {
        println( "Error al scrapear xHamster: ${e.message}")
    }
}