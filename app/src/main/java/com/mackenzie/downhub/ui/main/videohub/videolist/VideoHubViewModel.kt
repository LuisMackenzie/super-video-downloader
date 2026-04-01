package com.mackenzie.downhub.ui.main.videohub.videolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mackenzie.downhub.data.local.model.hub.video.VideoDomainItem
import com.mackenzie.naughtyhub.usecases.list.GetBeegVideoListUseCase
import com.mackenzie.naughtyhub.usecases.list.GetPrimaryVideoListUseCase
import com.mackenzie.naughtyhub.usecases.list.GetSecondaryVideoListUseCase
import com.mackenzie.naughtyhub.usecases.list.GetTertiaryVideoListUseCase
import com.mackenzie.naughtyhub.usecases.list.GetVideoDefaultListUseCase
import com.mackenzie.naughtyhub.usecases.list.GetVideoListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoHubViewModel @Inject constructor(
    private val getVideoListUseCase: GetVideoListUseCase,
    private val getDefaultListUseCase: GetVideoDefaultListUseCase,
    private val getPrimaryVideoListUseCase: GetPrimaryVideoListUseCase,
    private val getSecondaryVideoListUseCase: GetSecondaryVideoListUseCase,
    private val getTertiaryVideoListUseCase: GetTertiaryVideoListUseCase,
    private val getBeegVideoListUseCase: GetBeegVideoListUseCase
): ViewModel() {

    private val _state = MutableStateFlow(VideoHubUiState())
    val state: StateFlow<VideoHubUiState> = _state.asStateFlow()


    fun loadVideos(serverId: Int, serverUrl: String) {
        when (serverId) {
            2 -> getDefaultVideoList()
            3 -> getBeegVideoList(serverId, serverUrl)
            4, 9, 14 -> getSecondaryVideoList(serverId, serverUrl)
            1, 5, 6, 7, 8, 11, 12, 15, 16, 17, 50, 51, 54, 55, 66, 67, 68 -> getTertiaryVideoList(serverId, serverUrl)
            10, 13, 18, 19, in 20..29, 57, 60, 62, 65 -> getPrimaryVideoList(serverId, serverUrl)
            // 12, 16, 17, 50, 51 -> getTertiaryVideoList(serverId, serverUrl)
            88 -> { /* Mock server, no loading needed */ }
            else -> {
                getPrimaryVideoList(serverId, serverUrl)
                android.util.Log.e("VideoHubViewModel", "Unknown server ID: $serverId")
            }
        }
    }

    private fun getVideoList(
        page: Int?,
        thumbsize: String?,
        search: String?,
        tags: List<String>?,
        stars: List<String>?,
        category: String?,
        ordering: String?,
        period: String?
    ) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getVideoListUseCase(
                page, thumbsize, search, tags, stars, category, ordering, period
            ).fold(
                ifLeft = { error ->
                    _state.update { it.copy(isLoading = false, error = error.toString()) }
                },
                ifRight = { videoListItem ->
                    _state.update { it.copy(isLoading = false, videos = videoListItem.videos, error = null) }
                })
        }
    }

    private fun getDefaultVideoList() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getDefaultListUseCase().fold(
                ifLeft = { error ->
                    _state.update { it.copy(isLoading = false, error = error.toString()) }
                },
                ifRight = { videoListItem ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            videos = videoListItem.videos,
                            error = null
                        )
                    }
                }
            )
        }
    }

    private fun getPrimaryVideoList(serverId: Int, serverUrl: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getPrimaryVideoListUseCase(serverId, serverUrl).fold(
                ifLeft = { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = "Error al cargar la lista de videos.\nCausa: ${(error as? Error.Unknown)?.message}"
                    ) }
                },
                ifRight = { videoListItem ->
                    _state.update { it.copy(isLoading = false, videos = videoListItem.videos, error = null) }
                }
            )
        }
    }

    private fun getSecondaryVideoList(serverId: Int, serverUrl: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getSecondaryVideoListUseCase(serverId, serverUrl).fold(
                ifLeft = { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = "Error al cargar la lista de videos.\nCausa: ${(error as? Error.Unknown)?.message}"
                    ) }
                },
                ifRight = { videoListItem ->
                    _state.update { it.copy(isLoading = false, videos = videoListItem.videos, error = null) }
                }
            )
        }
    }

    private fun getTertiaryVideoList(serverId: Int, serverUrl: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getTertiaryVideoListUseCase(serverId, serverUrl).fold(
                ifLeft = { error ->
                    _state.update { it.copy(
                        isLoading = false,
                        error = "Error al cargar la lista de videos.\nCausa: ${(error as? Error.Unknown)?.message}"
                    ) }
                },
                ifRight = { videoListItem ->
                    _state.update { it.copy(isLoading = false, videos = videoListItem.videos, error = null) }
                }
            )
        }
    }

    private fun getBeegVideoList(serverId: Int, serverUrl: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getBeegVideoListUseCase(serverId, serverUrl).fold(
                ifLeft = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = "Error al cargar videos de Beeg.\nCausa: ${(error as? Error.Unknown)?.message ?: error}"
                        )
                    }
                },
                ifRight = { videoListItem ->
                    _state.update { it.copy(isLoading = false, videos = videoListItem.videos, error = null) }
                }
            )
        }
    }

    data class VideoHubUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val videos: List<VideoDomainItem> = emptyList()
    )


}
