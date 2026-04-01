package com.mackenzie.downhub.ui.component.binding;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.recyclerview.widget.RecyclerView;

import com.mackenzie.downhub.data.local.model.LocalVideo;
import com.mackenzie.downhub.data.local.model.Proxy;
import com.mackenzie.downhub.data.local.room.entity.HistoryItem;
import com.mackenzie.downhub.data.local.room.entity.PageInfo;
import com.mackenzie.downhub.data.local.room.entity.ProgressInfo;
import com.mackenzie.downhub.data.local.room.entity.VideoInfo;
import com.mackenzie.downhub.ui.component.adapter.BookmarksAdapter;
import com.mackenzie.downhub.ui.component.adapter.HistoryAdapter;
import com.mackenzie.downhub.ui.component.adapter.HistorySearchAdapter;
import com.mackenzie.downhub.ui.component.adapter.ProgressAdapter;
import com.mackenzie.downhub.ui.component.adapter.ProxiesAdapter;
import com.mackenzie.downhub.ui.component.adapter.VideoAdapter;
import com.mackenzie.downhub.ui.component.adapter.VideoInfoAdapter;
import com.mackenzie.downhub.ui.component.adapter.WebTabsAdapter;
import com.mackenzie.downhub.ui.main.home.browser.webTab.WebTab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RecyclerViewBinding {

    @BindingAdapter("app:items")
    public static void setWebTabs(RecyclerView view, List<WebTab> tabs) {
        WebTabsAdapter adapter = (WebTabsAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(tabs != null ? tabs : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setProgressInfos(RecyclerView view, List<ProgressInfo> items) {
        ProgressAdapter adapter = (ProgressAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(items != null ? items : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setProgressInfosObservable(RecyclerView view, ObservableField<List<ProgressInfo>> items) {
        if (items != null) {
            setProgressInfos(view, items.get());
        }
    }

    @BindingAdapter("app:items")
    public static void setProxiesList(RecyclerView view, List<Proxy> items) {
        ProxiesAdapter adapter = (ProxiesAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(items != null ? items : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setVideoInfos(RecyclerView view, List<LocalVideo> items) {
        VideoAdapter adapter = (VideoAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(items != null ? items : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setVideoInfosObservable(RecyclerView view, ObservableField<? extends List<LocalVideo>> items) {
        if (items != null) {
            setVideoInfos(view, items.get());
        }
    }

    @BindingAdapter("app:items")
    public static void historyItems(RecyclerView view, List<HistoryItem> items) {
        List<HistoryItem> safeItems = items != null ? items : Collections.emptyList();
        if (view.getAdapter() instanceof HistoryAdapter) {
            ((HistoryAdapter) view.getAdapter()).setData(safeItems);
        } else if (view.getAdapter() instanceof HistorySearchAdapter) {
            ((HistorySearchAdapter) view.getAdapter()).setData(safeItems);
        }
    }

    @BindingAdapter("app:items")
    public static void historyItemsObservable(RecyclerView view, ObservableField<List<HistoryItem>> items) {
        if (items != null) {
            historyItems(view, items.get());
        }
    }

    @BindingAdapter("app:items")
    public static void setDetectedVideoInfos(RecyclerView view, List<VideoInfo> items) {
        VideoInfoAdapter adapter = (VideoInfoAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(items != null ? items : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setDetectedVideoInfosSet(RecyclerView view, Set<VideoInfo> items) {
        VideoInfoAdapter adapter = (VideoInfoAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(items != null ? new ArrayList<>(items) : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setDetectedVideoInfosSetObservable(RecyclerView view, ObservableField<Set<VideoInfo>> items) {
        if (items != null) {
            setDetectedVideoInfosSet(view, items.get());
        }
    }

    @BindingAdapter("app:items")
    public static void setBookmarks(RecyclerView view, List<PageInfo> items) {
        BookmarksAdapter adapter = (BookmarksAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(items != null ? items : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setBookmarksObservable(RecyclerView view, ObservableField<? extends List<PageInfo>> items) {
        if (items != null) {
            setBookmarks(view, items.get());
        }
    }
}
