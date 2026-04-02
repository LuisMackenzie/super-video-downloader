package com.mackenzie.downhub.ui.component.binding;

import androidx.databinding.BindingAdapter;
import com.google.android.material.appbar.AppBarLayout;

public class AppBarBinding {
    @BindingAdapter("app:smoothExpanded")
    public static void setExpanded(AppBarLayout view, boolean isExpanded) {
        view.setExpanded(isExpanded, true);
    }
}
