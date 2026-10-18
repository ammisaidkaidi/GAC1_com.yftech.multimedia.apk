package android.os;

import android.content.Context;
import android.media.AudioAttributes;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class SystemVibrator extends Vibrator {
    private static final String TAG = "Vibrator";
    private final IVibratorService mService;
    private final Binder mToken;

    public SystemVibrator() {
        this.mToken = new Binder();
        this.mService = IVibratorService.Stub.asInterface(ServiceManager.getService(Context.VIBRATOR_SERVICE));
    }

    public SystemVibrator(Context context) {
        super(context);
        this.mToken = new Binder();
        this.mService = IVibratorService.Stub.asInterface(ServiceManager.getService(Context.VIBRATOR_SERVICE));
    }

    private static int usageForAttributes(AudioAttributes audioAttributes) {
        if (audioAttributes != null) {
            return audioAttributes.getUsage();
        }
        return 0;
    }

    @Override // android.os.Vibrator
    public void cancel() {
        if (this.mService == null) {
            return;
        }
        try {
            this.mService.cancelVibrate(this.mToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to cancel vibration.", e);
        }
    }

    @Override // android.os.Vibrator
    public boolean hasAmplitudeControl() {
        if (this.mService == null) {
            Log.w(TAG, "Failed to check amplitude control; no vibrator service.");
            return false;
        }
        try {
            return this.mService.hasAmplitudeControl();
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.os.Vibrator
    public boolean hasVibrator() {
        if (this.mService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
            return false;
        }
        try {
            return this.mService.hasVibrator();
        } catch (RemoteException unused) {
            return false;
        }
    }

    @Override // android.os.Vibrator
    public void vibrate(int i, String str, VibrationEffect vibrationEffect, AudioAttributes audioAttributes) {
        if (this.mService == null) {
            Log.w(TAG, "Failed to vibrate; no vibrator service.");
            return;
        }
        try {
            this.mService.vibrate(i, str, vibrationEffect, usageForAttributes(audioAttributes), this.mToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Failed to vibrate.", e);
        }
    }
}
