package com.alibaba.android.vlayout.layout;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.OrientationHelperEx;
import com.alibaba.android.vlayout.R;
import com.alibaba.android.vlayout.VirtualLayoutManager;

/* JADX INFO: loaded from: classes3.dex */
public abstract class BaseLayoutHelper extends MarginLayoutHelper {
    public static boolean DEBUG = false;
    private static final String TAG = "BaseLayoutHelper";
    int mBgColor;
    View mLayoutView;
    private LayoutViewBindListener mLayoutViewBindListener;
    private LayoutViewUnBindListener mLayoutViewUnBindListener;
    protected Rect mLayoutRegion = new Rect();
    float mAspectRatio = Float.NaN;
    private int mItemCount = 0;

    public static class DefaultLayoutViewHelper implements LayoutViewBindListener, LayoutViewUnBindListener, LayoutViewHelper {
        private final LayoutViewBindListener mLayoutViewBindListener;
        private final LayoutViewUnBindListener mLayoutViewUnBindListener;

        public DefaultLayoutViewHelper(LayoutViewBindListener layoutViewBindListener, LayoutViewUnBindListener layoutViewUnBindListener) {
            this.mLayoutViewBindListener = layoutViewBindListener;
            this.mLayoutViewUnBindListener = layoutViewUnBindListener;
        }

        @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper.LayoutViewBindListener
        public void onBind(View view, BaseLayoutHelper baseLayoutHelper) {
            if (view.getTag(R.id.tag_layout_helper_bg) != null || this.mLayoutViewBindListener == null) {
                return;
            }
            this.mLayoutViewBindListener.onBind(view, baseLayoutHelper);
        }

        @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper.LayoutViewHelper
        public void onBindViewSuccess(View view, String str) {
            view.setTag(R.id.tag_layout_helper_bg, str);
        }

        @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper.LayoutViewUnBindListener
        public void onUnbind(View view, BaseLayoutHelper baseLayoutHelper) {
            if (this.mLayoutViewUnBindListener != null) {
                this.mLayoutViewUnBindListener.onUnbind(view, baseLayoutHelper);
            }
            view.setTag(R.id.tag_layout_helper_bg, null);
        }
    }

    public interface LayoutViewBindListener {
        void onBind(View view, BaseLayoutHelper baseLayoutHelper);
    }

    public interface LayoutViewHelper {
        void onBindViewSuccess(View view, String str);
    }

    public interface LayoutViewUnBindListener {
        void onUnbind(View view, BaseLayoutHelper baseLayoutHelper);
    }

    private int calGap(int i, int i2) {
        if (i < i2) {
            return i2 - i;
        }
        return 0;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void adjustLayout(int i, int i2, LayoutManagerHelper layoutManagerHelper) {
        if (requireLayoutView()) {
            Rect rect = new Rect();
            OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
            for (int i3 = 0; i3 < layoutManagerHelper.getChildCount(); i3++) {
                View childAt = layoutManagerHelper.getChildAt(i3);
                if (getRange().contains(Integer.valueOf(layoutManagerHelper.getPosition(childAt)))) {
                    if (childAt.getVisibility() == 8) {
                        rect.setEmpty();
                    } else {
                        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) childAt.getLayoutParams();
                        if (layoutManagerHelper.getOrientation() == 1) {
                            rect.union(layoutManagerHelper.getDecoratedLeft(childAt) - layoutParams.leftMargin, mainOrientationHelper.getDecoratedStart(childAt), layoutManagerHelper.getDecoratedRight(childAt) + layoutParams.rightMargin, mainOrientationHelper.getDecoratedEnd(childAt));
                        } else {
                            rect.union(mainOrientationHelper.getDecoratedStart(childAt), layoutManagerHelper.getDecoratedTop(childAt) - layoutParams.topMargin, mainOrientationHelper.getDecoratedEnd(childAt), layoutManagerHelper.getDecoratedBottom(childAt) + layoutParams.bottomMargin);
                        }
                    }
                }
            }
            if (rect.isEmpty()) {
                this.mLayoutRegion.setEmpty();
            } else {
                this.mLayoutRegion.set(rect.left - this.mPaddingLeft, rect.top - this.mPaddingTop, rect.right + this.mPaddingRight, rect.bottom + this.mPaddingBottom);
            }
            if (this.mLayoutView != null) {
                this.mLayoutView.layout(this.mLayoutRegion.left, this.mLayoutRegion.top, this.mLayoutRegion.right, this.mLayoutRegion.bottom);
            }
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void afterLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper) {
        if (DEBUG) {
            Log.d(TAG, "call afterLayout() on " + getClass().getSimpleName());
        }
        if (requireLayoutView()) {
            if (isValidScrolled(i3) && this.mLayoutView != null) {
                this.mLayoutRegion.union(this.mLayoutView.getLeft(), this.mLayoutView.getTop(), this.mLayoutView.getRight(), this.mLayoutView.getBottom());
            }
            if (!this.mLayoutRegion.isEmpty()) {
                if (isValidScrolled(i3)) {
                    if (layoutManagerHelper.getOrientation() == 1) {
                        this.mLayoutRegion.offset(0, -i3);
                    } else {
                        this.mLayoutRegion.offset(-i3, 0);
                    }
                }
                int contentWidth = layoutManagerHelper.getContentWidth();
                int contentHeight = layoutManagerHelper.getContentHeight();
                if (layoutManagerHelper.getOrientation() != 1 ? this.mLayoutRegion.intersects((-contentWidth) / 4, 0, contentWidth + (contentWidth / 4), contentHeight) : this.mLayoutRegion.intersects(0, (-contentHeight) / 4, contentWidth, contentHeight + (contentHeight / 4))) {
                    if (this.mLayoutView == null) {
                        this.mLayoutView = layoutManagerHelper.generateLayoutView();
                        layoutManagerHelper.addOffFlowView(this.mLayoutView, true);
                    }
                    if (layoutManagerHelper.getOrientation() == 1) {
                        this.mLayoutRegion.left = layoutManagerHelper.getPaddingLeft() + this.mMarginLeft;
                        this.mLayoutRegion.right = (layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingRight()) - this.mMarginRight;
                    } else {
                        this.mLayoutRegion.top = layoutManagerHelper.getPaddingTop() + this.mMarginTop;
                        this.mLayoutRegion.bottom = (layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingBottom()) - this.mMarginBottom;
                    }
                    bindLayoutView(this.mLayoutView);
                    return;
                }
                this.mLayoutRegion.set(0, 0, 0, 0);
                if (this.mLayoutView != null) {
                    this.mLayoutView.layout(0, 0, 0, 0);
                }
            }
        }
        if (this.mLayoutView != null) {
            if (this.mLayoutViewUnBindListener != null) {
                this.mLayoutViewUnBindListener.onUnbind(this.mLayoutView, this);
            }
            layoutManagerHelper.removeChildView(this.mLayoutView);
            this.mLayoutView = null;
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void beforeLayout(RecyclerView.Recycler recycler, RecyclerView.State state, LayoutManagerHelper layoutManagerHelper) {
        if (DEBUG) {
            Log.d(TAG, "call beforeLayout() on " + getClass().getSimpleName());
        }
        if (requireLayoutView()) {
            View view = this.mLayoutView;
        } else if (this.mLayoutView != null) {
            if (this.mLayoutViewUnBindListener != null) {
                this.mLayoutViewUnBindListener.onUnbind(this.mLayoutView, this);
            }
            layoutManagerHelper.removeChildView(this.mLayoutView);
            this.mLayoutView = null;
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void bindLayoutView(@NonNull View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(this.mLayoutRegion.width(), 1073741824), View.MeasureSpec.makeMeasureSpec(this.mLayoutRegion.height(), 1073741824));
        view.layout(this.mLayoutRegion.left, this.mLayoutRegion.top, this.mLayoutRegion.right, this.mLayoutRegion.bottom);
        view.setBackgroundColor(this.mBgColor);
        if (this.mLayoutViewBindListener != null) {
            this.mLayoutViewBindListener.onBind(view, this);
        }
        this.mLayoutRegion.set(0, 0, 0, 0);
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public final void clear(LayoutManagerHelper layoutManagerHelper) {
        if (this.mLayoutView != null) {
            if (this.mLayoutViewUnBindListener != null) {
                this.mLayoutViewUnBindListener.onUnbind(this.mLayoutView, this);
            }
            layoutManagerHelper.removeChildView(this.mLayoutView);
            this.mLayoutView = null;
        }
        onClear(layoutManagerHelper);
    }

    protected int computeEndSpace(LayoutManagerHelper layoutManagerHelper, boolean z, boolean z2, boolean z3) {
        int i;
        int i2;
        if (z) {
            i = this.mMarginBottom;
            i2 = this.mPaddingBottom;
        } else {
            i = this.mMarginLeft;
            i2 = this.mPaddingLeft;
        }
        return i + i2;
    }

    protected int computeStartSpace(LayoutManagerHelper layoutManagerHelper, boolean z, boolean z2, boolean z3) {
        int i;
        int i2;
        int iCalGap;
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        MarginLayoutHelper marginLayoutHelper = null;
        LayoutHelper layoutHelperFindNeighbourNonfixLayoutHelper = layoutManagerHelper instanceof VirtualLayoutManager ? ((VirtualLayoutManager) layoutManagerHelper).findNeighbourNonfixLayoutHelper(this, z2) : null;
        if (layoutHelperFindNeighbourNonfixLayoutHelper != null && (layoutHelperFindNeighbourNonfixLayoutHelper instanceof MarginLayoutHelper)) {
            marginLayoutHelper = (MarginLayoutHelper) layoutHelperFindNeighbourNonfixLayoutHelper;
        }
        if (layoutHelperFindNeighbourNonfixLayoutHelper == this) {
            return 0;
        }
        if (!z3) {
            if (z) {
                i8 = this.mMarginTop;
                i9 = this.mPaddingTop;
            } else {
                i8 = this.mMarginLeft;
                i9 = this.mPaddingLeft;
            }
            return i8 + i9;
        }
        if (marginLayoutHelper == null) {
            if (z) {
                i6 = this.mMarginTop;
                i7 = this.mPaddingTop;
            } else {
                i6 = this.mMarginLeft;
                i7 = this.mPaddingLeft;
            }
            iCalGap = i6 + i7;
        } else if (z) {
            if (z2) {
                i3 = marginLayoutHelper.mMarginBottom;
                i4 = this.mMarginTop;
            } else {
                i3 = marginLayoutHelper.mMarginTop;
                i4 = this.mMarginBottom;
            }
            iCalGap = calGap(i3, i4);
        } else {
            if (z2) {
                i = marginLayoutHelper.mMarginRight;
                i2 = this.mMarginLeft;
            } else {
                i = marginLayoutHelper.mMarginLeft;
                i2 = this.mMarginRight;
            }
            iCalGap = calGap(i, i2);
        }
        if (z) {
            i5 = z2 ? this.mPaddingTop : this.mPaddingBottom;
        } else {
            i5 = z2 ? this.mPaddingLeft : this.mPaddingRight;
        }
        return iCalGap + i5 + 0;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void doLayout(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        layoutViews(recycler, state, layoutStateWrapper, layoutChunkResult, layoutManagerHelper);
    }

    public float getAspectRatio() {
        return this.mAspectRatio;
    }

    public int getBgColor() {
        return this.mBgColor;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public int getItemCount() {
        return this.mItemCount;
    }

    protected void handleStateOnResult(LayoutChunkResult layoutChunkResult, View view) {
        if (view == null) {
            return;
        }
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        boolean z = true;
        if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
            layoutChunkResult.mIgnoreConsumed = true;
        }
        if (!layoutChunkResult.mFocusable && !view.isFocusable()) {
            z = false;
        }
        layoutChunkResult.mFocusable = z;
    }

    protected void handleStateOnResult(LayoutChunkResult layoutChunkResult, View... viewArr) {
        if (viewArr == null) {
            return;
        }
        for (View view : viewArr) {
            if (view != null) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
                boolean z = true;
                if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                    layoutChunkResult.mIgnoreConsumed = true;
                }
                if (!layoutChunkResult.mFocusable && !view.isFocusable()) {
                    z = false;
                }
                layoutChunkResult.mFocusable = z;
                if (layoutChunkResult.mFocusable && layoutChunkResult.mIgnoreConsumed) {
                    return;
                }
            }
        }
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public boolean isFixLayout() {
        return false;
    }

    protected boolean isValidScrolled(int i) {
        return (i == Integer.MAX_VALUE || i == Integer.MIN_VALUE) ? false : true;
    }

    protected void layoutChild(View view, int i, int i2, int i3, int i4, @NonNull LayoutManagerHelper layoutManagerHelper) {
        layoutChild(view, i, i2, i3, i4, layoutManagerHelper, false);
    }

    protected void layoutChild(View view, int i, int i2, int i3, int i4, @NonNull LayoutManagerHelper layoutManagerHelper, boolean z) {
        layoutManagerHelper.layoutChild(view, i, i2, i3, i4);
        if (requireLayoutView()) {
            if (z) {
                this.mLayoutRegion.union((i - this.mPaddingLeft) - this.mMarginLeft, (i2 - this.mPaddingTop) - this.mMarginTop, i3 + this.mPaddingRight + this.mMarginRight, i4 + this.mPaddingBottom + this.mMarginBottom);
            } else {
                this.mLayoutRegion.union(i - this.mPaddingLeft, i2 - this.mPaddingTop, i3 + this.mPaddingRight, i4 + this.mPaddingBottom);
            }
        }
    }

    protected void layoutChildWithMargin(View view, int i, int i2, int i3, int i4, @NonNull LayoutManagerHelper layoutManagerHelper) {
        layoutChildWithMargin(view, i, i2, i3, i4, layoutManagerHelper, false);
    }

    protected void layoutChildWithMargin(View view, int i, int i2, int i3, int i4, @NonNull LayoutManagerHelper layoutManagerHelper, boolean z) {
        layoutManagerHelper.layoutChildWithMargins(view, i, i2, i3, i4);
        if (requireLayoutView()) {
            if (z) {
                this.mLayoutRegion.union((i - this.mPaddingLeft) - this.mMarginLeft, (i2 - this.mPaddingTop) - this.mMarginTop, i3 + this.mPaddingRight + this.mMarginRight, i4 + this.mPaddingBottom + this.mMarginBottom);
            } else {
                this.mLayoutRegion.union(i - this.mPaddingLeft, i2 - this.mPaddingTop, i3 + this.mPaddingRight, i4 + this.mPaddingBottom);
            }
        }
    }

    public abstract void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper);

    @Nullable
    public final View nextView(RecyclerView.Recycler recycler, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutManagerHelper layoutManagerHelper, LayoutChunkResult layoutChunkResult) {
        View next = layoutStateWrapper.next(recycler);
        if (next != null) {
            layoutManagerHelper.addChildView(layoutStateWrapper, next);
            return next;
        }
        if (DEBUG && !layoutStateWrapper.hasScrapList()) {
            throw new RuntimeException("received null view when unexpected");
        }
        layoutChunkResult.mFinished = true;
        return null;
    }

    protected void onClear(LayoutManagerHelper layoutManagerHelper) {
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public boolean requireLayoutView() {
        return (this.mBgColor == 0 && this.mLayoutViewBindListener == null) ? false : true;
    }

    public void setAspectRatio(float f) {
        this.mAspectRatio = f;
    }

    public void setBgColor(int i) {
        this.mBgColor = i;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void setItemCount(int i) {
        this.mItemCount = i;
    }

    public void setLayoutViewBindListener(LayoutViewBindListener layoutViewBindListener) {
        this.mLayoutViewBindListener = layoutViewBindListener;
    }

    public void setLayoutViewHelper(DefaultLayoutViewHelper defaultLayoutViewHelper) {
        this.mLayoutViewBindListener = defaultLayoutViewHelper;
        this.mLayoutViewUnBindListener = defaultLayoutViewHelper;
    }

    public void setLayoutViewUnBindListener(LayoutViewUnBindListener layoutViewUnBindListener) {
        this.mLayoutViewUnBindListener = layoutViewUnBindListener;
    }
}
