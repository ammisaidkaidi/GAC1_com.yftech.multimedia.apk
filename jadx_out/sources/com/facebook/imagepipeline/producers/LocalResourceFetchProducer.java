package com.facebook.imagepipeline.producers;

import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import com.facebook.common.memory.PooledByteBufferFactory;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest;
import java.io.IOException;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes3.dex */
public class LocalResourceFetchProducer extends LocalFetchProducer {
    public static final String PRODUCER_NAME = "LocalResourceFetchProducer";
    private final Resources mResources;

    public LocalResourceFetchProducer(Executor executor, PooledByteBufferFactory pooledByteBufferFactory, Resources resources) {
        super(executor, pooledByteBufferFactory);
        this.mResources = resources;
    }

    private int getLength(ImageRequest imageRequest) throws Throwable {
        AssetFileDescriptor assetFileDescriptor = null;
        try {
            AssetFileDescriptor assetFileDescriptorOpenRawResourceFd = this.mResources.openRawResourceFd(getResourceId(imageRequest));
            try {
                int length = (int) assetFileDescriptorOpenRawResourceFd.getLength();
                if (assetFileDescriptorOpenRawResourceFd != null) {
                    try {
                        assetFileDescriptorOpenRawResourceFd.close();
                    } catch (IOException unused) {
                    }
                }
                return length;
            } catch (Resources.NotFoundException unused2) {
                assetFileDescriptor = assetFileDescriptorOpenRawResourceFd;
                if (assetFileDescriptor != null) {
                    try {
                        assetFileDescriptor.close();
                    } catch (IOException unused3) {
                    }
                }
                return -1;
            } catch (Throwable th) {
                th = th;
                assetFileDescriptor = assetFileDescriptorOpenRawResourceFd;
                if (assetFileDescriptor != null) {
                    try {
                        assetFileDescriptor.close();
                    } catch (IOException unused4) {
                    }
                }
                throw th;
            }
        } catch (Resources.NotFoundException unused5) {
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static int getResourceId(ImageRequest imageRequest) {
        return Integer.parseInt(imageRequest.getSourceUri().getPath().substring(1));
    }

    @Override // com.facebook.imagepipeline.producers.LocalFetchProducer
    protected EncodedImage getEncodedImage(ImageRequest imageRequest) throws IOException {
        return getEncodedImage(this.mResources.openRawResource(getResourceId(imageRequest)), getLength(imageRequest));
    }

    @Override // com.facebook.imagepipeline.producers.LocalFetchProducer
    protected String getProducerName() {
        return PRODUCER_NAME;
    }
}
