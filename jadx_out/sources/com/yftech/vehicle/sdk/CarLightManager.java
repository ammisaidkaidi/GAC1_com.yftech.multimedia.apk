package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarLight;

/* JADX INFO: loaded from: classes4.dex */
public class CarLightManager extends BaseApiManager implements ICarLight {
    private static final String TAG = "CarLightManager";
    private static CarLightManager sMe;

    public static abstract class CarLightListener {
        @SignalReceiver(signalId = 553)
        public void onADBEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 275)
        public void onAtmosphereLampBrightnessChanged(int i) {
        }

        @SignalReceiver(signalId = 277)
        public void onAtmosphereLampColorChanged(int i) {
        }

        @SignalReceiver(signalId = 281)
        public void onAtmosphereLampMusicModeEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 279)
        public void onAtmosphereLampWorkModeChanged(ICarLight.AtmosphereLampWorkMode atmosphereLampWorkMode) {
        }

        @SignalReceiver(signalId = 495)
        public void onAutoHoldLightEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 198, valueToEnabledState = true)
        public void onBrakeLampStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 178)
        public void onHeadLightDelayEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 177)
        public void onIHBCEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 199, valueToEnabledState = true)
        public void onILBCStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 511)
        public void onLightShowMainLampSwitchStChanged(ICarLight.MainLampSwitchSt mainLampSwitchSt) {
        }

        @SignalReceiver(signalId = 508)
        public void onLightShowModeChanged(ICarLight.LightShowMode lightShowMode) {
        }

        @SignalReceiver(signalId = 209, valueToEnabledState = true)
        public void onLowLightStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 262, valueToEnabledState = true)
        public void onSmallLightStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 220, valueToEnabledState = true)
        public void onTopLightStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 497)
        public void onWelLightEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = ICarLight.WEL_LIGHT_MODE)
        public void onWelLightModeChanged(ICarLight.WelLightMode welLightMode) {
        }
    }

    private CarLightManager() {
    }

    public static synchronized CarLightManager get() {
        CarLightManager carLightManager;
        if (sMe != null) {
            carLightManager = sMe;
        } else {
            carLightManager = new CarLightManager();
            sMe = carLightManager;
        }
        return carLightManager;
    }

    public IBaseApi.EnableState getADBEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(553).intValue());
    }

    public int getAtmosphereLampBrightness() {
        return getSignalValue(275).intValue();
    }

    public int getAtmosphereLampColor() {
        return getSignalValue(277).intValue();
    }

    public IBaseApi.EnableState getAtmosphereLampMusicModeEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(281).intValue());
    }

    public ICarLight.AtmosphereLampWorkMode getAtmosphereLampWorkMode() {
        return ICarLight.AtmosphereLampWorkMode.valueOf(getSignalValue(279).intValue());
    }

    public IBaseApi.EnableState getAutoHoldLightEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(495).intValue());
    }

    public IBaseApi.EnableState getHeadLightDelayEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(178).intValue());
    }

    public IBaseApi.EnableState getIHBCEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(177).intValue());
    }

    public ICarLight.MainLampSwitchSt getLightShowMainLampSwitchSt() {
        return ICarLight.MainLampSwitchSt.valueOf(getSignalValue(511).intValue());
    }

    public ICarLight.LightShowMode getLightShowMode() {
        return ICarLight.LightShowMode.valueOf(getSignalValue(508).intValue());
    }

    public IBaseApi.EnableState getWelLightEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(497).intValue());
    }

    public ICarLight.WelLightMode getWelLightMode() {
        return ICarLight.WelLightMode.valueOf(getSignalValue(ICarLight.WEL_LIGHT_MODE).intValue());
    }

    public boolean isBrakeLampEnabled() {
        return getSignalValue(198).byteValue() == 1;
    }

    public boolean isILBCEnabled() {
        return getSignalValue(199).byteValue() == 1;
    }

    public boolean isLowLightEnabled() {
        return getSignalValue(209).byteValue() == 1;
    }

    public boolean isSmallLightEnabled() {
        return getSignalValue(262).byteValue() == 1;
    }

    public boolean isTopLightEnabled() {
        return getSignalValue(220).byteValue() == 1;
    }

    public void registerListener(CarLightListener carLightListener) {
        registerReceiver(carLightListener);
    }

    public boolean setADBEnabled(boolean z) {
        return setSignalValue(554, z ? (byte) 2 : (byte) 1);
    }

    public boolean setAtmosphereLampBrightness(int i) {
        return setSignalValue(276, i);
    }

    public boolean setAtmosphereLampColor(int i) {
        return setSignalValue(278, i);
    }

    public boolean setAtmosphereLampDriveModeEnabled(boolean z) {
        return setSignalValue(280, z ? (byte) 2 : (byte) 1);
    }

    public boolean setAtmosphereLampMusicModeEnabled(boolean z) {
        return setSignalValue(282, z ? (byte) 1 : (byte) 2);
    }

    public boolean setAtmosphereLampStateEnabled(boolean z) {
        return setSignalValue(274, z ? (byte) 1 : (byte) 2);
    }

    public boolean setAtmosphereLampWorkMode(ICarLight.AtmosphereLampWorkMode atmosphereLampWorkMode) {
        return setSignalValue(351, atmosphereLampWorkMode.value);
    }

    public boolean setAutoHoldLightFunCfgEnabled(boolean z) {
        return setSignalValue(496, z ? (byte) 2 : (byte) 1);
    }

    public boolean setHeadLightDelayEnabled(boolean z) {
        return setSignalValue(70, z ? (byte) 2 : (byte) 1);
    }

    public boolean setIHBCEnabled(boolean z) {
        return setSignalValue(69, z ? (byte) 2 : (byte) 1);
    }

    public boolean setILBCEnabled(boolean z) {
        return setSignalValue(200, z ? (byte) 2 : (byte) 1);
    }

    public boolean setLightShowFunCmd(ICarLight.LightShowFunCmd lightShowFunCmd) {
        return setSignalValue(507, lightShowFunCmd.value);
    }

    public boolean setLightShowMode(ICarLight.LightShowMode lightShowMode) {
        return setSignalValue(509, lightShowMode.value);
    }

    public boolean setLightShowTime(int i) {
        return setSignalValue(510, i);
    }

    public boolean setTopLightEnabled(boolean z) {
        return setSignalValue(221, z ? (byte) 2 : (byte) 1);
    }

    public boolean setWelLightFunCfgEnabled(boolean z) {
        return setSignalValue(498, z ? (byte) 2 : (byte) 1);
    }

    public boolean setWelLightMode(ICarLight.WelLightMode welLightMode) {
        return setSignalValue(500, welLightMode.value);
    }

    public void unregisterListener(CarLightListener carLightListener) {
        unregisterReceiver(carLightListener);
    }
}
