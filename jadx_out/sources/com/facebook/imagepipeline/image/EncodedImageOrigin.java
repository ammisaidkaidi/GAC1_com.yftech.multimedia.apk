package com.facebook.imagepipeline.image;

import android.location.LocationManager;
import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public enum EncodedImageOrigin {
    NOT_SET("not_set"),
    NETWORK(LocationManager.NETWORK_PROVIDER),
    DISK("disk"),
    ENCODED_MEM_CACHE("encoded_mem_cache");

    private final String mOrigin;

    EncodedImageOrigin(String str) {
        this.mOrigin = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.mOrigin;
    }
}
