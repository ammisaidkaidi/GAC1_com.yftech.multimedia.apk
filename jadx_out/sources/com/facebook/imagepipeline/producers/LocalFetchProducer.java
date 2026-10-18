package com.facebook.imagepipeline.producers;

import com.facebook.common.internal.Closeables;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public abstract class LocalFetchProducer implements Producer<EncodedImage> {
    private final Executor mExecutor;
    private final PooledByteBufferFactory mPooledByteBufferFactory;

    protected LocalFetchProducer(Executor executor, PooledByteBufferFactory pooledByteBufferFactory) {
        this.mExecutor = executor;
        this.mPooledByteBufferFactory = pooledByteBufferFactory;
    }

    protected EncodedImage getByteBufferBackedEncodedImage(InputStream inputStream, int i) throws IOException {
        CloseableReference closeableReferenceOf = null;
        try {
            closeableReferenceOf = i <= 0 ? CloseableReference.of(this.mPooledByteBufferFactory.newByteBuffer(inputStream)) : CloseableReference.of(this.mPooledByteBufferFactory.newByteBuffer(inputStream, i));
            EncodedImage encodedImage = new EncodedImage((CloseableReference<PooledByteBuffer>) closeableReferenceOf);
            Closeables.closeQuietly(inputStream);
            CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceOf);
            return encodedImage;
        } catch (Throwable th) {
            Closeables.closeQuietly(inputStream);
            CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceOf);
            throw th;
        }
    }

    protected abstract EncodedImage getEncodedImage(ImageRequest imageRequest) throws IOException;

    protected EncodedImage getEncodedImage(InputStream inputStream, int i) throws IOException {
        return getByteBufferBackedEncodedImage(inputStream, i);
    }

    protected abstract String getProducerName();

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(Consumer<EncodedImage> consumer, final ProducerContext producerContext) {
        final ProducerListener2 producerListener = producerContext.getProducerListener();
        final ImageRequest imageRequest = producerContext.getImageRequest();
        producerContext.putOriginExtra("local", "fetch");
        final StatefulProducerRunnable<EncodedImage> statefulProducerRunnable = new StatefulProducerRunnable<EncodedImage>(consumer, producerListener, producerContext, getProducerName()) { // from class: com.facebook.imagepipeline.producers.LocalFetchProducer.1
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.facebook.imagepipeline.producers.StatefulProducerRunnable, com.facebook.common.executors.StatefulRunnable
            public void disposeResult(EncodedImage encodedImage) {
                EncodedImage.closeSafely(encodedImage);
            }

            /* JADX INFO: Access modifiers changed from: protected */
            @Override // com.facebook.common.executors.StatefulRunnable
            @Nullable
            public EncodedImage getResult() throws Exception {
                EncodedImage encodedImage = LocalFetchProducer.this.getEncodedImage(imageRequest);
                if (encodedImage == null) {
                    producerListener.onUltimateProducerReached(producerContext, LocalFetchProducer.this.getProducerName(), false);
                    producerContext.putOriginExtra("local");
                    return null;
                }
                encodedImage.parseMetaData();
                producerListener.onUltimateProducerReached(producerContext, LocalFetchProducer.this.getProducerName(), true);
                producerContext.putOriginExtra("local");
                return encodedImage;
            }
        };
        producerContext.addCallbacks(new BaseProducerContextCallbacks() { // from class: com.facebook.imagepipeline.producers.LocalFetchProducer.2
            @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
            public void onCancellationRequested() {
                statefulProducerRunnable.cancel();
            }
        });
        this.mExecutor.execute(statefulProducerRunnable);
    }
}
