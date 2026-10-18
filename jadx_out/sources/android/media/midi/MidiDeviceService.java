package android.media.midi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public abstract class MidiDeviceService extends Service {
    public static final String SERVICE_INTERFACE = "android.media.midi.MidiDeviceService";
    private static final String TAG = "MidiDeviceService";
    private final MidiDeviceServer.Callback mCallback = new MidiDeviceServer.Callback() { // from class: android.media.midi.MidiDeviceService.1
        @Override // android.media.midi.MidiDeviceServer.Callback
        public void onClose() {
            MidiDeviceService.this.onClose();
        }

        @Override // android.media.midi.MidiDeviceServer.Callback
        public void onDeviceStatusChanged(MidiDeviceServer midiDeviceServer, MidiDeviceStatus midiDeviceStatus) {
            MidiDeviceService.this.onDeviceStatusChanged(midiDeviceStatus);
        }
    };
    private MidiDeviceInfo mDeviceInfo;
    private IMidiManager mMidiManager;
    private MidiDeviceServer mServer;

    public final MidiDeviceInfo getDeviceInfo() {
        return this.mDeviceInfo;
    }

    public final MidiReceiver[] getOutputPortReceivers() {
        if (this.mServer == null) {
            return null;
        }
        return this.mServer.getOutputPortReceivers();
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        if (!SERVICE_INTERFACE.equals(intent.getAction()) || this.mServer == null) {
            return null;
        }
        return this.mServer.getBinderInterface().asBinder();
    }

    public void onClose() {
    }

    @Override // android.app.Service
    public void onCreate() {
        MidiDeviceServer midiDeviceServer;
        this.mMidiManager = IMidiManager.Stub.asInterface(ServiceManager.getService("midi"));
        try {
            MidiDeviceInfo serviceDeviceInfo = this.mMidiManager.getServiceDeviceInfo(getPackageName(), getClass().getName());
            if (serviceDeviceInfo == null) {
                Log.e(TAG, "Could not find MidiDeviceInfo for MidiDeviceService " + this);
                return;
            }
            this.mDeviceInfo = serviceDeviceInfo;
            MidiReceiver[] midiReceiverArrOnGetInputPortReceivers = onGetInputPortReceivers();
            if (midiReceiverArrOnGetInputPortReceivers == null) {
                midiReceiverArrOnGetInputPortReceivers = new MidiReceiver[0];
            }
            midiDeviceServer = new MidiDeviceServer(this.mMidiManager, midiReceiverArrOnGetInputPortReceivers, serviceDeviceInfo, this.mCallback);
            this.mServer = midiDeviceServer;
        } catch (RemoteException unused) {
            Log.e(TAG, "RemoteException in IMidiManager.getServiceDeviceInfo");
            midiDeviceServer = null;
        }
    }

    public void onDeviceStatusChanged(MidiDeviceStatus midiDeviceStatus) {
    }

    public abstract MidiReceiver[] onGetInputPortReceivers();
}
