package com.mackenzie.downhub.ui.main.videohub.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mackenzie.downhub.ui.main.base.BaseFragment
import com.mackenzie.downhub.ui.main.home.MainActivity
import com.mackenzie.downhub.ui.main.videohub.common.nav.Navigation
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
        val mainActivity = requireActivity() as MainActivity
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                LaunchHomeVideoHub(
                    onOpenInBrowser = { url ->
                        mainActivity.mainViewModel.openedUrl.set(url)
                        mainActivity.mainViewModel.currentItem.set(1)
                    }
                )
            }
        }
    }
}

@Composable
private fun LaunchHomeVideoHub(onOpenInBrowser: ((String) -> Unit)? = null) {
    MainTheme {
        Navigation(onOpenInBrowser = onOpenInBrowser)
    }
}
