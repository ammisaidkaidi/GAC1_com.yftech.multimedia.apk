package com.facebook.imagepipeline.cache;

import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.Postprocessor;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class DefaultCacheKeyFactory implements CacheKeyFactory {
    private static DefaultCacheKeyFactory sInstance;

    protected DefaultCacheKeyFactory() {
    }

    public static synchronized DefaultCacheKeyFactory getInstance() {
        if (sInstance == null) {
            sInstance = new DefaultCacheKeyFactory();
        }
        return sInstance;
    }

    @Override // com.facebook.imagepipeline.cache.CacheKeyFactory
    public CacheKey getBitmapCacheKey(ImageRequest imageRequest, @Nullable Object obj) {
        return new BitmapMemoryCacheKey(getCacheKeySourceUri(imageRequest.getSourceUri()).toString(), imageRequest.getResizeOptions(), imageRequest.getRotationOptions(), imageRequest.getImageDecodeOptions(), null, null, obj);
    }

    protected Uri getCacheKeySourceUri(Uri uri) {
        return uri;
    }

    @Override // com.facebook.imagepipeline.cache.CacheKeyFactory
    public CacheKey getEncodedCacheKey(ImageRequest imageRequest, Uri uri, @Nullable Object obj) {
        return new SimpleCacheKey(getCacheKeySourceUri(uri).toString());
    }

    @Override // com.facebook.imagepipeline.cache.CacheKeyFactory
    public CacheKey getEncodedCacheKey(ImageRequest imageRequest, @Nullable Object obj) {
        return getEncodedCacheKey(imageRequest, imageRequest.getSourceUri(), obj);
    }

    @Override // com.facebook.imagepipeline.cache.CacheKeyFactory
    public CacheKey getPostprocessedBitmapCacheKey(ImageRequest imageRequest, @Nullable Object obj) {
        CacheKey cacheKey;
        String name;
        Postprocessor postprocessor = imageRequest.getPostprocessor();
        if (postprocessor != null) {
            CacheKey postprocessorCacheKey = postprocessor.getPostprocessorCacheKey();
            name = postprocessor.getClass().getName();
            cacheKey = postprocessorCacheKey;
        } else {
            cacheKey = null;
            name = null;
        }
        return new BitmapMemoryCacheKey(getCacheKeySourceUri(imageRequest.getSourceUri()).toString(), imageRequest.getResizeOptions(), imageRequest.getRotationOptions(), imageRequest.getImageDecodeOptions(), cacheKey, name, obj);
    }
}
