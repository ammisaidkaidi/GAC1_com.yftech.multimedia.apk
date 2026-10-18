package com.alibaba.android.vlayout.layout;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.OrientationHelperEx;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public class ColumnLayoutHelper extends AbstractFullFillLayoutHelper {
    private View[] mEqViews;
    private View[] mViews;
    private Rect mTempArea = new Rect();
    private float[] mWeights = new float[0];

    @Override // com.alibaba.android.vlayout.layout.AbstractFullFillLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void checkAnchorInfo(RecyclerView.State state, VirtualLayoutManager.AnchorInfoWrapper anchorInfoWrapper, LayoutManagerHelper layoutManagerHelper) {
        if (anchorInfoWrapper.layoutFromEnd) {
            anchorInfoWrapper.position = ((Integer) getRange().getUpper()).intValue();
        } else {
            anchorInfoWrapper.position = ((Integer) getRange().getLower()).intValue();
        }
    }

    @Override // com.alibaba.android.vlayout.layout.AbstractFullFillLayoutHelper, com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        int childMeasureSpec;
        int i;
        int i2;
        if (isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
            return;
        }
        boolean z = true;
        boolean z2 = layoutManagerHelper.getOrientation() == 1;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        int itemCount = getItemCount();
        if (this.mViews == null || this.mViews.length != itemCount) {
            this.mViews = new View[itemCount];
        }
        if (this.mEqViews == null || this.mEqViews.length != itemCount) {
            this.mEqViews = new View[itemCount];
        } else {
            Arrays.fill(this.mEqViews, (Object) null);
        }
        int allChildren = getAllChildren(this.mViews, recycler, layoutStateWrapper, layoutChunkResult, layoutManagerHelper);
        if (z2) {
            int i3 = 0;
            int i4 = 0;
            int iMax = 0;
            for (int i5 = 0; i5 < allChildren; i5++) {
                ViewGroup.LayoutParams layoutParams = this.mViews[i5].getLayoutParams();
                if (layoutParams instanceof RecyclerView.LayoutParams) {
                    RecyclerView.LayoutParams layoutParams2 = (RecyclerView.LayoutParams) layoutParams;
                    layoutParams2.leftMargin = Math.max(i4, layoutParams2.leftMargin);
                    i3 += layoutParams2.leftMargin;
                    if (i5 != allChildren - 1) {
                        i4 = layoutParams2.rightMargin;
                        layoutParams2.rightMargin = 0;
                    } else {
                        i3 += layoutParams2.rightMargin;
                    }
                    iMax = Math.max(iMax, layoutParams2.topMargin + layoutParams2.bottomMargin);
                }
            }
            int contentWidth = (((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingLeft()) - layoutManagerHelper.getPaddingRight()) - getHorizontalMargin()) - getHorizontalPadding();
            int i6 = contentWidth - i3;
            int i7 = Float.isNaN(this.mAspectRatio) ? -1 : (int) ((contentWidth / this.mAspectRatio) + 0.5f);
            int iMin = Integer.MAX_VALUE;
            int i8 = 0;
            int i9 = 0;
            int i10 = 0;
            while (i8 < allChildren) {
                View view = this.mViews[i8];
                VirtualLayoutManager.LayoutParams layoutParams3 = (VirtualLayoutManager.LayoutParams) view.getLayoutParams();
                int childMeasureSpec2 = layoutManagerHelper.getChildMeasureSpec((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom(), i7 > 0 ? i7 : layoutParams3.height, z);
                if (this.mWeights == null || i8 >= this.mWeights.length || Float.isNaN(this.mWeights[i8]) || this.mWeights[i8] < 0.0f) {
                    this.mEqViews[i10] = view;
                    i10++;
                } else {
                    int i11 = (int) ((((this.mWeights[i8] * 1.0f) / 100.0f) * i6) + 0.5f);
                    if (Float.isNaN(layoutParams3.mAspectRatio)) {
                        i2 = 1073741824;
                    } else {
                        int i12 = (int) ((i11 / layoutParams3.mAspectRatio) + 0.5f);
                        i2 = 1073741824;
                        childMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(i12, 1073741824);
                    }
                    layoutManagerHelper.measureChildWithMargins(view, View.MeasureSpec.makeMeasureSpec(i11, i2), childMeasureSpec2);
                    i9 += i11;
                    iMin = Math.min(iMin, view.getMeasuredHeight());
                }
                i8++;
                z = true;
            }
            for (int i13 = 0; i13 < i10; i13++) {
                View view2 = this.mEqViews[i13];
                VirtualLayoutManager.LayoutParams layoutParams4 = (VirtualLayoutManager.LayoutParams) view2.getLayoutParams();
                int i14 = (int) ((((i6 - i9) * 1.0f) / i10) + 0.5f);
                if (Float.isNaN(layoutParams4.mAspectRatio)) {
                    childMeasureSpec = layoutManagerHelper.getChildMeasureSpec((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom(), i7 > 0 ? i7 : layoutParams4.height, true);
                    i = 1073741824;
                } else {
                    i = 1073741824;
                    childMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) ((i14 / layoutParams4.mAspectRatio) + 0.5f), 1073741824);
                }
                layoutManagerHelper.measureChildWithMargins(view2, View.MeasureSpec.makeMeasureSpec(i14, i), childMeasureSpec);
                iMin = Math.min(iMin, view2.getMeasuredHeight());
            }
            for (int i15 = 0; i15 < allChildren; i15++) {
                View view3 = this.mViews[i15];
                if (view3.getMeasuredHeight() != iMin) {
                    layoutManagerHelper.measureChildWithMargins(view3, View.MeasureSpec.makeMeasureSpec(view3.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(iMin, 1073741824));
                }
            }
            int i16 = iMin + iMax;
            layoutChunkResult.mConsumed = getVerticalMargin() + i16 + getVerticalPadding();
            calculateRect(i16, this.mTempArea, layoutStateWrapper, layoutManagerHelper);
            int i17 = this.mTempArea.left;
            int i18 = 0;
            while (i18 < allChildren) {
                View view4 = this.mViews[i18];
                int i19 = this.mTempArea.top;
                int i20 = this.mTempArea.bottom;
                int decoratedMeasurementInOther = i17 + mainOrientationHelper.getDecoratedMeasurementInOther(view4);
                layoutChildWithMargin(view4, i17, i19, decoratedMeasurementInOther, i20, layoutManagerHelper);
                i18++;
                i17 = decoratedMeasurementInOther;
            }
        }
        Arrays.fill(this.mViews, (Object) null);
        Arrays.fill(this.mEqViews, (Object) null);
    }

    public void setWeights(float[] fArr) {
        if (fArr != null) {
            this.mWeights = Arrays.copyOf(fArr, fArr.length);
        } else {
            this.mWeights = new float[0];
        }
    }
}
