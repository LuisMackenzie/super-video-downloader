package com.mackenzie.downhub.ui.main.videohub.videolist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mackenzie.downhub.domain.mocks.getMedia
import com.mackenzie.downhub.domain.providers.getNameById

@Composable
fun VideoListScreenContent(
    serverId: Int,
    serverUrl: String,
    vm: VideoHubViewModel = hiltViewModel(),
    onNavigate: (String, String, String) -> Unit = { videoId, videoUrl, embedUrl -> }
) {

    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(serverId, serverUrl) {
        // vm.loadVideos(serverId, serverUrl)
    }

    Scaffold(
        // topBar = { MainAppBar() }
    ) { padding ->

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${state.error}")
                }
            }
            else -> {
                VideoHubList(
                    itemSection01 = if (serverId == 88) getMedia() else state.videos,
                    titleServer = getNameById(serverId),
                    padding = padding,
                    onItemClick = { item ->
                        onNavigate(item.video.videoId, item.video.url, item.video.embedUrl)
                    }
                )
            }
        }
    }
}