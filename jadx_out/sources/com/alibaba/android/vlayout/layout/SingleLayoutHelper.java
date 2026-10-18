package com.alibaba.android.vlayout.layout;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.OrientationHelperEx;
import com.alibaba.android.vlayout.VirtualLayoutManager;

/* JADX INFO: loaded from: classes3.dex */
public class SingleLayoutHelper extends ColumnLayoutHelper {
    private static final String TAG = "SingleLayoutHelper";
    private int mPos = -1;

    public SingleLayoutHelper() {
        setItemCount(1);
    }

    @Override // com.alibaba.android.vlayout.layout.ColumnLayoutHelper, com.alibaba.android.vlayout.layout.AbstractFullFillLayoutHelper, com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        int i;
        int i2;
        int i3;
        int i4;
        int paddingLeft;
        int offset;
        int offset2;
        int i5;
        if (isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
            return;
        }
        View next = layoutStateWrapper.next(recycler);
        if (next == null) {
            layoutChunkResult.mFinished = true;
            return;
        }
        layoutManagerHelper.addChildView(layoutStateWrapper, next);
        VirtualLayoutManager.LayoutParams layoutParams = (VirtualLayoutManager.LayoutParams) next.getLayoutParams();
        boolean z = layoutManagerHelper.getOrientation() == 1;
        int contentWidth = (((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingLeft()) - layoutManagerHelper.getPaddingRight()) - getHorizontalMargin()) - getHorizontalPadding();
        int contentHeight = (((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom()) - getVerticalMargin()) - getVerticalPadding();
        if (!Float.isNaN(this.mAspectRatio)) {
            if (z) {
                contentHeight = (int) ((contentWidth / this.mAspectRatio) + 0.5f);
            } else {
                contentWidth = (int) ((contentHeight * this.mAspectRatio) + 0.5f);
            }
        }
        if (z) {
            int childMeasureSpec = layoutManagerHelper.getChildMeasureSpec(contentWidth, Float.isNaN(this.mAspectRatio) ? layoutParams.width : contentWidth, !z && Float.isNaN(this.mAspectRatio));
            if (Float.isNaN(layoutParams.mAspectRatio)) {
                i5 = Float.isNaN(this.mAspectRatio) ? layoutParams.height : contentHeight;
            } else {
                i5 = (int) ((contentWidth / layoutParams.mAspectRatio) + 0.5f);
            }
            layoutManagerHelper.measureChildWithMargins(next, childMeasureSpec, layoutManagerHelper.getChildMeasureSpec(contentHeight, i5, z && Float.isNaN(this.mAspectRatio)));
        } else {
            if (Float.isNaN(layoutParams.mAspectRatio)) {
                i = Float.isNaN(this.mAspectRatio) ? layoutParams.width : contentWidth;
            } else {
                i = (int) ((contentHeight * layoutParams.mAspectRatio) + 0.5f);
            }
            layoutManagerHelper.measureChildWithMargins(next, layoutManagerHelper.getChildMeasureSpec(contentWidth, i, !z && Float.isNaN(this.mAspectRatio)), layoutManagerHelper.getChildMeasureSpec(contentHeight, Float.isNaN(this.mAspectRatio) ? layoutParams.height : contentHeight, z && Float.isNaN(this.mAspectRatio)));
        }
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        layoutChunkResult.mConsumed = mainOrientationHelper.getDecoratedMeasurement(next);
        if (z) {
            int decoratedMeasurementInOther = contentWidth - mainOrientationHelper.getDecoratedMeasurementInOther(next);
            if (decoratedMeasurementInOther < 0) {
                decoratedMeasurementInOther = 0;
            }
            int i6 = decoratedMeasurementInOther / 2;
            paddingLeft = this.mMarginLeft + this.mPaddingLeft + layoutManagerHelper.getPaddingLeft() + i6;
            int contentWidth2 = (((layoutManagerHelper.getContentWidth() - this.mMarginRight) - this.mPaddingRight) - layoutManagerHelper.getPaddingRight()) - i6;
            if (layoutStateWrapper.getLayoutDirection() == -1) {
                offset2 = (layoutStateWrapper.getOffset() - this.mMarginBottom) - this.mPaddingBottom;
                offset = offset2 - layoutChunkResult.mConsumed;
            } else {
                offset = this.mPaddingTop + layoutStateWrapper.getOffset() + this.mMarginTop;
                offset2 = layoutChunkResult.mConsumed + offset;
            }
            i3 = offset2;
            i2 = contentWidth2;
            i4 = offset;
        } else {
            int decoratedMeasurementInOther2 = contentHeight - mainOrientationHelper.getDecoratedMeasurementInOther(next);
            if (decoratedMeasurementInOther2 < 0) {
                decoratedMeasurementInOther2 = 0;
            }
            int i7 = decoratedMeasurementInOther2 / 2;
            int paddingTop = layoutManagerHelper.getPaddingTop() + this.mMarginTop + this.mPaddingTop + i7;
            int contentHeight2 = (((layoutManagerHelper.getContentHeight() - (-this.mMarginBottom)) - this.mPaddingBottom) - layoutManagerHelper.getPaddingBottom()) - i7;
            if (layoutStateWrapper.getLayoutDirection() == -1) {
                int offset3 = (layoutStateWrapper.getOffset() - this.mMarginRight) - this.mPaddingRight;
                i2 = offset3;
                i3 = contentHeight2;
                i4 = paddingTop;
                paddingLeft = offset3 - layoutChunkResult.mConsumed;
            } else {
                int offset4 = layoutStateWrapper.getOffset() + this.mMarginLeft + this.mPaddingLeft;
                i2 = layoutChunkResult.mConsumed + offset4;
                i3 = contentHeight2;
                i4 = paddingTop;
                paddingLeft = offset4;
            }
        }
        if (z) {
            layoutChunkResult.mConsumed += getVerticalMargin() + getVerticalPadding();
        } else {
            layoutChunkResult.mConsumed += getHorizontalMargin() + getHorizontalPadding();
        }
        layoutChildWithMargin(next, paddingLeft, i4, i2, i3, layoutManagerHelper);
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onRangeChange(int i, int i2) {
        this.mPos = i;
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void setItemCount(int i) {
        if (i > 0) {
            super.setItemCount(1);
        } else {
            super.setItemCount(0);
        }
    }
}
