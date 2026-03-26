package com.mackenzie.ui.component.binding

import android.widget.GridView
import androidx.databinding.BindingAdapter
import com.mackenzie.data.local.room.entity.PageInfo
import com.mackenzie.ui.component.adapter.*

object GridViewBinding {
    @BindingAdapter("app:items")
    @JvmStatic
    fun GridView.setTopPages(items: List<PageInfo>) {
        with(adapter as TopPageAdapter?) {
            this?.let { setData(items) }
        }
    }
}