package com.mackenzie.downhub.ui.main.videohub.common.nav

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mackenzie.downhub.BuildConfig
import com.mackenzie.downhub.R
import com.mackenzie.downhub.ui.main.videohub.common.SaveUtils
import com.mackenzie.downhub.ui.main.videohub.common.compareVersion
import com.mackenzie.downhub.ui.main.videohub.common.removeVersionSuffix
import com.mackenzie.downhub.ui.main.videohub.common.showToast
import com.mackenzie.downhub.ui.main.videohub.common.urlEncoder
import com.mackenzie.downhub.ui.main.videohub.favs.FavoritesScreenContent
import com.mackenzie.downhub.ui.main.videohub.favs.FavoritesViewModel
import com.mackenzie.downhub.ui.main.videohub.main.UpdateDialog
import com.mackenzie.downhub.ui.main.videohub.main.VideoHubScreenContent
import com.mackenzie.downhub.ui.main.videohub.player.VideoPlayerScreenContent
import com.mackenzie.downhub.ui.main.videohub.videolist.VideoListScreenContent
import com.mackenzie.downhub.util.RemoteConfigHelper
import com.mackenzie.downhub.util.SharedPrefHelper

@Composable
fun Navigation(
    onOpenInBrowser: ((String) -> Unit),
    onSettingsButtonClicked: (() -> Unit),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
    ) {

    val context = LocalContext.current
    val navController = rememberNavController()
    var openUpdateDialog by remember { mutableStateOf(false) }
    var latestServerVersion by remember { mutableStateOf("") }

    // val favoritesViewModel: FavoritesViewModel = hiltViewModel()
    val favoriteIds by favoritesViewModel.favoriteIds.collectAsState()
    val favorites by favoritesViewModel.favorites.collectAsState()

    // Fetch Remote Config and check for updates on first composition
    LaunchedEffect(Unit) {
        try {
            RemoteConfigHelper.init()
            RemoteConfigHelper.fetchAndActivate()
            val latestVersion = RemoteConfigHelper.latestServerVersion
            // val currentVersion = BuildConfig.VERSION_NAME.removeVersionSuffix()
            Log.d("Navigation", "Version check — current=${BuildConfig.VERSION_NAME.removeVersionSuffix()}, latest=$latestVersion")
            val existNewVersion = compareVersionName(latestVersion)
            if (existNewVersion) {
                latestServerVersion = latestVersion
                openUpdateDialog = true
            }
        } catch (e: Exception) {
            Log.e("Navigation", "Remote Config fetch failed", e)
        }
    }

    if (openUpdateDialog) {
        UpdateDialog(
            latestVersion = latestServerVersion,
            onDismissRequest = { openUpdateDialog = it },
            onConfirmation = {
                if (SaveUtils().downloadAndInstallUpdate(context, latestServerVersion)) {
                    getString(context, R.string.dialog_updates_downloading).showToast(context)
                    openUpdateDialog = false
                }
            }
        )
    }

    DisposableEffect(Unit) {
        favoritesViewModel.start()
        onDispose { favoritesViewModel.stop() }
    }

    NavHost(
        navController = navController,
        startDestination = NavItem.VideoServersScreen.route
    ) {
        composable(NavItem.VideoServersScreen) {
            VideoHubScreenContent(
                onSettingsButtonClicked = onSettingsButtonClicked,
                onFavoriteButtonClicked = { navController.navigate(route = NavItem.FavoriteScreen.route) },
                favoriteIds = favoriteIds,
                onToggleFavorite = { item -> favoritesViewModel.toggleFavorite(item) },
            ) { serverId, serverUrl ->
                val openInBrowser = context
                    .getSharedPreferences(SharedPrefHelper.PREF_KEY, Context.MODE_PRIVATE)
                    .getBoolean(SharedPrefHelper.IS_OPEN_SERVER_IN_BROWSER, false)
                if (openInBrowser) {
                    onOpenInBrowser(serverUrl)
                } else {
                    navController.navigate(route = NavItem.VideoListScreen.createRoute(serverId, serverUrl.urlEncoder()))
                }
            }
        }

        composable(NavItem.VideoListScreen) { backStackEntry ->
            VideoListScreenContent(
                serverId = backStackEntry.findArg(NavArg.VideoHubServerId),
                serverUrl = backStackEntry.findArg(NavArg.VideoHubServerUrl)
            ) { videoId, videoUrl, embedUrl ->
                navController.navigate(route= NavItem.PlayerScreen.createRoute(videoId, videoUrl.urlEncoder(), embedUrl.urlEncoder()) )
            }
        }

        composable(NavItem.PlayerScreen) { backStackEntry ->
            VideoPlayerScreenContent(
                videoId = backStackEntry.findArg(NavArg.VideoId),
                videoUrl = backStackEntry.findArg(NavArg.VideoUrl),
                embedUrl = backStackEntry.findArg(NavArg.VideoEmbeddedUrl),
                onBack = {
                    Log.e("Navigation", "Back pressed in PlayerScreen, navigating up")
                    navController.popBackStack()
                }
            )
        }

        composable(NavItem.FavoriteScreen) {
            FavoritesScreenContent(
                favorites = favorites,
                favoriteIds = favoriteIds,
                onSettingsButtonClicked = onSettingsButtonClicked,
                onFavoriteButtonClicked = { navController.navigate(route = NavItem.VideoServersScreen.route) },
                onToggleFavorite = { item -> favoritesViewModel.toggleFavorite(item) },
            ) { serverId, serverUrl ->
                val openInBrowser = context
                    .getSharedPreferences(SharedPrefHelper.PREF_KEY, Context.MODE_PRIVATE)
                    .getBoolean(SharedPrefHelper.IS_OPEN_SERVER_IN_BROWSER, false)
                if (openInBrowser) {
                    onOpenInBrowser(serverUrl)
                } else {
                    navController.navigate(route = NavItem.VideoListScreen.createRoute(serverId, serverUrl.urlEncoder()))
                }
            }
        }
    }
}

private fun compareVersionName(latest: String): Boolean {

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

private fun NavGraphBuilder.composable(
    navItem: NavItem,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(route = navItem.route, arguments = navItem.args) { backStackEntry ->
        content(backStackEntry)
    }

}

private inline fun <reified T> NavBackStackEntry.findArg(arg: NavArg): T {
    val value = arguments?.get(arg.key)
    requireNotNull(value) { "Argument ${arg.key} not found" }
    return value as T
}