package com.facebook.imagepipeline.image;

import android.graphics.ColorSpace;
import android.util.Pair;
import androidx.annotation.VisibleForTesting;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.SharedReference;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imageformat.ImageFormatChecker;
import com.facebook.imagepipeline.common.BytesRange;
import com.facebook.imageutils.BitmapUtil;
import com.facebook.imageutils.HeifExifUtil;
import com.facebook.imageutils.ImageMetaData;
import com.facebook.imageutils.JfifUtil;
import com.facebook.imageutils.WebpUtil;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/* JADX INFO: loaded from: classes3.dex */
@Immutable
public class EncodedImage implements Closeable {
    public static final int DEFAULT_SAMPLE_SIZE = 1;
    public static final int UNKNOWN_HEIGHT = -1;
    public static final int UNKNOWN_ROTATION_ANGLE = -1;
    public static final int UNKNOWN_STREAM_SIZE = -1;
    public static final int UNKNOWN_WIDTH = -1;
    private static boolean sUseCachedMetadata;

    @Nullable
    private BytesRange mBytesRange;

    @Nullable
    private ColorSpace mColorSpace;
    private int mExifOrientation;
    private boolean mHasParsedMetadata;
    private int mHeight;
    private ImageFormat mImageFormat;

    @Nullable
    private final Supplier<FileInputStream> mInputStreamSupplier;

    @Nullable
    private final CloseableReference<PooledByteBuffer> mPooledByteBufferRef;
    private int mRotationAngle;
    private int mSampleSize;
    private int mStreamSize;
    private int mWidth;

    public EncodedImage(Supplier<FileInputStream> supplier) {
        this.mImageFormat = ImageFormat.UNKNOWN;
        this.mRotationAngle = -1;
        this.mExifOrientation = 0;
        this.mWidth = -1;
        this.mHeight = -1;
        this.mSampleSize = 1;
        this.mStreamSize = -1;
        Preconditions.checkNotNull(supplier);
        this.mPooledByteBufferRef = null;
        this.mInputStreamSupplier = supplier;
    }

    public EncodedImage(Supplier<FileInputStream> supplier, int i) {
        this(supplier);
        this.mStreamSize = i;
    }

    public EncodedImage(CloseableReference<PooledByteBuffer> closeableReference) {
        this.mImageFormat = ImageFormat.UNKNOWN;
        this.mRotationAngle = -1;
        this.mExifOrientation = 0;
        this.mWidth = -1;
        this.mHeight = -1;
        this.mSampleSize = 1;
        this.mStreamSize = -1;
        Preconditions.checkArgument(Boolean.valueOf(CloseableReference.isValid(closeableReference)));
        this.mPooledByteBufferRef = closeableReference.mo57clone();
        this.mInputStreamSupplier = null;
    }

    @Nullable
    public static EncodedImage cloneOrNull(EncodedImage encodedImage) {
        if (encodedImage != null) {
            return encodedImage.cloneOrNull();
        }
        return null;
    }

    public static void closeSafely(@Nullable EncodedImage encodedImage) {
        if (encodedImage != null) {
            encodedImage.close();
        }
    }

    private void internalParseMetaData() {
        ImageFormat imageFormat_WrapIOException = ImageFormatChecker.getImageFormat_WrapIOException(getInputStream());
        this.mImageFormat = imageFormat_WrapIOException;
        Pair<Integer, Integer> webPImageSize = DefaultImageFormats.isWebpFormat(imageFormat_WrapIOException) ? readWebPImageSize() : readImageMetaData().getDimensions();
        if (imageFormat_WrapIOException == DefaultImageFormats.JPEG && this.mRotationAngle == -1) {
            if (webPImageSize != null) {
                this.mExifOrientation = JfifUtil.getOrientation(getInputStream());
                this.mRotationAngle = JfifUtil.getAutoRotateAngleFromOrientation(this.mExifOrientation);
                return;
            }
            return;
        }
        if (imageFormat_WrapIOException == DefaultImageFormats.HEIF && this.mRotationAngle == -1) {
            this.mExifOrientation = HeifExifUtil.getOrientation(getInputStream());
            this.mRotationAngle = JfifUtil.getAutoRotateAngleFromOrientation(this.mExifOrientation);
        } else if (this.mRotationAngle == -1) {
            this.mRotationAngle = 0;
        }
    }

    public static boolean isMetaDataAvailable(EncodedImage encodedImage) {
        return encodedImage.mRotationAngle >= 0 && encodedImage.mWidth >= 0 && encodedImage.mHeight >= 0;
    }

    public static boolean isValid(@Nullable EncodedImage encodedImage) {
        return encodedImage != null && encodedImage.isValid();
    }

    private void parseMetadataIfNeeded() {
        if (this.mWidth < 0 || this.mHeight < 0) {
            parseMetaData();
        }
    }

    private ImageMetaData readImageMetaData() throws Throwable {
        InputStream inputStream;
        try {
            inputStream = getInputStream();
            try {
                ImageMetaData imageMetaDataDecodeDimensionsAndColorSpace = BitmapUtil.decodeDimensionsAndColorSpace(inputStream);
                this.mColorSpace = imageMetaDataDecodeDimensionsAndColorSpace.getColorSpace();
                Pair<Integer, Integer> dimensions = imageMetaDataDecodeDimensionsAndColorSpace.getDimensions();
                if (dimensions != null) {
                    this.mWidth = dimensions.first.intValue();
                    this.mHeight = dimensions.second.intValue();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException unused) {
                    }
                }
                return imageMetaDataDecodeDimensionsAndColorSpace;
            } catch (Throwable th) {
                th = th;
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException unused2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            inputStream = null;
        }
    }

    @Nullable
    private Pair<Integer, Integer> readWebPImageSize() {
        Pair<Integer, Integer> size = WebpUtil.getSize(getInputStream());
        if (size != null) {
            this.mWidth = size.first.intValue();
            this.mHeight = size.second.intValue();
        }
        return size;
    }

    public static void setUseCachedMetadata(boolean z) {
        sUseCachedMetadata = z;
    }

    @Nullable
    public EncodedImage cloneOrNull() {
        EncodedImage encodedImage;
        EncodedImage encodedImage2;
        if (this.mInputStreamSupplier != null) {
            encodedImage2 = new EncodedImage(this.mInputStreamSupplier, this.mStreamSize);
        } else {
            CloseableReference closeableReferenceCloneOrNull = CloseableReference.cloneOrNull(this.mPooledByteBufferRef);
            if (closeableReferenceCloneOrNull == null) {
                encodedImage = null;
            } else {
                try {
                    encodedImage = new EncodedImage((CloseableReference<PooledByteBuffer>) closeableReferenceCloneOrNull);
                } catch (Throwable th) {
                    CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceCloneOrNull);
                    throw th;
                }
            }
            CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceCloneOrNull);
            encodedImage2 = encodedImage;
        }
        if (encodedImage2 != null) {
            encodedImage2.copyMetaDataFrom(this);
        }
        return encodedImage2;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        CloseableReference.closeSafely(this.mPooledByteBufferRef);
    }

    public void copyMetaDataFrom(EncodedImage encodedImage) {
        this.mImageFormat = encodedImage.getImageFormat();
        this.mWidth = encodedImage.getWidth();
        this.mHeight = encodedImage.getHeight();
        this.mRotationAngle = encodedImage.getRotationAngle();
        this.mExifOrientation = encodedImage.getExifOrientation();
        this.mSampleSize = encodedImage.getSampleSize();
        this.mStreamSize = encodedImage.getSize();
        this.mBytesRange = encodedImage.getBytesRange();
        this.mColorSpace = encodedImage.getColorSpace();
        this.mHasParsedMetadata = encodedImage.hasParsedMetaData();
    }

    public CloseableReference<PooledByteBuffer> getByteBufferRef() {
        return CloseableReference.cloneOrNull(this.mPooledByteBufferRef);
    }

    @Nullable
    public BytesRange getBytesRange() {
        return this.mBytesRange;
    }

    @Nullable
    public ColorSpace getColorSpace() {
        parseMetadataIfNeeded();
        return this.mColorSpace;
    }

    public int getExifOrientation() {
        parseMetadataIfNeeded();
        return this.mExifOrientation;
    }

    public String getFirstBytesAsHexString(int i) {
        CloseableReference<PooledByteBuffer> byteBufferRef = getByteBufferRef();
        if (byteBufferRef == null) {
            return "";
        }
        int iMin = Math.min(getSize(), i);
        byte[] bArr = new byte[iMin];
        try {
            PooledByteBuffer pooledByteBuffer = byteBufferRef.get();
            if (pooledByteBuffer == null) {
                byteBufferRef.close();
                return "";
            }
            pooledByteBuffer.read(0, bArr, 0, iMin);
            byteBufferRef.close();
            StringBuilder sb = new StringBuilder(bArr.length * 2);
            for (byte b : bArr) {
                sb.append(String.format("%02X", Byte.valueOf(b)));
            }
            return sb.toString();
        } catch (Throwable th) {
            byteBufferRef.close();
            throw th;
        }
    }

    public int getHeight() {
        parseMetadataIfNeeded();
        return this.mHeight;
    }

    public ImageFormat getImageFormat() {
        parseMetadataIfNeeded();
        return this.mImageFormat;
    }

    @Nullable
    public InputStream getInputStream() {
        if (this.mInputStreamSupplier != null) {
            return this.mInputStreamSupplier.get();
        }
        CloseableReference closeableReferenceCloneOrNull = CloseableReference.cloneOrNull(this.mPooledByteBufferRef);
        if (closeableReferenceCloneOrNull == null) {
            return null;
        }
        try {
            return new PooledByteBufferInputStream((PooledByteBuffer) closeableReferenceCloneOrNull.get());
        } finally {
            CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceCloneOrNull);
        }
    }

    public int getRotationAngle() {
        parseMetadataIfNeeded();
        return this.mRotationAngle;
    }

    public int getSampleSize() {
        return this.mSampleSize;
    }

    public int getSize() {
        return (this.mPooledByteBufferRef == null || this.mPooledByteBufferRef.get() == null) ? this.mStreamSize : this.mPooledByteBufferRef.get().size();
    }

    @Nullable
    @VisibleForTesting
    public synchronized SharedReference<PooledByteBuffer> getUnderlyingReferenceTestOnly() {
        return this.mPooledByteBufferRef != null ? this.mPooledByteBufferRef.getUnderlyingReferenceTestOnly() : null;
    }

    public int getWidth() {
        parseMetadataIfNeeded();
        return this.mWidth;
    }

    protected boolean hasParsedMetaData() {
        return this.mHasParsedMetadata;
    }

    public boolean isCompleteAt(int i) {
        if ((this.mImageFormat != DefaultImageFormats.JPEG && this.mImageFormat != DefaultImageFormats.DNG) || this.mInputStreamSupplier != null) {
            return true;
        }
        Preconditions.checkNotNull(this.mPooledByteBufferRef);
        PooledByteBuffer pooledByteBuffer = this.mPooledByteBufferRef.get();
        return pooledByteBuffer.read(i + (-2)) == -1 && pooledByteBuffer.read(i - 1) == -39;
    }

    public synchronized boolean isValid() {
        return CloseableReference.isValid(this.mPooledByteBufferRef) || this.mInputStreamSupplier != null;
    }

    public void parseMetaData() {
        if (!sUseCachedMetadata) {
            internalParseMetaData();
        } else {
            if (this.mHasParsedMetadata) {
                return;
            }
            internalParseMetaData();
            this.mHasParsedMetadata = true;
        }
    }

    public void setBytesRange(@Nullable BytesRange bytesRange) {
        this.mBytesRange = bytesRange;
    }

    public void setExifOrientation(int i) {
        this.mExifOrientation = i;
    }

    public void setHeight(int i) {
        this.mHeight = i;
    }

    public void setImageFormat(ImageFormat imageFormat) {
        this.mImageFormat = imageFormat;
    }

    public void setRotationAngle(int i) {
        this.mRotationAngle = i;
    }

    public void setSampleSize(int i) {
        this.mSampleSize = i;
    }

    public void setStreamSize(int i) {
        this.mStreamSize = i;
    }

    public void setWidth(int i) {
        this.mWidth = i;
    }
}
