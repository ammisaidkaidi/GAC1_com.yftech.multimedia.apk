package com.alibaba.android.vlayout;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Parcelable;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.layout.BaseLayoutHelper;
import com.alibaba.android.vlayout.layout.DefaultLayoutHelper;
import com.alibaba.android.vlayout.layout.FixAreaAdjuster;
import com.alibaba.android.vlayout.layout.FixAreaLayoutHelper;
import com.alibaba.android.vlayout.layout.LayoutChunkResult;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
public class VirtualLayoutManager extends ExposeLinearLayoutManagerEx implements LayoutManagerHelper {
    private static LayoutHelper DEFAULT_LAYOUT_HELPER = new DefaultLayoutHelper();
    public static final int HORIZONTAL = 0;
    private static final int MAX_NO_SCROLLING_SIZE = 134217727;
    protected static final String TAG = "VirtualLayoutManager";
    private static final String TRACE_LAYOUT = "VLM onLayoutChildren";
    private static final String TRACE_SCROLL = "VLM scroll";
    public static final int VERTICAL = 1;
    private static boolean sDebuggable = false;
    private Rect mDecorInsets;
    private LayoutHelper mDefaultLayoutHelper;
    private boolean mEnableMarginOverlapping;
    private FixAreaAdjuster mFixAreaAdjustor;
    private LayoutHelperFinder mHelperFinder;
    private BaseLayoutHelper.LayoutViewBindListener mLayoutViewBindListener;
    private LayoutViewFactory mLayoutViewFatory;
    private int mMaxMeasureSize;
    private int mMeasuredFullSpace;
    private int mNested;
    private boolean mNestedScrolling;
    private boolean mNoScrolling;
    protected OrientationHelperEx mOrientationHelper;
    private List<Pair<Range<Integer>, Integer>> mRangeLengths;
    private RecyclerView mRecyclerView;
    protected OrientationHelperEx mSecondaryOrientationHelper;
    private boolean mSpaceMeasured;
    private boolean mSpaceMeasuring;
    private AnchorInfoWrapper mTempAnchorInfoWrapper;
    private LayoutStateWrapper mTempLayoutStateWrapper;
    private HashMap<Integer, LayoutHelper> newHelpersSet;
    private HashMap<Integer, LayoutHelper> oldHelpersSet;

    public static class AnchorInfoWrapper {
        public int coordinate;
        public boolean layoutFromEnd;
        public int position;

        AnchorInfoWrapper() {
        }
    }

    public interface CacheViewHolder {
        boolean needCached();
    }

    public static class InflateLayoutParams extends LayoutParams {
        public InflateLayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }
    }

    public static class LayoutParams extends RecyclerView.LayoutParams {
        public static final int INVALIDE_SIZE = Integer.MIN_VALUE;
        public float mAspectRatio;
        private int mOriginHeight;
        private int mOriginWidth;
        public int zIndex;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.zIndex = 0;
            this.mAspectRatio = Float.NaN;
            this.mOriginWidth = Integer.MIN_VALUE;
            this.mOriginHeight = Integer.MIN_VALUE;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.zIndex = 0;
            this.mAspectRatio = Float.NaN;
            this.mOriginWidth = Integer.MIN_VALUE;
            this.mOriginHeight = Integer.MIN_VALUE;
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.zIndex = 0;
            this.mAspectRatio = Float.NaN;
            this.mOriginWidth = Integer.MIN_VALUE;
            this.mOriginHeight = Integer.MIN_VALUE;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.zIndex = 0;
            this.mAspectRatio = Float.NaN;
            this.mOriginWidth = Integer.MIN_VALUE;
            this.mOriginHeight = Integer.MIN_VALUE;
        }

        public LayoutParams(RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
            this.zIndex = 0;
            this.mAspectRatio = Float.NaN;
            this.mOriginWidth = Integer.MIN_VALUE;
            this.mOriginHeight = Integer.MIN_VALUE;
        }

        public void restoreOriginHeight() {
            if (this.mOriginHeight != Integer.MIN_VALUE) {
                this.height = this.mOriginHeight;
            }
        }

        public void restoreOriginWidth() {
            if (this.mOriginWidth != Integer.MIN_VALUE) {
                this.width = this.mOriginWidth;
            }
        }

        public void storeOriginHeight() {
            if (this.mOriginHeight == Integer.MIN_VALUE) {
                this.mOriginHeight = this.height;
            }
        }

        public void storeOriginWidth() {
            if (this.mOriginWidth == Integer.MIN_VALUE) {
                this.mOriginWidth = this.width;
            }
        }
    }

    public static class LayoutStateWrapper {
        static final int INVALID_LAYOUT = Integer.MIN_VALUE;
        public static final int ITEM_DIRECTION_HEAD = -1;
        public static final int ITEM_DIRECTION_TAIL = 1;
        public static final int LAYOUT_END = 1;
        public static final int LAYOUT_START = -1;
        static final int SCOLLING_OFFSET_NaN = Integer.MIN_VALUE;
        private ExposeLinearLayoutManagerEx.LayoutState mLayoutState;

        LayoutStateWrapper() {
        }

        LayoutStateWrapper(ExposeLinearLayoutManagerEx.LayoutState layoutState) {
            this.mLayoutState = layoutState;
        }

        public int getAvailable() {
            return this.mLayoutState.mAvailable;
        }

        public int getCurrentPosition() {
            return this.mLayoutState.mCurrentPosition;
        }

        public int getExtra() {
            return this.mLayoutState.mExtra;
        }

        public int getItemDirection() {
            return this.mLayoutState.mItemDirection;
        }

        public int getLayoutDirection() {
            return this.mLayoutState.mLayoutDirection;
        }

        public int getOffset() {
            return this.mLayoutState.mOffset;
        }

        public int getScrollingOffset() {
            return this.mLayoutState.mScrollingOffset;
        }

        public boolean hasMore(RecyclerView.State state) {
            return this.mLayoutState.hasMore(state);
        }

        public boolean hasScrapList() {
            return this.mLayoutState.mScrapList != null;
        }

        public boolean isPreLayout() {
            return this.mLayoutState.mIsPreLayout;
        }

        public boolean isRecycle() {
            return this.mLayoutState.mRecycle;
        }

        public boolean isRefreshLayout() {
            return this.mLayoutState.mOnRefresLayout;
        }

        public View next(RecyclerView.Recycler recycler) {
            return this.mLayoutState.next(recycler);
        }

        public View retrieve(RecyclerView.Recycler recycler, int i) {
            int i2 = this.mLayoutState.mCurrentPosition;
            this.mLayoutState.mCurrentPosition = i;
            View next = next(recycler);
            this.mLayoutState.mCurrentPosition = i2;
            return next;
        }

        public void skipCurrentPosition() {
            this.mLayoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
        }
    }

    private static class LayoutViewHolder extends RecyclerView.ViewHolder {
        public LayoutViewHolder(View view) {
            super(view);
        }
    }

    public VirtualLayoutManager(@NonNull Context context) {
        this(context, 1);
    }

    public VirtualLayoutManager(@NonNull Context context, int i) {
        this(context, i, false);
    }

    public VirtualLayoutManager(@NonNull Context context, int i, boolean z) {
        super(context, i, z);
        this.mNoScrolling = false;
        this.mNestedScrolling = false;
        this.mEnableMarginOverlapping = false;
        this.mMaxMeasureSize = -1;
        this.mFixAreaAdjustor = FixAreaAdjuster.mDefaultAdjuster;
        this.newHelpersSet = new HashMap<>();
        this.oldHelpersSet = new HashMap<>();
        this.mTempAnchorInfoWrapper = new AnchorInfoWrapper();
        this.mNested = 0;
        this.mTempLayoutStateWrapper = new LayoutStateWrapper();
        this.mRangeLengths = new LinkedList();
        this.mDefaultLayoutHelper = DEFAULT_LAYOUT_HELPER;
        this.mLayoutViewFatory = new LayoutViewFactory() { // from class: com.alibaba.android.vlayout.VirtualLayoutManager.3
            @Override // com.alibaba.android.vlayout.LayoutViewFactory
            public View generateLayoutView(@NonNull Context context2) {
                return new LayoutView(context2);
            }
        };
        this.mDecorInsets = new Rect();
        this.mSpaceMeasured = false;
        this.mMeasuredFullSpace = 0;
        this.mSpaceMeasuring = false;
        this.mOrientationHelper = OrientationHelperEx.createOrientationHelper(this, i);
        this.mSecondaryOrientationHelper = OrientationHelperEx.createOrientationHelper(this, i != 1 ? 1 : 0);
        setHelperFinder(new RangeLayoutHelperFinder());
    }

    public static void enableDebugging(boolean z) {
        sDebuggable = z;
    }

    @Nullable
    private int findRangeLength(@NonNull Range<Integer> range) {
        Pair<Range<Integer>, Integer> pair;
        Pair<Range<Integer>, Integer> pair2;
        Range<Integer> range2;
        int size = this.mRangeLengths.size();
        if (size == 0) {
            return -1;
        }
        int i = 0;
        int i2 = size - 1;
        int i3 = -1;
        while (true) {
            pair = null;
            if (i > i2 || (range2 = (pair2 = this.mRangeLengths.get((i3 = (i + i2) / 2))).first) == null) {
                break;
            }
            if (range2.contains(range.getLower()) || range2.contains(range.getUpper()) || range.contains(range2)) {
                pair = pair2;
                break;
            }
            if (((Integer) range2.getLower()).intValue() > ((Integer) range.getUpper()).intValue()) {
                i2 = i3 - 1;
            } else if (((Integer) range2.getUpper()).intValue() < ((Integer) range.getLower()).intValue()) {
                i = i3 + 1;
            }
        }
        if (pair == null) {
            return -1;
        }
        return i3;
    }

    private void measureChildWithDecorations(View view, int i, int i2) {
        calculateItemDecorationsForChild(view, this.mDecorInsets);
        view.measure(updateSpecWithExtra(i, this.mDecorInsets.left, this.mDecorInsets.right), updateSpecWithExtra(i2, this.mDecorInsets.top, this.mDecorInsets.bottom));
    }

    private void measureChildWithDecorationsAndMargin(View view, int i, int i2) {
        calculateItemDecorationsForChild(view, this.mDecorInsets);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        if (getOrientation() == 1) {
            i = updateSpecWithExtra(i, layoutParams.leftMargin + this.mDecorInsets.left, layoutParams.rightMargin + this.mDecorInsets.right);
        }
        if (getOrientation() == 0) {
            i2 = updateSpecWithExtra(i2, this.mDecorInsets.top, this.mDecorInsets.bottom);
        }
        view.measure(i, i2);
    }

    private void runPostLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int i) throws Exception {
        this.mNested--;
        if (this.mNested <= 0) {
            this.mNested = 0;
            int iFindFirstVisibleItemPosition = findFirstVisibleItemPosition();
            int iFindLastVisibleItemPosition = findLastVisibleItemPosition();
            Iterator<LayoutHelper> it = this.mHelperFinder.iterator();
            while (it.hasNext()) {
                try {
                    it.next().afterLayout(recycler, state, iFindFirstVisibleItemPosition, iFindLastVisibleItemPosition, i, this);
                } catch (Exception e) {
                    if (sDebuggable) {
                        throw e;
                    }
                }
            }
        }
    }

    private void runPreLayout(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (this.mNested == 0) {
            Iterator<LayoutHelper> it = this.mHelperFinder.reverse().iterator();
            while (it.hasNext()) {
                it.next().beforeLayout(recycler, state, this);
            }
        }
        this.mNested++;
    }

    private void setDefaultLayoutHelper(@NonNull LayoutHelper layoutHelper) {
        if (layoutHelper == null) {
            throw new IllegalArgumentException("layoutHelper should not be null");
        }
        this.mDefaultLayoutHelper = layoutHelper;
    }

    private int updateSpecWithExtra(int i, int i2, int i3) {
        if (i2 == 0 && i3 == 0) {
            return i;
        }
        int mode = View.MeasureSpec.getMode(i);
        if (mode == Integer.MIN_VALUE || mode == 1073741824) {
            return (View.MeasureSpec.getSize(i) - i2) - i3 < 0 ? View.MeasureSpec.makeMeasureSpec(0, mode) : View.MeasureSpec.makeMeasureSpec((View.MeasureSpec.getSize(i) - i2) - i3, mode);
        }
        return i;
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void addBackgroundView(View view, boolean z) {
        showView(view);
        addView(view, z ? 0 : -1);
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void addChildView(View view, int i) {
        super.addView(view, i);
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void addChildView(LayoutStateWrapper layoutStateWrapper, View view) {
        addChildView(layoutStateWrapper, view, layoutStateWrapper.getItemDirection() == 1 ? -1 : 0);
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void addChildView(LayoutStateWrapper layoutStateWrapper, View view, int i) {
        showView(view);
        if (layoutStateWrapper.hasScrapList()) {
            addDisappearingView(view, i);
        } else {
            addView(view, i);
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void addFixedView(View view) {
        addOffFlowView(view, false);
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void addOffFlowView(View view, boolean z) {
        showView(view);
        addHiddenView(view, z);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public /* bridge */ /* synthetic */ void assertNotInLayoutOrScroll(String str) {
        super.assertNotInLayoutOrScroll(str);
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollHorizontally() {
        return super.canScrollHorizontally() && !this.mNoScrolling;
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return super.canScrollVertically() && !this.mNoScrolling;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean checkLayoutParams(RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx
    protected int computeAlignOffset(int i, boolean z, boolean z2) {
        LayoutHelper layoutHelper;
        if (i == -1 || (layoutHelper = this.mHelperFinder.getLayoutHelper(i)) == null) {
            return 0;
        }
        return layoutHelper.computeAlignOffset(i - ((Integer) layoutHelper.getRange().getLower()).intValue(), z, z2, this);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx
    protected int computeAlignOffset(View view, boolean z, boolean z2) {
        return computeAlignOffset(getPosition(view), z, z2);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
    public /* bridge */ /* synthetic */ PointF computeScrollVectorForPosition(int i) {
        return super.computeScrollVectorForPosition(i);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void detachAndScrapAttachedViews(RecyclerView.Recycler recycler) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            RecyclerView.ViewHolder childViewHolder = getChildViewHolder(getChildAt(childCount));
            if ((childViewHolder instanceof CacheViewHolder) && ((CacheViewHolder) childViewHolder).needCached()) {
                ExposeLinearLayoutManagerEx.ViewHolderWrapper.setFlags(childViewHolder, 0, 6);
            }
        }
        super.detachAndScrapAttachedViews(recycler);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void detachAndScrapView(View view, RecyclerView.Recycler recycler) {
        super.detachAndScrapView(view, recycler);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void detachAndScrapViewAt(int i, RecyclerView.Recycler recycler) {
        RecyclerView.ViewHolder childViewHolder = getChildViewHolder(getChildAt(i));
        if ((childViewHolder instanceof CacheViewHolder) && ((CacheViewHolder) childViewHolder).needCached()) {
            ExposeLinearLayoutManagerEx.ViewHolderWrapper.setFlags(childViewHolder, 0, 4);
        }
        super.detachAndScrapViewAt(i, recycler);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager
    public /* bridge */ /* synthetic */ int findFirstVisibleItemPosition() {
        return super.findFirstVisibleItemPosition();
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager
    public /* bridge */ /* synthetic */ int findLastVisibleItemPosition() {
        return super.findLastVisibleItemPosition();
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public LayoutHelper findLayoutHelperByPosition(int i) {
        return this.mHelperFinder.getLayoutHelper(i);
    }

    public LayoutHelper findNeighbourNonfixLayoutHelper(LayoutHelper layoutHelper, boolean z) {
        List<LayoutHelper> layoutHelpers;
        int iIndexOf;
        LayoutHelper layoutHelper2;
        if (layoutHelper == null || (iIndexOf = (layoutHelpers = this.mHelperFinder.getLayoutHelpers()).indexOf(layoutHelper)) == -1) {
            return null;
        }
        int i = z ? iIndexOf - 1 : iIndexOf + 1;
        if (i < 0 || i >= layoutHelpers.size() || (layoutHelper2 = layoutHelpers.get(i)) == null || layoutHelper2.isFixLayout()) {
            return null;
        }
        return layoutHelper2;
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public View findViewByPosition(int i) {
        View viewFindViewByPosition = super.findViewByPosition(i);
        if (viewFindViewByPosition != null && getPosition(viewFindViewByPosition) == i) {
            return viewFindViewByPosition;
        }
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (childAt != null && getPosition(childAt) == i) {
                return childAt;
            }
        }
        return null;
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new InflateLayoutParams(context, attributeSet);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((RecyclerView.LayoutParams) layoutParams);
        }
        if (layoutParams instanceof RecyclerView.LayoutParams) {
            return new LayoutParams((RecyclerView.LayoutParams) layoutParams);
        }
        return layoutParams instanceof ViewGroup.MarginLayoutParams ? new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams) : new LayoutParams(layoutParams);
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public final View generateLayoutView() {
        if (this.mRecyclerView == null) {
            return null;
        }
        View viewGenerateLayoutView = this.mLayoutViewFatory.generateLayoutView(this.mRecyclerView.getContext());
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        attachViewHolder(layoutParams, new LayoutViewHolder(viewGenerateLayoutView));
        viewGenerateLayoutView.setLayoutParams(layoutParams);
        return viewGenerateLayoutView;
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public int getChildMeasureSpec(int i, int i2, boolean z) {
        return getChildMeasureSpec(i, 0, i2, z);
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public RecyclerView.ViewHolder getChildViewHolder(View view) {
        if (this.mRecyclerView != null) {
            return this.mRecyclerView.getChildViewHolder(view);
        }
        return null;
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public int getContentHeight() {
        return super.getHeight();
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public int getContentWidth() {
        return super.getWidth();
    }

    public List<View> getFixedViews() {
        if (this.mRecyclerView == null) {
            return Collections.emptyList();
        }
        LinkedList linkedList = new LinkedList();
        Iterator<LayoutHelper> it = this.mHelperFinder.iterator();
        while (it.hasNext()) {
            View fixedView = it.next().getFixedView();
            if (fixedView != null) {
                linkedList.add(fixedView);
            }
        }
        return linkedList;
    }

    @NonNull
    public List<LayoutHelper> getLayoutHelpers() {
        return this.mHelperFinder.getLayoutHelpers();
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public OrientationHelperEx getMainOrientationHelper() {
        return this.mOrientationHelper;
    }

    public int getOffsetToStart() {
        if (getChildCount() == 0) {
            return -1;
        }
        View childAt = getChildAt(0);
        if (childAt == null) {
            return -1;
        }
        int position = getPosition(childAt);
        int iFindRangeLength = findRangeLength(Range.create(Integer.valueOf(position), Integer.valueOf(position)));
        if (iFindRangeLength < 0 || iFindRangeLength >= this.mRangeLengths.size()) {
            return -1;
        }
        int iIntValue = -this.mOrientationHelper.getDecoratedStart(childAt);
        for (int i = 0; i < iFindRangeLength; i++) {
            Pair<Range<Integer>, Integer> pair = this.mRangeLengths.get(i);
            if (pair != null) {
                iIntValue += pair.second.intValue();
            }
        }
        return iIntValue;
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager, com.alibaba.android.vlayout.LayoutManagerHelper
    public int getOrientation() {
        return super.getOrientation();
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public RecyclerView getRecyclerView() {
        return this.mRecyclerView;
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public OrientationHelperEx getSecondaryOrientationHelper() {
        return this.mSecondaryOrientationHelper;
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, com.alibaba.android.vlayout.LayoutManagerHelper
    public void hideView(View view) {
        super.hideView(view);
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public boolean isDoLayoutRTL() {
        return isLayoutRTL();
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, com.alibaba.android.vlayout.LayoutManagerHelper
    public boolean isEnableMarginOverLap() {
        return this.mEnableMarginOverlapping;
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public boolean isViewHolderUpdated(View view) {
        RecyclerView.ViewHolder childViewHolder = getChildViewHolder(view);
        return childViewHolder == null || isViewHolderUpdated(childViewHolder);
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void layoutChild(View view, int i, int i2, int i3, int i4) {
        layoutDecorated(view, i, i2, i3, i4);
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void layoutChildWithMargins(View view, int i, int i2, int i3, int i4) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        layoutDecorated(view, i + marginLayoutParams.leftMargin, i2 + marginLayoutParams.topMargin, i3 - marginLayoutParams.rightMargin, i4 - marginLayoutParams.bottomMargin);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx
    protected void layoutChunk(RecyclerView.Recycler recycler, RecyclerView.State state, ExposeLinearLayoutManagerEx.LayoutState layoutState, LayoutChunkResult layoutChunkResult) {
        int i = layoutState.mCurrentPosition;
        this.mTempLayoutStateWrapper.mLayoutState = layoutState;
        LayoutHelper layoutHelper = this.mHelperFinder == null ? null : this.mHelperFinder.getLayoutHelper(i);
        if (layoutHelper == null) {
            layoutHelper = this.mDefaultLayoutHelper;
        }
        layoutHelper.doLayout(recycler, state, this.mTempLayoutStateWrapper, layoutChunkResult, this);
        this.mTempLayoutStateWrapper.mLayoutState = null;
        if (layoutState.mCurrentPosition == i) {
            Log.w(TAG, "layoutHelper[" + layoutHelper.getClass().getSimpleName() + "@" + layoutHelper.toString() + "] consumes no item!");
            layoutChunkResult.mFinished = true;
            return;
        }
        int i2 = layoutState.mCurrentPosition - layoutState.mItemDirection;
        int i3 = layoutChunkResult.mIgnoreConsumed ? 0 : layoutChunkResult.mConsumed;
        Range<Integer> range = new Range<>(Integer.valueOf(Math.min(i, i2)), Integer.valueOf(Math.max(i, i2)));
        int iFindRangeLength = findRangeLength(range);
        if (iFindRangeLength >= 0) {
            Pair<Range<Integer>, Integer> pair = this.mRangeLengths.get(iFindRangeLength);
            if (pair != null && pair.first.equals(range) && pair.second.intValue() == i3) {
                return;
            } else {
                this.mRangeLengths.remove(iFindRangeLength);
            }
        }
        this.mRangeLengths.add(Pair.create(range, Integer.valueOf(i3)));
        Collections.sort(this.mRangeLengths, new Comparator<Pair<Range<Integer>, Integer>>() { // from class: com.alibaba.android.vlayout.VirtualLayoutManager.2
            @Override // java.util.Comparator
            public int compare(Pair<Range<Integer>, Integer> pair2, Pair<Range<Integer>, Integer> pair3) {
                if (pair2 == null && pair3 == null) {
                    return 0;
                }
                if (pair2 == null) {
                    return -1;
                }
                if (pair3 == null) {
                    return 1;
                }
                return ((Integer) pair2.first.getLower()).intValue() - ((Integer) pair3.first.getLower()).intValue();
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, com.alibaba.android.vlayout.LayoutManagerHelper
    public void measureChild(View view, int i, int i2) {
        measureChildWithDecorations(view, i, i2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager, com.alibaba.android.vlayout.LayoutManagerHelper
    public void measureChildWithMargins(View view, int i, int i2) {
        measureChildWithDecorationsAndMargin(view, i, i2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void moveView(int i, int i2) {
        super.moveView(i, i2);
    }

    public int obtainExtraMargin(View view, boolean z) {
        return obtainExtraMargin(view, z, true);
    }

    public int obtainExtraMargin(View view, boolean z, boolean z2) {
        if (view != null) {
            return computeAlignOffset(view, z, z2);
        }
        return 0;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void offsetChildrenHorizontal(int i) {
        super.offsetChildrenHorizontal(i);
        Iterator<LayoutHelper> it = this.mHelperFinder.iterator();
        while (it.hasNext()) {
            it.next().onOffsetChildrenHorizontal(i, this);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void offsetChildrenVertical(int i) {
        super.offsetChildrenVertical(i);
        Iterator<LayoutHelper> it = this.mHelperFinder.iterator();
        while (it.hasNext()) {
            it.next().onOffsetChildrenVertical(i, this);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onAdapterChanged(RecyclerView.Adapter adapter, RecyclerView.Adapter adapter2) {
        super.onAdapterChanged(adapter, adapter2);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx
    public void onAnchorReady(RecyclerView.State state, ExposeLinearLayoutManagerEx.AnchorInfo anchorInfo) {
        super.onAnchorReady(state, anchorInfo);
        boolean z = true;
        while (z) {
            this.mTempAnchorInfoWrapper.position = anchorInfo.mPosition;
            this.mTempAnchorInfoWrapper.coordinate = anchorInfo.mCoordinate;
            this.mTempAnchorInfoWrapper.layoutFromEnd = anchorInfo.mLayoutFromEnd;
            LayoutHelper layoutHelper = this.mHelperFinder.getLayoutHelper(anchorInfo.mPosition);
            if (layoutHelper != null) {
                layoutHelper.checkAnchorInfo(state, this.mTempAnchorInfoWrapper, this);
            }
            if (this.mTempAnchorInfoWrapper.position == anchorInfo.mPosition) {
                z = false;
            } else {
                anchorInfo.mPosition = this.mTempAnchorInfoWrapper.position;
            }
            anchorInfo.mCoordinate = this.mTempAnchorInfoWrapper.coordinate;
            this.mTempAnchorInfoWrapper.position = -1;
        }
        this.mTempAnchorInfoWrapper.position = anchorInfo.mPosition;
        this.mTempAnchorInfoWrapper.coordinate = anchorInfo.mCoordinate;
        Iterator<LayoutHelper> it = this.mHelperFinder.iterator();
        while (it.hasNext()) {
            it.next().onRefreshLayout(state, this.mTempAnchorInfoWrapper, this);
        }
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        this.mRecyclerView = recyclerView;
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onDetachedFromWindow(RecyclerView recyclerView, RecyclerView.Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        Iterator<LayoutHelper> it = this.mHelperFinder.iterator();
        while (it.hasNext()) {
            it.next().clear(this);
        }
        this.mRecyclerView = null;
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public /* bridge */ /* synthetic */ View onFocusSearchFailed(View view, int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.onFocusSearchFailed(view, i, recycler, state);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        onItemsChanged(recyclerView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsChanged(RecyclerView recyclerView) {
        Iterator<LayoutHelper> it = this.mHelperFinder.iterator();
        while (it.hasNext()) {
            it.next().onItemsChanged(this);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        onItemsChanged(recyclerView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        onItemsChanged(recyclerView);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2) {
        onItemsChanged(recyclerView);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) throws Exception {
        if (Build.VERSION.SDK_INT >= 18) {
            Trace.beginSection(TRACE_LAYOUT);
        }
        if (this.mNoScrolling && state.didStructureChange()) {
            this.mSpaceMeasured = false;
            this.mSpaceMeasuring = true;
        }
        runPreLayout(recycler, state);
        try {
            try {
                super.onLayoutChildren(recycler, state);
                runPostLayout(recycler, state, Integer.MAX_VALUE);
                if ((this.mNestedScrolling || this.mNoScrolling) && this.mSpaceMeasuring) {
                    this.mSpaceMeasured = true;
                    View childAt = getChildAt(getChildCount() - 1);
                    if (childAt != null) {
                        this.mMeasuredFullSpace = getDecoratedBottom(childAt) + ((RecyclerView.LayoutParams) childAt.getLayoutParams()).bottomMargin + computeAlignOffset(childAt, true, false);
                        if (this.mRecyclerView != null && this.mNestedScrolling) {
                            Object parent = this.mRecyclerView.getParent();
                            if (parent instanceof View) {
                                this.mMeasuredFullSpace = Math.min(this.mMeasuredFullSpace, ((View) parent).getMeasuredHeight());
                            }
                        }
                    } else {
                        this.mSpaceMeasuring = false;
                    }
                    this.mSpaceMeasuring = false;
                    if (this.mRecyclerView != null && getItemCount() > 0) {
                        this.mRecyclerView.post(new Runnable() { // from class: com.alibaba.android.vlayout.VirtualLayoutManager.1
                            @Override // java.lang.Runnable
                            public void run() {
                                if (VirtualLayoutManager.this.mRecyclerView != null) {
                                    VirtualLayoutManager.this.mRecyclerView.requestLayout();
                                }
                            }
                        });
                    }
                }
                if (Build.VERSION.SDK_INT >= 18) {
                    Trace.endSection();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        } catch (Throwable th) {
            runPostLayout(recycler, state, Integer.MAX_VALUE);
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:18:0x002f  */
    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2) {
        int measuredHeight;
        if (!this.mNoScrolling && !this.mNestedScrolling) {
            super.onMeasure(recycler, state, i, i2);
            return;
        }
        if (this.mRecyclerView == null || !this.mNestedScrolling) {
            measuredHeight = MAX_NO_SCROLLING_SIZE;
        } else if (this.mMaxMeasureSize > 0) {
            measuredHeight = this.mMaxMeasureSize;
        } else {
            Object parent = this.mRecyclerView.getParent();
            if (parent instanceof View) {
                measuredHeight = ((View) parent).getMeasuredHeight();
            } else {
                measuredHeight = MAX_NO_SCROLLING_SIZE;
            }
        }
        if (this.mSpaceMeasured) {
            measuredHeight = this.mMeasuredFullSpace;
        }
        if (this.mNoScrolling) {
            this.mSpaceMeasuring = !this.mSpaceMeasured;
            if (getChildCount() > 0 || getChildCount() != getItemCount()) {
                View childAt = getChildAt(getChildCount() - 1);
                int iComputeAlignOffset = this.mMeasuredFullSpace;
                if (childAt != null) {
                    iComputeAlignOffset = computeAlignOffset(childAt, true, false) + getDecoratedBottom(childAt) + ((RecyclerView.LayoutParams) childAt.getLayoutParams()).bottomMargin;
                }
                if (getChildCount() != getItemCount() || (childAt != null && iComputeAlignOffset != this.mMeasuredFullSpace)) {
                    this.mSpaceMeasured = false;
                    this.mSpaceMeasuring = true;
                    measuredHeight = MAX_NO_SCROLLING_SIZE;
                }
            } else if (getItemCount() == 0) {
                this.mSpaceMeasured = true;
                this.mSpaceMeasuring = false;
                measuredHeight = 0;
            }
        }
        if (getOrientation() == 1) {
            super.onMeasure(recycler, state, i, View.MeasureSpec.makeMeasureSpec(measuredHeight, Integer.MIN_VALUE));
        } else {
            super.onMeasure(recycler, state, View.MeasureSpec.makeMeasureSpec(measuredHeight, Integer.MIN_VALUE), i2);
        }
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public /* bridge */ /* synthetic */ void onRestoreInstanceState(Parcelable parcelable) {
        super.onRestoreInstanceState(parcelable);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public /* bridge */ /* synthetic */ Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onScrollStateChanged(int i) {
        super.onScrollStateChanged(i);
        int iFindFirstVisibleItemPosition = findFirstVisibleItemPosition();
        int iFindLastVisibleItemPosition = findLastVisibleItemPosition();
        Iterator<LayoutHelper> it = this.mHelperFinder.iterator();
        while (it.hasNext()) {
            it.next().onScrollStateChanged(i, iFindFirstVisibleItemPosition, iFindLastVisibleItemPosition, this);
        }
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx
    protected void recycleChildren(RecyclerView.Recycler recycler, int i, int i2) {
        if (i == i2) {
            return;
        }
        if (sDebuggable) {
            Log.d(TAG, "Recycling " + Math.abs(i - i2) + " items");
        }
        if (i2 <= i) {
            View childAt = getChildAt(i);
            int position = getPosition(getChildAt(i2 + 1));
            int position2 = getPosition(childAt);
            while (i > i2) {
                int position3 = getPosition(getChildAt(i));
                if (position3 != -1) {
                    LayoutHelper layoutHelper = this.mHelperFinder.getLayoutHelper(position3);
                    if (layoutHelper == null || layoutHelper.isRecyclable(position3, position, position2, this, false)) {
                        removeAndRecycleViewAt(i, recycler);
                    }
                } else {
                    removeAndRecycleViewAt(i, recycler);
                }
                i--;
            }
            return;
        }
        View childAt2 = getChildAt(i2 - 1);
        int position4 = getPosition(getChildAt(i));
        int position5 = getPosition(childAt2);
        int i3 = i;
        while (i < i2) {
            int position6 = getPosition(getChildAt(i3));
            if (position6 != -1) {
                LayoutHelper layoutHelper2 = this.mHelperFinder.getLayoutHelper(position6);
                if (layoutHelper2 == null || layoutHelper2.isRecyclable(position6, position4, position5, this, true)) {
                    removeAndRecycleViewAt(i3, recycler);
                } else {
                    i3++;
                }
            } else {
                removeAndRecycleViewAt(i3, recycler);
            }
            i++;
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void recycleView(View view) {
        if (this.mRecyclerView != null) {
            this.mRecyclerView.getRecycledViewPool().putRecycledView(this.mRecyclerView.getChildViewHolder(view));
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutManagerHelper
    public void removeChildView(View view) {
        removeView(view);
    }

    public void runAdjustLayout() throws Exception {
        int iFindFirstVisibleItemPosition = findFirstVisibleItemPosition();
        LayoutHelper layoutHelper = this.mHelperFinder.getLayoutHelper(iFindFirstVisibleItemPosition);
        int iFindLastVisibleItemPosition = findLastVisibleItemPosition();
        LayoutHelper layoutHelper2 = this.mHelperFinder.getLayoutHelper(iFindLastVisibleItemPosition);
        List<LayoutHelper> layoutHelpers = this.mHelperFinder.getLayoutHelpers();
        int iIndexOf = layoutHelpers.indexOf(layoutHelper2);
        for (int iIndexOf2 = layoutHelpers.indexOf(layoutHelper); iIndexOf2 <= iIndexOf; iIndexOf2++) {
            try {
                layoutHelpers.get(iIndexOf2).adjustLayout(iFindFirstVisibleItemPosition, iFindLastVisibleItemPosition, this);
            } catch (Exception e) {
                if (sDebuggable) {
                    throw e;
                }
            }
        }
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public /* bridge */ /* synthetic */ int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollHorizontallyBy(i, recycler, state);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx
    protected int scrollInternalBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) throws Exception {
        if (Build.VERSION.SDK_INT >= 18) {
            Trace.beginSection(TRACE_SCROLL);
        }
        runPreLayout(recycler, state);
        int i2 = 0;
        try {
            try {
                if (this.mNoScrolling) {
                    if (getChildCount() != 0 && i != 0) {
                        this.mLayoutState.mRecycle = true;
                        ensureLayoutStateExpose();
                        int i3 = i > 0 ? 1 : -1;
                        int iAbs = Math.abs(i);
                        updateLayoutStateExpose(i3, iAbs, true, state);
                        int iFill = this.mLayoutState.mScrollingOffset + fill(recycler, this.mLayoutState, state, false);
                        if (iFill < 0) {
                            runPostLayout(recycler, state, 0);
                            return 0;
                        }
                        if (iAbs > iFill) {
                            i = i3 * iFill;
                        }
                    }
                    runPostLayout(recycler, state, 0);
                    return 0;
                }
                i = super.scrollInternalBy(i, recycler, state);
                i2 = i;
            } catch (Exception e) {
                Log.w(TAG, Log.getStackTraceString(e), e);
                if (sDebuggable) {
                    throw e;
                }
            }
            runPostLayout(recycler, state, i2);
            if (Build.VERSION.SDK_INT >= 18) {
                Trace.endSection();
            }
            return i2;
        } catch (Throwable th) {
            runPostLayout(recycler, state, 0);
            throw th;
        }
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void scrollToPosition(int i) {
        super.scrollToPosition(i);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager
    public void scrollToPositionWithOffset(int i, int i2) {
        super.scrollToPositionWithOffset(i, i2);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public /* bridge */ /* synthetic */ int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return super.scrollVerticallyBy(i, recycler, state);
    }

    public void setEnableMarginOverlapping(boolean z) {
        this.mEnableMarginOverlapping = z;
    }

    public void setFixOffset(int i, int i2, int i3, int i4) {
        this.mFixAreaAdjustor = new FixAreaAdjuster(i, i2, i3, i4);
    }

    public void setHelperFinder(@NonNull LayoutHelperFinder layoutHelperFinder) {
        if (layoutHelperFinder == null) {
            throw new IllegalArgumentException("finder is null");
        }
        LinkedList linkedList = new LinkedList();
        if (this.mHelperFinder != null) {
            Iterator<LayoutHelper> it = this.mHelperFinder.iterator();
            while (it.hasNext()) {
                linkedList.add(it.next());
            }
        }
        this.mHelperFinder = layoutHelperFinder;
        if (linkedList.size() > 0) {
            this.mHelperFinder.setLayouts(linkedList);
        }
        this.mSpaceMeasured = false;
        requestLayout();
    }

    public void setLayoutHelpers(@Nullable List<LayoutHelper> list) {
        for (LayoutHelper layoutHelper : this.mHelperFinder) {
            this.oldHelpersSet.put(Integer.valueOf(System.identityHashCode(layoutHelper)), layoutHelper);
        }
        if (list != null) {
            int itemCount = 0;
            for (int i = 0; i < list.size(); i++) {
                LayoutHelper layoutHelper2 = list.get(i);
                if (layoutHelper2 instanceof FixAreaLayoutHelper) {
                    ((FixAreaLayoutHelper) layoutHelper2).setAdjuster(this.mFixAreaAdjustor);
                }
                if ((layoutHelper2 instanceof BaseLayoutHelper) && this.mLayoutViewBindListener != null) {
                    ((BaseLayoutHelper) layoutHelper2).setLayoutViewBindListener(this.mLayoutViewBindListener);
                }
                if (layoutHelper2.getItemCount() > 0) {
                    layoutHelper2.setRange(itemCount, (layoutHelper2.getItemCount() + itemCount) - 1);
                } else {
                    layoutHelper2.setRange(-1, -1);
                }
                itemCount += layoutHelper2.getItemCount();
            }
        }
        this.mHelperFinder.setLayouts(list);
        for (LayoutHelper layoutHelper3 : this.mHelperFinder) {
            this.newHelpersSet.put(Integer.valueOf(System.identityHashCode(layoutHelper3)), layoutHelper3);
        }
        Iterator<Map.Entry<Integer, LayoutHelper>> it = this.oldHelpersSet.entrySet().iterator();
        while (it.hasNext()) {
            Integer key = it.next().getKey();
            if (this.newHelpersSet.containsKey(key)) {
                this.newHelpersSet.remove(key);
                it.remove();
            }
        }
        Iterator<LayoutHelper> it2 = this.oldHelpersSet.values().iterator();
        while (it2.hasNext()) {
            it2.next().clear(this);
        }
        if (!this.oldHelpersSet.isEmpty() || !this.newHelpersSet.isEmpty()) {
            this.mSpaceMeasured = false;
        }
        this.oldHelpersSet.clear();
        this.newHelpersSet.clear();
        requestLayout();
    }

    public void setLayoutViewFactory(@NonNull LayoutViewFactory layoutViewFactory) {
        if (layoutViewFactory == null) {
            throw new IllegalArgumentException("factory should not be null");
        }
        this.mLayoutViewFatory = layoutViewFactory;
    }

    public void setNestedScrolling(boolean z) {
        setNestedScrolling(z, -1);
    }

    public void setNestedScrolling(boolean z, int i) {
        this.mNestedScrolling = z;
        this.mSpaceMeasured = false;
        this.mSpaceMeasuring = false;
        this.mMeasuredFullSpace = 0;
    }

    public void setNoScrolling(boolean z) {
        this.mNoScrolling = z;
        this.mSpaceMeasured = false;
        this.mMeasuredFullSpace = 0;
        this.mSpaceMeasuring = false;
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager
    public void setOrientation(int i) {
        this.mOrientationHelper = OrientationHelperEx.createOrientationHelper(this, i);
        super.setOrientation(i);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx
    public /* bridge */ /* synthetic */ void setRecycleOffset(int i) {
        super.setRecycleOffset(i);
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager
    public void setReverseLayout(boolean z) {
        if (z) {
            throw new UnsupportedOperationException("VirtualLayoutManager does not support reverse layout in current version.");
        }
        super.setReverseLayout(false);
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager
    public void setStackFromEnd(boolean z) {
        if (z) {
            throw new UnsupportedOperationException("VirtualLayoutManager does not support stack from end.");
        }
        super.setStackFromEnd(false);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, com.alibaba.android.vlayout.LayoutManagerHelper
    public void showView(View view) {
        super.showView(view);
    }

    @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
        super.smoothScrollToPosition(recyclerView, state, i);
    }

    @Override // com.alibaba.android.vlayout.ExposeLinearLayoutManagerEx, androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean supportsPredictiveItemAnimations() {
        return this.mCurrentPendingSavedState == null;
    }
}
