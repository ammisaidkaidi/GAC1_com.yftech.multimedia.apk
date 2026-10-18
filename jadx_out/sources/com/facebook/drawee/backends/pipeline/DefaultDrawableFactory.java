package com.facebook.drawee.backends.pipeline;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.facebook.drawee.drawable.OrientedDrawable;
import com.facebook.imagepipeline.drawable.DrawableFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.systrace.FrescoSystrace;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class DefaultDrawableFactory implements DrawableFactory {

    @Nullable
    private final DrawableFactory mAnimatedDrawableFactory;
    private final Resources mResources;

    public DefaultDrawableFactory(Resources resources, @Nullable DrawableFactory drawableFactory) {
        this.mResources = resources;
        this.mAnimatedDrawableFactory = drawableFactory;
    }

    private static boolean hasTransformableExifOrientation(CloseableStaticBitmap closeableStaticBitmap) {
        return (closeableStaticBitmap.getExifOrientation() == 1 || closeableStaticBitmap.getExifOrientation() == 0) ? false : true;
    }

    private static boolean hasTransformableRotationAngle(CloseableStaticBitmap closeableStaticBitmap) {
        return (closeableStaticBitmap.getRotationAngle() == 0 || closeableStaticBitmap.getRotationAngle() == -1) ? false : true;
    }

    @Override // com.facebook.imagepipeline.drawable.DrawableFactory
    @Nullable
    public Drawable createDrawable(CloseableImage closeableImage) {
        try {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.beginSection("DefaultDrawableFactory#createDrawable");
            }
            if (closeableImage instanceof CloseableStaticBitmap) {
                CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) closeableImage;
                BitmapDrawable bitmapDrawable = new BitmapDrawable(this.mResources, closeableStaticBitmap.getUnderlyingBitmap());
                return (hasTransformableRotationAngle(closeableStaticBitmap) || hasTransformableExifOrientation(closeableStaticBitmap)) ? new OrientedDrawable(bitmapDrawable, closeableStaticBitmap.getRotationAngle(), closeableStaticBitmap.getExifOrientation()) : bitmapDrawable;
            }
            if (this.mAnimatedDrawableFactory == null || !this.mAnimatedDrawableFactory.supportsImageType(closeableImage)) {
                return null;
            }
            return this.mAnimatedDrawableFactory.createDrawable(closeableImage);
        } finally {
            if (FrescoSystrace.isTracing()) {
                FrescoSystrace.endSection();
            }
        }
    }

    @Override // com.facebook.imagepipeline.drawable.DrawableFactory
    public boolean supportsImageType(CloseableImage closeableImage) {
        return true;
    }
}
