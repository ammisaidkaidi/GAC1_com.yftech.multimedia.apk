package android.hardware.camera2.impl;

import android.hardware.camera2.CameraCaptureSession;
import android.os.Binder;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class CallbackProxies {

    public static class SessionStateCallbackProxy extends CameraCaptureSession.StateCallback {
        private final CameraCaptureSession.StateCallback mCallback;
        private final Executor mExecutor;

        public SessionStateCallbackProxy(Executor executor, CameraCaptureSession.StateCallback stateCallback) {
            this.mExecutor = (Executor) Preconditions.checkNotNull(executor, "executor must not be null");
            this.mCallback = (CameraCaptureSession.StateCallback) Preconditions.checkNotNull(stateCallback, "callback must not be null");
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onActive(final CameraCaptureSession cameraCaptureSession) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CallbackProxies$SessionStateCallbackProxy$ISQyEhLUI1khcOCin3OIsRyTUoU
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.mCallback.onActive(cameraCaptureSession);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onCaptureQueueEmpty(final CameraCaptureSession cameraCaptureSession) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CallbackProxies$SessionStateCallbackProxy$hoQOYc189Bss2NBtrutabMRw4VU
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.mCallback.onCaptureQueueEmpty(cameraCaptureSession);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onClosed(final CameraCaptureSession cameraCaptureSession) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CallbackProxies$SessionStateCallbackProxy$9H0ZdANdMrdpoq2bfIL2l3DVsKk
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.mCallback.onClosed(cameraCaptureSession);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigureFailed(final CameraCaptureSession cameraCaptureSession) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CallbackProxies$SessionStateCallbackProxy$gvbTsp9UPpKJAbdycdci_ZW5BeI
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.mCallback.onConfigureFailed(cameraCaptureSession);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigured(final CameraCaptureSession cameraCaptureSession) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CallbackProxies$SessionStateCallbackProxy$soW0qC12Osypoky6AfL3P2-TeDw
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.mCallback.onConfigured(cameraCaptureSession);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onReady(final CameraCaptureSession cameraCaptureSession) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CallbackProxies$SessionStateCallbackProxy$Hoz-iT1tD_pl7sCGu4flyo-xB90
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.mCallback.onReady(cameraCaptureSession);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onSurfacePrepared(final CameraCaptureSession cameraCaptureSession, final Surface surface) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() { // from class: android.hardware.camera2.impl.-$$Lambda$CallbackProxies$SessionStateCallbackProxy$tuajQwbKz3BV5CZZJdjl97HF6Tw
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.mCallback.onSurfacePrepared(cameraCaptureSession, surface);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }
    }

    private CallbackProxies() {
        throw new AssertionError();
    }
}
