package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.Metrics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class ConstraintWidgetContainer extends WidgetContainer {
    private static final boolean DEBUG = false;
    static final boolean DEBUG_GRAPH = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final int MAX_ITERATIONS = 8;
    private static final boolean USE_SNAPSHOT = true;
    int mDebugSolverPassCount;
    public boolean mGroupsWrapOptimized;
    private boolean mHeightMeasuredTooSmall;
    ChainHead[] mHorizontalChainsArray;
    int mHorizontalChainsSize;
    public boolean mHorizontalWrapOptimized;
    private boolean mIsRtl;
    private int mOptimizationLevel;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    public boolean mSkipSolver;
    private Snapshot mSnapshot;
    protected LinearSystem mSystem;
    ChainHead[] mVerticalChainsArray;
    int mVerticalChainsSize;
    public boolean mVerticalWrapOptimized;
    public List<ConstraintWidgetGroup> mWidgetGroups;
    private boolean mWidthMeasuredTooSmall;
    public int mWrapFixedHeight;
    public int mWrapFixedWidth;

    public ConstraintWidgetContainer() {
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mWidgetGroups = new ArrayList();
        this.mGroupsWrapOptimized = false;
        this.mHorizontalWrapOptimized = false;
        this.mVerticalWrapOptimized = false;
        this.mWrapFixedWidth = 0;
        this.mWrapFixedHeight = 0;
        this.mOptimizationLevel = 7;
        this.mSkipSolver = false;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        this.mDebugSolverPassCount = 0;
    }

    public ConstraintWidgetContainer(int i, int i2) {
        super(i, i2);
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mWidgetGroups = new ArrayList();
        this.mGroupsWrapOptimized = false;
        this.mHorizontalWrapOptimized = false;
        this.mVerticalWrapOptimized = false;
        this.mWrapFixedWidth = 0;
        this.mWrapFixedHeight = 0;
        this.mOptimizationLevel = 7;
        this.mSkipSolver = false;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        this.mDebugSolverPassCount = 0;
    }

    public ConstraintWidgetContainer(int i, int i2, int i3, int i4) {
        super(i, i2, i3, i4);
        this.mIsRtl = false;
        this.mSystem = new LinearSystem();
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
        this.mVerticalChainsArray = new ChainHead[4];
        this.mHorizontalChainsArray = new ChainHead[4];
        this.mWidgetGroups = new ArrayList();
        this.mGroupsWrapOptimized = false;
        this.mHorizontalWrapOptimized = false;
        this.mVerticalWrapOptimized = false;
        this.mWrapFixedWidth = 0;
        this.mWrapFixedHeight = 0;
        this.mOptimizationLevel = 7;
        this.mSkipSolver = false;
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        this.mDebugSolverPassCount = 0;
    }

    private void addHorizontalChain(ConstraintWidget constraintWidget) {
        if (this.mHorizontalChainsSize + 1 >= this.mHorizontalChainsArray.length) {
            this.mHorizontalChainsArray = (ChainHead[]) Arrays.copyOf(this.mHorizontalChainsArray, this.mHorizontalChainsArray.length * 2);
        }
        this.mHorizontalChainsArray[this.mHorizontalChainsSize] = new ChainHead(constraintWidget, 0, isRtl());
        this.mHorizontalChainsSize++;
    }

    private void addVerticalChain(ConstraintWidget constraintWidget) {
        if (this.mVerticalChainsSize + 1 >= this.mVerticalChainsArray.length) {
            this.mVerticalChainsArray = (ChainHead[]) Arrays.copyOf(this.mVerticalChainsArray, this.mVerticalChainsArray.length * 2);
        }
        this.mVerticalChainsArray[this.mVerticalChainsSize] = new ChainHead(constraintWidget, 1, isRtl());
        this.mVerticalChainsSize++;
    }

    private void resetChains() {
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
    }

    void addChain(ConstraintWidget constraintWidget, int i) {
        if (i == 0) {
            addHorizontalChain(constraintWidget);
        } else if (i == 1) {
            addVerticalChain(constraintWidget);
        }
    }

    public boolean addChildrenToSolver(LinearSystem linearSystem) {
        addToSolver(linearSystem);
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            if (constraintWidget instanceof ConstraintWidgetContainer) {
                ConstraintWidget.DimensionBehaviour dimensionBehaviour = constraintWidget.mListDimensionBehaviors[0];
                ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = constraintWidget.mListDimensionBehaviors[1];
                if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                constraintWidget.addToSolver(linearSystem);
                if (dimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setHorizontalDimensionBehaviour(dimensionBehaviour);
                }
                if (dimensionBehaviour2 == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    constraintWidget.setVerticalDimensionBehaviour(dimensionBehaviour2);
                }
            } else {
                Optimizer.checkMatchParent(this, linearSystem, constraintWidget);
                constraintWidget.addToSolver(linearSystem);
            }
        }
        if (this.mHorizontalChainsSize > 0) {
            Chain.applyChainConstraints(this, linearSystem, 0);
        }
        if (this.mVerticalChainsSize > 0) {
            Chain.applyChainConstraints(this, linearSystem, 1);
        }
        return true;
    }

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public void analyze(int i) {
        super.analyze(i);
        int size = this.mChildren.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mChildren.get(i2).analyze(i);
        }
    }

    public void fillMetrics(Metrics metrics) {
        this.mSystem.fillMetrics(metrics);
    }

    public ArrayList<Guideline> getHorizontalGuidelines() {
        ArrayList<Guideline> arrayList = new ArrayList<>();
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 0) {
                    arrayList.add(guideline);
                }
            }
        }
        return arrayList;
    }

    public int getOptimizationLevel() {
        return this.mOptimizationLevel;
    }

    public LinearSystem getSystem() {
        return this.mSystem;
    }

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public String getType() {
        return "ConstraintLayout";
    }

    public ArrayList<Guideline> getVerticalGuidelines() {
        ArrayList<Guideline> arrayList = new ArrayList<>();
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            if (constraintWidget instanceof Guideline) {
                Guideline guideline = (Guideline) constraintWidget;
                if (guideline.getOrientation() == 1) {
                    arrayList.add(guideline);
                }
            }
        }
        return arrayList;
    }

    public List<ConstraintWidgetGroup> getWidgetGroups() {
        return this.mWidgetGroups;
    }

    public boolean handlesInternalConstraints() {
        return false;
    }

    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }

    public boolean isRtl() {
        return this.mIsRtl;
    }

    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }

    /* JADX WARN: Code duplicated, block: B:107:0x0255  */
    /* JADX WARN: Code duplicated, block: B:110:0x026a  */
    /* JADX WARN: Code duplicated, block: B:113:0x0286  */
    /* JADX WARN: Code duplicated, block: B:114:0x0293  */
    /* JADX WARN: Code duplicated, block: B:116:0x0296  */
    /* JADX WARN: Code duplicated, block: B:118:0x029f A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:121:0x02a7  */
    /* JADX WARN: Code duplicated, block: B:124:0x02bd A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:128:0x02d3 A[PHI: r0 r9
      0x02d3: PHI (r0v36 ??) = (r0v35 ??), (r0v38 ??), (r0v38 ??), (r0v38 ??) binds: [B:115:0x0294, B:123:0x02bb, B:124:0x02bd, B:126:0x02c3] A[DONT_GENERATE, DONT_INLINE]
      0x02d3: PHI (r9v16 boolean) = (r9v15 boolean), (r9v18 boolean), (r9v18 boolean), (r9v18 boolean) binds: [B:115:0x0294, B:123:0x02bb, B:124:0x02bd, B:126:0x02c3] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:162:0x018f A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:164:0x01d7 A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:72:0x0188  */
    /* JADX WARN: Code duplicated, block: B:74:0x0191  */
    /* JADX WARN: Code duplicated, block: B:76:0x0199  */
    /* JADX WARN: Code duplicated, block: B:81:0x01bd  */
    /* JADX WARN: Code duplicated, block: B:83:0x01c6  */
    /* JADX WARN: Code duplicated, block: B:88:0x01dd  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v35 */
    /* JADX WARN: Type inference failed for: r0v36 */
    /* JADX WARN: Type inference failed for: r0v38 */
    /* JADX WARN: Type inference failed for: r0v41 */
    /* JADX WARN: Type inference failed for: r0v43 */
    /* JADX WARN: Type inference failed for: r0v90 */
    /* JADX WARN: Type inference failed for: r0v91 */
    /* JADX WARN: Type inference failed for: r0v92 */
    /* JADX WARN: Type inference failed for: r0v93 */
    /* JADX WARN: Type inference failed for: r0v94 */
    /* JADX WARN: Type inference failed for: r18v0 */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v15 */
    /* JADX WARN: Type inference failed for: r4v22 */
    /* JADX WARN: Type inference failed for: r4v42 */
    /* JADX WARN: Type inference failed for: r4v7 */
    /* JADX WARN: Type inference failed for: r4v8 */
    /* JADX WARN: Type inference failed for: r8v20 */
    /* JADX WARN: Type inference failed for: r8v21, types: [boolean] */
    /* JADX WARN: Type inference failed for: r8v22 */
    @Override // androidx.constraintlayout.solver.widgets.WidgetContainer
    public void layout() {
        int i;
        boolean z;
        boolean z2;
        boolean z3;
        int i2;
        ConstraintWidget constraintWidget;
        char c;
        int i3;
        boolean z4;
        boolean z5;
        int iMax;
        boolean z6;
        boolean z7;
        int iMax2;
        ?? r8;
        boolean z8;
        ?? r0;
        ?? r4;
        boolean z9;
        boolean z10;
        boolean zAddChildrenToSolver;
        int i4 = this.mX;
        int i5 = this.mY;
        int i6 = 0;
        int iMax3 = Math.max(0, getWidth());
        int iMax4 = Math.max(0, getHeight());
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            setX(this.mPaddingLeft);
            setY(this.mPaddingTop);
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = 0;
            this.mY = 0;
        }
        int i7 = 32;
        if (this.mOptimizationLevel != 0) {
            if (!optimizeFor(8)) {
                optimizeReset();
            }
            if (!optimizeFor(32)) {
                optimize();
            }
            this.mSystem.graphOptimizer = true;
        } else {
            this.mSystem.graphOptimizer = false;
        }
        ConstraintWidget.DimensionBehaviour dimensionBehaviour = this.mListDimensionBehaviors[1];
        ConstraintWidget.DimensionBehaviour dimensionBehaviour2 = this.mListDimensionBehaviors[0];
        resetChains();
        if (this.mWidgetGroups.size() == 0) {
            this.mWidgetGroups.clear();
            this.mWidgetGroups.add(0, new ConstraintWidgetGroup(this.mChildren));
        }
        int size = this.mWidgetGroups.size();
        ArrayList<ConstraintWidget> arrayList = this.mChildren;
        boolean z11 = getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        boolean z12 = false;
        int i8 = 0;
        while (i8 < size && !this.mSkipSolver) {
            if (this.mWidgetGroups.get(i8).mSkipSolver) {
                i = size;
            } else {
                if (optimizeFor(i7)) {
                    if (getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.FIXED && getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.FIXED) {
                        this.mChildren = (ArrayList) this.mWidgetGroups.get(i8).getWidgetsToSolve();
                    } else {
                        this.mChildren = (ArrayList) this.mWidgetGroups.get(i8).mConstrainedGroup;
                    }
                }
                resetChains();
                int size2 = this.mChildren.size();
                for (int i9 = i6; i9 < size2; i9++) {
                    ConstraintWidget constraintWidget2 = this.mChildren.get(i9);
                    if (constraintWidget2 instanceof WidgetContainer) {
                        ((WidgetContainer) constraintWidget2).layout();
                    }
                }
                boolean z13 = z12 ? 1 : 0;
                int i10 = 0;
                ?? r5 = 1;
                while (r5 != 0) {
                    ?? r18 = r5;
                    int i11 = i10 + 1;
                    try {
                        this.mSystem.reset();
                        resetChains();
                        createObjectVariables(this.mSystem);
                        int i12 = 0;
                        while (i12 < size2) {
                            ConstraintWidget constraintWidget3 = this.mChildren.get(i12);
                            z = z13 ? 1 : 0;
                            try {
                                constraintWidget3.createObjectVariables(this.mSystem);
                                i12++;
                                z13 = z ? 1 : 0;
                            } catch (Exception e) {
                                e = e;
                                zAddChildrenToSolver = r18 == true ? 1 : 0;
                                e.printStackTrace();
                                z2 = zAddChildrenToSolver;
                                System.out.println("EXCEPTION : " + e);
                                z3 = z;
                                if (z2) {
                                    updateChildrenFromSolver(this.mSystem, Optimizer.flags);
                                } else {
                                    updateFromSolver(this.mSystem);
                                    i2 = 0;
                                    while (true) {
                                        if (i2 < size2) {
                                            constraintWidget = this.mChildren.get(i2);
                                            if (constraintWidget.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                                                if (constraintWidget.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                                                }
                                                i2++;
                                            } else {
                                                if (constraintWidget.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                                                }
                                                i2++;
                                            }
                                        }
                                    }
                                    if (z11) {
                                        i3 = i11;
                                        z4 = z3 ? 1 : 0;
                                        z5 = false;
                                    } else {
                                        i3 = i11;
                                        z4 = z3 ? 1 : 0;
                                        z5 = false;
                                    }
                                    iMax = Math.max(this.mMinWidth, getWidth());
                                    z7 = z5;
                                    z6 = z4;
                                    if (iMax > getWidth()) {
                                        setWidth(iMax);
                                        this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                        z7 = true;
                                        z6 = true;
                                    }
                                    iMax2 = Math.max(this.mMinHeight, getHeight());
                                    if (iMax2 > getHeight()) {
                                        setHeight(iMax2);
                                        r8 = 1;
                                        this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                                        r0 = 1;
                                        z8 = true;
                                    } else {
                                        r8 = 1;
                                    }
                                    if (z8) {
                                        r0 = z7;
                                        z8 = z6;
                                        r4 = r0;
                                    } else {
                                        r0 = z7;
                                        z8 = z6;
                                        if (this.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                            r0 = r0;
                                            z8 = z8;
                                            if (getWidth() > iMax3) {
                                                this.mWidthMeasuredTooSmall = r8;
                                                this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                                setWidth(iMax3);
                                                ?? r1 = r8;
                                                z8 = r1 == true ? 1 : 0;
                                                r0 = r1;
                                            }
                                        }
                                        r0 = r0;
                                        r0 = r0;
                                        z8 = z8;
                                        z8 = z8;
                                        if (this.mListDimensionBehaviors[r8] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                            r0 = z7;
                                            z8 = z6;
                                            r4 = r0;
                                        } else {
                                            r0 = z7;
                                            z8 = z6;
                                            r4 = r0;
                                        }
                                    }
                                    size = size;
                                    i10 = i3;
                                    r5 = r4;
                                    z13 = z8;
                                }
                                c = 2;
                                if (z11) {
                                    i3 = i11;
                                    z4 = z3 ? 1 : 0;
                                    z5 = false;
                                } else {
                                    i3 = i11;
                                    z4 = z3 ? 1 : 0;
                                    z5 = false;
                                }
                                iMax = Math.max(this.mMinWidth, getWidth());
                                z7 = z5;
                                z6 = z4;
                                if (iMax > getWidth()) {
                                    setWidth(iMax);
                                    this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                    z7 = true;
                                    z6 = true;
                                }
                                iMax2 = Math.max(this.mMinHeight, getHeight());
                                if (iMax2 > getHeight()) {
                                    setHeight(iMax2);
                                    r8 = 1;
                                    this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                                    r0 = 1;
                                    z8 = true;
                                } else {
                                    r8 = 1;
                                }
                                if (z8) {
                                    r0 = z7;
                                    z8 = z6;
                                    if (this.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                        r0 = r0;
                                        z8 = z8;
                                        if (getWidth() > iMax3) {
                                            this.mWidthMeasuredTooSmall = r8;
                                            this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                            setWidth(iMax3);
                                            ?? r2 = r8;
                                            z8 = r2 == true ? 1 : 0;
                                            r0 = r2;
                                        }
                                    }
                                    r0 = r0;
                                    r0 = r0;
                                    z8 = z8;
                                    z8 = z8;
                                    if (this.mListDimensionBehaviors[r8] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                                        r0 = z7;
                                        z8 = z6;
                                        r4 = r0;
                                    } else {
                                        r0 = z7;
                                        z8 = z6;
                                        r4 = r0;
                                    }
                                } else {
                                    r0 = z7;
                                    z8 = z6;
                                    r4 = r0;
                                }
                                size = size;
                                i10 = i3;
                                r5 = r4;
                                z13 = z8;
                            }
                        }
                        z = z13 ? 1 : 0;
                        zAddChildrenToSolver = addChildrenToSolver(this.mSystem);
                        if (zAddChildrenToSolver) {
                            try {
                                this.mSystem.minimize();
                            } catch (Exception e2) {
                                e = e2;
                                e.printStackTrace();
                                z2 = zAddChildrenToSolver;
                                System.out.println("EXCEPTION : " + e);
                                z3 = z;
                            }
                        }
                        z2 = zAddChildrenToSolver ? 1 : 0;
                        z3 = z;
                    } catch (Exception e3) {
                        e = e3;
                        z = z13;
                    }
                    if (z2) {
                        updateChildrenFromSolver(this.mSystem, Optimizer.flags);
                    } else {
                        updateFromSolver(this.mSystem);
                        i2 = 0;
                        while (true) {
                            if (i2 < size2) {
                                constraintWidget = this.mChildren.get(i2);
                                if (constraintWidget.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getWidth() < constraintWidget.getWrapWidth()) {
                                    Optimizer.flags[2] = true;
                                    c = 2;
                                    break;
                                } else {
                                    if (constraintWidget.mListDimensionBehaviors[1] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getHeight() < constraintWidget.getWrapHeight()) {
                                        c = 2;
                                        Optimizer.flags[2] = true;
                                        break;
                                    }
                                    i2++;
                                }
                            }
                        }
                        if (z11 || i11 >= 8 || !Optimizer.flags[c]) {
                            i3 = i11;
                            z4 = z3 ? 1 : 0;
                            z5 = false;
                        } else {
                            int i13 = 0;
                            int iMax5 = 0;
                            int iMax6 = 0;
                            while (i13 < size2) {
                                ConstraintWidget constraintWidget4 = this.mChildren.get(i13);
                                iMax5 = Math.max(iMax5, constraintWidget4.mX + constraintWidget4.getWidth());
                                iMax6 = Math.max(iMax6, constraintWidget4.mY + constraintWidget4.getHeight());
                                i13++;
                                i11 = i11;
                            }
                            i3 = i11;
                            int iMax7 = Math.max(this.mMinWidth, iMax5);
                            int iMax8 = Math.max(this.mMinHeight, iMax6);
                            if (dimensionBehaviour2 != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || getWidth() >= iMax7) {
                                z9 = false;
                                z10 = z3;
                            } else {
                                setWidth(iMax7);
                                this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                                z9 = true;
                                z10 = true;
                            }
                            if (dimensionBehaviour != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || getHeight() >= iMax8) {
                                z4 = z10;
                                z5 = z9;
                            } else {
                                setHeight(iMax8);
                                this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                                z5 = true;
                                z4 = true;
                            }
                        }
                        iMax = Math.max(this.mMinWidth, getWidth());
                        z7 = z5;
                        z6 = z4;
                        if (iMax > getWidth()) {
                            setWidth(iMax);
                            this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                            z7 = true;
                            z6 = true;
                        }
                        iMax2 = Math.max(this.mMinHeight, getHeight());
                        if (iMax2 > getHeight()) {
                            setHeight(iMax2);
                            r8 = 1;
                            this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                            r0 = 1;
                            z8 = true;
                        } else {
                            r8 = 1;
                        }
                        if (z8) {
                            r0 = z7;
                            z8 = z6;
                            if (this.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && iMax3 > 0) {
                                r0 = r0;
                                z8 = z8;
                                if (getWidth() > iMax3) {
                                    this.mWidthMeasuredTooSmall = r8;
                                    this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                    setWidth(iMax3);
                                    ?? r3 = r8;
                                    z8 = r3 == true ? 1 : 0;
                                    r0 = r3;
                                }
                            }
                            r0 = r0;
                            r0 = r0;
                            z8 = z8;
                            z8 = z8;
                            if (this.mListDimensionBehaviors[r8] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || iMax4 <= 0 || getHeight() <= iMax4) {
                                r0 = z7;
                                z8 = z6;
                                r4 = r0;
                            } else {
                                this.mHeightMeasuredTooSmall = r8;
                                this.mListDimensionBehaviors[r8] = ConstraintWidget.DimensionBehaviour.FIXED;
                                setHeight(iMax4);
                                r4 = 1;
                                z8 = true;
                            }
                        } else {
                            r0 = z7;
                            z8 = z6;
                            r4 = r0;
                        }
                        size = size;
                        i10 = i3;
                        r5 = r4;
                        z13 = z8;
                    }
                    c = 2;
                    if (z11) {
                        i3 = i11;
                        z4 = z3 ? 1 : 0;
                        z5 = false;
                    } else {
                        i3 = i11;
                        z4 = z3 ? 1 : 0;
                        z5 = false;
                    }
                    iMax = Math.max(this.mMinWidth, getWidth());
                    z7 = z5;
                    z6 = z4;
                    if (iMax > getWidth()) {
                        setWidth(iMax);
                        this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                        z7 = true;
                        z6 = true;
                    }
                    iMax2 = Math.max(this.mMinHeight, getHeight());
                    if (iMax2 > getHeight()) {
                        setHeight(iMax2);
                        r8 = 1;
                        this.mListDimensionBehaviors[1] = ConstraintWidget.DimensionBehaviour.FIXED;
                        r0 = 1;
                        z8 = true;
                    } else {
                        r8 = 1;
                    }
                    if (z8) {
                        r0 = z7;
                        z8 = z6;
                        if (this.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                            r0 = r0;
                            z8 = z8;
                            if (getWidth() > iMax3) {
                                this.mWidthMeasuredTooSmall = r8;
                                this.mListDimensionBehaviors[0] = ConstraintWidget.DimensionBehaviour.FIXED;
                                setWidth(iMax3);
                                ?? r6 = r8;
                                z8 = r6 == true ? 1 : 0;
                                r0 = r6;
                            }
                        }
                        r0 = r0;
                        r0 = r0;
                        z8 = z8;
                        z8 = z8;
                        if (this.mListDimensionBehaviors[r8] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                            r0 = z7;
                            z8 = z6;
                            r4 = r0;
                        } else {
                            r0 = z7;
                            z8 = z6;
                            r4 = r0;
                        }
                    } else {
                        r0 = z7;
                        z8 = z6;
                        r4 = r0;
                    }
                    size = size;
                    i10 = i3;
                    r5 = r4;
                    z13 = z8;
                }
                boolean z14 = z13 ? 1 : 0;
                i = size;
                this.mWidgetGroups.get(i8).updateUnresolvedWidgets();
                z12 = z14 ? 1 : 0;
            }
            i8++;
            size = i;
            i6 = 0;
            i7 = 32;
            z12 = z12;
        }
        this.mChildren = arrayList;
        if (this.mParent != null) {
            int iMax9 = Math.max(this.mMinWidth, getWidth());
            int iMax10 = Math.max(this.mMinHeight, getHeight());
            this.mSnapshot.applyTo(this);
            setWidth(iMax9 + this.mPaddingLeft + this.mPaddingRight);
            setHeight(iMax10 + this.mPaddingTop + this.mPaddingBottom);
        } else {
            this.mX = i4;
            this.mY = i5;
        }
        if (z12) {
            this.mListDimensionBehaviors[0] = dimensionBehaviour2;
            this.mListDimensionBehaviors[1] = dimensionBehaviour;
        }
        resetSolverVariables(this.mSystem.getCache());
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    public void optimize() {
        if (!optimizeFor(8)) {
            analyze(this.mOptimizationLevel);
        }
        solveGraph();
    }

    public boolean optimizeFor(int i) {
        return (this.mOptimizationLevel & i) == i;
    }

    public void optimizeForDimensions(int i, int i2) {
        if (this.mListDimensionBehaviors[0] != ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && this.mResolutionWidth != null) {
            this.mResolutionWidth.resolve(i);
        }
        if (this.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || this.mResolutionHeight == null) {
            return;
        }
        this.mResolutionHeight.resolve(i2);
    }

    public void optimizeReset() {
        int size = this.mChildren.size();
        resetResolutionNodes();
        for (int i = 0; i < size; i++) {
            this.mChildren.get(i).resetResolutionNodes();
        }
    }

    public void preOptimize() {
        optimizeReset();
        analyze(this.mOptimizationLevel);
    }

    @Override // androidx.constraintlayout.solver.widgets.WidgetContainer, androidx.constraintlayout.solver.widgets.ConstraintWidget
    public void reset() {
        this.mSystem.reset();
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        this.mWidgetGroups.clear();
        this.mSkipSolver = false;
        super.reset();
    }

    public void resetGraph() {
        ResolutionAnchor resolutionNode = getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
        ResolutionAnchor resolutionNode2 = getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
        resolutionNode.invalidateAnchors();
        resolutionNode2.invalidateAnchors();
        resolutionNode.resolve(null, 0.0f);
        resolutionNode2.resolve(null, 0.0f);
    }

    public void setOptimizationLevel(int i) {
        this.mOptimizationLevel = i;
    }

    public void setPadding(int i, int i2, int i3, int i4) {
        this.mPaddingLeft = i;
        this.mPaddingTop = i2;
        this.mPaddingRight = i3;
        this.mPaddingBottom = i4;
    }

    public void setRtl(boolean z) {
        this.mIsRtl = z;
    }

    public void solveGraph() {
        ResolutionAnchor resolutionNode = getAnchor(ConstraintAnchor.Type.LEFT).getResolutionNode();
        ResolutionAnchor resolutionNode2 = getAnchor(ConstraintAnchor.Type.TOP).getResolutionNode();
        resolutionNode.resolve(null, 0.0f);
        resolutionNode2.resolve(null, 0.0f);
    }

    public void updateChildrenFromSolver(LinearSystem linearSystem, boolean[] zArr) {
        zArr[2] = false;
        updateFromSolver(linearSystem);
        int size = this.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = this.mChildren.get(i);
            constraintWidget.updateFromSolver(linearSystem);
            if (constraintWidget.mListDimensionBehaviors[0] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getWidth() < constraintWidget.getWrapWidth()) {
                zArr[2] = true;
            }
            if (constraintWidget.mListDimensionBehaviors[1] == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && constraintWidget.getHeight() < constraintWidget.getWrapHeight()) {
                zArr[2] = true;
            }
        }
    }
}
