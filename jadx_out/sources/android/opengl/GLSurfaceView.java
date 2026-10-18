package android.opengl;

import android.content.Context;
import android.os.Trace;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/* JADX INFO: loaded from: classes2.dex */
public class GLSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {
    public static final int DEBUG_CHECK_GL_ERROR = 1;
    public static final int DEBUG_LOG_GL_CALLS = 2;
    private static final boolean LOG_ATTACH_DETACH = false;
    private static final boolean LOG_EGL = false;
    private static final boolean LOG_PAUSE_RESUME = false;
    private static final boolean LOG_RENDERER = false;
    private static final boolean LOG_RENDERER_DRAW_FRAME = false;
    private static final boolean LOG_SURFACE = false;
    private static final boolean LOG_THREADS = false;
    public static final int RENDERMODE_CONTINUOUSLY = 1;
    public static final int RENDERMODE_WHEN_DIRTY = 0;
    private static final String TAG = "GLSurfaceView";
    private static final GLThreadManager sGLThreadManager = new GLThreadManager();
    private int mDebugFlags;
    private boolean mDetached;
    private EGLConfigChooser mEGLConfigChooser;
    private int mEGLContextClientVersion;
    private EGLContextFactory mEGLContextFactory;
    private EGLWindowSurfaceFactory mEGLWindowSurfaceFactory;
    private GLThread mGLThread;
    private GLWrapper mGLWrapper;
    private boolean mPreserveEGLContextOnPause;
    private Renderer mRenderer;
    private final WeakReference<GLSurfaceView> mThisWeakRef;

    private abstract class BaseConfigChooser implements EGLConfigChooser {
        protected int[] mConfigSpec;

        public BaseConfigChooser(int[] iArr) {
            this.mConfigSpec = filterConfigSpec(iArr);
        }

        private int[] filterConfigSpec(int[] iArr) {
            if (GLSurfaceView.this.mEGLContextClientVersion != 2 && GLSurfaceView.this.mEGLContextClientVersion != 3) {
                return iArr;
            }
            int length = iArr.length;
            int[] iArr2 = new int[length + 2];
            int i = length - 1;
            System.arraycopy(iArr, 0, iArr2, 0, i);
            iArr2[i] = 12352;
            if (GLSurfaceView.this.mEGLContextClientVersion == 2) {
                iArr2[length] = 4;
            } else {
                iArr2[length] = 64;
            }
            iArr2[length + 1] = 12344;
            return iArr2;
        }

        @Override // android.opengl.GLSurfaceView.EGLConfigChooser
        public javax.microedition.khronos.egl.EGLConfig chooseConfig(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay) {
            int[] iArr = new int[1];
            if (!egl10.eglChooseConfig(eGLDisplay, this.mConfigSpec, null, 0, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }
            int i = iArr[0];
            if (i <= 0) {
                throw new IllegalArgumentException("No configs match configSpec");
            }
            javax.microedition.khronos.egl.EGLConfig[] eGLConfigArr = new javax.microedition.khronos.egl.EGLConfig[i];
            if (!egl10.eglChooseConfig(eGLDisplay, this.mConfigSpec, eGLConfigArr, i, iArr)) {
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            javax.microedition.khronos.egl.EGLConfig eGLConfigChooseConfig = chooseConfig(egl10, eGLDisplay, eGLConfigArr);
            if (eGLConfigChooseConfig != null) {
                return eGLConfigChooseConfig;
            }
            throw new IllegalArgumentException("No config chosen");
        }

        abstract javax.microedition.khronos.egl.EGLConfig chooseConfig(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLConfig[] eGLConfigArr);
    }

    private class ComponentSizeChooser extends BaseConfigChooser {
        protected int mAlphaSize;
        protected int mBlueSize;
        protected int mDepthSize;
        protected int mGreenSize;
        protected int mRedSize;
        protected int mStencilSize;
        private int[] mValue;

        public ComponentSizeChooser(int i, int i2, int i3, int i4, int i5, int i6) {
            super(new int[]{12324, i, 12323, i2, 12322, i3, 12321, i4, 12325, i5, 12326, i6, 12344});
            this.mValue = new int[1];
            this.mRedSize = i;
            this.mGreenSize = i2;
            this.mBlueSize = i3;
            this.mAlphaSize = i4;
            this.mDepthSize = i5;
            this.mStencilSize = i6;
        }

        private int findConfigAttrib(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLConfig eGLConfig, int i, int i2) {
            return egl10.eglGetConfigAttrib(eGLDisplay, eGLConfig, i, this.mValue) ? this.mValue[0] : i2;
        }

        @Override // android.opengl.GLSurfaceView.BaseConfigChooser
        public javax.microedition.khronos.egl.EGLConfig chooseConfig(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLConfig[] eGLConfigArr) {
            for (javax.microedition.khronos.egl.EGLConfig eGLConfig : eGLConfigArr) {
                int iFindConfigAttrib = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12325, 0);
                int iFindConfigAttrib2 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12326, 0);
                if (iFindConfigAttrib >= this.mDepthSize && iFindConfigAttrib2 >= this.mStencilSize) {
                    int iFindConfigAttrib3 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12324, 0);
                    int iFindConfigAttrib4 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12323, 0);
                    int iFindConfigAttrib5 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12322, 0);
                    int iFindConfigAttrib6 = findConfigAttrib(egl10, eGLDisplay, eGLConfig, 12321, 0);
                    if (iFindConfigAttrib3 == this.mRedSize && iFindConfigAttrib4 == this.mGreenSize && iFindConfigAttrib5 == this.mBlueSize && iFindConfigAttrib6 == this.mAlphaSize) {
                        return eGLConfig;
                    }
                }
            }
            return null;
        }
    }

    private class DefaultContextFactory implements EGLContextFactory {
        private int EGL_CONTEXT_CLIENT_VERSION;

        private DefaultContextFactory() {
            this.EGL_CONTEXT_CLIENT_VERSION = 12440;
        }

        @Override // android.opengl.GLSurfaceView.EGLContextFactory
        public javax.microedition.khronos.egl.EGLContext createContext(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLConfig eGLConfig) {
            int[] iArr = {this.EGL_CONTEXT_CLIENT_VERSION, GLSurfaceView.this.mEGLContextClientVersion, 12344};
            javax.microedition.khronos.egl.EGLContext eGLContext = EGL10.EGL_NO_CONTEXT;
            if (GLSurfaceView.this.mEGLContextClientVersion == 0) {
                iArr = null;
            }
            return egl10.eglCreateContext(eGLDisplay, eGLConfig, eGLContext, iArr);
        }

        @Override // android.opengl.GLSurfaceView.EGLContextFactory
        public void destroyContext(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLContext eGLContext) {
            if (egl10.eglDestroyContext(eGLDisplay, eGLContext)) {
                return;
            }
            Log.e("DefaultContextFactory", "display:" + eGLDisplay + " context: " + eGLContext);
            EglHelper.throwEglException("eglDestroyContex", egl10.eglGetError());
        }
    }

    private static class DefaultWindowSurfaceFactory implements EGLWindowSurfaceFactory {
        private DefaultWindowSurfaceFactory() {
        }

        @Override // android.opengl.GLSurfaceView.EGLWindowSurfaceFactory
        public javax.microedition.khronos.egl.EGLSurface createWindowSurface(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLConfig eGLConfig, Object obj) {
            try {
                return egl10.eglCreateWindowSurface(eGLDisplay, eGLConfig, obj, null);
            } catch (IllegalArgumentException e) {
                Log.e(GLSurfaceView.TAG, "eglCreateWindowSurface", e);
                return null;
            }
        }

        @Override // android.opengl.GLSurfaceView.EGLWindowSurfaceFactory
        public void destroySurface(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLSurface eGLSurface) {
            egl10.eglDestroySurface(eGLDisplay, eGLSurface);
        }
    }

    public interface EGLConfigChooser {
        javax.microedition.khronos.egl.EGLConfig chooseConfig(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay);
    }

    public interface EGLContextFactory {
        javax.microedition.khronos.egl.EGLContext createContext(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLConfig eGLConfig);

        void destroyContext(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLContext eGLContext);
    }

    public interface EGLWindowSurfaceFactory {
        javax.microedition.khronos.egl.EGLSurface createWindowSurface(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLConfig eGLConfig, Object obj);

        void destroySurface(EGL10 egl10, javax.microedition.khronos.egl.EGLDisplay eGLDisplay, javax.microedition.khronos.egl.EGLSurface eGLSurface);
    }

    private static class EglHelper {
        EGL10 mEgl;
        javax.microedition.khronos.egl.EGLConfig mEglConfig;
        javax.microedition.khronos.egl.EGLContext mEglContext;
        javax.microedition.khronos.egl.EGLDisplay mEglDisplay;
        javax.microedition.khronos.egl.EGLSurface mEglSurface;
        private WeakReference<GLSurfaceView> mGLSurfaceViewWeakRef;

        public EglHelper(WeakReference<GLSurfaceView> weakReference) {
            this.mGLSurfaceViewWeakRef = weakReference;
        }

        private void destroySurfaceImp() {
            if (this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE) {
                return;
            }
            this.mEgl.eglMakeCurrent(this.mEglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
            GLSurfaceView gLSurfaceView = this.mGLSurfaceViewWeakRef.get();
            if (gLSurfaceView != null) {
                gLSurfaceView.mEGLWindowSurfaceFactory.destroySurface(this.mEgl, this.mEglDisplay, this.mEglSurface);
            }
            this.mEglSurface = null;
        }

        public static String formatEglError(String str, int i) {
            return str + " failed: " + EGLLogWrapper.getErrorString(i);
        }

        public static void logEglErrorAsWarning(String str, String str2, int i) {
            Log.w(str, formatEglError(str2, i));
        }

        private void throwEglException(String str) {
            throwEglException(str, this.mEgl.eglGetError());
        }

        public static void throwEglException(String str, int i) {
            throw new RuntimeException(formatEglError(str, i));
        }

        GL createGL() {
            GL gl = this.mEglContext.getGL();
            GLSurfaceView gLSurfaceView = this.mGLSurfaceViewWeakRef.get();
            if (gLSurfaceView == null) {
                return gl;
            }
            if (gLSurfaceView.mGLWrapper != null) {
                gl = gLSurfaceView.mGLWrapper.wrap(gl);
            }
            if ((gLSurfaceView.mDebugFlags & 3) != 0) {
                return GLDebugHelper.wrap(gl, (gLSurfaceView.mDebugFlags & 1) != 0 ? 1 : 0, (gLSurfaceView.mDebugFlags & 2) != 0 ? new LogWriter() : null);
            }
            return gl;
        }

        public boolean createSurface() {
            if (this.mEgl == null) {
                throw new RuntimeException("egl not initialized");
            }
            if (this.mEglDisplay == null) {
                throw new RuntimeException("eglDisplay not initialized");
            }
            if (this.mEglConfig == null) {
                throw new RuntimeException("mEglConfig not initialized");
            }
            destroySurfaceImp();
            GLSurfaceView gLSurfaceView = this.mGLSurfaceViewWeakRef.get();
            if (gLSurfaceView != null) {
                this.mEglSurface = gLSurfaceView.mEGLWindowSurfaceFactory.createWindowSurface(this.mEgl, this.mEglDisplay, this.mEglConfig, gLSurfaceView.getHolder());
            } else {
                this.mEglSurface = null;
            }
            if (this.mEglSurface == null || this.mEglSurface == EGL10.EGL_NO_SURFACE) {
                if (this.mEgl.eglGetError() == 12299) {
                    Log.e("EglHelper", "createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
                }
                return false;
            }
            if (this.mEgl.eglMakeCurrent(this.mEglDisplay, this.mEglSurface, this.mEglSurface, this.mEglContext)) {
                return true;
            }
            logEglErrorAsWarning("EGLHelper", "eglMakeCurrent", this.mEgl.eglGetError());
            return false;
        }

        public void destroySurface() {
            destroySurfaceImp();
        }

        public void finish() {
            if (this.mEglContext != null) {
                GLSurfaceView gLSurfaceView = this.mGLSurfaceViewWeakRef.get();
                if (gLSurfaceView != null) {
                    gLSurfaceView.mEGLContextFactory.destroyContext(this.mEgl, this.mEglDisplay, this.mEglContext);
                }
                this.mEglContext = null;
            }
            if (this.mEglDisplay != null) {
                this.mEgl.eglTerminate(this.mEglDisplay);
                this.mEglDisplay = null;
            }
        }

        public void start() {
            this.mEgl = (EGL10) javax.microedition.khronos.egl.EGLContext.getEGL();
            this.mEglDisplay = this.mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
            if (this.mEglDisplay == EGL10.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed");
            }
            if (!this.mEgl.eglInitialize(this.mEglDisplay, new int[2])) {
                throw new RuntimeException("eglInitialize failed");
            }
            GLSurfaceView gLSurfaceView = this.mGLSurfaceViewWeakRef.get();
            if (gLSurfaceView == null) {
                this.mEglConfig = null;
                this.mEglContext = null;
            } else {
                this.mEglConfig = gLSurfaceView.mEGLConfigChooser.chooseConfig(this.mEgl, this.mEglDisplay);
                this.mEglContext = gLSurfaceView.mEGLContextFactory.createContext(this.mEgl, this.mEglDisplay, this.mEglConfig);
            }
            if (this.mEglContext == null || this.mEglContext == EGL10.EGL_NO_CONTEXT) {
                this.mEglContext = null;
                throwEglException("createContext");
            }
            this.mEglSurface = null;
        }

        public int swap() {
            if (this.mEgl.eglSwapBuffers(this.mEglDisplay, this.mEglSurface)) {
                return 12288;
            }
            return this.mEgl.eglGetError();
        }
    }

    static class GLThread extends Thread {
        private EglHelper mEglHelper;
        private boolean mExited;
        private boolean mFinishedCreatingEglSurface;
        private WeakReference<GLSurfaceView> mGLSurfaceViewWeakRef;
        private boolean mHasSurface;
        private boolean mHaveEglContext;
        private boolean mHaveEglSurface;
        private boolean mPaused;
        private boolean mRenderComplete;
        private boolean mRequestPaused;
        private boolean mShouldExit;
        private boolean mShouldReleaseEglContext;
        private boolean mSurfaceIsBad;
        private boolean mWaitingForSurface;
        private ArrayList<Runnable> mEventQueue = new ArrayList<>();
        private boolean mSizeChanged = true;
        private Runnable mFinishDrawingRunnable = null;
        private int mWidth = 0;
        private int mHeight = 0;
        private boolean mRequestRender = true;
        private int mRenderMode = 1;
        private boolean mWantRenderNotification = false;

        GLThread(WeakReference<GLSurfaceView> weakReference) {
            this.mGLSurfaceViewWeakRef = weakReference;
        }

        /* JADX WARN: Code duplicated, block: B:112:0x018c A[Catch: all -> 0x0264, TryCatch #3 {all -> 0x0264, blocks: (B:3:0x001f, B:4:0x0023, B:88:0x0147, B:90:0x0154, B:92:0x015c, B:93:0x0160, B:100:0x0170, B:101:0x0171, B:102:0x0175, B:110:0x0189, B:112:0x018c, B:116:0x019f, B:119:0x01b9, B:121:0x01be, B:122:0x01c1, B:125:0x01c5, B:128:0x01db, B:130:0x01e0, B:131:0x01e3, B:133:0x01e5, B:140:0x0203, B:145:0x020e, B:149:0x021d, B:150:0x0228, B:157:0x0237, B:142:0x0208, B:143:0x020b, B:170:0x0263, B:5:0x0024, B:7:0x0028, B:16:0x0039, B:18:0x0041, B:86:0x0144, B:19:0x004f, B:21:0x0055, B:23:0x0064, B:25:0x0068, B:27:0x0074, B:29:0x007d, B:31:0x0081, B:33:0x0086, B:35:0x008a, B:40:0x009c, B:38:0x0096, B:41:0x009f, B:43:0x00a3, B:45:0x00a7, B:47:0x00ab, B:48:0x00ae, B:49:0x00bb, B:51:0x00bf, B:53:0x00c3, B:55:0x00cf, B:56:0x00dd, B:58:0x00e1, B:60:0x00e9, B:62:0x00ef, B:66:0x00f7, B:68:0x00fd, B:70:0x0109, B:71:0x0110, B:72:0x0111, B:74:0x0115, B:76:0x0119, B:77:0x011f, B:79:0x0123, B:81:0x0127, B:83:0x0136, B:167:0x0257, B:165:0x024a, B:152:0x022a, B:153:0x0233, B:136:0x01f1, B:138:0x01ff, B:104:0x0177, B:105:0x0182, B:118:0x01a9, B:95:0x0162, B:96:0x016b, B:127:0x01cf), top: B:185:0x001f, inners: #0, #4, #6, #7, #8, #9, #10 }] */
        /* JADX WARN: Code duplicated, block: B:113:0x0199  */
        /* JADX WARN: Code duplicated, block: B:116:0x019f A[Catch: all -> 0x0264, TRY_LEAVE, TryCatch #3 {all -> 0x0264, blocks: (B:3:0x001f, B:4:0x0023, B:88:0x0147, B:90:0x0154, B:92:0x015c, B:93:0x0160, B:100:0x0170, B:101:0x0171, B:102:0x0175, B:110:0x0189, B:112:0x018c, B:116:0x019f, B:119:0x01b9, B:121:0x01be, B:122:0x01c1, B:125:0x01c5, B:128:0x01db, B:130:0x01e0, B:131:0x01e3, B:133:0x01e5, B:140:0x0203, B:145:0x020e, B:149:0x021d, B:150:0x0228, B:157:0x0237, B:142:0x0208, B:143:0x020b, B:170:0x0263, B:5:0x0024, B:7:0x0028, B:16:0x0039, B:18:0x0041, B:86:0x0144, B:19:0x004f, B:21:0x0055, B:23:0x0064, B:25:0x0068, B:27:0x0074, B:29:0x007d, B:31:0x0081, B:33:0x0086, B:35:0x008a, B:40:0x009c, B:38:0x0096, B:41:0x009f, B:43:0x00a3, B:45:0x00a7, B:47:0x00ab, B:48:0x00ae, B:49:0x00bb, B:51:0x00bf, B:53:0x00c3, B:55:0x00cf, B:56:0x00dd, B:58:0x00e1, B:60:0x00e9, B:62:0x00ef, B:66:0x00f7, B:68:0x00fd, B:70:0x0109, B:71:0x0110, B:72:0x0111, B:74:0x0115, B:76:0x0119, B:77:0x011f, B:79:0x0123, B:81:0x0127, B:83:0x0136, B:167:0x0257, B:165:0x024a, B:152:0x022a, B:153:0x0233, B:136:0x01f1, B:138:0x01ff, B:104:0x0177, B:105:0x0182, B:118:0x01a9, B:95:0x0162, B:96:0x016b, B:127:0x01cf), top: B:185:0x001f, inners: #0, #4, #6, #7, #8, #9, #10 }] */
        /* JADX WARN: Code duplicated, block: B:125:0x01c5 A[Catch: all -> 0x0264, TRY_LEAVE, TryCatch #3 {all -> 0x0264, blocks: (B:3:0x001f, B:4:0x0023, B:88:0x0147, B:90:0x0154, B:92:0x015c, B:93:0x0160, B:100:0x0170, B:101:0x0171, B:102:0x0175, B:110:0x0189, B:112:0x018c, B:116:0x019f, B:119:0x01b9, B:121:0x01be, B:122:0x01c1, B:125:0x01c5, B:128:0x01db, B:130:0x01e0, B:131:0x01e3, B:133:0x01e5, B:140:0x0203, B:145:0x020e, B:149:0x021d, B:150:0x0228, B:157:0x0237, B:142:0x0208, B:143:0x020b, B:170:0x0263, B:5:0x0024, B:7:0x0028, B:16:0x0039, B:18:0x0041, B:86:0x0144, B:19:0x004f, B:21:0x0055, B:23:0x0064, B:25:0x0068, B:27:0x0074, B:29:0x007d, B:31:0x0081, B:33:0x0086, B:35:0x008a, B:40:0x009c, B:38:0x0096, B:41:0x009f, B:43:0x00a3, B:45:0x00a7, B:47:0x00ab, B:48:0x00ae, B:49:0x00bb, B:51:0x00bf, B:53:0x00c3, B:55:0x00cf, B:56:0x00dd, B:58:0x00e1, B:60:0x00e9, B:62:0x00ef, B:66:0x00f7, B:68:0x00fd, B:70:0x0109, B:71:0x0110, B:72:0x0111, B:74:0x0115, B:76:0x0119, B:77:0x011f, B:79:0x0123, B:81:0x0127, B:83:0x0136, B:167:0x0257, B:165:0x024a, B:152:0x022a, B:153:0x0233, B:136:0x01f1, B:138:0x01ff, B:104:0x0177, B:105:0x0182, B:118:0x01a9, B:95:0x0162, B:96:0x016b, B:127:0x01cf), top: B:185:0x001f, inners: #0, #4, #6, #7, #8, #9, #10 }] */
        /* JADX WARN: Code duplicated, block: B:135:0x01ef  */
        /* JADX WARN: Code duplicated, block: B:138:0x01ff A[Catch: all -> 0x0207, TRY_LEAVE, TryCatch #6 {all -> 0x0207, blocks: (B:136:0x01f1, B:138:0x01ff), top: B:190:0x01f1, outer: #3 }] */
        /* JADX WARN: Code duplicated, block: B:144:0x020c  */
        /* JADX WARN: Code duplicated, block: B:147:0x0219  */
        /* JADX WARN: Code duplicated, block: B:149:0x021d A[Catch: all -> 0x0264, TryCatch #3 {all -> 0x0264, blocks: (B:3:0x001f, B:4:0x0023, B:88:0x0147, B:90:0x0154, B:92:0x015c, B:93:0x0160, B:100:0x0170, B:101:0x0171, B:102:0x0175, B:110:0x0189, B:112:0x018c, B:116:0x019f, B:119:0x01b9, B:121:0x01be, B:122:0x01c1, B:125:0x01c5, B:128:0x01db, B:130:0x01e0, B:131:0x01e3, B:133:0x01e5, B:140:0x0203, B:145:0x020e, B:149:0x021d, B:150:0x0228, B:157:0x0237, B:142:0x0208, B:143:0x020b, B:170:0x0263, B:5:0x0024, B:7:0x0028, B:16:0x0039, B:18:0x0041, B:86:0x0144, B:19:0x004f, B:21:0x0055, B:23:0x0064, B:25:0x0068, B:27:0x0074, B:29:0x007d, B:31:0x0081, B:33:0x0086, B:35:0x008a, B:40:0x009c, B:38:0x0096, B:41:0x009f, B:43:0x00a3, B:45:0x00a7, B:47:0x00ab, B:48:0x00ae, B:49:0x00bb, B:51:0x00bf, B:53:0x00c3, B:55:0x00cf, B:56:0x00dd, B:58:0x00e1, B:60:0x00e9, B:62:0x00ef, B:66:0x00f7, B:68:0x00fd, B:70:0x0109, B:71:0x0110, B:72:0x0111, B:74:0x0115, B:76:0x0119, B:77:0x011f, B:79:0x0123, B:81:0x0127, B:83:0x0136, B:167:0x0257, B:165:0x024a, B:152:0x022a, B:153:0x0233, B:136:0x01f1, B:138:0x01ff, B:104:0x0177, B:105:0x0182, B:118:0x01a9, B:95:0x0162, B:96:0x016b, B:127:0x01cf), top: B:185:0x001f, inners: #0, #4, #6, #7, #8, #9, #10 }] */
        /* JADX WARN: Code duplicated, block: B:151:0x0229  */
        /* JADX WARN: Code duplicated, block: B:158:0x0238  */
        /* JADX WARN: Code duplicated, block: B:159:0x023b  */
        /* JADX WARN: Code duplicated, block: B:162:0x0240  */
        /* JADX WARN: Code duplicated, block: B:163:0x0244  */
        /* JADX WARN: Code duplicated, block: B:181:0x026a A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:194:0x01a9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        /* JADX WARN: Code duplicated, block: B:198:0x01cf A[EXC_TOP_SPLITTER, SYNTHETIC] */
        private void guardedRun() throws InterruptedException {
            Runnable runnable;
            boolean z;
            GLSurfaceView gLSurfaceView;
            int iSwap;
            boolean z2;
            GLSurfaceView gLSurfaceView2;
            GLSurfaceView gLSurfaceView3;
            boolean z3;
            this.mEglHelper = new EglHelper(this.mGLSurfaceViewWeakRef);
            this.mHaveEglContext = false;
            this.mHaveEglSurface = false;
            this.mWantRenderNotification = false;
            boolean z4 = false;
            boolean z5 = false;
            boolean z6 = false;
            boolean z7 = false;
            boolean z8 = false;
            boolean z9 = false;
            int i = 0;
            int i2 = 0;
            boolean z10 = false;
            boolean z11 = false;
            Runnable runnable2 = null;
            GL10 gl10 = null;
            Runnable runnableRemove = null;
            while (true) {
                try {
                    synchronized (GLSurfaceView.sGLThreadManager) {
                        while (true) {
                            if (this.mShouldExit) {
                                synchronized (GLSurfaceView.sGLThreadManager) {
                                    stopEglSurfaceLocked();
                                    stopEglContextLocked();
                                }
                                return;
                            }
                            if (!this.mEventQueue.isEmpty()) {
                                runnableRemove = this.mEventQueue.remove(0);
                                runnable = runnable2;
                                z = false;
                                break;
                            }
                            if (this.mPaused != this.mRequestPaused) {
                                z3 = this.mRequestPaused;
                                this.mPaused = this.mRequestPaused;
                                GLSurfaceView.sGLThreadManager.notifyAll();
                            } else {
                                z3 = false;
                            }
                            if (this.mShouldReleaseEglContext) {
                                stopEglSurfaceLocked();
                                stopEglContextLocked();
                                this.mShouldReleaseEglContext = false;
                                z6 = true;
                            }
                            if (z4) {
                                stopEglSurfaceLocked();
                                stopEglContextLocked();
                                z4 = false;
                            }
                            if (z3 && this.mHaveEglSurface) {
                                stopEglSurfaceLocked();
                            }
                            if (z3 && this.mHaveEglContext) {
                                GLSurfaceView gLSurfaceView4 = this.mGLSurfaceViewWeakRef.get();
                                if (!(gLSurfaceView4 == null ? false : gLSurfaceView4.mPreserveEGLContextOnPause)) {
                                    stopEglContextLocked();
                                }
                            }
                            if (!this.mHasSurface && !this.mWaitingForSurface) {
                                if (this.mHaveEglSurface) {
                                    stopEglSurfaceLocked();
                                }
                                this.mWaitingForSurface = true;
                                this.mSurfaceIsBad = false;
                                GLSurfaceView.sGLThreadManager.notifyAll();
                            }
                            if (this.mHasSurface && this.mWaitingForSurface) {
                                this.mWaitingForSurface = false;
                                GLSurfaceView.sGLThreadManager.notifyAll();
                            }
                            if (z5) {
                                this.mWantRenderNotification = false;
                                this.mRenderComplete = true;
                                GLSurfaceView.sGLThreadManager.notifyAll();
                                z5 = false;
                            }
                            if (this.mFinishDrawingRunnable != null) {
                                runnable = this.mFinishDrawingRunnable;
                                this.mFinishDrawingRunnable = null;
                            } else {
                                runnable = runnable2;
                            }
                            if (readyToDraw()) {
                                if (!this.mHaveEglContext) {
                                    if (z6) {
                                        z6 = false;
                                    } else {
                                        try {
                                            this.mEglHelper.start();
                                            this.mHaveEglContext = true;
                                            GLSurfaceView.sGLThreadManager.notifyAll();
                                            z10 = true;
                                        } catch (RuntimeException e) {
                                            GLSurfaceView.sGLThreadManager.releaseEglContextLocked(this);
                                            throw e;
                                        }
                                    }
                                }
                                if (this.mHaveEglContext && !this.mHaveEglSurface) {
                                    this.mHaveEglSurface = true;
                                    z7 = true;
                                    z8 = true;
                                    z11 = true;
                                }
                                if (this.mHaveEglSurface) {
                                    if (this.mSizeChanged) {
                                        i = this.mWidth;
                                        i2 = this.mHeight;
                                        this.mWantRenderNotification = true;
                                        this.mSizeChanged = false;
                                        z = false;
                                        z8 = true;
                                        z11 = true;
                                    } else {
                                        z = false;
                                    }
                                    this.mRequestRender = z;
                                    GLSurfaceView.sGLThreadManager.notifyAll();
                                    if (!this.mWantRenderNotification) {
                                        break;
                                    }
                                    z9 = true;
                                    break;
                                }
                            } else {
                                if (runnable != null) {
                                    Log.w(GLSurfaceView.TAG, "Warning, !readyToDraw() but waiting for draw finished! Early reporting draw finished.");
                                    runnable.run();
                                    runnable2 = null;
                                }
                                GLSurfaceView.sGLThreadManager.wait();
                            }
                            runnable2 = runnable;
                            GLSurfaceView.sGLThreadManager.wait();
                        }
                    }
                    if (runnableRemove != null) {
                        runnableRemove.run();
                        runnableRemove = null;
                        runnable2 = runnable;
                    } else {
                        if (z11) {
                            if (this.mEglHelper.createSurface()) {
                                synchronized (GLSurfaceView.sGLThreadManager) {
                                    this.mFinishedCreatingEglSurface = true;
                                    GLSurfaceView.sGLThreadManager.notifyAll();
                                }
                                z11 = z;
                                if (z7) {
                                    gl10 = (GL10) this.mEglHelper.createGL();
                                    z7 = false;
                                }
                                if (z10) {
                                    gLSurfaceView3 = this.mGLSurfaceViewWeakRef.get();
                                    if (gLSurfaceView3 != null) {
                                        Trace.traceBegin(8L, "onSurfaceCreated");
                                        gLSurfaceView3.mRenderer.onSurfaceCreated(gl10, this.mEglHelper.mEglConfig);
                                        Trace.traceEnd(8L);
                                    }
                                    z10 = false;
                                }
                                if (z8) {
                                    gLSurfaceView2 = this.mGLSurfaceViewWeakRef.get();
                                    if (gLSurfaceView2 != null) {
                                        Trace.traceBegin(8L, "onSurfaceChanged");
                                        gLSurfaceView2.mRenderer.onSurfaceChanged(gl10, i, i2);
                                        Trace.traceEnd(8L);
                                    }
                                    z8 = false;
                                }
                                gLSurfaceView = this.mGLSurfaceViewWeakRef.get();
                                if (gLSurfaceView != null) {
                                    Trace.traceBegin(8L, "onDrawFrame");
                                    gLSurfaceView.mRenderer.onDrawFrame(gl10);
                                    if (runnable != null) {
                                        runnable.run();
                                        runnable = null;
                                    }
                                    Trace.traceEnd(8L);
                                }
                                runnable2 = runnable;
                                iSwap = this.mEglHelper.swap();
                                if (iSwap != 12288) {
                                    if (iSwap != 12302) {
                                        EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", iSwap);
                                        synchronized (GLSurfaceView.sGLThreadManager) {
                                            z2 = true;
                                            this.mSurfaceIsBad = true;
                                            GLSurfaceView.sGLThreadManager.notifyAll();
                                        }
                                    } else {
                                        z2 = true;
                                        z4 = true;
                                    }
                                    if (z9) {
                                        z5 = z2;
                                        z9 = false;
                                    } else {
                                        z5 = z5;
                                    }
                                } else {
                                    z2 = true;
                                }
                                z4 = z4;
                                if (z9) {
                                    z5 = z2;
                                    z9 = false;
                                } else {
                                    z5 = z5;
                                }
                            } else {
                                synchronized (GLSurfaceView.sGLThreadManager) {
                                    this.mFinishedCreatingEglSurface = true;
                                    this.mSurfaceIsBad = true;
                                    GLSurfaceView.sGLThreadManager.notifyAll();
                                }
                                runnable2 = runnable;
                            }
                            synchronized (GLSurfaceView.sGLThreadManager) {
                                stopEglSurfaceLocked();
                                stopEglContextLocked();
                                throw th;
                            }
                        }
                        if (z7) {
                            gl10 = (GL10) this.mEglHelper.createGL();
                            z7 = false;
                        }
                        if (z10) {
                            gLSurfaceView3 = this.mGLSurfaceViewWeakRef.get();
                            if (gLSurfaceView3 != null) {
                                try {
                                    Trace.traceBegin(8L, "onSurfaceCreated");
                                    gLSurfaceView3.mRenderer.onSurfaceCreated(gl10, this.mEglHelper.mEglConfig);
                                    Trace.traceEnd(8L);
                                } catch (Throwable th) {
                                    Trace.traceEnd(8L);
                                    throw th;
                                }
                            }
                            z10 = false;
                        }
                        if (z8) {
                            gLSurfaceView2 = this.mGLSurfaceViewWeakRef.get();
                            if (gLSurfaceView2 != null) {
                                try {
                                    Trace.traceBegin(8L, "onSurfaceChanged");
                                    gLSurfaceView2.mRenderer.onSurfaceChanged(gl10, i, i2);
                                    Trace.traceEnd(8L);
                                } catch (Throwable th2) {
                                    Trace.traceEnd(8L);
                                    throw th2;
                                }
                            }
                            z8 = false;
                        }
                        gLSurfaceView = this.mGLSurfaceViewWeakRef.get();
                        if (gLSurfaceView != null) {
                            try {
                                Trace.traceBegin(8L, "onDrawFrame");
                                gLSurfaceView.mRenderer.onDrawFrame(gl10);
                                if (runnable != null) {
                                    runnable.run();
                                    runnable = null;
                                }
                                Trace.traceEnd(8L);
                            } catch (Throwable th3) {
                                Trace.traceEnd(8L);
                                throw th3;
                            }
                        }
                        runnable2 = runnable;
                        iSwap = this.mEglHelper.swap();
                        if (iSwap != 12288) {
                            if (iSwap != 12302) {
                                EglHelper.logEglErrorAsWarning("GLThread", "eglSwapBuffers", iSwap);
                                synchronized (GLSurfaceView.sGLThreadManager) {
                                    z2 = true;
                                    this.mSurfaceIsBad = true;
                                    GLSurfaceView.sGLThreadManager.notifyAll();
                                }
                            } else {
                                z2 = true;
                                z4 = true;
                            }
                            if (z9) {
                                z5 = z2;
                                z9 = false;
                            } else {
                                z5 = z5;
                            }
                        } else {
                            z2 = true;
                        }
                        z4 = z4;
                        if (z9) {
                            z5 = z2;
                            z9 = false;
                        } else {
                            z5 = z5;
                        }
                    }
                } catch (Throwable th4) {
                    synchronized (GLSurfaceView.sGLThreadManager) {
                        stopEglSurfaceLocked();
                        stopEglContextLocked();
                        throw th4;
                    }
                }
            }
        }

        private boolean readyToDraw() {
            return !this.mPaused && this.mHasSurface && !this.mSurfaceIsBad && this.mWidth > 0 && this.mHeight > 0 && (this.mRequestRender || this.mRenderMode == 1);
        }

        private void stopEglContextLocked() {
            if (this.mHaveEglContext) {
                this.mEglHelper.finish();
                this.mHaveEglContext = false;
                GLSurfaceView.sGLThreadManager.releaseEglContextLocked(this);
            }
        }

        private void stopEglSurfaceLocked() {
            if (this.mHaveEglSurface) {
                this.mHaveEglSurface = false;
                this.mEglHelper.destroySurface();
            }
        }

        public boolean ableToDraw() {
            return this.mHaveEglContext && this.mHaveEglSurface && readyToDraw();
        }

        public int getRenderMode() {
            int i;
            synchronized (GLSurfaceView.sGLThreadManager) {
                i = this.mRenderMode;
            }
            return i;
        }

        public void onPause() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mRequestPaused = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onResume() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mRequestPaused = false;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited && this.mPaused && !this.mRenderComplete) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void onWindowResize(int i, int i2) {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mWidth = i;
                this.mHeight = i2;
                this.mSizeChanged = true;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                if (Thread.currentThread() == this) {
                    return;
                }
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited && !this.mPaused && !this.mRenderComplete && ableToDraw()) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void queueEvent(Runnable runnable) {
            if (runnable == null) {
                throw new IllegalArgumentException("r must not be null");
            }
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mEventQueue.add(runnable);
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void requestExitAndWait() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mShouldExit = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mExited) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void requestReleaseEglContextLocked() {
            this.mShouldReleaseEglContext = true;
            GLSurfaceView.sGLThreadManager.notifyAll();
        }

        public void requestRender() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mRequestRender = true;
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void requestRenderAndNotify(Runnable runnable) {
            synchronized (GLSurfaceView.sGLThreadManager) {
                if (Thread.currentThread() == this) {
                    return;
                }
                this.mWantRenderNotification = true;
                this.mRequestRender = true;
                this.mRenderComplete = false;
                this.mFinishDrawingRunnable = runnable;
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            setName("GLThread " + getId());
            try {
                guardedRun();
            } catch (InterruptedException unused) {
            } finally {
                GLSurfaceView.sGLThreadManager.threadExiting(this);
            }
        }

        public void setRenderMode(int i) {
            if (i < 0 || i > 1) {
                throw new IllegalArgumentException("renderMode");
            }
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mRenderMode = i;
                GLSurfaceView.sGLThreadManager.notifyAll();
            }
        }

        public void surfaceCreated() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mHasSurface = true;
                this.mFinishedCreatingEglSurface = false;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (this.mWaitingForSurface && !this.mFinishedCreatingEglSurface && !this.mExited) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        public void surfaceDestroyed() {
            synchronized (GLSurfaceView.sGLThreadManager) {
                this.mHasSurface = false;
                GLSurfaceView.sGLThreadManager.notifyAll();
                while (!this.mWaitingForSurface && !this.mExited) {
                    try {
                        GLSurfaceView.sGLThreadManager.wait();
                    } catch (InterruptedException unused) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
    }

    private static class GLThreadManager {
        private static String TAG = "GLThreadManager";

        private GLThreadManager() {
        }

        public void releaseEglContextLocked(GLThread gLThread) {
            notifyAll();
        }

        public synchronized void threadExiting(GLThread gLThread) {
            gLThread.mExited = true;
            notifyAll();
        }
    }

    public interface GLWrapper {
        GL wrap(GL gl);
    }

    static class LogWriter extends Writer {
        private StringBuilder mBuilder = new StringBuilder();

        LogWriter() {
        }

        private void flushBuilder() {
            if (this.mBuilder.length() > 0) {
                Log.v(GLSurfaceView.TAG, this.mBuilder.toString());
                this.mBuilder.delete(0, this.mBuilder.length());
            }
        }

        @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            flushBuilder();
        }

        @Override // java.io.Writer, java.io.Flushable
        public void flush() {
            flushBuilder();
        }

        @Override // java.io.Writer
        public void write(char[] cArr, int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                char c = cArr[i + i3];
                if (c == '\n') {
                    flushBuilder();
                } else {
                    this.mBuilder.append(c);
                }
            }
        }
    }

    public interface Renderer {
        void onDrawFrame(GL10 gl10);

        void onSurfaceChanged(GL10 gl10, int i, int i2);

        void onSurfaceCreated(GL10 gl10, javax.microedition.khronos.egl.EGLConfig eGLConfig);
    }

    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser(boolean z) {
            super(8, 8, 8, 0, z ? 16 : 0, 0);
        }
    }

    public GLSurfaceView(Context context) {
        super(context);
        this.mThisWeakRef = new WeakReference<>(this);
        init();
    }

    public GLSurfaceView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mThisWeakRef = new WeakReference<>(this);
        init();
    }

    private void checkRenderThreadState() {
        if (this.mGLThread != null) {
            throw new IllegalStateException("setRenderer has already been called for this instance.");
        }
    }

    private void init() {
        getHolder().addCallback(this);
    }

    protected void finalize() throws Throwable {
        try {
            if (this.mGLThread != null) {
                this.mGLThread.requestExitAndWait();
            }
        } finally {
            super.finalize();
        }
    }

    public int getDebugFlags() {
        return this.mDebugFlags;
    }

    public boolean getPreserveEGLContextOnPause() {
        return this.mPreserveEGLContextOnPause;
    }

    public int getRenderMode() {
        return this.mGLThread.getRenderMode();
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mDetached && this.mRenderer != null) {
            int renderMode = this.mGLThread != null ? this.mGLThread.getRenderMode() : 1;
            this.mGLThread = new GLThread(this.mThisWeakRef);
            if (renderMode != 1) {
                this.mGLThread.setRenderMode(renderMode);
            }
            this.mGLThread.start();
        }
        this.mDetached = false;
    }

    @Override // android.view.SurfaceView, android.view.View
    protected void onDetachedFromWindow() throws Throwable {
        if (this.mGLThread != null) {
            this.mGLThread.requestExitAndWait();
        }
        this.mDetached = true;
        super.onDetachedFromWindow();
    }

    public void onPause() {
        this.mGLThread.onPause();
    }

    public void onResume() {
        this.mGLThread.onResume();
    }

    public void queueEvent(Runnable runnable) {
        this.mGLThread.queueEvent(runnable);
    }

    public void requestRender() {
        this.mGLThread.requestRender();
    }

    public void setDebugFlags(int i) {
        this.mDebugFlags = i;
    }

    public void setEGLConfigChooser(int i, int i2, int i3, int i4, int i5, int i6) {
        setEGLConfigChooser(new ComponentSizeChooser(i, i2, i3, i4, i5, i6));
    }

    public void setEGLConfigChooser(EGLConfigChooser eGLConfigChooser) {
        checkRenderThreadState();
        this.mEGLConfigChooser = eGLConfigChooser;
    }

    public void setEGLConfigChooser(boolean z) {
        setEGLConfigChooser(new SimpleEGLConfigChooser(z));
    }

    public void setEGLContextClientVersion(int i) {
        checkRenderThreadState();
        this.mEGLContextClientVersion = i;
    }

    public void setEGLContextFactory(EGLContextFactory eGLContextFactory) {
        checkRenderThreadState();
        this.mEGLContextFactory = eGLContextFactory;
    }

    public void setEGLWindowSurfaceFactory(EGLWindowSurfaceFactory eGLWindowSurfaceFactory) {
        checkRenderThreadState();
        this.mEGLWindowSurfaceFactory = eGLWindowSurfaceFactory;
    }

    public void setGLWrapper(GLWrapper gLWrapper) {
        this.mGLWrapper = gLWrapper;
    }

    public void setPreserveEGLContextOnPause(boolean z) {
        this.mPreserveEGLContextOnPause = z;
    }

    public void setRenderMode(int i) {
        this.mGLThread.setRenderMode(i);
    }

    public void setRenderer(Renderer renderer) {
        checkRenderThreadState();
        if (this.mEGLConfigChooser == null) {
            this.mEGLConfigChooser = new SimpleEGLConfigChooser(true);
        }
        if (this.mEGLContextFactory == null) {
            this.mEGLContextFactory = new DefaultContextFactory();
        }
        if (this.mEGLWindowSurfaceFactory == null) {
            this.mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();
        }
        this.mRenderer = renderer;
        this.mGLThread = new GLThread(this.mThisWeakRef);
        this.mGLThread.start();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
        this.mGLThread.onWindowResize(i2, i3);
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.mGLThread.surfaceCreated();
    }

    @Override // android.view.SurfaceHolder.Callback
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        this.mGLThread.surfaceDestroyed();
    }

    @Override // android.view.SurfaceHolder.Callback2
    @Deprecated
    public void surfaceRedrawNeeded(SurfaceHolder surfaceHolder) {
    }

    @Override // android.view.SurfaceHolder.Callback2
    public void surfaceRedrawNeededAsync(SurfaceHolder surfaceHolder, Runnable runnable) {
        if (this.mGLThread != null) {
            this.mGLThread.requestRenderAndNotify(runnable);
        }
    }
}
