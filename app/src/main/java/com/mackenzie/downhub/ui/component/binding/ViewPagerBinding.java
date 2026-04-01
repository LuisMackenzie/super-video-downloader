package com.mackenzie.downhub.ui.component.binding;

import androidx.databinding.BindingAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerBinding {

    @BindingAdapter("app:offScreenPageLimit")
    public static void setOffScreenPageLimit(ViewPager view, int pageLimit) {
        view.setOffscreenPageLimit(pageLimit);
    }
}
