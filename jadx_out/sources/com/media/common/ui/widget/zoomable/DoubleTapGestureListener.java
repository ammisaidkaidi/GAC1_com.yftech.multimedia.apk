package com.media.common.ui.widget.zoomable;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.facebook.infer.annotation.Nullsafe;
import com.media.common.util.LogUtil;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class DoubleTapGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final int DOUBLE_TAP_SCROLL_THRESHOLD = 20;
    private static final int DURATION_MS = 300;
    private final ZoomableDraweeView mDraweeView;
    private OnSingleClickListener mListener;
    private final PointF mDoubleTapViewPoint = new PointF();
    private final PointF mDoubleTapImagePoint = new PointF();
    private float mDoubleTapScale = 1.0f;
    private boolean mDoubleTapScroll = false;

    public interface OnSingleClickListener {
        void onDoubleClickDown();

        void onDoubleTapMoving();

        void onNext();

        void onPrev();

        void onSingleClick();
    }

    public DoubleTapGestureListener(ZoomableDraweeView zoomableDraweeView) {
        this.mDraweeView = zoomableDraweeView;
    }

    private float calcScale(PointF pointF) {
        float f = pointF.y - this.mDoubleTapViewPoint.y;
        float fAbs = (Math.abs(f) * 0.001f) + 1.0f;
        return f < 0.0f ? this.mDoubleTapScale / fAbs : this.mDoubleTapScale * fAbs;
    }

    private boolean shouldStartDoubleTapScroll(PointF pointF) {
        return Math.hypot((double) (pointF.x - this.mDoubleTapViewPoint.x), (double) (pointF.y - this.mDoubleTapViewPoint.y)) > 20.0d;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
    public boolean onDoubleTapEvent(MotionEvent motionEvent) {
        AbstractAnimatedZoomableController abstractAnimatedZoomableController = (AbstractAnimatedZoomableController) this.mDraweeView.getZoomableController();
        PointF pointF = new PointF(motionEvent.getX(), motionEvent.getY());
        PointF pointFMapViewToImage = abstractAnimatedZoomableController.mapViewToImage(pointF);
        switch (motionEvent.getActionMasked()) {
            case 0:
                this.mDoubleTapViewPoint.set(pointF);
                this.mDoubleTapImagePoint.set(pointFMapViewToImage);
                this.mDoubleTapScale = abstractAnimatedZoomableController.getScaleFactor();
                return true;
            case 1:
                if (this.mDoubleTapScroll) {
                    abstractAnimatedZoomableController.zoomToPoint(calcScale(pointF), this.mDoubleTapImagePoint, this.mDoubleTapViewPoint);
                } else if (abstractAnimatedZoomableController.getScaleFactor() < 1.25f) {
                    abstractAnimatedZoomableController.zoomToPoint(1.5f, pointFMapViewToImage, pointF, 7, 300L, null);
                } else {
                    abstractAnimatedZoomableController.zoomToPoint(1.0f, pointFMapViewToImage, pointF, 7, 300L, null);
                }
                this.mDoubleTapScroll = false;
                return true;
            case 2:
                this.mDoubleTapScroll = this.mDoubleTapScroll || shouldStartDoubleTapScroll(pointF);
                if (this.mDoubleTapScroll) {
                    abstractAnimatedZoomableController.zoomToPoint(calcScale(pointF), this.mDoubleTapImagePoint, this.mDoubleTapViewPoint);
                }
                return true;
            default:
                return true;
        }
    }

    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) throws Throwable {
        LogUtil.e(" velocityX = " + f + "  velocityY = " + f2);
        if (f < -1000.0f) {
            if (this.mListener != null) {
                this.mListener.onNext();
            }
            return true;
        }
        if (f <= 1000.0f) {
            return false;
        }
        if (this.mListener != null) {
            this.mListener.onPrev();
        }
        return true;
    }

    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        if (this.mListener != null) {
            this.mListener.onDoubleTapMoving();
        }
        return super.onScroll(motionEvent, motionEvent2, f, f2);
    }

    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        if (this.mListener == null) {
            return true;
        }
        this.mListener.onSingleClick();
        return true;
    }

    public void setOnSingleClick(OnSingleClickListener onSingleClickListener) {
        this.mListener = onSingleClickListener;
    }
}
