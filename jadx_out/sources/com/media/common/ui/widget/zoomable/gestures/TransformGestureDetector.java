package com.media.common.ui.widget.zoomable.gestures;

import android.view.MotionEvent;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class TransformGestureDetector implements MultiPointerGestureDetector.Listener {
    private final MultiPointerGestureDetector mDetector;

    @Nullable
    private Listener mListener = null;

    public interface Listener {
        void onGestureBegin(TransformGestureDetector transformGestureDetector);

        void onGestureEnd(TransformGestureDetector transformGestureDetector);

        void onGestureUpdate(TransformGestureDetector transformGestureDetector);
    }

    public TransformGestureDetector(MultiPointerGestureDetector multiPointerGestureDetector) {
        this.mDetector = multiPointerGestureDetector;
        this.mDetector.setListener(this);
    }

    private float calcAverage(float[] fArr, int i) {
        float f = 0.0f;
        for (int i2 = 0; i2 < i; i2++) {
            f += fArr[i2];
        }
        if (i > 0) {
            return f / i;
        }
        return 0.0f;
    }

    public static TransformGestureDetector newInstance() {
        return new TransformGestureDetector(MultiPointerGestureDetector.newInstance());
    }

    public int getNewPointerCount() {
        return this.mDetector.getNewPointerCount();
    }

    public float getPivotX() {
        return calcAverage(this.mDetector.getStartX(), this.mDetector.getPointerCount());
    }

    public float getPivotY() {
        return calcAverage(this.mDetector.getStartY(), this.mDetector.getPointerCount());
    }

    public int getPointerCount() {
        return this.mDetector.getPointerCount();
    }

    public float getRotation() {
        if (this.mDetector.getPointerCount() < 2) {
            return 0.0f;
        }
        float f = this.mDetector.getStartX()[1] - this.mDetector.getStartX()[0];
        float f2 = this.mDetector.getStartY()[1] - this.mDetector.getStartY()[0];
        float f3 = this.mDetector.getCurrentX()[1] - this.mDetector.getCurrentX()[0];
        return ((float) Math.atan2(this.mDetector.getCurrentY()[1] - this.mDetector.getCurrentY()[0], f3)) - ((float) Math.atan2(f2, f));
    }

    public float getScale() {
        if (this.mDetector.getPointerCount() < 2) {
            return 1.0f;
        }
        float f = this.mDetector.getStartX()[1] - this.mDetector.getStartX()[0];
        float f2 = this.mDetector.getStartY()[1] - this.mDetector.getStartY()[0];
        return ((float) Math.hypot(this.mDetector.getCurrentX()[1] - this.mDetector.getCurrentX()[0], this.mDetector.getCurrentY()[1] - this.mDetector.getCurrentY()[0])) / ((float) Math.hypot(f, f2));
    }

    public float getTranslationX() {
        return calcAverage(this.mDetector.getCurrentX(), this.mDetector.getPointerCount()) - calcAverage(this.mDetector.getStartX(), this.mDetector.getPointerCount());
    }

    public float getTranslationY() {
        return calcAverage(this.mDetector.getCurrentY(), this.mDetector.getPointerCount()) - calcAverage(this.mDetector.getStartY(), this.mDetector.getPointerCount());
    }

    public boolean isGestureInProgress() {
        return this.mDetector.isGestureInProgress();
    }

    @Override // com.media.common.ui.widget.zoomable.gestures.MultiPointerGestureDetector.Listener
    public void onGestureBegin(MultiPointerGestureDetector multiPointerGestureDetector) {
        if (this.mListener != null) {
            this.mListener.onGestureBegin(this);
        }
    }

    @Override // com.media.common.ui.widget.zoomable.gestures.MultiPointerGestureDetector.Listener
    public void onGestureEnd(MultiPointerGestureDetector multiPointerGestureDetector) {
        if (this.mListener != null) {
            this.mListener.onGestureEnd(this);
        }
    }

    @Override // com.media.common.ui.widget.zoomable.gestures.MultiPointerGestureDetector.Listener
    public void onGestureUpdate(MultiPointerGestureDetector multiPointerGestureDetector) {
        if (this.mListener != null) {
            this.mListener.onGestureUpdate(this);
        }
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.mDetector.onTouchEvent(motionEvent);
    }

    public void reset() {
        this.mDetector.reset();
    }

    public void restartGesture() {
        this.mDetector.restartGesture();
    }

    public void setListener(Listener listener) {
        this.mListener = listener;
    }
}
