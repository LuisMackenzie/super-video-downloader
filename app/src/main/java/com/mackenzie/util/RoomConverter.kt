package com.mackenzie.util

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.mackenzie.data.local.room.entity.VideoInfo

class RoomConverter {

    @TypeConverter
    fun convertJsonToVideo(json: String): VideoInfo {
        return Gson().fromJson(json, VideoInfo::class.java)
    }

    @TypeConverter
    fun convertListVideosToJson(video: VideoInfo): String {
        return Gson().toJson(video)
    }
}