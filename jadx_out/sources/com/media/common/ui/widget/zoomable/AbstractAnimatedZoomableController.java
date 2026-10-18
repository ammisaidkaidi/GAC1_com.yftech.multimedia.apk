package com.media.common.ui.widget.zoomable;

import android.graphics.Matrix;
import android.graphics.PointF;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.Nullsafe;
import com.media.common.ui.widget.zoomable.gestures.TransformGestureDetector;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public abstract class AbstractAnimatedZoomableController extends DefaultZoomableController {
    private final float[] mCurrentValues;
    private boolean mIsAnimating;
    private final Matrix mNewTransform;
    private final float[] mStartValues;
    private final float[] mStopValues;
    private final Matrix mWorkingTransform;

    public AbstractAnimatedZoomableController(TransformGestureDetector transformGestureDetector) {
        super(transformGestureDetector);
        this.mStartValues = new float[9];
        this.mStopValues = new float[9];
        this.mCurrentValues = new float[9];
        this.mNewTransform = new Matrix();
        this.mWorkingTransform = new Matrix();
    }

    private void setTransformImmediate(Matrix matrix) {
        FLog.v(getLogTag(), "setTransformImmediate");
        stopAnimation();
        this.mWorkingTransform.set(matrix);
        super.setTransform(matrix);
        getDetector().restartGesture();
    }

    protected void calculateInterpolation(Matrix matrix, float f) {
        for (int i = 0; i < 9; i++) {
            this.mCurrentValues[i] = ((1.0f - f) * this.mStartValues[i]) + (this.mStopValues[i] * f);
        }
        matrix.setValues(this.mCurrentValues);
    }

    protected abstract Class<?> getLogTag();

    protected float[] getStartValues() {
        return this.mStartValues;
    }

    protected float[] getStopValues() {
        return this.mStopValues;
    }

    protected Matrix getWorkingTransform() {
        return this.mWorkingTransform;
    }

    protected boolean isAnimating() {
        return this.mIsAnimating;
    }

    @Override // com.media.common.ui.widget.zoomable.DefaultZoomableController, com.media.common.ui.widget.zoomable.ZoomableController
    public boolean isIdentity() {
        return !isAnimating() && super.isIdentity();
    }

    @Override // com.media.common.ui.widget.zoomable.DefaultZoomableController, com.media.common.ui.widget.zoomable.gestures.TransformGestureDetector.Listener
    public void onGestureBegin(TransformGestureDetector transformGestureDetector) {
        FLog.v(getLogTag(), "onGestureBegin");
        stopAnimation();
        super.onGestureBegin(transformGestureDetector);
    }

    @Override // com.media.common.ui.widget.zoomable.DefaultZoomableController, com.media.common.ui.widget.zoomable.gestures.TransformGestureDetector.Listener
    public void onGestureUpdate(TransformGestureDetector transformGestureDetector) {
        FLog.v(getLogTag(), "onGestureUpdate %s", isAnimating() ? "(ignored)" : "");
        if (isAnimating()) {
            return;
        }
        super.onGestureUpdate(transformGestureDetector);
    }

    @Override // com.media.common.ui.widget.zoomable.DefaultZoomableController
    public void reset() {
        FLog.v(getLogTag(), "reset");
        stopAnimation();
        this.mWorkingTransform.reset();
        this.mNewTransform.reset();
        super.reset();
    }

    protected void setAnimating(boolean z) {
        this.mIsAnimating = z;
    }

    public void setTransform(Matrix matrix, long j, @Nullable Runnable runnable) {
        FLog.v(getLogTag(), "setTransform: duration %d ms", Long.valueOf(j));
        if (j <= 0) {
            setTransformImmediate(matrix);
        } else {
            setTransformAnimated(matrix, j, runnable);
        }
    }

    public abstract void setTransformAnimated(Matrix matrix, long j, @Nullable Runnable runnable);

    protected abstract void stopAnimation();

    @Override // com.media.common.ui.widget.zoomable.DefaultZoomableController
    public void zoomToPoint(float f, PointF pointF, PointF pointF2) {
        zoomToPoint(f, pointF, pointF2, 7, 0L, null);
    }

    public void zoomToPoint(float f, PointF pointF, PointF pointF2, int i, long j, @Nullable Runnable runnable) {
        FLog.v(getLogTag(), "zoomToPoint: duration %d ms", Long.valueOf(j));
        calculateZoomToPointTransform(this.mNewTransform, f, pointF, pointF2, i);
        setTransform(this.mNewTransform, j, runnable);
    }
}
