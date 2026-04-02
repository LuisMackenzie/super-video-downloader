package com.mackenzie.downhub.ui.main.videohub.common.nav

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mackenzie.downhub.ui.main.videohub.common.urlEncoder
import com.mackenzie.downhub.ui.main.videohub.main.VideoHubScreenContent
import com.mackenzie.downhub.ui.main.videohub.player.VideoPlayerScreenContent
import com.mackenzie.downhub.ui.main.videohub.videolist.VideoListScreenContent

@Composable
fun Navigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavItem.VideoServersScreen.route
    ) {
        composable(NavItem.VideoServersScreen) {
            VideoHubScreenContent() { serverId, serverUrl ->
                navController.navigate(route= NavItem.VideoListScreen.createRoute(serverId, serverUrl.urlEncoder()))
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