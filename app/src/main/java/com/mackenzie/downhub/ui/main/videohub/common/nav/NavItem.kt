package com.mackenzie.downhub.ui.main.videohub.common.nav

import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.io.File

sealed class NavItem(
    internal val baseRoute: String,
    private val navArgs: List<NavArg> = emptyList()
) {

    object SplashScreen : NavItem("splash_screen")

    object VideoServersScreen : NavItem("video_hub_screen")

    object VideoListScreen : NavItem("video_list_screen", listOf(NavArg.VideoHubServerId, NavArg.VideoHubServerUrl)) {
        fun createRoute(serverId: Int, serverUrl: String,) = baseRoute + File.separator + serverId + File.separator + serverUrl
    }

    object PlayerScreen : NavItem("player_screen", listOf(NavArg.VideoId, NavArg.VideoUrl, NavArg.VideoEmbeddedUrl)) {
        fun createRoute( videoId: String, videoUrl: String, embedUrl: String) = baseRoute + File.separator + videoId + File.separator + videoUrl + File.separator + embedUrl
    }

    val route = run {
        val argValues = navArgs.map { "{${it.key}}" }
        listOf(baseRoute)
            .plus(argValues)
            .joinToString(File.separator)
    }

    val args = navArgs.map {
        navArgument(it.key) { type = it.navType }
    }
}

enum class NavArg(val key: String, val navType: NavType<*>) {
    VideoHubServerId("videoHubServerId", NavType.IntType),
    VideoHubServerUrl("videoHubServerUrl", NavType.StringType),
    VideoUrl("videoUrl", NavType.StringType),

    VideoEmbeddedUrl("videoEmbeddedUrl", NavType.StringType),
    VideoId("videoId", NavType.StringType)
}