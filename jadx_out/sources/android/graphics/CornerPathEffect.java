package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class CornerPathEffect extends PathEffect {
    public CornerPathEffect(float f) {
        this.native_instance = nativeCreate(f);
    }

    private static native long nativeCreate(float f);
}
