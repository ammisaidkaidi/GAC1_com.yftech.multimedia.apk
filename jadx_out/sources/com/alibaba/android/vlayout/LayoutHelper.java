package com.alibaba.android.vlayout;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.layout.LayoutChunkResult;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class LayoutHelper {
    public static final Range<Integer> RANGE_ALL = Range.create(Integer.MIN_VALUE, Integer.MAX_VALUE);
    public static final Range<Integer> RANGE_EMPTY = Range.create(-1, -1);

    @NonNull
    Range<Integer> mRange = RANGE_EMPTY;
    int mZIndex = 0;

    @NonNull
    protected final List<View> mOffFlowViews = new LinkedList();

    public abstract void adjustLayout(int i, int i2, LayoutManagerHelper layoutManagerHelper);

    public abstract void afterLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper);

    public abstract void beforeLayout(RecyclerView.Recycler recycler, RecyclerView.State state, LayoutManagerHelper layoutManagerHelper);

    public abstract void bindLayoutView(View view);

    public void checkAnchorInfo(RecyclerView.State state, VirtualLayoutManager.AnchorInfoWrapper anchorInfoWrapper, LayoutManagerHelper layoutManagerHelper) {
    }

    public abstract void clear(LayoutManagerHelper layoutManagerHelper);

    public abstract int computeAlignOffset(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper);

    public abstract int computeMarginEnd(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper);

    public abstract int computeMarginStart(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper);

    public abstract int computePaddingEnd(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper);

    public abstract int computePaddingStart(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper);

    public abstract void doLayout(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper);

    @Nullable
    public View getFixedView() {
        return null;
    }

    public abstract int getItemCount();

    @NonNull
    public List<View> getOffFlowViews() {
        return this.mOffFlowViews;
    }

    @NonNull
    public final Range<Integer> getRange() {
        return this.mRange;
    }

    public int getZIndex() {
        return this.mZIndex;
    }

    public abstract boolean isFixLayout();

    public boolean isOutOfRange(int i) {
        return !this.mRange.contains(Integer.valueOf(i));
    }

    public boolean isRecyclable(int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper, boolean z) {
        return true;
    }

    public void onItemsChanged(LayoutManagerHelper layoutManagerHelper) {
    }

    public void onOffsetChildrenHorizontal(int i, LayoutManagerHelper layoutManagerHelper) {
    }

    public void onOffsetChildrenVertical(int i, LayoutManagerHelper layoutManagerHelper) {
    }

    public void onRangeChange(int i, int i2) {
    }

    public void onRefreshLayout(RecyclerView.State state, VirtualLayoutManager.AnchorInfoWrapper anchorInfoWrapper, LayoutManagerHelper layoutManagerHelper) {
    }

    public void onRestoreInstanceState(Bundle bundle) {
    }

    public void onSaveState(Bundle bundle) {
    }

    public void onScrollStateChanged(int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper) {
    }

    public abstract boolean requireLayoutView();

    public abstract void setItemCount(int i);

    public void setRange(int i, int i2) {
        if (i2 < i) {
            throw new IllegalArgumentException("end should be larger or equeal then start position");
        }
        if (i == -1 && i2 == -1) {
            this.mRange = RANGE_EMPTY;
            onRangeChange(i, i2);
            return;
        }
        if ((i2 - i) + 1 == getItemCount()) {
            if (i == ((Integer) this.mRange.getUpper()).intValue() && i2 == ((Integer) this.mRange.getLower()).intValue()) {
                return;
            }
            this.mRange = Range.create(Integer.valueOf(i), Integer.valueOf(i2));
            onRangeChange(i, i2);
            return;
        }
        throw new MismatchChildCountException("ItemCount mismatch when range: " + this.mRange.toString() + " childCount: " + getItemCount());
    }

    public void setZIndex(int i) {
        this.mZIndex = i;
    }
}
