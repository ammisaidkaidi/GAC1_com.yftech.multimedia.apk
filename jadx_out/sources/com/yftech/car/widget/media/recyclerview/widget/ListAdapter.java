package com.yftech.car.widget.media.recyclerview.widget;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yftech.car.widget.media.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ListAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    final AsyncListDiffer<T> mDiffer;
    private final AsyncListDiffer.ListListener<T> mListener = new AsyncListDiffer.ListListener<T>() { // from class: com.yftech.car.widget.media.recyclerview.widget.ListAdapter.1
        @Override // com.yftech.car.widget.media.recyclerview.widget.AsyncListDiffer.ListListener
        public void onCurrentListChanged(@NonNull List<T> list, @NonNull List<T> list2) {
            ListAdapter.this.onCurrentListChanged(list, list2);
        }
    };

    protected ListAdapter(@NonNull AsyncDifferConfig<T> asyncDifferConfig) {
        this.mDiffer = new AsyncListDiffer<>(new AdapterListUpdateCallback(this), asyncDifferConfig);
        this.mDiffer.addListListener(this.mListener);
    }

    protected ListAdapter(@NonNull DiffUtil.ItemCallback<T> itemCallback) {
        this.mDiffer = new AsyncListDiffer<>(new AdapterListUpdateCallback(this), new AsyncDifferConfig.Builder(itemCallback).build());
        this.mDiffer.addListListener(this.mListener);
    }

    @NonNull
    public List<T> getCurrentList() {
        return this.mDiffer.getCurrentList();
    }

    protected T getItem(int i) {
        return this.mDiffer.getCurrentList().get(i);
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mDiffer.getCurrentList().size();
    }

    public void onCurrentListChanged(@NonNull List<T> list, @NonNull List<T> list2) {
    }

    public void submitList(@Nullable List<T> list) {
        this.mDiffer.submitList(list);
    }

    public void submitList(@Nullable List<T> list, @Nullable Runnable runnable) {
        this.mDiffer.submitList(list, runnable);
    }
}
