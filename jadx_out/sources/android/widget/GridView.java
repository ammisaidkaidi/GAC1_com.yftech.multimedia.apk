package android.widget;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.MathUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.RemotableViewMethod;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.ViewRootImpl;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.GridLayoutAnimationController;
import com.android.internal.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
public class GridView extends AbsListView {
    public static final int AUTO_FIT = -1;
    public static final int NO_STRETCH = 0;
    public static final int STRETCH_COLUMN_WIDTH = 2;
    public static final int STRETCH_SPACING = 1;
    public static final int STRETCH_SPACING_UNIFORM = 3;
    private int mColumnWidth;
    private int mGravity;
    private int mHorizontalSpacing;
    private int mNumColumns;
    private View mReferenceView;
    private View mReferenceViewInSelectedRow;
    private int mRequestedColumnWidth;
    private int mRequestedHorizontalSpacing;
    private int mRequestedNumColumns;
    private int mStretchMode;
    private final Rect mTempRect;
    private int mVerticalSpacing;

    @Retention(RetentionPolicy.SOURCE)
    public @interface StretchMode {
    }

    public GridView(Context context) {
        this(context, null);
    }

    public GridView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842865);
    }

    public GridView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public GridView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mNumColumns = -1;
        this.mHorizontalSpacing = 0;
        this.mVerticalSpacing = 0;
        this.mStretchMode = 2;
        this.mReferenceView = null;
        this.mReferenceViewInSelectedRow = null;
        this.mGravity = 8388611;
        this.mTempRect = new Rect();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.GridView, i, i2);
        setHorizontalSpacing(typedArrayObtainStyledAttributes.getDimensionPixelOffset(1, 0));
        setVerticalSpacing(typedArrayObtainStyledAttributes.getDimensionPixelOffset(2, 0));
        int i3 = typedArrayObtainStyledAttributes.getInt(3, 2);
        if (i3 >= 0) {
            setStretchMode(i3);
        }
        int dimensionPixelOffset = typedArrayObtainStyledAttributes.getDimensionPixelOffset(4, -1);
        if (dimensionPixelOffset > 0) {
            setColumnWidth(dimensionPixelOffset);
        }
        setNumColumns(typedArrayObtainStyledAttributes.getInt(5, 1));
        int i4 = typedArrayObtainStyledAttributes.getInt(0, -1);
        if (i4 >= 0) {
            setGravity(i4);
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    private void adjustForBottomFadingEdge(View view, int i, int i2) {
        if (view.getBottom() > i2) {
            offsetChildrenTopAndBottom(-Math.min(view.getTop() - i, view.getBottom() - i2));
        }
    }

    private void adjustForTopFadingEdge(View view, int i, int i2) {
        if (view.getTop() < i) {
            offsetChildrenTopAndBottom(Math.min(i - view.getTop(), i2 - view.getBottom()));
        }
    }

    private void adjustViewsUpOrDown() {
        int childCount = getChildCount();
        if (childCount > 0) {
            int i = 0;
            if (this.mStackFromBottom) {
                int bottom = getChildAt(childCount - 1).getBottom() - (getHeight() - this.mListPadding.bottom);
                if (this.mFirstPosition + childCount < this.mItemCount) {
                    bottom += this.mVerticalSpacing;
                }
                if (bottom <= 0) {
                    i = bottom;
                }
            } else {
                int top = getChildAt(0).getTop() - this.mListPadding.top;
                if (this.mFirstPosition != 0) {
                    top -= this.mVerticalSpacing;
                }
                if (top >= 0) {
                    i = top;
                }
            }
            if (i != 0) {
                offsetChildrenTopAndBottom(-i);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:33:0x0059  */
    /* JADX WARN: Code duplicated, block: B:34:0x005c  */
    private boolean commonKey(int i, int i2, KeyEvent keyEvent) throws Throwable {
        boolean zResurrectSelectionIfNeeded;
        if (this.mAdapter == null) {
            return false;
        }
        if (this.mDataChanged) {
            layoutChildren();
        }
        int action = keyEvent.getAction();
        if (KeyEvent.isConfirmKey(i) && keyEvent.hasNoModifiers() && action != 1) {
            zResurrectSelectionIfNeeded = resurrectSelectionIfNeeded();
            if (!zResurrectSelectionIfNeeded && keyEvent.getRepeatCount() == 0 && getChildCount() > 0) {
                keyPressed();
                zResurrectSelectionIfNeeded = true;
            }
        } else {
            zResurrectSelectionIfNeeded = false;
        }
        if (!zResurrectSelectionIfNeeded && action != 1) {
            switch (i) {
                case 19:
                    if (!keyEvent.hasNoModifiers()) {
                        if (keyEvent.hasModifiers(2)) {
                            if (resurrectSelectionIfNeeded() || fullScroll(33)) {
                                zResurrectSelectionIfNeeded = true;
                            } else {
                                zResurrectSelectionIfNeeded = false;
                            }
                        }
                    } else if (resurrectSelectionIfNeeded() || arrowScroll(33)) {
                        zResurrectSelectionIfNeeded = true;
                    } else {
                        zResurrectSelectionIfNeeded = false;
                    }
                    break;
                case 20:
                    if (!keyEvent.hasNoModifiers()) {
                        if (keyEvent.hasModifiers(2)) {
                            if (resurrectSelectionIfNeeded() || fullScroll(130)) {
                                zResurrectSelectionIfNeeded = true;
                            } else {
                                zResurrectSelectionIfNeeded = false;
                            }
                        }
                    } else if (resurrectSelectionIfNeeded() || arrowScroll(130)) {
                        zResurrectSelectionIfNeeded = true;
                    } else {
                        zResurrectSelectionIfNeeded = false;
                    }
                    break;
                case 21:
                    if (keyEvent.hasNoModifiers()) {
                        if (resurrectSelectionIfNeeded() || arrowScroll(17)) {
                            zResurrectSelectionIfNeeded = true;
                        } else {
                            zResurrectSelectionIfNeeded = false;
                        }
                    }
                    break;
                case 22:
                    if (keyEvent.hasNoModifiers()) {
                        if (resurrectSelectionIfNeeded() || arrowScroll(66)) {
                            zResurrectSelectionIfNeeded = true;
                        } else {
                            zResurrectSelectionIfNeeded = false;
                        }
                    }
                    break;
                case 61:
                    if (!keyEvent.hasNoModifiers()) {
                        if (keyEvent.hasModifiers(1)) {
                            if (resurrectSelectionIfNeeded() || sequenceScroll(1)) {
                                zResurrectSelectionIfNeeded = true;
                            } else {
                                zResurrectSelectionIfNeeded = false;
                            }
                        }
                    } else if (resurrectSelectionIfNeeded() || sequenceScroll(2)) {
                        zResurrectSelectionIfNeeded = true;
                    } else {
                        zResurrectSelectionIfNeeded = false;
                    }
                    break;
                case 92:
                    if (!keyEvent.hasNoModifiers()) {
                        if (keyEvent.hasModifiers(2)) {
                            if (resurrectSelectionIfNeeded() || fullScroll(33)) {
                                zResurrectSelectionIfNeeded = true;
                            } else {
                                zResurrectSelectionIfNeeded = false;
                            }
                        }
                    } else if (resurrectSelectionIfNeeded() || pageScroll(33)) {
                        zResurrectSelectionIfNeeded = true;
                    } else {
                        zResurrectSelectionIfNeeded = false;
                    }
                    break;
                case 93:
                    if (!keyEvent.hasNoModifiers()) {
                        if (keyEvent.hasModifiers(2)) {
                            if (resurrectSelectionIfNeeded() || fullScroll(130)) {
                                zResurrectSelectionIfNeeded = true;
                            } else {
                                zResurrectSelectionIfNeeded = false;
                            }
                        }
                    } else if (resurrectSelectionIfNeeded() || pageScroll(130)) {
                        zResurrectSelectionIfNeeded = true;
                    } else {
                        zResurrectSelectionIfNeeded = false;
                    }
                    break;
                case 122:
                    if (keyEvent.hasNoModifiers()) {
                        if (resurrectSelectionIfNeeded() || fullScroll(33)) {
                            zResurrectSelectionIfNeeded = true;
                        } else {
                            zResurrectSelectionIfNeeded = false;
                        }
                    }
                    break;
                case 123:
                    if (keyEvent.hasNoModifiers()) {
                        if (resurrectSelectionIfNeeded() || fullScroll(130)) {
                            zResurrectSelectionIfNeeded = true;
                        } else {
                            zResurrectSelectionIfNeeded = false;
                        }
                    }
                    break;
            }
        }
        if (zResurrectSelectionIfNeeded || sendToTextFilter(i, i2, keyEvent)) {
            return true;
        }
        switch (action) {
            case 0:
                return super.onKeyDown(i, keyEvent);
            case 1:
                return super.onKeyUp(i, keyEvent);
            case 2:
                return super.onKeyMultiple(i, i2, keyEvent);
            default:
                return false;
        }
    }

    private void correctTooHigh(int i, int i2, int i3) {
        if ((this.mFirstPosition + i3) - 1 != this.mItemCount - 1 || i3 <= 0) {
            return;
        }
        int bottom = ((this.mBottom - this.mTop) - this.mListPadding.bottom) - getChildAt(i3 - 1).getBottom();
        View childAt = getChildAt(0);
        int top = childAt.getTop();
        if (bottom > 0) {
            if (this.mFirstPosition > 0 || top < this.mListPadding.top) {
                if (this.mFirstPosition == 0) {
                    bottom = Math.min(bottom, this.mListPadding.top - top);
                }
                offsetChildrenTopAndBottom(bottom);
                if (this.mFirstPosition > 0) {
                    int i4 = this.mFirstPosition;
                    if (this.mStackFromBottom) {
                        i = 1;
                    }
                    fillUp(i4 - i, childAt.getTop() - i2);
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    private void correctTooLow(int i, int i2, int i3) {
        if (this.mFirstPosition != 0 || i3 <= 0) {
            return;
        }
        int top = getChildAt(0).getTop();
        int i4 = this.mListPadding.top;
        int i5 = (this.mBottom - this.mTop) - this.mListPadding.bottom;
        int iMin = top - i4;
        View childAt = getChildAt(i3 - 1);
        int bottom = childAt.getBottom();
        int i6 = (this.mFirstPosition + i3) - 1;
        if (iMin > 0) {
            if (i6 < this.mItemCount - 1 || bottom > i5) {
                if (i6 == this.mItemCount - 1) {
                    iMin = Math.min(iMin, bottom - i5);
                }
                offsetChildrenTopAndBottom(-iMin);
                if (i6 < this.mItemCount - 1) {
                    if (!this.mStackFromBottom) {
                        i = 1;
                    }
                    fillDown(i6 + i, childAt.getBottom() + i2);
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private boolean determineColumns(int i) {
        int i2 = this.mRequestedHorizontalSpacing;
        int i3 = this.mStretchMode;
        int i4 = this.mRequestedColumnWidth;
        if (this.mRequestedNumColumns != -1) {
            this.mNumColumns = this.mRequestedNumColumns;
        } else if (i4 > 0) {
            this.mNumColumns = (i + i2) / (i4 + i2);
        } else {
            this.mNumColumns = 2;
        }
        if (this.mNumColumns <= 0) {
            this.mNumColumns = 1;
        }
        boolean z = false;
        if (i3 != 0) {
            int i5 = (i - (this.mNumColumns * i4)) - ((this.mNumColumns - 1) * i2);
            z = i5 < 0;
            switch (i3) {
                case 1:
                    this.mColumnWidth = i4;
                    if (this.mNumColumns <= 1) {
                        this.mHorizontalSpacing = i2 + i5;
                    } else {
                        this.mHorizontalSpacing = i2 + (i5 / (this.mNumColumns - 1));
                    }
                    break;
                case 2:
                    this.mColumnWidth = i4 + (i5 / this.mNumColumns);
                    this.mHorizontalSpacing = i2;
                    break;
                case 3:
                    this.mColumnWidth = i4;
                    if (this.mNumColumns <= 1) {
                        this.mHorizontalSpacing = i2 + i5;
                    } else {
                        this.mHorizontalSpacing = i2 + (i5 / (this.mNumColumns + 1));
                    }
                    break;
            }
        } else {
            this.mColumnWidth = i4;
            this.mHorizontalSpacing = i2;
        }
        return z;
    }

    private View fillDown(int i, int i2) {
        int i3 = this.mBottom - this.mTop;
        View view = null;
        if ((this.mGroupFlags & 34) == 34) {
            i3 -= this.mListPadding.bottom;
        }
        while (i2 < i3 && i < this.mItemCount) {
            View viewMakeRow = makeRow(i, i2, true);
            if (viewMakeRow != null) {
                view = viewMakeRow;
            }
            i2 = this.mReferenceView.getBottom() + this.mVerticalSpacing;
            i += this.mNumColumns;
        }
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return view;
    }

    private View fillFromBottom(int i, int i2) {
        int iMin = (this.mItemCount - 1) - Math.min(Math.max(i, this.mSelectedPosition), this.mItemCount - 1);
        return fillUp((this.mItemCount - 1) - (iMin - (iMin % this.mNumColumns)), i2);
    }

    private View fillFromSelection(int i, int i2, int i3) {
        int i4;
        int iMax;
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int i5 = this.mSelectedPosition;
        int i6 = this.mNumColumns;
        int i7 = this.mVerticalSpacing;
        if (this.mStackFromBottom) {
            int i8 = (this.mItemCount - 1) - i5;
            i4 = (this.mItemCount - 1) - (i8 - (i8 % i6));
            iMax = Math.max(0, (i4 - i6) + 1);
        } else {
            iMax = i5 - (i5 % i6);
            i4 = -1;
        }
        int topSelectionPixel = getTopSelectionPixel(i2, verticalFadingEdgeLength, iMax);
        int bottomSelectionPixel = getBottomSelectionPixel(i3, verticalFadingEdgeLength, i6, iMax);
        View viewMakeRow = makeRow(this.mStackFromBottom ? i4 : iMax, i, true);
        this.mFirstPosition = iMax;
        View view = this.mReferenceView;
        adjustForTopFadingEdge(view, topSelectionPixel, bottomSelectionPixel);
        adjustForBottomFadingEdge(view, topSelectionPixel, bottomSelectionPixel);
        if (this.mStackFromBottom) {
            fillDown(i4 + i6, view.getBottom() + i7);
            adjustViewsUpOrDown();
            fillUp(iMax - 1, view.getTop() - i7);
        } else {
            fillUp(iMax - i6, view.getTop() - i7);
            adjustViewsUpOrDown();
            fillDown(iMax + i6, view.getBottom() + i7);
        }
        return viewMakeRow;
    }

    private View fillFromTop(int i) {
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mSelectedPosition);
        this.mFirstPosition = Math.min(this.mFirstPosition, this.mItemCount - 1);
        if (this.mFirstPosition < 0) {
            this.mFirstPosition = 0;
        }
        this.mFirstPosition -= this.mFirstPosition % this.mNumColumns;
        return fillDown(this.mFirstPosition, i);
    }

    private View fillSelection(int i, int i2) {
        int i3;
        int iMax;
        int iReconcileSelectedPosition = reconcileSelectedPosition();
        int i4 = this.mNumColumns;
        int i5 = this.mVerticalSpacing;
        if (this.mStackFromBottom) {
            int i6 = (this.mItemCount - 1) - iReconcileSelectedPosition;
            i3 = (this.mItemCount - 1) - (i6 - (i6 % i4));
            iMax = Math.max(0, (i3 - i4) + 1);
        } else {
            iMax = iReconcileSelectedPosition - (iReconcileSelectedPosition % i4);
            i3 = -1;
        }
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        View viewMakeRow = makeRow(this.mStackFromBottom ? i3 : iMax, getTopSelectionPixel(i, verticalFadingEdgeLength, iMax), true);
        this.mFirstPosition = iMax;
        View view = this.mReferenceView;
        if (this.mStackFromBottom) {
            offsetChildrenTopAndBottom(getBottomSelectionPixel(i2, verticalFadingEdgeLength, i4, iMax) - view.getBottom());
            fillUp(iMax - 1, view.getTop() - i5);
            pinToTop(i);
            fillDown(i3 + i4, view.getBottom() + i5);
            adjustViewsUpOrDown();
        } else {
            fillDown(iMax + i4, view.getBottom() + i5);
            pinToBottom(i2);
            fillUp(iMax - i4, view.getTop() - i5);
            adjustViewsUpOrDown();
        }
        return viewMakeRow;
    }

    private View fillSpecific(int i, int i2) {
        int i3;
        int iMax;
        View viewFillDown;
        View viewFillUp;
        int i4 = this.mNumColumns;
        if (this.mStackFromBottom) {
            int i5 = (this.mItemCount - 1) - i;
            i3 = (this.mItemCount - 1) - (i5 - (i5 % i4));
            iMax = Math.max(0, (i3 - i4) + 1);
        } else {
            iMax = i - (i % i4);
            i3 = -1;
        }
        View viewMakeRow = makeRow(this.mStackFromBottom ? i3 : iMax, i2, true);
        this.mFirstPosition = iMax;
        View view = this.mReferenceView;
        if (view == null) {
            return null;
        }
        int i6 = this.mVerticalSpacing;
        if (this.mStackFromBottom) {
            viewFillDown = fillDown(i3 + i4, view.getBottom() + i6);
            adjustViewsUpOrDown();
            viewFillUp = fillUp(iMax - 1, view.getTop() - i6);
            int childCount = getChildCount();
            if (childCount > 0) {
                correctTooLow(i4, i6, childCount);
            }
        } else {
            View viewFillUp2 = fillUp(iMax - i4, view.getTop() - i6);
            adjustViewsUpOrDown();
            View viewFillDown2 = fillDown(iMax + i4, view.getBottom() + i6);
            int childCount2 = getChildCount();
            if (childCount2 > 0) {
                correctTooHigh(i4, i6, childCount2);
            }
            viewFillDown = viewFillDown2;
            viewFillUp = viewFillUp2;
        }
        if (viewMakeRow != null) {
            return viewMakeRow;
        }
        return viewFillUp != null ? viewFillUp : viewFillDown;
    }

    private View fillUp(int i, int i2) {
        View view = null;
        int i3 = (this.mGroupFlags & 34) == 34 ? this.mListPadding.top : 0;
        while (i2 > i3 && i >= 0) {
            View viewMakeRow = makeRow(i, i2, false);
            if (viewMakeRow != null) {
                view = viewMakeRow;
            }
            i2 = this.mReferenceView.getTop() - this.mVerticalSpacing;
            this.mFirstPosition = i;
            i -= this.mNumColumns;
        }
        if (this.mStackFromBottom) {
            this.mFirstPosition = Math.max(0, i + 1);
        }
        setVisibleRangeHint(this.mFirstPosition, (this.mFirstPosition + getChildCount()) - 1);
        return view;
    }

    private int getBottomSelectionPixel(int i, int i2, int i3, int i4) {
        return (i4 + i3) + (-1) < this.mItemCount + (-1) ? i - i2 : i;
    }

    private int getTopSelectionPixel(int i, int i2, int i3) {
        return i3 > 0 ? i + i2 : i;
    }

    private boolean isCandidateSelection(int i, int i2) {
        int iMax;
        int iMin;
        int childCount = getChildCount();
        int i3 = childCount - 1;
        int i4 = i3 - i;
        if (this.mStackFromBottom) {
            int i5 = i3 - (i4 - (i4 % this.mNumColumns));
            iMax = Math.max(0, (i5 - this.mNumColumns) + 1);
            iMin = i5;
        } else {
            iMax = i - (i % this.mNumColumns);
            iMin = Math.min((this.mNumColumns + iMax) - 1, childCount);
        }
        if (i2 == 17) {
            return i == iMin;
        }
        if (i2 == 33) {
            return iMin == i3;
        }
        if (i2 == 66) {
            return i == iMax;
        }
        if (i2 == 130) {
            return iMax == 0;
        }
        switch (i2) {
            case 1:
                return i == iMin && iMin == i3;
            case 2:
                return i == iMax && iMax == 0;
            default:
                throw new IllegalArgumentException("direction must be one of {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT, FOCUS_FORWARD, FOCUS_BACKWARD}.");
        }
    }

    private View makeAndAddView(int i, int i2, boolean z, int i3, boolean z2, int i4) {
        View activeView;
        if (!this.mDataChanged && (activeView = this.mRecycler.getActiveView(i)) != null) {
            setupChild(activeView, i, i2, z, i3, z2, true, i4);
            return activeView;
        }
        View viewObtainView = obtainView(i, this.mIsScrap);
        setupChild(viewObtainView, i, i2, z, i3, z2, this.mIsScrap[0], i4);
        return viewObtainView;
    }

    private View makeRow(int i, int i2, boolean z) {
        int width;
        int iMin;
        int i3;
        int i4 = this.mColumnWidth;
        int i5 = this.mHorizontalSpacing;
        boolean zIsLayoutRtl = isLayoutRtl();
        boolean z2 = false;
        if (zIsLayoutRtl) {
            width = ((getWidth() - this.mListPadding.right) - i4) - (this.mStretchMode == 3 ? i5 : 0);
        } else {
            width = this.mListPadding.left + (this.mStretchMode == 3 ? i5 : 0);
        }
        if (this.mStackFromBottom) {
            iMin = i + 1;
            int iMax = Math.max(0, (i - this.mNumColumns) + 1);
            int i6 = iMin - iMax;
            if (i6 < this.mNumColumns) {
                width += (zIsLayoutRtl ? -1 : 1) * (this.mNumColumns - i6) * (i4 + i5);
            }
            i3 = iMax;
        } else {
            iMin = Math.min(i + this.mNumColumns, this.mItemCount);
            i3 = i;
        }
        int i7 = iMin;
        boolean zShouldShowSelector = shouldShowSelector();
        boolean z3 = touchModeDrawsInPressedState();
        int i8 = this.mSelectedPosition;
        int i9 = zIsLayoutRtl ? -1 : 1;
        View viewMakeAndAddView = null;
        View view = null;
        int i10 = width;
        int i11 = i3;
        while (i11 < i7) {
            boolean z4 = i11 == i8 ? true : z2;
            int i12 = i11;
            int i13 = i8;
            viewMakeAndAddView = makeAndAddView(i11, i2, z, i10, z4, z ? -1 : i11 - i3);
            i10 += i9 * i4;
            if (i12 < i7 - 1) {
                i10 += i9 * i5;
            }
            if (z4 && (zShouldShowSelector || z3)) {
                view = viewMakeAndAddView;
            }
            i11 = i12 + 1;
            i8 = i13;
            z2 = false;
        }
        this.mReferenceView = viewMakeAndAddView;
        if (view != null) {
            this.mReferenceViewInSelectedRow = this.mReferenceView;
        }
        return view;
    }

    private View moveSelection(int i, int i2, int i3) {
        int i4;
        int iMax;
        int i5;
        View viewMakeRow;
        View view;
        int verticalFadingEdgeLength = getVerticalFadingEdgeLength();
        int i6 = this.mSelectedPosition;
        int i7 = this.mNumColumns;
        int i8 = this.mVerticalSpacing;
        if (this.mStackFromBottom) {
            int i9 = (this.mItemCount - 1) - i6;
            i4 = (this.mItemCount - 1) - (i9 - (i9 % i7));
            int iMax2 = Math.max(0, (i4 - i7) + 1);
            int i10 = (this.mItemCount - 1) - (i6 - i);
            iMax = Math.max(0, (((this.mItemCount - 1) - (i10 - (i10 % i7))) - i7) + 1);
            i5 = iMax2;
        } else {
            int i11 = i6 - i;
            iMax = i11 - (i11 % i7);
            i5 = i6 - (i6 % i7);
            i4 = -1;
        }
        int i12 = i5 - iMax;
        int topSelectionPixel = getTopSelectionPixel(i2, verticalFadingEdgeLength, i5);
        int bottomSelectionPixel = getBottomSelectionPixel(i3, verticalFadingEdgeLength, i7, i5);
        this.mFirstPosition = i5;
        if (i12 > 0) {
            viewMakeRow = makeRow(this.mStackFromBottom ? i4 : i5, (this.mReferenceViewInSelectedRow != null ? this.mReferenceViewInSelectedRow.getBottom() : 0) + i8, true);
            view = this.mReferenceView;
            adjustForBottomFadingEdge(view, topSelectionPixel, bottomSelectionPixel);
        } else if (i12 < 0) {
            viewMakeRow = makeRow(this.mStackFromBottom ? i4 : i5, (this.mReferenceViewInSelectedRow == null ? 0 : this.mReferenceViewInSelectedRow.getTop()) - i8, false);
            view = this.mReferenceView;
            adjustForTopFadingEdge(view, topSelectionPixel, bottomSelectionPixel);
        } else {
            viewMakeRow = makeRow(this.mStackFromBottom ? i4 : i5, this.mReferenceViewInSelectedRow != null ? this.mReferenceViewInSelectedRow.getTop() : 0, true);
            view = this.mReferenceView;
        }
        if (this.mStackFromBottom) {
            fillDown(i4 + i7, view.getBottom() + i8);
            adjustViewsUpOrDown();
            fillUp(i5 - 1, view.getTop() - i8);
        } else {
            fillUp(i5 - i7, view.getTop() - i8);
            adjustViewsUpOrDown();
            fillDown(i5 + i7, view.getBottom() + i8);
        }
        return viewMakeRow;
    }

    private void pinToBottom(int i) {
        int bottom;
        int childCount = getChildCount();
        if (this.mFirstPosition + childCount != this.mItemCount || (bottom = i - getChildAt(childCount - 1).getBottom()) <= 0) {
            return;
        }
        offsetChildrenTopAndBottom(bottom);
    }

    private void pinToTop(int i) {
        int top;
        if (this.mFirstPosition != 0 || (top = i - getChildAt(0).getTop()) >= 0) {
            return;
        }
        offsetChildrenTopAndBottom(top);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void setupChild(View view, int i, int i2, boolean z, int i3, boolean z2, boolean z3, int i4) {
        int i5;
        Trace.traceBegin(8L, "setupGridItem");
        boolean z4 = z2 && shouldShowSelector();
        boolean z5 = z4 != view.isSelected();
        int i6 = this.mTouchMode;
        boolean z6 = i6 > 0 && i6 < 3 && this.mMotionPosition == i;
        boolean z7 = z6 != view.isPressed();
        boolean z8 = !z3 || z5 || view.isLayoutRequested();
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = (AbsListView.LayoutParams) generateDefaultLayoutParams();
        }
        AbsListView.LayoutParams layoutParams2 = layoutParams;
        layoutParams2.viewType = this.mAdapter.getItemViewType(i);
        layoutParams2.isEnabled = this.mAdapter.isEnabled(i);
        if (z5) {
            view.setSelected(z4);
            if (z4) {
                requestFocus();
            }
        }
        if (z7) {
            view.setPressed(z6);
        }
        if (this.mChoiceMode != 0 && this.mCheckStates != null) {
            if (view instanceof Checkable) {
                ((Checkable) view).setChecked(this.mCheckStates.get(i));
            } else if (getContext().getApplicationInfo().targetSdkVersion >= 11) {
                view.setActivated(this.mCheckStates.get(i));
            }
        }
        if (!z3 || layoutParams2.forceAdd) {
            layoutParams2.forceAdd = false;
            addViewInLayout(view, i4, layoutParams2, true);
        } else {
            attachViewToParent(view, i4, layoutParams2);
            if (!z3 || ((AbsListView.LayoutParams) view.getLayoutParams()).scrappedFromPosition != i) {
                view.jumpDrawablesToCurrentState();
            }
        }
        if (z8) {
            view.measure(ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(this.mColumnWidth, 1073741824), 0, layoutParams2.width), ViewGroup.getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(0, 0), 0, layoutParams2.height));
        } else {
            cleanupLayoutState(view);
        }
        int measuredWidth = view.getMeasuredWidth();
        int measuredHeight = view.getMeasuredHeight();
        int i7 = z ? i2 : i2 - measuredHeight;
        int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, getLayoutDirection()) & 7;
        if (absoluteGravity != 1) {
            i5 = (absoluteGravity == 3 || absoluteGravity != 5) ? i3 : (i3 + this.mColumnWidth) - measuredWidth;
        } else {
            i5 = i3 + ((this.mColumnWidth - measuredWidth) / 2);
        }
        if (z8) {
            view.layout(i5, i7, measuredWidth + i5, measuredHeight + i7);
        } else {
            view.offsetLeftAndRight(i5 - view.getLeft());
            view.offsetTopAndBottom(i7 - view.getTop());
        }
        if (this.mCachingStarted && !view.isDrawingCacheEnabled()) {
            view.setDrawingCacheEnabled(true);
        }
        Trace.traceEnd(8L);
    }

    /* JADX WARN: Code duplicated, block: B:17:0x0055  */
    boolean arrowScroll(int i) throws Throwable {
        int iMin;
        int iMax;
        boolean z;
        int i2 = this.mSelectedPosition;
        int i3 = this.mNumColumns;
        boolean z2 = true;
        if (this.mStackFromBottom) {
            iMin = (this.mItemCount - 1) - ((((this.mItemCount - 1) - i2) / i3) * i3);
            iMax = Math.max(0, (iMin - i3) + 1);
        } else {
            iMax = (i2 / i3) * i3;
            iMin = Math.min((iMax + i3) - 1, this.mItemCount - 1);
        }
        if (i != 33) {
            if (i == 130 && iMin < this.mItemCount - 1) {
                this.mLayoutMode = 6;
                setSelectionInt(Math.min(i3 + i2, this.mItemCount - 1));
                z = true;
            } else {
                z = false;
            }
        } else if (iMax > 0) {
            this.mLayoutMode = 6;
            setSelectionInt(Math.max(0, i2 - i3));
            z = true;
        } else {
            z = false;
        }
        boolean zIsLayoutRtl = isLayoutRtl();
        if (i2 > iMax && ((i == 17 && !zIsLayoutRtl) || (i == 66 && zIsLayoutRtl))) {
            this.mLayoutMode = 6;
            setSelectionInt(Math.max(0, i2 - 1));
        } else if (i2 >= iMin || (!(i == 17 && zIsLayoutRtl) && (i != 66 || zIsLayoutRtl))) {
            z2 = z;
        } else {
            this.mLayoutMode = 6;
            setSelectionInt(Math.min(i2 + 1, this.mItemCount - 1));
        }
        if (z2) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
            invokeOnItemScrollListener();
        }
        if (z2) {
            awakenScrollBars();
        }
        return z2;
    }

    @Override // android.view.ViewGroup
    protected void attachLayoutAnimationParameters(View view, ViewGroup.LayoutParams layoutParams, int i, int i2) {
        GridLayoutAnimationController.AnimationParameters animationParameters = (GridLayoutAnimationController.AnimationParameters) layoutParams.layoutAnimationParameters;
        if (animationParameters == null) {
            animationParameters = new GridLayoutAnimationController.AnimationParameters();
            layoutParams.layoutAnimationParameters = animationParameters;
        }
        animationParameters.count = i2;
        animationParameters.index = i;
        animationParameters.columnsCount = this.mNumColumns;
        animationParameters.rowsCount = i2 / this.mNumColumns;
        if (!this.mStackFromBottom) {
            animationParameters.column = i % this.mNumColumns;
            animationParameters.row = i / this.mNumColumns;
        } else {
            int i3 = (i2 - 1) - i;
            animationParameters.column = (this.mNumColumns - 1) - (i3 % this.mNumColumns);
            animationParameters.row = (animationParameters.rowsCount - 1) - (i3 / this.mNumColumns);
        }
    }

    @Override // android.widget.AbsListView, android.view.View
    protected int computeVerticalScrollExtent() {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return 0;
        }
        int i = this.mNumColumns;
        int i2 = (((childCount + i) - 1) / i) * 100;
        View childAt = getChildAt(0);
        int top = childAt.getTop();
        int height = childAt.getHeight();
        if (height > 0) {
            i2 += (top * 100) / height;
        }
        View childAt2 = getChildAt(childCount - 1);
        int bottom = childAt2.getBottom();
        int height2 = childAt2.getHeight();
        return height2 > 0 ? i2 - (((bottom - getHeight()) * 100) / height2) : i2;
    }

    @Override // android.widget.AbsListView, android.view.View
    protected int computeVerticalScrollOffset() {
        if (this.mFirstPosition >= 0 && getChildCount() > 0) {
            View childAt = getChildAt(0);
            int top = childAt.getTop();
            int height = childAt.getHeight();
            if (height > 0) {
                int i = this.mNumColumns;
                int i2 = ((this.mItemCount + i) - 1) / i;
                return Math.max(((((this.mFirstPosition + (isStackFromBottom() ? (i2 * i) - this.mItemCount : 0)) / i) * 100) - ((top * 100) / height)) + ((int) ((this.mScrollY / getHeight()) * i2 * 100.0f)), 0);
            }
        }
        return 0;
    }

    @Override // android.widget.AbsListView, android.view.View
    protected int computeVerticalScrollRange() {
        int i = this.mNumColumns;
        int i2 = ((this.mItemCount + i) - 1) / i;
        int iMax = Math.max(i2 * 100, 0);
        return this.mScrollY != 0 ? iMax + Math.abs((int) ((this.mScrollY / getHeight()) * i2 * 100.0f)) : iMax;
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
        super.encodeProperties(viewHierarchyEncoder);
        viewHierarchyEncoder.addProperty("numColumns", getNumColumns());
    }

    @Override // android.widget.AbsListView
    void fillGap(boolean z) {
        int i = this.mNumColumns;
        int i2 = this.mVerticalSpacing;
        int childCount = getChildCount();
        if (!z) {
            int top = childCount > 0 ? getChildAt(0).getTop() - i2 : getHeight() - ((this.mGroupFlags & 34) == 34 ? getListPaddingBottom() : 0);
            int i3 = this.mFirstPosition;
            fillUp(!this.mStackFromBottom ? i3 - i : i3 - 1, top);
            correctTooLow(i, i2, getChildCount());
            return;
        }
        int listPaddingTop = (this.mGroupFlags & 34) == 34 ? getListPaddingTop() : 0;
        if (childCount > 0) {
            listPaddingTop = getChildAt(childCount - 1).getBottom() + i2;
        }
        int i4 = this.mFirstPosition + childCount;
        if (this.mStackFromBottom) {
            i4 += i - 1;
        }
        fillDown(i4, listPaddingTop);
        correctTooHigh(i, i2, getChildCount());
    }

    @Override // android.widget.AbsListView
    int findMotionRow(int i) {
        int childCount = getChildCount();
        if (childCount <= 0) {
            return -1;
        }
        int i2 = this.mNumColumns;
        if (this.mStackFromBottom) {
            for (int i3 = childCount - 1; i3 >= 0; i3 -= i2) {
                if (i >= getChildAt(i3).getTop()) {
                    return this.mFirstPosition + i3;
                }
            }
            return -1;
        }
        for (int i4 = 0; i4 < childCount; i4 += i2) {
            if (i <= getChildAt(i4).getBottom()) {
                return this.mFirstPosition + i4;
            }
        }
        return -1;
    }

    boolean fullScroll(int i) throws Throwable {
        boolean z = true;
        if (i == 33) {
            this.mLayoutMode = 2;
            setSelectionInt(0);
            invokeOnItemScrollListener();
        } else if (i == 130) {
            this.mLayoutMode = 2;
            setSelectionInt(this.mItemCount - 1);
            invokeOnItemScrollListener();
        } else {
            z = false;
        }
        if (z) {
            awakenScrollBars();
        }
        return z;
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return GridView.class.getName();
    }

    @Override // android.widget.AdapterView
    public ListAdapter getAdapter() {
        return this.mAdapter;
    }

    public int getColumnWidth() {
        return this.mColumnWidth;
    }

    public int getGravity() {
        return this.mGravity;
    }

    public int getHorizontalSpacing() {
        return this.mHorizontalSpacing;
    }

    @ViewDebug.ExportedProperty
    public int getNumColumns() {
        return this.mNumColumns;
    }

    public int getRequestedColumnWidth() {
        return this.mRequestedColumnWidth;
    }

    public int getRequestedHorizontalSpacing() {
        return this.mRequestedHorizontalSpacing;
    }

    public int getStretchMode() {
        return this.mStretchMode;
    }

    public int getVerticalSpacing() {
        return this.mVerticalSpacing;
    }

    @Override // android.widget.AbsListView
    protected void layoutChildren() throws Throwable {
        boolean z;
        int i;
        View childAt;
        View childAt2;
        View childAt3;
        int positionForView;
        AccessibilityNodeInfo accessibilityFocusedVirtualView;
        View accessibilityFocusedHost;
        int i2;
        View viewFillFromTop;
        View childAt4;
        View accessibilityFocusedChild;
        boolean z2 = this.mBlockLayoutRequests;
        if (!z2) {
            this.mBlockLayoutRequests = true;
        }
        try {
            super.layoutChildren();
            invalidate();
            if (this.mAdapter == null) {
                resetList();
                invokeOnItemScrollListener();
                if (z2) {
                    return;
                }
                this.mBlockLayoutRequests = false;
                return;
            }
            int top = this.mListPadding.top;
            int i3 = (this.mBottom - this.mTop) - this.mListPadding.bottom;
            int childCount = getChildCount();
            switch (this.mLayoutMode) {
                case 1:
                case 3:
                case 4:
                case 5:
                    i = 0;
                    childAt = null;
                    childAt2 = null;
                    childAt3 = null;
                    break;
                case 2:
                    int i4 = this.mNextSelectedPosition - this.mFirstPosition;
                    if (i4 < 0 || i4 >= childCount) {
                        i = 0;
                        childAt = null;
                    } else {
                        childAt = getChildAt(i4);
                        i = 0;
                    }
                    childAt2 = null;
                    childAt3 = null;
                    break;
                case 6:
                    i = this.mNextSelectedPosition >= 0 ? this.mNextSelectedPosition - this.mSelectedPosition : 0;
                    childAt = null;
                    childAt2 = null;
                    childAt3 = null;
                    break;
                default:
                    int i5 = this.mSelectedPosition - this.mFirstPosition;
                    childAt2 = (i5 < 0 || i5 >= childCount) ? null : getChildAt(i5);
                    childAt3 = getChildAt(0);
                    childAt = null;
                    i = 0;
                    break;
            }
            boolean z3 = this.mDataChanged;
            if (z3) {
                handleDataChanged();
            }
            if (this.mItemCount == 0) {
                resetList();
                invokeOnItemScrollListener();
                if (z2) {
                    return;
                }
                this.mBlockLayoutRequests = false;
                return;
            }
            setSelectedPositionInt(this.mNextSelectedPosition);
            ViewRootImpl viewRootImpl = getViewRootImpl();
            if (viewRootImpl == null || (accessibilityFocusedHost = viewRootImpl.getAccessibilityFocusedHost()) == null || (accessibilityFocusedChild = getAccessibilityFocusedChild(accessibilityFocusedHost)) == null) {
                positionForView = -1;
                accessibilityFocusedVirtualView = null;
                accessibilityFocusedHost = null;
            } else {
                if (!z3 || accessibilityFocusedChild.hasTransientState() || this.mAdapterHasStableIds) {
                    accessibilityFocusedVirtualView = viewRootImpl.getAccessibilityFocusedVirtualView();
                } else {
                    accessibilityFocusedVirtualView = null;
                    accessibilityFocusedHost = null;
                }
                positionForView = getPositionForView(accessibilityFocusedChild);
            }
            int i6 = this.mFirstPosition;
            AbsListView.RecycleBin recycleBin = this.mRecycler;
            try {
                if (z3) {
                    int i7 = 0;
                    while (i7 < childCount) {
                        boolean z4 = z2;
                        recycleBin.addScrapView(getChildAt(i7), i6 + i7);
                        i7++;
                        z2 = z4;
                        positionForView = positionForView;
                    }
                    z = z2;
                    i2 = positionForView;
                } else {
                    z = z2;
                    i2 = positionForView;
                    recycleBin.fillActiveViews(childCount, i6);
                }
                detachAllViewsFromParent();
                recycleBin.removeSkippedScrap();
                switch (this.mLayoutMode) {
                    case 1:
                        this.mFirstPosition = 0;
                        viewFillFromTop = fillFromTop(top);
                        adjustViewsUpOrDown();
                        break;
                    case 2:
                        viewFillFromTop = childAt == null ? fillSelection(top, i3) : fillFromSelection(childAt.getTop(), top, i3);
                        break;
                    case 3:
                        viewFillFromTop = fillUp(this.mItemCount - 1, i3);
                        adjustViewsUpOrDown();
                        break;
                    case 4:
                        viewFillFromTop = fillSpecific(this.mSelectedPosition, this.mSpecificTop);
                        break;
                    case 5:
                        viewFillFromTop = fillSpecific(this.mSyncPosition, this.mSpecificTop);
                        break;
                    case 6:
                        viewFillFromTop = moveSelection(i, top, i3);
                        break;
                    default:
                        if (childCount != 0) {
                            if (this.mSelectedPosition >= 0 && this.mSelectedPosition < this.mItemCount) {
                                int i8 = this.mSelectedPosition;
                                if (childAt2 != null) {
                                    top = childAt2.getTop();
                                }
                                viewFillFromTop = fillSpecific(i8, top);
                            } else if (this.mFirstPosition >= this.mItemCount) {
                                viewFillFromTop = fillSpecific(0, top);
                            } else {
                                int i9 = this.mFirstPosition;
                                if (childAt3 != null) {
                                    top = childAt3.getTop();
                                }
                                viewFillFromTop = fillSpecific(i9, top);
                            }
                        } else if (!this.mStackFromBottom) {
                            setSelectedPositionInt((this.mAdapter == null || isInTouchMode()) ? -1 : 0);
                            viewFillFromTop = fillFromTop(top);
                        } else {
                            int i10 = this.mItemCount - 1;
                            setSelectedPositionInt((this.mAdapter == null || isInTouchMode()) ? -1 : i10);
                            viewFillFromTop = fillFromBottom(i10, i3);
                        }
                        break;
                }
                recycleBin.scrapActiveViews();
                if (viewFillFromTop != null) {
                    positionSelector(-1, viewFillFromTop);
                    this.mSelectedTop = viewFillFromTop.getTop();
                } else if (this.mTouchMode > 0 && this.mTouchMode < 3) {
                    View childAt5 = getChildAt(this.mMotionPosition - this.mFirstPosition);
                    if (childAt5 != null) {
                        positionSelector(this.mMotionPosition, childAt5);
                    }
                } else if (this.mSelectedPosition != -1) {
                    View childAt6 = getChildAt(this.mSelectorPosition - this.mFirstPosition);
                    if (childAt6 != null) {
                        positionSelector(this.mSelectorPosition, childAt6);
                    }
                } else {
                    this.mSelectedTop = 0;
                    this.mSelectorRect.setEmpty();
                }
                if (viewRootImpl != null && viewRootImpl.getAccessibilityFocusedHost() == null) {
                    if (accessibilityFocusedHost == null || !accessibilityFocusedHost.isAttachedToWindow()) {
                        int i11 = i2;
                        if (i11 != -1 && (childAt4 = getChildAt(MathUtils.constrain(i11 - this.mFirstPosition, 0, getChildCount() - 1))) != null) {
                            childAt4.requestAccessibilityFocus();
                        }
                    } else {
                        AccessibilityNodeProvider accessibilityNodeProvider = accessibilityFocusedHost.getAccessibilityNodeProvider();
                        if (accessibilityFocusedVirtualView == null || accessibilityNodeProvider == null) {
                            accessibilityFocusedHost.requestAccessibilityFocus();
                        } else {
                            accessibilityNodeProvider.performAction(AccessibilityNodeInfo.getVirtualDescendantId(accessibilityFocusedVirtualView.getSourceNodeId()), 64, null);
                        }
                    }
                }
                this.mLayoutMode = 0;
                this.mDataChanged = false;
                if (this.mPositionScrollAfterLayout != null) {
                    post(this.mPositionScrollAfterLayout);
                    this.mPositionScrollAfterLayout = null;
                }
                this.mNeedSync = false;
                setNextSelectedPositionInt(this.mSelectedPosition);
                updateScrollIndicators();
                if (this.mItemCount > 0) {
                    checkSelectionChanged();
                }
                invokeOnItemScrollListener();
                if (z) {
                    return;
                }
                this.mBlockLayoutRequests = false;
            } catch (Throwable th) {
                th = th;
                if (!z) {
                    this.mBlockLayoutRequests = false;
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            z = z2;
        }
    }

    @Override // android.widget.AdapterView
    int lookForSelectablePosition(int i, boolean z) {
        if (this.mAdapter == null || isInTouchMode() || i < 0 || i >= this.mItemCount) {
            return -1;
        }
        return i;
    }

    @Override // android.widget.AbsListView, android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        int i2 = -1;
        if (z && rect != null) {
            rect.offset(this.mScrollX, this.mScrollY);
            Rect rect2 = this.mTempRect;
            int i3 = Integer.MAX_VALUE;
            int childCount = getChildCount();
            for (int i4 = 0; i4 < childCount; i4++) {
                if (isCandidateSelection(i4, i)) {
                    View childAt = getChildAt(i4);
                    childAt.getDrawingRect(rect2);
                    offsetDescendantRectToMyCoords(childAt, rect2);
                    int distance = getDistance(rect, rect2, i);
                    if (distance < i3) {
                        i2 = i4;
                        i3 = distance;
                    }
                }
            }
        }
        if (i2 >= 0) {
            setSelection(i2 + this.mFirstPosition);
        } else {
            requestLayout();
        }
    }

    @Override // android.widget.AbsListView
    public void onInitializeAccessibilityNodeInfoForItem(View view, int i, AccessibilityNodeInfo accessibilityNodeInfo) {
        int i2;
        int i3;
        super.onInitializeAccessibilityNodeInfoForItem(view, i, accessibilityNodeInfo);
        int count = getCount();
        int numColumns = getNumColumns();
        int i4 = count / numColumns;
        if (this.mStackFromBottom) {
            int i5 = (count - 1) - i;
            i2 = (i4 - 1) - (i5 / numColumns);
            i3 = (numColumns - 1) - (i5 % numColumns);
        } else {
            i3 = i % numColumns;
            i2 = i / numColumns;
        }
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) view.getLayoutParams();
        accessibilityNodeInfo.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(i2, 1, i3, 1, layoutParams != null && layoutParams.viewType == -2, isItemChecked(i)));
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        int numColumns = getNumColumns();
        int count = getCount() / numColumns;
        accessibilityNodeInfo.setCollectionInfo(AccessibilityNodeInfo.CollectionInfo.obtain(count, numColumns, false, getSelectionModeForAccessibility()));
        if (numColumns > 0 || count > 0) {
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_TO_POSITION);
        }
    }

    @Override // android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        return commonKey(i, 1, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        return commonKey(i, i2, keyEvent);
    }

    @Override // android.widget.AbsListView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        return commonKey(i, 1, keyEvent);
    }

    @Override // android.widget.AbsListView, android.view.View
    protected void onMeasure(int i, int i2) {
        int measuredHeight;
        super.onMeasure(i, i2);
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        if (mode == 0) {
            size = (this.mColumnWidth > 0 ? this.mColumnWidth + this.mListPadding.left + this.mListPadding.right : this.mListPadding.left + this.mListPadding.right) + getVerticalScrollbarWidth();
        }
        boolean zDetermineColumns = determineColumns((size - this.mListPadding.left) - this.mListPadding.right);
        int i3 = 0;
        this.mItemCount = this.mAdapter == null ? 0 : this.mAdapter.getCount();
        int i4 = this.mItemCount;
        if (i4 > 0) {
            View viewObtainView = obtainView(0, this.mIsScrap);
            AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) viewObtainView.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = (AbsListView.LayoutParams) generateDefaultLayoutParams();
                viewObtainView.setLayoutParams(layoutParams);
            }
            layoutParams.viewType = this.mAdapter.getItemViewType(0);
            layoutParams.isEnabled = this.mAdapter.isEnabled(0);
            layoutParams.forceAdd = true;
            viewObtainView.measure(getChildMeasureSpec(View.MeasureSpec.makeMeasureSpec(this.mColumnWidth, 1073741824), 0, layoutParams.width), getChildMeasureSpec(View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(i2), 0), 0, layoutParams.height));
            measuredHeight = viewObtainView.getMeasuredHeight();
            combineMeasuredStates(0, viewObtainView.getMeasuredState());
            if (this.mRecycler.shouldRecycleViewType(layoutParams.viewType)) {
                this.mRecycler.addScrapView(viewObtainView, -1);
            }
        } else {
            measuredHeight = 0;
        }
        if (mode2 == 0) {
            size2 = this.mListPadding.top + this.mListPadding.bottom + measuredHeight + (getVerticalFadingEdgeLength() * 2);
        }
        if (mode2 == Integer.MIN_VALUE) {
            int i5 = this.mListPadding.top + this.mListPadding.bottom;
            int i6 = this.mNumColumns;
            do {
                if (i3 >= i4) {
                    size2 = i5;
                    break;
                }
                i5 += measuredHeight;
                i3 += i6;
                if (i3 < i4) {
                    i5 += this.mVerticalSpacing;
                }
            } while (i5 < size2);
        }
        if (mode == Integer.MIN_VALUE && this.mRequestedNumColumns != -1 && ((this.mRequestedNumColumns * this.mColumnWidth) + ((this.mRequestedNumColumns - 1) * this.mHorizontalSpacing) + this.mListPadding.left + this.mListPadding.right > size || zDetermineColumns)) {
            size |= 16777216;
        }
        setMeasuredDimension(size, size2);
        this.mWidthMeasureSpec = i;
    }

    boolean pageScroll(int i) throws Throwable {
        int iMin;
        if (i == 33) {
            iMin = Math.max(0, this.mSelectedPosition - getChildCount());
        } else {
            iMin = i == 130 ? Math.min(this.mItemCount - 1, this.mSelectedPosition + getChildCount()) : -1;
        }
        if (iMin < 0) {
            return false;
        }
        setSelectionInt(iMin);
        invokeOnItemScrollListener();
        awakenScrollBars();
        return true;
    }

    @Override // android.widget.AbsListView, android.view.View
    public boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        if (super.performAccessibilityActionInternal(i, bundle)) {
            return true;
        }
        if (i != 16908343) {
            return false;
        }
        int numColumns = getNumColumns();
        int i2 = bundle.getInt("android.view.accessibility.action.ARGUMENT_ROW_INT", -1);
        int iMin = Math.min(numColumns * i2, getCount() - 1);
        if (i2 < 0) {
            return false;
        }
        smoothScrollToPosition(iMin);
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x003a  */
    boolean sequenceScroll(int i) throws Throwable {
        int iMin;
        int iMax;
        int i2 = this.mSelectedPosition;
        int i3 = this.mNumColumns;
        int i4 = this.mItemCount;
        boolean z = false;
        boolean z2 = true;
        if (this.mStackFromBottom) {
            int i5 = i4 - 1;
            iMin = i5 - (((i5 - i2) / i3) * i3);
            iMax = Math.max(0, (iMin - i3) + 1);
        } else {
            int i6 = (i2 / i3) * i3;
            iMin = Math.min((i3 + i6) - 1, i4 - 1);
            iMax = i6;
        }
        switch (i) {
            case 1:
                if (i2 <= 0) {
                    z2 = false;
                } else {
                    this.mLayoutMode = 6;
                    setSelectionInt(i2 - 1);
                    if (i2 == iMax) {
                        z = true;
                    }
                }
                break;
            case 2:
                if (i2 >= i4 - 1) {
                    z2 = false;
                } else {
                    this.mLayoutMode = 6;
                    setSelectionInt(i2 + 1);
                    if (i2 == iMin) {
                        z = true;
                    }
                }
                break;
            default:
                z2 = false;
                break;
        }
        if (z2) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(i));
            invokeOnItemScrollListener();
        }
        if (z) {
            awakenScrollBars();
        }
        return z2;
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView
    public void setAdapter(ListAdapter listAdapter) {
        if (this.mAdapter != null && this.mDataSetObserver != null) {
            this.mAdapter.unregisterDataSetObserver(this.mDataSetObserver);
        }
        resetList();
        this.mRecycler.clear();
        this.mAdapter = listAdapter;
        this.mOldSelectedPosition = -1;
        this.mOldSelectedRowId = Long.MIN_VALUE;
        super.setAdapter(listAdapter);
        if (this.mAdapter != null) {
            this.mOldItemCount = this.mItemCount;
            this.mItemCount = this.mAdapter.getCount();
            this.mDataChanged = true;
            checkFocus();
            this.mDataSetObserver = new AbsListView.AdapterDataSetObserver();
            this.mAdapter.registerDataSetObserver(this.mDataSetObserver);
            this.mRecycler.setViewTypeCount(this.mAdapter.getViewTypeCount());
            int iLookForSelectablePosition = this.mStackFromBottom ? lookForSelectablePosition(this.mItemCount - 1, false) : lookForSelectablePosition(0, true);
            setSelectedPositionInt(iLookForSelectablePosition);
            setNextSelectedPositionInt(iLookForSelectablePosition);
            checkSelectionChanged();
        } else {
            checkFocus();
            checkSelectionChanged();
        }
        requestLayout();
    }

    public void setColumnWidth(int i) {
        if (i != this.mRequestedColumnWidth) {
            this.mRequestedColumnWidth = i;
            requestLayoutIfNecessary();
        }
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            this.mGravity = i;
            requestLayoutIfNecessary();
        }
    }

    public void setHorizontalSpacing(int i) {
        if (i != this.mRequestedHorizontalSpacing) {
            this.mRequestedHorizontalSpacing = i;
            requestLayoutIfNecessary();
        }
    }

    public void setNumColumns(int i) {
        if (i != this.mRequestedNumColumns) {
            this.mRequestedNumColumns = i;
            requestLayoutIfNecessary();
        }
    }

    @Override // android.widget.AbsListView
    @RemotableViewMethod(asyncImpl = "setRemoteViewsAdapterAsync")
    public void setRemoteViewsAdapter(Intent intent) {
        super.setRemoteViewsAdapter(intent);
    }

    @Override // android.widget.AdapterView
    public void setSelection(int i) {
        if (isInTouchMode()) {
            this.mResurrectToPosition = i;
        } else {
            setNextSelectedPositionInt(i);
        }
        this.mLayoutMode = 2;
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        requestLayout();
    }

    @Override // android.widget.AbsListView
    void setSelectionInt(int i) throws Throwable {
        int i2 = this.mNextSelectedPosition;
        if (this.mPositionScroller != null) {
            this.mPositionScroller.stop();
        }
        setNextSelectedPositionInt(i);
        layoutChildren();
        int i3 = this.mStackFromBottom ? (this.mItemCount - 1) - this.mNextSelectedPosition : this.mNextSelectedPosition;
        if (this.mStackFromBottom) {
            i2 = (this.mItemCount - 1) - i2;
        }
        if (i3 / this.mNumColumns != i2 / this.mNumColumns) {
            awakenScrollBars();
        }
    }

    public void setStretchMode(int i) {
        if (i != this.mStretchMode) {
            this.mStretchMode = i;
            requestLayoutIfNecessary();
        }
    }

    public void setVerticalSpacing(int i) {
        if (i != this.mVerticalSpacing) {
            this.mVerticalSpacing = i;
            requestLayoutIfNecessary();
        }
    }

    @Override // android.widget.AbsListView
    @RemotableViewMethod
    public void smoothScrollByOffset(int i) {
        super.smoothScrollByOffset(i);
    }

    @Override // android.widget.AbsListView
    @RemotableViewMethod
    public void smoothScrollToPosition(int i) {
        super.smoothScrollToPosition(i);
    }
}
