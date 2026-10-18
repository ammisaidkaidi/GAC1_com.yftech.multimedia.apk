package com.facebook.imagepipeline.datasource;

import com.facebook.common.internal.Preconditions;
import com.facebook.datasource.AbstractDataSource;
import com.facebook.imagepipeline.listener.RequestListener2;
import com.facebook.imagepipeline.producers.BaseConsumer;
import com.facebook.imagepipeline.producers.Consumer;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.ProducerContext;
import com.facebook.imagepipeline.producers.SettableProducerContext;
import com.facebook.imagepipeline.request.HasImageRequest;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.infer.annotation.Nullsafe;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@ThreadSafe
@Nullsafe(Nullsafe.Mode.STRICT)
public abstract class AbstractProducerToDataSourceAdapter<T> extends AbstractDataSource<T> implements HasImageRequest {
    private final RequestListener2 mRequestListener;
    private final SettableProducerContext mSettableProducerContext;

    protected AbstractProducerToDataSourceAdapter(Producer<T> producer, SettableProducerContext settableProducerContext, RequestListener2 requestListener2) {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("AbstractProducerToDataSourceAdapter()");
        }
        this.mSettableProducerContext = settableProducerContext;
        this.mRequestListener = requestListener2;
        setInitialExtras();
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("AbstractProducerToDataSourceAdapter()->onRequestStart");
        }
        this.mRequestListener.onRequestStart(this.mSettableProducerContext);
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("AbstractProducerToDataSourceAdapter()->produceResult");
        }
        producer.produceResults(createConsumer(), settableProducerContext);
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
    }

    private Consumer<T> createConsumer() {
        return new BaseConsumer<T>() { // from class: com.facebook.imagepipeline.datasource.AbstractProducerToDataSourceAdapter.1
            @Override // com.facebook.imagepipeline.producers.BaseConsumer
            protected void onCancellationImpl() {
                AbstractProducerToDataSourceAdapter.this.onCancellationImpl();
            }

            @Override // com.facebook.imagepipeline.producers.BaseConsumer
            protected void onFailureImpl(Throwable th) {
                AbstractProducerToDataSourceAdapter.this.onFailureImpl(th);
            }

            @Override // com.facebook.imagepipeline.producers.BaseConsumer
            protected void onNewResultImpl(@Nullable T t, int i) {
                AbstractProducerToDataSourceAdapter.this.onNewResultImpl(t, i, AbstractProducerToDataSourceAdapter.this.mSettableProducerContext);
            }

            @Override // com.facebook.imagepipeline.producers.BaseConsumer
            protected void onProgressUpdateImpl(float f) {
                AbstractProducerToDataSourceAdapter.this.setProgress(f);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void onCancellationImpl() {
        Preconditions.checkState(isClosed());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFailureImpl(Throwable th) {
        if (super.setFailure(th, getExtras(this.mSettableProducerContext))) {
            this.mRequestListener.onRequestFailure(this.mSettableProducerContext, th);
        }
    }

    private void setInitialExtras() {
        setExtras(this.mSettableProducerContext.getExtras());
    }

    @Override // com.facebook.datasource.AbstractDataSource, com.facebook.datasource.DataSource
    public boolean close() {
        if (!super.close()) {
            return false;
        }
        if (super.isFinished()) {
            return true;
        }
        this.mRequestListener.onRequestCancellation(this.mSettableProducerContext);
        this.mSettableProducerContext.cancel();
        return true;
    }

    protected Map<String, Object> getExtras(ProducerContext producerContext) {
        return producerContext.getExtras();
    }

    @Override // com.facebook.imagepipeline.request.HasImageRequest
    public ImageRequest getImageRequest() {
        return this.mSettableProducerContext.getImageRequest();
    }

    protected void onNewResultImpl(@Nullable T t, int i, ProducerContext producerContext) {
        boolean zIsLast = BaseConsumer.isLast(i);
        if (super.setResult(t, zIsLast, getExtras(producerContext)) && zIsLast) {
            this.mRequestListener.onRequestSuccess(this.mSettableProducerContext);
        }
    }
}
