package com.facebook.imagepipeline.drawable;

import android.graphics.drawable.Drawable;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public interface DrawableFactory {
    @Nullable
    Drawable createDrawable(@Nonnull CloseableImage closeableImage);

    boolean supportsImageType(@Nonnull CloseableImage closeableImage);
}
