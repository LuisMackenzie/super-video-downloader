package com.mackenzie.downhub.ui.component.binding;

import android.graphics.Bitmap;

import androidx.databinding.BindingAdapter;

import com.google.android.material.imageview.ShapeableImageView;

public class ShapeableImageBinding {

    @BindingAdapter("app:srcCompat")
    public static void setImageDrawable(ShapeableImageView view, int drawable) {
        view.setImageResource(drawable);
    }

    @BindingAdapter("app:bitmap")
    public static void setImageBitmap(ShapeableImageView view, Bitmap bitmap) {
        if (bitmap != null) {
            view.setImageBitmap(bitmap);
        }
    }
}
