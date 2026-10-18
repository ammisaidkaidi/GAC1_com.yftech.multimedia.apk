package android.opengl;

/* JADX INFO: loaded from: classes2.dex */
public abstract class EGLObjectHandle {
    private final long mHandle;

    @Deprecated
    protected EGLObjectHandle(int i) {
        this.mHandle = i;
    }

    protected EGLObjectHandle(long j) {
        this.mHandle = j;
    }

    @Deprecated
    public int getHandle() {
        if ((this.mHandle & 4294967295L) == this.mHandle) {
            return (int) this.mHandle;
        }
        throw new UnsupportedOperationException();
    }

    public long getNativeHandle() {
        return this.mHandle;
    }

    public int hashCode() {
        return 527 + ((int) (this.mHandle ^ (this.mHandle >>> 32)));
    }
}
