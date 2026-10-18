package com.alibaba.android.vlayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class LayoutHelperFinder implements Iterable<LayoutHelper> {
    @Nullable
    public abstract LayoutHelper getLayoutHelper(int i);

    @NonNull
    protected abstract List<LayoutHelper> getLayoutHelpers();

    protected abstract Iterable<LayoutHelper> reverse();

    abstract void setLayouts(@Nullable List<LayoutHelper> list);
}
