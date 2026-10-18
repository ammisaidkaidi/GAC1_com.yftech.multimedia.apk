package com.facebook.imagepipeline.memory;

import com.facebook.infer.annotation.Nullsafe;
import com.facebook.infer.annotation.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class BitmapCounterProvider {
    private static final long KB = 1024;
    private static final long MB = 1048576;
    private static volatile BitmapCounter sBitmapCounter;
    public static final int MAX_BITMAP_TOTAL_SIZE = getMaxSizeHardCap();
    private static int sMaxBitmapCount = 384;

    @ThreadSafe
    public static BitmapCounter get() {
        if (sBitmapCounter == null) {
            synchronized (BitmapCounterProvider.class) {
                if (sBitmapCounter == null) {
                    sBitmapCounter = new BitmapCounter(sMaxBitmapCount, MAX_BITMAP_TOTAL_SIZE);
                }
            }
        }
        return sBitmapCounter;
    }

    private static int getMaxSizeHardCap() {
        int iMin = (int) Math.min(Runtime.getRuntime().maxMemory(), 2147483647L);
        return ((long) iMin) > 16777216 ? (iMin / 4) * 3 : iMin / 2;
    }

    public static void initialize(BitmapCounterConfig bitmapCounterConfig) {
        if (sBitmapCounter != null) {
            throw new IllegalStateException("BitmapCounter has already been created! `BitmapCounterProvider.initialize(...)` should only be called before `BitmapCounterProvider.get()` or not at all!");
        }
        sMaxBitmapCount = bitmapCounterConfig.getMaxBitmapCount();
    }
}
