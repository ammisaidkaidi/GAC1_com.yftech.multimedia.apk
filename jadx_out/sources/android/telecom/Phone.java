package android.telecom;

import android.annotation.SystemApi;
import android.os.Bundle;
import android.util.ArrayMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
@Deprecated
public final class Phone {
    private CallAudioState mCallAudioState;
    private final String mCallingPackage;
    private final InCallAdapter mInCallAdapter;
    private final int mTargetSdkVersion;
    private final Map<String, Call> mCallByTelecomCallId = new ArrayMap();
    private final List<Call> mCalls = new CopyOnWriteArrayList();
    private final List<Call> mUnmodifiableCalls = Collections.unmodifiableList(this.mCalls);
    private final List<Listener> mListeners = new CopyOnWriteArrayList();
    private boolean mCanAddCall = true;

    public static abstract class Listener {
        @Deprecated
        public void onAudioStateChanged(Phone phone, AudioState audioState) {
        }

        public void onBringToForeground(Phone phone, boolean z) {
        }

        public void onCallAdded(Phone phone, Call call) {
        }

        public void onCallAudioStateChanged(Phone phone, CallAudioState callAudioState) {
        }

        public void onCallRemoved(Phone phone, Call call) {
        }

        public void onCanAddCallChanged(Phone phone, boolean z) {
        }

        public void onSilenceRinger(Phone phone) {
        }
    }

    Phone(InCallAdapter inCallAdapter, String str, int i) {
        this.mInCallAdapter = inCallAdapter;
        this.mCallingPackage = str;
        this.mTargetSdkVersion = i;
    }

    private void checkCallTree(ParcelableCall parcelableCall) {
        if (parcelableCall.getChildCallIds() != null) {
            for (int i = 0; i < parcelableCall.getChildCallIds().size(); i++) {
                if (!this.mCallByTelecomCallId.containsKey(parcelableCall.getChildCallIds().get(i))) {
                    Log.wtf(this, "ParcelableCall %s has nonexistent child %s", parcelableCall.getId(), parcelableCall.getChildCallIds().get(i));
                }
            }
        }
    }

    private void fireBringToForeground(boolean z) {
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onBringToForeground(this, z);
        }
    }

    private void fireCallAdded(Call call) {
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onCallAdded(this, call);
        }
    }

    private void fireCallAudioStateChanged(CallAudioState callAudioState) {
        for (Listener listener : this.mListeners) {
            listener.onCallAudioStateChanged(this, callAudioState);
            listener.onAudioStateChanged(this, new AudioState(callAudioState));
        }
    }

    private void fireCallRemoved(Call call) {
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onCallRemoved(this, call);
        }
    }

    private void fireCanAddCallChanged(boolean z) {
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onCanAddCallChanged(this, z);
        }
    }

    private void fireSilenceRinger() {
        Iterator<Listener> it = this.mListeners.iterator();
        while (it.hasNext()) {
            it.next().onSilenceRinger(this);
        }
    }

    public final void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public final boolean canAddCall() {
        return this.mCanAddCall;
    }

    final void destroy() {
        for (Call call : this.mCalls) {
            InCallService.VideoCall videoCall = call.getVideoCall();
            if (videoCall != null) {
                videoCall.destroy();
            }
            if (call.getState() != 7) {
                call.internalSetDisconnected();
            }
        }
    }

    @Deprecated
    public final AudioState getAudioState() {
        return new AudioState(this.mCallAudioState);
    }

    public final CallAudioState getCallAudioState() {
        return this.mCallAudioState;
    }

    public final List<Call> getCalls() {
        return this.mUnmodifiableCalls;
    }

    final void internalAddCall(ParcelableCall parcelableCall) {
        Call call = new Call(this, parcelableCall.getId(), this.mInCallAdapter, parcelableCall.getState(), this.mCallingPackage, this.mTargetSdkVersion);
        this.mCallByTelecomCallId.put(parcelableCall.getId(), call);
        this.mCalls.add(call);
        checkCallTree(parcelableCall);
        call.internalUpdate(parcelableCall, this.mCallByTelecomCallId);
        fireCallAdded(call);
    }

    final void internalBringToForeground(boolean z) {
        fireBringToForeground(z);
    }

    final void internalCallAudioStateChanged(CallAudioState callAudioState) {
        if (Objects.equals(this.mCallAudioState, callAudioState)) {
            return;
        }
        this.mCallAudioState = callAudioState;
        fireCallAudioStateChanged(callAudioState);
    }

    final Call internalGetCallByTelecomId(String str) {
        return this.mCallByTelecomCallId.get(str);
    }

    final void internalOnConnectionEvent(String str, String str2, Bundle bundle) {
        Call call = this.mCallByTelecomCallId.get(str);
        if (call != null) {
            call.internalOnConnectionEvent(str2, bundle);
        }
    }

    final void internalOnHandoverComplete(String str) {
        Call call = this.mCallByTelecomCallId.get(str);
        if (call != null) {
            call.internalOnHandoverComplete();
        }
    }

    final void internalOnHandoverFailed(String str, int i) {
        Call call = this.mCallByTelecomCallId.get(str);
        if (call != null) {
            call.internalOnHandoverFailed(i);
        }
    }

    final void internalOnRttInitiationFailure(String str, int i) {
        Call call = this.mCallByTelecomCallId.get(str);
        if (call != null) {
            call.internalOnRttInitiationFailure(i);
        }
    }

    final void internalOnRttUpgradeRequest(String str, int i) {
        Call call = this.mCallByTelecomCallId.get(str);
        if (call != null) {
            call.internalOnRttUpgradeRequest(i);
        }
    }

    final void internalRemoveCall(Call call) {
        this.mCallByTelecomCallId.remove(call.internalGetCallId());
        this.mCalls.remove(call);
        InCallService.VideoCall videoCall = call.getVideoCall();
        if (videoCall != null) {
            videoCall.destroy();
        }
        fireCallRemoved(call);
    }

    final void internalSetCanAddCall(boolean z) {
        if (this.mCanAddCall != z) {
            this.mCanAddCall = z;
            fireCanAddCallChanged(z);
        }
    }

    final void internalSetPostDialWait(String str, String str2) {
        Call call = this.mCallByTelecomCallId.get(str);
        if (call != null) {
            call.internalSetPostDialWait(str2);
        }
    }

    final void internalSilenceRinger() {
        fireSilenceRinger();
    }

    final void internalUpdateCall(ParcelableCall parcelableCall) {
        Call call = this.mCallByTelecomCallId.get(parcelableCall.getId());
        if (call != null) {
            checkCallTree(parcelableCall);
            call.internalUpdate(parcelableCall, this.mCallByTelecomCallId);
        }
    }

    public final void removeListener(Listener listener) {
        if (listener != null) {
            this.mListeners.remove(listener);
        }
    }

    public void requestBluetoothAudio(String str) {
        this.mInCallAdapter.requestBluetoothAudio(str);
    }

    public final void setAudioRoute(int i) {
        this.mInCallAdapter.setAudioRoute(i);
    }

    public final void setMuted(boolean z) {
        this.mInCallAdapter.mute(z);
    }

    public final void setProximitySensorOff(boolean z) {
        this.mInCallAdapter.turnProximitySensorOff(z);
    }

    public final void setProximitySensorOn() {
        this.mInCallAdapter.turnProximitySensorOn();
    }
}
