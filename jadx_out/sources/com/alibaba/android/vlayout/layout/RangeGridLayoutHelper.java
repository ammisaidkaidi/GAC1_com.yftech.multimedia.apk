package com.alibaba.android.vlayout.layout;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.Range;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public class RangeGridLayoutHelper extends BaseLayoutHelper {
    private static boolean DEBUG = false;
    private static final int MAIN_DIR_SPEC = View.MeasureSpec.makeMeasureSpec(0, 0);
    private static final String TAG = "RGLayoutHelper";
    private boolean mLayoutWithAnchor;
    private GridRangeStyle mRangeStyle;
    private int mTotalSize;

    public static class GridRangeStyle extends RangeStyle<GridRangeStyle> {
        private float mAspectRatio;
        private int mHGap;
        private boolean mIgnoreExtra;
        private boolean mIsAutoExpand;
        private View[] mSet;
        private int mSizePerSpan;
        private int[] mSpanCols;
        private int mSpanCount;
        private int[] mSpanIndices;

        @NonNull
        private GridLayoutHelper.SpanSizeLookup mSpanSizeLookup;
        private int mVGap;
        private float[] mWeights;

        public GridRangeStyle() {
            this.mAspectRatio = Float.NaN;
            this.mSpanCount = 4;
            this.mSizePerSpan = 0;
            this.mIsAutoExpand = true;
            this.mIgnoreExtra = false;
            this.mSpanSizeLookup = new GridLayoutHelper.DefaultSpanSizeLookup();
            this.mVGap = 0;
            this.mHGap = 0;
            this.mWeights = new float[0];
            this.mSpanSizeLookup.setSpanIndexCacheEnabled(true);
        }

        public GridRangeStyle(RangeGridLayoutHelper rangeGridLayoutHelper) {
            super(rangeGridLayoutHelper);
            this.mAspectRatio = Float.NaN;
            this.mSpanCount = 4;
            this.mSizePerSpan = 0;
            this.mIsAutoExpand = true;
            this.mIgnoreExtra = false;
            this.mSpanSizeLookup = new GridLayoutHelper.DefaultSpanSizeLookup();
            this.mVGap = 0;
            this.mHGap = 0;
            this.mWeights = new float[0];
            this.mSpanSizeLookup.setSpanIndexCacheEnabled(true);
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        public static int computeEndAlignOffset(GridRangeStyle gridRangeStyle, boolean z) {
            int i;
            int i2;
            int i3;
            int i4;
            if (z) {
                i = gridRangeStyle.mMarginBottom;
                i2 = gridRangeStyle.mPaddingBottom;
            } else {
                i = gridRangeStyle.mMarginRight;
                i2 = gridRangeStyle.mPaddingRight;
            }
            int iComputeEndAlignOffset = i + i2;
            int iIntValue = ((Integer) gridRangeStyle.getRange().getUpper()).intValue();
            int size = gridRangeStyle.mChildren.size();
            for (int i5 = 0; i5 < size; i5++) {
                GridRangeStyle gridRangeStyle2 = (GridRangeStyle) gridRangeStyle.mChildren.valueAt(i5);
                if (!gridRangeStyle2.isChildrenEmpty()) {
                    iComputeEndAlignOffset += computeEndAlignOffset(gridRangeStyle2, z);
                } else if (((Integer) gridRangeStyle2.mRange.getUpper()).intValue() == iIntValue) {
                    if (z) {
                        i3 = gridRangeStyle2.mMarginBottom;
                        i4 = gridRangeStyle2.mPaddingBottom;
                    } else {
                        i3 = gridRangeStyle2.mMarginRight;
                        i4 = gridRangeStyle2.mPaddingRight;
                    }
                    return iComputeEndAlignOffset + i3 + i4;
                }
            }
            return iComputeEndAlignOffset;
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        public static int computeStartAlignOffset(GridRangeStyle gridRangeStyle, boolean z) {
            int i;
            int i2;
            int i3;
            int i4;
            if (z) {
                i = -gridRangeStyle.mMarginTop;
                i2 = gridRangeStyle.mPaddingTop;
            } else {
                i = -gridRangeStyle.mMarginLeft;
                i2 = gridRangeStyle.mPaddingLeft;
            }
            int iComputeStartAlignOffset = i - i2;
            int iIntValue = ((Integer) gridRangeStyle.getRange().getLower()).intValue();
            int size = gridRangeStyle.mChildren.size();
            for (int i5 = 0; i5 < size; i5++) {
                GridRangeStyle gridRangeStyle2 = (GridRangeStyle) gridRangeStyle.mChildren.valueAt(i5);
                if (!gridRangeStyle2.isChildrenEmpty()) {
                    iComputeStartAlignOffset += computeStartAlignOffset(gridRangeStyle2, z);
                } else if (((Integer) gridRangeStyle2.mRange.getLower()).intValue() == iIntValue) {
                    if (z) {
                        i3 = -gridRangeStyle2.mMarginTop;
                        i4 = gridRangeStyle2.mPaddingTop;
                    } else {
                        i3 = -gridRangeStyle2.mMarginLeft;
                        i4 = gridRangeStyle2.mPaddingLeft;
                    }
                    return iComputeStartAlignOffset + (i3 - i4);
                }
            }
            return iComputeStartAlignOffset;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void ensureSpanCount() {
            if (this.mSet == null || this.mSet.length != this.mSpanCount) {
                this.mSet = new View[this.mSpanCount];
            }
            if (this.mSpanIndices == null || this.mSpanIndices.length != this.mSpanCount) {
                this.mSpanIndices = new int[this.mSpanCount];
            }
            if (this.mSpanCols == null || this.mSpanCols.length != this.mSpanCount) {
                this.mSpanCols = new int[this.mSpanCount];
            }
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        private GridRangeStyle findRangeStyle(GridRangeStyle gridRangeStyle, int i) {
            int size = gridRangeStyle.mChildren.size();
            for (int i2 = 0; i2 < size; i2++) {
                GridRangeStyle gridRangeStyle2 = (GridRangeStyle) gridRangeStyle.mChildren.valueAt(i2);
                Range range = (Range) gridRangeStyle.mChildren.keyAt(i2);
                if (!gridRangeStyle2.isChildrenEmpty()) {
                    return findRangeStyle(gridRangeStyle2, i);
                }
                if (range.contains(Integer.valueOf(i))) {
                    return (GridRangeStyle) gridRangeStyle.mChildren.valueAt(i2);
                }
            }
            return gridRangeStyle;
        }

        public GridRangeStyle findRangeStyleByPosition(int i) {
            return findRangeStyle(this, i);
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        public GridRangeStyle findSiblingStyleByPosition(int i) {
            if (this.mParent != 0) {
                SimpleArrayMap simpleArrayMap = ((GridRangeStyle) this.mParent).mChildren;
                int size = simpleArrayMap.size();
                for (int i2 = 0; i2 < size; i2++) {
                    if (((Range) simpleArrayMap.keyAt(i2)).contains(Integer.valueOf(i))) {
                        GridRangeStyle gridRangeStyle = (GridRangeStyle) simpleArrayMap.valueAt(i2);
                        if (!gridRangeStyle.equals(this)) {
                            return gridRangeStyle;
                        }
                    }
                }
            }
            return null;
        }

        public float getAspectRatio() {
            return this.mAspectRatio;
        }

        public int getSpanCount() {
            return this.mSpanCount;
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        public void onInvalidateSpanIndexCache() {
            this.mSpanSizeLookup.invalidateSpanIndexCache();
            int size = this.mChildren.size();
            for (int i = 0; i < size; i++) {
                ((GridRangeStyle) this.mChildren.valueAt(i)).onInvalidateSpanIndexCache();
            }
        }

        public void setAspectRatio(float f) {
            this.mAspectRatio = f;
        }

        public void setAutoExpand(boolean z) {
            this.mIsAutoExpand = z;
        }

        public void setGap(int i) {
            setVGap(i);
            setHGap(i);
        }

        public void setHGap(int i) {
            if (i < 0) {
                i = 0;
            }
            this.mHGap = i;
        }

        public void setIgnoreExtra(boolean z) {
            this.mIgnoreExtra = z;
        }

        @Override // com.alibaba.android.vlayout.layout.RangeStyle
        public void setRange(int i, int i2) {
            super.setRange(i, i2);
            this.mSpanSizeLookup.setStartPosition(i);
            this.mSpanSizeLookup.invalidateSpanIndexCache();
        }

        public void setSpanCount(int i) {
            if (i == this.mSpanCount) {
                return;
            }
            if (i >= 1) {
                this.mSpanCount = i;
                this.mSpanSizeLookup.invalidateSpanIndexCache();
                ensureSpanCount();
            } else {
                throw new IllegalArgumentException("Span count should be at least 1. Provided " + i);
            }
        }

        public void setSpanSizeLookup(GridLayoutHelper.SpanSizeLookup spanSizeLookup) {
            if (spanSizeLookup != null) {
                spanSizeLookup.setStartPosition(this.mSpanSizeLookup.getStartPosition());
                this.mSpanSizeLookup = spanSizeLookup;
            }
        }

        public void setVGap(int i) {
            if (i < 0) {
                i = 0;
            }
            this.mVGap = i;
        }

        public void setWeights(float[] fArr) {
            if (fArr != null) {
                this.mWeights = Arrays.copyOf(fArr, fArr.length);
            } else {
                this.mWeights = new float[0];
            }
        }
    }

    public RangeGridLayoutHelper(int i) {
        this(i, -1, -1);
    }

    public RangeGridLayoutHelper(int i, int i2) {
        this(i, i2, 0);
    }

    public RangeGridLayoutHelper(int i, int i2, int i3) {
        this(i, i2, i3, i3);
    }

    public RangeGridLayoutHelper(int i, int i2, int i3, int i4) {
        this.mTotalSize = 0;
        this.mLayoutWithAnchor = false;
        this.mRangeStyle = new GridRangeStyle(this);
        this.mRangeStyle.setSpanCount(i);
        this.mRangeStyle.setVGap(i3);
        this.mRangeStyle.setHGap(i4);
        setItemCount(i2);
    }

    private void assignSpans(GridRangeStyle gridRangeStyle, RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, boolean z, LayoutManagerHelper layoutManagerHelper) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7 = 0;
        if (z) {
            i4 = i;
            i3 = 0;
            i5 = 1;
        } else {
            i3 = i - 1;
            i4 = -1;
            i5 = -1;
        }
        if (layoutManagerHelper.getOrientation() == 1 && layoutManagerHelper.isDoLayoutRTL()) {
            i7 = i2 - 1;
            i6 = -1;
        } else {
            i6 = 1;
        }
        while (i3 != i4) {
            int spanSize = getSpanSize(gridRangeStyle.mSpanSizeLookup, recycler, state, layoutManagerHelper.getPosition(gridRangeStyle.mSet[i3]));
            if (i6 != -1 || spanSize <= 1) {
                gridRangeStyle.mSpanIndices[i3] = i7;
            } else {
                gridRangeStyle.mSpanIndices[i3] = i7 - (spanSize - 1);
            }
            i7 += spanSize * i6;
            i3 += i5;
        }
    }

    private int getMainDirSpec(GridRangeStyle gridRangeStyle, int i, int i2, int i3, float f) {
        if (!Float.isNaN(f) && f > 0.0f && i3 > 0) {
            return View.MeasureSpec.makeMeasureSpec((int) ((i3 / f) + 0.5f), 1073741824);
        }
        if (Float.isNaN(gridRangeStyle.mAspectRatio) || gridRangeStyle.mAspectRatio <= 0.0f) {
            return i < 0 ? MAIN_DIR_SPEC : View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        }
        return View.MeasureSpec.makeMeasureSpec((int) ((i2 / gridRangeStyle.mAspectRatio) + 0.5f), 1073741824);
    }

    private int getSpanIndex(GridLayoutHelper.SpanSizeLookup spanSizeLookup, int i, RecyclerView.Recycler recycler, RecyclerView.State state, int i2) {
        if (!state.isPreLayout()) {
            return spanSizeLookup.getCachedSpanIndex(i2, i);
        }
        int iConvertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i2);
        if (iConvertPreLayoutPositionToPostLayout == -1) {
            return 0;
        }
        return spanSizeLookup.getCachedSpanIndex(iConvertPreLayoutPositionToPostLayout, i);
    }

    private int getSpanSize(GridLayoutHelper.SpanSizeLookup spanSizeLookup, RecyclerView.Recycler recycler, RecyclerView.State state, int i) {
        if (!state.isPreLayout()) {
            return spanSizeLookup.getSpanSize(i);
        }
        int iConvertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (iConvertPreLayoutPositionToPostLayout == -1) {
            return 0;
        }
        return spanSizeLookup.getSpanSize(iConvertPreLayoutPositionToPostLayout);
    }

    public void addRangeStyle(int i, int i2, GridRangeStyle gridRangeStyle) {
        this.mRangeStyle.addChildRangeStyle(i, i2, gridRangeStyle);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void adjustLayout(int i, int i2, LayoutManagerHelper layoutManagerHelper) {
        this.mRangeStyle.adjustLayout(i, i2, layoutManagerHelper);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void afterLayout(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, int i3, LayoutManagerHelper layoutManagerHelper) {
        this.mRangeStyle.afterLayout(recycler, state, i, i2, i3, layoutManagerHelper);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public void beforeLayout(RecyclerView.Recycler recycler, RecyclerView.State state, LayoutManagerHelper layoutManagerHelper) {
        this.mRangeStyle.beforeLayout(recycler, state, layoutManagerHelper);
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void checkAnchorInfo(RecyclerView.State state, VirtualLayoutManager.AnchorInfoWrapper anchorInfoWrapper, LayoutManagerHelper layoutManagerHelper) {
        if (state.getItemCount() > 0) {
            GridRangeStyle gridRangeStyleFindRangeStyleByPosition = this.mRangeStyle.findRangeStyleByPosition(anchorInfoWrapper.position);
            int cachedSpanIndex = gridRangeStyleFindRangeStyleByPosition.mSpanSizeLookup.getCachedSpanIndex(anchorInfoWrapper.position, gridRangeStyleFindRangeStyleByPosition.mSpanCount);
            if (anchorInfoWrapper.layoutFromEnd) {
                while (cachedSpanIndex < gridRangeStyleFindRangeStyleByPosition.mSpanCount - 1 && anchorInfoWrapper.position < ((Integer) getRange().getUpper()).intValue()) {
                    anchorInfoWrapper.position++;
                    cachedSpanIndex = gridRangeStyleFindRangeStyleByPosition.mSpanSizeLookup.getCachedSpanIndex(anchorInfoWrapper.position, gridRangeStyleFindRangeStyleByPosition.mSpanCount);
                }
            } else {
                while (cachedSpanIndex > 0 && anchorInfoWrapper.position > 0) {
                    anchorInfoWrapper.position--;
                    cachedSpanIndex = gridRangeStyleFindRangeStyleByPosition.mSpanSizeLookup.getCachedSpanIndex(anchorInfoWrapper.position, gridRangeStyleFindRangeStyleByPosition.mSpanCount);
                }
            }
            this.mLayoutWithAnchor = true;
        }
    }

    @Override // com.alibaba.android.vlayout.layout.MarginLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public int computeAlignOffset(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper) {
        boolean z3 = layoutManagerHelper.getOrientation() == 1;
        if (z) {
            if (i == getItemCount() - 1) {
                return GridRangeStyle.computeEndAlignOffset(this.mRangeStyle, z3);
            }
        } else if (i == 0) {
            return GridRangeStyle.computeStartAlignOffset(this.mRangeStyle, z3);
        }
        return super.computeAlignOffset(i, z, z2, layoutManagerHelper);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public float getAspectRatio() {
        return this.mRangeStyle.getAspectRatio();
    }

    public int getBorderEndSpace(LayoutManagerHelper layoutManagerHelper) {
        GridRangeStyle gridRangeStyleFindRangeStyleByPosition = this.mRangeStyle.findRangeStyleByPosition(((Integer) getRange().getUpper()).intValue());
        return layoutManagerHelper.getOrientation() == 1 ? gridRangeStyleFindRangeStyleByPosition.getFamilyMarginBottom() + gridRangeStyleFindRangeStyleByPosition.getFamilyPaddingBottom() : gridRangeStyleFindRangeStyleByPosition.getFamilyMarginRight() + gridRangeStyleFindRangeStyleByPosition.getFamilyPaddingRight();
    }

    public int getBorderStartSpace(LayoutManagerHelper layoutManagerHelper) {
        GridRangeStyle gridRangeStyleFindRangeStyleByPosition = this.mRangeStyle.findRangeStyleByPosition(((Integer) getRange().getLower()).intValue());
        return layoutManagerHelper.getOrientation() == 1 ? gridRangeStyleFindRangeStyleByPosition.getFamilyMarginTop() + gridRangeStyleFindRangeStyleByPosition.getFamilyPaddingTop() : gridRangeStyleFindRangeStyleByPosition.getFamilyMarginLeft() + gridRangeStyleFindRangeStyleByPosition.getFamilyPaddingLeft();
    }

    public GridRangeStyle getRootRangeStyle() {
        return this.mRangeStyle;
    }

    public int getSpanCount() {
        return this.mRangeStyle.getSpanCount();
    }

    /* JADX WARN: Code duplicated, block: B:306:0x0762  */
    /* JADX WARN: Code duplicated, block: B:337:0x082a A[PHI: r30
      0x082a: PHI (r30v1 com.alibaba.android.vlayout.layout.RangeGridLayoutHelper$GridRangeStyle) = 
      (r30v0 com.alibaba.android.vlayout.layout.RangeGridLayoutHelper$GridRangeStyle)
      (r30v3 com.alibaba.android.vlayout.layout.RangeGridLayoutHelper$GridRangeStyle)
      (r30v3 com.alibaba.android.vlayout.layout.RangeGridLayoutHelper$GridRangeStyle)
     binds: [B:336:0x0828, B:327:0x07dc, B:329:0x07e8] A[DONT_GENERATE, DONT_INLINE]] */
    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:70:0x020f
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.checkLoopExits(LoopRegionMaker.java:272)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeLoopRegion(LoopRegionMaker.java:237)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:80)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:117)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:49)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void layoutViews(androidx.recyclerview.widget.RecyclerView.Recycler r34, androidx.recyclerview.widget.RecyclerView.State r35, com.alibaba.android.vlayout.VirtualLayoutManager.LayoutStateWrapper r36, com.alibaba.android.vlayout.layout.LayoutChunkResult r37, com.alibaba.android.vlayout.LayoutManagerHelper r38) {
        /*
            Method dump skipped, instruction units count: 2581
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.android.vlayout.layout.RangeGridLayoutHelper.layoutViews(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State, com.alibaba.android.vlayout.VirtualLayoutManager$LayoutStateWrapper, com.alibaba.android.vlayout.layout.LayoutChunkResult, com.alibaba.android.vlayout.LayoutManagerHelper):void");
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void onClear(LayoutManagerHelper layoutManagerHelper) {
        super.onClear(layoutManagerHelper);
        this.mRangeStyle.onClear(layoutManagerHelper);
        this.mRangeStyle.onInvalidateSpanIndexCache();
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onItemsChanged(LayoutManagerHelper layoutManagerHelper) {
        super.onItemsChanged(layoutManagerHelper);
        this.mRangeStyle.onInvalidateSpanIndexCache();
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onRangeChange(int i, int i2) {
        this.mRangeStyle.setRange(i, i2);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public boolean requireLayoutView() {
        return this.mRangeStyle.requireLayoutView();
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void setAspectRatio(float f) {
        this.mRangeStyle.setAspectRatio(f);
    }

    public void setAutoExpand(boolean z) {
        this.mRangeStyle.setAutoExpand(z);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void setBgColor(int i) {
        this.mRangeStyle.setBgColor(i);
    }

    public void setGap(int i) {
        setVGap(i);
        setHGap(i);
    }

    public void setHGap(int i) {
        this.mRangeStyle.setHGap(i);
    }

    public void setIgnoreExtra(boolean z) {
        this.mRangeStyle.setIgnoreExtra(z);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void setLayoutViewBindListener(BaseLayoutHelper.LayoutViewBindListener layoutViewBindListener) {
        this.mRangeStyle.setLayoutViewBindListener(layoutViewBindListener);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void setLayoutViewHelper(BaseLayoutHelper.DefaultLayoutViewHelper defaultLayoutViewHelper) {
        this.mRangeStyle.setLayoutViewHelper(defaultLayoutViewHelper);
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void setLayoutViewUnBindListener(BaseLayoutHelper.LayoutViewUnBindListener layoutViewUnBindListener) {
        this.mRangeStyle.setLayoutViewUnBindListener(layoutViewUnBindListener);
    }

    @Override // com.alibaba.android.vlayout.layout.MarginLayoutHelper
    public void setMargin(int i, int i2, int i3, int i4) {
        super.setMargin(i, i2, i3, i4);
        this.mRangeStyle.setMargin(i, i2, i3, i4);
    }

    @Override // com.alibaba.android.vlayout.layout.MarginLayoutHelper
    public void setPadding(int i, int i2, int i3, int i4) {
        super.setPadding(i, i2, i3, i4);
        this.mRangeStyle.setPadding(i, i2, i3, i4);
    }

    public void setSpanCount(int i) {
        this.mRangeStyle.setSpanCount(i);
    }

    public void setSpanSizeLookup(GridLayoutHelper.SpanSizeLookup spanSizeLookup) {
        this.mRangeStyle.setSpanSizeLookup(spanSizeLookup);
    }

    public void setVGap(int i) {
        this.mRangeStyle.setVGap(i);
    }

    public void setWeights(float[] fArr) {
        this.mRangeStyle.setWeights(fArr);
    }
}
