package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarSeat;

/* JADX INFO: loaded from: classes4.dex */
public class CarSeatManager extends BaseApiManager implements ICarSeat {
    private static final String TAG = "CarSeatManager";
    private static CarSeatManager sMe;

    public static abstract class CarSeatListener {
        @SignalReceiver(signalId = 347)
        public void onChildLockRLChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 349)
        public void onChildLockRRChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 501)
        public void onCurrPepsPuidChanged(ICarSeat.CurrPepsPuid currPepsPuid) {
        }

        @SignalReceiver(signalId = 87)
        public void onHeatLevelFLChanged(ICarSeat.HeatLevel heatLevel) {
        }

        @SignalReceiver(signalId = 88)
        public void onHeatLevelFRChanged(ICarSeat.HeatLevel heatLevel) {
        }

        @SignalReceiver(signalId = 89)
        public void onHeatLevelRLChanged(ICarSeat.HeatLevel heatLevel) {
        }

        @SignalReceiver(signalId = 90)
        public void onHeatLevelRRChanged(ICarSeat.HeatLevel heatLevel) {
        }

        @SignalReceiver(signalId = 95, valueToEnabledState = true)
        public void onHeatVentAutoModeFLStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 96, valueToEnabledState = true)
        public void onHeatVentAutoModeFRStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 97, valueToEnabledState = true)
        public void onHeatVentAutoModeRLStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 98, valueToEnabledState = true)
        public void onHeatVentAutoModeRRStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 506)
        public void onMemSysOperationReqCmdChanged(ICarSeat.MemSysOperationReqCmd memSysOperationReqCmd) {
        }

        @SignalReceiver(signalId = 296)
        public void onSeatMassageLevelRLChanged(ICarSeat.SeatMassageLevel seatMassageLevel) {
        }

        @SignalReceiver(signalId = 302)
        public void onSeatMassageLevelRRChanged(ICarSeat.SeatMassageLevel seatMassageLevel) {
        }

        @SignalReceiver(signalId = 294)
        public void onSeatMassageModeRLChanged(ICarSeat.SeatMassageMode seatMassageMode) {
        }

        @SignalReceiver(signalId = 300)
        public void onSeatMassageModeRRChanged(ICarSeat.SeatMassageMode seatMassageMode) {
        }

        @SignalReceiver(signalId = 292)
        public void onSeatMassageOnOffRLChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 298)
        public void onSeatMassageOnOffRRChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 505)
        public void onSeatOperationStateChanged(ICarSeat.SeatOperationState seatOperationState) {
        }

        @SignalReceiver(signalId = 91)
        public void onVentLevelFLChanged(ICarSeat.VentLevel ventLevel) {
        }

        @SignalReceiver(signalId = 92)
        public void onVentLevelFRChanged(ICarSeat.VentLevel ventLevel) {
        }

        @SignalReceiver(signalId = 93)
        public void onVentLevelRLChanged(ICarSeat.VentLevel ventLevel) {
        }

        @SignalReceiver(signalId = 94)
        public void onVentLevelRRChanged(ICarSeat.VentLevel ventLevel) {
        }
    }

    private CarSeatManager() {
    }

    public static synchronized CarSeatManager get() {
        CarSeatManager carSeatManager;
        if (sMe != null) {
            carSeatManager = sMe;
        } else {
            carSeatManager = new CarSeatManager();
            sMe = carSeatManager;
        }
        return carSeatManager;
    }

    public IBaseApi.EnableState getChildLockRL() {
        return IBaseApi.EnableState.valueOf(getSignalValue(347).intValue());
    }

    public IBaseApi.EnableState getChildLockRR() {
        return IBaseApi.EnableState.valueOf(getSignalValue(349).intValue());
    }

    public ICarSeat.CurrPepsPuid getCurrPepsPuid() {
        return ICarSeat.CurrPepsPuid.valueOf(getSignalValue(501).intValue());
    }

    public ICarSeat.HeatLevel getHeatLevelFL() {
        return ICarSeat.HeatLevel.valueOf(getSignalValue(87).intValue());
    }

    public ICarSeat.HeatLevel getHeatLevelFR() {
        return ICarSeat.HeatLevel.valueOf(getSignalValue(88).intValue());
    }

    public ICarSeat.HeatLevel getHeatLevelRL() {
        return ICarSeat.HeatLevel.valueOf(getSignalValue(89).intValue());
    }

    public ICarSeat.HeatLevel getHeatLevelRR() {
        return ICarSeat.HeatLevel.valueOf(getSignalValue(90).intValue());
    }

    public ICarSeat.MemSysOperationReqCmd getMemSysOperationReqCmd() {
        return ICarSeat.MemSysOperationReqCmd.valueOf(getSignalValue(506).intValue());
    }

    public ICarSeat.SeatMassageLevel getSeatMassageLevelRL() {
        return ICarSeat.SeatMassageLevel.valueOf(getSignalValue(296).intValue());
    }

    public ICarSeat.SeatMassageLevel getSeatMassageLevelRR() {
        return ICarSeat.SeatMassageLevel.valueOf(getSignalValue(302).intValue());
    }

    public ICarSeat.SeatMassageMode getSeatMassageModeRL() {
        return ICarSeat.SeatMassageMode.valueOf(getSignalValue(294).intValue());
    }

    public ICarSeat.SeatMassageMode getSeatMassageModeRR() {
        return ICarSeat.SeatMassageMode.valueOf(getSignalValue(300).intValue());
    }

    public IBaseApi.EnableState getSeatMassageOnOffRL() {
        return IBaseApi.EnableState.valueOf(getSignalValue(292).intValue());
    }

    public IBaseApi.EnableState getSeatMassageOnOffRR() {
        return IBaseApi.EnableState.valueOf(getSignalValue(298).intValue());
    }

    public ICarSeat.SeatOperationState getSeatOperationState() {
        return ICarSeat.SeatOperationState.valueOf(getSignalValue(505).intValue());
    }

    public ICarSeat.VentLevel getVentLevelFL() {
        return ICarSeat.VentLevel.valueOf(getSignalValue(91).intValue());
    }

    public ICarSeat.VentLevel getVentLevelFR() {
        return ICarSeat.VentLevel.valueOf(getSignalValue(92).intValue());
    }

    public ICarSeat.VentLevel getVentLevelRL() {
        return ICarSeat.VentLevel.valueOf(getSignalValue(93).intValue());
    }

    public ICarSeat.VentLevel getVentLevelRR() {
        return ICarSeat.VentLevel.valueOf(getSignalValue(94).intValue());
    }

    public boolean isHeatVentAutoModeFLEnabled() {
        return getSignalValue(95).byteValue() == 1;
    }

    public boolean isHeatVentAutoModeFREnabled() {
        return getSignalValue(96).byteValue() == 1;
    }

    public boolean isHeatVentAutoModeRLEnabled() {
        return getSignalValue(97).byteValue() == 1;
    }

    public boolean isHeatVentAutoModeRREnabled() {
        return getSignalValue(98).byteValue() == 1;
    }

    public void registerListener(CarSeatListener carSeatListener) {
        registerReceiver(carSeatListener);
    }

    public boolean setAccountDelete(ICarSeat.AccountDelete accountDelete) {
        return setSignalValue(503, accountDelete.value);
    }

    public boolean setChildLockRLEnabled(boolean z) {
        return setSignalValue(348, z ? (byte) 2 : (byte) 1);
    }

    public boolean setChildLockRREnabled(boolean z) {
        return setSignalValue(350, z ? (byte) 2 : (byte) 1);
    }

    public boolean setCurrAccountCfg(ICarSeat.CurrAccountCfg currAccountCfg) {
        return setSignalValue(502, currAccountCfg.value);
    }

    public boolean setDriverSeatPostionCmd(ICarSeat.DriverSeatPostionCmd driverSeatPostionCmd) {
        return setSignalValue(504, driverSeatPostionCmd.value);
    }

    public boolean setHeatLevelFL(ICarSeat.HeatLevel heatLevel) {
        return setSignalValue(15, heatLevel.value);
    }

    public boolean setHeatLevelFR(ICarSeat.HeatLevel heatLevel) {
        return setSignalValue(16, heatLevel.value);
    }

    public boolean setHeatLevelRL(ICarSeat.HeatLevel heatLevel) {
        return setSignalValue(17, heatLevel.value);
    }

    public boolean setHeatLevelRR(ICarSeat.HeatLevel heatLevel) {
        return setSignalValue(18, heatLevel.value);
    }

    public boolean setHeatVentAutoModeFLEnabled(boolean z) {
        return setSignalValue(27, z ? (byte) 2 : (byte) 1);
    }

    public boolean setHeatVentAutoModeFREnabled(boolean z) {
        return setSignalValue(28, z ? (byte) 2 : (byte) 1);
    }

    public boolean setHeatVentAutoModeRLEnabled(boolean z) {
        return setSignalValue(29, z ? (byte) 2 : (byte) 1);
    }

    public boolean setHeatVentAutoModeRREnabled(boolean z) {
        return setSignalValue(30, z ? (byte) 2 : (byte) 1);
    }

    public boolean setHeatVentModeFL(ICarSeat.HeatVentMode heatVentMode) {
        return setSignalValue(23, heatVentMode.value);
    }

    public boolean setHeatVentModeFR(ICarSeat.HeatVentMode heatVentMode) {
        return setSignalValue(24, heatVentMode.value);
    }

    public boolean setHeatVentModeRL(ICarSeat.HeatVentMode heatVentMode) {
        return setSignalValue(25, heatVentMode.value);
    }

    public boolean setHeatVentModeRR(ICarSeat.HeatVentMode heatVentMode) {
        return setSignalValue(26, heatVentMode.value);
    }

    public boolean setSeatMassageLevelRL(ICarSeat.SeatMassageLevel seatMassageLevel) {
        return setSignalValue(297, seatMassageLevel.value);
    }

    public boolean setSeatMassageLevelRR(ICarSeat.SeatMassageLevel seatMassageLevel) {
        return setSignalValue(303, seatMassageLevel.value);
    }

    public boolean setSeatMassageModeRL(ICarSeat.SeatMassageMode seatMassageMode) {
        return setSignalValue(295, seatMassageMode.value);
    }

    public boolean setSeatMassageModeRR(ICarSeat.SeatMassageMode seatMassageMode) {
        return setSignalValue(301, seatMassageMode.value);
    }

    public boolean setSeatMassageOnOffRLEnabled(boolean z) {
        return setSignalValue(293, z ? (byte) 2 : (byte) 1);
    }

    public boolean setSeatMassageOnOffRREnabled(boolean z) {
        return setSignalValue(299, z ? (byte) 2 : (byte) 1);
    }

    public boolean setSeatSteeringWheel(ICarSeat.SSWOperation sSWOperation) {
        return setSignalValue(31, sSWOperation.value);
    }

    public boolean setVentLevelFL(ICarSeat.VentLevel ventLevel) {
        return setSignalValue(19, ventLevel.value);
    }

    public boolean setVentLevelFR(ICarSeat.VentLevel ventLevel) {
        return setSignalValue(20, ventLevel.value);
    }

    public boolean setVentLevelRL(ICarSeat.VentLevel ventLevel) {
        return setSignalValue(21, ventLevel.value);
    }

    public boolean setVentLevelRR(ICarSeat.VentLevel ventLevel) {
        return setSignalValue(22, ventLevel.value);
    }

    public void unregisterListener(CarSeatListener carSeatListener) {
        unregisterReceiver(carSeatListener);
    }
}
