package com.mackenzie.downhub.ui.main.videohub.common.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.mackenzie.downhub.BuildConfig
import com.mackenzie.downhub.ui.main.videohub.common.compareVersion
import com.mackenzie.downhub.ui.main.videohub.common.isLandscape

@Composable
fun isNavigationBarVisible(): Boolean {
    val insets = LocalView.current.rootWindowInsets
    val navBarHeightPx = insets.stableInsetBottom
    val navBarHeightDp = with(LocalDensity.current) { navBarHeightPx.toDp() }
    // Para tabletas la barra de navegacion puede ser mayor
    if (LocalContext.current.isLandscape()) {
        return navBarHeightDp > 32.dp
    } else {
        return navBarHeightDp > 20.dp
    }
}


internal fun compareVersionName(latest: String): Boolean {

    when (latest.compareVersion()) {
        0 -> {
            Log.e("SelectorScreenContentRoute", "La version del Servidor es la misma que la local")
            Log.e("SelectorScreenContentRoute", "local Version=${BuildConfig.VERSION_NAME}")
            Log.e("SelectorScreenContentRoute", "Server Version=${latest}")
            return false
        }
        1 -> {
            Log.e("SelectorScreenContentRoute", "La version del Servidor es MENOR que la local")
            Log.e("SelectorScreenContentRoute", "local Version=${BuildConfig.VERSION_NAME}")
            Log.e("SelectorScreenContentRoute", "Server Version=${latest}")
            return false
        }
        -1 -> {
            Log.e("SelectorScreenContentRoute", "La version del Servidor es MAYOR que la local")
            Log.e("SelectorScreenContentRoute", "local Version=${BuildConfig.VERSION_NAME}")
            Log.e("SelectorScreenContentRoute", "Server Version=${latest}")
            return true
        }
        else -> {
            Log.e("SelectorScreenContentRoute", "Error al comparar versiones")
            Log.e("SelectorScreenContentRoute", "local Version=${BuildConfig.VERSION_NAME}")
            Log.e("SelectorScreenContentRoute", "Server Version=${latest}")
            return false
        }
    }
}