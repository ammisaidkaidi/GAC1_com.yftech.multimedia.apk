package com.yftech.car.widget.media.recyclerview.widget;

/* JADX INFO: loaded from: classes3.dex */
public abstract class SortedListAdapterCallback<T2> extends SortedList.Callback<T2> {
    final RecyclerView.Adapter mAdapter;

    public SortedListAdapterCallback(RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.SortedList.Callback
    public void onChanged(int i, int i2) {
        this.mAdapter.notifyItemRangeChanged(i, i2);
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.SortedList.Callback, com.yftech.car.widget.media.recyclerview.widget.ListUpdateCallback
    public void onChanged(int i, int i2, Object obj) {
        this.mAdapter.notifyItemRangeChanged(i, i2, obj);
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.ListUpdateCallback
    public void onInserted(int i, int i2) {
        this.mAdapter.notifyItemRangeInserted(i, i2);
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.ListUpdateCallback
    public void onMoved(int i, int i2) {
        this.mAdapter.notifyItemMoved(i, i2);
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.ListUpdateCallback
    public void onRemoved(int i, int i2) {
        this.mAdapter.notifyItemRangeRemoved(i, i2);
    }
}
