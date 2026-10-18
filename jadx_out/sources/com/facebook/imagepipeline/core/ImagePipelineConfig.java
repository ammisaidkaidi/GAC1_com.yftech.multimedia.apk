package com.facebook.imagepipeline.core;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import androidx.annotation.VisibleForTesting;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.callercontext.CallerContextVerifier;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Supplier;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.common.memory.PooledByteBuffer;
import com.facebook.common.webp.BitmapCreator;
import com.facebook.common.webp.WebpBitmapFactory;
import com.facebook.common.webp.WebpSupportStatus;
import com.facebook.imagepipeline.bitmaps.HoneycombBitmapCreator;
import com.facebook.imagepipeline.bitmaps.PlatformBitmapFactory;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheFactory;
import com.facebook.imagepipeline.cache.BitmapMemoryCacheTrimStrategy;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.CountingLruBitmapMemoryCacheFactory;
import com.facebook.imagepipeline.cache.CountingMemoryCache;
import com.facebook.imagepipeline.cache.DefaultBitmapMemoryCacheParamsSupplier;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.cache.DefaultEncodedMemoryCacheParamsSupplier;
import com.facebook.imagepipeline.cache.ImageCacheStatsTracker;
import com.facebook.imagepipeline.cache.MemoryCache;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.cache.NoOpImageCacheStatsTracker;
import com.facebook.imagepipeline.debug.CloseableReferenceLeakTracker;
import com.facebook.imagepipeline.debug.NoOpCloseableReferenceLeakTracker;
import com.facebook.imagepipeline.decoder.ImageDecoder;
import com.facebook.imagepipeline.decoder.ImageDecoderConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestListener2;
import com.facebook.imagepipeline.memory.PoolConfig;
import com.facebook.imagepipeline.memory.PoolFactory;
import com.facebook.imagepipeline.producers.HttpUrlConnectionNetworkFetcher;
import com.facebook.imagepipeline.producers.NetworkFetcher;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.imagepipeline.transcoder.ImageTranscoderFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
public class ImagePipelineConfig {
    private static DefaultImageRequestConfig sDefaultImageRequestConfig = new DefaultImageRequestConfig();

    @Nullable
    private final MemoryCache<CacheKey, CloseableImage> mBitmapCache;
    private final Bitmap.Config mBitmapConfig;

    @Nullable
    private final CountingMemoryCache.EntryStateObserver<CacheKey> mBitmapMemoryCacheEntryStateObserver;
    private final BitmapMemoryCacheFactory mBitmapMemoryCacheFactory;
    private final Supplier<MemoryCacheParams> mBitmapMemoryCacheParamsSupplier;
    private final MemoryCache.CacheTrimStrategy mBitmapMemoryCacheTrimStrategy;
    private final CacheKeyFactory mCacheKeyFactory;

    @Nullable
    private final CallerContextVerifier mCallerContextVerifier;
    private final CloseableReferenceLeakTracker mCloseableReferenceLeakTracker;
    private final Context mContext;
    private final boolean mDiskCacheEnabled;
    private final boolean mDownsampleEnabled;

    @Nullable
    private final MemoryCache<CacheKey, PooledByteBuffer> mEncodedMemoryCache;
    private final Supplier<MemoryCacheParams> mEncodedMemoryCacheParamsSupplier;
    private final ExecutorSupplier mExecutorSupplier;
    private final FileCacheFactory mFileCacheFactory;
    private final int mHttpNetworkTimeout;
    private final ImageCacheStatsTracker mImageCacheStatsTracker;

    @Nullable
    private final ImageDecoder mImageDecoder;

    @Nullable
    private final ImageDecoderConfig mImageDecoderConfig;
    private final ImagePipelineExperiments mImagePipelineExperiments;

    @Nullable
    private final ImageTranscoderFactory mImageTranscoderFactory;

    @Nullable
    private final Integer mImageTranscoderType;
    private final Supplier<Boolean> mIsPrefetchEnabledSupplier;
    private final DiskCacheConfig mMainDiskCacheConfig;
    private final int mMemoryChunkType;
    private final MemoryTrimmableRegistry mMemoryTrimmableRegistry;
    private final NetworkFetcher mNetworkFetcher;

    @Nullable
    private final PlatformBitmapFactory mPlatformBitmapFactory;
    private final PoolFactory mPoolFactory;
    private final ProgressiveJpegConfig mProgressiveJpegConfig;
    private final Set<RequestListener2> mRequestListener2s;
    private final Set<RequestListener> mRequestListeners;
    private final boolean mResizeAndRotateEnabledForNetwork;
    private final DiskCacheConfig mSmallImageDiskCacheConfig;

    public static class Builder {

        @Nullable
        private Bitmap.Config mBitmapConfig;

        @Nullable
        private MemoryCache<CacheKey, CloseableImage> mBitmapMemoryCache;

        @Nullable
        private CountingMemoryCache.EntryStateObserver<CacheKey> mBitmapMemoryCacheEntryStateObserver;

        @Nullable
        private BitmapMemoryCacheFactory mBitmapMemoryCacheFactory;

        @Nullable
        private Supplier<MemoryCacheParams> mBitmapMemoryCacheParamsSupplier;

        @Nullable
        private MemoryCache.CacheTrimStrategy mBitmapMemoryCacheTrimStrategy;

        @Nullable
        private CacheKeyFactory mCacheKeyFactory;

        @Nullable
        private CallerContextVerifier mCallerContextVerifier;
        private CloseableReferenceLeakTracker mCloseableReferenceLeakTracker;
        private final Context mContext;
        private boolean mDiskCacheEnabled;
        private boolean mDownsampleEnabled;

        @Nullable
        private MemoryCache<CacheKey, PooledByteBuffer> mEncodedMemoryCache;

        @Nullable
        private Supplier<MemoryCacheParams> mEncodedMemoryCacheParamsSupplier;

        @Nullable
        private ExecutorSupplier mExecutorSupplier;
        private final ImagePipelineExperiments.Builder mExperimentsBuilder;

        @Nullable
        private FileCacheFactory mFileCacheFactory;
        private int mHttpConnectionTimeout;

        @Nullable
        private ImageCacheStatsTracker mImageCacheStatsTracker;

        @Nullable
        private ImageDecoder mImageDecoder;

        @Nullable
        private ImageDecoderConfig mImageDecoderConfig;

        @Nullable
        private ImageTranscoderFactory mImageTranscoderFactory;

        @Nullable
        private Integer mImageTranscoderType;

        @Nullable
        private Supplier<Boolean> mIsPrefetchEnabledSupplier;

        @Nullable
        private DiskCacheConfig mMainDiskCacheConfig;

        @Nullable
        private Integer mMemoryChunkType;

        @Nullable
        private MemoryTrimmableRegistry mMemoryTrimmableRegistry;

        @Nullable
        private NetworkFetcher mNetworkFetcher;

        @Nullable
        private PlatformBitmapFactory mPlatformBitmapFactory;

        @Nullable
        private PoolFactory mPoolFactory;

        @Nullable
        private ProgressiveJpegConfig mProgressiveJpegConfig;

        @Nullable
        private Set<RequestListener2> mRequestListener2s;

        @Nullable
        private Set<RequestListener> mRequestListeners;
        private boolean mResizeAndRotateEnabledForNetwork;

        @Nullable
        private DiskCacheConfig mSmallImageDiskCacheConfig;

        private Builder(Context context) {
            this.mDownsampleEnabled = false;
            this.mImageTranscoderType = null;
            this.mMemoryChunkType = null;
            this.mResizeAndRotateEnabledForNetwork = true;
            this.mHttpConnectionTimeout = -1;
            this.mExperimentsBuilder = new ImagePipelineExperiments.Builder(this);
            this.mDiskCacheEnabled = true;
            this.mCloseableReferenceLeakTracker = new NoOpCloseableReferenceLeakTracker();
            this.mContext = (Context) Preconditions.checkNotNull(context);
        }

        public ImagePipelineConfig build() {
            return new ImagePipelineConfig(this);
        }

        public ImagePipelineExperiments.Builder experiment() {
            return this.mExperimentsBuilder;
        }

        @Nullable
        public BitmapMemoryCacheFactory getBitmapMemoryCacheFactory() {
            return this.mBitmapMemoryCacheFactory;
        }

        @Nullable
        public Integer getImageTranscoderType() {
            return this.mImageTranscoderType;
        }

        @Nullable
        public Integer getMemoryChunkType() {
            return this.mMemoryChunkType;
        }

        public boolean isDiskCacheEnabled() {
            return this.mDiskCacheEnabled;
        }

        public boolean isDownsampleEnabled() {
            return this.mDownsampleEnabled;
        }

        public Builder setBitmapMemoryCache(@Nullable MemoryCache<CacheKey, CloseableImage> memoryCache) {
            this.mBitmapMemoryCache = memoryCache;
            return this;
        }

        public Builder setBitmapMemoryCacheEntryStateObserver(CountingMemoryCache.EntryStateObserver<CacheKey> entryStateObserver) {
            this.mBitmapMemoryCacheEntryStateObserver = entryStateObserver;
            return this;
        }

        public Builder setBitmapMemoryCacheFactory(@Nullable BitmapMemoryCacheFactory bitmapMemoryCacheFactory) {
            this.mBitmapMemoryCacheFactory = bitmapMemoryCacheFactory;
            return this;
        }

        public Builder setBitmapMemoryCacheParamsSupplier(Supplier<MemoryCacheParams> supplier) {
            this.mBitmapMemoryCacheParamsSupplier = (Supplier) Preconditions.checkNotNull(supplier);
            return this;
        }

        public Builder setBitmapMemoryCacheTrimStrategy(MemoryCache.CacheTrimStrategy cacheTrimStrategy) {
            this.mBitmapMemoryCacheTrimStrategy = cacheTrimStrategy;
            return this;
        }

        public Builder setBitmapsConfig(Bitmap.Config config) {
            this.mBitmapConfig = config;
            return this;
        }

        public Builder setCacheKeyFactory(CacheKeyFactory cacheKeyFactory) {
            this.mCacheKeyFactory = cacheKeyFactory;
            return this;
        }

        public Builder setCallerContextVerifier(CallerContextVerifier callerContextVerifier) {
            this.mCallerContextVerifier = callerContextVerifier;
            return this;
        }

        public Builder setCloseableReferenceLeakTracker(CloseableReferenceLeakTracker closeableReferenceLeakTracker) {
            this.mCloseableReferenceLeakTracker = closeableReferenceLeakTracker;
            return this;
        }

        public Builder setDiskCacheEnabled(boolean z) {
            this.mDiskCacheEnabled = z;
            return this;
        }

        public Builder setDownsampleEnabled(boolean z) {
            this.mDownsampleEnabled = z;
            return this;
        }

        public Builder setEncodedMemoryCache(@Nullable MemoryCache<CacheKey, PooledByteBuffer> memoryCache) {
            this.mEncodedMemoryCache = memoryCache;
            return this;
        }

        public Builder setEncodedMemoryCacheParamsSupplier(Supplier<MemoryCacheParams> supplier) {
            this.mEncodedMemoryCacheParamsSupplier = (Supplier) Preconditions.checkNotNull(supplier);
            return this;
        }

        public Builder setExecutorSupplier(ExecutorSupplier executorSupplier) {
            this.mExecutorSupplier = executorSupplier;
            return this;
        }

        public Builder setFileCacheFactory(FileCacheFactory fileCacheFactory) {
            this.mFileCacheFactory = fileCacheFactory;
            return this;
        }

        public Builder setHttpConnectionTimeout(int i) {
            this.mHttpConnectionTimeout = i;
            return this;
        }

        public Builder setImageCacheStatsTracker(ImageCacheStatsTracker imageCacheStatsTracker) {
            this.mImageCacheStatsTracker = imageCacheStatsTracker;
            return this;
        }

        public Builder setImageDecoder(ImageDecoder imageDecoder) {
            this.mImageDecoder = imageDecoder;
            return this;
        }

        public Builder setImageDecoderConfig(ImageDecoderConfig imageDecoderConfig) {
            this.mImageDecoderConfig = imageDecoderConfig;
            return this;
        }

        public Builder setImageTranscoderFactory(ImageTranscoderFactory imageTranscoderFactory) {
            this.mImageTranscoderFactory = imageTranscoderFactory;
            return this;
        }

        public Builder setImageTranscoderType(int i) {
            this.mImageTranscoderType = Integer.valueOf(i);
            return this;
        }

        public Builder setIsPrefetchEnabledSupplier(Supplier<Boolean> supplier) {
            this.mIsPrefetchEnabledSupplier = supplier;
            return this;
        }

        public Builder setMainDiskCacheConfig(DiskCacheConfig diskCacheConfig) {
            this.mMainDiskCacheConfig = diskCacheConfig;
            return this;
        }

        public Builder setMemoryChunkType(int i) {
            this.mMemoryChunkType = Integer.valueOf(i);
            return this;
        }

        public Builder setMemoryTrimmableRegistry(MemoryTrimmableRegistry memoryTrimmableRegistry) {
            this.mMemoryTrimmableRegistry = memoryTrimmableRegistry;
            return this;
        }

        public Builder setNetworkFetcher(NetworkFetcher networkFetcher) {
            this.mNetworkFetcher = networkFetcher;
            return this;
        }

        public Builder setPlatformBitmapFactory(PlatformBitmapFactory platformBitmapFactory) {
            this.mPlatformBitmapFactory = platformBitmapFactory;
            return this;
        }

        public Builder setPoolFactory(PoolFactory poolFactory) {
            this.mPoolFactory = poolFactory;
            return this;
        }

        public Builder setProgressiveJpegConfig(ProgressiveJpegConfig progressiveJpegConfig) {
            this.mProgressiveJpegConfig = progressiveJpegConfig;
            return this;
        }

        public Builder setRequestListener2s(Set<RequestListener2> set) {
            this.mRequestListener2s = set;
            return this;
        }

        public Builder setRequestListeners(Set<RequestListener> set) {
            this.mRequestListeners = set;
            return this;
        }

        public Builder setResizeAndRotateEnabledForNetwork(boolean z) {
            this.mResizeAndRotateEnabledForNetwork = z;
            return this;
        }

        public Builder setSmallImageDiskCacheConfig(DiskCacheConfig diskCacheConfig) {
            this.mSmallImageDiskCacheConfig = diskCacheConfig;
            return this;
        }
    }

    public static class DefaultImageRequestConfig {
        private boolean mProgressiveRenderingEnabled;

        private DefaultImageRequestConfig() {
            this.mProgressiveRenderingEnabled = false;
        }

        public boolean isProgressiveRenderingEnabled() {
            return this.mProgressiveRenderingEnabled;
        }

        public void setProgressiveRenderingEnabled(boolean z) {
            this.mProgressiveRenderingEnabled = z;
        }
    }

    private ImagePipelineConfig(Builder builder) {
        WebpBitmapFactory webpBitmapFactoryLoadWebpBitmapFactoryIfExists;
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ImagePipelineConfig()");
        }
        this.mImagePipelineExperiments = builder.mExperimentsBuilder.build();
        this.mBitmapMemoryCacheParamsSupplier = builder.mBitmapMemoryCacheParamsSupplier == null ? new DefaultBitmapMemoryCacheParamsSupplier((ActivityManager) builder.mContext.getSystemService(Context.ACTIVITY_SERVICE)) : builder.mBitmapMemoryCacheParamsSupplier;
        this.mBitmapMemoryCacheTrimStrategy = builder.mBitmapMemoryCacheTrimStrategy == null ? new BitmapMemoryCacheTrimStrategy() : builder.mBitmapMemoryCacheTrimStrategy;
        this.mBitmapMemoryCacheEntryStateObserver = builder.mBitmapMemoryCacheEntryStateObserver;
        this.mBitmapConfig = builder.mBitmapConfig == null ? Bitmap.Config.ARGB_8888 : builder.mBitmapConfig;
        this.mCacheKeyFactory = builder.mCacheKeyFactory == null ? DefaultCacheKeyFactory.getInstance() : builder.mCacheKeyFactory;
        this.mContext = (Context) Preconditions.checkNotNull(builder.mContext);
        this.mFileCacheFactory = builder.mFileCacheFactory == null ? new DiskStorageCacheFactory(new DynamicDefaultDiskStorageFactory()) : builder.mFileCacheFactory;
        this.mDownsampleEnabled = builder.mDownsampleEnabled;
        this.mEncodedMemoryCacheParamsSupplier = builder.mEncodedMemoryCacheParamsSupplier == null ? new DefaultEncodedMemoryCacheParamsSupplier() : builder.mEncodedMemoryCacheParamsSupplier;
        this.mImageCacheStatsTracker = builder.mImageCacheStatsTracker == null ? NoOpImageCacheStatsTracker.getInstance() : builder.mImageCacheStatsTracker;
        this.mImageDecoder = builder.mImageDecoder;
        this.mImageTranscoderFactory = getImageTranscoderFactory(builder);
        this.mImageTranscoderType = builder.mImageTranscoderType;
        this.mIsPrefetchEnabledSupplier = builder.mIsPrefetchEnabledSupplier == null ? new Supplier<Boolean>() { // from class: com.facebook.imagepipeline.core.ImagePipelineConfig.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // com.facebook.common.internal.Supplier
            public Boolean get() {
                return true;
            }
        } : builder.mIsPrefetchEnabledSupplier;
        this.mMainDiskCacheConfig = builder.mMainDiskCacheConfig == null ? getDefaultMainDiskCacheConfig(builder.mContext) : builder.mMainDiskCacheConfig;
        this.mMemoryTrimmableRegistry = builder.mMemoryTrimmableRegistry == null ? NoOpMemoryTrimmableRegistry.getInstance() : builder.mMemoryTrimmableRegistry;
        this.mMemoryChunkType = getMemoryChunkType(builder, this.mImagePipelineExperiments);
        this.mHttpNetworkTimeout = builder.mHttpConnectionTimeout < 0 ? 30000 : builder.mHttpConnectionTimeout;
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.beginSection("ImagePipelineConfig->mNetworkFetcher");
        }
        this.mNetworkFetcher = builder.mNetworkFetcher == null ? new HttpUrlConnectionNetworkFetcher(this.mHttpNetworkTimeout) : builder.mNetworkFetcher;
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
        this.mPlatformBitmapFactory = builder.mPlatformBitmapFactory;
        this.mPoolFactory = builder.mPoolFactory == null ? new PoolFactory(PoolConfig.newBuilder().build()) : builder.mPoolFactory;
        this.mProgressiveJpegConfig = builder.mProgressiveJpegConfig == null ? new SimpleProgressiveJpegConfig() : builder.mProgressiveJpegConfig;
        this.mRequestListeners = builder.mRequestListeners == null ? new HashSet<>() : builder.mRequestListeners;
        this.mRequestListener2s = builder.mRequestListener2s == null ? new HashSet<>() : builder.mRequestListener2s;
        this.mResizeAndRotateEnabledForNetwork = builder.mResizeAndRotateEnabledForNetwork;
        this.mSmallImageDiskCacheConfig = builder.mSmallImageDiskCacheConfig == null ? this.mMainDiskCacheConfig : builder.mSmallImageDiskCacheConfig;
        this.mImageDecoderConfig = builder.mImageDecoderConfig;
        this.mExecutorSupplier = builder.mExecutorSupplier == null ? new DefaultExecutorSupplier(this.mPoolFactory.getFlexByteArrayPoolMaxNumThreads()) : builder.mExecutorSupplier;
        this.mDiskCacheEnabled = builder.mDiskCacheEnabled;
        this.mCallerContextVerifier = builder.mCallerContextVerifier;
        this.mCloseableReferenceLeakTracker = builder.mCloseableReferenceLeakTracker;
        this.mBitmapCache = builder.mBitmapMemoryCache;
        this.mBitmapMemoryCacheFactory = builder.mBitmapMemoryCacheFactory == null ? new CountingLruBitmapMemoryCacheFactory() : builder.mBitmapMemoryCacheFactory;
        this.mEncodedMemoryCache = builder.mEncodedMemoryCache;
        WebpBitmapFactory webpBitmapFactory = this.mImagePipelineExperiments.getWebpBitmapFactory();
        if (webpBitmapFactory != null) {
            setWebpBitmapFactory(webpBitmapFactory, this.mImagePipelineExperiments, new HoneycombBitmapCreator(getPoolFactory()));
        } else if (this.mImagePipelineExperiments.isWebpSupportEnabled() && WebpSupportStatus.sIsWebpSupportRequired && (webpBitmapFactoryLoadWebpBitmapFactoryIfExists = WebpSupportStatus.loadWebpBitmapFactoryIfExists()) != null) {
            setWebpBitmapFactory(webpBitmapFactoryLoadWebpBitmapFactoryIfExists, this.mImagePipelineExperiments, new HoneycombBitmapCreator(getPoolFactory()));
        }
        if (FrescoSystrace.isTracing()) {
            FrescoSystrace.endSection();
        }
    }

    public static DefaultImageRequestConfig getDefaultImageRequestConfig() {
        return sDefaultImageRequestConfig;
    }

    private static DiskCacheConfig getDefaultMainDiskCacheConfig(Context context) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("DiskCacheConfig.getDefaultMainDiskCacheConfig");
            }
            return DiskCacheConfig.newBuilder(context).build();
        } finally {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
    }

    @Nullable
    private static ImageTranscoderFactory getImageTranscoderFactory(Builder builder) {
        if (builder.mImageTranscoderFactory != null && builder.mImageTranscoderType != null) {
            throw new IllegalStateException("You can't define a custom ImageTranscoderFactory and provide an ImageTranscoderType");
        }
        if (builder.mImageTranscoderFactory != null) {
            return builder.mImageTranscoderFactory;
        }
        return null;
    }

    private static int getMemoryChunkType(Builder builder, ImagePipelineExperiments imagePipelineExperiments) {
        if (builder.mMemoryChunkType != null) {
            return builder.mMemoryChunkType.intValue();
        }
        if (imagePipelineExperiments.getMemoryType() == 2 && Build.VERSION.SDK_INT >= 27) {
            return 2;
        }
        if (imagePipelineExperiments.getMemoryType() == 1) {
            return 1;
        }
        return imagePipelineExperiments.getMemoryType() == 0 ? 0 : 0;
    }

    public static Builder newBuilder(Context context) {
        return new Builder(context);
    }

    @VisibleForTesting
    static void resetDefaultRequestConfig() {
        sDefaultImageRequestConfig = new DefaultImageRequestConfig();
    }

    private static void setWebpBitmapFactory(WebpBitmapFactory webpBitmapFactory, ImagePipelineExperiments imagePipelineExperiments, BitmapCreator bitmapCreator) {
        WebpSupportStatus.sWebpBitmapFactory = webpBitmapFactory;
        WebpBitmapFactory.WebpErrorLogger webpErrorLogger = imagePipelineExperiments.getWebpErrorLogger();
        if (webpErrorLogger != null) {
            webpBitmapFactory.setWebpErrorLogger(webpErrorLogger);
        }
        if (bitmapCreator != null) {
            webpBitmapFactory.setBitmapCreator(bitmapCreator);
        }
    }

    @Nullable
    public MemoryCache<CacheKey, CloseableImage> getBitmapCacheOverride() {
        return this.mBitmapCache;
    }

    public Bitmap.Config getBitmapConfig() {
        return this.mBitmapConfig;
    }

    @Nullable
    public CountingMemoryCache.EntryStateObserver<CacheKey> getBitmapMemoryCacheEntryStateObserver() {
        return this.mBitmapMemoryCacheEntryStateObserver;
    }

    public BitmapMemoryCacheFactory getBitmapMemoryCacheFactory() {
        return this.mBitmapMemoryCacheFactory;
    }

    public Supplier<MemoryCacheParams> getBitmapMemoryCacheParamsSupplier() {
        return this.mBitmapMemoryCacheParamsSupplier;
    }

    public MemoryCache.CacheTrimStrategy getBitmapMemoryCacheTrimStrategy() {
        return this.mBitmapMemoryCacheTrimStrategy;
    }

    public CacheKeyFactory getCacheKeyFactory() {
        return this.mCacheKeyFactory;
    }

    @Nullable
    public CallerContextVerifier getCallerContextVerifier() {
        return this.mCallerContextVerifier;
    }

    public CloseableReferenceLeakTracker getCloseableReferenceLeakTracker() {
        return this.mCloseableReferenceLeakTracker;
    }

    public Context getContext() {
        return this.mContext;
    }

    @Nullable
    public MemoryCache<CacheKey, PooledByteBuffer> getEncodedMemoryCacheOverride() {
        return this.mEncodedMemoryCache;
    }

    public Supplier<MemoryCacheParams> getEncodedMemoryCacheParamsSupplier() {
        return this.mEncodedMemoryCacheParamsSupplier;
    }

    public ExecutorSupplier getExecutorSupplier() {
        return this.mExecutorSupplier;
    }

    public ImagePipelineExperiments getExperiments() {
        return this.mImagePipelineExperiments;
    }

    public FileCacheFactory getFileCacheFactory() {
        return this.mFileCacheFactory;
    }

    public ImageCacheStatsTracker getImageCacheStatsTracker() {
        return this.mImageCacheStatsTracker;
    }

    @Nullable
    public ImageDecoder getImageDecoder() {
        return this.mImageDecoder;
    }

    @Nullable
    public ImageDecoderConfig getImageDecoderConfig() {
        return this.mImageDecoderConfig;
    }

    @Nullable
    public ImageTranscoderFactory getImageTranscoderFactory() {
        return this.mImageTranscoderFactory;
    }

    @Nullable
    public Integer getImageTranscoderType() {
        return this.mImageTranscoderType;
    }

    public Supplier<Boolean> getIsPrefetchEnabledSupplier() {
        return this.mIsPrefetchEnabledSupplier;
    }

    public DiskCacheConfig getMainDiskCacheConfig() {
        return this.mMainDiskCacheConfig;
    }

    public int getMemoryChunkType() {
        return this.mMemoryChunkType;
    }

    public MemoryTrimmableRegistry getMemoryTrimmableRegistry() {
        return this.mMemoryTrimmableRegistry;
    }

    public NetworkFetcher getNetworkFetcher() {
        return this.mNetworkFetcher;
    }

    @Nullable
    public PlatformBitmapFactory getPlatformBitmapFactory() {
        return this.mPlatformBitmapFactory;
    }

    public PoolFactory getPoolFactory() {
        return this.mPoolFactory;
    }

    public ProgressiveJpegConfig getProgressiveJpegConfig() {
        return this.mProgressiveJpegConfig;
    }

    public Set<RequestListener2> getRequestListener2s() {
        return Collections.unmodifiableSet(this.mRequestListener2s);
    }

    public Set<RequestListener> getRequestListeners() {
        return Collections.unmodifiableSet(this.mRequestListeners);
    }

    public DiskCacheConfig getSmallImageDiskCacheConfig() {
        return this.mSmallImageDiskCacheConfig;
    }

    public boolean isDiskCacheEnabled() {
        return this.mDiskCacheEnabled;
    }

    public boolean isDownsampleEnabled() {
        return this.mDownsampleEnabled;
    }

    public boolean isResizeAndRotateEnabledForNetwork() {
        return this.mResizeAndRotateEnabledForNetwork;
    }
}
