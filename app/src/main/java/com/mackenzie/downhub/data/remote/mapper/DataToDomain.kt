package com.mackenzie.downhub.data.remote.mapper

import com.mackenzie.downhub.data.remote.models.videohub.CategoriesResponse
import com.mackenzie.downhub.data.remote.models.videohub.CategoryResponse
import com.mackenzie.downhub.data.remote.models.videohub.DeletedVideo
import com.mackenzie.downhub.data.remote.models.videohub.DetailedStarInfo
import com.mackenzie.downhub.data.remote.models.videohub.EmbedInfo
import com.mackenzie.downhub.data.remote.models.videohub.StarsDetailedResponse
import com.mackenzie.downhub.data.remote.models.videohub.StarsResponse
import com.mackenzie.downhub.data.remote.models.videohub.TagItem
import com.mackenzie.downhub.data.remote.models.videohub.TagsResponse
import com.mackenzie.downhub.data.remote.models.videohub.VideoActiveResponse
import com.mackenzie.downhub.data.remote.models.videohub.VideoByIdResponse
import com.mackenzie.downhub.data.remote.models.videohub.VideoDetails
import com.mackenzie.downhub.data.remote.models.videohub.VideoDetailsById
import com.mackenzie.downhub.data.remote.models.videohub.VideoEmbedCodeResponse
import com.mackenzie.downhub.data.remote.models.videohub.VideoResponse
import com.mackenzie.downhub.data.remote.models.videohub.VideoSearchResponse
import com.mackenzie.downhub.data.remote.models.videohub.VideosDeletedResponse
import com.mackenzie.downhub.domain.video.ActiveInfo
import com.mackenzie.downhub.domain.video.CategoriesItem
import com.mackenzie.downhub.domain.video.CategoryItem
import com.mackenzie.downhub.domain.video.DeletedInfoItem
import com.mackenzie.downhub.domain.video.DeletedVideoItem
import com.mackenzie.downhub.domain.video.DetailedStarDomainItem
import com.mackenzie.downhub.domain.video.DetailedStarInfoItem
import com.mackenzie.downhub.domain.video.EmbedInfoItem
import com.mackenzie.downhub.domain.video.StarBasicDomainInfo
import com.mackenzie.downhub.domain.video.StarDomainItem
import com.mackenzie.downhub.domain.video.StarInfoItem
import com.mackenzie.downhub.domain.video.StarItem
import com.mackenzie.downhub.domain.video.StarsDetailedItems
import com.mackenzie.downhub.domain.video.StarsItems
import com.mackenzie.downhub.domain.video.TagDomainInfo
import com.mackenzie.downhub.domain.video.TagDomainItem
import com.mackenzie.downhub.domain.video.TagsResponseItem
import com.mackenzie.downhub.domain.video.ThumbItem
import com.mackenzie.downhub.domain.video.VideoActiveItems
import com.mackenzie.downhub.domain.video.VideoByIdItem
import com.mackenzie.downhub.domain.video.VideoDetailsByIdItem
import com.mackenzie.downhub.domain.video.VideoDomainItem
import com.mackenzie.downhub.domain.video.VideoEmbedCodeItem
import com.mackenzie.downhub.domain.video.VideoItemDetails
import com.mackenzie.downhub.domain.video.VideoListItem
import com.mackenzie.downhub.domain.video.VideosDeletedItem
import kotlin.collections.map


fun VideoSearchResponse.toDomainModel(): VideoListItem =
    VideoListItem(
        videos = videos.map { it.toDomainModel() },
        count = count
    )

private fun VideoResponse.toDomainModel() =
    VideoDomainItem(
        video = video.toDomainModel()
    )

private fun VideoDetails.toDomainModel() =
    VideoItemDetails(
        videoId = videoId,
        title = title,
        thumb = thumb,
        url = url,
        embedUrl = embedUrl,
        publishDate = publishDate,
        rating = rating,
        ratings = ratings,
        views = views,
        duration = duration,
        defaultThumb = defaultThumb,
        type = "VIDEO",
        thumbs = thumbs?.map {
            ThumbItem(
                size = it.size,
                width = it.width,
                height = it.height,
                src = it.src
            )
        },
        tags = tags?.map {
            TagDomainInfo(tagName = it.tagName)
        },
        stars = stars?.map {
            StarItem(
                star = StarInfoItem(
                    starName = it.star.starName,
                    starThumb = it.star.starThumb ?: ""
                )
            )
        }
    )

fun CategoriesResponse.toDomainModel(): CategoriesItem =
    CategoriesItem(categories = categories.toDomainCategoryModel(), count = count)

private fun List<CategoryResponse>.toDomainCategoryModel(): List<CategoryItem> =
    this.map { CategoryItem(category = it.category) }

fun TagsResponse.toDomainModel(): TagsResponseItem =
    TagsResponseItem(tags = tags.toDomainTagItemModel(), count = count)

private fun List<TagItem>.toDomainTagItemModel(): List<TagDomainItem> =
    this.map { TagDomainItem(tag = TagDomainInfo(tagName = it.tag.tagName)) }

fun StarsResponse.toDomainModel(): StarsItems =
    StarsItems(
        stars = stars.map {
            StarDomainItem(
                star = StarBasicDomainInfo(
                    starName = it.star.starName,
                    starThumb = it.star.starThumb
                )
            )
        },
        count = count)


/*private fun StarBasicInfo.toDomainModel(): StarBasicDomainInfo =
    StarBasicDomainInfo(starName = starName, starThumb = starThumb)  */

fun StarsDetailedResponse.toDomainModel(): StarsDetailedItems =
    StarsDetailedItems(
        stars = stars.map { it.star.toDomainModel() },
        count = count
    )

private fun DetailedStarInfo.toDomainModel(): DetailedStarDomainItem =
    DetailedStarDomainItem(
        star = DetailedStarInfoItem(
            starName = starName,
            starThumb = starThumb,
            starUrl = starUrl,
            videosCountAll = videosCountAll
        )
    )

fun VideoActiveResponse.toDomainModel(): VideoActiveItems =
    VideoActiveItems(active = ActiveInfo(active.active))

fun VideoByIdResponse.toDomainModel(): VideoByIdItem =
    VideoByIdItem(video = video.toDomainModel())

private fun VideoDetailsById.toDomainModel(): VideoDetailsByIdItem =
    VideoDetailsByIdItem(
        videoId = videoId,
        title = title,
        thumb = thumb,
        url = url,
        embedUrl = embedUrl,
        publishDate = publishDate,
        rating = rating,
        ratings = ratings,
        views = views,
        duration = duration,
        defaultThumb = defaultThumb,
        thumbs = thumbs?.map {
            ThumbItem(
                size = it.size,
                width = it.width,
                height = it.height,
                src = it.src
            )
        },
        tags = tags?.map {
            TagDomainItem(
                tag = TagDomainInfo(it.tagName)
            )
        },
        stars = stars?.map {
            StarItem(
                star = StarInfoItem(
                    starName = it.star.starName,
                    starThumb = it.star.starThumb ?: ""
                )
            )
        }
    )

fun VideoEmbedCodeResponse.toDomainModel(): VideoEmbedCodeItem =
    VideoEmbedCodeItem(embed = embed.toDomainModel())

private fun EmbedInfo.toDomainModel()= EmbedInfoItem(
    code = code,
)

fun VideosDeletedResponse.toDomainModel(): VideosDeletedItem =
    VideosDeletedItem(deleted = DeletedInfoItem(
        count = deleted.count,
        videos = deleted.videos.map { it.toDomainModel() })
    )

private fun DeletedVideo.toDomainModel(): DeletedVideoItem =
    DeletedVideoItem(
        videoId = videoId,
        deleted = deleted
    )
