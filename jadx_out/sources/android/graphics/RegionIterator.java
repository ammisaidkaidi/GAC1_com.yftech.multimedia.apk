package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class RegionIterator {
    private long mNativeIter;

    public RegionIterator(Region region) {
        this.mNativeIter = nativeConstructor(region.ni());
    }

    private static native long nativeConstructor(long j);

    private static native void nativeDestructor(long j);

    private static native boolean nativeNext(long j, Rect rect);

    protected void finalize() throws Throwable {
        nativeDestructor(this.mNativeIter);
        this.mNativeIter = 0L;
    }

    public final boolean next(Rect rect) {
        if (rect != null) {
            return nativeNext(this.mNativeIter, rect);
        }
        throw new NullPointerException("The Rect must be provided");
    }
}
