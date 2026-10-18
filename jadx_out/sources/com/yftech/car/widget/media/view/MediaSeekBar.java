package com.yftech.car.widget.media.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatSeekBar;

/* JADX INFO: loaded from: classes3.dex */
public class MediaSeekBar extends AppCompatSeekBar {
    private boolean isTouchSeekBar;

    public MediaSeekBar(Context context) {
        super(context);
        this.isTouchSeekBar = false;
    }

    public MediaSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.isTouchSeekBar = false;
    }

    public MediaSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isTouchSeekBar = false;
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.widget.AbsSeekBar, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent != null && motionEvent.getAction() == 1) {
            playSoundEffect(0);
        }
        return super.onTouchEvent(motionEvent);
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int i) {
        if (!this.isTouchSeekBar) {
            super.setProgress(i);
        }
    }

    public void setTouchSeekBar(boolean z) {
        this.isTouchSeekBar = z;
    }
}
