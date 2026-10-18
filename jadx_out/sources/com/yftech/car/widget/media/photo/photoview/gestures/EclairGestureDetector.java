package com.yftech.car.widget.media.photo.photoview.gestures;

import android.annotation.TargetApi;
import android.content.Context;
import android.view.MotionEvent;
import com.yftech.car.widget.media.photo.photoview.Compat;

/* JADX INFO: loaded from: classes3.dex */
@TargetApi(5)
public class EclairGestureDetector extends CupcakeGestureDetector {
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId;
    private int mActivePointerIndex;

    public EclairGestureDetector(Context context) {
        super(context);
        this.mActivePointerId = -1;
        this.mActivePointerIndex = 0;
    }

    @Override // com.yftech.car.widget.media.photo.photoview.gestures.CupcakeGestureDetector
    float getActiveX(MotionEvent motionEvent) {
        try {
            return motionEvent.getX(this.mActivePointerIndex);
        } catch (Exception unused) {
            return motionEvent.getX();
        }
    }

    @Override // com.yftech.car.widget.media.photo.photoview.gestures.CupcakeGestureDetector
    float getActiveY(MotionEvent motionEvent) {
        try {
            return motionEvent.getY(this.mActivePointerIndex);
        } catch (Exception unused) {
            return motionEvent.getY();
        }
    }

    /* JADX WARN: Code duplicated, block: B:15:0x0041  */
    @Override // com.yftech.car.widget.media.photo.photoview.gestures.CupcakeGestureDetector, com.yftech.car.widget.media.photo.photoview.gestures.GestureDetector
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction() & 255;
        if (action == 3) {
            this.mActivePointerId = -1;
        } else if (action != 6) {
            switch (action) {
                case 0:
                    this.mActivePointerId = motionEvent.getPointerId(0);
                    break;
                case 1:
                    this.mActivePointerId = -1;
                    break;
            }
        } else {
            int pointerIndex = Compat.getPointerIndex(motionEvent.getAction());
            if (motionEvent.getPointerId(pointerIndex) == this.mActivePointerId) {
                int i = pointerIndex == 0 ? 1 : 0;
                this.mActivePointerId = motionEvent.getPointerId(i);
                this.mLastTouchX = motionEvent.getX(i);
                this.mLastTouchY = motionEvent.getY(i);
            }
        }
        this.mActivePointerIndex = motionEvent.findPointerIndex(this.mActivePointerId != -1 ? this.mActivePointerId : 0);
        return super.onTouchEvent(motionEvent);
    }
}
