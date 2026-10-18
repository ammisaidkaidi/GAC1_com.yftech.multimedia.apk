package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.ICarIcm;

/* JADX INFO: loaded from: classes4.dex */
public class CarIcmManager extends BaseApiManager implements ICarIcm {
    private static final String TAG = "CarIcmManager";
    private static CarIcmManager sMe;

    public static abstract class CarIcmListener {
        @SignalReceiver(signalId = 114)
        public void onBackLightLevelChanged(ICarIcm.BackLightLevel backLightLevel) {
        }

        @SignalReceiver(signalId = 119)
        public void onFuelEconomyAndDistanceUnitChanged(ICarIcm.FuelEconomyAndDistanceUnit fuelEconomyAndDistanceUnit) {
        }

        @SignalReceiver(signalId = 120, valueToEnabledState = true)
        public void onKeepMuteWarningStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 226)
        public void onKeyBoardBackLightChanged(int i) {
        }

        @SignalReceiver(signalId = 118)
        public void onLanguageChanged(ICarIcm.Language language) {
        }

        @SignalReceiver(signalId = 258)
        public void onMeterVolumeModeChanged(ICarIcm.MeterVolumeMode meterVolumeMode) {
        }

        @SignalReceiver(signalId = 116)
        public void onPressureUnitChanged(ICarIcm.PressureUnit pressureUnit) {
        }

        @SignalReceiver(signalId = 117)
        public void onSpeedWarningChanged(int i) {
        }

        @SignalReceiver(signalId = 115)
        public void onTemperatureUnitChanged(ICarIcm.TemperatureUnit temperatureUnit) {
        }
    }

    private CarIcmManager() {
    }

    public static synchronized CarIcmManager get() {
        CarIcmManager carIcmManager;
        if (sMe != null) {
            carIcmManager = sMe;
        } else {
            carIcmManager = new CarIcmManager();
            sMe = carIcmManager;
        }
        return carIcmManager;
    }

    public ICarIcm.BackLightLevel getBackLightLevel() {
        return ICarIcm.BackLightLevel.valueOf(getSignalValue(114).intValue());
    }

    public ICarIcm.FuelEconomyAndDistanceUnit getFuelEconomyAndDistanceUnit() {
        return ICarIcm.FuelEconomyAndDistanceUnit.valueOf(getSignalValue(119).intValue());
    }

    public int getKeyBoardBackLight() {
        return getSignalValue(226).intValue();
    }

    public ICarIcm.Language getLanguage() {
        return ICarIcm.Language.valueOf(getSignalValue(118).intValue());
    }

    public ICarIcm.MeterVolumeMode getMeterVolumeMode() {
        return ICarIcm.MeterVolumeMode.valueOf(getSignalValue(258).intValue());
    }

    public ICarIcm.PressureUnit getPressureUnit() {
        return ICarIcm.PressureUnit.valueOf(getSignalValue(116).intValue());
    }

    public int getSpeedWarning() {
        return getSignalValue(117).intValue();
    }

    public ICarIcm.TemperatureUnit getTemperatureUnit() {
        return ICarIcm.TemperatureUnit.valueOf(getSignalValue(115).intValue());
    }

    public boolean isKeepMuteWarningEnabled() {
        return getSignalValue(120).byteValue() == 1;
    }

    public void registerListener(CarIcmListener carIcmListener) {
        registerReceiver(carIcmListener);
    }

    public boolean setBackLightLevel(ICarIcm.BackLightLevel backLightLevel) {
        return setSignalValue(219, backLightLevel.value);
    }

    public boolean setFMFreqSymbol(int i) {
        return setSignalValue(533, i);
    }

    public boolean setFuelEconomyAndDistanceUnit(ICarIcm.FuelEconomyAndDistanceUnit fuelEconomyAndDistanceUnit) {
        return setSignalValue(51, fuelEconomyAndDistanceUnit.value);
    }

    public boolean setLanguage(ICarIcm.Language language) {
        return setSignalValue(50, language.value);
    }

    public boolean setMeterVolumeMode(ICarIcm.MeterVolumeMode meterVolumeMode) {
        return setSignalValue(259, meterVolumeMode.value);
    }

    public boolean setPressureUnit(ICarIcm.PressureUnit pressureUnit) {
        return setSignalValue(48, pressureUnit.value);
    }

    public boolean setSpeedWarning(int i) {
        return setSignalValue(49, i);
    }

    public boolean setTemperatureUnit(ICarIcm.TemperatureUnit temperatureUnit) {
        return setSignalValue(47, temperatureUnit.value);
    }

    public void unregisterListener(CarIcmListener carIcmListener) {
        unregisterReceiver(carIcmListener);
    }
}
