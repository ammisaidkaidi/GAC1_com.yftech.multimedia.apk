package com.google.android.gles_jni;

import javax.microedition.khronos.egl.EGLDisplay;

/* JADX INFO: loaded from: classes3.dex */
public class EGLDisplayImpl extends EGLDisplay {
    long mEGLDisplay;

    public EGLDisplayImpl(long j) {
        this.mEGLDisplay = j;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.mEGLDisplay == ((EGLDisplayImpl) obj).mEGLDisplay;
    }

    public int hashCode() {
        return 527 + ((int) (this.mEGLDisplay ^ (this.mEGLDisplay >>> 32)));
    }
}
