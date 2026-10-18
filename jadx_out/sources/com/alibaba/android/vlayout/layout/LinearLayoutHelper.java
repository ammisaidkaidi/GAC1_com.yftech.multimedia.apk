package com.alibaba.android.vlayout.layout;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.OrientationHelperEx;
import com.alibaba.android.vlayout.VirtualLayoutManager;

/* JADX INFO: loaded from: classes3.dex */
public class LinearLayoutHelper extends BaseLayoutHelper {
    private static final boolean DEBUG = false;
    private static final String TAG = "LinearLayoutHelper";
    private int mDividerHeight;

    public LinearLayoutHelper() {
        this(0);
    }

    public LinearLayoutHelper(int i) {
        this(i, 0);
    }

    public LinearLayoutHelper(int i, int i2) {
        this.mDividerHeight = 0;
        setItemCount(i2);
        setDividerHeight(i);
    }

    @Override // com.alibaba.android.vlayout.layout.MarginLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public int computeAlignOffset(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper) {
        int i2;
        int i3;
        int i4;
        int i5;
        boolean z3 = layoutManagerHelper.getOrientation() == 1;
        if (z) {
            if (i == getItemCount() - 1) {
                if (z3) {
                    i4 = this.mMarginBottom;
                    i5 = this.mPaddingBottom;
                } else {
                    i4 = this.mMarginRight;
                    i5 = this.mPaddingRight;
                }
                return i4 + i5;
            }
        } else if (i == 0) {
            if (z3) {
                i2 = -this.mMarginTop;
                i3 = this.mPaddingTop;
            } else {
                i2 = -this.mMarginLeft;
                i3 = this.mPaddingLeft;
            }
            return i2 - i3;
        }
        return super.computeAlignOffset(i, z, z2, layoutManagerHelper);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        int iMax;
        int childMeasureSpec;
        int paddingTop;
        int decoratedMeasurementInOther;
        int offset;
        int decoratedMeasurement;
        int paddingLeft;
        int decoratedMeasurementInOther2;
        if (isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
            return;
        }
        int currentPosition = layoutStateWrapper.getCurrentPosition();
        View viewNextView = nextView(recycler, layoutStateWrapper, layoutManagerHelper, layoutChunkResult);
        if (viewNextView == null) {
            return;
        }
        boolean zIsEnableMarginOverLap = layoutManagerHelper.isEnableMarginOverLap();
        VirtualLayoutManager.LayoutParams layoutParams = (VirtualLayoutManager.LayoutParams) viewNextView.getLayoutParams();
        boolean z = layoutManagerHelper.getOrientation() == 1;
        boolean z2 = layoutStateWrapper.getLayoutDirection() == 1;
        boolean z3 = !z2 ? currentPosition != ((Integer) getRange().getUpper()).intValue() : currentPosition != ((Integer) getRange().getLower()).intValue();
        boolean z4 = !z2 ? currentPosition != ((Integer) getRange().getLower()).intValue() : currentPosition != ((Integer) getRange().getUpper()).intValue();
        int iComputeStartSpace = z3 ? computeStartSpace(layoutManagerHelper, z, z2, zIsEnableMarginOverLap) : 0;
        int iComputeEndSpace = z4 ? computeEndSpace(layoutManagerHelper, z, z2, zIsEnableMarginOverLap) : 0;
        if (z3) {
            iMax = 0;
        } else if (!zIsEnableMarginOverLap) {
            iMax = this.mDividerHeight;
        } else if (z2) {
            int i = layoutParams.topMargin;
            View viewFindViewByPosition = layoutManagerHelper.findViewByPosition(currentPosition - 1);
            int i2 = viewFindViewByPosition != null ? ((VirtualLayoutManager.LayoutParams) viewFindViewByPosition.getLayoutParams()).bottomMargin : 0;
            iMax = (i2 < 0 || i < 0) ? i2 + i : Math.max(i2, i);
        } else {
            int i3 = layoutParams.bottomMargin;
            View viewFindViewByPosition2 = layoutManagerHelper.findViewByPosition(currentPosition + 1);
            int i4 = viewFindViewByPosition2 != null ? ((VirtualLayoutManager.LayoutParams) viewFindViewByPosition2.getLayoutParams()).topMargin : 0;
            iMax = (i3 < 0 || i4 < 0) ? i4 + i3 : Math.max(i3, i4);
        }
        int contentWidth = (((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingLeft()) - layoutManagerHelper.getPaddingRight()) - getHorizontalMargin()) - getHorizontalPadding();
        int childMeasureSpec2 = layoutManagerHelper.getChildMeasureSpec(contentWidth, layoutParams.width, !z);
        float f = layoutParams.mAspectRatio;
        if (Float.isNaN(f) || f <= 0.0f) {
            childMeasureSpec = (Float.isNaN(this.mAspectRatio) || this.mAspectRatio <= 0.0f) ? layoutManagerHelper.getChildMeasureSpec((((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom()) - getVerticalMargin()) - getVerticalPadding(), layoutParams.height, z) : View.MeasureSpec.makeMeasureSpec((int) (((double) (contentWidth / this.mAspectRatio)) + 0.5d), 1073741824);
        } else {
            childMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) ((contentWidth / f) + 0.5f), 1073741824);
        }
        if (zIsEnableMarginOverLap) {
            layoutManagerHelper.measureChild(viewNextView, childMeasureSpec2, childMeasureSpec);
        } else {
            layoutManagerHelper.measureChildWithMargins(viewNextView, childMeasureSpec2, childMeasureSpec);
        }
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        layoutChunkResult.mConsumed = mainOrientationHelper.getDecoratedMeasurement(viewNextView) + iComputeStartSpace + iComputeEndSpace + iMax;
        if (layoutManagerHelper.getOrientation() == 1) {
            if (layoutManagerHelper.isDoLayoutRTL()) {
                decoratedMeasurementInOther2 = ((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingRight()) - this.mMarginRight) - this.mPaddingRight;
                paddingLeft = decoratedMeasurementInOther2 - mainOrientationHelper.getDecoratedMeasurementInOther(viewNextView);
            } else {
                paddingLeft = this.mPaddingLeft + layoutManagerHelper.getPaddingLeft() + this.mMarginLeft;
                decoratedMeasurementInOther2 = mainOrientationHelper.getDecoratedMeasurementInOther(viewNextView) + paddingLeft;
            }
            if (layoutStateWrapper.getLayoutDirection() == -1) {
                int offset2 = (layoutStateWrapper.getOffset() - iComputeStartSpace) - (z3 ? 0 : iMax);
                decoratedMeasurement = decoratedMeasurementInOther2;
                paddingTop = offset2 - mainOrientationHelper.getDecoratedMeasurement(viewNextView);
                int i5 = paddingLeft;
                decoratedMeasurementInOther = offset2;
                offset = i5;
            } else {
                int offset3 = layoutStateWrapper.getOffset() + iComputeStartSpace + (z3 ? 0 : iMax);
                int decoratedMeasurement2 = mainOrientationHelper.getDecoratedMeasurement(viewNextView) + offset3;
                decoratedMeasurement = decoratedMeasurementInOther2;
                paddingTop = offset3;
                offset = paddingLeft;
                decoratedMeasurementInOther = decoratedMeasurement2;
            }
        } else {
            paddingTop = layoutManagerHelper.getPaddingTop() + this.mMarginTop + this.mPaddingTop;
            decoratedMeasurementInOther = mainOrientationHelper.getDecoratedMeasurementInOther(viewNextView) + paddingTop;
            if (layoutStateWrapper.getLayoutDirection() == -1) {
                int offset4 = (layoutStateWrapper.getOffset() - iComputeStartSpace) - (z3 ? 0 : iMax);
                decoratedMeasurement = offset4;
                offset = offset4 - mainOrientationHelper.getDecoratedMeasurement(viewNextView);
            } else {
                offset = layoutStateWrapper.getOffset() + iComputeStartSpace + (z3 ? 0 : iMax);
                decoratedMeasurement = mainOrientationHelper.getDecoratedMeasurement(viewNextView) + offset;
            }
        }
        layoutChildWithMargin(viewNextView, offset, paddingTop, decoratedMeasurement, decoratedMeasurementInOther, layoutManagerHelper);
        handleStateOnResult(layoutChunkResult, viewNextView);
    }

    public void setDividerHeight(int i) {
        if (i < 0) {
            i = 0;
        }
        this.mDividerHeight = i;
    }
}
