package com.facebook.imagepipeline.producers;

import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public interface Producer<T> {
    void produceResults(Consumer<T> consumer, ProducerContext producerContext);
}
