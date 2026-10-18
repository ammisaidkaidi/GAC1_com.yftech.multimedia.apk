package android.media;

import android.hardware.cas.V1_0.HidlCasPluginDescriptor;
import android.hardware.cas.V1_0.ICas;
import android.hardware.cas.V1_0.ICasListener;
import android.hardware.cas.V1_0.IMediaCasService;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IHwBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.Singleton;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public final class MediaCas implements AutoCloseable {
    private static final String TAG = "MediaCas";
    private static final Singleton<IMediaCasService> gDefault = new Singleton<IMediaCasService>() { // from class: android.media.MediaCas.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public IMediaCasService create() {
            try {
                return IMediaCasService.getService();
            } catch (RemoteException unused) {
                return null;
            }
        }
    };
    private final ICasListener.Stub mBinder = new ICasListener.Stub() { // from class: android.media.MediaCas.2
        @Override // android.hardware.cas.V1_0.ICasListener
        public void onEvent(int i, int i2, ArrayList<Byte> arrayList) throws RemoteException {
            MediaCas.this.mEventHandler.sendMessage(MediaCas.this.mEventHandler.obtainMessage(0, i, i2, arrayList));
        }
    };
    private EventHandler mEventHandler;
    private HandlerThread mHandlerThread;
    private ICas mICas;
    private EventListener mListener;

    private class EventHandler extends Handler {
        private static final int MSG_CAS_EVENT = 0;

        public EventHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what == 0) {
                MediaCas.this.mListener.onEvent(MediaCas.this, message.arg1, message.arg2, MediaCas.this.toBytes((ArrayList) message.obj));
            }
        }
    }

    public interface EventListener {
        void onEvent(MediaCas mediaCas, int i, int i2, byte[] bArr);
    }

    private class OpenSessionCallback implements ICas.openSessionCallback {
        public Session mSession;
        public int mStatus;

        private OpenSessionCallback() {
        }

        @Override // android.hardware.cas.V1_0.ICas.openSessionCallback
        public void onValues(int i, ArrayList<Byte> arrayList) {
            this.mStatus = i;
            this.mSession = MediaCas.this.createFromSessionId(arrayList);
        }
    }

    public static class PluginDescriptor {
        private final int mCASystemId;
        private final String mName;

        private PluginDescriptor() {
            this.mCASystemId = 65535;
            this.mName = null;
        }

        PluginDescriptor(HidlCasPluginDescriptor hidlCasPluginDescriptor) {
            this.mCASystemId = hidlCasPluginDescriptor.caSystemId;
            this.mName = hidlCasPluginDescriptor.name;
        }

        public String getName() {
            return this.mName;
        }

        public int getSystemId() {
            return this.mCASystemId;
        }

        public String toString() {
            return "PluginDescriptor {" + this.mCASystemId + ", " + this.mName + "}";
        }
    }

    public final class Session implements AutoCloseable {
        final ArrayList<Byte> mSessionId;

        Session(ArrayList<Byte> arrayList) {
            this.mSessionId = arrayList;
        }

        @Override // java.lang.AutoCloseable
        public void close() {
            MediaCas.this.validateInternalStates();
            try {
                MediaCasStateException.throwExceptionIfNeeded(MediaCas.this.mICas.closeSession(this.mSessionId));
            } catch (RemoteException unused) {
                MediaCas.this.cleanupAndRethrowIllegalState();
            }
        }

        public void processEcm(byte[] bArr) throws MediaCasException {
            processEcm(bArr, 0, bArr.length);
        }

        public void processEcm(byte[] bArr, int i, int i2) throws MediaCasException {
            MediaCas.this.validateInternalStates();
            try {
                MediaCasException.throwExceptionIfNeeded(MediaCas.this.mICas.processEcm(this.mSessionId, MediaCas.this.toByteArray(bArr, i, i2)));
            } catch (RemoteException unused) {
                MediaCas.this.cleanupAndRethrowIllegalState();
            }
        }

        public void setPrivateData(byte[] bArr) throws MediaCasException {
            MediaCas.this.validateInternalStates();
            try {
                MediaCasException.throwExceptionIfNeeded(MediaCas.this.mICas.setSessionPrivateData(this.mSessionId, MediaCas.this.toByteArray(bArr, 0, bArr.length)));
            } catch (RemoteException unused) {
                MediaCas.this.cleanupAndRethrowIllegalState();
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public MediaCas(int i) throws MediaCasException.UnsupportedCasException {
        try {
            try {
                this.mICas = getService().createPlugin(i, this.mBinder);
                ICas iCas = this.mICas;
                this = iCas;
                if (iCas == null) {
                    throw new MediaCasException.UnsupportedCasException("Unsupported CA_system_id " + i);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to create plugin: " + e);
                this.mICas = null;
                ICas iCas2 = this.mICas;
                this = iCas2;
                if (iCas2 == null) {
                    throw new MediaCasException.UnsupportedCasException("Unsupported CA_system_id " + i);
                }
            }
        } catch (Throwable th) {
            if (this.mICas != null) {
                throw th;
            }
            throw new MediaCasException.UnsupportedCasException("Unsupported CA_system_id " + i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanupAndRethrowIllegalState() {
        this.mICas = null;
        throw new IllegalStateException();
    }

    public static PluginDescriptor[] enumeratePlugins() {
        IMediaCasService service = getService();
        if (service != null) {
            try {
                ArrayList<HidlCasPluginDescriptor> arrayListEnumeratePlugins = service.enumeratePlugins();
                if (arrayListEnumeratePlugins.size() == 0) {
                    return null;
                }
                PluginDescriptor[] pluginDescriptorArr = new PluginDescriptor[arrayListEnumeratePlugins.size()];
                for (int i = 0; i < pluginDescriptorArr.length; i++) {
                    pluginDescriptorArr[i] = new PluginDescriptor(arrayListEnumeratePlugins.get(i));
                }
                return pluginDescriptorArr;
            } catch (RemoteException unused) {
            }
        }
        return null;
    }

    static IMediaCasService getService() {
        return gDefault.get();
    }

    public static boolean isSystemIdSupported(int i) {
        IMediaCasService service = getService();
        if (service == null) {
            return false;
        }
        try {
            return service.isSystemIdSupported(i);
        } catch (RemoteException unused) {
            return false;
        }
    }

    private ArrayList<Byte> toByteArray(byte[] bArr) {
        return bArr == null ? new ArrayList<>() : toByteArray(bArr, 0, bArr.length);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<Byte> toByteArray(byte[] bArr, int i, int i2) {
        ArrayList<Byte> arrayList = new ArrayList<>(i2);
        for (int i3 = 0; i3 < i2; i3++) {
            arrayList.add(Byte.valueOf(bArr[i + i3]));
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public byte[] toBytes(ArrayList<Byte> arrayList) {
        if (arrayList == null) {
            return null;
        }
        byte[] bArr = new byte[arrayList.size()];
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = arrayList.get(i).byteValue();
        }
        return bArr;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void validateInternalStates() {
        if (this.mICas == null) {
            throw new IllegalStateException();
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.mICas != null) {
            try {
                this.mICas.release();
            } catch (RemoteException unused) {
            } finally {
                this.mICas = null;
            }
        }
    }

    Session createFromSessionId(ArrayList<Byte> arrayList) {
        if (arrayList == null || arrayList.size() == 0) {
            return null;
        }
        return new Session(arrayList);
    }

    protected void finalize() {
        close();
    }

    IHwBinder getBinder() {
        validateInternalStates();
        return this.mICas.asBinder();
    }

    public Session openSession() throws MediaCasException {
        validateInternalStates();
        try {
            OpenSessionCallback openSessionCallback = new OpenSessionCallback();
            this.mICas.openSession(openSessionCallback);
            MediaCasException.throwExceptionIfNeeded(openSessionCallback.mStatus);
            return openSessionCallback.mSession;
        } catch (RemoteException unused) {
            cleanupAndRethrowIllegalState();
            return null;
        }
    }

    public void processEmm(byte[] bArr) throws MediaCasException {
        processEmm(bArr, 0, bArr.length);
    }

    public void processEmm(byte[] bArr, int i, int i2) throws MediaCasException {
        validateInternalStates();
        try {
            MediaCasException.throwExceptionIfNeeded(this.mICas.processEmm(toByteArray(bArr, i, i2)));
        } catch (RemoteException unused) {
            cleanupAndRethrowIllegalState();
        }
    }

    public void provision(String str) throws MediaCasException {
        validateInternalStates();
        try {
            MediaCasException.throwExceptionIfNeeded(this.mICas.provision(str));
        } catch (RemoteException unused) {
            cleanupAndRethrowIllegalState();
        }
    }

    public void refreshEntitlements(int i, byte[] bArr) throws MediaCasException {
        validateInternalStates();
        try {
            MediaCasException.throwExceptionIfNeeded(this.mICas.refreshEntitlements(i, toByteArray(bArr)));
        } catch (RemoteException unused) {
            cleanupAndRethrowIllegalState();
        }
    }

    public void sendEvent(int i, int i2, byte[] bArr) throws MediaCasException {
        validateInternalStates();
        try {
            MediaCasException.throwExceptionIfNeeded(this.mICas.sendEvent(i, i2, toByteArray(bArr)));
        } catch (RemoteException unused) {
            cleanupAndRethrowIllegalState();
        }
    }

    public void setEventListener(EventListener eventListener, Handler handler) {
        this.mListener = eventListener;
        if (this.mListener == null) {
            this.mEventHandler = null;
            return;
        }
        Looper looper = handler != null ? handler.getLooper() : null;
        if (looper == null && (looper = Looper.myLooper()) == null && (looper = Looper.getMainLooper()) == null) {
            if (this.mHandlerThread == null || !this.mHandlerThread.isAlive()) {
                this.mHandlerThread = new HandlerThread("MediaCasEventThread", -2);
                this.mHandlerThread.start();
            }
            looper = this.mHandlerThread.getLooper();
        }
        this.mEventHandler = new EventHandler(looper);
    }

    public void setPrivateData(byte[] bArr) throws MediaCasException {
        validateInternalStates();
        try {
            MediaCasException.throwExceptionIfNeeded(this.mICas.setPrivateData(toByteArray(bArr, 0, bArr.length)));
        } catch (RemoteException unused) {
            cleanupAndRethrowIllegalState();
        }
    }
}
