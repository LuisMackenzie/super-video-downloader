package com.mackenzie.downhub.data.map

import com.mackenzie.downhub.domain.providers.getEmbedUrl
import com.mackenzie.downhub.domain.video.TagDomainInfo
import com.mackenzie.downhub.domain.video.ThumbItem
import com.mackenzie.downhub.domain.video.VideoDomainItem
import com.mackenzie.downhub.domain.video.VideoItemDetails

internal fun createVideoItem(
    serverId: Int,
    videoId: String,
    title: String,
    thumb: String,
    url: String,
    baseUrl: String,
    duration: String = "",
    views: String = "",
    rating: String = "",
    tags: List<TagDomainInfo>? = null
): VideoDomainItem {

    return VideoDomainItem(
        video = VideoItemDetails(
            videoId = videoId,
            title = title,
            thumb = thumb,
            url = url,
            embedUrl = fetchEmbedUrl(serverId, videoId, url),
            publishDate = "",
            rating = rating.replace("%", "").trim(),
            ratings = "0",
            views = views.trim(),
            duration = duration.trim(),
            defaultThumb = thumb,
            type = "video",
            thumbs = if (thumb.isNotEmpty()) listOf(ThumbItem("medium", "640", "480", thumb)) else emptyList(),
            tags = tags,
            stars = null
        )
    )
}

private fun fetchEmbedUrl(serverId: Int, videoId: String, url: String): String {
    val xvideosId = url.substringAfter("/video.", "").substringBefore("/").substringBefore("?")
    val youCrazyId = url.substringAfterLast("-", "").substringBefore(".html")
    val xFreeHD = url.substringAfter("/video/", "").substringBefore("/")
    val peekVids = url.substringAfterLast("/", "")
    return when (serverId) {
        9 -> getEmbedUrl(serverId, xvideosId)
        19, 27 -> getEmbedUrl(serverId, xFreeHD)
        26 -> getEmbedUrl(serverId, peekVids)
        28, 29 -> getEmbedUrl(serverId, youCrazyId)
        else -> getEmbedUrl(serverId, videoId)
    }
}