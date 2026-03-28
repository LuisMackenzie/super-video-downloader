package com.mackenzie.downhub.ui.component.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mackenzie.downhub.util.fragment.FragmentFactory

class MainAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    private val fragmentFactory: FragmentFactory
) : FragmentStateAdapter(fm, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> fragmentFactory.createHomeFragment()
            1 -> fragmentFactory.createBrowserFragment()
            2 -> fragmentFactory.createProgressFragment()
            3 -> fragmentFactory.createVideoFragment()
            else -> fragmentFactory.createVideoFragment()
        }
    }

    override fun getItemCount(): Int {
        return 4
    }
}
