package com.mackenzie.downhub.data.local.model.hub.video

data class VideosDeletedItem(
    val deleted: DeletedInfoItem
)

data class DeletedInfoItem(
    val count: Int,
    val videos: List<DeletedVideoItem>
)

data class DeletedVideoItem(
    val videoId: String,
    val deleted: String
)
