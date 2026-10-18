package com.facebook.imagepipeline.producers;

import android.app.slice.Slice;
import android.net.Uri;
import androidx.annotation.VisibleForTesting;
import bolts.Continuation;
import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.cache.BufferedDiskCache;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class PartialDiskCacheProducer implements Producer<EncodedImage> {
    public static final String ENCODED_IMAGE_SIZE = "encodedImageSize";
    public static final String EXTRA_CACHED_VALUE_FOUND = "cached_value_found";
    public static final String PRODUCER_NAME = "PartialDiskCacheProducer";
    private final ByteArrayPool mByteArrayPool;
    private final CacheKeyFactory mCacheKeyFactory;
    private final BufferedDiskCache mDefaultBufferedDiskCache;
    private final Producer<EncodedImage> mInputProducer;
    private final PooledByteBufferFactory mPooledByteBufferFactory;

    private static class PartialDiskCacheConsumer extends DelegatingConsumer<EncodedImage, EncodedImage> {
        private static final int READ_SIZE = 16384;
        private final ByteArrayPool mByteArrayPool;
        private final BufferedDiskCache mDefaultBufferedDiskCache;

        @Nullable
        private final EncodedImage mPartialEncodedImageFromCache;
        private final CacheKey mPartialImageCacheKey;
        private final PooledByteBufferFactory mPooledByteBufferFactory;

        private PartialDiskCacheConsumer(Consumer<EncodedImage> consumer, BufferedDiskCache bufferedDiskCache, CacheKey cacheKey, PooledByteBufferFactory pooledByteBufferFactory, ByteArrayPool byteArrayPool, @Nullable EncodedImage encodedImage) {
            super(consumer);
            this.mDefaultBufferedDiskCache = bufferedDiskCache;
            this.mPartialImageCacheKey = cacheKey;
            this.mPooledByteBufferFactory = pooledByteBufferFactory;
            this.mByteArrayPool = byteArrayPool;
            this.mPartialEncodedImageFromCache = encodedImage;
        }

        private void copy(InputStream inputStream, OutputStream outputStream, int i) throws IOException {
            byte[] bArr = this.mByteArrayPool.get(16384);
            int i2 = i;
            while (i2 > 0) {
                try {
                    int i3 = inputStream.read(bArr, 0, Math.min(16384, i2));
                    if (i3 < 0) {
                        break;
                    } else if (i3 > 0) {
                        outputStream.write(bArr, 0, i3);
                        i2 -= i3;
                    }
                } catch (Throwable th) {
                    this.mByteArrayPool.release(bArr);
                    throw th;
                }
            }
            this.mByteArrayPool.release(bArr);
            if (i2 > 0) {
                throw new IOException(String.format((Locale) null, "Failed to read %d bytes - finished %d short", Integer.valueOf(i), Integer.valueOf(i2)));
            }
        }

        private PooledByteBufferOutputStream merge(EncodedImage encodedImage, EncodedImage encodedImage2) throws IOException {
            PooledByteBufferOutputStream pooledByteBufferOutputStreamNewOutputStream = this.mPooledByteBufferFactory.newOutputStream(encodedImage2.getSize() + encodedImage2.getBytesRange().from);
            copy(encodedImage.getInputStream(), pooledByteBufferOutputStreamNewOutputStream, encodedImage2.getBytesRange().from);
            copy(encodedImage2.getInputStream(), pooledByteBufferOutputStreamNewOutputStream, encodedImage2.getSize());
            return pooledByteBufferOutputStreamNewOutputStream;
        }

        private void sendFinalResultToConsumer(PooledByteBufferOutputStream pooledByteBufferOutputStream) throws Throwable {
            EncodedImage encodedImage;
            CloseableReference closeableReferenceOf = CloseableReference.of(pooledByteBufferOutputStream.toByteBuffer());
            try {
                encodedImage = new EncodedImage((CloseableReference<PooledByteBuffer>) closeableReferenceOf);
                try {
                    encodedImage.parseMetaData();
                    getConsumer().onNewResult(encodedImage, 1);
                    EncodedImage.closeSafely(encodedImage);
                    CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceOf);
                } catch (Throwable th) {
                    th = th;
                    EncodedImage.closeSafely(encodedImage);
                    CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceOf);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                encodedImage = null;
            }
        }

        @Override // com.facebook.imagepipeline.producers.BaseConsumer
        public void onNewResultImpl(EncodedImage encodedImage, int i) {
            if (isNotLast(i)) {
                return;
            }
            if (this.mPartialEncodedImageFromCache != null) {
                try {
                    if (encodedImage.getBytesRange() != null) {
                        try {
                            sendFinalResultToConsumer(merge(this.mPartialEncodedImageFromCache, encodedImage));
                        } catch (IOException e) {
                            FLog.e(PartialDiskCacheProducer.PRODUCER_NAME, "Error while merging image data", e);
                            getConsumer().onFailure(e);
                        }
                        encodedImage.close();
                        encodedImage = this.mPartialEncodedImageFromCache;
                        encodedImage.close();
                        this.mDefaultBufferedDiskCache.remove(this.mPartialImageCacheKey);
                        return;
                    }
                } catch (Throwable th) {
                    encodedImage.close();
                    this.mPartialEncodedImageFromCache.close();
                    throw th;
                }
            }
            if (!statusHasFlag(i, 8) || !isLast(i) || encodedImage.getImageFormat() == ImageFormat.UNKNOWN) {
                getConsumer().onNewResult(encodedImage, i);
            } else {
                this.mDefaultBufferedDiskCache.put(this.mPartialImageCacheKey, encodedImage);
                getConsumer().onNewResult(encodedImage, i);
            }
        }
    }

    public PartialDiskCacheProducer(BufferedDiskCache bufferedDiskCache, CacheKeyFactory cacheKeyFactory, PooledByteBufferFactory pooledByteBufferFactory, ByteArrayPool byteArrayPool, Producer<EncodedImage> producer) {
        this.mDefaultBufferedDiskCache = bufferedDiskCache;
        this.mCacheKeyFactory = cacheKeyFactory;
        this.mPooledByteBufferFactory = pooledByteBufferFactory;
        this.mByteArrayPool = byteArrayPool;
        this.mInputProducer = producer;
    }

    private static Uri createUriForPartialCacheKey(ImageRequest imageRequest) {
        return imageRequest.getSourceUri().buildUpon().appendQueryParameter("fresco_partial", "true").build();
    }

    @Nullable
    @VisibleForTesting
    static Map<String, String> getExtraMap(ProducerListener2 producerListener2, ProducerContext producerContext, boolean z, int i) {
        if (producerListener2.requiresExtraMap(producerContext, PRODUCER_NAME)) {
            return z ? ImmutableMap.of("cached_value_found", String.valueOf(z), "encodedImageSize", String.valueOf(i)) : ImmutableMap.of("cached_value_found", String.valueOf(z));
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isTaskCancelled(Task<?> task) {
        return task.isCancelled() || (task.isFaulted() && (task.getError() instanceof CancellationException));
    }

    private Continuation<EncodedImage, Void> onFinishDiskReads(final Consumer<EncodedImage> consumer, final ProducerContext producerContext, final CacheKey cacheKey) {
        final ProducerListener2 producerListener = producerContext.getProducerListener();
        return new Continuation<EncodedImage, Void>() { // from class: com.facebook.imagepipeline.producers.PartialDiskCacheProducer.1
            @Override // bolts.Continuation
            public Void then(Task<EncodedImage> task) throws Exception {
                if (PartialDiskCacheProducer.isTaskCancelled(task)) {
                    producerListener.onProducerFinishWithCancellation(producerContext, PartialDiskCacheProducer.PRODUCER_NAME, null);
                    consumer.onCancellation();
                } else if (task.isFaulted()) {
                    producerListener.onProducerFinishWithFailure(producerContext, PartialDiskCacheProducer.PRODUCER_NAME, task.getError(), null);
                    PartialDiskCacheProducer.this.startInputProducer(consumer, producerContext, cacheKey, null);
                } else {
                    EncodedImage result = task.getResult();
                    if (result != null) {
                        producerListener.onProducerFinishWithSuccess(producerContext, PartialDiskCacheProducer.PRODUCER_NAME, PartialDiskCacheProducer.getExtraMap(producerListener, producerContext, true, result.getSize()));
                        BytesRange max = BytesRange.toMax(result.getSize() - 1);
                        result.setBytesRange(max);
                        int size = result.getSize();
                        ImageRequest imageRequest = producerContext.getImageRequest();
                        if (max.contains(imageRequest.getBytesRange())) {
                            producerContext.putOriginExtra("disk", Slice.HINT_PARTIAL);
                            producerListener.onUltimateProducerReached(producerContext, PartialDiskCacheProducer.PRODUCER_NAME, true);
                            consumer.onNewResult(result, 9);
                        } else {
                            consumer.onNewResult(result, 8);
                            PartialDiskCacheProducer.this.startInputProducer(consumer, new SettableProducerContext(ImageRequestBuilder.fromRequest(imageRequest).setBytesRange(BytesRange.from(size - 1)).build(), producerContext), cacheKey, result);
                        }
                    } else {
                        producerListener.onProducerFinishWithSuccess(producerContext, PartialDiskCacheProducer.PRODUCER_NAME, PartialDiskCacheProducer.getExtraMap(producerListener, producerContext, false, 0));
                        PartialDiskCacheProducer.this.startInputProducer(consumer, producerContext, cacheKey, result);
                    }
                }
                return null;
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startInputProducer(Consumer<EncodedImage> consumer, ProducerContext producerContext, CacheKey cacheKey, @Nullable EncodedImage encodedImage) {
        this.mInputProducer.produceResults(new PartialDiskCacheConsumer(consumer, this.mDefaultBufferedDiskCache, cacheKey, this.mPooledByteBufferFactory, this.mByteArrayPool, encodedImage), producerContext);
    }

    private void subscribeTaskForRequestCancellation(final AtomicBoolean atomicBoolean, ProducerContext producerContext) {
        producerContext.addCallbacks(new BaseProducerContextCallbacks() { // from class: com.facebook.imagepipeline.producers.PartialDiskCacheProducer.2
            @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
            public void onCancellationRequested() {
                atomicBoolean.set(true);
            }
        });
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
        ImageRequest imageRequest = producerContext.getImageRequest();
        if (!imageRequest.isDiskCacheEnabled()) {
            this.mInputProducer.produceResults(consumer, producerContext);
            return;
        }
        producerContext.getProducerListener().onProducerStart(producerContext, PRODUCER_NAME);
        CacheKey encodedCacheKey = this.mCacheKeyFactory.getEncodedCacheKey(imageRequest, createUriForPartialCacheKey(imageRequest), producerContext.getCallerContext());
        AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        this.mDefaultBufferedDiskCache.get(encodedCacheKey, atomicBoolean).continueWith(onFinishDiskReads(consumer, producerContext, encodedCacheKey));
        subscribeTaskForRequestCancellation(atomicBoolean, producerContext);
    }
}
