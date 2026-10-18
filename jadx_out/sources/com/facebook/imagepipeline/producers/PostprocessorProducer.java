package com.facebook.imagepipeline.producers;

import android.graphics.Bitmap;
import androidx.annotation.VisibleForTesting;
import com.facebook.common.internal.ImmutableMap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.request.Postprocessor;
import com.facebook.imagepipeline.request.RepeatedPostprocessor;
import com.facebook.imagepipeline.request.RepeatedPostprocessorRunner;
import java.util.Map;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/* JADX INFO: loaded from: classes3.dex */
public class PostprocessorProducer implements Producer<CloseableReference<CloseableImage>> {
    public static final String NAME = "PostprocessorProducer";

    @VisibleForTesting
    static final String POSTPROCESSOR = "Postprocessor";
    private final PlatformBitmapFactory mBitmapFactory;
    private final Executor mExecutor;
    private final Producer<CloseableReference<CloseableImage>> mInputProducer;

    private class PostprocessorConsumer extends DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>> {

        @GuardedBy("PostprocessorConsumer.this")
        private boolean mIsClosed;

        @GuardedBy("PostprocessorConsumer.this")
        private boolean mIsDirty;

        @GuardedBy("PostprocessorConsumer.this")
        private boolean mIsPostProcessingRunning;
        private final ProducerListener2 mListener;
        private final Postprocessor mPostprocessor;
        private final ProducerContext mProducerContext;

        @GuardedBy("PostprocessorConsumer.this")
        @Nullable
        private CloseableReference<CloseableImage> mSourceImageRef;

        @GuardedBy("PostprocessorConsumer.this")
        private int mStatus;

        public PostprocessorConsumer(Consumer<CloseableReference<CloseableImage>> consumer, ProducerListener2 producerListener2, Postprocessor postprocessor, ProducerContext producerContext) {
            super(consumer);
            this.mSourceImageRef = null;
            this.mStatus = 0;
            this.mIsDirty = false;
            this.mIsPostProcessingRunning = false;
            this.mListener = producerListener2;
            this.mPostprocessor = postprocessor;
            this.mProducerContext = producerContext;
            producerContext.addCallbacks(new BaseProducerContextCallbacks() { // from class: com.facebook.imagepipeline.producers.PostprocessorProducer.PostprocessorConsumer.1
                @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
                public void onCancellationRequested() {
                    PostprocessorConsumer.this.maybeNotifyOnCancellation();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearRunningAndStartIfDirty() {
            boolean runningIfDirtyAndNotRunning;
            synchronized (this) {
                this.mIsPostProcessingRunning = false;
                runningIfDirtyAndNotRunning = setRunningIfDirtyAndNotRunning();
            }
            if (runningIfDirtyAndNotRunning) {
                submitPostprocessing();
            }
        }

        private boolean close() {
            synchronized (this) {
                if (this.mIsClosed) {
                    return false;
                }
                CloseableReference<CloseableImage> closeableReference = this.mSourceImageRef;
                this.mSourceImageRef = null;
                this.mIsClosed = true;
                CloseableReference.closeSafely(closeableReference);
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void doPostprocessing(CloseableReference<CloseableImage> closeableReference, int i) throws Throwable {
            Preconditions.checkArgument(Boolean.valueOf(CloseableReference.isValid(closeableReference)));
            if (!shouldPostprocess(closeableReference.get())) {
                maybeNotifyOnNewResult(closeableReference, i);
                return;
            }
            this.mListener.onProducerStart(this.mProducerContext, PostprocessorProducer.NAME);
            CloseableReference<CloseableImage> closeableReference2 = null;
            try {
                try {
                    CloseableReference<CloseableImage> closeableReferencePostprocessInternal = postprocessInternal(closeableReference.get());
                    try {
                        this.mListener.onProducerFinishWithSuccess(this.mProducerContext, PostprocessorProducer.NAME, getExtraMap(this.mListener, this.mProducerContext, this.mPostprocessor));
                        maybeNotifyOnNewResult(closeableReferencePostprocessInternal, i);
                        CloseableReference.closeSafely(closeableReferencePostprocessInternal);
                    } catch (Throwable th) {
                        th = th;
                        closeableReference2 = closeableReferencePostprocessInternal;
                        CloseableReference.closeSafely(closeableReference2);
                        throw th;
                    }
                } catch (Exception e) {
                    this.mListener.onProducerFinishWithFailure(this.mProducerContext, PostprocessorProducer.NAME, e, getExtraMap(this.mListener, this.mProducerContext, this.mPostprocessor));
                    maybeNotifyOnFailure(e);
                    CloseableReference.closeSafely((CloseableReference<?>) null);
                }
            } catch (Throwable th2) {
                th = th2;
            }
        }

        @Nullable
        private Map<String, String> getExtraMap(ProducerListener2 producerListener2, ProducerContext producerContext, Postprocessor postprocessor) {
            if (producerListener2.requiresExtraMap(producerContext, PostprocessorProducer.NAME)) {
                return ImmutableMap.of(PostprocessorProducer.POSTPROCESSOR, postprocessor.getName());
            }
            return null;
        }

        private synchronized boolean isClosed() {
            return this.mIsClosed;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void maybeNotifyOnCancellation() {
            if (close()) {
                getConsumer().onCancellation();
            }
        }

        private void maybeNotifyOnFailure(Throwable th) {
            if (close()) {
                getConsumer().onFailure(th);
            }
        }

        private void maybeNotifyOnNewResult(CloseableReference<CloseableImage> closeableReference, int i) {
            boolean zIsLast = isLast(i);
            if ((zIsLast || isClosed()) && !(zIsLast && close())) {
                return;
            }
            getConsumer().onNewResult(closeableReference, i);
        }

        private CloseableReference<CloseableImage> postprocessInternal(CloseableImage closeableImage) {
            CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) closeableImage;
            CloseableReference<Bitmap> closeableReferenceProcess = this.mPostprocessor.process(closeableStaticBitmap.getUnderlyingBitmap(), PostprocessorProducer.this.mBitmapFactory);
            try {
                CloseableStaticBitmap closeableStaticBitmap2 = new CloseableStaticBitmap(closeableReferenceProcess, closeableImage.getQualityInfo(), closeableStaticBitmap.getRotationAngle(), closeableStaticBitmap.getExifOrientation());
                closeableStaticBitmap2.setImageExtras(closeableStaticBitmap.getExtras());
                return CloseableReference.of(closeableStaticBitmap2);
            } finally {
                CloseableReference.closeSafely(closeableReferenceProcess);
            }
        }

        private synchronized boolean setRunningIfDirtyAndNotRunning() {
            if (this.mIsClosed || !this.mIsDirty || this.mIsPostProcessingRunning || !CloseableReference.isValid(this.mSourceImageRef)) {
                return false;
            }
            this.mIsPostProcessingRunning = true;
            return true;
        }

        private boolean shouldPostprocess(CloseableImage closeableImage) {
            return closeableImage instanceof CloseableStaticBitmap;
        }

        private void submitPostprocessing() {
            PostprocessorProducer.this.mExecutor.execute(new Runnable() { // from class: com.facebook.imagepipeline.producers.PostprocessorProducer.PostprocessorConsumer.2
                @Override // java.lang.Runnable
                public void run() {
                    CloseableReference closeableReference;
                    int i;
                    synchronized (PostprocessorConsumer.this) {
                        closeableReference = PostprocessorConsumer.this.mSourceImageRef;
                        i = PostprocessorConsumer.this.mStatus;
                        PostprocessorConsumer.this.mSourceImageRef = null;
                        PostprocessorConsumer.this.mIsDirty = false;
                    }
                    if (CloseableReference.isValid(closeableReference)) {
                        try {
                            PostprocessorConsumer.this.doPostprocessing(closeableReference, i);
                            CloseableReference.closeSafely((CloseableReference<?>) closeableReference);
                        } catch (Throwable th) {
                            CloseableReference.closeSafely((CloseableReference<?>) closeableReference);
                            throw th;
                        }
                    }
                    PostprocessorConsumer.this.clearRunningAndStartIfDirty();
                }
            });
        }

        private void updateSourceImageRef(@Nullable CloseableReference<CloseableImage> closeableReference, int i) {
            synchronized (this) {
                if (this.mIsClosed) {
                    return;
                }
                CloseableReference<CloseableImage> closeableReference2 = this.mSourceImageRef;
                this.mSourceImageRef = CloseableReference.cloneOrNull(closeableReference);
                this.mStatus = i;
                this.mIsDirty = true;
                boolean runningIfDirtyAndNotRunning = setRunningIfDirtyAndNotRunning();
                CloseableReference.closeSafely(closeableReference2);
                if (runningIfDirtyAndNotRunning) {
                    submitPostprocessing();
                }
            }
        }

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        protected void onCancellationImpl() {
            maybeNotifyOnCancellation();
        }

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        protected void onFailureImpl(Throwable th) {
            maybeNotifyOnFailure(th);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.facebook.imagepipeline.producers.BaseConsumer
        public void onNewResultImpl(CloseableReference<CloseableImage> closeableReference, int i) {
            if (CloseableReference.isValid(closeableReference)) {
                updateSourceImageRef(closeableReference, i);
            } else if (isLast(i)) {
                maybeNotifyOnNewResult(null, i);
            }
        }
    }

    class RepeatedPostprocessorConsumer extends DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>> implements RepeatedPostprocessorRunner {

        @GuardedBy("RepeatedPostprocessorConsumer.this")
        private boolean mIsClosed;

        @GuardedBy("RepeatedPostprocessorConsumer.this")
        @Nullable
        private CloseableReference<CloseableImage> mSourceImageRef;

        private RepeatedPostprocessorConsumer(PostprocessorConsumer postprocessorConsumer, RepeatedPostprocessor repeatedPostprocessor, ProducerContext producerContext) {
            super(postprocessorConsumer);
            this.mIsClosed = false;
            this.mSourceImageRef = null;
            repeatedPostprocessor.setCallback(this);
            producerContext.addCallbacks(new BaseProducerContextCallbacks() { // from class: com.facebook.imagepipeline.producers.PostprocessorProducer.RepeatedPostprocessorConsumer.1
                @Override // com.facebook.imagepipeline.producers.BaseProducerContextCallbacks, com.facebook.imagepipeline.producers.ProducerContextCallbacks
                public void onCancellationRequested() {
                    if (RepeatedPostprocessorConsumer.this.close()) {
                        RepeatedPostprocessorConsumer.this.getConsumer().onCancellation();
                    }
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean close() {
            synchronized (this) {
                if (this.mIsClosed) {
                    return false;
                }
                CloseableReference<CloseableImage> closeableReference = this.mSourceImageRef;
                this.mSourceImageRef = null;
                this.mIsClosed = true;
                CloseableReference.closeSafely(closeableReference);
                return true;
            }
        }

        private void setSourceImageRef(CloseableReference<CloseableImage> closeableReference) {
            synchronized (this) {
                if (this.mIsClosed) {
                    return;
                }
                CloseableReference<CloseableImage> closeableReference2 = this.mSourceImageRef;
                this.mSourceImageRef = CloseableReference.cloneOrNull(closeableReference);
                CloseableReference.closeSafely(closeableReference2);
            }
        }

        private void updateInternal() {
            synchronized (this) {
                if (this.mIsClosed) {
                    return;
                }
                CloseableReference<CloseableImage> closeableReferenceCloneOrNull = CloseableReference.cloneOrNull(this.mSourceImageRef);
                try {
                    getConsumer().onNewResult(closeableReferenceCloneOrNull, 0);
                } finally {
                    CloseableReference.closeSafely(closeableReferenceCloneOrNull);
                }
            }
        }

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        protected void onCancellationImpl() {
            if (close()) {
                getConsumer().onCancellation();
            }
        }

        @Override // com.facebook.imagepipeline.producers.DelegatingConsumer, com.facebook.imagepipeline.producers.BaseConsumer
        protected void onFailureImpl(Throwable th) {
            if (close()) {
                getConsumer().onFailure(th);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.facebook.imagepipeline.producers.BaseConsumer
        public void onNewResultImpl(CloseableReference<CloseableImage> closeableReference, int i) {
            if (isNotLast(i)) {
                return;
            }
            setSourceImageRef(closeableReference);
            updateInternal();
        }

        @Override // com.facebook.imagepipeline.request.RepeatedPostprocessorRunner
        public synchronized void update() {
            updateInternal();
        }
    }

    class SingleUsePostprocessorConsumer extends DelegatingConsumer<CloseableReference<CloseableImage>, CloseableReference<CloseableImage>> {
        private SingleUsePostprocessorConsumer(PostprocessorConsumer postprocessorConsumer) {
            super(postprocessorConsumer);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.facebook.imagepipeline.producers.BaseConsumer
        public void onNewResultImpl(CloseableReference<CloseableImage> closeableReference, int i) {
            if (isNotLast(i)) {
                return;
            }
            getConsumer().onNewResult(closeableReference, i);
        }
    }

    public PostprocessorProducer(Producer<CloseableReference<CloseableImage>> producer, PlatformBitmapFactory platformBitmapFactory, Executor executor) {
        this.mInputProducer = (Producer) Preconditions.checkNotNull(producer);
        this.mBitmapFactory = platformBitmapFactory;
        this.mExecutor = (Executor) Preconditions.checkNotNull(executor);
    }

    @Override // com.facebook.imagepipeline.producers.Producer
    public void produceResults(Consumer<CloseableReference<CloseableImage>> consumer, ProducerContext producerContext) {
        ProducerListener2 producerListener = producerContext.getProducerListener();
        Postprocessor postprocessor = producerContext.getImageRequest().getPostprocessor();
        PostprocessorConsumer postprocessorConsumer = new PostprocessorConsumer(consumer, producerListener, postprocessor, producerContext);
        this.mInputProducer.produceResults(postprocessor instanceof RepeatedPostprocessor ? new RepeatedPostprocessorConsumer(postprocessorConsumer, (RepeatedPostprocessor) postprocessor, producerContext) : new SingleUsePostprocessorConsumer(postprocessorConsumer), producerContext);
    }
}
