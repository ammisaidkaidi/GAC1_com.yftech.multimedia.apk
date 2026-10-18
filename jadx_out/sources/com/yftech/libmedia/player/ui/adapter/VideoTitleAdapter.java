package com.yftech.libmedia.player.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.StickyLayoutHelper;
import com.chad.library.adapter.base.BaseViewHolder;
import com.yftech.libmedia.R;

/* JADX INFO: loaded from: classes4.dex */
public class VideoTitleAdapter extends DelegateAdapter.Adapter<BaseViewHolder> {
    private String date;
    private Context mContext;

    public VideoTitleAdapter(Context context, String str) {
        this.mContext = context;
        this.date = str;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return 1;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(@NonNull BaseViewHolder baseViewHolder, int i) {
        baseViewHolder.setText(R.id.tv_item_date, this.date);
    }

    @Override // com.alibaba.android.vlayout.DelegateAdapter.Adapter
    public LayoutHelper onCreateLayoutHelper() {
        return new StickyLayoutHelper();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    @NonNull
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new BaseViewHolder(LayoutInflater.from(this.mContext).inflate(R.layout.item_date, viewGroup, false));
    }
}
