package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarDrive;

/* JADX INFO: loaded from: classes4.dex */
public class CarDriveManager extends BaseApiManager implements ICarDrive {
    private static final String TAG = "CarDriveManager";
    private static CarDriveManager sMe;

    public static abstract class CarDriveListener {
        @SignalReceiver(signalId = ICarDrive.ASSIST_PARA_SWITCH_REQ_STATE)
        public void onAssistParaSwitchReqStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 287)
        public void onAutoHoldChanged(ICarDrive.AutoHoldState autoHoldState) {
        }

        @SignalReceiver(signalId = 222)
        public void onAvdcModeChanged(ICarDrive.AvdcMode avdcMode) {
        }

        @SignalReceiver(signalId = 405)
        public void onBcsVdcOffStateChanged(ICarDrive.VdcOffState vdcOffState) {
        }

        @SignalReceiver(signalId = 179)
        public void onDriveModeChanged(ICarDrive.DriveMode driveMode) {
        }

        @SignalReceiver(signalId = 406)
        public void onDriveModeHevChanged(ICarDrive.DriveModeHev driveModeHev) {
        }

        @SignalReceiver(signalId = 261)
        public void onDriveSystemModeChanged(ICarDrive.DriveSystemMode driveSystemMode) {
        }

        @SignalReceiver(signalId = 181)
        public void onEMSEngineWaveSwitchStateChanged(IBaseApi.SwitchState switchState) {
        }

        @SignalReceiver(signalId = 180)
        public void onEPSStyleChanged(ICarDrive.EPSStyle ePSStyle) {
        }

        @SignalReceiver(signalId = 291)
        public void onEpsModeStateChanged(ICarDrive.EpsModeState epsModeState) {
        }

        @SignalReceiver(signalId = 215)
        public void onEspStateChanged(ICarDrive.EspState espState) {
        }

        @SignalReceiver(signalId = 290)
        public void onEspStyleChanged(ICarDrive.EspStyle espStyle) {
        }

        @SignalReceiver(signalId = 224)
        public void onGasPedalLevelChanged(ICarDrive.GasPedalLevel gasPedalLevel) {
        }

        @SignalReceiver(signalId = 217)
        public void onHdcStateChanged(ICarDrive.HdcState hdcState) {
        }

        @SignalReceiver(signalId = ICarDrive.MEMORY_DRIVE_MODE_A9E)
        public void onMemoryDriveModeA9EChanged(ICarDrive.MemoryDriveMode_A9E memoryDriveMode_A9E) {
        }

        @SignalReceiver(signalId = 207)
        public void onMemoryDriveModeChanged(ICarDrive.MemoryDriveMode memoryDriveMode) {
        }

        @SignalReceiver(signalId = ICarDrive.MRR_ACC_MODE)
        public void onMrrAccModeChanged(ICarDrive.MrrAccMode mrrAccMode) {
        }

        @SignalReceiver(signalId = 260)
        public void onTcuDriveModeChanged(ICarDrive.TcuDriveMode tcuDriveMode) {
        }
    }

    private CarDriveManager() {
    }

    public static synchronized CarDriveManager get() {
        CarDriveManager carDriveManager;
        if (sMe != null) {
            carDriveManager = sMe;
        } else {
            carDriveManager = new CarDriveManager();
            sMe = carDriveManager;
        }
        return carDriveManager;
    }

    public IBaseApi.EnableState getAssistParaSwitchReqState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(ICarDrive.ASSIST_PARA_SWITCH_REQ_STATE).intValue());
    }

    public ICarDrive.AutoHoldState getAutoHold() {
        return ICarDrive.AutoHoldState.valueOf(getSignalValue(287).intValue());
    }

    public ICarDrive.AvdcMode getAvdcMode() {
        return ICarDrive.AvdcMode.valueOf(getSignalValue(222).intValue());
    }

    public ICarDrive.VdcOffState getBcsVdcOffState() {
        return ICarDrive.VdcOffState.valueOf(getSignalValue(405).intValue());
    }

    public ICarDrive.DriveMode getDriveMode() {
        return ICarDrive.DriveMode.valueOf(getSignalValue(179).intValue());
    }

    public ICarDrive.DriveModeHev getDriveModeHev() {
        return ICarDrive.DriveModeHev.valueOf(getSignalValue(406).intValue());
    }

    public ICarDrive.DriveSystemMode getDriveSystemMode() {
        return ICarDrive.DriveSystemMode.valueOf(getSignalValue(261).intValue());
    }

    public IBaseApi.SwitchState getEMSEngineWaveSwitchState() {
        return IBaseApi.SwitchState.valueOf(getSignalValue(181).intValue());
    }

    public ICarDrive.EPSStyle getEPSStyle() {
        return ICarDrive.EPSStyle.valueOf(getSignalValue(180).intValue());
    }

    public ICarDrive.EpsModeState getEpsModeState() {
        return ICarDrive.EpsModeState.valueOf(getSignalValue(291).intValue());
    }

    public ICarDrive.EspState getEspState() {
        return ICarDrive.EspState.valueOf(getSignalValue(215).intValue());
    }

    public ICarDrive.EspStyle getEspStyle() {
        return ICarDrive.EspStyle.valueOf(getSignalValue(290).intValue());
    }

    public ICarDrive.GasPedalLevel getGasPedalLevel() {
        return ICarDrive.GasPedalLevel.valueOf(getSignalValue(224).intValue());
    }

    public ICarDrive.HdcState getHdcState() {
        return ICarDrive.HdcState.valueOf(getSignalValue(217).intValue());
    }

    public ICarDrive.MemoryDriveMode getMemoryDriveMode() {
        return ICarDrive.MemoryDriveMode.valueOf(getSignalValue(207).intValue());
    }

    public ICarDrive.MemoryDriveMode_A9E getMemoryDriveModeA9E() {
        return ICarDrive.MemoryDriveMode_A9E.valueOf(getSignalValue(ICarDrive.MEMORY_DRIVE_MODE_A9E).intValue());
    }

    public ICarDrive.MrrAccMode getMrrAccMode() {
        return ICarDrive.MrrAccMode.valueOf(getSignalValue(ICarDrive.MRR_ACC_MODE).intValue());
    }

    public ICarDrive.TcuDriveMode getTcuDriveMode() {
        return ICarDrive.TcuDriveMode.valueOf(getSignalValue(260).intValue());
    }

    public boolean performAutoHoldButtonClick() {
        return setSignalValue(288, (byte) 1);
    }

    public void registerListener(CarDriveListener carDriveListener) {
        registerReceiver(carDriveListener);
    }

    public boolean setAvdcMode(ICarDrive.AvdcMode avdcMode) {
        return setSignalValue(223, avdcMode.value);
    }

    public boolean setDriveMode(ICarDrive.DriveMode driveMode) {
        return setSignalValue(71, driveMode.value);
    }

    public boolean setEMSEngineWaveEnabled(boolean z) {
        return setSignalValue(73, z ? (byte) 2 : (byte) 1);
    }

    public boolean setEPSStyle(ICarDrive.EPSStyle ePSStyle) {
        return setSignalValue(72, ePSStyle.value);
    }

    public boolean setEspState(ICarDrive.EspState espState) {
        return setSignalValue(216, espState.value);
    }

    public boolean setGasPedalLevel(ICarDrive.GasPedalLevel gasPedalLevel) {
        return setSignalValue(225, gasPedalLevel.value);
    }

    public boolean setHdcState(ICarDrive.HdcState hdcState) {
        return setSignalValue(218, hdcState.value);
    }

    public boolean setMemoryDriveMode(ICarDrive.MemoryDriveMode memoryDriveMode) {
        return setSignalValue(208, memoryDriveMode.value);
    }

    public void unregisterListener(CarDriveListener carDriveListener) {
        unregisterReceiver(carDriveListener);
    }
}
