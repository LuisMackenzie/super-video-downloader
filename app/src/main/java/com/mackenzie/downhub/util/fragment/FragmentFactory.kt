package com.mackenzie.downhub.util.fragment

import androidx.fragment.app.Fragment
import com.mackenzie.downhub.ui.main.history.HistoryFragment
import com.mackenzie.downhub.ui.main.home.browser.BrowserFragment
import com.mackenzie.downhub.ui.main.home.browser.detectedVideos.DetectedVideosTabFragment
import com.mackenzie.downhub.ui.main.home.browser.homeTab.BrowserHomeFragment
import com.mackenzie.downhub.ui.main.home.browser.webTab.WebTabFragment
import com.mackenzie.downhub.ui.main.progress.ProgressFragment
import com.mackenzie.downhub.ui.main.settings.SettingsFragment
import com.mackenzie.downhub.ui.main.video.VideoFragment
import javax.inject.Inject

interface FragmentFactory {
    fun createBrowserFragment(): Fragment
    fun createProgressFragment(): Fragment
    fun createVideoFragment(): Fragment
    fun createSettingsFragment(): Fragment
    fun createHistoryFragment(): Fragment

    fun createBrowserHomeFragment(): Fragment

    fun createWebTabFragment(): Fragment

    fun createDetectedVideosTabFragment(): Fragment
}

class FragmentFactoryImpl @Inject constructor() : FragmentFactory {
    override fun createBrowserFragment() = BrowserFragment.Companion.newInstance()

    override fun createProgressFragment() = ProgressFragment.Companion.newInstance()

    override fun createVideoFragment() = VideoFragment.Companion.newInstance()

    override fun createSettingsFragment() = SettingsFragment.Companion.newInstance()

    override fun createHistoryFragment() = HistoryFragment.Companion.newInstance()

    override fun createBrowserHomeFragment() = BrowserHomeFragment.Companion.newInstance()

    override fun createWebTabFragment() = WebTabFragment.Companion.newInstance()

    override fun createDetectedVideosTabFragment() = DetectedVideosTabFragment.Companion.newInstance()
}