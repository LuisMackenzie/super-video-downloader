package com.mackenzie.downhub.data.local

import com.mackenzie.downhub.data.local.room.dao.FavoriteDao
import com.mackenzie.downhub.data.local.room.entity.FavoriteItemEntity
import com.mackenzie.downhub.data.repository.FavoritesRepository
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesLocalDataSource @Inject constructor(
    private val favoriteDao: FavoriteDao,
) : FavoritesRepository {

    override fun getAllFavorites(): Flowable<List<FavoriteItemEntity>> {
        return favoriteDao.getFavorites()
    }

    override fun getFavoriteById(id: Int): Maybe<FavoriteItemEntity> {
        return favoriteDao.getFavoriteById(id)
    }

    override fun saveFavorite(item: FavoriteItemEntity) {
        favoriteDao.insertFavorite(item)
    }

    override fun deleteFavorite(item: FavoriteItemEntity) {
        favoriteDao.deleteFavorite(item)
    }

    override fun deleteAllFavorites() {
        favoriteDao.clear()
    }
}
