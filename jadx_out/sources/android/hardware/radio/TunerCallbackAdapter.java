package android.hardware.radio;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
class TunerCallbackAdapter extends ITunerCallback.Stub {
    private static final String TAG = "BroadcastRadio.TunerCallbackAdapter";
    private final RadioTuner.Callback mCallback;
    RadioManager.ProgramInfo mCurrentProgramInfo;
    private final Handler mHandler;
    List<RadioManager.ProgramInfo> mLastCompleteList;
    ProgramList mProgramList;
    private final Object mLock = new Object();
    boolean mIsAntennaConnected = true;
    private boolean mDelayedCompleteCallback = false;

    TunerCallbackAdapter(RadioTuner.Callback callback, Handler handler) {
        this.mCallback = callback;
        if (handler == null) {
            this.mHandler = new Handler(Looper.getMainLooper());
        } else {
            this.mHandler = handler;
        }
    }

    public static /* synthetic */ void lambda$onCurrentProgramInfoChanged$6(TunerCallbackAdapter tunerCallbackAdapter, RadioManager.ProgramInfo programInfo) {
        tunerCallbackAdapter.mCallback.onProgramInfoChanged(programInfo);
        RadioMetadata metadata = programInfo.getMetadata();
        if (metadata != null) {
            tunerCallbackAdapter.mCallback.onMetadataChanged(metadata);
        }
    }

    public static /* synthetic */ void lambda$setProgramListObserver$0(TunerCallbackAdapter tunerCallbackAdapter, ProgramList programList, ProgramList.OnCloseListener onCloseListener) {
        synchronized (tunerCallbackAdapter.mLock) {
            if (tunerCallbackAdapter.mProgramList != programList) {
                return;
            }
            tunerCallbackAdapter.mProgramList = null;
            tunerCallbackAdapter.mLastCompleteList = null;
            onCloseListener.onClose();
        }
    }

    public static /* synthetic */ void lambda$setProgramListObserver$1(TunerCallbackAdapter tunerCallbackAdapter, ProgramList programList) {
        synchronized (tunerCallbackAdapter.mLock) {
            if (tunerCallbackAdapter.mProgramList != programList) {
                return;
            }
            tunerCallbackAdapter.mLastCompleteList = programList.toList();
            if (tunerCallbackAdapter.mDelayedCompleteCallback) {
                Log.d(TAG, "Sending delayed onBackgroundScanComplete callback");
                tunerCallbackAdapter.sendBackgroundScanCompleteLocked();
            }
        }
    }

    private void sendBackgroundScanCompleteLocked() {
        this.mDelayedCompleteCallback = false;
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$xIUT1Qu5TkA83V8ttYy1zv-JuFo
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onBackgroundScanComplete();
            }
        });
    }

    void clearLastCompleteList() {
        synchronized (this.mLock) {
            this.mLastCompleteList = null;
        }
    }

    void close() {
        synchronized (this.mLock) {
            if (this.mProgramList != null) {
                this.mProgramList.close();
            }
        }
    }

    RadioManager.ProgramInfo getCurrentProgramInformation() {
        RadioManager.ProgramInfo programInfo;
        synchronized (this.mLock) {
            programInfo = this.mCurrentProgramInfo;
        }
        return programInfo;
    }

    List<RadioManager.ProgramInfo> getLastCompleteList() {
        List<RadioManager.ProgramInfo> list;
        synchronized (this.mLock) {
            list = this.mLastCompleteList;
        }
        return list;
    }

    boolean isAntennaConnected() {
        return this.mIsAntennaConnected;
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onAntennaState(final boolean z) {
        this.mIsAntennaConnected = z;
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$dR-VQmFrL_tBD2wpNvborTd8W08
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onAntennaState(z);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onBackgroundScanAvailabilityChange(final boolean z) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$4zf9n0sz_rU8z6a9GJmRInWrYkQ
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onBackgroundScanAvailabilityChange(z);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onBackgroundScanComplete() {
        synchronized (this.mLock) {
            if (this.mLastCompleteList != null) {
                sendBackgroundScanCompleteLocked();
            } else {
                Log.i(TAG, "Got onBackgroundScanComplete callback, but the program list didn't get through yet. Delaying it...");
                this.mDelayedCompleteCallback = true;
            }
        }
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onConfigurationChanged(final RadioManager.BandConfig bandConfig) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$B4BuskgdSatf-Xt5wzgLniEltQk
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onConfigurationChanged(bandConfig);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onCurrentProgramInfoChanged(final RadioManager.ProgramInfo programInfo) {
        if (programInfo == null) {
            Log.e(TAG, "ProgramInfo must not be null");
            return;
        }
        synchronized (this.mLock) {
            this.mCurrentProgramInfo = programInfo;
        }
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$RSNrzX5-O3nayC2_jg0kAR6KkKY
            @Override // java.lang.Runnable
            public final void run() {
                TunerCallbackAdapter.lambda$onCurrentProgramInfoChanged$6(this.f$0, programInfo);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onEmergencyAnnouncement(final boolean z) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$ZwPm3xxjeLvbP12KweyzqFJVnj4
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onEmergencyAnnouncement(z);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onError(final int i) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$jl29exheqPoYrltfLs9fLsjsI1A
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onError(i);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onParametersUpdated(final Map map) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$Yz-4KCDu1MOynGdkDf_oMxqhjeY
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onParametersUpdated(map);
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onProgramListChanged() {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$UsmGhKordXy4lhCylRP0mm2NcYc
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onProgramListChanged();
            }
        });
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onProgramListUpdated(ProgramList.Chunk chunk) {
        synchronized (this.mLock) {
            if (this.mProgramList == null) {
                return;
            }
            this.mProgramList.apply((ProgramList.Chunk) Objects.requireNonNull(chunk));
        }
    }

    @Override // android.hardware.radio.ITunerCallback
    public void onTrafficAnnouncement(final boolean z) {
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$tiaoLZrR2K56rYeqHvSRh5lRdBI
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onTrafficAnnouncement(z);
            }
        });
    }

    /* JADX WARN: Code duplicated, block: B:16:0x0024  */
    /* JADX WARN: Instruction removed from duplicated block: B:16:0x0024, please report this as an issue */
    @Override // android.hardware.radio.ITunerCallback
    public void onTuneFailed(final int i, final ProgramSelector programSelector) {
        final int i2;
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$Hj_P___HTEx_8p7qvYVPXmhwu7w
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onTuneFailed(i, programSelector);
            }
        });
        if (i == Integer.MIN_VALUE || i == -38) {
            Log.i(TAG, "Got an error with no mapping to the legacy API (" + i + "), doing a best-effort conversion to ERROR_SCAN_TIMEOUT");
            i2 = 3;
        } else {
            if (i != -32) {
                if (i == -22 || i == -19) {
                    Log.i(TAG, "Got an error with no mapping to the legacy API (" + i + "), doing a best-effort conversion to ERROR_SCAN_TIMEOUT");
                } else if (i != -1) {
                }
                i2 = 3;
            }
            i2 = 1;
        }
        this.mHandler.post(new Runnable() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$HcS5_voI1xju970_jCP6Iz0LgPE
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.mCallback.onError(i2);
            }
        });
    }

    void setProgramListObserver(final ProgramList programList, final ProgramList.OnCloseListener onCloseListener) {
        Objects.requireNonNull(onCloseListener);
        synchronized (this.mLock) {
            if (this.mProgramList != null) {
                Log.w(TAG, "Previous program list observer wasn't properly closed, closing it...");
                this.mProgramList.close();
            }
            this.mProgramList = programList;
            if (programList == null) {
                return;
            }
            programList.setOnCloseListener(new ProgramList.OnCloseListener() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$Hl80-0ppQ17uTjZuGamwBQMrO6Y
                @Override // android.hardware.radio.ProgramList.OnCloseListener
                public final void onClose() {
                    TunerCallbackAdapter.lambda$setProgramListObserver$0(this.f$0, programList, onCloseListener);
                }
            });
            programList.addOnCompleteListener(new ProgramList.OnCompleteListener() { // from class: android.hardware.radio.-$$Lambda$TunerCallbackAdapter$V-mJUy8dIlOVjsZ1ckkgn490jFI
                @Override // android.hardware.radio.ProgramList.OnCompleteListener
                public final void onComplete() {
                    TunerCallbackAdapter.lambda$setProgramListObserver$1(this.f$0, programList);
                }
            });
        }
    }
}
