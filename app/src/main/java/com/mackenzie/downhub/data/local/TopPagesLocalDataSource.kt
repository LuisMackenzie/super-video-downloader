package com.mackenzie.downhub.data.local

import android.net.Uri
import com.mackenzie.downhub.data.local.room.dao.PageDao
import com.mackenzie.downhub.data.local.room.entity.PageInfo
import com.mackenzie.downhub.data.repository.TopPagesRepository
import com.mackenzie.downhub.util.SharedPrefHelper
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TopPagesLocalDataSource @Inject constructor(
    private val pageDao: PageDao,
    private val sharedPrefHelper: SharedPrefHelper
) : TopPagesRepository {

    override suspend fun getTopPages(): List<PageInfo> {
        val localBookmarks = pageDao.getPageInfos().blockingFirst(emptyList())
        if (localBookmarks.isEmpty()) {
            val isFirstStart = sharedPrefHelper.getIsFirstStart()
            if (isFirstStart) {
                val defaultList = getDefaultBookmarks()
                pageDao.insertAllProgressInfo(defaultList)

                return defaultList
            }
        }

        return localBookmarks
    }

    override fun saveTopPage(pageInfo: PageInfo) {
        pageDao.insertProgressInfo(pageInfo)
    }

    override fun replaceBookmarksWith(pageInfos: List<PageInfo>) {
        pageDao.deleteAll()
        pageDao.insertAllProgressInfo(pageInfos)
    }

    override fun deletePageInfo(pageInfo: PageInfo) {
        pageDao.deleteProgressInfo(pageInfo)
    }

    override suspend fun updateLocalStorageFavicons(): Flow<PageInfo> {
        throw NotImplementedError("NO NEED, HANDLED BY REPO")
    }

    private fun getDefaultBookmarks(): List<PageInfo> {
        val defaultList = arrayListOf<PageInfo>()

        defaultList.add(PageInfo(link = "https://es.pornhub.com/video"))
        defaultList.add(PageInfo(link = "https://es.redtube.com/newest"))
        defaultList.add(PageInfo(link = "https://beeg.com"))
        defaultList.add(PageInfo(link = "https://www.eporner.com"))
        defaultList.add(PageInfo(link = "https://tube8.com"))
        defaultList.add(PageInfo(link = "https://xhamster.com/best/daily"))
        defaultList.add(PageInfo(link = "https://www.youjizz.com"))
        defaultList.add(PageInfo(link = "https://www.youporn.com/most_viewed"))
        defaultList.add(PageInfo(link = "https://www.xvideos.com/"))
        defaultList.add(PageInfo(link = "https://www.xnxx.es/todays-selection"))
        defaultList.add(PageInfo(link = "https://www.tnaflix.com/new"))
        defaultList.add(PageInfo(link = "https://blowjobs.pro"))
        defaultList.add(PageInfo(link = "https://es.spankbang.com/"))
        defaultList.add(PageInfo(link = "https://www.porntrex.com"))
        defaultList.add(PageInfo(link = "https://hqporner.com"))
        defaultList.add(PageInfo(link = "https://www.analdin.com/"))
        defaultList.add(PageInfo(link = "https://www.xxxfiles.com/latest-updates/"))
        defaultList.add(PageInfo(link = "https://www.pornslash.com"))
        defaultList.add(PageInfo(link = "https://watchporn.to/"))
        defaultList.add(PageInfo(link = "https://taboodude.com/"))
        defaultList.add(PageInfo(link = "https://www.freeuseporn.com/"))
        defaultList.add(PageInfo(link = "https://pornxp.ph/"))
        defaultList.add(PageInfo(link = "https://neporn.com/latest-updates"))
        defaultList.add(PageInfo(link = "https://www.theyarehuge.com"))
        defaultList.add(PageInfo(link = "https://www.superporn.com/es"))
        defaultList.add(PageInfo(link = "https://www.peekvids.com/Trending-Porn"))
        defaultList.add(PageInfo(link = "https://beta.xfreehd.com/trends/es"))
        defaultList.add(PageInfo(link = "https://www.trendyporn.com"))
        defaultList.add(PageInfo(link = "https://www.youcrazyx.com"))

        return defaultList.mapIndexed { index, page ->
            page.name = Uri.parse(page.link).host.toString()
            page.order = index
            page
        }
    }
}