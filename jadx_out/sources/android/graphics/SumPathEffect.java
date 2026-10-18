package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class SumPathEffect extends PathEffect {
    public SumPathEffect(PathEffect pathEffect, PathEffect pathEffect2) {
        this.native_instance = nativeCreate(pathEffect.native_instance, pathEffect2.native_instance);
    }

    private static native long nativeCreate(long j, long j2);
}
