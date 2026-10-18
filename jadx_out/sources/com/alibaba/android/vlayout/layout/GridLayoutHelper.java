package com.alibaba.android.vlayout.layout;

import android.util.SparseIntArray;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.vlayout.LayoutManagerHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public class GridLayoutHelper extends BaseLayoutHelper {
    private static boolean DEBUG = false;
    private static final int MAIN_DIR_SPEC = View.MeasureSpec.makeMeasureSpec(0, 0);
    private static final String TAG = "GridLayoutHelper";
    private int mHGap;
    private boolean mIgnoreExtra;
    private boolean mIsAutoExpand;
    private boolean mLayoutWithAnchor;
    private View[] mSet;
    private int mSizePerSpan;
    private int[] mSpanCols;
    private int mSpanCount;
    private int[] mSpanIndices;

    @NonNull
    private SpanSizeLookup mSpanSizeLookup;
    private int mTotalSize;
    private int mVGap;
    private float[] mWeights;

    static final class DefaultSpanSizeLookup extends SpanSizeLookup {
        DefaultSpanSizeLookup() {
        }

        @Override // com.alibaba.android.vlayout.layout.GridLayoutHelper.SpanSizeLookup
        public int getSpanIndex(int i, int i2) {
            return (i - this.mStartPosition) % i2;
        }

        @Override // com.alibaba.android.vlayout.layout.GridLayoutHelper.SpanSizeLookup
        public int getSpanSize(int i) {
            return 1;
        }
    }

    public static abstract class SpanSizeLookup {
        final SparseIntArray mSpanIndexCache = new SparseIntArray();
        private boolean mCacheSpanIndices = false;
        int mStartPosition = 0;

        int findReferenceIndexFromCache(int i) {
            int size = this.mSpanIndexCache.size() - 1;
            int i2 = 0;
            while (i2 <= size) {
                int i3 = (i2 + size) >>> 1;
                if (this.mSpanIndexCache.keyAt(i3) < i) {
                    i2 = i3 + 1;
                } else {
                    size = i3 - 1;
                }
            }
            int i4 = i2 - 1;
            if (i4 < 0 || i4 >= this.mSpanIndexCache.size()) {
                return -1;
            }
            return this.mSpanIndexCache.keyAt(i4);
        }

        int getCachedSpanIndex(int i, int i2) {
            if (!this.mCacheSpanIndices) {
                return getSpanIndex(i, i2);
            }
            int i3 = this.mSpanIndexCache.get(i, -1);
            if (i3 != -1) {
                return i3;
            }
            int spanIndex = getSpanIndex(i, i2);
            this.mSpanIndexCache.put(i, spanIndex);
            return spanIndex;
        }

        public int getSpanGroupIndex(int i, int i2) {
            int spanSize = getSpanSize(i);
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < i; i5++) {
                int spanSize2 = getSpanSize(i5);
                i3 += spanSize2;
                if (i3 == i2) {
                    i4++;
                    i3 = 0;
                } else if (i3 > i2) {
                    i4++;
                    i3 = spanSize2;
                }
            }
            return i3 + spanSize > i2 ? i4 + 1 : i4;
        }

        public int getSpanIndex(int i, int i2) {
            int i3;
            int iFindReferenceIndexFromCache;
            int spanSize = getSpanSize(i);
            if (spanSize == i2) {
                return 0;
            }
            int i4 = this.mStartPosition;
            if (!this.mCacheSpanIndices || this.mSpanIndexCache.size() <= 0 || (iFindReferenceIndexFromCache = findReferenceIndexFromCache(i)) < 0) {
                i3 = 0;
            } else {
                int spanSize2 = this.mSpanIndexCache.get(iFindReferenceIndexFromCache) + getSpanSize(iFindReferenceIndexFromCache);
                int i5 = iFindReferenceIndexFromCache + 1;
                i3 = spanSize2;
                i4 = i5;
            }
            while (i4 < i) {
                int spanSize3 = getSpanSize(i4);
                i3 += spanSize3;
                if (i3 == i2) {
                    i3 = 0;
                } else if (i3 > i2) {
                    i3 = spanSize3;
                }
                i4++;
            }
            if (spanSize + i3 <= i2) {
                return i3;
            }
            return 0;
        }

        public abstract int getSpanSize(int i);

        public int getStartPosition() {
            return this.mStartPosition;
        }

        public void invalidateSpanIndexCache() {
            this.mSpanIndexCache.clear();
        }

        public boolean isSpanIndexCacheEnabled() {
            return this.mCacheSpanIndices;
        }

        public void setSpanIndexCacheEnabled(boolean z) {
            this.mCacheSpanIndices = z;
        }

        public void setStartPosition(int i) {
            this.mStartPosition = i;
        }
    }

    public GridLayoutHelper(int i) {
        this(i, -1, -1);
    }

    public GridLayoutHelper(int i, int i2) {
        this(i, i2, 0);
    }

    public GridLayoutHelper(int i, int i2, int i3) {
        this(i, i2, i3, i3);
    }

    public GridLayoutHelper(int i, int i2, int i3, int i4) {
        this.mSpanCount = 4;
        this.mSizePerSpan = 0;
        this.mTotalSize = 0;
        this.mIsAutoExpand = true;
        this.mIgnoreExtra = false;
        this.mSpanSizeLookup = new DefaultSpanSizeLookup();
        this.mVGap = 0;
        this.mHGap = 0;
        this.mWeights = new float[0];
        this.mLayoutWithAnchor = false;
        setSpanCount(i);
        this.mSpanSizeLookup.setSpanIndexCacheEnabled(true);
        setItemCount(i2);
        setVGap(i3);
        setHGap(i4);
    }

    private void assignSpans(RecyclerView.Recycler recycler, RecyclerView.State state, int i, int i2, boolean z, LayoutManagerHelper layoutManagerHelper) {
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
            int spanSize = getSpanSize(recycler, state, layoutManagerHelper.getPosition(this.mSet[i3]));
            if (i6 != -1 || spanSize <= 1) {
                this.mSpanIndices[i3] = i7;
            } else {
                this.mSpanIndices[i3] = i7 - (spanSize - 1);
            }
            i7 += spanSize * i6;
            i3 += i5;
        }
    }

    private void ensureSpanCount() {
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

    private int getMainDirSpec(int i, int i2, int i3, float f) {
        if (!Float.isNaN(f) && f > 0.0f && i3 > 0) {
            return View.MeasureSpec.makeMeasureSpec((int) ((i3 / f) + 0.5f), 1073741824);
        }
        if (Float.isNaN(this.mAspectRatio) || this.mAspectRatio <= 0.0f) {
            return i < 0 ? MAIN_DIR_SPEC : View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        }
        return View.MeasureSpec.makeMeasureSpec((int) ((i2 / this.mAspectRatio) + 0.5f), 1073741824);
    }

    private int getSpanIndex(RecyclerView.Recycler recycler, RecyclerView.State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getCachedSpanIndex(i, this.mSpanCount);
        }
        int iConvertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (iConvertPreLayoutPositionToPostLayout == -1) {
            return 0;
        }
        return this.mSpanSizeLookup.getCachedSpanIndex(iConvertPreLayoutPositionToPostLayout, this.mSpanCount);
    }

    private int getSpanSize(RecyclerView.Recycler recycler, RecyclerView.State state, int i) {
        if (!state.isPreLayout()) {
            return this.mSpanSizeLookup.getSpanSize(i);
        }
        int iConvertPreLayoutPositionToPostLayout = recycler.convertPreLayoutPositionToPostLayout(i);
        if (iConvertPreLayoutPositionToPostLayout == -1) {
            return 0;
        }
        return this.mSpanSizeLookup.getSpanSize(iConvertPreLayoutPositionToPostLayout);
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void checkAnchorInfo(RecyclerView.State state, VirtualLayoutManager.AnchorInfoWrapper anchorInfoWrapper, LayoutManagerHelper layoutManagerHelper) {
        if (state.getItemCount() <= 0 || state.isPreLayout()) {
            return;
        }
        int cachedSpanIndex = this.mSpanSizeLookup.getCachedSpanIndex(anchorInfoWrapper.position, this.mSpanCount);
        if (anchorInfoWrapper.layoutFromEnd) {
            while (cachedSpanIndex < this.mSpanCount - 1 && anchorInfoWrapper.position < ((Integer) getRange().getUpper()).intValue()) {
                anchorInfoWrapper.position++;
                cachedSpanIndex = this.mSpanSizeLookup.getCachedSpanIndex(anchorInfoWrapper.position, this.mSpanCount);
            }
        } else {
            while (cachedSpanIndex > 0 && anchorInfoWrapper.position > 0) {
                anchorInfoWrapper.position--;
                cachedSpanIndex = this.mSpanSizeLookup.getCachedSpanIndex(anchorInfoWrapper.position, this.mSpanCount);
            }
        }
        this.mLayoutWithAnchor = true;
    }

    @Override // com.alibaba.android.vlayout.layout.MarginLayoutHelper, com.alibaba.android.vlayout.LayoutHelper
    public int computeAlignOffset(int i, boolean z, boolean z2, LayoutManagerHelper layoutManagerHelper) {
        int i2;
        int i3;
        int i4;
        int i5;
        boolean z3 = layoutManagerHelper.getOrientation() == 1;
        if (z) {
            if (i == getItemCount() - 1) {
                if (z3) {
                    i4 = this.mMarginBottom;
                    i5 = this.mPaddingBottom;
                } else {
                    i4 = this.mMarginRight;
                    i5 = this.mPaddingRight;
                }
                return i4 + i5;
            }
        } else if (i == 0) {
            if (z3) {
                i2 = -this.mMarginTop;
                i3 = this.mPaddingTop;
            } else {
                i2 = -this.mMarginLeft;
                i3 = this.mPaddingLeft;
            }
            return i2 - i3;
        }
        return super.computeAlignOffset(i, z, z2, layoutManagerHelper);
    }

    public int getHGap() {
        return this.mHGap;
    }

    public int getSpanCount() {
        return this.mSpanCount;
    }

    public int getVGap() {
        return this.mVGap;
    }

    /* JADX WARN: Code duplicated, block: B:38:0x0101 A[PHI: r22
      0x0101: PHI (r22v5 int) = (r22v2 int), (r22v6 int) binds: [B:41:0x0115, B:37:0x00ff] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:39:0x0103 A[PHI: r22
      0x0103: PHI (r22v3 int) = (r22v2 int), (r22v6 int) binds: [B:41:0x0115, B:37:0x00ff] A[DONT_GENERATE, DONT_INLINE]] */
    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:71:0x01bf
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
    public void layoutViews(androidx.recyclerview.widget.RecyclerView.Recycler r28, androidx.recyclerview.widget.RecyclerView.State r29, com.alibaba.android.vlayout.VirtualLayoutManager.LayoutStateWrapper r30, com.alibaba.android.vlayout.layout.LayoutChunkResult r31, com.alibaba.android.vlayout.LayoutManagerHelper r32) {
        /*
            Method dump skipped, instruction units count: 1636
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.alibaba.android.vlayout.layout.GridLayoutHelper.layoutViews(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State, com.alibaba.android.vlayout.VirtualLayoutManager$LayoutStateWrapper, com.alibaba.android.vlayout.layout.LayoutChunkResult, com.alibaba.android.vlayout.LayoutManagerHelper):void");
    }

    @Override // com.alibaba.android.vlayout.layout.BaseLayoutHelper
    public void onClear(LayoutManagerHelper layoutManagerHelper) {
        super.onClear(layoutManagerHelper);
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onItemsChanged(LayoutManagerHelper layoutManagerHelper) {
        super.onItemsChanged(layoutManagerHelper);
        this.mSpanSizeLookup.invalidateSpanIndexCache();
    }

    @Override // com.alibaba.android.vlayout.LayoutHelper
    public void onRangeChange(int i, int i2) {
        this.mSpanSizeLookup.setStartPosition(i);
        this.mSpanSizeLookup.invalidateSpanIndexCache();
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

    public void setSpanSizeLookup(SpanSizeLookup spanSizeLookup) {
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
