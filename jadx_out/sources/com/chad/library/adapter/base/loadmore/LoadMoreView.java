package com.chad.library.adapter.base.loadmore;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import com.chad.library.adapter.base.BaseViewHolder;

/* JADX INFO: loaded from: classes3.dex */
public abstract class LoadMoreView {
    public static final int STATUS_DEFAULT = 1;
    public static final int STATUS_END = 4;
    public static final int STATUS_FAIL = 3;
    public static final int STATUS_LOADING = 2;
    private int mLoadMoreStatus = 1;
    private boolean mLoadMoreEndGone = false;

    private void visibleLoadEnd(BaseViewHolder baseViewHolder, boolean z) {
        int loadEndViewId = getLoadEndViewId();
        if (loadEndViewId != 0) {
            baseViewHolder.setGone(loadEndViewId, z);
        }
    }

    private void visibleLoadFail(BaseViewHolder baseViewHolder, boolean z) {
        baseViewHolder.setGone(getLoadFailViewId(), z);
    }

    private void visibleLoading(BaseViewHolder baseViewHolder, boolean z) {
        baseViewHolder.setGone(getLoadingViewId(), z);
    }

    public void convert(BaseViewHolder baseViewHolder) {
        switch (this.mLoadMoreStatus) {
            case 1:
                visibleLoading(baseViewHolder, false);
                visibleLoadFail(baseViewHolder, false);
                visibleLoadEnd(baseViewHolder, false);
                break;
            case 2:
                visibleLoading(baseViewHolder, true);
                visibleLoadFail(baseViewHolder, false);
                visibleLoadEnd(baseViewHolder, false);
                break;
            case 3:
                visibleLoading(baseViewHolder, false);
                visibleLoadFail(baseViewHolder, true);
                visibleLoadEnd(baseViewHolder, false);
                break;
            case 4:
                visibleLoading(baseViewHolder, false);
                visibleLoadFail(baseViewHolder, false);
                visibleLoadEnd(baseViewHolder, true);
                break;
        }
    }

    @LayoutRes
    public abstract int getLayoutId();

    @IdRes
    protected abstract int getLoadEndViewId();

    @IdRes
    protected abstract int getLoadFailViewId();

    public int getLoadMoreStatus() {
        return this.mLoadMoreStatus;
    }

    @IdRes
    protected abstract int getLoadingViewId();

    @Deprecated
    public boolean isLoadEndGone() {
        return this.mLoadMoreEndGone;
    }

    public final boolean isLoadEndMoreGone() {
        if (getLoadEndViewId() == 0) {
            return true;
        }
        return this.mLoadMoreEndGone;
    }

    public final void setLoadMoreEndGone(boolean z) {
        this.mLoadMoreEndGone = z;
    }

    public void setLoadMoreStatus(int i) {
        this.mLoadMoreStatus = i;
    }
}
