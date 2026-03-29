package com.mackenzie.downhub.ui.main.home.newhome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OnlinePrediction
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mackenzie.downhub.R
import com.mackenzie.downhub.data.local.model.hub.VideoItem
import com.mackenzie.downhub.data.local.model.hub.VideoItemType
import com.mackenzie.downhub.util.hub.getMedia2

@Preview(showBackground = true, heightDp = 150, widthDp = 120)
@Composable
fun RenderServerItem(
    modifier: Modifier = Modifier,
    item: VideoItem = getMedia2().first(),
    onFavoriteClick: () -> Unit = {},
    onItemClick: (VideoItem) -> Unit = {}
) {
    Card(
        shape =  MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clickable { onItemClick(item) }
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .height(120.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AsyncImage(
                model= ImageRequest.Builder(LocalContext.current)
                    .data(item.thumb)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.downloading_24px),
                error = painterResource(R.drawable.baseline_report_error),
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxSize(),
            )

            Text(
                textAlign = TextAlign.Center,
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 1.dp)
                    .background(
                        shape = MaterialTheme.shapes.small,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.background
                            )
                        ))
            )

            Icon(
                imageVector = Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onFavoriteClick() }

            )

            if (item.status.isOffline) {
                Icon(
                    imageVector = Icons.Default.OnlinePrediction,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(2.dp)
                        .size(20.dp)
                        .background(Color.Red, shape = MaterialTheme.shapes.medium)
                        .align(Alignment.TopStart)
                        .clickable { onFavoriteClick() }

                )
            }

            if (item.status.canChargeList || item.status.isFullyFunctional) {
                Icon(
                    imageVector = Icons.Outlined.PlayCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(2.dp)
                        .size(10.dp)
                        .background(
                            color = if (item.status.isFullyFunctional) Color.Green else Color.Blue,
                            shape = MaterialTheme.shapes.medium
                        )
                        .align(Alignment.BottomEnd)
                        .clickable { onFavoriteClick() }

                )
            }
        }
    }
}

fun loadIcon(videoItemType: VideoItemType): ImageVector = when (videoItemType) {
    VideoItemType.PHOTO -> Icons.Default.Image
    VideoItemType.VIDEO -> Icons.Default.SmartDisplay
    VideoItemType.AUDIO -> Icons.Default.Mic
    VideoItemType.SERVER -> Icons.Default.PlayCircle
}