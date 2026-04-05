package com.mackenzie.downhub.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mackenzie.downhub.data.local.room.entity.FavoriteItemEntity
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe

@Dao
interface FavoriteDao {

    @Query("SELECT * FROM FavoriteItem")
    fun getFavorites(): Flowable<List<FavoriteItemEntity>>

    @Query("SELECT * FROM FavoriteItem WHERE id = :id")
    fun getFavoriteById(id: Int): Maybe<FavoriteItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(item: FavoriteItemEntity)

    @Delete
    fun deleteFavorite(item: FavoriteItemEntity)

    @Query("DELETE FROM FavoriteItem")
    fun clear()
}
