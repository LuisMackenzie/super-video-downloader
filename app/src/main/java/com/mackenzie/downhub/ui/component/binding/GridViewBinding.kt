package com.mackenzie.downhub.ui.component.binding

import android.widget.GridView
import androidx.databinding.BindingAdapter
import com.mackenzie.downhub.data.local.room.entity.PageInfo
import com.mackenzie.downhub.ui.component.adapter.TopPageAdapter

object GridViewBinding {
    @BindingAdapter("app:items")
    @JvmStatic
    fun GridView.setTopPages(items: List<PageInfo>) {
        with(adapter as TopPageAdapter?) {
            this?.let { setData(items) }
        }
    }
}