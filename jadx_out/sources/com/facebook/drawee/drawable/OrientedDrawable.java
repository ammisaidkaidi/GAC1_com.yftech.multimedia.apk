package com.facebook.drawee.drawable;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.VisibleForTesting;

/* JADX INFO: loaded from: classes3.dex */
public class OrientedDrawable extends ForwardingDrawable {
    private int mExifOrientation;
    private int mRotationAngle;

    @VisibleForTesting
    final Matrix mRotationMatrix;
    private final Matrix mTempMatrix;
    private final RectF mTempRectF;

    public OrientedDrawable(Drawable drawable, int i) {
        this(drawable, i, 0);
    }

    public OrientedDrawable(Drawable drawable, int i, int i2) {
        super(drawable);
        this.mTempMatrix = new Matrix();
        this.mTempRectF = new RectF();
        this.mRotationMatrix = new Matrix();
        this.mRotationAngle = i - (i % 90);
        this.mExifOrientation = (i2 < 0 || i2 > 8) ? 0 : i2;
    }

    @Override // com.facebook.drawee.drawable.ForwardingDrawable, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mRotationAngle <= 0 && (this.mExifOrientation == 0 || this.mExifOrientation == 1)) {
            super.draw(canvas);
            return;
        }
        int iSave = canvas.save();
        canvas.concat(this.mRotationMatrix);
        super.draw(canvas);
        canvas.restoreToCount(iSave);
    }

    @Override // com.facebook.drawee.drawable.ForwardingDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return (this.mExifOrientation == 5 || this.mExifOrientation == 7 || this.mRotationAngle % 180 != 0) ? super.getIntrinsicWidth() : super.getIntrinsicHeight();
    }

    @Override // com.facebook.drawee.drawable.ForwardingDrawable, android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return (this.mExifOrientation == 5 || this.mExifOrientation == 7 || this.mRotationAngle % 180 != 0) ? super.getIntrinsicHeight() : super.getIntrinsicWidth();
    }

    @Override // com.facebook.drawee.drawable.ForwardingDrawable, com.facebook.drawee.drawable.TransformCallback
    public void getTransform(Matrix matrix) {
        getParentTransform(matrix);
        if (this.mRotationMatrix.isIdentity()) {
            return;
        }
        matrix.preConcat(this.mRotationMatrix);
    }

    @Override // com.facebook.drawee.drawable.ForwardingDrawable, android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        Drawable current = getCurrent();
        if (this.mRotationAngle <= 0 && (this.mExifOrientation == 0 || this.mExifOrientation == 1)) {
            current.setBounds(rect);
            return;
        }
        int i = this.mExifOrientation;
        if (i == 2) {
            this.mRotationMatrix.setScale(-1.0f, 1.0f);
        } else if (i != 7) {
            switch (i) {
                case 4:
                    this.mRotationMatrix.setScale(1.0f, -1.0f);
                    break;
                case 5:
                    this.mRotationMatrix.setRotate(270.0f, rect.centerX(), rect.centerY());
                    this.mRotationMatrix.postScale(1.0f, -1.0f);
                    break;
                default:
                    this.mRotationMatrix.setRotate(this.mRotationAngle, rect.centerX(), rect.centerY());
                    break;
            }
        } else {
            this.mRotationMatrix.setRotate(270.0f, rect.centerX(), rect.centerY());
            this.mRotationMatrix.postScale(-1.0f, 1.0f);
        }
        this.mTempMatrix.reset();
        this.mRotationMatrix.invert(this.mTempMatrix);
        this.mTempRectF.set(rect);
        this.mTempMatrix.mapRect(this.mTempRectF);
        current.setBounds((int) this.mTempRectF.left, (int) this.mTempRectF.top, (int) this.mTempRectF.right, (int) this.mTempRectF.bottom);
    }
}
