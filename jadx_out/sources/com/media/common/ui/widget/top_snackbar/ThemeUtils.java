package com.media.common.ui.widget.top_snackbar;

import android.content.Context;
import android.content.res.TypedArray;

/* JADX INFO: loaded from: classes3.dex */
class ThemeUtils {
    private static final int[] APPCOMPAT_CHECK_ATTRS = new int[0];

    ThemeUtils() {
    }

    static void checkAppCompatTheme(Context context) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(APPCOMPAT_CHECK_ATTRS);
        boolean z = !typedArrayObtainStyledAttributes.hasValue(0);
        if (typedArrayObtainStyledAttributes != null) {
            typedArrayObtainStyledAttributes.recycle();
        }
        if (z) {
            throw new IllegalArgumentException("You need to use a Theme.AppCompat theme (or descendant) with the design library.");
        }
    }
}
