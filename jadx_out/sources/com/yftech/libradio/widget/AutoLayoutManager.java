package com.yftech.libradio.widget;

import android.graphics.Rect;
import android.util.SparseArray;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class AutoLayoutManager extends RecyclerView.LayoutManager {
    private int totalHeight = 0;
    private SparseArray<Rect> allItemframs = new SparseArray<>();
    private SparseArray<ItemLine> allLines = new SparseArray<>();
    private int verticalScrollOffset = 0;

    private class ItemLine {
        private List<Integer> frameIndexs;
        private int lineIndex;

        public ItemLine(int i, List<Integer> list) {
            this.lineIndex = i;
            this.frameIndexs = list;
        }
    }

    private void recyclerViewFillView(RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        Rect rect = new Rect(0, this.verticalScrollOffset, getWidth(), this.verticalScrollOffset + getHeight());
        new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (!Rect.intersects(rect, this.allItemframs.get(i))) {
                removeAndRecycleView(childAt, recycler);
            }
        }
        for (int i2 = 0; i2 < getItemCount(); i2++) {
            if (Rect.intersects(rect, this.allItemframs.get(i2))) {
                View viewForPosition = recycler.getViewForPosition(i2);
                measureChildWithMargins(viewForPosition, 0, 0);
                addView(viewForPosition);
                Rect rect2 = this.allItemframs.get(i2);
                layoutDecorated(viewForPosition, rect2.left, rect2.top - this.verticalScrollOffset, rect2.right, rect2.bottom - this.verticalScrollOffset);
            }
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        Rect rect;
        if (getItemCount() > 0 && !state.isPreLayout()) {
            detachAndScrapAttachedViews(recycler);
            int i = 0;
            int i2 = 0;
            int decoratedMeasuredHeight = 0;
            int i3 = 0;
            int i4 = 0;
            while (i < getItemCount()) {
                View viewForPosition = recycler.getViewForPosition(i);
                addView(viewForPosition);
                measureChildWithMargins(viewForPosition, 0, 0);
                int decoratedMeasuredWidth = getDecoratedMeasuredWidth(viewForPosition);
                decoratedMeasuredHeight = getDecoratedMeasuredHeight(viewForPosition);
                Rect rect2 = this.allItemframs.get(i);
                if (rect2 == null) {
                    rect2 = new Rect();
                }
                if (i == 0) {
                    this.allLines.put(i3, new ItemLine(i3, new ArrayList()));
                    i3++;
                }
                int i5 = i4 + decoratedMeasuredWidth;
                if (i5 > getWidth()) {
                    i2 = (i <= 0 || (rect = this.allItemframs.get(i + (-1))) == null) ? i2 + decoratedMeasuredHeight : i2 + (rect.bottom - rect.top);
                    rect2.set(0, i2, decoratedMeasuredWidth, i2 + decoratedMeasuredHeight);
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(Integer.valueOf(i));
                    this.allLines.put(i3, new ItemLine(i3, arrayList));
                    i3++;
                    i4 = decoratedMeasuredWidth;
                } else {
                    rect2.set(i4, i2, i5, i2 + decoratedMeasuredHeight);
                    this.allLines.get(i3 - 1).frameIndexs.add(Integer.valueOf(i));
                    i4 = i5;
                }
                this.allItemframs.put(i, rect2);
                i++;
            }
            this.totalHeight = i2 + decoratedMeasuredHeight;
            recyclerViewFillView(recycler, state);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        detachAndScrapAttachedViews(recycler);
        int height = getHeight();
        int i2 = this.totalHeight > height ? this.totalHeight - height : 0;
        if (this.verticalScrollOffset + i < 0) {
            i = -this.verticalScrollOffset;
        } else if (this.verticalScrollOffset + i > i2) {
            i = i2 - this.verticalScrollOffset;
        }
        this.verticalScrollOffset += i;
        offsetChildrenVertical(i);
        recyclerViewFillView(recycler, state);
        return i;
    }
}
