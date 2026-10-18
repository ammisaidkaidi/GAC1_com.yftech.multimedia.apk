package android.graphics;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class SurfaceTexture {
    private final Looper mCreatorLooper;
    private long mFrameAvailableListener;
    private boolean mIsSingleBuffered;
    private Handler mOnFrameAvailableHandler;
    private long mProducer;
    private long mSurfaceTexture;

    public interface OnFrameAvailableListener {
        void onFrameAvailable(SurfaceTexture surfaceTexture);
    }

    @Deprecated
    public static class OutOfResourcesException extends Exception {
        public OutOfResourcesException() {
        }

        public OutOfResourcesException(String str) {
            super(str);
        }
    }

    public SurfaceTexture(int i) {
        this(i, false);
    }

    public SurfaceTexture(int i, boolean z) {
        this.mCreatorLooper = Looper.myLooper();
        this.mIsSingleBuffered = z;
        nativeInit(false, i, z, new WeakReference<>(this));
    }

    public SurfaceTexture(boolean z) {
        this.mCreatorLooper = Looper.myLooper();
        this.mIsSingleBuffered = z;
        nativeInit(true, 0, z, new WeakReference<>(this));
    }

    private native int nativeAttachToGLContext(int i);

    private native int nativeDetachFromGLContext();

    private native void nativeFinalize();

    private native long nativeGetTimestamp();

    private native void nativeGetTransformMatrix(float[] fArr);

    private native void nativeInit(boolean z, int i, boolean z2, WeakReference<SurfaceTexture> weakReference) throws Surface.OutOfResourcesException;

    private native boolean nativeIsReleased();

    private native void nativeRelease();

    private native void nativeReleaseTexImage();

    private native void nativeSetDefaultBufferSize(int i, int i2);

    private native void nativeUpdateTexImage();

    private static void postEventFromNative(WeakReference<SurfaceTexture> weakReference) {
        Handler handler;
        SurfaceTexture surfaceTexture = weakReference.get();
        if (surfaceTexture == null || (handler = surfaceTexture.mOnFrameAvailableHandler) == null) {
            return;
        }
        handler.sendEmptyMessage(0);
    }

    public void attachToGLContext(int i) {
        if (nativeAttachToGLContext(i) != 0) {
            throw new RuntimeException("Error during attachToGLContext (see logcat for details)");
        }
    }

    public void detachFromGLContext() {
        if (nativeDetachFromGLContext() != 0) {
            throw new RuntimeException("Error during detachFromGLContext (see logcat for details)");
        }
    }

    protected void finalize() throws Throwable {
        try {
            nativeFinalize();
        } finally {
            super.finalize();
        }
    }

    public long getTimestamp() {
        return nativeGetTimestamp();
    }

    public void getTransformMatrix(float[] fArr) {
        if (fArr.length != 16) {
            throw new IllegalArgumentException();
        }
        nativeGetTransformMatrix(fArr);
    }

    public boolean isReleased() {
        return nativeIsReleased();
    }

    public boolean isSingleBuffered() {
        return this.mIsSingleBuffered;
    }

    public void release() {
        nativeRelease();
    }

    public void releaseTexImage() {
        nativeReleaseTexImage();
    }

    public void setDefaultBufferSize(int i, int i2) {
        nativeSetDefaultBufferSize(i, i2);
    }

    public void setOnFrameAvailableListener(OnFrameAvailableListener onFrameAvailableListener) {
        setOnFrameAvailableListener(onFrameAvailableListener, null);
    }

    public void setOnFrameAvailableListener(final OnFrameAvailableListener onFrameAvailableListener, Handler handler) {
        Looper mainLooper;
        if (onFrameAvailableListener == null) {
            this.mOnFrameAvailableHandler = null;
            return;
        }
        if (handler != null) {
            mainLooper = handler.getLooper();
        } else {
            mainLooper = this.mCreatorLooper != null ? this.mCreatorLooper : Looper.getMainLooper();
        }
        this.mOnFrameAvailableHandler = new Handler(mainLooper, null, true) { // from class: android.graphics.SurfaceTexture.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                onFrameAvailableListener.onFrameAvailable(SurfaceTexture.this);
            }
        };
    }

    public void updateTexImage() {
        nativeUpdateTexImage();
    }
}
