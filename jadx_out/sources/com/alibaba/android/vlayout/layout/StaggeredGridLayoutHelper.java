package com.alibaba.android.vlayout.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.OrientationHelperEx;
import com.alibaba.android.vlayout.Range;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class StaggeredGridLayoutHelper extends BaseLayoutHelper {
    static final int INVALID_LINE = Integer.MIN_VALUE;
    private static final int INVALID_SPAN_ID = Integer.MIN_VALUE;
    private static final String LOOKUP_BUNDLE_KEY = "StaggeredGridLayoutHelper_LazySpanLookup";
    private static final String TAG = "Staggered";
    private int anchorPosition;
    private final Runnable checkForGapsRunnable;
    private int mColLength;
    private int mEachGap;
    private int mHGap;
    private int mLastGap;
    private WeakReference<VirtualLayoutManager> mLayoutManager;
    private boolean mLayoutWithAnchor;
    private LazySpanLookup mLazySpanLookup;
    private int mNumLanes;
    private BitSet mRemainingSpans;
    private Span[] mSpans;
    private int mVGap;
    private List<View> prelayoutViewList;

    static class LazySpanLookup {
        private static final int MIN_SIZE = 10;
        int[] mData;

        LazySpanLookup() {
        }

        void clear() {
            if (this.mData != null) {
                Arrays.fill(this.mData, Integer.MIN_VALUE);
            }
        }

        void ensureSize(int i) {
            if (this.mData == null) {
                this.mData = new int[Math.max(i, 10) + 1];
                Arrays.fill(this.mData, Integer.MIN_VALUE);
            } else if (i >= this.mData.length) {
                int[] iArr = this.mData;
                this.mData = new int[sizeForPosition(i)];
                System.arraycopy(iArr, 0, this.mData, 0, iArr.length);
                Arrays.fill(this.mData, iArr.length, this.mData.length, Integer.MIN_VALUE);
            }
        }

        int getSpan(int i) {
            if (this.mData == null || i >= this.mData.length) {
                return Integer.MIN_VALUE;
            }
            return this.mData[i];
        }

        int invalidateAfter(int i) {
            if (this.mData == null || i >= this.mData.length) {
                return -1;
            }
            Arrays.fill(this.mData, i, this.mData.length, Integer.MIN_VALUE);
            return this.mData.length;
        }

        void offsetForAddition(int i, int i2) {
            if (this.mData == null || i >= this.mData.length) {
                return;
            }
            int i3 = i + i2;
            ensureSize(i3);
            System.arraycopy(this.mData, i, this.mData, i3, (this.mData.length - i) - i2);
            Arrays.fill(this.mData, i, i3, Integer.MIN_VALUE);
        }

        void offsetForRemoval(int i, int i2) {
            if (this.mData == null || i >= this.mData.length) {
                return;
            }
            int i3 = i + i2;
            ensureSize(i3);
            System.arraycopy(this.mData, i3, this.mData, i, (this.mData.length - i) - i2);
            Arrays.fill(this.mData, this.mData.length - i2, this.mData.length, Integer.MIN_VALUE);
        }

        void setSpan(int i, Span span) {
            ensureSize(i);
            this.mData[i] = span.mIndex;
        }

        int sizeForPosition(int i) {
            int length = this.mData.length;
            while (length <= i) {
                length *= 2;
            }
            return length;
        }
    }

    static class Span {
        static final int INVALID_OFFSET = Integer.MIN_VALUE;
        int mCachedEnd;
        int mCachedStart;
        int mDeletedSize;
        final int mIndex;
        int mLastEdgeEnd;
        int mLastEdgeStart;
        private ArrayList<View> mViews;

        private Span(int i) {
            this.mViews = new ArrayList<>();
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
            this.mDeletedSize = 0;
            this.mLastEdgeStart = Integer.MIN_VALUE;
            this.mLastEdgeEnd = Integer.MIN_VALUE;
            this.mIndex = i;
        }

        void appendToSpan(View view, OrientationHelperEx orientationHelperEx) {
            RecyclerView.LayoutParams layoutParams = getLayoutParams(view);
            this.mViews.add(view);
            this.mCachedEnd = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize += orientationHelperEx.getDecoratedMeasurement(view);
            }
        }

        void cacheReferenceLineAndClear(boolean z, int i, OrientationHelperEx orientationHelperEx) {
            int endLine = z ? getEndLine(orientationHelperEx) : getStartLine(orientationHelperEx);
            clear();
            if (endLine == Integer.MIN_VALUE) {
                return;
            }
            if ((!z || endLine >= orientationHelperEx.getEndAfterPadding()) && !z) {
                orientationHelperEx.getStartAfterPadding();
            }
            if (i != Integer.MIN_VALUE) {
                endLine += i;
            }
            this.mCachedEnd = endLine;
            this.mCachedStart = endLine;
            this.mLastEdgeEnd = Integer.MIN_VALUE;
            this.mLastEdgeStart = Integer.MIN_VALUE;
        }

        void calculateCachedEnd(OrientationHelperEx orientationHelperEx) {
            if (this.mViews.size() == 0) {
                this.mCachedEnd = Integer.MIN_VALUE;
            } else {
                this.mCachedEnd = orientationHelperEx.getDecoratedEnd(this.mViews.get(this.mViews.size() - 1));
            }
        }

        void calculateCachedStart(@NonNull OrientationHelperEx orientationHelperEx) {
            if (this.mViews.size() == 0) {
                this.mCachedStart = Integer.MIN_VALUE;
            } else {
                this.mCachedStart = orientationHelperEx.getDecoratedStart(this.mViews.get(0));
            }
        }

        void clear() {
            Log.d("Longer", "clear span");
            this.mViews.clear();
            invalidateCache();
            this.mDeletedSize = 0;
        }

        boolean findEnd(View view) {
            int size = this.mViews.size();
            return size > 0 && this.mViews.get(size - 1) == view;
        }

        boolean findStart(View view) {
            return this.mViews.size() > 0 && this.mViews.get(0) == view;
        }

        public int getDeletedSize() {
            return this.mDeletedSize;
        }

        int getEndLine(int i, OrientationHelperEx orientationHelperEx) {
            if (this.mCachedEnd != Integer.MIN_VALUE) {
                return this.mCachedEnd;
            }
            if (i != Integer.MIN_VALUE && this.mViews.size() == 0) {
                return this.mLastEdgeStart != Integer.MIN_VALUE ? this.mLastEdgeStart : i;
            }
            calculateCachedEnd(orientationHelperEx);
            return this.mCachedEnd;
        }

        int getEndLine(OrientationHelperEx orientationHelperEx) {
            return getEndLine(Integer.MIN_VALUE, orientationHelperEx);
        }

        RecyclerView.LayoutParams getLayoutParams(View view) {
            return (RecyclerView.LayoutParams) view.getLayoutParams();
        }

        int getNormalizedOffset(int i, int i2, int i3, OrientationHelperEx orientationHelperEx) {
            if (this.mViews.size() == 0) {
                return 0;
            }
            if (i < 0) {
                int endLine = getEndLine(0, orientationHelperEx) - i3;
                if (endLine <= 0) {
                    return 0;
                }
                return (-i) > endLine ? -endLine : i;
            }
            int startLine = i2 - getStartLine(0, orientationHelperEx);
            if (startLine <= 0) {
                return 0;
            }
            return startLine < i ? startLine : i;
        }

        int getStartLine(int i, OrientationHelperEx orientationHelperEx) {
            if (this.mCachedStart != Integer.MIN_VALUE) {
                return this.mCachedStart;
            }
            if (i != Integer.MIN_VALUE && this.mViews.size() == 0) {
                return this.mLastEdgeEnd != Integer.MIN_VALUE ? this.mLastEdgeEnd : i;
            }
            calculateCachedStart(orientationHelperEx);
            return this.mCachedStart;
        }

        int getStartLine(OrientationHelperEx orientationHelperEx) {
            return getStartLine(Integer.MIN_VALUE, orientationHelperEx);
        }

        void invalidateCache() {
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
            this.mLastEdgeEnd = Integer.MIN_VALUE;
            this.mLastEdgeStart = Integer.MIN_VALUE;
        }

        boolean isEmpty(int i, int i2, OrientationHelperEx orientationHelperEx) {
            int size = this.mViews.size();
            for (int i3 = 0; i3 < size; i3++) {
                View view = this.mViews.get(i3);
                if (orientationHelperEx.getDecoratedStart(view) < i2 && orientationHelperEx.getDecoratedEnd(view) > i) {
                    return false;
                }
            }
            return true;
        }

        void onOffset(int i) {
            if (this.mLastEdgeStart != Integer.MIN_VALUE) {
                this.mLastEdgeStart += i;
            }
            if (this.mCachedStart != Integer.MIN_VALUE) {
                this.mCachedStart += i;
            }
            if (this.mLastEdgeEnd != Integer.MIN_VALUE) {
                this.mLastEdgeEnd += i;
            }
            if (this.mCachedEnd != Integer.MIN_VALUE) {
                this.mCachedEnd += i;
            }
        }

        void popEnd(OrientationHelperEx orientationHelperEx) {
            int size = this.mViews.size();
            View viewRemove = this.mViews.remove(size - 1);
            RecyclerView.LayoutParams layoutParams = getLayoutParams(viewRemove);
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize -= orientationHelperEx.getDecoratedMeasurement(viewRemove);
            }
            if (size == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            this.mCachedEnd = Integer.MIN_VALUE;
        }

        void popStart(OrientationHelperEx orientationHelperEx) {
            View viewRemove = this.mViews.remove(0);
            RecyclerView.LayoutParams layoutParams = getLayoutParams(viewRemove);
            if (this.mViews.size() == 0) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize -= orientationHelperEx.getDecoratedMeasurement(viewRemove);
            }
            this.mCachedStart = Integer.MIN_VALUE;
        }

        void prependToSpan(View view, OrientationHelperEx orientationHelperEx) {
            RecyclerView.LayoutParams layoutParams = getLayoutParams(view);
            this.mViews.add(0, view);
            this.mCachedStart = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize += orientationHelperEx.getDecoratedMeasurement(view);
            }
        }

        void setLine(int i) {
            this.mCachedStart = i;
            this.mCachedEnd = i;
            this.mLastEdgeEnd = Integer.MIN_VALUE;
            this.mLastEdgeStart = Integer.MIN_VALUE;
        }
    }

    public StaggeredGridLayoutHelper() {
        this(1, 0);
    }

    public StaggeredGridLayoutHelper(int i) {
        this(i, 0);
    }

    public StaggeredGridLayoutHelper(int i, int i2) {
        this.mNumLanes = 0;
        this.mHGap = 0;
        this.mVGap = 0;
        this.mColLength = 0;
        this.mEachGap = 0;
        this.mLastGap = 0;
        this.mRemainingSpans = null;
        this.mLazySpanLookup = new LazySpanLookup();
        this.prelayoutViewList = new ArrayList();
        this.mLayoutManager = null;
        this.checkForGapsRunnable = new Runnable() { // from class: com.alibaba.android.vlayout.layout.StaggeredGridLayoutHelper.1
            @Override // java.lang.Runnable
            public void run() {
                StaggeredGridLayoutHelper.this.checkForGaps();
            }
        };
        setLane(i);
        setGap(i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkForGaps() {
        VirtualLayoutManager virtualLayoutManager;
        int iIntValue;
        int position;
        int decoratedStart;
        if (this.mLayoutManager == null || (virtualLayoutManager = this.mLayoutManager.get()) == null || virtualLayoutManager.getChildCount() == 0) {
            return;
        }
        Range<Integer> range = getRange();
        if (virtualLayoutManager.getReverseLayout()) {
            virtualLayoutManager.findLastVisibleItemPosition();
            virtualLayoutManager.findFirstVisibleItemPosition();
            iIntValue = ((Integer) range.getUpper()).intValue() - 1;
        } else {
            virtualLayoutManager.findFirstVisibleItemPosition();
            virtualLayoutManager.findLastCompletelyVisibleItemPosition();
            iIntValue = ((Integer) range.getLower()).intValue();
        }
        OrientationHelperEx mainOrientationHelper = virtualLayoutManager.getMainOrientationHelper();
        int childCount = virtualLayoutManager.getChildCount();
        if (virtualLayoutManager.getReverseLayout()) {
            int i = childCount - 1;
            int i2 = i;
            while (true) {
                if (i2 < 0) {
                    decoratedStart = Integer.MIN_VALUE;
                    position = Integer.MIN_VALUE;
                    break;
                }
                View childAt = virtualLayoutManager.getChildAt(i2);
                position = virtualLayoutManager.getPosition(childAt);
                if (position == iIntValue) {
                    if (i2 == i) {
                        decoratedStart = mainOrientationHelper.getDecoratedEnd(childAt);
                        break;
                    } else {
                        View childAt2 = virtualLayoutManager.getChildAt(i2 + 1);
                        decoratedStart = virtualLayoutManager.getPosition(childAt2) == position + (-1) ? (mainOrientationHelper.getDecoratedStart(childAt2) - virtualLayoutManager.obtainExtraMargin(childAt2, false)) + virtualLayoutManager.obtainExtraMargin(childAt, true) : mainOrientationHelper.getDecoratedEnd(childAt);
                        break;
                    }
                }
                i2--;
            }
        } else {
            int i3 = 0;
            while (true) {
                if (i3 >= childCount) {
                    decoratedStart = Integer.MIN_VALUE;
                    position = Integer.MIN_VALUE;
                    break;
                }
                View childAt3 = virtualLayoutManager.getChildAt(i3);
                position = virtualLayoutManager.getPosition(childAt3);
                if (position == iIntValue) {
                    if (i3 != 0) {
                        View childAt4 = virtualLayoutManager.getChildAt(i3 - 1);
                        int decoratedEnd = (mainOrientationHelper.getDecoratedEnd(childAt4) + virtualLayoutManager.obtainExtraMargin(childAt4, true, false)) - virtualLayoutManager.obtainExtraMargin(childAt3, false, false);
                        if (decoratedEnd == mainOrientationHelper.getDecoratedStart(childAt3)) {
                            position = Integer.MIN_VALUE;
                        } else {
                            int position2 = virtualLayoutManager.getPosition(childAt4);
                            int i4 = iIntValue - 1;
                            if (position2 != i4) {
                                LayoutHelper layoutHelperFindLayoutHelperByPosition = virtualLayoutManager.findLayoutHelperByPosition(i4);
                                if (layoutHelperFindLayoutHelperByPosition != null && (layoutHelperFindLayoutHelperByPosition instanceof StickyLayoutHelper) && layoutHelperFindLayoutHelperByPosition.getFixedView() != null) {
                                    decoratedEnd += layoutHelperFindLayoutHelperByPosition.getFixedView().getMeasuredHeight();
                                }
                            } else {
                                virtualLayoutManager.findLayoutHelperByPosition(position2).getRange();
                            }
                        }
                        decoratedStart = decoratedEnd;
                        break;
                    }
                    decoratedStart = mainOrientationHelper.getDecoratedStart(childAt3);
                    break;
                }
                i3++;
            }
        }
        if (position == Integer.MIN_VALUE || hasGapsToFix(virtualLayoutManager, position, decoratedStart) == null) {
            return;
        }
        for (Span span : this.mSpans) {
            span.setLine(decoratedStart);
        }
        virtualLayoutManager.requestSimpleAnimationsInNextLayout();
        virtualLayoutManager.requestLayout();
    }

    private boolean checkSpanForGap(Span span, VirtualLayoutManager virtualLayoutManager, int i) {
        OrientationHelperEx mainOrientationHelper = virtualLayoutManager.getMainOrientationHelper();
        if (virtualLayoutManager.getReverseLayout()) {
            return span.getEndLine(mainOrientationHelper) < i;
        }
        return span.getStartLine(mainOrientationHelper) > i;
    }

    private void ensureLanes() {
        if (this.mSpans == null || this.mSpans.length != this.mNumLanes || this.mRemainingSpans == null) {
            this.mRemainingSpans = new BitSet(this.mNumLanes);
            this.mSpans = new Span[this.mNumLanes];
            for (int i = 0; i < this.mNumLanes; i++) {
                this.mSpans[i] = new Span(i);
            }
        }
    }

    private Span findSpan(int i, View view, boolean z) {
        int span = this.mLazySpanLookup.getSpan(i);
        if (span >= 0 && span < this.mSpans.length) {
            Span span2 = this.mSpans[span];
            if (z && span2.findStart(view)) {
                return span2;
            }
            if (!z && span2.findEnd(view)) {
                return span2;
            }
        }
        for (int i2 = 0; i2 < this.mSpans.length; i2++) {
            if (i2 != span) {
                Span span3 = this.mSpans[i2];
                if (z && span3.findStart(view)) {
                    return span3;
                }
                if (!z && span3.findEnd(view)) {
                    return span3;
                }
            }
        }
        return null;
    }

    private int getMaxEnd(int i, OrientationHelperEx orientationHelperEx) {
        int endLine = this.mSpans[0].getEndLine(i, orientationHelperEx);
        for (int i2 = 1; i2 < this.mNumLanes; i2++) {
            int endLine2 = this.mSpans[i2].getEndLine(i, orientationHelperEx);
            if (endLine2 > endLine) {
                endLine = endLine2;
            }
        }
        return endLine;
    }

    private int getMaxStart(int i, OrientationHelperEx orientationHelperEx) {
        int startLine = this.mSpans[0].getStartLine(i, orientationHelperEx);
        for (int i2 = 1; i2 < this.mNumLanes; i2++) {
            int startLine2 = this.mSpans[i2].getStartLine(i, orientationHelperEx);
            if (startLine2 > startLine) {
                startLine = startLine2;
            }
        }
        return startLine;
    }

    private int getMinEnd(int i, OrientationHelperEx orientationHelperEx) {
        int endLine = this.mSpans[0].getEndLine(i, orientationHelperEx);
        for (int i2 = 1; i2 < this.mNumLanes; i2++) {
            int endLine2 = this.mSpans[i2].getEndLine(i, orientationHelperEx);
            if (endLine2 < endLine) {
                endLine = endLine2;
            }
        }
        return endLine;
    }

    private int getMinStart(int i, OrientationHelperEx orientationHelperEx) {
        int startLine = this.mSpans[0].getStartLine(i, orientationHelperEx);
        for (int i2 = 1; i2 < this.mNumLanes; i2++) {
            int startLine2 = this.mSpans[i2].getStartLine(i, orientationHelperEx);
            if (startLine2 < startLine) {
                startLine = startLine2;
            }
        }
        return startLine;
    }

    /* JADX WARN: Code duplicated, block: B:10:0x001c  */
    /* JADX WARN: Code duplicated, block: B:11:0x001e  */
    private Span getNextSpan(int i, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutManagerHelper layoutManagerHelper) {
        boolean z;
        int i2;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        int i3 = -1;
        int i4 = 0;
        if (layoutManagerHelper.getOrientation() == 0) {
            if ((layoutStateWrapper.getLayoutDirection() == -1) != layoutManagerHelper.getReverseLayout()) {
                z = true;
            } else {
                z = false;
            }
        } else {
            if (((layoutStateWrapper.getLayoutDirection() == -1) == layoutManagerHelper.getReverseLayout()) == layoutManagerHelper.isDoLayoutRTL()) {
                z = true;
            } else {
                z = false;
            }
        }
        if (z) {
            i4 = this.mNumLanes - 1;
            i2 = -1;
        } else {
            i3 = this.mNumLanes;
            i2 = 1;
        }
        Span span = null;
        if (layoutStateWrapper.getLayoutDirection() == 1) {
            int i5 = Integer.MAX_VALUE;
            while (i4 != i3) {
                Span span2 = this.mSpans[i4];
                int endLine = span2.getEndLine(i, mainOrientationHelper);
                if (endLine < i5) {
                    span = span2;
                    i5 = endLine;
                }
                i4 += i2;
            }
            return span;
        }
        int i6 = Integer.MIN_VALUE;
        while (i4 != i3) {
            Span span3 = this.mSpans[i4];
            int startLine = span3.getStartLine(i, mainOrientationHelper);
            if (startLine > i6) {
                span = span3;
                i6 = startLine;
            }
            i4 += i2;
        }
        return span;
    }

    private View hasGapsToFix(VirtualLayoutManager virtualLayoutManager, int i, int i2) {
        if (virtualLayoutManager.findViewByPosition(i) == null) {
            return null;
        }
        new BitSet(this.mNumLanes).set(0, this.mNumLanes, true);
        for (Span span : this.mSpans) {
            if (span.mViews.size() != 0 && checkSpanForGap(span, virtualLayoutManager, i2)) {
                return (View) (virtualLayoutManager.getReverseLayout() ? span.mViews.get(span.mViews.size() - 1) : span.mViews.get(0));
            }
        }
        return null;
    }

    private void recycle(RecyclerView.Recycler recycler, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, Span span, int i, LayoutManagerHelper layoutManagerHelper) {
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        if (layoutStateWrapper.getLayoutDirection() == -1) {
            recycleFromEnd(recycler, Math.max(i, getMaxStart(span.getStartLine(mainOrientationHelper), mainOrientationHelper)) + (mainOrientationHelper.getEnd() - mainOrientationHelper.getStartAfterPadding()), layoutManagerHelper);
        } else {
            recycleFromStart(recycler, Math.min(i, getMinEnd(span.getEndLine(mainOrientationHelper), mainOrientationHelper)) - (mainOrientationHelper.getEnd() - mainOrientationHelper.getStartAfterPadding()), layoutManagerHelper);
        }
    }

    private void recycleForPreLayout(RecyclerView.Recycler recycler, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutManagerHelper layoutManagerHelper) {
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        for (int size = this.prelayoutViewList.size() - 1; size >= 0; size--) {
            View view = this.prelayoutViewList.get(size);
            if (view == null || mainOrientationHelper.getDecoratedStart(view) <= mainOrientationHelper.getEndAfterPadding()) {
                Span spanFindSpan = findSpan(((RecyclerView.LayoutParams) view.getLayoutParams()).getViewPosition(), view, false);
                if (spanFindSpan != null) {
                    spanFindSpan.popEnd(mainOrientationHelper);
                    layoutManagerHelper.removeChildView(view);
                    recycler.recycleView(view);
                    return;
                }
                return;
            }
            Span spanFindSpan2 = findSpan(((RecyclerView.LayoutParams) view.getLayoutParams()).getViewPosition(), view, false);
            if (spanFindSpan2 != null) {
                spanFindSpan2.popEnd(mainOrientationHelper);
                layoutManagerHelper.removeChildView(view);
                recycler.recycleView(view);
            }
        }
    }

    private void recycleFromEnd(RecyclerView.Recycler recycler, int i, LayoutManagerHelper layoutManagerHelper) {
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        for (int childCount = layoutManagerHelper.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = layoutManagerHelper.getChildAt(childCount);
            if (childAt == null || mainOrientationHelper.getDecoratedStart(childAt) <= i) {
                return;
            }
            Span spanFindSpan = findSpan(((RecyclerView.LayoutParams) childAt.getLayoutParams()).getViewPosition(), childAt, false);
            if (spanFindSpan != null) {
                spanFindSpan.popEnd(mainOrientationHelper);
                layoutManagerHelper.removeChildView(childAt);
                recycler.recycleView(childAt);
            }
        }
    }

    private void recycleFromStart(RecyclerView.Recycler recycler, int i, LayoutManagerHelper layoutManagerHelper) {
        View childAt;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        boolean z = true;
        while (layoutManagerHelper.getChildCount() > 0 && z && (childAt = layoutManagerHelper.getChildAt(0)) != null && mainOrientationHelper.getDecoratedEnd(childAt) < i) {
            Span spanFindSpan = findSpan(((RecyclerView.LayoutParams) childAt.getLayoutParams()).getViewPosition(), childAt, true);
            if (spanFindSpan != null) {
                spanFindSpan.popStart(mainOrientationHelper);
                layoutManagerHelper.removeChildView(childAt);
                recycler.recycleView(childAt);
            } else {
                z = false;
            }
        }
    }

    private void updateAllRemainingSpans(int i, int i2, OrientationHelperEx orientationHelperEx) {
        for (int i3 = 0; i3 < this.mNumLanes; i3++) {
            if (!this.mSpans[i3].mViews.isEmpty()) {
                updateRemainingSpans(this.mSpans[i3], i, i2, orientationHelperEx);
            }
        }
    }

    private void updateRemainingSpans(Span span, int i, int i2, OrientationHelperEx orientationHelperEx) {
        int deletedSize = span.getDeletedSize();
        if (i == -1) {
            if (span.getStartLine(orientationHelperEx) + deletedSize < i2) {
                this.mRemainingSpans.set(span.mIndex, false);
            }
        } else if (span.getEndLine(orientationHelperEx) - deletedSize > i2) {
            this.mRemainingSpans.set(span.mIndex, false);
        }
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void afterLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper) {
        super.afterLayout(recycler, state, i, i2, i3, layoutManagerHelper);
        this.mLayoutWithAnchor = false;
        if (i > ((Integer) getRange().getUpper()).intValue() || i2 < ((Integer) getRange().getLower()).intValue() || state.isPreLayout() || layoutManagerHelper.getChildCount() <= 0) {
            return;
        }
        ViewCompat.postOnAnimation(layoutManagerHelper.getChildAt(0), this.checkForGapsRunnable);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void beforeLayout(RecyclerView.Recycler recycler, RecyclerView.State state, LayoutManagerHelper layoutManagerHelper) {
        super.beforeLayout(recycler, state, layoutManagerHelper);
        int contentWidth = layoutManagerHelper.getOrientation() == 1 ? (((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingLeft()) - layoutManagerHelper.getPaddingRight()) - getHorizontalMargin()) - getHorizontalPadding() : (((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom()) - getVerticalMargin()) - getVerticalPadding();
        this.mColLength = (int) (((double) ((contentWidth - (this.mHGap * (this.mNumLanes - 1))) / this.mNumLanes)) + 0.5d);
        int i = contentWidth - (this.mColLength * this.mNumLanes);
        if (this.mNumLanes <= 1) {
            this.mLastGap = 0;
            this.mEachGap = 0;
        } else if (this.mNumLanes == 2) {
            this.mEachGap = i;
            this.mLastGap = i;
        } else {
            int i2 = layoutManagerHelper.getOrientation() == 1 ? this.mHGap : this.mVGap;
            this.mLastGap = i2;
            this.mEachGap = i2;
        }
        if ((this.mLayoutManager == null || this.mLayoutManager.get() == null || this.mLayoutManager.get() != layoutManagerHelper) && (layoutManagerHelper instanceof VirtualLayoutManager)) {
            this.mLayoutManager = new WeakReference<>((VirtualLayoutManager) layoutManagerHelper);
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void checkAnchorInfo(RecyclerView.State state, VirtualLayoutManager.AnchorInfoWrapper anchorInfoWrapper, LayoutManagerHelper layoutManagerHelper) {
        int i;
        super.checkAnchorInfo(state, anchorInfoWrapper, layoutManagerHelper);
        ensureLanes();
        Range<Integer> range = getRange();
        if (anchorInfoWrapper.layoutFromEnd) {
            if (anchorInfoWrapper.position < (((Integer) range.getLower()).intValue() + this.mNumLanes) - 1) {
                anchorInfoWrapper.position = Math.min((((Integer) range.getLower()).intValue() + this.mNumLanes) - 1, ((Integer) range.getUpper()).intValue());
            }
        } else if (anchorInfoWrapper.position > ((Integer) range.getUpper()).intValue() - (this.mNumLanes - 1)) {
            anchorInfoWrapper.position = Math.max(((Integer) range.getLower()).intValue(), ((Integer) range.getUpper()).intValue() - (this.mNumLanes - 1));
        }
        View viewFindViewByPosition = layoutManagerHelper.findViewByPosition(anchorInfoWrapper.position);
        int i2 = 0;
        int i3 = layoutManagerHelper.getOrientation() == 1 ? this.mVGap : this.mHGap;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        if (viewFindViewByPosition == null) {
            Span[] spanArr = this.mSpans;
            int length = spanArr.length;
            while (i2 < length) {
                Span span = spanArr[i2];
                span.clear();
                span.setLine(anchorInfoWrapper.coordinate);
                i2++;
            }
            return;
        }
        int i4 = Integer.MIN_VALUE;
        int i5 = anchorInfoWrapper.layoutFromEnd ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int iMax = i5;
        for (Span span2 : this.mSpans) {
            if (!span2.mViews.isEmpty()) {
                iMax = anchorInfoWrapper.layoutFromEnd ? Math.max(iMax, layoutManagerHelper.getPosition((View) span2.mViews.get(span2.mViews.size() - 1))) : Math.min(iMax, layoutManagerHelper.getPosition((View) span2.mViews.get(0)));
            }
        }
        if (isOutOfRange(iMax)) {
            this.anchorPosition = anchorInfoWrapper.position;
            this.mLayoutWithAnchor = true;
        } else {
            boolean z = iMax == ((Integer) range.getLower()).intValue();
            View viewFindViewByPosition2 = layoutManagerHelper.findViewByPosition(iMax);
            if (viewFindViewByPosition2 != null) {
                if (anchorInfoWrapper.layoutFromEnd) {
                    anchorInfoWrapper.position = iMax;
                    int decoratedEnd = mainOrientationHelper.getDecoratedEnd(viewFindViewByPosition);
                    if (decoratedEnd < anchorInfoWrapper.coordinate) {
                        int i6 = anchorInfoWrapper.coordinate - decoratedEnd;
                        if (z) {
                            i3 = 0;
                        }
                        i = i6 + i3;
                        anchorInfoWrapper.coordinate = mainOrientationHelper.getDecoratedEnd(viewFindViewByPosition2) + i;
                    } else {
                        i = z ? 0 : i3;
                        anchorInfoWrapper.coordinate = mainOrientationHelper.getDecoratedEnd(viewFindViewByPosition2) + i;
                    }
                } else {
                    anchorInfoWrapper.position = iMax;
                    int decoratedStart = mainOrientationHelper.getDecoratedStart(viewFindViewByPosition);
                    if (decoratedStart > anchorInfoWrapper.coordinate) {
                        int i7 = anchorInfoWrapper.coordinate - decoratedStart;
                        if (z) {
                            i3 = 0;
                        }
                        i = i7 - i3;
                        anchorInfoWrapper.coordinate = mainOrientationHelper.getDecoratedStart(viewFindViewByPosition2) + i;
                    } else {
                        if (z) {
                            i3 = 0;
                        }
                        int i8 = -i3;
                        anchorInfoWrapper.coordinate = mainOrientationHelper.getDecoratedStart(viewFindViewByPosition2) + i8;
                        i4 = i8;
                    }
                }
                i4 = i;
            }
        }
        Span[] spanArr2 = this.mSpans;
        int length2 = spanArr2.length;
        while (i2 < length2) {
            spanArr2[i2].cacheReferenceLineAndClear(layoutManagerHelper.getReverseLayout() ^ anchorInfoWrapper.layoutFromEnd, i4, mainOrientationHelper);
            i2++;
        }
    }

    @Override // com.alibaba.android.vlayout.layout.MarginLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public int computeAlignOffset(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper) {
        boolean z3 = layoutManagerHelper.getOrientation() == 1;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        View viewFindViewByPosition = layoutManagerHelper.findViewByPosition(((Integer) getRange().getLower()).intValue() + i);
        if (viewFindViewByPosition == null) {
            return 0;
        }
        ensureLanes();
        if (z3) {
            if (z) {
                if (i == getItemCount() - 1) {
                    return this.mMarginBottom + this.mPaddingBottom + (getMaxEnd(mainOrientationHelper.getDecoratedEnd(viewFindViewByPosition), mainOrientationHelper) - mainOrientationHelper.getDecoratedEnd(viewFindViewByPosition));
                }
                if (!z2) {
                    return getMinEnd(mainOrientationHelper.getDecoratedStart(viewFindViewByPosition), mainOrientationHelper) - mainOrientationHelper.getDecoratedEnd(viewFindViewByPosition);
                }
            } else {
                if (i == 0) {
                    return ((-this.mMarginTop) - this.mPaddingTop) - (mainOrientationHelper.getDecoratedStart(viewFindViewByPosition) - getMinStart(mainOrientationHelper.getDecoratedStart(viewFindViewByPosition), mainOrientationHelper));
                }
                if (!z2) {
                    return getMaxStart(mainOrientationHelper.getDecoratedEnd(viewFindViewByPosition), mainOrientationHelper) - mainOrientationHelper.getDecoratedStart(viewFindViewByPosition);
                }
            }
        }
        return 0;
    }

    public int getColLength() {
        return this.mColLength;
    }

    public int getHGap() {
        return this.mHGap;
    }

    public int getLane() {
        return this.mNumLanes;
    }

    public int getVGap() {
        return this.mVGap;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public boolean isRecyclable(int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper, boolean z) {
        View viewFindViewByPosition;
        boolean zIsRecyclable = super.isRecyclable(i, i2, i3, layoutManagerHelper, z);
        if (zIsRecyclable && (viewFindViewByPosition = layoutManagerHelper.findViewByPosition(i)) != null) {
            OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
            int viewPosition = ((RecyclerView.LayoutParams) viewFindViewByPosition.getLayoutParams()).getViewPosition();
            if (layoutManagerHelper.getReverseLayout()) {
                if (z) {
                    Span spanFindSpan = findSpan(viewPosition, viewFindViewByPosition, true);
                    if (spanFindSpan != null) {
                        spanFindSpan.popEnd(mainOrientationHelper);
                    }
                } else {
                    Span spanFindSpan2 = findSpan(viewPosition, viewFindViewByPosition, false);
                    if (spanFindSpan2 != null) {
                        spanFindSpan2.popStart(mainOrientationHelper);
                    }
                }
            } else if (z) {
                Span spanFindSpan3 = findSpan(viewPosition, viewFindViewByPosition, true);
                if (spanFindSpan3 != null) {
                    spanFindSpan3.popStart(mainOrientationHelper);
                }
            } else {
                Span spanFindSpan4 = findSpan(viewPosition, viewFindViewByPosition, false);
                if (spanFindSpan4 != null) {
                    spanFindSpan4.popEnd(mainOrientationHelper);
                }
            }
        }
        return zIsRecyclable;
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        int offset;
        int extra;
        VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper2;
        int i;
        int i2;
        int i3;
        int i4;
        Span nextSpan;
        boolean z;
        int decoratedMeasurement;
        int decoratedMeasurement2;
        View view;
        Span span;
        int i5;
        OrientationHelperEx orientationHelperEx;
        if (isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
            return;
        }
        ensureLanes();
        boolean z2 = layoutManagerHelper.getOrientation() == 1;
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        OrientationHelperEx secondaryOrientationHelper = layoutManagerHelper.getSecondaryOrientationHelper();
        boolean zIsEnableMarginOverLap = layoutManagerHelper.isEnableMarginOverLap();
        this.mRemainingSpans.set(0, this.mNumLanes, true);
        if (layoutStateWrapper.getLayoutDirection() == 1) {
            offset = layoutStateWrapper.getOffset() + layoutStateWrapper.getAvailable();
            extra = layoutStateWrapper.getExtra() + offset + mainOrientationHelper.getEndPadding();
        } else {
            offset = layoutStateWrapper.getOffset() - layoutStateWrapper.getAvailable();
            extra = (offset - layoutStateWrapper.getExtra()) - mainOrientationHelper.getStartAfterPadding();
        }
        int i6 = offset;
        int i7 = extra;
        updateAllRemainingSpans(layoutStateWrapper.getLayoutDirection(), i7, mainOrientationHelper);
        int offset2 = layoutStateWrapper.getOffset();
        this.prelayoutViewList.clear();
        while (layoutStateWrapper.hasMore(state) && !this.mRemainingSpans.isEmpty() && !isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
            int currentPosition = layoutStateWrapper.getCurrentPosition();
            View next = layoutStateWrapper.next(recycler);
            if (next == null) {
                break;
            }
            VirtualLayoutManager.LayoutParams layoutParams = (VirtualLayoutManager.LayoutParams) next.getLayoutParams();
            int viewPosition = layoutParams.getViewPosition();
            int i8 = i7;
            int span2 = this.mLazySpanLookup.getSpan(viewPosition);
            if (span2 == Integer.MIN_VALUE) {
                nextSpan = getNextSpan(offset2, layoutStateWrapper, layoutManagerHelper);
                this.mLazySpanLookup.setSpan(viewPosition, nextSpan);
            } else {
                nextSpan = this.mSpans[span2];
            }
            Span span3 = nextSpan;
            boolean z3 = viewPosition - ((Integer) getRange().getLower()).intValue() < this.mNumLanes;
            boolean z4 = ((Integer) getRange().getUpper()).intValue() - viewPosition < this.mNumLanes;
            if (layoutStateWrapper.isPreLayout()) {
                this.prelayoutViewList.add(next);
            }
            layoutManagerHelper.addChildView(layoutStateWrapper, next);
            if (z2) {
                int childMeasureSpec = layoutManagerHelper.getChildMeasureSpec(this.mColLength, layoutParams.width, false);
                layoutManagerHelper.measureChildWithMargins(next, childMeasureSpec, layoutManagerHelper.getChildMeasureSpec(mainOrientationHelper.getTotalSpace(), Float.isNaN(layoutParams.mAspectRatio) ? layoutParams.height : (int) ((View.MeasureSpec.getSize(childMeasureSpec) / layoutParams.mAspectRatio) + 0.5f), true));
                z = true;
            } else {
                int childMeasureSpec2 = layoutManagerHelper.getChildMeasureSpec(this.mColLength, layoutParams.height, false);
                int totalSpace = mainOrientationHelper.getTotalSpace();
                int size = Float.isNaN(layoutParams.mAspectRatio) ? layoutParams.width : (int) ((View.MeasureSpec.getSize(childMeasureSpec2) * layoutParams.mAspectRatio) + 0.5f);
                z = true;
                layoutManagerHelper.measureChildWithMargins(next, layoutManagerHelper.getChildMeasureSpec(totalSpace, size, true), childMeasureSpec2);
            }
            if (layoutStateWrapper.getLayoutDirection() == z) {
                decoratedMeasurement2 = span3.getEndLine(offset2, mainOrientationHelper);
                if (z3) {
                    decoratedMeasurement2 += computeStartSpace(layoutManagerHelper, z2, z, zIsEnableMarginOverLap);
                } else if (!this.mLayoutWithAnchor) {
                    decoratedMeasurement2 += z2 ? this.mVGap : this.mHGap;
                } else if (Math.abs(currentPosition - this.anchorPosition) >= this.mNumLanes) {
                    decoratedMeasurement2 += z2 ? this.mVGap : this.mHGap;
                }
                decoratedMeasurement = mainOrientationHelper.getDecoratedMeasurement(next) + decoratedMeasurement2;
            } else {
                int startLine = z4 ? span3.getStartLine(offset2, mainOrientationHelper) - ((z2 ? this.mMarginBottom : this.mMarginRight) + this.mPaddingRight) : span3.getStartLine(offset2, mainOrientationHelper) - (z2 ? this.mVGap : this.mHGap);
                decoratedMeasurement = startLine;
                decoratedMeasurement2 = startLine - mainOrientationHelper.getDecoratedMeasurement(next);
            }
            if (layoutStateWrapper.getLayoutDirection() == 1) {
                span3.appendToSpan(next, mainOrientationHelper);
            } else {
                span3.prependToSpan(next, mainOrientationHelper);
            }
            int startAfterPadding = (span3.mIndex == this.mNumLanes - 1 ? ((span3.mIndex * (this.mColLength + this.mEachGap)) - this.mEachGap) + this.mLastGap : span3.mIndex * (this.mColLength + this.mEachGap)) + secondaryOrientationHelper.getStartAfterPadding();
            int i9 = z2 ? startAfterPadding + this.mMarginLeft + this.mPaddingLeft : startAfterPadding + this.mMarginTop + this.mPaddingTop;
            int decoratedMeasurementInOther = i9 + mainOrientationHelper.getDecoratedMeasurementInOther(next);
            if (z2) {
                view = next;
                layoutChildWithMargin(next, i9, decoratedMeasurement2, decoratedMeasurementInOther, decoratedMeasurement, layoutManagerHelper);
                i5 = i8;
                span = span3;
                orientationHelperEx = mainOrientationHelper;
            } else {
                view = next;
                int i10 = decoratedMeasurement2;
                span = span3;
                int i11 = decoratedMeasurement;
                i5 = i8;
                orientationHelperEx = mainOrientationHelper;
                layoutChildWithMargin(view, i10, i9, i11, decoratedMeasurementInOther, layoutManagerHelper);
            }
            updateRemainingSpans(span, layoutStateWrapper.getLayoutDirection(), i5, orientationHelperEx);
            recycle(recycler, layoutStateWrapper, span, i6, layoutManagerHelper);
            handleStateOnResult(layoutChunkResult, view);
            i7 = i5;
            mainOrientationHelper = orientationHelperEx;
            offset2 = offset2;
            zIsEnableMarginOverLap = zIsEnableMarginOverLap;
        }
        OrientationHelperEx orientationHelperEx2 = mainOrientationHelper;
        if (isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
            if (layoutStateWrapper.getLayoutDirection() == -1) {
                for (Span span4 : this.mSpans) {
                    if (span4.mCachedStart != Integer.MIN_VALUE) {
                        span4.mLastEdgeStart = span4.mCachedStart;
                    }
                }
            } else {
                for (Span span5 : this.mSpans) {
                    if (span5.mCachedEnd != Integer.MIN_VALUE) {
                        span5.mLastEdgeEnd = span5.mCachedEnd;
                    }
                }
            }
        }
        if (layoutStateWrapper.getLayoutDirection() == -1) {
            if (isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
                layoutStateWrapper2 = layoutStateWrapper;
            } else {
                layoutStateWrapper2 = layoutStateWrapper;
                if (layoutStateWrapper2.hasMore(state)) {
                    layoutChunkResult.mConsumed = layoutStateWrapper.getOffset() - getMaxStart(orientationHelperEx2.getStartAfterPadding(), orientationHelperEx2);
                }
            }
            int offset3 = layoutStateWrapper.getOffset() - getMinStart(orientationHelperEx2.getEndAfterPadding(), orientationHelperEx2);
            if (z2) {
                i3 = this.mMarginTop;
                i4 = this.mPaddingTop;
            } else {
                i3 = this.mMarginLeft;
                i4 = this.mPaddingLeft;
            }
            layoutChunkResult.mConsumed = offset3 + i3 + i4;
        } else {
            layoutStateWrapper2 = layoutStateWrapper;
            if (isOutOfRange(layoutStateWrapper.getCurrentPosition()) || !layoutStateWrapper2.hasMore(state)) {
                int maxEnd = getMaxEnd(orientationHelperEx2.getEndAfterPadding(), orientationHelperEx2) - layoutStateWrapper.getOffset();
                if (z2) {
                    i = this.mMarginBottom;
                    i2 = this.mPaddingBottom;
                } else {
                    i = this.mMarginRight;
                    i2 = this.mPaddingRight;
                }
                layoutChunkResult.mConsumed = maxEnd + i + i2;
            } else {
                layoutChunkResult.mConsumed = getMinEnd(orientationHelperEx2.getEndAfterPadding(), orientationHelperEx2) - layoutStateWrapper.getOffset();
            }
        }
        recycleForPreLayout(recycler, layoutStateWrapper2, layoutManagerHelper);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void onClear(LayoutManagerHelper layoutManagerHelper) {
        super.onClear(layoutManagerHelper);
        this.mLazySpanLookup.clear();
        this.mSpans = null;
        this.mLayoutManager = null;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onItemsChanged(LayoutManagerHelper layoutManagerHelper) {
        this.mLazySpanLookup.clear();
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onOffsetChildrenHorizontal(int i, LayoutManagerHelper layoutManagerHelper) {
        super.onOffsetChildrenHorizontal(i, layoutManagerHelper);
        if (layoutManagerHelper.getOrientation() == 0) {
            for (Span span : this.mSpans) {
                span.onOffset(i);
            }
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onOffsetChildrenVertical(int i, LayoutManagerHelper layoutManagerHelper) {
        super.onOffsetChildrenVertical(i, layoutManagerHelper);
        if (layoutManagerHelper.getOrientation() == 1) {
            for (Span span : this.mSpans) {
                span.onOffset(i);
            }
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onRefreshLayout(RecyclerView.State state, VirtualLayoutManager.AnchorInfoWrapper anchorInfoWrapper, LayoutManagerHelper layoutManagerHelper) {
        super.onRefreshLayout(state, anchorInfoWrapper, layoutManagerHelper);
        ensureLanes();
        if (isOutOfRange(anchorInfoWrapper.position)) {
            for (Span span : this.mSpans) {
                span.clear();
            }
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        this.mLazySpanLookup.mData = bundle.getIntArray(LOOKUP_BUNDLE_KEY);
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onSaveState(Bundle bundle) {
        super.onSaveState(bundle);
        bundle.putIntArray(LOOKUP_BUNDLE_KEY, this.mLazySpanLookup.mData);
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onScrollStateChanged(int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper) {
        if (i2 > ((Integer) getRange().getUpper()).intValue() || i3 < ((Integer) getRange().getLower()).intValue() || i != 0) {
            return;
        }
        checkForGaps();
    }

    public void setGap(int i) {
        setHGap(i);
        setVGap(i);
    }

    public void setHGap(int i) {
        this.mHGap = i;
    }

    public void setLane(int i) {
        this.mNumLanes = i;
        ensureLanes();
    }

    public void setVGap(int i) {
        this.mVGap = i;
    }
}
