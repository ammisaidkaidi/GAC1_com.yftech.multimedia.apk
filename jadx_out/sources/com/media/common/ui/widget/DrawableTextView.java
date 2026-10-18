package com.media.common.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class DrawableTextView extends TextView {
    private final int LEFT;
    private final int RIGHT;
    private Drawable drawableBottom;
    private int drawableHeight;
    private Drawable drawableLeft;
    private Drawable drawableRight;
    private Drawable drawableTop;
    private int drawableWidth;
    private OnDrawableLeftListener leftListener;
    private OnDrawableRightListener rightListener;

    public interface OnDrawableLeftListener {
        void onLeftClick(View view);
    }

    public interface OnDrawableRightListener {
        void onRightClick(View view);
    }

    public DrawableTextView(Context context) {
        this(context, null);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DrawableTextView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.drawableLeft = null;
        this.drawableTop = null;
        this.drawableRight = null;
        this.drawableBottom = null;
        this.LEFT = 0;
        this.RIGHT = 2;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.DrawableTextView);
        int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
        for (int i2 = 0; i2 < indexCount; i2++) {
            int index = typedArrayObtainStyledAttributes.getIndex(i2);
            if (index == R.styleable.DrawableTextView_drawableRight) {
                this.drawableRight = typedArrayObtainStyledAttributes.getDrawable(index);
            } else if (index == R.styleable.DrawableTextView_drawableLeft) {
                this.drawableLeft = typedArrayObtainStyledAttributes.getDrawable(index);
            } else if (index == R.styleable.DrawableTextView_drawableTop) {
                this.drawableTop = typedArrayObtainStyledAttributes.getDrawable(index);
            } else if (index == R.styleable.DrawableTextView_drawableBottom) {
                this.drawableBottom = typedArrayObtainStyledAttributes.getDrawable(index);
            } else if (index == R.styleable.DrawableTextView_drawableWidth) {
                this.drawableWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(index, 0);
            } else if (index == R.styleable.DrawableTextView_drawableHeight) {
                this.drawableHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(index, 0);
            }
        }
        typedArrayObtainStyledAttributes.recycle();
        if (this.drawableLeft != null) {
            this.drawableLeft.setBounds(0, 0, this.drawableWidth, this.drawableHeight);
        }
        if (this.drawableRight != null) {
            this.drawableRight.setBounds(0, 0, this.drawableWidth, this.drawableHeight);
        }
        if (this.drawableTop != null) {
            this.drawableTop.setBounds(0, 0, this.drawableWidth, this.drawableHeight);
        }
        if (this.drawableBottom != null) {
            this.drawableBottom.setBounds(0, 0, this.drawableWidth, this.drawableHeight);
        }
        setCompoundDrawables(this.drawableLeft, this.drawableTop, this.drawableRight, this.drawableBottom);
    }

    @Override // android.widget.TextView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 1) {
            Drawable drawable = getCompoundDrawables()[0];
            if (drawable != null && motionEvent.getRawX() <= getLeft() + drawable.getBounds().width() && this.leftListener != null) {
                this.leftListener.onLeftClick(this);
            }
            Drawable drawable2 = getCompoundDrawables()[2];
            if (drawable2 == null || motionEvent.getRawX() < getRight() - drawable2.getBounds().width() || this.leftListener == null) {
                performClick();
            } else {
                this.rightListener.onRightClick(this);
            }
        }
        return true;
    }

    public void setOnDrawableLeftListener(OnDrawableLeftListener onDrawableLeftListener) {
        this.leftListener = onDrawableLeftListener;
    }

    public void setOnDrawableRightListener(OnDrawableRightListener onDrawableRightListener) {
        this.rightListener = onDrawableRightListener;
    }
}
