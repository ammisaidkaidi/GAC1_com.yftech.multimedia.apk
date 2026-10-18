package com.facebook.fresco.animation.bitmap.preparation;

import android.graphics.Bitmap;
import android.util.SparseArray;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.fresco.animation.backend.AnimationBackend;
import com.facebook.fresco.animation.bitmap.BitmapFrameCache;
import com.facebook.fresco.animation.bitmap.BitmapFrameRenderer;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import java.util.concurrent.ExecutorService;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultBitmapFramePreparer implements BitmapFramePreparer {
    private static final Class<?> TAG = DefaultBitmapFramePreparer.class;
    private final Bitmap.Config mBitmapConfig;
    private final BitmapFrameRenderer mBitmapFrameRenderer;
    private final ExecutorService mExecutorService;
    private final SparseArray<Runnable> mPendingFrameDecodeJobs = new SparseArray<>();
    private final PlatformBitmapFactory mPlatformBitmapFactory;

    private class FrameDecodeRunnable implements Runnable {
        private final AnimationBackend mAnimationBackend;
        private final BitmapFrameCache mBitmapFrameCache;
        private final int mFrameNumber;
        private final int mHashCode;

        public FrameDecodeRunnable(AnimationBackend animationBackend, BitmapFrameCache bitmapFrameCache, int i, int i2) {
            this.mAnimationBackend = animationBackend;
            this.mBitmapFrameCache = bitmapFrameCache;
            this.mFrameNumber = i;
            this.mHashCode = i2;
        }

        private boolean prepareFrameAndCache(int i, int i2) {
            int i3;
            CloseableReference<Bitmap> closeableReferenceCreateBitmap;
            try {
                switch (i2) {
                    case 1:
                        closeableReferenceCreateBitmap = this.mBitmapFrameCache.getBitmapToReuseForFrame(i, this.mAnimationBackend.getIntrinsicWidth(), this.mAnimationBackend.getIntrinsicHeight());
                        i3 = 2;
                        boolean zRenderFrameAndCache = renderFrameAndCache(i, closeableReferenceCreateBitmap, i2);
                        CloseableReference.closeSafely(closeableReferenceCreateBitmap);
                        return (!zRenderFrameAndCache || i3 == -1) ? zRenderFrameAndCache : prepareFrameAndCache(i, i3);
                    case 2:
                        i3 = -1;
                        closeableReferenceCreateBitmap = DefaultBitmapFramePreparer.this.mPlatformBitmapFactory.createBitmap(this.mAnimationBackend.getIntrinsicWidth(), this.mAnimationBackend.getIntrinsicHeight(), DefaultBitmapFramePreparer.this.mBitmapConfig);
                        boolean zRenderFrameAndCache2 = renderFrameAndCache(i, closeableReferenceCreateBitmap, i2);
                        CloseableReference.closeSafely(closeableReferenceCreateBitmap);
                        if (zRenderFrameAndCache2) {
                        }
                    default:
                        CloseableReference.closeSafely((CloseableReference<?>) null);
                        return false;
                }
            } catch (RuntimeException e) {
                FLog.w((Class<?>) DefaultBitmapFramePreparer.TAG, "Failed to create frame bitmap", e);
                return false;
            } finally {
                CloseableReference.closeSafely((CloseableReference<?>) null);
            }
        }

        private boolean renderFrameAndCache(int i, CloseableReference<Bitmap> closeableReference, int i2) {
            if (!CloseableReference.isValid(closeableReference) || !DefaultBitmapFramePreparer.this.mBitmapFrameRenderer.renderFrame(i, closeableReference.get())) {
                return false;
            }
            FLog.v((Class<?>) DefaultBitmapFramePreparer.TAG, "Frame %d ready.", Integer.valueOf(this.mFrameNumber));
            synchronized (DefaultBitmapFramePreparer.this.mPendingFrameDecodeJobs) {
                this.mBitmapFrameCache.onFramePrepared(this.mFrameNumber, closeableReference, i2);
            }
            return true;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (this.mBitmapFrameCache.contains(this.mFrameNumber)) {
                    FLog.v((Class<?>) DefaultBitmapFramePreparer.TAG, "Frame %d is cached already.", Integer.valueOf(this.mFrameNumber));
                    synchronized (DefaultBitmapFramePreparer.this.mPendingFrameDecodeJobs) {
                        DefaultBitmapFramePreparer.this.mPendingFrameDecodeJobs.remove(this.mHashCode);
                    }
                    return;
                }
                if (prepareFrameAndCache(this.mFrameNumber, 1)) {
                    FLog.v((Class<?>) DefaultBitmapFramePreparer.TAG, "Prepared frame frame %d.", Integer.valueOf(this.mFrameNumber));
                } else {
                    FLog.e((Class<?>) DefaultBitmapFramePreparer.TAG, "Could not prepare frame %d.", Integer.valueOf(this.mFrameNumber));
                }
                synchronized (DefaultBitmapFramePreparer.this.mPendingFrameDecodeJobs) {
                    DefaultBitmapFramePreparer.this.mPendingFrameDecodeJobs.remove(this.mHashCode);
                }
            } catch (Throwable th) {
                synchronized (DefaultBitmapFramePreparer.this.mPendingFrameDecodeJobs) {
                    DefaultBitmapFramePreparer.this.mPendingFrameDecodeJobs.remove(this.mHashCode);
                    throw th;
                }
            }
        }
    }

    public DefaultBitmapFramePreparer(PlatformBitmapFactory platformBitmapFactory, BitmapFrameRenderer bitmapFrameRenderer, Bitmap.Config config, ExecutorService executorService) {
        this.mPlatformBitmapFactory = platformBitmapFactory;
        this.mBitmapFrameRenderer = bitmapFrameRenderer;
        this.mBitmapConfig = config;
        this.mExecutorService = executorService;
    }

    private static int getUniqueId(AnimationBackend animationBackend, int i) {
        return (animationBackend.hashCode() * 31) + i;
    }

    @Override // com.facebook.fresco.animation.bitmap.preparation.BitmapFramePreparer
    public boolean prepareFrame(BitmapFrameCache bitmapFrameCache, AnimationBackend animationBackend, int i) {
        int uniqueId = getUniqueId(animationBackend, i);
        synchronized (this.mPendingFrameDecodeJobs) {
            if (this.mPendingFrameDecodeJobs.get(uniqueId) != null) {
                FLog.v(TAG, "Already scheduled decode job for frame %d", Integer.valueOf(i));
                return true;
            }
            if (bitmapFrameCache.contains(i)) {
                FLog.v(TAG, "Frame %d is cached already.", Integer.valueOf(i));
                return true;
            }
            FrameDecodeRunnable frameDecodeRunnable = new FrameDecodeRunnable(animationBackend, bitmapFrameCache, i, uniqueId);
            this.mPendingFrameDecodeJobs.put(uniqueId, frameDecodeRunnable);
            this.mExecutorService.execute(frameDecodeRunnable);
            return true;
        }
    }
}
