package com.facebook.drawee.components;

import android.os.Looper;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public abstract class DeferredReleaser {

    @Nullable
    private static DeferredReleaser sInstance;

    public interface Releasable {
        void release();
    }

    public static synchronized DeferredReleaser getInstance() {
        if (sInstance == null) {
            sInstance = new DeferredReleaserConcurrentImpl();
        }
        return sInstance;
    }

    static boolean isOnUiThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    public abstract void cancelDeferredRelease(Releasable releasable);

    public abstract void scheduleDeferredRelease(Releasable releasable);
}
