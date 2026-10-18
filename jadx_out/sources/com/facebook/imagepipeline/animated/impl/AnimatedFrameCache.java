package com.facebook.imagepipeline.animated.impl;

import android.net.Uri;
import androidx.annotation.VisibleForTesting;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.Objects;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.cache.CountingMemoryCache;
import com.facebook.imagepipeline.image.CloseableImage;
import java.util.Iterator;
import java.util.LinkedHashSet;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/* JADX INFO: loaded from: classes3.dex */
public class AnimatedFrameCache {
    private final CountingMemoryCache<CacheKey, CloseableImage> mBackingCache;
    private final CacheKey mImageCacheKey;

    @GuardedBy("this")
    private final LinkedHashSet<CacheKey> mFreeItemsPool = new LinkedHashSet<>();
    private final CountingMemoryCache.EntryStateObserver<CacheKey> mEntryStateObserver = new CountingMemoryCache.EntryStateObserver<CacheKey>() { // from class: com.facebook.imagepipeline.animated.impl.AnimatedFrameCache.1
        @Override // com.facebook.imagepipeline.cache.CountingMemoryCache.EntryStateObserver
        public void onExclusivityChanged(CacheKey cacheKey, boolean z) {
            AnimatedFrameCache.this.onReusabilityChange(cacheKey, z);
        }
    };

    @VisibleForTesting
    static class FrameKey implements CacheKey {
        private final int mFrameIndex;
        private final CacheKey mImageCacheKey;

        public FrameKey(CacheKey cacheKey, int i) {
            this.mImageCacheKey = cacheKey;
            this.mFrameIndex = i;
        }

        @Override // com.facebook.cache.common.CacheKey
        public boolean containsUri(Uri uri) {
            return this.mImageCacheKey.containsUri(uri);
        }

        @Override // com.facebook.cache.common.CacheKey
        public boolean equals(@Nullable Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof FrameKey)) {
                return false;
            }
            FrameKey frameKey = (FrameKey) obj;
            return this.mFrameIndex == frameKey.mFrameIndex && this.mImageCacheKey.equals(frameKey.mImageCacheKey);
        }

        @Override // com.facebook.cache.common.CacheKey
        @Nullable
        public String getUriString() {
            return null;
        }

        @Override // com.facebook.cache.common.CacheKey
        public int hashCode() {
            return (this.mImageCacheKey.hashCode() * 1013) + this.mFrameIndex;
        }

        @Override // com.facebook.cache.common.CacheKey
        public boolean isResourceIdForDebugging() {
            return false;
        }

        @Override // com.facebook.cache.common.CacheKey
        public String toString() {
            return Objects.toStringHelper(this).add("imageCacheKey", this.mImageCacheKey).add("frameIndex", this.mFrameIndex).toString();
        }
    }

    public AnimatedFrameCache(CacheKey cacheKey, CountingMemoryCache<CacheKey, CloseableImage> countingMemoryCache) {
        this.mImageCacheKey = cacheKey;
        this.mBackingCache = countingMemoryCache;
    }

    private FrameKey keyFor(int i) {
        return new FrameKey(this.mImageCacheKey, i);
    }

    @Nullable
    private synchronized CacheKey popFirstFreeItemKey() {
        CacheKey next;
        next = null;
        Iterator<CacheKey> it = this.mFreeItemsPool.iterator();
        if (it.hasNext()) {
            next = it.next();
            it.remove();
        }
        return next;
    }

    @Nullable
    public CloseableReference<CloseableImage> cache(int i, CloseableReference<CloseableImage> closeableReference) {
        return this.mBackingCache.cache(keyFor(i), closeableReference, this.mEntryStateObserver);
    }

    public boolean contains(int i) {
        return this.mBackingCache.contains(keyFor(i));
    }

    @Nullable
    public CloseableReference<CloseableImage> get(int i) {
        return this.mBackingCache.get(keyFor(i));
    }

    @Nullable
    public CloseableReference<CloseableImage> getForReuse() {
        CloseableReference<CloseableImage> closeableReferenceReuse;
        do {
            CacheKey cacheKeyPopFirstFreeItemKey = popFirstFreeItemKey();
            if (cacheKeyPopFirstFreeItemKey == null) {
                return null;
            }
            closeableReferenceReuse = this.mBackingCache.reuse(cacheKeyPopFirstFreeItemKey);
        } while (closeableReferenceReuse == null);
        return closeableReferenceReuse;
    }

    public synchronized void onReusabilityChange(CacheKey cacheKey, boolean z) {
        try {
            if (z) {
                this.mFreeItemsPool.add(cacheKey);
            } else {
                this.mFreeItemsPool.remove(cacheKey);
            }
        } catch (Throwable th) {
            throw th;
        }
    }
}
