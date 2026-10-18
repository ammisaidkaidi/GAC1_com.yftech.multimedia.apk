package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IAirCondition;
import com.yftech.vehicle.internal.adapter.IBaseApi;

/* JADX INFO: loaded from: classes4.dex */
public class AirConditionManager extends BaseApiManager implements IAirCondition {
    private static final String TAG = "AirConditionManager";
    private static AirConditionManager sMe;

    public static abstract class AirConditionListener {
        @SignalReceiver(signalId = 78, valueToEnabledState = true)
        public void onACStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 401)
        public void onAcDisplayPopupChanged(IAirCondition.AcDisplayPopup acDisplayPopup) {
        }

        @SignalReceiver(signalId = 85)
        public void onAirCirculationModeOfAutoModeChanged(IAirCondition.AirCirculationMode airCirculationMode) {
        }

        @SignalReceiver(signalId = 82)
        public void onAirCirculationStateChanged(IAirCondition.AirCirculationState airCirculationState) {
        }

        @SignalReceiver(signalId = 263)
        public void onAirSelfDryingEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 265)
        public void onAirUnlockVentilationEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = -1, valueToEnabledState = true)
        public void onAutoDeforstPresentStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 83)
        public void onAutoModeChanged(IAirCondition.AutoMode autoMode) {
        }

        @SignalReceiver(signalId = 194)
        public void onBatCoolStateChanged(float f) {
        }

        @SignalReceiver(signalId = 192)
        public void onBatFhWorkStateChanged(float f) {
        }

        @SignalReceiver(signalId = 193)
        public void onBatPtcWorkStateChanged(float f) {
        }

        @SignalReceiver(signalId = 191, valueToEnabledState = true)
        public void onBodyDisalarmVentStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 86)
        public void onComfortCurveModeChanged(IAirCondition.ComfortCurveMode comfortCurveMode) {
        }

        @SignalReceiver(signalId = 557)
        public void onCupholderAutoModeChanged(IAirCondition.CupholderAutoMode cupholderAutoMode) {
        }

        @SignalReceiver(signalId = 555)
        public void onCupholderHeatCoolModeChanged(IAirCondition.CupholderHeatCoolMode cupholderHeatCoolMode) {
        }

        @SignalReceiver(signalId = 74)
        public void onDriverTemperatureChanged(float f) {
        }

        @SignalReceiver(signalId = 80, valueToEnabledState = true)
        public void onDualSyncStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = -1)
        public void onEcoModeChanged(IAirCondition.EcoMode ecoMode) {
        }

        @SignalReceiver(signalId = 196)
        public void onEnvironmentTemperatureChanged(float f) {
        }

        @SignalReceiver(signalId = 84)
        public void onFanSpeedOfAutoModeChanged(IAirCondition.FanSpeed fanSpeed) {
        }

        @SignalReceiver(signalId = 404, valueToEnabledState = true)
        public void onFrontDefrostStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 81, valueToEnabledState = true)
        public void onIonStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = IAirCondition.ONE_BUTTON_VENTLAION_STATE, valueToEnabledState = true)
        public void onOneBtnVentlaionStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 195)
        public void onOutAirQualityLevelChanged(float f) {
        }

        @SignalReceiver(signalId = 75)
        public void onPassengerTemperatureChanged(float f) {
        }

        @SignalReceiver(signalId = -1, valueToEnabledState = true)
        public void onPowerStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 231, valueToEnabledState = true)
        public void onRearAcAutoModeStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 236)
        public void onRearAcTemperatureChanged(float f) {
        }

        @SignalReceiver(signalId = 238)
        public void onRearAcWindModeChanged(IAirCondition.WindMode windMode) {
        }

        @SignalReceiver(signalId = 233)
        public void onRearAcWindSpeedChanged(IAirCondition.WindSpeed windSpeed) {
        }

        @SignalReceiver(signalId = 79, valueToEnabledState = true)
        public void onRearDefrostStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = -1)
        public void onWORKINGPOWERChanged(float f) {
        }

        @SignalReceiver(signalId = 77)
        public void onWindModeChanged(IAirCondition.WindMode windMode) {
        }

        @SignalReceiver(signalId = 76)
        public void onWindSpeedChanged(IAirCondition.WindSpeed windSpeed) {
        }
    }

    private AirConditionManager() {
    }

    public static synchronized AirConditionManager get() {
        AirConditionManager airConditionManager;
        if (sMe != null) {
            airConditionManager = sMe;
        } else {
            airConditionManager = new AirConditionManager();
            sMe = airConditionManager;
        }
        return airConditionManager;
    }

    public IAirCondition.AcDisplayPopup getAcDisplayPopup() {
        return IAirCondition.AcDisplayPopup.valueOf(getSignalValue(401).intValue());
    }

    public IAirCondition.AirCirculationMode getAirCirculationModeOfAutoMode() {
        return IAirCondition.AirCirculationMode.valueOf(getSignalValue(85).intValue());
    }

    public IAirCondition.AirCirculationState getAirCirculationState() {
        return IAirCondition.AirCirculationState.valueOf(getSignalValue(82).intValue());
    }

    public IBaseApi.EnableState getAirSelfDryingEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(263).intValue());
    }

    public IBaseApi.EnableState getAirUnlockVentilationEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(265).intValue());
    }

    public IAirCondition.AutoMode getAutoMode() {
        return IAirCondition.AutoMode.valueOf(getSignalValue(83).intValue());
    }

    public float getBatCoolState() {
        return getSignalValue(194).floatValue();
    }

    public float getBatFhWorkState() {
        return getSignalValue(192).floatValue();
    }

    public float getBatPtcWorkState() {
        return getSignalValue(193).floatValue();
    }

    public IAirCondition.ComfortCurveMode getComfortCurveMode() {
        return IAirCondition.ComfortCurveMode.valueOf(getSignalValue(86).intValue());
    }

    public IAirCondition.CupholderAutoMode getCupholderAutoMode() {
        return IAirCondition.CupholderAutoMode.valueOf(getSignalValue(557).intValue());
    }

    public IAirCondition.CupholderHeatCoolMode getCupholderHeatCoolMode() {
        return IAirCondition.CupholderHeatCoolMode.valueOf(getSignalValue(555).intValue());
    }

    public float getDriverTemperature() {
        return getSignalValue(74).floatValue();
    }

    public IAirCondition.EcoMode getEcoMode() {
        return IAirCondition.EcoMode.valueOf(getSignalValue(-1).intValue());
    }

    public float getEnvironmentTemperature() {
        return getSignalValue(196).floatValue();
    }

    public IAirCondition.FanSpeed getFanSpeedOfAutoMode() {
        return IAirCondition.FanSpeed.valueOf(getSignalValue(84).intValue());
    }

    public float getOutAirQualityLevel() {
        return getSignalValue(195).floatValue();
    }

    public float getPassengerTemperature() {
        return getSignalValue(75).floatValue();
    }

    public float getRearAcTemperature() {
        return getSignalValue(236).floatValue();
    }

    public IAirCondition.WindMode getRearAcWindMode() {
        return IAirCondition.WindMode.valueOf(getSignalValue(238).intValue());
    }

    public IAirCondition.WindSpeed getRearAcWindSpeed() {
        return IAirCondition.WindSpeed.valueOf(getSignalValue(233).intValue());
    }

    public float getWORKINGPOWER() {
        return getSignalValue(-1).floatValue();
    }

    public IAirCondition.WindMode getWindMode() {
        return IAirCondition.WindMode.valueOf(getSignalValue(77).intValue());
    }

    public IAirCondition.WindSpeed getWindSpeed() {
        return IAirCondition.WindSpeed.valueOf(getSignalValue(76).intValue());
    }

    public boolean isACEnabled() {
        return getSignalValue(78).byteValue() == 1;
    }

    public boolean isAutoDeforstPresentEnabled() {
        return getSignalValue(-1).byteValue() == 1;
    }

    public boolean isBodyDisalarmVentEnabled() {
        return getSignalValue(191).byteValue() == 1;
    }

    public boolean isDualSyncEnabled() {
        return getSignalValue(80).byteValue() == 1;
    }

    public boolean isFrontDefrostEnabled() {
        return getSignalValue(404).byteValue() == 1;
    }

    public boolean isIonEnabled() {
        return getSignalValue(81).byteValue() == 1;
    }

    public boolean isOneBtnVentlaionEnabled() {
        return getSignalValue(IAirCondition.ONE_BUTTON_VENTLAION_STATE).byteValue() == 1;
    }

    public boolean isPowerEnabled() {
        return getSignalValue(-1).byteValue() == 1;
    }

    public boolean isRearAcAutoModeEnabled() {
        return getSignalValue(231).byteValue() == 1;
    }

    public boolean isRearDefrostEnabled() {
        return getSignalValue(79).byteValue() == 1;
    }

    public boolean performACButtonClick() {
        return setSignalValue(6, (byte) 1);
    }

    public boolean performAutoButtonClick() {
        return setSignalValue(11, (byte) 1);
    }

    public boolean performAutoDeforstPresentButtonClick() {
        return setSignalValue(-1, (byte) 1);
    }

    public boolean performBodyDisalarmVentButtonClick() {
        return setSignalValue(-1, (byte) 1);
    }

    public boolean performCirculationButtonClick() {
        return setSignalValue(10, (byte) 1);
    }

    public boolean performDualSyncButtonClick() {
        return setSignalValue(8, (byte) 1);
    }

    public boolean performFrontDefrostButtonClick() {
        return setSignalValue(5, (byte) 1);
    }

    public boolean performIonButtonClick() {
        return setSignalValue(9, (byte) 1);
    }

    public boolean performOffButtonClick() {
        return setSignalValue(3, (byte) 1);
    }

    public boolean performPowerButtonClick() {
        return setSignalValue(-1, (byte) 1);
    }

    public boolean performRearAcAutoModeButtonClick() {
        return setSignalValue(232, (byte) 1);
    }

    public boolean performRearAcOffButtonClick() {
        return setSignalValue(235, (byte) 1);
    }

    public boolean performRearDefrostButtonClick() {
        return setSignalValue(7, (byte) 1);
    }

    public boolean performTemperatureDecButtonClick() {
        return setSignalValue(IAirCondition.TEMPERATURE_DEC_BUTTON_SETTING, (byte) 1);
    }

    public boolean performTemperatureFRDecButtonClick() {
        return setSignalValue(524, (byte) 1);
    }

    public boolean performTemperatureFRIncButtonClick() {
        return setSignalValue(522, (byte) 1);
    }

    public boolean performTemperatureIncButtonClick() {
        return setSignalValue(432, (byte) 1);
    }

    public boolean performWindModeButtonClick() {
        return setSignalValue(534, (byte) 1);
    }

    public boolean performWindSpeedDecButtonClick() {
        return setSignalValue(537, (byte) 1);
    }

    public boolean performWindSpeedIncButtonClick() {
        return setSignalValue(536, (byte) 1);
    }

    public void registerListener(AirConditionListener airConditionListener) {
        registerReceiver(airConditionListener);
    }

    public boolean setAirCirculationModeOfAutoMode(IAirCondition.AirCirculationMode airCirculationMode) {
        return setSignalValue(13, airCirculationMode.value);
    }

    public boolean setAirSelfDryingEnabled(boolean z) {
        return setSignalValue(264, z ? (byte) 2 : (byte) 1);
    }

    public boolean setAirUnlockVentilationEnabled(boolean z) {
        return setSignalValue(266, z ? (byte) 2 : (byte) 1);
    }

    public boolean setComfortCurveMode(IAirCondition.ComfortCurveMode comfortCurveMode) {
        return setSignalValue(14, comfortCurveMode.value);
    }

    public boolean setCupholderAutoMode(IAirCondition.CupholderAutoMode cupholderAutoMode) {
        return setSignalValue(558, cupholderAutoMode.value);
    }

    public boolean setCupholderHeatCoolMode(IAirCondition.CupholderHeatCoolMode cupholderHeatCoolMode) {
        return setSignalValue(556, cupholderHeatCoolMode.value);
    }

    public boolean setDriverTemperature(float f) {
        return setSignalValue(0, f);
    }

    public boolean setEcoMode(IAirCondition.EcoMode ecoMode) {
        return setSignalValue(-1, ecoMode.value);
    }

    public boolean setFanSpeedOfAutoMode(IAirCondition.FanSpeed fanSpeed) {
        return setSignalValue(12, fanSpeed.value);
    }

    public boolean setOnOffByVrState(IAirCondition.OnOffByVrState onOffByVrState) {
        return setSignalValue(IAirCondition.AC_VR_ON_OFF_SETTING, onOffByVrState.value);
    }

    public boolean setOneBtnVentlaionEnabled(boolean z) {
        return setSignalValue(420, z ? (byte) 2 : (byte) 1);
    }

    public boolean setPassengerTemperature(float f) {
        return setSignalValue(1, f);
    }

    public boolean setRearAcTemperature(float f) {
        return setSignalValue(237, f);
    }

    public boolean setRearAcWindMode(IAirCondition.WindMode windMode) {
        return setSignalValue(239, windMode.value);
    }

    public boolean setRearAcWindSpeed(IAirCondition.WindSpeed windSpeed) {
        return setSignalValue(234, windSpeed.value);
    }

    public boolean setTemperatureDecBtnStVDSwitch(boolean z) {
        return setSignalValue(IAirCondition.TEMPERATURE_DEC_VD_SETTING, z ? (byte) 1 : (byte) 0);
    }

    public boolean setTemperatureFRDecBtnStVDSwitch(boolean z) {
        return setSignalValue(525, z ? (byte) 1 : (byte) 0);
    }

    public boolean setTemperatureFRIncBtnStVDSwitch(boolean z) {
        return setSignalValue(523, z ? (byte) 1 : (byte) 0);
    }

    public boolean setTemperatureIncBtnStVDSwitch(boolean z) {
        return setSignalValue(IAirCondition.TEMPERATURE_INC_VD_SETTING, z ? (byte) 1 : (byte) 0);
    }

    public boolean setWindMode(IAirCondition.WindMode windMode) {
        return setSignalValue(4, windMode.value);
    }

    public boolean setWindSpeed(IAirCondition.WindSpeed windSpeed) {
        return setSignalValue(2, windSpeed.value);
    }

    public void unregisterListener(AirConditionListener airConditionListener) {
        unregisterReceiver(airConditionListener);
    }
}
