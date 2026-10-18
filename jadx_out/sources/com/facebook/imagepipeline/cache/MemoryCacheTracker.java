package com.facebook.imagepipeline.cache;

/* JADX INFO: loaded from: classes3.dex */
public interface MemoryCacheTracker<K> {
    void onCacheHit(K k);

    void onCacheMiss(K k);

    void onCachePut(K k);
}
