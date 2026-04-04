package com.mackenzie.downhub.domain.providers

import com.mackenzie.downhub.domain.ServerStatus
import com.mackenzie.downhub.domain.VideoItem
import com.mackenzie.downhub.domain.VideoItemType

fun getHentaiServers() = (200..219).map {
    VideoItem(
        it,
        getNameById(it),
        getImageFromServerId(it).ifBlank { "https://loremflickr.com/400/400/girl?lock=$it" },
        getServerUrlById(it),
        VideoItemType.SERVER,
        getServerStatus(it),
        "Generic Description $it"
    )
}

fun getLiveCamsServers() = (250..258).map {
    VideoItem(
        it,
        getNameById(it),
        getImageFromServerId(it).ifBlank { "https://loremflickr.com/400/400/girl?lock=$it" },
        getServerUrlById(it),
        VideoItemType.SERVER,
        ServerStatus(),
        "Generic Description $it"
    )
}

fun getVideoServers() = (1..29).map {
    VideoItem(
        it,
        getNameById(it),
        getImageFromServerId(it).ifBlank { "https://loremflickr.com/400/400/girl?lock=$it" },
        getServerUrlById(it),
        VideoItemType.SERVER,
        getServerStatus(it),
        "Generic Description of ${getNameById(it)}"
    )
}