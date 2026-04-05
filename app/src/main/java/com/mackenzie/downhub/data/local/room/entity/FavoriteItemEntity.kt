package com.mackenzie.downhub.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mackenzie.downhub.domain.ServerStatus
import com.mackenzie.downhub.domain.VideoItem
import com.mackenzie.downhub.domain.VideoItemType

@Entity(tableName = "FavoriteItem")
data class FavoriteItemEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val thumb: String,
    val url: String,
    val videoItemType: String,
    val isOffline: Boolean,
    val canChargeList: Boolean,
    val isFullyFunctional: Boolean,
    val description: String,
) {

    fun toDomain(): VideoItem = VideoItem(
        id = id,
        title = title,
        thumb = thumb,
        url = url,
        videoItemType = VideoItemType.valueOf(videoItemType),
        status = ServerStatus(
            isOffline = isOffline,
            canChargeList = canChargeList,
            isFullyFunctional = isFullyFunctional,
        ),
        description = description,
    )

    companion object {
        fun fromDomain(item: VideoItem): FavoriteItemEntity = FavoriteItemEntity(
            id = item.id,
            title = item.title,
            thumb = item.thumb,
            url = item.url,
            videoItemType = item.videoItemType.name,
            isOffline = item.status.isOffline,
            canChargeList = item.status.canChargeList,
            isFullyFunctional = item.status.isFullyFunctional,
            description = item.description,
        )
    }
}
