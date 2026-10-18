package com.alibaba.android.vlayout.layout;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.Range;
import com.alibaba.android.vlayout.VirtualLayoutManager;

/* JADX INFO: loaded from: classes3.dex */
public abstract class AbstractFullFillLayoutHelper extends BaseLayoutHelper {
    private static final String TAG = "FullFillLayoutHelper";
    private LayoutManagerHelper mTempLayoutHelper;
    protected boolean hasHeader = false;
    protected boolean hasFooter = false;
    protected boolean mLayoutWithAnchor = false;

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void afterLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper) {
        super.afterLayout(recycler, state, i, i2, i3, layoutManagerHelper);
        this.mLayoutWithAnchor = false;
    }

    protected void calculateRect(int i, Rect rect, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutManagerHelper layoutManagerHelper) {
        int i2 = 0;
        if (layoutManagerHelper.getOrientation() == 1) {
            rect.left = layoutManagerHelper.getPaddingLeft() + this.mMarginLeft + this.mPaddingLeft;
            rect.right = ((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingRight()) - this.mMarginRight) - this.mPaddingRight;
            if (layoutStateWrapper.getLayoutDirection() == -1) {
                int offset = layoutStateWrapper.getOffset();
                if (!this.mLayoutWithAnchor && !this.hasFooter) {
                    i2 = this.mMarginBottom + this.mPaddingBottom;
                }
                rect.bottom = offset - i2;
                rect.top = rect.bottom - i;
                return;
            }
            int offset2 = layoutStateWrapper.getOffset();
            if (!this.mLayoutWithAnchor && !this.hasHeader) {
                i2 = this.mMarginTop + this.mPaddingTop;
            }
            rect.top = offset2 + i2;
            rect.bottom = rect.top + i;
            return;
        }
        rect.top = layoutManagerHelper.getPaddingTop() + this.mMarginTop + this.mPaddingTop;
        rect.bottom = ((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingBottom()) - this.mMarginBottom) - this.mPaddingBottom;
        if (layoutStateWrapper.getLayoutDirection() == -1) {
            int offset3 = layoutStateWrapper.getOffset();
            if (!this.mLayoutWithAnchor && !this.hasFooter) {
                i2 = this.mMarginRight + this.mPaddingRight;
            }
            rect.right = offset3 - i2;
            rect.left = rect.right - i;
            return;
        }
        int offset4 = layoutStateWrapper.getOffset();
        if (!this.mLayoutWithAnchor && !this.hasHeader) {
            i2 = this.mMarginLeft + this.mPaddingLeft;
        }
        rect.left = offset4 + i2;
        rect.right = rect.left + i;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void checkAnchorInfo(RecyclerView.State state, VirtualLayoutManager.AnchorInfoWrapper anchorInfoWrapper, LayoutManagerHelper layoutManagerHelper) {
        if (anchorInfoWrapper.layoutFromEnd) {
            if (!this.hasFooter) {
                anchorInfoWrapper.position = ((Integer) getRange().getUpper()).intValue();
            }
        } else if (!this.hasHeader) {
            anchorInfoWrapper.position = ((Integer) getRange().getLower()).intValue();
        }
        this.mLayoutWithAnchor = true;
    }

    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return true;
    }

    @Override // com.alibaba.android.vlayout.layout.MarginLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public int computeAlignOffset(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper) {
        if (layoutManagerHelper.getOrientation() == 1) {
            return z ? this.mMarginBottom + this.mPaddingBottom : (-this.mMarginTop) - this.mPaddingTop;
        }
        return z ? this.mMarginRight + this.mPaddingRight : (-this.mMarginLeft) - this.mPaddingLeft;
    }

    protected void doLayoutView(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
    }

    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.LayoutParams(-1, -2);
    }

    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return new ViewGroup.LayoutParams(layoutParams);
    }

    protected int getAllChildren(View[] viewArr, RecyclerView.Recycler recycler, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        View viewNextView;
        int i = 0;
        boolean z = layoutStateWrapper.getItemDirection() == 1;
        int iIntValue = ((Integer) (z ? getRange().getLower() : getRange().getUpper())).intValue();
        int currentPosition = layoutStateWrapper.getCurrentPosition();
        if (!z ? currentPosition > iIntValue : currentPosition > iIntValue) {
            Log.w(TAG, "Please handle strange order views carefully");
        }
        int i2 = 0;
        while (i2 < viewArr.length && !isOutOfRange(layoutStateWrapper.getCurrentPosition()) && (viewNextView = nextView(recycler, layoutStateWrapper, layoutManagerHelper, layoutChunkResult)) != null) {
            viewArr[i2] = viewNextView;
            ViewGroup.LayoutParams layoutParams = viewNextView.getLayoutParams();
            if (layoutParams == null) {
                viewNextView.setLayoutParams(generateDefaultLayoutParams());
            } else if (!checkLayoutParams(layoutParams)) {
                viewNextView.setLayoutParams(generateLayoutParams(layoutParams));
            }
            i2++;
        }
        if (i2 > 0 && !z) {
            for (int i3 = i2 - 1; i < i3; i3--) {
                View view = viewArr[i];
                viewArr[i] = viewArr[i3];
                viewArr[i3] = view;
                i++;
            }
        }
        return i2;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public boolean isRecyclable(int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper, boolean z) {
        Range<Integer> range = getRange();
        if (!range.contains(Integer.valueOf(i))) {
            Log.w(TAG, "Child item not match");
            return true;
        }
        if (this.hasHeader && i == ((Integer) getRange().getLower()).intValue()) {
            return true;
        }
        if (this.hasFooter && i == ((Integer) getRange().getUpper()).intValue()) {
            return true;
        }
        return Range.create(Integer.valueOf(i2), Integer.valueOf(i3)).contains(Range.create(Integer.valueOf(((Integer) range.getLower()).intValue() + (this.hasHeader ? 1 : 0)), Integer.valueOf(((Integer) range.getUpper()).intValue() - (this.hasFooter ? 1 : 0))));
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        this.mTempLayoutHelper = layoutManagerHelper;
        doLayoutView(recycler, state, layoutStateWrapper, layoutChunkResult, layoutManagerHelper);
        this.mTempLayoutHelper = null;
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
    }

    protected void onMeasure(int i, int i2) {
    }

    public void setHasFooter(boolean z) {
        this.hasFooter = z;
    }

    public void setHasHeader(boolean z) {
        this.hasHeader = z;
    }

    protected final void setMeasuredDimension(int i, int i2) {
    }
}
