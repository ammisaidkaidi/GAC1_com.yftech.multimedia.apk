package com.facebook.imagepipeline.cache;

import com.facebook.infer.annotation.Nullsafe;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class MemoryCacheParams {
    public final int maxCacheEntries;
    public final int maxCacheEntrySize;
    public final int maxCacheSize;
    public final int maxEvictionQueueEntries;
    public final int maxEvictionQueueSize;
    public final long paramsCheckIntervalMs;

    public MemoryCacheParams(int i, int i2, int i3, int i4, int i5) {
        this(i, i2, i3, i4, i5, TimeUnit.MINUTES.toMillis(5L));
    }

    public MemoryCacheParams(int i, int i2, int i3, int i4, int i5, long j) {
        this.maxCacheSize = i;
        this.maxCacheEntries = i2;
        this.maxEvictionQueueSize = i3;
        this.maxEvictionQueueEntries = i4;
        this.maxCacheEntrySize = i5;
        this.paramsCheckIntervalMs = j;
    }
}
