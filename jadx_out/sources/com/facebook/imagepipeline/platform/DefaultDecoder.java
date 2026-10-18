package com.facebook.imagepipeline.platform;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.ColorSpace;
import android.graphics.Rect;
import android.os.Build;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pools;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.streams.LimitedInputStream;
import com.facebook.common.streams.TailAppendingInputStream;
import com.facebook.imagepipeline.bitmaps.SimpleBitmapReleaser;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.memory.BitmapPool;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@ThreadSafe
@TargetApi(11)
public abstract class DefaultDecoder implements PlatformDecoder {
    private static final int DECODE_BUFFER_SIZE = 16384;
    private final BitmapPool mBitmapPool;

    @VisibleForTesting
    final Pools.SynchronizedPool<ByteBuffer> mDecodeBuffers;

    @Nullable
    private final PreverificationHelper mPreverificationHelper;
    private static final Class<?> TAG = DefaultDecoder.class;
    private static final byte[] EOI_TAIL = {-1, -39};

    public DefaultDecoder(BitmapPool bitmapPool, int i, Pools.SynchronizedPool synchronizedPool) {
        this.mPreverificationHelper = Build.VERSION.SDK_INT >= 26 ? new PreverificationHelper() : null;
        this.mBitmapPool = bitmapPool;
        this.mDecodeBuffers = synchronizedPool;
        for (int i2 = 0; i2 < i; i2++) {
            this.mDecodeBuffers.release(ByteBuffer.allocate(16384));
        }
    }

    /* JADX WARN: Code duplicated, block: B:48:0x00aa A[Catch: all -> 0x00d4, RuntimeException -> 0x00d6, IllegalArgumentException -> 0x00df, TryCatch #8 {IllegalArgumentException -> 0x00df, RuntimeException -> 0x00d6, blocks: (B:30:0x006f, B:38:0x008e, B:52:0x00b1, B:45:0x00a3, B:48:0x00aa, B:49:0x00ad), top: B:82:0x006f, outer: #3 }] */
    /* JADX WARN: Code duplicated, block: B:52:0x00b1 A[Catch: all -> 0x00d4, RuntimeException -> 0x00d6, IllegalArgumentException -> 0x00df, TRY_LEAVE, TryCatch #8 {IllegalArgumentException -> 0x00df, RuntimeException -> 0x00d6, blocks: (B:30:0x006f, B:38:0x008e, B:52:0x00b1, B:45:0x00a3, B:48:0x00aa, B:49:0x00ad), top: B:82:0x006f, outer: #3 }] */
    private CloseableReference<Bitmap> decodeFromStream(InputStream inputStream, BitmapFactory.Options options, @Nullable Rect rect, @Nullable ColorSpace colorSpace) {
        Bitmap bitmap;
        Bitmap bitmapDecodeStream;
        BitmapRegionDecoder bitmapRegionDecoderNewInstance;
        Preconditions.checkNotNull(inputStream);
        int iWidth = options.outWidth;
        int iHeight = options.outHeight;
        if (rect != null) {
            iWidth = rect.width() / options.inSampleSize;
            iHeight = rect.height() / options.inSampleSize;
        }
        boolean z = Build.VERSION.SDK_INT >= 26 && this.mPreverificationHelper != null && this.mPreverificationHelper.shouldUseHardwareBitmapConfig(options.inPreferredConfig);
        if (rect == null && z) {
            options.inMutable = false;
            bitmap = null;
        } else {
            if (rect != null && z) {
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            }
            bitmap = this.mBitmapPool.get(getBitmapSize(iWidth, iHeight, options));
            if (bitmap == null) {
                throw new NullPointerException("BitmapPool.get returned null");
            }
        }
        options.inBitmap = bitmap;
        if (Build.VERSION.SDK_INT >= 26) {
            if (colorSpace == null) {
                colorSpace = ColorSpace.get(ColorSpace.Named.SRGB);
            }
            options.inPreferredColorSpace = colorSpace;
        }
        ByteBuffer byteBufferAcquire = this.mDecodeBuffers.acquire();
        if (byteBufferAcquire == null) {
            byteBufferAcquire = ByteBuffer.allocate(16384);
        }
        try {
            try {
                options.inTempStorage = byteBufferAcquire.array();
                if (Build.VERSION.SDK_INT < 19 || rect == null || bitmap == null) {
                    bitmapDecodeStream = null;
                } else {
                    try {
                        bitmap.reconfigure(iWidth, iHeight, options.inPreferredConfig);
                        bitmapRegionDecoderNewInstance = BitmapRegionDecoder.newInstance(inputStream, true);
                        try {
                            try {
                                bitmapDecodeStream = bitmapRegionDecoderNewInstance.decodeRegion(rect, options);
                                if (bitmapRegionDecoderNewInstance != null) {
                                    bitmapRegionDecoderNewInstance.recycle();
                                }
                            } catch (IOException unused) {
                                FLog.e(TAG, "Could not decode region %s, decoding full bitmap instead.", rect);
                                if (bitmapRegionDecoderNewInstance != null) {
                                    bitmapRegionDecoderNewInstance.recycle();
                                }
                                bitmapDecodeStream = null;
                                if (bitmapDecodeStream == null) {
                                    bitmapDecodeStream = BitmapFactory.decodeStream(inputStream, null, options);
                                }
                                this.mDecodeBuffers.release(byteBufferAcquire);
                                if (bitmap != null) {
                                }
                                return CloseableReference.of(bitmapDecodeStream, this.mBitmapPool);
                            }
                        } catch (Throwable th) {
                            th = th;
                            if (bitmapRegionDecoderNewInstance != null) {
                                bitmapRegionDecoderNewInstance.recycle();
                            }
                            throw th;
                        }
                    } catch (IOException unused2) {
                        bitmapRegionDecoderNewInstance = null;
                    } catch (Throwable th2) {
                        th = th2;
                        bitmapRegionDecoderNewInstance = null;
                        if (bitmapRegionDecoderNewInstance != null) {
                            bitmapRegionDecoderNewInstance.recycle();
                        }
                        throw th;
                    }
                }
                if (bitmapDecodeStream == null) {
                    bitmapDecodeStream = BitmapFactory.decodeStream(inputStream, null, options);
                }
                this.mDecodeBuffers.release(byteBufferAcquire);
                if (bitmap != null || bitmap == bitmapDecodeStream) {
                    return CloseableReference.of(bitmapDecodeStream, this.mBitmapPool);
                }
                this.mBitmapPool.release(bitmap);
                bitmapDecodeStream.recycle();
                throw new IllegalStateException();
            } catch (Throwable th3) {
                this.mDecodeBuffers.release(byteBufferAcquire);
                throw th3;
            }
        } catch (IllegalArgumentException e) {
            if (bitmap != null) {
                this.mBitmapPool.release(bitmap);
            }
            try {
                inputStream.reset();
                Bitmap bitmapDecodeStream2 = BitmapFactory.decodeStream(inputStream);
                if (bitmapDecodeStream2 == null) {
                    throw e;
                }
                CloseableReference<Bitmap> closeableReferenceOf = CloseableReference.of(bitmapDecodeStream2, SimpleBitmapReleaser.getInstance());
                this.mDecodeBuffers.release(byteBufferAcquire);
                return closeableReferenceOf;
            } catch (IOException unused3) {
                throw e;
            }
        } catch (RuntimeException e2) {
            if (bitmap != null) {
                this.mBitmapPool.release(bitmap);
            }
            throw e2;
        }
    }

    private static BitmapFactory.Options getDecodeOptionsForStream(EncodedImage encodedImage, Bitmap.Config config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = encodedImage.getSampleSize();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(encodedImage.getInputStream(), null, options);
        if (options.outWidth == -1 || options.outHeight == -1) {
            throw new IllegalArgumentException();
        }
        options.inJustDecodeBounds = false;
        options.inDither = true;
        options.inPreferredConfig = config;
        options.inMutable = true;
        return options;
    }

    @Override // com.facebook.imagepipeline.platform.PlatformDecoder
    public CloseableReference<Bitmap> decodeFromEncodedImage(EncodedImage encodedImage, Bitmap.Config config, @Nullable Rect rect) {
        return decodeFromEncodedImageWithColorSpace(encodedImage, config, rect, null);
    }

    @Override // com.facebook.imagepipeline.platform.PlatformDecoder
    public CloseableReference<Bitmap> decodeFromEncodedImageWithColorSpace(EncodedImage encodedImage, Bitmap.Config config, @Nullable Rect rect, @Nullable ColorSpace colorSpace) {
        BitmapFactory.Options decodeOptionsForStream = getDecodeOptionsForStream(encodedImage, config);
        boolean z = decodeOptionsForStream.inPreferredConfig != Bitmap.Config.ARGB_8888;
        try {
            return decodeFromStream(encodedImage.getInputStream(), decodeOptionsForStream, rect, colorSpace);
        } catch (RuntimeException e) {
            if (z) {
                return decodeFromEncodedImageWithColorSpace(encodedImage, Bitmap.Config.ARGB_8888, rect, colorSpace);
            }
            throw e;
        }
    }

    @Override // com.facebook.imagepipeline.platform.PlatformDecoder
    public CloseableReference<Bitmap> decodeJPEGFromEncodedImage(EncodedImage encodedImage, Bitmap.Config config, @Nullable Rect rect, int i) {
        return decodeJPEGFromEncodedImageWithColorSpace(encodedImage, config, rect, i, null);
    }

    @Override // com.facebook.imagepipeline.platform.PlatformDecoder
    public CloseableReference<Bitmap> decodeJPEGFromEncodedImageWithColorSpace(EncodedImage encodedImage, Bitmap.Config config, @Nullable Rect rect, int i, @Nullable ColorSpace colorSpace) {
        boolean zIsCompleteAt = encodedImage.isCompleteAt(i);
        BitmapFactory.Options decodeOptionsForStream = getDecodeOptionsForStream(encodedImage, config);
        InputStream inputStream = encodedImage.getInputStream();
        Preconditions.checkNotNull(inputStream);
        if (encodedImage.getSize() > i) {
            inputStream = new LimitedInputStream(inputStream, i);
        }
        InputStream tailAppendingInputStream = !zIsCompleteAt ? new TailAppendingInputStream(inputStream, EOI_TAIL) : inputStream;
        boolean z = decodeOptionsForStream.inPreferredConfig != Bitmap.Config.ARGB_8888;
        try {
            try {
                CloseableReference<Bitmap> closeableReferenceDecodeFromStream = decodeFromStream(tailAppendingInputStream, decodeOptionsForStream, rect, colorSpace);
                try {
                    tailAppendingInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return closeableReferenceDecodeFromStream;
            } catch (RuntimeException e2) {
                if (!z) {
                    throw e2;
                }
                CloseableReference<Bitmap> closeableReferenceDecodeJPEGFromEncodedImageWithColorSpace = decodeJPEGFromEncodedImageWithColorSpace(encodedImage, Bitmap.Config.ARGB_8888, rect, i, colorSpace);
                try {
                    tailAppendingInputStream.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
                return closeableReferenceDecodeJPEGFromEncodedImageWithColorSpace;
            }
        } catch (Throwable th) {
            try {
                tailAppendingInputStream.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            throw th;
        }
    }

    protected CloseableReference<Bitmap> decodeStaticImageFromStream(InputStream inputStream, BitmapFactory.Options options, @Nullable Rect rect) {
        return decodeFromStream(inputStream, options, rect, null);
    }

    public abstract int getBitmapSize(int i, int i2, BitmapFactory.Options options);
}
