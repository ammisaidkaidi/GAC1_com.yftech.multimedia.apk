package com.yftech.car.widget.media.radio.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yftech.car.utils.Loger;

/* JADX INFO: loaded from: classes3.dex */
public class RadioFrameLayout extends FrameLayout {
    private static final String TAG = "RadioFrameLayout";
    private LayoutChildManager mLayoutChildManager;

    public RadioFrameLayout(@NonNull Context context) {
        super(context);
    }

    public RadioFrameLayout(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RadioFrameLayout(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mLayoutChildManager == null) {
            this.mLayoutChildManager = new LayoutChildManager();
        }
        this.mLayoutChildManager.initView(this);
        Loger.d(TAG, " onAttachedToWindow: ");
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
