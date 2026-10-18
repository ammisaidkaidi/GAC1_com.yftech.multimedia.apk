package com.facebook.imagepipeline.platform;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.MemoryFile;
import com.facebook.common.internal.ByteStreams;
import com.facebook.common.internal.Closeables;
import com.facebook.common.internal.DoNotStrip;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Throwables;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.memory.PooledByteBufferInputStream;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.streams.LimitedInputStream;
import com.facebook.common.webp.WebpBitmapFactory;
import com.facebook.common.webp.WebpSupportStatus;
import com.facebook.imagepipeline.nativecode.DalvikPurgeableDecoder;
import com.facebook.infer.annotation.Nullsafe;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@DoNotStrip
@Nullsafe(Nullsafe.Mode.STRICT)
public class GingerbreadPurgeableDecoder extends DalvikPurgeableDecoder {
    private static Method sGetFileDescriptorMethod;

    @Nullable
    private final WebpBitmapFactory mWebpBitmapFactory = WebpSupportStatus.loadWebpBitmapFactoryIfExists();

    @DoNotStrip
    public GingerbreadPurgeableDecoder() {
    }

    private static MemoryFile copyToMemoryFile(CloseableReference<PooledByteBuffer> closeableReference, int i, @Nullable byte[] bArr) throws Throwable {
        PooledByteBufferInputStream pooledByteBufferInputStream;
        OutputStream outputStream;
        LimitedInputStream limitedInputStream = null;
        MemoryFile memoryFile = new MemoryFile(null, (bArr == null ? 0 : bArr.length) + i);
        memoryFile.allowPurging(false);
        try {
            pooledByteBufferInputStream = new PooledByteBufferInputStream(closeableReference.get());
            try {
                LimitedInputStream limitedInputStream2 = new LimitedInputStream(pooledByteBufferInputStream, i);
                try {
                    outputStream = memoryFile.getOutputStream();
                    try {
                        ByteStreams.copy(limitedInputStream2, outputStream);
                        if (bArr != null) {
                            memoryFile.writeBytes(bArr, 0, i, bArr.length);
                        }
                        CloseableReference.closeSafely(closeableReference);
                        Closeables.closeQuietly(pooledByteBufferInputStream);
                        Closeables.closeQuietly(limitedInputStream2);
                        Closeables.close(outputStream, true);
                        return memoryFile;
                    } catch (Throwable th) {
                        th = th;
                        limitedInputStream = limitedInputStream2;
                        CloseableReference.closeSafely(closeableReference);
                        Closeables.closeQuietly(pooledByteBufferInputStream);
                        Closeables.closeQuietly(limitedInputStream);
                        Closeables.close(outputStream, true);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    outputStream = null;
                }
            } catch (Throwable th3) {
                th = th3;
                outputStream = null;
            }
        } catch (Throwable th4) {
            th = th4;
            pooledByteBufferInputStream = null;
            outputStream = null;
        }
    }

    private Bitmap decodeFileDescriptorAsPurgeable(CloseableReference<PooledByteBuffer> closeableReference, int i, @Nullable byte[] bArr, BitmapFactory.Options options) throws Throwable {
        MemoryFile memoryFileCopyToMemoryFile;
        MemoryFile memoryFile = null;
        try {
            try {
                memoryFileCopyToMemoryFile = copyToMemoryFile(closeableReference, i, bArr);
                try {
                    FileDescriptor memoryFileDescriptor = getMemoryFileDescriptor(memoryFileCopyToMemoryFile);
                    if (this.mWebpBitmapFactory == null) {
                        throw new IllegalStateException("WebpBitmapFactory is null");
                    }
                    Bitmap bitmap = (Bitmap) Preconditions.checkNotNull(this.mWebpBitmapFactory.decodeFileDescriptor(memoryFileDescriptor, null, options), "BitmapFactory returned null");
                    if (memoryFileCopyToMemoryFile != null) {
                        memoryFileCopyToMemoryFile.close();
                    }
                    return bitmap;
                } catch (IOException e) {
                    e = e;
                    memoryFile = memoryFileCopyToMemoryFile;
                    throw Throwables.propagate(e);
                } catch (Throwable th) {
                    th = th;
                    if (memoryFileCopyToMemoryFile != null) {
                        memoryFileCopyToMemoryFile.close();
                    }
                    throw th;
                }
            } catch (IOException e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
            memoryFileCopyToMemoryFile = memoryFile;
        }
    }

    private synchronized Method getFileDescriptorMethod() {
        if (sGetFileDescriptorMethod == null) {
            try {
                sGetFileDescriptorMethod = MemoryFile.class.getDeclaredMethod("getFileDescriptor", new Class[0]);
            } catch (Exception e) {
                throw Throwables.propagate(e);
            }
        }
        return sGetFileDescriptorMethod;
    }

    private FileDescriptor getMemoryFileDescriptor(MemoryFile memoryFile) {
        try {
            return (FileDescriptor) Preconditions.checkNotNull(getFileDescriptorMethod().invoke(memoryFile, new Object[0]));
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override // com.facebook.imagepipeline.nativecode.DalvikPurgeableDecoder
    protected Bitmap decodeByteArrayAsPurgeable(CloseableReference<PooledByteBuffer> closeableReference, BitmapFactory.Options options) {
        return decodeFileDescriptorAsPurgeable(closeableReference, closeableReference.get().size(), null, options);
    }

    @Override // com.facebook.imagepipeline.nativecode.DalvikPurgeableDecoder
    protected Bitmap decodeJPEGByteArrayAsPurgeable(CloseableReference<PooledByteBuffer> closeableReference, int i, BitmapFactory.Options options) {
        return decodeFileDescriptorAsPurgeable(closeableReference, i, endsWithEOI(closeableReference, i) ? null : EOI, options);
    }
}
