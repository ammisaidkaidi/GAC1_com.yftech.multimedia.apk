package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarSwc;

/* JADX INFO: loaded from: classes4.dex */
public class CarSwcManager extends BaseApiManager implements ICarSwc {
    private static final String TAG = "CarSwcManager";
    private static CarSwcManager sMe;

    public static abstract class CarSwcListener {
        @SignalReceiver(signalId = 306)
        public void onSwcAngleChanged(float f) {
        }

        @SignalReceiver(signalId = 338)
        public void onSwcAngleVdEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 304)
        public void onSwcHeatStateChanged(ICarSwc.SwcHeatState swcHeatState) {
        }
    }

    private CarSwcManager() {
    }

    public static synchronized CarSwcManager get() {
        CarSwcManager carSwcManager;
        if (sMe != null) {
            carSwcManager = sMe;
        } else {
            carSwcManager = new CarSwcManager();
            sMe = carSwcManager;
        }
        return carSwcManager;
    }

    public float getSwcAngle() {
        return getSignalValue(306).floatValue();
    }

    public IBaseApi.EnableState getSwcAngleVdEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(338).intValue());
    }

    public ICarSwc.SwcHeatState getSwcHeatState() {
        return ICarSwc.SwcHeatState.valueOf(getSignalValue(304).intValue());
    }

    public boolean performSwcStarKeyButtonClick() {
        return setSignalValue(284, (byte) 1);
    }

    public boolean performSwcStarKeyVDButtonClick() {
        return setSignalValue(285, (byte) 1);
    }

    public void registerListener(CarSwcListener carSwcListener) {
        registerReceiver(carSwcListener);
    }

    public boolean setSwcHeatStateEnabled(boolean z) {
        return setSignalValue(305, z ? (byte) 1 : (byte) 2);
    }

    public void unregisterListener(CarSwcListener carSwcListener) {
        unregisterReceiver(carSwcListener);
    }
}
