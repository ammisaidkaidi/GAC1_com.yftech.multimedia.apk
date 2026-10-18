package com.facebook.fresco.animation.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import com.facebook.common.logging.FLog;
import com.facebook.drawable.base.DrawableWithCaches;
import com.facebook.drawee.drawable.DrawableProperties;
import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.fresco.animation.frame.DropFramesFrameScheduler;
import com.facebook.fresco.animation.frame.FrameScheduler;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class AnimatedDrawable2 extends Drawable implements Animatable, DrawableWithCaches {
    private static final int DEFAULT_FRAME_SCHEDULING_DELAY_MS = 8;
    private static final int DEFAULT_FRAME_SCHEDULING_OFFSET_MS = 0;

    @Nullable
    private AnimationBackend mAnimationBackend;
    private volatile AnimationListener mAnimationListener;

    @Nullable
    private volatile DrawListener mDrawListener;

    @Nullable
    private DrawableProperties mDrawableProperties;
    private int mDroppedFrames;
    private long mExpectedRenderTimeMs;

    @Nullable
    private FrameScheduler mFrameScheduler;
    private long mFrameSchedulingDelayMs;
    private long mFrameSchedulingOffsetMs;
    private final Runnable mInvalidateRunnable;
    private volatile boolean mIsRunning;
    private int mLastDrawnFrameNumber;
    private long mLastFrameAnimationTimeMs;
    private int mPausedLastDrawnFrameNumber;
    private long mPausedLastFrameAnimationTimeMsDifference;
    private long mPausedStartTimeMsDifference;
    private long mStartTimeMs;
    private static final Class<?> TAG = AnimatedDrawable2.class;
    private static final AnimationListener NO_OP_LISTENER = new BaseAnimationListener();

    public interface DrawListener {
        void onDraw(AnimatedDrawable2 animatedDrawable2, FrameScheduler frameScheduler, int i, boolean z, boolean z2, long j, long j2, long j3, long j4, long j5, long j6, long j7);
    }

    public AnimatedDrawable2() {
        this(null);
    }

    public AnimatedDrawable2(@Nullable AnimationBackend animationBackend) {
        this.mFrameSchedulingDelayMs = 8L;
        this.mFrameSchedulingOffsetMs = 0L;
        this.mAnimationListener = NO_OP_LISTENER;
        this.mDrawListener = null;
        this.mInvalidateRunnable = new Runnable() { // from class: com.facebook.fresco.animation.drawable.AnimatedDrawable2.1
            @Override // java.lang.Runnable
            public void run() {
                AnimatedDrawable2.this.unscheduleSelf(AnimatedDrawable2.this.mInvalidateRunnable);
                AnimatedDrawable2.this.invalidateSelf();
            }
        };
        this.mAnimationBackend = animationBackend;
        this.mFrameScheduler = createSchedulerForBackendAndDelayMethod(this.mAnimationBackend);
    }

    @Nullable
    private static FrameScheduler createSchedulerForBackendAndDelayMethod(@Nullable AnimationBackend animationBackend) {
        if (animationBackend == null) {
            return null;
        }
        return new DropFramesFrameScheduler(animationBackend);
    }

    private long now() {
        return SystemClock.uptimeMillis();
    }

    private void onFrameDropped() {
        this.mDroppedFrames++;
        if (FLog.isLoggable(2)) {
            FLog.v(TAG, "Dropped a frame. Count: %s", Integer.valueOf(this.mDroppedFrames));
        }
    }

    private void scheduleNextFrame(long j) {
        this.mExpectedRenderTimeMs = this.mStartTimeMs + j;
        scheduleSelf(this.mInvalidateRunnable, this.mExpectedRenderTimeMs);
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        long j;
        long j2;
        long j3;
        if (this.mAnimationBackend == null || this.mFrameScheduler == null) {
            return;
        }
        long jNow = now();
        long jMax = this.mIsRunning ? (jNow - this.mStartTimeMs) + this.mFrameSchedulingOffsetMs : Math.max(this.mLastFrameAnimationTimeMs, 0L);
        int frameNumberToRender = this.mFrameScheduler.getFrameNumberToRender(jMax, this.mLastFrameAnimationTimeMs);
        if (frameNumberToRender == -1) {
            frameNumberToRender = this.mAnimationBackend.getFrameCount() - 1;
            this.mAnimationListener.onAnimationStop(this);
            this.mIsRunning = false;
        } else if (frameNumberToRender == 0 && this.mLastDrawnFrameNumber != -1 && jNow >= this.mExpectedRenderTimeMs) {
            this.mAnimationListener.onAnimationRepeat(this);
        }
        int i = frameNumberToRender;
        boolean zDrawFrame = this.mAnimationBackend.drawFrame(this, canvas, i);
        if (zDrawFrame) {
            this.mAnimationListener.onAnimationFrame(this, i);
            this.mLastDrawnFrameNumber = i;
        }
        if (!zDrawFrame) {
            onFrameDropped();
        }
        long jNow2 = now();
        if (this.mIsRunning) {
            long targetRenderTimeForNextFrameMs = this.mFrameScheduler.getTargetRenderTimeForNextFrameMs(jNow2 - this.mStartTimeMs);
            if (targetRenderTimeForNextFrameMs != -1) {
                long j4 = this.mFrameSchedulingDelayMs + targetRenderTimeForNextFrameMs;
                scheduleNextFrame(j4);
                j2 = j4;
            } else {
                this.mAnimationListener.onAnimationStop(this);
                this.mIsRunning = false;
                j2 = -1;
            }
            j = targetRenderTimeForNextFrameMs;
        } else {
            j = -1;
            j2 = -1;
        }
        DrawListener drawListener = this.mDrawListener;
        if (drawListener != null) {
            drawListener.onDraw(this, this.mFrameScheduler, i, zDrawFrame, this.mIsRunning, this.mStartTimeMs, jMax, this.mLastFrameAnimationTimeMs, jNow, jNow2, j, j2);
            j3 = jMax;
        } else {
            j3 = jMax;
        }
        this.mLastFrameAnimationTimeMs = j3;
    }

    @Override // com.facebook.drawable.base.DrawableWithCaches
    public void dropCaches() {
        if (this.mAnimationBackend != null) {
            this.mAnimationBackend.clear();
        }
    }

    @Nullable
    public AnimationBackend getAnimationBackend() {
        return this.mAnimationBackend;
    }

    public long getDroppedFrames() {
        return this.mDroppedFrames;
    }

    public int getFrameCount() {
        if (this.mAnimationBackend == null) {
            return 0;
        }
        return this.mAnimationBackend.getFrameCount();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mAnimationBackend == null ? super.getIntrinsicHeight() : this.mAnimationBackend.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mAnimationBackend == null ? super.getIntrinsicWidth() : this.mAnimationBackend.getIntrinsicWidth();
    }

    public int getLoopCount() {
        if (this.mAnimationBackend == null) {
            return 0;
        }
        return this.mAnimationBackend.getLoopCount();
    }

    public long getLoopDurationMs() {
        if (this.mAnimationBackend == null) {
            return 0L;
        }
        if (this.mFrameScheduler != null) {
            return this.mFrameScheduler.getLoopDurationMs();
        }
        int frameDurationMs = 0;
        for (int i = 0; i < this.mAnimationBackend.getFrameCount(); i++) {
            frameDurationMs += this.mAnimationBackend.getFrameDurationMs(i);
        }
        return frameDurationMs;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public long getStartTimeMs() {
        return this.mStartTimeMs;
    }

    public boolean isInfiniteAnimation() {
        return this.mFrameScheduler != null && this.mFrameScheduler.isInfiniteAnimation();
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.mIsRunning;
    }

    public void jumpToFrame(int i) {
        if (this.mAnimationBackend == null || this.mFrameScheduler == null) {
            return;
        }
        this.mLastFrameAnimationTimeMs = this.mFrameScheduler.getTargetRenderTimeMs(i);
        this.mStartTimeMs = now() - this.mLastFrameAnimationTimeMs;
        this.mExpectedRenderTimeMs = this.mStartTimeMs;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        if (this.mAnimationBackend != null) {
            this.mAnimationBackend.setBounds(rect);
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        if (this.mIsRunning) {
            return false;
        }
        long j = i;
        if (this.mLastFrameAnimationTimeMs == j) {
            return false;
        }
        this.mLastFrameAnimationTimeMs = j;
        invalidateSelf();
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        if (this.mDrawableProperties == null) {
            this.mDrawableProperties = new DrawableProperties();
        }
        this.mDrawableProperties.setAlpha(i);
        if (this.mAnimationBackend != null) {
            this.mAnimationBackend.setAlpha(i);
        }
    }

    public void setAnimationBackend(@Nullable AnimationBackend animationBackend) {
        this.mAnimationBackend = animationBackend;
        if (this.mAnimationBackend != null) {
            this.mFrameScheduler = new DropFramesFrameScheduler(this.mAnimationBackend);
            this.mAnimationBackend.setBounds(getBounds());
            if (this.mDrawableProperties != null) {
                this.mDrawableProperties.applyTo(this);
            }
        }
        this.mFrameScheduler = createSchedulerForBackendAndDelayMethod(this.mAnimationBackend);
        stop();
    }

    public void setAnimationListener(@Nullable AnimationListener animationListener) {
        if (animationListener == null) {
            animationListener = NO_OP_LISTENER;
        }
        this.mAnimationListener = animationListener;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        if (this.mDrawableProperties == null) {
            this.mDrawableProperties = new DrawableProperties();
        }
        this.mDrawableProperties.setColorFilter(colorFilter);
        if (this.mAnimationBackend != null) {
            this.mAnimationBackend.setColorFilter(colorFilter);
        }
    }

    public void setDrawListener(@Nullable DrawListener drawListener) {
        this.mDrawListener = drawListener;
    }

    public void setFrameSchedulingDelayMs(long j) {
        this.mFrameSchedulingDelayMs = j;
    }

    public void setFrameSchedulingOffsetMs(long j) {
        this.mFrameSchedulingOffsetMs = j;
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        if (this.mIsRunning || this.mAnimationBackend == null || this.mAnimationBackend.getFrameCount() <= 1) {
            return;
        }
        this.mIsRunning = true;
        long jNow = now();
        this.mStartTimeMs = jNow - this.mPausedStartTimeMsDifference;
        this.mExpectedRenderTimeMs = this.mStartTimeMs;
        this.mLastFrameAnimationTimeMs = jNow - this.mPausedLastFrameAnimationTimeMsDifference;
        this.mLastDrawnFrameNumber = this.mPausedLastDrawnFrameNumber;
        invalidateSelf();
        this.mAnimationListener.onAnimationStart(this);
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        if (this.mIsRunning) {
            long jNow = now();
            this.mPausedStartTimeMsDifference = jNow - this.mStartTimeMs;
            this.mPausedLastFrameAnimationTimeMsDifference = jNow - this.mLastFrameAnimationTimeMs;
            this.mPausedLastDrawnFrameNumber = this.mLastDrawnFrameNumber;
            this.mIsRunning = false;
            this.mStartTimeMs = 0L;
            this.mExpectedRenderTimeMs = this.mStartTimeMs;
            this.mLastFrameAnimationTimeMs = -1L;
            this.mLastDrawnFrameNumber = -1;
            unscheduleSelf(this.mInvalidateRunnable);
            this.mAnimationListener.onAnimationStop(this);
        }
    }
}
