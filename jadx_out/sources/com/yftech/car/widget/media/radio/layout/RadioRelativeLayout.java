package com.yftech.car.widget.media.radio.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/* JADX INFO: loaded from: classes3.dex */
public class RadioRelativeLayout extends RelativeLayout {
    private LayoutChildManager mLayoutChildManager;

    public RadioRelativeLayout(Context context) {
        super(context);
    }

    public RadioRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public RadioRelativeLayout(Context context, AttributeSet attributeSet, int i) {
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
