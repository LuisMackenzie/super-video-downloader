package com.mackenzie.downhub.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun MainTheme(content: @Composable () -> Unit) {
    VideoHubTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}