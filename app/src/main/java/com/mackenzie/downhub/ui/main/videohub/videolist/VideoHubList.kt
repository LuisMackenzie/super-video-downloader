package com.mackenzie.downhub.ui.main.videohub.videolist

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mackenzie.downhub.data.local.model.hub.video.VideoDomainItem
import com.mackenzie.downhub.ui.main.videohub.main.TitleText
import com.mackenzie.downhub.util.hub.getMedia

@Preview(showBackground = true)
@Composable
fun VideoHubList(
    itemSection01: List<VideoDomainItem> = getMedia(),
    titleServer: String = "Video Server",
    padding: PaddingValues = PaddingValues(),
    onItemClick: (VideoDomainItem) -> Unit = {}
) {
    LazyVerticalGrid(
        contentPadding = PaddingValues(4.dp),
        // columns = GridCells.Fixed(3),
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(padding)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            TitleText(titleServer)
        }
        items(itemSection01) { item ->
            RenderVideo(
                item = item,
                onItemClick = { onItemClick(item) }
            )
        }
    }
}