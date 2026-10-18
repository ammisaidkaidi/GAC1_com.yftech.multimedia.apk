package com.chad.library.adapter.base;

import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.SectionEntity;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class BaseSectionQuickAdapter<T extends SectionEntity, K extends BaseViewHolder> extends BaseQuickAdapter<T, K> {
    protected static final int SECTION_HEADER_VIEW = 1092;
    protected int mSectionHeadResId;

    public BaseSectionQuickAdapter(int i, int i2, List<T> list) {
        super(i, list);
        this.mSectionHeadResId = i2;
    }

    protected abstract void convertHead(K k, T t);

    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    protected int getDefItemViewType(int i) {
        return this.mData.get(i).isHeader ? 1092 : 0;
    }

    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    protected boolean isFixedViewType(int i) {
        return super.isFixedViewType(i) || i == 1092;
    }

    @Override // com.chad.library.adapter.base.BaseQuickAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(K k, int i) {
        if (k.getItemViewType() != 1092) {
            super.onBindViewHolder((BaseViewHolder) k, i);
        } else {
            setFullSpan(k);
            convertHead(k, getItem(i - getHeaderLayoutCount()));
        }
    }

    @Override // com.chad.library.adapter.base.BaseQuickAdapter
    protected K onCreateDefViewHolder(ViewGroup viewGroup, int i) {
        return i == 1092 ? createBaseViewHolder(getItemView(this.mSectionHeadResId, viewGroup)) : (K) super.onCreateDefViewHolder(viewGroup, i);
    }
}
