package com.facebook.drawee.backends.pipeline;

import android.content.Context;
import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.internal.ImmutableList;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Suppliers;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.info.ImageOriginListener;
import com.facebook.drawee.backends.pipeline.info.ImagePerfDataListener;
import com.facebook.drawee.controller.AbstractDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.fresco.ui.common.ControllerListener2;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.drawable.DrawableFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import java.util.Set;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class PipelineDraweeControllerBuilder extends AbstractDraweeControllerBuilder<PipelineDraweeControllerBuilder, ImageRequest, CloseableReference<CloseableImage>, ImageInfo> {

    @Nullable
    private ImmutableList<DrawableFactory> mCustomDrawableFactories;

    @Nullable
    private ImageOriginListener mImageOriginListener;

    @Nullable
    private ImagePerfDataListener mImagePerfDataListener;
    private final ImagePipeline mImagePipeline;
    private final PipelineDraweeControllerFactory mPipelineDraweeControllerFactory;

    public PipelineDraweeControllerBuilder(Context context, PipelineDraweeControllerFactory pipelineDraweeControllerFactory, ImagePipeline imagePipeline, Set<ControllerListener> set, Set<ControllerListener2> set2) {
        super(context, set, set2);
        this.mImagePipeline = imagePipeline;
        this.mPipelineDraweeControllerFactory = pipelineDraweeControllerFactory;
    }

    public static ImageRequest.RequestLevel convertCacheLevelToRequestLevel(AbstractDraweeControllerBuilder.CacheLevel cacheLevel) {
        switch (cacheLevel) {
            case FULL_FETCH:
                return ImageRequest.RequestLevel.FULL_FETCH;
            case DISK_CACHE:
                return ImageRequest.RequestLevel.DISK_CACHE;
            case BITMAP_MEMORY_CACHE:
                return ImageRequest.RequestLevel.BITMAP_MEMORY_CACHE;
            default:
                throw new RuntimeException("Cache level" + cacheLevel + "is not supported. ");
        }
    }

    @Nullable
    private CacheKey getCacheKey() {
        ImageRequest imageRequest = getImageRequest();
        CacheKeyFactory cacheKeyFactory = this.mImagePipeline.getCacheKeyFactory();
        if (cacheKeyFactory == null || imageRequest == null) {
            return null;
        }
        return imageRequest.getPostprocessor() != null ? cacheKeyFactory.getPostprocessedBitmapCacheKey(imageRequest, getCallerContext()) : cacheKeyFactory.getBitmapCacheKey(imageRequest, getCallerContext());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.drawee.controller.AbstractDraweeControllerBuilder
    public DataSource<CloseableReference<CloseableImage>> getDataSourceForRequest(DraweeController draweeController, String str, ImageRequest imageRequest, Object obj, AbstractDraweeControllerBuilder.CacheLevel cacheLevel) {
        return this.mImagePipeline.fetchDecodedImage(imageRequest, obj, convertCacheLevelToRequestLevel(cacheLevel), getRequestListener(draweeController), str);
    }

    @Nullable
    protected RequestListener getRequestListener(DraweeController draweeController) {
        if (draweeController instanceof PipelineDraweeController) {
            return ((PipelineDraweeController) draweeController).getRequestListener();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.facebook.drawee.controller.AbstractDraweeControllerBuilder
    public PipelineDraweeController obtainController() {
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("PipelineDraweeControllerBuilder#obtainController");
        }
        try {
            DraweeController oldController = getOldController();
            String strGenerateUniqueControllerId = generateUniqueControllerId();
            PipelineDraweeController pipelineDraweeControllerNewController = oldController instanceof PipelineDraweeController ? (PipelineDraweeController) oldController : this.mPipelineDraweeControllerFactory.newController();
            pipelineDraweeControllerNewController.initialize(obtainDataSourceSupplier(pipelineDraweeControllerNewController, strGenerateUniqueControllerId), strGenerateUniqueControllerId, getCacheKey(), getCallerContext(), this.mCustomDrawableFactories, this.mImageOriginListener);
            pipelineDraweeControllerNewController.initializePerformanceMonitoring(this.mImagePerfDataListener, this, Suppliers.BOOLEAN_FALSE);
            return pipelineDraweeControllerNewController;
        } finally {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
    }

    public PipelineDraweeControllerBuilder setCustomDrawableFactories(@Nullable ImmutableList<DrawableFactory> immutableList) {
        this.mCustomDrawableFactories = immutableList;
        return getThis();
    }

    public PipelineDraweeControllerBuilder setCustomDrawableFactories(DrawableFactory... drawableFactoryArr) {
        Preconditions.checkNotNull(drawableFactoryArr);
        return setCustomDrawableFactories(ImmutableList.of((Object[]) drawableFactoryArr));
    }

    public PipelineDraweeControllerBuilder setCustomDrawableFactory(DrawableFactory drawableFactory) {
        Preconditions.checkNotNull(drawableFactory);
        return setCustomDrawableFactories(ImmutableList.of((Object[]) new DrawableFactory[]{drawableFactory}));
    }

    public PipelineDraweeControllerBuilder setImageOriginListener(@Nullable ImageOriginListener imageOriginListener) {
        this.mImageOriginListener = imageOriginListener;
        return getThis();
    }

    public PipelineDraweeControllerBuilder setPerfDataListener(@Nullable ImagePerfDataListener imagePerfDataListener) {
        this.mImagePerfDataListener = imagePerfDataListener;
        return getThis();
    }

    @Override // com.facebook.drawee.interfaces.SimpleDraweeControllerBuilder
    public PipelineDraweeControllerBuilder setUri(@Nullable Uri uri) {
        return uri == null ? (PipelineDraweeControllerBuilder) super.setImageRequest(null) : (PipelineDraweeControllerBuilder) super.setImageRequest(ImageRequestBuilder.newBuilderWithSource(uri).setRotationOptions(RotationOptions.autoRotateAtRenderTime()).build());
    }

    @Override // com.facebook.drawee.interfaces.SimpleDraweeControllerBuilder
    public PipelineDraweeControllerBuilder setUri(@Nullable String str) {
        return (str == null || str.isEmpty()) ? (PipelineDraweeControllerBuilder) super.setImageRequest(ImageRequest.fromUri(str)) : setUri(Uri.parse(str));
    }
}
