package com.mackenzie.downhub.ui.main.videohub.favs

import androidx.lifecycle.viewModelScope
import com.mackenzie.downhub.data.local.room.entity.FavoriteItemEntity
import com.mackenzie.downhub.data.repository.FavoritesRepository
import com.mackenzie.downhub.domain.VideoItem
import com.mackenzie.downhub.ui.main.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritesRepository: FavoritesRepository,
) : BaseViewModel() {

    private val _favorites = MutableStateFlow<List<VideoItem>>(emptyList())
    val favorites: StateFlow<List<VideoItem>> = _favorites.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    override fun start() {
        fetchFavorites()
    }

    override fun stop() {}

    private fun fetchFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesRepository.getAllFavorites()
                .subscribe { entities ->
                    val items = entities.map { it.toDomain() }
                    _favorites.value = items
                    _favoriteIds.value = items.map { it.id }.toSet()
                }
        }
    }

    fun toggleFavorite(item: VideoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val entity = FavoriteItemEntity.fromDomain(item)
            if (_favoriteIds.value.contains(item.id)) {
                favoritesRepository.deleteFavorite(entity)
            } else {
                favoritesRepository.saveFavorite(entity)
            }
        }
    }

    fun removeFavorite(item: VideoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            favoritesRepository.deleteFavorite(FavoriteItemEntity.fromDomain(item))
        }
    }
}
