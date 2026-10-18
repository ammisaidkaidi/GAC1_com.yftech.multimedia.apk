package com.facebook.common.internal;

import com.facebook.infer.annotation.Nullsafe;

/* JADX INFO: loaded from: classes3.dex */
@Nullsafe(Nullsafe.Mode.STRICT)
public class AndroidPredicates {
    private AndroidPredicates() {
    }

    public static <T> Predicate<T> False() {
        return new Predicate<T>() { // from class: com.facebook.common.internal.AndroidPredicates.2
            @Override // com.facebook.common.internal.Predicate
            public boolean apply(T t) {
                return false;
            }
        };
    }

    public static <T> Predicate<T> True() {
        return new Predicate<T>() { // from class: com.facebook.common.internal.AndroidPredicates.1
            @Override // com.facebook.common.internal.Predicate
            public boolean apply(T t) {
                return true;
            }
        };
    }
}
