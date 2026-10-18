package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.ICarInfo;

/* JADX INFO: loaded from: classes4.dex */
public class CarInfoManager extends BaseApiManager implements ICarInfo {
    private static final String TAG = "CarInfoManager";
    private static CarInfoManager sMe;

    public static abstract class CarInfoListener {
        @SignalReceiver(signalId = 124)
        public void onBatteryVoltageChanged(float f) {
        }

        @SignalReceiver(signalId = 187)
        public void onDayNightModeChanged(ICarInfo.DayNightMode dayNightMode) {
        }

        @SignalReceiver(signalId = 407)
        public void onEngineCrankStateChanged(ICarInfo.EngineCrankState engineCrankState) {
        }

        @SignalReceiver(signalId = ICarInfo.ENGINE_IGN_ANGLE_STATE)
        public void onEngineIgnAngleChanged(float f) {
        }

        @SignalReceiver(signalId = 121)
        public void onEngineSpeedChanged(int i) {
        }

        @SignalReceiver(signalId = ICarInfo.GEAR_POSITION_A9E)
        public void onGearPositionA9EChanged(ICarInfo.GearPositionA9E gearPositionA9E) {
        }

        @SignalReceiver(signalId = 185)
        public void onGearPositionChanged(ICarInfo.GearPosition gearPosition) {
        }

        @SignalReceiver(signalId = 125)
        public void onLowBatteryWarningLevelChanged(ICarInfo.LowBatteryWarningLevel lowBatteryWarningLevel) {
        }

        @SignalReceiver(signalId = 201)
        public void onParkingBrakeStateChanged(ICarInfo.ParkingBrakeState parkingBrakeState) {
        }

        @SignalReceiver(signalId = 182)
        public void onPowerModeChanged(ICarInfo.PowerMode powerMode) {
        }

        @SignalReceiver(signalId = ICarInfo.PRESSURE_ATMOSPHERIC_STATE)
        public void onPressureOfAtmosphericChanged(int i) {
        }

        @SignalReceiver(signalId = ICarInfo.PRESSURE_OIL_STATE)
        public void onPressureOfOilChanged(int i) {
        }

        @SignalReceiver(signalId = ICarInfo.PRESSURE_TURBINE_STATE)
        public void onPressureOfTurbineChanged(int i) {
        }

        @SignalReceiver(signalId = 184)
        public void onRearGearStateChanged(ICarInfo.RearGearState rearGearState) {
        }

        @SignalReceiver(signalId = -1)
        public void onSleepWakeUpCommandChanged(ICarInfo.SleepWakeUpCommand sleepWakeUpCommand) {
        }

        @SignalReceiver(signalId = ICarInfo.TEMP_ENGINE_WATER_STATE)
        public void onTemperatureOfEngineWaterChanged(int i) {
        }

        @SignalReceiver(signalId = 440)
        public void onTemperatureOfExhaustChanged(int i) {
        }

        @SignalReceiver(signalId = ICarInfo.TEMP_INTAKE_AIR_STATE)
        public void onTemperatureOfIntakeAirChanged(float f) {
        }

        @SignalReceiver(signalId = ICarInfo.TEMP_TRANSMISSION_OIL_STATE)
        public void onTemperatureOfTransmissionOilChanged(int i) {
        }

        @SignalReceiver(signalId = 123)
        public void onTotalOdometerChanged(int i) {
        }

        @SignalReceiver(signalId = 369, valueToEnabledState = true)
        public void onTransportationModeStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 368)
        public void onVehicleModeBroadcastChanged(ICarInfo.VehicleModeBroadcast vehicleModeBroadcast) {
        }

        @SignalReceiver(signalId = 122)
        public void onVehicleSpeedChanged(int i) {
        }

        @SignalReceiver(signalId = 186)
        public void onVehicleStateChanged(ICarInfo.VehicleState vehicleState) {
        }
    }

    private CarInfoManager() {
    }

    public static synchronized CarInfoManager get() {
        CarInfoManager carInfoManager;
        if (sMe != null) {
            carInfoManager = sMe;
        } else {
            carInfoManager = new CarInfoManager();
            sMe = carInfoManager;
        }
        return carInfoManager;
    }

    public float getBatteryVoltage() {
        return getSignalValue(124).floatValue();
    }

    public ICarInfo.DayNightMode getDayNightMode() {
        return ICarInfo.DayNightMode.valueOf(getSignalValue(187).intValue());
    }

    public ICarInfo.EngineCrankState getEngineCrankState() {
        return ICarInfo.EngineCrankState.valueOf(getSignalValue(407).intValue());
    }

    public float getEngineIgnAngle() {
        return getSignalValue(ICarInfo.ENGINE_IGN_ANGLE_STATE).floatValue();
    }

    public int getEngineSpeed() {
        return getSignalValue(121).intValue();
    }

    public ICarInfo.GearPosition getGearPosition() {
        return ICarInfo.GearPosition.valueOf(getSignalValue(185).intValue());
    }

    public ICarInfo.GearPositionA9E getGearPositionA9E() {
        return ICarInfo.GearPositionA9E.valueOf(getSignalValue(ICarInfo.GEAR_POSITION_A9E).intValue());
    }

    public ICarInfo.LowBatteryWarningLevel getLowBatteryWarningLevel() {
        return ICarInfo.LowBatteryWarningLevel.valueOf(getSignalValue(125).intValue());
    }

    public ICarInfo.ParkingBrakeState getParkingBrakeState() {
        return ICarInfo.ParkingBrakeState.valueOf(getSignalValue(201).intValue());
    }

    public ICarInfo.PowerMode getPowerMode() {
        return ICarInfo.PowerMode.valueOf(getSignalValue(182).intValue());
    }

    public int getPressureOfAtmospheric() {
        return getSignalValue(ICarInfo.PRESSURE_ATMOSPHERIC_STATE).intValue();
    }

    public int getPressureOfOil() {
        return getSignalValue(ICarInfo.PRESSURE_OIL_STATE).intValue();
    }

    public int getPressureOfTurbine() {
        return getSignalValue(ICarInfo.PRESSURE_TURBINE_STATE).intValue();
    }

    public ICarInfo.RearGearState getRearGearState() {
        return ICarInfo.RearGearState.valueOf(getSignalValue(184).intValue());
    }

    public ICarInfo.SleepWakeUpCommand getSleepWakeUpCommand() {
        return ICarInfo.SleepWakeUpCommand.valueOf(getSignalValue(-1).intValue());
    }

    public int getTemperatureOfEngineWater() {
        return getSignalValue(ICarInfo.TEMP_ENGINE_WATER_STATE).intValue();
    }

    public int getTemperatureOfExhaust() {
        return getSignalValue(440).intValue();
    }

    public float getTemperatureOfIntakeAir() {
        return getSignalValue(ICarInfo.TEMP_INTAKE_AIR_STATE).floatValue();
    }

    public int getTemperatureOfTransmissionOil() {
        return getSignalValue(ICarInfo.TEMP_TRANSMISSION_OIL_STATE).intValue();
    }

    public int getTotalOdometer() {
        return getSignalValue(123).intValue();
    }

    public ICarInfo.VehicleModeBroadcast getVehicleModeBroadcast() {
        return ICarInfo.VehicleModeBroadcast.valueOf(getSignalValue(368).intValue());
    }

    public int getVehicleSpeed() {
        return getSignalValue(122).intValue();
    }

    public ICarInfo.VehicleState getVehicleState() {
        return ICarInfo.VehicleState.valueOf(getSignalValue(186).intValue());
    }

    public boolean isTransportationModeEnabled() {
        return getSignalValue(369).byteValue() == 1;
    }

    public void registerListener(CarInfoListener carInfoListener) {
        registerReceiver(carInfoListener);
    }

    public void unregisterListener(CarInfoListener carInfoListener) {
        unregisterReceiver(carInfoListener);
    }
}
