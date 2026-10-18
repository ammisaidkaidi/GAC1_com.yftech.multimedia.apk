package com.facebook.imagepipeline.core;

import android.content.ContentResolver;
import android.net.Uri;
import android.telecom.Logging.Session;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.media.MediaUtils;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.webp.WebpSupportStatus;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.producers.DiskCacheReadProducer;
import com.facebook.imagepipeline.producers.DiskCacheWriteProducer;
import com.facebook.imagepipeline.producers.EncodedMemoryCacheProducer;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.facebook.imagepipeline.producers.Producer;
import com.facebook.imagepipeline.producers.RemoveImageTransformMetaDataProducer;
import com.facebook.imagepipeline.producers.ThreadHandoffProducerQueue;
import com.facebook.imagepipeline.producers.ThrottlingProducer;
import com.facebook.imagepipeline.producers.ThumbnailProducer;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory;
import com.facebook.infer.annotation.Nullsafe;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class ProducerSequenceFactory {
    private final boolean mAllowDelay;

    @Nullable
    @VisibleForTesting
    Producer<EncodedImage> mBackgroundLocalContentUriFetchToEncodedMemorySequence;

    @Nullable
    @VisibleForTesting
    Producer<EncodedImage> mBackgroundLocalFileFetchToEncodedMemorySequence;

    @Nullable
    @VisibleForTesting
    Producer<EncodedImage> mBackgroundNetworkFetchToEncodedMemorySequence;

    @Nullable
    private Producer<EncodedImage> mCommonNetworkFetchToEncodedMemorySequence;
    private final ContentResolver mContentResolver;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<CloseableImage>> mDataFetchSequence;
    private final boolean mDiskCacheEnabled;
    private final boolean mDownsampleEnabled;
    private final ImageTranscoderFactory mImageTranscoderFactory;
    private final boolean mIsDiskCacheProbingEnabled;
    private final boolean mIsEncodedMemoryCacheProbingEnabled;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<CloseableImage>> mLocalAssetFetchSequence;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<PooledByteBuffer>> mLocalContentUriEncodedImageProducerSequence;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<CloseableImage>> mLocalContentUriFetchSequence;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<PooledByteBuffer>> mLocalFileEncodedImageProducerSequence;

    @Nullable
    @VisibleForTesting
    Producer<Void> mLocalFileFetchToEncodedMemoryPrefetchSequence;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<CloseableImage>> mLocalImageFileFetchSequence;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<CloseableImage>> mLocalResourceFetchSequence;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<CloseableImage>> mLocalVideoFileFetchSequence;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<PooledByteBuffer>> mNetworkEncodedImageProducerSequence;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<CloseableImage>> mNetworkFetchSequence;

    @Nullable
    @VisibleForTesting
    Producer<Void> mNetworkFetchToEncodedMemoryPrefetchSequence;
    private final NetworkFetcher mNetworkFetcher;
    private final boolean mPartialImageCachingEnabled;
    private final ProducerFactory mProducerFactory;

    @Nullable
    @VisibleForTesting
    Producer<CloseableReference<CloseableImage>> mQualifiedResourceFetchSequence;
    private final boolean mResizeAndRotateEnabledForNetwork;
    private final ThreadHandoffProducerQueue mThreadHandoffProducerQueue;
    private final boolean mUseBitmapPrepareToDraw;
    private final boolean mUseCombinedNetworkAndCacheProducer;
    private final boolean mWebpSupportEnabled;

    @VisibleForTesting
    Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> mPostprocessorSequences = new HashMap();

    @VisibleForTesting
    Map<Producer<CloseableReference<CloseableImage>>, Producer<Void>> mCloseableImagePrefetchSequences = new HashMap();

    @VisibleForTesting
    Map<Producer<CloseableReference<CloseableImage>>, Producer<CloseableReference<CloseableImage>>> mBitmapPrepareSequences = new HashMap();

    public ProducerSequenceFactory(ContentResolver contentResolver, ProducerFactory producerFactory, NetworkFetcher networkFetcher, boolean z, boolean z2, ThreadHandoffProducerQueue threadHandoffProducerQueue, boolean z3, boolean z4, boolean z5, boolean z6, ImageTranscoderFactory imageTranscoderFactory, boolean z7, boolean z8, boolean z9, boolean z10) {
        this.mContentResolver = contentResolver;
        this.mProducerFactory = producerFactory;
        this.mNetworkFetcher = networkFetcher;
        this.mResizeAndRotateEnabledForNetwork = z;
        this.mWebpSupportEnabled = z2;
        this.mUseCombinedNetworkAndCacheProducer = z9;
        this.mThreadHandoffProducerQueue = threadHandoffProducerQueue;
        this.mDownsampleEnabled = z3;
        this.mUseBitmapPrepareToDraw = z4;
        this.mPartialImageCachingEnabled = z5;
        this.mDiskCacheEnabled = z6;
        this.mImageTranscoderFactory = imageTranscoderFactory;
        this.mIsEncodedMemoryCacheProbingEnabled = z7;
        this.mIsDiskCacheProbingEnabled = z8;
        this.mAllowDelay = z10;
    }

    private synchronized Producer<EncodedImage> getBackgroundLocalContentUriFetchToEncodeMemorySequence() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalContentUriFetchToEncodeMemorySequence");
        }
        if (this.mBackgroundLocalContentUriFetchToEncodedMemorySequence == null) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalContentUriFetchToEncodeMemorySequence:init");
            }
            this.mBackgroundLocalContentUriFetchToEncodedMemorySequence = this.mProducerFactory.newBackgroundThreadHandoffProducer(newEncodedCacheMultiplexToTranscodeSequence(this.mProducerFactory.newLocalContentUriFetchProducer()), this.mThreadHandoffProducerQueue);
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return this.mBackgroundLocalContentUriFetchToEncodedMemorySequence;
    }

    private synchronized Producer<EncodedImage> getBackgroundLocalFileFetchToEncodeMemorySequence() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalFileFetchToEncodeMemorySequence");
        }
        if (this.mBackgroundLocalFileFetchToEncodedMemorySequence == null) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundLocalFileFetchToEncodeMemorySequence:init");
            }
            this.mBackgroundLocalFileFetchToEncodedMemorySequence = this.mProducerFactory.newBackgroundThreadHandoffProducer(newEncodedCacheMultiplexToTranscodeSequence(this.mProducerFactory.newLocalFileFetchProducer()), this.mThreadHandoffProducerQueue);
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return this.mBackgroundLocalFileFetchToEncodedMemorySequence;
    }

    private synchronized Producer<EncodedImage> getBackgroundNetworkFetchToEncodedMemorySequence() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundNetworkFetchToEncodedMemorySequence");
        }
        if (this.mBackgroundNetworkFetchToEncodedMemorySequence == null) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getBackgroundNetworkFetchToEncodedMemorySequence:init");
            }
            this.mBackgroundNetworkFetchToEncodedMemorySequence = this.mProducerFactory.newBackgroundThreadHandoffProducer(getCommonNetworkFetchToEncodedMemorySequence(), this.mThreadHandoffProducerQueue);
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return this.mBackgroundNetworkFetchToEncodedMemorySequence;
    }

    private Producer<CloseableReference<CloseableImage>> getBasicDecodedImageSequence(ImageRequest imageRequest) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getBasicDecodedImageSequence");
            }
            Preconditions.checkNotNull(imageRequest);
            Uri sourceUri = imageRequest.getSourceUri();
            Preconditions.checkNotNull(sourceUri, "Uri is null.");
            int sourceUriType = imageRequest.getSourceUriType();
            if (sourceUriType == 0) {
                Producer<CloseableReference<CloseableImage>> networkFetchSequence = getNetworkFetchSequence();
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
                return networkFetchSequence;
            }
            switch (sourceUriType) {
                case 2:
                    Producer<CloseableReference<CloseableImage>> localVideoFileFetchSequence = getLocalVideoFileFetchSequence();
                    if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                    }
                    return localVideoFileFetchSequence;
                case 3:
                    Producer<CloseableReference<CloseableImage>> localImageFileFetchSequence = getLocalImageFileFetchSequence();
                    if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                    }
                    return localImageFileFetchSequence;
                case 4:
                    if (MediaUtils.isVideo(this.mContentResolver.getType(sourceUri))) {
                        Producer<CloseableReference<CloseableImage>> localVideoFileFetchSequence2 = getLocalVideoFileFetchSequence();
                        if (FrescoSystrace.isTracing()) {
                            FrescoSystrace.endSection();
                        }
                        return localVideoFileFetchSequence2;
                    }
                    Producer<CloseableReference<CloseableImage>> localContentUriFetchSequence = getLocalContentUriFetchSequence();
                    if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                    }
                    return localContentUriFetchSequence;
                case 5:
                    Producer<CloseableReference<CloseableImage>> localAssetFetchSequence = getLocalAssetFetchSequence();
                    if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                    }
                    return localAssetFetchSequence;
                case 6:
                    Producer<CloseableReference<CloseableImage>> localResourceFetchSequence = getLocalResourceFetchSequence();
                    if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                    }
                    return localResourceFetchSequence;
                case 7:
                    Producer<CloseableReference<CloseableImage>> dataFetchSequence = getDataFetchSequence();
                    if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                    }
                    return dataFetchSequence;
                case 8:
                    Producer<CloseableReference<CloseableImage>> qualifiedResourceFetchSequence = getQualifiedResourceFetchSequence();
                    if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                    }
                    return qualifiedResourceFetchSequence;
                default:
                    throw new IllegalArgumentException("Unsupported uri scheme! Uri is: " + getShortenedUriString(sourceUri));
            }
        } catch (Throwable th) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
            throw th;
        }
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getBitmapPrepareSequence(Producer<CloseableReference<CloseableImage>> producer) {
        Producer<CloseableReference<CloseableImage>> producerNewBitmapPrepareProducer;
        producerNewBitmapPrepareProducer = this.mBitmapPrepareSequences.get(producer);
        if (producerNewBitmapPrepareProducer == null) {
            producerNewBitmapPrepareProducer = this.mProducerFactory.newBitmapPrepareProducer(producer);
            this.mBitmapPrepareSequences.put(producer, producerNewBitmapPrepareProducer);
        }
        return producerNewBitmapPrepareProducer;
    }

    private synchronized Producer<EncodedImage> getCommonNetworkFetchToEncodedMemorySequence() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#getCommonNetworkFetchToEncodedMemorySequence");
        }
        if (this.mCommonNetworkFetchToEncodedMemorySequence == null) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getCommonNetworkFetchToEncodedMemorySequence:init");
            }
            this.mCommonNetworkFetchToEncodedMemorySequence = ProducerFactory.newAddImageTransformMetaDataProducer((Producer) Preconditions.checkNotNull(this.mUseCombinedNetworkAndCacheProducer ? this.mProducerFactory.newCombinedNetworkAndCacheProducer(this.mNetworkFetcher) : newEncodedCacheMultiplexToTranscodeSequence(this.mProducerFactory.newNetworkFetchProducer(this.mNetworkFetcher))));
            this.mCommonNetworkFetchToEncodedMemorySequence = this.mProducerFactory.newResizeAndRotateProducer(this.mCommonNetworkFetchToEncodedMemorySequence, this.mResizeAndRotateEnabledForNetwork && !this.mDownsampleEnabled, this.mImageTranscoderFactory);
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return this.mCommonNetworkFetchToEncodedMemorySequence;
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getDataFetchSequence() {
        if (this.mDataFetchSequence == null) {
            Producer<EncodedImage> producerNewDataFetchProducer = this.mProducerFactory.newDataFetchProducer();
            if (WebpSupportStatus.sIsWebpSupportRequired && (!this.mWebpSupportEnabled || WebpSupportStatus.sWebpBitmapFactory == null)) {
                producerNewDataFetchProducer = this.mProducerFactory.newWebpTranscodeProducer(producerNewDataFetchProducer);
            }
            ProducerFactory producerFactory = this.mProducerFactory;
            this.mDataFetchSequence = newBitmapCacheGetToDecodeSequence(this.mProducerFactory.newResizeAndRotateProducer(ProducerFactory.newAddImageTransformMetaDataProducer(producerNewDataFetchProducer), true, this.mImageTranscoderFactory));
        }
        return this.mDataFetchSequence;
    }

    private synchronized Producer<Void> getDecodedImagePrefetchSequence(Producer<CloseableReference<CloseableImage>> producer) {
        Producer<Void> producerNewSwallowResultProducer;
        producerNewSwallowResultProducer = this.mCloseableImagePrefetchSequences.get(producer);
        if (producerNewSwallowResultProducer == null) {
            producerNewSwallowResultProducer = this.mProducerFactory.newSwallowResultProducer(producer);
            this.mCloseableImagePrefetchSequences.put(producer, producerNewSwallowResultProducer);
        }
        return producerNewSwallowResultProducer;
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getDelaySequence(Producer<CloseableReference<CloseableImage>> producer) {
        return this.mProducerFactory.newDelayProducer(producer);
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getLocalAssetFetchSequence() {
        if (this.mLocalAssetFetchSequence == null) {
            this.mLocalAssetFetchSequence = newBitmapCacheGetToLocalTransformSequence(this.mProducerFactory.newLocalAssetFetchProducer());
        }
        return this.mLocalAssetFetchSequence;
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getLocalContentUriFetchSequence() {
        if (this.mLocalContentUriFetchSequence == null) {
            this.mLocalContentUriFetchSequence = newBitmapCacheGetToLocalTransformSequence(this.mProducerFactory.newLocalContentUriFetchProducer(), new ThumbnailProducer[]{this.mProducerFactory.newLocalContentUriThumbnailFetchProducer(), this.mProducerFactory.newLocalExifThumbnailProducer()});
        }
        return this.mLocalContentUriFetchSequence;
    }

    private synchronized Producer<Void> getLocalFileFetchToEncodedMemoryPrefetchSequence() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchToEncodedMemoryPrefetchSequence");
        }
        if (this.mLocalFileFetchToEncodedMemoryPrefetchSequence == null) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchToEncodedMemoryPrefetchSequence:init");
            }
            this.mLocalFileFetchToEncodedMemoryPrefetchSequence = this.mProducerFactory.newSwallowResultProducer(getBackgroundLocalFileFetchToEncodeMemorySequence());
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return this.mLocalFileFetchToEncodedMemoryPrefetchSequence;
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getLocalImageFileFetchSequence() {
        if (this.mLocalImageFileFetchSequence == null) {
            this.mLocalImageFileFetchSequence = newBitmapCacheGetToLocalTransformSequence(this.mProducerFactory.newLocalFileFetchProducer());
        }
        return this.mLocalImageFileFetchSequence;
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getLocalResourceFetchSequence() {
        if (this.mLocalResourceFetchSequence == null) {
            this.mLocalResourceFetchSequence = newBitmapCacheGetToLocalTransformSequence(this.mProducerFactory.newLocalResourceFetchProducer());
        }
        return this.mLocalResourceFetchSequence;
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getLocalVideoFileFetchSequence() {
        if (this.mLocalVideoFileFetchSequence == null) {
            this.mLocalVideoFileFetchSequence = newBitmapCacheGetToBitmapCacheSequence(this.mProducerFactory.newLocalVideoThumbnailProducer());
        }
        return this.mLocalVideoFileFetchSequence;
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getNetworkFetchSequence() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchSequence");
        }
        if (this.mNetworkFetchSequence == null) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchSequence:init");
            }
            this.mNetworkFetchSequence = newBitmapCacheGetToDecodeSequence(getCommonNetworkFetchToEncodedMemorySequence());
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return this.mNetworkFetchSequence;
    }

    private synchronized Producer<Void> getNetworkFetchToEncodedMemoryPrefetchSequence() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchToEncodedMemoryPrefetchSequence");
        }
        if (this.mNetworkFetchToEncodedMemoryPrefetchSequence == null) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchToEncodedMemoryPrefetchSequence:init");
            }
            this.mNetworkFetchToEncodedMemoryPrefetchSequence = this.mProducerFactory.newSwallowResultProducer(getBackgroundNetworkFetchToEncodedMemorySequence());
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return this.mNetworkFetchToEncodedMemoryPrefetchSequence;
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getPostprocessorSequence(Producer<CloseableReference<CloseableImage>> producer) {
        Producer<CloseableReference<CloseableImage>> producerNewPostprocessorBitmapMemoryCacheProducer;
        producerNewPostprocessorBitmapMemoryCacheProducer = this.mPostprocessorSequences.get(producer);
        if (producerNewPostprocessorBitmapMemoryCacheProducer == null) {
            producerNewPostprocessorBitmapMemoryCacheProducer = this.mProducerFactory.newPostprocessorBitmapMemoryCacheProducer(this.mProducerFactory.newPostprocessorProducer(producer));
            this.mPostprocessorSequences.put(producer, producerNewPostprocessorBitmapMemoryCacheProducer);
        }
        return producerNewPostprocessorBitmapMemoryCacheProducer;
    }

    private synchronized Producer<CloseableReference<CloseableImage>> getQualifiedResourceFetchSequence() {
        if (this.mQualifiedResourceFetchSequence == null) {
            this.mQualifiedResourceFetchSequence = newBitmapCacheGetToLocalTransformSequence(this.mProducerFactory.newQualifiedResourceFetchProducer());
        }
        return this.mQualifiedResourceFetchSequence;
    }

    private static String getShortenedUriString(Uri uri) {
        String strValueOf = String.valueOf(uri);
        if (strValueOf.length() <= 30) {
            return strValueOf;
        }
        return strValueOf.substring(0, 30) + Session.TRUNCATE_STRING;
    }

    private Producer<CloseableReference<CloseableImage>> newBitmapCacheGetToBitmapCacheSequence(Producer<CloseableReference<CloseableImage>> producer) {
        Producer<CloseableReference<CloseableImage>> producerNewBackgroundThreadHandoffProducer = this.mProducerFactory.newBackgroundThreadHandoffProducer(this.mProducerFactory.newBitmapMemoryCacheKeyMultiplexProducer(this.mProducerFactory.newBitmapMemoryCacheProducer(producer)), this.mThreadHandoffProducerQueue);
        if (!this.mIsEncodedMemoryCacheProbingEnabled && !this.mIsDiskCacheProbingEnabled) {
            return this.mProducerFactory.newBitmapMemoryCacheGetProducer(producerNewBackgroundThreadHandoffProducer);
        }
        return this.mProducerFactory.newBitmapProbeProducer(this.mProducerFactory.newBitmapMemoryCacheGetProducer(producerNewBackgroundThreadHandoffProducer));
    }

    private Producer<CloseableReference<CloseableImage>> newBitmapCacheGetToDecodeSequence(Producer<EncodedImage> producer) {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#newBitmapCacheGetToDecodeSequence");
        }
        Producer<CloseableReference<CloseableImage>> producerNewBitmapCacheGetToBitmapCacheSequence = newBitmapCacheGetToBitmapCacheSequence(this.mProducerFactory.newDecodeProducer(producer));
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return producerNewBitmapCacheGetToBitmapCacheSequence;
    }

    private Producer<CloseableReference<CloseableImage>> newBitmapCacheGetToLocalTransformSequence(Producer<EncodedImage> producer) {
        return newBitmapCacheGetToLocalTransformSequence(producer, new ThumbnailProducer[]{this.mProducerFactory.newLocalExifThumbnailProducer()});
    }

    private Producer<CloseableReference<CloseableImage>> newBitmapCacheGetToLocalTransformSequence(Producer<EncodedImage> producer, ThumbnailProducer<EncodedImage>[] thumbnailProducerArr) {
        return newBitmapCacheGetToDecodeSequence(newLocalTransformationsSequence(newEncodedCacheMultiplexToTranscodeSequence(producer), thumbnailProducerArr));
    }

    private Producer<EncodedImage> newDiskCacheSequence(Producer<EncodedImage> producer) {
        DiskCacheWriteProducer diskCacheWriteProducerNewDiskCacheWriteProducer;
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#newDiskCacheSequence");
        }
        if (this.mPartialImageCachingEnabled) {
            diskCacheWriteProducerNewDiskCacheWriteProducer = this.mProducerFactory.newDiskCacheWriteProducer(this.mProducerFactory.newPartialDiskCacheProducer(producer));
        } else {
            diskCacheWriteProducerNewDiskCacheWriteProducer = this.mProducerFactory.newDiskCacheWriteProducer(producer);
        }
        DiskCacheReadProducer diskCacheReadProducerNewDiskCacheReadProducer = this.mProducerFactory.newDiskCacheReadProducer(diskCacheWriteProducerNewDiskCacheWriteProducer);
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return diskCacheReadProducerNewDiskCacheReadProducer;
    }

    private Producer<EncodedImage> newEncodedCacheMultiplexToTranscodeSequence(Producer<EncodedImage> producer) {
        if (WebpSupportStatus.sIsWebpSupportRequired && (!this.mWebpSupportEnabled || WebpSupportStatus.sWebpBitmapFactory == null)) {
            producer = this.mProducerFactory.newWebpTranscodeProducer(producer);
        }
        if (this.mDiskCacheEnabled) {
            producer = newDiskCacheSequence(producer);
        }
        EncodedMemoryCacheProducer encodedMemoryCacheProducerNewEncodedMemoryCacheProducer = this.mProducerFactory.newEncodedMemoryCacheProducer(producer);
        if (!this.mIsDiskCacheProbingEnabled) {
            return this.mProducerFactory.newEncodedCacheKeyMultiplexProducer(encodedMemoryCacheProducerNewEncodedMemoryCacheProducer);
        }
        return this.mProducerFactory.newEncodedCacheKeyMultiplexProducer(this.mProducerFactory.newEncodedProbeProducer(encodedMemoryCacheProducerNewEncodedMemoryCacheProducer));
    }

    private Producer<EncodedImage> newLocalThumbnailProducer(ThumbnailProducer<EncodedImage>[] thumbnailProducerArr) {
        return this.mProducerFactory.newResizeAndRotateProducer(this.mProducerFactory.newThumbnailBranchProducer(thumbnailProducerArr), true, this.mImageTranscoderFactory);
    }

    private Producer<EncodedImage> newLocalTransformationsSequence(Producer<EncodedImage> producer, ThumbnailProducer<EncodedImage>[] thumbnailProducerArr) {
        ThrottlingProducer throttlingProducerNewThrottlingProducer = this.mProducerFactory.newThrottlingProducer(this.mProducerFactory.newResizeAndRotateProducer(ProducerFactory.newAddImageTransformMetaDataProducer(producer), true, this.mImageTranscoderFactory));
        ProducerFactory producerFactory = this.mProducerFactory;
        return ProducerFactory.newBranchOnSeparateImagesProducer(newLocalThumbnailProducer(thumbnailProducerArr), throttlingProducerNewThrottlingProducer);
    }

    private static void validateEncodedImageRequest(ImageRequest imageRequest) {
        Preconditions.checkNotNull(imageRequest);
        Preconditions.checkArgument(Boolean.valueOf(imageRequest.getLowestPermittedRequestLevel().getValue() <= ImageRequest.RequestLevel.ENCODED_MEMORY_CACHE.getValue()));
    }

    public Producer<Void> getDecodedImagePrefetchProducerSequence(ImageRequest imageRequest) {
        Producer<CloseableReference<CloseableImage>> basicDecodedImageSequence = getBasicDecodedImageSequence(imageRequest);
        if (this.mUseBitmapPrepareToDraw) {
            basicDecodedImageSequence = getBitmapPrepareSequence(basicDecodedImageSequence);
        }
        return getDecodedImagePrefetchSequence(basicDecodedImageSequence);
    }

    public Producer<CloseableReference<CloseableImage>> getDecodedImageProducerSequence(ImageRequest imageRequest) {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ProducerSequenceFactory#getDecodedImageProducerSequence");
        }
        Producer<CloseableReference<CloseableImage>> basicDecodedImageSequence = getBasicDecodedImageSequence(imageRequest);
        if (imageRequest.getPostprocessor() != null) {
            basicDecodedImageSequence = getPostprocessorSequence(basicDecodedImageSequence);
        }
        if (this.mUseBitmapPrepareToDraw) {
            basicDecodedImageSequence = getBitmapPrepareSequence(basicDecodedImageSequence);
        }
        if (this.mAllowDelay && imageRequest.getDelayMs() > 0) {
            basicDecodedImageSequence = getDelaySequence(basicDecodedImageSequence);
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return basicDecodedImageSequence;
    }

    public Producer<Void> getEncodedImagePrefetchProducerSequence(ImageRequest imageRequest) {
        validateEncodedImageRequest(imageRequest);
        int sourceUriType = imageRequest.getSourceUriType();
        if (sourceUriType == 0) {
            return getNetworkFetchToEncodedMemoryPrefetchSequence();
        }
        switch (sourceUriType) {
            case 2:
            case 3:
                return getLocalFileFetchToEncodedMemoryPrefetchSequence();
            default:
                throw new IllegalArgumentException("Unsupported uri scheme for encoded image fetch! Uri is: " + getShortenedUriString(imageRequest.getSourceUri()));
        }
    }

    public Producer<CloseableReference<PooledByteBuffer>> getEncodedImageProducerSequence(ImageRequest imageRequest) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getEncodedImageProducerSequence");
            }
            validateEncodedImageRequest(imageRequest);
            Uri sourceUri = imageRequest.getSourceUri();
            int sourceUriType = imageRequest.getSourceUriType();
            if (sourceUriType == 0) {
                Producer<CloseableReference<PooledByteBuffer>> networkFetchEncodedImageProducerSequence = getNetworkFetchEncodedImageProducerSequence();
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
                return networkFetchEncodedImageProducerSequence;
            }
            switch (sourceUriType) {
                case 2:
                case 3:
                    Producer<CloseableReference<PooledByteBuffer>> localFileFetchEncodedImageProducerSequence = getLocalFileFetchEncodedImageProducerSequence();
                    if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                    }
                    return localFileFetchEncodedImageProducerSequence;
                case 4:
                    Producer<CloseableReference<PooledByteBuffer>> localContentUriFetchEncodedImageProducerSequence = getLocalContentUriFetchEncodedImageProducerSequence();
                    if (FrescoSystrace.isTracing()) {
                        FrescoSystrace.endSection();
                    }
                    return localContentUriFetchEncodedImageProducerSequence;
                default:
                    throw new IllegalArgumentException("Unsupported uri scheme for encoded image fetch! Uri is: " + getShortenedUriString(sourceUri));
            }
        } catch (Throwable th) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
            throw th;
        }
    }

    public Producer<CloseableReference<PooledByteBuffer>> getLocalContentUriFetchEncodedImageProducerSequence() {
        synchronized (this) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalContentUriFetchEncodedImageProducerSequence");
            }
            if (this.mLocalContentUriEncodedImageProducerSequence == null) {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalContentUriFetchEncodedImageProducerSequence:init");
                }
                this.mLocalContentUriEncodedImageProducerSequence = new RemoveImageTransformMetaDataProducer(getBackgroundLocalContentUriFetchToEncodeMemorySequence());
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
            }
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        return this.mLocalContentUriEncodedImageProducerSequence;
    }

    public Producer<CloseableReference<PooledByteBuffer>> getLocalFileFetchEncodedImageProducerSequence() {
        synchronized (this) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchEncodedImageProducerSequence");
            }
            if (this.mLocalFileEncodedImageProducerSequence == null) {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.beginSection("ProducerSequenceFactory#getLocalFileFetchEncodedImageProducerSequence:init");
                }
                this.mLocalFileEncodedImageProducerSequence = new RemoveImageTransformMetaDataProducer(getBackgroundLocalFileFetchToEncodeMemorySequence());
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
            }
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        return this.mLocalFileEncodedImageProducerSequence;
    }

    public Producer<CloseableReference<PooledByteBuffer>> getNetworkFetchEncodedImageProducerSequence() {
        synchronized (this) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchEncodedImageProducerSequence");
            }
            if (this.mNetworkEncodedImageProducerSequence == null) {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.beginSection("ProducerSequenceFactory#getNetworkFetchEncodedImageProducerSequence:init");
                }
                this.mNetworkEncodedImageProducerSequence = new RemoveImageTransformMetaDataProducer(getBackgroundNetworkFetchToEncodedMemorySequence());
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
            }
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
        return this.mNetworkEncodedImageProducerSequence;
    }
}
