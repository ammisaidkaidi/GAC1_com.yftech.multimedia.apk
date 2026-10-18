package com.google.android.gles_jni;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL;

/* JADX INFO: loaded from: classes3.dex */
public class EGLContextImpl extends EGLContext {
    long mEGLContext;
    private GLImpl mGLContext = new GLImpl();

    public EGLContextImpl(long j) {
        this.mEGLContext = j;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.mEGLContext == ((EGLContextImpl) obj).mEGLContext;
    }

    @Override // javax.microedition.khronos.egl.EGLContext
    public GL getGL() {
        return this.mGLContext;
    }

    public int hashCode() {
        return 527 + ((int) (this.mEGLContext ^ (this.mEGLContext >>> 32)));
    }
}
