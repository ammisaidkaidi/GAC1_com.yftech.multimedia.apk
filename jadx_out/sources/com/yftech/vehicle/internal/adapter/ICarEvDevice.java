package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarEvDevice extends IBaseApi {
    public static final int BATTERY_QUANTITY_PERCENT = 314;
    public static final int BOOK_CHRG_START_DAY = 385;
    public static final int BOOK_CHRG_START_HOUR = 386;
    public static final int BOOK_CHRG_START_MIN = 387;
    public static final int BOOK_CHRG_START_MON = 384;
    public static final int BOOK_CHRG_START_REMAINING_DAY = 380;
    public static final int BOOK_CHRG_START_REMAINING_HOUR = 381;
    public static final int BOOK_CHRG_START_REMAINING_MIN = 382;
    public static final int BOOK_CHRG_START_YEAR = 383;
    public static final int BOOK_CHRG_STOP_DAY = 390;
    public static final int BOOK_CHRG_STOP_HOUR = 391;
    public static final int BOOK_CHRG_STOP_MIN = 392;
    public static final int BOOK_CHRG_STOP_MON = 389;
    public static final int BOOK_CHRG_STOP_YEAR = 388;
    public static final int CHARGE_FULL_NOTICE_LEVEL = 324;
    public static final int CHARGE_MODE_STATE = 393;
    public static final int CHARGE_SOC_PERCENT_SETTING = 319;
    public static final int CHARGE_SOC_PERCENT_STATE = 318;
    public static final int CHARGE_SOC_PERCENT_VALID_STATE = 320;
    public static final int CHARGE_WARM_TIME_SETTING = 322;
    public static final int CHARGE_WARM_TIME_STATE = 321;
    public static final int CHARGE_WARM_TIME_VALID_STATE = 323;
    public static final int CHARG_CURRENT_SETTING = 493;
    public static final int CHARG_CURRENT_STATE = 492;
    public static final int CHG_ACTIVE_STATE = 490;
    public static final int CHG_END_REQ_SETTING = 491;
    public static final int DISCHG_ACTIVE_SETTING = 483;
    public static final int DISCHG_ACTIVE_STATE = 482;
    public static final int DISCHG_END_VEHRNG_STATE = 484;
    public static final int DISCHG_ENG_START_CFG_SETTING = 479;
    public static final int DISCHG_ENG_START_CFG_STATE = 478;
    public static final int DISCHG_SET_INFO = 494;
    public static final int DISCHG_SOC_CFG_SETTING = 481;
    public static final int DISCHG_SOC_CFG_STATE = 480;
    public static final int DISCHG_TIME_CFG_SETTING = 486;
    public static final int DISCHG_TIME_CFG_STATE = 485;
    public static final int DRIVE_MODE_EV = 355;
    public static final int DRIVE_MODE_EV_SETTING = 356;
    public static final int DRV_TARGET_SOE_SETTING = 477;
    public static final int DRV_TARGET_SOE_STATE = 476;
    public static final int DSP_CHG_STATE = 489;
    public static final int ECO_MODE_VALID_STATE = 359;
    public static final int ECO_NORMAL_SPORT_MODE_VALID_STATE = 361;
    public static final int ECO_PLUS_MODE = 357;
    public static final int ECO_PLUS_MODE_VALID_STATE = 358;
    public static final int ENERGY_MODEL = 474;
    public static final int ENERGY_MODEL_SETTING = 475;
    public static final int ENG_SELF_MAINT_DISP_STATE = 473;
    public static final int HEAL_CHECK_NOTICE_STATE = 325;
    public static final int IPEDAL_CREEP_SETTING = 316;
    public static final int IPEDAL_CREEP_STATE = 315;
    public static final int IPEDAL_CREEP_VALID_STATE = 317;
    public static final int IPEDAL_MODE_VALID_STATE = 360;
    public static final int OBC_CONN_STATE = 513;
    public static final int PLUG_AUTO_UNLOCK_CFG = 487;
    public static final int PLUG_AUTO_UNLOCK_CFG_SETTING = 488;
    public static final int POWER_FLOW_MODE = 313;
    public static final int RECUPERATION_LEVEL = 352;
    public static final int RECUPERATION_LEVEL_A9E = 412;
    public static final int RECUPERATION_LEVEL_A9E_SETTING = 413;
    public static final int RECUPERATION_LEVEL_SETTING = 353;
    public static final int RECUPERATION_LEVEL_VALID_STATE = 354;
    public static final int REPEAT_BOOK_CHRG_DAY1 = 394;
    public static final int REPEAT_BOOK_CHRG_DAY2 = 395;
    public static final int REPEAT_BOOK_CHRG_DAY3 = 396;
    public static final int REPEAT_BOOK_CHRG_DAY4 = 397;
    public static final int REPEAT_BOOK_CHRG_DAY5 = 398;
    public static final int REPEAT_BOOK_CHRG_DAY6 = 399;
    public static final int REPEAT_BOOK_CHRG_DAY7 = 400;
    public static final int VEH_RDY_STATE = 472;

    public enum ChargActive {
        NOT_CHARGING(0),
        CHARGING(1),
        __UNKNOWN__(-1);

        public final int value;

        ChargActive(int i) {
            this.value = i;
        }

        public static ChargActive valueOf(int i) {
            if (i == NOT_CHARGING.value) {
                return NOT_CHARGING;
            }
            return i == CHARGING.value ? CHARGING : __UNKNOWN__;
        }
    }

    public enum ChargEnd {
        NO_COMMAND(0),
        END_CHARGE(1),
        __UNKNOWN__(-1);

        public final int value;

        ChargEnd(int i) {
            this.value = i;
        }

        public static ChargEnd valueOf(int i) {
            if (i == NO_COMMAND.value) {
                return NO_COMMAND;
            }
            return i == END_CHARGE.value ? END_CHARGE : __UNKNOWN__;
        }
    }

    public enum ChargState {
        CHARGE_CURRENT_VOLTAGE_INTERFACE(0),
        BLANK_MODULE_INTERFACE(1),
        __UNKNOWN__(-1);

        public final int value;

        ChargState(int i) {
            this.value = i;
        }

        public static ChargState valueOf(int i) {
            if (i == CHARGE_CURRENT_VOLTAGE_INTERFACE.value) {
                return CHARGE_CURRENT_VOLTAGE_INTERFACE;
            }
            return i == BLANK_MODULE_INTERFACE.value ? BLANK_MODULE_INTERFACE : __UNKNOWN__;
        }
    }

    public enum ChargeFullNoticeLevel {
        NO_NOTICE(0),
        LEVEL_2(1),
        LEVEL_3(2),
        __UNKNOWN__(-1);

        public final int value;

        ChargeFullNoticeLevel(int i) {
            this.value = i;
        }

        public static ChargeFullNoticeLevel valueOf(int i) {
            if (i == NO_NOTICE.value) {
                return NO_NOTICE;
            }
            if (i == LEVEL_2.value) {
                return LEVEL_2;
            }
            return i == LEVEL_3.value ? LEVEL_3 : __UNKNOWN__;
        }
    }

    public enum ChargeMode {
        IMMEDIATELY(0),
        BOOKING(1),
        __UNKNOWN__(-1);

        public final int value;

        ChargeMode(int i) {
            this.value = i;
        }

        public static ChargeMode valueOf(int i) {
            if (i == IMMEDIATELY.value) {
                return IMMEDIATELY;
            }
            return i == BOOKING.value ? BOOKING : __UNKNOWN__;
        }
    }

    public enum ChargeSocPercent {
        NO_COMMAND(0),
        PERC_50(1),
        PERC_55(2),
        PERC_60(3),
        PERC_65(4),
        PERC_70(5),
        PERC_75(6),
        PERC_80(7),
        PERC_85(8),
        PERC_90(9),
        PERC_95(10),
        PERC_100(11),
        __UNKNOWN__(-1);

        public final int value;

        ChargeSocPercent(int i) {
            this.value = i;
        }

        public static ChargeSocPercent valueOf(int i) {
            if (i == NO_COMMAND.value) {
                return NO_COMMAND;
            }
            if (i == PERC_50.value) {
                return PERC_50;
            }
            if (i == PERC_55.value) {
                return PERC_55;
            }
            if (i == PERC_60.value) {
                return PERC_60;
            }
            if (i == PERC_65.value) {
                return PERC_65;
            }
            if (i == PERC_70.value) {
                return PERC_70;
            }
            if (i == PERC_75.value) {
                return PERC_75;
            }
            if (i == PERC_80.value) {
                return PERC_80;
            }
            if (i == PERC_85.value) {
                return PERC_85;
            }
            if (i == PERC_90.value) {
                return PERC_90;
            }
            if (i == PERC_95.value) {
                return PERC_95;
            }
            return i == PERC_100.value ? PERC_100 : __UNKNOWN__;
        }
    }

    public enum ChargeWarmTime {
        HOUR_1(1),
        HOUR_2(2),
        HOUR_3(3),
        HOUR_4(4),
        HOUR_5(5),
        HOUR_6(6),
        HOUR_7(7),
        HOUR_8(8),
        HOUR_9(9),
        HOUR_10(10),
        HOUR_11(11),
        ON(12),
        OFF(15),
        __UNKNOWN__(-1);

        public final int value;

        ChargeWarmTime(int i) {
            this.value = i;
        }

        public static ChargeWarmTime valueOf(int i) {
            if (i == HOUR_1.value) {
                return HOUR_1;
            }
            if (i == HOUR_2.value) {
                return HOUR_2;
            }
            if (i == HOUR_3.value) {
                return HOUR_3;
            }
            if (i == HOUR_4.value) {
                return HOUR_4;
            }
            if (i == HOUR_5.value) {
                return HOUR_5;
            }
            if (i == HOUR_6.value) {
                return HOUR_6;
            }
            if (i == HOUR_7.value) {
                return HOUR_7;
            }
            if (i == HOUR_8.value) {
                return HOUR_8;
            }
            if (i == HOUR_9.value) {
                return HOUR_9;
            }
            if (i == HOUR_10.value) {
                return HOUR_10;
            }
            if (i == HOUR_11.value) {
                return HOUR_11;
            }
            if (i == ON.value) {
                return ON;
            }
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }

    public enum DischgSetInfo {
        NO_INFORMATION(0),
        SET_SUCCEED_DISCHARGE_ALLOW(1),
        LOWER_THAN_SET_DISCHARGE_NOT_ALLOW(2),
        LOWER_OIL_SET_ENGINE_NOT_ALLOW(3),
        __UNKNOWN__(-1);

        public final int value;

        DischgSetInfo(int i) {
            this.value = i;
        }

        public static DischgSetInfo valueOf(int i) {
            if (i == NO_INFORMATION.value) {
                return NO_INFORMATION;
            }
            if (i == SET_SUCCEED_DISCHARGE_ALLOW.value) {
                return SET_SUCCEED_DISCHARGE_ALLOW;
            }
            if (i == LOWER_THAN_SET_DISCHARGE_NOT_ALLOW.value) {
                return LOWER_THAN_SET_DISCHARGE_NOT_ALLOW;
            }
            return i == LOWER_OIL_SET_ENGINE_NOT_ALLOW.value ? LOWER_OIL_SET_ENGINE_NOT_ALLOW : __UNKNOWN__;
        }
    }

    public enum DriveModeEv {
        INVALID(0),
        ECO_PLUS_MODE(1),
        ECO_MODE(2),
        NORMAL_MODE(3),
        DRIVING_MODE(4),
        SPORT_MODE(5),
        IPEDAL_ON(6),
        IPEDAL_OFF(7),
        __UNKNOWN__(-1);

        public final int value;

        DriveModeEv(int i) {
            this.value = i;
        }

        public static DriveModeEv valueOf(int i) {
            if (i == INVALID.value) {
                return INVALID;
            }
            if (i == ECO_PLUS_MODE.value) {
                return ECO_PLUS_MODE;
            }
            if (i == ECO_MODE.value) {
                return ECO_MODE;
            }
            if (i == NORMAL_MODE.value) {
                return NORMAL_MODE;
            }
            if (i == DRIVING_MODE.value) {
                return DRIVING_MODE;
            }
            if (i == SPORT_MODE.value) {
                return SPORT_MODE;
            }
            if (i == IPEDAL_ON.value) {
                return IPEDAL_ON;
            }
            return i == IPEDAL_OFF.value ? IPEDAL_OFF : __UNKNOWN__;
        }
    }

    public enum EcoPlusMode {
        ON(1),
        OFF(2),
        __UNKNOWN__(-1);

        public final int value;

        EcoPlusMode(int i) {
            this.value = i;
        }

        public static EcoPlusMode valueOf(int i) {
            if (i == ON.value) {
                return ON;
            }
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }

    public enum EnergyModel {
        NORMAL(0),
        INTELLIGENT_HYBRID(1),
        ELECTICITY_DRIVE_FIRST(2),
        ELECTICITY_CONSERVATION(3),
        __UNKNOWN__(-1);

        public final int value;

        EnergyModel(int i) {
            this.value = i;
        }

        public static EnergyModel valueOf(int i) {
            if (i == NORMAL.value) {
                return NORMAL;
            }
            if (i == INTELLIGENT_HYBRID.value) {
                return INTELLIGENT_HYBRID;
            }
            if (i == ELECTICITY_DRIVE_FIRST.value) {
                return ELECTICITY_DRIVE_FIRST;
            }
            return i == ELECTICITY_CONSERVATION.value ? ELECTICITY_CONSERVATION : __UNKNOWN__;
        }
    }

    public enum EngStartCfg {
        NO_REQUEST(0),
        ENGINE_START_ENABLE(1),
        ENGINE_START_DISABLE(2),
        __UNKNOWN__(-1);

        public final int value;

        EngStartCfg(int i) {
            this.value = i;
        }

        public static EngStartCfg valueOf(int i) {
            if (i == NO_REQUEST.value) {
                return NO_REQUEST;
            }
            if (i == ENGINE_START_ENABLE.value) {
                return ENGINE_START_ENABLE;
            }
            return i == ENGINE_START_DISABLE.value ? ENGINE_START_DISABLE : __UNKNOWN__;
        }
    }

    public enum HealCheckNotice {
        NO_NOTICE(0),
        NOTICE(1),
        __UNKNOWN__(-1);

        public final int value;

        HealCheckNotice(int i) {
            this.value = i;
        }

        public static HealCheckNotice valueOf(int i) {
            if (i == NO_NOTICE.value) {
                return NO_NOTICE;
            }
            return i == NOTICE.value ? NOTICE : __UNKNOWN__;
        }
    }

    public enum ObcConnState {
        DISCONNECTED(0),
        HALF_CONNECTED_CHARGING_V2V_V2L(1),
        CONNECTED_CHARGING_V2V(2),
        CONNECTED_V2L(4),
        __UNKNOWN__(-1);

        public final int value;

        ObcConnState(int i) {
            this.value = i;
        }

        public static ObcConnState valueOf(int i) {
            if (i == DISCONNECTED.value) {
                return DISCONNECTED;
            }
            if (i == HALF_CONNECTED_CHARGING_V2V_V2L.value) {
                return HALF_CONNECTED_CHARGING_V2V_V2L;
            }
            if (i == CONNECTED_CHARGING_V2V.value) {
                return CONNECTED_CHARGING_V2V;
            }
            return i == CONNECTED_V2L.value ? CONNECTED_V2L : __UNKNOWN__;
        }
    }

    public enum PlugAutoUnlockCfg {
        NOT_AUTO_UNLOCK(0),
        AUTO_UNLOCK(1),
        __UNKNOWN__(-1);

        public final int value;

        PlugAutoUnlockCfg(int i) {
            this.value = i;
        }

        public static PlugAutoUnlockCfg valueOf(int i) {
            if (i == NOT_AUTO_UNLOCK.value) {
                return NOT_AUTO_UNLOCK;
            }
            return i == AUTO_UNLOCK.value ? AUTO_UNLOCK : __UNKNOWN__;
        }
    }

    public enum PowerFlowMode {
        VEHICLE_INITIAL(0),
        VEHICLE_STANDBY(1),
        IDLE_CHARGE(2),
        EV(3),
        RANGE_EXTEND_MODE1(4),
        RANGE_EXTEND_MODE2(5),
        ENGINE_MODE(6),
        HEV_MODE(7),
        REGENERATION_MODE(8),
        RANGE_EXTEND_MODE3(9),
        HEV_MODE2(10),
        __UNKNOWN__(-1);

        public final int value;

        PowerFlowMode(int i) {
            this.value = i;
        }

        public static PowerFlowMode valueOf(int i) {
            if (i == VEHICLE_INITIAL.value) {
                return VEHICLE_INITIAL;
            }
            if (i == VEHICLE_STANDBY.value) {
                return VEHICLE_STANDBY;
            }
            if (i == IDLE_CHARGE.value) {
                return IDLE_CHARGE;
            }
            if (i == EV.value) {
                return EV;
            }
            if (i == RANGE_EXTEND_MODE1.value) {
                return RANGE_EXTEND_MODE1;
            }
            if (i == RANGE_EXTEND_MODE2.value) {
                return RANGE_EXTEND_MODE2;
            }
            if (i == ENGINE_MODE.value) {
                return ENGINE_MODE;
            }
            if (i == HEV_MODE.value) {
                return HEV_MODE;
            }
            if (i == REGENERATION_MODE.value) {
                return REGENERATION_MODE;
            }
            if (i == RANGE_EXTEND_MODE3.value) {
                return RANGE_EXTEND_MODE3;
            }
            return i == HEV_MODE2.value ? HEV_MODE2 : __UNKNOWN__;
        }
    }

    public enum RecpLevelAvail {
        L0_L3(0),
        L0_L1_AND_L2(1),
        L0(2),
        ALL_AVAILABLE(3),
        __UNKNOWN__(-1);

        public final int value;

        RecpLevelAvail(int i) {
            this.value = i;
        }

        public static RecpLevelAvail valueOf(int i) {
            if (i == L0_L3.value) {
                return L0_L3;
            }
            if (i == L0_L1_AND_L2.value) {
                return L0_L1_AND_L2;
            }
            if (i == L0.value) {
                return L0;
            }
            return i == ALL_AVAILABLE.value ? ALL_AVAILABLE : __UNKNOWN__;
        }
    }

    public enum RecuperationLevel {
        NOT_VALID(0),
        LEVEL_0_OFF(1),
        LEVEL_1_LOW(2),
        LEVEL_2_MIDDLE(3),
        LEVEL_3_HIGH(4),
        __UNKNOWN__(-1);

        public final int value;

        RecuperationLevel(int i) {
            this.value = i;
        }

        public static RecuperationLevel valueOf(int i) {
            if (i == NOT_VALID.value) {
                return NOT_VALID;
            }
            if (i == LEVEL_0_OFF.value) {
                return LEVEL_0_OFF;
            }
            if (i == LEVEL_1_LOW.value) {
                return LEVEL_1_LOW;
            }
            if (i == LEVEL_2_MIDDLE.value) {
                return LEVEL_2_MIDDLE;
            }
            return i == LEVEL_3_HIGH.value ? LEVEL_3_HIGH : __UNKNOWN__;
        }
    }

    public enum RecuperationLevelA9E {
        NOT_VALID(0),
        LEVEL_1_OFF(1),
        LEVEL_2_LOW(2),
        LEVEL_3_MIDDLE(3),
        LEVEL_4_HIGH(4),
        __UNKNOWN__(-1);

        public final int value;

        RecuperationLevelA9E(int i) {
            this.value = i;
        }

        public static RecuperationLevelA9E valueOf(int i) {
            if (i == NOT_VALID.value) {
                return NOT_VALID;
            }
            if (i == LEVEL_1_OFF.value) {
                return LEVEL_1_OFF;
            }
            if (i == LEVEL_2_LOW.value) {
                return LEVEL_2_LOW;
            }
            if (i == LEVEL_3_MIDDLE.value) {
                return LEVEL_3_MIDDLE;
            }
            return i == LEVEL_4_HIGH.value ? LEVEL_4_HIGH : __UNKNOWN__;
        }
    }

    public enum RepeatBookMode {
        NOT_REPEAT(0),
        BOOKING(1),
        __UNKNOWN__(-1);

        public final int value;

        RepeatBookMode(int i) {
            this.value = i;
        }

        public static RepeatBookMode valueOf(int i) {
            if (i == NOT_REPEAT.value) {
                return NOT_REPEAT;
            }
            return i == BOOKING.value ? BOOKING : __UNKNOWN__;
        }
    }
}
