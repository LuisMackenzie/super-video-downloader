package com.mackenzie.downhub.data.embed

import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request

internal fun fetchHtml(url: String, okHttpClient: OkHttpClient): Pair<String, String?> {
    val request = Request.Builder()
        .url(url)
        .headers(
            Headers.Builder()
                .add("User-Agent", "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .add("Accept-Language", "es-ES,es;q=0.9,en-US;q=0.8,en;q=0.7")
                .add("Cache-Control", "no-cache")
                .add("Pragma", "no-cache")
                .add("Upgrade-Insecure-Requests", "1")
                .add("Sec-Fetch-Dest", "document")
                .add("Sec-Fetch-Mode", "navigate")
                .add("Sec-Fetch-Site", "none")
                .build()
        )
        .get()
        .build()

    okHttpClient.newCall(request).execute().use { resp ->
        val body = resp.body?.string().orEmpty()
        val finalUrl = resp.request.url.toString()
        val err = if (!resp.isSuccessful) "HTTP ${resp.code}" else null
        return body to (err?.let { "$it ($finalUrl)" })
    }
}

internal fun extractVideoIdFromHref(href: String): String {
    return when {
        href.contains("viewkey=") -> href.substringAfter("viewkey=").substringBefore("&").substringBefore("?")
        href.contains("v=") -> href.substringAfter("v=").substringBefore("&").substringBefore("?")
        href.contains("/watch/") -> href.substringAfter("/watch/").substringBefore("/").substringBefore("?") // pornslash.com: /watch/{id}
        href.contains("/video/") -> href.substringAfter("/video/").substringBefore("/").substringBefore("?")
        href.contains("/video-") -> href.substringAfter("/video-").substringBefore("/").substringBefore("?")
        href.contains("/v/") -> href.substringAfter("/v/").substringBefore("/").substringBefore("?")
        href.matches(Regex(".*/\\d+.*")) -> {
            val segments = href.split("/").filter { it.isNotEmpty() }
            segments.find { it.matches(Regex("\\d+")) } ?: ""
        }
        else -> {
            println("No se pudo extraer ID, href=$href")
            "ID_NOT_FOUND"
        }
    }
}

internal fun extractXhamsterIdFromUrl(url: String): String {
    // Ejemplos comunes: /videos/slug-12345678 o /videos/12345678/...
    val numeric = Regex("(?:-|/)(\\d{4,})(?:\\b|/|\\?|$)").find(url)?.groupValues?.get(1)
    return numeric ?: extractVideoIdFromHref(url)
}