package com.mackenzie.downhub.data.repos

import arrow.core.Either
import com.mackenzie.downhub.data.datasources.VideoHubRemoteDataSource
import com.mackenzie.downhub.domain.Error
import com.mackenzie.downhub.domain.providers.getEmbedUrl
import com.mackenzie.downhub.domain.video.TagDomainInfo
import com.mackenzie.downhub.domain.video.ThumbItem
import com.mackenzie.downhub.domain.video.VideoDomainItem
import com.mackenzie.downhub.domain.video.VideoItemDetails
import com.mackenzie.downhub.domain.video.VideoListItem
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject

private const val BEEG_SERVER_ID = 3
private const val BEEG_BASE_URL = "https://beeg.com"
private const val BEEG_STORE_URL = "https://store.externulls.com"
private const val BEEG_LIST_PATH = "/tag/videos/checklater"
private const val BEEG_PAGE_SIZE = 30
private const val BEEG_DEFAULT_THUMB_WIDTH = 640
private const val BEEG_DEFAULT_THUMB_HEIGHT = 360

@JsonClass(generateAdapter = true)
private data class BeegVideoListEntry(
    @Json(name = "file") val file: BeegFile?,
    @Json(name = "fc_facts") val facts: List<BeegFact>? = null,
    @Json(name = "fc_start") val clipStart: Int? = null,
    @Json(name = "fc_end") val clipEnd: Int? = null
)

@JsonClass(generateAdapter = true)
private data class BeegFile(
    @Json(name = "id") val id: Long?,
    @Json(name = "fl_duration") val durationSeconds: Int? = null,
    @Json(name = "fl_width") val width: Int? = null,
    @Json(name = "fl_height") val height: Int? = null,
    @Json(name = "hls_resources") val hlsResources: BeegHlsResources? = null,
    @Json(name = "data") val metadata: List<BeegFileData>? = null
)

@JsonClass(generateAdapter = true)
private data class BeegHlsResources(
    @Json(name = "fl_cdn_multi") val multi: String? = null,
    @Json(name = "fl_cdn_1080") val p1080: String? = null,
    @Json(name = "fl_cdn_720") val p720: String? = null,
    @Json(name = "fl_cdn_480") val p480: String? = null,
    @Json(name = "fl_cdn_360") val p360: String? = null,
    @Json(name = "fl_cdn_240") val p240: String? = null
)

@JsonClass(generateAdapter = true)
private data class BeegFileData(
    @Json(name = "cd_column") val column: String? = null,
    @Json(name = "cd_value") val value: String? = null
)

@JsonClass(generateAdapter = true)
private data class BeegFact(
    @Json(name = "fc_st_views") val views: Long? = null,
    @Json(name = "reactions_count") val reactionsCount: Long? = null,
    @Json(name = "reactions_count_unreg") val unregisteredReactionsCount: Long? = null
)

class BeegRepository @Inject constructor(
    // val remoteDataSource: VideoHubRemoteDataSource,
    // val okHttpClient: OkHttpClient,
    val moshi: Moshi
) {

    suspend fun beegVideoScrapper(serverUrl: String, okHttpClient: OkHttpClient): Either<Error, VideoListItem> =
        withContext(Dispatchers.IO) {
            val page = extractPageNumber(serverUrl)
            val offset = ((page - 1).coerceAtLeast(0)) * BEEG_PAGE_SIZE
            val apiUrl = "$BEEG_STORE_URL$BEEG_LIST_PATH?limit=$BEEG_PAGE_SIZE&offset=$offset"

            val request = Request.Builder()
                .url(apiUrl)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Language", "en-US,en;q=0.9")
                .header("Referer", "$BEEG_BASE_URL/")
                .header("Origin", BEEG_BASE_URL)
                .get()
                .build()

            try {
                okHttpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        return@withContext Either.Left(Error.Server(response.code))
                    }

                    val body = response.body?.string()
                        ?: return@withContext Either.Left(Error.Unknown("Empty response body"))

                    val listType = Types.newParameterizedType(List::class.java, BeegVideoListEntry::class.java)
                    val adapter = moshi.adapter<List<BeegVideoListEntry>>(listType)
                    val parsed = runCatching { adapter.fromJson(body) }.getOrNull()
                        ?: return@withContext Either.Left(Error.Unknown("Failed to parse Beeg listing response"))

                    if (parsed.isEmpty()) {
                        return@withContext Either.Left(Error.Unknown("No videos returned by Beeg listing (page $page)"))
                    }

                    val videos = parsed
                        .mapNotNull { it.toDomainItem() }
                        .distinctBy { it.video.videoId }

                    if (videos.isEmpty()) {
                        return@withContext Either.Left(Error.Unknown("Beeg returned items but none could be mapped (page $page)"))
                    }

                    Either.Right(VideoListItem(videos = videos, count = videos.size))
                }
            } catch (e: IOException) {
                Either.Left(Error.Connectivity)
            } catch (e: Exception) {
                Either.Left(Error.Unknown(e.message ?: "Unknown error fetching Beeg videos"))
            }
        }

    private fun extractPageNumber(url: String): Int {
        val pageParam = Regex("[?&]page=(\\d+)").find(url)?.groupValues?.getOrNull(1)
        if (pageParam != null) return pageParam.toIntOrNull() ?: 1

        val lastSegment = url.trimEnd('/').substringAfterLast('/')
        return lastSegment.toIntOrNull() ?: 1
    }
}

private fun Int.secondsToMinutes(): String {
    val totalSeconds = toLong()
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(java.util.Locale.US, "%d:%02d", minutes, seconds)
}

private fun BeegVideoListEntry.toDomainItem(): VideoDomainItem? {
    val fileModel = file ?: return null
    val fileId = fileModel.id?.toString()?.takeIf { it.isNotBlank() } ?: return null
    val title = fileModel.metadata
        .orEmpty()
        .firstOrNull { it.column == "sf_name" }
        ?.value
        ?.trim()
        .orEmpty()
        .ifBlank { "Beeg video $fileId" }

    val pageUrl = "$BEEG_BASE_URL/-0$fileId"
    val embedUrl = getEmbedUrl(3, fileId)
    val playbackUrl = fileModel.hlsResources.toPlaybackUrl()
    val thumbUrl = buildBeegThumbUrl(fileId)
    val duration = fileModel.durationSeconds?.secondsToMinutes().orEmpty()
    val views = facts.orEmpty().firstNotNullOfOrNull { it.views }?.toString().orEmpty()
    val ratingsCount = facts.orEmpty().firstOrNull()?.let {
        ((it.reactionsCount ?: 0) + (it.unregisteredReactionsCount ?: 0)).toString()
    } ?: "0"
    val tags = buildBeegTags(duration, fileModel.width, fileModel.height)

    return VideoDomainItem(
        video = VideoItemDetails(
            videoId = fileId,
            title = title,
            thumb = thumbUrl,
            url = playbackUrl ?: pageUrl,
            embedUrl = embedUrl,
            publishDate = "",
            rating = "",
            ratings = ratingsCount,
            views = views,
            duration = duration,
            defaultThumb = thumbUrl,
            type = "video",
            thumbs = if (thumbUrl.isNotEmpty()) {
                listOf(
                    ThumbItem(
                        size = "medium",
                        width = BEEG_DEFAULT_THUMB_WIDTH.toString(),
                        height = BEEG_DEFAULT_THUMB_HEIGHT.toString(),
                        src = thumbUrl
                    )
                )
            } else {
                emptyList()
            },
            tags = tags,
            stars = null
        )
    )
}

private fun BeegHlsResources?.toPlaybackUrl(): String? {
    val rawPath = this?.multi
        ?: this?.p1080
        ?: this?.p720
        ?: this?.p480
        ?: this?.p360
        ?: this?.p240
        ?: return null

    return "https://video.beeg.com/$rawPath"
}

private fun buildBeegThumbUrl(fileId: String): String {
    return "$BEEG_STORE_URL".replace("store", "thumbs") +
            "/videos/$fileId/0.webp?size=${BEEG_DEFAULT_THUMB_WIDTH}x${BEEG_DEFAULT_THUMB_HEIGHT}"
}

private fun buildBeegTags(duration: String, width: Int?, height: Int?): List<TagDomainInfo>? {
    val tags = buildList {
        if (duration.isNotBlank()) add(TagDomainInfo(duration))
        if (width != null && height != null) add(TagDomainInfo("${width}x$height"))
        add(TagDomainInfo("Beeg"))
    }.distinctBy { it.tagName }

    return tags.takeIf { it.isNotEmpty() }
}
