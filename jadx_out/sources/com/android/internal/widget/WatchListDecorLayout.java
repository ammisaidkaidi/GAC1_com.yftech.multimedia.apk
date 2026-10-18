package com.android.internal.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public class WatchListDecorLayout extends FrameLayout implements ViewTreeObserver.OnScrollChangedListener {
    private View mBottomPanel;
    private int mForegroundPaddingBottom;
    private int mForegroundPaddingLeft;
    private int mForegroundPaddingRight;
    private int mForegroundPaddingTop;
    private ListView mListView;
    private final ArrayList<View> mMatchParentChildren;
    private ViewTreeObserver mObserver;
    private int mPendingScroll;
    private View mTopPanel;

    public WatchListDecorLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mMatchParentChildren = new ArrayList<>(1);
    }

    public WatchListDecorLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mMatchParentChildren = new ArrayList<>(1);
    }

    public WatchListDecorLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mMatchParentChildren = new ArrayList<>(1);
    }

    private void applyMeasureToChild(View view, int i, int i2) {
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        view.measure(marginLayoutParams.width == -1 ? View.MeasureSpec.makeMeasureSpec(Math.max(0, (((getMeasuredWidth() - getPaddingLeftWithForeground()) - getPaddingRightWithForeground()) - marginLayoutParams.leftMargin) - marginLayoutParams.rightMargin), 1073741824) : getChildMeasureSpec(i, getPaddingLeftWithForeground() + getPaddingRightWithForeground() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin, marginLayoutParams.width), marginLayoutParams.height == -1 ? View.MeasureSpec.makeMeasureSpec(Math.max(0, (((getMeasuredHeight() - getPaddingTopWithForeground()) - getPaddingBottomWithForeground()) - marginLayoutParams.topMargin) - marginLayoutParams.bottomMargin), 1073741824) : getChildMeasureSpec(i2, getPaddingTopWithForeground() + getPaddingBottomWithForeground() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin, marginLayoutParams.height));
    }

    private int getPaddingBottomWithForeground() {
        if (isForegroundInsidePadding()) {
            return Math.max(this.mPaddingBottom, this.mForegroundPaddingBottom);
        }
        return this.mForegroundPaddingBottom + this.mPaddingBottom;
    }

    private int getPaddingLeftWithForeground() {
        if (isForegroundInsidePadding()) {
            return Math.max(this.mPaddingLeft, this.mForegroundPaddingLeft);
        }
        return this.mForegroundPaddingLeft + this.mPaddingLeft;
    }

    private int getPaddingRightWithForeground() {
        if (isForegroundInsidePadding()) {
            return Math.max(this.mPaddingRight, this.mForegroundPaddingRight);
        }
        return this.mForegroundPaddingRight + this.mPaddingRight;
    }

    private int getPaddingTopWithForeground() {
        if (isForegroundInsidePadding()) {
            return Math.max(this.mPaddingTop, this.mForegroundPaddingTop);
        }
        return this.mForegroundPaddingTop + this.mPaddingTop;
    }

    private int measureAndGetHeight(View view, int i, int i2) {
        if (view == null) {
            return 0;
        }
        if (view.getVisibility() != 8) {
            applyMeasureToChild(this.mBottomPanel, i, i2);
            return view.getMeasuredHeight();
        }
        if (!getMeasureAllChildren()) {
            return 0;
        }
        applyMeasureToChild(this.mBottomPanel, i, i2);
        return 0;
    }

    private void setScrolling(View view, float f) {
        if (view.getTranslationY() != f) {
            view.setTranslationY(f);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mPendingScroll = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (!(childAt instanceof ListView)) {
                int i2 = ((FrameLayout.LayoutParams) childAt.getLayoutParams()).gravity & 112;
                if (i2 == 48 && this.mTopPanel == null) {
                    this.mTopPanel = childAt;
                } else if (i2 == 80 && this.mBottomPanel == null) {
                    this.mBottomPanel = childAt;
                }
            } else {
                if (this.mListView != null) {
                    throw new IllegalArgumentException("only one ListView child allowed");
                }
                this.mListView = (ListView) childAt;
                this.mListView.setNestedScrollingEnabled(true);
                this.mObserver = this.mListView.getViewTreeObserver();
                this.mObserver.addOnScrollChangedListener(this);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        this.mListView = null;
        this.mBottomPanel = null;
        this.mTopPanel = null;
        if (this.mObserver != null) {
            if (this.mObserver.isAlive()) {
                this.mObserver.removeOnScrollChangedListener(this);
            }
            this.mObserver = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int childCount = getChildCount();
        boolean z = (View.MeasureSpec.getMode(i) == 1073741824 && View.MeasureSpec.getMode(i2) == 1073741824) ? false : true;
        this.mMatchParentChildren.clear();
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            if (getMeasureAllChildren() || childAt.getVisibility() != 8) {
                measureChildWithMargins(childAt, i, 0, i2, 0);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                int iMax = Math.max(i4, childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
                int iMax2 = Math.max(i5, childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
                int iCombineMeasuredStates = combineMeasuredStates(i3, childAt.getMeasuredState());
                if (z && (layoutParams.width == -1 || layoutParams.height == -1)) {
                    this.mMatchParentChildren.add(childAt);
                }
                i4 = iMax;
                i5 = iMax2;
                i3 = iCombineMeasuredStates;
            }
        }
        int i7 = i3;
        int paddingLeftWithForeground = i4 + getPaddingLeftWithForeground() + getPaddingRightWithForeground();
        int iMax3 = Math.max(i5 + getPaddingTopWithForeground() + getPaddingBottomWithForeground(), getSuggestedMinimumHeight());
        int iMax4 = Math.max(paddingLeftWithForeground, getSuggestedMinimumWidth());
        Drawable foreground = getForeground();
        if (foreground != null) {
            iMax3 = Math.max(iMax3, foreground.getMinimumHeight());
            iMax4 = Math.max(iMax4, foreground.getMinimumWidth());
        }
        setMeasuredDimension(resolveSizeAndState(iMax4, i, i7), resolveSizeAndState(iMax3, i2, i7 << 16));
        if (this.mListView != null) {
            if (this.mPendingScroll != 0) {
                this.mListView.scrollListBy(this.mPendingScroll);
                this.mPendingScroll = 0;
            }
            int iMax5 = Math.max(this.mListView.getPaddingTop(), measureAndGetHeight(this.mTopPanel, i, i2));
            int iMax6 = Math.max(this.mListView.getPaddingBottom(), measureAndGetHeight(this.mBottomPanel, i, i2));
            if (iMax5 != this.mListView.getPaddingTop() || iMax6 != this.mListView.getPaddingBottom()) {
                this.mPendingScroll += this.mListView.getPaddingTop() - iMax5;
                this.mListView.setPadding(this.mListView.getPaddingLeft(), iMax5, this.mListView.getPaddingRight(), iMax6);
            }
        }
        int size = this.mMatchParentChildren.size();
        if (size > 1) {
            for (int i8 = 0; i8 < size; i8++) {
                View view = this.mMatchParentChildren.get(i8);
                if (this.mListView == null || (view != this.mTopPanel && view != this.mBottomPanel)) {
                    applyMeasureToChild(view, i, i2);
                }
            }
        }
    }

    @Override // android.view.ViewTreeObserver.OnScrollChangedListener
    public void onScrollChanged() {
        if (this.mListView == null) {
            return;
        }
        if (this.mTopPanel != null) {
            if (this.mListView.getChildCount() <= 0) {
                setScrolling(this.mTopPanel, 0.0f);
            } else if (this.mListView.getFirstVisiblePosition() == 0) {
                setScrolling(this.mTopPanel, (this.mListView.getChildAt(0).getY() - this.mTopPanel.getHeight()) - this.mTopPanel.getTop());
            } else {
                setScrolling(this.mTopPanel, -this.mTopPanel.getHeight());
            }
        }
        if (this.mBottomPanel != null) {
            if (this.mListView.getChildCount() <= 0) {
                setScrolling(this.mBottomPanel, 0.0f);
            } else if (this.mListView.getLastVisiblePosition() < this.mListView.getCount() - 1) {
                setScrolling(this.mBottomPanel, this.mBottomPanel.getHeight());
            } else {
                View childAt = this.mListView.getChildAt(this.mListView.getChildCount() - 1);
                setScrolling(this.mBottomPanel, Math.max(0.0f, (childAt.getY() + childAt.getHeight()) - this.mBottomPanel.getTop()));
            }
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void setForegroundGravity(int i) {
        if (getForegroundGravity() != i) {
            super.setForegroundGravity(i);
            Drawable foreground = getForeground();
            if (getForegroundGravity() != 119 || foreground == null) {
                this.mForegroundPaddingLeft = 0;
                this.mForegroundPaddingTop = 0;
                this.mForegroundPaddingRight = 0;
                this.mForegroundPaddingBottom = 0;
                return;
            }
            Rect rect = new Rect();
            if (foreground.getPadding(rect)) {
                this.mForegroundPaddingLeft = rect.left;
                this.mForegroundPaddingTop = rect.top;
                this.mForegroundPaddingRight = rect.right;
                this.mForegroundPaddingBottom = rect.bottom;
            }
        }
    }
}
