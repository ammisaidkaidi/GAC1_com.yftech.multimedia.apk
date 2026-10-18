package com.facebook.callercontext;

import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public interface CallerContextVerifier {
    void verifyCallerContext(@Nullable Object obj, boolean z);
}
