package com.alibaba.android.vlayout.layout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.OrientationHelperEx;
import com.alibaba.android.vlayout.VirtualLayoutManager;

/* JADX INFO: loaded from: classes3.dex */
public class FixLayoutHelper extends FixAreaLayoutHelper {
    public static final int BOTTOM_LEFT = 2;
    public static final int BOTTOM_RIGHT = 3;
    private static final String TAG = "FixLayoutHelper";
    public static final int TOP_LEFT = 0;
    public static final int TOP_RIGHT = 1;
    private boolean isAddFixViewImmediately;
    private boolean isRemoveFixViewImmediately;
    private int mAlignType;
    protected boolean mDoNormalHandle;
    protected View mFixView;
    private FixViewAppearAnimatorListener mFixViewAppearAnimatorListener;
    private FixViewDisappearAnimatorListener mFixViewDisappearAnimatorListener;
    private int mPos;
    private boolean mShouldDrawn;
    private boolean mSketchMeasure;
    protected int mX;
    protected int mY;

    private static class FixViewAppearAnimatorListener extends AnimatorListenerAdapter {
        private View mFixView;
        private LayoutManagerHelper mLayoutManagerHelper;

        private FixViewAppearAnimatorListener() {
        }

        public void bindAction(LayoutManagerHelper layoutManagerHelper, View view) {
            this.mLayoutManagerHelper = layoutManagerHelper;
            this.mFixView = view;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            this.mFixView.setVisibility(0);
        }
    }

    private static class FixViewDisappearAnimatorListener extends AnimatorListenerAdapter {
        private boolean isAnimating;
        private Runnable mEndAction;
        private View mFixView;
        private LayoutManagerHelper mLayoutManagerHelper;
        private RecyclerView.Recycler mRecycler;

        private FixViewDisappearAnimatorListener() {
        }

        public void bindAction(RecyclerView.Recycler recycler, LayoutManagerHelper layoutManagerHelper, View view) {
            this.isAnimating = true;
            this.mRecycler = recycler;
            this.mLayoutManagerHelper = layoutManagerHelper;
            this.mFixView = view;
        }

        public boolean isAnimating() {
            return this.isAnimating;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            this.mLayoutManagerHelper.removeChildView(this.mFixView);
            this.mRecycler.recycleView(this.mFixView);
            this.isAnimating = false;
            if (this.mEndAction != null) {
                this.mEndAction.run();
                this.mEndAction = null;
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }

        public void withEndAction(Runnable runnable) {
            this.mEndAction = runnable;
        }
    }

    public FixLayoutHelper(int i, int i2) {
        this(0, i, i2);
    }

    public FixLayoutHelper(int i, int i2, int i3) {
        this.mPos = -1;
        this.mAlignType = 0;
        this.mX = 0;
        this.mY = 0;
        this.mSketchMeasure = false;
        this.mFixView = null;
        this.mDoNormalHandle = false;
        this.mShouldDrawn = true;
        this.isAddFixViewImmediately = false;
        this.isRemoveFixViewImmediately = true;
        this.mFixViewAppearAnimatorListener = new FixViewAppearAnimatorListener();
        this.mFixViewDisappearAnimatorListener = new FixViewDisappearAnimatorListener();
        this.mAlignType = i;
        this.mX = i2;
        this.mY = i3;
        setItemCount(1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addFixViewWithAnimator(LayoutManagerHelper layoutManagerHelper, View view) {
        ViewPropertyAnimator viewPropertyAnimatorOnGetFixViewAppearAnimator;
        if (this.mFixViewAnimatorHelper == null || (viewPropertyAnimatorOnGetFixViewAppearAnimator = this.mFixViewAnimatorHelper.onGetFixViewAppearAnimator(view)) == null) {
            layoutManagerHelper.addFixedView(view);
        } else {
            view.setVisibility(4);
            layoutManagerHelper.addFixedView(view);
            this.mFixViewAppearAnimatorListener.bindAction(layoutManagerHelper, view);
            viewPropertyAnimatorOnGetFixViewAppearAnimator.setListener(this.mFixViewAppearAnimatorListener).start();
        }
        this.isRemoveFixViewImmediately = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void doMeasureAndLayout(View view, LayoutManagerHelper layoutManagerHelper) {
        int i;
        int childMeasureSpec;
        int i2;
        int decoratedMeasurement;
        int i3;
        int i4;
        int contentWidth;
        int contentHeight;
        int measuredWidth;
        int measuredHeight;
        int i5;
        int childMeasureSpec2;
        if (view == null || layoutManagerHelper == null) {
            return;
        }
        VirtualLayoutManager.LayoutParams layoutParams = (VirtualLayoutManager.LayoutParams) view.getLayoutParams();
        OrientationHelperEx mainOrientationHelper = layoutManagerHelper.getMainOrientationHelper();
        boolean z = layoutManagerHelper.getOrientation() == 1;
        int i6 = -1;
        if (z) {
            int contentWidth2 = (layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingLeft()) - layoutManagerHelper.getPaddingRight();
            if (layoutParams.width >= 0) {
                i5 = layoutParams.width;
            } else {
                i5 = (this.mSketchMeasure && z) ? -1 : -2;
            }
            int childMeasureSpec3 = layoutManagerHelper.getChildMeasureSpec(contentWidth2, i5, false);
            if (!Float.isNaN(layoutParams.mAspectRatio) && layoutParams.mAspectRatio > 0.0f) {
                childMeasureSpec2 = layoutManagerHelper.getChildMeasureSpec((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom(), (int) ((View.MeasureSpec.getSize(childMeasureSpec3) / layoutParams.mAspectRatio) + 0.5f), false);
            } else if (Float.isNaN(this.mAspectRatio) || this.mAspectRatio <= 0.0f) {
                int contentHeight2 = (layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom();
                if (layoutParams.height >= 0) {
                    i6 = layoutParams.height;
                } else if (!this.mSketchMeasure || z) {
                    i6 = -2;
                }
                childMeasureSpec2 = layoutManagerHelper.getChildMeasureSpec(contentHeight2, i6, false);
            } else {
                childMeasureSpec2 = layoutManagerHelper.getChildMeasureSpec((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom(), (int) ((View.MeasureSpec.getSize(childMeasureSpec3) / this.mAspectRatio) + 0.5f), false);
            }
            layoutManagerHelper.measureChildWithMargins(view, childMeasureSpec3, childMeasureSpec2);
        } else {
            int contentHeight3 = (layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingTop()) - layoutManagerHelper.getPaddingBottom();
            if (layoutParams.height >= 0) {
                i = layoutParams.height;
            } else {
                i = (!this.mSketchMeasure || z) ? -2 : -1;
            }
            int childMeasureSpec4 = layoutManagerHelper.getChildMeasureSpec(contentHeight3, i, false);
            if (!Float.isNaN(layoutParams.mAspectRatio) && layoutParams.mAspectRatio > 0.0f) {
                childMeasureSpec = layoutManagerHelper.getChildMeasureSpec((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingLeft()) - layoutManagerHelper.getPaddingRight(), (int) ((View.MeasureSpec.getSize(childMeasureSpec4) * layoutParams.mAspectRatio) + 0.5f), false);
            } else if (Float.isNaN(this.mAspectRatio) || this.mAspectRatio <= 0.0f) {
                int contentWidth3 = (layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingLeft()) - layoutManagerHelper.getPaddingRight();
                if (layoutParams.width >= 0) {
                    i6 = layoutParams.width;
                } else if (!this.mSketchMeasure || !z) {
                    i6 = -2;
                }
                childMeasureSpec = layoutManagerHelper.getChildMeasureSpec(contentWidth3, i6, false);
            } else {
                childMeasureSpec = layoutManagerHelper.getChildMeasureSpec((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingLeft()) - layoutManagerHelper.getPaddingRight(), (int) ((View.MeasureSpec.getSize(childMeasureSpec4) * this.mAspectRatio) + 0.5f), false);
            }
            layoutManagerHelper.measureChildWithMargins(view, childMeasureSpec, childMeasureSpec4);
        }
        if (this.mAlignType == 1) {
            measuredHeight = layoutManagerHelper.getPaddingTop() + this.mY + this.mAdjuster.top;
            contentWidth = ((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingRight()) - this.mX) - this.mAdjuster.right;
            measuredWidth = ((contentWidth - layoutParams.leftMargin) - layoutParams.rightMargin) - view.getMeasuredWidth();
            contentHeight = layoutParams.topMargin + measuredHeight + layoutParams.bottomMargin + view.getMeasuredHeight();
        } else {
            if (this.mAlignType != 2) {
                if (this.mAlignType == 3) {
                    contentWidth = ((layoutManagerHelper.getContentWidth() - layoutManagerHelper.getPaddingRight()) - this.mX) - this.mAdjuster.right;
                    contentHeight = ((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingBottom()) - this.mY) - this.mAdjuster.bottom;
                    measuredWidth = ((contentWidth - layoutParams.leftMargin) - layoutParams.rightMargin) - view.getMeasuredWidth();
                    measuredHeight = ((contentHeight - view.getMeasuredHeight()) - layoutParams.topMargin) - layoutParams.bottomMargin;
                } else {
                    int paddingLeft = this.mAdjuster.left + layoutManagerHelper.getPaddingLeft() + this.mX;
                    int paddingTop = layoutManagerHelper.getPaddingTop() + this.mY + this.mAdjuster.top;
                    int decoratedMeasurementInOther = (z ? mainOrientationHelper.getDecoratedMeasurementInOther(view) : mainOrientationHelper.getDecoratedMeasurement(view)) + paddingLeft;
                    i2 = paddingTop;
                    decoratedMeasurement = (z ? mainOrientationHelper.getDecoratedMeasurement(view) : mainOrientationHelper.getDecoratedMeasurementInOther(view)) + paddingTop;
                    i3 = paddingLeft;
                    i4 = decoratedMeasurementInOther;
                }
                layoutChildWithMargin(view, i3, i2, i4, decoratedMeasurement, layoutManagerHelper);
            }
            measuredWidth = layoutManagerHelper.getPaddingLeft() + this.mX + this.mAdjuster.left;
            contentHeight = ((layoutManagerHelper.getContentHeight() - layoutManagerHelper.getPaddingBottom()) - this.mY) - this.mAdjuster.bottom;
            contentWidth = view.getMeasuredWidth() + layoutParams.leftMargin + measuredWidth + layoutParams.rightMargin;
            measuredHeight = ((contentHeight - view.getMeasuredHeight()) - layoutParams.topMargin) - layoutParams.bottomMargin;
        }
        i2 = measuredHeight;
        i4 = contentWidth;
        i3 = measuredWidth;
        decoratedMeasurement = contentHeight;
        layoutChildWithMargin(view, i3, i2, i4, decoratedMeasurement, layoutManagerHelper);
    }

    private void removeFixViewWithAnimator(RecyclerView.Recycler recycler, LayoutManagerHelper layoutManagerHelper, View view) {
        if (this.isRemoveFixViewImmediately || this.mFixViewAnimatorHelper == null) {
            layoutManagerHelper.removeChildView(view);
            recycler.recycleView(view);
            this.isAddFixViewImmediately = false;
            return;
        }
        ViewPropertyAnimator viewPropertyAnimatorOnGetFixViewDisappearAnimator = this.mFixViewAnimatorHelper.onGetFixViewDisappearAnimator(view);
        if (viewPropertyAnimatorOnGetFixViewDisappearAnimator != null) {
            this.mFixViewDisappearAnimatorListener.bindAction(recycler, layoutManagerHelper, view);
            viewPropertyAnimatorOnGetFixViewDisappearAnimator.setListener(this.mFixViewDisappearAnimatorListener).start();
            this.isAddFixViewImmediately = false;
        } else {
            layoutManagerHelper.removeChildView(view);
            recycler.recycleView(view);
            this.isAddFixViewImmediately = false;
        }
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void afterLayout(final RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, int i3, final LayoutManagerHelper layoutManagerHelper) {
        super.afterLayout(recycler, state, i, i2, i3, layoutManagerHelper);
        if (this.mPos < 0) {
            return;
        }
        if (this.mDoNormalHandle && state.isPreLayout()) {
            if (this.mFixView != null) {
                layoutManagerHelper.removeChildView(this.mFixView);
                recycler.recycleView(this.mFixView);
                this.isAddFixViewImmediately = false;
            }
            this.mFixView = null;
            return;
        }
        if (!shouldBeDraw(layoutManagerHelper, i, i2, i3)) {
            this.mShouldDrawn = false;
            if (this.mFixView != null) {
                removeFixViewWithAnimator(recycler, layoutManagerHelper, this.mFixView);
                this.mFixView = null;
                return;
            }
            return;
        }
        this.mShouldDrawn = true;
        if (this.mFixView != null) {
            if (this.mFixView.getParent() == null) {
                addFixViewWithAnimator(layoutManagerHelper, this.mFixView);
                return;
            } else {
                layoutManagerHelper.addFixedView(this.mFixView);
                this.isRemoveFixViewImmediately = false;
                return;
            }
        }
        Runnable runnable = new Runnable() { // from class: com.alibaba.android.vlayout.layout.FixLayoutHelper.1
            @Override // java.lang.Runnable
            public void run() {
                FixLayoutHelper.this.mFixView = recycler.getViewForPosition(FixLayoutHelper.this.mPos);
                FixLayoutHelper.this.doMeasureAndLayout(FixLayoutHelper.this.mFixView, layoutManagerHelper);
                if (!FixLayoutHelper.this.isAddFixViewImmediately) {
                    FixLayoutHelper.this.addFixViewWithAnimator(layoutManagerHelper, FixLayoutHelper.this.mFixView);
                } else {
                    layoutManagerHelper.addFixedView(FixLayoutHelper.this.mFixView);
                    FixLayoutHelper.this.isRemoveFixViewImmediately = false;
                }
            }
        };
        if (this.mFixViewDisappearAnimatorListener.isAnimating()) {
            this.mFixViewDisappearAnimatorListener.withEndAction(runnable);
        } else {
            runnable.run();
        }
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void beforeLayout(RecyclerView.Recycler recycler, RecyclerView.State state, LayoutManagerHelper layoutManagerHelper) {
        super.beforeLayout(recycler, state, layoutManagerHelper);
        if (this.mFixView != null && layoutManagerHelper.isViewHolderUpdated(this.mFixView)) {
            layoutManagerHelper.removeChildView(this.mFixView);
            recycler.recycleView(this.mFixView);
            this.mFixView = null;
            this.isAddFixViewImmediately = true;
        }
        this.mDoNormalHandle = false;
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public View getFixedView() {
        return this.mFixView;
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void layoutViews(RecyclerView.Recycler recycler, RecyclerView.State state, VirtualLayoutManager.LayoutStateWrapper layoutStateWrapper, LayoutChunkResult layoutChunkResult, LayoutManagerHelper layoutManagerHelper) {
        if (isOutOfRange(layoutStateWrapper.getCurrentPosition())) {
            return;
        }
        if (!this.mShouldDrawn) {
            layoutStateWrapper.skipCurrentPosition();
            return;
        }
        View next = this.mFixView;
        if (next == null) {
            next = layoutStateWrapper.next(recycler);
        } else {
            layoutStateWrapper.skipCurrentPosition();
        }
        if (next == null) {
            layoutChunkResult.mFinished = true;
            return;
        }
        this.mDoNormalHandle = state.isPreLayout();
        if (this.mDoNormalHandle) {
            layoutManagerHelper.addChildView(layoutStateWrapper, next);
        }
        this.mFixView = next;
        doMeasureAndLayout(next, layoutManagerHelper);
        layoutChunkResult.mConsumed = 0;
        layoutChunkResult.mIgnoreConsumed = true;
        handleStateOnResult(layoutChunkResult, next);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void onClear(LayoutManagerHelper layoutManagerHelper) {
        super.onClear(layoutManagerHelper);
        if (this.mFixView != null) {
            layoutManagerHelper.removeChildView(this.mFixView);
            layoutManagerHelper.recycleView(this.mFixView);
            this.mFixView.animate().cancel();
            this.mFixView = null;
            this.isAddFixViewImmediately = false;
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

    public void setAlignType(int i) {
        this.mAlignType = i;
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void setItemCount(int i) {
        if (i > 0) {
            super.setItemCount(1);
        } else {
            super.setItemCount(0);
        }
    }

    @Override // com.alibaba.android.vlayout.layout.MarginLayoutHelper
    public void setMargin(int i, int i2, int i3, int i4) {
    }

    public void setSketchMeasure(boolean z) {
        this.mSketchMeasure = z;
    }

    public void setX(int i) {
        this.mX = i;
    }

    public void setY(int i) {
        this.mY = i;
    }

    protected boolean shouldBeDraw(LayoutManagerHelper layoutManagerHelper, int i, int i2, int i3) {
        return true;
    }
}
