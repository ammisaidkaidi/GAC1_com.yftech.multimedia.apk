package com.facebook.fresco.ui.common;

import android.net.Uri;
import com.facebook.common.internal.Fn;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public abstract class MultiUriHelper {
    @Nullable
    public static <T> Uri getMainUri(@Nullable T t, @Nullable T t2, @Nullable T[] tArr, Fn<T, Uri> fn) {
        Uri uriApply;
        Uri uriApply2;
        if (t != null && (uriApply2 = fn.apply(t)) != null) {
            return uriApply2;
        }
        if (tArr != null && tArr.length > 0 && tArr[0] != null && (uriApply = fn.apply(tArr[0])) != null) {
            return uriApply;
        }
        if (t2 != null) {
            return fn.apply(t2);
        }
        return null;
    }
}
