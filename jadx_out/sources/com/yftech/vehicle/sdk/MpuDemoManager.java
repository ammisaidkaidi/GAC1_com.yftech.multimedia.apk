package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseCmd;
import com.yftech.vehicle.internal.adapter.IDemo;

/* JADX INFO: loaded from: classes4.dex */
public class MpuDemoManager extends BaseApiManager implements IDemo {
    private static final String TAG = "CarMiscManager";
    private static MpuDemoManager sMe;

    public static abstract class CarDemoListener {
        @SignalReceiver(signalId = IBaseCmd.GAME_BRAKE_CTL_PERCENT_GET)
        public void onGameBrakeCtlPercentChanged(float f) {
        }

        @SignalReceiver(signalId = IBaseCmd.GAME_PEDAL_CTL_PERCENT_GET)
        public void onGamePedalCtlPercentChanged(float f) {
        }

        @SignalReceiver(signalId = IBaseCmd.GAME_SWC_CTL_ANGLE_GET)
        public void onGameSwcCtlAngleChanged(float f) {
        }
    }

    private MpuDemoManager() {
    }

    public static synchronized MpuDemoManager get() {
        MpuDemoManager mpuDemoManager;
        if (sMe != null) {
            mpuDemoManager = sMe;
        } else {
            mpuDemoManager = new MpuDemoManager();
            sMe = mpuDemoManager;
        }
        return mpuDemoManager;
    }

    public float getGameBrakeCtlPercent() {
        return getSignalValue(IBaseCmd.GAME_BRAKE_CTL_PERCENT_GET).floatValue();
    }

    public float getGamePedalCtlPercent() {
        return getSignalValue(IBaseCmd.GAME_PEDAL_CTL_PERCENT_GET).floatValue();
    }

    public float getGameSwcCtlAngle() {
        return getSignalValue(IBaseCmd.GAME_SWC_CTL_ANGLE_GET).floatValue();
    }

    public void registerListener(CarDemoListener carDemoListener) {
        registerReceiver(carDemoListener);
    }

    public void unregisterListener(CarDemoListener carDemoListener) {
        unregisterReceiver(carDemoListener);
    }
}
