package android.media;

import android.graphics.Rect;
import android.hardware.HardwareBuffer;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public abstract class Image implements AutoCloseable {
    private Rect mCropRect;
    protected boolean mIsImageValid = false;

    public static abstract class Plane {
        protected Plane() {
        }

        public abstract ByteBuffer getBuffer();

        public abstract int getPixelStride();

        public abstract int getRowStride();
    }

    protected Image() {
    }

    @Override // java.lang.AutoCloseable
    public abstract void close();

    public Rect getCropRect() {
        throwISEIfImageIsInvalid();
        return this.mCropRect == null ? new Rect(0, 0, getWidth(), getHeight()) : new Rect(this.mCropRect);
    }

    public abstract int getFormat();

    public HardwareBuffer getHardwareBuffer() {
        throwISEIfImageIsInvalid();
        return null;
    }

    public abstract int getHeight();

    long getNativeContext() {
        throwISEIfImageIsInvalid();
        return 0L;
    }

    Object getOwner() {
        throwISEIfImageIsInvalid();
        return null;
    }

    public abstract Plane[] getPlanes();

    public abstract int getScalingMode();

    public abstract long getTimestamp();

    public abstract int getTransform();

    public abstract int getWidth();

    boolean isAttachable() {
        throwISEIfImageIsInvalid();
        return false;
    }

    public void setCropRect(Rect rect) {
        throwISEIfImageIsInvalid();
        if (rect != null) {
            Rect rect2 = new Rect(rect);
            if (!rect2.intersect(0, 0, getWidth(), getHeight())) {
                rect2.setEmpty();
            }
            rect = rect2;
        }
        this.mCropRect = rect;
    }

    public void setTimestamp(long j) {
        throwISEIfImageIsInvalid();
    }

    protected void throwISEIfImageIsInvalid() {
        if (!this.mIsImageValid) {
            throw new IllegalStateException("Image is already closed");
        }
    }
}
