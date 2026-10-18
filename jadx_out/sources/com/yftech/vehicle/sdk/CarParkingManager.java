package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarParking;

/* JADX INFO: loaded from: classes4.dex */
public class CarParkingManager extends BaseApiManager implements ICarParking {
    private static final String TAG = "CarParkingManager";
    private static CarParkingManager sMe;

    public static abstract class CarParkingListener {
        @SignalReceiver(signalId = 136)
        public void onPASDistanceFLChanged(int i) {
        }

        @SignalReceiver(signalId = 131)
        public void onPASDistanceFLMiddleChanged(int i) {
        }

        @SignalReceiver(signalId = 140)
        public void onPASDistanceFLSideChanged(int i) {
        }

        @SignalReceiver(signalId = 135)
        public void onPASDistanceFRChanged(int i) {
        }

        @SignalReceiver(signalId = 132)
        public void onPASDistanceFRMiddleChanged(int i) {
        }

        @SignalReceiver(signalId = 139)
        public void onPASDistanceFRSideChanged(int i) {
        }

        @SignalReceiver(signalId = 134)
        public void onPASDistanceRLChanged(int i) {
        }

        @SignalReceiver(signalId = 129)
        public void onPASDistanceRLMiddleChanged(int i) {
        }

        @SignalReceiver(signalId = 138)
        public void onPASDistanceRLSideChanged(int i) {
        }

        @SignalReceiver(signalId = 133)
        public void onPASDistanceRRChanged(int i) {
        }

        @SignalReceiver(signalId = 130)
        public void onPASDistanceRRMiddleChanged(int i) {
        }

        @SignalReceiver(signalId = 137)
        public void onPASDistanceRRSideChanged(int i) {
        }

        @SignalReceiver(signalId = 145)
        public void onPASErrorFlagOfEEPROMChanged(ICarParking.PASErrorFlag pASErrorFlag) {
        }

        @SignalReceiver(signalId = 146)
        public void onPASErrorFlagOfIGNVoltageChanged(ICarParking.PASErrorFlag pASErrorFlag) {
        }

        @SignalReceiver(signalId = 144)
        public void onPASErrorFlagRLChanged(ICarParking.PASErrorFlag pASErrorFlag) {
        }

        @SignalReceiver(signalId = 142)
        public void onPASErrorFlagRLMiddleChanged(ICarParking.PASErrorFlag pASErrorFlag) {
        }

        @SignalReceiver(signalId = 143)
        public void onPASErrorFlagRRChanged(ICarParking.PASErrorFlag pASErrorFlag) {
        }

        @SignalReceiver(signalId = 141)
        public void onPASErrorFlagRRMiddleChanged(ICarParking.PASErrorFlag pASErrorFlag) {
        }

        @SignalReceiver(signalId = 127)
        public void onPASSystemFailureFlagChanged(ICarParking.PASSystemFailureFlag pASSystemFailureFlag) {
        }

        @SignalReceiver(signalId = 126)
        public void onPASSystemTypeChanged(ICarParking.PASSystemType pASSystemType) {
        }

        @SignalReceiver(signalId = 128)
        public void onPASWarnToneChanged(ICarParking.PASWarnTone pASWarnTone) {
        }

        @SignalReceiver(signalId = 254)
        public void onParkingRadarAlarmEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 256)
        public void onParkingVoiceSwitchEnableStateChanged(IBaseApi.EnableState enableState) {
        }
    }

    private CarParkingManager() {
    }

    public static synchronized CarParkingManager get() {
        CarParkingManager carParkingManager;
        if (sMe != null) {
            carParkingManager = sMe;
        } else {
            carParkingManager = new CarParkingManager();
            sMe = carParkingManager;
        }
        return carParkingManager;
    }

    public int getPASDistanceFL() {
        return getSignalValue(136).intValue();
    }

    public int getPASDistanceFLMiddle() {
        return getSignalValue(131).intValue();
    }

    public int getPASDistanceFLSide() {
        return getSignalValue(140).intValue();
    }

    public int getPASDistanceFR() {
        return getSignalValue(135).intValue();
    }

    public int getPASDistanceFRMiddle() {
        return getSignalValue(132).intValue();
    }

    public int getPASDistanceFRSide() {
        return getSignalValue(139).intValue();
    }

    public int getPASDistanceRL() {
        return getSignalValue(134).intValue();
    }

    public int getPASDistanceRLMiddle() {
        return getSignalValue(129).intValue();
    }

    public int getPASDistanceRLSide() {
        return getSignalValue(138).intValue();
    }

    public int getPASDistanceRR() {
        return getSignalValue(133).intValue();
    }

    public int getPASDistanceRRMiddle() {
        return getSignalValue(130).intValue();
    }

    public int getPASDistanceRRSide() {
        return getSignalValue(137).intValue();
    }

    public ICarParking.PASErrorFlag getPASErrorFlagOfEEPROM() {
        return ICarParking.PASErrorFlag.valueOf(getSignalValue(145).intValue());
    }

    public ICarParking.PASErrorFlag getPASErrorFlagOfIGNVoltage() {
        return ICarParking.PASErrorFlag.valueOf(getSignalValue(146).intValue());
    }

    public ICarParking.PASErrorFlag getPASErrorFlagRL() {
        return ICarParking.PASErrorFlag.valueOf(getSignalValue(144).intValue());
    }

    public ICarParking.PASErrorFlag getPASErrorFlagRLMiddle() {
        return ICarParking.PASErrorFlag.valueOf(getSignalValue(142).intValue());
    }

    public ICarParking.PASErrorFlag getPASErrorFlagRR() {
        return ICarParking.PASErrorFlag.valueOf(getSignalValue(143).intValue());
    }

    public ICarParking.PASErrorFlag getPASErrorFlagRRMiddle() {
        return ICarParking.PASErrorFlag.valueOf(getSignalValue(141).intValue());
    }

    public ICarParking.PASSystemFailureFlag getPASSystemFailureFlag() {
        return ICarParking.PASSystemFailureFlag.valueOf(getSignalValue(127).intValue());
    }

    public ICarParking.PASSystemType getPASSystemType() {
        return ICarParking.PASSystemType.valueOf(getSignalValue(126).intValue());
    }

    public ICarParking.PASWarnTone getPASWarnTone() {
        return ICarParking.PASWarnTone.valueOf(getSignalValue(128).intValue());
    }

    public IBaseApi.EnableState getParkingRadarAlarmEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(254).intValue());
    }

    public IBaseApi.EnableState getParkingVoiceSwitchEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(256).intValue());
    }

    public boolean performParkingRadarAlarmButtonClick() {
        return setSignalValue(255, (byte) 1);
    }

    public void registerListener(CarParkingListener carParkingListener) {
        registerReceiver(carParkingListener);
    }

    public boolean setParkingVoiceSwitchEnabled(boolean z) {
        return setSignalValue(257, z ? (byte) 2 : (byte) 1);
    }

    public void unregisterListener(CarParkingListener carParkingListener) {
        unregisterReceiver(carParkingListener);
    }
}
