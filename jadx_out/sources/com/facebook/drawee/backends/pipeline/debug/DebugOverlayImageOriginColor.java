package com.facebook.drawee.backends.pipeline.debug;

import android.graphics.Color;
import android.util.SparseIntArray;
import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class DebugOverlayImageOriginColor {
    private static final SparseIntArray IMAGE_ORIGIN_COLOR_MAP = new SparseIntArray(7);

    static {
        IMAGE_ORIGIN_COLOR_MAP.append(1, Color.GRAY);
        IMAGE_ORIGIN_COLOR_MAP.append(2, -65536);
        IMAGE_ORIGIN_COLOR_MAP.append(3, -256);
        IMAGE_ORIGIN_COLOR_MAP.append(4, -256);
        IMAGE_ORIGIN_COLOR_MAP.append(5, Color.GREEN);
        IMAGE_ORIGIN_COLOR_MAP.append(6, Color.GREEN);
        IMAGE_ORIGIN_COLOR_MAP.append(7, Color.GREEN);
    }

    public static int getImageOriginColor(int i) {
        return IMAGE_ORIGIN_COLOR_MAP.get(i, -1);
    }
}
