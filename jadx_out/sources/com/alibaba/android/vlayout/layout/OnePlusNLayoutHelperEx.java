package com.alibaba.android.vlayout.layout;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.OrientationHelperEx;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public class OnePlusNLayoutHelperEx extends AbstractFullFillLayoutHelper {
    private static final String TAG = "OnePlusNLayoutHelper";
    private Rect mAreaRect;
    private View[] mChildrenViews;
    private float[] mColWeights;
    private float mRowWeight;

    public OnePlusNLayoutHelperEx() {
        this.mAreaRect = new Rect();
        this.mColWeights = new float[0];
        this.mRowWeight = Float.NaN;
        setItemCount(0);
    }

    public OnePlusNLayoutHelperEx(int i) {
        this(i, 0, 0, 0, 0);
    }

    public OnePlusNLayoutHelperEx(int i, int i2, int i3, int i4, int i5) {
        this.mAreaRect = new Rect();
        this.mColWeights = new float[0];
        this.mRowWeight = Float.NaN;
        setItemCount(i);
    }

    private float getViewMainWeight(ViewGroup.MarginLayoutParams marginLayoutParams, int i) {
        if (this.mColWeights.length > i) {
            return this.mColWeights[i];
        }
        return Float.NaN;
    }

    private int handSeven(VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper, boolean z, int i, int i2, int i3, int i4) {
        View view;
        View view2;
        View view3;
        View view4;
        char c;
        View view5;
        int i5;
        int i6;
        char c2;
        int i7;
        float f;
        int i8;
        float f2;
        float f3;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        View view6 = this.mChildrenViews[0];
        VirtualLayoutManager.LayoutParams layoutParams = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view6.getLayoutParams());
        View view7 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[6] : this.mChildrenViews[1];
        VirtualLayoutManager.LayoutParams layoutParams2 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view7.getLayoutParams());
        View view8 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[5] : this.mChildrenViews[2];
        VirtualLayoutManager.LayoutParams layoutParams3 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view8.getLayoutParams());
        View view9 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[4] : this.mChildrenViews[3];
        VirtualLayoutManager.LayoutParams layoutParams4 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view9.getLayoutParams());
        View view10 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[3] : this.mChildrenViews[4];
        VirtualLayoutManager.LayoutParams layoutParams5 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view10.getLayoutParams());
        View view11 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[2] : this.mChildrenViews[5];
        View view12 = view11;
        VirtualLayoutManager.LayoutParams layoutParams6 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view11.getLayoutParams());
        View view13 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[1] : this.mChildrenViews[6];
        View view14 = view13;
        VirtualLayoutManager.LayoutParams layoutParams7 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view13.getLayoutParams());
        float viewMainWeight = getViewMainWeight(layoutParams, 0);
        float viewMainWeight2 = getViewMainWeight(layoutParams, 1);
        float viewMainWeight3 = getViewMainWeight(layoutParams, 2);
        float viewMainWeight4 = getViewMainWeight(layoutParams, 3);
        float viewMainWeight5 = getViewMainWeight(layoutParams, 4);
        float viewMainWeight6 = getViewMainWeight(layoutParams, 5);
        float viewMainWeight7 = getViewMainWeight(layoutParams, 6);
        if (z) {
            if (!Float.isNaN(this.mAspectRatio)) {
                layoutParams.height = (int) ((i - i3) / this.mAspectRatio);
            }
            int i9 = ((((((i - i3) - layoutParams.leftMargin) - layoutParams.rightMargin) - layoutParams2.leftMargin) - layoutParams2.rightMargin) - layoutParams3.leftMargin) - layoutParams3.rightMargin;
            int i10 = (int) ((Float.isNaN(viewMainWeight) ? i9 / 3.0f : (i9 * viewMainWeight) / 100.0f) + 0.5f);
            if (Float.isNaN(viewMainWeight2)) {
                c2 = 2;
                i7 = (i9 - i10) / 2;
            } else {
                c2 = 2;
                i7 = (int) (((i9 * viewMainWeight2) / 100.0f) + 0.5f);
            }
            int i11 = Float.isNaN(viewMainWeight3) ? i7 : (int) (((double) ((i9 * viewMainWeight3) / 100.0f)) + 0.5d);
            if (Float.isNaN(viewMainWeight4)) {
                i8 = i7;
                f = 100.0f;
            } else {
                f = 100.0f;
                i8 = (int) (((i9 * viewMainWeight4) / 100.0f) + 0.5f);
            }
            int i12 = Float.isNaN(viewMainWeight5) ? i7 : (int) (((i9 * viewMainWeight5) / f) + 0.5f);
            int i13 = Float.isNaN(viewMainWeight6) ? i7 : (int) (((i9 * viewMainWeight6) / f) + 0.5f);
            int i14 = Float.isNaN(viewMainWeight6) ? i7 : (int) (((i9 * viewMainWeight7) / f) + 0.5f);
            layoutManagerHelper.measureChildWithMargins(view6, View.MeasureSpec.makeMeasureSpec(i10 + layoutParams.leftMargin + layoutParams.rightMargin, 1073741824), layoutManagerHelper.getChildMeasureSpec(layoutManagerHelper.getContentHeight(), layoutParams.height, true));
            int measuredHeight = view6.getMeasuredHeight();
            if (Float.isNaN(this.mRowWeight)) {
                f2 = (measuredHeight - layoutParams2.bottomMargin) - layoutParams3.topMargin;
                f3 = 3.0f;
            } else {
                f2 = ((measuredHeight - layoutParams2.bottomMargin) - layoutParams3.topMargin) * this.mRowWeight;
                f3 = 100.0f;
            }
            int i15 = (int) ((f2 / f3) + 0.5f);
            layoutManagerHelper.measureChildWithMargins(view7, View.MeasureSpec.makeMeasureSpec(i7 + layoutParams2.leftMargin + layoutParams2.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams2.topMargin + i15 + layoutParams2.bottomMargin, 1073741824));
            layoutManagerHelper.measureChildWithMargins(view8, View.MeasureSpec.makeMeasureSpec(i11 + layoutParams3.leftMargin + layoutParams3.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams3.topMargin + i15 + layoutParams3.bottomMargin, 1073741824));
            View view15 = view9;
            layoutManagerHelper.measureChildWithMargins(view15, View.MeasureSpec.makeMeasureSpec(i8 + layoutParams4.leftMargin + layoutParams4.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams4.topMargin + i15 + layoutParams4.bottomMargin, 1073741824));
            int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i12 + layoutParams5.leftMargin + layoutParams5.rightMargin, 1073741824);
            int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(layoutParams5.topMargin + i15 + layoutParams5.bottomMargin, 1073741824);
            View view16 = view10;
            layoutManagerHelper.measureChildWithMargins(view16, iMakeMeasureSpec, iMakeMeasureSpec2);
            layoutManagerHelper.measureChildWithMargins(view12, View.MeasureSpec.makeMeasureSpec(i13 + layoutParams6.leftMargin + layoutParams6.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams6.topMargin + i15 + layoutParams6.bottomMargin, 1073741824));
            layoutManagerHelper.measureChildWithMargins(view14, View.MeasureSpec.makeMeasureSpec(i14 + layoutParams7.leftMargin + layoutParams7.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams7.topMargin + i15 + layoutParams7.bottomMargin, 1073741824));
            int iMax = Math.max(measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin, Math.max(layoutParams2.topMargin + i15 + layoutParams2.bottomMargin, layoutParams3.topMargin + i15 + layoutParams3.bottomMargin) + Math.max(layoutParams4.topMargin + i15 + layoutParams4.bottomMargin, layoutParams5.topMargin + i15 + layoutParams5.bottomMargin) + Math.max(layoutParams6.topMargin + i15 + layoutParams6.bottomMargin, i15 + layoutParams7.topMargin + layoutParams7.bottomMargin)) + getVerticalMargin() + getVerticalPadding();
            calculateRect((iMax - getVerticalMargin()) - getVerticalPadding(), this.mAreaRect, layoutStateWrapper, layoutManagerHelper);
            view3 = view6;
            int decoratedMeasurementInOther = this.mAreaRect.left + mainOrientationHelper.getDecoratedMeasurementInOther(view3);
            view = view8;
            i6 = iMax;
            c = c2;
            view5 = view16;
            layoutChildWithMargin(view3, this.mAreaRect.left, this.mAreaRect.top, decoratedMeasurementInOther, this.mAreaRect.bottom, layoutManagerHelper);
            int decoratedMeasurementInOther2 = decoratedMeasurementInOther + mainOrientationHelper.getDecoratedMeasurementInOther(view7);
            layoutChildWithMargin(view7, decoratedMeasurementInOther, this.mAreaRect.top, decoratedMeasurementInOther2, this.mAreaRect.top + mainOrientationHelper.getDecoratedMeasurement(view7), layoutManagerHelper);
            layoutChildWithMargin(view, decoratedMeasurementInOther2, this.mAreaRect.top, decoratedMeasurementInOther2 + mainOrientationHelper.getDecoratedMeasurementInOther(view), this.mAreaRect.top + mainOrientationHelper.getDecoratedMeasurement(view), layoutManagerHelper);
            int decoratedMeasurementInOther3 = decoratedMeasurementInOther + mainOrientationHelper.getDecoratedMeasurementInOther(view15);
            view2 = view15;
            view4 = view12;
            layoutChildWithMargin(view15, decoratedMeasurementInOther, this.mAreaRect.top + mainOrientationHelper.getDecoratedMeasurement(view7), decoratedMeasurementInOther3, this.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view12), layoutManagerHelper);
            layoutChildWithMargin(view5, decoratedMeasurementInOther3, this.mAreaRect.top + mainOrientationHelper.getDecoratedMeasurement(view7), decoratedMeasurementInOther3 + mainOrientationHelper.getDecoratedMeasurementInOther(view5), this.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view14), layoutManagerHelper);
            int decoratedMeasurementInOther4 = decoratedMeasurementInOther + mainOrientationHelper.getDecoratedMeasurementInOther(view4);
            layoutChildWithMargin(view4, decoratedMeasurementInOther, this.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view4), decoratedMeasurementInOther4, this.mAreaRect.bottom, layoutManagerHelper);
            layoutChildWithMargin(view14, decoratedMeasurementInOther4, this.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view14), decoratedMeasurementInOther4 + mainOrientationHelper.getDecoratedMeasurementInOther(view14), this.mAreaRect.bottom, layoutManagerHelper);
            i5 = 6;
        } else {
            view = view8;
            view2 = view9;
            view3 = view6;
            view4 = view12;
            c = 2;
            view5 = view10;
            i5 = 6;
            i6 = 0;
        }
        View[] viewArr = new View[i5];
        viewArr[0] = view3;
        viewArr[1] = view7;
        viewArr[c] = view;
        viewArr[3] = view2;
        viewArr[4] = view5;
        viewArr[5] = view4;
        handleStateOnResult(layoutChunkResult, viewArr);
        return i6;
    }

    private int handSix(VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper, boolean z, int i, int i2, int i3, int i4) {
        OnePlusNLayoutHelperEx onePlusNLayoutHelperEx;
        View view;
        View view2;
        char c;
        View view3;
        View view4;
        View view5;
        int iMax;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        View view6 = this.mChildrenViews[0];
        VirtualLayoutManager.LayoutParams layoutParams = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view6.getLayoutParams());
        View view7 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[5] : this.mChildrenViews[1];
        VirtualLayoutManager.LayoutParams layoutParams2 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view7.getLayoutParams());
        View view8 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[4] : this.mChildrenViews[2];
        VirtualLayoutManager.LayoutParams layoutParams3 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view8.getLayoutParams());
        View view9 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[3] : this.mChildrenViews[3];
        VirtualLayoutManager.LayoutParams layoutParams4 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view9.getLayoutParams());
        View view10 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[2] : this.mChildrenViews[4];
        VirtualLayoutManager.LayoutParams layoutParams5 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view10.getLayoutParams());
        View view11 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[1] : this.mChildrenViews[5];
        View view12 = view11;
        VirtualLayoutManager.LayoutParams layoutParams6 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view11.getLayoutParams());
        float viewMainWeight = getViewMainWeight(layoutParams, 0);
        float viewMainWeight2 = getViewMainWeight(layoutParams, 1);
        float viewMainWeight3 = getViewMainWeight(layoutParams, 2);
        float viewMainWeight4 = getViewMainWeight(layoutParams, 3);
        float viewMainWeight5 = getViewMainWeight(layoutParams, 4);
        float viewMainWeight6 = getViewMainWeight(layoutParams, 5);
        if (z) {
            layoutParams2.topMargin = layoutParams.topMargin;
            int i5 = layoutParams.bottomMargin;
            layoutParams4.bottomMargin = i5;
            layoutParams3.bottomMargin = i5;
            layoutParams3.leftMargin = layoutParams2.leftMargin;
            layoutParams4.rightMargin = layoutParams2.rightMargin;
            layoutParams5.rightMargin = layoutParams2.rightMargin;
            if (!Float.isNaN(this.mAspectRatio)) {
                layoutParams.height = (int) ((i - i3) / this.mAspectRatio);
            }
            int i6 = i - i3;
            int i7 = (((i6 - layoutParams.leftMargin) - layoutParams.rightMargin) - layoutParams2.leftMargin) - layoutParams2.rightMargin;
            int i8 = (int) ((Float.isNaN(viewMainWeight) ? i7 / 2.0f : (i7 * viewMainWeight) / 100.0f) + 0.5f);
            int i9 = Float.isNaN(viewMainWeight2) ? i7 - i8 : (int) (((i7 * viewMainWeight2) / 100.0f) + 0.5f);
            if (!Float.isNaN(viewMainWeight3)) {
                i9 = (int) (((double) ((i7 * viewMainWeight3) / 100.0f)) + 0.5d);
            }
            int i10 = (int) ((Float.isNaN(viewMainWeight4) ? ((((((i6 - layoutParams4.leftMargin) - layoutParams4.rightMargin) - layoutParams5.leftMargin) - layoutParams5.rightMargin) - layoutParams6.leftMargin) - layoutParams6.rightMargin) / 3.0f : (i7 * viewMainWeight4) / 100.0f) + 0.5f);
            int i11 = Float.isNaN(viewMainWeight5) ? i10 : (int) (((i7 * viewMainWeight5) / 100.0f) + 0.5f);
            int i12 = Float.isNaN(viewMainWeight6) ? i10 : (int) (((i7 * viewMainWeight6) / 100.0f) + 0.5f);
            layoutManagerHelper.measureChildWithMargins(view6, View.MeasureSpec.makeMeasureSpec(i8 + layoutParams.leftMargin + layoutParams.rightMargin, 1073741824), layoutManagerHelper.getChildMeasureSpec(layoutManagerHelper.getContentHeight(), layoutParams.height, true));
            int measuredHeight = view6.getMeasuredHeight();
            int i13 = (int) ((Float.isNaN(this.mRowWeight) ? ((measuredHeight - layoutParams2.bottomMargin) - layoutParams3.topMargin) / 2.0f : (((measuredHeight - layoutParams2.bottomMargin) - layoutParams3.topMargin) * this.mRowWeight) / 100.0f) + 0.5f);
            int i14 = ((measuredHeight - layoutParams2.bottomMargin) - layoutParams3.topMargin) - i13;
            view = view7;
            layoutManagerHelper.measureChildWithMargins(view, View.MeasureSpec.makeMeasureSpec(i9 + layoutParams2.leftMargin + layoutParams2.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams2.topMargin + i13 + layoutParams2.bottomMargin, 1073741824));
            layoutManagerHelper.measureChildWithMargins(view8, View.MeasureSpec.makeMeasureSpec(i9 + layoutParams3.leftMargin + layoutParams3.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams3.topMargin + i14 + layoutParams3.bottomMargin, 1073741824));
            view4 = view9;
            layoutManagerHelper.measureChildWithMargins(view4, View.MeasureSpec.makeMeasureSpec(i10 + layoutParams4.leftMargin + layoutParams4.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams4.topMargin + i14 + layoutParams4.bottomMargin, 1073741824));
            view5 = view10;
            layoutManagerHelper.measureChildWithMargins(view5, View.MeasureSpec.makeMeasureSpec(i11 + layoutParams5.leftMargin + layoutParams5.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams5.topMargin + i14 + layoutParams5.bottomMargin, 1073741824));
            layoutManagerHelper.measureChildWithMargins(view12, View.MeasureSpec.makeMeasureSpec(i12 + layoutParams6.leftMargin + layoutParams6.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams6.topMargin + i14 + layoutParams6.bottomMargin, 1073741824));
            iMax = Math.max(measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin, (i13 + layoutParams2.topMargin + layoutParams2.bottomMargin) * 2) + Math.max(layoutParams4.topMargin + i14 + layoutParams4.bottomMargin, Math.max(layoutParams5.topMargin + i14 + layoutParams5.bottomMargin, i14 + layoutParams6.topMargin + layoutParams6.bottomMargin)) + getVerticalMargin() + getVerticalPadding();
            onePlusNLayoutHelperEx = this;
            onePlusNLayoutHelperEx.calculateRect((iMax - getVerticalMargin()) - getVerticalPadding(), onePlusNLayoutHelperEx.mAreaRect, layoutStateWrapper, layoutManagerHelper);
            int decoratedMeasurementInOther = onePlusNLayoutHelperEx.mAreaRect.left + mainOrientationHelper.getDecoratedMeasurementInOther(view6);
            view3 = view8;
            view2 = view12;
            c = 2;
            view6 = view6;
            layoutChildWithMargin(view6, onePlusNLayoutHelperEx.mAreaRect.left, onePlusNLayoutHelperEx.mAreaRect.top, decoratedMeasurementInOther, onePlusNLayoutHelperEx.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view4), layoutManagerHelper);
            layoutChildWithMargin(view, decoratedMeasurementInOther, onePlusNLayoutHelperEx.mAreaRect.top, decoratedMeasurementInOther + mainOrientationHelper.getDecoratedMeasurementInOther(view), onePlusNLayoutHelperEx.mAreaRect.top + mainOrientationHelper.getDecoratedMeasurement(view), layoutManagerHelper);
            layoutChildWithMargin(view3, decoratedMeasurementInOther, onePlusNLayoutHelperEx.mAreaRect.top + mainOrientationHelper.getDecoratedMeasurement(view3), decoratedMeasurementInOther + mainOrientationHelper.getDecoratedMeasurementInOther(view3), onePlusNLayoutHelperEx.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view4), layoutManagerHelper);
            int decoratedMeasurementInOther2 = onePlusNLayoutHelperEx.mAreaRect.left + mainOrientationHelper.getDecoratedMeasurementInOther(view4);
            layoutChildWithMargin(view4, onePlusNLayoutHelperEx.mAreaRect.left, onePlusNLayoutHelperEx.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view4), decoratedMeasurementInOther2, onePlusNLayoutHelperEx.mAreaRect.bottom, layoutManagerHelper);
            int decoratedMeasurementInOther3 = decoratedMeasurementInOther2 + mainOrientationHelper.getDecoratedMeasurementInOther(view5);
            layoutChildWithMargin(view5, decoratedMeasurementInOther2, onePlusNLayoutHelperEx.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view5), decoratedMeasurementInOther3, onePlusNLayoutHelperEx.mAreaRect.bottom, layoutManagerHelper);
            layoutChildWithMargin(view2, decoratedMeasurementInOther3, onePlusNLayoutHelperEx.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view2), decoratedMeasurementInOther3 + mainOrientationHelper.getDecoratedMeasurementInOther(view2), onePlusNLayoutHelperEx.mAreaRect.bottom, layoutManagerHelper);
        } else {
            onePlusNLayoutHelperEx = this;
            view = view7;
            view2 = view12;
            c = 2;
            view3 = view8;
            View view13 = view10;
            view4 = view9;
            view5 = view13;
            iMax = 0;
        }
        View[] viewArr = new View[6];
        viewArr[0] = view6;
        viewArr[1] = view;
        viewArr[c] = view3;
        viewArr[3] = view4;
        viewArr[4] = view5;
        viewArr[5] = view2;
        onePlusNLayoutHelperEx.handleStateOnResult(layoutChunkResult, viewArr);
        return iMax;
    }

    private int handleFive(VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper, boolean z, int i, int i2, int i3, int i4) {
        View view;
        View view2;
        char c;
        View view3;
        int iMax;
        char c2;
        float f;
        int i5;
        float f2;
        float f3;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        View view4 = this.mChildrenViews[0];
        VirtualLayoutManager.LayoutParams layoutParams = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view4.getLayoutParams());
        View view5 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[4] : this.mChildrenViews[1];
        VirtualLayoutManager.LayoutParams layoutParams2 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view5.getLayoutParams());
        View view6 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[3] : this.mChildrenViews[2];
        VirtualLayoutManager.LayoutParams layoutParams3 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view6.getLayoutParams());
        View view7 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[2] : this.mChildrenViews[3];
        VirtualLayoutManager.LayoutParams layoutParams4 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view7.getLayoutParams());
        View view8 = layoutManagerHelper.getReverseLayout() ? this.mChildrenViews[1] : this.mChildrenViews[4];
        VirtualLayoutManager.LayoutParams layoutParams5 = new VirtualLayoutManager.LayoutParams((RecyclerView.LayoutParams) view8.getLayoutParams());
        float viewMainWeight = getViewMainWeight(layoutParams, 0);
        float viewMainWeight2 = getViewMainWeight(layoutParams, 1);
        float viewMainWeight3 = getViewMainWeight(layoutParams, 2);
        float viewMainWeight4 = getViewMainWeight(layoutParams, 3);
        float viewMainWeight5 = getViewMainWeight(layoutParams, 4);
        if (z) {
            layoutParams2.topMargin = layoutParams.topMargin;
            int i6 = layoutParams.bottomMargin;
            layoutParams4.bottomMargin = i6;
            layoutParams3.bottomMargin = i6;
            layoutParams3.leftMargin = layoutParams2.leftMargin;
            layoutParams4.rightMargin = layoutParams2.rightMargin;
            layoutParams5.rightMargin = layoutParams3.rightMargin;
            if (!Float.isNaN(this.mAspectRatio)) {
                layoutParams.height = (int) ((i - i3) / this.mAspectRatio);
            }
            int i7 = ((((((i - i3) - layoutParams.leftMargin) - layoutParams.rightMargin) - layoutParams2.leftMargin) - layoutParams2.rightMargin) - layoutParams3.leftMargin) - layoutParams3.rightMargin;
            int i8 = (int) ((Float.isNaN(viewMainWeight) ? i7 / 3.0f : (i7 * viewMainWeight) / 100.0f) + 0.5f);
            if (Float.isNaN(viewMainWeight2)) {
                c2 = 2;
                f = 0.5f;
                i5 = (i7 - i8) / 2;
            } else {
                c2 = 2;
                f = 0.5f;
                i5 = (int) (((i7 * viewMainWeight2) / 100.0f) + 0.5f);
            }
            int i9 = Float.isNaN(viewMainWeight3) ? i5 : (int) (((i7 * viewMainWeight3) / 100.0f) + f);
            int i10 = Float.isNaN(viewMainWeight4) ? i5 : (int) (((i7 * viewMainWeight4) / 100.0f) + f);
            int i11 = Float.isNaN(viewMainWeight5) ? i5 : (int) (((i7 * viewMainWeight5) / 100.0f) + f);
            layoutManagerHelper.measureChildWithMargins(view4, View.MeasureSpec.makeMeasureSpec(i8 + layoutParams.leftMargin + layoutParams.rightMargin, 1073741824), layoutManagerHelper.getChildMeasureSpec(layoutManagerHelper.getContentHeight(), layoutParams.height, true));
            int measuredHeight = view4.getMeasuredHeight();
            if (Float.isNaN(this.mRowWeight)) {
                f3 = ((measuredHeight - layoutParams2.bottomMargin) - layoutParams3.topMargin) / 2.0f;
                f2 = 0.5f;
            } else {
                f2 = 0.5f;
                f3 = (((measuredHeight - layoutParams2.bottomMargin) - layoutParams3.topMargin) * this.mRowWeight) / 100.0f;
            }
            int i12 = (int) (f3 + f2);
            int i13 = ((measuredHeight - layoutParams2.bottomMargin) - layoutParams3.topMargin) - i12;
            layoutManagerHelper.measureChildWithMargins(view5, View.MeasureSpec.makeMeasureSpec(i5 + layoutParams2.leftMargin + layoutParams2.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams2.topMargin + i12 + layoutParams2.bottomMargin, 1073741824));
            layoutManagerHelper.measureChildWithMargins(view6, View.MeasureSpec.makeMeasureSpec(i9 + layoutParams3.leftMargin + layoutParams3.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams3.topMargin + i13 + layoutParams3.bottomMargin, 1073741824));
            layoutManagerHelper.measureChildWithMargins(view7, View.MeasureSpec.makeMeasureSpec(i10 + layoutParams4.leftMargin + layoutParams4.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams4.topMargin + i13 + layoutParams4.bottomMargin, 1073741824));
            view3 = view8;
            layoutManagerHelper.measureChildWithMargins(view3, View.MeasureSpec.makeMeasureSpec(i11 + layoutParams5.leftMargin + layoutParams5.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams5.topMargin + i13 + layoutParams5.bottomMargin, 1073741824));
            iMax = Math.max(measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin, i12 + layoutParams2.topMargin + layoutParams2.bottomMargin + Math.max(layoutParams3.topMargin + i13 + layoutParams3.bottomMargin, i13 + layoutParams4.topMargin + layoutParams4.bottomMargin)) + getVerticalMargin() + getVerticalPadding();
            calculateRect((iMax - getVerticalMargin()) - getVerticalPadding(), this.mAreaRect, layoutStateWrapper, layoutManagerHelper);
            view2 = view4;
            int decoratedMeasurementInOther = this.mAreaRect.left + mainOrientationHelper.getDecoratedMeasurementInOther(view2);
            view = view6;
            c = c2;
            layoutChildWithMargin(view2, this.mAreaRect.left, this.mAreaRect.top, decoratedMeasurementInOther, this.mAreaRect.bottom, layoutManagerHelper);
            int decoratedMeasurementInOther2 = decoratedMeasurementInOther + mainOrientationHelper.getDecoratedMeasurementInOther(view5);
            layoutChildWithMargin(view5, decoratedMeasurementInOther, this.mAreaRect.top, decoratedMeasurementInOther2, this.mAreaRect.top + mainOrientationHelper.getDecoratedMeasurement(view5), layoutManagerHelper);
            layoutChildWithMargin(view, decoratedMeasurementInOther2, this.mAreaRect.top, decoratedMeasurementInOther2 + mainOrientationHelper.getDecoratedMeasurementInOther(view), this.mAreaRect.top + mainOrientationHelper.getDecoratedMeasurement(view), layoutManagerHelper);
            int decoratedMeasurementInOther3 = decoratedMeasurementInOther + mainOrientationHelper.getDecoratedMeasurementInOther(view7);
            layoutChildWithMargin(view7, decoratedMeasurementInOther, this.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view7), decoratedMeasurementInOther3, this.mAreaRect.bottom, layoutManagerHelper);
            layoutChildWithMargin(view3, decoratedMeasurementInOther3, this.mAreaRect.bottom - mainOrientationHelper.getDecoratedMeasurement(view3), decoratedMeasurementInOther3 + mainOrientationHelper.getDecoratedMeasurementInOther(view3), this.mAreaRect.bottom, layoutManagerHelper);
        } else {
            view = view6;
            view2 = view4;
            c = 2;
            view3 = view8;
            iMax = 0;
        }
        View[] viewArr = new View[5];
        viewArr[0] = view2;
        viewArr[1] = view5;
        viewArr[c] = view;
        viewArr[3] = view7;
        viewArr[4] = view3;
        handleStateOnResult(layoutChunkResult, viewArr);
        return iMax;
    }

    @Override // com.alibaba.android.vlayout.layout.AbstractFullFillLayoutHelper, com.alibaba.android.vlayout.layout.MarginLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public int computeAlignOffset(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper) {
        if (getItemCount() == 3) {
            if (i == 1 && z) {
                Log.w(TAG, "Should not happen after adjust anchor");
                return 0;
            }
        } else if (getItemCount() == 4 && i == 1 && z) {
            return 0;
        }
        if (layoutManagerHelper.getOrientation() == 1) {
            return z ? this.mMarginBottom + this.mPaddingBottom : (-this.mMarginTop) - this.mPaddingTop;
        }
        return z ? this.mMarginRight + this.mPaddingRight : (-this.mMarginLeft) - this.mPaddingLeft;
    }

    @Override // com.alibaba.android.vlayout.layout.AbstractFullFillLayoutHelper, com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        if (isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
            return;
        }
        if (this.mChildrenViews == null || this.mChildrenViews.length != getItemCount()) {
            this.mChildrenViews = new View[getItemCount()];
        }
        int allChildren = getAllChildren(this.mChildrenViews, recycler, layoutStateWrapper, layoutChunkResult, layoutManagerHelper);
        if (allChildren != getItemCount()) {
            Log.w(TAG, "The real number of children is not match with range of LayoutHelper");
        }
        int iHandSeven = 0;
        boolean z = layoutManagerHelper.getOrientation() == 1;
        int contentWidth = layoutManagerHelper.getContentWidth();
        int contentHeight = layoutManagerHelper.getContentHeight();
        int paddingLeft = layoutManagerHelper.getPaddingLeft() + layoutManagerHelper.getPaddingRight() + getHorizontalMargin() + getHorizontalPadding();
        int paddingTop = layoutManagerHelper.getPaddingTop() + layoutManagerHelper.getPaddingBottom() + getVerticalMargin() + getVerticalPadding();
        if (allChildren == 5) {
            iHandSeven = handleFive(layoutStateWrapper, layoutChunkResult, layoutManagerHelper, z, contentWidth, contentHeight, paddingLeft, paddingTop);
        } else if (allChildren == 6) {
            iHandSeven = handSix(layoutStateWrapper, layoutChunkResult, layoutManagerHelper, z, contentWidth, contentHeight, paddingLeft, paddingTop);
        } else if (allChildren == 7) {
            iHandSeven = handSeven(layoutStateWrapper, layoutChunkResult, layoutManagerHelper, z, contentWidth, contentHeight, paddingLeft, paddingTop);
        }
        layoutChunkResult.mConsumed = iHandSeven;
        Arrays.fill(this.mChildrenViews, (Object) null);
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onRangeChange(int i, int i2) {
        int i3 = i2 - i;
        if (i3 < 4) {
            throw new IllegalArgumentException("pls use OnePlusNLayoutHelper instead of OnePlusNLayoutHelperEx which childcount <= 5");
        }
        if (i3 > 6) {
            throw new IllegalArgumentException("OnePlusNLayoutHelper only supports maximum 7 children now");
        }
    }

    public void setColWeights(float[] fArr) {
        if (fArr != null) {
            this.mColWeights = Arrays.copyOf(fArr, fArr.length);
        } else {
            this.mColWeights = new float[0];
        }
    }

    public void setRowWeight(float f) {
        this.mRowWeight = f;
    }
}
