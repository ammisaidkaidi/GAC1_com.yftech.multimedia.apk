package com.facebook.imagepipeline.producers;

import android.location.LocationManager;
import android.os.SystemClock;
import androidx.annotation.VisibleForTesting;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.memory.PooledByteBufferOutputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.EncodedImageOrigin;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.infer.annotation.Nullsafe;
import java.io.InputStream;
import java.util.Map;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class NetworkFetchProducer implements Producer<EncodedImage> {
    public static final String INTERMEDIATE_RESULT_PRODUCER_EVENT = "intermediate_result";
    public static final String PRODUCER_NAME = "NetworkFetchProducer";
    private static final int READ_SIZE = 16384;

    @VisibleForTesting
    static final long TIME_BETWEEN_PARTIAL_RESULTS_MS = 100;
    private final ByteArrayPool mByteArrayPool;
    private final NetworkFetcher mNetworkFetcher;
    protected final PooledByteBufferFactory mPooledByteBufferFactory;

    public NetworkFetchProducer(PooledByteBufferFactory pooledByteBufferFactory, ByteArrayPool byteArrayPool, NetworkFetcher networkFetcher) {
        this.mPooledByteBufferFactory = pooledByteBufferFactory;
        this.mByteArrayPool = byteArrayPool;
        this.mNetworkFetcher = networkFetcher;
    }

    protected static float calculateProgress(int i, int i2) {
        return i2 > 0 ? i / i2 : 1.0f - ((float) Math.exp(((double) (-i)) / 50000.0d));
    }

    @Nullable
    private Map<String, String> getExtraMap(FetchState fetchState, int i) {
        if (fetchState.getListener().requiresExtraMap(fetchState.getContext(), PRODUCER_NAME)) {
            return this.mNetworkFetcher.getExtraMap(fetchState, i);
        }
        return null;
    }

    protected static void notifyConsumer(PooledByteBufferOutputStream pooledByteBufferOutputStream, int i, @Nullable BytesRange bytesRange, Consumer<EncodedImage> consumer, ProducerContext producerContext) throws Throwable {
        EncodedImage encodedImage;
        CloseableReference closeableReferenceOf = CloseableReference.of(pooledByteBufferOutputStream.toByteBuffer());
        try {
            encodedImage = new EncodedImage((CloseableReference<PooledByteBuffer>) closeableReferenceOf);
            try {
                encodedImage.setBytesRange(bytesRange);
                encodedImage.parseMetaData();
                producerContext.setEncodedImageOrigin(EncodedImageOrigin.NETWORK);
                consumer.onNewResult(encodedImage, i);
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

    /* JADX INFO: Access modifiers changed from: private */
    public void onCancellation(FetchState fetchState) {
        fetchState.getListener().onProducerFinishWithCancellation(fetchState.getContext(), PRODUCER_NAME, null);
        fetchState.getConsumer().onCancellation();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFailure(FetchState fetchState, Throwable th) {
        fetchState.getListener().onProducerFinishWithFailure(fetchState.getContext(), PRODUCER_NAME, th, null);
        fetchState.getListener().onUltimateProducerReached(fetchState.getContext(), PRODUCER_NAME, false);
        fetchState.getContext().putOriginExtra(LocationManager.NETWORK_PROVIDER);
        fetchState.getConsumer().onFailure(th);
    }

    private boolean shouldPropagateIntermediateResults(FetchState fetchState) {
        if (fetchState.getContext().isIntermediateResultExpected()) {
            return this.mNetworkFetcher.shouldPropagate(fetchState);
        }
        return false;
    }

    @VisibleForTesting
    protected long getSystemUptime() {
        return SystemClock.uptimeMillis();
    }

    protected void handleFinalResult(PooledByteBufferOutputStream pooledByteBufferOutputStream, FetchState fetchState) throws Throwable {
        Map<String, String> extraMap = getExtraMap(fetchState, pooledByteBufferOutputStream.size());
        ProducerListener2 listener = fetchState.getListener();
        listener.onProducerFinishWithSuccess(fetchState.getContext(), PRODUCER_NAME, extraMap);
        listener.onUltimateProducerReached(fetchState.getContext(), PRODUCER_NAME, true);
        fetchState.getContext().putOriginExtra(LocationManager.NETWORK_PROVIDER);
        notifyConsumer(pooledByteBufferOutputStream, fetchState.getOnNewResultStatusFlags() | 1, fetchState.getResponseBytesRange(), fetchState.getConsumer(), fetchState.getContext());
    }

    protected void maybeHandleIntermediateResult(PooledByteBufferOutputStream pooledByteBufferOutputStream, FetchState fetchState) throws Throwable {
        long systemUptime = getSystemUptime();
        if (!shouldPropagateIntermediateResults(fetchState) || systemUptime - fetchState.getLastIntermediateResultTimeMs() < TIME_BETWEEN_PARTIAL_RESULTS_MS) {
            return;
        }
        fetchState.setLastIntermediateResultTimeMs(systemUptime);
        fetchState.getListener().onProducerEvent(fetchState.getContext(), PRODUCER_NAME, INTERMEDIATE_RESULT_PRODUCER_EVENT);
        notifyConsumer(pooledByteBufferOutputStream, fetchState.getOnNewResultStatusFlags(), fetchState.getResponseBytesRange(), fetchState.getConsumer(), fetchState.getContext());
    }

    protected void onResponse(FetchState fetchState, InputStream inputStream, int i) throws Throwable {
        PooledByteBufferOutputStream pooledByteBufferOutputStreamNewOutputStream = i > 0 ? this.mPooledByteBufferFactory.newOutputStream(i) : this.mPooledByteBufferFactory.newOutputStream();
        byte[] bArr = this.mByteArrayPool.get(16384);
        while (true) {
            try {
                int i2 = inputStream.read(bArr);
                if (i2 < 0) {
                    this.mNetworkFetcher.onFetchCompletion(fetchState, pooledByteBufferOutputStreamNewOutputStream.size());
                    handleFinalResult(pooledByteBufferOutputStreamNewOutputStream, fetchState);
                    this.mByteArrayPool.release(bArr);
                    pooledByteBufferOutputStreamNewOutputStream.close();
                    return;
                }
                if (i2 > 0) {
                    pooledByteBufferOutputStreamNewOutputStream.write(bArr, 0, i2);
                    maybeHandleIntermediateResult(pooledByteBufferOutputStreamNewOutputStream, fetchState);
                    fetchState.getConsumer().onProgressUpdate(calculateProgress(pooledByteBufferOutputStreamNewOutputStream.size(), i));
                }
            } catch (Throwable th) {
                this.mByteArrayPool.release(bArr);
                pooledByteBufferOutputStreamNewOutputStream.close();
                throw th;
            }
        }
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(Consumer<EncodedImage> consumer, ProducerContext producerContext) {
        producerContext.getProducerListener().onProducerStart(producerContext, PRODUCER_NAME);
        final FetchState fetchStateCreateFetchState = this.mNetworkFetcher.createFetchState(consumer, producerContext);
        this.mNetworkFetcher.fetch(fetchStateCreateFetchState, new NetworkFetcher.Callback() { // from class: com.facebook.imagepipeline.producers.NetworkFetchProducer.1
            @Override // com.facebook.imagepipeline.producers.NetworkFetcher.Callback
            public void onCancellation() {
                NetworkFetchProducer.this.onCancellation(fetchStateCreateFetchState);
            }

            @Override // com.facebook.imagepipeline.producers.NetworkFetcher.Callback
            public void onFailure(Throwable th) {
                NetworkFetchProducer.this.onFailure(fetchStateCreateFetchState, th);
            }

            @Override // com.facebook.imagepipeline.producers.NetworkFetcher.Callback
            public void onResponse(InputStream inputStream, int i) throws Throwable {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.beginSection("NetworkFetcher->onResponse");
                }
                NetworkFetchProducer.this.onResponse(fetchStateCreateFetchState, inputStream, i);
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
            }
        });
    }
}
