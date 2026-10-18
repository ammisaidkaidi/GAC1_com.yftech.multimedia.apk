package com.yftech.car.widget.media.radio.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.yftech.car.media.radio.entity.DataOfRadio;
import com.yftech.car.utils.Loger;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.manager.RadioManager;
import com.yftech.libradio.Utils;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public class RulerView extends View {
    public static final int BAND_AM = 2;
    public static final int BAND_FM = 0;
    public static final int MOD_TYPE_SCALE = 5;
    private static final String TAG = "RulerView";
    private boolean isDisallowIntercept;
    public boolean isScaleGradient;
    private boolean isScrollingPerformed;
    public boolean isShowScaleValue;
    public int mBand;
    public int mCurrentValue;
    private float mDownFocusX;
    private float mDownFocusY;
    public int mMaxScaleColor;
    private int mMaxScaleHeight;
    public float mMaxScaleHeightRatio;
    public float mMaxScaleWidth;
    public int mMaxValue;
    public int mMidScaleColor;
    private int mMidScaleHeight;
    public float mMidScaleWidth;
    private Bitmap mMiddleImg;
    private Paint mMiddleImgPaint;
    public int mMinScaleColor;
    private int mMinScaleHeight;
    public float mMinScaleHeightRatio;
    public float mMinScaleWidth;
    public int mMinValue;
    public int mScaleBase;
    private Paint mScalePaint;
    private int mScaleSpace;
    public int mScaleValueColor;
    private TextPaint mScaleValuePaint;
    public float mScaleValueSize;
    private float mTpDesiredWidth;
    private OnRulerViewScrollListener onWheelListener;
    private RulerViewScroller scroller;
    private final RulerViewScroller.ScrollingListener scrollingListener;
    private int scrollingOffset;

    public interface OnRulerViewScrollListener<T> {
        void onChanged(RulerView rulerView, String str, String str2);

        void onScrollingFinished(RulerView rulerView, int i);

        void onScrollingStarted(RulerView rulerView);
    }

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public RulerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mScaleBase = 1;
        this.mScaleSpace = 20;
        this.mBand = 0;
        this.scrollingListener = new RulerViewScroller.ScrollingListener() { // from class: com.yftech.car.widget.media.radio.view.RulerView.1
            @Override // com.yftech.car.widget.media.radio.view.RulerViewScroller.ScrollingListener
            public void onFinished() {
                if (RulerView.this.outOfRange()) {
                    return;
                }
                if (RulerView.this.isScrollingPerformed) {
                    if (RulerView.this.onWheelListener != null) {
                        RulerView.this.onWheelListener.onScrollingFinished(RulerView.this, Math.min(Math.max(RulerView.this.mMinValue, RulerView.this.mCurrentValue * RulerView.this.mScaleBase), RulerView.this.mMaxValue));
                    }
                    RulerView.this.isScrollingPerformed = false;
                }
                RulerView.this.scrollingOffset = 0;
                RulerView.this.invalidate();
            }

            @Override // com.yftech.car.widget.media.radio.view.RulerViewScroller.ScrollingListener
            public void onJustify() {
                if (!RulerView.this.outOfRange() && Math.abs(RulerView.this.scrollingOffset) > 1) {
                    if (RulerView.this.scrollingOffset < (-RulerView.this.mScaleSpace) / 2) {
                        RulerView.this.scroller.scroll(RulerView.this.mScaleSpace + RulerView.this.scrollingOffset, 0);
                    } else if (RulerView.this.scrollingOffset > RulerView.this.mScaleSpace / 2) {
                        RulerView.this.scroller.scroll(RulerView.this.scrollingOffset - RulerView.this.mScaleSpace, 0);
                    } else {
                        RulerView.this.scroller.scroll(RulerView.this.scrollingOffset, 0);
                    }
                }
            }

            @Override // com.yftech.car.widget.media.radio.view.RulerViewScroller.ScrollingListener
            public void onScroll(int i2) {
                RulerView.this.doScroll(i2);
            }

            @Override // com.yftech.car.widget.media.radio.view.RulerViewScroller.ScrollingListener
            public void onStarted() {
                RulerView.this.isScrollingPerformed = true;
                if (RulerView.this.onWheelListener != null) {
                    RulerView.this.onWheelListener.onScrollingStarted(RulerView.this);
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.RulerView);
        if (isEnabled()) {
            this.mMaxScaleColor = getResources().getColor(17170443);
            this.mMidScaleColor = getResources().getColor(17170443);
            this.mMinScaleColor = getResources().getColor(17170443);
            this.mScaleValueColor = getResources().getColor(17170443);
            this.mMiddleImg = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_cursor_normal);
        } else {
            this.mMaxScaleColor = getResources().getColor(17170456);
            this.mMidScaleColor = getResources().getColor(17170456);
            this.mMinScaleColor = getResources().getColor(17170456);
            this.mScaleValueColor = getResources().getColor(17170456);
        }
        this.mMaxScaleWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.RulerView_mMaxScaleWidth, 15);
        this.mMidScaleWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.RulerView_mMidScaleWidth, 12);
        this.mMinScaleWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.RulerView_mMinScaleWidth, 10);
        this.mScaleValueSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.RulerView_mScaleValueSize, 12);
        this.mScaleSpace = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.RulerView_mScaleSpace, 20);
        this.mMaxScaleHeightRatio = typedArrayObtainStyledAttributes.getFloat(R.styleable.RulerView_mMaxScaleHeightRatio, 0.3f);
        this.mMinScaleHeightRatio = typedArrayObtainStyledAttributes.getFloat(R.styleable.RulerView_mMinScaleHeightRatio, 0.1f);
        this.isShowScaleValue = typedArrayObtainStyledAttributes.getBoolean(R.styleable.RulerView_isShowScaleValue, true);
        this.isScaleGradient = typedArrayObtainStyledAttributes.getBoolean(R.styleable.RulerView_isScaleGradient, true);
        int max = RadioManager.getInstance().getMax(0);
        this.mMaxValue = typedArrayObtainStyledAttributes.getInteger(R.styleable.RulerView_mMaxValue, max == -1 ? Utils.SHARED_KEY_FM_MAX_DEFAULT : max);
        int min = RadioManager.getInstance().getMin(0);
        this.mMinValue = typedArrayObtainStyledAttributes.getInteger(R.styleable.RulerView_mMinValue, min == -1 ? Utils.SHARED_KEY_FM_MIN_DEFAULT : min);
        this.mScaleBase = typedArrayObtainStyledAttributes.getInteger(R.styleable.RulerView_mScaleBase, 1);
        this.mCurrentValue = typedArrayObtainStyledAttributes.getInteger(R.styleable.RulerView_mCurrentValue, 0);
        setCurrentValue(this.mCurrentValue);
        this.mMiddleImg = BitmapFactory.decodeResource(getResources(), typedArrayObtainStyledAttributes.getResourceId(R.styleable.RulerView_mMiddleImg, R.mipmap.ic_cursor_normal));
        typedArrayObtainStyledAttributes.recycle();
        this.mScalePaint = new Paint(1);
        this.mScalePaint.setStyle(Paint.Style.STROKE);
        this.mScalePaint.setAntiAlias(true);
        this.mScaleValuePaint = new TextPaint(1);
        this.mScaleValuePaint.setColor(this.mScaleValueColor);
        this.mScaleValuePaint.setTextSize(this.mScaleValueSize);
        this.mScaleValuePaint.setTextAlign(Paint.Align.CENTER);
        this.mTpDesiredWidth = Layout.getDesiredWidth("0", this.mScaleValuePaint);
        this.mMiddleImgPaint = new Paint(1);
        this.mMiddleImgPaint.setStyle(Paint.Style.STROKE);
        this.mMiddleImgPaint.setAntiAlias(true);
        this.scroller = new RulerViewScroller(context, this.scrollingListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doScroll(int i) {
        this.scrollingOffset += i;
        int i2 = this.scrollingOffset / this.mScaleSpace;
        if (i2 != 0) {
            int iMin = Math.min(Math.max(this.mMinValue, this.mCurrentValue * this.mScaleBase), this.mMaxValue);
            this.mCurrentValue -= i2;
            this.scrollingOffset -= i2 * this.mScaleSpace;
            if (this.mBand == 0 && this.mCurrentValue > this.mMaxValue / this.mScaleBase) {
                this.mCurrentValue = (this.mMinValue / this.mScaleBase) + (this.mCurrentValue - (this.mMaxValue / this.mScaleBase));
            }
            if (this.mBand == 0 && this.mCurrentValue < this.mMinValue / this.mScaleBase) {
                this.mCurrentValue = (this.mMaxValue / this.mScaleBase) - ((this.mMinValue / this.mScaleBase) - this.mCurrentValue);
            }
            if (this.mBand == 2 && this.mCurrentValue > this.mMaxValue / this.mScaleBase) {
                this.mCurrentValue = (this.mMinValue / this.mScaleBase) + (this.mCurrentValue - (this.mMaxValue / this.mScaleBase));
            }
            if (this.mBand == 2 && this.mCurrentValue < this.mMinValue / this.mScaleBase) {
                this.mCurrentValue = (this.mMaxValue / this.mScaleBase) - ((this.mMinValue / this.mScaleBase) - this.mCurrentValue);
            }
            Log.v(TAG, "mScaleBase:" + this.mScaleBase);
            Loger.v(TAG, "mCurrentValue:" + this.mCurrentValue);
            Log.v(TAG, "scrollingOffset:" + this.scrollingOffset);
            if (this.onWheelListener != null) {
                int iMin2 = Math.min(Math.max(this.mMinValue, this.mCurrentValue * this.mScaleBase), this.mMaxValue);
                this.onWheelListener.onChanged(this, iMin + "", iMin2 + "");
            }
        }
        invalidate();
    }

    private void drawMiddleImg(Canvas canvas, int i, int i2) {
        canvas.drawBitmap(this.mMiddleImg, (i - this.mMiddleImg.getWidth()) / 2, ((int) (this.mScaleValuePaint.getTextSize() / 2.0f)) + (this.mMiddleImg.getHeight() / 2), this.mMiddleImgPaint);
    }

    private void drawScaleLine(Canvas canvas, float f, int i, int i2, float f2, float f3, float f4, float f5) {
        this.mScalePaint.setStrokeWidth(f);
        this.mScalePaint.setColor(i);
        this.mScalePaint.setAlpha(i2);
        canvas.drawLine(f2, f3, f4, f5, this.mScalePaint);
        int iAbs = (int) Math.abs(f5 - f3);
        float f6 = f3 + 20.0f;
        this.mScalePaint.setColor(Color.rgb(126, 126, 126));
        canvas.drawLine(f2, f6, f4, f6 + iAbs, this.mScalePaint);
    }

    private void drawScaleLine(Canvas canvas, int i, int i2) {
        drawScaleLine(canvas, ((int) Math.ceil((i / 2.0f) / this.mScaleSpace)) + 2, this.scrollingOffset, this.mCurrentValue, i, i2);
    }

    private void drawScaleLine(Canvas canvas, int i, int i2, int i3, int i4, int i5) {
        int i6;
        int textSize = (((int) ((i5 - this.mTpDesiredWidth) - this.mScaleValuePaint.getTextSize())) - getPaddingBottom()) - 10;
        for (int i7 = 0; i7 < i; i7++) {
            float f = i4;
            float f2 = f / 2.0f;
            float f3 = i2;
            float f4 = (this.mScaleSpace * i7) + f2 + f3;
            int i8 = i3 + i7;
            if (this.mBand == 0 && i8 > this.mMaxValue / this.mScaleBase) {
                i8 = ((this.mMinValue / this.mScaleBase) + (i8 - (this.mMaxValue / this.mScaleBase))) - 1;
            }
            int i9 = (this.mBand != 2 || i8 <= this.mMaxValue / this.mScaleBase) ? i8 : ((this.mMinValue / this.mScaleBase) + (i8 - (this.mMaxValue / this.mScaleBase))) - 1;
            if (f4 <= f) {
                i6 = 2;
                drawScaleLine(canvas, i9, f4, textSize, i, i7, i5);
            } else {
                i6 = 2;
            }
            float f5 = (f2 - (this.mScaleSpace * i7)) + f3;
            int i10 = i3 - i7;
            if (this.mBand == 0 && i10 < this.mMinValue / this.mScaleBase) {
                i10 = ((this.mMaxValue / this.mScaleBase) - ((this.mMinValue / this.mScaleBase) - i10)) + 1;
            }
            int i11 = (this.mBand != i6 || i10 >= this.mMinValue / this.mScaleBase) ? i10 : ((this.mMaxValue / this.mScaleBase) - ((this.mMinValue / this.mScaleBase) - i10)) + 1;
            if (f5 > getPaddingLeft()) {
                drawScaleLine(canvas, i11, f5, textSize, i, i7, i5);
            }
        }
    }

    private int getAlpha(int i, int i2) {
        if (this.isScaleGradient) {
            return (255 / i) * (i - i2);
        }
        return 255;
    }

    private int measureHeightSize(int i) {
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (mode == 1073741824) {
            return size;
        }
        int height = ((int) (this.mMiddleImg.getHeight() + getPaddingTop() + getPaddingBottom() + (this.mScaleValuePaint.getTextSize() * 2.0f))) + 5;
        return mode == Integer.MIN_VALUE ? Math.min(height, size) : height;
    }

    private int measureWidthSize(int i) {
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        if (mode == 1073741824) {
            return size;
        }
        if (mode == Integer.MIN_VALUE) {
            return Math.min(400, size);
        }
        return 400;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean outOfRange() {
        int i;
        if (this.mCurrentValue < this.mMinValue / this.mScaleBase) {
            i = (this.mCurrentValue - (this.mMinValue / this.mScaleBase)) * this.mScaleSpace;
        } else {
            i = this.mCurrentValue > this.mMaxValue / this.mScaleBase ? (this.mCurrentValue - (this.mMaxValue / this.mScaleBase)) * this.mScaleSpace : 0;
        }
        if (i == 0) {
            return false;
        }
        this.scrollingOffset = 0;
        this.scroller.scroll(-i, 100);
        return true;
    }

    private void setBand(int i) {
        if (i == this.mBand) {
            Log.e(TAG, "setBand: band == mBand");
            return;
        }
        this.mBand = i;
        if (i == 2) {
            int max = RadioManager.getInstance().getMax(2);
            if (max == -1) {
                max = 1620;
            }
            setMaxValue(max);
            int min = RadioManager.getInstance().getMin(2);
            if (min == -1) {
                min = 522;
            }
            setMinValue(min);
            int step = RadioManager.getInstance().getStep(2);
            if (step == -1) {
                step = 9;
            }
            setStep(step);
        } else {
            int max2 = RadioManager.getInstance().getMax(0);
            if (max2 == -1) {
                max2 = Utils.SHARED_KEY_FM_MAX_DEFAULT;
            }
            setMaxValue(max2);
            int min2 = RadioManager.getInstance().getMin(0);
            if (min2 == -1) {
                min2 = Utils.SHARED_KEY_FM_MIN_DEFAULT;
            }
            setMinValue(min2);
            int step2 = RadioManager.getInstance().getStep(0);
            if (step2 == -1) {
                step2 = 100;
            }
            setStep(step2);
        }
        invalidate();
    }

    public void drawScaleLine(Canvas canvas, int i, float f, int i2, int i3, int i4, int i5) {
        if (i % 5 != 0) {
            drawScaleLine(canvas, this.mMinScaleWidth, this.mMinScaleColor, getAlpha(i3, i4), f, i2, f, i2 - this.mMinScaleHeight);
            return;
        }
        if (i % 10 != 0) {
            drawScaleLine(canvas, this.mMinScaleWidth, this.mMinScaleColor, getAlpha(i3, i4), f, i2, f, i2 - this.mMinScaleHeight);
            return;
        }
        drawScaleLine(canvas, this.mMaxScaleWidth, this.mMaxScaleColor, getAlpha(i3, i4), f, i2, f, i2 - this.mMaxScaleHeight);
        if (this.isShowScaleValue) {
            this.mScaleValuePaint.setAlpha(getAlpha(i3, i4));
            if (this.mBand != 0) {
                String strValueOf = String.valueOf(this.mScaleBase * i);
                if (this.mMinValue == 522 && strValueOf.equals("540")) {
                    canvas.drawText(strValueOf, 12.0f + f, (i2 - this.mMaxScaleHeight) - 10, this.mScaleValuePaint);
                    return;
                } else if (this.mMinValue == 522 && strValueOf.equals("1620")) {
                    canvas.drawText(strValueOf, f - 18.0f, (i2 - this.mMaxScaleHeight) - 10, this.mScaleValuePaint);
                    return;
                } else {
                    canvas.drawText(strValueOf, f, (i2 - this.mMaxScaleHeight) - 10, this.mScaleValuePaint);
                    return;
                }
            }
            if (this.mScaleBase == 100) {
                canvas.drawText(String.valueOf((this.mScaleBase * i) / 1000), f, (i2 - this.mMaxScaleHeight) - 10, this.mScaleValuePaint);
                return;
            }
            String str = String.format(Locale.US, "%.2f", Float.valueOf((this.mScaleBase * i) / 1000.0f));
            Log.d(TAG, "drawScaleLine tx:" + str);
            if (str.equals("87.50")) {
                canvas.drawText(str, 22.0f + f, (i2 - this.mMaxScaleHeight) - 10, this.mScaleValuePaint);
            } else if (str.equals("108.00")) {
                canvas.drawText(str, f - 26.0f, (i2 - this.mMaxScaleHeight) - 10, this.mScaleValuePaint);
            } else {
                canvas.drawText(str, f, (i2 - this.mMaxScaleHeight) - 10, this.mScaleValuePaint);
            }
        }
    }

    public int getCurrentValue() {
        return Math.min(Math.max(0, this.mCurrentValue * this.mScaleBase), this.mMaxValue);
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    public int getStep() {
        return this.mScaleBase;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int measuredWidth = (getMeasuredWidth() - getPaddingLeft()) - getPaddingRight();
        int measuredHeight = (getMeasuredHeight() - getPaddingTop()) - getPaddingBottom();
        drawScaleLine(canvas, measuredWidth, measuredHeight);
        drawMiddleImg(canvas, measuredWidth, measuredHeight);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(measureWidthSize(i), measureHeightSize(i2));
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i == 0 || i2 == 0) {
            return;
        }
        float paddingTop = (i2 - getPaddingTop()) - getPaddingBottom();
        this.mMaxScaleHeight = (int) (this.mMaxScaleHeightRatio * paddingTop);
        this.mMinScaleHeight = (int) (paddingTop * this.mMinScaleHeightRatio);
        this.mMidScaleHeight = this.mMinScaleHeight * 2;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return true;
        }
        switch (motionEvent.getAction()) {
            case 0:
                this.mDownFocusX = motionEvent.getX();
                this.mDownFocusY = motionEvent.getY();
                break;
            case 1:
            case 3:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                this.isDisallowIntercept = false;
                break;
            case 2:
                if (!this.isDisallowIntercept && Math.abs(motionEvent.getY() - this.mDownFocusY) < Math.abs(motionEvent.getX() - this.mDownFocusX)) {
                    this.isDisallowIntercept = true;
                    if (getParent() != null) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                break;
        }
        return this.scroller.onTouchEvent(motionEvent);
    }

    public void setCurrentValue(int i) {
        Log.v(TAG, "setCurrentValue:" + i);
        if (i < 0) {
            i = 0;
        }
        if (i <= this.mMinValue) {
            i = this.mMinValue;
        }
        if (i >= this.mMaxValue) {
            i = this.mMaxValue;
        }
        this.mCurrentValue = i / this.mScaleBase;
        invalidate();
    }

    public void setCurrentValue(DataOfRadio dataOfRadio) {
        setBand(dataOfRadio.getType());
        setCurrentValue(dataOfRadio.getFreq());
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (z) {
            this.mMaxScaleColor = getResources().getColor(17170443);
            this.mMidScaleColor = getResources().getColor(17170443);
            this.mMinScaleColor = getResources().getColor(17170443);
            this.mScaleValueColor = getResources().getColor(17170443);
            this.mMiddleImg = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_cursor_normal);
        } else {
            this.mMaxScaleColor = getResources().getColor(17170456);
            this.mMidScaleColor = getResources().getColor(17170456);
            this.mMinScaleColor = getResources().getColor(17170456);
            this.mScaleValueColor = getResources().getColor(17170456);
        }
        this.mScaleValuePaint.setColor(this.mScaleValueColor);
        invalidate();
    }

    public void setMaxValue(int i) {
        Log.v(TAG, "setMaxValue:" + i);
        if (i > 0) {
            this.mMaxValue = i;
            setCurrentValue(this.mCurrentValue);
        }
        invalidate();
    }

    public void setMinValue(int i) {
        Log.v(TAG, "setMinValue:" + i);
        if (i / this.mScaleBase < 1) {
            i = 0;
        }
        this.mMinValue = i;
        setCurrentValue(this.mCurrentValue);
        invalidate();
    }

    public void setScrollingListener(OnRulerViewScrollListener onRulerViewScrollListener) {
        this.onWheelListener = onRulerViewScrollListener;
    }

    public void setStep(int i) {
        Log.v(TAG, "setStep:" + i);
        this.mScaleBase = i;
        invalidate();
    }
}
