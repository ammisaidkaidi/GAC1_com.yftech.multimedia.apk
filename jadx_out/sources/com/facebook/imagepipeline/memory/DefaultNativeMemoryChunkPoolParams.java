package com.facebook.imagepipeline.memory;

import android.content.IntentFilter;
import android.util.SparseIntArray;
import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class DefaultNativeMemoryChunkPoolParams {
    private static final int LARGE_BUCKET_LENGTH = 2;
    private static final int SMALL_BUCKET_LENGTH = 5;

    public static PoolParams get() {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sparseIntArray.put(1024, 5);
        sparseIntArray.put(2048, 5);
        sparseIntArray.put(4096, 5);
        sparseIntArray.put(8192, 5);
        sparseIntArray.put(16384, 5);
        sparseIntArray.put(32768, 5);
        sparseIntArray.put(65536, 5);
        sparseIntArray.put(131072, 5);
        sparseIntArray.put(262144, 2);
        sparseIntArray.put(524288, 2);
        sparseIntArray.put(1048576, 2);
        return new PoolParams(getMaxSizeSoftCap(), getMaxSizeHardCap(), sparseIntArray);
    }

    private static int getMaxSizeHardCap() {
        int iMin = (int) Math.min(Runtime.getRuntime().maxMemory(), 2147483647L);
        return iMin < 16777216 ? iMin / 2 : (iMin / 4) * 3;
    }

    private static int getMaxSizeSoftCap() {
        int iMin = (int) Math.min(Runtime.getRuntime().maxMemory(), 2147483647L);
        if (iMin < 16777216) {
            return IntentFilter.MATCH_CATEGORY_HOST;
        }
        if (iMin < 33554432) {
            return IntentFilter.MATCH_CATEGORY_TYPE;
        }
        return 12582912;
    }
}
