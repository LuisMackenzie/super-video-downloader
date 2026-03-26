package com.mackenzie.downhub.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mackenzie.downhub.data.local.room.dao.ConfigDao
import com.mackenzie.downhub.data.local.room.dao.HistoryDao
import com.mackenzie.downhub.data.local.room.dao.PageDao
import com.mackenzie.downhub.data.local.room.dao.ProgressDao
import com.mackenzie.downhub.data.local.room.dao.VideoDao
import com.mackenzie.downhub.data.local.room.entity.DownloadUrlsConverter
import com.mackenzie.downhub.data.local.room.entity.FormatsConverter
import com.mackenzie.downhub.data.local.room.entity.HistoryItem
import com.mackenzie.downhub.data.local.room.entity.PageInfo
import com.mackenzie.downhub.data.local.room.entity.ProgressInfo
import com.mackenzie.downhub.data.local.room.entity.SupportedPage
import com.mackenzie.downhub.data.local.room.entity.VideoInfo

const val DB_VERSION = 8

@Database(
    entities = [PageInfo::class, SupportedPage::class, VideoInfo::class, ProgressInfo::class, HistoryItem::class],
    version = DB_VERSION,
)
@TypeConverters(FormatsConverter::class, DownloadUrlsConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun configDao(): ConfigDao

    abstract fun videoDao(): VideoDao

    abstract fun progressDao(): ProgressDao

    abstract fun pageDao(): PageDao

    abstract fun historyDao(): HistoryDao
}