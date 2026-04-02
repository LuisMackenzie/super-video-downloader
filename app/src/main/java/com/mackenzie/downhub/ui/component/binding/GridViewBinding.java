package com.mackenzie.downhub.ui.component.binding;

import android.widget.GridView;
import androidx.databinding.BindingAdapter;
import com.mackenzie.downhub.data.local.room.entity.PageInfo;
import com.mackenzie.downhub.ui.component.adapter.TopPageAdapter;
import java.util.Collections;
import java.util.List;

public class GridViewBinding {
    @BindingAdapter("app:items")
    public static void setTopPages(GridView view, List<PageInfo> items) {
        TopPageAdapter adapter = (TopPageAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(items != null ? items : Collections.emptyList());
        }
    }
}
