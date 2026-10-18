package android.filterfw.core;

import android.graphics.Bitmap;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public abstract class Frame {
    public static final int NO_BINDING = 0;
    public static final long TIMESTAMP_NOT_SET = -2;
    public static final long TIMESTAMP_UNKNOWN = -1;
    private long mBindingId;
    private int mBindingType;
    private FrameFormat mFormat;
    private FrameManager mFrameManager;
    private boolean mReadOnly;
    private int mRefCount;
    private boolean mReusable;
    private long mTimestamp;

    Frame(FrameFormat frameFormat, FrameManager frameManager) {
        this.mReadOnly = false;
        this.mReusable = false;
        this.mRefCount = 1;
        this.mBindingType = 0;
        this.mBindingId = 0L;
        this.mTimestamp = -2L;
        this.mFormat = frameFormat.mutableCopy();
        this.mFrameManager = frameManager;
    }

    Frame(FrameFormat frameFormat, FrameManager frameManager, int i, long j) {
        this.mReadOnly = false;
        this.mReusable = false;
        this.mRefCount = 1;
        this.mBindingType = 0;
        this.mBindingId = 0L;
        this.mTimestamp = -2L;
        this.mFormat = frameFormat.mutableCopy();
        this.mFrameManager = frameManager;
        this.mBindingType = i;
        this.mBindingId = j;
    }

    protected static Bitmap convertBitmapToRGBA(Bitmap bitmap) {
        if (bitmap.getConfig() == Bitmap.Config.ARGB_8888) {
            return bitmap;
        }
        Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.ARGB_8888, false);
        if (bitmapCopy == null) {
            throw new RuntimeException("Error converting bitmap to RGBA!");
        }
        if (bitmapCopy.getRowBytes() == bitmapCopy.getWidth() * 4) {
            return bitmapCopy;
        }
        throw new RuntimeException("Unsupported row byte count in bitmap!");
    }

    protected void assertFrameMutable() {
        if (isReadOnly()) {
            throw new RuntimeException("Attempting to modify read-only frame!");
        }
    }

    final int decRefCount() {
        this.mRefCount--;
        return this.mRefCount;
    }

    public long getBindingId() {
        return this.mBindingId;
    }

    public int getBindingType() {
        return this.mBindingType;
    }

    public abstract Bitmap getBitmap();

    public int getCapacity() {
        return getFormat().getSize();
    }

    public abstract ByteBuffer getData();

    public abstract float[] getFloats();

    public FrameFormat getFormat() {
        return this.mFormat;
    }

    public FrameManager getFrameManager() {
        return this.mFrameManager;
    }

    public abstract int[] getInts();

    public abstract Object getObjectValue();

    public int getRefCount() {
        return this.mRefCount;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    protected abstract boolean hasNativeAllocation();

    final int incRefCount() {
        this.mRefCount++;
        return this.mRefCount;
    }

    public boolean isReadOnly() {
        return this.mReadOnly;
    }

    final boolean isReusable() {
        return this.mReusable;
    }

    final void markReadOnly() {
        this.mReadOnly = true;
    }

    protected void onFrameFetch() {
    }

    protected void onFrameStore() {
    }

    public Frame release() {
        return this.mFrameManager != null ? this.mFrameManager.releaseFrame(this) : this;
    }

    protected abstract void releaseNativeAllocation();

    protected boolean requestResize(int[] iArr) {
        return false;
    }

    protected void reset(FrameFormat frameFormat) {
        this.mFormat = frameFormat.mutableCopy();
        this.mReadOnly = false;
        this.mRefCount = 1;
    }

    public Frame retain() {
        return this.mFrameManager != null ? this.mFrameManager.retainFrame(this) : this;
    }

    public abstract void setBitmap(Bitmap bitmap);

    public void setData(ByteBuffer byteBuffer) {
        setData(byteBuffer, 0, byteBuffer.limit());
    }

    public abstract void setData(ByteBuffer byteBuffer, int i, int i2);

    public void setData(byte[] bArr, int i, int i2) {
        setData(ByteBuffer.wrap(bArr, i, i2));
    }

    public void setDataFromFrame(Frame frame) {
        setData(frame.getData());
    }

    public abstract void setFloats(float[] fArr);

    protected void setFormat(FrameFormat frameFormat) {
        this.mFormat = frameFormat.mutableCopy();
    }

    protected void setGenericObjectValue(Object obj) {
        throw new RuntimeException("Cannot set object value of unsupported type: " + obj.getClass());
    }

    public abstract void setInts(int[] iArr);

    public void setObjectValue(Object obj) {
        assertFrameMutable();
        if (obj instanceof int[]) {
            setInts((int[]) obj);
            return;
        }
        if (obj instanceof float[]) {
            setFloats((float[]) obj);
            return;
        }
        if (obj instanceof ByteBuffer) {
            setData((ByteBuffer) obj);
        } else if (obj instanceof Bitmap) {
            setBitmap((Bitmap) obj);
        } else {
            setGenericObjectValue(obj);
        }
    }

    protected void setReusable(boolean z) {
        this.mReusable = z;
    }

    public void setTimestamp(long j) {
        this.mTimestamp = j;
    }
}
