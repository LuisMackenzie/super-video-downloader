package com.mackenzie.downhub.ui.component.binding;

import android.graphics.Bitmap;
import androidx.annotation.OptIn;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.media3.common.util.UnstableApi;
import com.mackenzie.downhub.ui.main.home.browser.BrowserFragment;
import com.mackenzie.downhub.ui.main.home.browser.CustomViewPager2;
import com.mackenzie.downhub.ui.main.home.browser.webTab.WebTab;
import java.util.Collections;
import java.util.List;

public class CustomViewPager2Binding {

    @BindingAdapter("app:items")
    public static void setWebItems(CustomViewPager2 view, List<WebTab> currentItems) {
        BrowserFragment.TabsFragmentStateAdapter adapter =
            (BrowserFragment.TabsFragmentStateAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setRoutes(currentItems != null ? currentItems : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setWebItemsObservable(CustomViewPager2 view, ObservableField<List<WebTab>> currentItems) {
        setWebItems(view, currentItems != null ? currentItems.get() : null);
    }

    @BindingAdapter("app:offScreenPageLimit")
    public static void setOffScreenPageLimit(CustomViewPager2 view, int pageLimit) {
        view.setOffscreenPageLimit(pageLimit);
    }

    @BindingAdapter("app:currentItem")
    public static void setCurrentItem(CustomViewPager2 view, int currentItemPosition) {
        view.setCurrentItem(currentItemPosition);
    }
}
