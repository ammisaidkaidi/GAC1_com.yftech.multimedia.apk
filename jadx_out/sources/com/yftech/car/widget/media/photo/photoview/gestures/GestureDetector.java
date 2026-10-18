package com.yftech.car.widget.media.photo.photoview.gestures;

import android.view.MotionEvent;

/* JADX INFO: loaded from: classes3.dex */
public interface GestureDetector {
    boolean isScaling();

    boolean onTouchEvent(MotionEvent motionEvent);

    void setOnGestureListener(OnGestureListener onGestureListener);
}
