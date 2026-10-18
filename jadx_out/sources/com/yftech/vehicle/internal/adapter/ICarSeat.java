package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarSeat extends IBaseApi {
    public static final int ACCOUNT_DELETE1_SETTING = 503;
    public static final int CHILD_LOCK_RL = 347;
    public static final int CHILD_LOCK_RL_SETTING = 348;
    public static final int CHILD_LOCK_RR = 349;
    public static final int CHILD_LOCK_RR_SETTING = 350;
    public static final int CURR_ACCOUNT_CFG_SETTING = 502;
    public static final int CURR_PEPS_PUID_STATE = 501;
    public static final int DRIVER_SAVE_RECAL_COMD_SETTING = 504;
    public static final int HEAT_LEVEL_FL = 87;
    public static final int HEAT_LEVEL_FL_SETTING = 15;
    public static final int HEAT_LEVEL_FR = 88;
    public static final int HEAT_LEVEL_FR_SETTING = 16;
    public static final int HEAT_LEVEL_RL = 89;
    public static final int HEAT_LEVEL_RL_SETTING = 17;
    public static final int HEAT_LEVEL_RR = 90;
    public static final int HEAT_LEVEL_RR_SETTING = 18;
    public static final int HEAT_VENT_AUTO_MODE_FL = 95;
    public static final int HEAT_VENT_AUTO_MODE_FL_SETTING = 27;
    public static final int HEAT_VENT_AUTO_MODE_FR = 96;
    public static final int HEAT_VENT_AUTO_MODE_FR_SETTING = 28;
    public static final int HEAT_VENT_AUTO_MODE_RL = 97;
    public static final int HEAT_VENT_AUTO_MODE_RL_SETTING = 29;
    public static final int HEAT_VENT_AUTO_MODE_RR = 98;
    public static final int HEAT_VENT_AUTO_MODE_RR_SETTING = 30;
    public static final int HEAT_VENT_MODE_FL_SETTING = 23;
    public static final int HEAT_VENT_MODE_FR_SETTING = 24;
    public static final int HEAT_VENT_MODE_RL_SETTING = 25;
    public static final int HEAT_VENT_MODE_RR_SETTING = 26;
    public static final int MEMSYS_OPERATION_REQ_STATE = 506;
    public static final int SEAT_MASSAGE_LEVEL_RL = 296;
    public static final int SEAT_MASSAGE_LEVEL_RL_SETTING = 297;
    public static final int SEAT_MASSAGE_LEVEL_RR = 302;
    public static final int SEAT_MASSAGE_LEVEL_RR_SETTING = 303;
    public static final int SEAT_MASSAGE_MODE_RL = 294;
    public static final int SEAT_MASSAGE_MODE_RL_SETTING = 295;
    public static final int SEAT_MASSAGE_MODE_RR = 300;
    public static final int SEAT_MASSAGE_MODE_RR_SETTING = 301;
    public static final int SEAT_MASSAGE_ONOFF_RL = 292;
    public static final int SEAT_MASSAGE_ONOFF_RL_SETTING = 293;
    public static final int SEAT_MASSAGE_ONOFF_RR = 298;
    public static final int SEAT_MASSAGE_ONOFF_RR_SETTING = 299;
    public static final int SEAT_OPERATION_STATE = 505;
    public static final int SEAT_STEERING_WHEEL_SETTING = 31;
    public static final int VENT_LEVEL_FL = 91;
    public static final int VENT_LEVEL_FL_SETTING = 19;
    public static final int VENT_LEVEL_FR = 92;
    public static final int VENT_LEVEL_FR_SETTING = 20;
    public static final int VENT_LEVEL_RL = 93;
    public static final int VENT_LEVEL_RL_SETTING = 21;
    public static final int VENT_LEVEL_RR = 94;
    public static final int VENT_LEVEL_RR_SETTING = 22;

    public enum AccountDelete {
        ACCOUNT_1(1),
        ACCOUNT_2(2),
        ACCOUNT_3(4),
        ACCOUNT_4(8),
        ACCOUNT_5(10),
        ACCOUNT_6(32),
        ACCOUNT_7(64),
        ACCOUNT_8(80),
        __UNKNOWN__(-1);

        public final int value;

        AccountDelete(int i) {
            this.value = i;
        }

        public static AccountDelete valueOf(int i) {
            if (i == ACCOUNT_1.value) {
                return ACCOUNT_1;
            }
            if (i == ACCOUNT_2.value) {
                return ACCOUNT_2;
            }
            if (i == ACCOUNT_3.value) {
                return ACCOUNT_3;
            }
            if (i == ACCOUNT_4.value) {
                return ACCOUNT_4;
            }
            if (i == ACCOUNT_5.value) {
                return ACCOUNT_5;
            }
            if (i == ACCOUNT_6.value) {
                return ACCOUNT_6;
            }
            if (i == ACCOUNT_7.value) {
                return ACCOUNT_7;
            }
            return i == ACCOUNT_8.value ? ACCOUNT_8 : __UNKNOWN__;
        }
    }

    public enum CurrAccountCfg {
        DEFAULT(0),
        ACCOUNT_1(1),
        ACCOUNT_2(2),
        ACCOUNT_3(3),
        ACCOUNT_4(4),
        ACCOUNT_5(5),
        ACCOUNT_6(6),
        ACCOUNT_7(7),
        ACCOUNT_8(8),
        __UNKNOWN__(-1);

        public final int value;

        CurrAccountCfg(int i) {
            this.value = i;
        }

        public static CurrAccountCfg valueOf(int i) {
            if (i == DEFAULT.value) {
                return DEFAULT;
            }
            if (i == ACCOUNT_1.value) {
                return ACCOUNT_1;
            }
            if (i == ACCOUNT_2.value) {
                return ACCOUNT_2;
            }
            if (i == ACCOUNT_3.value) {
                return ACCOUNT_3;
            }
            if (i == ACCOUNT_4.value) {
                return ACCOUNT_4;
            }
            if (i == ACCOUNT_5.value) {
                return ACCOUNT_5;
            }
            if (i == ACCOUNT_6.value) {
                return ACCOUNT_6;
            }
            if (i == ACCOUNT_7.value) {
                return ACCOUNT_7;
            }
            return i == ACCOUNT_8.value ? ACCOUNT_8 : __UNKNOWN__;
        }
    }

    public enum CurrPepsPuid {
        DEFAULT(0),
        FOB_ID1(1),
        FOB_ID2(2),
        FOB_ID3(3),
        NFC_ID1(4),
        NFC_ID2(5),
        NFC_ID3(6),
        NFC_ID4(7),
        FINGER_PRINT1(8),
        FINGER_PRINT2(9),
        FINGER_PRINT3(10),
        FINGER_PRINT4(11),
        FINGER_PRINT5(12),
        __UNKNOWN__(-1);

        public final int value;

        CurrPepsPuid(int i) {
            this.value = i;
        }

        public static CurrPepsPuid valueOf(int i) {
            if (i == DEFAULT.value) {
                return DEFAULT;
            }
            if (i == FOB_ID1.value) {
                return FOB_ID1;
            }
            if (i == FOB_ID2.value) {
                return FOB_ID2;
            }
            if (i == FOB_ID3.value) {
                return FOB_ID3;
            }
            if (i == NFC_ID1.value) {
                return NFC_ID1;
            }
            if (i == NFC_ID2.value) {
                return NFC_ID2;
            }
            if (i == NFC_ID3.value) {
                return NFC_ID3;
            }
            if (i == NFC_ID4.value) {
                return NFC_ID4;
            }
            if (i == FINGER_PRINT1.value) {
                return FINGER_PRINT1;
            }
            if (i == FINGER_PRINT2.value) {
                return FINGER_PRINT2;
            }
            if (i == FINGER_PRINT3.value) {
                return FINGER_PRINT3;
            }
            if (i == FINGER_PRINT4.value) {
                return FINGER_PRINT4;
            }
            return i == FINGER_PRINT5.value ? FINGER_PRINT5 : __UNKNOWN__;
        }
    }

    public enum DriverSeatPostionCmd {
        SAVE(1),
        RECALL(2),
        __UNKNOWN__(-1);

        public final int value;

        DriverSeatPostionCmd(int i) {
            this.value = i;
        }

        public static DriverSeatPostionCmd valueOf(int i) {
            if (i == SAVE.value) {
                return SAVE;
            }
            return i == RECALL.value ? RECALL : __UNKNOWN__;
        }
    }

    public enum HeatLevel {
        LEVEL_1(1),
        LEVEL_2(2),
        LEVEL_3(3),
        ERROR(7),
        OFF(0),
        __UNKNOWN__(-1);

        public final int value;

        HeatLevel(int i) {
            this.value = i;
        }

        public static HeatLevel valueOf(int i) {
            if (i == LEVEL_1.value) {
                return LEVEL_1;
            }
            if (i == LEVEL_2.value) {
                return LEVEL_2;
            }
            if (i == LEVEL_3.value) {
                return LEVEL_3;
            }
            if (i == ERROR.value) {
                return ERROR;
            }
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }

    public enum HeatVentMode {
        VENT_MODE(1),
        HEAT_MODE(2),
        OFF(0),
        __UNKNOWN__(-1);

        public final int value;

        HeatVentMode(int i) {
            this.value = i;
        }

        public static HeatVentMode valueOf(int i) {
            if (i == VENT_MODE.value) {
                return VENT_MODE;
            }
            if (i == HEAT_MODE.value) {
                return HEAT_MODE;
            }
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }

    public enum MemSysOperationReqCmd {
        NO_COMMAND(0),
        ACTIVE_POP_UP_WINDOW(1),
        __UNKNOWN__(-1);

        public final int value;

        MemSysOperationReqCmd(int i) {
            this.value = i;
        }

        public static MemSysOperationReqCmd valueOf(int i) {
            if (i == NO_COMMAND.value) {
                return NO_COMMAND;
            }
            return i == ACTIVE_POP_UP_WINDOW.value ? ACTIVE_POP_UP_WINDOW : __UNKNOWN__;
        }
    }

    public enum SSWOperation {
        SLIDE_FORWARD(1),
        SLIDE_BACKWARD(2),
        CUSHION_RISE(3),
        CUSHION_DOWN(4),
        BACKREST_FORWARD(5),
        BACKREST_BACKWARD(6),
        TILT_RISE(7),
        TILT_DOWN(8),
        STEERING_WHEEL_UP(9),
        STEERING_WHEEL_DOWN(10),
        STEERING_WHEEL_OUT(11),
        STEERING_WHEEL_IN(12),
        CUSHION_FORWARD(13),
        CUSHION_BACKWARD(14),
        __UNKNOWN__(-1);

        public final int value;

        SSWOperation(int i) {
            this.value = i;
        }

        public static SSWOperation valueOf(int i) {
            if (i == SLIDE_FORWARD.value) {
                return SLIDE_FORWARD;
            }
            if (i == SLIDE_BACKWARD.value) {
                return SLIDE_BACKWARD;
            }
            if (i == CUSHION_RISE.value) {
                return CUSHION_RISE;
            }
            if (i == CUSHION_DOWN.value) {
                return CUSHION_DOWN;
            }
            if (i == BACKREST_FORWARD.value) {
                return BACKREST_FORWARD;
            }
            if (i == BACKREST_BACKWARD.value) {
                return BACKREST_BACKWARD;
            }
            if (i == TILT_RISE.value) {
                return TILT_RISE;
            }
            if (i == TILT_DOWN.value) {
                return TILT_DOWN;
            }
            if (i == STEERING_WHEEL_UP.value) {
                return STEERING_WHEEL_UP;
            }
            if (i == STEERING_WHEEL_DOWN.value) {
                return STEERING_WHEEL_DOWN;
            }
            if (i == STEERING_WHEEL_OUT.value) {
                return STEERING_WHEEL_OUT;
            }
            if (i == STEERING_WHEEL_IN.value) {
                return STEERING_WHEEL_IN;
            }
            if (i == CUSHION_FORWARD.value) {
                return CUSHION_FORWARD;
            }
            return i == CUSHION_BACKWARD.value ? CUSHION_BACKWARD : __UNKNOWN__;
        }
    }

    public enum SeatMassageLevel {
        OFF(0),
        LEVEL_ONE(1),
        LEVEL_TW0(2),
        LEVEL_THIRD(3),
        __UNKNOWN__(-1);

        public final int value;

        SeatMassageLevel(int i) {
            this.value = i;
        }

        public static SeatMassageLevel valueOf(int i) {
            if (i == OFF.value) {
                return OFF;
            }
            if (i == LEVEL_ONE.value) {
                return LEVEL_ONE;
            }
            if (i == LEVEL_TW0.value) {
                return LEVEL_TW0;
            }
            return i == LEVEL_THIRD.value ? LEVEL_THIRD : __UNKNOWN__;
        }
    }

    public enum SeatMassageMode {
        OFF(0),
        MODE_ONE(1),
        MODE_TW0(2),
        MODE_THIRD(3),
        MODE_FOUR(4),
        MODE_FIVE(5),
        MODE_SIX(6),
        __UNKNOWN__(-1);

        public final int value;

        SeatMassageMode(int i) {
            this.value = i;
        }

        public static SeatMassageMode valueOf(int i) {
            if (i == OFF.value) {
                return OFF;
            }
            if (i == MODE_ONE.value) {
                return MODE_ONE;
            }
            if (i == MODE_TW0.value) {
                return MODE_TW0;
            }
            if (i == MODE_THIRD.value) {
                return MODE_THIRD;
            }
            if (i == MODE_FOUR.value) {
                return MODE_FOUR;
            }
            if (i == MODE_FIVE.value) {
                return MODE_FIVE;
            }
            return i == MODE_SIX.value ? MODE_SIX : __UNKNOWN__;
        }
    }

    public enum SeatOperationState {
        NO_OPERATION(0),
        MANUAL_OPERATING(1),
        SAVE_COMPLETED(2),
        SAVE_FAILED(3),
        RECALLING(4),
        RECALL_INTERRUPTED(5),
        RECALL_COMPLETED(6),
        INITIALIZATION_COMPLETED(7),
        RECALL_STUDYING(8),
        STUDY_COMPLETED(9),
        STUDY_FAILED(10),
        __UNKNOWN__(-1);

        public final int value;

        SeatOperationState(int i) {
            this.value = i;
        }

        public static SeatOperationState valueOf(int i) {
            if (i == NO_OPERATION.value) {
                return NO_OPERATION;
            }
            if (i == MANUAL_OPERATING.value) {
                return MANUAL_OPERATING;
            }
            if (i == SAVE_COMPLETED.value) {
                return SAVE_COMPLETED;
            }
            if (i == SAVE_FAILED.value) {
                return SAVE_FAILED;
            }
            if (i == RECALLING.value) {
                return RECALLING;
            }
            if (i == RECALL_INTERRUPTED.value) {
                return RECALL_INTERRUPTED;
            }
            if (i == RECALL_COMPLETED.value) {
                return RECALL_COMPLETED;
            }
            if (i == INITIALIZATION_COMPLETED.value) {
                return INITIALIZATION_COMPLETED;
            }
            if (i == RECALL_STUDYING.value) {
                return RECALL_STUDYING;
            }
            if (i == STUDY_COMPLETED.value) {
                return STUDY_COMPLETED;
            }
            return i == STUDY_FAILED.value ? STUDY_FAILED : __UNKNOWN__;
        }
    }

    public enum SteeringWheelHeatState {
        OFF(0),
        HEATING(1),
        PRESERVATION(2),
        __UNKNOWN__(-1);

        public final int value;

        SteeringWheelHeatState(int i) {
            this.value = i;
        }

        public static SteeringWheelHeatState valueOf(int i) {
            if (i == OFF.value) {
                return OFF;
            }
            if (i == HEATING.value) {
                return HEATING;
            }
            return i == PRESERVATION.value ? PRESERVATION : __UNKNOWN__;
        }
    }

    public enum VentLevel {
        LEVEL_1(1),
        LEVEL_2(2),
        LEVEL_3(3),
        ERROR(7),
        OFF(0),
        __UNKNOWN__(-1);

        public final int value;

        VentLevel(int i) {
            this.value = i;
        }

        public static VentLevel valueOf(int i) {
            if (i == LEVEL_1.value) {
                return LEVEL_1;
            }
            if (i == LEVEL_2.value) {
                return LEVEL_2;
            }
            if (i == LEVEL_3.value) {
                return LEVEL_3;
            }
            if (i == ERROR.value) {
                return ERROR;
            }
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }
}
