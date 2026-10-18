package android.filterfw.core;

/* JADX INFO: loaded from: classes.dex */
public class NativeBuffer {
    private Frame mAttachedFrame;
    private long mDataPointer;
    private boolean mOwnsData;
    private int mRefCount;
    private int mSize;

    static {
        System.loadLibrary("filterfw");
    }

    public NativeBuffer() {
        this.mDataPointer = 0L;
        this.mSize = 0;
        this.mOwnsData = false;
        this.mRefCount = 1;
    }

    public NativeBuffer(int i) {
        this.mDataPointer = 0L;
        this.mSize = 0;
        this.mOwnsData = false;
        this.mRefCount = 1;
        allocate(i * getElementSize());
        this.mOwnsData = true;
    }

    private native boolean allocate(int i);

    private native boolean deallocate(boolean z);

    private native boolean nativeCopyTo(NativeBuffer nativeBuffer);

    protected void assertReadable() {
        if (this.mDataPointer == 0 || this.mSize == 0 || !(this.mAttachedFrame == null || this.mAttachedFrame.hasNativeAllocation())) {
            throw new NullPointerException("Attempting to read from null data frame!");
        }
    }

    protected void assertWritable() {
        if (isReadOnly()) {
            throw new RuntimeException("Attempting to modify read-only native (structured) data!");
        }
    }

    void attachToFrame(Frame frame) {
        this.mAttachedFrame = frame;
    }

    public int count() {
        if (this.mDataPointer != 0) {
            return this.mSize / getElementSize();
        }
        return 0;
    }

    public int getElementSize() {
        return 1;
    }

    public boolean isReadOnly() {
        if (this.mAttachedFrame != null) {
            return this.mAttachedFrame.isReadOnly();
        }
        return false;
    }

    public NativeBuffer mutableCopy() {
        try {
            NativeBuffer nativeBuffer = (NativeBuffer) getClass().newInstance();
            if (this.mSize <= 0 || nativeCopyTo(nativeBuffer)) {
                return nativeBuffer;
            }
            throw new RuntimeException("Failed to copy NativeBuffer to mutable instance!");
        } catch (Exception unused) {
            throw new RuntimeException("Unable to allocate a copy of " + getClass() + "! Make sure the class has a default constructor!");
        }
    }

    /* JADX WARN: Code duplicated, block: B:6:0x000e  */
    public NativeBuffer release() {
        boolean z = false;
        if (this.mAttachedFrame != null) {
            if (this.mAttachedFrame.release() == null) {
                z = true;
            }
        } else if (this.mOwnsData) {
            this.mRefCount--;
            if (this.mRefCount == 0) {
                z = true;
            }
        }
        if (!z) {
            return this;
        }
        deallocate(this.mOwnsData);
        return null;
    }

    public NativeBuffer retain() {
        if (this.mAttachedFrame != null) {
            this.mAttachedFrame.retain();
        } else if (this.mOwnsData) {
            this.mRefCount++;
        }
        return this;
    }

    public int size() {
        return this.mSize;
    }
}
