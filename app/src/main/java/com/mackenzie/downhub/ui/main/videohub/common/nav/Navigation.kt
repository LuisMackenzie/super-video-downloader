package com.mackenzie.downhub.ui.main.videohub.common.nav

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mackenzie.downhub.ui.main.videohub.common.urlEncoder
import com.mackenzie.downhub.ui.main.videohub.favs.FavoritesScreenContent
import com.mackenzie.downhub.ui.main.videohub.main.VideoHubScreenContent
import com.mackenzie.downhub.ui.main.videohub.player.VideoPlayerScreenContent
import com.mackenzie.downhub.ui.main.videohub.videolist.VideoListScreenContent
import com.mackenzie.downhub.util.SharedPrefHelper

@Composable
fun Navigation(
    onOpenInBrowser: ((String) -> Unit),
    onSettingsButtonClicked: (() -> Unit)
    ) {

    val context = LocalContext.current
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavItem.VideoServersScreen.route
    ) {
        composable(NavItem.VideoServersScreen) {
            VideoHubScreenContent(
                onSettingsButtonClicked = onSettingsButtonClicked,
                onFavoriteButtonClicked = { navController.navigate(route = NavItem.FavoriteScreen.route) },
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
                onSettingsButtonClicked = onSettingsButtonClicked,
                onFavoriteButtonClicked = { navController.navigate(route = NavItem.VideoServersScreen.route)  }
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