package com.mackenzie.downhub.ui.main.videohub.main

import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.mackenzie.downhub.domain.VideoItem

@Preview
@Composable
fun VideoHubScreenContent(
    onSettingsButtonClicked: () -> Unit = {},
    onFavoriteButtonClicked: () -> Unit = {},
    favoriteIds: Set<Int> = emptySet(),
    onToggleFavorite: (VideoItem) -> Unit = {},
    onNavigate: (Int, String) -> Unit = { serverId, serverUrl -> }
) {

    Scaffold(
        topBar = { MainAppBar(
            onSettingsButtonClicked= onSettingsButtonClicked,
            onFavoriteButtonClicked = onFavoriteButtonClicked
        ) }
    ) { padding ->

        ServerList(
            padding = padding,
            favoriteIds = favoriteIds,
            onFavoriteClick = { item -> onToggleFavorite(item) }
        ) { item ->
            Log.e("VideoHubScreenContent", "Server ID=${item.id}, Clicked Server: ${item.title}")
            onNavigate(item.id, item.url)
        }
    }
}
