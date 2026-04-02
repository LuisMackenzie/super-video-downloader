package com.mackenzie.downhub.ui.main.videohub.player

import android.view.ContextThemeWrapper
import androidx.compose.foundation.layout.padding
import androidx.mediarouter.app.MediaRouteButton
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.cast.framework.CastButtonFactory
import com.mackenzie.downhub.R

@Composable
fun CastButton(modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier
            .padding(all = 16.dp)
            .padding(top = 30.dp)
            .size(48.dp),
        factory = { ctx ->
            // Wrap context with a theme that has solid colors to avoid MediaRouter background crash
            val themedContext = ContextThemeWrapper(ctx, R.style.CustomMediaRouteButtonStyle)
            MediaRouteButton(themedContext).apply {
                try {
                    CastButtonFactory.setUpMediaRouteButton(themedContext, this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    )
}