package com.yftech.car.widget.media.photo.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yftech.car.utils.Loger;

/* JADX INFO: loaded from: classes3.dex */
public class PhotoFrameLayout extends FrameLayout {
    private static final String TAG = "VideoFrameLayout";
    private PhotoLayoutChildManager mLayoutChildManager;

    public PhotoFrameLayout(@NonNull Context context) {
        super(context);
    }

    public PhotoFrameLayout(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PhotoFrameLayout(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Loger.d(TAG, " onAttachedToWindow: ");
        if (this.mLayoutChildManager == null) {
            this.mLayoutChildManager = new PhotoLayoutChildManager();
        }
        this.mLayoutChildManager.initView(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Loger.d(TAG, " onDetachedFromWindow: ");
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
