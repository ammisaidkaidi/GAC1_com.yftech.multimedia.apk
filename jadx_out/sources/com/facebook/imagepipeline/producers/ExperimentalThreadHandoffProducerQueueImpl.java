package com.facebook.imagepipeline.producers;

import com.facebook.common.internal.Preconditions;
import com.facebook.infer.annotation.Nullsafe;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class ExperimentalThreadHandoffProducerQueueImpl implements ThreadHandoffProducerQueue {
    private final Executor mExecutor;

    public ExperimentalThreadHandoffProducerQueueImpl(Executor executor) {
        this.mExecutor = (Executor) Preconditions.checkNotNull(executor);
    }

    @Override // com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue
    public void addToQueueOrExecute(Runnable runnable) {
        this.mExecutor.execute(runnable);
    }

    @Override // com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue
    public boolean isQueueing() {
        return false;
    }

    @Override // com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue
    public void remove(Runnable runnable) {
    }

    @Override // com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue
    public void startQueueing() {
        throw new UnsupportedOperationException();
    }

    @Override // com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue
    public void stopQueuing() {
        throw new UnsupportedOperationException();
    }
}
