package com.facebook.imagepipeline.bitmaps;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imagepipeline.core.CloseableReferenceFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.platform.PlatformDecoder;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.concurrent.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@ThreadSafe
@TargetApi(11)
@Nullsafe(Nullsafe.Mode.LOCAL)
public class HoneycombBitmapFactory extends PlatformBitmapFactory {
    private static final String TAG = "HoneycombBitmapFactory";
    private final CloseableReferenceFactory mCloseableReferenceFactory;
    private boolean mImmutableBitmapFallback;
    private final EmptyJpegGenerator mJpegGenerator;
    private final PlatformDecoder mPurgeableDecoder;

    public HoneycombBitmapFactory(EmptyJpegGenerator emptyJpegGenerator, PlatformDecoder platformDecoder, CloseableReferenceFactory closeableReferenceFactory) {
        this.mJpegGenerator = emptyJpegGenerator;
        this.mPurgeableDecoder = platformDecoder;
        this.mCloseableReferenceFactory = closeableReferenceFactory;
    }

    private CloseableReference<Bitmap> createFallbackBitmap(int i, int i2, Bitmap.Config config) {
        return this.mCloseableReferenceFactory.create(Bitmap.createBitmap(i, i2, config), SimpleBitmapReleaser.getInstance());
    }

    @Override // com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory
    @TargetApi(12)
    public CloseableReference<Bitmap> createBitmapInternal(int i, int i2, Bitmap.Config config) throws Throwable {
        if (this.mImmutableBitmapFallback) {
            return createFallbackBitmap(i, i2, config);
        }
        CloseableReference<PooledByteBuffer> closeableReferenceGenerate = this.mJpegGenerator.generate((short) i, (short) i2);
        try {
            EncodedImage encodedImage = new EncodedImage(closeableReferenceGenerate);
            encodedImage.setImageFormat(DefaultImageFormats.JPEG);
            try {
                CloseableReference<Bitmap> closeableReferenceDecodeJPEGFromEncodedImage = this.mPurgeableDecoder.decodeJPEGFromEncodedImage(encodedImage, config, null, closeableReferenceGenerate.get().size());
                if (closeableReferenceDecodeJPEGFromEncodedImage.get().isMutable()) {
                    closeableReferenceDecodeJPEGFromEncodedImage.get().setHasAlpha(true);
                    closeableReferenceDecodeJPEGFromEncodedImage.get().eraseColor(0);
                    EncodedImage.closeSafely(encodedImage);
                    closeableReferenceGenerate.close();
                    return closeableReferenceDecodeJPEGFromEncodedImage;
                }
                CloseableReference.closeSafely(closeableReferenceDecodeJPEGFromEncodedImage);
                this.mImmutableBitmapFallback = true;
                FLog.wtf(TAG, "Immutable bitmap returned by decoder");
                CloseableReference<Bitmap> closeableReferenceCreateFallbackBitmap = createFallbackBitmap(i, i2, config);
                EncodedImage.closeSafely(encodedImage);
                closeableReferenceGenerate.close();
                return closeableReferenceCreateFallbackBitmap;
            } catch (Throwable th) {
                EncodedImage.closeSafely(encodedImage);
                throw th;
            }
        } catch (Throwable th2) {
            closeableReferenceGenerate.close();
            throw th2;
        }
    }
}
