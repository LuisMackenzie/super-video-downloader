package com.mackenzie.downhub.util.hub

import com.mackenzie.downhub.data.local.model.hub.ServerStatus
import com.mackenzie.downhub.data.local.model.hub.VideoItem
import com.mackenzie.downhub.data.local.model.hub.video.StarInfoItem
import com.mackenzie.downhub.data.local.model.hub.video.StarItem
import com.mackenzie.downhub.data.local.model.hub.video.TagDomainInfo
import com.mackenzie.downhub.data.local.model.hub.video.ThumbItem
import com.mackenzie.downhub.data.local.model.hub.video.VideoDomainItem
import com.mackenzie.downhub.data.local.model.hub.video.VideoItemDetails

fun getMedia() = (1..20).map {
    VideoDomainItem(
        video = VideoItemDetails(
            videoId = it.toString(),
            title = "Title $it",
            thumb = "https://loremflickr.com/400/400/girl?lock=$it",
            url = getSFWUrlVideo(it),
            // url = "https://www.porntrex.com/video/3002206/reflected",
            embedUrl = getSFWUrlVideo(it),
            // embedUrl = "https://www.porntrex.com/embed/3002206",
            publishDate = "",
            rating = "4.5",
            ratings = "4.5",
            views = (1000 * it).toString(),
            duration = "5:00",
            defaultThumb = "https://loremflickr.com/400/400/girl?lock=$it",
            type = getType(it).name,
            thumbs = listOf(
                ThumbItem(
                    size = "small",
                    width = "200",
                    height = "200",
                    src = "https://loremflickr.com/200/200/girl?lock=$it"
                )
            ),
            tags = listOf(TagDomainInfo("tag2"), TagDomainInfo("tag2")),

            stars = listOf(
                StarItem(
                    star = StarInfoItem(
                        starName = "Star 01 $it",
                        starThumb = "https://loremflickr.com/400/400/cat?lock=$it"
                    )
                ), StarItem(
                    star = StarInfoItem(
                        starName = "Star 02 $it",
                        starThumb = "https://loremflickr.com/400/400/cat?lock=$it"
                    )
                )
            )

        )
    )
}

fun getMedia2() = (1..20).map {
    VideoItem(
        it,
        "Title $it",
        "https://loremflickr.com/400/400/cat?lock=1",
        "https://loremflickr.com/400/400/girl?lock=$it",
        getType(it),
        ServerStatus(),
        "Generic Description $it"
    )
}