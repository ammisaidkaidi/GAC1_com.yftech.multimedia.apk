package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarTire;

/* JADX INFO: loaded from: classes4.dex */
public class CarTireManager extends BaseApiManager implements ICarTire {
    private static final String TAG = "CarTireManager";
    private static CarTireManager sMe;

    public static abstract class CarTireListener {
        @SignalReceiver(signalId = 339)
        public void onWheelSpeedFLChanged(float f) {
        }

        @SignalReceiver(signalId = 340)
        public void onWheelSpeedFLEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 341)
        public void onWheelSpeedFRChanged(float f) {
        }

        @SignalReceiver(signalId = 342)
        public void onWheelSpeedFREnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 343)
        public void onWheelSpeedRLChanged(float f) {
        }

        @SignalReceiver(signalId = 344)
        public void onWheelSpeedRLEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 345)
        public void onWheelSpeedRRChanged(float f) {
        }

        @SignalReceiver(signalId = 346)
        public void onWheelSpeedRREnableStateChanged(IBaseApi.EnableState enableState) {
        }
    }

    private CarTireManager() {
    }

    public static synchronized CarTireManager get() {
        CarTireManager carTireManager;
        if (sMe != null) {
            carTireManager = sMe;
        } else {
            carTireManager = new CarTireManager();
            sMe = carTireManager;
        }
        return carTireManager;
    }

    public float getWheelSpeedFL() {
        return getSignalValue(339).floatValue();
    }

    public IBaseApi.EnableState getWheelSpeedFLEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(340).intValue());
    }

    public float getWheelSpeedFR() {
        return getSignalValue(341).floatValue();
    }

    public IBaseApi.EnableState getWheelSpeedFREnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(342).intValue());
    }

    public float getWheelSpeedRL() {
        return getSignalValue(343).floatValue();
    }

    public IBaseApi.EnableState getWheelSpeedRLEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(344).intValue());
    }

    public float getWheelSpeedRR() {
        return getSignalValue(345).floatValue();
    }

    public IBaseApi.EnableState getWheelSpeedRREnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(346).intValue());
    }

    public void registerListener(CarTireListener carTireListener) {
        registerReceiver(carTireListener);
    }

    public void unregisterListener(CarTireListener carTireListener) {
        unregisterReceiver(carTireListener);
    }
}
