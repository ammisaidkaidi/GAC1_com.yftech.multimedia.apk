package com.yftech.car.widget.media.flexbox;

import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.view.MarginLayoutParamsCompat;
import androidx.core.widget.CompoundButtonCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
class FlexboxHelper {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final int INITIAL_CAPACITY = 10;
    private static final long MEASURE_SPEC_WIDTH_MASK = 4294967295L;
    private boolean[] mChildrenFrozen;
    private final FlexContainer mFlexContainer;

    @Nullable
    int[] mIndexToFlexLine;

    @Nullable
    long[] mMeasureSpecCache;

    @Nullable
    private long[] mMeasuredSizeCache;

    static class FlexLinesResult {
        int mChildState;
        List<FlexLine> mFlexLines;

        FlexLinesResult() {
        }

        void reset() {
            this.mFlexLines = null;
            this.mChildState = 0;
        }
    }

    private static class Order implements Comparable<Order> {
        int index;
        int order;

        private Order() {
        }

        @Override // java.lang.Comparable
        public int compareTo(@NonNull Order order) {
            return this.order != order.order ? this.order - order.order : this.index - order.index;
        }

        @NonNull
        public String toString() {
            return "Order{order=" + this.order + ", index=" + this.index + '}';
        }
    }

    FlexboxHelper(FlexContainer flexContainer) {
        this.mFlexContainer = flexContainer;
    }

    private void addFlexLine(List<FlexLine> list, FlexLine flexLine, int i, int i2) {
        flexLine.mSumCrossSizeBefore = i2;
        this.mFlexContainer.onNewFlexLineAdded(flexLine);
        flexLine.mLastIndex = i;
        list.add(flexLine);
    }

    /* JADX WARN: Code duplicated, block: B:12:0x002d  */
    /* JADX WARN: Code duplicated, block: B:13:0x0032  */
    /* JADX WARN: Code duplicated, block: B:15:0x0038  */
    /* JADX WARN: Code duplicated, block: B:16:0x003d  */
    /* JADX WARN: Code duplicated, block: B:18:0x0040  */
    /* JADX WARN: Code duplicated, block: B:20:? A[RETURN, SYNTHETIC] */
    private void checkSizeConstraints(View view, int i) {
        boolean z;
        FlexItem flexItem = (FlexItem) view.getLayoutParams();
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        boolean z2 = true;
        if (measuredWidth >= flexItem.getMinWidth()) {
            if (measuredWidth > flexItem.getMaxWidth()) {
                measuredWidth = flexItem.getMaxWidth();
            } else {
                z = false;
            }
            if (measuredHeight < flexItem.getMinHeight()) {
                measuredHeight = flexItem.getMinHeight();
            } else if (measuredHeight > flexItem.getMaxHeight()) {
                measuredHeight = flexItem.getMaxHeight();
            } else {
                z2 = z;
            }
            if (z2) {
                int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
                int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(measuredHeight, 1073741824);
                view.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
                updateMeasureCache(i, iMakeMeasureSpec, iMakeMeasureSpec2, view);
                this.mFlexContainer.updateViewCache(i, view);
            }
        }
        measuredWidth = flexItem.getMinWidth();
        z = true;
        if (measuredHeight < flexItem.getMinHeight()) {
            measuredHeight = flexItem.getMinHeight();
        } else if (measuredHeight > flexItem.getMaxHeight()) {
            measuredHeight = flexItem.getMaxHeight();
        } else {
            z2 = z;
        }
        if (z2) {
            int iMakeMeasureSpec3 = View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824);
            int iMakeMeasureSpec4 = View.MeasureSpec.makeMeasureSpec(measuredHeight, 1073741824);
            view.measure(iMakeMeasureSpec3, iMakeMeasureSpec4);
            updateMeasureCache(i, iMakeMeasureSpec3, iMakeMeasureSpec4, view);
            this.mFlexContainer.updateViewCache(i, view);
        }
    }

    private List<FlexLine> constructFlexLinesForAlignContentCenter(List<FlexLine> list, int i, int i2) {
        int i3 = (i - i2) / 2;
        ArrayList arrayList = new ArrayList();
        FlexLine flexLine = new FlexLine();
        flexLine.mCrossSize = i3;
        int size = list.size();
        for (int i4 = 0; i4 < size; i4++) {
            if (i4 == 0) {
                arrayList.add(flexLine);
            }
            arrayList.add(list.get(i4));
            if (i4 == list.size() - 1) {
                arrayList.add(flexLine);
            }
        }
        return arrayList;
    }

    @NonNull
    private List<Order> createOrders(int i) {
        ArrayList arrayList = new ArrayList(i);
        for (int i2 = 0; i2 < i; i2++) {
            FlexItem flexItem = (FlexItem) this.mFlexContainer.getFlexItemAt(i2).getLayoutParams();
            Order order = new Order();
            order.order = flexItem.getOrder();
            order.index = i2;
            arrayList.add(order);
        }
        return arrayList;
    }

    private void ensureChildrenFrozen(int i) {
        if (this.mChildrenFrozen == null) {
            this.mChildrenFrozen = new boolean[Math.max(i, 10)];
        } else if (this.mChildrenFrozen.length < i) {
            this.mChildrenFrozen = new boolean[Math.max(this.mChildrenFrozen.length * 2, i)];
        } else {
            Arrays.fill(this.mChildrenFrozen, false);
        }
    }

    private void evaluateMinimumSizeForCompoundButton(CompoundButton compoundButton) {
        FlexItem flexItem = (FlexItem) compoundButton.getLayoutParams();
        int minWidth = flexItem.getMinWidth();
        int minHeight = flexItem.getMinHeight();
        Drawable buttonDrawable = CompoundButtonCompat.getButtonDrawable(compoundButton);
        int minimumWidth = buttonDrawable == null ? 0 : buttonDrawable.getMinimumWidth();
        int minimumHeight = buttonDrawable == null ? 0 : buttonDrawable.getMinimumHeight();
        if (minWidth == -1) {
            minWidth = minimumWidth;
        }
        flexItem.setMinWidth(minWidth);
        if (minHeight != -1) {
            minimumHeight = minHeight;
        }
        flexItem.setMinHeight(minimumHeight);
    }

    private void expandFlexItems(int i, int i2, FlexLine flexLine, int i3, int i4, boolean z) {
        int measuredWidth;
        int measuredHeight;
        int iMax;
        float f;
        int measuredHeight2;
        int measuredWidth2;
        float f2;
        FlexLine flexLine2 = flexLine;
        if (flexLine2.mTotalFlexGrow <= 0.0f || i3 < flexLine2.mMainSize) {
            return;
        }
        int i5 = flexLine2.mMainSize;
        float f3 = (i3 - flexLine2.mMainSize) / flexLine2.mTotalFlexGrow;
        flexLine2.mMainSize = i4 + flexLine2.mDividerLengthInMainSize;
        if (!z) {
            flexLine2.mCrossSize = Integer.MIN_VALUE;
        }
        float f4 = 0.0f;
        boolean z2 = false;
        int i6 = 0;
        for (int i7 = 0; i7 < flexLine2.mItemCount; i7++) {
            int i8 = flexLine2.mFirstIndex + i7;
            View reorderedFlexItemAt = this.mFlexContainer.getReorderedFlexItemAt(i8);
            if (reorderedFlexItemAt != null && reorderedFlexItemAt.getVisibility() != 8) {
                FlexItem flexItem = (FlexItem) reorderedFlexItemAt.getLayoutParams();
                int flexDirection = this.mFlexContainer.getFlexDirection();
                if (flexDirection == 0 || flexDirection == 1) {
                    int measuredWidth3 = reorderedFlexItemAt.getMeasuredWidth();
                    if (this.mMeasuredSizeCache != null) {
                        measuredWidth3 = extractLowerInt(this.mMeasuredSizeCache[i8]);
                    }
                    int measuredHeight3 = reorderedFlexItemAt.getMeasuredHeight();
                    if (this.mMeasuredSizeCache != null) {
                        measuredHeight3 = extractHigherInt(this.mMeasuredSizeCache[i8]);
                    }
                    if (this.mChildrenFrozen[i8] || flexItem.getFlexGrow() <= 0.0f) {
                        measuredWidth = measuredWidth3;
                        measuredHeight = measuredHeight3;
                    } else {
                        float flexGrow = measuredWidth3 + (flexItem.getFlexGrow() * f3);
                        if (i7 == flexLine2.mItemCount - 1) {
                            flexGrow += f4;
                            f4 = 0.0f;
                        }
                        int iRound = Math.round(flexGrow);
                        if (iRound > flexItem.getMaxWidth()) {
                            iRound = flexItem.getMaxWidth();
                            this.mChildrenFrozen[i8] = true;
                            flexLine2.mTotalFlexGrow -= flexItem.getFlexGrow();
                            z2 = true;
                        } else {
                            f4 += flexGrow - iRound;
                            double d = f4;
                            if (d > 1.0d) {
                                iRound++;
                                f = (float) (d - 1.0d);
                            } else if (d < -1.0d) {
                                iRound--;
                                f = (float) (d + 1.0d);
                            }
                            f4 = f;
                        }
                        int childHeightMeasureSpecInternal = getChildHeightMeasureSpecInternal(i2, flexItem, flexLine2.mSumCrossSizeBefore);
                        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(iRound, 1073741824);
                        reorderedFlexItemAt.measure(iMakeMeasureSpec, childHeightMeasureSpecInternal);
                        measuredWidth = reorderedFlexItemAt.getMeasuredWidth();
                        measuredHeight = reorderedFlexItemAt.getMeasuredHeight();
                        updateMeasureCache(i8, iMakeMeasureSpec, childHeightMeasureSpecInternal, reorderedFlexItemAt);
                        this.mFlexContainer.updateViewCache(i8, reorderedFlexItemAt);
                    }
                    iMax = Math.max(i6, this.mFlexContainer.getDecorationLengthCrossAxis(reorderedFlexItemAt) + measuredHeight + flexItem.getMarginTop() + flexItem.getMarginBottom());
                    flexLine2.mMainSize += measuredWidth + flexItem.getMarginLeft() + flexItem.getMarginRight();
                } else {
                    int measuredHeight4 = reorderedFlexItemAt.getMeasuredHeight();
                    if (this.mMeasuredSizeCache != null) {
                        measuredHeight4 = extractHigherInt(this.mMeasuredSizeCache[i8]);
                    }
                    int measuredWidth4 = reorderedFlexItemAt.getMeasuredWidth();
                    if (this.mMeasuredSizeCache != null) {
                        measuredWidth4 = extractLowerInt(this.mMeasuredSizeCache[i8]);
                    }
                    if (this.mChildrenFrozen[i8] || flexItem.getFlexGrow() <= 0.0f) {
                        flexLine2 = flexLine;
                        measuredHeight2 = measuredHeight4;
                        measuredWidth2 = measuredWidth4;
                    } else {
                        float flexGrow2 = measuredHeight4 + (flexItem.getFlexGrow() * f3);
                        flexLine2 = flexLine;
                        if (i7 == flexLine2.mItemCount - 1) {
                            flexGrow2 += f4;
                            f4 = 0.0f;
                        }
                        int iRound2 = Math.round(flexGrow2);
                        if (iRound2 > flexItem.getMaxHeight()) {
                            iRound2 = flexItem.getMaxHeight();
                            this.mChildrenFrozen[i8] = true;
                            flexLine2.mTotalFlexGrow -= flexItem.getFlexGrow();
                            z2 = true;
                        } else {
                            f4 += flexGrow2 - iRound2;
                            double d2 = f4;
                            if (d2 > 1.0d) {
                                iRound2++;
                                f2 = (float) (d2 - 1.0d);
                            } else if (d2 < -1.0d) {
                                iRound2--;
                                f2 = (float) (d2 + 1.0d);
                            }
                            f4 = f2;
                        }
                        int childWidthMeasureSpecInternal = getChildWidthMeasureSpecInternal(i, flexItem, flexLine2.mSumCrossSizeBefore);
                        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(iRound2, 1073741824);
                        reorderedFlexItemAt.measure(childWidthMeasureSpecInternal, iMakeMeasureSpec2);
                        measuredWidth2 = reorderedFlexItemAt.getMeasuredWidth();
                        measuredHeight2 = reorderedFlexItemAt.getMeasuredHeight();
                        updateMeasureCache(i8, childWidthMeasureSpecInternal, iMakeMeasureSpec2, reorderedFlexItemAt);
                        this.mFlexContainer.updateViewCache(i8, reorderedFlexItemAt);
                    }
                    iMax = Math.max(i6, measuredWidth2 + flexItem.getMarginLeft() + flexItem.getMarginRight() + this.mFlexContainer.getDecorationLengthCrossAxis(reorderedFlexItemAt));
                    flexLine2.mMainSize += measuredHeight2 + flexItem.getMarginTop() + flexItem.getMarginBottom();
                }
                flexLine2.mCrossSize = Math.max(flexLine2.mCrossSize, iMax);
                i6 = iMax;
            }
        }
        if (!z2 || i5 == flexLine2.mMainSize) {
            return;
        }
        expandFlexItems(i, i2, flexLine, i3, i4, true);
    }

    private int getChildHeightMeasureSpecInternal(int i, FlexItem flexItem, int i2) {
        int childHeightMeasureSpec = this.mFlexContainer.getChildHeightMeasureSpec(i, this.mFlexContainer.getPaddingTop() + this.mFlexContainer.getPaddingBottom() + flexItem.getMarginTop() + flexItem.getMarginBottom() + i2, flexItem.getHeight());
        int size = View.MeasureSpec.getSize(childHeightMeasureSpec);
        if (size > flexItem.getMaxHeight()) {
            return View.MeasureSpec.makeMeasureSpec(flexItem.getMaxHeight(), View.MeasureSpec.getMode(childHeightMeasureSpec));
        }
        return size < flexItem.getMinHeight() ? View.MeasureSpec.makeMeasureSpec(flexItem.getMinHeight(), View.MeasureSpec.getMode(childHeightMeasureSpec)) : childHeightMeasureSpec;
    }

    private int getChildWidthMeasureSpecInternal(int i, FlexItem flexItem, int i2) {
        int childWidthMeasureSpec = this.mFlexContainer.getChildWidthMeasureSpec(i, this.mFlexContainer.getPaddingLeft() + this.mFlexContainer.getPaddingRight() + flexItem.getMarginLeft() + flexItem.getMarginRight() + i2, flexItem.getWidth());
        int size = View.MeasureSpec.getSize(childWidthMeasureSpec);
        if (size > flexItem.getMaxWidth()) {
            return View.MeasureSpec.makeMeasureSpec(flexItem.getMaxWidth(), View.MeasureSpec.getMode(childWidthMeasureSpec));
        }
        return size < flexItem.getMinWidth() ? View.MeasureSpec.makeMeasureSpec(flexItem.getMinWidth(), View.MeasureSpec.getMode(childWidthMeasureSpec)) : childWidthMeasureSpec;
    }

    private int getFlexItemMarginEndCross(FlexItem flexItem, boolean z) {
        return z ? flexItem.getMarginBottom() : flexItem.getMarginRight();
    }

    private int getFlexItemMarginEndMain(FlexItem flexItem, boolean z) {
        return z ? flexItem.getMarginRight() : flexItem.getMarginBottom();
    }

    private int getFlexItemMarginStartCross(FlexItem flexItem, boolean z) {
        return z ? flexItem.getMarginTop() : flexItem.getMarginLeft();
    }

    private int getFlexItemMarginStartMain(FlexItem flexItem, boolean z) {
        return z ? flexItem.getMarginLeft() : flexItem.getMarginTop();
    }

    private int getFlexItemSizeCross(FlexItem flexItem, boolean z) {
        return z ? flexItem.getHeight() : flexItem.getWidth();
    }

    private int getFlexItemSizeMain(FlexItem flexItem, boolean z) {
        return z ? flexItem.getWidth() : flexItem.getHeight();
    }

    private int getPaddingEndCross(boolean z) {
        return z ? this.mFlexContainer.getPaddingBottom() : this.mFlexContainer.getPaddingEnd();
    }

    private int getPaddingEndMain(boolean z) {
        return z ? this.mFlexContainer.getPaddingEnd() : this.mFlexContainer.getPaddingBottom();
    }

    private int getPaddingStartCross(boolean z) {
        return z ? this.mFlexContainer.getPaddingTop() : this.mFlexContainer.getPaddingStart();
    }

    private int getPaddingStartMain(boolean z) {
        return z ? this.mFlexContainer.getPaddingStart() : this.mFlexContainer.getPaddingTop();
    }

    private int getViewMeasuredSizeCross(View view, boolean z) {
        return z ? view.getMeasuredHeight() : view.getMeasuredWidth();
    }

    private int getViewMeasuredSizeMain(View view, boolean z) {
        return z ? view.getMeasuredWidth() : view.getMeasuredHeight();
    }

    private boolean isLastFlexItem(int i, int i2, FlexLine flexLine) {
        return i == i2 - 1 && flexLine.getItemCountNotGone() != 0;
    }

    private boolean isWrapRequired(View view, int i, int i2, int i3, int i4, FlexItem flexItem, int i5, int i6, int i7) {
        if (this.mFlexContainer.getFlexWrap() == 0) {
            return false;
        }
        if (flexItem.isWrapBefore()) {
            return true;
        }
        if (i == 0) {
            return false;
        }
        int maxLine = this.mFlexContainer.getMaxLine();
        if (maxLine != -1 && maxLine <= i7 + 1) {
            return false;
        }
        int decorationLengthMainAxis = this.mFlexContainer.getDecorationLengthMainAxis(view, i5, i6);
        if (decorationLengthMainAxis > 0) {
            i4 += decorationLengthMainAxis;
        }
        return i2 < i3 + i4;
    }

    private void shrinkFlexItems(int i, int i2, FlexLine flexLine, int i3, int i4, boolean z) {
        int i5;
        int measuredHeight;
        int iMax;
        int i6 = flexLine.mMainSize;
        if (flexLine.mTotalFlexShrink <= 0.0f || i3 > flexLine.mMainSize) {
            return;
        }
        float f = (flexLine.mMainSize - i3) / flexLine.mTotalFlexShrink;
        flexLine.mMainSize = i4 + flexLine.mDividerLengthInMainSize;
        if (!z) {
            flexLine.mCrossSize = Integer.MIN_VALUE;
        }
        int i7 = 0;
        float f2 = 0.0f;
        boolean z2 = false;
        int i8 = 0;
        while (i7 < flexLine.mItemCount) {
            int i9 = flexLine.mFirstIndex + i7;
            View reorderedFlexItemAt = this.mFlexContainer.getReorderedFlexItemAt(i9);
            if (reorderedFlexItemAt == null || reorderedFlexItemAt.getVisibility() == 8) {
                i5 = i7;
                z2 = z2;
            } else {
                FlexItem flexItem = (FlexItem) reorderedFlexItemAt.getLayoutParams();
                int flexDirection = this.mFlexContainer.getFlexDirection();
                if (flexDirection == 0 || flexDirection == 1) {
                    i5 = i7;
                    int measuredWidth = reorderedFlexItemAt.getMeasuredWidth();
                    if (this.mMeasuredSizeCache != null) {
                        measuredWidth = extractLowerInt(this.mMeasuredSizeCache[i9]);
                    }
                    int measuredHeight2 = reorderedFlexItemAt.getMeasuredHeight();
                    if (this.mMeasuredSizeCache != null) {
                        measuredHeight2 = extractHigherInt(this.mMeasuredSizeCache[i9]);
                    }
                    if (this.mChildrenFrozen[i9] || flexItem.getFlexShrink() <= 0.0f) {
                        measuredHeight = measuredHeight2;
                        z2 = z2;
                    } else {
                        float flexShrink = measuredWidth - (flexItem.getFlexShrink() * f);
                        if (i5 == flexLine.mItemCount - 1) {
                            flexShrink += f2;
                            f2 = 0.0f;
                        }
                        int iRound = Math.round(flexShrink);
                        if (iRound < flexItem.getMinWidth()) {
                            iRound = flexItem.getMinWidth();
                            this.mChildrenFrozen[i9] = true;
                            flexLine.mTotalFlexShrink -= flexItem.getFlexShrink();
                            z2 = true;
                        } else {
                            f2 += flexShrink - iRound;
                            boolean z3 = z2;
                            double d = f2;
                            if (d > 1.0d) {
                                iRound++;
                                f2 -= 1.0f;
                            } else if (d < -1.0d) {
                                iRound--;
                                f2 += 1.0f;
                            }
                            z2 = z3;
                        }
                        int childHeightMeasureSpecInternal = getChildHeightMeasureSpecInternal(i2, flexItem, flexLine.mSumCrossSizeBefore);
                        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(iRound, 1073741824);
                        reorderedFlexItemAt.measure(iMakeMeasureSpec, childHeightMeasureSpecInternal);
                        int measuredWidth2 = reorderedFlexItemAt.getMeasuredWidth();
                        measuredHeight = reorderedFlexItemAt.getMeasuredHeight();
                        updateMeasureCache(i9, iMakeMeasureSpec, childHeightMeasureSpecInternal, reorderedFlexItemAt);
                        this.mFlexContainer.updateViewCache(i9, reorderedFlexItemAt);
                        measuredWidth = measuredWidth2;
                    }
                    int iMax2 = Math.max(i8, measuredHeight + flexItem.getMarginTop() + flexItem.getMarginBottom() + this.mFlexContainer.getDecorationLengthCrossAxis(reorderedFlexItemAt));
                    flexLine.mMainSize += measuredWidth + flexItem.getMarginLeft() + flexItem.getMarginRight();
                    iMax = iMax2;
                } else {
                    int measuredHeight3 = reorderedFlexItemAt.getMeasuredHeight();
                    if (this.mMeasuredSizeCache != null) {
                        measuredHeight3 = extractHigherInt(this.mMeasuredSizeCache[i9]);
                    }
                    int measuredWidth3 = reorderedFlexItemAt.getMeasuredWidth();
                    if (this.mMeasuredSizeCache != null) {
                        measuredWidth3 = extractLowerInt(this.mMeasuredSizeCache[i9]);
                    }
                    if (this.mChildrenFrozen[i9] || flexItem.getFlexShrink() <= 0.0f) {
                        i5 = i7;
                    } else {
                        float flexShrink2 = measuredHeight3 - (flexItem.getFlexShrink() * f);
                        i5 = i7;
                        if (i5 == flexLine.mItemCount - 1) {
                            flexShrink2 += f2;
                            f2 = 0.0f;
                        }
                        int iRound2 = Math.round(flexShrink2);
                        if (iRound2 < flexItem.getMinHeight()) {
                            iRound2 = flexItem.getMinHeight();
                            this.mChildrenFrozen[i9] = true;
                            flexLine.mTotalFlexShrink -= flexItem.getFlexShrink();
                            z2 = true;
                        } else {
                            f2 += flexShrink2 - iRound2;
                            double d2 = f2;
                            if (d2 > 1.0d) {
                                iRound2++;
                                f2 -= 1.0f;
                            } else if (d2 < -1.0d) {
                                iRound2--;
                                f2 += 1.0f;
                            }
                        }
                        int childWidthMeasureSpecInternal = getChildWidthMeasureSpecInternal(i, flexItem, flexLine.mSumCrossSizeBefore);
                        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(iRound2, 1073741824);
                        reorderedFlexItemAt.measure(childWidthMeasureSpecInternal, iMakeMeasureSpec2);
                        int measuredWidth4 = reorderedFlexItemAt.getMeasuredWidth();
                        measuredHeight3 = reorderedFlexItemAt.getMeasuredHeight();
                        updateMeasureCache(i9, childWidthMeasureSpecInternal, iMakeMeasureSpec2, reorderedFlexItemAt);
                        this.mFlexContainer.updateViewCache(i9, reorderedFlexItemAt);
                        measuredWidth3 = measuredWidth4;
                    }
                    iMax = Math.max(i8, measuredWidth3 + flexItem.getMarginLeft() + flexItem.getMarginRight() + this.mFlexContainer.getDecorationLengthCrossAxis(reorderedFlexItemAt));
                    flexLine.mMainSize += measuredHeight3 + flexItem.getMarginTop() + flexItem.getMarginBottom();
                }
                flexLine.mCrossSize = Math.max(flexLine.mCrossSize, iMax);
                i8 = iMax;
            }
            i7 = i5 + 1;
        }
        if (!z2 || i6 == flexLine.mMainSize) {
            return;
        }
        shrinkFlexItems(i, i2, flexLine, i3, i4, true);
    }

    private int[] sortOrdersIntoReorderedIndices(int i, List<Order> list, SparseIntArray sparseIntArray) {
        Collections.sort(list);
        sparseIntArray.clear();
        int[] iArr = new int[i];
        int i2 = 0;
        for (Order order : list) {
            iArr[i2] = order.index;
            sparseIntArray.append(order.index, order.order);
            i2++;
        }
        return iArr;
    }

    private void stretchViewHorizontally(View view, int i, int i2) {
        FlexItem flexItem = (FlexItem) view.getLayoutParams();
        int iMin = Math.min(Math.max(((i - flexItem.getMarginLeft()) - flexItem.getMarginRight()) - this.mFlexContainer.getDecorationLengthCrossAxis(view), flexItem.getMinWidth()), flexItem.getMaxWidth());
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mMeasuredSizeCache != null ? extractHigherInt(this.mMeasuredSizeCache[i2]) : view.getMeasuredHeight(), 1073741824);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(iMin, 1073741824);
        view.measure(iMakeMeasureSpec2, iMakeMeasureSpec);
        updateMeasureCache(i2, iMakeMeasureSpec2, iMakeMeasureSpec, view);
        this.mFlexContainer.updateViewCache(i2, view);
    }

    private void stretchViewVertically(View view, int i, int i2) {
        FlexItem flexItem = (FlexItem) view.getLayoutParams();
        int iMin = Math.min(Math.max(((i - flexItem.getMarginTop()) - flexItem.getMarginBottom()) - this.mFlexContainer.getDecorationLengthCrossAxis(view), flexItem.getMinHeight()), flexItem.getMaxHeight());
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.mMeasuredSizeCache != null ? extractLowerInt(this.mMeasuredSizeCache[i2]) : view.getMeasuredWidth(), 1073741824);
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(iMin, 1073741824);
        view.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
        updateMeasureCache(i2, iMakeMeasureSpec, iMakeMeasureSpec2, view);
        this.mFlexContainer.updateViewCache(i2, view);
    }

    private void updateMeasureCache(int i, int i2, int i3, View view) {
        if (this.mMeasureSpecCache != null) {
            this.mMeasureSpecCache[i] = makeCombinedLong(i2, i3);
        }
        if (this.mMeasuredSizeCache != null) {
            this.mMeasuredSizeCache[i] = makeCombinedLong(view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }

    void calculateFlexLines(FlexLinesResult flexLinesResult, int i, int i2, int i3, int i4, int i5, @Nullable List<FlexLine> list) {
        int i6;
        List<FlexLine> list2;
        int i7;
        View view;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        int i13;
        int i14 = i2;
        int i15 = i5;
        boolean zIsMainAxisDirectionHorizontal = this.mFlexContainer.isMainAxisDirectionHorizontal();
        int mode = View.MeasureSpec.getMode(i);
        int size = View.MeasureSpec.getSize(i);
        List<FlexLine> arrayList = list == null ? new ArrayList() : list;
        flexLinesResult.mFlexLines = arrayList;
        boolean z = i15 == -1;
        int paddingStartMain = getPaddingStartMain(zIsMainAxisDirectionHorizontal);
        int paddingEndMain = getPaddingEndMain(zIsMainAxisDirectionHorizontal);
        int paddingStartCross = getPaddingStartCross(zIsMainAxisDirectionHorizontal);
        int paddingEndCross = getPaddingEndCross(zIsMainAxisDirectionHorizontal);
        FlexLine flexLine = new FlexLine();
        int i16 = i4;
        flexLine.mFirstIndex = i16;
        int i17 = paddingEndMain + paddingStartMain;
        flexLine.mMainSize = i17;
        int flexItemCount = this.mFlexContainer.getFlexItemCount();
        boolean z2 = z;
        int i18 = Integer.MIN_VALUE;
        int i19 = 0;
        int iCombineMeasuredStates = 0;
        int i20 = 0;
        while (i16 < flexItemCount) {
            View reorderedFlexItemAt = this.mFlexContainer.getReorderedFlexItemAt(i16);
            if (reorderedFlexItemAt != null) {
                if (reorderedFlexItemAt.getVisibility() != 8) {
                    if (reorderedFlexItemAt instanceof CompoundButton) {
                        evaluateMinimumSizeForCompoundButton((CompoundButton) reorderedFlexItemAt);
                    }
                    FlexItem flexItem = (FlexItem) reorderedFlexItemAt.getLayoutParams();
                    int i21 = flexItemCount;
                    if (flexItem.getAlignSelf() == 4) {
                        flexLine.mIndicesAlignSelfStretch.add(Integer.valueOf(i16));
                    }
                    int flexItemSizeMain = getFlexItemSizeMain(flexItem, zIsMainAxisDirectionHorizontal);
                    if (flexItem.getFlexBasisPercent() != -1.0f && mode == 1073741824) {
                        flexItemSizeMain = Math.round(size * flexItem.getFlexBasisPercent());
                    }
                    if (zIsMainAxisDirectionHorizontal) {
                        int childWidthMeasureSpec = this.mFlexContainer.getChildWidthMeasureSpec(i, i17 + getFlexItemMarginStartMain(flexItem, true) + getFlexItemMarginEndMain(flexItem, true), flexItemSizeMain);
                        int childHeightMeasureSpec = this.mFlexContainer.getChildHeightMeasureSpec(i14, paddingStartCross + paddingEndCross + getFlexItemMarginStartCross(flexItem, true) + getFlexItemMarginEndCross(flexItem, true) + i19, getFlexItemSizeCross(flexItem, true));
                        reorderedFlexItemAt.measure(childWidthMeasureSpec, childHeightMeasureSpec);
                        updateMeasureCache(i16, childWidthMeasureSpec, childHeightMeasureSpec, reorderedFlexItemAt);
                        i6 = childWidthMeasureSpec;
                    } else {
                        int childWidthMeasureSpec2 = this.mFlexContainer.getChildWidthMeasureSpec(i14, paddingStartCross + paddingEndCross + getFlexItemMarginStartCross(flexItem, false) + getFlexItemMarginEndCross(flexItem, false) + i19, getFlexItemSizeCross(flexItem, false));
                        int childHeightMeasureSpec2 = this.mFlexContainer.getChildHeightMeasureSpec(i, getFlexItemMarginStartMain(flexItem, false) + i17 + getFlexItemMarginEndMain(flexItem, false), flexItemSizeMain);
                        reorderedFlexItemAt.measure(childWidthMeasureSpec2, childHeightMeasureSpec2);
                        updateMeasureCache(i16, childWidthMeasureSpec2, childHeightMeasureSpec2, reorderedFlexItemAt);
                        i6 = childHeightMeasureSpec2;
                    }
                    this.mFlexContainer.updateViewCache(i16, reorderedFlexItemAt);
                    checkSizeConstraints(reorderedFlexItemAt, i16);
                    iCombineMeasuredStates = View.combineMeasuredStates(iCombineMeasuredStates, reorderedFlexItemAt.getMeasuredState());
                    int i22 = flexLine.mMainSize;
                    int i23 = i19;
                    int i24 = i17;
                    FlexLine flexLine2 = flexLine;
                    int i25 = size;
                    int i26 = i16;
                    list2 = arrayList;
                    int i27 = i6;
                    if (isWrapRequired(reorderedFlexItemAt, mode, i25, i22, getFlexItemMarginEndMain(flexItem, zIsMainAxisDirectionHorizontal) + getViewMeasuredSizeMain(reorderedFlexItemAt, zIsMainAxisDirectionHorizontal) + getFlexItemMarginStartMain(flexItem, zIsMainAxisDirectionHorizontal), flexItem, i26, i20, arrayList.size())) {
                        if (flexLine2.getItemCountNotGone() > 0) {
                            addFlexLine(list2, flexLine2, i26 > 0 ? i26 - 1 : 0, i23);
                            i13 = flexLine2.mCrossSize + i23;
                        } else {
                            i13 = i23;
                        }
                        if (!zIsMainAxisDirectionHorizontal) {
                            i16 = i26;
                            view = reorderedFlexItemAt;
                            i8 = i2;
                            if (flexItem.getWidth() == -1) {
                                view.measure(this.mFlexContainer.getChildWidthMeasureSpec(i8, this.mFlexContainer.getPaddingLeft() + this.mFlexContainer.getPaddingRight() + flexItem.getMarginLeft() + flexItem.getMarginRight() + i13, flexItem.getWidth()), i27);
                                checkSizeConstraints(view, i16);
                            }
                        } else if (flexItem.getHeight() == -1) {
                            i16 = i26;
                            i8 = i2;
                            view = reorderedFlexItemAt;
                            view.measure(i27, this.mFlexContainer.getChildHeightMeasureSpec(i8, this.mFlexContainer.getPaddingTop() + this.mFlexContainer.getPaddingBottom() + flexItem.getMarginTop() + flexItem.getMarginBottom() + i13, flexItem.getHeight()));
                            checkSizeConstraints(view, i16);
                        } else {
                            i16 = i26;
                            view = reorderedFlexItemAt;
                            i8 = i2;
                        }
                        flexLine = new FlexLine();
                        flexLine.mItemCount = 1;
                        i7 = i24;
                        flexLine.mMainSize = i7;
                        flexLine.mFirstIndex = i16;
                        i23 = i13;
                        i10 = Integer.MIN_VALUE;
                        i9 = 0;
                    } else {
                        i16 = i26;
                        flexLine = flexLine2;
                        i7 = i24;
                        view = reorderedFlexItemAt;
                        i8 = i2;
                        flexLine.mItemCount++;
                        i9 = i20 + 1;
                        i10 = i18;
                    }
                    flexLine.mAnyItemsHaveFlexGrow |= flexItem.getFlexGrow() != 0.0f;
                    flexLine.mAnyItemsHaveFlexShrink |= flexItem.getFlexShrink() != 0.0f;
                    if (this.mIndexToFlexLine != null) {
                        this.mIndexToFlexLine[i16] = list2.size();
                    }
                    flexLine.mMainSize += getViewMeasuredSizeMain(view, zIsMainAxisDirectionHorizontal) + getFlexItemMarginStartMain(flexItem, zIsMainAxisDirectionHorizontal) + getFlexItemMarginEndMain(flexItem, zIsMainAxisDirectionHorizontal);
                    flexLine.mTotalFlexGrow += flexItem.getFlexGrow();
                    flexLine.mTotalFlexShrink += flexItem.getFlexShrink();
                    this.mFlexContainer.onNewFlexItemAdded(view, i16, i9, flexLine);
                    int iMax = Math.max(i10, getViewMeasuredSizeCross(view, zIsMainAxisDirectionHorizontal) + getFlexItemMarginStartCross(flexItem, zIsMainAxisDirectionHorizontal) + getFlexItemMarginEndCross(flexItem, zIsMainAxisDirectionHorizontal) + this.mFlexContainer.getDecorationLengthCrossAxis(view));
                    flexLine.mCrossSize = Math.max(flexLine.mCrossSize, iMax);
                    if (zIsMainAxisDirectionHorizontal) {
                        if (this.mFlexContainer.getFlexWrap() != 2) {
                            flexLine.mMaxBaseline = Math.max(flexLine.mMaxBaseline, view.getBaseline() + flexItem.getMarginTop());
                        } else {
                            flexLine.mMaxBaseline = Math.max(flexLine.mMaxBaseline, (view.getMeasuredHeight() - view.getBaseline()) + flexItem.getMarginBottom());
                        }
                    }
                    i11 = i21;
                    if (isLastFlexItem(i16, i11, flexLine)) {
                        addFlexLine(list2, flexLine, i16, i23);
                        i23 += flexLine.mCrossSize;
                    }
                    i12 = i5;
                    if (i12 != -1 && list2.size() > 0) {
                        if (list2.get(list2.size() - 1).mLastIndex >= i12 && i16 >= i12 && !z2) {
                            i23 = -flexLine.getCrossSize();
                            z2 = true;
                        }
                    }
                    int i28 = i23;
                    if (i28 > i3 && z2) {
                        break;
                    }
                    i20 = i9;
                    i18 = iMax;
                    i19 = i28;
                } else {
                    flexLine.mGoneItemCount++;
                    flexLine.mItemCount++;
                    if (isLastFlexItem(i16, flexItemCount, flexLine)) {
                        addFlexLine(arrayList, flexLine, i16, i19);
                    }
                }
                i16++;
                flexItemCount = i11;
                i14 = i8;
                i17 = i7;
                arrayList = list2;
                mode = mode;
                i15 = i12;
                size = size;
            } else if (isLastFlexItem(i16, flexItemCount, flexLine)) {
                addFlexLine(arrayList, flexLine, i16, i19);
            }
            i8 = i14;
            i12 = i15;
            list2 = arrayList;
            i7 = i17;
            i11 = flexItemCount;
            i16++;
            flexItemCount = i11;
            i14 = i8;
            i17 = i7;
            arrayList = list2;
            mode = mode;
            i15 = i12;
            size = size;
        }
        flexLinesResult.mChildState = iCombineMeasuredStates;
    }

    void calculateHorizontalFlexLines(FlexLinesResult flexLinesResult, int i, int i2) {
        calculateFlexLines(flexLinesResult, i, i2, Integer.MAX_VALUE, 0, -1, null);
    }

    void calculateHorizontalFlexLines(FlexLinesResult flexLinesResult, int i, int i2, int i3, int i4, @Nullable List<FlexLine> list) {
        calculateFlexLines(flexLinesResult, i, i2, i3, i4, -1, list);
    }

    void calculateHorizontalFlexLinesToIndex(FlexLinesResult flexLinesResult, int i, int i2, int i3, int i4, List<FlexLine> list) {
        calculateFlexLines(flexLinesResult, i, i2, i3, 0, i4, list);
    }

    void calculateVerticalFlexLines(FlexLinesResult flexLinesResult, int i, int i2) {
        calculateFlexLines(flexLinesResult, i2, i, Integer.MAX_VALUE, 0, -1, null);
    }

    void calculateVerticalFlexLines(FlexLinesResult flexLinesResult, int i, int i2, int i3, int i4, @Nullable List<FlexLine> list) {
        calculateFlexLines(flexLinesResult, i2, i, i3, i4, -1, list);
    }

    void calculateVerticalFlexLinesToIndex(FlexLinesResult flexLinesResult, int i, int i2, int i3, int i4, List<FlexLine> list) {
        calculateFlexLines(flexLinesResult, i2, i, i3, 0, i4, list);
    }

    void clearFlexLines(List<FlexLine> list, int i) {
        int i2 = this.mIndexToFlexLine[i];
        if (i2 == -1) {
            i2 = 0;
        }
        if (list.size() > i2) {
            list.subList(i2, list.size()).clear();
        }
        int length = this.mIndexToFlexLine.length - 1;
        if (i > length) {
            Arrays.fill(this.mIndexToFlexLine, -1);
        } else {
            Arrays.fill(this.mIndexToFlexLine, i, length, -1);
        }
        int length2 = this.mMeasureSpecCache.length - 1;
        if (i > length2) {
            Arrays.fill(this.mMeasureSpecCache, 0L);
        } else {
            Arrays.fill(this.mMeasureSpecCache, i, length2, 0L);
        }
    }

    int[] createReorderedIndices(SparseIntArray sparseIntArray) {
        int flexItemCount = this.mFlexContainer.getFlexItemCount();
        return sortOrdersIntoReorderedIndices(flexItemCount, createOrders(flexItemCount), sparseIntArray);
    }

    /* JADX WARN: Multi-variable type inference failed */
    int[] createReorderedIndices(View view, int i, ViewGroup.LayoutParams layoutParams, SparseIntArray sparseIntArray) {
        int flexItemCount = this.mFlexContainer.getFlexItemCount();
        List<Order> listCreateOrders = createOrders(flexItemCount);
        Order order = new Order();
        if (view == null || !(layoutParams instanceof FlexItem)) {
            order.order = 1;
        } else {
            order.order = ((FlexItem) layoutParams).getOrder();
        }
        if (i == -1 || i == flexItemCount || i >= this.mFlexContainer.getFlexItemCount()) {
            order.index = flexItemCount;
        } else {
            order.index = i;
            while (i < flexItemCount) {
                listCreateOrders.get(i).index++;
                i++;
            }
        }
        listCreateOrders.add(order);
        return sortOrdersIntoReorderedIndices(flexItemCount + 1, listCreateOrders, sparseIntArray);
    }

    void determineCrossSize(int i, int i2, int i3) {
        int mode;
        int size;
        int flexDirection = this.mFlexContainer.getFlexDirection();
        switch (flexDirection) {
            case 0:
            case 1:
                mode = View.MeasureSpec.getMode(i2);
                size = View.MeasureSpec.getSize(i2);
                break;
            case 2:
            case 3:
                int mode2 = View.MeasureSpec.getMode(i);
                size = View.MeasureSpec.getSize(i);
                mode = mode2;
                break;
            default:
                throw new IllegalArgumentException("Invalid flex direction: " + flexDirection);
        }
        List<FlexLine> flexLinesInternal = this.mFlexContainer.getFlexLinesInternal();
        if (mode == 1073741824) {
            int sumOfCrossSize = this.mFlexContainer.getSumOfCrossSize() + i3;
            int i4 = 0;
            if (flexLinesInternal.size() == 1) {
                flexLinesInternal.get(0).mCrossSize = size - i3;
                return;
            }
            if (flexLinesInternal.size() >= 2) {
                switch (this.mFlexContainer.getAlignContent()) {
                    case 1:
                        FlexLine flexLine = new FlexLine();
                        flexLine.mCrossSize = size - sumOfCrossSize;
                        flexLinesInternal.add(0, flexLine);
                        return;
                    case 2:
                        this.mFlexContainer.setFlexLines(constructFlexLinesForAlignContentCenter(flexLinesInternal, size, sumOfCrossSize));
                        return;
                    case 3:
                        if (sumOfCrossSize >= size) {
                            return;
                        }
                        float size2 = (size - sumOfCrossSize) / (flexLinesInternal.size() - 1);
                        ArrayList arrayList = new ArrayList();
                        int size3 = flexLinesInternal.size();
                        float f = 0.0f;
                        while (i4 < size3) {
                            arrayList.add(flexLinesInternal.get(i4));
                            if (i4 != flexLinesInternal.size() - 1) {
                                FlexLine flexLine2 = new FlexLine();
                                if (i4 == flexLinesInternal.size() - 2) {
                                    flexLine2.mCrossSize = Math.round(f + size2);
                                    f = 0.0f;
                                } else {
                                    flexLine2.mCrossSize = Math.round(size2);
                                }
                                f += size2 - flexLine2.mCrossSize;
                                if (f > 1.0f) {
                                    flexLine2.mCrossSize++;
                                    f -= 1.0f;
                                } else if (f < -1.0f) {
                                    flexLine2.mCrossSize--;
                                    f += 1.0f;
                                }
                                arrayList.add(flexLine2);
                            }
                            i4++;
                        }
                        this.mFlexContainer.setFlexLines(arrayList);
                        return;
                    case 4:
                        if (sumOfCrossSize >= size) {
                            this.mFlexContainer.setFlexLines(constructFlexLinesForAlignContentCenter(flexLinesInternal, size, sumOfCrossSize));
                            return;
                        }
                        int size4 = (size - sumOfCrossSize) / (flexLinesInternal.size() * 2);
                        ArrayList arrayList2 = new ArrayList();
                        FlexLine flexLine3 = new FlexLine();
                        flexLine3.mCrossSize = size4;
                        for (FlexLine flexLine4 : flexLinesInternal) {
                            arrayList2.add(flexLine3);
                            arrayList2.add(flexLine4);
                            arrayList2.add(flexLine3);
                        }
                        this.mFlexContainer.setFlexLines(arrayList2);
                        return;
                    case 5:
                        if (sumOfCrossSize >= size) {
                            return;
                        }
                        float size5 = (size - sumOfCrossSize) / flexLinesInternal.size();
                        int size6 = flexLinesInternal.size();
                        float f2 = 0.0f;
                        while (i4 < size6) {
                            FlexLine flexLine5 = flexLinesInternal.get(i4);
                            float f3 = flexLine5.mCrossSize + size5;
                            if (i4 == flexLinesInternal.size() - 1) {
                                f3 += f2;
                                f2 = 0.0f;
                            }
                            int iRound = Math.round(f3);
                            f2 += f3 - iRound;
                            if (f2 > 1.0f) {
                                iRound++;
                                f2 -= 1.0f;
                            } else if (f2 < -1.0f) {
                                iRound--;
                                f2 += 1.0f;
                            }
                            flexLine5.mCrossSize = iRound;
                            i4++;
                        }
                        return;
                    default:
                        return;
                }
            }
        }
    }

    void determineMainSize(int i, int i2) {
        determineMainSize(i, i2, 0);
    }

    void determineMainSize(int i, int i2, int i3) {
        int size;
        int paddingLeft;
        ensureChildrenFrozen(this.mFlexContainer.getFlexItemCount());
        if (i3 >= this.mFlexContainer.getFlexItemCount()) {
            return;
        }
        int flexDirection = this.mFlexContainer.getFlexDirection();
        switch (this.mFlexContainer.getFlexDirection()) {
            case 0:
            case 1:
                int mode = View.MeasureSpec.getMode(i);
                size = View.MeasureSpec.getSize(i);
                int largestMainSize = this.mFlexContainer.getLargestMainSize();
                if (mode != 1073741824) {
                    size = Math.min(largestMainSize, size);
                }
                paddingLeft = this.mFlexContainer.getPaddingLeft() + this.mFlexContainer.getPaddingRight();
                break;
            case 2:
            case 3:
                int mode2 = View.MeasureSpec.getMode(i2);
                size = View.MeasureSpec.getSize(i2);
                if (mode2 != 1073741824) {
                    size = this.mFlexContainer.getLargestMainSize();
                }
                paddingLeft = this.mFlexContainer.getPaddingTop() + this.mFlexContainer.getPaddingBottom();
                break;
            default:
                throw new IllegalArgumentException("Invalid flex direction: " + flexDirection);
        }
        int i4 = this.mIndexToFlexLine != null ? this.mIndexToFlexLine[i3] : 0;
        List<FlexLine> flexLinesInternal = this.mFlexContainer.getFlexLinesInternal();
        int size2 = flexLinesInternal.size();
        for (int i5 = i4; i5 < size2; i5++) {
            FlexLine flexLine = flexLinesInternal.get(i5);
            if (flexLine.mMainSize < size && flexLine.mAnyItemsHaveFlexGrow) {
                expandFlexItems(i, i2, flexLine, size, paddingLeft, false);
            } else if (flexLine.mMainSize > size && flexLine.mAnyItemsHaveFlexShrink) {
                shrinkFlexItems(i, i2, flexLine, size, paddingLeft, false);
            }
        }
    }

    void ensureIndexToFlexLine(int i) {
        if (this.mIndexToFlexLine == null) {
            this.mIndexToFlexLine = new int[Math.max(i, 10)];
        } else if (this.mIndexToFlexLine.length < i) {
            this.mIndexToFlexLine = Arrays.copyOf(this.mIndexToFlexLine, Math.max(this.mIndexToFlexLine.length * 2, i));
        }
    }

    void ensureMeasureSpecCache(int i) {
        if (this.mMeasureSpecCache == null) {
            this.mMeasureSpecCache = new long[Math.max(i, 10)];
        } else if (this.mMeasureSpecCache.length < i) {
            this.mMeasureSpecCache = Arrays.copyOf(this.mMeasureSpecCache, Math.max(this.mMeasureSpecCache.length * 2, i));
        }
    }

    void ensureMeasuredSizeCache(int i) {
        if (this.mMeasuredSizeCache == null) {
            this.mMeasuredSizeCache = new long[Math.max(i, 10)];
        } else if (this.mMeasuredSizeCache.length < i) {
            this.mMeasuredSizeCache = Arrays.copyOf(this.mMeasuredSizeCache, Math.max(this.mMeasuredSizeCache.length * 2, i));
        }
    }

    int extractHigherInt(long j) {
        return (int) (j >> 32);
    }

    int extractLowerInt(long j) {
        return (int) j;
    }

    boolean isOrderChangedFromLastMeasurement(SparseIntArray sparseIntArray) {
        int flexItemCount = this.mFlexContainer.getFlexItemCount();
        if (sparseIntArray.size() != flexItemCount) {
            return true;
        }
        for (int i = 0; i < flexItemCount; i++) {
            View flexItemAt = this.mFlexContainer.getFlexItemAt(i);
            if (flexItemAt != null && ((FlexItem) flexItemAt.getLayoutParams()).getOrder() != sparseIntArray.get(i)) {
                return true;
            }
        }
        return false;
    }

    void layoutSingleChildHorizontal(View view, FlexLine flexLine, int i, int i2, int i3, int i4) {
        FlexItem flexItem = (FlexItem) view.getLayoutParams();
        int alignItems = this.mFlexContainer.getAlignItems();
        if (flexItem.getAlignSelf() != -1) {
            alignItems = flexItem.getAlignSelf();
        }
        int i5 = flexLine.mCrossSize;
        switch (alignItems) {
            case 0:
            case 4:
                if (this.mFlexContainer.getFlexWrap() == 2) {
                    view.layout(i, i2 - flexItem.getMarginBottom(), i3, i4 - flexItem.getMarginBottom());
                } else {
                    view.layout(i, i2 + flexItem.getMarginTop(), i3, i4 + flexItem.getMarginTop());
                }
                break;
            case 1:
                if (this.mFlexContainer.getFlexWrap() == 2) {
                    view.layout(i, (i2 - i5) + view.getMeasuredHeight() + flexItem.getMarginTop(), i3, (i4 - i5) + view.getMeasuredHeight() + flexItem.getMarginTop());
                } else {
                    int i6 = i2 + i5;
                    view.layout(i, (i6 - view.getMeasuredHeight()) - flexItem.getMarginBottom(), i3, i6 - flexItem.getMarginBottom());
                }
                break;
            case 2:
                int measuredHeight = (((i5 - view.getMeasuredHeight()) + flexItem.getMarginTop()) - flexItem.getMarginBottom()) / 2;
                if (this.mFlexContainer.getFlexWrap() == 2) {
                    int i7 = i2 - measuredHeight;
                    view.layout(i, i7, i3, view.getMeasuredHeight() + i7);
                } else {
                    int i8 = i2 + measuredHeight;
                    view.layout(i, i8, i3, view.getMeasuredHeight() + i8);
                }
                break;
            case 3:
                if (this.mFlexContainer.getFlexWrap() == 2) {
                    int iMax = Math.max((flexLine.mMaxBaseline - view.getMeasuredHeight()) + view.getBaseline(), flexItem.getMarginBottom());
                    view.layout(i, i2 - iMax, i3, i4 - iMax);
                } else {
                    int iMax2 = Math.max(flexLine.mMaxBaseline - view.getBaseline(), flexItem.getMarginTop());
                    view.layout(i, i2 + iMax2, i3, i4 + iMax2);
                }
                break;
        }
    }

    void layoutSingleChildVertical(View view, FlexLine flexLine, boolean z, int i, int i2, int i3, int i4) {
        FlexItem flexItem = (FlexItem) view.getLayoutParams();
        int alignItems = this.mFlexContainer.getAlignItems();
        if (flexItem.getAlignSelf() != -1) {
            alignItems = flexItem.getAlignSelf();
        }
        int i5 = flexLine.mCrossSize;
        switch (alignItems) {
            case 0:
            case 3:
            case 4:
                if (!z) {
                    view.layout(i + flexItem.getMarginLeft(), i2, i3 + flexItem.getMarginLeft(), i4);
                } else {
                    view.layout(i - flexItem.getMarginRight(), i2, i3 - flexItem.getMarginRight(), i4);
                }
                break;
            case 1:
                if (!z) {
                    view.layout(((i + i5) - view.getMeasuredWidth()) - flexItem.getMarginRight(), i2, ((i3 + i5) - view.getMeasuredWidth()) - flexItem.getMarginRight(), i4);
                } else {
                    view.layout((i - i5) + view.getMeasuredWidth() + flexItem.getMarginLeft(), i2, (i3 - i5) + view.getMeasuredWidth() + flexItem.getMarginLeft(), i4);
                }
                break;
            case 2:
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                int measuredWidth = (((i5 - view.getMeasuredWidth()) + MarginLayoutParamsCompat.getMarginStart(marginLayoutParams)) - MarginLayoutParamsCompat.getMarginEnd(marginLayoutParams)) / 2;
                if (!z) {
                    view.layout(i + measuredWidth, i2, i3 + measuredWidth, i4);
                } else {
                    view.layout(i - measuredWidth, i2, i3 - measuredWidth, i4);
                }
                break;
        }
    }

    @VisibleForTesting
    long makeCombinedLong(int i, int i2) {
        return (((long) i) & 4294967295L) | (((long) i2) << 32);
    }

    void stretchViews() {
        stretchViews(0);
    }

    void stretchViews(int i) {
        View reorderedFlexItemAt;
        if (i >= this.mFlexContainer.getFlexItemCount()) {
            return;
        }
        int flexDirection = this.mFlexContainer.getFlexDirection();
        if (this.mFlexContainer.getAlignItems() != 4) {
            for (FlexLine flexLine : this.mFlexContainer.getFlexLinesInternal()) {
                for (Integer num : flexLine.mIndicesAlignSelfStretch) {
                    View reorderedFlexItemAt2 = this.mFlexContainer.getReorderedFlexItemAt(num.intValue());
                    switch (flexDirection) {
                        case 0:
                        case 1:
                            stretchViewVertically(reorderedFlexItemAt2, flexLine.mCrossSize, num.intValue());
                            break;
                        case 2:
                        case 3:
                            stretchViewHorizontally(reorderedFlexItemAt2, flexLine.mCrossSize, num.intValue());
                            break;
                        default:
                            throw new IllegalArgumentException("Invalid flex direction: " + flexDirection);
                    }
                }
            }
            return;
        }
        List<FlexLine> flexLinesInternal = this.mFlexContainer.getFlexLinesInternal();
        int size = flexLinesInternal.size();
        for (int i2 = this.mIndexToFlexLine != null ? this.mIndexToFlexLine[i] : 0; i2 < size; i2++) {
            FlexLine flexLine2 = flexLinesInternal.get(i2);
            int i3 = flexLine2.mItemCount;
            for (int i4 = 0; i4 < i3; i4++) {
                int i5 = flexLine2.mFirstIndex + i4;
                if (i4 < this.mFlexContainer.getFlexItemCount() && (reorderedFlexItemAt = this.mFlexContainer.getReorderedFlexItemAt(i5)) != null && reorderedFlexItemAt.getVisibility() != 8) {
                    FlexItem flexItem = (FlexItem) reorderedFlexItemAt.getLayoutParams();
                    if (flexItem.getAlignSelf() == -1 || flexItem.getAlignSelf() == 4) {
                        switch (flexDirection) {
                            case 0:
                            case 1:
                                stretchViewVertically(reorderedFlexItemAt, flexLine2.mCrossSize, i5);
                                break;
                            case 2:
                            case 3:
                                stretchViewHorizontally(reorderedFlexItemAt, flexLine2.mCrossSize, i5);
                                break;
                            default:
                                throw new IllegalArgumentException("Invalid flex direction: " + flexDirection);
                        }
                    }
                }
            }
        }
    }
}
