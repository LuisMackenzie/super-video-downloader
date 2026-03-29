package com.mackenzie.downhub.ui.main.home.newhome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mackenzie.downhub.ui.main.base.BaseFragment
import com.mackenzie.downhub.ui.theme.MainTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeComposeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeComposeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LaunchHomeVideoHub()
            }
        }
    }
}

@Composable
private fun LaunchHomeVideoHub() {
    MainTheme {
        VideoHubScreenContent() { serverId, serverUrl ->
            // navController.navigate(route= NavItem.VideoListScreen.createRoute(serverId, serverUrl.urlEncoder()))
        }
    }
}
