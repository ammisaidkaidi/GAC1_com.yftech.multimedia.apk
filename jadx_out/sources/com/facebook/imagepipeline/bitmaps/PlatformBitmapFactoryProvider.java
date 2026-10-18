package com.facebook.imagepipeline.bitmaps;

import android.os.Build;
import com.facebook.imagepipeline.core.CloseableReferenceFactory;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.facebook.imagepipeline.platform.PlatformDecoder;
import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class PlatformBitmapFactoryProvider {
    public static PlatformBitmapFactory buildPlatformBitmapFactory(PoolFactory poolFactory, PlatformDecoder platformDecoder, CloseableReferenceFactory closeableReferenceFactory) {
        if (Build.VERSION.SDK_INT >= 21) {
            return new ArtBitmapFactory(poolFactory.getBitmapPool(), closeableReferenceFactory);
        }
        return Build.VERSION.SDK_INT >= 11 ? new HoneycombBitmapFactory(new EmptyJpegGenerator(poolFactory.getPooledByteBufferFactory()), platformDecoder, closeableReferenceFactory) : new GingerbreadBitmapFactory();
    }
}
