package com.mackenzie.naughtyhub.usecases.player

import arrow.core.Either
import com.mackenzie.naughtyhub.domain.video.embed.EmbeddedVideoResolveResult
import com.mackenzie.naughtyhub.data.datasources.EmbeddedVideoResolver
import com.mackenzie.naughtyhub.data.embed.EmbeddedVideoResolveError
import javax.inject.Inject

class ResolveEmbeddedVideoUrlUseCase @Inject constructor(
    private val resolver: EmbeddedVideoResolver
) {
    suspend operator fun invoke(embedUrl: String): Either<EmbeddedVideoResolveError, EmbeddedVideoResolveResult> {
        return resolver.resolve(embedUrl)
    }
}
