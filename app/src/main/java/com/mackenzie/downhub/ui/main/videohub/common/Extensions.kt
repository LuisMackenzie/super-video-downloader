package com.mackenzie.downhub.ui.main.videohub.common

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaMetadata
import com.mackenzie.downhub.BuildConfig
import com.mackenzie.downhub.R
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

fun Context.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

fun String.urlEncoder(): String {
    val encodedUrl = URLEncoder.encode(this, StandardCharsets.UTF_8.toString())
    return encodedUrl
}

fun String.urlDecoder(): String {
    val decodedUrl = URLDecoder.decode(this, StandardCharsets.UTF_8.toString())
    return decodedUrl
}

fun String.removeVersionSuffix(): String {
    return this.substringBefore("-")
}

fun String.getFlavorLink(context: Context): String {
    val baseLink = context.getString(R.string.dialog_update_download_base_link)
    val debugFile = context.getString(R.string.dialog_update_download_debug_file_name)
    // val enhancedFile = context.getString(R.string.dialog_update_download_enhanced_file_name)
    val releaseFile = context.getString(R.string.dialog_update_download_release_file_name)
    return when (BuildConfig.VERSION_NAME.substringAfterLast("-")) {
        "DEBUG" -> { baseLink + this + debugFile }
        // "PRIME" -> { baseLink + this + enhancedFile }
        else -> { baseLink + this + releaseFile }
    }
}

fun Context.hasWriteExternalStoragePermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED
}

fun Activity.hideSystemBars() {
    Log.e("hideSystemBars", "Hiding system bars for activity=${localClassName}")
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.hide(WindowInsetsCompat.Type.systemBars())
    controller.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

fun Activity.restoreSystemBars() {
    Log.e("showSystemBars", "Restoring system bars for activity=${localClassName}")
    WindowCompat.setDecorFitsSystemWindows(window, true)
    val controller = WindowInsetsControllerCompat(window, window.decorView)
    controller.show(WindowInsetsCompat.Type.systemBars())
}

fun String.getMimeType(): String {
    val normalizedUrl = substringBefore('?')
        .substringBefore('#')
        .trim()
        .lowercase()

    return when {
        normalizedUrl.endsWith(".m3u8") -> "application/x-mpegURL"
        normalizedUrl.endsWith(".mp4") -> "video/mp4"
        normalizedUrl.endsWith(".mkv") -> "video/x-matroska"
        normalizedUrl.endsWith(".webm") -> "video/webm"
        normalizedUrl.endsWith(".mov") -> "video/quicktime"
        normalizedUrl.endsWith(".m4v") -> "video/x-m4v"
        normalizedUrl.endsWith(".avi") -> "video/x-msvideo"
        else -> "video/*"
    }
}

fun String.shouldOpenDirectly(): Boolean {
    val mimeType = getMimeType()
    Log.e("shouldOpenDirectly", "URL=$this  MIMEtype=$mimeType")
    return mimeType != "video/*"
}

fun Context.launchExternalPlayer(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    videoUrl: String,
    headers: Map<String, String> = emptyMap()
) {
    val uri = videoUrl.toUri()
    val mimeType = videoUrl.getMimeType()
    Log.e("launchExternalPlayer", "MIME type=${mimeType}")

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, mimeType)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (this@launchExternalPlayer !is Activity) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        // Agregar headers para reproductores que lo soportan (VLC, MX Player, AGI Player)
        if (headers.isNotEmpty()) {
            val headersBundle = Bundle()
            for ((key, value) in headers) {
                headersBundle.putString(key, value)
            }
            putExtra("android.media.intent.extra.HTTP_HEADERS", headersBundle)
            // Algunos reproductores usan extras directos
            headers["Referer"]?.let { putExtra("Referer", it) }
            headers["User-Agent"]?.let { putExtra("User-Agent", it) }
        }

        setPackage("dev.mackenzie.agiplayer.debug")
    }

    try {
        val chooser = Intent.createChooser(intent, "Selecciona un reproductor de video")
        launcher.launch(chooser)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No se encontró ningún reproductor instalado", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Construye el MediaInfo a partir de la URL y lo carga en el RemoteMediaClient
 * del Chromecast conectado.
 */
fun CastSession.loadVideoOnCast(videoUrl: String, headers: Map<String, String> = emptyMap()) {
    val TAG = "VideoPlayer"

    val remoteMediaClient = remoteMediaClient ?: run {
        Log.w(TAG, "RemoteMediaClient no disponible en la sesión")
        return
    }

    // Detectar el tipo MIME según la extensión de la URL
    val contentType = when {
        videoUrl.contains(".m3u8", ignoreCase = true) -> "application/x-mpegurl"
        videoUrl.contains(".mpd",  ignoreCase = true) -> "application/dash+xml"
        videoUrl.contains(".mp4",  ignoreCase = true) -> "video/mp4"
        videoUrl.contains(".mkv",  ignoreCase = true) -> "video/x-matroska"
        videoUrl.contains(".webm", ignoreCase = true) -> "video/webm"
        else -> "video/mp4" // fallback razonable
    }

    Log.d(TAG, "Cargando en Cast → contentType=$contentType  url=$videoUrl")

    val metadata = MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)

    val mediaInfoBuilder = MediaInfo.Builder(videoUrl)
        .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
        .setContentType(contentType)
        .setMetadata(metadata)

    // Algunos receptores personalizados pueden leer headers de customData
    if (headers.isNotEmpty()) {
        try {
            val customData = JSONObject()
            val httpHeaders = JSONObject()
            for ((key, value) in headers) {
                httpHeaders.put(key, value)
            }
            customData.put("httpHeaders", httpHeaders)
            mediaInfoBuilder.setCustomData(customData)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting customData for Cast", e)
        }
    }

    val loadRequest = MediaLoadRequestData.Builder()
        .setMediaInfo(mediaInfoBuilder.build())
        .setAutoplay(true)
        .build()

    remoteMediaClient.load(loadRequest)
}

fun Context.setExternalPlayerMode(modeExternal: Boolean) {
    val sharedPref = getSharedPreferences("video_player_prefs", Context.MODE_PRIVATE)
    requireNotNull(sharedPref) { "No se pudo obtener SharedPreferences para video_player_prefs" }
    sharedPref.edit {
        putBoolean("external_player_mode", modeExternal)
    }
    Log.v("SetMode", "SET::modeExternal=${modeExternal}")
}

fun Context.getExternalPlayerMode(): Boolean {
    val sharedPref = getSharedPreferences("video_player_prefs", Context.MODE_PRIVATE)
    val modeExternal = sharedPref.getBoolean("external_player_mode", false)
    Log.v("GetMode", "GET::modeExternal=${modeExternal}")
    return modeExternal
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}