package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.ArrayRow;
import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.SolverVariable;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
class Chain {
    private static final boolean DEBUG = false;

    Chain() {
    }

    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i) {
        int i2;
        int i3;
        ChainHead[] chainHeadArr;
        if (i == 0) {
            int i4 = constraintWidgetContainer.mHorizontalChainsSize;
            chainHeadArr = constraintWidgetContainer.mHorizontalChainsArray;
            i3 = i4;
            i2 = 0;
        } else {
            i2 = 2;
            i3 = constraintWidgetContainer.mVerticalChainsSize;
            chainHeadArr = constraintWidgetContainer.mVerticalChainsArray;
        }
        for (int i5 = 0; i5 < i3; i5++) {
            ChainHead chainHead = chainHeadArr[i5];
            chainHead.define();
            if (!constraintWidgetContainer.optimizeFor(4)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i2, chainHead);
            } else if (!Optimizer.applyChainOptimized(constraintWidgetContainer, linearSystem, i, i2, chainHead)) {
                applyChainConstraints(constraintWidgetContainer, linearSystem, i, i2, chainHead);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:18:0x0037 A[PHI: r6 r8
      0x0037: PHI (r6v49 boolean) = (r6v3 boolean), (r6v52 boolean) binds: [B:30:0x0053, B:17:0x0035] A[DONT_GENERATE, DONT_INLINE]
      0x0037: PHI (r8v37 boolean) = (r8v1 boolean), (r8v40 boolean) binds: [B:30:0x0053, B:17:0x0035] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:19:0x0039 A[PHI: r6 r8
      0x0039: PHI (r6v5 boolean) = (r6v3 boolean), (r6v52 boolean) binds: [B:30:0x0053, B:17:0x0035] A[DONT_GENERATE, DONT_INLINE]
      0x0039: PHI (r8v3 boolean) = (r8v1 boolean), (r8v40 boolean) binds: [B:30:0x0053, B:17:0x0035] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:200:0x03b4  */
    /* JADX WARN: Code duplicated, block: B:201:0x03b7  */
    /* JADX WARN: Code duplicated, block: B:85:0x0161  */
    static void applyChainConstraints(ConstraintWidgetContainer constraintWidgetContainer, LinearSystem linearSystem, int i, int i2, ChainHead chainHead) {
        boolean z;
        boolean z2;
        boolean z3;
        LinearSystem linearSystem2;
        LinearSystem linearSystem3;
        ConstraintWidget constraintWidget;
        ConstraintAnchor constraintAnchor;
        SolverVariable solverVariable;
        SolverVariable solverVariable2;
        int i3;
        SolverVariable solverVariable3;
        SolverVariable solverVariable4;
        ConstraintAnchor constraintAnchor2;
        int i4;
        int size;
        int i5;
        int i6;
        ConstraintWidget constraintWidget2 = chainHead.mFirst;
        ConstraintWidget constraintWidget3 = chainHead.mLast;
        ConstraintWidget constraintWidget4 = chainHead.mFirstVisibleWidget;
        ConstraintWidget constraintWidget5 = chainHead.mLastVisibleWidget;
        ConstraintWidget constraintWidget6 = chainHead.mHead;
        float f = chainHead.mTotalWeight;
        ConstraintWidget constraintWidget7 = chainHead.mFirstMatchConstraintWidget;
        ConstraintWidget constraintWidget8 = chainHead.mLastMatchConstraintWidget;
        boolean z4 = constraintWidgetContainer.mListDimensionBehaviors[i] == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        if (i == 0) {
            z = constraintWidget6.mHorizontalChainStyle == 0;
            z2 = constraintWidget6.mHorizontalChainStyle == 1;
            if (constraintWidget6.mHorizontalChainStyle == 2) {
                z3 = true;
            } else {
                z3 = false;
            }
        } else {
            z = constraintWidget6.mVerticalChainStyle == 0;
            z2 = constraintWidget6.mVerticalChainStyle == 1;
            if (constraintWidget6.mVerticalChainStyle == 2) {
                z3 = true;
            } else {
                z3 = false;
            }
        }
        boolean z5 = z3;
        boolean z6 = z2;
        boolean z7 = z;
        ConstraintWidget constraintWidget9 = constraintWidget2;
        boolean z8 = false;
        while (true) {
            ConstraintWidget constraintWidget10 = null;
            if (z8) {
                break;
            }
            ConstraintAnchor constraintAnchor3 = constraintWidget9.mListAnchors[i2];
            int i7 = (z4 || z5) ? 1 : 4;
            int margin = constraintAnchor3.getMargin();
            float f2 = f;
            if (constraintAnchor3.mTarget != null && constraintWidget9 != constraintWidget2) {
                margin += constraintAnchor3.mTarget.getMargin();
            }
            int i8 = margin;
            if (!z5 || constraintWidget9 == constraintWidget2 || constraintWidget9 == constraintWidget4) {
                i5 = (z7 && z4) ? 4 : i7;
            } else {
                i5 = 6;
            }
            if (constraintAnchor3.mTarget != null) {
                if (constraintWidget9 == constraintWidget4) {
                    linearSystem.addGreaterThan(constraintAnchor3.mSolverVariable, constraintAnchor3.mTarget.mSolverVariable, i8, 5);
                } else {
                    linearSystem.addGreaterThan(constraintAnchor3.mSolverVariable, constraintAnchor3.mTarget.mSolverVariable, i8, 6);
                }
                linearSystem.addEquality(constraintAnchor3.mSolverVariable, constraintAnchor3.mTarget.mSolverVariable, i8, i5);
            } else {
                z5 = z5;
                constraintWidget2 = constraintWidget2;
            }
            if (z4) {
                if (constraintWidget9.getVisibility() == 8 || constraintWidget9.mListDimensionBehaviors[i] != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    i6 = 0;
                } else {
                    i6 = 0;
                    linearSystem.addGreaterThan(constraintWidget9.mListAnchors[i2 + 1].mSolverVariable, constraintWidget9.mListAnchors[i2].mSolverVariable, 0, 5);
                }
                linearSystem.addGreaterThan(constraintWidget9.mListAnchors[i2].mSolverVariable, constraintWidgetContainer.mListAnchors[i2].mSolverVariable, i6, 6);
            }
            ConstraintAnchor constraintAnchor4 = constraintWidget9.mListAnchors[i2 + 1].mTarget;
            if (constraintAnchor4 != null) {
                ConstraintWidget constraintWidget11 = constraintAnchor4.mOwner;
                if (constraintWidget11.mListAnchors[i2].mTarget != null && constraintWidget11.mListAnchors[i2].mTarget.mOwner == constraintWidget9) {
                    constraintWidget10 = constraintWidget11;
                }
            }
            if (constraintWidget10 != null) {
                constraintWidget9 = constraintWidget10;
                z8 = z8;
            } else {
                z8 = true;
            }
            f = f2;
            constraintWidget6 = constraintWidget6;
            constraintWidget2 = constraintWidget2;
            z5 = z5;
        }
        ConstraintWidget constraintWidget12 = constraintWidget6;
        float f3 = f;
        boolean z9 = z5;
        ConstraintWidget constraintWidget13 = constraintWidget2;
        if (constraintWidget5 != null) {
            int i9 = i2 + 1;
            if (constraintWidget3.mListAnchors[i9].mTarget != null) {
                ConstraintAnchor constraintAnchor5 = constraintWidget5.mListAnchors[i9];
                linearSystem.addLowerThan(constraintAnchor5.mSolverVariable, constraintWidget3.mListAnchors[i9].mTarget.mSolverVariable, -constraintAnchor5.getMargin(), 5);
            }
        }
        if (z4) {
            int i10 = i2 + 1;
            linearSystem.addGreaterThan(constraintWidgetContainer.mListAnchors[i10].mSolverVariable, constraintWidget3.mListAnchors[i10].mSolverVariable, constraintWidget3.mListAnchors[i10].getMargin(), 6);
        }
        ArrayList<ConstraintWidget> arrayList = chainHead.mWeightedMatchConstraintsWidgets;
        if (arrayList != null && (size = arrayList.size()) > 1) {
            if (chainHead.mHasUndefinedWeights && !chainHead.mHasComplexMatchWeights) {
                f3 = chainHead.mWidgetsMatchCount;
            }
            float f4 = 0.0f;
            float f5 = 0.0f;
            ConstraintWidget constraintWidget14 = null;
            int i11 = 0;
            while (i11 < size) {
                ConstraintWidget constraintWidget15 = arrayList.get(i11);
                float f6 = constraintWidget15.mWeight[i];
                if (f6 < f4) {
                    if (chainHead.mHasComplexMatchWeights) {
                        linearSystem.addEquality(constraintWidget15.mListAnchors[i2 + 1].mSolverVariable, constraintWidget15.mListAnchors[i2].mSolverVariable, 0, 4);
                    } else {
                        f6 = 1.0f;
                    }
                    arrayList = arrayList;
                    i11++;
                    arrayList = arrayList;
                    f4 = 0.0f;
                }
                if (f6 == f4) {
                    linearSystem.addEquality(constraintWidget15.mListAnchors[i2 + 1].mSolverVariable, constraintWidget15.mListAnchors[i2].mSolverVariable, 0, 6);
                    arrayList = arrayList;
                } else {
                    if (constraintWidget14 != null) {
                        SolverVariable solverVariable5 = constraintWidget14.mListAnchors[i2].mSolverVariable;
                        int i12 = i2 + 1;
                        SolverVariable solverVariable6 = constraintWidget14.mListAnchors[i12].mSolverVariable;
                        SolverVariable solverVariable7 = constraintWidget15.mListAnchors[i2].mSolverVariable;
                        SolverVariable solverVariable8 = constraintWidget15.mListAnchors[i12].mSolverVariable;
                        ArrayRow arrayRowCreateRow = linearSystem.createRow();
                        arrayRowCreateRow.createRowEqualMatchDimensions(f5, f3, f6, solverVariable5, solverVariable6, solverVariable7, solverVariable8);
                        linearSystem.addConstraint(arrayRowCreateRow);
                    }
                    constraintWidget14 = constraintWidget15;
                    f5 = f6;
                }
                i11++;
                arrayList = arrayList;
                f4 = 0.0f;
            }
        }
        if (constraintWidget4 != null && (constraintWidget4 == constraintWidget5 || z9)) {
            ConstraintAnchor constraintAnchor6 = constraintWidget13.mListAnchors[i2];
            int i13 = i2 + 1;
            ConstraintAnchor constraintAnchor7 = constraintWidget3.mListAnchors[i13];
            SolverVariable solverVariable9 = constraintWidget13.mListAnchors[i2].mTarget != null ? constraintWidget13.mListAnchors[i2].mTarget.mSolverVariable : null;
            SolverVariable solverVariable10 = constraintWidget3.mListAnchors[i13].mTarget != null ? constraintWidget3.mListAnchors[i13].mTarget.mSolverVariable : null;
            if (constraintWidget4 == constraintWidget5) {
                constraintAnchor6 = constraintWidget4.mListAnchors[i2];
                constraintAnchor7 = constraintWidget4.mListAnchors[i13];
            }
            if (solverVariable9 != null && solverVariable10 != null) {
                linearSystem.addCentering(constraintAnchor6.mSolverVariable, solverVariable9, constraintAnchor6.getMargin(), i == 0 ? constraintWidget12.mHorizontalBiasPercent : constraintWidget12.mVerticalBiasPercent, solverVariable10, constraintAnchor7.mSolverVariable, constraintAnchor7.getMargin(), 5);
            }
        } else if (!z7 || constraintWidget4 == null) {
            int i14 = 8;
            if (z6 && constraintWidget4 != null) {
                boolean z10 = chainHead.mWidgetsMatchCount > 0 && chainHead.mWidgetsCount == chainHead.mWidgetsMatchCount;
                ConstraintWidget constraintWidget16 = constraintWidget4;
                ConstraintWidget constraintWidget17 = constraintWidget16;
                while (constraintWidget17 != null) {
                    ConstraintWidget constraintWidget18 = constraintWidget17.mNextChainWidget[i];
                    while (constraintWidget18 != null && constraintWidget18.getVisibility() == i14) {
                        constraintWidget18 = constraintWidget18.mNextChainWidget[i];
                    }
                    if (constraintWidget17 == constraintWidget4 || constraintWidget17 == constraintWidget5 || constraintWidget18 == null) {
                        constraintWidget = constraintWidget16;
                        constraintWidget17 = constraintWidget17;
                        constraintWidget17 = constraintWidget18;
                    } else {
                        ConstraintWidget constraintWidget19 = constraintWidget18 == constraintWidget5 ? null : constraintWidget18;
                        ConstraintAnchor constraintAnchor8 = constraintWidget17.mListAnchors[i2];
                        SolverVariable solverVariable11 = constraintAnchor8.mSolverVariable;
                        if (constraintAnchor8.mTarget != null) {
                            SolverVariable solverVariable12 = constraintAnchor8.mTarget.mSolverVariable;
                        }
                        int i15 = i2 + 1;
                        SolverVariable solverVariable13 = constraintWidget16.mListAnchors[i15].mSolverVariable;
                        int margin2 = constraintAnchor8.getMargin();
                        int margin3 = constraintWidget17.mListAnchors[i15].getMargin();
                        if (constraintWidget19 != null) {
                            constraintAnchor = constraintWidget19.mListAnchors[i2];
                            SolverVariable solverVariable14 = constraintAnchor.mSolverVariable;
                            solverVariable2 = constraintAnchor.mTarget != null ? constraintAnchor.mTarget.mSolverVariable : null;
                            solverVariable = solverVariable14;
                        } else {
                            constraintAnchor = constraintWidget17.mListAnchors[i15].mTarget;
                            solverVariable = constraintAnchor != null ? constraintAnchor.mSolverVariable : null;
                            solverVariable2 = constraintWidget17.mListAnchors[i15].mSolverVariable;
                        }
                        if (constraintAnchor != null) {
                            margin3 += constraintAnchor.getMargin();
                        }
                        int i16 = margin3;
                        if (constraintWidget16 != null) {
                            margin2 += constraintWidget16.mListAnchors[i15].getMargin();
                        }
                        int i17 = margin2;
                        int i18 = z10 ? 6 : 4;
                        if (solverVariable11 == null || solverVariable13 == null || solverVariable == null || solverVariable2 == null) {
                            constraintWidget = constraintWidget16;
                        } else {
                            constraintWidget = constraintWidget16;
                            linearSystem.addCentering(solverVariable11, solverVariable13, i17, 0.5f, solverVariable, solverVariable2, i16, i18);
                        }
                        constraintWidget17 = constraintWidget19;
                    }
                    constraintWidget16 = constraintWidget17.getVisibility() != 8 ? constraintWidget17 : constraintWidget;
                    i14 = 8;
                }
                ConstraintAnchor constraintAnchor9 = constraintWidget4.mListAnchors[i2];
                ConstraintAnchor constraintAnchor10 = constraintWidget13.mListAnchors[i2].mTarget;
                int i19 = i2 + 1;
                ConstraintAnchor constraintAnchor11 = constraintWidget5.mListAnchors[i19];
                ConstraintAnchor constraintAnchor12 = constraintWidget3.mListAnchors[i19].mTarget;
                if (constraintAnchor10 != null) {
                    if (constraintWidget4 != constraintWidget5) {
                        linearSystem3 = linearSystem;
                        linearSystem3.addEquality(constraintAnchor9.mSolverVariable, constraintAnchor10.mSolverVariable, constraintAnchor9.getMargin(), 5);
                    } else {
                        linearSystem3 = linearSystem;
                        if (constraintAnchor12 != null) {
                            linearSystem2 = linearSystem3;
                            linearSystem.addCentering(constraintAnchor9.mSolverVariable, constraintAnchor10.mSolverVariable, constraintAnchor9.getMargin(), 0.5f, constraintAnchor11.mSolverVariable, constraintAnchor12.mSolverVariable, constraintAnchor11.getMargin(), 5);
                        }
                    }
                    linearSystem2 = linearSystem3;
                } else {
                    linearSystem2 = linearSystem;
                }
                if (constraintAnchor12 != null && constraintWidget4 != constraintWidget5) {
                    linearSystem2.addEquality(constraintAnchor11.mSolverVariable, constraintAnchor12.mSolverVariable, -constraintAnchor11.getMargin(), 5);
                }
            }
        } else {
            boolean z11 = chainHead.mWidgetsMatchCount > 0 && chainHead.mWidgetsCount == chainHead.mWidgetsMatchCount;
            ConstraintWidget constraintWidget20 = constraintWidget4;
            ConstraintWidget constraintWidget21 = constraintWidget20;
            while (constraintWidget21 != null) {
                ConstraintWidget constraintWidget22 = constraintWidget21.mNextChainWidget[i];
                while (true) {
                    if (constraintWidget22 == null) {
                        i3 = 8;
                        break;
                    }
                    i3 = 8;
                    if (constraintWidget22.getVisibility() != 8) {
                        break;
                    } else {
                        constraintWidget22 = constraintWidget22.mNextChainWidget[i];
                    }
                }
                if (constraintWidget22 != null || constraintWidget21 == constraintWidget5) {
                    ConstraintAnchor constraintAnchor13 = constraintWidget21.mListAnchors[i2];
                    SolverVariable solverVariable15 = constraintAnchor13.mSolverVariable;
                    SolverVariable solverVariable16 = constraintAnchor13.mTarget != null ? constraintAnchor13.mTarget.mSolverVariable : null;
                    if (constraintWidget20 != constraintWidget21) {
                        solverVariable16 = constraintWidget20.mListAnchors[i2 + 1].mSolverVariable;
                    } else if (constraintWidget21 == constraintWidget4 && constraintWidget20 == constraintWidget21) {
                        solverVariable16 = constraintWidget13.mListAnchors[i2].mTarget != null ? constraintWidget13.mListAnchors[i2].mTarget.mSolverVariable : null;
                    }
                    int margin4 = constraintAnchor13.getMargin();
                    int i20 = i2 + 1;
                    int margin5 = constraintWidget21.mListAnchors[i20].getMargin();
                    if (constraintWidget22 != null) {
                        ConstraintAnchor constraintAnchor14 = constraintWidget22.mListAnchors[i2];
                        solverVariable3 = constraintAnchor14.mSolverVariable;
                        constraintAnchor2 = constraintAnchor14;
                        solverVariable4 = constraintWidget21.mListAnchors[i20].mSolverVariable;
                    } else {
                        ConstraintAnchor constraintAnchor15 = constraintWidget3.mListAnchors[i20].mTarget;
                        solverVariable3 = constraintAnchor15 != null ? constraintAnchor15.mSolverVariable : null;
                        solverVariable4 = constraintWidget21.mListAnchors[i20].mSolverVariable;
                        constraintAnchor2 = constraintAnchor15;
                    }
                    SolverVariable solverVariable17 = solverVariable3;
                    SolverVariable solverVariable18 = solverVariable4;
                    if (constraintAnchor2 != null) {
                        margin5 += constraintAnchor2.getMargin();
                    }
                    if (constraintWidget20 != null) {
                        margin4 += constraintWidget20.mListAnchors[i20].getMargin();
                    }
                    if (solverVariable15 == null || solverVariable16 == null || solverVariable17 == null || solverVariable18 == null) {
                        i4 = 8;
                    } else {
                        if (constraintWidget21 == constraintWidget4) {
                            margin4 = constraintWidget4.mListAnchors[i2].getMargin();
                        }
                        int i21 = margin4;
                        if (constraintWidget21 == constraintWidget5) {
                            margin5 = constraintWidget5.mListAnchors[i20].getMargin();
                        }
                        constraintWidget22 = constraintWidget22;
                        constraintWidget20 = constraintWidget20;
                        i4 = 8;
                        constraintWidget21 = constraintWidget21;
                        linearSystem.addCentering(solverVariable15, solverVariable16, i21, 0.5f, solverVariable17, solverVariable18, margin5, z11 ? 6 : 4);
                    }
                    if (constraintWidget21.getVisibility() != i4) {
                        constraintWidget20 = constraintWidget21;
                    } else {
                        constraintWidget20 = constraintWidget20;
                    }
                    constraintWidget21 = constraintWidget22;
                } else {
                    i4 = i3;
                }
                if (constraintWidget21.getVisibility() != i4) {
                    constraintWidget20 = constraintWidget21;
                } else {
                    constraintWidget20 = constraintWidget20;
                }
                constraintWidget21 = constraintWidget22;
            }
        }
        if ((z7 || z6) && constraintWidget4 != null) {
            ConstraintAnchor constraintAnchor16 = constraintWidget4.mListAnchors[i2];
            int i22 = i2 + 1;
            ConstraintAnchor constraintAnchor17 = constraintWidget5.mListAnchors[i22];
            SolverVariable solverVariable19 = constraintAnchor16.mTarget != null ? constraintAnchor16.mTarget.mSolverVariable : null;
            SolverVariable solverVariable20 = constraintAnchor17.mTarget != null ? constraintAnchor17.mTarget.mSolverVariable : null;
            if (constraintWidget3 != constraintWidget5) {
                ConstraintAnchor constraintAnchor18 = constraintWidget3.mListAnchors[i22];
                solverVariable20 = constraintAnchor18.mTarget != null ? constraintAnchor18.mTarget.mSolverVariable : null;
            }
            SolverVariable solverVariable21 = solverVariable20;
            if (constraintWidget4 == constraintWidget5) {
                constraintAnchor16 = constraintWidget4.mListAnchors[i2];
                constraintAnchor17 = constraintWidget4.mListAnchors[i22];
            }
            if (solverVariable19 == null || solverVariable21 == null) {
                return;
            }
            int margin6 = constraintAnchor16.getMargin();
            if (constraintWidget5 != null) {
                constraintWidget3 = constraintWidget5;
            }
            linearSystem.addCentering(constraintAnchor16.mSolverVariable, solverVariable19, margin6, 0.5f, solverVariable21, constraintAnchor17.mSolverVariable, constraintWidget3.mListAnchors[i22].getMargin(), 5);
        }
    }
}
