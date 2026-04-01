package com.mackenzie.downhub.ui.component.binding;

import android.net.Uri;
import android.widget.VideoView;

import androidx.core.content.FileProvider;
import androidx.databinding.BindingAdapter;

import java.io.File;

public class VideoViewBinding {

    @BindingAdapter("app:videoURI")
    public static void setVideoURI(VideoView view, String videoPath) {
        if (videoPath != null) {
            Uri uri;
            if (videoPath.startsWith("http")) {
                uri = Uri.parse(videoPath);
            } else {
                uri = FileProvider.getUriForFile(
                        view.getContext(),
                        view.getContext().getPackageName() + ".provider",
                        new File(videoPath)
                );
            }
            view.setVideoURI(uri);
        }
    }
}
