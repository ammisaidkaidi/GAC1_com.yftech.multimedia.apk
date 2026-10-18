package com.media.common.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.wifi.WifiEnterpriseConfig;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import com.media.common.util.DebugLog;
import com.media.common.util.LogUtil;

/* JADX INFO: loaded from: classes3.dex */
public class SeekBarView extends View {
    private static final int DEFAULT_TOUCH_TARGET_SIZE = 40;
    private final int DEFAULT_TEXT_PADDING;
    private final int DEFAULT_THUMB_COLOR;
    private boolean isTouchLegal;
    private ObjectAnimator mAnimator;
    private RectF mBackRectF;
    private boolean mIsCenterState;
    private long mLastTime;
    private LinearGradient mProgressLinearGradient;
    private final Paint mProgressPaint;
    private RectF mProgressRectF;

    @ColorInt
    private int mTextBackColor;
    private float mTextBackRadius;

    @ColorInt
    private int mTextColor;
    private float mTextLocation;
    private RectF mTextRectF;
    private float mTextSize;

    @ColorInt
    private int mThumbColor;
    private int mThumbDrawColor;
    private float mThumbNormalRadius;
    private float mThumbPressedRadius;
    private float mThumbRadius;
    private float mWidth;
    private int maxProgress;
    private int minProgress;
    private OnSeekBarCallbackListener onSeekBarCallbackListener;
    private boolean onlyChangeProgressByThumb;
    private final Paint paint;
    private int progress;

    @ColorInt
    private int progressBackColor;
    private float progressBackHeight;

    @ColorInt
    private int progressColor;

    @ColorInt
    private int progressFrameColor;
    private float progressFrameHeight;
    private float progressHeight;

    @ColorInt
    private int progressMinusColor;
    private float progressPosition;

    public static abstract class OnSeekBarCallbackListener {
        public void onFinished(int i) {
        }

        public void onProgress(int i) {
        }

        public void onStarted() {
        }
    }

    public SeekBarView(Context context) {
        this(context, null);
    }

    public SeekBarView(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public SeekBarView(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.DEFAULT_TEXT_PADDING = 10;
        this.DEFAULT_THUMB_COLOR = Color.GRAY;
        this.mWidth = 800.0f;
        this.minProgress = 0;
        this.maxProgress = 100;
        this.progressBackColor = -16777216;
        this.progressBackHeight = 10.0f;
        this.progressFrameColor = -1;
        this.progressFrameHeight = 3.0f;
        this.progressColor = Color.GREEN;
        this.progressHeight = 20.0f;
        this.progressMinusColor = -65536;
        this.progress = 50;
        this.mThumbNormalRadius = 14.0f;
        this.mThumbPressedRadius = 24.0f;
        this.mThumbColor = Color.GRAY;
        this.mTextLocation = 1.0f;
        this.mTextSize = 40.0f;
        this.mTextColor = -1;
        this.mTextBackColor = 2110968788;
        this.mTextBackRadius = 10.0f;
        this.mIsCenterState = false;
        this.onlyChangeProgressByThumb = true;
        this.mThumbRadius = this.mThumbNormalRadius;
        this.isTouchLegal = false;
        this.mThumbDrawColor = Color.GRAY;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.CenterSeekBar);
            this.maxProgress = typedArrayObtainStyledAttributes.getInteger(R.styleable.CenterSeekBar_maxProgress, 100);
            this.minProgress = typedArrayObtainStyledAttributes.getInteger(R.styleable.CenterSeekBar_minProgress, 0);
            this.mWidth = typedArrayObtainStyledAttributes.getDimension(R.styleable.CenterSeekBar_width, 800.0f);
            this.mIsCenterState = typedArrayObtainStyledAttributes.getBoolean(R.styleable.CenterSeekBar_centerState, false);
            this.progressBackColor = typedArrayObtainStyledAttributes.getColor(R.styleable.CenterSeekBar_backColor, -16777216);
            this.progressBackHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.CenterSeekBar_backHeight, 10.0f);
            this.progressFrameColor = typedArrayObtainStyledAttributes.getColor(R.styleable.CenterSeekBar_backFrameColor, -1);
            this.progressFrameHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.CenterSeekBar_backFrameSize, 3.0f);
            this.progressHeight = typedArrayObtainStyledAttributes.getDimension(R.styleable.CenterSeekBar_progressHeight, this.progressBackHeight);
            this.progressMinusColor = typedArrayObtainStyledAttributes.getColor(R.styleable.CenterSeekBar_progressMinusColor, -65536);
            this.progress = typedArrayObtainStyledAttributes.getInteger(R.styleable.CenterSeekBar_progress, 50);
            this.mThumbNormalRadius = typedArrayObtainStyledAttributes.getDimension(R.styleable.CenterSeekBar_thumbNormalRadius, 14.0f);
            this.mThumbPressedRadius = typedArrayObtainStyledAttributes.getDimension(R.styleable.CenterSeekBar_thumbPressRadius, 24.0f);
            this.mThumbColor = typedArrayObtainStyledAttributes.getColor(R.styleable.CenterSeekBar_thumbColor, -16776961);
            this.progressColor = typedArrayObtainStyledAttributes.getColor(R.styleable.CenterSeekBar_progressColor, -16776961);
            this.mProgressLinearGradient = new LinearGradient(60.0f, 0.0f, 95.0f, 100.0f, typedArrayObtainStyledAttributes.getColor(R.styleable.CenterSeekBar_progressHeightColor, -16776961), this.progressColor, Shader.TileMode.MIRROR);
            this.mTextLocation = typedArrayObtainStyledAttributes.getInteger(R.styleable.CenterSeekBar_textLocation, 1);
            this.mTextColor = typedArrayObtainStyledAttributes.getColor(R.styleable.CenterSeekBar_textColor, -1);
            this.mTextSize = typedArrayObtainStyledAttributes.getDimension(R.styleable.CenterSeekBar_textSize, 40.0f);
            this.mTextBackColor = typedArrayObtainStyledAttributes.getColor(R.styleable.CenterSeekBar_textBackColor, 2110968788);
            this.mTextBackRadius = typedArrayObtainStyledAttributes.getDimension(R.styleable.CenterSeekBar_textBackRadius, 10.0f);
            this.onlyChangeProgressByThumb = typedArrayObtainStyledAttributes.getBoolean(R.styleable.CenterSeekBar_onlyChangedProgressByThumb, true);
            this.mThumbRadius = this.mThumbNormalRadius;
            this.mThumbDrawColor = this.mThumbColor;
            typedArrayObtainStyledAttributes.recycle();
        }
        this.mProgressPaint = new Paint();
        this.mProgressPaint.setAntiAlias(true);
        this.mProgressPaint.setShader(this.mProgressLinearGradient);
        this.mAnimator = getTargetAnimator(false);
        this.mTextRectF = new RectF();
        this.mBackRectF = new RectF();
        this.mProgressRectF = new RectF();
    }

    private void checkTouchingTarget(MotionEvent motionEvent) {
        if (isTouchingTarget(motionEvent)) {
            this.mAnimator.cancel();
            this.mAnimator = getTargetAnimator(true);
            this.mAnimator.start();
        }
    }

    private float clamp(int i) {
        if (!this.mIsCenterState) {
            float width = getWidth() / 2;
            float f = width - (this.mWidth / 2.0f);
            float f2 = i;
            if (f2 >= width + (this.mWidth / 2.0f)) {
                return this.maxProgress;
            }
            return f2 <= f ? this.minProgress : ((this.maxProgress - this.minProgress) * (f2 - f)) / this.mWidth;
        }
        int width2 = getWidth() / 2;
        float f3 = width2;
        float f4 = f3 - (this.mWidth / 2.0f);
        float f5 = f3 + (this.mWidth / 2.0f);
        if (i > width2) {
            return ((float) i) >= f5 ? this.maxProgress : (int) (((this.maxProgress - this.minProgress) * (i - width2)) / (this.mWidth / 2.0f));
        }
        if (i < width2) {
            return ((float) i) <= f4 ? -this.maxProgress : (int) (((this.maxProgress - this.minProgress) * (i - width2)) / (this.mWidth / 2.0f));
        }
        return this.minProgress;
    }

    private ObjectAnimator getTargetAnimator(boolean z) {
        float[] fArr = new float[2];
        fArr[0] = this.mThumbRadius;
        fArr[1] = z ? this.mThumbPressedRadius : this.mThumbNormalRadius;
        final ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, "mThumbRadius", fArr);
        objectAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.media.common.ui.widget.SeekBarView.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SeekBarView.this.postInvalidate();
            }
        });
        objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.media.common.ui.widget.SeekBarView.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                objectAnimatorOfFloat.removeAllListeners();
            }
        });
        objectAnimatorOfFloat.setInterpolator(new AccelerateInterpolator());
        return objectAnimatorOfFloat;
    }

    private boolean isTouchingTarget(MotionEvent motionEvent) {
        if (!this.onlyChangeProgressByThumb) {
            this.isTouchLegal = true;
            return true;
        }
        float f = this.progressPosition;
        this.isTouchLegal = motionEvent.getX() > f - 40.0f && motionEvent.getX() < 40.0f + f;
        DebugLog.i("slack:\nisTouchLegal " + this.isTouchLegal + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + motionEvent.getX() + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + motionEvent.getY() + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + f);
        return this.isTouchLegal;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public int getProgress() {
        return this.progress;
    }

    public float getmWidth() {
        return this.mWidth;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth() / 2;
        int height = getHeight() / 2;
        float f = width;
        float f2 = f - (this.mWidth / 2.0f);
        this.paint.setColor(this.progressBackColor);
        this.paint.setStrokeWidth(this.progressBackHeight);
        this.paint.setStyle(Paint.Style.FILL);
        this.mBackRectF.left = f2;
        float f3 = height;
        this.mBackRectF.top = f3 - this.progressBackHeight;
        this.mBackRectF.right = this.mWidth + f2;
        this.mBackRectF.bottom = f3;
        canvas.drawRoundRect(this.mBackRectF, this.mTextBackRadius, this.mTextBackRadius, this.paint);
        if (this.progressFrameHeight > 0.0f) {
            this.paint.setColor(this.progressFrameColor);
            this.paint.setStrokeWidth(this.progressFrameHeight);
            this.paint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(this.mBackRectF, this.mTextBackRadius, this.mTextBackRadius, this.paint);
        }
        this.paint.setStyle(Paint.Style.FILL);
        this.mProgressPaint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeWidth(this.progressHeight);
        this.mProgressPaint.setStrokeWidth(this.progressHeight);
        if (this.mIsCenterState) {
            this.progressPosition = ((int) ((this.progress * (this.mWidth / 2.0f)) / (this.maxProgress - this.minProgress))) + f;
        } else {
            this.progressPosition = ((this.progress * this.mWidth) / (this.maxProgress - this.minProgress)) + f2;
            f = f2;
        }
        this.mProgressRectF.top = f3 - this.progressBackHeight;
        this.mProgressRectF.bottom = f3;
        if (this.progress > 0) {
            this.mProgressRectF.left = f;
            this.mProgressRectF.right = this.progressPosition;
        } else {
            this.mProgressRectF.left = this.progressPosition;
            this.mProgressRectF.right = f;
        }
        if (this.mIsCenterState) {
            canvas.drawRoundRect(this.mProgressRectF, 0.0f, 0.0f, this.mProgressPaint);
        } else {
            canvas.drawRoundRect(this.mProgressRectF, this.mTextBackRadius, this.mTextBackRadius, this.mProgressPaint);
        }
        this.paint.setColor(this.mThumbDrawColor);
        canvas.drawCircle(this.progressPosition, f3 - (this.progressBackHeight / 2.0f), this.mThumbRadius, this.paint);
        int i = (int) (((this.mThumbRadius - this.mThumbNormalRadius) * 255.0f) / (this.mThumbPressedRadius - this.mThumbNormalRadius));
        if (this.mTextLocation != 1.0f) {
            if (this.mTextLocation == 2.0f) {
                this.paint.setTextSize(this.mTextSize);
                this.paint.setColor(this.mTextColor);
                this.paint.setAlpha(i);
                this.paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(this.progress + "%", this.progressPosition, f3, this.paint);
                return;
            }
            return;
        }
        this.paint.setColor(this.mTextBackColor);
        this.paint.setAlpha(i);
        this.mTextRectF.bottom = (f3 - this.mThumbPressedRadius) - 10.0f;
        this.mTextRectF.right = this.progressPosition + this.mTextSize + 10.0f;
        this.mTextRectF.top = (this.mTextRectF.bottom - this.mTextSize) - 30.0f;
        this.mTextRectF.left = (this.progressPosition - this.mTextSize) - 10.0f;
        canvas.drawRoundRect(this.mTextRectF, this.mTextBackRadius, this.mTextBackRadius, this.paint);
        this.paint.setTextSize(this.mTextSize);
        this.paint.setColor(this.mTextColor);
        this.paint.setAlpha(i);
        this.paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(this.progress + "%", this.progressPosition, this.mTextRectF.bottom - 20.0f, this.paint);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return false;
        }
        LogUtil.d("seekBar action=" + motionEvent.getAction() + " ,isTouchLegal=" + this.isTouchLegal);
        int action = motionEvent.getAction();
        if (action == 0) {
            checkTouchingTarget(motionEvent);
            if (this.onSeekBarCallbackListener != null) {
                this.onSeekBarCallbackListener.onStarted();
            }
            if (!this.onlyChangeProgressByThumb) {
                this.progress = (int) clamp((int) motionEvent.getX());
                invalidate();
            }
        } else if (action != 2) {
            if (this.isTouchLegal) {
                getParent().requestDisallowInterceptTouchEvent(false);
                this.mAnimator.cancel();
                this.mAnimator = getTargetAnimator(false);
                this.mAnimator.start();
                if (this.onSeekBarCallbackListener != null) {
                    this.onSeekBarCallbackListener.onFinished(this.progress);
                }
            }
        } else if (this.isTouchLegal) {
            getParent().requestDisallowInterceptTouchEvent(true);
            this.progress = (int) clamp((int) motionEvent.getX());
            invalidate();
            if (this.onSeekBarCallbackListener != null) {
                this.onSeekBarCallbackListener.onProgress(this.progress);
            }
        }
        LogUtil.d("seekBar action=" + motionEvent.getAction() + " ,progress=" + this.progress);
        return true;
    }

    public SeekBarView setCenterModeEnable(boolean z) {
        if (this.mIsCenterState && !z && this.progress < 0) {
            this.progress = -this.progress;
        }
        this.mIsCenterState = z;
        invalidate();
        return this;
    }

    public void setMThumbRadius(float f) {
        this.mThumbRadius = f;
    }

    public SeekBarView setMaxProgress(int i) {
        this.maxProgress = i;
        invalidate();
        return this;
    }

    public SeekBarView setMinProgress(int i) {
        this.minProgress = i;
        invalidate();
        return this;
    }

    public void setOnSeekBarCallbackListener(OnSeekBarCallbackListener onSeekBarCallbackListener) {
        this.onSeekBarCallbackListener = onSeekBarCallbackListener;
    }

    public SeekBarView setProgress(int i) {
        if (this.mIsCenterState) {
            if (i > this.maxProgress || i < this.minProgress - this.maxProgress) {
                this.progress = this.minProgress;
            } else {
                this.progress = i;
            }
        } else if (i > this.maxProgress || i < this.minProgress) {
            this.progress = this.minProgress;
        } else {
            this.progress = i;
        }
        invalidate();
        return this;
    }

    public SeekBarView setProgressEnable(boolean z) {
        if (z) {
            setEnabled(true);
            this.mThumbDrawColor = this.mThumbColor;
        } else {
            setEnabled(false);
            this.progress = 0;
            this.mThumbDrawColor = Color.GRAY;
        }
        invalidate();
        return this;
    }
}
