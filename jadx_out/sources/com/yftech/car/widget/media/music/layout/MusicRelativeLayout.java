package com.yftech.car.widget.media.music.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import com.yftech.car.utils.Loger;

/* JADX INFO: loaded from: classes3.dex */
public class MusicRelativeLayout extends RelativeLayout {
    private static final String TAG = "MusicRelativeLayout";
    private MusicLayoutChildManager mLayoutChildManager;

    public MusicRelativeLayout(Context context) {
        super(context);
    }

    public MusicRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MusicRelativeLayout(Context context, AttributeSet attributeSet, int i) {
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
            this.mLayoutChildManager = new MusicLayoutChildManager();
        }
        Loger.d(TAG, "onAttachedToWindow: ");
        this.mLayoutChildManager.initView(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Loger.d(TAG, "onDetachedFromWindow: ");
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

    public void release() {
        Loger.d(TAG, "release: ");
        if (this.mLayoutChildManager != null) {
            this.mLayoutChildManager.unregister();
        }
    }
}
