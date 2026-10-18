package com.facebook.imagepipeline.producers;

import android.graphics.Bitmap;
import android.os.Build;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.ByteArrayPool;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.ExceptionWithNoStacktrace;
import com.facebook.common.util.UriUtil;
import com.facebook.imageformat.DefaultImageFormats;
import com.facebook.imageformat.ImageFormat;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.CloseableReferenceFactory;
import com.facebook.imagepipeline.decoder.DecodeException;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegParser;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.imagepipeline.transcoder.DownsampleUtil;
import com.facebook.imageutils.BitmapUtil;
import com.facebook.infer.annotation.Nullsafe;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class DecodeProducer implements Producer<CloseableReference<CloseableImage>> {
    public static final String ENCODED_IMAGE_SIZE = "encodedImageSize";
    public static final String EXTRA_BITMAP_BYTES = "byteCount";
    public static final String EXTRA_BITMAP_SIZE = "bitmapSize";
    public static final String EXTRA_HAS_GOOD_QUALITY = "hasGoodQuality";
    public static final String EXTRA_IMAGE_FORMAT_NAME = "imageFormat";
    public static final String EXTRA_IS_FINAL = "isFinal";
    private static final int MAX_BITMAP_SIZE = 104857600;
    public static final String PRODUCER_NAME = "DecodeProducer";
    public static final String REQUESTED_IMAGE_SIZE = "requestedImageSize";
    public static final String SAMPLE_SIZE = "sampleSize";
    private final ByteArrayPool mByteArrayPool;
    private final CloseableReferenceFactory mCloseableReferenceFactory;
    private final boolean mDecodeCancellationEnabled;
    private final boolean mDownsampleEnabled;
    private final boolean mDownsampleEnabledForNetwork;
    private final Executor mExecutor;
    private final ImageDecoder mImageDecoder;
    private final Producer<EncodedImage> mInputProducer;
    private final int mMaxBitmapSize;
    private final ProgressiveJpegConfig mProgressiveJpegConfig;

    @Nullable
    private final Runnable mReclaimMemoryRunnable;
    private final Supplier<Boolean> mRecoverFromDecoderOOM;

    private class LocalImagesProgressiveDecoder extends ProgressiveDecoder {
        public LocalImagesProgressiveDecoder(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext, boolean z, int i) {
            super(consumer, producerContext, z, i);
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected int getIntermediateImageEndOffset(EncodedImage encodedImage) {
            return encodedImage.getSize();
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected QualityInfo getQualityInfo() {
            return ImmutableQualityInfo.of(0, false, false);
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected synchronized boolean updateDecodeJob(EncodedImage encodedImage, int i) {
            if (isNotLast(i)) {
                return false;
            }
            return super.updateDecodeJob(encodedImage, i);
        }
    }

    private class NetworkImagesProgressiveDecoder extends ProgressiveDecoder {
        private int mLastScheduledScanNumber;
        private final ProgressiveJpegConfig mProgressiveJpegConfig;
        private final ProgressiveJpegParser mProgressiveJpegParser;

        public NetworkImagesProgressiveDecoder(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext, ProgressiveJpegParser progressiveJpegParser, ProgressiveJpegConfig progressiveJpegConfig, boolean z, int i) {
            super(consumer, producerContext, z, i);
            this.mProgressiveJpegParser = (ProgressiveJpegParser) Preconditions.checkNotNull(progressiveJpegParser);
            this.mProgressiveJpegConfig = (ProgressiveJpegConfig) Preconditions.checkNotNull(progressiveJpegConfig);
            this.mLastScheduledScanNumber = 0;
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected int getIntermediateImageEndOffset(EncodedImage encodedImage) {
            return this.mProgressiveJpegParser.getBestScanEndOffset();
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected QualityInfo getQualityInfo() {
            return this.mProgressiveJpegConfig.getQualityInfo(this.mProgressiveJpegParser.getBestScanNumber());
        }

        @Override // com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder
        protected synchronized boolean updateDecodeJob(EncodedImage encodedImage, int i) {
            boolean zUpdateDecodeJob = super.updateDecodeJob(encodedImage, i);
            if ((isNotLast(i) || statusHasFlag(i, 8)) && !statusHasFlag(i, 4) && EncodedImage.isValid(encodedImage) && encodedImage.getImageFormat() == DefaultImageFormats.JPEG) {
                if (!this.mProgressiveJpegParser.parseMoreData(encodedImage)) {
                    return false;
                }
                int bestScanNumber = this.mProgressiveJpegParser.getBestScanNumber();
                if (bestScanNumber <= this.mLastScheduledScanNumber) {
                    return false;
                }
                if (bestScanNumber < this.mProgressiveJpegConfig.getNextScanNumberToDecode(this.mLastScheduledScanNumber) && !this.mProgressiveJpegParser.isEndMarkerRead()) {
                    return false;
                }
                this.mLastScheduledScanNumber = bestScanNumber;
            }
            return zUpdateDecodeJob;
        }
    }

    private abstract class ProgressiveDecoder extends DelegatingConsumer<EncodedImage, CloseableReference<CloseableImage>> {
        private static final int DECODE_EXCEPTION_MESSAGE_NUM_HEADER_BYTES = 10;
        private final String TAG;
        private final ImageDecodeOptions mImageDecodeOptions;

        @GuardedBy("this")
        private boolean mIsFinished;
        private final JobScheduler mJobScheduler;
        private final ProducerContext mProducerContext;
        private final ProducerListener2 mProducerListener;

        public ProgressiveDecoder(Consumer<CloseableReference<CloseableImage>> consumer, final ProducerContext producerContext, final boolean z, final int i) {
            super(consumer);
            this.TAG = "ProgressiveDecoder";
            this.mProducerContext = producerContext;
            this.mProducerListener = producerContext.getProducerListener();
            this.mImageDecodeOptions = producerContext.getImageRequest().getImageDecodeOptions();
            this.mIsFinished = false;
            this.mJobScheduler = new JobScheduler(DecodeProducer.this.mExecutor, new JobScheduler.JobRunnable() { // from class: com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder.1
                @Override // com.facebook.imagepipeline.producers.JobScheduler.JobRunnable
                public void run(EncodedImage encodedImage, int i2) {
                    if (encodedImage != null) {
                        ProgressiveDecoder.this.mProducerContext.setExtra(ProducerContext.ExtraKeys.IMAGE_FORMAT, encodedImage.getImageFormat().getName());
                        if (DecodeProducer.this.mDownsampleEnabled || !BaseConsumer.statusHasFlag(i2, 16)) {
                            ImageRequest imageRequest = producerContext.getImageRequest();
                            if (DecodeProducer.this.mDownsampleEnabledForNetwork || !UriUtil.isNetworkUri(imageRequest.getSourceUri())) {
                                encodedImage.setSampleSize(DownsampleUtil.determineSampleSize(imageRequest.getRotationOptions(), imageRequest.getResizeOptions(), encodedImage, i));
                            }
                        }
                        if (producerContext.getImagePipelineConfig().getExperiments().shouldDownsampleIfLargeBitmap()) {
                            ProgressiveDecoder.this.maybeIncreaseSampleSize(encodedImage);
                        }
                        ProgressiveDecoder.this.doDecode(encodedImage, i2);
                    }
                }
            }, this.mImageDecodeOptions.minDecodeIntervalMs);
            this.mProducerContext.addCallbacks(new BaseProducerContextCallbacks() { // from class: com.facebook.imagepipeline.producers.DecodeProducer.ProgressiveDecoder.2
                @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
                public void onCancellationRequested() {
                    if (z) {
                        ProgressiveDecoder.this.handleCancellation();
                    }
                }

                @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
                public void onIsIntermediateResultExpectedChanged() {
                    if (ProgressiveDecoder.this.mProducerContext.isIntermediateResultExpected()) {
                        ProgressiveDecoder.this.mJobScheduler.scheduleJob();
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void doDecode(EncodedImage encodedImage, int i) {
            String str;
            CloseableImage closeableImage;
            int i2 = i;
            if ((encodedImage.getImageFormat() != DefaultImageFormats.JPEG && isNotLast(i)) || isFinished() || !EncodedImage.isValid(encodedImage)) {
                return;
            }
            ImageFormat imageFormat = encodedImage.getImageFormat();
            String name = imageFormat != null ? imageFormat.getName() : "unknown";
            String str2 = encodedImage.getWidth() + "x" + encodedImage.getHeight();
            String strValueOf = String.valueOf(encodedImage.getSampleSize());
            boolean zIsLast = isLast(i);
            boolean z = zIsLast && !statusHasFlag(i2, 8);
            boolean zStatusHasFlag = statusHasFlag(i2, 4);
            ResizeOptions resizeOptions = this.mProducerContext.getImageRequest().getResizeOptions();
            if (resizeOptions != null) {
                str = resizeOptions.width + "x" + resizeOptions.height;
            } else {
                str = "unknown";
            }
            try {
                long queuedTime = this.mJobScheduler.getQueuedTime();
                String strValueOf2 = String.valueOf(this.mProducerContext.getImageRequest().getSourceUri());
                int size = (z || zStatusHasFlag) ? encodedImage.getSize() : getIntermediateImageEndOffset(encodedImage);
                QualityInfo qualityInfo = (z || zStatusHasFlag) ? ImmutableQualityInfo.FULL_QUALITY : getQualityInfo();
                this.mProducerListener.onProducerStart(this.mProducerContext, DecodeProducer.PRODUCER_NAME);
                try {
                    try {
                        CloseableImage closeableImageInternalDecode = internalDecode(encodedImage, size, qualityInfo);
                        try {
                            if (encodedImage.getSampleSize() != 1) {
                                i2 |= 16;
                            }
                            this.mProducerListener.onProducerFinishWithSuccess(this.mProducerContext, DecodeProducer.PRODUCER_NAME, getExtraMap(closeableImageInternalDecode, queuedTime, qualityInfo, zIsLast, name, str2, str, strValueOf));
                            setImageExtras(encodedImage, closeableImageInternalDecode);
                            handleResult(closeableImageInternalDecode, i2);
                            EncodedImage.closeSafely(encodedImage);
                        } catch (Exception e) {
                            e = e;
                            closeableImage = closeableImageInternalDecode;
                            this.mProducerListener.onProducerFinishWithFailure(this.mProducerContext, DecodeProducer.PRODUCER_NAME, e, getExtraMap(closeableImage, queuedTime, qualityInfo, zIsLast, name, str2, str, strValueOf));
                            handleError(e);
                            EncodedImage.closeSafely(encodedImage);
                        }
                    } catch (Exception e2) {
                        e = e2;
                        closeableImage = null;
                    }
                } catch (DecodeException e3) {
                    EncodedImage encodedImage2 = e3.getEncodedImage();
                    FLog.w("ProgressiveDecoder", "%s, {uri: %s, firstEncodedBytes: %s, length: %d}", e3.getMessage(), strValueOf2, encodedImage2.getFirstBytesAsHexString(10), Integer.valueOf(encodedImage2.getSize()));
                    throw e3;
                }
            } catch (Throwable th) {
                EncodedImage.closeSafely(encodedImage);
                throw th;
            }
        }

        @Nullable
        private Map<String, String> getExtraMap(@Nullable CloseableImage closeableImage, long j, QualityInfo qualityInfo, boolean z, String str, String str2, String str3, String str4) {
            if (!this.mProducerListener.requiresExtraMap(this.mProducerContext, DecodeProducer.PRODUCER_NAME)) {
                return null;
            }
            String strValueOf = String.valueOf(j);
            String strValueOf2 = String.valueOf(qualityInfo.isOfGoodEnoughQuality());
            String strValueOf3 = String.valueOf(z);
            if (!(closeableImage instanceof CloseableStaticBitmap)) {
                HashMap map = new HashMap(7);
                map.put("queueTime", strValueOf);
                map.put(DecodeProducer.EXTRA_HAS_GOOD_QUALITY, strValueOf2);
                map.put(DecodeProducer.EXTRA_IS_FINAL, strValueOf3);
                map.put("encodedImageSize", str2);
                map.put(DecodeProducer.EXTRA_IMAGE_FORMAT_NAME, str);
                map.put(DecodeProducer.REQUESTED_IMAGE_SIZE, str3);
                map.put(DecodeProducer.SAMPLE_SIZE, str4);
                return ImmutableMap.copyOf((Map) map);
            }
            Bitmap underlyingBitmap = ((CloseableStaticBitmap) closeableImage).getUnderlyingBitmap();
            String str5 = underlyingBitmap.getWidth() + "x" + underlyingBitmap.getHeight();
            HashMap map2 = new HashMap(8);
            map2.put(DecodeProducer.EXTRA_BITMAP_SIZE, str5);
            map2.put("queueTime", strValueOf);
            map2.put(DecodeProducer.EXTRA_HAS_GOOD_QUALITY, strValueOf2);
            map2.put(DecodeProducer.EXTRA_IS_FINAL, strValueOf3);
            map2.put("encodedImageSize", str2);
            map2.put(DecodeProducer.EXTRA_IMAGE_FORMAT_NAME, str);
            map2.put(DecodeProducer.REQUESTED_IMAGE_SIZE, str3);
            map2.put(DecodeProducer.SAMPLE_SIZE, str4);
            if (Build.VERSION.SDK_INT >= 12) {
                map2.put(DecodeProducer.EXTRA_BITMAP_BYTES, underlyingBitmap.getByteCount() + "");
            }
            return ImmutableMap.copyOf((Map) map2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void handleCancellation() {
            maybeFinish(true);
            getConsumer().onCancellation();
        }

        private void handleError(Throwable th) {
            maybeFinish(true);
            getConsumer().onFailure(th);
        }

        private void handleResult(CloseableImage closeableImage, int i) {
            CloseableReference<CloseableImage> closeableReferenceCreate = DecodeProducer.this.mCloseableReferenceFactory.create(closeableImage);
            try {
                maybeFinish(isLast(i));
                getConsumer().onNewResult(closeableReferenceCreate, i);
            } finally {
                CloseableReference.closeSafely(closeableReferenceCreate);
            }
        }

        private CloseableImage internalDecode(EncodedImage encodedImage, int i, QualityInfo qualityInfo) {
            boolean z = DecodeProducer.this.mReclaimMemoryRunnable != null && ((Boolean) DecodeProducer.this.mRecoverFromDecoderOOM.get()).booleanValue();
            try {
                return DecodeProducer.this.mImageDecoder.decode(encodedImage, i, qualityInfo, this.mImageDecodeOptions);
            } catch (OutOfMemoryError e) {
                if (!z) {
                    throw e;
                }
                DecodeProducer.this.mReclaimMemoryRunnable.run();
                System.gc();
                return DecodeProducer.this.mImageDecoder.decode(encodedImage, i, qualityInfo, this.mImageDecodeOptions);
            }
        }

        private synchronized boolean isFinished() {
            return this.mIsFinished;
        }

        private void maybeFinish(boolean z) {
            synchronized (this) {
                if (z) {
                    if (!this.mIsFinished) {
                        getConsumer().onProgressUpdate(1.0f);
                        this.mIsFinished = true;
                        this.mJobScheduler.clearJob();
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void maybeIncreaseSampleSize(EncodedImage encodedImage) {
            if (encodedImage.getImageFormat() != DefaultImageFormats.JPEG) {
                return;
            }
            encodedImage.setSampleSize(DownsampleUtil.determineSampleSizeJPEG(encodedImage, BitmapUtil.getPixelSizeForBitmapConfig(this.mImageDecodeOptions.bitmapConfig), DecodeProducer.MAX_BITMAP_SIZE));
        }

        private void setImageExtras(EncodedImage encodedImage, CloseableImage closeableImage) {
            this.mProducerContext.setExtra(ProducerContext.ExtraKeys.ENCODED_WIDTH, Integer.valueOf(encodedImage.getWidth()));
            this.mProducerContext.setExtra(ProducerContext.ExtraKeys.ENCODED_HEIGHT, Integer.valueOf(encodedImage.getHeight()));
            this.mProducerContext.setExtra(ProducerContext.ExtraKeys.ENCODED_SIZE, Integer.valueOf(encodedImage.getSize()));
            if (closeableImage instanceof CloseableBitmap) {
                Bitmap underlyingBitmap = ((CloseableBitmap) closeableImage).getUnderlyingBitmap();
                this.mProducerContext.setExtra("bitmap_config", String.valueOf(underlyingBitmap == null ? null : underlyingBitmap.getConfig()));
            }
            if (closeableImage != null) {
                closeableImage.setImageExtras(this.mProducerContext.getExtras());
            }
        }

        protected abstract int getIntermediateImageEndOffset(EncodedImage encodedImage);

        protected abstract QualityInfo getQualityInfo();

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        public void onCancellationImpl() {
            handleCancellation();
        }

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        public void onFailureImpl(Throwable th) {
            handleError(th);
        }

        @Override // com.facebook.imagepipeline.producers.BaseConsumer
        public void onNewResultImpl(EncodedImage encodedImage, int i) {
            boolean zIsTracing;
            try {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.beginSection("DecodeProducer#onNewResultImpl");
                }
                boolean zIsLast = isLast(i);
                if (zIsLast) {
                    if (encodedImage == null) {
                        handleError(new ExceptionWithNoStacktrace("Encoded image is null."));
                        if (zIsTracing) {
                            return;
                        } else {
                            return;
                        }
                    } else if (!encodedImage.isValid()) {
                        handleError(new ExceptionWithNoStacktrace("Encoded image is not valid."));
                        if (zIsTracing) {
                            return;
                        } else {
                            return;
                        }
                    }
                }
                if (!updateDecodeJob(encodedImage, i)) {
                    if (zIsTracing) {
                        return;
                    } else {
                        return;
                    }
                }
                boolean zStatusHasFlag = statusHasFlag(i, 4);
                if (zIsLast || zStatusHasFlag || this.mProducerContext.isIntermediateResultExpected()) {
                    this.mJobScheduler.scheduleJob();
                }
            } finally {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
            }
        }

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        protected void onProgressUpdateImpl(float f) {
            super.onProgressUpdateImpl(f * 0.99f);
        }

        protected boolean updateDecodeJob(EncodedImage encodedImage, int i) {
            return this.mJobScheduler.updateJob(encodedImage, i);
        }
    }

    public DecodeProducer(ByteArrayPool byteArrayPool, Executor executor, ImageDecoder imageDecoder, ProgressiveJpegConfig progressiveJpegConfig, boolean z, boolean z2, boolean z3, Producer<EncodedImage> producer, int i, CloseableReferenceFactory closeableReferenceFactory, @Nullable Runnable runnable, Supplier<Boolean> supplier) {
        this.mByteArrayPool = (ByteArrayPool) Preconditions.checkNotNull(byteArrayPool);
        this.mExecutor = (Executor) Preconditions.checkNotNull(executor);
        this.mImageDecoder = (ImageDecoder) Preconditions.checkNotNull(imageDecoder);
        this.mProgressiveJpegConfig = (ProgressiveJpegConfig) Preconditions.checkNotNull(progressiveJpegConfig);
        this.mDownsampleEnabled = z;
        this.mDownsampleEnabledForNetwork = z2;
        this.mInputProducer = (Producer) Preconditions.checkNotNull(producer);
        this.mDecodeCancellationEnabled = z3;
        this.mMaxBitmapSize = i;
        this.mCloseableReferenceFactory = closeableReferenceFactory;
        this.mReclaimMemoryRunnable = runnable;
        this.mRecoverFromDecoderOOM = supplier;
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("DecodeProducer#produceResults");
            }
            this.mInputProducer.produceResults(!UriUtil.isNetworkUri(producerContext.getImageRequest().getSourceUri()) ? new LocalImagesProgressiveDecoder(consumer, producerContext, this.mDecodeCancellationEnabled, this.mMaxBitmapSize) : new NetworkImagesProgressiveDecoder(consumer, producerContext, new ProgressiveJpegParser(this.mByteArrayPool), this.mProgressiveJpegConfig, this.mDecodeCancellationEnabled, this.mMaxBitmapSize), producerContext);
        } finally {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
    }
}
