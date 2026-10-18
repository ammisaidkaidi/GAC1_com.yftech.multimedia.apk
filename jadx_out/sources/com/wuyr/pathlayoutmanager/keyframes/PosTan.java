package com.wuyr.pathlayoutmanager.keyframes;

import android.graphics.PointF;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public class PosTan extends PointF {
    private float angle;
    public float fraction;
    public int index;

    PosTan() {
    }

    private PosTan(int i, float f, float f2, float f3) {
        super(f, f2);
        this.angle = f3;
        this.index = i;
    }

    public PosTan(PosTan posTan, int i, float f) {
        this(i, posTan.x, posTan.y, posTan.angle);
        this.fraction = f;
    }

    @Override // android.graphics.PointF
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof PosTan)) {
            if (this != obj) {
                return false;
            }
        } else if (this.index != ((PosTan) obj).index) {
            return false;
        }
        return true;
    }

    public float getChildAngle() {
        return this.angle - 90.0f;
    }

    public void set(float f, float f2, float f3) {
        this.x = f;
        this.y = f2;
        this.angle = f3;
    }

    @Override // android.graphics.PointF
    public String toString() {
        return String.format(Locale.getDefault(), "x: %f\ty: %f\tangle: %f", Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.angle));
    }
}
