package com.mackenzie.downhub.ui.main.videohub.player

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mackenzie.downhub.ui.main.videohub.common.getExternalPlayerMode
import com.mackenzie.downhub.ui.main.videohub.common.hideSystemBars
import com.mackenzie.downhub.ui.main.videohub.common.launchExternalPlayer
import com.mackenzie.downhub.ui.main.videohub.common.restoreSystemBars
import com.mackenzie.downhub.ui.main.videohub.common.shouldOpenDirectly

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VideoPlayerScreenContent(
    videoId: String = "",
    videoUrl: String = "",
    embedUrl: String = "",
    onBack: (() -> Unit)? = null,
    vm: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val state by vm.state.collectAsStateWithLifecycle()
    val choosenPlayer = remember { context.getExternalPlayerMode() }

    // Launcher que detecta cuando el usuario vuelve desde el reproductor externo
    val externalPlayerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // Se dispara siempre al volver de la app externa, independientemente del resultado
        Log.e("VideoPlayerScreenContent", "Returned from external player -> invoking onBack()")
        onBack?.invoke()
    }

    // Interceptar el botón físico / gesto de volver atrás
    BackHandler {
        Log.e("BackHandler", "BackHandler::Back pressed in PlayerScreen")
        onBack?.invoke()
    }

    // Ocultar barras al entrar y restaurarlas al salir del player
    DisposableEffect(Unit) {
        (activity)?.hideSystemBars()

        onDispose {
            (activity)?.restoreSystemBars()
        }
    }

    LaunchedEffect(videoUrl) {
        val primaryUrl = videoUrl.trim()
        val fallbackUrl = embedUrl.trim()

        if (primaryUrl.isBlank() && fallbackUrl.isBlank()) return@LaunchedEffect

        if (primaryUrl.shouldOpenDirectly()) {
            Log.e("VideoPlayerScreenContent", "Fetching video sample launch")
            vm.getVideoFromSampleUrl(primaryUrl)
        } else {
            Log.e("VideoPlayerScreenContent", "Fetching COMPLEX launch")
            vm.getVideoFromEmbeddedUrl(primaryUrl, fallbackUrl)
        }
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        state.error != null -> {
            Log.e("VideoPlayerScreenContent", "Error: ${state.error}")
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: ${state.error}")
            }
            Toast.makeText( context, "Error=${state.error}", Toast.LENGTH_SHORT).show()

        }
        state.resolvedResult != null -> {

            state.resolvedResult?.let { result ->
                if(choosenPlayer) {
                    LaunchedEffect(result) {
                        context.launchExternalPlayer(
                            launcher = externalPlayerLauncher,
                            videoUrl = result.mediaUrl,
                            headers = result.playbackHeaders
                        )
                        // Opcional: Cerrar la pantalla actual si no quieres que quede en negro detrás
                        // onBack?.invoke()
                    }

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Abriendo en reproductor externo...")
                    }
                } else {
                    VideoPlayer(
                        resolvedResult = result,
                        isDownloading = state.isDownloading,
                        downloadProgress = state.downloadProgress,
                        downloadError = state.downloadError,
                        onOkHttpDownload = { url -> vm.startOkHttpDownload(url) },
                    )
                }
            }
        }
    }
}
