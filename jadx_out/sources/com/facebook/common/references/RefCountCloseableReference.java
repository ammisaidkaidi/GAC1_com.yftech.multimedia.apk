package com.facebook.common.references;

import com.facebook.common.internal.Preconditions;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class RefCountCloseableReference<T> extends CloseableReference<T> {
    private RefCountCloseableReference(SharedReference<T> sharedReference, CloseableReference.LeakHandler leakHandler, @Nullable Throwable th) {
        super(sharedReference, leakHandler, th);
    }

    RefCountCloseableReference(T t, ResourceReleaser<T> resourceReleaser, CloseableReference.LeakHandler leakHandler, @Nullable Throwable th) {
        super(t, resourceReleaser, leakHandler, th);
    }

    @Override // com.facebook.common.references.CloseableReference
    /* JADX INFO: renamed from: clone */
    public CloseableReference<T> mo57clone() {
        Preconditions.checkState(isValid());
        return new RefCountCloseableReference(this.mSharedReference, this.mLeakHandler, this.mStacktrace);
    }
}
