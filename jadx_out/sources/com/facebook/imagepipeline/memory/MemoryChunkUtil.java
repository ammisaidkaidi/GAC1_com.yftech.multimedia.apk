package com.facebook.imagepipeline.memory;

import com.facebook.common.internal.Preconditions;
import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class MemoryChunkUtil {
    static int adjustByteCount(int i, int i2, int i3) {
        return Math.min(Math.max(0, i3 - i), i2);
    }

    static void checkBounds(int i, int i2, int i3, int i4, int i5) {
        Preconditions.checkArgument(Boolean.valueOf(i4 >= 0));
        Preconditions.checkArgument(Boolean.valueOf(i >= 0));
        Preconditions.checkArgument(Boolean.valueOf(i3 >= 0));
        Preconditions.checkArgument(Boolean.valueOf(i + i4 <= i5));
        Preconditions.checkArgument(Boolean.valueOf(i3 + i4 <= i2));
    }
}
