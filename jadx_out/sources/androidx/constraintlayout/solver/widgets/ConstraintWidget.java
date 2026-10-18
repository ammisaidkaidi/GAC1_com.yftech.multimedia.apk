package androidx.constraintlayout.solver.widgets;

import android.net.wifi.WifiEnterpriseConfig;
import androidx.constraintlayout.solver.Cache;
import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.SolverVariable;
import androidx.exifinterface.media.ExifInterface;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class ConstraintWidget {
    protected static final int ANCHOR_BASELINE = 4;
    protected static final int ANCHOR_BOTTOM = 3;
    protected static final int ANCHOR_LEFT = 0;
    protected static final int ANCHOR_RIGHT = 1;
    protected static final int ANCHOR_TOP = 2;
    private static final boolean AUTOTAG_CENTER = false;
    public static final int CHAIN_PACKED = 2;
    public static final int CHAIN_SPREAD = 0;
    public static final int CHAIN_SPREAD_INSIDE = 1;
    public static float DEFAULT_BIAS = 0.5f;
    static final int DIMENSION_HORIZONTAL = 0;
    static final int DIMENSION_VERTICAL = 1;
    protected static final int DIRECT = 2;
    public static final int GONE = 8;
    public static final int HORIZONTAL = 0;
    public static final int INVISIBLE = 4;
    public static final int MATCH_CONSTRAINT_PERCENT = 2;
    public static final int MATCH_CONSTRAINT_RATIO = 3;
    public static final int MATCH_CONSTRAINT_RATIO_RESOLVED = 4;
    public static final int MATCH_CONSTRAINT_SPREAD = 0;
    public static final int MATCH_CONSTRAINT_WRAP = 1;
    protected static final int SOLVER = 1;
    public static final int UNKNOWN = -1;
    public static final int VERTICAL = 1;
    public static final int VISIBLE = 0;
    private static final int WRAP = -2;
    protected ArrayList<ConstraintAnchor> mAnchors;
    ConstraintAnchor mBaseline;
    int mBaselineDistance;
    ConstraintWidgetGroup mBelongingGroup;
    ConstraintAnchor mBottom;
    boolean mBottomHasCentered;
    ConstraintAnchor mCenter;
    ConstraintAnchor mCenterX;
    ConstraintAnchor mCenterY;
    private float mCircleConstraintAngle;
    private Object mCompanionWidget;
    private int mContainerItemSkip;
    private String mDebugName;
    protected float mDimensionRatio;
    protected int mDimensionRatioSide;
    int mDistToBottom;
    int mDistToLeft;
    int mDistToRight;
    int mDistToTop;
    private int mDrawHeight;
    private int mDrawWidth;
    private int mDrawX;
    private int mDrawY;
    boolean mGroupsToSolver;
    int mHeight;
    float mHorizontalBiasPercent;
    boolean mHorizontalChainFixedPosition;
    int mHorizontalChainStyle;
    ConstraintWidget mHorizontalNextWidget;
    public int mHorizontalResolution;
    boolean mHorizontalWrapVisited;
    boolean mIsHeightWrapContent;
    boolean mIsWidthWrapContent;
    ConstraintAnchor mLeft;
    boolean mLeftHasCentered;
    protected ConstraintAnchor[] mListAnchors;
    protected DimensionBehaviour[] mListDimensionBehaviors;
    protected ConstraintWidget[] mListNextMatchConstraintsWidget;
    int mMatchConstraintDefaultHeight;
    int mMatchConstraintDefaultWidth;
    int mMatchConstraintMaxHeight;
    int mMatchConstraintMaxWidth;
    int mMatchConstraintMinHeight;
    int mMatchConstraintMinWidth;
    float mMatchConstraintPercentHeight;
    float mMatchConstraintPercentWidth;
    private int[] mMaxDimension;
    protected int mMinHeight;
    protected int mMinWidth;
    protected ConstraintWidget[] mNextChainWidget;
    protected int mOffsetX;
    protected int mOffsetY;
    boolean mOptimizerMeasurable;
    boolean mOptimizerMeasured;
    ConstraintWidget mParent;
    int mRelX;
    int mRelY;
    ResolutionDimension mResolutionHeight;
    ResolutionDimension mResolutionWidth;
    float mResolvedDimensionRatio;
    int mResolvedDimensionRatioSide;
    int[] mResolvedMatchConstraintDefault;
    ConstraintAnchor mRight;
    boolean mRightHasCentered;
    ConstraintAnchor mTop;
    boolean mTopHasCentered;
    private String mType;
    float mVerticalBiasPercent;
    boolean mVerticalChainFixedPosition;
    int mVerticalChainStyle;
    ConstraintWidget mVerticalNextWidget;
    public int mVerticalResolution;
    boolean mVerticalWrapVisited;
    private int mVisibility;
    float[] mWeight;
    int mWidth;
    private int mWrapHeight;
    private int mWrapWidth;
    protected int mX;
    protected int mY;

    public enum ContentAlignment {
        BEGIN,
        MIDDLE,
        END,
        TOP,
        VERTICAL_MIDDLE,
        BOTTOM,
        LEFT,
        RIGHT
    }

    public enum DimensionBehaviour {
        FIXED,
        WRAP_CONTENT,
        MATCH_CONSTRAINT,
        MATCH_PARENT
    }

    public ConstraintWidget() {
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mResolvedMatchConstraintDefault = new int[2];
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMaxWidth = 0;
        this.mMatchConstraintPercentWidth = 1.0f;
        this.mMatchConstraintMinHeight = 0;
        this.mMatchConstraintMaxHeight = 0;
        this.mMatchConstraintPercentHeight = 1.0f;
        this.mResolvedDimensionRatioSide = -1;
        this.mResolvedDimensionRatio = 1.0f;
        this.mBelongingGroup = null;
        this.mMaxDimension = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
        this.mCircleConstraintAngle = 0.0f;
        this.mLeft = new ConstraintAnchor(this, ConstraintAnchor.Type.LEFT);
        this.mTop = new ConstraintAnchor(this, ConstraintAnchor.Type.TOP);
        this.mRight = new ConstraintAnchor(this, ConstraintAnchor.Type.RIGHT);
        this.mBottom = new ConstraintAnchor(this, ConstraintAnchor.Type.BOTTOM);
        this.mBaseline = new ConstraintAnchor(this, ConstraintAnchor.Type.BASELINE);
        this.mCenterX = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_X);
        this.mCenterY = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_Y);
        this.mCenter = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER);
        this.mListAnchors = new ConstraintAnchor[]{this.mLeft, this.mRight, this.mTop, this.mBottom, this.mBaseline, this.mCenter};
        this.mAnchors = new ArrayList<>();
        this.mListDimensionBehaviors = new DimensionBehaviour[]{DimensionBehaviour.FIXED, DimensionBehaviour.FIXED};
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mX = 0;
        this.mY = 0;
        this.mRelX = 0;
        this.mRelY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mOptimizerMeasurable = false;
        this.mOptimizerMeasured = false;
        this.mGroupsToSolver = false;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mWeight = new float[]{-1.0f, -1.0f};
        this.mListNextMatchConstraintsWidget = new ConstraintWidget[]{null, null};
        this.mNextChainWidget = new ConstraintWidget[]{null, null};
        this.mHorizontalNextWidget = null;
        this.mVerticalNextWidget = null;
        addAnchors();
    }

    public ConstraintWidget(int i, int i2) {
        this(0, 0, i, i2);
    }

    public ConstraintWidget(int i, int i2, int i3, int i4) {
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mResolvedMatchConstraintDefault = new int[2];
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMaxWidth = 0;
        this.mMatchConstraintPercentWidth = 1.0f;
        this.mMatchConstraintMinHeight = 0;
        this.mMatchConstraintMaxHeight = 0;
        this.mMatchConstraintPercentHeight = 1.0f;
        this.mResolvedDimensionRatioSide = -1;
        this.mResolvedDimensionRatio = 1.0f;
        this.mBelongingGroup = null;
        this.mMaxDimension = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE};
        this.mCircleConstraintAngle = 0.0f;
        this.mLeft = new ConstraintAnchor(this, ConstraintAnchor.Type.LEFT);
        this.mTop = new ConstraintAnchor(this, ConstraintAnchor.Type.TOP);
        this.mRight = new ConstraintAnchor(this, ConstraintAnchor.Type.RIGHT);
        this.mBottom = new ConstraintAnchor(this, ConstraintAnchor.Type.BOTTOM);
        this.mBaseline = new ConstraintAnchor(this, ConstraintAnchor.Type.BASELINE);
        this.mCenterX = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_X);
        this.mCenterY = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_Y);
        this.mCenter = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER);
        this.mListAnchors = new ConstraintAnchor[]{this.mLeft, this.mRight, this.mTop, this.mBottom, this.mBaseline, this.mCenter};
        this.mAnchors = new ArrayList<>();
        this.mListDimensionBehaviors = new DimensionBehaviour[]{DimensionBehaviour.FIXED, DimensionBehaviour.FIXED};
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mX = 0;
        this.mY = 0;
        this.mRelX = 0;
        this.mRelY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mOptimizerMeasurable = false;
        this.mOptimizerMeasured = false;
        this.mGroupsToSolver = false;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mWeight = new float[]{-1.0f, -1.0f};
        this.mListNextMatchConstraintsWidget = new ConstraintWidget[]{null, null};
        this.mNextChainWidget = new ConstraintWidget[]{null, null};
        this.mHorizontalNextWidget = null;
        this.mVerticalNextWidget = null;
        this.mX = i;
        this.mY = i2;
        this.mWidth = i3;
        this.mHeight = i4;
        addAnchors();
        forceUpdateDrawPosition();
    }

    private void addAnchors() {
        this.mAnchors.add(this.mLeft);
        this.mAnchors.add(this.mTop);
        this.mAnchors.add(this.mRight);
        this.mAnchors.add(this.mBottom);
        this.mAnchors.add(this.mCenterX);
        this.mAnchors.add(this.mCenterY);
        this.mAnchors.add(this.mCenter);
        this.mAnchors.add(this.mBaseline);
    }

    /* JADX WARN: Code duplicated, block: B:160:0x02a8  */
    /* JADX WARN: Code duplicated, block: B:164:0x02df A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:165:0x02e1 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:167:0x02e9  */
    /* JADX WARN: Code duplicated, block: B:170:0x02f8  */
    /* JADX WARN: Code duplicated, block: B:171:0x02fb  */
    /* JADX WARN: Code duplicated, block: B:173:0x0300 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:174:0x0302 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:177:0x030d A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:178:0x030f A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:182:0x031c  */
    /* JADX WARN: Code duplicated, block: B:183:0x0325  */
    private void applyConstraints(LinearSystem linearSystem, boolean z, SolverVariable solverVariable, SolverVariable solverVariable2, DimensionBehaviour dimensionBehaviour, boolean z2, ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i, int i2, int i3, int i4, float f, boolean z3, boolean z4, int i5, int i6, int i7, float f2, boolean z5) {
        boolean z6;
        int iMin;
        int i8;
        int i9;
        int i10;
        int i11;
        int i12;
        SolverVariable solverVariableCreateObjectVariable;
        SolverVariable solverVariable3;
        int i13;
        int i14;
        int i15;
        SolverVariable solverVariable4;
        boolean z7;
        boolean z8;
        SolverVariable solverVariable5;
        SolverVariable solverVariable6;
        SolverVariable solverVariable7;
        boolean z9;
        boolean z10;
        int i16;
        int i17;
        int i18;
        int i19;
        SolverVariable solverVariable8;
        SolverVariable solverVariable9;
        int i20;
        int i21;
        boolean z11;
        int i22;
        boolean z12;
        int i23;
        SolverVariable solverVariableCreateObjectVariable2 = linearSystem.createObjectVariable(constraintAnchor);
        SolverVariable solverVariableCreateObjectVariable3 = linearSystem.createObjectVariable(constraintAnchor2);
        SolverVariable solverVariableCreateObjectVariable4 = linearSystem.createObjectVariable(constraintAnchor.getTarget());
        SolverVariable solverVariableCreateObjectVariable5 = linearSystem.createObjectVariable(constraintAnchor2.getTarget());
        if (linearSystem.graphOptimizer && constraintAnchor.getResolutionNode().state == 1 && constraintAnchor2.getResolutionNode().state == 1) {
            if (LinearSystem.getMetrics() != null) {
                LinearSystem.getMetrics().resolvedWidgets++;
            }
            constraintAnchor.getResolutionNode().addResolvedValue(linearSystem);
            constraintAnchor2.getResolutionNode().addResolvedValue(linearSystem);
            if (z4 || !z) {
                return;
            }
            linearSystem.addGreaterThan(solverVariable2, solverVariableCreateObjectVariable3, 0, 6);
            return;
        }
        if (LinearSystem.getMetrics() != null) {
            LinearSystem.getMetrics().nonresolvedWidgets++;
        }
        boolean zIsConnected = constraintAnchor.isConnected();
        boolean zIsConnected2 = constraintAnchor2.isConnected();
        boolean zIsConnected3 = this.mCenter.isConnected();
        int i24 = zIsConnected ? 1 : 0;
        if (zIsConnected2) {
            i24++;
        }
        if (zIsConnected3) {
            i24++;
        }
        int i25 = z3 ? 3 : i5;
        switch (dimensionBehaviour) {
            case MATCH_CONSTRAINT:
                if (i25 != 4) {
                    z6 = true;
                    break;
                }
            case FIXED:
            case WRAP_CONTENT:
            case MATCH_PARENT:
            default:
                z6 = false;
                break;
        }
        int i26 = i24;
        if (this.mVisibility == 8) {
            iMin = 0;
            z6 = false;
        } else {
            iMin = i2;
        }
        if (z5) {
            if (!zIsConnected && !zIsConnected2 && !zIsConnected3) {
                linearSystem.addEquality(solverVariableCreateObjectVariable2, i);
            } else if (zIsConnected && !zIsConnected2) {
                i8 = 6;
                linearSystem.addEquality(solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable4, constraintAnchor.getMargin(), 6);
            }
            i8 = 6;
        } else {
            i8 = 6;
        }
        if (z6) {
            i9 = i6 == -2 ? iMin : i6;
            i10 = i7 == -2 ? iMin : i7;
            if (i9 > 0) {
                i11 = 6;
                linearSystem.addGreaterThan(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, i9, 6);
                iMin = Math.max(iMin, i9);
            } else {
                i11 = 6;
            }
            if (i10 > 0) {
                linearSystem.addLowerThan(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, i10, i11);
                iMin = Math.min(iMin, i10);
            }
            if (i25 == 1) {
                if (z) {
                    linearSystem.addEquality(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, iMin, 6);
                } else if (z4) {
                    linearSystem.addEquality(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, iMin, 4);
                    solverVariableCreateObjectVariable4 = solverVariableCreateObjectVariable4;
                    i25 = i25;
                    solverVariableCreateObjectVariable5 = solverVariableCreateObjectVariable5;
                    i26 = i26;
                    i12 = 2;
                    i13 = 4;
                } else {
                    linearSystem.addEquality(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, iMin, 1);
                }
                i12 = 2;
                i13 = 4;
            } else if (i25 == 2) {
                solverVariableCreateObjectVariable4 = solverVariableCreateObjectVariable4;
                if (constraintAnchor.getType() == ConstraintAnchor.Type.TOP || constraintAnchor.getType() == ConstraintAnchor.Type.BOTTOM) {
                    SolverVariable solverVariableCreateObjectVariable6 = linearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.TOP));
                    solverVariableCreateObjectVariable = linearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.BOTTOM));
                    solverVariable3 = solverVariableCreateObjectVariable6;
                } else {
                    SolverVariable solverVariableCreateObjectVariable7 = linearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.LEFT));
                    solverVariableCreateObjectVariable = linearSystem.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.RIGHT));
                    solverVariable3 = solverVariableCreateObjectVariable7;
                }
                i25 = i25;
                solverVariableCreateObjectVariable5 = solverVariableCreateObjectVariable5;
                i26 = i26;
                i12 = 2;
                i13 = 4;
                linearSystem.addConstraint(linearSystem.createRow().createRowDimensionRatio(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable, solverVariable3, f2));
                z6 = false;
            } else {
                i12 = 2;
                i13 = 4;
            }
            if (!z6 || i26 == i12) {
                i14 = i13;
            } else {
                i14 = i13;
                if (!z3) {
                    int iMax = Math.max(i9, iMin);
                    if (i10 > 0) {
                        iMax = Math.min(i10, iMax);
                    }
                    linearSystem.addEquality(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, iMax, 6);
                    z6 = false;
                }
            }
        } else {
            if (z2) {
                linearSystem.addEquality(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, 0, 3);
                if (i3 > 0) {
                    i23 = 6;
                    linearSystem.addGreaterThan(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, i3, 6);
                } else {
                    i23 = 6;
                }
                if (i4 < Integer.MAX_VALUE) {
                    linearSystem.addLowerThan(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, i4, i23);
                }
            } else {
                linearSystem.addEquality(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, iMin, i8);
            }
            i9 = i6;
            i10 = i7;
            solverVariableCreateObjectVariable4 = solverVariableCreateObjectVariable4;
            i25 = i25;
            solverVariableCreateObjectVariable5 = solverVariableCreateObjectVariable5;
            i26 = i26;
            i14 = 4;
        }
        if (!z5 || z4) {
            if (i26 >= 2 || !z) {
                return;
            }
            linearSystem.addGreaterThan(solverVariableCreateObjectVariable2, solverVariable, 0, 6);
            linearSystem.addGreaterThan(solverVariable2, solverVariableCreateObjectVariable3, 0, 6);
            return;
        }
        int i27 = 5;
        if (zIsConnected || zIsConnected2 || zIsConnected3) {
            i15 = 0;
            if (!zIsConnected || zIsConnected2) {
                if (!zIsConnected && zIsConnected2) {
                    linearSystem.addEquality(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable5, -constraintAnchor2.getMargin(), 6);
                    if (z) {
                        linearSystem.addGreaterThan(solverVariableCreateObjectVariable2, solverVariable, 0, 5);
                    }
                } else if (zIsConnected && zIsConnected2) {
                    if (z6) {
                        if (z && i3 == 0) {
                            linearSystem.addGreaterThan(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable2, 0, 6);
                        }
                        if (i25 == 0) {
                            if (i10 > 0 || i9 > 0) {
                                i22 = i14;
                                z12 = true;
                            } else {
                                i22 = 6;
                                z12 = false;
                            }
                            solverVariable4 = solverVariableCreateObjectVariable4;
                            linearSystem.addEquality(solverVariableCreateObjectVariable2, solverVariable4, constraintAnchor.getMargin(), i22);
                            linearSystem.addEquality(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable5, -constraintAnchor2.getMargin(), i22);
                            z8 = z12;
                            i27 = 5;
                            z7 = i10 > 0 || i9 > 0;
                        } else {
                            solverVariable4 = solverVariableCreateObjectVariable4;
                            int i28 = i25;
                            if (i28 == 1) {
                                z7 = true;
                                z8 = true;
                                i27 = 6;
                            } else if (i28 == 3) {
                                int i29 = (z3 || this.mResolvedDimensionRatioSide == -1 || i10 > 0) ? i14 : 6;
                                linearSystem.addEquality(solverVariableCreateObjectVariable2, solverVariable4, constraintAnchor.getMargin(), i29);
                                linearSystem.addEquality(solverVariableCreateObjectVariable3, solverVariableCreateObjectVariable5, -constraintAnchor2.getMargin(), i29);
                                i27 = 5;
                                z7 = true;
                                z8 = true;
                            } else {
                                z7 = false;
                            }
                        }
                        if (z7) {
                            solverVariable5 = solverVariableCreateObjectVariable5;
                            solverVariable6 = solverVariableCreateObjectVariable3;
                            solverVariable7 = solverVariableCreateObjectVariable2;
                            linearSystem.addCentering(solverVariableCreateObjectVariable2, solverVariable4, constraintAnchor.getMargin(), f, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable3, constraintAnchor2.getMargin(), i27);
                            z11 = constraintAnchor.mTarget.mOwner instanceof Barrier;
                            z10 = true;
                            boolean z13 = constraintAnchor2.mTarget.mOwner instanceof Barrier;
                            if (!z11 && !z13) {
                                z9 = true;
                                i16 = 6;
                                i17 = 5;
                                z10 = z;
                            } else if (z11 && z13) {
                                z9 = z;
                                i16 = 5;
                                i17 = 6;
                            }
                            if (z8) {
                                i19 = 6;
                                i18 = 6;
                            } else {
                                i18 = i16;
                                i19 = i17;
                            }
                            if ((z6 && z10) || z8) {
                                linearSystem.addGreaterThan(solverVariable7, solverVariable4, constraintAnchor.getMargin(), i19);
                            }
                            if ((z6 && z9) || z8) {
                                linearSystem.addLowerThan(solverVariable6, solverVariable5, -constraintAnchor2.getMargin(), i18);
                            }
                            solverVariable8 = solverVariable7;
                            if (z) {
                                solverVariable9 = solverVariable6;
                                i20 = 0;
                                i21 = 6;
                                linearSystem.addGreaterThan(solverVariable8, solverVariable, 0, 6);
                            } else {
                                solverVariable9 = solverVariable6;
                                i20 = 0;
                                i21 = 6;
                            }
                        } else {
                            solverVariable5 = solverVariableCreateObjectVariable5;
                            solverVariable6 = solverVariableCreateObjectVariable3;
                            solverVariable7 = solverVariableCreateObjectVariable2;
                        }
                        z9 = z;
                        z10 = z9;
                        i16 = 5;
                        i17 = 5;
                        if (z8) {
                            i19 = 6;
                            i18 = 6;
                        } else {
                            i18 = i16;
                            i19 = i17;
                        }
                        if (z6) {
                            linearSystem.addGreaterThan(solverVariable7, solverVariable4, constraintAnchor.getMargin(), i19);
                        } else {
                            linearSystem.addGreaterThan(solverVariable7, solverVariable4, constraintAnchor.getMargin(), i19);
                        }
                        if (z6) {
                            linearSystem.addLowerThan(solverVariable6, solverVariable5, -constraintAnchor2.getMargin(), i18);
                        } else {
                            linearSystem.addLowerThan(solverVariable6, solverVariable5, -constraintAnchor2.getMargin(), i18);
                        }
                        solverVariable8 = solverVariable7;
                        if (z) {
                            solverVariable9 = solverVariable6;
                            i20 = 0;
                            i21 = 6;
                            linearSystem.addGreaterThan(solverVariable8, solverVariable, 0, 6);
                        } else {
                            solverVariable9 = solverVariable6;
                            i20 = 0;
                            i21 = 6;
                        }
                    } else {
                        solverVariable4 = solverVariableCreateObjectVariable4;
                        z7 = true;
                    }
                    z8 = false;
                    if (z7) {
                        solverVariable5 = solverVariableCreateObjectVariable5;
                        solverVariable6 = solverVariableCreateObjectVariable3;
                        solverVariable7 = solverVariableCreateObjectVariable2;
                        linearSystem.addCentering(solverVariableCreateObjectVariable2, solverVariable4, constraintAnchor.getMargin(), f, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable3, constraintAnchor2.getMargin(), i27);
                        z11 = constraintAnchor.mTarget.mOwner instanceof Barrier;
                        z10 = true;
                        boolean z14 = constraintAnchor2.mTarget.mOwner instanceof Barrier;
                        if (!z11) {
                            if (z11) {
                            }
                        } else if (z11) {
                        }
                        if (z8) {
                            i19 = 6;
                            i18 = 6;
                        } else {
                            i18 = i16;
                            i19 = i17;
                        }
                        if (z6) {
                            linearSystem.addGreaterThan(solverVariable7, solverVariable4, constraintAnchor.getMargin(), i19);
                        } else {
                            linearSystem.addGreaterThan(solverVariable7, solverVariable4, constraintAnchor.getMargin(), i19);
                        }
                        if (z6) {
                            linearSystem.addLowerThan(solverVariable6, solverVariable5, -constraintAnchor2.getMargin(), i18);
                        } else {
                            linearSystem.addLowerThan(solverVariable6, solverVariable5, -constraintAnchor2.getMargin(), i18);
                        }
                        solverVariable8 = solverVariable7;
                        if (z) {
                            solverVariable9 = solverVariable6;
                            i20 = 0;
                            i21 = 6;
                            linearSystem.addGreaterThan(solverVariable8, solverVariable, 0, 6);
                        } else {
                            solverVariable9 = solverVariable6;
                            i20 = 0;
                            i21 = 6;
                        }
                    } else {
                        solverVariable5 = solverVariableCreateObjectVariable5;
                        solverVariable6 = solverVariableCreateObjectVariable3;
                        solverVariable7 = solverVariableCreateObjectVariable2;
                    }
                    z9 = z;
                    z10 = z9;
                    i16 = 5;
                    i17 = 5;
                    if (z8) {
                        i19 = 6;
                        i18 = 6;
                    } else {
                        i18 = i16;
                        i19 = i17;
                    }
                    if (z6) {
                        linearSystem.addGreaterThan(solverVariable7, solverVariable4, constraintAnchor.getMargin(), i19);
                    } else {
                        linearSystem.addGreaterThan(solverVariable7, solverVariable4, constraintAnchor.getMargin(), i19);
                    }
                    if (z6) {
                        linearSystem.addLowerThan(solverVariable6, solverVariable5, -constraintAnchor2.getMargin(), i18);
                    } else {
                        linearSystem.addLowerThan(solverVariable6, solverVariable5, -constraintAnchor2.getMargin(), i18);
                    }
                    solverVariable8 = solverVariable7;
                    if (z) {
                        solverVariable9 = solverVariable6;
                        i20 = 0;
                        i21 = 6;
                        linearSystem.addGreaterThan(solverVariable8, solverVariable, 0, 6);
                    } else {
                        solverVariable9 = solverVariable6;
                        i20 = 0;
                        i21 = 6;
                    }
                }
            } else if (z) {
                linearSystem.addGreaterThan(solverVariable2, solverVariableCreateObjectVariable3, 0, 5);
            }
            i20 = i15;
            solverVariable9 = solverVariableCreateObjectVariable3;
            i21 = 6;
        } else if (z) {
            i15 = 0;
            linearSystem.addGreaterThan(solverVariable2, solverVariableCreateObjectVariable3, 0, 5);
            i20 = i15;
            solverVariable9 = solverVariableCreateObjectVariable3;
            i21 = 6;
        } else {
            solverVariable9 = solverVariableCreateObjectVariable3;
            i20 = 0;
            i21 = 6;
        }
        if (z) {
            linearSystem.addGreaterThan(solverVariable2, solverVariable9, i20, i21);
        }
    }

    private boolean isChainHead(int i) {
        int i2 = i * 2;
        if (this.mListAnchors[i2].mTarget != null && this.mListAnchors[i2].mTarget.mTarget != this.mListAnchors[i2]) {
            int i3 = i2 + 1;
            if (this.mListAnchors[i3].mTarget != null && this.mListAnchors[i3].mTarget.mTarget == this.mListAnchors[i3]) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:100:0x01c1  */
    /* JADX WARN: Code duplicated, block: B:107:0x01cf A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:111:0x01db  */
    /* JADX WARN: Code duplicated, block: B:114:0x01e2  */
    /* JADX WARN: Code duplicated, block: B:117:0x01f4  */
    /* JADX WARN: Code duplicated, block: B:119:0x01f8  */
    /* JADX WARN: Code duplicated, block: B:120:0x0203  */
    /* JADX WARN: Code duplicated, block: B:123:0x0209  */
    /* JADX WARN: Code duplicated, block: B:124:0x0214  */
    /* JADX WARN: Code duplicated, block: B:126:0x025e  */
    /* JADX WARN: Code duplicated, block: B:129:0x026f A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:130:0x0270  */
    /* JADX WARN: Code duplicated, block: B:132:0x0279  */
    /* JADX WARN: Code duplicated, block: B:135:0x027f  */
    /* JADX WARN: Code duplicated, block: B:137:0x0282  */
    /* JADX WARN: Code duplicated, block: B:142:0x028e  */
    /* JADX WARN: Code duplicated, block: B:145:0x0294  */
    /* JADX WARN: Code duplicated, block: B:147:0x029e  */
    /* JADX WARN: Code duplicated, block: B:148:0x02ac  */
    /* JADX WARN: Code duplicated, block: B:150:0x02c0  */
    /* JADX WARN: Code duplicated, block: B:151:0x02cf  */
    /* JADX WARN: Code duplicated, block: B:155:0x02d9  */
    /* JADX WARN: Code duplicated, block: B:156:0x02e4  */
    /* JADX WARN: Code duplicated, block: B:159:0x02ea  */
    /* JADX WARN: Code duplicated, block: B:160:0x02f4  */
    /* JADX WARN: Code duplicated, block: B:163:0x032b  */
    /* JADX WARN: Code duplicated, block: B:165:0x0333  */
    /* JADX WARN: Code duplicated, block: B:166:0x0344  */
    /* JADX WARN: Code duplicated, block: B:167:0x0354  */
    /* JADX WARN: Code duplicated, block: B:170:0x035e  */
    /* JADX WARN: Code duplicated, block: B:172:? A[RETURN, SYNTHETIC] */
    public void addToSolver(LinearSystem linearSystem) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        SolverVariable solverVariable;
        int i;
        int i2;
        boolean z5;
        boolean z6;
        boolean z7;
        boolean z8;
        boolean z9;
        boolean z10;
        SolverVariable solverVariable2;
        LinearSystem linearSystem2;
        boolean z11;
        SolverVariable solverVariableCreateObjectVariable;
        SolverVariable solverVariableCreateObjectVariable2;
        SolverVariable solverVariable3;
        ConstraintWidget constraintWidget;
        SolverVariable solverVariable4;
        SolverVariable solverVariableCreateObjectVariable3;
        SolverVariable solverVariableCreateObjectVariable4;
        boolean zIsInHorizontalChain;
        boolean zIsInVerticalChain;
        SolverVariable solverVariableCreateObjectVariable5 = linearSystem.createObjectVariable(this.mLeft);
        SolverVariable solverVariableCreateObjectVariable6 = linearSystem.createObjectVariable(this.mRight);
        SolverVariable solverVariableCreateObjectVariable7 = linearSystem.createObjectVariable(this.mTop);
        SolverVariable solverVariableCreateObjectVariable8 = linearSystem.createObjectVariable(this.mBottom);
        SolverVariable solverVariableCreateObjectVariable9 = linearSystem.createObjectVariable(this.mBaseline);
        if (this.mParent != null) {
            z = this.mParent != null && this.mParent.mListDimensionBehaviors[0] == DimensionBehaviour.WRAP_CONTENT;
            boolean z12 = this.mParent != null && this.mParent.mListDimensionBehaviors[1] == DimensionBehaviour.WRAP_CONTENT;
            if (isChainHead(0)) {
                ((ConstraintWidgetContainer) this.mParent).addChain(this, 0);
                zIsInHorizontalChain = true;
            } else {
                zIsInHorizontalChain = isInHorizontalChain();
            }
            if (isChainHead(1)) {
                ((ConstraintWidgetContainer) this.mParent).addChain(this, 1);
                zIsInVerticalChain = true;
            } else {
                zIsInVerticalChain = isInVerticalChain();
            }
            if (z && this.mVisibility != 8 && this.mLeft.mTarget == null && this.mRight.mTarget == null) {
                linearSystem.addGreaterThan(linearSystem.createObjectVariable(this.mParent.mRight), solverVariableCreateObjectVariable6, 0, 1);
            }
            if (z12 && this.mVisibility != 8 && this.mTop.mTarget == null && this.mBottom.mTarget == null && this.mBaseline == null) {
                linearSystem.addGreaterThan(linearSystem.createObjectVariable(this.mParent.mBottom), solverVariableCreateObjectVariable8, 0, 1);
            }
            z2 = z12;
            z3 = zIsInHorizontalChain;
            z4 = zIsInVerticalChain;
        } else {
            z = false;
            z2 = false;
            z3 = false;
            z4 = false;
        }
        int i3 = this.mWidth;
        if (i3 < this.mMinWidth) {
            i3 = this.mMinWidth;
        }
        int i4 = this.mHeight;
        if (i4 < this.mMinHeight) {
            i4 = this.mMinHeight;
        }
        boolean z13 = this.mListDimensionBehaviors[0] != DimensionBehaviour.MATCH_CONSTRAINT;
        boolean z14 = this.mListDimensionBehaviors[1] != DimensionBehaviour.MATCH_CONSTRAINT;
        this.mResolvedDimensionRatioSide = this.mDimensionRatioSide;
        this.mResolvedDimensionRatio = this.mDimensionRatio;
        int i5 = this.mMatchConstraintDefaultWidth;
        int i6 = this.mMatchConstraintDefaultHeight;
        if (this.mDimensionRatio > 0.0f && this.mVisibility != 8) {
            solverVariable = solverVariableCreateObjectVariable9;
            if (this.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && i5 == 0) {
                i5 = 3;
            }
            if (this.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && i6 == 0) {
                i6 = 3;
            }
            if (this.mListDimensionBehaviors[0] != DimensionBehaviour.MATCH_CONSTRAINT || this.mListDimensionBehaviors[1] != DimensionBehaviour.MATCH_CONSTRAINT || i5 != 3 || i6 != 3) {
                if (this.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT && i5 == 3) {
                    this.mResolvedDimensionRatioSide = 0;
                    int i7 = (int) (this.mResolvedDimensionRatio * this.mHeight);
                    if (this.mListDimensionBehaviors[1] != DimensionBehaviour.MATCH_CONSTRAINT) {
                        i3 = i7;
                        i6 = i6;
                        i = i4;
                        i2 = 4;
                    } else {
                        i3 = i7;
                        i6 = i6;
                        i = i4;
                        z5 = true;
                        i2 = i5;
                    }
                    this.mResolvedMatchConstraintDefault[0] = i2;
                    this.mResolvedMatchConstraintDefault[1] = i6;
                    if (z5) {
                    }
                    if (this.mListDimensionBehaviors[0] == DimensionBehaviour.WRAP_CONTENT) {
                        z7 = false;
                    } else {
                        z7 = false;
                    }
                    z8 = !this.mCenter.isConnected();
                    if (this.mHorizontalResolution == 2) {
                        if (this.mParent != null) {
                            solverVariableCreateObjectVariable3 = linearSystem.createObjectVariable(this.mParent.mRight);
                        } else {
                            solverVariableCreateObjectVariable3 = null;
                        }
                        if (this.mParent != null) {
                            solverVariableCreateObjectVariable4 = linearSystem.createObjectVariable(this.mParent.mLeft);
                        } else {
                            solverVariableCreateObjectVariable4 = null;
                        }
                        applyConstraints(linearSystem, z, solverVariableCreateObjectVariable4, solverVariableCreateObjectVariable3, this.mListDimensionBehaviors[0], z7, this.mLeft, this.mRight, this.mX, i3, this.mMinWidth, this.mMaxDimension[0], this.mHorizontalBiasPercent, z6, z3, i2, this.mMatchConstraintMinWidth, this.mMatchConstraintMaxWidth, this.mMatchConstraintPercentWidth, z8);
                    }
                    if (this.mVerticalResolution == 2) {
                        return;
                    }
                    if (this.mListDimensionBehaviors[1] == DimensionBehaviour.WRAP_CONTENT) {
                        z9 = false;
                    } else {
                        z9 = false;
                    }
                    if (z5) {
                        z10 = false;
                    } else {
                        z10 = false;
                    }
                    if (this.mBaselineDistance > 0) {
                        solverVariable2 = solverVariableCreateObjectVariable7;
                        linearSystem2 = linearSystem;
                    } else {
                        if (this.mBaseline.getResolutionNode().state == 1) {
                            linearSystem2 = linearSystem;
                            solverVariable4 = solverVariable;
                            solverVariable2 = solverVariableCreateObjectVariable7;
                            linearSystem2.addEquality(solverVariable4, solverVariable2, getBaselineDistance(), 6);
                            if (this.mBaseline.mTarget != null) {
                                linearSystem2.addEquality(solverVariable4, linearSystem2.createObjectVariable(this.mBaseline.mTarget), 0, 6);
                                z11 = false;
                            }
                            if (this.mParent != null) {
                                solverVariableCreateObjectVariable = linearSystem2.createObjectVariable(this.mParent.mBottom);
                            } else {
                                solverVariableCreateObjectVariable = null;
                            }
                            if (this.mParent != null) {
                                solverVariableCreateObjectVariable2 = linearSystem2.createObjectVariable(this.mParent.mTop);
                            } else {
                                solverVariableCreateObjectVariable2 = null;
                            }
                            solverVariable3 = solverVariable2;
                            applyConstraints(linearSystem, z2, solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable, this.mListDimensionBehaviors[1], z9, this.mTop, this.mBottom, this.mY, i, this.mMinHeight, this.mMaxDimension[1], this.mVerticalBiasPercent, z10, z4, i6, this.mMatchConstraintMinHeight, this.mMatchConstraintMaxHeight, this.mMatchConstraintPercentHeight, z11);
                            if (z5) {
                                constraintWidget = this;
                                if (constraintWidget.mResolvedDimensionRatioSide == 1) {
                                    linearSystem.addRatio(solverVariableCreateObjectVariable8, solverVariable3, solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, constraintWidget.mResolvedDimensionRatio, 6);
                                } else {
                                    linearSystem.addRatio(solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable8, solverVariable3, constraintWidget.mResolvedDimensionRatio, 6);
                                }
                            } else {
                                constraintWidget = this;
                            }
                            if (constraintWidget.mCenter.isConnected()) {
                                linearSystem.addCenterPoint(constraintWidget, constraintWidget.mCenter.getTarget().getOwner(), (float) Math.toRadians(constraintWidget.mCircleConstraintAngle + 90.0f), constraintWidget.mCenter.getMargin());
                            }
                        }
                        linearSystem2 = linearSystem;
                        this.mBaseline.getResolutionNode().addResolvedValue(linearSystem2);
                        solverVariable2 = solverVariableCreateObjectVariable7;
                    }
                    z11 = z8;
                    if (this.mParent != null) {
                        solverVariableCreateObjectVariable = linearSystem2.createObjectVariable(this.mParent.mBottom);
                    } else {
                        solverVariableCreateObjectVariable = null;
                    }
                    if (this.mParent != null) {
                        solverVariableCreateObjectVariable2 = linearSystem2.createObjectVariable(this.mParent.mTop);
                    } else {
                        solverVariableCreateObjectVariable2 = null;
                    }
                    solverVariable3 = solverVariable2;
                    applyConstraints(linearSystem, z2, solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable, this.mListDimensionBehaviors[1], z9, this.mTop, this.mBottom, this.mY, i, this.mMinHeight, this.mMaxDimension[1], this.mVerticalBiasPercent, z10, z4, i6, this.mMatchConstraintMinHeight, this.mMatchConstraintMaxHeight, this.mMatchConstraintPercentHeight, z11);
                    if (z5) {
                        constraintWidget = this;
                        if (constraintWidget.mResolvedDimensionRatioSide == 1) {
                            linearSystem.addRatio(solverVariableCreateObjectVariable8, solverVariable3, solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, constraintWidget.mResolvedDimensionRatio, 6);
                        } else {
                            linearSystem.addRatio(solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable8, solverVariable3, constraintWidget.mResolvedDimensionRatio, 6);
                        }
                    } else {
                        constraintWidget = this;
                    }
                    if (constraintWidget.mCenter.isConnected()) {
                        linearSystem.addCenterPoint(constraintWidget, constraintWidget.mCenter.getTarget().getOwner(), (float) Math.toRadians(constraintWidget.mCircleConstraintAngle + 90.0f), constraintWidget.mCenter.getMargin());
                    }
                }
                if (this.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT && i6 == 3) {
                    this.mResolvedDimensionRatioSide = 1;
                    if (this.mDimensionRatioSide == -1) {
                        this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                    }
                    int i8 = (int) (this.mResolvedDimensionRatio * this.mWidth);
                    if (this.mListDimensionBehaviors[0] != DimensionBehaviour.MATCH_CONSTRAINT) {
                        i = i8;
                        i3 = i3;
                        i2 = i5;
                        i6 = 4;
                    } else {
                        i = i8;
                    }
                }
                i2 = i5;
                z5 = true;
                this.mResolvedMatchConstraintDefault[0] = i2;
                this.mResolvedMatchConstraintDefault[1] = i6;
                z6 = !z5 && (this.mResolvedDimensionRatioSide == 0 || this.mResolvedDimensionRatioSide == -1);
                if (this.mListDimensionBehaviors[0] == DimensionBehaviour.WRAP_CONTENT || !(this instanceof ConstraintWidgetContainer)) {
                    z7 = false;
                } else {
                    z7 = true;
                }
                z8 = !this.mCenter.isConnected();
                if (this.mHorizontalResolution == 2) {
                    if (this.mParent != null) {
                        solverVariableCreateObjectVariable3 = linearSystem.createObjectVariable(this.mParent.mRight);
                    } else {
                        solverVariableCreateObjectVariable3 = null;
                    }
                    if (this.mParent != null) {
                        solverVariableCreateObjectVariable4 = linearSystem.createObjectVariable(this.mParent.mLeft);
                    } else {
                        solverVariableCreateObjectVariable4 = null;
                    }
                    applyConstraints(linearSystem, z, solverVariableCreateObjectVariable4, solverVariableCreateObjectVariable3, this.mListDimensionBehaviors[0], z7, this.mLeft, this.mRight, this.mX, i3, this.mMinWidth, this.mMaxDimension[0], this.mHorizontalBiasPercent, z6, z3, i2, this.mMatchConstraintMinWidth, this.mMatchConstraintMaxWidth, this.mMatchConstraintPercentWidth, z8);
                }
                if (this.mVerticalResolution == 2) {
                    return;
                }
                if (this.mListDimensionBehaviors[1] == DimensionBehaviour.WRAP_CONTENT || !(this instanceof ConstraintWidgetContainer)) {
                    z9 = false;
                } else {
                    z9 = true;
                }
                if (z5 || !(this.mResolvedDimensionRatioSide == 1 || this.mResolvedDimensionRatioSide == -1)) {
                    z10 = false;
                } else {
                    z10 = true;
                }
                if (this.mBaselineDistance > 0) {
                    solverVariable2 = solverVariableCreateObjectVariable7;
                    linearSystem2 = linearSystem;
                } else {
                    if (this.mBaseline.getResolutionNode().state == 1) {
                        linearSystem2 = linearSystem;
                        solverVariable4 = solverVariable;
                        solverVariable2 = solverVariableCreateObjectVariable7;
                        linearSystem2.addEquality(solverVariable4, solverVariable2, getBaselineDistance(), 6);
                        if (this.mBaseline.mTarget != null) {
                            linearSystem2.addEquality(solverVariable4, linearSystem2.createObjectVariable(this.mBaseline.mTarget), 0, 6);
                            z11 = false;
                        }
                        if (this.mParent != null) {
                            solverVariableCreateObjectVariable = linearSystem2.createObjectVariable(this.mParent.mBottom);
                        } else {
                            solverVariableCreateObjectVariable = null;
                        }
                        if (this.mParent != null) {
                            solverVariableCreateObjectVariable2 = linearSystem2.createObjectVariable(this.mParent.mTop);
                        } else {
                            solverVariableCreateObjectVariable2 = null;
                        }
                        solverVariable3 = solverVariable2;
                        applyConstraints(linearSystem, z2, solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable, this.mListDimensionBehaviors[1], z9, this.mTop, this.mBottom, this.mY, i, this.mMinHeight, this.mMaxDimension[1], this.mVerticalBiasPercent, z10, z4, i6, this.mMatchConstraintMinHeight, this.mMatchConstraintMaxHeight, this.mMatchConstraintPercentHeight, z11);
                        if (z5) {
                            constraintWidget = this;
                            if (constraintWidget.mResolvedDimensionRatioSide == 1) {
                                linearSystem.addRatio(solverVariableCreateObjectVariable8, solverVariable3, solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, constraintWidget.mResolvedDimensionRatio, 6);
                            } else {
                                linearSystem.addRatio(solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable8, solverVariable3, constraintWidget.mResolvedDimensionRatio, 6);
                            }
                        } else {
                            constraintWidget = this;
                        }
                        if (constraintWidget.mCenter.isConnected()) {
                            linearSystem.addCenterPoint(constraintWidget, constraintWidget.mCenter.getTarget().getOwner(), (float) Math.toRadians(constraintWidget.mCircleConstraintAngle + 90.0f), constraintWidget.mCenter.getMargin());
                        }
                    }
                    linearSystem2 = linearSystem;
                    this.mBaseline.getResolutionNode().addResolvedValue(linearSystem2);
                    solverVariable2 = solverVariableCreateObjectVariable7;
                }
                z11 = z8;
                if (this.mParent != null) {
                    solverVariableCreateObjectVariable = linearSystem2.createObjectVariable(this.mParent.mBottom);
                } else {
                    solverVariableCreateObjectVariable = null;
                }
                if (this.mParent != null) {
                    solverVariableCreateObjectVariable2 = linearSystem2.createObjectVariable(this.mParent.mTop);
                } else {
                    solverVariableCreateObjectVariable2 = null;
                }
                solverVariable3 = solverVariable2;
                applyConstraints(linearSystem, z2, solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable, this.mListDimensionBehaviors[1], z9, this.mTop, this.mBottom, this.mY, i, this.mMinHeight, this.mMaxDimension[1], this.mVerticalBiasPercent, z10, z4, i6, this.mMatchConstraintMinHeight, this.mMatchConstraintMaxHeight, this.mMatchConstraintPercentHeight, z11);
                if (z5) {
                    constraintWidget = this;
                    if (constraintWidget.mResolvedDimensionRatioSide == 1) {
                        linearSystem.addRatio(solverVariableCreateObjectVariable8, solverVariable3, solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, constraintWidget.mResolvedDimensionRatio, 6);
                    } else {
                        linearSystem.addRatio(solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable8, solverVariable3, constraintWidget.mResolvedDimensionRatio, 6);
                    }
                } else {
                    constraintWidget = this;
                }
                if (constraintWidget.mCenter.isConnected()) {
                    linearSystem.addCenterPoint(constraintWidget, constraintWidget.mCenter.getTarget().getOwner(), (float) Math.toRadians(constraintWidget.mCircleConstraintAngle + 90.0f), constraintWidget.mCenter.getMargin());
                }
            }
            setupDimensionRatio(z, z2, z13, z14);
            i = i4;
            i2 = i5;
            z5 = true;
            this.mResolvedMatchConstraintDefault[0] = i2;
            this.mResolvedMatchConstraintDefault[1] = i6;
            if (z5) {
            }
            if (this.mListDimensionBehaviors[0] == DimensionBehaviour.WRAP_CONTENT) {
                z7 = false;
            } else {
                z7 = false;
            }
            z8 = !this.mCenter.isConnected();
            if (this.mHorizontalResolution == 2) {
                if (this.mParent != null) {
                    solverVariableCreateObjectVariable3 = linearSystem.createObjectVariable(this.mParent.mRight);
                } else {
                    solverVariableCreateObjectVariable3 = null;
                }
                if (this.mParent != null) {
                    solverVariableCreateObjectVariable4 = linearSystem.createObjectVariable(this.mParent.mLeft);
                } else {
                    solverVariableCreateObjectVariable4 = null;
                }
                applyConstraints(linearSystem, z, solverVariableCreateObjectVariable4, solverVariableCreateObjectVariable3, this.mListDimensionBehaviors[0], z7, this.mLeft, this.mRight, this.mX, i3, this.mMinWidth, this.mMaxDimension[0], this.mHorizontalBiasPercent, z6, z3, i2, this.mMatchConstraintMinWidth, this.mMatchConstraintMaxWidth, this.mMatchConstraintPercentWidth, z8);
            }
            if (this.mVerticalResolution == 2) {
                return;
            }
            if (this.mListDimensionBehaviors[1] == DimensionBehaviour.WRAP_CONTENT) {
                z9 = false;
            } else {
                z9 = false;
            }
            if (z5) {
                z10 = false;
            } else {
                z10 = false;
            }
            if (this.mBaselineDistance > 0) {
                solverVariable2 = solverVariableCreateObjectVariable7;
                linearSystem2 = linearSystem;
            } else {
                if (this.mBaseline.getResolutionNode().state == 1) {
                    linearSystem2 = linearSystem;
                    solverVariable4 = solverVariable;
                    solverVariable2 = solverVariableCreateObjectVariable7;
                    linearSystem2.addEquality(solverVariable4, solverVariable2, getBaselineDistance(), 6);
                    if (this.mBaseline.mTarget != null) {
                        linearSystem2.addEquality(solverVariable4, linearSystem2.createObjectVariable(this.mBaseline.mTarget), 0, 6);
                        z11 = false;
                    }
                    if (this.mParent != null) {
                        solverVariableCreateObjectVariable = linearSystem2.createObjectVariable(this.mParent.mBottom);
                    } else {
                        solverVariableCreateObjectVariable = null;
                    }
                    if (this.mParent != null) {
                        solverVariableCreateObjectVariable2 = linearSystem2.createObjectVariable(this.mParent.mTop);
                    } else {
                        solverVariableCreateObjectVariable2 = null;
                    }
                    solverVariable3 = solverVariable2;
                    applyConstraints(linearSystem, z2, solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable, this.mListDimensionBehaviors[1], z9, this.mTop, this.mBottom, this.mY, i, this.mMinHeight, this.mMaxDimension[1], this.mVerticalBiasPercent, z10, z4, i6, this.mMatchConstraintMinHeight, this.mMatchConstraintMaxHeight, this.mMatchConstraintPercentHeight, z11);
                    if (z5) {
                        constraintWidget = this;
                        if (constraintWidget.mResolvedDimensionRatioSide == 1) {
                            linearSystem.addRatio(solverVariableCreateObjectVariable8, solverVariable3, solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, constraintWidget.mResolvedDimensionRatio, 6);
                        } else {
                            linearSystem.addRatio(solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable8, solverVariable3, constraintWidget.mResolvedDimensionRatio, 6);
                        }
                    } else {
                        constraintWidget = this;
                    }
                    if (constraintWidget.mCenter.isConnected()) {
                        linearSystem.addCenterPoint(constraintWidget, constraintWidget.mCenter.getTarget().getOwner(), (float) Math.toRadians(constraintWidget.mCircleConstraintAngle + 90.0f), constraintWidget.mCenter.getMargin());
                    }
                }
                linearSystem2 = linearSystem;
                this.mBaseline.getResolutionNode().addResolvedValue(linearSystem2);
                solverVariable2 = solverVariableCreateObjectVariable7;
            }
            z11 = z8;
            if (this.mParent != null) {
                solverVariableCreateObjectVariable = linearSystem2.createObjectVariable(this.mParent.mBottom);
            } else {
                solverVariableCreateObjectVariable = null;
            }
            if (this.mParent != null) {
                solverVariableCreateObjectVariable2 = linearSystem2.createObjectVariable(this.mParent.mTop);
            } else {
                solverVariableCreateObjectVariable2 = null;
            }
            solverVariable3 = solverVariable2;
            applyConstraints(linearSystem, z2, solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable, this.mListDimensionBehaviors[1], z9, this.mTop, this.mBottom, this.mY, i, this.mMinHeight, this.mMaxDimension[1], this.mVerticalBiasPercent, z10, z4, i6, this.mMatchConstraintMinHeight, this.mMatchConstraintMaxHeight, this.mMatchConstraintPercentHeight, z11);
            if (z5) {
                constraintWidget = this;
                if (constraintWidget.mResolvedDimensionRatioSide == 1) {
                    linearSystem.addRatio(solverVariableCreateObjectVariable8, solverVariable3, solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, constraintWidget.mResolvedDimensionRatio, 6);
                } else {
                    linearSystem.addRatio(solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable8, solverVariable3, constraintWidget.mResolvedDimensionRatio, 6);
                }
            } else {
                constraintWidget = this;
            }
            if (constraintWidget.mCenter.isConnected()) {
                linearSystem.addCenterPoint(constraintWidget, constraintWidget.mCenter.getTarget().getOwner(), (float) Math.toRadians(constraintWidget.mCircleConstraintAngle + 90.0f), constraintWidget.mCenter.getMargin());
            }
        }
        solverVariable = solverVariableCreateObjectVariable9;
        i6 = i6;
        i3 = i3;
        i = i4;
        i2 = i5;
        z5 = false;
        this.mResolvedMatchConstraintDefault[0] = i2;
        this.mResolvedMatchConstraintDefault[1] = i6;
        if (z5) {
        }
        if (this.mListDimensionBehaviors[0] == DimensionBehaviour.WRAP_CONTENT) {
            z7 = false;
        } else {
            z7 = false;
        }
        z8 = !this.mCenter.isConnected();
        if (this.mHorizontalResolution == 2) {
            if (this.mParent != null) {
                solverVariableCreateObjectVariable3 = linearSystem.createObjectVariable(this.mParent.mRight);
            } else {
                solverVariableCreateObjectVariable3 = null;
            }
            if (this.mParent != null) {
                solverVariableCreateObjectVariable4 = linearSystem.createObjectVariable(this.mParent.mLeft);
            } else {
                solverVariableCreateObjectVariable4 = null;
            }
            applyConstraints(linearSystem, z, solverVariableCreateObjectVariable4, solverVariableCreateObjectVariable3, this.mListDimensionBehaviors[0], z7, this.mLeft, this.mRight, this.mX, i3, this.mMinWidth, this.mMaxDimension[0], this.mHorizontalBiasPercent, z6, z3, i2, this.mMatchConstraintMinWidth, this.mMatchConstraintMaxWidth, this.mMatchConstraintPercentWidth, z8);
        }
        if (this.mVerticalResolution == 2) {
            return;
        }
        if (this.mListDimensionBehaviors[1] == DimensionBehaviour.WRAP_CONTENT) {
            z9 = false;
        } else {
            z9 = false;
        }
        if (z5) {
            z10 = false;
        } else {
            z10 = false;
        }
        if (this.mBaselineDistance > 0) {
            solverVariable2 = solverVariableCreateObjectVariable7;
            linearSystem2 = linearSystem;
        } else {
            if (this.mBaseline.getResolutionNode().state == 1) {
                linearSystem2 = linearSystem;
                solverVariable4 = solverVariable;
                solverVariable2 = solverVariableCreateObjectVariable7;
                linearSystem2.addEquality(solverVariable4, solverVariable2, getBaselineDistance(), 6);
                if (this.mBaseline.mTarget != null) {
                    linearSystem2.addEquality(solverVariable4, linearSystem2.createObjectVariable(this.mBaseline.mTarget), 0, 6);
                    z11 = false;
                }
                if (this.mParent != null) {
                    solverVariableCreateObjectVariable = linearSystem2.createObjectVariable(this.mParent.mBottom);
                } else {
                    solverVariableCreateObjectVariable = null;
                }
                if (this.mParent != null) {
                    solverVariableCreateObjectVariable2 = linearSystem2.createObjectVariable(this.mParent.mTop);
                } else {
                    solverVariableCreateObjectVariable2 = null;
                }
                solverVariable3 = solverVariable2;
                applyConstraints(linearSystem, z2, solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable, this.mListDimensionBehaviors[1], z9, this.mTop, this.mBottom, this.mY, i, this.mMinHeight, this.mMaxDimension[1], this.mVerticalBiasPercent, z10, z4, i6, this.mMatchConstraintMinHeight, this.mMatchConstraintMaxHeight, this.mMatchConstraintPercentHeight, z11);
                if (z5) {
                    constraintWidget = this;
                    if (constraintWidget.mResolvedDimensionRatioSide == 1) {
                        linearSystem.addRatio(solverVariableCreateObjectVariable8, solverVariable3, solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, constraintWidget.mResolvedDimensionRatio, 6);
                    } else {
                        linearSystem.addRatio(solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable8, solverVariable3, constraintWidget.mResolvedDimensionRatio, 6);
                    }
                } else {
                    constraintWidget = this;
                }
                if (constraintWidget.mCenter.isConnected()) {
                    linearSystem.addCenterPoint(constraintWidget, constraintWidget.mCenter.getTarget().getOwner(), (float) Math.toRadians(constraintWidget.mCircleConstraintAngle + 90.0f), constraintWidget.mCenter.getMargin());
                }
            }
            linearSystem2 = linearSystem;
            this.mBaseline.getResolutionNode().addResolvedValue(linearSystem2);
            solverVariable2 = solverVariableCreateObjectVariable7;
        }
        z11 = z8;
        if (this.mParent != null) {
            solverVariableCreateObjectVariable = linearSystem2.createObjectVariable(this.mParent.mBottom);
        } else {
            solverVariableCreateObjectVariable = null;
        }
        if (this.mParent != null) {
            solverVariableCreateObjectVariable2 = linearSystem2.createObjectVariable(this.mParent.mTop);
        } else {
            solverVariableCreateObjectVariable2 = null;
        }
        solverVariable3 = solverVariable2;
        applyConstraints(linearSystem, z2, solverVariableCreateObjectVariable2, solverVariableCreateObjectVariable, this.mListDimensionBehaviors[1], z9, this.mTop, this.mBottom, this.mY, i, this.mMinHeight, this.mMaxDimension[1], this.mVerticalBiasPercent, z10, z4, i6, this.mMatchConstraintMinHeight, this.mMatchConstraintMaxHeight, this.mMatchConstraintPercentHeight, z11);
        if (z5) {
            constraintWidget = this;
            if (constraintWidget.mResolvedDimensionRatioSide == 1) {
                linearSystem.addRatio(solverVariableCreateObjectVariable8, solverVariable3, solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, constraintWidget.mResolvedDimensionRatio, 6);
            } else {
                linearSystem.addRatio(solverVariableCreateObjectVariable6, solverVariableCreateObjectVariable5, solverVariableCreateObjectVariable8, solverVariable3, constraintWidget.mResolvedDimensionRatio, 6);
            }
        } else {
            constraintWidget = this;
        }
        if (constraintWidget.mCenter.isConnected()) {
            linearSystem.addCenterPoint(constraintWidget, constraintWidget.mCenter.getTarget().getOwner(), (float) Math.toRadians(constraintWidget.mCircleConstraintAngle + 90.0f), constraintWidget.mCenter.getMargin());
        }
    }

    public boolean allowedInBarrier() {
        return this.mVisibility != 8;
    }

    public void analyze(int i) {
        Optimizer.analyze(i, this);
    }

    public void connect(ConstraintAnchor.Type type, ConstraintWidget constraintWidget, ConstraintAnchor.Type type2) {
        connect(type, constraintWidget, type2, 0, ConstraintAnchor.Strength.STRONG);
    }

    public void connect(ConstraintAnchor.Type type, ConstraintWidget constraintWidget, ConstraintAnchor.Type type2, int i) {
        connect(type, constraintWidget, type2, i, ConstraintAnchor.Strength.STRONG);
    }

    public void connect(ConstraintAnchor.Type type, ConstraintWidget constraintWidget, ConstraintAnchor.Type type2, int i, ConstraintAnchor.Strength strength) {
        connect(type, constraintWidget, type2, i, strength, 0);
    }

    public void connect(ConstraintAnchor.Type type, ConstraintWidget constraintWidget, ConstraintAnchor.Type type2, int i, ConstraintAnchor.Strength strength, int i2) {
        boolean z;
        int i3 = 0;
        if (type == ConstraintAnchor.Type.CENTER) {
            if (type2 != ConstraintAnchor.Type.CENTER) {
                if (type2 == ConstraintAnchor.Type.LEFT || type2 == ConstraintAnchor.Type.RIGHT) {
                    connect(ConstraintAnchor.Type.LEFT, constraintWidget, type2, 0, strength, i2);
                    connect(ConstraintAnchor.Type.RIGHT, constraintWidget, type2, 0, strength, i2);
                    getAnchor(ConstraintAnchor.Type.CENTER).connect(constraintWidget.getAnchor(type2), 0, i2);
                    return;
                } else {
                    if (type2 == ConstraintAnchor.Type.TOP || type2 == ConstraintAnchor.Type.BOTTOM) {
                        connect(ConstraintAnchor.Type.TOP, constraintWidget, type2, 0, strength, i2);
                        connect(ConstraintAnchor.Type.BOTTOM, constraintWidget, type2, 0, strength, i2);
                        getAnchor(ConstraintAnchor.Type.CENTER).connect(constraintWidget.getAnchor(type2), 0, i2);
                        return;
                    }
                    return;
                }
            }
            ConstraintAnchor anchor = getAnchor(ConstraintAnchor.Type.LEFT);
            ConstraintAnchor anchor2 = getAnchor(ConstraintAnchor.Type.RIGHT);
            ConstraintAnchor anchor3 = getAnchor(ConstraintAnchor.Type.TOP);
            ConstraintAnchor anchor4 = getAnchor(ConstraintAnchor.Type.BOTTOM);
            boolean z2 = true;
            if ((anchor == null || !anchor.isConnected()) && (anchor2 == null || !anchor2.isConnected())) {
                connect(ConstraintAnchor.Type.LEFT, constraintWidget, ConstraintAnchor.Type.LEFT, 0, strength, i2);
                connect(ConstraintAnchor.Type.RIGHT, constraintWidget, ConstraintAnchor.Type.RIGHT, 0, strength, i2);
                z = true;
            } else {
                z = false;
            }
            if ((anchor3 == null || !anchor3.isConnected()) && (anchor4 == null || !anchor4.isConnected())) {
                connect(ConstraintAnchor.Type.TOP, constraintWidget, ConstraintAnchor.Type.TOP, 0, strength, i2);
                connect(ConstraintAnchor.Type.BOTTOM, constraintWidget, ConstraintAnchor.Type.BOTTOM, 0, strength, i2);
            } else {
                z2 = false;
            }
            if (z && z2) {
                getAnchor(ConstraintAnchor.Type.CENTER).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.CENTER), 0, i2);
                return;
            } else if (z) {
                getAnchor(ConstraintAnchor.Type.CENTER_X).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.CENTER_X), 0, i2);
                return;
            } else {
                if (z2) {
                    getAnchor(ConstraintAnchor.Type.CENTER_Y).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.CENTER_Y), 0, i2);
                    return;
                }
                return;
            }
        }
        if (type == ConstraintAnchor.Type.CENTER_X && (type2 == ConstraintAnchor.Type.LEFT || type2 == ConstraintAnchor.Type.RIGHT)) {
            ConstraintAnchor anchor5 = getAnchor(ConstraintAnchor.Type.LEFT);
            ConstraintAnchor anchor6 = constraintWidget.getAnchor(type2);
            ConstraintAnchor anchor7 = getAnchor(ConstraintAnchor.Type.RIGHT);
            anchor5.connect(anchor6, 0, i2);
            anchor7.connect(anchor6, 0, i2);
            getAnchor(ConstraintAnchor.Type.CENTER_X).connect(anchor6, 0, i2);
            return;
        }
        if (type == ConstraintAnchor.Type.CENTER_Y && (type2 == ConstraintAnchor.Type.TOP || type2 == ConstraintAnchor.Type.BOTTOM)) {
            ConstraintAnchor anchor8 = constraintWidget.getAnchor(type2);
            getAnchor(ConstraintAnchor.Type.TOP).connect(anchor8, 0, i2);
            getAnchor(ConstraintAnchor.Type.BOTTOM).connect(anchor8, 0, i2);
            getAnchor(ConstraintAnchor.Type.CENTER_Y).connect(anchor8, 0, i2);
            return;
        }
        if (type == ConstraintAnchor.Type.CENTER_X && type2 == ConstraintAnchor.Type.CENTER_X) {
            getAnchor(ConstraintAnchor.Type.LEFT).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT), 0, i2);
            getAnchor(ConstraintAnchor.Type.RIGHT).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.RIGHT), 0, i2);
            getAnchor(ConstraintAnchor.Type.CENTER_X).connect(constraintWidget.getAnchor(type2), 0, i2);
            return;
        }
        if (type == ConstraintAnchor.Type.CENTER_Y && type2 == ConstraintAnchor.Type.CENTER_Y) {
            getAnchor(ConstraintAnchor.Type.TOP).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.TOP), 0, i2);
            getAnchor(ConstraintAnchor.Type.BOTTOM).connect(constraintWidget.getAnchor(ConstraintAnchor.Type.BOTTOM), 0, i2);
            getAnchor(ConstraintAnchor.Type.CENTER_Y).connect(constraintWidget.getAnchor(type2), 0, i2);
            return;
        }
        ConstraintAnchor anchor9 = getAnchor(type);
        ConstraintAnchor anchor10 = constraintWidget.getAnchor(type2);
        if (anchor9.isValidConnection(anchor10)) {
            if (type == ConstraintAnchor.Type.BASELINE) {
                ConstraintAnchor anchor11 = getAnchor(ConstraintAnchor.Type.TOP);
                ConstraintAnchor anchor12 = getAnchor(ConstraintAnchor.Type.BOTTOM);
                if (anchor11 != null) {
                    anchor11.reset();
                }
                if (anchor12 != null) {
                    anchor12.reset();
                }
            } else {
                if (type == ConstraintAnchor.Type.TOP || type == ConstraintAnchor.Type.BOTTOM) {
                    ConstraintAnchor anchor13 = getAnchor(ConstraintAnchor.Type.BASELINE);
                    if (anchor13 != null) {
                        anchor13.reset();
                    }
                    ConstraintAnchor anchor14 = getAnchor(ConstraintAnchor.Type.CENTER);
                    if (anchor14.getTarget() != anchor10) {
                        anchor14.reset();
                    }
                    ConstraintAnchor opposite = getAnchor(type).getOpposite();
                    ConstraintAnchor anchor15 = getAnchor(ConstraintAnchor.Type.CENTER_Y);
                    if (anchor15.isConnected()) {
                        opposite.reset();
                        anchor15.reset();
                    }
                } else if (type == ConstraintAnchor.Type.LEFT || type == ConstraintAnchor.Type.RIGHT) {
                    ConstraintAnchor anchor16 = getAnchor(ConstraintAnchor.Type.CENTER);
                    if (anchor16.getTarget() != anchor10) {
                        anchor16.reset();
                    }
                    ConstraintAnchor opposite2 = getAnchor(type).getOpposite();
                    ConstraintAnchor anchor17 = getAnchor(ConstraintAnchor.Type.CENTER_X);
                    if (anchor17.isConnected()) {
                        opposite2.reset();
                        anchor17.reset();
                    }
                }
                i3 = i;
            }
            anchor9.connect(anchor10, i3, strength, i2);
            anchor10.getOwner().connectedTo(anchor9.getOwner());
        }
    }

    public void connect(ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i) {
        connect(constraintAnchor, constraintAnchor2, i, ConstraintAnchor.Strength.STRONG, 0);
    }

    public void connect(ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i, int i2) {
        connect(constraintAnchor, constraintAnchor2, i, ConstraintAnchor.Strength.STRONG, i2);
    }

    public void connect(ConstraintAnchor constraintAnchor, ConstraintAnchor constraintAnchor2, int i, ConstraintAnchor.Strength strength, int i2) {
        if (constraintAnchor.getOwner() == this) {
            connect(constraintAnchor.getType(), constraintAnchor2.getOwner(), constraintAnchor2.getType(), i, strength, i2);
        }
    }

    public void connectCircularConstraint(ConstraintWidget constraintWidget, float f, int i) {
        immediateConnect(ConstraintAnchor.Type.CENTER, constraintWidget, ConstraintAnchor.Type.CENTER, i, 0);
        this.mCircleConstraintAngle = f;
    }

    public void connectedTo(ConstraintWidget constraintWidget) {
    }

    public void createObjectVariables(LinearSystem linearSystem) {
        linearSystem.createObjectVariable(this.mLeft);
        linearSystem.createObjectVariable(this.mTop);
        linearSystem.createObjectVariable(this.mRight);
        linearSystem.createObjectVariable(this.mBottom);
        if (this.mBaselineDistance > 0) {
            linearSystem.createObjectVariable(this.mBaseline);
        }
    }

    public void disconnectUnlockedWidget(ConstraintWidget constraintWidget) {
        ArrayList<ConstraintAnchor> anchors = getAnchors();
        int size = anchors.size();
        for (int i = 0; i < size; i++) {
            ConstraintAnchor constraintAnchor = anchors.get(i);
            if (constraintAnchor.isConnected() && constraintAnchor.getTarget().getOwner() == constraintWidget && constraintAnchor.getConnectionCreator() == 2) {
                constraintAnchor.reset();
            }
        }
    }

    public void disconnectWidget(ConstraintWidget constraintWidget) {
        ArrayList<ConstraintAnchor> anchors = getAnchors();
        int size = anchors.size();
        for (int i = 0; i < size; i++) {
            ConstraintAnchor constraintAnchor = anchors.get(i);
            if (constraintAnchor.isConnected() && constraintAnchor.getTarget().getOwner() == constraintWidget) {
                constraintAnchor.reset();
            }
        }
    }

    public void forceUpdateDrawPosition() {
        int i = this.mX;
        int i2 = this.mY;
        int i3 = this.mX + this.mWidth;
        int i4 = this.mY + this.mHeight;
        this.mDrawX = i;
        this.mDrawY = i2;
        this.mDrawWidth = i3 - i;
        this.mDrawHeight = i4 - i2;
    }

    public ConstraintAnchor getAnchor(ConstraintAnchor.Type type) {
        switch (type) {
            case LEFT:
                return this.mLeft;
            case TOP:
                return this.mTop;
            case RIGHT:
                return this.mRight;
            case BOTTOM:
                return this.mBottom;
            case BASELINE:
                return this.mBaseline;
            case CENTER:
                return this.mCenter;
            case CENTER_X:
                return this.mCenterX;
            case CENTER_Y:
                return this.mCenterY;
            case NONE:
                return null;
            default:
                throw new AssertionError(type.name());
        }
    }

    public ArrayList<ConstraintAnchor> getAnchors() {
        return this.mAnchors;
    }

    public int getBaselineDistance() {
        return this.mBaselineDistance;
    }

    public float getBiasPercent(int i) {
        if (i == 0) {
            return this.mHorizontalBiasPercent;
        }
        if (i == 1) {
            return this.mVerticalBiasPercent;
        }
        return -1.0f;
    }

    public int getBottom() {
        return getY() + this.mHeight;
    }

    public Object getCompanionWidget() {
        return this.mCompanionWidget;
    }

    public int getContainerItemSkip() {
        return this.mContainerItemSkip;
    }

    public String getDebugName() {
        return this.mDebugName;
    }

    public DimensionBehaviour getDimensionBehaviour(int i) {
        if (i == 0) {
            return getHorizontalDimensionBehaviour();
        }
        if (i == 1) {
            return getVerticalDimensionBehaviour();
        }
        return null;
    }

    public float getDimensionRatio() {
        return this.mDimensionRatio;
    }

    public int getDimensionRatioSide() {
        return this.mDimensionRatioSide;
    }

    public int getDrawBottom() {
        return getDrawY() + this.mDrawHeight;
    }

    public int getDrawHeight() {
        return this.mDrawHeight;
    }

    public int getDrawRight() {
        return getDrawX() + this.mDrawWidth;
    }

    public int getDrawWidth() {
        return this.mDrawWidth;
    }

    public int getDrawX() {
        return this.mDrawX + this.mOffsetX;
    }

    public int getDrawY() {
        return this.mDrawY + this.mOffsetY;
    }

    public int getHeight() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mHeight;
    }

    public float getHorizontalBiasPercent() {
        return this.mHorizontalBiasPercent;
    }

    public ConstraintWidget getHorizontalChainControlWidget() {
        if (!isInHorizontalChain()) {
            return null;
        }
        ConstraintWidget constraintWidget = this;
        ConstraintWidget constraintWidget2 = null;
        while (constraintWidget2 == null && constraintWidget != null) {
            ConstraintAnchor anchor = constraintWidget.getAnchor(ConstraintAnchor.Type.LEFT);
            ConstraintAnchor target = anchor == null ? null : anchor.getTarget();
            ConstraintWidget owner = target == null ? null : target.getOwner();
            if (owner == getParent()) {
                return constraintWidget;
            }
            ConstraintAnchor target2 = owner == null ? null : owner.getAnchor(ConstraintAnchor.Type.RIGHT).getTarget();
            if (target2 == null || target2.getOwner() == constraintWidget) {
                constraintWidget = owner;
            } else {
                constraintWidget2 = constraintWidget;
            }
        }
        return constraintWidget2;
    }

    public int getHorizontalChainStyle() {
        return this.mHorizontalChainStyle;
    }

    public DimensionBehaviour getHorizontalDimensionBehaviour() {
        return this.mListDimensionBehaviors[0];
    }

    public int getInternalDrawBottom() {
        return this.mDrawY + this.mDrawHeight;
    }

    public int getInternalDrawRight() {
        return this.mDrawX + this.mDrawWidth;
    }

    int getInternalDrawX() {
        return this.mDrawX;
    }

    int getInternalDrawY() {
        return this.mDrawY;
    }

    public int getLeft() {
        return getX();
    }

    public int getLength(int i) {
        if (i == 0) {
            return getWidth();
        }
        if (i == 1) {
            return getHeight();
        }
        return 0;
    }

    public int getMaxHeight() {
        return this.mMaxDimension[1];
    }

    public int getMaxWidth() {
        return this.mMaxDimension[0];
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getOptimizerWrapHeight() {
        int iMax;
        int i = this.mHeight;
        if (this.mListDimensionBehaviors[1] != DimensionBehaviour.MATCH_CONSTRAINT) {
            return i;
        }
        if (this.mMatchConstraintDefaultHeight == 1) {
            iMax = Math.max(this.mMatchConstraintMinHeight, i);
        } else if (this.mMatchConstraintMinHeight > 0) {
            iMax = this.mMatchConstraintMinHeight;
            this.mHeight = iMax;
        } else {
            iMax = 0;
        }
        return (this.mMatchConstraintMaxHeight <= 0 || this.mMatchConstraintMaxHeight >= iMax) ? iMax : this.mMatchConstraintMaxHeight;
    }

    public int getOptimizerWrapWidth() {
        int iMax;
        int i = this.mWidth;
        if (this.mListDimensionBehaviors[0] != DimensionBehaviour.MATCH_CONSTRAINT) {
            return i;
        }
        if (this.mMatchConstraintDefaultWidth == 1) {
            iMax = Math.max(this.mMatchConstraintMinWidth, i);
        } else if (this.mMatchConstraintMinWidth > 0) {
            iMax = this.mMatchConstraintMinWidth;
            this.mWidth = iMax;
        } else {
            iMax = 0;
        }
        return (this.mMatchConstraintMaxWidth <= 0 || this.mMatchConstraintMaxWidth >= iMax) ? iMax : this.mMatchConstraintMaxWidth;
    }

    public ConstraintWidget getParent() {
        return this.mParent;
    }

    int getRelativePositioning(int i) {
        if (i == 0) {
            return this.mRelX;
        }
        if (i == 1) {
            return this.mRelY;
        }
        return 0;
    }

    public ResolutionDimension getResolutionHeight() {
        if (this.mResolutionHeight == null) {
            this.mResolutionHeight = new ResolutionDimension();
        }
        return this.mResolutionHeight;
    }

    public ResolutionDimension getResolutionWidth() {
        if (this.mResolutionWidth == null) {
            this.mResolutionWidth = new ResolutionDimension();
        }
        return this.mResolutionWidth;
    }

    public int getRight() {
        return getX() + this.mWidth;
    }

    public WidgetContainer getRootWidgetContainer() {
        while (this.getParent() != null) {
            this = this.getParent();
        }
        if (this instanceof WidgetContainer) {
            return (WidgetContainer) this;
        }
        return null;
    }

    protected int getRootX() {
        return this.mX + this.mOffsetX;
    }

    protected int getRootY() {
        return this.mY + this.mOffsetY;
    }

    public int getTop() {
        return getY();
    }

    public String getType() {
        return this.mType;
    }

    public float getVerticalBiasPercent() {
        return this.mVerticalBiasPercent;
    }

    public ConstraintWidget getVerticalChainControlWidget() {
        if (!isInVerticalChain()) {
            return null;
        }
        ConstraintWidget constraintWidget = this;
        ConstraintWidget constraintWidget2 = null;
        while (constraintWidget2 == null && constraintWidget != null) {
            ConstraintAnchor anchor = constraintWidget.getAnchor(ConstraintAnchor.Type.TOP);
            ConstraintAnchor target = anchor == null ? null : anchor.getTarget();
            ConstraintWidget owner = target == null ? null : target.getOwner();
            if (owner == getParent()) {
                return constraintWidget;
            }
            ConstraintAnchor target2 = owner == null ? null : owner.getAnchor(ConstraintAnchor.Type.BOTTOM).getTarget();
            if (target2 == null || target2.getOwner() == constraintWidget) {
                constraintWidget = owner;
            } else {
                constraintWidget2 = constraintWidget;
            }
        }
        return constraintWidget2;
    }

    public int getVerticalChainStyle() {
        return this.mVerticalChainStyle;
    }

    public DimensionBehaviour getVerticalDimensionBehaviour() {
        return this.mListDimensionBehaviors[1];
    }

    public int getVisibility() {
        return this.mVisibility;
    }

    public int getWidth() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mWidth;
    }

    public int getWrapHeight() {
        return this.mWrapHeight;
    }

    public int getWrapWidth() {
        return this.mWrapWidth;
    }

    public int getX() {
        return this.mX;
    }

    public int getY() {
        return this.mY;
    }

    public boolean hasAncestor(ConstraintWidget constraintWidget) {
        ConstraintWidget parent = getParent();
        if (parent == constraintWidget) {
            return true;
        }
        if (parent == constraintWidget.getParent()) {
            return false;
        }
        while (parent != null) {
            if (parent == constraintWidget || parent == constraintWidget.getParent()) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    public boolean hasBaseline() {
        return this.mBaselineDistance > 0;
    }

    public void immediateConnect(ConstraintAnchor.Type type, ConstraintWidget constraintWidget, ConstraintAnchor.Type type2, int i, int i2) {
        getAnchor(type).connect(constraintWidget.getAnchor(type2), i, i2, ConstraintAnchor.Strength.STRONG, 0, true);
    }

    public boolean isFullyResolved() {
        return this.mLeft.getResolutionNode().state == 1 && this.mRight.getResolutionNode().state == 1 && this.mTop.getResolutionNode().state == 1 && this.mBottom.getResolutionNode().state == 1;
    }

    public boolean isHeightWrapContent() {
        return this.mIsHeightWrapContent;
    }

    public boolean isInHorizontalChain() {
        if (this.mLeft.mTarget == null || this.mLeft.mTarget.mTarget != this.mLeft) {
            return this.mRight.mTarget != null && this.mRight.mTarget.mTarget == this.mRight;
        }
        return true;
    }

    public boolean isInVerticalChain() {
        if (this.mTop.mTarget == null || this.mTop.mTarget.mTarget != this.mTop) {
            return this.mBottom.mTarget != null && this.mBottom.mTarget.mTarget == this.mBottom;
        }
        return true;
    }

    public boolean isInsideConstraintLayout() {
        ConstraintWidget parent = getParent();
        if (parent == null) {
            return false;
        }
        while (parent != null) {
            if (parent instanceof ConstraintWidgetContainer) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    public boolean isRoot() {
        return this.mParent == null;
    }

    public boolean isRootContainer() {
        return (this instanceof ConstraintWidgetContainer) && (this.mParent == null || !(this.mParent instanceof ConstraintWidgetContainer));
    }

    public boolean isSpreadHeight() {
        return this.mMatchConstraintDefaultHeight == 0 && this.mDimensionRatio == 0.0f && this.mMatchConstraintMinHeight == 0 && this.mMatchConstraintMaxHeight == 0 && this.mListDimensionBehaviors[1] == DimensionBehaviour.MATCH_CONSTRAINT;
    }

    public boolean isSpreadWidth() {
        return this.mMatchConstraintDefaultWidth == 0 && this.mDimensionRatio == 0.0f && this.mMatchConstraintMinWidth == 0 && this.mMatchConstraintMaxWidth == 0 && this.mListDimensionBehaviors[0] == DimensionBehaviour.MATCH_CONSTRAINT;
    }

    public boolean isWidthWrapContent() {
        return this.mIsWidthWrapContent;
    }

    public void reset() {
        this.mLeft.reset();
        this.mTop.reset();
        this.mRight.reset();
        this.mBottom.reset();
        this.mBaseline.reset();
        this.mCenterX.reset();
        this.mCenterY.reset();
        this.mCenter.reset();
        this.mParent = null;
        this.mCircleConstraintAngle = 0.0f;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mX = 0;
        this.mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mWrapWidth = 0;
        this.mWrapHeight = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mListDimensionBehaviors[0] = DimensionBehaviour.FIXED;
        this.mListDimensionBehaviors[1] = DimensionBehaviour.FIXED;
        this.mCompanionWidget = null;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mType = null;
        this.mHorizontalWrapVisited = false;
        this.mVerticalWrapVisited = false;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mHorizontalChainFixedPosition = false;
        this.mVerticalChainFixedPosition = false;
        this.mWeight[0] = -1.0f;
        this.mWeight[1] = -1.0f;
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
        this.mMaxDimension[0] = Integer.MAX_VALUE;
        this.mMaxDimension[1] = Integer.MAX_VALUE;
        this.mMatchConstraintDefaultWidth = 0;
        this.mMatchConstraintDefaultHeight = 0;
        this.mMatchConstraintPercentWidth = 1.0f;
        this.mMatchConstraintPercentHeight = 1.0f;
        this.mMatchConstraintMaxWidth = Integer.MAX_VALUE;
        this.mMatchConstraintMaxHeight = Integer.MAX_VALUE;
        this.mMatchConstraintMinWidth = 0;
        this.mMatchConstraintMinHeight = 0;
        this.mResolvedDimensionRatioSide = -1;
        this.mResolvedDimensionRatio = 1.0f;
        if (this.mResolutionWidth != null) {
            this.mResolutionWidth.reset();
        }
        if (this.mResolutionHeight != null) {
            this.mResolutionHeight.reset();
        }
        this.mBelongingGroup = null;
        this.mOptimizerMeasurable = false;
        this.mOptimizerMeasured = false;
        this.mGroupsToSolver = false;
    }

    public void resetAllConstraints() {
        resetAnchors();
        setVerticalBiasPercent(DEFAULT_BIAS);
        setHorizontalBiasPercent(DEFAULT_BIAS);
        if (this instanceof ConstraintWidgetContainer) {
            return;
        }
        if (getHorizontalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT) {
            if (getWidth() == getWrapWidth()) {
                setHorizontalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
            } else if (getWidth() > getMinWidth()) {
                setHorizontalDimensionBehaviour(DimensionBehaviour.FIXED);
            }
        }
        if (getVerticalDimensionBehaviour() == DimensionBehaviour.MATCH_CONSTRAINT) {
            if (getHeight() == getWrapHeight()) {
                setVerticalDimensionBehaviour(DimensionBehaviour.WRAP_CONTENT);
            } else if (getHeight() > getMinHeight()) {
                setVerticalDimensionBehaviour(DimensionBehaviour.FIXED);
            }
        }
    }

    public void resetAnchor(ConstraintAnchor constraintAnchor) {
        if (getParent() != null && (getParent() instanceof ConstraintWidgetContainer) && ((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            return;
        }
        ConstraintAnchor anchor = getAnchor(ConstraintAnchor.Type.LEFT);
        ConstraintAnchor anchor2 = getAnchor(ConstraintAnchor.Type.RIGHT);
        ConstraintAnchor anchor3 = getAnchor(ConstraintAnchor.Type.TOP);
        ConstraintAnchor anchor4 = getAnchor(ConstraintAnchor.Type.BOTTOM);
        ConstraintAnchor anchor5 = getAnchor(ConstraintAnchor.Type.CENTER);
        ConstraintAnchor anchor6 = getAnchor(ConstraintAnchor.Type.CENTER_X);
        ConstraintAnchor anchor7 = getAnchor(ConstraintAnchor.Type.CENTER_Y);
        if (constraintAnchor == anchor5) {
            if (anchor.isConnected() && anchor2.isConnected() && anchor.getTarget() == anchor2.getTarget()) {
                anchor.reset();
                anchor2.reset();
            }
            if (anchor3.isConnected() && anchor4.isConnected() && anchor3.getTarget() == anchor4.getTarget()) {
                anchor3.reset();
                anchor4.reset();
            }
            this.mHorizontalBiasPercent = 0.5f;
            this.mVerticalBiasPercent = 0.5f;
        } else if (constraintAnchor == anchor6) {
            if (anchor.isConnected() && anchor2.isConnected() && anchor.getTarget().getOwner() == anchor2.getTarget().getOwner()) {
                anchor.reset();
                anchor2.reset();
            }
            this.mHorizontalBiasPercent = 0.5f;
        } else if (constraintAnchor == anchor7) {
            if (anchor3.isConnected() && anchor4.isConnected() && anchor3.getTarget().getOwner() == anchor4.getTarget().getOwner()) {
                anchor3.reset();
                anchor4.reset();
            }
            this.mVerticalBiasPercent = 0.5f;
        } else if (constraintAnchor == anchor || constraintAnchor == anchor2) {
            if (anchor.isConnected() && anchor.getTarget() == anchor2.getTarget()) {
                anchor5.reset();
            }
        } else if ((constraintAnchor == anchor3 || constraintAnchor == anchor4) && anchor3.isConnected() && anchor3.getTarget() == anchor4.getTarget()) {
            anchor5.reset();
        }
        constraintAnchor.reset();
    }

    public void resetAnchors() {
        ConstraintWidget parent = getParent();
        if (parent != null && (parent instanceof ConstraintWidgetContainer) && ((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            return;
        }
        int size = this.mAnchors.size();
        for (int i = 0; i < size; i++) {
            this.mAnchors.get(i).reset();
        }
    }

    public void resetAnchors(int i) {
        ConstraintWidget parent = getParent();
        if (parent != null && (parent instanceof ConstraintWidgetContainer) && ((ConstraintWidgetContainer) getParent()).handlesInternalConstraints()) {
            return;
        }
        int size = this.mAnchors.size();
        for (int i2 = 0; i2 < size; i2++) {
            ConstraintAnchor constraintAnchor = this.mAnchors.get(i2);
            if (i == constraintAnchor.getConnectionCreator()) {
                if (constraintAnchor.isVerticalAnchor()) {
                    setVerticalBiasPercent(DEFAULT_BIAS);
                } else {
                    setHorizontalBiasPercent(DEFAULT_BIAS);
                }
                constraintAnchor.reset();
            }
        }
    }

    public void resetResolutionNodes() {
        for (int i = 0; i < 6; i++) {
            this.mListAnchors[i].getResolutionNode().reset();
        }
    }

    public void resetSolverVariables(Cache cache) {
        this.mLeft.resetSolverVariable(cache);
        this.mTop.resetSolverVariable(cache);
        this.mRight.resetSolverVariable(cache);
        this.mBottom.resetSolverVariable(cache);
        this.mBaseline.resetSolverVariable(cache);
        this.mCenter.resetSolverVariable(cache);
        this.mCenterX.resetSolverVariable(cache);
        this.mCenterY.resetSolverVariable(cache);
    }

    public void resolve() {
    }

    public void setBaselineDistance(int i) {
        this.mBaselineDistance = i;
    }

    public void setCompanionWidget(Object obj) {
        this.mCompanionWidget = obj;
    }

    public void setContainerItemSkip(int i) {
        if (i >= 0) {
            this.mContainerItemSkip = i;
        } else {
            this.mContainerItemSkip = 0;
        }
    }

    public void setDebugName(String str) {
        this.mDebugName = str;
    }

    public void setDebugSolverName(LinearSystem linearSystem, String str) {
        this.mDebugName = str;
        SolverVariable solverVariableCreateObjectVariable = linearSystem.createObjectVariable(this.mLeft);
        SolverVariable solverVariableCreateObjectVariable2 = linearSystem.createObjectVariable(this.mTop);
        SolverVariable solverVariableCreateObjectVariable3 = linearSystem.createObjectVariable(this.mRight);
        SolverVariable solverVariableCreateObjectVariable4 = linearSystem.createObjectVariable(this.mBottom);
        solverVariableCreateObjectVariable.setName(str + ".left");
        solverVariableCreateObjectVariable2.setName(str + ".top");
        solverVariableCreateObjectVariable3.setName(str + ".right");
        solverVariableCreateObjectVariable4.setName(str + ".bottom");
        if (this.mBaselineDistance > 0) {
            linearSystem.createObjectVariable(this.mBaseline).setName(str + ".baseline");
        }
    }

    public void setDimension(int i, int i2) {
        this.mWidth = i;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
        this.mHeight = i2;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setDimensionRatio(float f, int i) {
        this.mDimensionRatio = f;
        this.mDimensionRatioSide = i;
    }

    /* JADX WARN: Code duplicated, block: B:38:0x0084 A[PHI: r0
      0x0084: PHI (r0v2 int) = (r0v1 int), (r0v0 int), (r0v0 int), (r0v0 int), (r0v0 int), (r0v0 int) binds: [B:45:0x0084, B:35:0x007d, B:23:0x004f, B:25:0x0055, B:27:0x0061, B:29:0x0065] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:38:0x0084 -> B:39:0x0085). Please report as a decompilation issue!!! */
    public void setDimensionRatio(String str) {
        float fAbs;
        int i = 0;
        if (str == null || str.length() == 0) {
            this.mDimensionRatio = 0.0f;
            return;
        }
        int i2 = -1;
        int length = str.length();
        int iIndexOf = str.indexOf(44);
        int i3 = 0;
        if (iIndexOf > 0 && iIndexOf < length - 1) {
            String strSubstring = str.substring(0, iIndexOf);
            if (strSubstring.equalsIgnoreCase(ExifInterface.LONGITUDE_WEST)) {
                i2 = 0;
            } else if (strSubstring.equalsIgnoreCase("H")) {
                i2 = 1;
            }
            i3 = iIndexOf + 1;
        }
        int iIndexOf2 = str.indexOf(58);
        try {
            if (iIndexOf2 < 0 || iIndexOf2 >= length - 1) {
                String strSubstring2 = str.substring(i3);
                if (strSubstring2.length() > 0) {
                    fAbs = Float.parseFloat(strSubstring2);
                } else {
                    fAbs = i;
                }
            } else {
                String strSubstring3 = str.substring(i3, iIndexOf2);
                String strSubstring4 = str.substring(iIndexOf2 + 1);
                if (strSubstring3.length() <= 0 || strSubstring4.length() <= 0) {
                    fAbs = i;
                } else {
                    float f = Float.parseFloat(strSubstring3);
                    float f2 = Float.parseFloat(strSubstring4);
                    if (f <= 0.0f || f2 <= 0.0f) {
                        fAbs = i;
                    } else {
                        fAbs = i2 == 1 ? Math.abs(f2 / f) : Math.abs(f / f2);
                    }
                }
            }
        } catch (NumberFormatException unused) {
        }
        i = (fAbs > i ? 1 : (fAbs == i ? 0 : -1));
        if (i > 0) {
            this.mDimensionRatio = fAbs;
            this.mDimensionRatioSide = i2;
        }
    }

    public void setDrawHeight(int i) {
        this.mDrawHeight = i;
    }

    public void setDrawOrigin(int i, int i2) {
        this.mDrawX = i - this.mOffsetX;
        this.mDrawY = i2 - this.mOffsetY;
        this.mX = this.mDrawX;
        this.mY = this.mDrawY;
    }

    public void setDrawWidth(int i) {
        this.mDrawWidth = i;
    }

    public void setDrawX(int i) {
        this.mDrawX = i - this.mOffsetX;
        this.mX = this.mDrawX;
    }

    public void setDrawY(int i) {
        this.mDrawY = i - this.mOffsetY;
        this.mY = this.mDrawY;
    }

    public void setFrame(int i, int i2, int i3) {
        if (i3 == 0) {
            setHorizontalDimension(i, i2);
        } else if (i3 == 1) {
            setVerticalDimension(i, i2);
        }
        this.mOptimizerMeasured = true;
    }

    public void setFrame(int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        int i6 = i4 - i2;
        this.mX = i;
        this.mY = i2;
        if (this.mVisibility == 8) {
            this.mWidth = 0;
            this.mHeight = 0;
            return;
        }
        if (this.mListDimensionBehaviors[0] == DimensionBehaviour.FIXED && i5 < this.mWidth) {
            i5 = this.mWidth;
        }
        if (this.mListDimensionBehaviors[1] == DimensionBehaviour.FIXED && i6 < this.mHeight) {
            i6 = this.mHeight;
        }
        this.mWidth = i5;
        this.mHeight = i6;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
        this.mOptimizerMeasured = true;
    }

    public void setGoneMargin(ConstraintAnchor.Type type, int i) {
        switch (type) {
            case LEFT:
                this.mLeft.mGoneMargin = i;
                break;
            case TOP:
                this.mTop.mGoneMargin = i;
                break;
            case RIGHT:
                this.mRight.mGoneMargin = i;
                break;
            case BOTTOM:
                this.mBottom.mGoneMargin = i;
                break;
        }
    }

    public void setHeight(int i) {
        this.mHeight = i;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setHeightWrapContent(boolean z) {
        this.mIsHeightWrapContent = z;
    }

    public void setHorizontalBiasPercent(float f) {
        this.mHorizontalBiasPercent = f;
    }

    public void setHorizontalChainStyle(int i) {
        this.mHorizontalChainStyle = i;
    }

    public void setHorizontalDimension(int i, int i2) {
        this.mX = i;
        this.mWidth = i2 - i;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setHorizontalDimensionBehaviour(DimensionBehaviour dimensionBehaviour) {
        this.mListDimensionBehaviors[0] = dimensionBehaviour;
        if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setWidth(this.mWrapWidth);
        }
    }

    public void setHorizontalMatchStyle(int i, int i2, int i3, float f) {
        this.mMatchConstraintDefaultWidth = i;
        this.mMatchConstraintMinWidth = i2;
        this.mMatchConstraintMaxWidth = i3;
        this.mMatchConstraintPercentWidth = f;
        if (f >= 1.0f || this.mMatchConstraintDefaultWidth != 0) {
            return;
        }
        this.mMatchConstraintDefaultWidth = 2;
    }

    public void setHorizontalWeight(float f) {
        this.mWeight[0] = f;
    }

    public void setLength(int i, int i2) {
        if (i2 == 0) {
            setWidth(i);
        } else if (i2 == 1) {
            setHeight(i);
        }
    }

    public void setMaxHeight(int i) {
        this.mMaxDimension[1] = i;
    }

    public void setMaxWidth(int i) {
        this.mMaxDimension[0] = i;
    }

    public void setMinHeight(int i) {
        if (i < 0) {
            this.mMinHeight = 0;
        } else {
            this.mMinHeight = i;
        }
    }

    public void setMinWidth(int i) {
        if (i < 0) {
            this.mMinWidth = 0;
        } else {
            this.mMinWidth = i;
        }
    }

    public void setOffset(int i, int i2) {
        this.mOffsetX = i;
        this.mOffsetY = i2;
    }

    public void setOrigin(int i, int i2) {
        this.mX = i;
        this.mY = i2;
    }

    public void setParent(ConstraintWidget constraintWidget) {
        this.mParent = constraintWidget;
    }

    void setRelativePositioning(int i, int i2) {
        if (i2 == 0) {
            this.mRelX = i;
        } else if (i2 == 1) {
            this.mRelY = i;
        }
    }

    public void setType(String str) {
        this.mType = str;
    }

    public void setVerticalBiasPercent(float f) {
        this.mVerticalBiasPercent = f;
    }

    public void setVerticalChainStyle(int i) {
        this.mVerticalChainStyle = i;
    }

    public void setVerticalDimension(int i, int i2) {
        this.mY = i;
        this.mHeight = i2 - i;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setVerticalDimensionBehaviour(DimensionBehaviour dimensionBehaviour) {
        this.mListDimensionBehaviors[1] = dimensionBehaviour;
        if (dimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setHeight(this.mWrapHeight);
        }
    }

    public void setVerticalMatchStyle(int i, int i2, int i3, float f) {
        this.mMatchConstraintDefaultHeight = i;
        this.mMatchConstraintMinHeight = i2;
        this.mMatchConstraintMaxHeight = i3;
        this.mMatchConstraintPercentHeight = f;
        if (f >= 1.0f || this.mMatchConstraintDefaultHeight != 0) {
            return;
        }
        this.mMatchConstraintDefaultHeight = 2;
    }

    public void setVerticalWeight(float f) {
        this.mWeight[1] = f;
    }

    public void setVisibility(int i) {
        this.mVisibility = i;
    }

    public void setWidth(int i) {
        this.mWidth = i;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setWidthWrapContent(boolean z) {
        this.mIsWidthWrapContent = z;
    }

    public void setWrapHeight(int i) {
        this.mWrapHeight = i;
    }

    public void setWrapWidth(int i) {
        this.mWrapWidth = i;
    }

    public void setX(int i) {
        this.mX = i;
    }

    public void setY(int i) {
        this.mY = i;
    }

    public void setupDimensionRatio(boolean z, boolean z2, boolean z3, boolean z4) {
        if (this.mResolvedDimensionRatioSide == -1) {
            if (z3 && !z4) {
                this.mResolvedDimensionRatioSide = 0;
            } else if (!z3 && z4) {
                this.mResolvedDimensionRatioSide = 1;
                if (this.mDimensionRatioSide == -1) {
                    this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                }
            }
        }
        if (this.mResolvedDimensionRatioSide == 0 && (!this.mTop.isConnected() || !this.mBottom.isConnected())) {
            this.mResolvedDimensionRatioSide = 1;
        } else if (this.mResolvedDimensionRatioSide == 1 && (!this.mLeft.isConnected() || !this.mRight.isConnected())) {
            this.mResolvedDimensionRatioSide = 0;
        }
        if (this.mResolvedDimensionRatioSide == -1 && (!this.mTop.isConnected() || !this.mBottom.isConnected() || !this.mLeft.isConnected() || !this.mRight.isConnected())) {
            if (this.mTop.isConnected() && this.mBottom.isConnected()) {
                this.mResolvedDimensionRatioSide = 0;
            } else if (this.mLeft.isConnected() && this.mRight.isConnected()) {
                this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                this.mResolvedDimensionRatioSide = 1;
            }
        }
        if (this.mResolvedDimensionRatioSide == -1) {
            if (z && !z2) {
                this.mResolvedDimensionRatioSide = 0;
            } else if (!z && z2) {
                this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                this.mResolvedDimensionRatioSide = 1;
            }
        }
        if (this.mResolvedDimensionRatioSide == -1) {
            if (this.mMatchConstraintMinWidth > 0 && this.mMatchConstraintMinHeight == 0) {
                this.mResolvedDimensionRatioSide = 0;
            } else if (this.mMatchConstraintMinWidth == 0 && this.mMatchConstraintMinHeight > 0) {
                this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
                this.mResolvedDimensionRatioSide = 1;
            }
        }
        if (this.mResolvedDimensionRatioSide == -1 && z && z2) {
            this.mResolvedDimensionRatio = 1.0f / this.mResolvedDimensionRatio;
            this.mResolvedDimensionRatioSide = 1;
        }
    }

    public String toString() {
        String str;
        String str2;
        StringBuilder sb = new StringBuilder();
        if (this.mType != null) {
            str = "type: " + this.mType + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
        } else {
            str = "";
        }
        sb.append(str);
        if (this.mDebugName != null) {
            str2 = "id: " + this.mDebugName + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
        } else {
            str2 = "";
        }
        sb.append(str2);
        sb.append("(");
        sb.append(this.mX);
        sb.append(", ");
        sb.append(this.mY);
        sb.append(") - (");
        sb.append(this.mWidth);
        sb.append(" x ");
        sb.append(this.mHeight);
        sb.append(") wrap: (");
        sb.append(this.mWrapWidth);
        sb.append(" x ");
        sb.append(this.mWrapHeight);
        sb.append(")");
        return sb.toString();
    }

    public void updateDrawPosition() {
        int i = this.mX;
        int i2 = this.mY;
        int i3 = this.mX + this.mWidth;
        int i4 = this.mY + this.mHeight;
        this.mDrawX = i;
        this.mDrawY = i2;
        this.mDrawWidth = i3 - i;
        this.mDrawHeight = i4 - i2;
    }

    public void updateFromSolver(LinearSystem linearSystem) {
        int objectVariableValue = linearSystem.getObjectVariableValue(this.mLeft);
        int objectVariableValue2 = linearSystem.getObjectVariableValue(this.mTop);
        int objectVariableValue3 = linearSystem.getObjectVariableValue(this.mRight);
        int objectVariableValue4 = linearSystem.getObjectVariableValue(this.mBottom);
        int i = objectVariableValue4 - objectVariableValue2;
        if (objectVariableValue3 - objectVariableValue < 0 || i < 0 || objectVariableValue == Integer.MIN_VALUE || objectVariableValue == Integer.MAX_VALUE || objectVariableValue2 == Integer.MIN_VALUE || objectVariableValue2 == Integer.MAX_VALUE || objectVariableValue3 == Integer.MIN_VALUE || objectVariableValue3 == Integer.MAX_VALUE || objectVariableValue4 == Integer.MIN_VALUE || objectVariableValue4 == Integer.MAX_VALUE) {
            objectVariableValue4 = 0;
            objectVariableValue = 0;
            objectVariableValue2 = 0;
            objectVariableValue3 = 0;
        }
        setFrame(objectVariableValue, objectVariableValue2, objectVariableValue3, objectVariableValue4);
    }

    public void updateResolutionNodes() {
        for (int i = 0; i < 6; i++) {
            this.mListAnchors[i].getResolutionNode().update();
        }
    }
}
