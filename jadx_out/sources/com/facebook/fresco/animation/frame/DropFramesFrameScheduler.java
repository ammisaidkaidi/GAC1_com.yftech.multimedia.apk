package com.facebook.fresco.animation.frame;

import androidx.annotation.VisibleForTesting;
import com.facebook.fresco.animation.backend.AnimationInformation;
import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class DropFramesFrameScheduler implements FrameScheduler {
    private static final int UNSET = -1;
    private final AnimationInformation mAnimationInformation;
    private long mLoopDurationMs = -1;

    public DropFramesFrameScheduler(AnimationInformation animationInformation) {
        this.mAnimationInformation = animationInformation;
    }

    @Override // com.facebook.fresco.animation.frame.FrameScheduler
    public int getFrameNumberToRender(long j, long j2) {
        long loopDurationMs = getLoopDurationMs();
        if (loopDurationMs == 0) {
            return getFrameNumberWithinLoop(0L);
        }
        if (isInfiniteAnimation() || j / loopDurationMs < this.mAnimationInformation.getLoopCount()) {
            return getFrameNumberWithinLoop(j % loopDurationMs);
        }
        return -1;
    }

    @VisibleForTesting
    int getFrameNumberWithinLoop(long j) {
        int i = 0;
        long frameDurationMs = 0;
        do {
            frameDurationMs += (long) this.mAnimationInformation.getFrameDurationMs(i);
            i++;
        } while (j >= frameDurationMs);
        return i - 1;
    }

    @Override // com.facebook.fresco.animation.frame.FrameScheduler
    public long getLoopDurationMs() {
        if (this.mLoopDurationMs != -1) {
            return this.mLoopDurationMs;
        }
        this.mLoopDurationMs = 0L;
        int frameCount = this.mAnimationInformation.getFrameCount();
        for (int i = 0; i < frameCount; i++) {
            this.mLoopDurationMs += (long) this.mAnimationInformation.getFrameDurationMs(i);
        }
        return this.mLoopDurationMs;
    }

    @Override // com.facebook.fresco.animation.frame.FrameScheduler
    public long getTargetRenderTimeForNextFrameMs(long j) {
        long loopDurationMs = getLoopDurationMs();
        long frameDurationMs = 0;
        if (loopDurationMs == 0) {
            return -1L;
        }
        if (!isInfiniteAnimation() && j / getLoopDurationMs() >= this.mAnimationInformation.getLoopCount()) {
            return -1L;
        }
        long j2 = j % loopDurationMs;
        int frameCount = this.mAnimationInformation.getFrameCount();
        for (int i = 0; i < frameCount && frameDurationMs <= j2; i++) {
            frameDurationMs += (long) this.mAnimationInformation.getFrameDurationMs(i);
        }
        return j + (frameDurationMs - j2);
    }

    @Override // com.facebook.fresco.animation.frame.FrameScheduler
    public long getTargetRenderTimeMs(int i) {
        long frameDurationMs = 0;
        for (int i2 = 0; i2 < i; i2++) {
            frameDurationMs += (long) this.mAnimationInformation.getFrameDurationMs(i);
        }
        return frameDurationMs;
    }

    @Override // com.facebook.fresco.animation.frame.FrameScheduler
    public boolean isInfiniteAnimation() {
        return this.mAnimationInformation.getLoopCount() == 0;
    }
}
