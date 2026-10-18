package android.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.TtmlUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.view.menu.ActionMenuItemView;
import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuPresenter;
import com.android.internal.view.menu.MenuView;

/* JADX INFO: loaded from: classes2.dex */
public class ActionMenuView extends LinearLayout implements MenuBuilder.ItemInvoker, MenuView {
    static final int GENERATED_ITEM_PADDING = 4;
    static final int MIN_CELL_SIZE = 56;
    private static final String TAG = "ActionMenuView";
    private MenuPresenter.Callback mActionMenuPresenterCallback;
    private boolean mFormatItems;
    private int mFormatItemsWidth;
    private int mGeneratedItemPadding;
    private MenuBuilder mMenu;
    private MenuBuilder.Callback mMenuBuilderCallback;
    private int mMinCellSize;
    private OnMenuItemClickListener mOnMenuItemClickListener;
    private Context mPopupContext;
    private int mPopupTheme;
    private ActionMenuPresenter mPresenter;
    private boolean mReserveOverflow;

    public interface ActionMenuChildView {
        boolean needsDividerAfter();

        boolean needsDividerBefore();
    }

    private class ActionMenuPresenterCallback implements MenuPresenter.Callback {
        private ActionMenuPresenterCallback() {
        }

        @Override // com.android.internal.view.menu.MenuPresenter.Callback
        public void onCloseMenu(MenuBuilder menuBuilder, boolean z) {
        }

        @Override // com.android.internal.view.menu.MenuPresenter.Callback
        public boolean onOpenSubMenu(MenuBuilder menuBuilder) {
            return false;
        }
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {

        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public int cellsUsed;

        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public boolean expandable;
        public boolean expanded;

        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public int extraPixels;

        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public boolean isOverflowButton;

        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public boolean preventEdgeOffset;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.isOverflowButton = false;
        }

        public LayoutParams(int i, int i2, boolean z) {
            super(i, i2);
            this.isOverflowButton = z;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((LinearLayout.LayoutParams) layoutParams);
            this.isOverflowButton = layoutParams.isOverflowButton;
        }

        @Override // android.widget.LinearLayout.LayoutParams, android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
            super.encodeProperties(viewHierarchyEncoder);
            viewHierarchyEncoder.addProperty("layout:overFlowButton", this.isOverflowButton);
            viewHierarchyEncoder.addProperty("layout:cellsUsed", this.cellsUsed);
            viewHierarchyEncoder.addProperty("layout:extraPixels", this.extraPixels);
            viewHierarchyEncoder.addProperty("layout:expandable", this.expandable);
            viewHierarchyEncoder.addProperty("layout:preventEdgeOffset", this.preventEdgeOffset);
        }
    }

    private class MenuBuilderCallback implements MenuBuilder.Callback {
        private MenuBuilderCallback() {
        }

        @Override // com.android.internal.view.menu.MenuBuilder.Callback
        public boolean onMenuItemSelected(MenuBuilder menuBuilder, MenuItem menuItem) {
            return ActionMenuView.this.mOnMenuItemClickListener != null && ActionMenuView.this.mOnMenuItemClickListener.onMenuItemClick(menuItem);
        }

        @Override // com.android.internal.view.menu.MenuBuilder.Callback
        public void onMenuModeChange(MenuBuilder menuBuilder) {
            if (ActionMenuView.this.mMenuBuilderCallback != null) {
                ActionMenuView.this.mMenuBuilderCallback.onMenuModeChange(menuBuilder);
            }
        }
    }

    public interface OnMenuItemClickListener {
        boolean onMenuItemClick(MenuItem menuItem);
    }

    public ActionMenuView(Context context) {
        this(context, null);
    }

    public ActionMenuView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBaselineAligned(false);
        float f = context.getResources().getDisplayMetrics().density;
        this.mMinCellSize = (int) (56.0f * f);
        this.mGeneratedItemPadding = (int) (f * 4.0f);
        this.mPopupContext = context;
        this.mPopupTheme = 0;
    }

    static int measureChildForCells(View view, int i, int i2, int i3, int i4) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3) - i4, View.MeasureSpec.getMode(i3));
        ActionMenuItemView actionMenuItemView = view instanceof ActionMenuItemView ? (ActionMenuItemView) view : null;
        boolean z = actionMenuItemView != null && actionMenuItemView.hasText();
        int i5 = 2;
        if (i2 <= 0 || (z && i2 < 2)) {
            i5 = 0;
        } else {
            view.measure(View.MeasureSpec.makeMeasureSpec(i2 * i, Integer.MIN_VALUE), iMakeMeasureSpec);
            int measuredWidth = view.getMeasuredWidth();
            int i6 = measuredWidth / i;
            if (measuredWidth % i != 0) {
                i6++;
            }
            if (!z || i6 >= 2) {
                i5 = i6;
            }
        }
        layoutParams.expandable = !layoutParams.isOverflowButton && z;
        layoutParams.cellsUsed = i5;
        view.measure(View.MeasureSpec.makeMeasureSpec(i * i5, 1073741824), iMakeMeasureSpec);
        return i5;
    }

    /* JADX WARN: Code duplicated, block: B:134:0x0254  */
    /* JADX WARN: Code duplicated, block: B:137:0x025b A[ADDED_TO_REGION, LOOP:5: B:137:0x025b->B:142:0x027d, LOOP_START, PHI: r3 r29
      0x025b: PHI (r3v8 int) = (r3v7 int), (r3v9 int) binds: [B:136:0x0259, B:142:0x027d] A[DONT_GENERATE, DONT_INLINE]
      0x025b: PHI (r29v1 int) = (r29v0 int), (r29v2 int) binds: [B:136:0x0259, B:142:0x027d] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:138:0x025d  */
    /* JADX WARN: Code duplicated, block: B:140:0x026b  */
    /* JADX WARN: Code duplicated, block: B:141:0x026e  */
    /* JADX WARN: Code duplicated, block: B:144:0x0284  */
    /* JADX WARN: Code duplicated, block: B:145:0x0289  */
    /* JADX WARN: Type inference failed for: r2v30 */
    /* JADX WARN: Type inference failed for: r2v31, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r2v46 */
    /* JADX WARN: Type inference failed for: r6v15 */
    /* JADX WARN: Type inference failed for: r6v16, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r6v18 */
    /* JADX WARN: Type inference failed for: r6v20 */
    private void onMeasureExactFormat(int i, int i2) {
        boolean z;
        int i3;
        int i4;
        int i5;
        int i6;
        View childAt;
        LayoutParams layoutParams;
        int i7;
        ?? r6;
        int i8;
        ?? r2;
        int mode = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        int childMeasureSpec = getChildMeasureSpec(i2, paddingTop, -2);
        int i9 = size - paddingLeft;
        int i10 = i9 / this.mMinCellSize;
        int i11 = i9 % this.mMinCellSize;
        if (i10 == 0) {
            setMeasuredDimension(i9, 0);
            return;
        }
        int i12 = this.mMinCellSize + (i11 / i10);
        int childCount = getChildCount();
        int i13 = i10;
        int i14 = 0;
        int iMax = 0;
        boolean z2 = false;
        int i15 = 0;
        int i16 = 0;
        int i17 = 0;
        long j = 0;
        while (i14 < childCount) {
            View childAt2 = getChildAt(i14);
            int i18 = size2;
            if (childAt2.getVisibility() == 8) {
                i9 = i9;
            } else {
                boolean z3 = childAt2 instanceof ActionMenuItemView;
                int i19 = i15 + 1;
                if (z3) {
                    r2 = 0;
                    childAt2.setPadding(this.mGeneratedItemPadding, 0, this.mGeneratedItemPadding, 0);
                } else {
                    r2 = 0;
                }
                LayoutParams layoutParams2 = (LayoutParams) childAt2.getLayoutParams();
                layoutParams2.expanded = r2;
                layoutParams2.extraPixels = r2;
                layoutParams2.cellsUsed = r2;
                layoutParams2.expandable = r2;
                layoutParams2.leftMargin = r2;
                layoutParams2.rightMargin = r2;
                layoutParams2.preventEdgeOffset = z3 && ((ActionMenuItemView) childAt2).hasText();
                int iMeasureChildForCells = measureChildForCells(childAt2, i12, layoutParams2.isOverflowButton ? 1 : i13, childMeasureSpec, paddingTop);
                int iMax2 = Math.max(i16, iMeasureChildForCells);
                if (layoutParams2.expandable) {
                    i17++;
                }
                if (layoutParams2.isOverflowButton) {
                    z2 = true;
                }
                i13 -= iMeasureChildForCells;
                iMax = Math.max(iMax, childAt2.getMeasuredHeight());
                if (iMeasureChildForCells == 1) {
                    j |= (long) (1 << i14);
                }
                i15 = i19;
                i16 = iMax2;
            }
            i14++;
            size2 = i18;
            i9 = i9;
        }
        int i20 = i9;
        int i21 = size2;
        boolean z4 = z2 && i15 == 2;
        boolean z5 = false;
        while (true) {
            if (i17 <= 0 || i13 <= 0) {
                z = z5;
                break;
            }
            int i22 = Integer.MAX_VALUE;
            int i23 = 0;
            int i24 = 0;
            long j2 = 0;
            while (i23 < childCount) {
                LayoutParams layoutParams3 = (LayoutParams) getChildAt(i23).getLayoutParams();
                boolean z6 = z5;
                if (!layoutParams3.expandable) {
                    i8 = i23;
                } else if (layoutParams3.cellsUsed < i22) {
                    i8 = i23;
                    i22 = layoutParams3.cellsUsed;
                    j2 = 1 << i23;
                    i24 = 1;
                } else {
                    i8 = i23;
                    if (layoutParams3.cellsUsed == i22) {
                        i24++;
                        j2 |= (long) (1 << i8);
                    }
                }
                i23 = i8 + 1;
                z5 = z6;
            }
            z = z5;
            j |= j2;
            if (i24 > i13) {
                break;
            }
            int i25 = i22 + 1;
            int i26 = 0;
            while (i26 < childCount) {
                View childAt3 = getChildAt(i26);
                LayoutParams layoutParams4 = (LayoutParams) childAt3.getLayoutParams();
                int i27 = iMax;
                int i28 = childMeasureSpec;
                int i29 = childCount;
                long j3 = 1 << i26;
                if ((j2 & j3) != 0) {
                    if (z4 && layoutParams4.preventEdgeOffset) {
                        r6 = 1;
                        r6 = 1;
                        if (i13 == 1) {
                            childAt3.setPadding(this.mGeneratedItemPadding + i12, 0, this.mGeneratedItemPadding, 0);
                        }
                    } else {
                        r6 = 1;
                    }
                    layoutParams4.cellsUsed += r6;
                    layoutParams4.expanded = r6;
                    i13--;
                } else if (layoutParams4.cellsUsed == i25) {
                    j |= j3;
                }
                i26++;
                iMax = i27;
                childMeasureSpec = i28;
                childCount = i29;
            }
            z5 = true;
        }
        int i30 = childMeasureSpec;
        int i31 = childCount;
        int i32 = iMax;
        if (!z2) {
            i3 = 1;
            boolean z7 = i15 == 1;
            if (i13 > 0 || j == 0 || (i13 >= i15 - i3 && !z7 && i16 <= i3)) {
                i4 = i31;
                i5 = 0;
            } else {
                float fBitCount = Long.bitCount(j);
                if (z7) {
                    i5 = 0;
                } else {
                    if ((j & 1) != 0) {
                        i5 = 0;
                        if (!((LayoutParams) getChildAt(0).getLayoutParams()).preventEdgeOffset) {
                            fBitCount -= 0.5f;
                        }
                    } else {
                        i5 = 0;
                    }
                    int i33 = i31 - 1;
                    if ((j & ((long) (1 << i33))) != 0 && !((LayoutParams) getChildAt(i33).getLayoutParams()).preventEdgeOffset) {
                        fBitCount -= 0.5f;
                    }
                }
                int i34 = fBitCount > 0.0f ? (int) ((i13 * i12) / fBitCount) : i5;
                boolean z8 = z;
                i4 = i31;
                for (int i35 = i5; i35 < i4; i35++) {
                    if ((j & ((long) (1 << i35))) != 0) {
                        View childAt4 = getChildAt(i35);
                        LayoutParams layoutParams5 = (LayoutParams) childAt4.getLayoutParams();
                        if (childAt4 instanceof ActionMenuItemView) {
                            layoutParams5.extraPixels = i34;
                            layoutParams5.expanded = true;
                            if (i35 == 0 && !layoutParams5.preventEdgeOffset) {
                                layoutParams5.leftMargin = (-i34) / 2;
                            }
                            z8 = true;
                        } else if (layoutParams5.isOverflowButton) {
                            layoutParams5.extraPixels = i34;
                            layoutParams5.expanded = true;
                            layoutParams5.rightMargin = (-i34) / 2;
                            z8 = true;
                        } else {
                            if (i35 != 0) {
                                layoutParams5.leftMargin = i34 / 2;
                            }
                            if (i35 != i4 - 1) {
                                layoutParams5.rightMargin = i34 / 2;
                            }
                        }
                    }
                }
                z = z8;
            }
            if (z) {
                while (i5 < i4) {
                    childAt = getChildAt(i5);
                    layoutParams = (LayoutParams) childAt.getLayoutParams();
                    if (layoutParams.expanded) {
                        i7 = i30;
                        childAt.measure(View.MeasureSpec.makeMeasureSpec((layoutParams.cellsUsed * i12) + layoutParams.extraPixels, 1073741824), i7);
                    } else {
                        i7 = i30;
                    }
                    i5++;
                    i30 = i7;
                }
            }
            if (mode != 1073741824) {
                i6 = i32;
            } else {
                i6 = i21;
            }
            setMeasuredDimension(i20, i6);
        }
        i3 = 1;
        if (i13 > 0) {
            i4 = i31;
            i5 = 0;
        } else {
            i4 = i31;
            i5 = 0;
        }
        if (z) {
            while (i5 < i4) {
                childAt = getChildAt(i5);
                layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.expanded) {
                    i7 = i30;
                } else {
                    i7 = i30;
                    childAt.measure(View.MeasureSpec.makeMeasureSpec((layoutParams.cellsUsed * i12) + layoutParams.extraPixels, 1073741824), i7);
                }
                i5++;
                i30 = i7;
            }
        }
        if (mode != 1073741824) {
            i6 = i32;
        } else {
            i6 = i21;
        }
        setMeasuredDimension(i20, i6);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams != null && (layoutParams instanceof LayoutParams);
    }

    public void dismissPopupMenus() {
        if (this.mPresenter != null) {
            this.mPresenter.dismissPopupMenus();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchPopulateAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        layoutParams.gravity = 16;
        return layoutParams;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams == null) {
            return generateDefaultLayoutParams();
        }
        LayoutParams layoutParams2 = layoutParams instanceof LayoutParams ? new LayoutParams((LayoutParams) layoutParams) : new LayoutParams(layoutParams);
        if (layoutParams2.gravity <= 0) {
            layoutParams2.gravity = 16;
        }
        return layoutParams2;
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams layoutParamsGenerateDefaultLayoutParams = generateDefaultLayoutParams();
        layoutParamsGenerateDefaultLayoutParams.isOverflowButton = true;
        return layoutParamsGenerateDefaultLayoutParams;
    }

    public Menu getMenu() {
        if (this.mMenu == null) {
            Context context = getContext();
            this.mMenu = new MenuBuilder(context);
            this.mMenu.setCallback(new MenuBuilderCallback());
            this.mPresenter = new ActionMenuPresenter(context);
            this.mPresenter.setReserveOverflow(true);
            this.mPresenter.setCallback(this.mActionMenuPresenterCallback != null ? this.mActionMenuPresenterCallback : new ActionMenuPresenterCallback());
            this.mMenu.addMenuPresenter(this.mPresenter, this.mPopupContext);
            this.mPresenter.setMenuView(this);
        }
        return this.mMenu;
    }

    public Drawable getOverflowIcon() {
        getMenu();
        return this.mPresenter.getOverflowIcon();
    }

    public int getPopupTheme() {
        return this.mPopupTheme;
    }

    @Override // com.android.internal.view.menu.MenuView
    public int getWindowAnimations() {
        return 0;
    }

    @Override // android.widget.LinearLayout
    protected boolean hasDividerBeforeChildAt(int i) {
        boolean zNeedsDividerAfter = false;
        if (i == 0) {
            return false;
        }
        KeyEvent.Callback childAt = getChildAt(i - 1);
        KeyEvent.Callback childAt2 = getChildAt(i);
        if (i < getChildCount() && (childAt instanceof ActionMenuChildView)) {
            zNeedsDividerAfter = false | ((ActionMenuChildView) childAt).needsDividerAfter();
        }
        return (i <= 0 || !(childAt2 instanceof ActionMenuChildView)) ? zNeedsDividerAfter : zNeedsDividerAfter | ((ActionMenuChildView) childAt2).needsDividerBefore();
    }

    public boolean hideOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.hideOverflowMenu();
    }

    @Override // com.android.internal.view.menu.MenuView
    public void initialize(MenuBuilder menuBuilder) {
        this.mMenu = menuBuilder;
    }

    @Override // com.android.internal.view.menu.MenuBuilder.ItemInvoker
    public boolean invokeItem(MenuItemImpl menuItemImpl) {
        return this.mMenu.performItemAction(menuItemImpl, 0);
    }

    public boolean isOverflowMenuShowPending() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowPending();
    }

    public boolean isOverflowMenuShowing() {
        return this.mPresenter != null && this.mPresenter.isOverflowMenuShowing();
    }

    public boolean isOverflowReserved() {
        return this.mReserveOverflow;
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mPresenter != null) {
            this.mPresenter.updateMenuView(false);
            if (this.mPresenter.isOverflowMenuShowing()) {
                this.mPresenter.hideOverflowMenu();
                this.mPresenter.showOverflowMenu();
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        dismissPopupMenus();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5;
        int i6;
        int width;
        int paddingLeft;
        if (!this.mFormatItems) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        int childCount = getChildCount();
        int i7 = (i4 - i2) / 2;
        int dividerWidth = getDividerWidth();
        int i8 = i3 - i;
        int paddingRight = (i8 - getPaddingRight()) - getPaddingLeft();
        boolean zIsLayoutRtl = isLayoutRtl();
        int measuredWidth = paddingRight;
        int i9 = 0;
        int i10 = 0;
        for (int i11 = 0; i11 < childCount; i11++) {
            View childAt = getChildAt(i11);
            if (childAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                if (layoutParams.isOverflowButton) {
                    int measuredWidth2 = childAt.getMeasuredWidth();
                    if (hasDividerBeforeChildAt(i11)) {
                        measuredWidth2 += dividerWidth;
                    }
                    int measuredHeight = childAt.getMeasuredHeight();
                    if (zIsLayoutRtl) {
                        paddingLeft = getPaddingLeft() + layoutParams.leftMargin;
                        width = paddingLeft + measuredWidth2;
                    } else {
                        width = (getWidth() - getPaddingRight()) - layoutParams.rightMargin;
                        paddingLeft = width - measuredWidth2;
                    }
                    int i12 = i7 - (measuredHeight / 2);
                    childAt.layout(paddingLeft, i12, width, measuredHeight + i12);
                    measuredWidth -= measuredWidth2;
                    i9 = 1;
                } else {
                    measuredWidth -= (childAt.getMeasuredWidth() + layoutParams.leftMargin) + layoutParams.rightMargin;
                    hasDividerBeforeChildAt(i11);
                    i10++;
                }
            }
        }
        if (childCount == 1 && i9 == 0) {
            View childAt2 = getChildAt(0);
            int measuredWidth3 = childAt2.getMeasuredWidth();
            int measuredHeight2 = childAt2.getMeasuredHeight();
            int i13 = (i8 / 2) - (measuredWidth3 / 2);
            int i14 = i7 - (measuredHeight2 / 2);
            childAt2.layout(i13, i14, measuredWidth3 + i13, measuredHeight2 + i14);
            return;
        }
        int i15 = i10 - (i9 ^ 1);
        if (i15 > 0) {
            i6 = measuredWidth / i15;
            i5 = 0;
        } else {
            i5 = 0;
            i6 = 0;
        }
        int iMax = Math.max(i5, i6);
        if (zIsLayoutRtl) {
            int width2 = getWidth() - getPaddingRight();
            while (i5 < childCount) {
                View childAt3 = getChildAt(i5);
                LayoutParams layoutParams2 = (LayoutParams) childAt3.getLayoutParams();
                if (childAt3.getVisibility() != 8 && !layoutParams2.isOverflowButton) {
                    int i16 = width2 - layoutParams2.rightMargin;
                    int measuredWidth4 = childAt3.getMeasuredWidth();
                    int measuredHeight3 = childAt3.getMeasuredHeight();
                    int i17 = i7 - (measuredHeight3 / 2);
                    childAt3.layout(i16 - measuredWidth4, i17, i16, measuredHeight3 + i17);
                    width2 = i16 - ((measuredWidth4 + layoutParams2.leftMargin) + iMax);
                }
                i5++;
            }
            return;
        }
        int paddingLeft2 = getPaddingLeft();
        while (i5 < childCount) {
            View childAt4 = getChildAt(i5);
            LayoutParams layoutParams3 = (LayoutParams) childAt4.getLayoutParams();
            if (childAt4.getVisibility() != 8 && !layoutParams3.isOverflowButton) {
                int i18 = paddingLeft2 + layoutParams3.leftMargin;
                int measuredWidth5 = childAt4.getMeasuredWidth();
                int measuredHeight4 = childAt4.getMeasuredHeight();
                int i19 = i7 - (measuredHeight4 / 2);
                childAt4.layout(i18, i19, i18 + measuredWidth5, measuredHeight4 + i19);
                paddingLeft2 = i18 + measuredWidth5 + layoutParams3.rightMargin + iMax;
            }
            i5++;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int i, int i2) {
        boolean z = this.mFormatItems;
        this.mFormatItems = View.MeasureSpec.getMode(i) == 1073741824;
        if (z != this.mFormatItems) {
            this.mFormatItemsWidth = 0;
        }
        int size = View.MeasureSpec.getSize(i);
        if (this.mFormatItems && this.mMenu != null && size != this.mFormatItemsWidth) {
            this.mFormatItemsWidth = size;
            this.mMenu.onItemsChanged(true);
        }
        int childCount = getChildCount();
        if (this.mFormatItems && childCount > 0) {
            onMeasureExactFormat(i, i2);
            return;
        }
        for (int i3 = 0; i3 < childCount; i3++) {
            LayoutParams layoutParams = (LayoutParams) getChildAt(i3).getLayoutParams();
            layoutParams.rightMargin = 0;
            layoutParams.leftMargin = 0;
        }
        super.onMeasure(i, i2);
    }

    public MenuBuilder peekMenu() {
        return this.mMenu;
    }

    public void setExpandedActionViewsExclusive(boolean z) {
        this.mPresenter.setExpandedActionViewsExclusive(z);
    }

    public void setMenuCallbacks(MenuPresenter.Callback callback, MenuBuilder.Callback callback2) {
        this.mActionMenuPresenterCallback = callback;
        this.mMenuBuilderCallback = callback2;
    }

    public void setOnMenuItemClickListener(OnMenuItemClickListener onMenuItemClickListener) {
        this.mOnMenuItemClickListener = onMenuItemClickListener;
    }

    public void setOverflowIcon(Drawable drawable) {
        getMenu();
        this.mPresenter.setOverflowIcon(drawable);
    }

    public void setOverflowReserved(boolean z) {
        this.mReserveOverflow = z;
    }

    public void setPopupTheme(int i) {
        if (this.mPopupTheme != i) {
            this.mPopupTheme = i;
            if (i == 0) {
                this.mPopupContext = this.mContext;
            } else {
                this.mPopupContext = new ContextThemeWrapper(this.mContext, i);
            }
        }
    }

    public void setPresenter(ActionMenuPresenter actionMenuPresenter) {
        this.mPresenter = actionMenuPresenter;
        this.mPresenter.setMenuView(this);
    }

    public boolean showOverflowMenu() {
        return this.mPresenter != null && this.mPresenter.showOverflowMenu();
    }
}
