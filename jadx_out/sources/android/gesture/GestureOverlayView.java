package android.gesture;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import com.android.internal.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class GestureOverlayView extends FrameLayout {
    private static final boolean DITHER_FLAG = true;
    private static final int FADE_ANIMATION_RATE = 16;
    private static final boolean GESTURE_RENDERING_ANTIALIAS = true;
    public static final int GESTURE_STROKE_TYPE_MULTIPLE = 1;
    public static final int GESTURE_STROKE_TYPE_SINGLE = 0;
    public static final int ORIENTATION_HORIZONTAL = 0;
    public static final int ORIENTATION_VERTICAL = 1;
    private int mCertainGestureColor;
    private int mCurrentColor;
    private Gesture mCurrentGesture;
    private float mCurveEndX;
    private float mCurveEndY;
    private long mFadeDuration;
    private boolean mFadeEnabled;
    private long mFadeOffset;
    private float mFadingAlpha;
    private boolean mFadingHasStarted;
    private final FadeOutRunnable mFadingOut;
    private long mFadingStart;
    private final Paint mGesturePaint;
    private float mGestureStrokeAngleThreshold;
    private float mGestureStrokeLengthThreshold;
    private float mGestureStrokeSquarenessTreshold;
    private int mGestureStrokeType;
    private float mGestureStrokeWidth;
    private boolean mGestureVisible;
    private boolean mHandleGestureActions;
    private boolean mInterceptEvents;
    private final AccelerateDecelerateInterpolator mInterpolator;
    private final Rect mInvalidRect;
    private int mInvalidateExtraBorder;
    private boolean mIsFadingOut;
    private boolean mIsGesturing;
    private boolean mIsListeningForGestures;
    private final ArrayList<OnGestureListener> mOnGestureListeners;
    private final ArrayList<OnGesturePerformedListener> mOnGesturePerformedListeners;
    private final ArrayList<OnGesturingListener> mOnGesturingListeners;
    private int mOrientation;
    private final Path mPath;
    private boolean mPreviousWasGesturing;
    private boolean mResetGesture;
    private final ArrayList<GesturePoint> mStrokeBuffer;
    private float mTotalLength;
    private int mUncertainGestureColor;
    private float mX;
    private float mY;

    private class FadeOutRunnable implements Runnable {
        boolean fireActionPerformed;
        boolean resetMultipleStrokes;

        private FadeOutRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (GestureOverlayView.this.mIsFadingOut) {
                long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis() - GestureOverlayView.this.mFadingStart;
                if (jCurrentAnimationTimeMillis > GestureOverlayView.this.mFadeDuration) {
                    if (this.fireActionPerformed) {
                        GestureOverlayView.this.fireOnGesturePerformed();
                    }
                    GestureOverlayView.this.mPreviousWasGesturing = false;
                    GestureOverlayView.this.mIsFadingOut = false;
                    GestureOverlayView.this.mFadingHasStarted = false;
                    GestureOverlayView.this.mPath.rewind();
                    GestureOverlayView.this.mCurrentGesture = null;
                    GestureOverlayView.this.setPaintAlpha(255);
                } else {
                    GestureOverlayView.this.mFadingHasStarted = true;
                    GestureOverlayView.this.mFadingAlpha = 1.0f - GestureOverlayView.this.mInterpolator.getInterpolation(Math.max(0.0f, Math.min(1.0f, jCurrentAnimationTimeMillis / GestureOverlayView.this.mFadeDuration)));
                    GestureOverlayView.this.setPaintAlpha((int) (GestureOverlayView.this.mFadingAlpha * 255.0f));
                    GestureOverlayView.this.postDelayed(this, 16L);
                }
            } else if (this.resetMultipleStrokes) {
                GestureOverlayView.this.mResetGesture = true;
            } else {
                GestureOverlayView.this.fireOnGesturePerformed();
                GestureOverlayView.this.mFadingHasStarted = false;
                GestureOverlayView.this.mPath.rewind();
                GestureOverlayView.this.mCurrentGesture = null;
                GestureOverlayView.this.mPreviousWasGesturing = false;
                GestureOverlayView.this.setPaintAlpha(255);
            }
            GestureOverlayView.this.invalidate();
        }
    }

    public interface OnGestureListener {
        void onGesture(GestureOverlayView gestureOverlayView, MotionEvent motionEvent);

        void onGestureCancelled(GestureOverlayView gestureOverlayView, MotionEvent motionEvent);

        void onGestureEnded(GestureOverlayView gestureOverlayView, MotionEvent motionEvent);

        void onGestureStarted(GestureOverlayView gestureOverlayView, MotionEvent motionEvent);
    }

    public interface OnGesturePerformedListener {
        void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture);
    }

    public interface OnGesturingListener {
        void onGesturingEnded(GestureOverlayView gestureOverlayView);

        void onGesturingStarted(GestureOverlayView gestureOverlayView);
    }

    public GestureOverlayView(Context context) {
        super(context);
        this.mGesturePaint = new Paint();
        this.mFadeDuration = 150L;
        this.mFadeOffset = 420L;
        this.mFadeEnabled = true;
        this.mCertainGestureColor = -256;
        this.mUncertainGestureColor = 1224736512;
        this.mGestureStrokeWidth = 12.0f;
        this.mInvalidateExtraBorder = 10;
        this.mGestureStrokeType = 0;
        this.mGestureStrokeLengthThreshold = 50.0f;
        this.mGestureStrokeSquarenessTreshold = 0.275f;
        this.mGestureStrokeAngleThreshold = 40.0f;
        this.mOrientation = 1;
        this.mInvalidRect = new Rect();
        this.mPath = new Path();
        this.mGestureVisible = true;
        this.mIsGesturing = false;
        this.mPreviousWasGesturing = false;
        this.mInterceptEvents = true;
        this.mStrokeBuffer = new ArrayList<>(100);
        this.mOnGestureListeners = new ArrayList<>();
        this.mOnGesturePerformedListeners = new ArrayList<>();
        this.mOnGesturingListeners = new ArrayList<>();
        this.mIsFadingOut = false;
        this.mFadingAlpha = 1.0f;
        this.mInterpolator = new AccelerateDecelerateInterpolator();
        this.mFadingOut = new FadeOutRunnable();
        init();
    }

    public GestureOverlayView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.gestureOverlayViewStyle);
    }

    public GestureOverlayView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public GestureOverlayView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mGesturePaint = new Paint();
        this.mFadeDuration = 150L;
        this.mFadeOffset = 420L;
        this.mFadeEnabled = true;
        this.mCertainGestureColor = -256;
        this.mUncertainGestureColor = 1224736512;
        this.mGestureStrokeWidth = 12.0f;
        this.mInvalidateExtraBorder = 10;
        this.mGestureStrokeType = 0;
        this.mGestureStrokeLengthThreshold = 50.0f;
        this.mGestureStrokeSquarenessTreshold = 0.275f;
        this.mGestureStrokeAngleThreshold = 40.0f;
        this.mOrientation = 1;
        this.mInvalidRect = new Rect();
        this.mPath = new Path();
        this.mGestureVisible = true;
        this.mIsGesturing = false;
        this.mPreviousWasGesturing = false;
        this.mInterceptEvents = true;
        this.mStrokeBuffer = new ArrayList<>(100);
        this.mOnGestureListeners = new ArrayList<>();
        this.mOnGesturePerformedListeners = new ArrayList<>();
        this.mOnGesturingListeners = new ArrayList<>();
        this.mIsFadingOut = false;
        this.mFadingAlpha = 1.0f;
        this.mInterpolator = new AccelerateDecelerateInterpolator();
        this.mFadingOut = new FadeOutRunnable();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.GestureOverlayView, i, i2);
        this.mGestureStrokeWidth = typedArrayObtainStyledAttributes.getFloat(1, this.mGestureStrokeWidth);
        this.mInvalidateExtraBorder = Math.max(1, ((int) this.mGestureStrokeWidth) - 1);
        this.mCertainGestureColor = typedArrayObtainStyledAttributes.getColor(2, this.mCertainGestureColor);
        this.mUncertainGestureColor = typedArrayObtainStyledAttributes.getColor(3, this.mUncertainGestureColor);
        this.mFadeDuration = typedArrayObtainStyledAttributes.getInt(5, (int) this.mFadeDuration);
        this.mFadeOffset = typedArrayObtainStyledAttributes.getInt(4, (int) this.mFadeOffset);
        this.mGestureStrokeType = typedArrayObtainStyledAttributes.getInt(6, this.mGestureStrokeType);
        this.mGestureStrokeLengthThreshold = typedArrayObtainStyledAttributes.getFloat(7, this.mGestureStrokeLengthThreshold);
        this.mGestureStrokeAngleThreshold = typedArrayObtainStyledAttributes.getFloat(9, this.mGestureStrokeAngleThreshold);
        this.mGestureStrokeSquarenessTreshold = typedArrayObtainStyledAttributes.getFloat(8, this.mGestureStrokeSquarenessTreshold);
        this.mInterceptEvents = typedArrayObtainStyledAttributes.getBoolean(10, this.mInterceptEvents);
        this.mFadeEnabled = typedArrayObtainStyledAttributes.getBoolean(11, this.mFadeEnabled);
        this.mOrientation = typedArrayObtainStyledAttributes.getInt(0, this.mOrientation);
        typedArrayObtainStyledAttributes.recycle();
        init();
    }

    private void cancelGesture(MotionEvent motionEvent) {
        ArrayList<OnGestureListener> arrayList = this.mOnGestureListeners;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).onGestureCancelled(this, motionEvent);
        }
        clear(false);
    }

    private void clear(boolean z, boolean z2, boolean z3) {
        setPaintAlpha(255);
        removeCallbacks(this.mFadingOut);
        this.mResetGesture = false;
        this.mFadingOut.fireActionPerformed = z2;
        this.mFadingOut.resetMultipleStrokes = false;
        if (z && this.mCurrentGesture != null) {
            this.mFadingAlpha = 1.0f;
            this.mIsFadingOut = true;
            this.mFadingHasStarted = false;
            this.mFadingStart = AnimationUtils.currentAnimationTimeMillis() + this.mFadeOffset;
            postDelayed(this.mFadingOut, this.mFadeOffset);
            return;
        }
        this.mFadingAlpha = 1.0f;
        this.mIsFadingOut = false;
        this.mFadingHasStarted = false;
        if (z3) {
            this.mCurrentGesture = null;
            this.mPath.rewind();
            invalidate();
        } else {
            if (z2) {
                postDelayed(this.mFadingOut, this.mFadeOffset);
                return;
            }
            if (this.mGestureStrokeType == 1) {
                this.mFadingOut.resetMultipleStrokes = true;
                postDelayed(this.mFadingOut, this.mFadeOffset);
            } else {
                this.mCurrentGesture = null;
                this.mPath.rewind();
                invalidate();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fireOnGesturePerformed() {
        ArrayList<OnGesturePerformedListener> arrayList = this.mOnGesturePerformedListeners;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).onGesturePerformed(this, this.mCurrentGesture);
        }
    }

    private void init() {
        setWillNotDraw(false);
        Paint paint = this.mGesturePaint;
        paint.setAntiAlias(true);
        paint.setColor(this.mCertainGestureColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(this.mGestureStrokeWidth);
        paint.setDither(true);
        this.mCurrentColor = this.mCertainGestureColor;
        setPaintAlpha(255);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private boolean processEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case 0:
                touchDown(motionEvent);
                invalidate();
                return true;
            case 1:
                if (this.mIsListeningForGestures) {
                    touchUp(motionEvent, false);
                    invalidate();
                    return true;
                }
                return false;
            case 2:
                if (this.mIsListeningForGestures) {
                    Rect rect = touchMove(motionEvent);
                    if (rect != null) {
                        invalidate(rect);
                    }
                    return true;
                }
                return false;
            case 3:
                if (this.mIsListeningForGestures) {
                    touchUp(motionEvent, true);
                    invalidate();
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    private void setCurrentColor(int i) {
        this.mCurrentColor = i;
        if (this.mFadingHasStarted) {
            setPaintAlpha((int) (this.mFadingAlpha * 255.0f));
        } else {
            setPaintAlpha(255);
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setPaintAlpha(int i) {
        this.mGesturePaint.setColor(((this.mCurrentColor << 8) >>> 8) | ((((this.mCurrentColor >>> 24) * (i + (i >> 7))) >> 8) << 24));
    }

    private void touchDown(MotionEvent motionEvent) {
        this.mIsListeningForGestures = true;
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        this.mX = x;
        this.mY = y;
        this.mTotalLength = 0.0f;
        this.mIsGesturing = false;
        if (this.mGestureStrokeType == 0 || this.mResetGesture) {
            if (this.mHandleGestureActions) {
                setCurrentColor(this.mUncertainGestureColor);
            }
            this.mResetGesture = false;
            this.mCurrentGesture = null;
            this.mPath.rewind();
        } else if ((this.mCurrentGesture == null || this.mCurrentGesture.getStrokesCount() == 0) && this.mHandleGestureActions) {
            setCurrentColor(this.mUncertainGestureColor);
        }
        if (this.mFadingHasStarted) {
            cancelClearAnimation();
        } else if (this.mIsFadingOut) {
            setPaintAlpha(255);
            this.mIsFadingOut = false;
            this.mFadingHasStarted = false;
            removeCallbacks(this.mFadingOut);
        }
        if (this.mCurrentGesture == null) {
            this.mCurrentGesture = new Gesture();
        }
        this.mStrokeBuffer.add(new GesturePoint(x, y, motionEvent.getEventTime()));
        this.mPath.moveTo(x, y);
        int i = this.mInvalidateExtraBorder;
        int i2 = (int) x;
        int i3 = (int) y;
        this.mInvalidRect.set(i2 - i, i3 - i, i2 + i, i3 + i);
        this.mCurveEndX = x;
        this.mCurveEndY = y;
        ArrayList<OnGestureListener> arrayList = this.mOnGestureListeners;
        int size = arrayList.size();
        for (int i4 = 0; i4 < size; i4++) {
            arrayList.get(i4).onGestureStarted(this, motionEvent);
        }
    }

    private Rect touchMove(MotionEvent motionEvent) {
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        float f = this.mX;
        float f2 = this.mY;
        float fAbs = Math.abs(x - f);
        float fAbs2 = Math.abs(y - f2);
        if (fAbs < 3.0f && fAbs2 < 3.0f) {
            return null;
        }
        Rect rect = this.mInvalidRect;
        int i = this.mInvalidateExtraBorder;
        rect.set(((int) this.mCurveEndX) - i, ((int) this.mCurveEndY) - i, ((int) this.mCurveEndX) + i, ((int) this.mCurveEndY) + i);
        float f3 = (x + f) / 2.0f;
        this.mCurveEndX = f3;
        float f4 = (y + f2) / 2.0f;
        this.mCurveEndY = f4;
        this.mPath.quadTo(f, f2, f3, f4);
        int i2 = (int) f;
        int i3 = (int) f2;
        rect.union(i2 - i, i3 - i, i2 + i, i3 + i);
        int i4 = (int) f3;
        int i5 = (int) f4;
        rect.union(i4 - i, i5 - i, i4 + i, i5 + i);
        this.mX = x;
        this.mY = y;
        this.mStrokeBuffer.add(new GesturePoint(x, y, motionEvent.getEventTime()));
        if (this.mHandleGestureActions && !this.mIsGesturing) {
            this.mTotalLength += (float) Math.hypot(fAbs, fAbs2);
            if (this.mTotalLength > this.mGestureStrokeLengthThreshold) {
                OrientedBoundingBox orientedBoundingBoxComputeOrientedBoundingBox = GestureUtils.computeOrientedBoundingBox(this.mStrokeBuffer);
                float fAbs3 = Math.abs(orientedBoundingBoxComputeOrientedBoundingBox.orientation);
                if (fAbs3 > 90.0f) {
                    fAbs3 = 180.0f - fAbs3;
                }
                if (orientedBoundingBoxComputeOrientedBoundingBox.squareness > this.mGestureStrokeSquarenessTreshold || (this.mOrientation != 1 ? fAbs3 > this.mGestureStrokeAngleThreshold : fAbs3 < this.mGestureStrokeAngleThreshold)) {
                    this.mIsGesturing = true;
                    setCurrentColor(this.mCertainGestureColor);
                    ArrayList<OnGesturingListener> arrayList = this.mOnGesturingListeners;
                    int size = arrayList.size();
                    for (int i6 = 0; i6 < size; i6++) {
                        arrayList.get(i6).onGesturingStarted(this);
                    }
                }
            }
        }
        ArrayList<OnGestureListener> arrayList2 = this.mOnGestureListeners;
        int size2 = arrayList2.size();
        for (int i7 = 0; i7 < size2; i7++) {
            arrayList2.get(i7).onGesture(this, motionEvent);
        }
        return rect;
    }

    private void touchUp(MotionEvent motionEvent, boolean z) {
        this.mIsListeningForGestures = false;
        if (this.mCurrentGesture != null) {
            this.mCurrentGesture.addStroke(new GestureStroke(this.mStrokeBuffer));
            if (z) {
                cancelGesture(motionEvent);
            } else {
                ArrayList<OnGestureListener> arrayList = this.mOnGestureListeners;
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    arrayList.get(i).onGestureEnded(this, motionEvent);
                }
                clear(this.mHandleGestureActions && this.mFadeEnabled, this.mHandleGestureActions && this.mIsGesturing, false);
            }
        } else {
            cancelGesture(motionEvent);
        }
        this.mStrokeBuffer.clear();
        this.mPreviousWasGesturing = this.mIsGesturing;
        this.mIsGesturing = false;
        ArrayList<OnGesturingListener> arrayList2 = this.mOnGesturingListeners;
        int size2 = arrayList2.size();
        for (int i2 = 0; i2 < size2; i2++) {
            arrayList2.get(i2).onGesturingEnded(this);
        }
    }

    public void addOnGestureListener(OnGestureListener onGestureListener) {
        this.mOnGestureListeners.add(onGestureListener);
    }

    public void addOnGesturePerformedListener(OnGesturePerformedListener onGesturePerformedListener) {
        this.mOnGesturePerformedListeners.add(onGesturePerformedListener);
        if (this.mOnGesturePerformedListeners.size() > 0) {
            this.mHandleGestureActions = true;
        }
    }

    public void addOnGesturingListener(OnGesturingListener onGesturingListener) {
        this.mOnGesturingListeners.add(onGesturingListener);
    }

    public void cancelClearAnimation() {
        setPaintAlpha(255);
        this.mIsFadingOut = false;
        this.mFadingHasStarted = false;
        removeCallbacks(this.mFadingOut);
        this.mPath.rewind();
        this.mCurrentGesture = null;
    }

    public void cancelGesture() {
        this.mIsListeningForGestures = false;
        this.mCurrentGesture.addStroke(new GestureStroke(this.mStrokeBuffer));
        long jUptimeMillis = SystemClock.uptimeMillis();
        MotionEvent motionEventObtain = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 3, 0.0f, 0.0f, 0);
        ArrayList<OnGestureListener> arrayList = this.mOnGestureListeners;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).onGestureCancelled(this, motionEventObtain);
        }
        motionEventObtain.recycle();
        clear(false);
        this.mIsGesturing = false;
        this.mPreviousWasGesturing = false;
        this.mStrokeBuffer.clear();
        ArrayList<OnGesturingListener> arrayList2 = this.mOnGesturingListeners;
        int size2 = arrayList2.size();
        for (int i2 = 0; i2 < size2; i2++) {
            arrayList2.get(i2).onGesturingEnded(this);
        }
    }

    public void clear(boolean z) {
        clear(z, false, true);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled()) {
            return super.dispatchTouchEvent(motionEvent);
        }
        boolean z = (this.mIsGesturing || (this.mCurrentGesture != null && this.mCurrentGesture.getStrokesCount() > 0 && this.mPreviousWasGesturing)) && this.mInterceptEvents;
        processEvent(motionEvent);
        if (z) {
            motionEvent.setAction(3);
        }
        super.dispatchTouchEvent(motionEvent);
        return true;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (this.mCurrentGesture == null || !this.mGestureVisible) {
            return;
        }
        canvas.drawPath(this.mPath, this.mGesturePaint);
    }

    public ArrayList<GesturePoint> getCurrentStroke() {
        return this.mStrokeBuffer;
    }

    public long getFadeOffset() {
        return this.mFadeOffset;
    }

    public Gesture getGesture() {
        return this.mCurrentGesture;
    }

    public int getGestureColor() {
        return this.mCertainGestureColor;
    }

    public Paint getGesturePaint() {
        return this.mGesturePaint;
    }

    public Path getGesturePath() {
        return this.mPath;
    }

    public Path getGesturePath(Path path) {
        path.set(this.mPath);
        return path;
    }

    public float getGestureStrokeAngleThreshold() {
        return this.mGestureStrokeAngleThreshold;
    }

    public float getGestureStrokeLengthThreshold() {
        return this.mGestureStrokeLengthThreshold;
    }

    public float getGestureStrokeSquarenessTreshold() {
        return this.mGestureStrokeSquarenessTreshold;
    }

    public int getGestureStrokeType() {
        return this.mGestureStrokeType;
    }

    public float getGestureStrokeWidth() {
        return this.mGestureStrokeWidth;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public int getUncertainGestureColor() {
        return this.mUncertainGestureColor;
    }

    public boolean isEventsInterceptionEnabled() {
        return this.mInterceptEvents;
    }

    public boolean isFadeEnabled() {
        return this.mFadeEnabled;
    }

    public boolean isGestureVisible() {
        return this.mGestureVisible;
    }

    public boolean isGesturing() {
        return this.mIsGesturing;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelClearAnimation();
    }

    public void removeAllOnGestureListeners() {
        this.mOnGestureListeners.clear();
    }

    public void removeAllOnGesturePerformedListeners() {
        this.mOnGesturePerformedListeners.clear();
        this.mHandleGestureActions = false;
    }

    public void removeAllOnGesturingListeners() {
        this.mOnGesturingListeners.clear();
    }

    public void removeOnGestureListener(OnGestureListener onGestureListener) {
        this.mOnGestureListeners.remove(onGestureListener);
    }

    public void removeOnGesturePerformedListener(OnGesturePerformedListener onGesturePerformedListener) {
        this.mOnGesturePerformedListeners.remove(onGesturePerformedListener);
        if (this.mOnGesturePerformedListeners.size() <= 0) {
            this.mHandleGestureActions = false;
        }
    }

    public void removeOnGesturingListener(OnGesturingListener onGesturingListener) {
        this.mOnGesturingListeners.remove(onGesturingListener);
    }

    public void setEventsInterceptionEnabled(boolean z) {
        this.mInterceptEvents = z;
    }

    public void setFadeEnabled(boolean z) {
        this.mFadeEnabled = z;
    }

    public void setFadeOffset(long j) {
        this.mFadeOffset = j;
    }

    public void setGesture(Gesture gesture) {
        if (this.mCurrentGesture != null) {
            clear(false);
        }
        setCurrentColor(this.mCertainGestureColor);
        this.mCurrentGesture = gesture;
        Path path = this.mCurrentGesture.toPath();
        RectF rectF = new RectF();
        path.computeBounds(rectF, true);
        this.mPath.rewind();
        this.mPath.addPath(path, (-rectF.left) + ((getWidth() - rectF.width()) / 2.0f), (-rectF.top) + ((getHeight() - rectF.height()) / 2.0f));
        this.mResetGesture = true;
        invalidate();
    }

    public void setGestureColor(int i) {
        this.mCertainGestureColor = i;
    }

    public void setGestureStrokeAngleThreshold(float f) {
        this.mGestureStrokeAngleThreshold = f;
    }

    public void setGestureStrokeLengthThreshold(float f) {
        this.mGestureStrokeLengthThreshold = f;
    }

    public void setGestureStrokeSquarenessTreshold(float f) {
        this.mGestureStrokeSquarenessTreshold = f;
    }

    public void setGestureStrokeType(int i) {
        this.mGestureStrokeType = i;
    }

    public void setGestureStrokeWidth(float f) {
        this.mGestureStrokeWidth = f;
        this.mInvalidateExtraBorder = Math.max(1, ((int) f) - 1);
        this.mGesturePaint.setStrokeWidth(f);
    }

    public void setGestureVisible(boolean z) {
        this.mGestureVisible = z;
    }

    public void setOrientation(int i) {
        this.mOrientation = i;
    }

    public void setUncertainGestureColor(int i) {
        this.mUncertainGestureColor = i;
    }
}
