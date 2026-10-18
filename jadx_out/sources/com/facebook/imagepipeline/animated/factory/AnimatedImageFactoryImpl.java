package com.facebook.imagepipeline.animated.factory;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.animated.base.AnimatedDrawableBackend;
import com.facebook.imagepipeline.animated.base.AnimatedImage;
import com.facebook.imagepipeline.animated.base.AnimatedImageResult;
import com.facebook.imagepipeline.animated.impl.AnimatedDrawableBackendProvider;
import com.facebook.imagepipeline.animated.impl.AnimatedImageCompositor;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.image.CloseableAnimatedImage;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class AnimatedImageFactoryImpl implements AnimatedImageFactory {
    static AnimatedImageDecoder sGifAnimatedImageDecoder = loadIfPresent("com.facebook.animated.gif.GifImage");
    static AnimatedImageDecoder sWebpAnimatedImageDecoder = loadIfPresent("com.facebook.animated.webp.WebPImage");
    private final AnimatedDrawableBackendProvider mAnimatedDrawableBackendProvider;
    private final PlatformBitmapFactory mBitmapFactory;

    public AnimatedImageFactoryImpl(AnimatedDrawableBackendProvider animatedDrawableBackendProvider, PlatformBitmapFactory platformBitmapFactory) {
        this.mAnimatedDrawableBackendProvider = animatedDrawableBackendProvider;
        this.mBitmapFactory = platformBitmapFactory;
    }

    @SuppressLint({"NewApi"})
    private CloseableReference<Bitmap> createBitmap(int i, int i2, Bitmap.Config config) {
        CloseableReference<Bitmap> closeableReferenceCreateBitmapInternal = this.mBitmapFactory.createBitmapInternal(i, i2, config);
        closeableReferenceCreateBitmapInternal.get().eraseColor(0);
        if (Build.VERSION.SDK_INT >= 12) {
            closeableReferenceCreateBitmapInternal.get().setHasAlpha(true);
        }
        return closeableReferenceCreateBitmapInternal;
    }

    private CloseableReference<Bitmap> createPreviewBitmap(AnimatedImage animatedImage, Bitmap.Config config, int i) {
        CloseableReference<Bitmap> closeableReferenceCreateBitmap = createBitmap(animatedImage.getWidth(), animatedImage.getHeight(), config);
        new AnimatedImageCompositor(this.mAnimatedDrawableBackendProvider.get(AnimatedImageResult.forAnimatedImage(animatedImage), null), new AnimatedImageCompositor.Callback() { // from class: com.facebook.imagepipeline.animated.factory.AnimatedImageFactoryImpl.1
            @Override // com.facebook.imagepipeline.animated.impl.AnimatedImageCompositor.Callback
            @Nullable
            public CloseableReference<Bitmap> getCachedBitmap(int i2) {
                return null;
            }

            @Override // com.facebook.imagepipeline.animated.impl.AnimatedImageCompositor.Callback
            public void onIntermediateResult(int i2, Bitmap bitmap) {
            }
        }).renderFrame(i, closeableReferenceCreateBitmap.get());
        return closeableReferenceCreateBitmap;
    }

    private List<CloseableReference<Bitmap>> decodeAllFrames(AnimatedImage animatedImage, Bitmap.Config config) {
        AnimatedDrawableBackend animatedDrawableBackend = this.mAnimatedDrawableBackendProvider.get(AnimatedImageResult.forAnimatedImage(animatedImage), null);
        final ArrayList arrayList = new ArrayList(animatedDrawableBackend.getFrameCount());
        AnimatedImageCompositor animatedImageCompositor = new AnimatedImageCompositor(animatedDrawableBackend, new AnimatedImageCompositor.Callback() { // from class: com.facebook.imagepipeline.animated.factory.AnimatedImageFactoryImpl.2
            @Override // com.facebook.imagepipeline.animated.impl.AnimatedImageCompositor.Callback
            public CloseableReference<Bitmap> getCachedBitmap(int i) {
                return CloseableReference.cloneOrNull((CloseableReference) arrayList.get(i));
            }

            @Override // com.facebook.imagepipeline.animated.impl.AnimatedImageCompositor.Callback
            public void onIntermediateResult(int i, Bitmap bitmap) {
            }
        });
        for (int i = 0; i < animatedDrawableBackend.getFrameCount(); i++) {
            CloseableReference<Bitmap> closeableReferenceCreateBitmap = createBitmap(animatedDrawableBackend.getWidth(), animatedDrawableBackend.getHeight(), config);
            animatedImageCompositor.renderFrame(i, closeableReferenceCreateBitmap.get());
            arrayList.add(closeableReferenceCreateBitmap);
        }
        return arrayList;
    }

    private CloseableImage getCloseableImage(ImageDecodeOptions imageDecodeOptions, AnimatedImage animatedImage, Bitmap.Config config) throws Throwable {
        List<CloseableReference<Bitmap>> listDecodeAllFrames;
        CloseableReference<Bitmap> closeableReferenceCreatePreviewBitmap = null;
        try {
            int frameCount = imageDecodeOptions.useLastFrameForPreview ? animatedImage.getFrameCount() - 1 : 0;
            if (imageDecodeOptions.forceStaticImage) {
                CloseableStaticBitmap closeableStaticBitmap = new CloseableStaticBitmap(createPreviewBitmap(animatedImage, config, frameCount), ImmutableQualityInfo.FULL_QUALITY, 0);
                CloseableReference.closeSafely((CloseableReference<?>) null);
                CloseableReference.closeSafely((Iterable<? extends CloseableReference<?>>) null);
                return closeableStaticBitmap;
            }
            if (imageDecodeOptions.decodeAllFrames) {
                listDecodeAllFrames = decodeAllFrames(animatedImage, config);
                try {
                    closeableReferenceCreatePreviewBitmap = CloseableReference.cloneOrNull(listDecodeAllFrames.get(frameCount));
                } catch (Throwable th) {
                    th = th;
                    CloseableReference.closeSafely(closeableReferenceCreatePreviewBitmap);
                    CloseableReference.closeSafely(listDecodeAllFrames);
                    throw th;
                }
            } else {
                listDecodeAllFrames = null;
            }
            if (imageDecodeOptions.decodePreviewFrame && closeableReferenceCreatePreviewBitmap == null) {
                closeableReferenceCreatePreviewBitmap = createPreviewBitmap(animatedImage, config, frameCount);
            }
            CloseableAnimatedImage closeableAnimatedImage = new CloseableAnimatedImage(AnimatedImageResult.newBuilder(animatedImage).setPreviewBitmap(closeableReferenceCreatePreviewBitmap).setFrameForPreview(frameCount).setDecodedFrames(listDecodeAllFrames).setBitmapTransformation(imageDecodeOptions.bitmapTransformation).build());
            CloseableReference.closeSafely(closeableReferenceCreatePreviewBitmap);
            CloseableReference.closeSafely(listDecodeAllFrames);
            return closeableAnimatedImage;
        } catch (Throwable th2) {
            th = th2;
            listDecodeAllFrames = null;
        }
    }

    @Nullable
    private static AnimatedImageDecoder loadIfPresent(String str) {
        try {
            return (AnimatedImageDecoder) Class.forName(str).newInstance();
        } catch (Throwable unused) {
            return null;
        }
    }

    @Override // com.facebook.imagepipeline.animated.factory.AnimatedImageFactory
    public CloseableImage decodeGif(EncodedImage encodedImage, ImageDecodeOptions imageDecodeOptions, Bitmap.Config config) {
        if (sGifAnimatedImageDecoder == null) {
            throw new UnsupportedOperationException("To encode animated gif please add the dependency to the animated-gif module");
        }
        CloseableReference<PooledByteBuffer> byteBufferRef = encodedImage.getByteBufferRef();
        Preconditions.checkNotNull(byteBufferRef);
        try {
            PooledByteBuffer pooledByteBuffer = byteBufferRef.get();
            return getCloseableImage(imageDecodeOptions, pooledByteBuffer.getByteBuffer() != null ? sGifAnimatedImageDecoder.decodeFromByteBuffer(pooledByteBuffer.getByteBuffer(), imageDecodeOptions) : sGifAnimatedImageDecoder.decodeFromNativeMemory(pooledByteBuffer.getNativePtr(), pooledByteBuffer.size(), imageDecodeOptions), config);
        } finally {
            CloseableReference.closeSafely(byteBufferRef);
        }
    }

    @Override // com.facebook.imagepipeline.animated.factory.AnimatedImageFactory
    public CloseableImage decodeWebP(EncodedImage encodedImage, ImageDecodeOptions imageDecodeOptions, Bitmap.Config config) {
        if (sWebpAnimatedImageDecoder == null) {
            throw new UnsupportedOperationException("To encode animated webp please add the dependency to the animated-webp module");
        }
        CloseableReference<PooledByteBuffer> byteBufferRef = encodedImage.getByteBufferRef();
        Preconditions.checkNotNull(byteBufferRef);
        try {
            PooledByteBuffer pooledByteBuffer = byteBufferRef.get();
            return getCloseableImage(imageDecodeOptions, pooledByteBuffer.getByteBuffer() != null ? sWebpAnimatedImageDecoder.decodeFromByteBuffer(pooledByteBuffer.getByteBuffer(), imageDecodeOptions) : sWebpAnimatedImageDecoder.decodeFromNativeMemory(pooledByteBuffer.getNativePtr(), pooledByteBuffer.size(), imageDecodeOptions), config);
        } finally {
            CloseableReference.closeSafely(byteBufferRef);
        }
    }
}
