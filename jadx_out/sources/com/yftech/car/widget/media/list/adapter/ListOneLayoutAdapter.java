package com.yftech.car.widget.media.list.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yftech.car.widget.media.recyclerview.widget.RecyclerView;

/* JADX INFO: loaded from: classes3.dex */
public class ListOneLayoutAdapter extends RecyclerView.Adapter<ListOneLayoutHolder> {
    private final int mSearchLoadingLayoutId;

    protected static final class ListOneLayoutHolder extends RecyclerView.ViewHolder {
        public ListOneLayoutHolder(View view) {
            super(view);
        }
    }

    public ListOneLayoutAdapter(int i) {
        this.mSearchLoadingLayoutId = i;
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return 1;
    }

    public boolean isExistsLayoutId() {
        return this.mSearchLoadingLayoutId != -1;
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ListOneLayoutHolder listOneLayoutHolder, int i) {
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    public ListOneLayoutHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ListOneLayoutHolder(LayoutInflater.from(viewGroup.getContext()).inflate(this.mSearchLoadingLayoutId, viewGroup, false));
    }
}
