package com.facebook.imagepipeline.cache;

import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.imagepipeline.image.CloseableImage;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public interface BitmapMemoryCacheFactory {
    CountingMemoryCache<CacheKey, CloseableImage> create(Supplier<MemoryCacheParams> supplier, MemoryTrimmableRegistry memoryTrimmableRegistry, MemoryCache.CacheTrimStrategy cacheTrimStrategy, @Nullable CountingMemoryCache.EntryStateObserver<CacheKey> entryStateObserver);
}
