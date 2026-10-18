package com.facebook.common.references;

import android.graphics.Bitmap;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.infer.annotation.FalseOnNull;
import com.facebook.infer.annotation.Nullsafe;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class SharedReference<T> {

    @GuardedBy("itself")
    private static final Map<Object, Integer> sLiveObjects = new IdentityHashMap();

    @GuardedBy("this")
    private int mRefCount = 1;
    private final ResourceReleaser<T> mResourceReleaser;

    @GuardedBy("this")
    @Nullable
    private T mValue;

    public static class NullReferenceException extends RuntimeException {
        public NullReferenceException() {
            super("Null shared reference");
        }
    }

    public SharedReference(T t, ResourceReleaser<T> resourceReleaser) {
        this.mValue = (T) Preconditions.checkNotNull(t);
        this.mResourceReleaser = (ResourceReleaser) Preconditions.checkNotNull(resourceReleaser);
        addLiveReference(t);
    }

    private static void addLiveReference(Object obj) {
        if (CloseableReference.useGc() && ((obj instanceof Bitmap) || (obj instanceof HasBitmap))) {
            return;
        }
        synchronized (sLiveObjects) {
            Integer num = sLiveObjects.get(obj);
            if (num == null) {
                sLiveObjects.put(obj, 1);
            } else {
                sLiveObjects.put(obj, Integer.valueOf(num.intValue() + 1));
            }
        }
    }

    private synchronized int decreaseRefCount() {
        ensureValid();
        Preconditions.checkArgument(Boolean.valueOf(this.mRefCount > 0));
        this.mRefCount--;
        return this.mRefCount;
    }

    private void ensureValid() {
        if (!isValid(this)) {
            throw new NullReferenceException();
        }
    }

    @FalseOnNull
    public static boolean isValid(@Nullable SharedReference<?> sharedReference) {
        return sharedReference != null && sharedReference.isValid();
    }

    private static void removeLiveReference(Object obj) {
        synchronized (sLiveObjects) {
            Integer num = sLiveObjects.get(obj);
            if (num == null) {
                FLog.wtf("SharedReference", "No entry in sLiveObjects for value of type %s", obj.getClass());
            } else if (num.intValue() == 1) {
                sLiveObjects.remove(obj);
            } else {
                sLiveObjects.put(obj, Integer.valueOf(num.intValue() - 1));
            }
        }
    }

    public static String reportData() {
        return Objects.toStringHelper("SharedReference").add("live_objects_count", sLiveObjects.size()).toString();
    }

    public synchronized void addReference() {
        ensureValid();
        this.mRefCount++;
    }

    public synchronized boolean addReferenceIfValid() {
        if (!isValid()) {
            return false;
        }
        addReference();
        return true;
    }

    public void deleteReference() {
        T t;
        if (decreaseRefCount() == 0) {
            synchronized (this) {
                t = this.mValue;
                this.mValue = null;
            }
            if (t != null) {
                this.mResourceReleaser.release(t);
                removeLiveReference(t);
            }
        }
    }

    public synchronized boolean deleteReferenceIfValid() {
        if (!isValid()) {
            return false;
        }
        deleteReference();
        return true;
    }

    @Nullable
    public synchronized T get() {
        return this.mValue;
    }

    public synchronized int getRefCountTestOnly() {
        return this.mRefCount;
    }

    public synchronized boolean isValid() {
        return this.mRefCount > 0;
    }
}
