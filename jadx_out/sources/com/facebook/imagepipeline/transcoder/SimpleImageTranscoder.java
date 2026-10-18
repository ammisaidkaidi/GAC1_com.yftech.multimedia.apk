package com.facebook.imagepipeline.transcoder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import com.facebook.common.logging.FLog;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.image.EncodedImage;
import java.io.OutputStream;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class SimpleImageTranscoder implements ImageTranscoder {
    private static final String TAG = "SimpleImageTranscoder";
    private final int mMaxBitmapSize;
    private final boolean mResizingEnabled;

    public SimpleImageTranscoder(boolean z, int i) {
        this.mResizingEnabled = z;
        this.mMaxBitmapSize = i;
    }

    private static Bitmap.CompressFormat getOutputFormat(@Nullable ImageFormat imageFormat) {
        if (imageFormat != null && imageFormat != DefaultImageFormats.JPEG) {
            if (imageFormat == DefaultImageFormats.PNG) {
                return Bitmap.CompressFormat.PNG;
            }
            return (Build.VERSION.SDK_INT < 14 || !DefaultImageFormats.isStaticWebpFormat(imageFormat)) ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.WEBP;
        }
        return Bitmap.CompressFormat.JPEG;
    }

    private int getSampleSize(EncodedImage encodedImage, RotationOptions rotationOptions, @Nullable ResizeOptions resizeOptions) {
        if (this.mResizingEnabled) {
            return DownsampleUtil.determineSampleSize(rotationOptions, resizeOptions, encodedImage, this.mMaxBitmapSize);
        }
        return 1;
    }

    @Override // com.facebook.imagepipeline.transcoder.ImageTranscoder
    public boolean canResize(EncodedImage encodedImage, @Nullable RotationOptions rotationOptions, @Nullable ResizeOptions resizeOptions) {
        if (rotationOptions == null) {
            rotationOptions = RotationOptions.autoRotate();
        }
        return this.mResizingEnabled && DownsampleUtil.determineSampleSize(rotationOptions, resizeOptions, encodedImage, this.mMaxBitmapSize) > 1;
    }

    @Override // com.facebook.imagepipeline.transcoder.ImageTranscoder
    public boolean canTranscode(ImageFormat imageFormat) {
        return imageFormat == DefaultImageFormats.HEIF || imageFormat == DefaultImageFormats.JPEG;
    }

    @Override // com.facebook.imagepipeline.transcoder.ImageTranscoder
    public String getIdentifier() {
        return TAG;
    }

    @Override // com.facebook.imagepipeline.transcoder.ImageTranscoder
    public ImageTranscodeResult transcode(EncodedImage encodedImage, OutputStream outputStream, @Nullable RotationOptions rotationOptions, @Nullable ResizeOptions resizeOptions, @Nullable ImageFormat imageFormat, @Nullable Integer num) throws Throwable {
        Bitmap bitmapCreateBitmap;
        Throwable th;
        OutOfMemoryError e;
        Integer num2 = num == null ? 85 : num;
        RotationOptions rotationOptionsAutoRotate = rotationOptions == null ? RotationOptions.autoRotate() : rotationOptions;
        int sampleSize = getSampleSize(encodedImage, rotationOptionsAutoRotate, resizeOptions);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        try {
            Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(encodedImage.getInputStream(), null, options);
            if (bitmapDecodeStream == null) {
                FLog.e(TAG, "Couldn't decode the EncodedImage InputStream ! ");
                return new ImageTranscodeResult(2);
            }
            Matrix transformationMatrix = JpegTranscoderUtils.getTransformationMatrix(encodedImage, rotationOptionsAutoRotate);
            if (transformationMatrix != null) {
                try {
                    bitmapCreateBitmap = Bitmap.createBitmap(bitmapDecodeStream, 0, 0, bitmapDecodeStream.getWidth(), bitmapDecodeStream.getHeight(), transformationMatrix, false);
                } catch (OutOfMemoryError e2) {
                    e = e2;
                    bitmapCreateBitmap = bitmapDecodeStream;
                    FLog.e(TAG, "Out-Of-Memory during transcode", e);
                    ImageTranscodeResult imageTranscodeResult = new ImageTranscodeResult(2);
                    bitmapCreateBitmap.recycle();
                    bitmapDecodeStream.recycle();
                    return imageTranscodeResult;
                } catch (Throwable th2) {
                    th = th2;
                    bitmapCreateBitmap = bitmapDecodeStream;
                    bitmapCreateBitmap.recycle();
                    bitmapDecodeStream.recycle();
                    throw th;
                }
            } else {
                bitmapCreateBitmap = bitmapDecodeStream;
            }
            try {
                try {
                    bitmapCreateBitmap.compress(getOutputFormat(imageFormat), num2.intValue(), outputStream);
                    ImageTranscodeResult imageTranscodeResult2 = new ImageTranscodeResult(sampleSize > 1 ? 0 : 1);
                    bitmapCreateBitmap.recycle();
                    bitmapDecodeStream.recycle();
                    return imageTranscodeResult2;
                } catch (OutOfMemoryError e3) {
                    e = e3;
                    FLog.e(TAG, "Out-Of-Memory during transcode", e);
                    ImageTranscodeResult imageTranscodeResult3 = new ImageTranscodeResult(2);
                    bitmapCreateBitmap.recycle();
                    bitmapDecodeStream.recycle();
                    return imageTranscodeResult3;
                }
            } catch (Throwable th3) {
                th = th3;
                bitmapCreateBitmap.recycle();
                bitmapDecodeStream.recycle();
                throw th;
            }
        } catch (OutOfMemoryError e4) {
            FLog.e(TAG, "Out-Of-Memory during transcode", e4);
            return new ImageTranscodeResult(2);
        }
    }
}
