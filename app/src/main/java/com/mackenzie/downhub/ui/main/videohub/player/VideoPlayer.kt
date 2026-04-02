package com.mackenzie.downhub.ui.main.videohub.player

import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.mackenzie.downhub.domain.video.embed.EmbeddedVideoResolveResult
import com.mackenzie.downhub.ui.main.videohub.common.loadVideoOnCast

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    resolvedResult: EmbeddedVideoResolveResult,
    modifier: Modifier = Modifier,
    // Estado de descarga OkHttp (Opción B) — viene del ViewModel
    isDownloading: Boolean = false,
    downloadProgress: Int? = null,
    downloadError: String? = null,
    // Callbacks hacia el ViewModel / pantalla padre
    onOkHttpDownload: (url: String) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val TAG = "VideoPlayer"
    val videoUrl = resolvedResult.mediaUrl

    // ── Dialog de selección de método de descarga ───────────────────────────
    var showDownloadDialog by remember { mutableStateOf(false) }

    // ── Runtime permission launcher (solo API 24-28) ─────────────────────────
    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onOkHttpDownload(videoUrl)
        } else {
            Toast.makeText(
                context,
                "Permiso de almacenamiento denegado. La descarga no puede continuar.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ── ExoPlayer ───────────────────────────────────────────────────────────
    val exoPlayer = remember(resolvedResult) {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setDefaultRequestProperties(resolvedResult.playbackHeaders)
            .setUserAgent(resolvedResult.playbackHeaders["User-Agent"])

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        val mimeType = when (resolvedResult.mediaType) {
            EmbeddedVideoResolveResult.MediaType.HLS -> MimeTypes.APPLICATION_M3U8
            EmbeddedVideoResolveResult.MediaType.DASH -> MimeTypes.APPLICATION_MPD
            else -> null
        }

        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl.toUri())
            .apply {
                if (mimeType != null) setMimeType(mimeType)
            }
            .build()

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build().apply {
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
    }

    // ── Mostrar error de descarga como Toast ─────────────────────────────────
    LaunchedEffect(downloadError) {
        downloadError?.let {
            Toast.makeText(context, "Error al descargar: $it", Toast.LENGTH_LONG).show()
        }
    }

    // ── Mostrar Toast al completar la descarga (progress == 100) ─────────────
    LaunchedEffect(downloadProgress) {
        if (downloadProgress == 100 && !isDownloading) {
            Toast.makeText(
                context,
                "Video descargado en Downloads/NaughtyHub/",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ── Lifecycle: pausa/reanuda la reproducción local ───────────────────────
    DisposableEffect(lifecycleOwner, exoPlayer) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> {
                    val castContext = try { CastContext.getSharedInstance(context) } catch (_: Exception) { null }
                    if (castContext?.sessionManager?.currentCastSession == null) {
                        exoPlayer.play()
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.release()
        }
    }

    // ── Cast: escucha cambios de sesión ──────────────────────────────────────
    DisposableEffect(videoUrl) {
        val castContext = try { CastContext.getSharedInstance(context) } catch (ex: Exception) {
            Log.w(TAG, "CastContext no disponible: ${ex.message}")
            null
        }

        val sessionListener = object : SessionManagerListener<CastSession> {
            override fun onSessionStarted(session: CastSession, sessionId: String) {
                Log.d(TAG, "Cast sesión iniciada → enviando vídeo")
                exoPlayer.pause()
                session.loadVideoOnCast(videoUrl, resolvedResult.playbackHeaders)
            }
            override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
                Log.d(TAG, "Cast sesión reanudada → enviando vídeo")
                exoPlayer.pause()
                session.loadVideoOnCast(videoUrl, resolvedResult.playbackHeaders)
            }
            override fun onSessionEnded(session: CastSession, error: Int) {
                Log.d(TAG, "Cast sesión terminada → reproducción local")
                exoPlayer.play()
            }
            override fun onSessionStarting(session: CastSession) {}
            override fun onSessionStartFailed(session: CastSession, error: Int) {}
            override fun onSessionEnding(session: CastSession) {}
            override fun onSessionResuming(session: CastSession, sessionId: String) {}
            override fun onSessionResumeFailed(session: CastSession, error: Int) {}
            override fun onSessionSuspended(session: CastSession, reason: Int) {}
        }

        castContext?.sessionManager?.addSessionManagerListener(sessionListener, CastSession::class.java)
        onDispose {
            castContext?.sessionManager?.removeSessionManagerListener(sessionListener, CastSession::class.java)
        }
    }

    // ── Si ya hay sesión Cast activa al cargar el reproductor ────────────────
    LaunchedEffect(videoUrl) {
        if (videoUrl.isBlank()) return@LaunchedEffect
        val castContext = try { CastContext.getSharedInstance(context) } catch (_: Exception) { null }
        val activeSession = castContext?.sessionManager?.currentCastSession
        if (activeSession != null) {
            Log.d(TAG, "Sesión Cast ya activa al iniciar → enviando vídeo directamente")
            exoPlayer.pause()
            activeSession.loadVideoOnCast(videoUrl, resolvedResult.playbackHeaders)
        }
    }

    // ── UI ───────────────────────────────────────────────────────────────────
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx -> PlayerView(ctx).apply { player = exoPlayer } },
            update = { view -> view.player = exoPlayer }
        )

        Row(modifier = Modifier.align(Alignment.TopEnd)) {
            DownloadButton(
                isDownloading = isDownloading,
                progress = downloadProgress,
                onClick = { showDownloadDialog = true }
            )

            CastButton()
        }
    }

    // ── Dialog: elegir método de descarga ────────────────────────────────────
    if (showDownloadDialog) {
        // El DownloadManager del sistema no soporta HLS (.m3u8)
        val isHls = videoUrl.substringBefore("?").trimEnd('/').endsWith(".m3u8", ignoreCase = true)

        AlertDialog(
            onDismissRequest = { showDownloadDialog = false },
            title = { Text("Descargar video") },
            text = {
                if (isHls) {
                    Text(
                        "Este video es un stream HLS. Solo la descarga con progreso " +
                        "puede procesar streams .m3u8 (descarga y une los segmentos automáticamente)."
                    )
                } else {
                    Text("Elige cómo quieres descargar el video en Downloads/NaughtyHub/")
                }
            },
            confirmButton = {
                // Opción B — OkHttp con progreso (siempre disponible, soporta HLS)
                TextButton(onClick = {
                    showDownloadDialog = false
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        writePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        onOkHttpDownload(videoUrl)
                    }
                }) {
                    Text("Con progreso")
                }
            },
            dismissButton = {
                // Opción A — DownloadManager del sistema (solo para URLs directas)
                TextButton(
                    enabled = !isHls,
                    onClick = {
                        showDownloadDialog = false
                        val id = downloadWithSystemManager(videoUrl, context)
                        if (id != -1L) {
                            Toast.makeText(
                                context,
                                "Descarga iniciada — revisa las notificaciones",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "No se pudo iniciar la descarga con el sistema",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text(if (isHls) "Con el sistema (no disponible para HLS)" else "Con el sistema")
                }
            }
        )
    }
}
