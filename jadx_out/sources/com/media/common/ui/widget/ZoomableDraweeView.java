package com.media.common.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import com.facebook.drawee.view.SimpleDraweeView;

/* JADX INFO: loaded from: classes3.dex */
public class ZoomableDraweeView extends SimpleDraweeView {
    private OnClickListener mClickListener;
    private Matrix mCurrentMatrix;
    private float mCurrentScale;
    private GestureDetector mGestureDetector;
    private float mMidX;
    private float mMidY;
    private ScaleGestureDetector mScaleDetector;

    public interface OnClickListener {
        void onClick();
    }

    public ZoomableDraweeView(Context context) {
        super(context);
        this.mCurrentScale = 1.0f;
        init();
    }

    public ZoomableDraweeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCurrentScale = 1.0f;
        init();
    }

    public ZoomableDraweeView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurrentScale = 1.0f;
        init();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkBorder() {
        boolean z;
        float right;
        RectF displayRect = getDisplayRect(this.mCurrentMatrix);
        float height = 0.0f;
        if (displayRect.left > 0.0f) {
            right = getLeft() - displayRect.left;
            z = true;
        } else {
            z = false;
            right = 0.0f;
        }
        if (displayRect.top > 0.0f) {
            height = getTop() - displayRect.top;
            z = true;
        }
        if (displayRect.right < getRight()) {
            right = getRight() - displayRect.right;
            z = true;
        }
        if (displayRect.bottom < getHeight()) {
            height = getHeight() - displayRect.bottom;
            z = true;
        }
        if (z) {
            this.mCurrentMatrix.postTranslate(right, height);
            invalidate();
        }
    }

    private RectF getDisplayRect(Matrix matrix) {
        RectF rectF = new RectF(getLeft(), getTop(), getRight(), getBottom());
        matrix.mapRect(rectF);
        return rectF;
    }

    private void init() {
        this.mCurrentMatrix = new Matrix();
        this.mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() { // from class: com.media.common.ui.widget.ZoomableDraweeView.1
            @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
            public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
                float scaleFactor = scaleGestureDetector.getScaleFactor();
                ZoomableDraweeView.this.mCurrentScale *= scaleFactor;
                if (ZoomableDraweeView.this.mMidX == 0.0f) {
                    ZoomableDraweeView.this.mMidX = ZoomableDraweeView.this.getWidth() / 2.0f;
                }
                if (ZoomableDraweeView.this.mMidY == 0.0f) {
                    ZoomableDraweeView.this.mMidY = ZoomableDraweeView.this.getHeight() / 2.0f;
                }
                ZoomableDraweeView.this.mCurrentMatrix.postScale(scaleFactor, scaleFactor, ZoomableDraweeView.this.mMidX, ZoomableDraweeView.this.mMidY);
                ZoomableDraweeView.this.invalidate();
                return true;
            }

            @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
            public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
                super.onScaleEnd(scaleGestureDetector);
                if (ZoomableDraweeView.this.mCurrentScale < 1.0f) {
                    ZoomableDraweeView.this.reset();
                }
                ZoomableDraweeView.this.checkBorder();
            }
        });
        this.mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() { // from class: com.media.common.ui.widget.ZoomableDraweeView.2
            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (ZoomableDraweeView.this.mCurrentScale <= 1.0f) {
                    return true;
                }
                ZoomableDraweeView.this.mCurrentMatrix.postTranslate(-f, -f2);
                ZoomableDraweeView.this.invalidate();
                ZoomableDraweeView.this.checkBorder();
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
            public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
                if (ZoomableDraweeView.this.mClickListener == null) {
                    return true;
                }
                ZoomableDraweeView.this.mClickListener.onClick();
                return true;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reset() {
        this.mCurrentMatrix.reset();
        this.mCurrentScale = 1.0f;
        invalidate();
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDraw(Canvas canvas) {
        int iSave = canvas.save();
        canvas.concat(this.mCurrentMatrix);
        super.onDraw(canvas);
        canvas.restoreToCount(iSave);
    }

    @Override // com.facebook.drawee.view.DraweeView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        this.mScaleDetector.onTouchEvent(motionEvent);
        if (this.mScaleDetector.isInProgress()) {
            return true;
        }
        this.mGestureDetector.onTouchEvent(motionEvent);
        return true;
    }

    @Override // com.facebook.drawee.view.DraweeView, android.widget.ImageView
    public void setImageBitmap(Bitmap bitmap) {
        reset();
        super.setImageBitmap(bitmap);
    }

    @Override // com.facebook.drawee.view.SimpleDraweeView, com.facebook.drawee.view.DraweeView, android.widget.ImageView
    public void setImageURI(Uri uri) {
        reset();
        super.setImageURI(uri);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.mClickListener = onClickListener;
    }
}
