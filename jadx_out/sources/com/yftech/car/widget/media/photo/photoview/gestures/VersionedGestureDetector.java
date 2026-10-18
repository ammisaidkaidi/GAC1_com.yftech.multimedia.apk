package com.yftech.car.widget.media.photo.photoview.gestures;

import android.content.Context;
import android.os.Build;

/* JADX INFO: loaded from: classes3.dex */
public final class VersionedGestureDetector {
    public static GestureDetector newInstance(Context context, OnGestureListener onGestureListener) {
        GestureDetector eclairGestureDetector;
        int i = Build.VERSION.SDK_INT;
        if (i < 5) {
            eclairGestureDetector = new CupcakeGestureDetector(context);
        } else {
            eclairGestureDetector = i < 8 ? new EclairGestureDetector(context) : new FroyoGestureDetector(context);
        }
        eclairGestureDetector.setOnGestureListener(onGestureListener);
        return eclairGestureDetector;
    }
}
