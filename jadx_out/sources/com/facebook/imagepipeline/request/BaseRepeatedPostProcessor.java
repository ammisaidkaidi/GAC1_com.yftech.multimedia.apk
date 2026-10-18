package com.facebook.imagepipeline.request;

import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public abstract class BaseRepeatedPostProcessor extends BasePostprocessor implements RepeatedPostprocessor {

    @Nullable
    private RepeatedPostprocessorRunner mCallback;

    @Nullable
    private synchronized RepeatedPostprocessorRunner getCallback() {
        return this.mCallback;
    }

    @Override // com.facebook.imagepipeline.request.RepeatedPostprocessor
    public synchronized void setCallback(RepeatedPostprocessorRunner repeatedPostprocessorRunner) {
        this.mCallback = repeatedPostprocessorRunner;
    }

    public void update() {
        RepeatedPostprocessorRunner callback = getCallback();
        if (callback != null) {
            callback.update();
        }
    }
}
