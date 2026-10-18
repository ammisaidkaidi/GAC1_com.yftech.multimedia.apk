package com.facebook.drawee.gestures;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import androidx.annotation.VisibleForTesting;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class GestureDetector {

    @VisibleForTesting
    long mActionDownTime;

    @VisibleForTesting
    float mActionDownX;

    @VisibleForTesting
    float mActionDownY;

    @Nullable
    @VisibleForTesting
    ClickListener mClickListener;

    @VisibleForTesting
    boolean mIsCapturingGesture;

    @VisibleForTesting
    boolean mIsClickCandidate;

    @VisibleForTesting
    final float mSingleTapSlopPx;

    public interface ClickListener {
        boolean onClick();
    }

    public GestureDetector(Context context) {
        this.mSingleTapSlopPx = ViewConfiguration.get(context).getScaledTouchSlop();
        init();
    }

    public static GestureDetector newInstance(Context context) {
        return new GestureDetector(context);
    }

    public void init() {
        this.mClickListener = null;
        reset();
    }

    public boolean isCapturingGesture() {
        return this.mIsCapturingGesture;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                this.mIsCapturingGesture = true;
                this.mIsClickCandidate = true;
                this.mActionDownTime = motionEvent.getEventTime();
                this.mActionDownX = motionEvent.getX();
                this.mActionDownY = motionEvent.getY();
                return true;
            case 1:
                this.mIsCapturingGesture = false;
                if (Math.abs(motionEvent.getX() - this.mActionDownX) > this.mSingleTapSlopPx || Math.abs(motionEvent.getY() - this.mActionDownY) > this.mSingleTapSlopPx) {
                    this.mIsClickCandidate = false;
                }
                if (this.mIsClickCandidate && motionEvent.getEventTime() - this.mActionDownTime <= ViewConfiguration.getLongPressTimeout() && this.mClickListener != null) {
                    this.mClickListener.onClick();
                }
                this.mIsClickCandidate = false;
                return true;
            case 2:
                if (Math.abs(motionEvent.getX() - this.mActionDownX) > this.mSingleTapSlopPx || Math.abs(motionEvent.getY() - this.mActionDownY) > this.mSingleTapSlopPx) {
                    this.mIsClickCandidate = false;
                }
                return true;
            case 3:
                this.mIsCapturingGesture = false;
                this.mIsClickCandidate = false;
                return true;
            default:
                return true;
        }
    }

    public void reset() {
        this.mIsCapturingGesture = false;
        this.mIsClickCandidate = false;
    }

    public void setClickListener(ClickListener clickListener) {
        this.mClickListener = clickListener;
    }
}
