package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarEvDevice;

/* JADX INFO: loaded from: classes4.dex */
public class CarEvDeviceManager extends BaseApiManager implements ICarEvDevice {
    private static final String TAG = "CarEvDeviceManager";
    private static CarEvDeviceManager sMe;

    public static abstract class CarEvDeviceListener {
        @SignalReceiver(signalId = 314)
        public void onBatteryQuantityPercentChanged(float f) {
        }

        @SignalReceiver(signalId = 385)
        public void onBookChrgStartDayChanged(int i) {
        }

        @SignalReceiver(signalId = 386)
        public void onBookChrgStartHourChanged(int i) {
        }

        @SignalReceiver(signalId = 387)
        public void onBookChrgStartMinChanged(int i) {
        }

        @SignalReceiver(signalId = 384)
        public void onBookChrgStartMonChanged(int i) {
        }

        @SignalReceiver(signalId = 380)
        public void onBookChrgStartRemainingDayChanged(int i) {
        }

        @SignalReceiver(signalId = 381)
        public void onBookChrgStartRemainingHourChanged(int i) {
        }

        @SignalReceiver(signalId = 382)
        public void onBookChrgStartRemainingMinChanged(int i) {
        }

        @SignalReceiver(signalId = 383)
        public void onBookChrgStartYearChanged(int i) {
        }

        @SignalReceiver(signalId = 390)
        public void onBookChrgStopDayChanged(int i) {
        }

        @SignalReceiver(signalId = 391)
        public void onBookChrgStopHourChanged(int i) {
        }

        @SignalReceiver(signalId = 392)
        public void onBookChrgStopMinChanged(int i) {
        }

        @SignalReceiver(signalId = 389)
        public void onBookChrgStopMonChanged(int i) {
        }

        @SignalReceiver(signalId = 388)
        public void onBookChrgStopYearChanged(int i) {
        }

        @SignalReceiver(signalId = 490)
        public void onChargActiveChanged(ICarEvDevice.ChargActive chargActive) {
        }

        @SignalReceiver(signalId = 492)
        public void onChargCurrentChanged(int i) {
        }

        @SignalReceiver(signalId = 489)
        public void onChargStateChanged(ICarEvDevice.ChargState chargState) {
        }

        @SignalReceiver(signalId = 324)
        public void onChargeFullNoticeLevelChanged(ICarEvDevice.ChargeFullNoticeLevel chargeFullNoticeLevel) {
        }

        @SignalReceiver(signalId = 393)
        public void onChargeModeChanged(ICarEvDevice.ChargeMode chargeMode) {
        }

        @SignalReceiver(signalId = 318)
        public void onChargeSocPercentChanged(ICarEvDevice.ChargeSocPercent chargeSocPercent) {
        }

        @SignalReceiver(signalId = 320)
        public void onChargeSocPercentValidEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 321)
        public void onChargeWarmTimeChanged(ICarEvDevice.ChargeWarmTime chargeWarmTime) {
        }

        @SignalReceiver(signalId = 323)
        public void onChargeWarmTimeValidEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 482, valueToEnabledState = true)
        public void onDischgActiveStateStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 484)
        public void onDischgEndVehRngChanged(int i) {
        }

        @SignalReceiver(signalId = 478, valueToEnabledState = true)
        public void onDischgEngStartCfgStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 494)
        public void onDischgSetInfoChanged(ICarEvDevice.DischgSetInfo dischgSetInfo) {
        }

        @SignalReceiver(signalId = 480)
        public void onDischgSocCfgChanged(int i) {
        }

        @SignalReceiver(signalId = 485)
        public void onDischgTimeCfgChanged(int i) {
        }

        @SignalReceiver(signalId = 355)
        public void onDriveModeEvChanged(ICarEvDevice.DriveModeEv driveModeEv) {
        }

        @SignalReceiver(signalId = 476)
        public void onDrvTargetSOEChanged(int i) {
        }

        @SignalReceiver(signalId = 359)
        public void onEcoModeEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 361)
        public void onEcoNormalSportModeEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 358)
        public void onEcoPlusModeEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 474)
        public void onEnergyModelChanged(ICarEvDevice.EnergyModel energyModel) {
        }

        @SignalReceiver(signalId = 473, valueToEnabledState = true)
        public void onEngSelfMaintDispStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 325)
        public void onHealCheckNoticeChanged(ICarEvDevice.HealCheckNotice healCheckNotice) {
        }

        @SignalReceiver(signalId = 315, valueToEnabledState = true)
        public void onIpedalCreepStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 317)
        public void onIpedalCreepValidEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 360)
        public void onIpedalModeEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 513)
        public void onObcConnStateChanged(ICarEvDevice.ObcConnState obcConnState) {
        }

        @SignalReceiver(signalId = 487)
        public void onPlugAutoUnlockCfgChanged(ICarEvDevice.PlugAutoUnlockCfg plugAutoUnlockCfg) {
        }

        @SignalReceiver(signalId = 313)
        public void onPowerFlowModeChanged(ICarEvDevice.PowerFlowMode powerFlowMode) {
        }

        @SignalReceiver(signalId = 354)
        public void onRecpLevelAvailChanged(ICarEvDevice.RecpLevelAvail recpLevelAvail) {
        }

        @SignalReceiver(signalId = 412)
        public void onRecuperationLevelA9EChanged(ICarEvDevice.RecuperationLevelA9E recuperationLevelA9E) {
        }

        @SignalReceiver(signalId = 352)
        public void onRecuperationLevelChanged(ICarEvDevice.RecuperationLevel recuperationLevel) {
        }

        @SignalReceiver(signalId = 394)
        public void onRepeatBookChrgDay1Changed(ICarEvDevice.RepeatBookMode repeatBookMode) {
        }

        @SignalReceiver(signalId = 395)
        public void onRepeatBookChrgDay2Changed(ICarEvDevice.RepeatBookMode repeatBookMode) {
        }

        @SignalReceiver(signalId = 396)
        public void onRepeatBookChrgDay3Changed(ICarEvDevice.RepeatBookMode repeatBookMode) {
        }

        @SignalReceiver(signalId = 397)
        public void onRepeatBookChrgDay4Changed(ICarEvDevice.RepeatBookMode repeatBookMode) {
        }

        @SignalReceiver(signalId = 398)
        public void onRepeatBookChrgDay5Changed(ICarEvDevice.RepeatBookMode repeatBookMode) {
        }

        @SignalReceiver(signalId = 399)
        public void onRepeatBookChrgDay6Changed(ICarEvDevice.RepeatBookMode repeatBookMode) {
        }

        @SignalReceiver(signalId = 400)
        public void onRepeatBookChrgDay7Changed(ICarEvDevice.RepeatBookMode repeatBookMode) {
        }

        @SignalReceiver(signalId = 472, valueToEnabledState = true)
        public void onVehRdyStateStateChanged(boolean z) {
        }
    }

    private CarEvDeviceManager() {
    }

    public static synchronized CarEvDeviceManager get() {
        CarEvDeviceManager carEvDeviceManager;
        if (sMe != null) {
            carEvDeviceManager = sMe;
        } else {
            carEvDeviceManager = new CarEvDeviceManager();
            sMe = carEvDeviceManager;
        }
        return carEvDeviceManager;
    }

    public float getBatteryQuantityPercent() {
        return getSignalValue(314).floatValue();
    }

    public int getBookChrgStartDay() {
        return getSignalValue(385).intValue();
    }

    public int getBookChrgStartHour() {
        return getSignalValue(386).intValue();
    }

    public int getBookChrgStartMin() {
        return getSignalValue(387).intValue();
    }

    public int getBookChrgStartMon() {
        return getSignalValue(384).intValue();
    }

    public int getBookChrgStartRemainingDay() {
        return getSignalValue(380).intValue();
    }

    public int getBookChrgStartRemainingHour() {
        return getSignalValue(381).intValue();
    }

    public int getBookChrgStartRemainingMin() {
        return getSignalValue(382).intValue();
    }

    public int getBookChrgStartYear() {
        return getSignalValue(383).intValue();
    }

    public int getBookChrgStopDay() {
        return getSignalValue(390).intValue();
    }

    public int getBookChrgStopHour() {
        return getSignalValue(391).intValue();
    }

    public int getBookChrgStopMin() {
        return getSignalValue(392).intValue();
    }

    public int getBookChrgStopMon() {
        return getSignalValue(389).intValue();
    }

    public int getBookChrgStopYear() {
        return getSignalValue(388).intValue();
    }

    public ICarEvDevice.ChargActive getChargActive() {
        return ICarEvDevice.ChargActive.valueOf(getSignalValue(490).intValue());
    }

    public int getChargCurrent() {
        return getSignalValue(492).intValue();
    }

    public ICarEvDevice.ChargState getChargState() {
        return ICarEvDevice.ChargState.valueOf(getSignalValue(489).intValue());
    }

    public ICarEvDevice.ChargeFullNoticeLevel getChargeFullNoticeLevel() {
        return ICarEvDevice.ChargeFullNoticeLevel.valueOf(getSignalValue(324).intValue());
    }

    public ICarEvDevice.ChargeMode getChargeMode() {
        return ICarEvDevice.ChargeMode.valueOf(getSignalValue(393).intValue());
    }

    public ICarEvDevice.ChargeSocPercent getChargeSocPercent() {
        return ICarEvDevice.ChargeSocPercent.valueOf(getSignalValue(318).intValue());
    }

    public IBaseApi.EnableState getChargeSocPercentValidEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(320).intValue());
    }

    public ICarEvDevice.ChargeWarmTime getChargeWarmTime() {
        return ICarEvDevice.ChargeWarmTime.valueOf(getSignalValue(321).intValue());
    }

    public IBaseApi.EnableState getChargeWarmTimeValidEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(323).intValue());
    }

    public int getDischgEndVehRng() {
        return getSignalValue(484).intValue();
    }

    public ICarEvDevice.DischgSetInfo getDischgSetInfo() {
        return ICarEvDevice.DischgSetInfo.valueOf(getSignalValue(494).intValue());
    }

    public int getDischgSocCfg() {
        return getSignalValue(480).intValue();
    }

    public int getDischgTimeCfg() {
        return getSignalValue(485).intValue();
    }

    public ICarEvDevice.DriveModeEv getDriveModeEv() {
        return ICarEvDevice.DriveModeEv.valueOf(getSignalValue(355).intValue());
    }

    public int getDrvTargetSOE() {
        return getSignalValue(476).intValue();
    }

    public IBaseApi.EnableState getEcoModeEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(359).intValue());
    }

    public IBaseApi.EnableState getEcoNormalSportModeEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(361).intValue());
    }

    public IBaseApi.EnableState getEcoPlusModeEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(358).intValue());
    }

    public ICarEvDevice.EnergyModel getEnergyModel() {
        return ICarEvDevice.EnergyModel.valueOf(getSignalValue(474).intValue());
    }

    public ICarEvDevice.HealCheckNotice getHealCheckNotice() {
        return ICarEvDevice.HealCheckNotice.valueOf(getSignalValue(325).intValue());
    }

    public IBaseApi.EnableState getIpedalCreepValidEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(317).intValue());
    }

    public IBaseApi.EnableState getIpedalModeEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(360).intValue());
    }

    public ICarEvDevice.ObcConnState getObcConnState() {
        return ICarEvDevice.ObcConnState.valueOf(getSignalValue(513).intValue());
    }

    public ICarEvDevice.PlugAutoUnlockCfg getPlugAutoUnlockCfg() {
        return ICarEvDevice.PlugAutoUnlockCfg.valueOf(getSignalValue(487).intValue());
    }

    public ICarEvDevice.PowerFlowMode getPowerFlowMode() {
        return ICarEvDevice.PowerFlowMode.valueOf(getSignalValue(313).intValue());
    }

    public ICarEvDevice.RecpLevelAvail getRecpLevelAvail() {
        return ICarEvDevice.RecpLevelAvail.valueOf(getSignalValue(354).intValue());
    }

    public ICarEvDevice.RecuperationLevel getRecuperationLevel() {
        return ICarEvDevice.RecuperationLevel.valueOf(getSignalValue(352).intValue());
    }

    public ICarEvDevice.RecuperationLevelA9E getRecuperationLevelA9E() {
        return ICarEvDevice.RecuperationLevelA9E.valueOf(getSignalValue(412).intValue());
    }

    public ICarEvDevice.RepeatBookMode getRepeatBookChrgDay1() {
        return ICarEvDevice.RepeatBookMode.valueOf(getSignalValue(394).intValue());
    }

    public ICarEvDevice.RepeatBookMode getRepeatBookChrgDay2() {
        return ICarEvDevice.RepeatBookMode.valueOf(getSignalValue(395).intValue());
    }

    public ICarEvDevice.RepeatBookMode getRepeatBookChrgDay3() {
        return ICarEvDevice.RepeatBookMode.valueOf(getSignalValue(396).intValue());
    }

    public ICarEvDevice.RepeatBookMode getRepeatBookChrgDay4() {
        return ICarEvDevice.RepeatBookMode.valueOf(getSignalValue(397).intValue());
    }

    public ICarEvDevice.RepeatBookMode getRepeatBookChrgDay5() {
        return ICarEvDevice.RepeatBookMode.valueOf(getSignalValue(398).intValue());
    }

    public ICarEvDevice.RepeatBookMode getRepeatBookChrgDay6() {
        return ICarEvDevice.RepeatBookMode.valueOf(getSignalValue(399).intValue());
    }

    public ICarEvDevice.RepeatBookMode getRepeatBookChrgDay7() {
        return ICarEvDevice.RepeatBookMode.valueOf(getSignalValue(400).intValue());
    }

    public boolean isDischgActiveStateEnabled() {
        return getSignalValue(482).byteValue() == 1;
    }

    public boolean isDischgEngStartCfgEnabled() {
        return getSignalValue(478).byteValue() == 1;
    }

    public boolean isEngSelfMaintDispEnabled() {
        return getSignalValue(473).byteValue() == 1;
    }

    public boolean isIpedalCreepEnabled() {
        return getSignalValue(315).byteValue() == 1;
    }

    public boolean isVehRdyStateEnabled() {
        return getSignalValue(472).byteValue() == 1;
    }

    public void registerListener(CarEvDeviceListener carEvDeviceListener) {
        registerReceiver(carEvDeviceListener);
    }

    public boolean setChargCurrent(int i) {
        return setSignalValue(493, i);
    }

    public boolean setChargEnd(ICarEvDevice.ChargEnd chargEnd) {
        return setSignalValue(491, chargEnd.value);
    }

    public boolean setChargeSocPercent(ICarEvDevice.ChargeSocPercent chargeSocPercent) {
        return setSignalValue(319, chargeSocPercent.value);
    }

    public boolean setChargeWarmTime(ICarEvDevice.ChargeWarmTime chargeWarmTime) {
        return setSignalValue(322, chargeWarmTime.value);
    }

    public boolean setDischgActiveStateEnabled(boolean z) {
        return setSignalValue(483, z ? (byte) 1 : (byte) 2);
    }

    public boolean setDischgEngStartCfg(ICarEvDevice.EngStartCfg engStartCfg) {
        return setSignalValue(479, engStartCfg.value);
    }

    public boolean setDischgSocCfg(int i) {
        return setSignalValue(481, i);
    }

    public boolean setDischgTimeCfg(int i) {
        return setSignalValue(486, i);
    }

    public boolean setDriveModeEv(ICarEvDevice.DriveModeEv driveModeEv) {
        return setSignalValue(356, driveModeEv.value);
    }

    public boolean setDrvTargetSOE(int i) {
        return setSignalValue(477, i);
    }

    public boolean setEcoPlusMode(ICarEvDevice.EcoPlusMode ecoPlusMode) {
        return setSignalValue(357, ecoPlusMode.value);
    }

    public boolean setEnergyModel(ICarEvDevice.EnergyModel energyModel) {
        return setSignalValue(475, energyModel.value);
    }

    public boolean setIpedalCreepEnabled(boolean z) {
        return setSignalValue(316, z ? (byte) 1 : (byte) 2);
    }

    public boolean setPlugAutoUnlockCfg(ICarEvDevice.PlugAutoUnlockCfg plugAutoUnlockCfg) {
        return setSignalValue(488, plugAutoUnlockCfg.value);
    }

    public boolean setRecuperationLevel(ICarEvDevice.RecuperationLevel recuperationLevel) {
        return setSignalValue(353, recuperationLevel.value);
    }

    public boolean setRecuperationLevelA9E(ICarEvDevice.RecuperationLevelA9E recuperationLevelA9E) {
        return setSignalValue(ICarEvDevice.RECUPERATION_LEVEL_A9E_SETTING, recuperationLevelA9E.value);
    }

    public void unregisterListener(CarEvDeviceListener carEvDeviceListener) {
        unregisterReceiver(carEvDeviceListener);
    }
}
