package com.media.common.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class FlowLayout extends ViewGroup {
    private List<List<View>> mAllViews;
    private List<Integer> mLineHeight;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FlowLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mAllViews = new ArrayList();
        this.mLineHeight = new ArrayList();
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new ViewGroup.MarginLayoutParams(getContext(), attributeSet);
    }

    public int getAllViewSize() {
        int size = 0;
        for (int i = 0; i < this.mAllViews.size(); i++) {
            size += this.mAllViews.get(i).size();
        }
        return size;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.mAllViews.clear();
        this.mLineHeight.clear();
        int width = getWidth();
        ArrayList arrayList = new ArrayList();
        int childCount = getChildCount();
        ArrayList arrayList2 = arrayList;
        int i5 = 0;
        int iMax = 0;
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
            int measuredWidth = childAt.getMeasuredWidth();
            int measuredHeight = childAt.getMeasuredHeight();
            if (measuredWidth + i5 + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin > (width - getPaddingLeft()) - getPaddingRight()) {
                this.mLineHeight.add(Integer.valueOf(iMax));
                this.mAllViews.add(arrayList2);
                iMax = marginLayoutParams.bottomMargin + marginLayoutParams.topMargin + measuredHeight;
                arrayList2 = new ArrayList();
                i5 = 0;
            }
            i5 += measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            iMax = Math.max(iMax, measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin);
            arrayList2.add(childAt);
        }
        this.mLineHeight.add(Integer.valueOf(iMax));
        this.mAllViews.add(arrayList2);
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int size = this.mAllViews.size();
        int i7 = paddingTop;
        int paddingLeft2 = paddingLeft;
        for (int i8 = 0; i8 < size; i8++) {
            List<View> list = this.mAllViews.get(i8);
            int iIntValue = this.mLineHeight.get(i8).intValue();
            int measuredWidth2 = paddingLeft2;
            for (int i9 = 0; i9 < list.size(); i9++) {
                View view = list.get(i9);
                if (view.getVisibility() != 8) {
                    ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    int i10 = marginLayoutParams2.leftMargin + measuredWidth2;
                    int i11 = marginLayoutParams2.topMargin + i7;
                    view.layout(i10, i11, view.getMeasuredWidth() + i10, view.getMeasuredHeight() + i11);
                    measuredWidth2 += view.getMeasuredWidth() + marginLayoutParams2.leftMargin + marginLayoutParams2.rightMargin;
                }
            }
            paddingLeft2 = getPaddingLeft();
            i7 += iIntValue;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int mode2 = View.MeasureSpec.getMode(i2);
        int childCount = getChildCount();
        int i3 = 0;
        int i4 = 0;
        int iMax = 0;
        int i5 = 0;
        int iMax2 = 0;
        while (i3 < childCount) {
            View childAt = getChildAt(i3);
            measureChild(childAt, i, i2);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) childAt.getLayoutParams();
            int i6 = size2;
            int measuredWidth = childAt.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            int measuredHeight = childAt.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
            int i7 = i4 + measuredWidth;
            if (i7 > (size - getPaddingLeft()) - getPaddingRight()) {
                iMax = Math.max(iMax, i4);
                i5 += iMax2;
                iMax2 = measuredHeight;
                i4 = measuredWidth;
            } else {
                iMax2 = Math.max(iMax2, measuredHeight);
                i4 = i7;
            }
            if (i3 == childCount - 1) {
                i5 += iMax2;
                iMax = Math.max(i4, iMax);
            }
            i3++;
            size2 = i6;
        }
        int i8 = size2;
        if (mode != 1073741824) {
            size = getPaddingRight() + iMax + getPaddingLeft();
        }
        setMeasuredDimension(size, mode2 == 1073741824 ? i8 : i5 + getPaddingTop() + getPaddingBottom());
    }
}
