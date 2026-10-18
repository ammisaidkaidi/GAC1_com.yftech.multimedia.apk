package com.yftech.vehicle.sdk;

import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.IBaseApi;
import com.yftech.vehicle.internal.adapter.ICarSettings;

/* JADX INFO: loaded from: classes4.dex */
public class CarSettingsManager extends BaseApiManager implements ICarSettings {
    private static final String TAG = "CarSettingsManager";
    private static CarSettingsManager sMe;

    public static abstract class CarSettingsListener {
        @SignalReceiver(signalId = 526)
        public void onADASELKStatusChanged(ICarSettings.ADASELKStatus aDASELKStatus) {
        }

        @SignalReceiver(signalId = 108)
        public void onADASWarnTypeChanged(ICarSettings.ADASWarnType aDASWarnType) {
        }

        @SignalReceiver(signalId = 103)
        public void onAEBOffEnableStateChanged(IBaseApi.EnableStateOp enableStateOp) {
        }

        @SignalReceiver(signalId = 109)
        public void onAVASEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 416)
        public void onAcSmartChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 112)
        public void onAutoWiperModeChanged(ICarSettings.AutoWiperMode autoWiperMode) {
        }

        @SignalReceiver(signalId = 454)
        public void onBSDMDowStateChanged(ICarSettings.BSDMDowState bSDMDowState) {
        }

        @SignalReceiver(signalId = 452)
        public void onBSDMLcaStateChanged(ICarSettings.BSDMLcaState bSDMLcaState) {
        }

        @SignalReceiver(signalId = 458)
        public void onBSDMRctaAssistTypeChanged(ICarSettings.BSDMRctaAssistType bSDMRctaAssistType) {
        }

        @SignalReceiver(signalId = 456)
        public void onBSDMRctaStateChanged(ICarSettings.BSDMRctaState bSDMRctaState) {
        }

        @SignalReceiver(signalId = 460)
        public void onBSDMRcwStateChanged(ICarSettings.BSDMRcwState bSDMRcwState) {
        }

        @SignalReceiver(signalId = ICarSettings.COMFORT_PARK_STATE)
        public void onComfortParkStateChanged(ICarSettings.ComfortParkState comfortParkState) {
        }

        @SignalReceiver(signalId = 271)
        public void onCruiseAssistModeChanged(ICarSettings.CruiseAssistMode cruiseAssistMode) {
        }

        @SignalReceiver(signalId = ICarSettings.DOOR_HANDLE_ELECTRIC_HIDE_STATE, valueToEnabledState = true)
        public void onDoorHandleElectricHideStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 332)
        public void onDragModeChanged(ICarSettings.DragMode dragMode) {
        }

        @SignalReceiver(signalId = ICarSettings.DRIVER_EASY_ENTRY_EXIT_STATE, valueToEnabledState = true)
        public void onDriverEasyEntryAndExitStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 528)
        public void onEMSEngWaveStatusChanged(ICarSettings.EMSEngWaveStatus eMSEngWaveStatus) {
        }

        @SignalReceiver(signalId = 334)
        public void onEPBSystemMsgChanged(ICarSettings.EPBSystemMsg ePBSystemMsg) {
        }

        @SignalReceiver(signalId = 330)
        public void onEPBSystemStateChanged(ICarSettings.EPBSystemState ePBSystemState) {
        }

        @SignalReceiver(signalId = 110)
        public void onETCEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 101)
        public void onFCWOffEnableStateChanged(IBaseApi.EnableStateOp enableStateOp) {
        }

        @SignalReceiver(signalId = 102)
        public void onFCWWarningDistanceChanged(ICarSettings.FCWWarningDistance fCWWarningDistance) {
        }

        @SignalReceiver(signalId = 267)
        public void onFrontCarDistanceDisplayEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 111, valueToEnabledState = true)
        public void onFrontWiperServicePositionStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 468)
        public void onHudAngleStChanged(int i) {
        }

        @SignalReceiver(signalId = 464)
        public void onHudBrightnessStChanged(int i) {
        }

        @SignalReceiver(signalId = 466)
        public void onHudHeightStChanged(int i) {
        }

        @SignalReceiver(signalId = 470)
        public void onHudModeChanged(ICarSettings.HudMode hudMode) {
        }

        @SignalReceiver(signalId = 462)
        public void onHudOffStateChanged(ICarSettings.HudOffState hudOffState) {
        }

        @SignalReceiver(signalId = 106)
        public void onIAccEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 269)
        public void onIntelligentAvoidEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 362)
        public void onKeyNotforgetWarningEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 100)
        public void onLksModeChanged(ICarSettings.LksMode lksMode) {
        }

        @SignalReceiver(signalId = 99)
        public void onLksStateChanged(ICarSettings.LksState lksState) {
        }

        @SignalReceiver(signalId = 107)
        public void onOSRMAutoFoldEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = ICarSettings.OSRM_ANGLE_CMD_STATE)
        public void onOSRMRAngleCmdChanged(ICarSettings.OSRMRAngleCmd oSRMRAngleCmd) {
        }

        @SignalReceiver(signalId = ICarSettings.OSRMR_ANGLE_HORIZONTAL_FL)
        public void onOSRMRAngleHorizontalFLChanged(int i) {
        }

        @SignalReceiver(signalId = ICarSettings.OSRMR_ANGLE_HORIZONTAL_FR)
        public void onOSRMRAngleHorizontalFRChanged(int i) {
        }

        @SignalReceiver(signalId = ICarSettings.OSRMR_ANGLE_VERTICAL_FL)
        public void onOSRMRAngleVerticalFLChanged(int i) {
        }

        @SignalReceiver(signalId = ICarSettings.OSRMR_ANGLE_VERTICAL_FR)
        public void onOSRMRAngleVerticalFRChanged(int i) {
        }

        @SignalReceiver(signalId = 336)
        public void onParkAutoUnlockChanged(ICarSettings.ParkAutoUnlockState parkAutoUnlockState) {
        }

        @SignalReceiver(signalId = 375)
        public void onPowerKeepRemindConfChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 374)
        public void onPowerKeepRemindReqChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 370)
        public void onPowerKeepReqChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 372)
        public void onPowerKeepReqVdChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 377)
        public void onPowerOffReqVdChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 559)
        public void onRLSPABUttonStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 540)
        public void onRLSeatWelcomeChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 560)
        public void onRRSPABUttonStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 538)
        public void onRRSeatWelcomeChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 113)
        public void onRainstormModeChanged(ICarSettings.RainstormMode rainstormMode) {
        }

        @SignalReceiver(signalId = 310)
        public void onRearviewMirrorCmdForReverseChanged(ICarSettings.RearviewMirrorCmdForReverse rearviewMirrorCmdForReverse) {
        }

        @SignalReceiver(signalId = 252)
        public void onRearviewMirrorStateLChanged(ICarSettings.RearviewMirrorState rearviewMirrorState) {
        }

        @SignalReceiver(signalId = 253)
        public void onRearviewMirrorStateRChanged(ICarSettings.RearviewMirrorState rearviewMirrorState) {
        }

        @SignalReceiver(signalId = 563)
        public void onSeatNapModePositionStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 335)
        public void onStpOnBrkToRelParkBrkIndOnStateChanged(ICarSettings.StpOnBrkToRelParkBrkIndOnState stpOnBrkToRelParkBrkIndOnState) {
        }

        @SignalReceiver(signalId = 105)
        public void onTSRAudioWarningEnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = 104)
        public void onTSREnableStateChanged(IBaseApi.EnableState enableState) {
        }

        @SignalReceiver(signalId = ICarSettings.WCLCM_WORK_MODE_STATE)
        public void onWCLCMWorkModeChanged(ICarSettings.WCLCMWorkMode wCLCMWorkMode) {
        }

        @SignalReceiver(signalId = 515)
        public void onWCMCtlChargingStateChanged(ICarSettings.WCMCtlChargingState wCMCtlChargingState) {
        }

        @SignalReceiver(signalId = 520, valueToEnabledState = true)
        public void onWCMCtlErrorStateStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 516)
        public void onWCMCtlFailureStateChanged(ICarSettings.WCMCtlFailureState wCMCtlFailureState) {
        }

        @SignalReceiver(signalId = 519, valueToEnabledState = true)
        public void onWCMCtlPromptStateStateChanged(boolean z) {
        }

        @SignalReceiver(signalId = 448)
        public void onWCMCtlResultChanged(ICarSettings.WCMCtlResult wCMCtlResult) {
        }
    }

    private CarSettingsManager() {
    }

    public static synchronized CarSettingsManager get() {
        CarSettingsManager carSettingsManager;
        if (sMe != null) {
            carSettingsManager = sMe;
        } else {
            carSettingsManager = new CarSettingsManager();
            sMe = carSettingsManager;
        }
        return carSettingsManager;
    }

    public ICarSettings.ADASELKStatus getADASELKStatus() {
        return ICarSettings.ADASELKStatus.valueOf(getSignalValue(526).intValue());
    }

    public ICarSettings.ADASWarnType getADASWarnType() {
        return ICarSettings.ADASWarnType.valueOf(getSignalValue(108).intValue());
    }

    public IBaseApi.EnableStateOp getAEBOffEnableState() {
        return IBaseApi.EnableStateOp.valueOf(getSignalValue(103).intValue());
    }

    public IBaseApi.EnableState getAVASEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(109).intValue());
    }

    public IBaseApi.EnableState getAcSmart() {
        return IBaseApi.EnableState.valueOf(getSignalValue(416).intValue());
    }

    public ICarSettings.AutoWiperMode getAutoWiperMode() {
        return ICarSettings.AutoWiperMode.valueOf(getSignalValue(112).intValue());
    }

    public ICarSettings.BSDMDowState getBSDMDowState() {
        return ICarSettings.BSDMDowState.valueOf(getSignalValue(454).intValue());
    }

    public ICarSettings.BSDMLcaState getBSDMLcaState() {
        return ICarSettings.BSDMLcaState.valueOf(getSignalValue(452).intValue());
    }

    public ICarSettings.BSDMRctaAssistType getBSDMRctaAssistType() {
        return ICarSettings.BSDMRctaAssistType.valueOf(getSignalValue(458).intValue());
    }

    public ICarSettings.BSDMRctaState getBSDMRctaState() {
        return ICarSettings.BSDMRctaState.valueOf(getSignalValue(456).intValue());
    }

    public ICarSettings.BSDMRcwState getBSDMRcwState() {
        return ICarSettings.BSDMRcwState.valueOf(getSignalValue(460).intValue());
    }

    public ICarSettings.ComfortParkState getComfortParkState() {
        return ICarSettings.ComfortParkState.valueOf(getSignalValue(ICarSettings.COMFORT_PARK_STATE).intValue());
    }

    public ICarSettings.CruiseAssistMode getCruiseAssistMode() {
        return ICarSettings.CruiseAssistMode.valueOf(getSignalValue(271).intValue());
    }

    public ICarSettings.DragMode getDragMode() {
        return ICarSettings.DragMode.valueOf(getSignalValue(332).intValue());
    }

    public ICarSettings.EMSEngWaveStatus getEMSEngWaveStatus() {
        return ICarSettings.EMSEngWaveStatus.valueOf(getSignalValue(528).intValue());
    }

    public ICarSettings.EPBSystemMsg getEPBSystemMsg() {
        return ICarSettings.EPBSystemMsg.valueOf(getSignalValue(334).intValue());
    }

    public ICarSettings.EPBSystemState getEPBSystemState() {
        return ICarSettings.EPBSystemState.valueOf(getSignalValue(330).intValue());
    }

    public IBaseApi.EnableState getETCEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(110).intValue());
    }

    public IBaseApi.EnableStateOp getFCWOffEnableState() {
        return IBaseApi.EnableStateOp.valueOf(getSignalValue(101).intValue());
    }

    public ICarSettings.FCWWarningDistance getFCWWarningDistance() {
        return ICarSettings.FCWWarningDistance.valueOf(getSignalValue(102).intValue());
    }

    public IBaseApi.EnableState getFrontCarDistanceDisplayEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(267).intValue());
    }

    public int getHudAngleSt() {
        return getSignalValue(468).intValue();
    }

    public int getHudBrightnessSt() {
        return getSignalValue(464).intValue();
    }

    public int getHudHeightSt() {
        return getSignalValue(466).intValue();
    }

    public ICarSettings.HudMode getHudMode() {
        return ICarSettings.HudMode.valueOf(getSignalValue(470).intValue());
    }

    public ICarSettings.HudOffState getHudOffState() {
        return ICarSettings.HudOffState.valueOf(getSignalValue(462).intValue());
    }

    public IBaseApi.EnableState getIAccEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(106).intValue());
    }

    public IBaseApi.EnableState getIntelligentAvoidEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(269).intValue());
    }

    public IBaseApi.EnableState getKeyNotforgetWarningEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(362).intValue());
    }

    public ICarSettings.LksMode getLksMode() {
        return ICarSettings.LksMode.valueOf(getSignalValue(100).intValue());
    }

    public ICarSettings.LksState getLksState() {
        return ICarSettings.LksState.valueOf(getSignalValue(99).intValue());
    }

    public IBaseApi.EnableState getOSRMAutoFoldEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(107).intValue());
    }

    public ICarSettings.OSRMRAngleCmd getOSRMRAngleCmd() {
        return ICarSettings.OSRMRAngleCmd.valueOf(getSignalValue(ICarSettings.OSRM_ANGLE_CMD_STATE).intValue());
    }

    public int getOSRMRAngleHorizontalFL() {
        return getSignalValue(ICarSettings.OSRMR_ANGLE_HORIZONTAL_FL).intValue();
    }

    public int getOSRMRAngleHorizontalFR() {
        return getSignalValue(ICarSettings.OSRMR_ANGLE_HORIZONTAL_FR).intValue();
    }

    public int getOSRMRAngleVerticalFL() {
        return getSignalValue(ICarSettings.OSRMR_ANGLE_VERTICAL_FL).intValue();
    }

    public int getOSRMRAngleVerticalFR() {
        return getSignalValue(ICarSettings.OSRMR_ANGLE_VERTICAL_FR).intValue();
    }

    public ICarSettings.ParkAutoUnlockState getParkAutoUnlock() {
        return ICarSettings.ParkAutoUnlockState.valueOf(getSignalValue(336).intValue());
    }

    public IBaseApi.EnableState getPowerKeepRemindConf() {
        return IBaseApi.EnableState.valueOf(getSignalValue(375).intValue());
    }

    public IBaseApi.EnableState getPowerKeepRemindReq() {
        return IBaseApi.EnableState.valueOf(getSignalValue(374).intValue());
    }

    public IBaseApi.EnableState getPowerKeepReq() {
        return IBaseApi.EnableState.valueOf(getSignalValue(370).intValue());
    }

    public IBaseApi.EnableState getPowerKeepReqVd() {
        return IBaseApi.EnableState.valueOf(getSignalValue(372).intValue());
    }

    public IBaseApi.EnableState getPowerOffReqVd() {
        return IBaseApi.EnableState.valueOf(getSignalValue(377).intValue());
    }

    public IBaseApi.EnableState getRLSPABUttonState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(559).intValue());
    }

    public IBaseApi.EnableState getRLSeatWelcome() {
        return IBaseApi.EnableState.valueOf(getSignalValue(540).intValue());
    }

    public IBaseApi.EnableState getRRSPABUttonState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(560).intValue());
    }

    public IBaseApi.EnableState getRRSeatWelcome() {
        return IBaseApi.EnableState.valueOf(getSignalValue(538).intValue());
    }

    public ICarSettings.RainstormMode getRainstormMode() {
        return ICarSettings.RainstormMode.valueOf(getSignalValue(113).intValue());
    }

    public ICarSettings.RearviewMirrorCmdForReverse getRearviewMirrorCmdForReverse() {
        return ICarSettings.RearviewMirrorCmdForReverse.valueOf(getSignalValue(310).intValue());
    }

    public ICarSettings.RearviewMirrorState getRearviewMirrorStateL() {
        return ICarSettings.RearviewMirrorState.valueOf(getSignalValue(252).intValue());
    }

    public ICarSettings.RearviewMirrorState getRearviewMirrorStateR() {
        return ICarSettings.RearviewMirrorState.valueOf(getSignalValue(253).intValue());
    }

    public IBaseApi.EnableState getSeatNapModePositionState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(563).intValue());
    }

    public ICarSettings.StpOnBrkToRelParkBrkIndOnState getStpOnBrkToRelParkBrkIndOnState() {
        return ICarSettings.StpOnBrkToRelParkBrkIndOnState.valueOf(getSignalValue(335).intValue());
    }

    public IBaseApi.EnableState getTSRAudioWarningEnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(105).intValue());
    }

    public IBaseApi.EnableState getTSREnableState() {
        return IBaseApi.EnableState.valueOf(getSignalValue(104).intValue());
    }

    public ICarSettings.WCLCMWorkMode getWCLCMWorkMode() {
        return ICarSettings.WCLCMWorkMode.valueOf(getSignalValue(ICarSettings.WCLCM_WORK_MODE_STATE).intValue());
    }

    public ICarSettings.WCMCtlChargingState getWCMCtlChargingState() {
        return ICarSettings.WCMCtlChargingState.valueOf(getSignalValue(515).intValue());
    }

    public ICarSettings.WCMCtlFailureState getWCMCtlFailureState() {
        return ICarSettings.WCMCtlFailureState.valueOf(getSignalValue(516).intValue());
    }

    public ICarSettings.WCMCtlResult getWCMCtlResult() {
        return ICarSettings.WCMCtlResult.valueOf(getSignalValue(448).intValue());
    }

    public boolean isDoorHandleElectricHideEnabled() {
        return getSignalValue(ICarSettings.DOOR_HANDLE_ELECTRIC_HIDE_STATE).byteValue() == 1;
    }

    public boolean isDriverEasyEntryAndExitEnabled() {
        return getSignalValue(ICarSettings.DRIVER_EASY_ENTRY_EXIT_STATE).byteValue() == 1;
    }

    public boolean isFrontWiperServicePositionEnabled() {
        return getSignalValue(111).byteValue() == 1;
    }

    public boolean isWCMCtlErrorStateEnabled() {
        return getSignalValue(520).byteValue() == 1;
    }

    public boolean isWCMCtlPromptStateEnabled() {
        return getSignalValue(519).byteValue() == 1;
    }

    public boolean performFrontWiperServicePositionButtonClick() {
        return setSignalValue(44, (byte) 1);
    }

    public void registerListener(CarSettingsListener carSettingsListener) {
        registerReceiver(carSettingsListener);
    }

    public boolean setADASELKEnabled(boolean z) {
        return setSignalValue(527, z ? (byte) 2 : (byte) 1);
    }

    public boolean setADASWarnType(ICarSettings.ADASWarnType aDASWarnType) {
        return setSignalValue(41, aDASWarnType.value);
    }

    public boolean setAEBOffEnabled(boolean z) {
        return setSignalValue(36, z ? (byte) 1 : (byte) 2);
    }

    public boolean setAVASEnabled(boolean z) {
        return setSignalValue(42, z ? (byte) 2 : (byte) 1);
    }

    public boolean setAcSmartEnabled(boolean z) {
        return setSignalValue(ICarSettings.AC_SMART_SETTING, z ? (byte) 2 : (byte) 1);
    }

    public boolean setAutoWiperMode(ICarSettings.AutoWiperMode autoWiperMode) {
        return setSignalValue(45, autoWiperMode.value);
    }

    public boolean setBSDMDowCtl(ICarSettings.BSDMDowCtl bSDMDowCtl) {
        return setSignalValue(455, bSDMDowCtl.value);
    }

    public boolean setBSDMLcaCtl(ICarSettings.BSDMLcaCtl bSDMLcaCtl) {
        return setSignalValue(453, bSDMLcaCtl.value);
    }

    public boolean setBSDMRctaAssistType(ICarSettings.BSDMRctaAssistType bSDMRctaAssistType) {
        return setSignalValue(459, bSDMRctaAssistType.value);
    }

    public boolean setBSDMRctaCtl(ICarSettings.BSDMRctaCtl bSDMRctaCtl) {
        return setSignalValue(457, bSDMRctaCtl.value);
    }

    public boolean setBSDMRcwCtl(ICarSettings.BSDMRcwCtl bSDMRcwCtl) {
        return setSignalValue(461, bSDMRcwCtl.value);
    }

    public boolean setComfortParkStateEnabled(boolean z) {
        return setSignalValue(ICarSettings.COMFORT_PARK_SETTING, z ? (byte) 2 : (byte) 1);
    }

    public boolean setCruiseAssistMode(ICarSettings.CruiseAssistMode cruiseAssistMode) {
        return setSignalValue(272, cruiseAssistMode.value);
    }

    public boolean setDoorHandleElectricHideEnabled(boolean z) {
        return setSignalValue(ICarSettings.DOOR_HANDLE_ELECTRIC_HIDE_SETTING, z ? (byte) 2 : (byte) 1);
    }

    public boolean setDragModeEnabled(boolean z) {
        return setSignalValue(333, z ? (byte) 1 : (byte) 2);
    }

    public boolean setDriverEasyEntryAndExitEnabled(boolean z) {
        return setSignalValue(ICarSettings.DRIVER_EASY_ENTRY_EXIT_SETTING, z ? (byte) 2 : (byte) 1);
    }

    public boolean setEMSEngWaveEnabled(boolean z) {
        return setSignalValue(529, z ? (byte) 2 : (byte) 1);
    }

    public boolean setEPBSystemSetState(ICarSettings.EPBSystemSetState ePBSystemSetState) {
        return setSignalValue(331, ePBSystemSetState.value);
    }

    public boolean setETCEnabled(boolean z) {
        return setSignalValue(43, z ? (byte) 2 : (byte) 1);
    }

    public boolean setFCWOffEnabled(boolean z) {
        return setSignalValue(34, z ? (byte) 1 : (byte) 2);
    }

    public boolean setFCWWarningDistance(ICarSettings.FCWWarningDistance fCWWarningDistance) {
        return setSignalValue(35, fCWWarningDistance.value);
    }

    public boolean setFrontCarDistanceDisplayEnabled(boolean z) {
        return setSignalValue(268, z ? (byte) 2 : (byte) 1);
    }

    public boolean setHudAngleSt(int i) {
        return setSignalValue(469, i);
    }

    public boolean setHudBrightnessSt(int i) {
        return setSignalValue(465, i);
    }

    public boolean setHudHeightSt(int i) {
        return setSignalValue(467, i);
    }

    public boolean setHudMode(ICarSettings.HudMode hudMode) {
        return setSignalValue(471, hudMode.value);
    }

    public boolean setHudOffState(ICarSettings.HudOffState hudOffState) {
        return setSignalValue(463, hudOffState.value);
    }

    public boolean setIAccEnabled(boolean z) {
        return setSignalValue(39, z ? (byte) 2 : (byte) 1);
    }

    public boolean setIntelligentAvoidEnabled(boolean z) {
        return setSignalValue(270, z ? (byte) 2 : (byte) 1);
    }

    public boolean setKeyNotforgetWarningEnabled(boolean z) {
        return setSignalValue(363, z ? (byte) 2 : (byte) 1);
    }

    public boolean setLdwLkaEnabled(boolean z) {
        return setSignalValue(32, z ? (byte) 2 : (byte) 1);
    }

    public boolean setLksMode(ICarSettings.LksMode lksMode) {
        return setSignalValue(33, lksMode.value);
    }

    public boolean setOSRMAutoFoldEnabled(boolean z) {
        return setSignalValue(40, z ? (byte) 2 : (byte) 1);
    }

    public boolean setOSRMRAngleCmd(ICarSettings.OSRMRAngleCmd oSRMRAngleCmd) {
        return setSignalValue(ICarSettings.OSRM_ANGLE_CMD_SETTING, oSRMRAngleCmd.value);
    }

    public boolean setOSRMRAngleSaveCmd(ICarSettings.OSRMRAngleSaveCmd oSRMRAngleSaveCmd) {
        return setSignalValue(ICarSettings.OSRM_ANGLE_SAVE_CMD_SETTING, oSRMRAngleSaveCmd.value);
    }

    public boolean setParkAutoUnlock(ICarSettings.ParkAutoUnlockState parkAutoUnlockState) {
        return setSignalValue(337, parkAutoUnlockState.value);
    }

    public boolean setPowerKeepRemindConfEnabled(boolean z) {
        return setSignalValue(376, z ? (byte) 2 : (byte) 1);
    }

    public boolean setPowerKeepReqEnabled(boolean z) {
        return setSignalValue(371, z ? (byte) 2 : (byte) 1);
    }

    public boolean setPowerKeepReqVdSwitch(boolean z) {
        return setSignalValue(373, z ? (byte) 1 : (byte) 0);
    }

    public boolean setPowerOffReqSwitch(boolean z) {
        return setSignalValue(379, z ? (byte) 1 : (byte) 0);
    }

    public boolean setPowerOffReqVdSwitch(boolean z) {
        return setSignalValue(378, z ? (byte) 1 : (byte) 0);
    }

    public boolean setRLSeatWelcomeEnabled(boolean z) {
        return setSignalValue(541, z ? (byte) 2 : (byte) 1);
    }

    public boolean setRRSeatWelcomeEnabled(boolean z) {
        return setSignalValue(539, z ? (byte) 2 : (byte) 1);
    }

    public boolean setRainstormMode(ICarSettings.ShortcutMode shortcutMode) {
        return setSignalValue(46, shortcutMode.value);
    }

    public boolean setRearviewMirrorCmd(ICarSettings.RearviewMirrorCmd rearviewMirrorCmd) {
        return setSignalValue(251, rearviewMirrorCmd.value);
    }

    public boolean setRearviewMirrorCmdForReverse(ICarSettings.RearviewMirrorCmdForReverse rearviewMirrorCmdForReverse) {
        return setSignalValue(311, rearviewMirrorCmdForReverse.value);
    }

    public boolean setRearviewMirrorCmdVdSwitch(boolean z) {
        return setSignalValue(309, z ? (byte) 1 : (byte) 0);
    }

    public boolean setRearviewMirrorSettingCmd(ICarSettings.RearviewMirrorSettingCmd rearviewMirrorSettingCmd) {
        return setSignalValue(312, rearviewMirrorSettingCmd.value);
    }

    public boolean setSCMNapSettingSave(ICarSettings.NapSettingSave napSettingSave) {
        return setSignalValue(562, napSettingSave.value);
    }

    public boolean setSCMNapSettingSwitch(boolean z) {
        return setSignalValue(561, z ? (byte) 1 : (byte) 0);
    }

    public boolean setTSRAudioWarningEnabled(boolean z) {
        return setSignalValue(38, z ? (byte) 2 : (byte) 1);
    }

    public boolean setTSREnabled(boolean z) {
        return setSignalValue(37, z ? (byte) 2 : (byte) 1);
    }

    public boolean setWCLCMWorkModeCtl(ICarSettings.WCLCMWorkModeCtl wCLCMWorkModeCtl) {
        return setSignalValue(451, wCLCMWorkModeCtl.value);
    }

    public boolean setWCMCtl(ICarSettings.WCMCtl wCMCtl) {
        return setSignalValue(ICarSettings.WCM_CTL_SETTING, wCMCtl.value);
    }

    public void unregisterListener(CarSettingsListener carSettingsListener) {
        unregisterReceiver(carSettingsListener);
    }
}
