package com.facebook.imagepipeline.producers;

import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public abstract class DelegatingConsumer<I, O> extends BaseConsumer<I> {
    private final Consumer<O> mConsumer;

    public DelegatingConsumer(Consumer<O> consumer) {
        this.mConsumer = consumer;
    }

    public Consumer<O> getConsumer() {
        return this.mConsumer;
    }

    @Override // com.facebook.imagepipeline.producers.BaseConsumer
    protected void onCancellationImpl() {
        this.mConsumer.onCancellation();
    }

    @Override // com.facebook.imagepipeline.producers.BaseConsumer
    protected void onFailureImpl(Throwable th) {
        this.mConsumer.onFailure(th);
    }

    @Override // com.facebook.imagepipeline.producers.BaseConsumer
    protected void onProgressUpdateImpl(float f) {
        this.mConsumer.onProgressUpdate(f);
    }
}
