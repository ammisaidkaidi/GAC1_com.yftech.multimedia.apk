package com.yftech.car.widget.media.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.yftech.car.widget.media.R;

/* JADX INFO: loaded from: classes3.dex */
public class SideTextRetrievalView extends View {
    private static final String TAG = "SideTextRetrievalView";
    private boolean isTouchDown;
    private float mBackgroundHeight;
    private final int mBackgroundStartOffset;
    private final int mBackgroundTopOffset;
    private float mBackgroundWidth;
    private final float mHeightText;
    private final Drawable mHighlightDrawable;
    private final int mHighlightDrawableStartOffset;
    private float mHighlightHeight;
    private final float mHighlightMeasureText;
    private final int mHighlightTextStartOffset;
    private float mHighlightWidth;
    private boolean mInitStatus;
    private final float mMeasureText;
    private final int mMediaDataListId;
    private float mMovePositionY;
    private OnSideTextRetrievalListener mOnSideTextRetrievalListener;
    private int mPosition;
    private String mSortContent;
    private final Drawable mSortDrawable;
    private final Paint mSortPaint;
    private final Paint mSortPaintHighlight;
    private final Paint mSortPaintSelected;
    private String mSortShow;
    private float mSortTextStart;
    private final int mSortTextStartOffset;

    public interface OnSideTextRetrievalListener {
        void onTouchText(SideTextRetrievalView sideTextRetrievalView, char c, int i);
    }

    private SideTextRetrievalView(Context context) {
        this(context, null, 0, 0);
    }

    public SideTextRetrievalView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0, 0);
    }

    public SideTextRetrievalView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SideTextRetrievalView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mSortPaint = new Paint();
        this.mSortPaintSelected = new Paint();
        this.mSortPaintHighlight = new Paint();
        this.mPosition = 0;
        this.mInitStatus = true;
        this.isTouchDown = false;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SideTextRetrievalView, i, i2);
        this.mSortContent = typedArrayObtainStyledAttributes.getString(R.styleable.SideTextRetrievalView_side_sort_content);
        if (this.mSortContent == null || this.mSortContent.length() < 3) {
            this.mSortContent = "*ABCDEFGHIJKLMNOPQRSTUVWXYZ#";
            Log.w(TAG, "SideTextRetrievalView: sort content error,side_sort_content == " + this.mSortContent + " --> " + this.mSortContent);
        }
        this.mSortShow = typedArrayObtainStyledAttributes.getString(R.styleable.SideTextRetrievalView_side_sort_show);
        if (this.mSortShow == null || this.mSortShow.length() != this.mSortContent.length()) {
            Log.w(TAG, "SideTextRetrievalView: side_sort_show content length error , side_sort_show : " + this.mSortShow + " --> " + this.mSortContent);
            this.mSortShow = this.mSortContent;
        }
        Resources resources = context.getResources();
        int color = resources.getColor(17170443, null);
        int color2 = resources.getColor(17170457, null);
        int color3 = resources.getColor(17170455, null);
        int dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.SideTextRetrievalView_side_sort_text_size, 20);
        int color4 = typedArrayObtainStyledAttributes.getColor(R.styleable.SideTextRetrievalView_side_sort_text_color, color);
        int color5 = typedArrayObtainStyledAttributes.getColor(R.styleable.SideTextRetrievalView_side_sort_text_selected_color, color2);
        int dimensionPixelSize2 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.SideTextRetrievalView_side_sort_highlight_text_size, 24);
        int color6 = typedArrayObtainStyledAttributes.getColor(R.styleable.SideTextRetrievalView_side_sort_highlight_text_color, color3);
        this.mHighlightDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.SideTextRetrievalView_side_sort_highlight_drawable);
        this.mSortDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.SideTextRetrievalView_side_sort_text_background);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SideTextRetrievalView_side_sort_anti_alias, false);
        boolean z2 = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SideTextRetrievalView_side_sort_fake_bold_text, false);
        this.mSortPaint.setColor(color4);
        float f = dimensionPixelSize;
        this.mSortPaint.setTextSize(f);
        this.mSortPaint.setAntiAlias(z);
        this.mSortPaint.setTextAlign(Paint.Align.CENTER);
        this.mSortPaint.setFakeBoldText(z2);
        this.mSortPaintSelected.setColor(color5);
        this.mSortPaintSelected.setTextSize(f);
        this.mSortPaintSelected.setAntiAlias(z);
        this.mSortPaintSelected.setTextAlign(Paint.Align.CENTER);
        this.mSortPaintSelected.setFakeBoldText(z2);
        this.mSortPaintHighlight.setColor(color6);
        this.mSortPaintHighlight.setTextSize(dimensionPixelSize2);
        this.mSortPaintHighlight.setAntiAlias(typedArrayObtainStyledAttributes.getBoolean(R.styleable.SideTextRetrievalView_side_sort_highlight_anti_alias, false));
        this.mSortPaintHighlight.setTextAlign(Paint.Align.CENTER);
        this.mSortPaintHighlight.setFakeBoldText(typedArrayObtainStyledAttributes.getBoolean(R.styleable.SideTextRetrievalView_side_sort_highlight_fake_bold_text, false));
        this.mHighlightWidth = typedArrayObtainStyledAttributes.getDimension(R.styleable.SideTextRetrievalView_side_sort_highlight_width, 0.0f);
        this.mHighlightHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.SideTextRetrievalView_side_sort_highlight_height, 0.0f);
        this.mBackgroundWidth = typedArrayObtainStyledAttributes.getDimension(R.styleable.SideTextRetrievalView_side_sort_text_background_width, 0.0f);
        this.mBackgroundHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.SideTextRetrievalView_side_sort_text_background_height, 0.0f);
        this.mBackgroundStartOffset = typedArrayObtainStyledAttributes.getDimensionPixelOffset(R.styleable.SideTextRetrievalView_side_sort_text_background_start_offset, 0);
        this.mBackgroundTopOffset = typedArrayObtainStyledAttributes.getDimensionPixelOffset(R.styleable.SideTextRetrievalView_side_sort_text_background_top_offset, 0);
        this.mMediaDataListId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.SideTextRetrievalView_side_sort_media_data_list_id, -1);
        this.mHighlightDrawableStartOffset = typedArrayObtainStyledAttributes.getDimensionPixelOffset(R.styleable.SideTextRetrievalView_side_sort_highlight_drawable_start_offset, 0);
        this.mHighlightTextStartOffset = typedArrayObtainStyledAttributes.getDimensionPixelOffset(R.styleable.SideTextRetrievalView_side_sort_highlight_text_start_offset, 0);
        this.mSortTextStartOffset = typedArrayObtainStyledAttributes.getDimensionPixelOffset(R.styleable.SideTextRetrievalView_side_sort_text_start_offset, 0);
        this.mMeasureText = this.mSortPaint.measureText(String.valueOf(this.mSortShow.charAt(1)));
        this.mSortPaint.getFontMetrics();
        Rect rect = new Rect();
        this.mSortPaint.getTextBounds(this.mSortShow, 0, this.mSortShow.length(), rect);
        this.mHeightText = rect.height();
        if (this.mHighlightWidth < 1.0f || this.mHighlightHeight < 1.0f) {
            Rect rect2 = new Rect();
            if (this.mHighlightDrawable == null || this.mHighlightDrawable.getIntrinsicWidth() <= 0) {
                this.mSortPaintHighlight.getTextBounds(this.mSortShow, 0, this.mSortShow.length(), rect2);
                this.mHighlightHeight = rect2.height();
                this.mHighlightWidth = this.mSortPaintHighlight.measureText(String.valueOf(this.mSortShow.charAt(1)));
            } else {
                this.mHighlightWidth = this.mHighlightDrawable.getIntrinsicWidth();
                this.mHighlightHeight = this.mHighlightDrawable.getIntrinsicHeight();
            }
        }
        this.mHighlightMeasureText = this.mSortPaintHighlight.measureText(String.valueOf(this.mSortShow.charAt(1)));
        this.mSortTextStart = this.mHighlightWidth;
        this.mInitStatus = true;
        this.isTouchDown = false;
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return onTouchEvent(motionEvent);
    }

    public boolean isMediaDataListId(int i) {
        return this.mMediaDataListId == -1 || this.mMediaDataListId == i;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int length = this.mSortShow.length();
        int width = getWidth();
        int height = getHeight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingStart = getPaddingStart();
        if (paddingStart == 0) {
            paddingStart = getPaddingLeft();
        }
        int i = (height - (paddingBottom + paddingTop)) / length;
        float f = (this.mHighlightDrawable != null ? this.mSortTextStart + (this.mMeasureText / 2.0f) : width / 2.0f) + this.mSortTextStartOffset;
        if (this.mSortDrawable != null) {
            this.mSortDrawable.draw(canvas);
        }
        int i2 = 0;
        while (i2 < length) {
            canvas.drawText(String.valueOf(this.mSortShow.charAt(i2)), paddingStart + f, (i2 * i) + paddingTop + this.mHeightText, this.mPosition == i2 ? this.mSortPaintSelected : this.mSortPaint);
            i2++;
        }
        if (this.mHighlightDrawable == null || !this.isTouchDown) {
            return;
        }
        this.mHighlightDrawable.draw(canvas);
        canvas.drawText(String.valueOf(this.mSortContent.charAt(this.mPosition)), (this.mHighlightMeasureText / 2.0f) + paddingStart + this.mHighlightTextStartOffset, this.mMovePositionY + (this.mHighlightMeasureText / 2.0f), this.mSortPaintHighlight);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        int width = getWidth();
        if (width > 0) {
            if (this.mInitStatus) {
                this.mInitStatus = false;
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                Log.w(TAG, "onSizeChanged: width < mMeasureText : " + width + " < " + this.mMeasureText);
                if (width < this.mMeasureText) {
                    if (this.mHighlightDrawable == null) {
                        layoutParams.width = (int) this.mMeasureText;
                    } else if (this.mHighlightWidth > 0.0f) {
                        layoutParams.width = (int) (this.mHighlightWidth + this.mMeasureText);
                    } else {
                        layoutParams.width = (int) (this.mHighlightDrawable.getIntrinsicWidth() + this.mMeasureText);
                    }
                }
                int height = getHeight();
                if (height < this.mHeightText * this.mSortShow.length()) {
                    Log.w(TAG, "onSizeChanged: height < sortHeight : " + height + " < " + (this.mHeightText * this.mSortShow.length()));
                    layoutParams.height = (int) (this.mHeightText * ((float) this.mSortShow.length()));
                }
                requestLayout();
            }
            if (this.mSortDrawable != null) {
                if (this.mBackgroundWidth < 1.0f || this.mBackgroundHeight < 1.0f) {
                    this.mBackgroundWidth = this.mSortDrawable.getIntrinsicWidth();
                    this.mBackgroundHeight = this.mSortDrawable.getIntrinsicHeight();
                    if (this.mBackgroundWidth < 1.0f || this.mBackgroundHeight < 1.0f) {
                        this.mBackgroundWidth = this.mMeasureText * 2.0f;
                        this.mBackgroundHeight = getHeight() - getPaddingTop();
                    }
                }
                float f = ((this.mHighlightDrawable != null ? this.mSortTextStart : width / 2.0f) - (this.mMeasureText / 2.0f)) + this.mBackgroundStartOffset;
                this.mSortDrawable.setBounds((int) f, this.mBackgroundTopOffset, (int) (f + this.mBackgroundWidth), ((int) this.mBackgroundHeight) + this.mBackgroundTopOffset);
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        float width;
        float f;
        switch (motionEvent.getAction()) {
            case 0:
            case 2:
                this.isTouchDown = true;
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                int paddingStart = getPaddingStart();
                if (paddingStart == 0) {
                    paddingStart = getPaddingLeft();
                }
                if (this.mHighlightDrawable != null) {
                    width = this.mSortTextStart;
                    f = this.mMeasureText / 2.0f;
                } else {
                    width = getWidth() / 2.0f;
                    f = this.mMeasureText;
                }
                if (x > (width - f) + this.mSortTextStartOffset + paddingStart) {
                    int paddingTop = getPaddingTop();
                    float f2 = paddingTop;
                    if (y > f2) {
                        int length = this.mSortContent.length();
                        int height = getHeight();
                        int paddingBottom = (height - (paddingTop + getPaddingBottom())) / length;
                        this.mMovePositionY = y;
                        float f3 = this.mHighlightHeight / 2.0f;
                        if (this.mMovePositionY < f3) {
                            this.mMovePositionY = f3;
                        }
                        float f4 = height;
                        float f5 = f4 - f3;
                        if (this.mMovePositionY > f5) {
                            this.mMovePositionY = f5;
                        }
                        int i = (int) ((y - f2) / paddingBottom);
                        if (i >= 0 && i < length && this.mPosition != i) {
                            this.mPosition = i;
                            if (this.mOnSideTextRetrievalListener != null) {
                                this.mOnSideTextRetrievalListener.onTouchText(this, this.mSortContent.toLowerCase().charAt(i), i);
                            }
                        }
                        if (this.mHighlightDrawable != null) {
                            int i2 = (int) (y - (this.mHighlightHeight / 2.0f));
                            if (i2 < 0) {
                                i2 = 0;
                            }
                            if (i2 > f4 - this.mHighlightHeight) {
                                i2 = (int) (f4 - this.mHighlightHeight);
                            }
                            this.mHighlightDrawable.setBounds(this.mHighlightDrawableStartOffset + paddingStart, i2, (int) (this.mHighlightWidth + paddingStart + this.mHighlightDrawableStartOffset), (int) (i2 + this.mHighlightHeight));
                        }
                        invalidate();
                    }
                }
                return true;
            case 1:
            case 3:
                this.isTouchDown = false;
                invalidate();
                return true;
            default:
                return true;
        }
    }

    public void setCurrent(char c) {
        if (this.isTouchDown) {
            return;
        }
        String lowerCase = this.mSortContent.toLowerCase();
        for (int i = 0; i < this.mSortContent.length(); i++) {
            if (lowerCase.charAt(i) == c) {
                this.mPosition = i;
                invalidate();
                return;
            } else {
                if (c > 'z') {
                    this.mPosition = this.mSortContent.length() - 1;
                    invalidate();
                    return;
                }
            }
        }
    }

    public void setCurrent(String str) {
        if (TextUtils.isEmpty(str)) {
            Log.e(TAG, "setCurrent: content is null !!!");
        } else {
            setCurrent(str.charAt(0));
        }
    }

    public void setOnSideTextRetrievalListener(OnSideTextRetrievalListener onSideTextRetrievalListener) {
        this.mOnSideTextRetrievalListener = onSideTextRetrievalListener;
    }
}
