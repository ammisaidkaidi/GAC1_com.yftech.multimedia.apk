package com.alibaba.android.vlayout.layout;

import android.util.Log;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.OrientationHelperEx;
import com.alibaba.android.vlayout.VirtualLayoutManager;

/* JADX INFO: loaded from: classes3.dex */
public class StickyLayoutHelper extends FixAreaLayoutHelper {
    private static final String TAG = "StickyStartLayoutHelper";
    private boolean mDoNormalHandle;
    private View mFixView;
    private int mOffset;
    private int mPos;
    private boolean mStickyStart;

    public StickyLayoutHelper() {
        this(true);
    }

    public StickyLayoutHelper(boolean z) {
        this.mPos = -1;
        this.mStickyStart = true;
        this.mOffset = 0;
        this.mFixView = null;
        this.mDoNormalHandle = false;
        this.mStickyStart = z;
        setItemCount(1);
    }

    private void doMeasure(View view, LayoutManagerHelper layoutManagerHelper) {
        int childMeasureSpec;
        int childMeasureSpec2;
        VirtualLayoutManager.LayoutParams layoutParams = (VirtualLayoutManager.LayoutParams) view.getLayoutParams();
        boolean z = layoutManagerHelper.getOrientation() == 1;
        int contentWidth = ((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingLeft()) - layoutManagerHelper.getPaddingRight()) - getHorizontalMargin();
        int contentHeight = ((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom()) - getVerticalMargin();
        float f = layoutParams.mAspectRatio;
        if (z) {
            int childMeasureSpec3 = layoutManagerHelper.getChildMeasureSpec(contentWidth, layoutParams.width, false);
            if (Float.isNaN(f) || f <= 0.0f) {
                childMeasureSpec2 = (Float.isNaN(this.mAspectRatio) || this.mAspectRatio <= 0.0f) ? layoutManagerHelper.getChildMeasureSpec(contentHeight, layoutParams.height, true) : View.MeasureSpec.makeMeasureSpec((int) (((double) (contentWidth / this.mAspectRatio)) + 0.5d), 1073741824);
            } else {
                childMeasureSpec2 = View.MeasureSpec.makeMeasureSpec((int) ((contentWidth / f) + 0.5f), 1073741824);
            }
            layoutManagerHelper.measureChildWithMargins(view, childMeasureSpec3, childMeasureSpec2);
            return;
        }
        int childMeasureSpec4 = layoutManagerHelper.getChildMeasureSpec(contentHeight, layoutParams.height, false);
        if (Float.isNaN(f) || f <= 0.0f) {
            childMeasureSpec = (Float.isNaN(this.mAspectRatio) || this.mAspectRatio <= 0.0f) ? layoutManagerHelper.getChildMeasureSpec(contentWidth, layoutParams.width, true) : View.MeasureSpec.makeMeasureSpec((int) (((double) (contentHeight * this.mAspectRatio)) + 0.5d), 1073741824);
        } else {
            childMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (((double) (contentHeight * f)) + 0.5d), 1073741824);
        }
        layoutManagerHelper.measureChildWithMargins(view, childMeasureSpec, childMeasureSpec4);
    }

    private void fixLayoutStateFromAbnormal2Normal(OrientationHelperEx orientationHelperEx, RecyclerView.Recycler recycler, int i, int i2, LayoutManagerHelper layoutManagerHelper) {
        Log.i(TAG, "abnormal pos: " + this.mPos + " start: " + i + " end: " + i2);
        if (this.mFixView != null) {
            if (this.mStickyStart) {
                for (int childCount = layoutManagerHelper.getChildCount() - 1; childCount >= 0; childCount--) {
                    View childAt = layoutManagerHelper.getChildAt(childCount);
                    int position = layoutManagerHelper.getPosition(childAt);
                    if (position < this.mPos) {
                        int decoratedEnd = orientationHelperEx.getDecoratedEnd(childAt);
                        LayoutHelper layoutHelperFindLayoutHelperByPosition = layoutManagerHelper.findLayoutHelperByPosition(position);
                        if (layoutHelperFindLayoutHelperByPosition instanceof RangeGridLayoutHelper) {
                            decoratedEnd += ((RangeGridLayoutHelper) layoutHelperFindLayoutHelperByPosition).getBorderEndSpace(layoutManagerHelper);
                        } else if (layoutHelperFindLayoutHelperByPosition instanceof MarginLayoutHelper) {
                            MarginLayoutHelper marginLayoutHelper = (MarginLayoutHelper) layoutHelperFindLayoutHelperByPosition;
                            decoratedEnd = decoratedEnd + marginLayoutHelper.getMarginBottom() + marginLayoutHelper.getPaddingBottom();
                        }
                        if (decoratedEnd >= this.mOffset + this.mAdjuster.top) {
                            this.mDoNormalHandle = true;
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            for (int i3 = 0; i3 < layoutManagerHelper.getChildCount(); i3++) {
                View childAt2 = layoutManagerHelper.getChildAt(i3);
                int position2 = layoutManagerHelper.getPosition(childAt2);
                if (position2 > this.mPos) {
                    int decoratedStart = orientationHelperEx.getDecoratedStart(childAt2);
                    LayoutHelper layoutHelperFindLayoutHelperByPosition2 = layoutManagerHelper.findLayoutHelperByPosition(position2);
                    if (layoutHelperFindLayoutHelperByPosition2 instanceof RangeGridLayoutHelper) {
                        decoratedStart -= ((RangeGridLayoutHelper) layoutHelperFindLayoutHelperByPosition2).getBorderStartSpace(layoutManagerHelper);
                    } else if (layoutHelperFindLayoutHelperByPosition2 instanceof MarginLayoutHelper) {
                        MarginLayoutHelper marginLayoutHelper2 = (MarginLayoutHelper) layoutHelperFindLayoutHelperByPosition2;
                        decoratedStart = (decoratedStart - marginLayoutHelper2.getMarginTop()) - marginLayoutHelper2.getPaddingTop();
                    }
                    if (decoratedStart >= this.mOffset + this.mAdjuster.bottom) {
                        this.mDoNormalHandle = true;
                        return;
                    }
                    return;
                }
            }
        }
    }

    private void fixLayoutStateInCase1(OrientationHelperEx orientationHelperEx, RecyclerView.Recycler recycler, int i, int i2, LayoutManagerHelper layoutManagerHelper) {
        int i3;
        int startAfterPadding;
        int i4;
        int i5;
        int decoratedEnd;
        int i6;
        int paddingLeft;
        int decoratedMeasurementInOther;
        View childAt;
        int childCount;
        int endAfterPadding;
        if ((!this.mStickyStart || i2 < this.mPos) && (this.mStickyStart || i > this.mPos)) {
            layoutManagerHelper.removeChildView(this.mFixView);
            layoutManagerHelper.recycleView(this.mFixView);
            this.mFixView = null;
            return;
        }
        int decoratedMeasurement = orientationHelperEx.getDecoratedMeasurement(this.mFixView);
        int i7 = 0;
        boolean z = layoutManagerHelper.getOrientation() == 1;
        int i8 = z ? this.mAdjuster.top : this.mAdjuster.left;
        int i9 = z ? this.mAdjuster.bottom : this.mAdjuster.right;
        int i10 = -1;
        if (z) {
            if (layoutManagerHelper.isDoLayoutRTL()) {
                decoratedMeasurementInOther = layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingRight();
                paddingLeft = decoratedMeasurementInOther - orientationHelperEx.getDecoratedMeasurementInOther(this.mFixView);
            } else {
                paddingLeft = layoutManagerHelper.getPaddingLeft();
                decoratedMeasurementInOther = orientationHelperEx.getDecoratedMeasurementInOther(this.mFixView) + paddingLeft;
            }
            if (this.mStickyStart) {
                childCount = layoutManagerHelper.getChildCount() - 1;
                childAt = null;
                while (true) {
                    if (childCount < 0) {
                        startAfterPadding = 0;
                        endAfterPadding = 0;
                    } else {
                        childAt = layoutManagerHelper.getChildAt(childCount);
                        int position = layoutManagerHelper.getPosition(childAt);
                        if (position < this.mPos) {
                            int decoratedEnd2 = orientationHelperEx.getDecoratedEnd(childAt);
                            LayoutHelper layoutHelperFindLayoutHelperByPosition = layoutManagerHelper.findLayoutHelperByPosition(position);
                            if (layoutHelperFindLayoutHelperByPosition instanceof RangeGridLayoutHelper) {
                                decoratedEnd2 += ((RangeGridLayoutHelper) layoutHelperFindLayoutHelperByPosition).getBorderEndSpace(layoutManagerHelper);
                            } else if (layoutHelperFindLayoutHelperByPosition instanceof MarginLayoutHelper) {
                                MarginLayoutHelper marginLayoutHelper = (MarginLayoutHelper) layoutHelperFindLayoutHelperByPosition;
                                decoratedEnd2 = decoratedEnd2 + marginLayoutHelper.getMarginBottom() + marginLayoutHelper.getPaddingBottom();
                            }
                            endAfterPadding = decoratedEnd2 + decoratedMeasurement;
                            this.mDoNormalHandle = true;
                            startAfterPadding = decoratedEnd2;
                            i10 = childCount;
                        } else {
                            childCount--;
                        }
                    }
                }
            } else {
                int i11 = 0;
                childAt = null;
                while (true) {
                    if (i11 < layoutManagerHelper.getChildCount()) {
                        childAt = layoutManagerHelper.getChildAt(i11);
                        int position2 = layoutManagerHelper.getPosition(childAt);
                        if (position2 > this.mPos) {
                            int decoratedStart = orientationHelperEx.getDecoratedStart(childAt);
                            LayoutHelper layoutHelperFindLayoutHelperByPosition2 = layoutManagerHelper.findLayoutHelperByPosition(position2);
                            if (layoutHelperFindLayoutHelperByPosition2 instanceof RangeGridLayoutHelper) {
                                decoratedStart -= ((RangeGridLayoutHelper) layoutHelperFindLayoutHelperByPosition2).getBorderStartSpace(layoutManagerHelper);
                            } else if (layoutHelperFindLayoutHelperByPosition2 instanceof MarginLayoutHelper) {
                                MarginLayoutHelper marginLayoutHelper2 = (MarginLayoutHelper) layoutHelperFindLayoutHelperByPosition2;
                                decoratedStart = (decoratedStart - marginLayoutHelper2.getMarginTop()) - marginLayoutHelper2.getPaddingTop();
                            }
                            childCount = i11 + 1;
                            this.mDoNormalHandle = true;
                            startAfterPadding = decoratedStart - decoratedMeasurement;
                            endAfterPadding = decoratedStart;
                            i10 = childCount;
                        } else {
                            i11++;
                        }
                    } else {
                        startAfterPadding = 0;
                        endAfterPadding = 0;
                    }
                }
            }
            if (childAt == null || i10 < 0) {
                this.mDoNormalHandle = false;
            }
            if (layoutManagerHelper.getReverseLayout() || !this.mStickyStart) {
                if (endAfterPadding > (orientationHelperEx.getEndAfterPadding() - this.mOffset) - i9) {
                    this.mDoNormalHandle = false;
                }
            } else if (startAfterPadding < orientationHelperEx.getStartAfterPadding() + this.mOffset + i8) {
                this.mDoNormalHandle = false;
            }
            if (!this.mDoNormalHandle) {
                if (layoutManagerHelper.getReverseLayout() || !this.mStickyStart) {
                    endAfterPadding = (orientationHelperEx.getEndAfterPadding() - this.mOffset) - i9;
                    startAfterPadding = endAfterPadding - decoratedMeasurement;
                } else {
                    startAfterPadding = orientationHelperEx.getStartAfterPadding() + this.mOffset + i8;
                    endAfterPadding = startAfterPadding + decoratedMeasurement;
                }
            }
            i3 = decoratedMeasurementInOther;
            i5 = paddingLeft;
            i4 = endAfterPadding;
        } else {
            int paddingTop = layoutManagerHelper.getPaddingTop();
            int decoratedMeasurementInOther2 = orientationHelperEx.getDecoratedMeasurementInOther(this.mFixView) + paddingTop;
            if (this.mDoNormalHandle) {
                if (this.mStickyStart) {
                    int childCount2 = layoutManagerHelper.getChildCount() - 1;
                    while (true) {
                        if (childCount2 >= 0) {
                            View childAt2 = layoutManagerHelper.getChildAt(childCount2);
                            if (layoutManagerHelper.getPosition(childAt2) < this.mPos) {
                                decoratedEnd = orientationHelperEx.getDecoratedEnd(childAt2);
                                i6 = decoratedMeasurement + decoratedEnd;
                                i7 = decoratedEnd;
                            } else {
                                childCount2--;
                            }
                        } else {
                            i6 = 0;
                        }
                    }
                } else {
                    int i12 = 0;
                    while (true) {
                        if (i12 < layoutManagerHelper.getChildCount()) {
                            View childAt3 = layoutManagerHelper.getChildAt(i12);
                            if (layoutManagerHelper.getPosition(childAt3) > this.mPos) {
                                int decoratedStart2 = orientationHelperEx.getDecoratedStart(childAt3);
                                decoratedEnd = decoratedStart2 - decoratedMeasurement;
                                i6 = decoratedStart2;
                                i7 = decoratedEnd;
                            } else {
                                i12++;
                            }
                        } else {
                            i6 = 0;
                        }
                    }
                }
                startAfterPadding = paddingTop;
                i5 = i7;
                i4 = decoratedMeasurementInOther2;
                i3 = i6;
            } else if (layoutManagerHelper.getReverseLayout() || !this.mStickyStart) {
                int endAfterPadding2 = (orientationHelperEx.getEndAfterPadding() - this.mOffset) - i9;
                i3 = endAfterPadding2;
                startAfterPadding = paddingTop;
                i4 = decoratedMeasurementInOther2;
                i5 = endAfterPadding2 - decoratedMeasurement;
            } else {
                int startAfterPadding2 = orientationHelperEx.getStartAfterPadding() + this.mOffset + i8;
                i3 = decoratedMeasurement + startAfterPadding2;
                startAfterPadding = paddingTop;
                i4 = decoratedMeasurementInOther2;
                i5 = startAfterPadding2;
            }
        }
        layoutChildWithMargin(this.mFixView, i5, startAfterPadding, i3, i4, layoutManagerHelper);
        if (!this.mDoNormalHandle) {
            layoutManagerHelper.showView(this.mFixView);
            layoutManagerHelper.addFixedView(this.mFixView);
        } else if (i10 >= 0) {
            layoutManagerHelper.addChildView(this.mFixView, i10);
            this.mFixView = null;
        }
    }

    private void fixLayoutStateInCase2(OrientationHelperEx orientationHelperEx, RecyclerView.Recycler recycler, int i, int i2, LayoutManagerHelper layoutManagerHelper) {
        boolean z;
        boolean z2;
        int i3;
        int decoratedMeasurementInOther;
        int i4;
        int i5;
        int decoratedStart;
        int i6;
        int i7;
        int paddingLeft;
        int i8;
        View childAt;
        View viewFindViewByPosition = this.mFixView;
        if (viewFindViewByPosition == null) {
            viewFindViewByPosition = layoutManagerHelper.findViewByPosition(this.mPos);
        }
        boolean z3 = layoutManagerHelper.getOrientation() == 1;
        int i9 = z3 ? this.mAdjuster.top : this.mAdjuster.left;
        int i10 = z3 ? this.mAdjuster.bottom : this.mAdjuster.right;
        if ((!this.mStickyStart || i2 < this.mPos) && (this.mStickyStart || i > this.mPos)) {
            z = false;
        } else if (viewFindViewByPosition == null) {
            z = this.mOffset + (this.mStickyStart ? i9 : i10) > 0;
            this.mFixView = recycler.getViewForPosition(this.mPos);
            doMeasure(this.mFixView, layoutManagerHelper);
        } else {
            if (this.mStickyStart && orientationHelperEx.getDecoratedStart(viewFindViewByPosition) >= orientationHelperEx.getStartAfterPadding() + this.mOffset + i9) {
                this.mFixView = viewFindViewByPosition;
            } else if (this.mStickyStart || orientationHelperEx.getDecoratedEnd(viewFindViewByPosition) > (orientationHelperEx.getEndAfterPadding() - this.mOffset) - i10) {
                this.mFixView = viewFindViewByPosition;
                z = false;
            } else {
                this.mFixView = viewFindViewByPosition;
            }
            z = true;
        }
        if (this.mFixView != null) {
            if (((RecyclerView.LayoutParams) this.mFixView.getLayoutParams()).isItemRemoved()) {
                return;
            }
            int decoratedMeasurement = orientationHelperEx.getDecoratedMeasurement(this.mFixView);
            int i11 = -1;
            if (z3) {
                if (layoutManagerHelper.isDoLayoutRTL()) {
                    decoratedMeasurementInOther = layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingRight();
                    paddingLeft = decoratedMeasurementInOther - orientationHelperEx.getDecoratedMeasurementInOther(this.mFixView);
                } else {
                    paddingLeft = layoutManagerHelper.getPaddingLeft();
                    decoratedMeasurementInOther = orientationHelperEx.getDecoratedMeasurementInOther(this.mFixView) + paddingLeft;
                }
                if (z) {
                    if (!this.mStickyStart) {
                        childAt = null;
                        int i12 = 0;
                        while (true) {
                            if (i12 >= layoutManagerHelper.getChildCount()) {
                                i4 = 0;
                                i8 = 0;
                                break;
                            }
                            childAt = layoutManagerHelper.getChildAt(i12);
                            int position = layoutManagerHelper.getPosition(childAt);
                            if (position > this.mPos) {
                                int decoratedStart2 = orientationHelperEx.getDecoratedStart(childAt);
                                LayoutHelper layoutHelperFindLayoutHelperByPosition = layoutManagerHelper.findLayoutHelperByPosition(position);
                                if (layoutHelperFindLayoutHelperByPosition instanceof RangeGridLayoutHelper) {
                                    decoratedStart2 -= ((RangeGridLayoutHelper) layoutHelperFindLayoutHelperByPosition).getBorderStartSpace(layoutManagerHelper);
                                } else if (layoutHelperFindLayoutHelperByPosition instanceof MarginLayoutHelper) {
                                    MarginLayoutHelper marginLayoutHelper = (MarginLayoutHelper) layoutHelperFindLayoutHelperByPosition;
                                    decoratedStart2 = (decoratedStart2 - marginLayoutHelper.getMarginTop()) - marginLayoutHelper.getPaddingTop();
                                }
                                i8 = decoratedStart2;
                                i11 = i12;
                                i4 = i8 - decoratedMeasurement;
                                break;
                            }
                            i12++;
                        }
                    } else {
                        int childCount = layoutManagerHelper.getChildCount() - 1;
                        childAt = null;
                        while (true) {
                            if (childCount < 0) {
                                i4 = 0;
                                i8 = 0;
                                break;
                            }
                            childAt = layoutManagerHelper.getChildAt(childCount);
                            int position2 = layoutManagerHelper.getPosition(childAt);
                            if (position2 < this.mPos) {
                                int decoratedEnd = orientationHelperEx.getDecoratedEnd(childAt);
                                LayoutHelper layoutHelperFindLayoutHelperByPosition2 = layoutManagerHelper.findLayoutHelperByPosition(position2);
                                if (layoutHelperFindLayoutHelperByPosition2 instanceof RangeGridLayoutHelper) {
                                    decoratedEnd += ((RangeGridLayoutHelper) layoutHelperFindLayoutHelperByPosition2).getBorderEndSpace(layoutManagerHelper);
                                } else if (layoutHelperFindLayoutHelperByPosition2 instanceof MarginLayoutHelper) {
                                    MarginLayoutHelper marginLayoutHelper2 = (MarginLayoutHelper) layoutHelperFindLayoutHelperByPosition2;
                                    decoratedEnd = decoratedEnd + marginLayoutHelper2.getMarginBottom() + marginLayoutHelper2.getPaddingBottom();
                                }
                                i4 = decoratedEnd;
                                i8 = decoratedEnd + decoratedMeasurement;
                                i11 = childCount + 1;
                                break;
                            }
                            childCount--;
                        }
                    }
                    if (childAt == null || i11 < 0) {
                        z = false;
                    }
                    z2 = (layoutManagerHelper.getReverseLayout() || !this.mStickyStart ? i8 <= (orientationHelperEx.getEndAfterPadding() - this.mOffset) - i10 : i4 >= (orientationHelperEx.getStartAfterPadding() + this.mOffset) + i9) ? z : false;
                } else {
                    z2 = z;
                    i4 = 0;
                    i8 = 0;
                }
                if (z2) {
                    i3 = i8;
                } else {
                    if (layoutManagerHelper.getReverseLayout() || !this.mStickyStart) {
                        int endAfterPadding = (orientationHelperEx.getEndAfterPadding() - this.mOffset) - i10;
                        i3 = endAfterPadding;
                        i4 = endAfterPadding - decoratedMeasurement;
                    } else {
                        int startAfterPadding = orientationHelperEx.getStartAfterPadding() + this.mOffset + i9;
                        i4 = startAfterPadding;
                        i3 = decoratedMeasurement + startAfterPadding;
                    }
                    i5 = paddingLeft;
                }
                i5 = paddingLeft;
            } else {
                int paddingTop = layoutManagerHelper.getPaddingTop();
                int decoratedMeasurementInOther2 = orientationHelperEx.getDecoratedMeasurementInOther(this.mFixView) + paddingTop;
                if (z) {
                    if (!this.mStickyStart) {
                        int i13 = 0;
                        while (true) {
                            if (i13 >= layoutManagerHelper.getChildCount()) {
                                decoratedStart = 0;
                                i6 = 0;
                                break;
                            }
                            View childAt2 = layoutManagerHelper.getChildAt(i13);
                            if (layoutManagerHelper.getPosition(childAt2) > this.mPos) {
                                decoratedStart = orientationHelperEx.getDecoratedStart(childAt2);
                                i6 = decoratedStart - decoratedMeasurement;
                                break;
                            }
                            i13++;
                        }
                    } else {
                        int childCount2 = layoutManagerHelper.getChildCount() - 1;
                        while (true) {
                            if (childCount2 < 0) {
                                i7 = 0;
                                i6 = 0;
                                break;
                            }
                            View childAt3 = layoutManagerHelper.getChildAt(childCount2);
                            if (layoutManagerHelper.getPosition(childAt3) < this.mPos) {
                                int decoratedEnd2 = orientationHelperEx.getDecoratedEnd(childAt3);
                                i7 = decoratedMeasurement + decoratedEnd2;
                                i6 = decoratedEnd2;
                                break;
                            }
                            childCount2--;
                        }
                        decoratedStart = i7;
                    }
                    i4 = paddingTop;
                    i3 = decoratedMeasurementInOther2;
                    i5 = i6;
                    decoratedMeasurementInOther = decoratedStart;
                    z2 = z;
                } else if (layoutManagerHelper.getReverseLayout() || !this.mStickyStart) {
                    int endAfterPadding2 = (orientationHelperEx.getEndAfterPadding() - this.mOffset) - i10;
                    z2 = z;
                    i3 = decoratedMeasurementInOther2;
                    decoratedMeasurementInOther = endAfterPadding2;
                    i4 = paddingTop;
                    i5 = endAfterPadding2 - decoratedMeasurement;
                } else {
                    int startAfterPadding2 = orientationHelperEx.getStartAfterPadding() + this.mOffset + i9;
                    z2 = z;
                    i4 = paddingTop;
                    i3 = decoratedMeasurementInOther2;
                    decoratedMeasurementInOther = decoratedMeasurement + startAfterPadding2;
                    i5 = startAfterPadding2;
                }
            }
            layoutChildWithMargin(this.mFixView, i5, i4, decoratedMeasurementInOther, i3, layoutManagerHelper);
            if (!z2) {
                layoutManagerHelper.addFixedView(this.mFixView);
            } else if (i11 >= 0) {
                layoutManagerHelper.addChildView(this.mFixView, i11);
                this.mFixView = null;
            }
            z = z2;
        }
        this.mDoNormalHandle = z;
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void afterLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper) {
        super.afterLayout(recycler, state, i, i2, i3, layoutManagerHelper);
        if (this.mPos < 0) {
            return;
        }
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        if (!this.mDoNormalHandle && this.mPos >= i && this.mPos <= i2) {
            fixLayoutStateFromAbnormal2Normal(mainOrientationHelper, recycler, i, i2, layoutManagerHelper);
        }
        if (this.mDoNormalHandle || state.isPreLayout()) {
            state.isPreLayout();
            if (this.mFixView == null) {
                return;
            } else {
                layoutManagerHelper.removeChildView(this.mFixView);
            }
        }
        if (this.mDoNormalHandle || this.mFixView == null) {
            fixLayoutStateInCase2(mainOrientationHelper, recycler, i, i2, layoutManagerHelper);
        } else if (this.mFixView.getParent() == null) {
            layoutManagerHelper.addFixedView(this.mFixView);
        } else {
            fixLayoutStateInCase1(mainOrientationHelper, recycler, i, i2, layoutManagerHelper);
        }
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void beforeLayout(RecyclerView.Recycler recycler, RecyclerView.State state, LayoutManagerHelper layoutManagerHelper) {
        super.beforeLayout(recycler, state, layoutManagerHelper);
        if (this.mFixView != null && layoutManagerHelper.isViewHolderUpdated(this.mFixView)) {
            layoutManagerHelper.removeChildView(this.mFixView);
            recycler.recycleView(this.mFixView);
            this.mFixView = null;
        }
        this.mDoNormalHandle = false;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    @Nullable
    public View getFixedView() {
        return this.mFixView;
    }

    public boolean isStickyNow() {
        return !this.mDoNormalHandle;
    }

    /* JADX WARN: Code duplicated, block: B:67:0x01d6  */
    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        int paddingTop;
        int decoratedMeasurementInOther;
        int offset;
        int offset2;
        int startAfterPadding;
        int paddingLeft;
        int decoratedMeasurementInOther2;
        int endAfterPadding;
        int offset3;
        if (isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
            return;
        }
        View next = this.mFixView;
        if (next == null) {
            next = layoutStateWrapper.next(recycler);
        } else {
            layoutStateWrapper.skipCurrentPosition();
        }
        View view = next;
        if (view == null) {
            layoutChunkResult.mFinished = true;
            return;
        }
        doMeasure(view, layoutManagerHelper);
        boolean z = layoutManagerHelper.getOrientation() == 1;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        layoutChunkResult.mConsumed = mainOrientationHelper.getDecoratedMeasurement(view);
        this.mDoNormalHandle = true;
        int available = (layoutStateWrapper.getAvailable() - layoutChunkResult.mConsumed) + layoutStateWrapper.getExtra();
        if (layoutManagerHelper.getOrientation() == 1) {
            if (layoutManagerHelper.isDoLayoutRTL()) {
                decoratedMeasurementInOther2 = (layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingRight()) - this.mMarginRight;
                paddingLeft = decoratedMeasurementInOther2 - mainOrientationHelper.getDecoratedMeasurementInOther(view);
            } else {
                paddingLeft = this.mMarginLeft + layoutManagerHelper.getPaddingLeft();
                decoratedMeasurementInOther2 = mainOrientationHelper.getDecoratedMeasurementInOther(view) + paddingLeft;
            }
            if (layoutStateWrapper.getLayoutDirection() == -1) {
                endAfterPadding = layoutStateWrapper.getOffset() - this.mMarginBottom;
                offset3 = layoutStateWrapper.getOffset() - layoutChunkResult.mConsumed;
            } else if (this.mStickyStart) {
                offset3 = this.mMarginTop + layoutStateWrapper.getOffset();
                endAfterPadding = layoutStateWrapper.getOffset() + layoutChunkResult.mConsumed;
            } else {
                endAfterPadding = ((mainOrientationHelper.getEndAfterPadding() - this.mMarginBottom) - this.mOffset) - this.mAdjuster.bottom;
                offset3 = endAfterPadding - layoutChunkResult.mConsumed;
            }
            if (layoutManagerHelper.getReverseLayout() || !this.mStickyStart) {
                if ((available >= this.mOffset + this.mAdjuster.bottom || layoutStateWrapper.getItemDirection() != 1) && endAfterPadding <= this.mMarginBottom + this.mOffset + this.mAdjuster.bottom) {
                    offset2 = decoratedMeasurementInOther2;
                    startAfterPadding = paddingLeft;
                    decoratedMeasurementInOther = endAfterPadding;
                    paddingTop = offset3;
                } else {
                    this.mDoNormalHandle = false;
                    this.mFixView = view;
                    int endAfterPadding2 = ((mainOrientationHelper.getEndAfterPadding() - this.mMarginBottom) - this.mOffset) - this.mAdjuster.bottom;
                    offset2 = decoratedMeasurementInOther2;
                    startAfterPadding = paddingLeft;
                    decoratedMeasurementInOther = endAfterPadding2;
                    paddingTop = endAfterPadding2 - layoutChunkResult.mConsumed;
                }
            } else if ((available >= this.mOffset + this.mAdjuster.top || layoutStateWrapper.getItemDirection() != -1) && offset3 >= this.mMarginTop + this.mOffset + this.mAdjuster.top) {
                Log.i("Sticky", "remainingSpace: " + available + "    offset: " + this.mOffset);
                offset2 = decoratedMeasurementInOther2;
                startAfterPadding = paddingLeft;
                decoratedMeasurementInOther = endAfterPadding;
                paddingTop = offset3;
            } else {
                this.mDoNormalHandle = false;
                this.mFixView = view;
                int startAfterPadding2 = mainOrientationHelper.getStartAfterPadding() + this.mMarginTop + this.mOffset + this.mAdjuster.top;
                offset2 = decoratedMeasurementInOther2;
                startAfterPadding = paddingLeft;
                paddingTop = startAfterPadding2;
                decoratedMeasurementInOther = layoutChunkResult.mConsumed + startAfterPadding2;
            }
        } else {
            paddingTop = layoutManagerHelper.getPaddingTop();
            decoratedMeasurementInOther = mainOrientationHelper.getDecoratedMeasurementInOther(view) + paddingTop + this.mMarginTop;
            if (layoutStateWrapper.getLayoutDirection() == -1) {
                offset2 = layoutStateWrapper.getOffset() - this.mMarginRight;
                offset = layoutStateWrapper.getOffset() - layoutChunkResult.mConsumed;
            } else {
                offset = this.mMarginLeft + layoutStateWrapper.getOffset();
                offset2 = layoutStateWrapper.getOffset() + layoutChunkResult.mConsumed;
            }
            if (layoutManagerHelper.getReverseLayout() || !this.mStickyStart) {
                if (available < this.mOffset + this.mAdjuster.right) {
                    this.mDoNormalHandle = false;
                    this.mFixView = view;
                    int endAfterPadding3 = (mainOrientationHelper.getEndAfterPadding() - this.mOffset) - this.mAdjuster.right;
                    offset2 = endAfterPadding3;
                    startAfterPadding = endAfterPadding3 - layoutChunkResult.mConsumed;
                } else {
                    startAfterPadding = offset;
                }
            } else if (available < this.mOffset + this.mAdjuster.left) {
                this.mDoNormalHandle = false;
                this.mFixView = view;
                startAfterPadding = mainOrientationHelper.getStartAfterPadding() + this.mOffset + this.mAdjuster.left;
                offset2 = layoutChunkResult.mConsumed;
            } else {
                startAfterPadding = offset;
            }
        }
        layoutChildWithMargin(view, startAfterPadding, paddingTop, offset2, decoratedMeasurementInOther, layoutManagerHelper);
        layoutChunkResult.mConsumed += z ? getVerticalMargin() : getHorizontalMargin();
        if (state.isPreLayout()) {
            this.mDoNormalHandle = true;
        }
        if (this.mDoNormalHandle) {
            layoutManagerHelper.addChildView(layoutStateWrapper, view);
            handleStateOnResult(layoutChunkResult, view);
            this.mFixView = null;
        }
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void onClear(LayoutManagerHelper layoutManagerHelper) {
        super.onClear(layoutManagerHelper);
        if (this.mFixView != null) {
            layoutManagerHelper.removeChildView(this.mFixView);
            layoutManagerHelper.recycleView(this.mFixView);
            this.mFixView = null;
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onRangeChange(int i, int i2) {
        this.mPos = i;
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public boolean requireLayoutView() {
        return false;
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void setItemCount(int i) {
        if (i > 0) {
            super.setItemCount(1);
        } else {
            super.setItemCount(0);
        }
    }

    public void setOffset(int i) {
        this.mOffset = i;
    }

    public void setStickyStart(boolean z) {
        this.mStickyStart = z;
    }
}
