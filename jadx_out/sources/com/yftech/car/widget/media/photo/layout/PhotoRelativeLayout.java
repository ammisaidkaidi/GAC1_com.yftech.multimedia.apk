package com.yftech.car.widget.media.photo.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/* JADX INFO: loaded from: classes3.dex */
public class PhotoRelativeLayout extends RelativeLayout {
    private PhotoLayoutChildManager mLayoutChildManager;

    public PhotoRelativeLayout(Context context) {
        super(context);
    }

    public PhotoRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PhotoRelativeLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mLayoutChildManager == null) {
            this.mLayoutChildManager = new PhotoLayoutChildManager();
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
