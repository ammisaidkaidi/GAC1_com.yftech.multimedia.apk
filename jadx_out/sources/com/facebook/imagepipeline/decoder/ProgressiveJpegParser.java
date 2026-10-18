package com.facebook.imagepipeline.decoder;

import com.facebook.common.internal.Closeables;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Throwables;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.memory.PooledByteArrayBufferedInputStream;
import com.facebook.common.util.StreamUtil;
import com.facebook.imagepipeline.image.EncodedImage;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
public class ProgressiveJpegParser {
    private static final int BUFFER_SIZE = 16384;
    private static final int NOT_A_JPEG = 6;
    private static final int READ_FIRST_JPEG_BYTE = 0;
    private static final int READ_MARKER_FIRST_BYTE_OR_ENTROPY_DATA = 2;
    private static final int READ_MARKER_SECOND_BYTE = 3;
    private static final int READ_SECOND_JPEG_BYTE = 1;
    private static final int READ_SIZE_FIRST_BYTE = 4;
    private static final int READ_SIZE_SECOND_BYTE = 5;
    private final ByteArrayPool mByteArrayPool;
    private boolean mEndMarkerRead;
    private int mBytesParsed = 0;
    private int mLastByteRead = 0;
    private int mNextFullScanNumber = 0;
    private int mBestScanEndOffset = 0;
    private int mBestScanNumber = 0;
    private int mParserState = 0;

    public ProgressiveJpegParser(ByteArrayPool byteArrayPool) {
        this.mByteArrayPool = (ByteArrayPool) Preconditions.checkNotNull(byteArrayPool);
    }

    private boolean doParseMoreData(InputStream inputStream) throws Throwable {
        int i;
        int i2 = this.mBestScanNumber;
        while (this.mParserState != 6 && (i = inputStream.read()) != -1) {
            try {
                this.mBytesParsed++;
                if (this.mEndMarkerRead) {
                    this.mParserState = 6;
                    this.mEndMarkerRead = false;
                    return false;
                }
                switch (this.mParserState) {
                    case 0:
                        if (i != 255) {
                            this.mParserState = 6;
                        } else {
                            this.mParserState = 1;
                        }
                        break;
                    case 1:
                        if (i != 216) {
                            this.mParserState = 6;
                        } else {
                            this.mParserState = 2;
                        }
                        break;
                    case 2:
                        if (i == 255) {
                            this.mParserState = 3;
                        }
                        break;
                    case 3:
                        if (i == 255) {
                            this.mParserState = 3;
                        } else if (i == 0) {
                            this.mParserState = 2;
                        } else if (i != 217) {
                            if (i == 218) {
                                newScanOrImageEndFound(this.mBytesParsed - 2);
                            }
                            if (!doesMarkerStartSegment(i)) {
                                this.mParserState = 2;
                            } else {
                                this.mParserState = 4;
                            }
                        } else {
                            this.mEndMarkerRead = true;
                            newScanOrImageEndFound(this.mBytesParsed - 2);
                            this.mParserState = 2;
                        }
                        break;
                    case 4:
                        this.mParserState = 5;
                        break;
                    case 5:
                        int i3 = ((this.mLastByteRead << 8) + i) - 2;
                        StreamUtil.skip(inputStream, i3);
                        this.mBytesParsed += i3;
                        this.mParserState = 2;
                        break;
                    default:
                        Preconditions.checkState(false);
                        break;
                }
                this.mLastByteRead = i;
            } catch (IOException e) {
                Throwables.propagate(e);
            }
        }
        return (this.mParserState == 6 || this.mBestScanNumber == i2) ? false : true;
    }

    private static boolean doesMarkerStartSegment(int i) {
        if (i == 1) {
            return false;
        }
        if (i < 208 || i > 215) {
            return (i == 217 || i == 216) ? false : true;
        }
        return false;
    }

    private void newScanOrImageEndFound(int i) {
        if (this.mNextFullScanNumber > 0) {
            this.mBestScanEndOffset = i;
        }
        int i2 = this.mNextFullScanNumber;
        this.mNextFullScanNumber = i2 + 1;
        this.mBestScanNumber = i2;
    }

    public int getBestScanEndOffset() {
        return this.mBestScanEndOffset;
    }

    public int getBestScanNumber() {
        return this.mBestScanNumber;
    }

    public boolean isEndMarkerRead() {
        return this.mEndMarkerRead;
    }

    public boolean isJpeg() {
        return this.mBytesParsed > 1 && this.mParserState != 6;
    }

    public boolean parseMoreData(EncodedImage encodedImage) {
        if (this.mParserState == 6 || encodedImage.getSize() <= this.mBytesParsed) {
            return false;
        }
        PooledByteArrayBufferedInputStream pooledByteArrayBufferedInputStream = new PooledByteArrayBufferedInputStream(encodedImage.getInputStream(), this.mByteArrayPool.get(16384), this.mByteArrayPool);
        try {
            StreamUtil.skip(pooledByteArrayBufferedInputStream, this.mBytesParsed);
            return doParseMoreData(pooledByteArrayBufferedInputStream);
        } catch (IOException e) {
            Throwables.propagate(e);
            return false;
        } finally {
            Closeables.closeQuietly(pooledByteArrayBufferedInputStream);
        }
    }
}
