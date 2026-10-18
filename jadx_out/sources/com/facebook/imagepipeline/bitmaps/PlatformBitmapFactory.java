package com.facebook.imagepipeline.bitmaps;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.CloseableReference;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public abstract class PlatformBitmapFactory {
    private static void checkFinalImageBounds(Bitmap bitmap, int i, int i2, int i3, int i4) {
        Preconditions.checkArgument(i + i3 <= bitmap.getWidth(), "x + width must be <= bitmap.width()");
        Preconditions.checkArgument(i2 + i4 <= bitmap.getHeight(), "y + height must be <= bitmap.height()");
    }

    private static void checkWidthHeight(int i, int i2) {
        Preconditions.checkArgument(i > 0, "width must be > 0");
        Preconditions.checkArgument(i2 > 0, "height must be > 0");
    }

    private static void checkXYSign(int i, int i2) {
        Preconditions.checkArgument(i >= 0, "x must be >= 0");
        Preconditions.checkArgument(i2 >= 0, "y must be >= 0");
    }

    private CloseableReference<Bitmap> createBitmap(int i, int i2, Bitmap.Config config, boolean z) {
        return createBitmap(i, i2, config, z, (Object) null);
    }

    private CloseableReference<Bitmap> createBitmap(int i, int i2, Bitmap.Config config, boolean z, @Nullable Object obj) {
        return createBitmap((DisplayMetrics) null, i, i2, config, z, obj);
    }

    private CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int i, int i2, Bitmap.Config config, boolean z) {
        return createBitmap(displayMetrics, i, i2, config, z, (Object) null);
    }

    private CloseableReference<Bitmap> createBitmap(@Nullable DisplayMetrics displayMetrics, int i, int i2, Bitmap.Config config, boolean z, @Nullable Object obj) {
        checkWidthHeight(i, i2);
        CloseableReference<Bitmap> closeableReferenceCreateBitmapInternal = createBitmapInternal(i, i2, config);
        Bitmap bitmap = closeableReferenceCreateBitmapInternal.get();
        if (displayMetrics != null) {
            bitmap.setDensity(displayMetrics.densityDpi);
        }
        if (Build.VERSION.SDK_INT >= 12) {
            bitmap.setHasAlpha(z);
        }
        if (config == Bitmap.Config.ARGB_8888 && !z) {
            bitmap.eraseColor(-16777216);
        }
        return closeableReferenceCreateBitmapInternal;
    }

    private static Bitmap.Config getSuitableBitmapConfig(Bitmap bitmap) {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap.Config config2 = bitmap.getConfig();
        if (config2 == null) {
            return config;
        }
        switch (config2) {
            case RGB_565:
                return Bitmap.Config.RGB_565;
            case ALPHA_8:
                return Bitmap.Config.ALPHA_8;
            default:
                return Bitmap.Config.ARGB_8888;
        }
    }

    private static void setPropertyFromSourceBitmap(Bitmap bitmap, Bitmap bitmap2) {
        bitmap2.setDensity(bitmap.getDensity());
        if (Build.VERSION.SDK_INT >= 12) {
            bitmap2.setHasAlpha(bitmap.hasAlpha());
        }
        if (Build.VERSION.SDK_INT >= 19) {
            bitmap2.setPremultiplied(bitmap.isPremultiplied());
        }
    }

    public CloseableReference<Bitmap> createBitmap(int i, int i2) {
        return createBitmap(i, i2, Bitmap.Config.ARGB_8888);
    }

    public CloseableReference<Bitmap> createBitmap(int i, int i2, Bitmap.Config config) {
        return createBitmap(i, i2, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(int i, int i2, Bitmap.Config config, @Nullable Object obj) {
        return createBitmapInternal(i, i2, config);
    }

    public CloseableReference<Bitmap> createBitmap(int i, int i2, @Nullable Object obj) {
        return createBitmap(i, i2, Bitmap.Config.ARGB_8888, obj);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap) {
        return createBitmap(bitmap, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4) {
        return createBitmap(bitmap, i, i2, i3, i4, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4, @Nullable Matrix matrix, boolean z) {
        return createBitmap(bitmap, i, i2, i3, i4, matrix, z, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4, @Nullable Matrix matrix, boolean z, @Nullable Object obj) {
        CloseableReference<Bitmap> closeableReferenceCreateBitmap;
        Canvas canvas;
        Paint paint;
        Preconditions.checkNotNull(bitmap, "Source bitmap cannot be null");
        checkXYSign(i, i2);
        checkWidthHeight(i3, i4);
        checkFinalImageBounds(bitmap, i, i2, i3, i4);
        Rect rect = new Rect(i, i2, i + i3, i2 + i4);
        RectF rectF = new RectF(0.0f, 0.0f, i3, i4);
        Bitmap.Config suitableBitmapConfig = getSuitableBitmapConfig(bitmap);
        if (matrix == null || matrix.isIdentity()) {
            closeableReferenceCreateBitmap = createBitmap(i3, i4, suitableBitmapConfig, bitmap.hasAlpha(), obj);
            setPropertyFromSourceBitmap(bitmap, closeableReferenceCreateBitmap.get());
            canvas = new Canvas(closeableReferenceCreateBitmap.get());
            paint = null;
        } else {
            boolean z2 = !matrix.rectStaysRect();
            RectF rectF2 = new RectF();
            matrix.mapRect(rectF2, rectF);
            int iRound = Math.round(rectF2.width());
            int iRound2 = Math.round(rectF2.height());
            if (z2) {
                suitableBitmapConfig = Bitmap.Config.ARGB_8888;
            }
            closeableReferenceCreateBitmap = createBitmap(iRound, iRound2, suitableBitmapConfig, z2 || bitmap.hasAlpha(), obj);
            setPropertyFromSourceBitmap(bitmap, closeableReferenceCreateBitmap.get());
            canvas = new Canvas(closeableReferenceCreateBitmap.get());
            canvas.translate(-rectF2.left, -rectF2.top);
            canvas.concat(matrix);
            paint = new Paint();
            paint.setFilterBitmap(z);
            if (z2) {
                paint.setAntiAlias(true);
            }
        }
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        canvas.setBitmap(null);
        return closeableReferenceCreateBitmap;
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, int i, int i2, int i3, int i4, @Nullable Object obj) {
        return createBitmap(bitmap, i, i2, i3, i4, (Matrix) null, false, obj);
    }

    public CloseableReference<Bitmap> createBitmap(Bitmap bitmap, @Nullable Object obj) {
        return createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), obj);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int i, int i2, Bitmap.Config config) {
        return createBitmap(displayMetrics, i, i2, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int i, int i2, Bitmap.Config config, @Nullable Object obj) {
        return createBitmap(displayMetrics, i, i2, config, true, obj);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, int i3, int i4, Bitmap.Config config) {
        return createBitmap(displayMetrics, iArr, i, i2, i3, i4, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, int i3, int i4, Bitmap.Config config, @Nullable Object obj) {
        CloseableReference<Bitmap> closeableReferenceCreateBitmap = createBitmap(displayMetrics, i3, i4, config, obj);
        closeableReferenceCreateBitmap.get().setPixels(iArr, i, i2, 0, 0, i3, i4);
        return closeableReferenceCreateBitmap;
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, Bitmap.Config config) {
        return createBitmap(displayMetrics, iArr, i, i2, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(DisplayMetrics displayMetrics, int[] iArr, int i, int i2, Bitmap.Config config, @Nullable Object obj) {
        return createBitmap(displayMetrics, iArr, 0, i, i, i2, config, obj);
    }

    public CloseableReference<Bitmap> createBitmap(int[] iArr, int i, int i2, Bitmap.Config config) {
        return createBitmap(iArr, i, i2, config, (Object) null);
    }

    public CloseableReference<Bitmap> createBitmap(int[] iArr, int i, int i2, Bitmap.Config config, @Nullable Object obj) {
        CloseableReference<Bitmap> closeableReferenceCreateBitmapInternal = createBitmapInternal(i, i2, config);
        closeableReferenceCreateBitmapInternal.get().setPixels(iArr, 0, i, 0, 0, i, i2);
        return closeableReferenceCreateBitmapInternal;
    }

    public abstract CloseableReference<Bitmap> createBitmapInternal(int i, int i2, Bitmap.Config config);

    public CloseableReference<Bitmap> createScaledBitmap(Bitmap bitmap, int i, int i2, boolean z) {
        return createScaledBitmap(bitmap, i, i2, z, null);
    }

    public CloseableReference<Bitmap> createScaledBitmap(Bitmap bitmap, int i, int i2, boolean z, @Nullable Object obj) {
        checkWidthHeight(i, i2);
        Matrix matrix = new Matrix();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        matrix.setScale(i / width, i2 / height);
        return createBitmap(bitmap, 0, 0, width, height, matrix, z, obj);
    }
}
