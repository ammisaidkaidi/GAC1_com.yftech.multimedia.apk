package com.facebook.fresco.animation.bitmap;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.IntRange;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.fresco.animation.backend.AnimationBackendDelegateWithInactivityCheck;
import com.facebook.fresco.animation.backend.AnimationInformation;
import com.facebook.fresco.animation.bitmap.preparation.BitmapFramePreparationStrategy;
import com.facebook.fresco.animation.bitmap.preparation.BitmapFramePreparer;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.infer.annotation.Nullsafe;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class BitmapAnimationBackend implements AnimationBackend, AnimationBackendDelegateWithInactivityCheck.InactivityListener {
    public static final int FRAME_TYPE_CACHED = 0;
    public static final int FRAME_TYPE_CREATED = 2;
    public static final int FRAME_TYPE_FALLBACK = 3;
    public static final int FRAME_TYPE_REUSED = 1;
    public static final int FRAME_TYPE_UNKNOWN = -1;
    private static final Class<?> TAG = BitmapAnimationBackend.class;
    private final AnimationInformation mAnimationInformation;
    private final BitmapFrameCache mBitmapFrameCache;

    @Nullable
    private final BitmapFramePreparationStrategy mBitmapFramePreparationStrategy;

    @Nullable
    private final BitmapFramePreparer mBitmapFramePreparer;
    private final BitmapFrameRenderer mBitmapFrameRenderer;
    private int mBitmapHeight;
    private int mBitmapWidth;

    @Nullable
    private Rect mBounds;

    @Nullable
    private FrameListener mFrameListener;
    private final PlatformBitmapFactory mPlatformBitmapFactory;
    private Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;
    private final Paint mPaint = new Paint(6);

    public interface FrameListener {
        void onDrawFrameStart(BitmapAnimationBackend bitmapAnimationBackend, int i);

        void onFrameDrawn(BitmapAnimationBackend bitmapAnimationBackend, int i, int i2);

        void onFrameDropped(BitmapAnimationBackend bitmapAnimationBackend, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface FrameType {
    }

    public BitmapAnimationBackend(PlatformBitmapFactory platformBitmapFactory, BitmapFrameCache bitmapFrameCache, AnimationInformation animationInformation, BitmapFrameRenderer bitmapFrameRenderer, @Nullable BitmapFramePreparationStrategy bitmapFramePreparationStrategy, @Nullable BitmapFramePreparer bitmapFramePreparer) {
        this.mPlatformBitmapFactory = platformBitmapFactory;
        this.mBitmapFrameCache = bitmapFrameCache;
        this.mAnimationInformation = animationInformation;
        this.mBitmapFrameRenderer = bitmapFrameRenderer;
        this.mBitmapFramePreparationStrategy = bitmapFramePreparationStrategy;
        this.mBitmapFramePreparer = bitmapFramePreparer;
        updateBitmapDimensions();
    }

    private boolean drawBitmapAndCache(int i, @Nullable CloseableReference<Bitmap> closeableReference, Canvas canvas, int i2) {
        if (!CloseableReference.isValid(closeableReference)) {
            return false;
        }
        if (this.mBounds == null) {
            canvas.drawBitmap(closeableReference.get(), 0.0f, 0.0f, this.mPaint);
        } else {
            canvas.drawBitmap(closeableReference.get(), (Rect) null, this.mBounds, this.mPaint);
        }
        if (i2 != 3) {
            this.mBitmapFrameCache.onFrameRendered(i, closeableReference, i2);
        }
        if (this.mFrameListener == null) {
            return true;
        }
        this.mFrameListener.onFrameDrawn(this, i, i2);
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:34:0x006b A[ADDED_TO_REGION] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r12v0, types: [int] */
    /* JADX WARN: Type inference failed for: r5v0 */
    /* JADX WARN: Type inference failed for: r5v1, types: [com.facebook.common.references.CloseableReference] */
    /* JADX WARN: Type inference failed for: r5v2 */
    private boolean drawFrameOrFallback(Canvas canvas, int i, int i2) throws Throwable {
        CloseableReference<Bitmap> closeableReferenceCreateBitmap;
        boolean zDrawBitmapAndCache;
        int i3 = 3;
        boolean z = true;
        ?? r5 = 0;
        try {
            try {
                switch (i2) {
                    case 0:
                        closeableReferenceCreateBitmap = this.mBitmapFrameCache.getCachedFrame(i);
                        zDrawBitmapAndCache = drawBitmapAndCache(i, closeableReferenceCreateBitmap, canvas, 0);
                        i3 = 1;
                        CloseableReference.closeSafely(closeableReferenceCreateBitmap);
                        return (!zDrawBitmapAndCache || i3 == -1) ? zDrawBitmapAndCache : drawFrameOrFallback(canvas, i, i3);
                    case 1:
                        closeableReferenceCreateBitmap = this.mBitmapFrameCache.getBitmapToReuseForFrame(i, this.mBitmapWidth, this.mBitmapHeight);
                        z = renderFrameInBitmap(i, closeableReferenceCreateBitmap) && drawBitmapAndCache(i, closeableReferenceCreateBitmap, canvas, 1);
                        i3 = 2;
                        CloseableReference.closeSafely(closeableReferenceCreateBitmap);
                        if (zDrawBitmapAndCache) {
                        }
                    case 2:
                        try {
                            closeableReferenceCreateBitmap = this.mPlatformBitmapFactory.createBitmap(this.mBitmapWidth, this.mBitmapHeight, this.mBitmapConfig);
                            zDrawBitmapAndCache = (renderFrameInBitmap(i, closeableReferenceCreateBitmap) && drawBitmapAndCache(i, closeableReferenceCreateBitmap, canvas, 2)) ? z : false;
                            CloseableReference.closeSafely(closeableReferenceCreateBitmap);
                            if (zDrawBitmapAndCache) {
                            }
                        } catch (RuntimeException e) {
                            FLog.w(TAG, "Failed to create frame bitmap", e);
                            CloseableReference.closeSafely((CloseableReference<?>) null);
                            return false;
                        }
                    case 3:
                        closeableReferenceCreateBitmap = this.mBitmapFrameCache.getFallbackFrame(i);
                        zDrawBitmapAndCache = drawBitmapAndCache(i, closeableReferenceCreateBitmap, canvas, 3);
                        i3 = -1;
                        CloseableReference.closeSafely(closeableReferenceCreateBitmap);
                        if (zDrawBitmapAndCache) {
                        }
                    default:
                        CloseableReference.closeSafely((CloseableReference<?>) null);
                        return false;
                }
            } catch (Throwable th) {
                th = th;
                r5 = i2;
                CloseableReference.closeSafely((CloseableReference<?>) r5);
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            CloseableReference.closeSafely((CloseableReference<?>) r5);
            throw th;
        }
    }

    private boolean renderFrameInBitmap(int i, @Nullable CloseableReference<Bitmap> closeableReference) {
        if (!CloseableReference.isValid(closeableReference)) {
            return false;
        }
        boolean zRenderFrame = this.mBitmapFrameRenderer.renderFrame(i, closeableReference.get());
        if (!zRenderFrame) {
            CloseableReference.closeSafely(closeableReference);
        }
        return zRenderFrame;
    }

    private void updateBitmapDimensions() {
        this.mBitmapWidth = this.mBitmapFrameRenderer.getIntrinsicWidth();
        if (this.mBitmapWidth == -1) {
            this.mBitmapWidth = this.mBounds == null ? -1 : this.mBounds.width();
        }
        this.mBitmapHeight = this.mBitmapFrameRenderer.getIntrinsicHeight();
        if (this.mBitmapHeight == -1) {
            this.mBitmapHeight = this.mBounds != null ? this.mBounds.height() : -1;
        }
    }

    @Override // com.facebook.fresco.animation.backend.AnimationBackend
    public void clear() {
        this.mBitmapFrameCache.clear();
    }

    @Override // com.facebook.fresco.animation.backend.AnimationBackend
    public boolean drawFrame(Drawable drawable, Canvas canvas, int i) throws Throwable {
        if (this.mFrameListener != null) {
            this.mFrameListener.onDrawFrameStart(this, i);
        }
        boolean zDrawFrameOrFallback = drawFrameOrFallback(canvas, i, 0);
        if (!zDrawFrameOrFallback && this.mFrameListener != null) {
            this.mFrameListener.onFrameDropped(this, i);
        }
        if (this.mBitmapFramePreparationStrategy != null && this.mBitmapFramePreparer != null) {
            this.mBitmapFramePreparationStrategy.prepareFrames(this.mBitmapFramePreparer, this.mBitmapFrameCache, this, i);
        }
        return zDrawFrameOrFallback;
    }

    @Override // com.facebook.fresco.animation.backend.AnimationInformation
    public int getFrameCount() {
        return this.mAnimationInformation.getFrameCount();
    }

    @Override // com.facebook.fresco.animation.backend.AnimationInformation
    public int getFrameDurationMs(int i) {
        return this.mAnimationInformation.getFrameDurationMs(i);
    }

    @Override // com.facebook.fresco.animation.backend.AnimationBackend
    public int getIntrinsicHeight() {
        return this.mBitmapHeight;
    }

    @Override // com.facebook.fresco.animation.backend.AnimationBackend
    public int getIntrinsicWidth() {
        return this.mBitmapWidth;
    }

    @Override // com.facebook.fresco.animation.backend.AnimationInformation
    public int getLoopCount() {
        return this.mAnimationInformation.getLoopCount();
    }

    @Override // com.facebook.fresco.animation.backend.AnimationBackend
    public int getSizeInBytes() {
        return this.mBitmapFrameCache.getSizeInBytes();
    }

    @Override // com.facebook.fresco.animation.backend.AnimationBackendDelegateWithInactivityCheck.InactivityListener
    public void onInactive() {
        clear();
    }

    @Override // com.facebook.fresco.animation.backend.AnimationBackend
    public void setAlpha(@IntRange(from = 0, to = 255) int i) {
        this.mPaint.setAlpha(i);
    }

    public void setBitmapConfig(Bitmap.Config config) {
        this.mBitmapConfig = config;
    }

    @Override // com.facebook.fresco.animation.backend.AnimationBackend
    public void setBounds(@Nullable Rect rect) {
        this.mBounds = rect;
        this.mBitmapFrameRenderer.setBounds(rect);
        updateBitmapDimensions();
    }

    @Override // com.facebook.fresco.animation.backend.AnimationBackend
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public void setFrameListener(@Nullable FrameListener frameListener) {
        this.mFrameListener = frameListener;
    }
}
