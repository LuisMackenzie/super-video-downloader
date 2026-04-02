package com.mackenzie.downhub.ui.component.binding;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import android.widget.AutoCompleteTextView;
import com.mackenzie.downhub.data.local.model.Suggestion;
import com.mackenzie.downhub.data.local.room.entity.HistoryItem;
import com.mackenzie.downhub.ui.component.adapter.SuggestionAdapter;
import com.mackenzie.downhub.ui.component.adapter.TabSuggestionAdapter;
import java.util.Collections;
import java.util.List;

public class AutoCompleteTextViewBinding {

    @BindingAdapter("app:items")
    public static void setSuggestions(AutoCompleteTextView view, List<Suggestion> items) {
        SuggestionAdapter adapter = (SuggestionAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(items != null ? items : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setSuggestionsObservable(AutoCompleteTextView view, ObservableField<? extends List<Suggestion>> items) {
        setSuggestions(view, items != null ? items.get() : null);
    }

    @BindingAdapter("app:items")
    public static void setTabSuggestions(AutoCompleteTextView view, List<HistoryItem> items) {
        TabSuggestionAdapter adapter = (TabSuggestionAdapter) view.getAdapter();
        if (adapter != null) {
            adapter.setData(items != null ? items : Collections.emptyList());
        }
    }

    @BindingAdapter("app:items")
    public static void setTabSuggestionsObservable(AutoCompleteTextView view, ObservableField<? extends List<HistoryItem>> items) {
        setTabSuggestions(view, items != null ? items.get() : null);
    }
}
