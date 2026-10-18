package com.facebook.drawee.drawable;

import android.annotation.TargetApi;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class ForwardingDrawable extends Drawable implements Drawable.Callback, TransformCallback, TransformAwareDrawable, DrawableParent {
    private static final Matrix sTempTransform = new Matrix();

    @Nullable
    private Drawable mCurrentDelegate;
    private final DrawableProperties mDrawableProperties = new DrawableProperties();

    @Nullable
    protected TransformCallback mTransformCallback;

    public ForwardingDrawable(@Nullable Drawable drawable) {
        this.mCurrentDelegate = drawable;
        DrawableUtils.setCallbacks(this.mCurrentDelegate, this, this);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mCurrentDelegate != null) {
            this.mCurrentDelegate.draw(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    @Nullable
    public Drawable.ConstantState getConstantState() {
        return this.mCurrentDelegate == null ? super.getConstantState() : this.mCurrentDelegate.getConstantState();
    }

    @Override // android.graphics.drawable.Drawable
    @Nullable
    public Drawable getCurrent() {
        return this.mCurrentDelegate;
    }

    @Override // com.facebook.drawee.drawable.DrawableParent
    @Nullable
    public Drawable getDrawable() {
        return getCurrent();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mCurrentDelegate == null ? super.getIntrinsicHeight() : this.mCurrentDelegate.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mCurrentDelegate == null ? super.getIntrinsicWidth() : this.mCurrentDelegate.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        if (this.mCurrentDelegate == null) {
            return 0;
        }
        return this.mCurrentDelegate.getOpacity();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        return this.mCurrentDelegate == null ? super.getPadding(rect) : this.mCurrentDelegate.getPadding(rect);
    }

    protected void getParentTransform(Matrix matrix) {
        if (this.mTransformCallback != null) {
            this.mTransformCallback.getTransform(matrix);
        } else {
            matrix.reset();
        }
    }

    @Override // com.facebook.drawee.drawable.TransformCallback
    public void getRootBounds(RectF rectF) {
        if (this.mTransformCallback != null) {
            this.mTransformCallback.getRootBounds(rectF);
        } else {
            rectF.set(getBounds());
        }
    }

    @Override // com.facebook.drawee.drawable.TransformCallback
    public void getTransform(Matrix matrix) {
        getParentTransform(matrix);
    }

    public void getTransformedBounds(RectF rectF) {
        getParentTransform(sTempTransform);
        rectF.set(getBounds());
        sTempTransform.mapRect(rectF);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        if (this.mCurrentDelegate == null) {
            return false;
        }
        return this.mCurrentDelegate.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (this.mCurrentDelegate != null) {
            this.mCurrentDelegate.mutate();
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        if (this.mCurrentDelegate != null) {
            this.mCurrentDelegate.setBounds(rect);
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        return this.mCurrentDelegate == null ? super.onLevelChange(i) : this.mCurrentDelegate.setLevel(i);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        return this.mCurrentDelegate == null ? super.onStateChange(iArr) : this.mCurrentDelegate.setState(iArr);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        scheduleSelf(runnable, j);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mDrawableProperties.setAlpha(i);
        if (this.mCurrentDelegate != null) {
            this.mCurrentDelegate.setAlpha(i);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mDrawableProperties.setColorFilter(colorFilter);
        if (this.mCurrentDelegate != null) {
            this.mCurrentDelegate.setColorFilter(colorFilter);
        }
    }

    @Nullable
    public Drawable setCurrent(@Nullable Drawable drawable) {
        Drawable currentWithoutInvalidate = setCurrentWithoutInvalidate(drawable);
        invalidateSelf();
        return currentWithoutInvalidate;
    }

    @Nullable
    protected Drawable setCurrentWithoutInvalidate(@Nullable Drawable drawable) {
        Drawable drawable2 = this.mCurrentDelegate;
        DrawableUtils.setCallbacks(drawable2, null, null);
        DrawableUtils.setCallbacks(drawable, null, null);
        DrawableUtils.setDrawableProperties(drawable, this.mDrawableProperties);
        DrawableUtils.copyProperties(drawable, this);
        DrawableUtils.setCallbacks(drawable, this, this);
        this.mCurrentDelegate = drawable;
        return drawable2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setDither(boolean z) {
        this.mDrawableProperties.setDither(z);
        if (this.mCurrentDelegate != null) {
            this.mCurrentDelegate.setDither(z);
        }
    }

    @Override // com.facebook.drawee.drawable.DrawableParent
    public Drawable setDrawable(@Nullable Drawable drawable) {
        return setCurrent(drawable);
    }

    @Override // android.graphics.drawable.Drawable
    public void setFilterBitmap(boolean z) {
        this.mDrawableProperties.setFilterBitmap(z);
        if (this.mCurrentDelegate != null) {
            this.mCurrentDelegate.setFilterBitmap(z);
        }
    }

    @Override // android.graphics.drawable.Drawable
    @TargetApi(21)
    public void setHotspot(float f, float f2) {
        if (this.mCurrentDelegate != null) {
            this.mCurrentDelegate.setHotspot(f, f2);
        }
    }

    @Override // com.facebook.drawee.drawable.TransformAwareDrawable
    public void setTransformCallback(TransformCallback transformCallback) {
        this.mTransformCallback = transformCallback;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        return this.mCurrentDelegate == null ? super.setVisible(z, z2) : this.mCurrentDelegate.setVisible(z, z2);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        unscheduleSelf(runnable);
    }
}
