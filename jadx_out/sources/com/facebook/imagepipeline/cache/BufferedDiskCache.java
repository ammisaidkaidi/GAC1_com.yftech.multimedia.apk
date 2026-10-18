package com.facebook.imagepipeline.cache;

import bolts.Task;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.WriterCallback;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteStreams;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.instrumentation.FrescoInstrumenter;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class BufferedDiskCache {
    private static final Class<?> TAG = BufferedDiskCache.class;
    private final FileCache mFileCache;
    private final ImageCacheStatsTracker mImageCacheStatsTracker;
    private final PooledByteBufferFactory mPooledByteBufferFactory;
    private final PooledByteStreams mPooledByteStreams;
    private final Executor mReadExecutor;
    private final StagingArea mStagingArea = StagingArea.getInstance();
    private final Executor mWriteExecutor;

    public BufferedDiskCache(FileCache fileCache, PooledByteBufferFactory pooledByteBufferFactory, PooledByteStreams pooledByteStreams, Executor executor, Executor executor2, ImageCacheStatsTracker imageCacheStatsTracker) {
        this.mFileCache = fileCache;
        this.mPooledByteBufferFactory = pooledByteBufferFactory;
        this.mPooledByteStreams = pooledByteStreams;
        this.mReadExecutor = executor;
        this.mWriteExecutor = executor2;
        this.mImageCacheStatsTracker = imageCacheStatsTracker;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkInStagingAreaAndFileCache(CacheKey cacheKey) {
        EncodedImage encodedImage = this.mStagingArea.get(cacheKey);
        if (encodedImage != null) {
            encodedImage.close();
            FLog.v(TAG, "Found image for %s in staging area", cacheKey.getUriString());
            this.mImageCacheStatsTracker.onStagingAreaHit(cacheKey);
            return true;
        }
        FLog.v(TAG, "Did not find image for %s in staging area", cacheKey.getUriString());
        this.mImageCacheStatsTracker.onStagingAreaMiss(cacheKey);
        try {
            return this.mFileCache.hasKey(cacheKey);
        } catch (Exception unused) {
            return false;
        }
    }

    private Task<Boolean> containsAsync(final CacheKey cacheKey) {
        try {
            final Object objOnBeforeSubmitWork = FrescoInstrumenter.onBeforeSubmitWork("BufferedDiskCache_containsAsync");
            return Task.call(new Callable<Boolean>() { // from class: com.facebook.imagepipeline.cache.BufferedDiskCache.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                public Boolean call() throws Exception {
                    Object objOnBeginWork = FrescoInstrumenter.onBeginWork(objOnBeforeSubmitWork, null);
                    try {
                        try {
                            Boolean boolValueOf = Boolean.valueOf(BufferedDiskCache.this.checkInStagingAreaAndFileCache(cacheKey));
                            FrescoInstrumenter.onEndWork(objOnBeginWork);
                            return boolValueOf;
                        } catch (Throwable th) {
                            FrescoInstrumenter.markFailure(objOnBeforeSubmitWork, th);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        FrescoInstrumenter.onEndWork(objOnBeginWork);
                        throw th2;
                    }
                }
            }, this.mReadExecutor);
        } catch (Exception e) {
            FLog.w(TAG, e, "Failed to schedule disk-cache read for %s", cacheKey.getUriString());
            return Task.forError(e);
        }
    }

    private Task<EncodedImage> foundPinnedImage(CacheKey cacheKey, EncodedImage encodedImage) {
        FLog.v(TAG, "Found image for %s in staging area", cacheKey.getUriString());
        this.mImageCacheStatsTracker.onStagingAreaHit(cacheKey);
        return Task.forResult(encodedImage);
    }

    private Task<EncodedImage> getAsync(final CacheKey cacheKey, final AtomicBoolean atomicBoolean) {
        try {
            final Object objOnBeforeSubmitWork = FrescoInstrumenter.onBeforeSubmitWork("BufferedDiskCache_getAsync");
            return Task.call(new Callable<EncodedImage>() { // from class: com.facebook.imagepipeline.cache.BufferedDiskCache.3
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.util.concurrent.Callable
                @Nullable
                public EncodedImage call() throws Exception {
                    Object objOnBeginWork = FrescoInstrumenter.onBeginWork(objOnBeforeSubmitWork, null);
                    try {
                        try {
                            if (atomicBoolean.get()) {
                                throw new CancellationException();
                            }
                            EncodedImage encodedImage = BufferedDiskCache.this.mStagingArea.get(cacheKey);
                            if (encodedImage != null) {
                                FLog.v((Class<?>) BufferedDiskCache.TAG, "Found image for %s in staging area", cacheKey.getUriString());
                                BufferedDiskCache.this.mImageCacheStatsTracker.onStagingAreaHit(cacheKey);
                            } else {
                                FLog.v((Class<?>) BufferedDiskCache.TAG, "Did not find image for %s in staging area", cacheKey.getUriString());
                                BufferedDiskCache.this.mImageCacheStatsTracker.onStagingAreaMiss(cacheKey);
                                try {
                                    PooledByteBuffer fromDiskCache = BufferedDiskCache.this.readFromDiskCache(cacheKey);
                                    if (fromDiskCache == null) {
                                        FrescoInstrumenter.onEndWork(objOnBeginWork);
                                        return null;
                                    }
                                    CloseableReference closeableReferenceOf = CloseableReference.of(fromDiskCache);
                                    try {
                                        EncodedImage encodedImage2 = new EncodedImage((CloseableReference<PooledByteBuffer>) closeableReferenceOf);
                                        CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceOf);
                                        encodedImage = encodedImage2;
                                    } catch (Throwable th) {
                                        CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceOf);
                                        throw th;
                                    }
                                } catch (Exception unused) {
                                    FrescoInstrumenter.onEndWork(objOnBeginWork);
                                    return null;
                                }
                            }
                            if (!Thread.interrupted()) {
                                FrescoInstrumenter.onEndWork(objOnBeginWork);
                                return encodedImage;
                            }
                            FLog.v((Class<?>) BufferedDiskCache.TAG, "Host thread was interrupted, decreasing reference count");
                            if (encodedImage != null) {
                                encodedImage.close();
                            }
                            throw new InterruptedException();
                        } catch (Throwable th2) {
                            FrescoInstrumenter.markFailure(objOnBeforeSubmitWork, th2);
                            throw th2;
                        }
                    } catch (Throwable th3) {
                        FrescoInstrumenter.onEndWork(objOnBeginWork);
                        throw th3;
                    }
                }
            }, this.mReadExecutor);
        } catch (Exception e) {
            FLog.w(TAG, e, "Failed to schedule disk-cache read for %s", cacheKey.getUriString());
            return Task.forError(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public PooledByteBuffer readFromDiskCache(CacheKey cacheKey) throws IOException {
        try {
            FLog.v(TAG, "Disk cache read for %s", cacheKey.getUriString());
            BinaryResource resource = this.mFileCache.getResource(cacheKey);
            if (resource == null) {
                FLog.v(TAG, "Disk cache miss for %s", cacheKey.getUriString());
                this.mImageCacheStatsTracker.onDiskCacheMiss(cacheKey);
                return null;
            }
            FLog.v(TAG, "Found entry in disk cache for %s", cacheKey.getUriString());
            this.mImageCacheStatsTracker.onDiskCacheHit(cacheKey);
            InputStream inputStreamOpenStream = resource.openStream();
            try {
                PooledByteBuffer pooledByteBufferNewByteBuffer = this.mPooledByteBufferFactory.newByteBuffer(inputStreamOpenStream, (int) resource.size());
                inputStreamOpenStream.close();
                FLog.v(TAG, "Successful read from disk cache for %s", cacheKey.getUriString());
                return pooledByteBufferNewByteBuffer;
            } catch (Throwable th) {
                inputStreamOpenStream.close();
                throw th;
            }
        } catch (IOException e) {
            FLog.w(TAG, e, "Exception reading from cache for %s", cacheKey.getUriString());
            this.mImageCacheStatsTracker.onDiskCacheGetFail(cacheKey);
            throw e;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeToDiskCache(CacheKey cacheKey, final EncodedImage encodedImage) {
        FLog.v(TAG, "About to write to disk-cache for key %s", cacheKey.getUriString());
        try {
            this.mFileCache.insert(cacheKey, new WriterCallback() { // from class: com.facebook.imagepipeline.cache.BufferedDiskCache.7
                @Override // com.facebook.cache.common.WriterCallback
                public void write(OutputStream outputStream) throws IOException {
                    BufferedDiskCache.this.mPooledByteStreams.copy(encodedImage.getInputStream(), outputStream);
                }
            });
            this.mImageCacheStatsTracker.onDiskCachePut(cacheKey);
            FLog.v(TAG, "Successful disk-cache write for key %s", cacheKey.getUriString());
        } catch (IOException e) {
            FLog.w(TAG, e, "Failed to write to disk-cache for key %s", cacheKey.getUriString());
        }
    }

    public void addKeyForAsyncProbing(CacheKey cacheKey) {
        Preconditions.checkNotNull(cacheKey);
        this.mFileCache.probe(cacheKey);
    }

    public Task<Void> clearAll() {
        this.mStagingArea.clearAll();
        final Object objOnBeforeSubmitWork = FrescoInstrumenter.onBeforeSubmitWork("BufferedDiskCache_clearAll");
        try {
            return Task.call(new Callable<Void>() { // from class: com.facebook.imagepipeline.cache.BufferedDiskCache.6
                @Override // java.util.concurrent.Callable
                public Void call() throws Exception {
                    Object objOnBeginWork = FrescoInstrumenter.onBeginWork(objOnBeforeSubmitWork, null);
                    try {
                        try {
                            BufferedDiskCache.this.mStagingArea.clearAll();
                            BufferedDiskCache.this.mFileCache.clearAll();
                            FrescoInstrumenter.onEndWork(objOnBeginWork);
                            return null;
                        } catch (Throwable th) {
                            FrescoInstrumenter.markFailure(objOnBeforeSubmitWork, th);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        FrescoInstrumenter.onEndWork(objOnBeginWork);
                        throw th2;
                    }
                }
            }, this.mWriteExecutor);
        } catch (Exception e) {
            FLog.w(TAG, e, "Failed to schedule disk-cache clear", new Object[0]);
            return Task.forError(e);
        }
    }

    public Task<Boolean> contains(CacheKey cacheKey) {
        return containsSync(cacheKey) ? Task.forResult(true) : containsAsync(cacheKey);
    }

    public boolean containsSync(CacheKey cacheKey) {
        return this.mStagingArea.containsKey(cacheKey) || this.mFileCache.hasKeySync(cacheKey);
    }

    public boolean diskCheckSync(CacheKey cacheKey) {
        if (containsSync(cacheKey)) {
            return true;
        }
        return checkInStagingAreaAndFileCache(cacheKey);
    }

    public Task<EncodedImage> get(CacheKey cacheKey, AtomicBoolean atomicBoolean) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("BufferedDiskCache#get");
            }
            EncodedImage encodedImage = this.mStagingArea.get(cacheKey);
            return encodedImage != null ? foundPinnedImage(cacheKey, encodedImage) : getAsync(cacheKey, atomicBoolean);
        } finally {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
    }

    public long getSize() {
        return this.mFileCache.getSize();
    }

    public Task<Void> probe(final CacheKey cacheKey) {
        Preconditions.checkNotNull(cacheKey);
        try {
            final Object objOnBeforeSubmitWork = FrescoInstrumenter.onBeforeSubmitWork("BufferedDiskCache_probe");
            return Task.call(new Callable<Void>() { // from class: com.facebook.imagepipeline.cache.BufferedDiskCache.2
                @Override // java.util.concurrent.Callable
                public Void call() throws Exception {
                    Object objOnBeginWork = FrescoInstrumenter.onBeginWork(objOnBeforeSubmitWork, null);
                    try {
                        BufferedDiskCache.this.mFileCache.probe(cacheKey);
                        return null;
                    } finally {
                        FrescoInstrumenter.onEndWork(objOnBeginWork);
                    }
                }
            }, this.mWriteExecutor);
        } catch (Exception e) {
            FLog.w(TAG, e, "Failed to schedule disk-cache probe for %s", cacheKey.getUriString());
            return Task.forError(e);
        }
    }

    public void put(final CacheKey cacheKey, EncodedImage encodedImage) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("BufferedDiskCache#put");
            }
            Preconditions.checkNotNull(cacheKey);
            Preconditions.checkArgument(Boolean.valueOf(EncodedImage.isValid(encodedImage)));
            this.mStagingArea.put(cacheKey, encodedImage);
            final EncodedImage encodedImageCloneOrNull = EncodedImage.cloneOrNull(encodedImage);
            try {
                final Object objOnBeforeSubmitWork = FrescoInstrumenter.onBeforeSubmitWork("BufferedDiskCache_putAsync");
                this.mWriteExecutor.execute(new Runnable() { // from class: com.facebook.imagepipeline.cache.BufferedDiskCache.4
                    @Override // java.lang.Runnable
                    public void run() {
                        Object objOnBeginWork = FrescoInstrumenter.onBeginWork(objOnBeforeSubmitWork, null);
                        try {
                            try {
                                BufferedDiskCache.this.writeToDiskCache(cacheKey, encodedImageCloneOrNull);
                                BufferedDiskCache.this.mStagingArea.remove(cacheKey, encodedImageCloneOrNull);
                                EncodedImage.closeSafely(encodedImageCloneOrNull);
                                FrescoInstrumenter.onEndWork(objOnBeginWork);
                            } catch (Throwable th) {
                                FrescoInstrumenter.markFailure(objOnBeforeSubmitWork, th);
                                throw th;
                            }
                        } catch (Throwable th2) {
                            BufferedDiskCache.this.mStagingArea.remove(cacheKey, encodedImageCloneOrNull);
                            EncodedImage.closeSafely(encodedImageCloneOrNull);
                            FrescoInstrumenter.onEndWork(objOnBeginWork);
                            throw th2;
                        }
                    }
                });
            } catch (Exception e) {
                FLog.w(TAG, e, "Failed to schedule disk-cache write for %s", cacheKey.getUriString());
                this.mStagingArea.remove(cacheKey, encodedImage);
                EncodedImage.closeSafely(encodedImageCloneOrNull);
            }
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        } catch (Throwable th) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
            throw th;
        }
    }

    public Task<Void> remove(final CacheKey cacheKey) {
        Preconditions.checkNotNull(cacheKey);
        this.mStagingArea.remove(cacheKey);
        try {
            final Object objOnBeforeSubmitWork = FrescoInstrumenter.onBeforeSubmitWork("BufferedDiskCache_remove");
            return Task.call(new Callable<Void>() { // from class: com.facebook.imagepipeline.cache.BufferedDiskCache.5
                @Override // java.util.concurrent.Callable
                public Void call() throws Exception {
                    Object objOnBeginWork = FrescoInstrumenter.onBeginWork(objOnBeforeSubmitWork, null);
                    try {
                        try {
                            BufferedDiskCache.this.mStagingArea.remove(cacheKey);
                            BufferedDiskCache.this.mFileCache.remove(cacheKey);
                            FrescoInstrumenter.onEndWork(objOnBeginWork);
                            return null;
                        } catch (Throwable th) {
                            FrescoInstrumenter.markFailure(objOnBeforeSubmitWork, th);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        FrescoInstrumenter.onEndWork(objOnBeginWork);
                        throw th2;
                    }
                }
            }, this.mWriteExecutor);
        } catch (Exception e) {
            FLog.w(TAG, e, "Failed to schedule disk-cache remove for %s", cacheKey.getUriString());
            return Task.forError(e);
        }
    }
}
