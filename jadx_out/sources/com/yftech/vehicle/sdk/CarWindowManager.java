package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarWindow;

/* JADX INFO: loaded from: classes4.dex */
public class CarWindowManager extends BaseApiManager implements ICarWindow {
    private static final String TAG = "CarWindowManager";
    private static CarWindowManager sMe;

    public static abstract class CarWindowListener {
        @SignalReceiver(signalId = 160)
        public void onSunroofWindowAutoCloseEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 162)
        public void onWindowOpenPercentFLChanged(int i) {
        }

        @SignalReceiver(signalId = 166)
        public void onWindowOpenPercentFRChanged(int i) {
        }

        @SignalReceiver(signalId = 170)
        public void onWindowOpenPercentRLChanged(int i) {
        }

        @SignalReceiver(signalId = 174)
        public void onWindowOpenPercentRRChanged(int i) {
        }

        @SignalReceiver(signalId = 163)
        public void onWindowOperationStateFLChanged(ICarWindow.WindowOperationState windowOperationState) {
        }

        @SignalReceiver(signalId = 167)
        public void onWindowOperationStateFRChanged(ICarWindow.WindowOperationState windowOperationState) {
        }

        @SignalReceiver(signalId = 171)
        public void onWindowOperationStateRLChanged(ICarWindow.WindowOperationState windowOperationState) {
        }

        @SignalReceiver(signalId = 175)
        public void onWindowOperationStateRRChanged(ICarWindow.WindowOperationState windowOperationState) {
        }

        @SignalReceiver(signalId = 164)
        public void onWindowOverheatStateFLChanged(boolean z) {
        }

        @SignalReceiver(signalId = 168)
        public void onWindowOverheatStateFRChanged(boolean z) {
        }

        @SignalReceiver(signalId = 172)
        public void onWindowOverheatStateRLChanged(boolean z) {
        }

        @SignalReceiver(signalId = 176)
        public void onWindowOverheatStateRRChanged(boolean z) {
        }

        @SignalReceiver(signalId = 247)
        public void onWindowRoofOpenOpertionChanged(ICarWindow.WindowRoofOpenOpertion windowRoofOpenOpertion) {
        }

        @SignalReceiver(signalId = 246)
        public void onWindowRoofOpenPercentChanged(int i) {
        }

        @SignalReceiver(signalId = 250)
        public void onWindowRoofShadeOpenOpertionChanged(ICarWindow.WindowRoofShadeOpenOpertion windowRoofShadeOpenOpertion) {
        }

        @SignalReceiver(signalId = 249)
        public void onWindowRoofShadeOpenPercentChanged(int i) {
        }

        @SignalReceiver(signalId = 161)
        public void onWindowStateFLChanged(ICarWindow.WindowState windowState) {
        }

        @SignalReceiver(signalId = 165)
        public void onWindowStateFRChanged(ICarWindow.WindowState windowState) {
        }

        @SignalReceiver(signalId = 169)
        public void onWindowStateRLChanged(ICarWindow.WindowState windowState) {
        }

        @SignalReceiver(signalId = 173)
        public void onWindowStateRRChanged(ICarWindow.WindowState windowState) {
        }
    }

    private CarWindowManager() {
    }

    public static synchronized CarWindowManager get() {
        CarWindowManager carWindowManager;
        if (sMe != null) {
            carWindowManager = sMe;
        } else {
            carWindowManager = new CarWindowManager();
            sMe = carWindowManager;
        }
        return carWindowManager;
    }

    public IBaseApi.EnableState getSunroofWindowAutoCloseEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(160).intValue());
    }

    public int getWindowOpenPercentFL() {
        return getSignalValue(162).intValue();
    }

    public int getWindowOpenPercentFR() {
        return getSignalValue(166).intValue();
    }

    public int getWindowOpenPercentRL() {
        return getSignalValue(170).intValue();
    }

    public int getWindowOpenPercentRR() {
        return getSignalValue(174).intValue();
    }

    public ICarWindow.WindowOperationState getWindowOperationStateFL() {
        return ICarWindow.WindowOperationState.valueOf(getSignalValue(163).intValue());
    }

    public ICarWindow.WindowOperationState getWindowOperationStateFR() {
        return ICarWindow.WindowOperationState.valueOf(getSignalValue(167).intValue());
    }

    public ICarWindow.WindowOperationState getWindowOperationStateRL() {
        return ICarWindow.WindowOperationState.valueOf(getSignalValue(171).intValue());
    }

    public ICarWindow.WindowOperationState getWindowOperationStateRR() {
        return ICarWindow.WindowOperationState.valueOf(getSignalValue(175).intValue());
    }

    public boolean getWindowOverheatStateFL() {
        return getSignalValue(164).byteValue() != 0;
    }

    public boolean getWindowOverheatStateFR() {
        return getSignalValue(168).byteValue() != 0;
    }

    public boolean getWindowOverheatStateRL() {
        return getSignalValue(172).byteValue() != 0;
    }

    public boolean getWindowOverheatStateRR() {
        return getSignalValue(176).byteValue() != 0;
    }

    public ICarWindow.WindowRoofOpenOpertion getWindowRoofOpenOpertion() {
        return ICarWindow.WindowRoofOpenOpertion.valueOf(getSignalValue(247).intValue());
    }

    public int getWindowRoofOpenPercent() {
        return getSignalValue(246).intValue();
    }

    public ICarWindow.WindowRoofShadeOpenOpertion getWindowRoofShadeOpenOpertion() {
        return ICarWindow.WindowRoofShadeOpenOpertion.valueOf(getSignalValue(250).intValue());
    }

    public int getWindowRoofShadeOpenPercent() {
        return getSignalValue(249).intValue();
    }

    public ICarWindow.WindowState getWindowStateFL() {
        return ICarWindow.WindowState.valueOf(getSignalValue(161).intValue());
    }

    public ICarWindow.WindowState getWindowStateFR() {
        return ICarWindow.WindowState.valueOf(getSignalValue(165).intValue());
    }

    public ICarWindow.WindowState getWindowStateRL() {
        return ICarWindow.WindowState.valueOf(getSignalValue(169).intValue());
    }

    public ICarWindow.WindowState getWindowStateRR() {
        return ICarWindow.WindowState.valueOf(getSignalValue(173).intValue());
    }

    public void registerListener(CarWindowListener carWindowListener) {
        registerReceiver(carWindowListener);
    }

    public boolean setSunroofWindowAutoCloseEnabled(boolean z) {
        return setSignalValue(63, z ? (byte) 2 : (byte) 1);
    }

    public boolean setWindowOpenPercentFL(ICarWindow.WindowOpenPercent windowOpenPercent) {
        return setSignalValue(64, windowOpenPercent.value);
    }

    public boolean setWindowOpenPercentFLVdSwitch(boolean z) {
        return setSignalValue(364, z ? (byte) 1 : (byte) 0);
    }

    public boolean setWindowOpenPercentFR(ICarWindow.WindowOpenPercent windowOpenPercent) {
        return setSignalValue(65, windowOpenPercent.value);
    }

    public boolean setWindowOpenPercentFRVdSwitch(boolean z) {
        return setSignalValue(365, z ? (byte) 1 : (byte) 0);
    }

    public boolean setWindowOpenPercentRL(ICarWindow.WindowOpenPercent windowOpenPercent) {
        return setSignalValue(66, windowOpenPercent.value);
    }

    public boolean setWindowOpenPercentRLVdSwitch(boolean z) {
        return setSignalValue(366, z ? (byte) 1 : (byte) 0);
    }

    public boolean setWindowOpenPercentRR(ICarWindow.WindowOpenPercent windowOpenPercent) {
        return setSignalValue(67, windowOpenPercent.value);
    }

    public boolean setWindowOpenPercentRRVdSwitch(boolean z) {
        return setSignalValue(367, z ? (byte) 1 : (byte) 0);
    }

    public boolean setWindowRoofOpenState(ICarWindow.WindowRoofOpenState windowRoofOpenState) {
        return setSignalValue(68, windowRoofOpenState.value);
    }

    public boolean setWindowRoofShadeOpenPercent(int i) {
        return setSignalValue(248, i);
    }

    public boolean setWindowRoofShadeOpenState(ICarWindow.WindowRoofShadeOpenState windowRoofShadeOpenState) {
        return setSignalValue(248, windowRoofShadeOpenState.value);
    }

    public boolean setWindowStateFL(ICarWindow.WindowState windowState) {
        return setSignalValue(202, windowState.value);
    }

    public boolean setWindowStateFR(ICarWindow.WindowState windowState) {
        return setSignalValue(203, windowState.value);
    }

    public boolean setWindowStateRL(ICarWindow.WindowState windowState) {
        return setSignalValue(204, windowState.value);
    }

    public boolean setWindowStateRR(ICarWindow.WindowState windowState) {
        return setSignalValue(205, windowState.value);
    }

    public void unregisterListener(CarWindowListener carWindowListener) {
        unregisterReceiver(carWindowListener);
    }
}
