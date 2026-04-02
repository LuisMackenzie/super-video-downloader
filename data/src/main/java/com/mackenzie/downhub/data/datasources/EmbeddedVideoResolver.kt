package com.mackenzie.downhub.data.datasources

import arrow.core.Either
import com.mackenzie.downhub.data.embed.EmbeddedVideoResolveError
import com.mackenzie.downhub.domain.video.embed.EmbeddedVideoResolveResult

/**
 * Resuelve una URL de un "embed" a una URL reproducible (mp4/m3u8/mpd) cuando sea posible.
 *
 * Nota: esto es un resolver genérico basado en HTML estático. Si el sitio construye el DOM
 * 100% con JavaScript, puede devolver [com.mackenzie.waifuviewer.data.embed.EmbeddedVideoResolveError.RequiresJavaScript].
 */
interface EmbeddedVideoResolver {
    suspend fun resolve(embedUrl: String): Either<EmbeddedVideoResolveError, EmbeddedVideoResolveResult>
}