package android.widget;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.TtmlUtils;
import android.security.keystore.KeyProperties;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.view.ViewHierarchyEncoder;
import com.android.internal.R;
import com.google.android.material.badge.BadgeDrawable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
public class LinearLayout extends ViewGroup {
    public static final int HORIZONTAL = 0;
    private static final int INDEX_BOTTOM = 2;
    private static final int INDEX_CENTER_VERTICAL = 0;
    private static final int INDEX_FILL = 3;
    private static final int INDEX_TOP = 1;
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    public static final int VERTICAL = 1;
    private static final int VERTICAL_GRAVITY_COUNT = 4;
    private static boolean sCompatibilityDone = false;
    private static boolean sRemeasureWeightedChildren = true;
    private final boolean mAllowInconsistentMeasurement;

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private boolean mBaselineAligned;

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private int mBaselineAlignedChildIndex;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mBaselineChildTop;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mDividerPadding;
    private int mDividerWidth;

    @ViewDebug.ExportedProperty(category = "measurement", flagMapping = {@ViewDebug.FlagToString(equals = -1, mask = -1, name = KeyProperties.DIGEST_NONE), @ViewDebug.FlagToString(equals = 0, mask = 0, name = KeyProperties.DIGEST_NONE), @ViewDebug.FlagToString(equals = 48, mask = 48, name = "TOP"), @ViewDebug.FlagToString(equals = 80, mask = 80, name = "BOTTOM"), @ViewDebug.FlagToString(equals = 3, mask = 3, name = "LEFT"), @ViewDebug.FlagToString(equals = 5, mask = 5, name = "RIGHT"), @ViewDebug.FlagToString(equals = 8388611, mask = 8388611, name = "START"), @ViewDebug.FlagToString(equals = 8388613, mask = 8388613, name = "END"), @ViewDebug.FlagToString(equals = 16, mask = 16, name = "CENTER_VERTICAL"), @ViewDebug.FlagToString(equals = 112, mask = 112, name = "FILL_VERTICAL"), @ViewDebug.FlagToString(equals = 1, mask = 1, name = "CENTER_HORIZONTAL"), @ViewDebug.FlagToString(equals = 7, mask = 7, name = "FILL_HORIZONTAL"), @ViewDebug.FlagToString(equals = 17, mask = 17, name = "CENTER"), @ViewDebug.FlagToString(equals = 119, mask = 119, name = "FILL"), @ViewDebug.FlagToString(equals = 8388608, mask = 8388608, name = "RELATIVE")}, formatToHexString = true)
    private int mGravity;
    private int mLayoutDirection;
    private int[] mMaxAscent;
    private int[] mMaxDescent;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mOrientation;
    private int mShowDividers;

    @ViewDebug.ExportedProperty(category = "measurement")
    private int mTotalLength;

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private boolean mUseLargestChild;

    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    private float mWeightSum;

    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT, mapping = {@ViewDebug.IntToString(from = -1, to = KeyProperties.DIGEST_NONE), @ViewDebug.IntToString(from = 0, to = KeyProperties.DIGEST_NONE), @ViewDebug.IntToString(from = 48, to = "TOP"), @ViewDebug.IntToString(from = 80, to = "BOTTOM"), @ViewDebug.IntToString(from = 3, to = "LEFT"), @ViewDebug.IntToString(from = 5, to = "RIGHT"), @ViewDebug.IntToString(from = 8388611, to = "START"), @ViewDebug.IntToString(from = 8388613, to = "END"), @ViewDebug.IntToString(from = 16, to = "CENTER_VERTICAL"), @ViewDebug.IntToString(from = 112, to = "FILL_VERTICAL"), @ViewDebug.IntToString(from = 1, to = "CENTER_HORIZONTAL"), @ViewDebug.IntToString(from = 7, to = "FILL_HORIZONTAL"), @ViewDebug.IntToString(from = 17, to = "CENTER"), @ViewDebug.IntToString(from = 119, to = "FILL")})
        public int gravity;

        @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
        public float weight;

        public LayoutParams(int i, int i2) {
            super(i, i2);
            this.gravity = -1;
            this.weight = 0.0f;
        }

        public LayoutParams(int i, int i2, float f) {
            super(i, i2);
            this.gravity = -1;
            this.weight = f;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.gravity = -1;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LinearLayout_Layout);
            this.weight = typedArrayObtainStyledAttributes.getFloat(3, 0.0f);
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
            this.weight = layoutParams.weight;
            this.gravity = layoutParams.gravity;
        }

        @Override // android.view.ViewGroup.LayoutParams
        public String debug(String str) {
            return str + "LinearLayout.LayoutParams={width=" + sizeToString(this.width) + ", height=" + sizeToString(this.height) + " weight=" + this.weight + "}";
        }

        @Override // android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
            super.encodeProperties(viewHierarchyEncoder);
            viewHierarchyEncoder.addProperty("layout:weight", this.weight);
            viewHierarchyEncoder.addProperty("layout:gravity", this.gravity);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface OrientationMode {
    }

    public LinearLayout(Context context) {
        this(context, null);
    }

    public LinearLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LinearLayout(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public LinearLayout(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mBaselineAligned = true;
        this.mBaselineAlignedChildIndex = -1;
        this.mBaselineChildTop = 0;
        this.mGravity = BadgeDrawable.TOP_START;
        this.mLayoutDirection = -1;
        if (!sCompatibilityDone && context != null) {
            sRemeasureWeightedChildren = context.getApplicationInfo().targetSdkVersion >= 28;
            sCompatibilityDone = true;
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LinearLayout, i, i2);
        int i3 = typedArrayObtainStyledAttributes.getInt(1, -1);
        if (i3 >= 0) {
            setOrientation(i3);
        }
        int i4 = typedArrayObtainStyledAttributes.getInt(0, -1);
        if (i4 >= 0) {
            setGravity(i4);
        }
        boolean z = typedArrayObtainStyledAttributes.getBoolean(2, true);
        if (!z) {
            setBaselineAligned(z);
        }
        this.mWeightSum = typedArrayObtainStyledAttributes.getFloat(4, -1.0f);
        this.mBaselineAlignedChildIndex = typedArrayObtainStyledAttributes.getInt(3, -1);
        this.mUseLargestChild = typedArrayObtainStyledAttributes.getBoolean(6, false);
        this.mShowDividers = typedArrayObtainStyledAttributes.getInt(7, 0);
        this.mDividerPadding = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, 0);
        setDividerDrawable(typedArrayObtainStyledAttributes.getDrawable(5));
        this.mAllowInconsistentMeasurement = context.getApplicationInfo().targetSdkVersion <= 23;
        typedArrayObtainStyledAttributes.recycle();
    }

    private boolean allViewsAreGoneBefore(int i) {
        for (int i2 = i - 1; i2 >= 0; i2--) {
            View virtualChildAt = getVirtualChildAt(i2);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8) {
                return false;
            }
        }
        return true;
    }

    private void forceUniformHeight(int i, int i2) {
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.height == -1) {
                    int i4 = layoutParams.width;
                    layoutParams.width = virtualChildAt.getMeasuredWidth();
                    measureChildWithMargins(virtualChildAt, i2, 0, iMakeMeasureSpec, 0);
                    layoutParams.width = i4;
                }
            }
        }
    }

    private void forceUniformWidth(int i, int i2) {
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
        for (int i3 = 0; i3 < i; i3++) {
            View virtualChildAt = getVirtualChildAt(i3);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                if (layoutParams.width == -1) {
                    int i4 = layoutParams.height;
                    layoutParams.height = virtualChildAt.getMeasuredHeight();
                    measureChildWithMargins(virtualChildAt, iMakeMeasureSpec, 0, i2, 0);
                    layoutParams.height = i4;
                }
            }
        }
    }

    private View getLastNonGoneChild() {
        for (int virtualChildCount = getVirtualChildCount() - 1; virtualChildCount >= 0; virtualChildCount--) {
            View virtualChildAt = getVirtualChildAt(virtualChildCount);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8) {
                return virtualChildAt;
            }
        }
        return null;
    }

    private boolean isShowingDividers() {
        return (this.mShowDividers == 0 || this.mDivider == null) ? false : true;
    }

    private void setChildFrame(View view, int i, int i2, int i3, int i4) {
        view.layout(i, i2, i3 + i, i4 + i2);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    void drawDividersHorizontal(Canvas canvas) {
        int left;
        int virtualChildCount = getVirtualChildCount();
        boolean zIsLayoutRtl = isLayoutRtl();
        for (int i = 0; i < virtualChildCount; i++) {
            View virtualChildAt = getVirtualChildAt(i);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                drawVerticalDivider(canvas, zIsLayoutRtl ? virtualChildAt.getRight() + layoutParams.rightMargin : (virtualChildAt.getLeft() - layoutParams.leftMargin) - this.mDividerWidth);
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View lastNonGoneChild = getLastNonGoneChild();
            if (lastNonGoneChild == null) {
                left = zIsLayoutRtl ? getPaddingLeft() : (getWidth() - getPaddingRight()) - this.mDividerWidth;
            } else {
                LayoutParams layoutParams2 = (LayoutParams) lastNonGoneChild.getLayoutParams();
                left = zIsLayoutRtl ? (lastNonGoneChild.getLeft() - layoutParams2.leftMargin) - this.mDividerWidth : lastNonGoneChild.getRight() + layoutParams2.rightMargin;
            }
            drawVerticalDivider(canvas, left);
        }
    }

    void drawDividersVertical(Canvas canvas) {
        int virtualChildCount = getVirtualChildCount();
        for (int i = 0; i < virtualChildCount; i++) {
            View virtualChildAt = getVirtualChildAt(i);
            if (virtualChildAt != null && virtualChildAt.getVisibility() != 8 && hasDividerBeforeChildAt(i)) {
                drawHorizontalDivider(canvas, (virtualChildAt.getTop() - ((LayoutParams) virtualChildAt.getLayoutParams()).topMargin) - this.mDividerHeight);
            }
        }
        if (hasDividerBeforeChildAt(virtualChildCount)) {
            View lastNonGoneChild = getLastNonGoneChild();
            drawHorizontalDivider(canvas, lastNonGoneChild == null ? (getHeight() - getPaddingBottom()) - this.mDividerHeight : lastNonGoneChild.getBottom() + ((LayoutParams) lastNonGoneChild.getLayoutParams()).bottomMargin);
        }
    }

    void drawHorizontalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(getPaddingLeft() + this.mDividerPadding, i, (getWidth() - getPaddingRight()) - this.mDividerPadding, this.mDividerHeight + i);
        this.mDivider.draw(canvas);
    }

    void drawVerticalDivider(Canvas canvas, int i) {
        this.mDivider.setBounds(i, getPaddingTop() + this.mDividerPadding, this.mDividerWidth + i, (getHeight() - getPaddingBottom()) - this.mDividerPadding);
        this.mDivider.draw(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
        super.encodeProperties(viewHierarchyEncoder);
        viewHierarchyEncoder.addProperty("layout:baselineAligned", this.mBaselineAligned);
        viewHierarchyEncoder.addProperty("layout:baselineAlignedChildIndex", this.mBaselineAlignedChildIndex);
        viewHierarchyEncoder.addProperty("measurement:baselineChildTop", this.mBaselineChildTop);
        viewHierarchyEncoder.addProperty("measurement:orientation", this.mOrientation);
        viewHierarchyEncoder.addProperty("measurement:gravity", this.mGravity);
        viewHierarchyEncoder.addProperty("measurement:totalLength", this.mTotalLength);
        viewHierarchyEncoder.addProperty("layout:totalLength", this.mTotalLength);
        viewHierarchyEncoder.addProperty("layout:useLargestChild", this.mUseLargestChild);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -2);
        }
        if (this.mOrientation == 1) {
            return new LayoutParams(-1, -2);
        }
        return null;
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
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

    @Override // android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return LinearLayout.class.getName();
    }

    @Override // android.view.View
    public int getBaseline() {
        int i;
        if (this.mBaselineAlignedChildIndex < 0) {
            return super.getBaseline();
        }
        if (getChildCount() <= this.mBaselineAlignedChildIndex) {
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout set to an index that is out of bounds.");
        }
        View childAt = getChildAt(this.mBaselineAlignedChildIndex);
        int baseline = childAt.getBaseline();
        if (baseline == -1) {
            if (this.mBaselineAlignedChildIndex == 0) {
                return -1;
            }
            throw new RuntimeException("mBaselineAlignedChildIndex of LinearLayout points to a View that doesn't know how to get its baseline.");
        }
        int i2 = this.mBaselineChildTop;
        if (this.mOrientation == 1 && (i = this.mGravity & 112) != 48) {
            if (i == 16) {
                i2 += ((((this.mBottom - this.mTop) - this.mPaddingTop) - this.mPaddingBottom) - this.mTotalLength) / 2;
            } else if (i == 80) {
                i2 = ((this.mBottom - this.mTop) - this.mPaddingBottom) - this.mTotalLength;
            }
        }
        return i2 + ((LayoutParams) childAt.getLayoutParams()).topMargin + baseline;
    }

    public int getBaselineAlignedChildIndex() {
        return this.mBaselineAlignedChildIndex;
    }

    int getChildrenSkipCount(View view, int i) {
        return 0;
    }

    public Drawable getDividerDrawable() {
        return this.mDivider;
    }

    public int getDividerPadding() {
        return this.mDividerPadding;
    }

    public int getDividerWidth() {
        return this.mDividerWidth;
    }

    public int getGravity() {
        return this.mGravity;
    }

    int getLocationOffset(View view) {
        return 0;
    }

    int getNextLocationOffset(View view) {
        return 0;
    }

    public int getOrientation() {
        return this.mOrientation;
    }

    public int getShowDividers() {
        return this.mShowDividers;
    }

    View getVirtualChildAt(int i) {
        return getChildAt(i);
    }

    int getVirtualChildCount() {
        return getChildCount();
    }

    public float getWeightSum() {
        return this.mWeightSum;
    }

    protected boolean hasDividerBeforeChildAt(int i) {
        if (i == getVirtualChildCount()) {
            return (this.mShowDividers & 4) != 0;
        }
        if (allViewsAreGoneBefore(i)) {
            return (this.mShowDividers & 1) != 0;
        }
        return (this.mShowDividers & 2) != 0;
    }

    public boolean isBaselineAligned() {
        return this.mBaselineAligned;
    }

    public boolean isMeasureWithLargestChildEnabled() {
        return this.mUseLargestChild;
    }

    /* JADX WARN: Code duplicated, block: B:30:0x00aa  */
    /* JADX WARN: Code duplicated, block: B:33:0x00b3  */
    /* JADX WARN: Code duplicated, block: B:35:0x00b7  */
    /* JADX WARN: Code duplicated, block: B:37:0x00bb  */
    /* JADX WARN: Code duplicated, block: B:39:0x00c0  */
    /* JADX WARN: Code duplicated, block: B:41:0x00c8  */
    /* JADX WARN: Code duplicated, block: B:43:0x00d7  */
    /* JADX WARN: Code duplicated, block: B:45:0x00dd  */
    /* JADX WARN: Code duplicated, block: B:46:0x00e6  */
    /* JADX WARN: Code duplicated, block: B:48:0x00ea  */
    /* JADX WARN: Code duplicated, block: B:51:0x00fe  */
    void layoutHorizontal(int i, int i2, int i3, int i4) {
        int iMeasureNullChild;
        int i5;
        int i6;
        boolean z;
        int i7;
        int baseline;
        int i8;
        int i9;
        int i10;
        int i11;
        int measuredHeight;
        boolean zIsLayoutRtl = isLayoutRtl();
        int i12 = this.mPaddingTop;
        int i13 = i4 - i2;
        int i14 = i13 - this.mPaddingBottom;
        int i15 = (i13 - i12) - this.mPaddingBottom;
        int virtualChildCount = getVirtualChildCount();
        int i16 = this.mGravity & 8388615;
        int i17 = this.mGravity & 112;
        boolean z2 = this.mBaselineAligned;
        int[] iArr = this.mMaxAscent;
        int[] iArr2 = this.mMaxDescent;
        int absoluteGravity = Gravity.getAbsoluteGravity(i16, getLayoutDirection());
        boolean z3 = true;
        if (absoluteGravity != 1) {
            iMeasureNullChild = absoluteGravity != 5 ? this.mPaddingLeft : ((this.mPaddingLeft + i3) - i) - this.mTotalLength;
        } else {
            iMeasureNullChild = (((i3 - i) - this.mTotalLength) / 2) + this.mPaddingLeft;
        }
        if (zIsLayoutRtl) {
            i5 = virtualChildCount - 1;
            i6 = -1;
        } else {
            i5 = 0;
            i6 = 1;
        }
        int childrenSkipCount = 0;
        while (childrenSkipCount < virtualChildCount) {
            int i18 = i5 + (i6 * childrenSkipCount);
            View virtualChildAt = getVirtualChildAt(i18);
            if (virtualChildAt == null) {
                iMeasureNullChild += measureNullChild(i18);
                z = z3;
            } else {
                if (virtualChildAt.getVisibility() != 8) {
                    int measuredWidth = virtualChildAt.getMeasuredWidth();
                    int measuredHeight2 = virtualChildAt.getMeasuredHeight();
                    LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                    if (z2) {
                        i7 = childrenSkipCount;
                        virtualChildCount = virtualChildCount;
                        baseline = layoutParams.height != -1 ? virtualChildAt.getBaseline() : -1;
                        i8 = layoutParams.gravity;
                        if (i8 < 0) {
                            i8 = i17;
                        }
                        i9 = i8 & 112;
                        i17 = i17;
                        if (i9 != 16) {
                            z = true;
                            i10 = ((((i15 - measuredHeight2) / 2) + i12) + layoutParams.topMargin) - layoutParams.bottomMargin;
                        } else if (i9 != 48) {
                            if (i9 != 80) {
                                i10 = i12;
                            } else {
                                measuredHeight = (i14 - measuredHeight2) - layoutParams.bottomMargin;
                                if (baseline != -1) {
                                    measuredHeight -= iArr2[2] - (virtualChildAt.getMeasuredHeight() - baseline);
                                }
                                i10 = measuredHeight;
                            }
                            z = true;
                        } else {
                            i11 = layoutParams.topMargin + i12;
                            if (baseline != -1) {
                                z = true;
                                i11 += iArr[1] - baseline;
                            } else {
                                z = true;
                            }
                            i10 = i11;
                        }
                        if (hasDividerBeforeChildAt(i18)) {
                            iMeasureNullChild += this.mDividerWidth;
                        }
                        int i19 = layoutParams.leftMargin + iMeasureNullChild;
                        i12 = i12;
                        setChildFrame(virtualChildAt, i19 + getLocationOffset(virtualChildAt), i10, measuredWidth, measuredHeight2);
                        int nextLocationOffset = i19 + measuredWidth + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt);
                        childrenSkipCount = i7 + getChildrenSkipCount(virtualChildAt, i18);
                        iMeasureNullChild = nextLocationOffset;
                    } else {
                        i7 = childrenSkipCount;
                        virtualChildCount = virtualChildCount;
                    }
                    i8 = layoutParams.gravity;
                    if (i8 < 0) {
                        i8 = i17;
                    }
                    i9 = i8 & 112;
                    i17 = i17;
                    if (i9 != 16) {
                        z = true;
                        i10 = ((((i15 - measuredHeight2) / 2) + i12) + layoutParams.topMargin) - layoutParams.bottomMargin;
                    } else if (i9 != 48) {
                        if (i9 != 80) {
                            i10 = i12;
                        } else {
                            measuredHeight = (i14 - measuredHeight2) - layoutParams.bottomMargin;
                            if (baseline != -1) {
                                measuredHeight -= iArr2[2] - (virtualChildAt.getMeasuredHeight() - baseline);
                            }
                            i10 = measuredHeight;
                        }
                        z = true;
                    } else {
                        i11 = layoutParams.topMargin + i12;
                        if (baseline != -1) {
                            z = true;
                            i11 += iArr[1] - baseline;
                        } else {
                            z = true;
                        }
                        i10 = i11;
                    }
                    if (hasDividerBeforeChildAt(i18)) {
                        iMeasureNullChild += this.mDividerWidth;
                    }
                    int i110 = layoutParams.leftMargin + iMeasureNullChild;
                    i12 = i12;
                    setChildFrame(virtualChildAt, i110 + getLocationOffset(virtualChildAt), i10, measuredWidth, measuredHeight2);
                    int nextLocationOffset2 = i110 + measuredWidth + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt);
                    childrenSkipCount = i7 + getChildrenSkipCount(virtualChildAt, i18);
                    iMeasureNullChild = nextLocationOffset2;
                } else {
                    z = true;
                }
                childrenSkipCount++;
                z3 = z;
                virtualChildCount = virtualChildCount;
                i17 = i17;
                i12 = i12;
            }
            childrenSkipCount++;
            z3 = z;
            virtualChildCount = virtualChildCount;
            i17 = i17;
            i12 = i12;
        }
    }

    void layoutVertical(int i, int i2, int i3, int i4) {
        int iMeasureNullChild;
        int i5;
        int i6;
        int i7 = this.mPaddingLeft;
        int i8 = i3 - i;
        int i9 = i8 - this.mPaddingRight;
        int i10 = (i8 - i7) - this.mPaddingRight;
        int virtualChildCount = getVirtualChildCount();
        int i11 = this.mGravity & 112;
        int i12 = this.mGravity & 8388615;
        if (i11 != 16) {
            iMeasureNullChild = i11 != 80 ? this.mPaddingTop : ((this.mPaddingTop + i4) - i2) - this.mTotalLength;
        } else {
            iMeasureNullChild = (((i4 - i2) - this.mTotalLength) / 2) + this.mPaddingTop;
        }
        int childrenSkipCount = 0;
        while (childrenSkipCount < virtualChildCount) {
            View virtualChildAt = getVirtualChildAt(childrenSkipCount);
            if (virtualChildAt == null) {
                iMeasureNullChild += measureNullChild(childrenSkipCount);
            } else {
                if (virtualChildAt.getVisibility() != 8) {
                    int measuredWidth = virtualChildAt.getMeasuredWidth();
                    int measuredHeight = virtualChildAt.getMeasuredHeight();
                    LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                    int i13 = layoutParams.gravity;
                    if (i13 < 0) {
                        i13 = i12;
                    }
                    int absoluteGravity = Gravity.getAbsoluteGravity(i13, getLayoutDirection()) & 7;
                    if (absoluteGravity != 1) {
                        i5 = absoluteGravity != 5 ? layoutParams.leftMargin + i7 : (i9 - measuredWidth) - layoutParams.rightMargin;
                    } else {
                        i5 = ((((i10 - measuredWidth) / 2) + i7) + layoutParams.leftMargin) - layoutParams.rightMargin;
                    }
                    int i14 = i5;
                    if (hasDividerBeforeChildAt(childrenSkipCount)) {
                        iMeasureNullChild += this.mDividerHeight;
                    }
                    int i15 = iMeasureNullChild + layoutParams.topMargin;
                    setChildFrame(virtualChildAt, i14, i15 + getLocationOffset(virtualChildAt), measuredWidth, measuredHeight);
                    int nextLocationOffset = i15 + measuredHeight + layoutParams.bottomMargin + getNextLocationOffset(virtualChildAt);
                    childrenSkipCount += getChildrenSkipCount(virtualChildAt, childrenSkipCount);
                    iMeasureNullChild = nextLocationOffset;
                    i6 = 1;
                }
                childrenSkipCount += i6;
            }
            i6 = 1;
            childrenSkipCount += i6;
        }
    }

    void measureChildBeforeLayout(View view, int i, int i2, int i3, int i4, int i5) {
        measureChildWithMargins(view, i2, i3, i4, i5);
    }

    /* JADX WARN: Code duplicated, block: B:168:0x03a5  */
    /* JADX WARN: Code duplicated, block: B:170:0x03a9  */
    /* JADX WARN: Code duplicated, block: B:172:0x03ad  */
    /* JADX WARN: Code duplicated, block: B:175:0x03b2  */
    /* JADX WARN: Code duplicated, block: B:176:0x03b5  */
    /* JADX WARN: Code duplicated, block: B:206:0x0464  */
    /* JADX WARN: Code duplicated, block: B:207:0x0467  */
    void measureHorizontal(int i, int i2) {
        int[] iArr;
        int iMax;
        int i3;
        float f;
        int i4;
        int baseline;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        boolean z;
        int baseline2;
        boolean z2 = false;
        this.mTotalLength = 0;
        int virtualChildCount = getVirtualChildCount();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        if (this.mMaxAscent == null || this.mMaxDescent == null) {
            this.mMaxAscent = new int[4];
            this.mMaxDescent = new int[4];
        }
        int[] iArr2 = this.mMaxAscent;
        int[] iArr3 = this.mMaxDescent;
        iArr2[3] = -1;
        iArr2[2] = -1;
        iArr2[1] = -1;
        iArr2[0] = -1;
        iArr3[3] = -1;
        iArr3[2] = -1;
        iArr3[1] = -1;
        iArr3[0] = -1;
        boolean z3 = this.mBaselineAligned;
        boolean z4 = this.mUseLargestChild;
        int i10 = 1073741824;
        boolean z5 = mode == 1073741824;
        int childrenSkipCount = 0;
        int i11 = 0;
        int i12 = 0;
        int i13 = 0;
        boolean z6 = false;
        int iMax2 = 0;
        int iMax3 = 0;
        int i14 = 0;
        boolean z7 = false;
        boolean z8 = true;
        float f2 = 0.0f;
        int iMax4 = Integer.MIN_VALUE;
        while (true) {
            iArr = iArr3;
            if (childrenSkipCount >= virtualChildCount) {
                break;
            }
            View virtualChildAt = getVirtualChildAt(childrenSkipCount);
            if (virtualChildAt == null) {
                this.mTotalLength += measureNullChild(childrenSkipCount);
            } else {
                if (virtualChildAt.getVisibility() == 8) {
                    childrenSkipCount += getChildrenSkipCount(virtualChildAt, childrenSkipCount);
                } else {
                    i11++;
                    if (hasDividerBeforeChildAt(childrenSkipCount)) {
                        this.mTotalLength += this.mDividerWidth;
                    }
                    LayoutParams layoutParams = (LayoutParams) virtualChildAt.getLayoutParams();
                    f2 += layoutParams.weight;
                    boolean z9 = (layoutParams.width != 0 || layoutParams.weight <= 0.0f) ? z2 : true;
                    if (mode == i10 && z9) {
                        if (z5) {
                            this.mTotalLength += layoutParams.leftMargin + layoutParams.rightMargin;
                        } else {
                            int i15 = this.mTotalLength;
                            this.mTotalLength = Math.max(i15, layoutParams.leftMargin + i15 + layoutParams.rightMargin);
                        }
                        if (z3) {
                            virtualChildAt.measure(View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(i), 0), View.MeasureSpec.makeSafeMeasureSpec(View.MeasureSpec.getSize(i2), 0));
                        } else {
                            z6 = true;
                        }
                        i9 = 1073741824;
                        i8 = -1;
                    } else {
                        if (z9) {
                            layoutParams.width = -2;
                        }
                        int i16 = childrenSkipCount;
                        childrenSkipCount = i16;
                        z4 = z4;
                        z3 = z3;
                        layoutParams = layoutParams;
                        i8 = -1;
                        measureChildBeforeLayout(virtualChildAt, i16, i, f2 == 0.0f ? this.mTotalLength : 0, i2, 0);
                        int measuredWidth = virtualChildAt.getMeasuredWidth();
                        if (z9) {
                            layoutParams.width = 0;
                            i13 += measuredWidth;
                        }
                        if (z5) {
                            virtualChildAt = virtualChildAt;
                            this.mTotalLength += layoutParams.leftMargin + measuredWidth + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt);
                        } else {
                            virtualChildAt = virtualChildAt;
                            int i17 = this.mTotalLength;
                            this.mTotalLength = Math.max(i17, i17 + measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin + getNextLocationOffset(virtualChildAt));
                        }
                        if (z4) {
                            iMax4 = Math.max(measuredWidth, iMax4);
                        }
                        i9 = 1073741824;
                    }
                    if (mode2 == i9 || layoutParams.height != i8) {
                        z = false;
                    } else {
                        z = true;
                        z7 = true;
                    }
                    int i18 = layoutParams.topMargin + layoutParams.bottomMargin;
                    int measuredHeight = virtualChildAt.getMeasuredHeight() + i18;
                    int iCombineMeasuredStates = combineMeasuredStates(i14, virtualChildAt.getMeasuredState());
                    if (z3 && (baseline2 = virtualChildAt.getBaseline()) != i8) {
                        int i19 = ((((layoutParams.gravity < 0 ? this.mGravity : layoutParams.gravity) & 112) >> 4) & (-2)) >> 1;
                        iArr2[i19] = Math.max(iArr2[i19], baseline2);
                        iArr[i19] = Math.max(iArr[i19], measuredHeight - baseline2);
                    }
                    int iMax5 = Math.max(i12, measuredHeight);
                    boolean z10 = z8 && layoutParams.height == -1;
                    if (layoutParams.weight > 0.0f) {
                        if (!z) {
                            i18 = measuredHeight;
                        }
                        iMax3 = Math.max(iMax3, i18);
                    } else {
                        int i20 = iMax3;
                        if (!z) {
                            i18 = measuredHeight;
                        }
                        iMax2 = Math.max(iMax2, i18);
                        iMax3 = i20;
                    }
                    int i21 = childrenSkipCount;
                    childrenSkipCount = getChildrenSkipCount(virtualChildAt, i21) + i21;
                    i14 = iCombineMeasuredStates;
                    i12 = iMax5;
                    z8 = z10;
                }
                childrenSkipCount++;
                iArr3 = iArr;
                z4 = z4;
                z3 = z3;
                i10 = 1073741824;
                z2 = false;
            }
            z4 = z4;
            z3 = z3;
            childrenSkipCount++;
            iArr3 = iArr;
            z4 = z4;
            z3 = z3;
            i10 = 1073741824;
            z2 = false;
        }
        boolean z11 = z4;
        boolean z12 = z3;
        int iMax6 = i12;
        int i22 = iMax2;
        int i23 = iMax3;
        int i24 = i14;
        if (i11 > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerWidth;
        }
        if (iArr2[1] != -1 || iArr2[0] != -1 || iArr2[2] != -1 || iArr2[3] != -1) {
            iMax6 = Math.max(iMax6, Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))) + Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))));
        }
        if (z11 && (mode == Integer.MIN_VALUE || mode == 0)) {
            this.mTotalLength = 0;
            int childrenSkipCount2 = 0;
            while (childrenSkipCount2 < virtualChildCount) {
                View virtualChildAt2 = getVirtualChildAt(childrenSkipCount2);
                if (virtualChildAt2 == null) {
                    this.mTotalLength += measureNullChild(childrenSkipCount2);
                } else if (virtualChildAt2.getVisibility() == 8) {
                    childrenSkipCount2 += getChildrenSkipCount(virtualChildAt2, childrenSkipCount2);
                } else {
                    LayoutParams layoutParams2 = (LayoutParams) virtualChildAt2.getLayoutParams();
                    if (z5) {
                        this.mTotalLength += layoutParams2.leftMargin + iMax4 + layoutParams2.rightMargin + getNextLocationOffset(virtualChildAt2);
                    } else {
                        int i25 = this.mTotalLength;
                        i7 = childrenSkipCount2;
                        this.mTotalLength = Math.max(i25, i25 + iMax4 + layoutParams2.leftMargin + layoutParams2.rightMargin + getNextLocationOffset(virtualChildAt2));
                    }
                    childrenSkipCount2 = i7 + 1;
                }
                i7 = childrenSkipCount2;
                childrenSkipCount2 = i7 + 1;
            }
        }
        this.mTotalLength += this.mPaddingLeft + this.mPaddingRight;
        int iResolveSizeAndState = resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumWidth()), i, 0);
        int i26 = (16777215 & iResolveSizeAndState) - this.mTotalLength;
        if (this.mAllowInconsistentMeasurement) {
            i13 = 0;
        }
        int i27 = i26 + i13;
        if (z6 || ((sRemeasureWeightedChildren || i27 != 0) && f2 > 0.0f)) {
            float f3 = this.mWeightSum > 0.0f ? this.mWeightSum : f2;
            iArr2[3] = -1;
            iArr2[2] = -1;
            iArr2[1] = -1;
            iArr2[0] = -1;
            iArr[3] = -1;
            iArr[2] = -1;
            iArr[1] = -1;
            iArr[0] = -1;
            this.mTotalLength = 0;
            int i28 = i22;
            int iCombineMeasuredStates2 = i24;
            int iMax7 = -1;
            int i29 = 0;
            while (i29 < virtualChildCount) {
                View virtualChildAt3 = getVirtualChildAt(i29);
                if (virtualChildAt3 != null) {
                    i3 = iMax4;
                    if (virtualChildAt3.getVisibility() != 8) {
                        LayoutParams layoutParams3 = (LayoutParams) virtualChildAt3.getLayoutParams();
                        float f4 = layoutParams3.weight;
                        if (f4 > 0.0f) {
                            int measuredWidth2 = (int) ((i27 * f4) / f3);
                            int i30 = i27 - measuredWidth2;
                            float f5 = f3 - f4;
                            if (this.mUseLargestChild) {
                                i6 = 1073741824;
                                if (mode != 1073741824) {
                                    measuredWidth2 = i3;
                                } else {
                                    if (layoutParams3.width == 0) {
                                        i6 = 1073741824;
                                    } else if (this.mAllowInconsistentMeasurement) {
                                        i6 = 1073741824;
                                        if (mode != 1073741824) {
                                        }
                                    } else {
                                        i6 = 1073741824;
                                    }
                                    measuredWidth2 = virtualChildAt3.getMeasuredWidth() + measuredWidth2;
                                }
                            } else {
                                if (layoutParams3.width == 0) {
                                    i6 = 1073741824;
                                } else if (this.mAllowInconsistentMeasurement) {
                                    i6 = 1073741824;
                                    if (mode != 1073741824) {
                                    }
                                } else {
                                    i6 = 1073741824;
                                }
                                measuredWidth2 = virtualChildAt3.getMeasuredWidth() + measuredWidth2;
                            }
                            f = f5;
                            virtualChildAt3.measure(View.MeasureSpec.makeMeasureSpec(Math.max(0, measuredWidth2), i6), getChildMeasureSpec(i2, this.mPaddingTop + this.mPaddingBottom + layoutParams3.topMargin + layoutParams3.bottomMargin, layoutParams3.height));
                            iCombineMeasuredStates2 = combineMeasuredStates(iCombineMeasuredStates2, virtualChildAt3.getMeasuredState() & (-16777216));
                            i27 = i30;
                        } else {
                            f = f3;
                        }
                        if (z5) {
                            this.mTotalLength += virtualChildAt3.getMeasuredWidth() + layoutParams3.leftMargin + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt3);
                        } else {
                            int i31 = this.mTotalLength;
                            this.mTotalLength = Math.max(i31, virtualChildAt3.getMeasuredWidth() + i31 + layoutParams3.leftMargin + layoutParams3.rightMargin + getNextLocationOffset(virtualChildAt3));
                        }
                        boolean z13 = mode2 != 1073741824 && layoutParams3.height == -1;
                        int i32 = layoutParams3.topMargin + layoutParams3.bottomMargin;
                        int measuredHeight2 = virtualChildAt3.getMeasuredHeight() + i32;
                        iMax7 = Math.max(iMax7, measuredHeight2);
                        if (!z13) {
                            i32 = measuredHeight2;
                        }
                        int iMax8 = Math.max(i28, i32);
                        if (z8) {
                            i4 = -1;
                            boolean z14 = layoutParams3.height == -1;
                            if (z12 && (baseline = virtualChildAt3.getBaseline()) != i4) {
                                if (layoutParams3.gravity < 0) {
                                    i5 = this.mGravity;
                                } else {
                                    i5 = layoutParams3.gravity;
                                }
                                int i33 = (((i5 & 112) >> 4) & (-2)) >> 1;
                                iArr2[i33] = Math.max(iArr2[i33], baseline);
                                iArr[i33] = Math.max(iArr[i33], measuredHeight2 - baseline);
                            }
                            z8 = z14;
                            i28 = iMax8;
                        } else {
                            i4 = -1;
                        }
                        if (z12) {
                            if (layoutParams3.gravity < 0) {
                                i5 = this.mGravity;
                            } else {
                                i5 = layoutParams3.gravity;
                            }
                            int i34 = (((i5 & 112) >> 4) & (-2)) >> 1;
                            iArr2[i34] = Math.max(iArr2[i34], baseline);
                            iArr[i34] = Math.max(iArr[i34], measuredHeight2 - baseline);
                        }
                        z8 = z14;
                        i28 = iMax8;
                    }
                    i29++;
                    iMax4 = i3;
                    f3 = f;
                    mode = mode;
                } else {
                    i3 = iMax4;
                }
                f = f3;
                i29++;
                iMax4 = i3;
                f3 = f;
                mode = mode;
            }
            this.mTotalLength += this.mPaddingLeft + this.mPaddingRight;
            int iMax9 = (iArr2[1] == -1 && iArr2[0] == -1 && iArr2[2] == -1 && iArr2[3] == -1) ? iMax7 : Math.max(iMax7, Math.max(iArr2[3], Math.max(iArr2[0], Math.max(iArr2[1], iArr2[2]))) + Math.max(iArr[3], Math.max(iArr[0], Math.max(iArr[1], iArr[2]))));
            i24 = iCombineMeasuredStates2;
            iMax6 = iMax9;
            iMax = i28;
        } else {
            iMax = Math.max(i22, i23);
            if (z11 && mode != 1073741824) {
                for (int i35 = 0; i35 < virtualChildCount; i35++) {
                    View virtualChildAt4 = getVirtualChildAt(i35);
                    if (virtualChildAt4 != null && virtualChildAt4.getVisibility() != 8 && ((LayoutParams) virtualChildAt4.getLayoutParams()).weight > 0.0f) {
                        virtualChildAt4.measure(View.MeasureSpec.makeMeasureSpec(iMax4, 1073741824), View.MeasureSpec.makeMeasureSpec(virtualChildAt4.getMeasuredHeight(), 1073741824));
                    }
                }
            }
        }
        if (z8 || mode2 == 1073741824) {
            iMax = iMax6;
        }
        setMeasuredDimension(iResolveSizeAndState | ((-16777216) & i24), resolveSizeAndState(Math.max(iMax + this.mPaddingTop + this.mPaddingBottom, getSuggestedMinimumHeight()), i2, i24 << 16));
        if (z7) {
            forceUniformHeight(virtualChildCount, i);
        }
    }

    int measureNullChild(int i) {
        return 0;
    }

    /* JADX WARN: Code duplicated, block: B:139:0x02ba  */
    /* JADX WARN: Code duplicated, block: B:141:0x02be  */
    /* JADX WARN: Code duplicated, block: B:143:0x02c2  */
    /* JADX WARN: Code duplicated, block: B:146:0x02c7  */
    /* JADX WARN: Code duplicated, block: B:147:0x02ca  */
    /* JADX WARN: Code duplicated, block: B:160:0x032d  */
    /* JADX WARN: Code duplicated, block: B:166:0x033c  */
    void measureVertical(int i, int i2) {
        int i3;
        int iMax;
        int iMax2;
        int i4;
        int i5;
        int i6;
        int i7;
        boolean z;
        boolean z2;
        int i8;
        int i9;
        boolean z3;
        int i10;
        int i11;
        int i12;
        int i13;
        LayoutParams layoutParams;
        int i14;
        View view;
        int i15;
        boolean z4;
        int iMax3;
        this.mTotalLength = 0;
        int virtualChildCount = getVirtualChildCount();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int i16 = this.mBaselineAlignedChildIndex;
        boolean z5 = this.mUseLargestChild;
        int i17 = 0;
        int iMax4 = 0;
        int i18 = 0;
        int childrenSkipCount = 0;
        int i19 = 0;
        boolean z6 = false;
        int i20 = 0;
        int i21 = 0;
        boolean z7 = false;
        float f = 0.0f;
        boolean z8 = true;
        int iMax5 = Integer.MIN_VALUE;
        while (childrenSkipCount < virtualChildCount) {
            int i22 = iMax5;
            View virtualChildAt = getVirtualChildAt(childrenSkipCount);
            if (virtualChildAt == null) {
                this.mTotalLength += measureNullChild(childrenSkipCount);
            } else {
                if (virtualChildAt.getVisibility() == 8) {
                    childrenSkipCount += getChildrenSkipCount(virtualChildAt, childrenSkipCount);
                } else {
                    int i23 = i17 + 1;
                    if (hasDividerBeforeChildAt(childrenSkipCount)) {
                        this.mTotalLength += this.mDividerHeight;
                    }
                    LayoutParams layoutParams2 = (LayoutParams) virtualChildAt.getLayoutParams();
                    float f2 = f + layoutParams2.weight;
                    if (layoutParams2.height != 0 || layoutParams2.weight <= 0.0f) {
                        i9 = 1073741824;
                        z3 = false;
                    } else {
                        z3 = true;
                        i9 = 1073741824;
                    }
                    if (mode2 == i9 && z3) {
                        int i24 = this.mTotalLength;
                        this.mTotalLength = Math.max(i24, layoutParams2.topMargin + i24 + layoutParams2.bottomMargin);
                        i13 = childrenSkipCount;
                        layoutParams = layoutParams2;
                        i11 = i23;
                        i14 = mode2;
                        z6 = true;
                        i10 = iMax4;
                        i12 = i18;
                        view = virtualChildAt;
                        iMax5 = i22;
                    } else {
                        int i25 = iMax4;
                        if (z3) {
                            layoutParams2.height = -2;
                        }
                        i10 = i25;
                        i11 = i23;
                        i12 = i18;
                        i13 = childrenSkipCount;
                        layoutParams = layoutParams2;
                        i14 = mode2;
                        measureChildBeforeLayout(virtualChildAt, childrenSkipCount, i, 0, i2, f2 == 0.0f ? this.mTotalLength : 0);
                        int measuredHeight = virtualChildAt.getMeasuredHeight();
                        if (z3) {
                            layoutParams.height = 0;
                            i19 += measuredHeight;
                        }
                        int i26 = this.mTotalLength;
                        view = virtualChildAt;
                        this.mTotalLength = Math.max(i26, i26 + measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin + getNextLocationOffset(view));
                        iMax5 = z5 ? Math.max(measuredHeight, i22) : i22;
                    }
                    if (i16 >= 0) {
                        i15 = i13;
                        if (i16 == i15 + 1) {
                            this.mBaselineChildTop = this.mTotalLength;
                        }
                    } else {
                        i15 = i13;
                    }
                    if (i15 < i16 && layoutParams.weight > 0.0f) {
                        throw new RuntimeException("A child of LinearLayout with index less than mBaselineAlignedChildIndex has weight > 0, which won't work.  Either remove the weight, or don't set mBaselineAlignedChildIndex.");
                    }
                    if (mode == 1073741824 || layoutParams.width != -1) {
                        z4 = false;
                    } else {
                        z4 = true;
                        z7 = true;
                    }
                    int i27 = layoutParams.leftMargin + layoutParams.rightMargin;
                    int measuredWidth = view.getMeasuredWidth() + i27;
                    int iMax6 = Math.max(i20, measuredWidth);
                    int iCombineMeasuredStates = combineMeasuredStates(i21, view.getMeasuredState());
                    boolean z9 = z8 && layoutParams.width == -1;
                    if (layoutParams.weight > 0.0f) {
                        if (!z4) {
                            i27 = measuredWidth;
                        }
                        iMax4 = Math.max(i10, i27);
                        iMax3 = i12;
                    } else {
                        int i28 = i10;
                        if (!z4) {
                            i27 = measuredWidth;
                        }
                        iMax3 = Math.max(i12, i27);
                        iMax4 = i28;
                    }
                    childrenSkipCount = i15 + getChildrenSkipCount(view, i15);
                    i18 = iMax3;
                    i21 = iCombineMeasuredStates;
                    z8 = z9;
                    f = f2;
                    i17 = i11;
                    i20 = iMax6;
                }
                childrenSkipCount++;
                mode2 = i14;
            }
            i14 = mode2;
            iMax5 = i22;
            childrenSkipCount++;
            mode2 = i14;
        }
        int i29 = iMax4;
        int i30 = i18;
        int i31 = iMax5;
        int i32 = 8;
        int i33 = mode2;
        int iCombineMeasuredStates2 = i21;
        if (i17 > 0 && hasDividerBeforeChildAt(virtualChildCount)) {
            this.mTotalLength += this.mDividerHeight;
        }
        if (z5) {
            i3 = i33;
            if (i3 == Integer.MIN_VALUE || i3 == 0) {
                this.mTotalLength = 0;
                int childrenSkipCount2 = 0;
                while (childrenSkipCount2 < virtualChildCount) {
                    View virtualChildAt2 = getVirtualChildAt(childrenSkipCount2);
                    if (virtualChildAt2 == null) {
                        this.mTotalLength += measureNullChild(childrenSkipCount2);
                    } else if (virtualChildAt2.getVisibility() == i32) {
                        childrenSkipCount2 += getChildrenSkipCount(virtualChildAt2, childrenSkipCount2);
                    } else {
                        LayoutParams layoutParams3 = (LayoutParams) virtualChildAt2.getLayoutParams();
                        int i34 = this.mTotalLength;
                        this.mTotalLength = Math.max(i34, i34 + i31 + layoutParams3.topMargin + layoutParams3.bottomMargin + getNextLocationOffset(virtualChildAt2));
                    }
                    childrenSkipCount2++;
                    i32 = 8;
                }
            }
        } else {
            i3 = i33;
        }
        this.mTotalLength += this.mPaddingTop + this.mPaddingBottom;
        int iResolveSizeAndState = resolveSizeAndState(Math.max(this.mTotalLength, getSuggestedMinimumHeight()), i2, 0);
        int i35 = (16777215 & iResolveSizeAndState) - this.mTotalLength;
        if (this.mAllowInconsistentMeasurement) {
            i19 = 0;
        }
        int i36 = i35 + i19;
        if (z6 || ((sRemeasureWeightedChildren || i36 != 0) && f > 0.0f)) {
            if (this.mWeightSum > 0.0f) {
                f = this.mWeightSum;
            }
            this.mTotalLength = 0;
            float f3 = f;
            int i37 = i36;
            iMax = i20;
            int i38 = 0;
            while (i38 < virtualChildCount) {
                View virtualChildAt3 = getVirtualChildAt(i38);
                if (virtualChildAt3 == null || virtualChildAt3.getVisibility() == 8) {
                    i3 = i3;
                    i4 = i31;
                    i5 = i37;
                } else {
                    LayoutParams layoutParams4 = (LayoutParams) virtualChildAt3.getLayoutParams();
                    float f4 = layoutParams4.weight;
                    if (f4 > 0.0f) {
                        int i39 = i31;
                        int measuredHeight2 = (int) ((i37 * f4) / f3);
                        int i40 = i37 - measuredHeight2;
                        f3 -= f4;
                        if (this.mUseLargestChild) {
                            i8 = 1073741824;
                            if (i3 != 1073741824) {
                                measuredHeight2 = i39;
                            } else {
                                if (layoutParams4.height == 0) {
                                    i8 = 1073741824;
                                } else if (this.mAllowInconsistentMeasurement) {
                                    i8 = 1073741824;
                                    if (i3 != 1073741824) {
                                    }
                                } else {
                                    i8 = 1073741824;
                                }
                                measuredHeight2 = virtualChildAt3.getMeasuredHeight() + measuredHeight2;
                            }
                        } else {
                            if (layoutParams4.height == 0) {
                                i8 = 1073741824;
                            } else if (this.mAllowInconsistentMeasurement) {
                                i8 = 1073741824;
                                if (i3 != 1073741824) {
                                }
                            } else {
                                i8 = 1073741824;
                            }
                            measuredHeight2 = virtualChildAt3.getMeasuredHeight() + measuredHeight2;
                        }
                        i5 = i40;
                        i4 = i39;
                        virtualChildAt3.measure(getChildMeasureSpec(i, this.mPaddingLeft + this.mPaddingRight + layoutParams4.leftMargin + layoutParams4.rightMargin, layoutParams4.width), View.MeasureSpec.makeMeasureSpec(Math.max(0, measuredHeight2), i8));
                        iCombineMeasuredStates2 = combineMeasuredStates(iCombineMeasuredStates2, virtualChildAt3.getMeasuredState() & (-256));
                    } else {
                        i3 = i3;
                        i4 = i31;
                        i5 = i37;
                    }
                    int i41 = layoutParams4.leftMargin + layoutParams4.rightMargin;
                    int measuredWidth2 = virtualChildAt3.getMeasuredWidth() + i41;
                    iMax = Math.max(iMax, measuredWidth2);
                    if (mode != 1073741824) {
                        i6 = i41;
                        i7 = -1;
                        z = layoutParams4.width == -1;
                        if (z) {
                            measuredWidth2 = i6;
                        }
                        int iMax7 = Math.max(i30, measuredWidth2);
                        if (z8 || layoutParams4.width != i7) {
                            z2 = false;
                        } else {
                            z2 = true;
                        }
                        int i42 = this.mTotalLength;
                        this.mTotalLength = Math.max(i42, i42 + virtualChildAt3.getMeasuredHeight() + layoutParams4.topMargin + layoutParams4.bottomMargin + getNextLocationOffset(virtualChildAt3));
                        z8 = z2;
                        i30 = iMax7;
                    } else {
                        i6 = i41;
                        i7 = -1;
                    }
                    if (z) {
                        measuredWidth2 = i6;
                    }
                    int iMax8 = Math.max(i30, measuredWidth2);
                    if (z8) {
                        z2 = false;
                    } else {
                        z2 = false;
                    }
                    int i43 = this.mTotalLength;
                    this.mTotalLength = Math.max(i43, i43 + virtualChildAt3.getMeasuredHeight() + layoutParams4.topMargin + layoutParams4.bottomMargin + getNextLocationOffset(virtualChildAt3));
                    z8 = z2;
                    i30 = iMax8;
                }
                i38++;
                i31 = i4;
                i3 = i3;
                i37 = i5;
            }
            this.mTotalLength += this.mPaddingTop + this.mPaddingBottom;
            iMax2 = i30;
        } else {
            iMax2 = Math.max(i30, i29);
            if (z5 && i3 != 1073741824) {
                for (int i44 = 0; i44 < virtualChildCount; i44++) {
                    View virtualChildAt4 = getVirtualChildAt(i44);
                    if (virtualChildAt4 != null && virtualChildAt4.getVisibility() != 8 && ((LayoutParams) virtualChildAt4.getLayoutParams()).weight > 0.0f) {
                        virtualChildAt4.measure(View.MeasureSpec.makeMeasureSpec(virtualChildAt4.getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(i31, 1073741824));
                    }
                }
            }
            iMax = i20;
        }
        if (z8 || mode == 1073741824) {
            iMax2 = iMax;
        }
        setMeasuredDimension(resolveSizeAndState(Math.max(iMax2 + this.mPaddingLeft + this.mPaddingRight, getSuggestedMinimumWidth()), i, iCombineMeasuredStates2), iResolveSizeAndState);
        if (z7) {
            forceUniformWidth(virtualChildCount, i2);
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mDivider == null) {
            return;
        }
        if (this.mOrientation == 1) {
            drawDividersVertical(canvas);
        } else {
            drawDividersHorizontal(canvas);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (this.mOrientation == 1) {
            layoutVertical(i, i2, i3, i4);
        } else {
            layoutHorizontal(i, i2, i3, i4);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        if (this.mOrientation == 1) {
            measureVertical(i, i2);
        } else {
            measureHorizontal(i, i2);
        }
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        if (i != this.mLayoutDirection) {
            this.mLayoutDirection = i;
            if (this.mOrientation == 0) {
                requestLayout();
            }
        }
    }

    @RemotableViewMethod
    public void setBaselineAligned(boolean z) {
        this.mBaselineAligned = z;
    }

    @RemotableViewMethod
    public void setBaselineAlignedChildIndex(int i) {
        if (i >= 0 && i < getChildCount()) {
            this.mBaselineAlignedChildIndex = i;
            return;
        }
        throw new IllegalArgumentException("base aligned child index out of range (0, " + getChildCount() + ")");
    }

    public void setDividerDrawable(Drawable drawable) {
        if (drawable == this.mDivider) {
            return;
        }
        this.mDivider = drawable;
        if (drawable != null) {
            this.mDividerWidth = drawable.getIntrinsicWidth();
            this.mDividerHeight = drawable.getIntrinsicHeight();
        } else {
            this.mDividerWidth = 0;
            this.mDividerHeight = 0;
        }
        setWillNotDraw(!isShowingDividers());
        requestLayout();
    }

    public void setDividerPadding(int i) {
        if (i == this.mDividerPadding) {
            return;
        }
        this.mDividerPadding = i;
        if (isShowingDividers()) {
            requestLayout();
            invalidate();
        }
    }

    @RemotableViewMethod
    public void setGravity(int i) {
        if (this.mGravity != i) {
            if ((8388615 & i) == 0) {
                i |= 8388611;
            }
            if ((i & 112) == 0) {
                i |= 48;
            }
            this.mGravity = i;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setHorizontalGravity(int i) {
        int i2 = i & 8388615;
        if ((8388615 & this.mGravity) != i2) {
            this.mGravity = i2 | (this.mGravity & (-8388616));
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setMeasureWithLargestChildEnabled(boolean z) {
        this.mUseLargestChild = z;
    }

    public void setOrientation(int i) {
        if (this.mOrientation != i) {
            this.mOrientation = i;
            requestLayout();
        }
    }

    public void setShowDividers(int i) {
        if (i == this.mShowDividers) {
            return;
        }
        this.mShowDividers = i;
        setWillNotDraw(!isShowingDividers());
        requestLayout();
    }

    @RemotableViewMethod
    public void setVerticalGravity(int i) {
        int i2 = i & 112;
        if ((this.mGravity & 112) != i2) {
            this.mGravity = i2 | (this.mGravity & PackageManager.INSTALL_FAILED_NO_MATCHING_ABIS);
            requestLayout();
        }
    }

    @RemotableViewMethod
    public void setWeightSum(float f) {
        this.mWeightSum = Math.max(0.0f, f);
    }

    @Override // android.view.ViewGroup
    public boolean shouldDelayChildPressedState() {
        return false;
    }
}
