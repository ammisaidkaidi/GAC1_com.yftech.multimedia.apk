package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.ICarTBox;

/* JADX INFO: loaded from: classes4.dex */
public class CarTBoxManager extends BaseApiManager implements ICarTBox {
    private static final String TAG = "CarTBoxManager";
    private static CarTBoxManager sMe;

    public static abstract class CarTBoxListener {
        @SignalReceiver(signalId = 190)
        public void onMuteRadioOrderChanged(ICarTBox.MuteRadioOrder muteRadioOrder) {
        }

        @SignalReceiver(signalId = 329)
        public void onPhoneCallStateChanged(ICarTBox.PhoneCallState phoneCallState) {
        }

        @SignalReceiver(signalId = 328)
        public void onPhoneModeChanged(ICarTBox.PhoneMode phoneMode) {
        }
    }

    private CarTBoxManager() {
    }

    public static synchronized CarTBoxManager get() {
        CarTBoxManager carTBoxManager;
        if (sMe != null) {
            carTBoxManager = sMe;
        } else {
            carTBoxManager = new CarTBoxManager();
            sMe = carTBoxManager;
        }
        return carTBoxManager;
    }

    public ICarTBox.MuteRadioOrder getMuteRadioOrder() {
        return ICarTBox.MuteRadioOrder.valueOf(getSignalValue(190).intValue());
    }

    public ICarTBox.PhoneCallState getPhoneCallState() {
        return ICarTBox.PhoneCallState.valueOf(getSignalValue(329).intValue());
    }

    public ICarTBox.PhoneMode getPhoneMode() {
        return ICarTBox.PhoneMode.valueOf(getSignalValue(328).intValue());
    }

    public void registerListener(CarTBoxListener carTBoxListener) {
        registerReceiver(carTBoxListener);
    }

    public boolean setPhoneKeyAccept(ICarTBox.PhoneKeyAction phoneKeyAction) {
        return setSignalValue(326, phoneKeyAction.value);
    }

    public boolean setPhoneKeyCancel(ICarTBox.PhoneKeyAction phoneKeyAction) {
        return setSignalValue(327, phoneKeyAction.value);
    }

    public boolean setPhoneType(ICarTBox.PhoneType phoneType) {
        return setSignalValue(514, phoneType.value);
    }

    public void unregisterListener(CarTBoxListener carTBoxListener) {
        unregisterReceiver(carTBoxListener);
    }
}
