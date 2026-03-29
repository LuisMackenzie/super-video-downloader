package com.mackenzie.downhub.util.hub

import com.mackenzie.downhub.data.local.model.hub.ServerStatus
import com.mackenzie.downhub.data.local.model.hub.VideoItem
import com.mackenzie.downhub.data.local.model.hub.VideoItemType

fun getHentaiServers() = (50..69).map {
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

fun getLiveCamsServers() = (80..88).map {
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