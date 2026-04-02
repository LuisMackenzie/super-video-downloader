package com.mackenzie.downhub.ui.main.videohub.player

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.mackenzie.downhub.data.embed.EmbeddedVideoResolveError
import com.mackenzie.downhub.domain.video.embed.EmbeddedVideoResolveResult
import com.mackenzie.downhub.usecases.player.ResolveEmbeddedVideoUrlUseCase
import com.mackenzie.downhub.util.proxy_utils.OkHttpProxyClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val resolveEmbeddedVideoUrlUseCase: ResolveEmbeddedVideoUrlUseCase,
    private val okHttpClient: OkHttpProxyClient,
    @ApplicationContext private val appContext: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(PlayerUiState())
    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

    fun getVideoFromEmbeddedUrl(url: String, embedUrl: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, resolvedResult = null) }

            val primaryUrl = url.trim()
            val fallbackUrl = embedUrl.trim()

            val result = resolveEmbeddedVideoUrlUseCase(primaryUrl, okHttpClient.getProxyOkHttpClient())
                .fold(
                    ifLeft = { firstError ->
                        if (fallbackUrl.isNotBlank() && fallbackUrl != primaryUrl) {
                            resolveEmbeddedVideoUrlUseCase(fallbackUrl, okHttpClient.getProxyOkHttpClient())
                        } else {
                            Either.Left(firstError)
                        }
                    },
                    ifRight = { resolved -> Either.Right(resolved) }
                )

            result.fold(
                ifLeft = { err ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            resolvedResult = null,
                            error = err.toHumanMessage()
                        )
                    }
                },
                ifRight = { resolved ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            resolvedResult = resolved,
                            error = null
                        )
                    }
                }
            )
        }
    }

    fun getVideoFromSampleUrl(url: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null, resolvedResult = null) }
            delay(1000)
            val type = if (url.contains(".m3u8")) {
                EmbeddedVideoResolveResult.MediaType.HLS
            } else if (url.contains(".mpd")) {
                EmbeddedVideoResolveResult.MediaType.DASH
            } else {
                EmbeddedVideoResolveResult.MediaType.MP4
            }
            val result = EmbeddedVideoResolveResult(
                mediaUrl = url,
                mediaType = type,
                playbackHeaders = emptyMap(),
                resolvedFromUrl = url
            )
            _state.update {
                it.copy(isLoading = false, resolvedResult = result, error = null)
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Descarga con OkHttp (Opción B) — progreso en tiempo real
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Descarga [url] usando OkHttp en una coroutine del ViewModel.
     * Actualiza [PlayerUiState.downloadProgress] mientras avanza
     * y pone [PlayerUiState.isDownloading] en `false` al terminar.
     */
    fun startOkHttpDownload(url: String) {
        if (_state.value.isDownloading) return   // evitar descargas duplicadas
        viewModelScope.launch {
            _state.update { it.copy(isDownloading = true, downloadProgress = 0, downloadError = null) }

            downloadWithOkHttp(url, okHttpClient.getProxyOkHttpClient(), appContext).collect { event ->
                when (event) {
                    is Int -> _state.update { it.copy(downloadProgress = event) }
                    is DownloadResult.Success -> _state.update {
                        it.copy(
                            isDownloading = false,
                            downloadProgress = 100,
                            downloadError = null,
                        )
                    }
                    is DownloadResult.Error -> _state.update {
                        it.copy(
                            isDownloading = false,
                            downloadProgress = null,
                            downloadError = event.message,
                        )
                    }
                }
            }
        }
    }

    private fun EmbeddedVideoResolveError.toHumanMessage(): String = when (this) {
        is EmbeddedVideoResolveError.Http -> "HTTP $code: $message"
        is EmbeddedVideoResolveError.Network -> message
        is EmbeddedVideoResolveError.NotFound -> message
        is EmbeddedVideoResolveError.RequiresJavaScript -> message
    }

    data class PlayerUiState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val resolvedResult: EmbeddedVideoResolveResult? = null,
        val isDownloading: Boolean = false,
        val downloadProgress: Int? = null,   // 0-100; null = descarga no iniciada/terminada
        val downloadError: String? = null,
    )
}
