package androidx.constraintlayout.solver.widgets;

import androidx.constraintlayout.solver.LinearSystem;
import androidx.constraintlayout.solver.SolverVariable;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class Barrier extends Helper {
    public static final int BOTTOM = 3;
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int TOP = 2;
    private int mBarrierType = 0;
    private ArrayList<ResolutionAnchor> mNodes = new ArrayList<>(4);
    private boolean mAllowsGoneWidget = true;

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public void addToSolver(LinearSystem linearSystem) {
        boolean z;
        this.mListAnchors[0] = this.mLeft;
        this.mListAnchors[2] = this.mTop;
        this.mListAnchors[1] = this.mRight;
        this.mListAnchors[3] = this.mBottom;
        for (int i = 0; i < this.mListAnchors.length; i++) {
            this.mListAnchors[i].mSolverVariable = linearSystem.createObjectVariable(this.mListAnchors[i]);
        }
        if (this.mBarrierType < 0 || this.mBarrierType >= 4) {
            return;
        }
        ConstraintAnchor constraintAnchor = this.mListAnchors[this.mBarrierType];
        int i2 = 0;
        while (true) {
            if (i2 >= this.mWidgetsCount) {
                z = false;
                break;
            }
            ConstraintWidget constraintWidget = this.mWidgets[i2];
            if ((this.mAllowsGoneWidget || constraintWidget.allowedInBarrier()) && (((this.mBarrierType == 0 || this.mBarrierType == 1) && constraintWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) || ((this.mBarrierType == 2 || this.mBarrierType == 3) && constraintWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT))) {
                z = true;
                break;
            }
            i2++;
        }
        if (this.mBarrierType == 0 || this.mBarrierType == 1 ? getParent().getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT : getParent().getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
            z = false;
        }
        for (int i3 = 0; i3 < this.mWidgetsCount; i3++) {
            ConstraintWidget constraintWidget2 = this.mWidgets[i3];
            if (this.mAllowsGoneWidget || constraintWidget2.allowedInBarrier()) {
                SolverVariable solverVariableCreateObjectVariable = linearSystem.createObjectVariable(constraintWidget2.mListAnchors[this.mBarrierType]);
                constraintWidget2.mListAnchors[this.mBarrierType].mSolverVariable = solverVariableCreateObjectVariable;
                if (this.mBarrierType == 0 || this.mBarrierType == 2) {
                    linearSystem.addLowerBarrier(constraintAnchor.mSolverVariable, solverVariableCreateObjectVariable, z);
                } else {
                    linearSystem.addGreaterBarrier(constraintAnchor.mSolverVariable, solverVariableCreateObjectVariable, z);
                }
            }
        }
        if (this.mBarrierType == 0) {
            linearSystem.addEquality(this.mRight.mSolverVariable, this.mLeft.mSolverVariable, 0, 6);
            if (z) {
                return;
            }
            linearSystem.addEquality(this.mLeft.mSolverVariable, this.mParent.mRight.mSolverVariable, 0, 5);
            return;
        }
        if (this.mBarrierType == 1) {
            linearSystem.addEquality(this.mLeft.mSolverVariable, this.mRight.mSolverVariable, 0, 6);
            if (z) {
                return;
            }
            linearSystem.addEquality(this.mLeft.mSolverVariable, this.mParent.mLeft.mSolverVariable, 0, 5);
            return;
        }
        if (this.mBarrierType == 2) {
            linearSystem.addEquality(this.mBottom.mSolverVariable, this.mTop.mSolverVariable, 0, 6);
            if (z) {
                return;
            }
            linearSystem.addEquality(this.mTop.mSolverVariable, this.mParent.mBottom.mSolverVariable, 0, 5);
            return;
        }
        if (this.mBarrierType == 3) {
            linearSystem.addEquality(this.mTop.mSolverVariable, this.mBottom.mSolverVariable, 0, 6);
            if (z) {
                return;
            }
            linearSystem.addEquality(this.mTop.mSolverVariable, this.mParent.mTop.mSolverVariable, 0, 5);
        }
    }

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public boolean allowedInBarrier() {
        return true;
    }

    public boolean allowsGoneWidget() {
        return this.mAllowsGoneWidget;
    }

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public void analyze(int i) {
        ResolutionAnchor resolutionNode;
        ResolutionAnchor resolutionNode2;
        if (this.mParent != null && ((ConstraintWidgetContainer) this.mParent).optimizeFor(2)) {
            switch (this.mBarrierType) {
                case 0:
                    resolutionNode = this.mLeft.getResolutionNode();
                    break;
                case 1:
                    resolutionNode = this.mRight.getResolutionNode();
                    break;
                case 2:
                    resolutionNode = this.mTop.getResolutionNode();
                    break;
                case 3:
                    resolutionNode = this.mBottom.getResolutionNode();
                    break;
                default:
                    return;
            }
            resolutionNode.setType(5);
            if (this.mBarrierType == 0 || this.mBarrierType == 1) {
                this.mTop.getResolutionNode().resolve(null, 0.0f);
                this.mBottom.getResolutionNode().resolve(null, 0.0f);
            } else {
                this.mLeft.getResolutionNode().resolve(null, 0.0f);
                this.mRight.getResolutionNode().resolve(null, 0.0f);
            }
            this.mNodes.clear();
            for (int i2 = 0; i2 < this.mWidgetsCount; i2++) {
                ConstraintWidget constraintWidget = this.mWidgets[i2];
                if (this.mAllowsGoneWidget || constraintWidget.allowedInBarrier()) {
                    switch (this.mBarrierType) {
                        case 0:
                            resolutionNode2 = constraintWidget.mLeft.getResolutionNode();
                            break;
                        case 1:
                            resolutionNode2 = constraintWidget.mRight.getResolutionNode();
                            break;
                        case 2:
                            resolutionNode2 = constraintWidget.mTop.getResolutionNode();
                            break;
                        case 3:
                            resolutionNode2 = constraintWidget.mBottom.getResolutionNode();
                            break;
                        default:
                            resolutionNode2 = null;
                            break;
                    }
                    if (resolutionNode2 != null) {
                        this.mNodes.add(resolutionNode2);
                        resolutionNode2.addDependent(resolutionNode);
                    }
                }
            }
        }
    }

    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public void resetResolutionNodes() {
        super.resetResolutionNodes();
        this.mNodes.clear();
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0030  */
    /* JADX WARN: Code duplicated, block: B:15:0x003e  */
    /* JADX WARN: Code duplicated, block: B:17:0x0042  */
    /* JADX WARN: Code duplicated, block: B:23:0x0053  */
    /* JADX WARN: Code duplicated, block: B:25:0x0059  */
    /* JADX WARN: Code duplicated, block: B:29:0x0066  */
    /* JADX WARN: Code duplicated, block: B:33:0x007e  */
    /* JADX WARN: Code duplicated, block: B:34:0x0088  */
    /* JADX WARN: Code duplicated, block: B:35:0x0092  */
    /* JADX WARN: Code duplicated, block: B:36:0x009c  */
    /* JADX WARN: Code duplicated, block: B:39:0x003d A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:43:0x005d A[SYNTHETIC] */
    @Override // androidx.constraintlayout.solver.widgets.ConstraintWidget
    public void resolve() {
        ResolutionAnchor resolutionNode;
        int size;
        ResolutionAnchor resolutionAnchor;
        int i;
        ResolutionAnchor resolutionAnchor2;
        float f = Float.MAX_VALUE;
        switch (this.mBarrierType) {
            case 0:
                resolutionNode = this.mLeft.getResolutionNode();
                size = this.mNodes.size();
                resolutionAnchor = null;
                for (i = 0; i < size; i++) {
                    resolutionAnchor2 = this.mNodes.get(i);
                    if (resolutionAnchor2.state != 1) {
                        if (this.mBarrierType != 0 || this.mBarrierType == 2) {
                            if (resolutionAnchor2.resolvedOffset < f) {
                                f = resolutionAnchor2.resolvedOffset;
                                resolutionAnchor = resolutionAnchor2.resolvedTarget;
                            }
                        } else if (resolutionAnchor2.resolvedOffset > f) {
                            f = resolutionAnchor2.resolvedOffset;
                            resolutionAnchor = resolutionAnchor2.resolvedTarget;
                        }
                    }
                    break;
                }
                if (LinearSystem.getMetrics() != null) {
                    LinearSystem.getMetrics().barrierConnectionResolved++;
                }
                resolutionNode.resolvedTarget = resolutionAnchor;
                resolutionNode.resolvedOffset = f;
                resolutionNode.didResolve();
                switch (this.mBarrierType) {
                    case 0:
                        this.mRight.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 1:
                        this.mLeft.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 2:
                        this.mBottom.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 3:
                        this.mTop.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                }
                break;
            case 1:
                resolutionNode = this.mRight.getResolutionNode();
                f = 0.0f;
                size = this.mNodes.size();
                resolutionAnchor = null;
                while (i < size) {
                    resolutionAnchor2 = this.mNodes.get(i);
                    if (resolutionAnchor2.state != 1) {
                        if (this.mBarrierType != 0) {
                            if (resolutionAnchor2.resolvedOffset < f) {
                                f = resolutionAnchor2.resolvedOffset;
                                resolutionAnchor = resolutionAnchor2.resolvedTarget;
                            }
                        } else if (resolutionAnchor2.resolvedOffset < f) {
                            f = resolutionAnchor2.resolvedOffset;
                            resolutionAnchor = resolutionAnchor2.resolvedTarget;
                        }
                    }
                    break;
                }
                if (LinearSystem.getMetrics() != null) {
                    LinearSystem.getMetrics().barrierConnectionResolved++;
                }
                resolutionNode.resolvedTarget = resolutionAnchor;
                resolutionNode.resolvedOffset = f;
                resolutionNode.didResolve();
                switch (this.mBarrierType) {
                    case 0:
                        this.mRight.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 1:
                        this.mLeft.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 2:
                        this.mBottom.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 3:
                        this.mTop.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                }
                break;
            case 2:
                resolutionNode = this.mTop.getResolutionNode();
                size = this.mNodes.size();
                resolutionAnchor = null;
                while (i < size) {
                    resolutionAnchor2 = this.mNodes.get(i);
                    if (resolutionAnchor2.state != 1) {
                        if (this.mBarrierType != 0) {
                            if (resolutionAnchor2.resolvedOffset < f) {
                                f = resolutionAnchor2.resolvedOffset;
                                resolutionAnchor = resolutionAnchor2.resolvedTarget;
                            }
                        } else if (resolutionAnchor2.resolvedOffset < f) {
                            f = resolutionAnchor2.resolvedOffset;
                            resolutionAnchor = resolutionAnchor2.resolvedTarget;
                        }
                    }
                    break;
                }
                if (LinearSystem.getMetrics() != null) {
                    LinearSystem.getMetrics().barrierConnectionResolved++;
                }
                resolutionNode.resolvedTarget = resolutionAnchor;
                resolutionNode.resolvedOffset = f;
                resolutionNode.didResolve();
                switch (this.mBarrierType) {
                    case 0:
                        this.mRight.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 1:
                        this.mLeft.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 2:
                        this.mBottom.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 3:
                        this.mTop.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                }
                break;
            case 3:
                resolutionNode = this.mBottom.getResolutionNode();
                f = 0.0f;
                size = this.mNodes.size();
                resolutionAnchor = null;
                while (i < size) {
                    resolutionAnchor2 = this.mNodes.get(i);
                    if (resolutionAnchor2.state != 1) {
                        if (this.mBarrierType != 0) {
                            if (resolutionAnchor2.resolvedOffset < f) {
                                f = resolutionAnchor2.resolvedOffset;
                                resolutionAnchor = resolutionAnchor2.resolvedTarget;
                            }
                        } else if (resolutionAnchor2.resolvedOffset < f) {
                            f = resolutionAnchor2.resolvedOffset;
                            resolutionAnchor = resolutionAnchor2.resolvedTarget;
                        }
                    }
                    break;
                }
                if (LinearSystem.getMetrics() != null) {
                    LinearSystem.getMetrics().barrierConnectionResolved++;
                }
                resolutionNode.resolvedTarget = resolutionAnchor;
                resolutionNode.resolvedOffset = f;
                resolutionNode.didResolve();
                switch (this.mBarrierType) {
                    case 0:
                        this.mRight.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 1:
                        this.mLeft.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 2:
                        this.mBottom.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                    case 3:
                        this.mTop.getResolutionNode().resolve(resolutionAnchor, f);
                        break;
                }
                break;
        }
    }

    public void setAllowsGoneWidget(boolean z) {
        this.mAllowsGoneWidget = z;
    }

    public void setBarrierType(int i) {
        this.mBarrierType = i;
    }
}
