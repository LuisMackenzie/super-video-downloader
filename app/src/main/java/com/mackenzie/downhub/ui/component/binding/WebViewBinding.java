package com.mackenzie.downhub.ui.component.binding;

import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.databinding.BindingAdapter;

public class WebViewBinding {

    @BindingAdapter("app:loadUrl")
    public static void loadUrl(WebView view, String url) {
        if (url != null && !url.isEmpty()) {
            view.loadUrl(url);
        }
    }

    @BindingAdapter("app:javaScriptEnabled")
    public static void javaScriptEnabled(WebView view, Boolean isEnabled) {
        if (isEnabled != null) {
            view.getSettings().setJavaScriptEnabled(isEnabled);
        }
    }

    @BindingAdapter("app:addJavascriptInterface")
    public static void addJavascriptInterface(WebView view, String name) {
        if (name != null) {
            view.addJavascriptInterface(view.getContext(), name);
        }
    }

    @BindingAdapter("app:webViewClient")
    public static void setWebViewClient(WebView view, WebViewClient webViewClient) {
        if (webViewClient != null) {
            view.setWebViewClient(webViewClient);
        }
    }

    @BindingAdapter("app:webChromeClient")
    public static void setWebChromeClient(WebView view, WebChromeClient webChromeClient) {
        if (webChromeClient != null) {
            view.setWebChromeClient(webChromeClient);
        }
    }
}
