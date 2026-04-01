package com.mackenzie.downhub.ui.component.binding;

import androidx.databinding.BindingAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mackenzie.downhub.R;

public class BottomNavigationViewBinding {

    @BindingAdapter("app:selectedItemId")
    public static void setSelectedItemId(BottomNavigationView view, int position) {
        int id;
        switch (position) {
            case 0: id = R.id.tab_home; break;
            case 1: id = R.id.tab_browser; break;
            case 2: id = R.id.tab_progress; break;
            case 3: id = R.id.tab_video; break;
            default: id = R.id.tab_settings; break;
        }
        view.setSelectedItemId(id);
    }
}
