package com.yftech.car.widget.media.flexbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import com.yftech.car.widget.media.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class FlexboxLayout extends ViewGroup implements FlexContainer {
    public static final int SHOW_DIVIDER_BEGINNING = 1;
    public static final int SHOW_DIVIDER_END = 4;
    public static final int SHOW_DIVIDER_MIDDLE = 2;
    public static final int SHOW_DIVIDER_NONE = 0;
    private int mAlignContent;
    private int mAlignItems;

    @Nullable
    private Drawable mDividerDrawableHorizontal;

    @Nullable
    private Drawable mDividerDrawableVertical;
    private int mDividerHorizontalHeight;
    private int mDividerVerticalWidth;
    private int mFlexDirection;
    private List<FlexLine> mFlexLines;
    private FlexboxHelper.FlexLinesResult mFlexLinesResult;
    private int mFlexWrap;
    private FlexboxHelper mFlexboxHelper;
    private int mJustifyContent;
    private int mMaxLine;
    private SparseIntArray mOrderCache;
    private int[] mReorderedIndices;
    private int mShowDividerHorizontal;
    private int mShowDividerVertical;

    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerMode {
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams implements FlexItem {
        public static final Parcelable.Creator<LayoutParams> CREATOR = new Parcelable.Creator<LayoutParams>() { // from class: com.yftech.car.widget.media.flexbox.FlexboxLayout.LayoutParams.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public LayoutParams createFromParcel(Parcel parcel) {
                return new LayoutParams(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public LayoutParams[] newArray(int i) {
                return new LayoutParams[i];
            }
        };
        private int mAlignSelf;
        private float mFlexBasisPercent;
        private float mFlexGrow;
        private float mFlexShrink;
        private int mMaxHeight;
        private int mMaxWidth;
        private int mMinHeight;
        private int mMinWidth;
        private int mOrder;
        private boolean mWrapBefore;

        public LayoutParams(int i, int i2) {
            super(new ViewGroup.LayoutParams(i, i2));
            this.mOrder = 1;
            this.mFlexGrow = 0.0f;
            this.mFlexShrink = 1.0f;
            this.mAlignSelf = -1;
            this.mFlexBasisPercent = -1.0f;
            this.mMinWidth = -1;
            this.mMinHeight = -1;
            this.mMaxWidth = 16777215;
            this.mMaxHeight = 16777215;
        }

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.mOrder = 1;
            this.mFlexGrow = 0.0f;
            this.mFlexShrink = 1.0f;
            this.mAlignSelf = -1;
            this.mFlexBasisPercent = -1.0f;
            this.mMinWidth = -1;
            this.mMinHeight = -1;
            this.mMaxWidth = 16777215;
            this.mMaxHeight = 16777215;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FlexboxLayout_Layout);
            this.mOrder = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_Layout_layout_order, 1);
            this.mFlexGrow = typedArrayObtainStyledAttributes.getFloat(R.styleable.FlexboxLayout_Layout_layout_flexGrow, 0.0f);
            this.mFlexShrink = typedArrayObtainStyledAttributes.getFloat(R.styleable.FlexboxLayout_Layout_layout_flexShrink, 1.0f);
            this.mAlignSelf = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_Layout_layout_alignSelf, -1);
            this.mFlexBasisPercent = typedArrayObtainStyledAttributes.getFraction(R.styleable.FlexboxLayout_Layout_layout_flexBasisPercent, 1, 1, -1.0f);
            this.mMinWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.FlexboxLayout_Layout_layout_minWidth, -1);
            this.mMinHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.FlexboxLayout_Layout_layout_minHeight, -1);
            this.mMaxWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.FlexboxLayout_Layout_layout_maxWidth, 16777215);
            this.mMaxHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.FlexboxLayout_Layout_layout_maxHeight, 16777215);
            this.mWrapBefore = typedArrayObtainStyledAttributes.getBoolean(R.styleable.FlexboxLayout_Layout_layout_wrapBefore, false);
            typedArrayObtainStyledAttributes.recycle();
        }

        protected LayoutParams(Parcel parcel) {
            super(0, 0);
            this.mOrder = 1;
            this.mFlexGrow = 0.0f;
            this.mFlexShrink = 1.0f;
            this.mAlignSelf = -1;
            this.mFlexBasisPercent = -1.0f;
            this.mMinWidth = -1;
            this.mMinHeight = -1;
            this.mMaxWidth = 16777215;
            this.mMaxHeight = 16777215;
            this.mOrder = parcel.readInt();
            this.mFlexGrow = parcel.readFloat();
            this.mFlexShrink = parcel.readFloat();
            this.mAlignSelf = parcel.readInt();
            this.mFlexBasisPercent = parcel.readFloat();
            this.mMinWidth = parcel.readInt();
            this.mMinHeight = parcel.readInt();
            this.mMaxWidth = parcel.readInt();
            this.mMaxHeight = parcel.readInt();
            this.mWrapBefore = parcel.readByte() != 0;
            this.bottomMargin = parcel.readInt();
            this.leftMargin = parcel.readInt();
            this.rightMargin = parcel.readInt();
            this.topMargin = parcel.readInt();
            this.height = parcel.readInt();
            this.width = parcel.readInt();
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
            this.mOrder = 1;
            this.mFlexGrow = 0.0f;
            this.mFlexShrink = 1.0f;
            this.mAlignSelf = -1;
            this.mFlexBasisPercent = -1.0f;
            this.mMinWidth = -1;
            this.mMinHeight = -1;
            this.mMaxWidth = 16777215;
            this.mMaxHeight = 16777215;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
            this.mOrder = 1;
            this.mFlexGrow = 0.0f;
            this.mFlexShrink = 1.0f;
            this.mAlignSelf = -1;
            this.mFlexBasisPercent = -1.0f;
            this.mMinWidth = -1;
            this.mMinHeight = -1;
            this.mMaxWidth = 16777215;
            this.mMaxHeight = 16777215;
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((ViewGroup.MarginLayoutParams) layoutParams);
            this.mOrder = 1;
            this.mFlexGrow = 0.0f;
            this.mFlexShrink = 1.0f;
            this.mAlignSelf = -1;
            this.mFlexBasisPercent = -1.0f;
            this.mMinWidth = -1;
            this.mMinHeight = -1;
            this.mMaxWidth = 16777215;
            this.mMaxHeight = 16777215;
            this.mOrder = layoutParams.mOrder;
            this.mFlexGrow = layoutParams.mFlexGrow;
            this.mFlexShrink = layoutParams.mFlexShrink;
            this.mAlignSelf = layoutParams.mAlignSelf;
            this.mFlexBasisPercent = layoutParams.mFlexBasisPercent;
            this.mMinWidth = layoutParams.mMinWidth;
            this.mMinHeight = layoutParams.mMinHeight;
            this.mMaxWidth = layoutParams.mMaxWidth;
            this.mMaxHeight = layoutParams.mMaxHeight;
            this.mWrapBefore = layoutParams.mWrapBefore;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getAlignSelf() {
            return this.mAlignSelf;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public float getFlexBasisPercent() {
            return this.mFlexBasisPercent;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public float getFlexGrow() {
            return this.mFlexGrow;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public float getFlexShrink() {
            return this.mFlexShrink;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getHeight() {
            return this.height;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getMarginBottom() {
            return this.bottomMargin;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getMarginLeft() {
            return this.leftMargin;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getMarginRight() {
            return this.rightMargin;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getMarginTop() {
            return this.topMargin;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getMaxHeight() {
            return this.mMaxHeight;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getMaxWidth() {
            return this.mMaxWidth;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getMinHeight() {
            return this.mMinHeight;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getMinWidth() {
            return this.mMinWidth;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getOrder() {
            return this.mOrder;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public int getWidth() {
            return this.width;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public boolean isWrapBefore() {
            return this.mWrapBefore;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setAlignSelf(int i) {
            this.mAlignSelf = i;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setFlexBasisPercent(float f) {
            this.mFlexBasisPercent = f;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setFlexGrow(float f) {
            this.mFlexGrow = f;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setFlexShrink(float f) {
            this.mFlexShrink = f;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setHeight(int i) {
            this.height = i;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setMaxHeight(int i) {
            this.mMaxHeight = i;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setMaxWidth(int i) {
            this.mMaxWidth = i;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setMinHeight(int i) {
            this.mMinHeight = i;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setMinWidth(int i) {
            this.mMinWidth = i;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setOrder(int i) {
            this.mOrder = i;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setWidth(int i) {
            this.width = i;
        }

        @Override // com.yftech.car.widget.media.flexbox.FlexItem
        public void setWrapBefore(boolean z) {
            this.mWrapBefore = z;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mOrder);
            parcel.writeFloat(this.mFlexGrow);
            parcel.writeFloat(this.mFlexShrink);
            parcel.writeInt(this.mAlignSelf);
            parcel.writeFloat(this.mFlexBasisPercent);
            parcel.writeInt(this.mMinWidth);
            parcel.writeInt(this.mMinHeight);
            parcel.writeInt(this.mMaxWidth);
            parcel.writeInt(this.mMaxHeight);
            parcel.writeByte(this.mWrapBefore ? (byte) 1 : (byte) 0);
            parcel.writeInt(this.bottomMargin);
            parcel.writeInt(this.leftMargin);
            parcel.writeInt(this.rightMargin);
            parcel.writeInt(this.topMargin);
            parcel.writeInt(this.height);
            parcel.writeInt(this.width);
        }
    }

    public FlexboxLayout(Context context) {
        this(context, null);
    }

    public FlexboxLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public FlexboxLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mMaxLine = -1;
        this.mFlexboxHelper = new FlexboxHelper(this);
        this.mFlexLines = new ArrayList();
        this.mFlexLinesResult = new FlexboxHelper.FlexLinesResult();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FlexboxLayout, i, 0);
        this.mFlexDirection = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_flexDirection, 0);
        this.mFlexWrap = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_flexWrap, 0);
        this.mJustifyContent = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_justifyContent, 0);
        this.mAlignItems = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_alignItems, 0);
        this.mAlignContent = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_alignContent, 0);
        this.mMaxLine = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_maxLine, -1);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.FlexboxLayout_dividerDrawable);
        if (drawable != null) {
            setDividerDrawableHorizontal(drawable);
            setDividerDrawableVertical(drawable);
        }
        Drawable drawable2 = typedArrayObtainStyledAttributes.getDrawable(R.styleable.FlexboxLayout_dividerDrawableHorizontal);
        if (drawable2 != null) {
            setDividerDrawableHorizontal(drawable2);
        }
        Drawable drawable3 = typedArrayObtainStyledAttributes.getDrawable(R.styleable.FlexboxLayout_dividerDrawableVertical);
        if (drawable3 != null) {
            setDividerDrawableVertical(drawable3);
        }
        int i2 = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_showDivider, 0);
        if (i2 != 0) {
            this.mShowDividerVertical = i2;
            this.mShowDividerHorizontal = i2;
        }
        int i3 = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_showDividerVertical, 0);
        if (i3 != 0) {
            this.mShowDividerVertical = i3;
        }
        int i4 = typedArrayObtainStyledAttributes.getInt(R.styleable.FlexboxLayout_showDividerHorizontal, 0);
        if (i4 != 0) {
            this.mShowDividerHorizontal = i4;
        }
        typedArrayObtainStyledAttributes.recycle();
    }

    private boolean allFlexLinesAreDummyBefore(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            if (this.mFlexLines.get(i2).getItemCountNotGone() > 0) {
                return false;
            }
        }
        return true;
    }

    private boolean allViewsAreGoneBefore(int i, int i2) {
        for (int i3 = 1; i3 <= i2; i3++) {
            View reorderedChildAt = getReorderedChildAt(i - i3);
            if (reorderedChildAt != null && reorderedChildAt.getVisibility() != 8) {
                return false;
            }
        }
        return true;
    }

    private void drawDividersHorizontal(Canvas canvas, boolean z, boolean z2) {
        int paddingLeft = getPaddingLeft();
        int iMax = Math.max(0, (getWidth() - getPaddingRight()) - paddingLeft);
        int size = this.mFlexLines.size();
        for (int i = 0; i < size; i++) {
            FlexLine flexLine = this.mFlexLines.get(i);
            for (int i2 = 0; i2 < flexLine.mItemCount; i2++) {
                int i3 = flexLine.mFirstIndex + i2;
                View reorderedChildAt = getReorderedChildAt(i3);
                if (reorderedChildAt != null && reorderedChildAt.getVisibility() != 8) {
                    LayoutParams layoutParams = (LayoutParams) reorderedChildAt.getLayoutParams();
                    if (hasDividerBeforeChildAtAlongMainAxis(i3, i2)) {
                        drawVerticalDivider(canvas, z ? reorderedChildAt.getRight() + layoutParams.rightMargin : (reorderedChildAt.getLeft() - layoutParams.leftMargin) - this.mDividerVerticalWidth, flexLine.mTop, flexLine.mCrossSize);
                    }
                    if (i2 == flexLine.mItemCount - 1 && (this.mShowDividerVertical & 4) > 0) {
                        drawVerticalDivider(canvas, z ? (reorderedChildAt.getLeft() - layoutParams.leftMargin) - this.mDividerVerticalWidth : reorderedChildAt.getRight() + layoutParams.rightMargin, flexLine.mTop, flexLine.mCrossSize);
                    }
                }
            }
            if (hasDividerBeforeFlexLine(i)) {
                drawHorizontalDivider(canvas, paddingLeft, z2 ? flexLine.mBottom : flexLine.mTop - this.mDividerHorizontalHeight, iMax);
            }
            if (hasEndDividerAfterFlexLine(i) && (this.mShowDividerHorizontal & 4) > 0) {
                drawHorizontalDivider(canvas, paddingLeft, z2 ? flexLine.mTop - this.mDividerHorizontalHeight : flexLine.mBottom, iMax);
            }
        }
    }

    private void drawDividersVertical(Canvas canvas, boolean z, boolean z2) {
        int paddingTop = getPaddingTop();
        int iMax = Math.max(0, (getHeight() - getPaddingBottom()) - paddingTop);
        int size = this.mFlexLines.size();
        for (int i = 0; i < size; i++) {
            FlexLine flexLine = this.mFlexLines.get(i);
            for (int i2 = 0; i2 < flexLine.mItemCount; i2++) {
                int i3 = flexLine.mFirstIndex + i2;
                View reorderedChildAt = getReorderedChildAt(i3);
                if (reorderedChildAt != null && reorderedChildAt.getVisibility() != 8) {
                    LayoutParams layoutParams = (LayoutParams) reorderedChildAt.getLayoutParams();
                    if (hasDividerBeforeChildAtAlongMainAxis(i3, i2)) {
                        drawHorizontalDivider(canvas, flexLine.mLeft, z2 ? reorderedChildAt.getBottom() + layoutParams.bottomMargin : (reorderedChildAt.getTop() - layoutParams.topMargin) - this.mDividerHorizontalHeight, flexLine.mCrossSize);
                    }
                    if (i2 == flexLine.mItemCount - 1 && (this.mShowDividerHorizontal & 4) > 0) {
                        drawHorizontalDivider(canvas, flexLine.mLeft, z2 ? (reorderedChildAt.getTop() - layoutParams.topMargin) - this.mDividerHorizontalHeight : reorderedChildAt.getBottom() + layoutParams.bottomMargin, flexLine.mCrossSize);
                    }
                }
            }
            if (hasDividerBeforeFlexLine(i)) {
                drawVerticalDivider(canvas, z ? flexLine.mRight : flexLine.mLeft - this.mDividerVerticalWidth, paddingTop, iMax);
            }
            if (hasEndDividerAfterFlexLine(i) && (this.mShowDividerVertical & 4) > 0) {
                drawVerticalDivider(canvas, z ? flexLine.mLeft - this.mDividerVerticalWidth : flexLine.mRight, paddingTop, iMax);
            }
        }
    }

    private void drawHorizontalDivider(Canvas canvas, int i, int i2, int i3) {
        if (this.mDividerDrawableHorizontal == null) {
            return;
        }
        this.mDividerDrawableHorizontal.setBounds(i, i2, i3 + i, this.mDividerHorizontalHeight + i2);
        this.mDividerDrawableHorizontal.draw(canvas);
    }

    private void drawVerticalDivider(Canvas canvas, int i, int i2, int i3) {
        if (this.mDividerDrawableVertical == null) {
            return;
        }
        this.mDividerDrawableVertical.setBounds(i, i2, this.mDividerVerticalWidth + i, i3 + i2);
        this.mDividerDrawableVertical.draw(canvas);
    }

    private boolean hasDividerBeforeChildAtAlongMainAxis(int i, int i2) {
        if (allViewsAreGoneBefore(i, i2)) {
            if (isMainAxisDirectionHorizontal()) {
                return (this.mShowDividerVertical & 1) != 0;
            }
            return (this.mShowDividerHorizontal & 1) != 0;
        }
        if (isMainAxisDirectionHorizontal()) {
            return (this.mShowDividerVertical & 2) != 0;
        }
        return (this.mShowDividerHorizontal & 2) != 0;
    }

    private boolean hasDividerBeforeFlexLine(int i) {
        if (i < 0 || i >= this.mFlexLines.size()) {
            return false;
        }
        if (allFlexLinesAreDummyBefore(i)) {
            if (isMainAxisDirectionHorizontal()) {
                return (this.mShowDividerHorizontal & 1) != 0;
            }
            return (this.mShowDividerVertical & 1) != 0;
        }
        if (isMainAxisDirectionHorizontal()) {
            return (this.mShowDividerHorizontal & 2) != 0;
        }
        return (this.mShowDividerVertical & 2) != 0;
    }

    private boolean hasEndDividerAfterFlexLine(int i) {
        if (i < 0 || i >= this.mFlexLines.size()) {
            return false;
        }
        for (int i2 = i + 1; i2 < this.mFlexLines.size(); i2++) {
            if (this.mFlexLines.get(i2).getItemCountNotGone() > 0) {
                return false;
            }
        }
        if (isMainAxisDirectionHorizontal()) {
            return (this.mShowDividerHorizontal & 4) != 0;
        }
        return (this.mShowDividerVertical & 4) != 0;
    }

    /* JADX WARN: Code duplicated, block: B:33:0x00d4  */
    /* JADX WARN: Code duplicated, block: B:35:0x00dd  */
    /* JADX WARN: Code duplicated, block: B:37:0x00e7  */
    /* JADX WARN: Code duplicated, block: B:38:0x00ed  */
    /* JADX WARN: Code duplicated, block: B:40:0x0101  */
    /* JADX WARN: Code duplicated, block: B:41:0x010d  */
    /* JADX WARN: Code duplicated, block: B:44:0x0119  */
    /* JADX WARN: Code duplicated, block: B:47:0x0124  */
    /* JADX WARN: Code duplicated, block: B:50:0x012b A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:51:0x012d  */
    /* JADX WARN: Code duplicated, block: B:52:0x0156  */
    /* JADX WARN: Code duplicated, block: B:53:0x0179  */
    /* JADX WARN: Code duplicated, block: B:55:0x017f  */
    /* JADX WARN: Code duplicated, block: B:56:0x019d  */
    /* JADX WARN: Code duplicated, block: B:59:0x01d4  */
    /* JADX WARN: Code duplicated, block: B:60:0x01e2  */
    /* JADX WARN: Code duplicated, block: B:62:0x01f4  */
    private void layoutHorizontal(boolean z, int i, int i2, int i3, int i4) {
        float f;
        float f2;
        float f3;
        float fMax;
        float f4;
        int i5;
        int i6;
        View reorderedChildAt;
        int i7;
        int i8;
        boolean z2;
        float f5;
        float f6;
        float f7;
        float f8;
        int i9;
        int i10;
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int i11 = i3 - i;
        int paddingBottom = (i4 - i2) - getPaddingBottom();
        int paddingTop = getPaddingTop();
        int size = this.mFlexLines.size();
        int i12 = paddingTop;
        int i13 = paddingBottom;
        int i14 = 0;
        while (i14 < size) {
            FlexLine flexLine = this.mFlexLines.get(i14);
            if (hasDividerBeforeFlexLine(i14)) {
                i13 -= this.mDividerHorizontalHeight;
                i12 += this.mDividerHorizontalHeight;
            }
            boolean z3 = true;
            switch (this.mJustifyContent) {
                case 0:
                    f = paddingLeft;
                    f2 = i11 - paddingRight;
                    f3 = 0.0f;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingLeft;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z2 = true;
                            } else {
                                LayoutParams layoutParams = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams.leftMargin;
                                f6 = f4 - layoutParams.rightMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i15 = this.mDividerVerticalWidth;
                                    float f9 = i15;
                                    f7 = f6 - f9;
                                    i9 = i15;
                                    f8 = f5 + f9;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1 || (this.mShowDividerVertical & 4) <= 0) {
                                    i10 = 0;
                                } else {
                                    i10 = this.mDividerVerticalWidth;
                                }
                                if (this.mFlexWrap == 2) {
                                    i8 = i5;
                                    z2 = true;
                                    if (z) {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i12, Math.round(f7), i12 + reorderedChildAt.getMeasuredHeight());
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i12, Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i12 + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z) {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f7), i13);
                                } else {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i13);
                                }
                                float measuredWidth = f8 + reorderedChildAt.getMeasuredWidth() + fMax + layoutParams.rightMargin;
                                float measuredWidth2 = f7 - ((reorderedChildAt.getMeasuredWidth() + fMax) + layoutParams.leftMargin);
                                if (z) {
                                    flexLine.updatePositionFromView(reorderedChildAt, i10, 0, i9, 0);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, i9, 0, i10, 0);
                                }
                                f4 = measuredWidth2;
                                f = measuredWidth;
                            }
                        } else {
                            i7 = paddingLeft;
                            i8 = i5;
                            z2 = z3;
                        }
                        i5 = i8 + 1;
                        paddingLeft = i7;
                        z3 = z2;
                    }
                    i12 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i14++;
                    paddingLeft = paddingLeft;
                    break;
                case 1:
                    f = (i11 - flexLine.mMainSize) + paddingRight;
                    f2 = flexLine.mMainSize - paddingLeft;
                    f3 = 0.0f;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingLeft;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z2 = true;
                            } else {
                                LayoutParams layoutParams2 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams2.leftMargin;
                                f6 = f4 - layoutParams2.rightMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i16 = this.mDividerVerticalWidth;
                                    float f10 = i16;
                                    f7 = f6 - f10;
                                    i9 = i16;
                                    f8 = f5 + f10;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (this.mFlexWrap == 2) {
                                    i8 = i5;
                                    z2 = true;
                                    if (z) {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i12, Math.round(f7), i12 + reorderedChildAt.getMeasuredHeight());
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i12, Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i12 + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z) {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f7), i13);
                                } else {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i13);
                                }
                                float measuredWidth3 = f8 + reorderedChildAt.getMeasuredWidth() + fMax + layoutParams2.rightMargin;
                                float measuredWidth4 = f7 - ((reorderedChildAt.getMeasuredWidth() + fMax) + layoutParams2.leftMargin);
                                if (z) {
                                    flexLine.updatePositionFromView(reorderedChildAt, i10, 0, i9, 0);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, i9, 0, i10, 0);
                                }
                                f4 = measuredWidth4;
                                f = measuredWidth3;
                            }
                        } else {
                            i7 = paddingLeft;
                            i8 = i5;
                            z2 = z3;
                        }
                        i5 = i8 + 1;
                        paddingLeft = i7;
                        z3 = z2;
                    }
                    i12 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i14++;
                    paddingLeft = paddingLeft;
                    break;
                case 2:
                    float f11 = paddingLeft + ((i11 - flexLine.mMainSize) / 2.0f);
                    f2 = (i11 - paddingRight) - ((i11 - flexLine.mMainSize) / 2.0f);
                    f = f11;
                    f3 = 0.0f;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingLeft;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z2 = true;
                            } else {
                                LayoutParams layoutParams3 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams3.leftMargin;
                                f6 = f4 - layoutParams3.rightMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i17 = this.mDividerVerticalWidth;
                                    float f12 = i17;
                                    f7 = f6 - f12;
                                    i9 = i17;
                                    f8 = f5 + f12;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (this.mFlexWrap == 2) {
                                    i8 = i5;
                                    z2 = true;
                                    if (z) {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i12, Math.round(f7), i12 + reorderedChildAt.getMeasuredHeight());
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i12, Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i12 + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z) {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f7), i13);
                                } else {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i13);
                                }
                                float measuredWidth5 = f8 + reorderedChildAt.getMeasuredWidth() + fMax + layoutParams3.rightMargin;
                                float measuredWidth6 = f7 - ((reorderedChildAt.getMeasuredWidth() + fMax) + layoutParams3.leftMargin);
                                if (z) {
                                    flexLine.updatePositionFromView(reorderedChildAt, i10, 0, i9, 0);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, i9, 0, i10, 0);
                                }
                                f4 = measuredWidth6;
                                f = measuredWidth5;
                            }
                        } else {
                            i7 = paddingLeft;
                            i8 = i5;
                            z2 = z3;
                        }
                        i5 = i8 + 1;
                        paddingLeft = i7;
                        z3 = z2;
                    }
                    i12 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i14++;
                    paddingLeft = paddingLeft;
                    break;
                case 3:
                    f = paddingLeft;
                    int itemCountNotGone = flexLine.getItemCountNotGone();
                    f2 = i11 - paddingRight;
                    f3 = (i11 - flexLine.mMainSize) / (itemCountNotGone != 1 ? itemCountNotGone - 1 : 1.0f);
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingLeft;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z2 = true;
                            } else {
                                LayoutParams layoutParams4 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams4.leftMargin;
                                f6 = f4 - layoutParams4.rightMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i18 = this.mDividerVerticalWidth;
                                    float f13 = i18;
                                    f7 = f6 - f13;
                                    i9 = i18;
                                    f8 = f5 + f13;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (this.mFlexWrap == 2) {
                                    i8 = i5;
                                    z2 = true;
                                    if (z) {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i12, Math.round(f7), i12 + reorderedChildAt.getMeasuredHeight());
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i12, Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i12 + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z) {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f7), i13);
                                } else {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i13);
                                }
                                float measuredWidth7 = f8 + reorderedChildAt.getMeasuredWidth() + fMax + layoutParams4.rightMargin;
                                float measuredWidth8 = f7 - ((reorderedChildAt.getMeasuredWidth() + fMax) + layoutParams4.leftMargin);
                                if (z) {
                                    flexLine.updatePositionFromView(reorderedChildAt, i10, 0, i9, 0);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, i9, 0, i10, 0);
                                }
                                f4 = measuredWidth8;
                                f = measuredWidth7;
                            }
                        } else {
                            i7 = paddingLeft;
                            i8 = i5;
                            z2 = z3;
                        }
                        i5 = i8 + 1;
                        paddingLeft = i7;
                        z3 = z2;
                    }
                    i12 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i14++;
                    paddingLeft = paddingLeft;
                    break;
                case 4:
                    int itemCountNotGone2 = flexLine.getItemCountNotGone();
                    f3 = itemCountNotGone2 != 0 ? (i11 - flexLine.mMainSize) / itemCountNotGone2 : 0.0f;
                    float f14 = f3 / 2.0f;
                    float f15 = (i11 - paddingRight) - f14;
                    f = paddingLeft + f14;
                    f2 = f15;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingLeft;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z2 = true;
                            } else {
                                LayoutParams layoutParams5 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams5.leftMargin;
                                f6 = f4 - layoutParams5.rightMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i19 = this.mDividerVerticalWidth;
                                    float f16 = i19;
                                    f7 = f6 - f16;
                                    i9 = i19;
                                    f8 = f5 + f16;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (this.mFlexWrap == 2) {
                                    i8 = i5;
                                    z2 = true;
                                    if (z) {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i12, Math.round(f7), i12 + reorderedChildAt.getMeasuredHeight());
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i12, Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i12 + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z) {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f7), i13);
                                } else {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i13);
                                }
                                float measuredWidth9 = f8 + reorderedChildAt.getMeasuredWidth() + fMax + layoutParams5.rightMargin;
                                float measuredWidth10 = f7 - ((reorderedChildAt.getMeasuredWidth() + fMax) + layoutParams5.leftMargin);
                                if (z) {
                                    flexLine.updatePositionFromView(reorderedChildAt, i10, 0, i9, 0);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, i9, 0, i10, 0);
                                }
                                f4 = measuredWidth10;
                                f = measuredWidth9;
                            }
                        } else {
                            i7 = paddingLeft;
                            i8 = i5;
                            z2 = z3;
                        }
                        i5 = i8 + 1;
                        paddingLeft = i7;
                        z3 = z2;
                    }
                    i12 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i14++;
                    paddingLeft = paddingLeft;
                    break;
                case 5:
                    int itemCountNotGone3 = flexLine.getItemCountNotGone();
                    f3 = itemCountNotGone3 != 0 ? (i11 - flexLine.mMainSize) / (itemCountNotGone3 + 1) : 0.0f;
                    f = paddingLeft + f3;
                    f2 = (i11 - paddingRight) - f3;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingLeft;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z2 = true;
                            } else {
                                LayoutParams layoutParams6 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams6.leftMargin;
                                f6 = f4 - layoutParams6.rightMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i110 = this.mDividerVerticalWidth;
                                    float f17 = i110;
                                    f7 = f6 - f17;
                                    i9 = i110;
                                    f8 = f5 + f17;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (this.mFlexWrap == 2) {
                                    i8 = i5;
                                    z2 = true;
                                    if (z) {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i12, Math.round(f7), i12 + reorderedChildAt.getMeasuredHeight());
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i12, Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i12 + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z) {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f7) - reorderedChildAt.getMeasuredWidth(), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f7), i13);
                                } else {
                                    i8 = i5;
                                    z2 = true;
                                    this.mFlexboxHelper.layoutSingleChildHorizontal(reorderedChildAt, flexLine, Math.round(f8), i13 - reorderedChildAt.getMeasuredHeight(), Math.round(f8) + reorderedChildAt.getMeasuredWidth(), i13);
                                }
                                float measuredWidth11 = f8 + reorderedChildAt.getMeasuredWidth() + fMax + layoutParams6.rightMargin;
                                float measuredWidth12 = f7 - ((reorderedChildAt.getMeasuredWidth() + fMax) + layoutParams6.leftMargin);
                                if (z) {
                                    flexLine.updatePositionFromView(reorderedChildAt, i10, 0, i9, 0);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, i9, 0, i10, 0);
                                }
                                f4 = measuredWidth12;
                                f = measuredWidth11;
                            }
                        } else {
                            i7 = paddingLeft;
                            i8 = i5;
                            z2 = z3;
                        }
                        i5 = i8 + 1;
                        paddingLeft = i7;
                        z3 = z2;
                    }
                    i12 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i14++;
                    paddingLeft = paddingLeft;
                    break;
                default:
                    throw new IllegalStateException("Invalid justifyContent is set: " + this.mJustifyContent);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:33:0x00d4  */
    /* JADX WARN: Code duplicated, block: B:35:0x00dd  */
    /* JADX WARN: Code duplicated, block: B:37:0x00e7  */
    /* JADX WARN: Code duplicated, block: B:38:0x00ed  */
    /* JADX WARN: Code duplicated, block: B:40:0x0101  */
    /* JADX WARN: Code duplicated, block: B:41:0x010d  */
    /* JADX WARN: Code duplicated, block: B:44:0x0119  */
    /* JADX WARN: Code duplicated, block: B:47:0x0124  */
    /* JADX WARN: Code duplicated, block: B:49:0x0128 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:50:0x012a  */
    /* JADX WARN: Code duplicated, block: B:51:0x0158  */
    /* JADX WARN: Code duplicated, block: B:52:0x017c  */
    /* JADX WARN: Code duplicated, block: B:54:0x0182  */
    /* JADX WARN: Code duplicated, block: B:55:0x01a1  */
    /* JADX WARN: Code duplicated, block: B:58:0x01d9  */
    /* JADX WARN: Code duplicated, block: B:59:0x01e6  */
    /* JADX WARN: Code duplicated, block: B:61:0x01f7  */
    private void layoutVertical(boolean z, boolean z2, int i, int i2, int i3, int i4) {
        float f;
        float f2;
        float f3;
        float fMax;
        float f4;
        int i5;
        int i6;
        View reorderedChildAt;
        int i7;
        int i8;
        boolean z3;
        float f5;
        float f6;
        float f7;
        float f8;
        int i9;
        int i10;
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingRight = getPaddingRight();
        int paddingLeft = getPaddingLeft();
        int i11 = i4 - i2;
        int i12 = (i3 - i) - paddingRight;
        int size = this.mFlexLines.size();
        int i13 = i12;
        int i14 = paddingLeft;
        int i15 = 0;
        while (i15 < size) {
            FlexLine flexLine = this.mFlexLines.get(i15);
            if (hasDividerBeforeFlexLine(i15)) {
                i14 += this.mDividerVerticalWidth;
                i13 -= this.mDividerVerticalWidth;
            }
            boolean z4 = true;
            switch (this.mJustifyContent) {
                case 0:
                    f = paddingTop;
                    f2 = i11 - paddingBottom;
                    f3 = 0.0f;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingTop;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z3 = true;
                            } else {
                                LayoutParams layoutParams = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams.topMargin;
                                f6 = f4 - layoutParams.bottomMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i16 = this.mDividerHorizontalHeight;
                                    float f9 = i16;
                                    f7 = f6 - f9;
                                    i9 = i16;
                                    f8 = f5 + f9;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1 || (this.mShowDividerHorizontal & 4) <= 0) {
                                    i10 = 0;
                                } else {
                                    i10 = this.mDividerHorizontalHeight;
                                }
                                if (z) {
                                    i8 = i5;
                                    z3 = true;
                                    if (z2) {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f7));
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f8), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z2) {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i13, Math.round(f7));
                                } else {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f8), i13, Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                }
                                float measuredHeight = f8 + reorderedChildAt.getMeasuredHeight() + fMax + layoutParams.bottomMargin;
                                float measuredHeight2 = f7 - ((reorderedChildAt.getMeasuredHeight() + fMax) + layoutParams.topMargin);
                                if (z2) {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i10, 0, i9);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i9, 0, i10);
                                }
                                f4 = measuredHeight2;
                                f = measuredHeight;
                            }
                        } else {
                            i7 = paddingTop;
                            i8 = i5;
                            z3 = z4;
                        }
                        i5 = i8 + 1;
                        paddingTop = i7;
                        z4 = z3;
                    }
                    i14 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i15++;
                    paddingTop = paddingTop;
                    break;
                case 1:
                    f = (i11 - flexLine.mMainSize) + paddingBottom;
                    f2 = flexLine.mMainSize - paddingTop;
                    f3 = 0.0f;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingTop;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z3 = true;
                            } else {
                                LayoutParams layoutParams2 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams2.topMargin;
                                f6 = f4 - layoutParams2.bottomMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i17 = this.mDividerHorizontalHeight;
                                    float f10 = i17;
                                    f7 = f6 - f10;
                                    i9 = i17;
                                    f8 = f5 + f10;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (z) {
                                    i8 = i5;
                                    z3 = true;
                                    if (z2) {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f7));
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f8), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z2) {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i13, Math.round(f7));
                                } else {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f8), i13, Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                }
                                float measuredHeight3 = f8 + reorderedChildAt.getMeasuredHeight() + fMax + layoutParams2.bottomMargin;
                                float measuredHeight4 = f7 - ((reorderedChildAt.getMeasuredHeight() + fMax) + layoutParams2.topMargin);
                                if (z2) {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i10, 0, i9);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i9, 0, i10);
                                }
                                f4 = measuredHeight4;
                                f = measuredHeight3;
                            }
                        } else {
                            i7 = paddingTop;
                            i8 = i5;
                            z3 = z4;
                        }
                        i5 = i8 + 1;
                        paddingTop = i7;
                        z4 = z3;
                    }
                    i14 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i15++;
                    paddingTop = paddingTop;
                    break;
                case 2:
                    float f11 = paddingTop + ((i11 - flexLine.mMainSize) / 2.0f);
                    f2 = (i11 - paddingBottom) - ((i11 - flexLine.mMainSize) / 2.0f);
                    f = f11;
                    f3 = 0.0f;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingTop;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z3 = true;
                            } else {
                                LayoutParams layoutParams3 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams3.topMargin;
                                f6 = f4 - layoutParams3.bottomMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i18 = this.mDividerHorizontalHeight;
                                    float f12 = i18;
                                    f7 = f6 - f12;
                                    i9 = i18;
                                    f8 = f5 + f12;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (z) {
                                    i8 = i5;
                                    z3 = true;
                                    if (z2) {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f7));
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f8), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z2) {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i13, Math.round(f7));
                                } else {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f8), i13, Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                }
                                float measuredHeight5 = f8 + reorderedChildAt.getMeasuredHeight() + fMax + layoutParams3.bottomMargin;
                                float measuredHeight6 = f7 - ((reorderedChildAt.getMeasuredHeight() + fMax) + layoutParams3.topMargin);
                                if (z2) {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i10, 0, i9);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i9, 0, i10);
                                }
                                f4 = measuredHeight6;
                                f = measuredHeight5;
                            }
                        } else {
                            i7 = paddingTop;
                            i8 = i5;
                            z3 = z4;
                        }
                        i5 = i8 + 1;
                        paddingTop = i7;
                        z4 = z3;
                    }
                    i14 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i15++;
                    paddingTop = paddingTop;
                    break;
                case 3:
                    f = paddingTop;
                    int itemCountNotGone = flexLine.getItemCountNotGone();
                    f2 = i11 - paddingBottom;
                    f3 = (i11 - flexLine.mMainSize) / (itemCountNotGone != 1 ? itemCountNotGone - 1 : 1.0f);
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingTop;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z3 = true;
                            } else {
                                LayoutParams layoutParams4 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams4.topMargin;
                                f6 = f4 - layoutParams4.bottomMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i19 = this.mDividerHorizontalHeight;
                                    float f13 = i19;
                                    f7 = f6 - f13;
                                    i9 = i19;
                                    f8 = f5 + f13;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (z) {
                                    i8 = i5;
                                    z3 = true;
                                    if (z2) {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f7));
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f8), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z2) {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i13, Math.round(f7));
                                } else {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f8), i13, Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                }
                                float measuredHeight7 = f8 + reorderedChildAt.getMeasuredHeight() + fMax + layoutParams4.bottomMargin;
                                float measuredHeight8 = f7 - ((reorderedChildAt.getMeasuredHeight() + fMax) + layoutParams4.topMargin);
                                if (z2) {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i10, 0, i9);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i9, 0, i10);
                                }
                                f4 = measuredHeight8;
                                f = measuredHeight7;
                            }
                        } else {
                            i7 = paddingTop;
                            i8 = i5;
                            z3 = z4;
                        }
                        i5 = i8 + 1;
                        paddingTop = i7;
                        z4 = z3;
                    }
                    i14 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i15++;
                    paddingTop = paddingTop;
                    break;
                case 4:
                    int itemCountNotGone2 = flexLine.getItemCountNotGone();
                    f3 = itemCountNotGone2 != 0 ? (i11 - flexLine.mMainSize) / itemCountNotGone2 : 0.0f;
                    float f14 = f3 / 2.0f;
                    float f15 = (i11 - paddingBottom) - f14;
                    f = paddingTop + f14;
                    f2 = f15;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingTop;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z3 = true;
                            } else {
                                LayoutParams layoutParams5 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams5.topMargin;
                                f6 = f4 - layoutParams5.bottomMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i110 = this.mDividerHorizontalHeight;
                                    float f16 = i110;
                                    f7 = f6 - f16;
                                    i9 = i110;
                                    f8 = f5 + f16;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (z) {
                                    i8 = i5;
                                    z3 = true;
                                    if (z2) {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f7));
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f8), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z2) {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i13, Math.round(f7));
                                } else {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f8), i13, Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                }
                                float measuredHeight9 = f8 + reorderedChildAt.getMeasuredHeight() + fMax + layoutParams5.bottomMargin;
                                float measuredHeight10 = f7 - ((reorderedChildAt.getMeasuredHeight() + fMax) + layoutParams5.topMargin);
                                if (z2) {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i10, 0, i9);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i9, 0, i10);
                                }
                                f4 = measuredHeight10;
                                f = measuredHeight9;
                            }
                        } else {
                            i7 = paddingTop;
                            i8 = i5;
                            z3 = z4;
                        }
                        i5 = i8 + 1;
                        paddingTop = i7;
                        z4 = z3;
                    }
                    i14 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i15++;
                    paddingTop = paddingTop;
                    break;
                case 5:
                    int itemCountNotGone3 = flexLine.getItemCountNotGone();
                    f3 = itemCountNotGone3 != 0 ? (i11 - flexLine.mMainSize) / (itemCountNotGone3 + 1) : 0.0f;
                    f = paddingTop + f3;
                    f2 = (i11 - paddingBottom) - f3;
                    fMax = Math.max(f3, 0.0f);
                    f4 = f2;
                    i5 = 0;
                    while (i5 < flexLine.mItemCount) {
                        i6 = flexLine.mFirstIndex + i5;
                        reorderedChildAt = getReorderedChildAt(i6);
                        if (reorderedChildAt != null) {
                            i7 = paddingTop;
                            if (reorderedChildAt.getVisibility() == 8) {
                                i8 = i5;
                                z3 = true;
                            } else {
                                LayoutParams layoutParams6 = (LayoutParams) reorderedChildAt.getLayoutParams();
                                f5 = f + layoutParams6.topMargin;
                                f6 = f4 - layoutParams6.bottomMargin;
                                if (hasDividerBeforeChildAtAlongMainAxis(i6, i5)) {
                                    int i111 = this.mDividerHorizontalHeight;
                                    float f17 = i111;
                                    f7 = f6 - f17;
                                    i9 = i111;
                                    f8 = f5 + f17;
                                } else {
                                    f7 = f6;
                                    f8 = f5;
                                    i9 = 0;
                                }
                                if (i5 == flexLine.mItemCount - 1) {
                                    i10 = 0;
                                } else {
                                    i10 = 0;
                                }
                                if (z) {
                                    i8 = i5;
                                    z3 = true;
                                    if (z2) {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f7));
                                    } else {
                                        this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, false, i14, Math.round(f8), i14 + reorderedChildAt.getMeasuredWidth(), Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                    }
                                } else if (z2) {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f7) - reorderedChildAt.getMeasuredHeight(), i13, Math.round(f7));
                                } else {
                                    i8 = i5;
                                    z3 = true;
                                    this.mFlexboxHelper.layoutSingleChildVertical(reorderedChildAt, flexLine, true, i13 - reorderedChildAt.getMeasuredWidth(), Math.round(f8), i13, Math.round(f8) + reorderedChildAt.getMeasuredHeight());
                                }
                                float measuredHeight11 = f8 + reorderedChildAt.getMeasuredHeight() + fMax + layoutParams6.bottomMargin;
                                float measuredHeight12 = f7 - ((reorderedChildAt.getMeasuredHeight() + fMax) + layoutParams6.topMargin);
                                if (z2) {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i10, 0, i9);
                                } else {
                                    flexLine.updatePositionFromView(reorderedChildAt, 0, i9, 0, i10);
                                }
                                f4 = measuredHeight12;
                                f = measuredHeight11;
                            }
                        } else {
                            i7 = paddingTop;
                            i8 = i5;
                            z3 = z4;
                        }
                        i5 = i8 + 1;
                        paddingTop = i7;
                        z4 = z3;
                    }
                    i14 += flexLine.mCrossSize;
                    i13 -= flexLine.mCrossSize;
                    i15++;
                    paddingTop = paddingTop;
                    break;
                default:
                    throw new IllegalStateException("Invalid justifyContent is set: " + this.mJustifyContent);
            }
        }
    }

    private void measureHorizontal(int i, int i2) {
        this.mFlexLines.clear();
        this.mFlexLinesResult.reset();
        this.mFlexboxHelper.calculateHorizontalFlexLines(this.mFlexLinesResult, i, i2);
        this.mFlexLines = this.mFlexLinesResult.mFlexLines;
        this.mFlexboxHelper.determineMainSize(i, i2);
        if (this.mAlignItems == 3) {
            for (FlexLine flexLine : this.mFlexLines) {
                int iMax = Integer.MIN_VALUE;
                for (int i3 = 0; i3 < flexLine.mItemCount; i3++) {
                    View reorderedChildAt = getReorderedChildAt(flexLine.mFirstIndex + i3);
                    if (reorderedChildAt != null && reorderedChildAt.getVisibility() != 8) {
                        LayoutParams layoutParams = (LayoutParams) reorderedChildAt.getLayoutParams();
                        iMax = this.mFlexWrap != 2 ? Math.max(iMax, reorderedChildAt.getMeasuredHeight() + Math.max(flexLine.mMaxBaseline - reorderedChildAt.getBaseline(), layoutParams.topMargin) + layoutParams.bottomMargin) : Math.max(iMax, reorderedChildAt.getMeasuredHeight() + layoutParams.topMargin + Math.max((flexLine.mMaxBaseline - reorderedChildAt.getMeasuredHeight()) + reorderedChildAt.getBaseline(), layoutParams.bottomMargin));
                    }
                }
                flexLine.mCrossSize = iMax;
            }
        }
        this.mFlexboxHelper.determineCrossSize(i, i2, getPaddingTop() + getPaddingBottom());
        this.mFlexboxHelper.stretchViews();
        setMeasuredDimensionForFlex(this.mFlexDirection, i, i2, this.mFlexLinesResult.mChildState);
    }

    private void measureVertical(int i, int i2) {
        this.mFlexLines.clear();
        this.mFlexLinesResult.reset();
        this.mFlexboxHelper.calculateVerticalFlexLines(this.mFlexLinesResult, i, i2);
        this.mFlexLines = this.mFlexLinesResult.mFlexLines;
        this.mFlexboxHelper.determineMainSize(i, i2);
        this.mFlexboxHelper.determineCrossSize(i, i2, getPaddingLeft() + getPaddingRight());
        this.mFlexboxHelper.stretchViews();
        setMeasuredDimensionForFlex(this.mFlexDirection, i, i2, this.mFlexLinesResult.mChildState);
    }

    private void setMeasuredDimensionForFlex(int i, int i2, int i3, int i4) {
        int sumOfCrossSize;
        int largestMainSize;
        int iResolveSizeAndState;
        int iResolveSizeAndState2;
        int mode = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i2);
        int mode2 = View.MeasureSpec.getMode(i3);
        int size2 = View.MeasureSpec.getSize(i3);
        switch (i) {
            case 0:
            case 1:
                sumOfCrossSize = getSumOfCrossSize() + getPaddingTop() + getPaddingBottom();
                largestMainSize = getLargestMainSize();
                break;
            case 2:
            case 3:
                sumOfCrossSize = getLargestMainSize();
                largestMainSize = getSumOfCrossSize() + getPaddingLeft() + getPaddingRight();
                break;
            default:
                throw new IllegalArgumentException("Invalid flex direction: " + i);
        }
        if (mode == Integer.MIN_VALUE) {
            if (size < largestMainSize) {
                i4 = View.combineMeasuredStates(i4, 16777216);
            } else {
                size = largestMainSize;
            }
            iResolveSizeAndState = View.resolveSizeAndState(size, i2, i4);
        } else if (mode == 0) {
            iResolveSizeAndState = View.resolveSizeAndState(largestMainSize, i2, i4);
        } else {
            if (mode != 1073741824) {
                throw new IllegalStateException("Unknown width mode is set: " + mode);
            }
            if (size < largestMainSize) {
                i4 = View.combineMeasuredStates(i4, 16777216);
            }
            iResolveSizeAndState = View.resolveSizeAndState(size, i2, i4);
        }
        if (mode2 == Integer.MIN_VALUE) {
            if (size2 < sumOfCrossSize) {
                i4 = View.combineMeasuredStates(i4, 256);
                sumOfCrossSize = size2;
            }
            iResolveSizeAndState2 = View.resolveSizeAndState(sumOfCrossSize, i3, i4);
        } else if (mode2 == 0) {
            iResolveSizeAndState2 = View.resolveSizeAndState(sumOfCrossSize, i3, i4);
        } else {
            if (mode2 != 1073741824) {
                throw new IllegalStateException("Unknown height mode is set: " + mode2);
            }
            if (size2 < sumOfCrossSize) {
                i4 = View.combineMeasuredStates(i4, 256);
            }
            iResolveSizeAndState2 = View.resolveSizeAndState(size2, i3, i4);
        }
        setMeasuredDimension(iResolveSizeAndState, iResolveSizeAndState2);
    }

    private void setWillNotDrawFlag() {
        if (this.mDividerDrawableHorizontal == null && this.mDividerDrawableVertical == null) {
            setWillNotDraw(true);
        } else {
            setWillNotDraw(false);
        }
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        if (this.mOrderCache == null) {
            this.mOrderCache = new SparseIntArray(getChildCount());
        }
        this.mReorderedIndices = this.mFlexboxHelper.createReorderedIndices(view, i, layoutParams, this.mOrderCache);
        super.addView(view, i, layoutParams);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public boolean checkLayoutParams(ViewGroup.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        return layoutParams instanceof ViewGroup.MarginLayoutParams ? new LayoutParams((ViewGroup.MarginLayoutParams) layoutParams) : new LayoutParams(layoutParams);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new LayoutParams(getContext(), attributeSet);
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getAlignContent() {
        return this.mAlignContent;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getAlignItems() {
        return this.mAlignItems;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getChildHeightMeasureSpec(int i, int i2, int i3) {
        return getChildMeasureSpec(i, i2, i3);
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getChildWidthMeasureSpec(int i, int i2, int i3) {
        return getChildMeasureSpec(i, i2, i3);
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getDecorationLengthCrossAxis(View view) {
        return 0;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getDecorationLengthMainAxis(View view, int i, int i2) {
        int i3;
        if (isMainAxisDirectionHorizontal()) {
            i3 = hasDividerBeforeChildAtAlongMainAxis(i, i2) ? 0 + this.mDividerVerticalWidth : 0;
            return (this.mShowDividerVertical & 4) > 0 ? i3 + this.mDividerVerticalWidth : i3;
        }
        i3 = hasDividerBeforeChildAtAlongMainAxis(i, i2) ? 0 + this.mDividerHorizontalHeight : 0;
        return (this.mShowDividerHorizontal & 4) > 0 ? i3 + this.mDividerHorizontalHeight : i3;
    }

    @Nullable
    public Drawable getDividerDrawableHorizontal() {
        return this.mDividerDrawableHorizontal;
    }

    @Nullable
    public Drawable getDividerDrawableVertical() {
        return this.mDividerDrawableVertical;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getFlexDirection() {
        return this.mFlexDirection;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public View getFlexItemAt(int i) {
        return getChildAt(i);
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getFlexItemCount() {
        return getChildCount();
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public List<FlexLine> getFlexLines() {
        ArrayList arrayList = new ArrayList(this.mFlexLines.size());
        for (FlexLine flexLine : this.mFlexLines) {
            if (flexLine.getItemCountNotGone() != 0) {
                arrayList.add(flexLine);
            }
        }
        return arrayList;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public List<FlexLine> getFlexLinesInternal() {
        return this.mFlexLines;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getFlexWrap() {
        return this.mFlexWrap;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getJustifyContent() {
        return this.mJustifyContent;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getLargestMainSize() {
        Iterator<FlexLine> it = this.mFlexLines.iterator();
        int iMax = Integer.MIN_VALUE;
        while (it.hasNext()) {
            iMax = Math.max(iMax, it.next().mMainSize);
        }
        return iMax;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getMaxLine() {
        return this.mMaxLine;
    }

    public View getReorderedChildAt(int i) {
        if (i < 0 || i >= this.mReorderedIndices.length) {
            return null;
        }
        return getChildAt(this.mReorderedIndices[i]);
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public View getReorderedFlexItemAt(int i) {
        return getReorderedChildAt(i);
    }

    public int getShowDividerHorizontal() {
        return this.mShowDividerHorizontal;
    }

    public int getShowDividerVertical() {
        return this.mShowDividerVertical;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public int getSumOfCrossSize() {
        int size = this.mFlexLines.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            FlexLine flexLine = this.mFlexLines.get(i2);
            if (hasDividerBeforeFlexLine(i2)) {
                i = isMainAxisDirectionHorizontal() ? i + this.mDividerHorizontalHeight : i + this.mDividerVerticalWidth;
            }
            if (hasEndDividerAfterFlexLine(i2)) {
                i = isMainAxisDirectionHorizontal() ? i + this.mDividerHorizontalHeight : i + this.mDividerVerticalWidth;
            }
            i += flexLine.mCrossSize;
        }
        return i;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public boolean isMainAxisDirectionHorizontal() {
        return this.mFlexDirection == 0 || this.mFlexDirection == 1;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        if (this.mDividerDrawableVertical == null && this.mDividerDrawableHorizontal == null) {
        }
        if (this.mShowDividerHorizontal == 0 && this.mShowDividerVertical == 0) {
            return;
        }
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        switch (this.mFlexDirection) {
            case 0:
                drawDividersHorizontal(canvas, layoutDirection == 1, this.mFlexWrap == 2);
                break;
            case 1:
                drawDividersHorizontal(canvas, layoutDirection != 1, this.mFlexWrap == 2);
                break;
            case 2:
                boolean z = layoutDirection == 1;
                if (this.mFlexWrap == 2) {
                    z = !z;
                }
                drawDividersVertical(canvas, z, false);
                break;
            case 3:
                boolean z2 = layoutDirection == 1;
                if (this.mFlexWrap == 2) {
                    z2 = !z2;
                }
                drawDividersVertical(canvas, z2, true);
                break;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        boolean z2;
        int layoutDirection = ViewCompat.getLayoutDirection(this);
        switch (this.mFlexDirection) {
            case 0:
                layoutHorizontal(layoutDirection == 1, i, i2, i3, i4);
                return;
            case 1:
                layoutHorizontal(layoutDirection != 1, i, i2, i3, i4);
                return;
            case 2:
                z2 = layoutDirection == 1;
                layoutVertical(this.mFlexWrap == 2 ? !z2 : z2, false, i, i2, i3, i4);
                return;
            case 3:
                z2 = layoutDirection == 1;
                layoutVertical(this.mFlexWrap == 2 ? !z2 : z2, true, i, i2, i3, i4);
                return;
            default:
                throw new IllegalStateException("Invalid flex direction is set: " + this.mFlexDirection);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        if (this.mOrderCache == null) {
            this.mOrderCache = new SparseIntArray(getChildCount());
        }
        if (this.mFlexboxHelper.isOrderChangedFromLastMeasurement(this.mOrderCache)) {
            this.mReorderedIndices = this.mFlexboxHelper.createReorderedIndices(this.mOrderCache);
        }
        switch (this.mFlexDirection) {
            case 0:
            case 1:
                measureHorizontal(i, i2);
                return;
            case 2:
            case 3:
                measureVertical(i, i2);
                return;
            default:
                throw new IllegalStateException("Invalid value for the flex direction is set: " + this.mFlexDirection);
        }
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void onNewFlexItemAdded(View view, int i, int i2, FlexLine flexLine) {
        if (hasDividerBeforeChildAtAlongMainAxis(i, i2)) {
            if (isMainAxisDirectionHorizontal()) {
                flexLine.mMainSize += this.mDividerVerticalWidth;
                flexLine.mDividerLengthInMainSize += this.mDividerVerticalWidth;
            } else {
                flexLine.mMainSize += this.mDividerHorizontalHeight;
                flexLine.mDividerLengthInMainSize += this.mDividerHorizontalHeight;
            }
        }
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void onNewFlexLineAdded(FlexLine flexLine) {
        if (isMainAxisDirectionHorizontal()) {
            if ((this.mShowDividerVertical & 4) > 0) {
                flexLine.mMainSize += this.mDividerVerticalWidth;
                flexLine.mDividerLengthInMainSize += this.mDividerVerticalWidth;
                return;
            }
            return;
        }
        if ((this.mShowDividerHorizontal & 4) > 0) {
            flexLine.mMainSize += this.mDividerHorizontalHeight;
            flexLine.mDividerLengthInMainSize += this.mDividerHorizontalHeight;
        }
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void setAlignContent(int i) {
        if (this.mAlignContent != i) {
            this.mAlignContent = i;
            requestLayout();
        }
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void setAlignItems(int i) {
        if (this.mAlignItems != i) {
            this.mAlignItems = i;
            requestLayout();
        }
    }

    public void setDividerDrawable(Drawable drawable) {
        setDividerDrawableHorizontal(drawable);
        setDividerDrawableVertical(drawable);
    }

    public void setDividerDrawableHorizontal(@Nullable Drawable drawable) {
        if (drawable == this.mDividerDrawableHorizontal) {
            return;
        }
        this.mDividerDrawableHorizontal = drawable;
        if (drawable != null) {
            this.mDividerHorizontalHeight = drawable.getIntrinsicHeight();
        } else {
            this.mDividerHorizontalHeight = 0;
        }
        setWillNotDrawFlag();
        requestLayout();
    }

    public void setDividerDrawableVertical(@Nullable Drawable drawable) {
        if (drawable == this.mDividerDrawableVertical) {
            return;
        }
        this.mDividerDrawableVertical = drawable;
        if (drawable != null) {
            this.mDividerVerticalWidth = drawable.getIntrinsicWidth();
        } else {
            this.mDividerVerticalWidth = 0;
        }
        setWillNotDrawFlag();
        requestLayout();
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void setFlexDirection(int i) {
        if (this.mFlexDirection != i) {
            this.mFlexDirection = i;
            requestLayout();
        }
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void setFlexLines(List<FlexLine> list) {
        this.mFlexLines = list;
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void setFlexWrap(int i) {
        if (this.mFlexWrap != i) {
            this.mFlexWrap = i;
            requestLayout();
        }
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void setJustifyContent(int i) {
        if (this.mJustifyContent != i) {
            this.mJustifyContent = i;
            requestLayout();
        }
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void setMaxLine(int i) {
        if (this.mMaxLine != i) {
            this.mMaxLine = i;
            requestLayout();
        }
    }

    public void setShowDivider(int i) {
        setShowDividerVertical(i);
        setShowDividerHorizontal(i);
    }

    public void setShowDividerHorizontal(int i) {
        if (i != this.mShowDividerHorizontal) {
            this.mShowDividerHorizontal = i;
            requestLayout();
        }
    }

    public void setShowDividerVertical(int i) {
        if (i != this.mShowDividerVertical) {
            this.mShowDividerVertical = i;
            requestLayout();
        }
    }

    @Override // com.yftech.car.widget.media.flexbox.FlexContainer
    public void updateViewCache(int i, View view) {
    }
}
