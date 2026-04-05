package com.mackenzie.downhub.ui.main.videohub.favs

import android.util.Log
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.mackenzie.downhub.ui.main.videohub.main.MainAppBar
import com.mackenzie.downhub.ui.main.videohub.main.ServerList

@Preview
@Composable
fun FavoritesScreenContent(
    onSettingsButtonClicked: (() -> Unit) = {},
    onFavoriteButtonClicked: () -> Unit = {},
    onNavigate: (Int, String) -> Unit = { serverId, serverUrl -> }
) {

    Scaffold(
        topBar = { MainAppBar(
            title = "Favorites Servers",
            imageVector = Icons.Default.FavoriteBorder,
            onSettingsButtonClicked = onSettingsButtonClicked,
            onFavoriteButtonClicked = onFavoriteButtonClicked
        ) }
    ) { padding ->

        val ctx = LocalContext.current

        ServerList(
            itemSection01 = emptyList(), // No video servers in favorites
            itemSection02 = emptyList(),
            itemSection03 = emptyList(), // No hentai servers in favorites
            padding= padding,
            onFavoriteClick = {
                Toast.makeText( ctx, "Favorite Feature Under Development!", Toast.LENGTH_SHORT).show()
            }
        ) { item ->
            Log.e( "VideoHubScreenContent", "Server ID=${item.id}, Clicked Server: ${item.title}")
            onNavigate(item.id, item.url)
        }

        Toast.makeText( ctx, "Under Development!", Toast.LENGTH_SHORT).show()
    }
}