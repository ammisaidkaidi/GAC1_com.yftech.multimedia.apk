package com.facebook.imagepipeline.filter;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public final class InPlaceRoundFilter {
    private InPlaceRoundFilter() {
    }

    public static void roundBitmapInPlace(Bitmap bitmap) {
        Preconditions.checkNotNull(bitmap);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int iMin = Math.min(width, height) / 2;
        int i = width / 2;
        int i2 = height / 2;
        if (iMin == 0) {
            return;
        }
        Preconditions.checkArgument(Boolean.valueOf(iMin >= 1));
        Preconditions.checkArgument(Boolean.valueOf(width > 0 && ((float) width) <= 2048.0f));
        Preconditions.checkArgument(Boolean.valueOf(height > 0 && ((float) height) <= 2048.0f));
        Preconditions.checkArgument(Boolean.valueOf(i > 0 && i < width));
        Preconditions.checkArgument(Boolean.valueOf(i2 > 0 && i2 < height));
        int[] iArr = new int[width * height];
        bitmap.getPixels(iArr, 0, width, 0, 0, width, height);
        int i3 = iMin - 1;
        Preconditions.checkArgument(Boolean.valueOf(i - i3 >= 0 && i2 - i3 >= 0 && i + i3 < width && i2 + i3 < height));
        int i4 = (-iMin) * 2;
        int[] iArr2 = new int[width];
        int i5 = i4 + 1;
        int i6 = 1;
        int i7 = 1;
        int i8 = 0;
        while (i3 >= i8) {
            int i9 = i + i3;
            int i10 = i - i3;
            int i11 = i + i8;
            iMin = iMin;
            int i12 = i - i8;
            int i13 = i2 + i3;
            int i14 = i2 - i3;
            i = i;
            int i15 = i2 + i8;
            int i16 = i2 - i8;
            Preconditions.checkArgument(Boolean.valueOf(i3 >= 0 && i11 < width && i12 >= 0 && i15 < height && i16 >= 0));
            int i17 = i15 * width;
            height = height;
            int i18 = width * i16;
            i2 = i2;
            int i19 = width * i13;
            i4 = i4;
            int i20 = width * i14;
            int i21 = i6;
            System.arraycopy(iArr2, 0, iArr, i17, i10);
            System.arraycopy(iArr2, 0, iArr, i18, i10);
            System.arraycopy(iArr2, 0, iArr, i19, i12);
            System.arraycopy(iArr2, 0, iArr, i20, i12);
            int i22 = width - i9;
            System.arraycopy(iArr2, 0, iArr, i17 + i9, i22);
            System.arraycopy(iArr2, 0, iArr, i18 + i9, i22);
            int i23 = width - i11;
            System.arraycopy(iArr2, 0, iArr, i19 + i11, i23);
            System.arraycopy(iArr2, 0, iArr, i20 + i11, i23);
            if (i5 <= 0) {
                i8++;
                i7 += 2;
                i5 += i7;
            }
            if (i5 > 0) {
                i3--;
                i6 = i21 + 2;
                i5 += i6 + i4;
            } else {
                i6 = i21;
            }
        }
        int i24 = height;
        int i25 = iMin;
        int i26 = i2;
        for (int i27 = i26 - i25; i27 >= 0; i27--) {
            System.arraycopy(iArr2, 0, iArr, i27 * width, width);
        }
        for (int i28 = i26 + i25; i28 < i24; i28++) {
            System.arraycopy(iArr2, 0, iArr, i28 * width, width);
        }
        bitmap.setPixels(iArr, 0, width, 0, 0, width, i24);
    }
}
