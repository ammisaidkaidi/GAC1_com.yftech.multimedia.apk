package com.alibaba.android.vlayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class VirtualLayoutAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    @NonNull
    protected VirtualLayoutManager mLayoutManager;

    public VirtualLayoutAdapter(@NonNull VirtualLayoutManager virtualLayoutManager) {
        this.mLayoutManager = virtualLayoutManager;
    }

    @NonNull
    public List<LayoutHelper> getLayoutHelpers() {
        return this.mLayoutManager.getLayoutHelpers();
    }

    public void setLayoutHelpers(List<LayoutHelper> list) {
        this.mLayoutManager.setLayoutHelpers(list);
    }
}
