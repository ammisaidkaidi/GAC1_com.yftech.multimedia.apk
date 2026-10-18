package com.yftech.car.widget.media.video.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/* JADX INFO: loaded from: classes3.dex */
public class VideoRelativeLayout extends RelativeLayout {
    private static final String TAG = "VideoRelativeLayout";
    private VideoLayoutChildManager mLayoutChildManager;

    public VideoRelativeLayout(Context context) {
        super(context);
    }

    public VideoRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public VideoRelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.mLayoutChildManager.dispatchTouchEvent(motionEvent)) {
            return true;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mLayoutChildManager == null) {
            this.mLayoutChildManager = new VideoLayoutChildManager();
        }
        this.mLayoutChildManager.initView(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mLayoutChildManager != null) {
            this.mLayoutChildManager.unregister();
        }
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (this.mLayoutChildManager != null) {
            this.mLayoutChildManager.onWindowFocusChanged(z);
        }
    }
}
