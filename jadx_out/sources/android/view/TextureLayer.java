package android.view;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import com.android.internal.util.VirtualRefBasePtr;

/* JADX INFO: loaded from: classes2.dex */
final class TextureLayer {
    private VirtualRefBasePtr mFinalizer;
    private ThreadedRenderer mRenderer;

    private TextureLayer(ThreadedRenderer threadedRenderer, long j) {
        if (threadedRenderer != null && j != 0) {
            this.mRenderer = threadedRenderer;
            this.mFinalizer = new VirtualRefBasePtr(j);
            return;
        }
        throw new IllegalArgumentException("Either hardware renderer: " + threadedRenderer + " or deferredUpdater: " + j + " is invalid");
    }

    static TextureLayer adoptTextureLayer(ThreadedRenderer threadedRenderer, long j) {
        return new TextureLayer(threadedRenderer, j);
    }

    private static native boolean nPrepare(long j, int i, int i2, boolean z);

    private static native void nSetLayerPaint(long j, long j2);

    private static native void nSetSurfaceTexture(long j, SurfaceTexture surfaceTexture);

    private static native void nSetTransform(long j, long j2);

    private static native void nUpdateSurfaceTexture(long j);

    public boolean copyInto(Bitmap bitmap) {
        return this.mRenderer.copyLayerInto(this, bitmap);
    }

    public void destroy() {
        if (isValid()) {
            this.mRenderer.onLayerDestroyed(this);
            this.mRenderer = null;
            this.mFinalizer.release();
            this.mFinalizer = null;
        }
    }

    public void detachSurfaceTexture() {
        this.mRenderer.detachSurfaceTexture(this.mFinalizer.get());
    }

    public long getDeferredLayerUpdater() {
        return this.mFinalizer.get();
    }

    public long getLayerHandle() {
        return this.mFinalizer.get();
    }

    public boolean isValid() {
        return (this.mFinalizer == null || this.mFinalizer.get() == 0) ? false : true;
    }

    public boolean prepare(int i, int i2, boolean z) {
        return nPrepare(this.mFinalizer.get(), i, i2, z);
    }

    public void setLayerPaint(Paint paint) {
        nSetLayerPaint(this.mFinalizer.get(), paint != null ? paint.getNativeInstance() : 0L);
        this.mRenderer.pushLayerUpdate(this);
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        nSetSurfaceTexture(this.mFinalizer.get(), surfaceTexture);
        this.mRenderer.pushLayerUpdate(this);
    }

    public void setTransform(Matrix matrix) {
        nSetTransform(this.mFinalizer.get(), matrix.native_instance);
        this.mRenderer.pushLayerUpdate(this);
    }

    public void updateSurfaceTexture() {
        nUpdateSurfaceTexture(this.mFinalizer.get());
        this.mRenderer.pushLayerUpdate(this);
    }
}
