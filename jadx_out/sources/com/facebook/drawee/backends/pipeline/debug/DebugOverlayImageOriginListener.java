package com.facebook.drawee.backends.pipeline.debug;

import com.facebook.drawee.backends.pipeline.info.ImageOriginListener;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class DebugOverlayImageOriginListener implements ImageOriginListener {
    private int mImageOrigin = 1;

    public int getImageOrigin() {
        return this.mImageOrigin;
    }

    @Override // com.facebook.drawee.backends.pipeline.info.ImageOriginListener
    public void onImageLoaded(String str, int i, boolean z, @Nullable String str2) {
        this.mImageOrigin = i;
    }
}
