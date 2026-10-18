package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseCmd;
import com.yftech.vehicle.internal.adapter.ICarPower;

/* JADX INFO: loaded from: classes4.dex */
public class CarPowerManager extends BaseApiManager implements ICarPower {
    private static final String TAG = "CarPowerManager";
    private static CarPowerManager sMe;

    public static abstract class CarPowerListener {
        @SignalReceiver(signalId = IBaseCmd.POWER_MODE)
        public void onPowerModeChanged(ICarPower.PowerMode powerMode) {
        }

        @SignalReceiver(signalId = 9997)
        public void onPowerStateChanged(ICarPower.PowerState powerState) {
        }
    }

    private CarPowerManager() {
    }

    public static synchronized CarPowerManager get() {
        CarPowerManager carPowerManager;
        if (sMe != null) {
            carPowerManager = sMe;
        } else {
            carPowerManager = new CarPowerManager();
            sMe = carPowerManager;
        }
        return carPowerManager;
    }

    public ICarPower.PowerMode getPowerMode() {
        return ICarPower.PowerMode.valueOf(getSignalValue(IBaseCmd.POWER_MODE).intValue());
    }

    public ICarPower.PowerState getPowerState() {
        return ICarPower.PowerState.valueOf(getSignalValue(9997).intValue());
    }

    public void registerListener(CarPowerListener carPowerListener) {
        registerReceiver(carPowerListener);
    }

    public void unregisterListener(CarPowerListener carPowerListener) {
        unregisterReceiver(carPowerListener);
    }
}
