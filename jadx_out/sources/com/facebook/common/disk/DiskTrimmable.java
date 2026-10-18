package com.facebook.common.disk;

import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public interface DiskTrimmable {
    void trimToMinimum();

    void trimToNothing();
}
