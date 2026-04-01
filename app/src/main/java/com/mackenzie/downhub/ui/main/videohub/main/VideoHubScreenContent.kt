package com.mackenzie.downhub.ui.main.videohub.main

import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun VideoHubScreenContent(
    onNavigate: (Int, String) -> Unit = { serverId, serverUrl -> }
) {

    Scaffold(
        // topBar = { MainAppBar() }
    ) { padding ->

        val ctx = LocalContext.current

        ServerList(
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