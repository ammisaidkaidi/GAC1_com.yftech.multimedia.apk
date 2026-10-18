package com.facebook.imagepipeline.decoder;

import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class DecodeException extends RuntimeException {
    private final EncodedImage mEncodedImage;

    public DecodeException(String str, EncodedImage encodedImage) {
        super(str);
        this.mEncodedImage = encodedImage;
    }

    public DecodeException(String str, Throwable th, EncodedImage encodedImage) {
        super(str, th);
        this.mEncodedImage = encodedImage;
    }

    public EncodedImage getEncodedImage() {
        return this.mEncodedImage;
    }
}
