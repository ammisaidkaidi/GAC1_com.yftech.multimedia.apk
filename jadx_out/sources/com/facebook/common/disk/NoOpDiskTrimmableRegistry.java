package com.facebook.common.disk;

import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class NoOpDiskTrimmableRegistry implements DiskTrimmableRegistry {

    @Nullable
    private static NoOpDiskTrimmableRegistry sInstance;

    private NoOpDiskTrimmableRegistry() {
    }

    public static synchronized NoOpDiskTrimmableRegistry getInstance() {
        if (sInstance == null) {
            sInstance = new NoOpDiskTrimmableRegistry();
        }
        return sInstance;
    }

    @Override // com.facebook.common.disk.DiskTrimmableRegistry
    public void registerDiskTrimmable(DiskTrimmable diskTrimmable) {
    }

    @Override // com.facebook.common.disk.DiskTrimmableRegistry
    public void unregisterDiskTrimmable(DiskTrimmable diskTrimmable) {
    }
}
