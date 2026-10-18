package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class LightingColorFilter extends ColorFilter {
    private int mAdd;
    private int mMul;

    public LightingColorFilter(int i, int i2) {
        this.mMul = i;
        this.mAdd = i2;
    }

    private static native long native_CreateLightingFilter(int i, int i2);

    @Override // android.graphics.ColorFilter
    long createNativeInstance() {
        return native_CreateLightingFilter(this.mMul, this.mAdd);
    }

    public int getColorAdd() {
        return this.mAdd;
    }

    public int getColorMultiply() {
        return this.mMul;
    }

    public void setColorAdd(int i) {
        if (this.mAdd != i) {
            this.mAdd = i;
            discardNativeInstance();
        }
    }

    public void setColorMultiply(int i) {
        if (this.mMul != i) {
            this.mMul = i;
            discardNativeInstance();
        }
    }
}
