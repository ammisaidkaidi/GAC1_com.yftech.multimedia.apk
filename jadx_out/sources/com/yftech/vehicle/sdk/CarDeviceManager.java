package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarDevice;

/* JADX INFO: loaded from: classes4.dex */
public class CarDeviceManager extends BaseApiManager implements ICarDevice {
    private static final String TAG = "CarDeviceManager";
    private static CarDeviceManager sMe;

    public static abstract class CarDeviceListener {
        @SignalReceiver(signalId = 532)
        public void onBSVDisplayStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 521)
        public void onPASBackParkDisplayStateChanged(ICarDevice.PASBackParkDisplayState pASBackParkDisplayState) {
        }

        @SignalReceiver(signalId = 408)
        public void onRecorderDisplayStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 409)
        public void onRecorderRecordStateChanged(IBaseApi.EnableState enableState) {
        }
    }

    private CarDeviceManager() {
    }

    public static synchronized CarDeviceManager get() {
        CarDeviceManager carDeviceManager;
        if (sMe != null) {
            carDeviceManager = sMe;
        } else {
            carDeviceManager = new CarDeviceManager();
            sMe = carDeviceManager;
        }
        return carDeviceManager;
    }

    public IBaseApi.EnableState getBSVDisplayState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(532).intValue());
    }

    public ICarDevice.PASBackParkDisplayState getPASBackParkDisplayState() {
        return ICarDevice.PASBackParkDisplayState.valueOf(getSignalValue(521).intValue());
    }

    public IBaseApi.EnableState getRecorderDisplayState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(408).intValue());
    }

    public IBaseApi.EnableState getRecorderRecordState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(409).intValue());
    }

    public boolean perform360SvmButtonClick() {
        return setSignalValue(308, (byte) 1);
    }

    public boolean performAPAButtonClick() {
        return setSignalValue(518, (byte) 1);
    }

    public boolean performBSVButtonClick() {
        return setSignalValue(535, (byte) 1);
    }

    public void registerListener(CarDeviceListener carDeviceListener) {
        registerReceiver(carDeviceListener);
    }

    public boolean setBSVDisplaySwitch(boolean z) {
        return setSignalValue(530, z ? (byte) 1 : (byte) 0);
    }

    public boolean setBsvFunctionCmd(ICarDevice.BsvFunctionCmd bsvFunctionCmd) {
        return setSignalValue(531, bsvFunctionCmd.value);
    }

    public boolean setPAAMode(ICarDevice.PAAMode pAAMode) {
        return setSignalValue(517, pAAMode.value);
    }

    public boolean setRecorderEnterCmd(ICarDevice.RecorderEnterCmd recorderEnterCmd) {
        return setSignalValue(410, recorderEnterCmd.value);
    }

    public boolean setSvmFunctionCmd(ICarDevice.SvmFunctionCmd svmFunctionCmd) {
        return setSignalValue(411, svmFunctionCmd.value);
    }

    public void unregisterListener(CarDeviceListener carDeviceListener) {
        unregisterReceiver(carDeviceListener);
    }
}
