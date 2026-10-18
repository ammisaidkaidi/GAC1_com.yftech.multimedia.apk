package android.hardware.soundtrigger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public class SoundTriggerModule {
    private static final int EVENT_RECOGNITION = 1;
    private static final int EVENT_SERVICE_DIED = 2;
    private static final int EVENT_SERVICE_STATE_CHANGE = 4;
    private static final int EVENT_SOUNDMODEL = 3;
    private NativeEventHandlerDelegate mEventHandlerDelegate;
    private int mId;
    private long mNativeContext;

    private class NativeEventHandlerDelegate {
        private final Handler mHandler;

        NativeEventHandlerDelegate(final SoundTrigger.StatusListener statusListener, Handler handler) {
            Looper looper = handler != null ? handler.getLooper() : Looper.getMainLooper();
            if (looper != null) {
                this.mHandler = new Handler(looper) { // from class: android.hardware.soundtrigger.SoundTriggerModule.NativeEventHandlerDelegate.1
                    @Override // android.os.Handler
                    public void handleMessage(Message message) {
                        switch (message.what) {
                            case 1:
                                if (statusListener != null) {
                                    statusListener.onRecognition((SoundTrigger.RecognitionEvent) message.obj);
                                }
                                break;
                            case 2:
                                if (statusListener != null) {
                                    statusListener.onServiceDied();
                                }
                                break;
                            case 3:
                                if (statusListener != null) {
                                    statusListener.onSoundModelUpdate((SoundTrigger.SoundModelEvent) message.obj);
                                }
                                break;
                            case 4:
                                if (statusListener != null) {
                                    statusListener.onServiceStateChange(message.arg1);
                                }
                                break;
                        }
                    }
                };
            } else {
                this.mHandler = null;
            }
        }

        Handler handler() {
            return this.mHandler;
        }
    }

    SoundTriggerModule(int i, SoundTrigger.StatusListener statusListener, Handler handler) {
        this.mId = i;
        this.mEventHandlerDelegate = new NativeEventHandlerDelegate(statusListener, handler);
        native_setup(new WeakReference(this));
    }

    private native void native_finalize();

    private native void native_setup(Object obj);

    private static void postEventFromNative(Object obj, int i, int i2, int i3, Object obj2) {
        NativeEventHandlerDelegate nativeEventHandlerDelegate;
        Handler handler;
        SoundTriggerModule soundTriggerModule = (SoundTriggerModule) ((WeakReference) obj).get();
        if (soundTriggerModule == null || (nativeEventHandlerDelegate = soundTriggerModule.mEventHandlerDelegate) == null || (handler = nativeEventHandlerDelegate.handler()) == null) {
            return;
        }
        handler.sendMessage(handler.obtainMessage(i, i2, i3, obj2));
    }

    public native void detach();

    protected void finalize() {
        native_finalize();
    }

    public native int loadSoundModel(SoundTrigger.SoundModel soundModel, int[] iArr);

    public native int startRecognition(int i, SoundTrigger.RecognitionConfig recognitionConfig);

    public native int stopRecognition(int i);

    public native int unloadSoundModel(int i);
}
