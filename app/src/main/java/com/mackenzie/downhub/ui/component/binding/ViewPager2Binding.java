package com.mackenzie.downhub.ui.component.binding;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.viewpager2.widget.ViewPager2;

import com.mackenzie.downhub.ui.main.home.browser.BrowserFragment;
import com.mackenzie.downhub.ui.main.home.browser.webTab.WebTab;

import java.util.Collections;
import java.util.List;

public class ViewPager2Binding {

    @BindingAdapter("app:items")
    public static void setWebItems(ViewPager2 view, List<WebTab> currentItems) {
        BrowserFragment.TabsFragmentStateAdapter adapter =
                (BrowserFragment.TabsFragmentStateAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setRoutes(currentItems != null ? currentItems : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setWebItemsObservable(ViewPager2 view, ObservableField<List<WebTab>> currentItems) {
        if (currentItems != null) {
            setWebItems(view, currentItems.get());
        }
    }

    @BindingAdapter("app:offScreenPageLimit")
    public static void setOffScreenPageLimit(ViewPager2 view, int pageLimit) {
        view.setOffscreenPageLimit(pageLimit);
    }

    @BindingAdapter("app:offScreenPageLimit")
    public static void setOffScreenPageLimitObservable(ViewPager2 view, ObservableField<Integer> pageLimit) {
        if (pageLimit != null && pageLimit.get() != null) {
            view.setOffscreenPageLimit(pageLimit.get());
        }
    }
}
