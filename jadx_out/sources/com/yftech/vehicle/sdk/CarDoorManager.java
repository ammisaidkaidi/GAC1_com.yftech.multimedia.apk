package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarDoor;

/* JADX INFO: loaded from: classes4.dex */
public class CarDoorManager extends BaseApiManager implements ICarDoor {
    private static final String TAG = "CarDoorManager";
    private static CarDoorManager sMe;

    public static abstract class CarDoorListener {
        @SignalReceiver(signalId = 148)
        public void onAutoUnlockChanged(ICarDoor.AutoUnlockState autoUnlockState) {
        }

        @SignalReceiver(signalId = 402)
        public void onDriverDoorLockChanged(ICarDoor.DoorLockState doorLockState) {
        }

        @SignalReceiver(signalId = 188)
        public void onDriverDoorStateChanged(ICarDoor.DoorState doorState) {
        }

        @SignalReceiver(signalId = 283)
        public void onEngineHoodStateChanged(ICarDoor.EngineHoodState engineHoodState) {
        }

        @SignalReceiver(signalId = 210)
        public void onHoodDoorStateChanged(ICarDoor.DoorState doorState) {
        }

        @SignalReceiver(signalId = 150)
        public void onLockUnlockSoundEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 153)
        public void onPEPSAutoTrunkOpenChanged(ICarDoor.PEPSAutoTrunkOpenState pEPSAutoTrunkOpenState) {
        }

        @SignalReceiver(signalId = 151)
        public void onPEPSUnlockEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 152)
        public void onPEPSWalkAwayLockEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 158)
        public void onPSDDoorSpeedLChanged(ICarDoor.PSDDoorSpeed pSDDoorSpeed) {
        }

        @SignalReceiver(signalId = 159)
        public void onPSDDoorSpeedRChanged(ICarDoor.PSDDoorSpeed pSDDoorSpeed) {
        }

        @SignalReceiver(signalId = 403)
        public void onPassengerDoorLockChanged(ICarDoor.DoorLockState doorLockState) {
        }

        @SignalReceiver(signalId = 211)
        public void onPassengerDoorStateChanged(ICarDoor.DoorState doorState) {
        }

        @SignalReceiver(signalId = 212)
        public void onRearLeftDoorStateChanged(ICarDoor.DoorState doorState) {
        }

        @SignalReceiver(signalId = 213)
        public void onRearRightDoorStateChanged(ICarDoor.DoorState doorState) {
        }

        @SignalReceiver(signalId = 147)
        public void onRemoteUnlockModeChanged(ICarDoor.RemoteUnlockMode remoteUnlockMode) {
        }

        @SignalReceiver(signalId = 227, valueToEnabledState = true)
        public void onSlidingDoorModeAutoStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 243)
        public void onSlidingDoorOperationLChanged(ICarDoor.SlidingDoorOperation slidingDoorOperation) {
        }

        @SignalReceiver(signalId = 245)
        public void onSlidingDoorOperationRChanged(ICarDoor.SlidingDoorOperation slidingDoorOperation) {
        }

        @SignalReceiver(signalId = 242)
        public void onSlidingDoorStateLChanged(ICarDoor.SlidingDoorState slidingDoorState) {
        }

        @SignalReceiver(signalId = 244)
        public void onSlidingDoorStateRChanged(ICarDoor.SlidingDoorState slidingDoorState) {
        }

        @SignalReceiver(signalId = 149)
        public void onSpeedAutoLockEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 157, valueToEnabledState = true)
        public void onTailDoorModeStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 154)
        public void onTailDoorPercentChanged(int i) {
        }

        @SignalReceiver(signalId = 156)
        public void onTailDoorStateChanged(ICarDoor.TailDoorState tailDoorState) {
        }

        @SignalReceiver(signalId = 155)
        public void onTailDoorTravelPercentChanged(int i) {
        }

        @SignalReceiver(signalId = 214)
        public void onTrunkDoorStateChanged(ICarDoor.DoorState doorState) {
        }
    }

    private CarDoorManager() {
    }

    public static synchronized CarDoorManager get() {
        CarDoorManager carDoorManager;
        if (sMe != null) {
            carDoorManager = sMe;
        } else {
            carDoorManager = new CarDoorManager();
            sMe = carDoorManager;
        }
        return carDoorManager;
    }

    public ICarDoor.AutoUnlockState getAutoUnlock() {
        return ICarDoor.AutoUnlockState.valueOf(getSignalValue(148).intValue());
    }

    public ICarDoor.DoorLockState getDriverDoorLock() {
        return ICarDoor.DoorLockState.valueOf(getSignalValue(402).intValue());
    }

    public ICarDoor.DoorState getDriverDoorState() {
        return ICarDoor.DoorState.valueOf(getSignalValue(188).intValue());
    }

    public ICarDoor.EngineHoodState getEngineHoodState() {
        return ICarDoor.EngineHoodState.valueOf(getSignalValue(283).intValue());
    }

    public ICarDoor.DoorState getHoodDoorState() {
        return ICarDoor.DoorState.valueOf(getSignalValue(210).intValue());
    }

    public IBaseApi.EnableState getLockUnlockSoundEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(150).intValue());
    }

    public ICarDoor.PEPSAutoTrunkOpenState getPEPSAutoTrunkOpen() {
        return ICarDoor.PEPSAutoTrunkOpenState.valueOf(getSignalValue(153).intValue());
    }

    public IBaseApi.EnableState getPEPSUnlockEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(151).intValue());
    }

    public IBaseApi.EnableState getPEPSWalkAwayLockEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(152).intValue());
    }

    public ICarDoor.PSDDoorSpeed getPSDDoorSpeedL() {
        return ICarDoor.PSDDoorSpeed.valueOf(getSignalValue(158).intValue());
    }

    public ICarDoor.PSDDoorSpeed getPSDDoorSpeedR() {
        return ICarDoor.PSDDoorSpeed.valueOf(getSignalValue(159).intValue());
    }

    public ICarDoor.DoorLockState getPassengerDoorLock() {
        return ICarDoor.DoorLockState.valueOf(getSignalValue(403).intValue());
    }

    public ICarDoor.DoorState getPassengerDoorState() {
        return ICarDoor.DoorState.valueOf(getSignalValue(211).intValue());
    }

    public ICarDoor.DoorState getRearLeftDoorState() {
        return ICarDoor.DoorState.valueOf(getSignalValue(212).intValue());
    }

    public ICarDoor.DoorState getRearRightDoorState() {
        return ICarDoor.DoorState.valueOf(getSignalValue(213).intValue());
    }

    public ICarDoor.RemoteUnlockMode getRemoteUnlockMode() {
        return ICarDoor.RemoteUnlockMode.valueOf(getSignalValue(147).intValue());
    }

    public ICarDoor.SlidingDoorOperation getSlidingDoorOperationL() {
        return ICarDoor.SlidingDoorOperation.valueOf(getSignalValue(243).intValue());
    }

    public ICarDoor.SlidingDoorOperation getSlidingDoorOperationR() {
        return ICarDoor.SlidingDoorOperation.valueOf(getSignalValue(245).intValue());
    }

    public ICarDoor.SlidingDoorState getSlidingDoorStateL() {
        return ICarDoor.SlidingDoorState.valueOf(getSignalValue(242).intValue());
    }

    public ICarDoor.SlidingDoorState getSlidingDoorStateR() {
        return ICarDoor.SlidingDoorState.valueOf(getSignalValue(244).intValue());
    }

    public IBaseApi.EnableState getSpeedAutoLockEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(149).intValue());
    }

    public int getTailDoorPercent() {
        return getSignalValue(154).intValue();
    }

    public ICarDoor.TailDoorState getTailDoorState() {
        return ICarDoor.TailDoorState.valueOf(getSignalValue(156).intValue());
    }

    public int getTailDoorTravelPercent() {
        return getSignalValue(155).intValue();
    }

    public ICarDoor.DoorState getTrunkDoorState() {
        return ICarDoor.DoorState.valueOf(getSignalValue(214).intValue());
    }

    public boolean isSlidingDoorModeAutoEnabled() {
        return getSignalValue(227).byteValue() == 1;
    }

    public boolean isTailDoorModeEnabled() {
        return getSignalValue(157).byteValue() == 1;
    }

    public boolean performTailDoorMaxPositionMemoryButtonClick() {
        return setSignalValue(289, (byte) 1);
    }

    public boolean performTailDoorPositionMemoryClearButtonClick() {
        return setSignalValue(286, (byte) 1);
    }

    public void registerListener(CarDoorListener carDoorListener) {
        registerReceiver(carDoorListener);
    }

    public boolean setAutoUnlock(ICarDoor.AutoUnlockState autoUnlockState) {
        return setSignalValue(53, autoUnlockState.value);
    }

    public boolean setLockUnlockSoundEnabled(boolean z) {
        return setSignalValue(55, z ? (byte) 2 : (byte) 1);
    }

    public boolean setPEPSAutoTrunkOpen(ICarDoor.PEPSAutoTrunkOpenState pEPSAutoTrunkOpenState) {
        return setSignalValue(58, pEPSAutoTrunkOpenState.value);
    }

    public boolean setPEPSUnlockEnabled(boolean z) {
        return setSignalValue(56, z ? (byte) 2 : (byte) 1);
    }

    public boolean setPEPSWalkAwayLockEnabled(boolean z) {
        return setSignalValue(57, z ? (byte) 2 : (byte) 1);
    }

    public boolean setPLGMControl(ICarDoor.PLGMControl pLGMControl) {
        return setSignalValue(-1, pLGMControl.value);
    }

    public boolean setPLGMFunction(ICarDoor.PLGMFunction pLGMFunction) {
        return setSignalValue(61, pLGMFunction.value);
    }

    public boolean setPLGMMaxLevel(ICarDoor.PLGMMaxLevel pLGMMaxLevel) {
        return setSignalValue(60, pLGMMaxLevel.value);
    }

    public boolean setPSDDoorSpeedL(ICarDoor.PSDDoorSpeed pSDDoorSpeed) {
        return setSignalValue(229, pSDDoorSpeed.value);
    }

    public boolean setPSDDoorSpeedR(ICarDoor.PSDDoorSpeed pSDDoorSpeed) {
        return setSignalValue(230, pSDDoorSpeed.value);
    }

    public boolean setRemoteUnlockMode(ICarDoor.RemoteUnlockMode remoteUnlockMode) {
        return setSignalValue(52, remoteUnlockMode.value);
    }

    public boolean setSlidingDoorCmdL(ICarDoor.SlidingDoorCmd slidingDoorCmd) {
        return setSignalValue(241, slidingDoorCmd.value);
    }

    public boolean setSlidingDoorCmdR(ICarDoor.SlidingDoorCmd slidingDoorCmd) {
        return setSignalValue(240, slidingDoorCmd.value);
    }

    public boolean setSlidingDoorModeAutoEnabled(boolean z) {
        return setSignalValue(228, z ? (byte) 2 : (byte) 1);
    }

    public boolean setSpeedAutoLockEnabled(boolean z) {
        return setSignalValue(54, z ? (byte) 2 : (byte) 1);
    }

    public boolean setTailDoorCmd(ICarDoor.TailDoorCmd tailDoorCmd) {
        return setSignalValue(59, tailDoorCmd.value);
    }

    public void unregisterListener(CarDoorListener carDoorListener) {
        unregisterReceiver(carDoorListener);
    }
}
