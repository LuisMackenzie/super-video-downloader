package com.mackenzie.downhub.ui.main.videohub.player

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Botón de descarga con indicador de progreso visual.
 *
 * @param isDownloading Si es `true`, muestra un [CircularProgressIndicator] superpuesto al icono.
 * @param progress Progreso de la descarga de 0 a 100. Si es `null`, el indicador es indeterminado.
 * @param onClick Callback al pulsar el botón (solo activo cuando [isDownloading] es `false`).
 */
@Composable
fun DownloadButton(
    modifier: Modifier = Modifier,
    isDownloading: Boolean = false,
    progress: Int? = null,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier
            .padding(vertical = 16.dp)
            .padding(top = 30.dp)
            .size(48.dp),
        onClick = { if (!isDownloading) onClick() },
        enabled = !isDownloading,
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Icono de descarga (siempre visible)
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = if (isDownloading) "Descargando…" else "Descargar video",
                tint = if (isDownloading) Color.White.copy(alpha = 0.4f) else Color.White,
            )

            // Indicador de progreso superpuesto
            if (isDownloading) {
                if (progress != null && progress in 0..100) {
                    CircularProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.size(40.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                        trackColor = Color.White.copy(alpha = 0.2f),
                    )
                } else {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = Color.White,
                        strokeWidth = 2.5.dp,
                        trackColor = Color.White.copy(alpha = 0.2f),
                    )
                }
            }
        }
    }
}
