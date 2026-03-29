package com.mackenzie.downhub.data.local.model.hub

data class VideoItem(
    val id: Int,
    val title: String,
    val thumb: String,
    val url: String,
    val videoItemType: VideoItemType,
    val status: ServerStatus, // Pair<isOffline, isFullyFunctional>
    val description: String
)