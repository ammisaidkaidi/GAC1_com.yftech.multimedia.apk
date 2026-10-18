package com.facebook.drawee.backends.pipeline.info;

/* JADX INFO: loaded from: classes3.dex */
public interface ImagePerfNotifier {
    void notifyListenersOfVisibilityStateUpdate(ImagePerfState imagePerfState, int i);

    void notifyStatusUpdated(ImagePerfState imagePerfState, int i);
}
