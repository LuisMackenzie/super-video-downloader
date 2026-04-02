package com.mackenzie.downhub.data.remote.models.videohub

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoEmbedCodeResponse(
    @Json(name = "embed") val embed: EmbedInfo
)

@JsonClass(generateAdapter = true)
data class EmbedInfo(
    @Json(name = "code") val code: String
)

