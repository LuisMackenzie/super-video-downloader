package com.mackenzie.downhub.ui.main.videohub.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mackenzie.downhub.BuildConfig
import com.mackenzie.downhub.domain.VideoItem
import com.mackenzie.downhub.domain.providers.getHentaiServers
import com.mackenzie.downhub.domain.providers.getLiveCamsServers
import com.mackenzie.downhub.domain.providers.getVideoServers

@Preview
@Composable
fun ServerList(
    itemSection01: List<VideoItem> = getVideoServers(),
    itemSection02: List<VideoItem> = getLiveCamsServers(),
    itemSection03: List<VideoItem> = getHentaiServers(),
    padding: PaddingValues = PaddingValues(),
    onFavoriteClick: () -> Unit = {},
    onItemClick: (VideoItem) -> Unit = {}
) {

    val ctx = LocalContext.current
    val flavor = BuildConfig.DEBUG
    val filteredItems01 = itemSection01.filter { it.status.isFullyFunctional }
    val filteredItems02 = itemSection02.filter { it.status.isFullyFunctional }
    val filteredItems03 = itemSection03.filter { it.status.isFullyFunctional }

    LazyVerticalGrid(
        contentPadding = PaddingValues(4.dp),
        columns = GridCells.Fixed(3),
        // columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            TitleText("Seccion Videos")
        }
        items(if (flavor) itemSection01 else filteredItems01) { item ->
            RenderServerItem(
                item = item,
                modifier = Modifier.padding(4.dp),
                onFavoriteClick = onFavoriteClick,
                onItemClick = { onItemClick(item) }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            TitleText("Seccion LiveCams")
        }
        items(itemSection02) { item ->
            RenderServerItem(
                item = item,
                modifier = Modifier.padding(4.dp),
                onFavoriteClick = onFavoriteClick,
                onItemClick = {
                    if (item.id == 258) {
                        onItemClick(item)
                    } else {
                        Toast.makeText( ctx, "LiveCams Section Under Development!", Toast.LENGTH_SHORT).show()
                    }
                    // onItemClick(item)
                }
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            TitleText("Seccion Hentai")
        }
        items(if (flavor) itemSection03 else filteredItems03) { item ->
            RenderServerItem(
                item = item,
                modifier = Modifier.padding(4.dp),
                onFavoriteClick = onFavoriteClick,
                onItemClick = { onItemClick(item) }
            )
        }
    }
}