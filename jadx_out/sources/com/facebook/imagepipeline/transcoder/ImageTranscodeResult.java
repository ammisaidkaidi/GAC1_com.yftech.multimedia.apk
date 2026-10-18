package com.facebook.imagepipeline.transcoder;

import com.facebook.infer.annotation.Nullsafe;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class ImageTranscodeResult {
    private final int mTranscodeStatus;

    public ImageTranscodeResult(int i) {
        this.mTranscodeStatus = i;
    }

    public int getTranscodeStatus() {
        return this.mTranscodeStatus;
    }

    public String toString() {
        return String.format((Locale) null, "Status: %d", Integer.valueOf(this.mTranscodeStatus));
    }
}
