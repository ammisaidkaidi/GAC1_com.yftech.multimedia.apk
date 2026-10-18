package com.facebook.drawee.backends.pipeline;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableList;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.time.AwakeTimeSinceBootClock;
import com.facebook.datasource.DataSource;
import com.facebook.drawable.base.DrawableWithCaches;
import com.facebook.drawee.backends.pipeline.debug.DebugOverlayImageOriginColor;
import com.facebook.drawee.backends.pipeline.debug.DebugOverlayImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ForwardingImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ImageOriginRequestListener;
import com.facebook.drawee.backends.pipeline.info.ImageOriginUtils;
import com.facebook.drawee.backends.pipeline.info.ImagePerfDataListener;
import com.facebook.drawee.backends.pipeline.info.ImagePerfMonitor;
import com.facebook.drawee.components.DeferredReleaser;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.debug.DebugControllerOverlayDrawable;
import com.facebook.drawee.debug.listener.ImageLoadingTimeControllerListener;
import com.facebook.drawee.drawable.ScaleTypeDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.interfaces.DraweeHierarchy;
import com.facebook.fresco.ui.common.MultiUriHelper;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.drawable.DrawableFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.listener.ForwardingRequestListener;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

/* JADX INFO: loaded from: classes3.dex */
public class PipelineDraweeController extends AbstractDraweeController<CloseableReference<CloseableImage>, ImageInfo> {
    private static final Class<?> TAG = PipelineDraweeController.class;
    private CacheKey mCacheKey;

    @Nullable
    private ImmutableList<DrawableFactory> mCustomDrawableFactories;
    private Supplier<DataSource<CloseableReference<CloseableImage>>> mDataSourceSupplier;
    private DebugOverlayImageOriginListener mDebugOverlayImageOriginListener;
    private final DrawableFactory mDefaultDrawableFactory;
    private boolean mDrawDebugOverlay;

    @Nullable
    private ImageRequest[] mFirstAvailableImageRequests;

    @Nullable
    private final ImmutableList<DrawableFactory> mGlobalDrawableFactories;

    @GuardedBy("this")
    @Nullable
    private ImageOriginListener mImageOriginListener;

    @Nullable
    private ImagePerfMonitor mImagePerfMonitor;

    @Nullable
    private ImageRequest mImageRequest;

    @Nullable
    private ImageRequest mLowResImageRequest;

    @Nullable
    private final MemoryCache<CacheKey, CloseableImage> mMemoryCache;

    @GuardedBy("this")
    @Nullable
    private Set<RequestListener> mRequestListeners;
    private final Resources mResources;

    public PipelineDraweeController(Resources resources, DeferredReleaser deferredReleaser, DrawableFactory drawableFactory, Executor executor, @Nullable MemoryCache<CacheKey, CloseableImage> memoryCache, @Nullable ImmutableList<DrawableFactory> immutableList) {
        super(deferredReleaser, executor, null, null);
        this.mResources = resources;
        this.mDefaultDrawableFactory = new DefaultDrawableFactory(resources, drawableFactory);
        this.mGlobalDrawableFactories = immutableList;
        this.mMemoryCache = memoryCache;
    }

    private void init(Supplier<DataSource<CloseableReference<CloseableImage>>> supplier) {
        this.mDataSourceSupplier = supplier;
        maybeUpdateDebugOverlay(null);
    }

    @Nullable
    private Drawable maybeCreateDrawableFromFactories(@Nullable ImmutableList<DrawableFactory> immutableList, CloseableImage closeableImage) {
        Drawable drawableCreateDrawable;
        if (immutableList == null) {
            return null;
        }
        for (DrawableFactory drawableFactory : immutableList) {
            if (drawableFactory.supportsImageType(closeableImage) && (drawableCreateDrawable = drawableFactory.createDrawable(closeableImage)) != null) {
                return drawableCreateDrawable;
            }
        }
        return null;
    }

    private void maybeUpdateDebugOverlay(@Nullable CloseableImage closeableImage) {
        if (this.mDrawDebugOverlay) {
            if (getControllerOverlay() == null) {
                DebugControllerOverlayDrawable debugControllerOverlayDrawable = new DebugControllerOverlayDrawable();
                ImageLoadingTimeControllerListener imageLoadingTimeControllerListener = new ImageLoadingTimeControllerListener(debugControllerOverlayDrawable);
                this.mDebugOverlayImageOriginListener = new DebugOverlayImageOriginListener();
                addControllerListener(imageLoadingTimeControllerListener);
                setControllerOverlay(debugControllerOverlayDrawable);
            }
            if (this.mImageOriginListener == null) {
                addImageOriginListener(this.mDebugOverlayImageOriginListener);
            }
            if (getControllerOverlay() instanceof DebugControllerOverlayDrawable) {
                updateDebugOverlay(closeableImage, (DebugControllerOverlayDrawable) getControllerOverlay());
            }
        }
    }

    public synchronized void addImageOriginListener(ImageOriginListener imageOriginListener) {
        if (this.mImageOriginListener instanceof ForwardingImageOriginListener) {
            ((ForwardingImageOriginListener) this.mImageOriginListener).addImageOriginListener(imageOriginListener);
        } else if (this.mImageOriginListener != null) {
            this.mImageOriginListener = new ForwardingImageOriginListener(this.mImageOriginListener, imageOriginListener);
        } else {
            this.mImageOriginListener = imageOriginListener;
        }
    }

    public synchronized void addRequestListener(RequestListener requestListener) {
        if (this.mRequestListeners == null) {
            this.mRequestListeners = new HashSet();
        }
        this.mRequestListeners.add(requestListener);
    }

    protected void clearImageOriginListeners() {
        synchronized (this) {
            this.mImageOriginListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.drawee.controller.AbstractDraweeController
    public Drawable createDrawable(CloseableReference<CloseableImage> closeableReference) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("PipelineDraweeController#createDrawable");
            }
            Preconditions.checkState(CloseableReference.isValid(closeableReference));
            CloseableImage closeableImage = closeableReference.get();
            maybeUpdateDebugOverlay(closeableImage);
            Drawable drawableMaybeCreateDrawableFromFactories = maybeCreateDrawableFromFactories(this.mCustomDrawableFactories, closeableImage);
            if (drawableMaybeCreateDrawableFromFactories != null) {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
                return drawableMaybeCreateDrawableFromFactories;
            }
            Drawable drawableMaybeCreateDrawableFromFactories2 = maybeCreateDrawableFromFactories(this.mGlobalDrawableFactories, closeableImage);
            if (drawableMaybeCreateDrawableFromFactories2 != null) {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
                return drawableMaybeCreateDrawableFromFactories2;
            }
            Drawable drawableCreateDrawable = this.mDefaultDrawableFactory.createDrawable(closeableImage);
            if (drawableCreateDrawable != null) {
                if (FrescoSystrace.isTracing()) {
                    FrescoSystrace.endSection();
                }
                return drawableCreateDrawable;
            }
            throw new UnsupportedOperationException("Unrecognized image class: " + closeableImage);
        } catch (Throwable th) {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
            throw th;
        }
    }

    protected CacheKey getCacheKey() {
        return this.mCacheKey;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.facebook.drawee.controller.AbstractDraweeController
    @Nullable
    public CloseableReference<CloseableImage> getCachedImage() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("PipelineDraweeController#getCachedImage");
        }
        try {
            if (this.mMemoryCache != null && this.mCacheKey != null) {
                CloseableReference<CloseableImage> closeableReference = this.mMemoryCache.get(this.mCacheKey);
                if (closeableReference == null || closeableReference.get().getQualityInfo().isOfFullQuality()) {
                    return closeableReference;
                }
                closeableReference.close();
                return null;
            }
            return null;
        } finally {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
    }

    @Override // com.facebook.drawee.controller.AbstractDraweeController
    protected DataSource<CloseableReference<CloseableImage>> getDataSource() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("PipelineDraweeController#getDataSource");
        }
        if (FLog.isLoggable(2)) {
            FLog.v(TAG, "controller %x: getDataSource", Integer.valueOf(System.identityHashCode(this)));
        }
        DataSource<CloseableReference<CloseableImage>> dataSource = this.mDataSourceSupplier.get();
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        return dataSource;
    }

    protected Supplier<DataSource<CloseableReference<CloseableImage>>> getDataSourceSupplier() {
        return this.mDataSourceSupplier;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.drawee.controller.AbstractDraweeController
    public int getImageHash(@Nullable CloseableReference<CloseableImage> closeableReference) {
        if (closeableReference != null) {
            return closeableReference.getValueHash();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.drawee.controller.AbstractDraweeController
    public ImageInfo getImageInfo(CloseableReference<CloseableImage> closeableReference) {
        Preconditions.checkState(CloseableReference.isValid(closeableReference));
        return closeableReference.get();
    }

    @Override // com.facebook.drawee.controller.AbstractDraweeController
    @Nullable
    protected Uri getMainUri() {
        return MultiUriHelper.getMainUri(this.mImageRequest, this.mLowResImageRequest, this.mFirstAvailableImageRequests, ImageRequest.REQUEST_TO_URI_FN);
    }

    @Nullable
    public synchronized RequestListener getRequestListener() {
        ImageOriginRequestListener imageOriginRequestListener = this.mImageOriginListener != null ? new ImageOriginRequestListener(getId(), this.mImageOriginListener) : null;
        if (this.mRequestListeners == null) {
            return imageOriginRequestListener;
        }
        ForwardingRequestListener forwardingRequestListener = new ForwardingRequestListener(this.mRequestListeners);
        if (imageOriginRequestListener != null) {
            forwardingRequestListener.addRequestListener(imageOriginRequestListener);
        }
        return forwardingRequestListener;
    }

    protected Resources getResources() {
        return this.mResources;
    }

    public void initialize(Supplier<DataSource<CloseableReference<CloseableImage>>> supplier, String str, CacheKey cacheKey, Object obj, @Nullable ImmutableList<DrawableFactory> immutableList, @Nullable ImageOriginListener imageOriginListener) {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("PipelineDraweeController#initialize");
        }
        super.initialize(str, obj);
        init(supplier);
        this.mCacheKey = cacheKey;
        setCustomDrawableFactories(immutableList);
        clearImageOriginListeners();
        maybeUpdateDebugOverlay(null);
        addImageOriginListener(imageOriginListener);
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
    }

    protected synchronized void initializePerformanceMonitoring(@Nullable ImagePerfDataListener imagePerfDataListener, AbstractDraweeControllerBuilder<PipelineDraweeControllerBuilder, ImageRequest, CloseableReference<CloseableImage>, ImageInfo> abstractDraweeControllerBuilder, Supplier<Boolean> supplier) {
        if (this.mImagePerfMonitor != null) {
            this.mImagePerfMonitor.reset();
        }
        if (imagePerfDataListener != null) {
            if (this.mImagePerfMonitor == null) {
                this.mImagePerfMonitor = new ImagePerfMonitor(AwakeTimeSinceBootClock.get(), this, supplier);
            }
            this.mImagePerfMonitor.addImagePerfDataListener(imagePerfDataListener);
            this.mImagePerfMonitor.setEnabled(true);
            this.mImagePerfMonitor.updateImageRequestData(abstractDraweeControllerBuilder);
        }
        this.mImageRequest = abstractDraweeControllerBuilder.getImageRequest();
        this.mFirstAvailableImageRequests = abstractDraweeControllerBuilder.getFirstAvailableImageRequests();
        this.mLowResImageRequest = abstractDraweeControllerBuilder.getLowResImageRequest();
    }

    @Override // com.facebook.drawee.interfaces.DraweeController
    public boolean isSameImageRequest(@Nullable DraweeController draweeController) {
        if (this.mCacheKey == null || !(draweeController instanceof PipelineDraweeController)) {
            return false;
        }
        return Objects.equal(this.mCacheKey, ((PipelineDraweeController) draweeController).getCacheKey());
    }

    @Override // com.facebook.drawee.controller.AbstractDraweeController
    @Nullable
    public Map<String, Object> obtainExtrasFromImage(ImageInfo imageInfo) {
        if (imageInfo == null) {
            return null;
        }
        return imageInfo.getExtras();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.drawee.controller.AbstractDraweeController
    public void onImageLoadedFromCacheImmediately(String str, CloseableReference<CloseableImage> closeableReference) {
        super.onImageLoadedFromCacheImmediately(str, closeableReference);
        synchronized (this) {
            if (this.mImageOriginListener != null) {
                this.mImageOriginListener.onImageLoaded(str, 6, true, "PipelineDraweeController");
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.facebook.drawee.controller.AbstractDraweeController
    protected void releaseDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof DrawableWithCaches) {
            ((DrawableWithCaches) drawable).dropCaches();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.drawee.controller.AbstractDraweeController
    public void releaseImage(@Nullable CloseableReference<CloseableImage> closeableReference) {
        CloseableReference.closeSafely(closeableReference);
    }

    public synchronized void removeImageOriginListener(ImageOriginListener imageOriginListener) {
        if (this.mImageOriginListener instanceof ForwardingImageOriginListener) {
            ((ForwardingImageOriginListener) this.mImageOriginListener).removeImageOriginListener(imageOriginListener);
        } else {
            if (this.mImageOriginListener == imageOriginListener) {
                this.mImageOriginListener = null;
            }
        }
    }

    public synchronized void removeRequestListener(RequestListener requestListener) {
        if (this.mRequestListeners == null) {
            return;
        }
        this.mRequestListeners.remove(requestListener);
    }

    public void setCustomDrawableFactories(@Nullable ImmutableList<DrawableFactory> immutableList) {
        this.mCustomDrawableFactories = immutableList;
    }

    public void setDrawDebugOverlay(boolean z) {
        this.mDrawDebugOverlay = z;
    }

    @Override // com.facebook.drawee.controller.AbstractDraweeController, com.facebook.drawee.interfaces.DraweeController
    public void setHierarchy(@Nullable DraweeHierarchy draweeHierarchy) {
        super.setHierarchy(draweeHierarchy);
        maybeUpdateDebugOverlay(null);
    }

    @Override // com.facebook.drawee.controller.AbstractDraweeController
    public String toString() {
        return Objects.toStringHelper(this).add("super", super.toString()).add("dataSourceSupplier", this.mDataSourceSupplier).toString();
    }

    protected void updateDebugOverlay(@Nullable CloseableImage closeableImage, DebugControllerOverlayDrawable debugControllerOverlayDrawable) {
        ScaleTypeDrawable activeScaleTypeDrawable;
        debugControllerOverlayDrawable.setControllerId(getId());
        DraweeHierarchy hierarchy = getHierarchy();
        ScalingUtils.ScaleType scaleType = null;
        if (hierarchy != null && (activeScaleTypeDrawable = ScalingUtils.getActiveScaleTypeDrawable(hierarchy.getTopLevelDrawable())) != null) {
            scaleType = activeScaleTypeDrawable.getScaleType();
        }
        debugControllerOverlayDrawable.setScaleType(scaleType);
        int imageOrigin = this.mDebugOverlayImageOriginListener.getImageOrigin();
        debugControllerOverlayDrawable.setOrigin(ImageOriginUtils.toString(imageOrigin), DebugOverlayImageOriginColor.getImageOriginColor(imageOrigin));
        if (closeableImage == null) {
            debugControllerOverlayDrawable.reset();
        } else {
            debugControllerOverlayDrawable.setDimensions(closeableImage.getWidth(), closeableImage.getHeight());
            debugControllerOverlayDrawable.setImageSize(closeableImage.getSizeInBytes());
        }
    }
}
