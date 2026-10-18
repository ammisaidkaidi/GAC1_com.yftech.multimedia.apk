package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import com.android.internal.R;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
public class FrameLayout extends ViewGroup {
    private static final int DEFAULT_CHILD_GRAVITY = 8388659;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingBottom;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingLeft;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingRight;

    @ViewDebug.ExportedProperty(category = "padding")
    private int mForegroundPaddingTop;
    private final ArrayList<View> mMatchParentChildren;

    @ViewDebug.ExportedProperty(category = "measurement")
    boolean mMeasureAllChildren;

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public static final int UNSPECIFIED_GRAVITY = -1;
        public int gravity;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = -1;
        }

        public LayoutParams(int i, int i2, int i3) {
            super(i, i2);
            this.gravity = -1;
            this.gravity = i3;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.gravity = -1;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FrameLayout_Layout);
            this.gravity = typedArrayObtainStyledAttributes.getInt(0, -1);
            typedArrayObtainStyledAttributes.recycle();
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.gravity = -1;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.gravity = -1;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.MarginLayoutParams) layoutParams);
            this.gravity = -1;
            this.gravity = layoutParams.gravity;
        }
    }

    public FrameLayout(Context context) {
        super(context);
        this.mMeasureAllChildren = false;
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mMatchParentChildren = new ArrayList<>(1);
    }

    public FrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FrameLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public FrameLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mMeasureAllChildren = false;
        this.mForegroundPaddingLeft = 0;
        this.mForegroundPaddingTop = 0;
        this.mForegroundPaddingRight = 0;
        this.mForegroundPaddingBottom = 0;
        this.mMatchParentChildren = new ArrayList<>(1);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FrameLayout, i, i2);
        if (typedArrayObtainStyledAttributes.getBoolean(0, false)) {
            setMeasureAllChildren(true);
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    private int getPaddingBottomWithForeground() {
        if (isForegroundInsidePadding()) {
            return Math.max(this.mPaddingBottom, this.mForegroundPaddingBottom);
        }
        return this.mForegroundPaddingBottom + this.mPaddingBottom;
    }

    private int getPaddingTopWithForeground() {
        if (isForegroundInsidePadding()) {
            return Math.max(this.mPaddingTop, this.mForegroundPaddingTop);
        }
        return this.mForegroundPaddingTop + this.mPaddingTop;
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
        super.encodeProperties(viewHierarchyEncoder);
        viewHierarchyEncoder.addProperty("measurement:measureAllChildren", this.mMeasureAllChildren);
        viewHierarchyEncoder.addProperty("padding:foregroundPaddingLeft", this.mForegroundPaddingLeft);
        viewHierarchyEncoder.addProperty("padding:foregroundPaddingTop", this.mForegroundPaddingTop);
        viewHierarchyEncoder.addProperty("padding:foregroundPaddingRight", this.mForegroundPaddingRight);
        viewHierarchyEncoder.addProperty("padding:foregroundPaddingBottom", this.mForegroundPaddingBottom);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-1, -1);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (sPreserveMarginParamsInLayoutParamConversion) {
            if (layoutParams instanceof LayoutParams) {
                return new LayoutParams((LayoutParams) layoutParams);
            }
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                return new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams);
            }
        }
        return new LayoutParams(layoutParams);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return FrameLayout.class.getName();
    }

    @Deprecated
    public boolean getConsiderGoneChildrenWhenMeasuring() {
        return getMeasureAllChildren();
    }

    public boolean getMeasureAllChildren() {
        return this.mMeasureAllChildren;
    }

    int getPaddingLeftWithForeground() {
        if (isForegroundInsidePadding()) {
            return Math.max(this.mPaddingLeft, this.mForegroundPaddingLeft);
        }
        return this.mForegroundPaddingLeft + this.mPaddingLeft;
    }

    int getPaddingRightWithForeground() {
        if (isForegroundInsidePadding()) {
            return Math.max(this.mPaddingRight, this.mForegroundPaddingRight);
        }
        return this.mForegroundPaddingRight + this.mPaddingRight;
    }

    void layoutChildren(int i, int i2, int i3, int i4, boolean z) {
        int childCount = getChildCount();
        int paddingLeftWithForeground = getPaddingLeftWithForeground();
        int paddingRightWithForeground = (i3 - i) - getPaddingRightWithForeground();
        int paddingTopWithForeground = getPaddingTopWithForeground();
        int paddingBottomWithForeground = (i4 - i2) - getPaddingBottomWithForeground();
        for (int i5 = 0; i5 < childCount; i5++) {
            View childAt = getChildAt(i5);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int measuredWidth = childAt.getMeasuredWidth();
                int measuredHeight = childAt.getMeasuredHeight();
                int i6 = layoutParams.gravity;
                if (i6 == -1) {
                    i6 = 8388659;
                }
                int absoluteGravity = Gravity.getAbsoluteGravity(i6, getLayoutDirection());
                int i7 = i6 & 112;
                int i8 = absoluteGravity & 7;
                int i9 = i8 != 1 ? (i8 == 5 && !z) ? (paddingRightWithForeground - measuredWidth) - layoutParams.rightMargin : layoutParams.leftMargin + paddingLeftWithForeground : (((((paddingRightWithForeground - paddingLeftWithForeground) - measuredWidth) / 2) + paddingLeftWithForeground) + layoutParams.leftMargin) - layoutParams.rightMargin;
                int i10 = i7 != 16 ? (i7 == 48 || i7 != 80) ? layoutParams.topMargin + paddingTopWithForeground : (paddingBottomWithForeground - measuredHeight) - layoutParams.bottomMargin : (((((paddingBottomWithForeground - paddingTopWithForeground) - measuredHeight) / 2) + paddingTopWithForeground) + layoutParams.topMargin) - layoutParams.bottomMargin;
                childAt.layout(i9, i10, measuredWidth + i9, measuredHeight + i10);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        layoutChildren(i, i2, i3, i4, false);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int childCount = getChildCount();
        boolean z = (View.MeasureSpec.getMode(i) == 1073741824 && View.MeasureSpec.getMode(i2) == 1073741824) ? false : true;
        this.mMatchParentChildren.clear();
        int i3 = 0;
        int i4 = 0;
        int i5 = 0;
        for (int i6 = 0; i6 < childCount; i6++) {
            View childAt = getChildAt(i6);
            if (this.mMeasureAllChildren || childAt.getVisibility() != 8) {
                measureChildWithMargins(childAt, i, 0, i2, 0);
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                int iMax = Math.max(i5, childAt.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin);
                int iMax2 = Math.max(i4, childAt.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin);
                int iCombineMeasuredStates = combineMeasuredStates(i3, childAt.getMeasuredState());
                if (z && (layoutParams.width == -1 || layoutParams.height == -1)) {
                    this.mMatchParentChildren.add(childAt);
                }
                i5 = iMax;
                i4 = iMax2;
                i3 = iCombineMeasuredStates;
            }
        }
        int i7 = i3;
        int paddingLeftWithForeground = i5 + getPaddingLeftWithForeground() + getPaddingRightWithForeground();
        int iMax3 = Math.max(i4 + getPaddingTopWithForeground() + getPaddingBottomWithForeground(), getSuggestedMinimumHeight());
        int iMax4 = Math.max(paddingLeftWithForeground, getSuggestedMinimumWidth());
        Drawable foreground = getForeground();
        if (foreground != null) {
            iMax3 = Math.max(iMax3, foreground.getMinimumHeight());
            iMax4 = Math.max(iMax4, foreground.getMinimumWidth());
        }
        setMeasuredDimension(resolveSizeAndState(iMax4, i, i7), resolveSizeAndState(iMax3, i2, i7 << 16));
        int size = this.mMatchParentChildren.size();
        if (size > 1) {
            for (int i8 = 0; i8 < size; i8++) {
                View view = this.mMatchParentChildren.get(i8);
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                view.measure(marginLayoutParams.width == -1 ? View.MeasureSpec.makeMeasureSpec(Math.max(0, (((getMeasuredWidth() - getPaddingLeftWithForeground()) - getPaddingRightWithForeground()) - marginLayoutParams.leftMargin) - marginLayoutParams.rightMargin), 1073741824) : getChildMeasureSpec(i, getPaddingLeftWithForeground() + getPaddingRightWithForeground() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin, marginLayoutParams.width), marginLayoutParams.height == -1 ? View.MeasureSpec.makeMeasureSpec(Math.max(0, (((getMeasuredHeight() - getPaddingTopWithForeground()) - getPaddingBottomWithForeground()) - marginLayoutParams.topMargin) - marginLayoutParams.bottomMargin), 1073741824) : getChildMeasureSpec(i2, getPaddingTopWithForeground() + getPaddingBottomWithForeground() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin, marginLayoutParams.height));
            }
        }
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setForegroundGravity(int i) {
        if (getForegroundGravity() != i) {
            super.setForegroundGravity(i);
            Drawable foreground = getForeground();
            if (getForegroundGravity() != 119 || foreground == null) {
                this.mForegroundPaddingLeft = 0;
                this.mForegroundPaddingTop = 0;
                this.mForegroundPaddingRight = 0;
                this.mForegroundPaddingBottom = 0;
            } else {
                Rect rect = new Rect();
                if (foreground.getPadding(rect)) {
                    this.mForegroundPaddingLeft = rect.left;
                    this.mForegroundPaddingTop = rect.top;
                    this.mForegroundPaddingRight = rect.right;
                    this.mForegroundPaddingBottom = rect.bottom;
                }
            }
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setMeasureAllChildren(boolean z) {
        this.mMeasureAllChildren = z;
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }
}
