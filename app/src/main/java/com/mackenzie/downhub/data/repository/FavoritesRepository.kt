package com.mackenzie.downhub.data.repository

import com.mackenzie.downhub.data.local.room.entity.FavoriteItemEntity
import com.mackenzie.downhub.di.qualifier.LocalData
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject
import javax.inject.Singleton

interface FavoritesRepository {

    fun getAllFavorites(): Flowable<List<FavoriteItemEntity>>

    fun getFavoriteById(id: Int): Maybe<FavoriteItemEntity>

    fun saveFavorite(item: FavoriteItemEntity)

    fun deleteFavorite(item: FavoriteItemEntity)

    fun deleteAllFavorites()
}

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    @param:LocalData private val localDataSource: FavoritesRepository,
) : FavoritesRepository {

    override fun getAllFavorites(): Flowable<List<FavoriteItemEntity>> {
        return localDataSource.getAllFavorites()
    }

    override fun getFavoriteById(id: Int): Maybe<FavoriteItemEntity> {
        return localDataSource.getFavoriteById(id)
    }

    override fun saveFavorite(item: FavoriteItemEntity) {
        localDataSource.saveFavorite(item)
    }

    override fun deleteFavorite(item: FavoriteItemEntity) {
        localDataSource.deleteFavorite(item)
    }

    override fun deleteAllFavorites() {
        localDataSource.deleteAllFavorites()
    }
}
