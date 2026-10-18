package com.facebook.imagepipeline.bitmaps;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.webp.BitmapCreator;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.FlexByteArrayPool;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class HoneycombBitmapCreator implements BitmapCreator {
    private final FlexByteArrayPool mFlexByteArrayPool;
    private final EmptyJpegGenerator mJpegGenerator;

    public HoneycombBitmapCreator(PoolFactory poolFactory) {
        this.mFlexByteArrayPool = poolFactory.getFlexByteArrayPool();
        this.mJpegGenerator = new EmptyJpegGenerator(poolFactory.getPooledByteBufferFactory());
    }

    private static BitmapFactory.Options getBitmapFactoryOptions(int i, Bitmap.Config config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = true;
        options.inPreferredConfig = config;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = i;
        if (Build.VERSION.SDK_INT >= 11) {
            options.inMutable = true;
        }
        return options;
    }

    @Override // com.facebook.common.webp.BitmapCreator
    @TargetApi(12)
    public Bitmap createNakedBitmap(int i, int i2, Bitmap.Config config) throws Throwable {
        EncodedImage encodedImage;
        CloseableReference<PooledByteBuffer> closeableReferenceGenerate = this.mJpegGenerator.generate((short) i, (short) i2);
        CloseableReference<byte[]> closeableReference = null;
        try {
            encodedImage = new EncodedImage(closeableReferenceGenerate);
            try {
                encodedImage.setImageFormat(DefaultImageFormats.JPEG);
                BitmapFactory.Options bitmapFactoryOptions = getBitmapFactoryOptions(encodedImage.getSampleSize(), config);
                int size = closeableReferenceGenerate.get().size();
                PooledByteBuffer pooledByteBuffer = closeableReferenceGenerate.get();
                CloseableReference<byte[]> closeableReference2 = this.mFlexByteArrayPool.get(size + 2);
                try {
                    byte[] bArr = closeableReference2.get();
                    pooledByteBuffer.read(0, bArr, 0, size);
                    Bitmap bitmap = (Bitmap) Preconditions.checkNotNull(BitmapFactory.decodeByteArray(bArr, 0, size, bitmapFactoryOptions));
                    bitmap.setHasAlpha(true);
                    bitmap.eraseColor(0);
                    CloseableReference.closeSafely((CloseableReference<?>) closeableReference2);
                    EncodedImage.closeSafely(encodedImage);
                    CloseableReference.closeSafely(closeableReferenceGenerate);
                    return bitmap;
                } catch (Throwable th) {
                    closeableReference = closeableReference2;
                    th = th;
                    CloseableReference.closeSafely((CloseableReference<?>) closeableReference);
                    EncodedImage.closeSafely(encodedImage);
                    CloseableReference.closeSafely(closeableReferenceGenerate);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Throwable th3) {
            th = th3;
            encodedImage = null;
        }
    }
}
