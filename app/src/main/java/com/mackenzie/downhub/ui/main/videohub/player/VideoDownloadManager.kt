package com.mackenzie.downhub.ui.main.videohub.player

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

// ─────────────────────────────────────────────────────────────────────────────
// Constantes y helpers internos
// ─────────────────────────────────────────────────────────────────────────────

private const val NAUGHTYHUB_FOLDER = "NaughtyHub"

/** Devuelve `true` si la URL apunta a una playlist HLS. */
private fun String.isHls(): Boolean =
    substringBefore("?").trimEnd('/').endsWith(".m3u8", ignoreCase = true)

/** Nombre de archivo de salida a partir de la URL; siempre termina en .mp4. */
private fun outputFileName(url: String): String {
    val raw = url.substringAfterLast("/").substringBefore("?").trim()
    val base = raw.substringBeforeLast(".").ifBlank { "video_${System.currentTimeMillis()}" }
    return "$base.mp4"
}

/** Resuelve una URL relativa de segmento frente a la URL base de la playlist. */
private fun resolveSegmentUrl(baseUrl: String, segment: String): String {
    if (segment.startsWith("http://") || segment.startsWith("https://")) return segment
    val base = baseUrl.substringBeforeLast("/")
    return "$base/$segment"
}

/** Hace una petición GET síncrona y devuelve el cuerpo como texto, o null en error. */
private fun OkHttpClient.fetchText(url: String, referer: String): String? {
    val req = Request.Builder()
        .url(url)
        .addHeader("User-Agent", "Mozilla/5.0")
        .addHeader("Referer", referer)
        .build()
    return try {
        newCall(req).execute().use { resp ->
            if (resp.isSuccessful) resp.body.string() else null
        }
    } catch (_: Exception) {
        null
    }
}

/** Hace una petición GET síncrona y escribe los bytes en [output]. Devuelve los bytes escritos o -1. */
private fun OkHttpClient.fetchBytes(url: String, referer: String, output: OutputStream): Long {
    val req = Request.Builder()
        .url(url)
        .addHeader("User-Agent", "Mozilla/5.0")
        .addHeader("Referer", referer)
        .build()
    return try {
        newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) return -1L
            val buf = ByteArray(DEFAULT_BUFFER_SIZE)
            var total = 0L
            resp.body.byteStream().use { input ->
                var n: Int
                while (input.read(buf).also { n = it } != -1) {
                    output.write(buf, 0, n)
                    total += n
                }
            }
            total
        }
    } catch (_: Exception) {
        -1L
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Resultado de la descarga
// ─────────────────────────────────────────────────────────────────────────────

sealed interface DownloadResult {
    /** Descarga completada con éxito. */
    data class Success(val filePath: String) : DownloadResult
    /** La descarga falló. */
    data class Error(val message: String) : DownloadResult
}

// ─────────────────────────────────────────────────────────────────────────────
// Opción A — Android DownloadManager (sistema, solo para URLs directas)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Encola la descarga usando el servicio nativo [DownloadManager].
 * **Solo funciona con URLs directas** (mp4, mkv, etc.).
 * Para streams HLS (.m3u8) usa [downloadWithOkHttp] que incluye el parser HLS.
 *
 * @return ID de la descarga encolada, o -1 si el servicio no está disponible.
 */
fun downloadWithSystemManager(url: String, context: Context): Long {
    val fileName = outputFileName(url)
    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager ?: return -1

    val request = DownloadManager.Request(url.toUri()).apply {
        setTitle(fileName)
        setDescription("Descargando con NaughtyHub…")
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "$NAUGHTYHUB_FOLDER/$fileName"
        )
        setAllowedOverMetered(true)
        setAllowedOverRoaming(false)
        addRequestHeader("User-Agent", "Mozilla/5.0")
        addRequestHeader("Referer", url.toUri().run { "$scheme://$host/" })
    }

    return dm.enqueue(request)
}

// ─────────────────────────────────────────────────────────────────────────────
// Opción B — OkHttp + Coroutine, con soporte HLS completo
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Descarga el video con progreso en tiempo real.
 *
 * - Si la URL es `.m3u8` → parsea la playlist HLS, descarga todos los segmentos `.ts`
 *   y los concatena en un único archivo `.mp4`.
 * - Si la URL es un archivo directo → descarga el stream byte a byte.
 *
 * Emite:
 * - `Int` 0-99 → porcentaje de progreso.
 * - `Int` 100  → completado.
 * - [DownloadResult.Success] o [DownloadResult.Error] al terminar.
 *
 * Guarda en `Downloads/NaughtyHub/<nombre>.mp4`.
 */
fun downloadWithOkHttp(
    url: String,
    okHttpClient: OkHttpClient,
    context: Context,
): Flow<Any> = flow {
    if (url.isHls()) {
        downloadHls(url, okHttpClient, context)
    } else {
        downloadDirect(url, okHttpClient, context)
    }
}.flowOn(Dispatchers.IO)

// ─────────────────────────────────────────────────────────────────────────────
// Descarga directa (mp4, mkv, etc.)
// ─────────────────────────────────────────────────────────────────────────────

private suspend fun FlowCollector<Any>.downloadDirect(
    url: String,
    client: OkHttpClient,
    context: Context,
) {
    val fileName = outputFileName(url)
    val referer = url.toUri().run { "$scheme://$host/" }

    val request = Request.Builder()
        .url(url)
        .addHeader("User-Agent", "Mozilla/5.0")
        .addHeader("Referer", referer)
        .build()

    val response = try {
        client.newCall(request).execute()
    } catch (e: Exception) {
        emit(DownloadResult.Error("Error de red: ${e.message}"))
        return
    }

    if (!response.isSuccessful) {
        emit(DownloadResult.Error("HTTP ${response.code}"))
        return
    }

    val body = response.body
    val totalBytes = body.contentLength()

    openOutputFile(context, fileName) { outputStream, filePath ->
        var downloadedBytes = 0L
        var lastPercent = -1
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        body.byteStream().use { input ->
            var n: Int
            while (input.read(buffer).also { n = it } != -1) {
                outputStream.write(buffer, 0, n)
                downloadedBytes += n
                if (totalBytes > 0) {
                    val p = ((downloadedBytes * 100) / totalBytes).toInt().coerceAtMost(99)
                    if (p != lastPercent) { lastPercent = p; emit(p) }
                }
            }
        }
        emit(100)
        emit(DownloadResult.Success(filePath))
    } ?: emit(DownloadResult.Error("No se pudo crear el archivo de destino"))
}

// ─────────────────────────────────────────────────────────────────────────────
// Descarga HLS: parseo de playlist + descarga de segmentos + concatenación
// ─────────────────────────────────────────────────────────────────────────────

private suspend fun FlowCollector<Any>.downloadHls(
    masterUrl: String,
    client: OkHttpClient,
    context: Context,
) {
    val referer = masterUrl.toUri().run { "$scheme://$host/" }
    val fileName = outputFileName(masterUrl)

    // 1. Descargar la playlist (puede ser master o media playlist directamente)
    val masterText = client.fetchText(masterUrl, referer)
        ?: run { emit(DownloadResult.Error("No se pudo obtener la playlist HLS")); return }

    // 2. Resolver la URL de la playlist de media (con los segmentos)
    val mediaPlaylistUrl = resolveMediaPlaylistUrl(masterUrl, masterText)
        ?: run { emit(DownloadResult.Error("No se encontró ninguna variante de stream en la playlist")); return }

    // 3. Obtener la playlist de media si es distinta de la master
    val mediaText = if (mediaPlaylistUrl == masterUrl) masterText
    else client.fetchText(mediaPlaylistUrl, referer)
        ?: run { emit(DownloadResult.Error("No se pudo obtener la playlist de segmentos")); return }

    // 4. Extraer URLs de segmentos .ts / .m4s
    val segments = parseSegmentUrls(mediaPlaylistUrl, mediaText)
    if (segments.isEmpty()) {
        emit(DownloadResult.Error("La playlist no contiene segmentos descargables"))
        return
    }

    val total = segments.size
    emit(0)

    // 5. Descargar y concatenar en un único archivo
    openOutputFile(context, fileName) { outputStream, filePath ->
        segments.forEachIndexed { index, segUrl ->
            val written = client.fetchBytes(segUrl, referer, outputStream)
            if (written < 0) {
                emit(DownloadResult.Error("Error al descargar segmento ${index + 1}/$total"))
                return@openOutputFile
            }
            val percent = (((index + 1) * 100) / total).coerceAtMost(99)
            emit(percent)
        }
        emit(100)
        emit(DownloadResult.Success(filePath))
    } ?: emit(DownloadResult.Error("No se pudo crear el archivo de destino"))
}

/**
 * Dado el texto de una playlist HLS, devuelve la URL de la media playlist
 * con la mayor calidad disponible (mayor BANDWIDTH), o la misma URL si ya
 * es una media playlist (contiene líneas `#EXTINF`).
 */
private fun resolveMediaPlaylistUrl(baseUrl: String, playlistText: String): String? {
    // Si ya es una media playlist, los segmentos están aquí mismo
    if (playlistText.lines().any { it.startsWith("#EXTINF") }) return baseUrl

    // Es una master playlist — buscar la variante de mayor BANDWIDTH
    data class Variant(val bandwidth: Long, val uri: String)

    val variants = mutableListOf<Variant>()
    val lines = playlistText.lines()
    for (i in lines.indices) {
        val line = lines[i].trim()
        if (line.startsWith("#EXT-X-STREAM-INF")) {
            val bandwidth = Regex("BANDWIDTH=(\\d+)").find(line)
                ?.groupValues?.get(1)?.toLongOrNull() ?: 0L
            // La siguiente línea no comentada es la URI
            val uriLine = lines.drop(i + 1).firstOrNull { it.isNotBlank() && !it.startsWith("#") }
                ?: continue
            variants += Variant(bandwidth, resolveSegmentUrl(baseUrl, uriLine.trim()))
        }
    }

    return variants.maxByOrNull { it.bandwidth }?.uri
}

/**
 * Extrae la lista ordenada de URLs de segmentos desde el texto de una media playlist HLS.
 * Soporta segmentos `.ts`, `.m4s` y URLs sin extensión.
 */
private fun parseSegmentUrls(mediaPlaylistUrl: String, playlistText: String): List<String> {
    val segments = mutableListOf<String>()
    val lines = playlistText.lines()
    for (i in lines.indices) {
        val line = lines[i].trim()
        // Cada segmento está precedido por una línea #EXTINF
        if (line.startsWith("#EXTINF")) {
            val nextLine = lines.drop(i + 1)
                .firstOrNull { it.isNotBlank() && !it.startsWith("#") }
                ?: continue
            segments += resolveSegmentUrl(mediaPlaylistUrl, nextLine.trim())
        }
    }
    return segments
}

// ─────────────────────────────────────────────────────────────────────────────
// Abstracción de escritura: MediaStore (API 29+) o File directo (API 24-28)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Abre un [OutputStream] hacia el archivo de destino y ejecuta [block] con él.
 * Gestiona automáticamente MediaStore (API 29+) vs archivo directo (API 24-28).
 *
 * @return El resultado de [block], o `null` si no se pudo abrir el stream.
 */
private inline fun <T> openOutputFile(
    context: Context,
    fileName: String,
    block: (outputStream: OutputStream, filePath: String) -> T,
): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "video/mp4")
            put(
                MediaStore.Downloads.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOWNLOADS}/$NAUGHTYHUB_FOLDER"
            )
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = context.contentResolver.insert(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
        ) ?: return null

        val result = context.contentResolver.openOutputStream(uri)?.use { out ->
            block(out, uri.toString())
        }

        val done = ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }
        context.contentResolver.update(uri, done, null, null)
        result
    } else {
        val dir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            NAUGHTYHUB_FOLDER
        ).also { if (!it.exists()) it.mkdirs() }

        val file = File(dir, fileName)
        FileOutputStream(file).use { out -> block(out, file.absolutePath) }
    }
}
