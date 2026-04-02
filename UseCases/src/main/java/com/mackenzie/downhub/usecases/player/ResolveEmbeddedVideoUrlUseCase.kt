package com.mackenzie.downhub.usecases.player

import arrow.core.Either
import com.mackenzie.downhub.data.datasources.EmbeddedVideoResolver
import com.mackenzie.downhub.data.embed.EmbeddedVideoResolveError
import com.mackenzie.downhub.domain.video.embed.EmbeddedVideoResolveResult
import javax.inject.Inject

class ResolveEmbeddedVideoUrlUseCase @Inject constructor(
    private val resolver: EmbeddedVideoResolver
) {
    suspend operator fun invoke(embedUrl: String): Either<EmbeddedVideoResolveError, EmbeddedVideoResolveResult> {
        return resolver.resolve(embedUrl)
    }
}
