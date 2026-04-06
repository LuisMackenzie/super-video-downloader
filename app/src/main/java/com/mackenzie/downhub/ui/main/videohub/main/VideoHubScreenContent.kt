package com.mackenzie.downhub.ui.main.videohub.main

import android.util.Log
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getString
import com.mackenzie.downhub.R
import com.mackenzie.downhub.domain.VideoItem
import com.mackenzie.downhub.ui.main.videohub.common.SaveUtils

@Preview
@Composable
fun VideoHubScreenContent(
    onSettingsButtonClicked: () -> Unit = {},
    onFavoriteButtonClicked: () -> Unit = {},
    favoriteIds: Set<Int> = emptySet(),
    onToggleFavorite: (VideoItem) -> Unit = {},
    onNavigate: (Int, String) -> Unit = { serverId, serverUrl -> }
) {

    var openUpdateDialog by remember { mutableStateOf(false) }

    if (openUpdateDialog) {
        UpdateDialog(
            // TODO fetch from firebase remoe config
            // latestVersion = remote.latestServerVersion ?: "",
            onDismissRequest = { openUpdateDialog = it },
            onConfirmation = {
                // remote.latestServerVersion?.let {
                if (SaveUtils().downloadAndInstallUpdate(context, it)) {
                    getString(context, R.string.waifus_updates_downloading).showToast(context)
                    openUpdateDialog = false
                }
                // }
            }
        )
    }

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
