package com.yftech.car.widget.media.list.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.yftech.car.widget.media.list.SearchHistoryList;
import com.yftech.car.widget.media.list.holder.SearchHistoryHolder;
import com.yftech.car.widget.media.recyclerview.widget.RecyclerView;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryHolder> implements View.OnClickListener {
    private boolean mDeleteShow;
    private final boolean mInitDeleteShow;
    private final int mItemLayoutId;
    private final List<String> mList;
    private OnSearchHistoryItemClickListener mListener;
    private final SearchHistoryList mSearchHistoryList;
    private final View.OnClickListener mDeleteOnClickListener = new View.OnClickListener() { // from class: com.yftech.car.widget.media.list.adapter.SearchHistoryAdapter.1
        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            Object tag = view.getTag();
            if (tag == null || SearchHistoryAdapter.this.mListener == null) {
                return;
            }
            SearchHistoryAdapter.this.mListener.onItemDelete(SearchHistoryAdapter.this.mSearchHistoryList, tag.toString());
        }
    };
    private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() { // from class: com.yftech.car.widget.media.list.adapter.SearchHistoryAdapter.2
        @Override // android.view.View.OnLongClickListener
        public boolean onLongClick(View view) {
            SearchHistoryAdapter.this.mDeleteShow = !SearchHistoryAdapter.this.mDeleteShow;
            SearchHistoryAdapter.this.notifyDataSetChanged();
            return true;
        }
    };

    public interface OnSearchHistoryItemClickListener {
        void onItemDelete(SearchHistoryList searchHistoryList, String str);

        void onItemSearch(SearchHistoryList searchHistoryList, String str);
    }

    public SearchHistoryAdapter(SearchHistoryList searchHistoryList, List<String> list, int i, boolean z) {
        this.mDeleteShow = false;
        this.mSearchHistoryList = searchHistoryList;
        this.mList = list;
        this.mItemLayoutId = i;
        this.mDeleteShow = z;
        this.mInitDeleteShow = z;
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mList.size();
    }

    public boolean isDeleteShow() {
        return this.mDeleteShow;
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(SearchHistoryHolder searchHistoryHolder, int i) {
        searchHistoryHolder.setItemData(this.mList.get(i), i);
        searchHistoryHolder.setDeleteVisibility(this.mDeleteShow ? 0 : 8);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Object tag = view.getTag();
        if (tag == null || this.mListener == null) {
            return;
        }
        this.mListener.onItemSearch(this.mSearchHistoryList, tag.toString());
    }

    @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.Adapter
    public SearchHistoryHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View viewInflate = LayoutInflater.from(viewGroup.getContext()).inflate(this.mItemLayoutId, viewGroup, false);
        viewInflate.setOnClickListener(this);
        if (!this.mInitDeleteShow) {
            viewInflate.setOnLongClickListener(this.mOnLongClickListener);
        }
        SearchHistoryHolder searchHistoryHolder = new SearchHistoryHolder(viewInflate);
        searchHistoryHolder.setDeleteOnClickListener(this.mDeleteOnClickListener);
        return searchHistoryHolder;
    }

    public void setDeleteShow(boolean z) {
        this.mDeleteShow = z;
        notifyDataSetChanged();
    }

    public void setOnSearchHistoryItemClickListener(OnSearchHistoryItemClickListener onSearchHistoryItemClickListener) {
        this.mListener = onSearchHistoryItemClickListener;
    }
}
