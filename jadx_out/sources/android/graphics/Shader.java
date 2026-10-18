package android.graphics;

import libcore.util.NativeAllocationRegistry;

/* JADX INFO: loaded from: classes.dex */
public class Shader {
    private Runnable mCleaner;
    private Matrix mLocalMatrix;
    private long mNativeInstance;

    private static class NoImagePreloadHolder {
        public static final NativeAllocationRegistry sRegistry = new NativeAllocationRegistry(Shader.class.getClassLoader(), Shader.nativeGetFinalizer(), 50);

        private NoImagePreloadHolder() {
        }
    }

    public enum TileMode {
        CLAMP(0),
        REPEAT(1),
        MIRROR(2);

        final int nativeInt;

        TileMode(int i) {
            this.nativeInt = i;
        }
    }

    @Deprecated
    public Shader() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nativeGetFinalizer();

    protected Shader copy() {
        Shader shader = new Shader();
        copyLocalMatrix(shader);
        return shader;
    }

    protected void copyLocalMatrix(Shader shader) {
        shader.mLocalMatrix.set(this.mLocalMatrix);
    }

    long createNativeInstance(long j) {
        return 0L;
    }

    protected final void discardNativeInstance() {
        if (this.mNativeInstance != 0) {
            this.mCleaner.run();
            this.mCleaner = null;
            this.mNativeInstance = 0L;
        }
    }

    public boolean getLocalMatrix(Matrix matrix) {
        if (this.mLocalMatrix == null) {
            return false;
        }
        matrix.set(this.mLocalMatrix);
        return true;
    }

    public final long getNativeInstance() {
        verifyNativeInstance();
        if (this.mNativeInstance == 0) {
            this.mNativeInstance = createNativeInstance(this.mLocalMatrix == null ? 0L : this.mLocalMatrix.native_instance);
            if (this.mNativeInstance != 0) {
                this.mCleaner = NoImagePreloadHolder.sRegistry.registerNativeAllocation(this, this.mNativeInstance);
            }
        }
        return this.mNativeInstance;
    }

    public void setLocalMatrix(Matrix matrix) {
        if (matrix == null || matrix.isIdentity()) {
            if (this.mLocalMatrix != null) {
                this.mLocalMatrix = null;
                discardNativeInstance();
                return;
            }
            return;
        }
        if (this.mLocalMatrix == null) {
            this.mLocalMatrix = new Matrix(matrix);
            discardNativeInstance();
        } else {
            if (this.mLocalMatrix.equals(matrix)) {
                return;
            }
            this.mLocalMatrix.set(matrix);
            discardNativeInstance();
        }
    }

    protected void verifyNativeInstance() {
    }
}
