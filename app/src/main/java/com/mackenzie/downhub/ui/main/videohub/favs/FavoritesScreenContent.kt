package com.mackenzie.downhub.ui.main.videohub.favs

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mackenzie.downhub.domain.VideoItem
import com.mackenzie.downhub.ui.main.videohub.main.MainAppBar
import com.mackenzie.downhub.ui.main.videohub.main.ServerList

@Preview
@Composable
fun FavoritesScreenContent(
    favorites: List<VideoItem> = emptyList(),
    favoriteIds: Set<Int> = emptySet(),
    onSettingsButtonClicked: (() -> Unit) = {},
    onFavoriteButtonClicked: () -> Unit = {},
    onToggleFavorite: (VideoItem) -> Unit = {},
    onNavigate: (Int, String) -> Unit = { _, _ -> },
) {

    Scaffold(
        topBar = {
            MainAppBar(
                title = "Favorites Servers",
                imageVector = Icons.Default.FavoriteBorder,
                onSettingsButtonClicked = onSettingsButtonClicked,
                onFavoriteButtonClicked = onFavoriteButtonClicked,
            )
        }
    ) { padding ->

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No favorites yet.\nTap the ❤ icon on a server to add it!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                )
            }
        } else {
            ServerList(
                itemSection01 = favorites,
                itemSection02 = emptyList(),
                itemSection03 = emptyList(),
                favoriteIds = favoriteIds,
                padding = padding,
                onFavoriteClick = { item -> onToggleFavorite(item) },
            ) { item ->
                Log.e("FavoritesScreenContent", "Server ID=${item.id}, Clicked Server: ${item.title}")
                onNavigate(item.id, item.url)
            }
        }
    }
}
