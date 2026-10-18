package com.facebook.imagepipeline.common;

import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public enum Priority {
    LOW,
    MEDIUM,
    HIGH;

    @Nullable
    public static Priority getHigherPriority(@Nullable Priority priority, @Nullable Priority priority2) {
        if (priority == null) {
            return priority2;
        }
        return (priority2 != null && priority.ordinal() <= priority2.ordinal()) ? priority2 : priority;
    }
}
