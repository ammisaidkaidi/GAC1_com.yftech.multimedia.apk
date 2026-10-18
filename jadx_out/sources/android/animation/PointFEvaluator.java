package android.animation;

import android.graphics.PointF;

/* JADX INFO: loaded from: classes.dex */
public class PointFEvaluator implements TypeEvaluator<PointF> {
    private PointF mPoint;

    public PointFEvaluator() {
    }

    public PointFEvaluator(PointF pointF) {
        this.mPoint = pointF;
    }

    @Override // android.animation.TypeEvaluator
    public PointF evaluate(float f, PointF pointF, PointF pointF2) {
        float f2 = pointF.x + ((pointF2.x - pointF.x) * f);
        float f3 = pointF.y + (f * (pointF2.y - pointF.y));
        if (this.mPoint == null) {
            return new PointF(f2, f3);
        }
        this.mPoint.set(f2, f3);
        return this.mPoint;
    }
}
