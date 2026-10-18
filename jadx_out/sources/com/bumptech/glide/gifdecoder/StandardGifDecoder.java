package com.bumptech.glide.gifdecoder;

import android.graphics.Bitmap;
import android.util.Log;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Iterator;

/* JADX INFO: loaded from: classes3.dex */
public class StandardGifDecoder implements GifDecoder {
    private static final int BYTES_PER_INTEGER = 4;

    @ColorInt
    private static final int COLOR_TRANSPARENT_BLACK = 0;
    private static final int INITIAL_FRAME_POINTER = -1;
    private static final int MASK_INT_LOWEST_BYTE = 255;
    private static final int MAX_STACK_SIZE = 4096;
    private static final int NULL_CODE = -1;
    private static final String TAG = "StandardGifDecoder";

    @ColorInt
    private int[] act;

    @NonNull
    private Bitmap.Config bitmapConfig;
    private final GifDecoder.BitmapProvider bitmapProvider;
    private byte[] block;
    private int downsampledHeight;
    private int downsampledWidth;
    private int framePointer;
    private GifHeader header;

    @Nullable
    private Boolean isFirstFrameTransparent;
    private byte[] mainPixels;

    @ColorInt
    private int[] mainScratch;
    private GifHeaderParser parser;

    @ColorInt
    private final int[] pct;
    private byte[] pixelStack;
    private short[] prefix;
    private Bitmap previousImage;
    private ByteBuffer rawData;
    private int sampleSize;
    private boolean savePrevious;
    private int status;
    private byte[] suffix;

    public StandardGifDecoder(@NonNull GifDecoder.BitmapProvider bitmapProvider) {
        this.pct = new int[256];
        this.bitmapConfig = Bitmap.Config.ARGB_8888;
        this.bitmapProvider = bitmapProvider;
        this.header = new GifHeader();
    }

    public StandardGifDecoder(@NonNull GifDecoder.BitmapProvider bitmapProvider, GifHeader gifHeader, ByteBuffer byteBuffer) {
        this(bitmapProvider, gifHeader, byteBuffer, 1);
    }

    public StandardGifDecoder(@NonNull GifDecoder.BitmapProvider bitmapProvider, GifHeader gifHeader, ByteBuffer byteBuffer, int i) {
        this(bitmapProvider);
        setData(gifHeader, byteBuffer, i);
    }

    @ColorInt
    private int averageColorsNear(int i, int i2, int i3) {
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        for (int i9 = i; i9 < this.sampleSize + i && i9 < this.mainPixels.length && i9 < i2; i9++) {
            int i10 = this.act[this.mainPixels[i9] & 255];
            if (i10 != 0) {
                i4 += (i10 >> 24) & 255;
                i5 += (i10 >> 16) & 255;
                i6 += (i10 >> 8) & 255;
                i7 += i10 & 255;
                i8++;
            }
        }
        int i11 = i + i3;
        for (int i12 = i11; i12 < this.sampleSize + i11 && i12 < this.mainPixels.length && i12 < i2; i12++) {
            int i13 = this.act[this.mainPixels[i12] & 255];
            if (i13 != 0) {
                i4 += (i13 >> 24) & 255;
                i5 += (i13 >> 16) & 255;
                i6 += (i13 >> 8) & 255;
                i7 += i13 & 255;
                i8++;
            }
        }
        if (i8 == 0) {
            return 0;
        }
        return ((i4 / i8) << 24) | ((i5 / i8) << 16) | ((i6 / i8) << 8) | (i7 / i8);
    }

    private void copyCopyIntoScratchRobust(GifFrame gifFrame) {
        int i;
        int i2;
        int i3;
        int[] iArr = this.mainScratch;
        int i4 = gifFrame.ih / this.sampleSize;
        int i5 = gifFrame.iy / this.sampleSize;
        int i6 = gifFrame.iw / this.sampleSize;
        int i7 = gifFrame.ix / this.sampleSize;
        boolean z = this.framePointer == 0;
        int i8 = this.sampleSize;
        int i9 = this.downsampledWidth;
        int i10 = this.downsampledHeight;
        byte[] bArr = this.mainPixels;
        int[] iArr2 = this.act;
        int i11 = 8;
        int i12 = 0;
        int i13 = 1;
        Boolean bool = this.isFirstFrameTransparent;
        int i14 = 0;
        while (i14 < i4) {
            if (gifFrame.interlace) {
                if (i12 >= i4) {
                    i13++;
                    switch (i13) {
                        case 2:
                            i12 = 4;
                            break;
                        case 3:
                            i12 = 2;
                            i11 = 4;
                            break;
                        case 4:
                            i11 = 2;
                            i12 = 1;
                            break;
                    }
                }
                i = i12 + i11;
            } else {
                i = i12;
                i12 = i14;
            }
            int i15 = i12 + i5;
            int i16 = i4;
            boolean z2 = i8 == 1;
            if (i15 < i10) {
                int i17 = i15 * i9;
                int i18 = i17 + i7;
                i2 = i5;
                int i19 = i18 + i6;
                int i20 = i17 + i9;
                if (i20 < i19) {
                    i19 = i20;
                }
                i3 = i6;
                int i21 = i14 * i8 * gifFrame.iw;
                if (z2) {
                    for (int i22 = i18; i22 < i19; i22++) {
                        int i23 = iArr2[bArr[i21] & 255];
                        if (i23 != 0) {
                            iArr[i22] = i23;
                        } else if (z && bool == null) {
                            bool = true;
                        }
                        i21 += i8;
                    }
                } else {
                    int i24 = ((i19 - i18) * i8) + i21;
                    int i25 = i18;
                    while (i25 < i19) {
                        int i26 = i19;
                        int iAverageColorsNear = averageColorsNear(i21, i24, gifFrame.iw);
                        if (iAverageColorsNear != 0) {
                            iArr[i25] = iAverageColorsNear;
                        } else {
                            if (z && bool == null) {
                                bool = true;
                            }
                            i21 += i8;
                            i25++;
                            i19 = i26;
                        }
                        i21 += i8;
                        i25++;
                        i19 = i26;
                    }
                }
            } else {
                i2 = i5;
                i3 = i6;
            }
            i14++;
            i12 = i;
            i4 = i16;
            i5 = i2;
            i6 = i3;
        }
        if (this.isFirstFrameTransparent == null) {
            this.isFirstFrameTransparent = Boolean.valueOf(bool == null ? false : bool.booleanValue());
        }
    }

    private void copyIntoScratchFast(GifFrame gifFrame) {
        GifFrame gifFrame2 = gifFrame;
        int[] iArr = this.mainScratch;
        int i = gifFrame2.ih;
        int i2 = gifFrame2.iy;
        int i3 = gifFrame2.iw;
        int i4 = gifFrame2.ix;
        boolean z = this.framePointer == 0;
        int i5 = this.downsampledWidth;
        byte[] bArr = this.mainPixels;
        int[] iArr2 = this.act;
        int i6 = 0;
        byte b = -1;
        while (i6 < i) {
            int i7 = (i6 + i2) * i5;
            int i8 = i7 + i4;
            int i9 = i8 + i3;
            int i10 = i7 + i5;
            if (i10 < i9) {
                i9 = i10;
            }
            byte b2 = b;
            int i11 = gifFrame2.iw * i6;
            int i12 = i8;
            while (i12 < i9) {
                byte b3 = bArr[i11];
                int i13 = i;
                int i14 = b3 & 255;
                if (i14 != b2) {
                    int i15 = iArr2[i14];
                    if (i15 != 0) {
                        iArr[i12] = i15;
                    } else {
                        b2 = b3;
                    }
                }
                i11++;
                i12++;
                i = i13;
            }
            i6++;
            b = b2;
            gifFrame2 = gifFrame;
        }
        this.isFirstFrameTransparent = Boolean.valueOf((this.isFirstFrameTransparent != null && this.isFirstFrameTransparent.booleanValue()) || (this.isFirstFrameTransparent == null && z && b != -1));
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r4v11 */
    /* JADX WARN: Type inference failed for: r4v12 */
    /* JADX WARN: Type inference failed for: r4v13 */
    /* JADX WARN: Type inference failed for: r4v18, types: [short] */
    /* JADX WARN: Type inference failed for: r4v21 */
    private void decodeBitmapData(GifFrame gifFrame) {
        short s;
        StandardGifDecoder standardGifDecoder = this;
        if (gifFrame != null) {
            standardGifDecoder.rawData.position(gifFrame.bufferFrameStart);
        }
        int i = gifFrame == null ? standardGifDecoder.header.width * standardGifDecoder.header.height : gifFrame.ih * gifFrame.iw;
        if (standardGifDecoder.mainPixels == null || standardGifDecoder.mainPixels.length < i) {
            standardGifDecoder.mainPixels = standardGifDecoder.bitmapProvider.obtainByteArray(i);
        }
        byte[] bArr = standardGifDecoder.mainPixels;
        if (standardGifDecoder.prefix == null) {
            standardGifDecoder.prefix = new short[4096];
        }
        short[] sArr = standardGifDecoder.prefix;
        if (standardGifDecoder.suffix == null) {
            standardGifDecoder.suffix = new byte[4096];
        }
        byte[] bArr2 = standardGifDecoder.suffix;
        if (standardGifDecoder.pixelStack == null) {
            standardGifDecoder.pixelStack = new byte[4097];
        }
        byte[] bArr3 = standardGifDecoder.pixelStack;
        int i2 = readByte();
        int i3 = 1 << i2;
        int i4 = i3 + 1;
        int i5 = i3 + 2;
        int i6 = i2 + 1;
        int i7 = (1 << i6) - 1;
        int i8 = 0;
        for (int i9 = 0; i9 < i3; i9++) {
            sArr[i9] = 0;
            bArr2[i9] = (byte) i9;
        }
        byte[] bArr4 = standardGifDecoder.block;
        int i10 = i6;
        int i11 = i5;
        int i12 = i7;
        int block = 0;
        int i13 = 0;
        int i14 = 0;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        int i18 = 0;
        int i19 = -1;
        while (i8 < i) {
            if (block == 0) {
                block = readBlock();
                if (block <= 0) {
                    standardGifDecoder.status = 3;
                    break;
                }
                i16 = 0;
            }
            i15 += (bArr4[i16] & 255) << i14;
            i16++;
            block--;
            int i20 = i14 + 8;
            i19 = i19;
            i17 = i17;
            int i21 = i11;
            i13 = i13;
            i8 = i8;
            i10 = i10;
            while (true) {
                if (i20 < i10) {
                    i11 = i21;
                    i14 = i20;
                    standardGifDecoder = this;
                    break;
                }
                int i22 = i15 & i12;
                i15 >>= i10;
                i20 -= i10;
                if (i22 == i3) {
                    i10 = i6;
                    i21 = i5;
                    i12 = i7;
                    i19 = -1;
                } else {
                    if (i22 == i4) {
                        i11 = i21;
                        i14 = i20;
                        break;
                    }
                    if (i19 == -1) {
                        bArr[i13] = bArr2[i22];
                        i13++;
                        i8++;
                        i19 = i22;
                        i17 = i19;
                        standardGifDecoder = this;
                    } else {
                        int i23 = i21;
                        if (i22 >= i23) {
                            bArr3[i18] = (byte) i17;
                            i18++;
                            s = i19;
                        } else {
                            s = i22;
                        }
                        while (s >= i3) {
                            bArr3[i18] = bArr2[s];
                            i18++;
                            s = sArr[s];
                        }
                        int i24 = bArr2[s] & 255;
                        int i25 = i6;
                        byte b = (byte) i24;
                        bArr[i13] = b;
                        i13++;
                        i8++;
                        while (i18 > 0) {
                            i18--;
                            bArr[i13] = bArr3[i18];
                            i13++;
                            i8++;
                        }
                        if (i23 < 4096) {
                            sArr[i23] = (short) i19;
                            bArr2[i23] = b;
                            i23++;
                            if ((i23 & i12) == 0 && i23 < 4096) {
                                i10++;
                                i12 += i23;
                            }
                        }
                        i21 = i23;
                        i19 = i22;
                        i20 = i20;
                        i6 = i25;
                        i17 = i24;
                        standardGifDecoder = this;
                    }
                }
            }
        }
        Arrays.fill(bArr, i13, i, (byte) 0);
    }

    @NonNull
    private GifHeaderParser getHeaderParser() {
        if (this.parser == null) {
            this.parser = new GifHeaderParser();
        }
        return this.parser;
    }

    private Bitmap getNextBitmap() {
        Bitmap bitmapObtain = this.bitmapProvider.obtain(this.downsampledWidth, this.downsampledHeight, (this.isFirstFrameTransparent == null || this.isFirstFrameTransparent.booleanValue()) ? Bitmap.Config.ARGB_8888 : this.bitmapConfig);
        bitmapObtain.setHasAlpha(true);
        return bitmapObtain;
    }

    private int readBlock() {
        int i = readByte();
        if (i <= 0) {
            return i;
        }
        this.rawData.get(this.block, 0, Math.min(i, this.rawData.remaining()));
        return i;
    }

    private int readByte() {
        return this.rawData.get() & 255;
    }

    private Bitmap setPixels(GifFrame gifFrame, GifFrame gifFrame2) {
        int[] iArr = this.mainScratch;
        int i = 0;
        if (gifFrame2 == null) {
            if (this.previousImage != null) {
                this.bitmapProvider.release(this.previousImage);
            }
            this.previousImage = null;
            Arrays.fill(iArr, 0);
        }
        if (gifFrame2 != null && gifFrame2.dispose == 3 && this.previousImage == null) {
            Arrays.fill(iArr, 0);
        }
        if (gifFrame2 != null && gifFrame2.dispose > 0) {
            if (gifFrame2.dispose == 2) {
                if (!gifFrame.transparency) {
                    int i2 = this.header.bgColor;
                    if (gifFrame.lct == null || this.header.bgIndex != gifFrame.transIndex) {
                        i = i2;
                    }
                }
                int i3 = gifFrame2.ih / this.sampleSize;
                int i4 = gifFrame2.iy / this.sampleSize;
                int i5 = gifFrame2.iw / this.sampleSize;
                int i6 = (i4 * this.downsampledWidth) + (gifFrame2.ix / this.sampleSize);
                int i7 = (i3 * this.downsampledWidth) + i6;
                while (i6 < i7) {
                    int i8 = i6 + i5;
                    for (int i9 = i6; i9 < i8; i9++) {
                        iArr[i9] = i;
                    }
                    i6 += this.downsampledWidth;
                }
            } else if (gifFrame2.dispose == 3 && this.previousImage != null) {
                this.previousImage.getPixels(iArr, 0, this.downsampledWidth, 0, 0, this.downsampledWidth, this.downsampledHeight);
            }
        }
        decodeBitmapData(gifFrame);
        if (gifFrame.interlace || this.sampleSize != 1) {
            copyCopyIntoScratchRobust(gifFrame);
        } else {
            copyIntoScratchFast(gifFrame);
        }
        if (this.savePrevious && (gifFrame.dispose == 0 || gifFrame.dispose == 1)) {
            if (this.previousImage == null) {
                this.previousImage = getNextBitmap();
            }
            this.previousImage.setPixels(iArr, 0, this.downsampledWidth, 0, 0, this.downsampledWidth, this.downsampledHeight);
        }
        Bitmap nextBitmap = getNextBitmap();
        nextBitmap.setPixels(iArr, 0, this.downsampledWidth, 0, 0, this.downsampledWidth, this.downsampledHeight);
        return nextBitmap;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public void advance() {
        this.framePointer = (this.framePointer + 1) % this.header.frameCount;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public void clear() {
        this.header = null;
        if (this.mainPixels != null) {
            this.bitmapProvider.release(this.mainPixels);
        }
        if (this.mainScratch != null) {
            this.bitmapProvider.release(this.mainScratch);
        }
        if (this.previousImage != null) {
            this.bitmapProvider.release(this.previousImage);
        }
        this.previousImage = null;
        this.rawData = null;
        this.isFirstFrameTransparent = null;
        if (this.block != null) {
            this.bitmapProvider.release(this.block);
        }
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getByteSize() {
        return this.rawData.limit() + this.mainPixels.length + (this.mainScratch.length * 4);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getCurrentFrameIndex() {
        return this.framePointer;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    @NonNull
    public ByteBuffer getData() {
        return this.rawData;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getDelay(int i) {
        if (i < 0 || i >= this.header.frameCount) {
            return -1;
        }
        return this.header.frames.get(i).delay;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getFrameCount() {
        return this.header.frameCount;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getHeight() {
        return this.header.height;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    @Deprecated
    public int getLoopCount() {
        if (this.header.loopCount == -1) {
            return 1;
        }
        return this.header.loopCount;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getNetscapeLoopCount() {
        return this.header.loopCount;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getNextDelay() {
        if (this.header.frameCount <= 0 || this.framePointer < 0) {
            return 0;
        }
        return getDelay(this.framePointer);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    @Nullable
    public synchronized Bitmap getNextFrame() {
        if (this.header.frameCount <= 0 || this.framePointer < 0) {
            if (Log.isLoggable(TAG, 3)) {
                Log.d(TAG, "Unable to decode frame, frameCount=" + this.header.frameCount + ", framePointer=" + this.framePointer);
            }
            this.status = 1;
        }
        if (this.status != 1 && this.status != 2) {
            this.status = 0;
            if (this.block == null) {
                this.block = this.bitmapProvider.obtainByteArray(255);
            }
            GifFrame gifFrame = this.header.frames.get(this.framePointer);
            int i = this.framePointer - 1;
            GifFrame gifFrame2 = i >= 0 ? this.header.frames.get(i) : null;
            this.act = gifFrame.lct != null ? gifFrame.lct : this.header.gct;
            if (this.act == null) {
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "No valid color table found for frame #" + this.framePointer);
                }
                this.status = 1;
                return null;
            }
            if (gifFrame.transparency) {
                System.arraycopy(this.act, 0, this.pct, 0, this.act.length);
                this.act = this.pct;
                this.act[gifFrame.transIndex] = 0;
                if (gifFrame.dispose == 2 && this.framePointer == 0) {
                    this.isFirstFrameTransparent = true;
                }
            }
            return setPixels(gifFrame, gifFrame2);
        }
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "Unable to decode frame, status=" + this.status);
        }
        return null;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getStatus() {
        return this.status;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getTotalIterationCount() {
        if (this.header.loopCount == -1) {
            return 1;
        }
        if (this.header.loopCount == 0) {
            return 0;
        }
        return this.header.loopCount + 1;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int getWidth() {
        return this.header.width;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public int read(@Nullable InputStream inputStream, int i) {
        if (inputStream != null) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(i > 0 ? i + 4096 : 16384);
                byte[] bArr = new byte[16384];
                while (true) {
                    int i2 = inputStream.read(bArr, 0, bArr.length);
                    if (i2 == -1) {
                        break;
                    }
                    byteArrayOutputStream.write(bArr, 0, i2);
                }
                byteArrayOutputStream.flush();
                read(byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                Log.w(TAG, "Error reading data from stream", e);
            }
        } else {
            this.status = 2;
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e2) {
                Log.w(TAG, "Error closing stream", e2);
            }
        }
        return this.status;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public synchronized int read(@Nullable byte[] bArr) {
        this.header = getHeaderParser().setData(bArr).parseHeader();
        if (bArr != null) {
            setData(this.header, bArr);
        }
        return this.status;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public void resetFrameIndex() {
        this.framePointer = -1;
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public synchronized void setData(@NonNull GifHeader gifHeader, @NonNull ByteBuffer byteBuffer) {
        setData(gifHeader, byteBuffer, 1);
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public synchronized void setData(@NonNull GifHeader gifHeader, @NonNull ByteBuffer byteBuffer, int i) {
        try {
            if (i <= 0) {
                throw new IllegalArgumentException("Sample size must be >=0, not: " + i);
            }
            int iHighestOneBit = Integer.highestOneBit(i);
            this.status = 0;
            this.header = gifHeader;
            this.framePointer = -1;
            this.rawData = byteBuffer.asReadOnlyBuffer();
            this.rawData.position(0);
            this.rawData.order(ByteOrder.LITTLE_ENDIAN);
            this.savePrevious = false;
            Iterator<GifFrame> it = gifHeader.frames.iterator();
            while (it.hasNext()) {
                if (it.next().dispose == 3) {
                    this.savePrevious = true;
                    break;
                }
            }
            this.sampleSize = iHighestOneBit;
            this.downsampledWidth = gifHeader.width / iHighestOneBit;
            this.downsampledHeight = gifHeader.height / iHighestOneBit;
            this.mainPixels = this.bitmapProvider.obtainByteArray(gifHeader.width * gifHeader.height);
            this.mainScratch = this.bitmapProvider.obtainIntArray(this.downsampledWidth * this.downsampledHeight);
        } catch (Throwable th) {
            throw th;
        }
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public synchronized void setData(@NonNull GifHeader gifHeader, @NonNull byte[] bArr) {
        setData(gifHeader, ByteBuffer.wrap(bArr));
    }

    @Override // com.bumptech.glide.gifdecoder.GifDecoder
    public void setDefaultBitmapConfig(@NonNull Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888 || config == Bitmap.Config.RGB_565) {
            this.bitmapConfig = config;
            return;
        }
        throw new IllegalArgumentException("Unsupported format: " + config + ", must be one of " + Bitmap.Config.ARGB_8888 + " or " + Bitmap.Config.RGB_565);
    }
}
