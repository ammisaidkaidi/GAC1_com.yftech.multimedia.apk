package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface IAirCondition extends IBaseApi {
    public static final int AC_BUTTON_SETTING = 6;
    public static final int AC_DISPLAY_POPUP_STATE = 401;
    public static final int AC_ENVIRONMENT_TEMPERATURE = 196;
    public static final int AC_OUT_AIRQUALITY_LEVEL = 195;
    public static final int AC_STATE = 78;
    public static final int AC_VR_ON_OFF_SETTING = 445;
    public static final int AC_WORKING_POWER = -1;
    public static final int AIR_CIRCULATION_MODE_OF_AUTO_MODE = 85;
    public static final int AIR_CIRCULATION_MODE_OF_AUTO_MODE_SETTING = 13;
    public static final int AIR_CIRCULATION_STATE = 82;
    public static final int AIR_SELF_DRYING = 263;
    public static final int AIR_SELF_DRYING_SETTING = 264;
    public static final int AIR_UNLOCK_VENTILATION = 265;
    public static final int AIR_UNLOCK_VENTILATION_SETTING = 266;
    public static final int AUTO_BUTTON_SETTING = 11;
    public static final int AUTO_DEFORST = -1;
    public static final int AUTO_DEFORST_BUTTON_SETTING = -1;
    public static final int AUTO_MODE = 83;
    public static final int BATTERY_COOL_STATE = 194;
    public static final int BATTERY_FH_STATE = 192;
    public static final int BATTERY_PTC_STATE = 193;
    public static final int BODYDISALARM_VENT_BUTTON_STATE = -1;
    public static final int BODYDISALARM_VENT_STATE = 191;
    public static final int CIRCULATION_BUTTON_SETTING = 10;
    public static final int COMFORT_CURVE_MODE = 86;
    public static final int COMFORT_CURVE_SETTING = 14;
    public static final int CUP_HOLDER_AUTO_MODE = 557;
    public static final int CUP_HOLDER_AUTO_MODE_SETTING = 558;
    public static final int CUP_HOLDER_HEAT_COOL_MODE = 555;
    public static final int CUP_HOLDER_HEAT_COOL_MODE_SETTING = 556;
    public static final int DRIVER_TEMPERATURE = 74;
    public static final int DRIVER_TEMPERATURE_SETTING = 0;
    public static final int DUAL_SYNC_BUTTON_SETTING = 8;
    public static final int DUAL_SYNC_STATE = 80;
    public static final int EcoMode_SETTING = -1;
    public static final int EcoMode_STATE = -1;
    public static final int FAN_SPEED_OF_AUTO_MODE = 84;
    public static final int FAN_SPEED_OF_AUTO_MODE_SETTING = 12;
    public static final int FRONT_DEFROST_BUTTON_SETTING = 5;
    public static final int FRONT_EFROST_STATE = 404;
    public static final int ION_BUTTON_SETTING = 9;
    public static final int ION_STATE = 81;
    public static final int OFF_BUTTON_SETTING = 3;
    public static final int ONE_BUTTON_VENTLAION_SETTING = 420;
    public static final int ONE_BUTTON_VENTLAION_STATE = 419;
    public static final int PASSENGER_TEMPERATURE = 75;
    public static final int PASSENGER_TEMPERATURE_SETTING = 1;
    public static final int POWER_BUTTON_STATE = -1;
    public static final int POWER_STATE = -1;
    public static final int REAR_AC_AUTO_MODE = 231;
    public static final int REAR_AC_AUTO_MODE_SETTING = 232;
    public static final int REAR_AC_OFF_BUTTON_SETTING = 235;
    public static final int REAR_AC_TEMPERATURE = 236;
    public static final int REAR_AC_TEMPERATURE_SETTING = 237;
    public static final int REAR_AC_WIND_MODE = 238;
    public static final int REAR_AC_WIND_MODE_SETTING = 239;
    public static final int REAR_AC_WIND_SPEED = 233;
    public static final int REAR_AC_WIND_SPEED_SETTING = 234;
    public static final int REAR_DEFROST_BUTTON_SETTING = 7;
    public static final int REAR_DEFROST_STATE = 79;
    public static final int TEMPERATURE_DEC_BUTTON_SETTING = 434;
    public static final int TEMPERATURE_DEC_VD_SETTING = 435;
    public static final int TEMPERATURE_FR_DEC_BUTTON_SETTING = 524;
    public static final int TEMPERATURE_FR_DEC_VD_SETTING = 525;
    public static final int TEMPERATURE_FR_INC_BUTTON_SETTING = 522;
    public static final int TEMPERATURE_FR_INC_VD_SETTING = 523;
    public static final int TEMPERATURE_INC_BUTTON_SETTING = 432;
    public static final int TEMPERATURE_INC_VD_SETTING = 433;
    public static final int WINDMODE_BUTTON_SETTING = 534;
    public static final int WINDSPEED_DEC_BUTTON_SETTING = 537;
    public static final int WINDSPEED_INC_BUTTON_SETTING = 536;
    public static final int WIND_MODE = 77;
    public static final int WIND_MODE_SETTING = 4;
    public static final int WIND_SPEED = 76;
    public static final int WIND_SPEED_SETTING = 2;

    public enum AcDisplayPopup {
        SYNC(13),
        ION(12),
        ACMAX(11),
        WIND_MODE_FRONT(10),
        RDEFROST(9),
        FDEFROST(8),
        CIRCULATION_MODE(7),
        ACST(6),
        AUTO_FRONT(5),
        WIND_FRONT(4),
        PASSENGER_TEMPERATURE(3),
        DRIVER_TEMPERATURE(2),
        ON_OFF(1),
        NOT_REQUEST(0),
        __UNKNOWN__(-1);

        public final int value;

        AcDisplayPopup(int i) {
            this.value = i;
        }

        public static AcDisplayPopup valueOf(int i) {
            if (i == SYNC.value) {
                return SYNC;
            }
            if (i == ION.value) {
                return ION;
            }
            if (i == ACMAX.value) {
                return ACMAX;
            }
            if (i == WIND_MODE_FRONT.value) {
                return WIND_MODE_FRONT;
            }
            if (i == RDEFROST.value) {
                return RDEFROST;
            }
            if (i == FDEFROST.value) {
                return FDEFROST;
            }
            if (i == CIRCULATION_MODE.value) {
                return CIRCULATION_MODE;
            }
            if (i == ACST.value) {
                return ACST;
            }
            if (i == AUTO_FRONT.value) {
                return AUTO_FRONT;
            }
            if (i == WIND_FRONT.value) {
                return WIND_FRONT;
            }
            if (i == PASSENGER_TEMPERATURE.value) {
                return PASSENGER_TEMPERATURE;
            }
            if (i == DRIVER_TEMPERATURE.value) {
                return DRIVER_TEMPERATURE;
            }
            if (i == ON_OFF.value) {
                return ON_OFF;
            }
            return i == NOT_REQUEST.value ? NOT_REQUEST : __UNKNOWN__;
        }
    }

    public enum AirCirculationMode {
        AUTO(0, 1),
        MANUAL(1, 2),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        AirCirculationMode(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static AirCirculationMode valueOf(int i) {
            if (i == AUTO.rx) {
                return AUTO;
            }
            return i == MANUAL.rx ? MANUAL : __UNKNOWN__;
        }
    }

    public enum AirCirculationState {
        INNER_AIR_CIRCULATION(1),
        OUTSIDE_AIR_CIRCULATION(2),
        AUTO_INNER_AIR_CIRCULATION(3),
        INVALID(0),
        AUTO_OUT_AIR_CIRCULATION(4),
        __UNKNOWN__(-1);

        public final int value;

        AirCirculationState(int i) {
            this.value = i;
        }

        public static AirCirculationState valueOf(int i) {
            if (i == INNER_AIR_CIRCULATION.value) {
                return INNER_AIR_CIRCULATION;
            }
            if (i == OUTSIDE_AIR_CIRCULATION.value) {
                return OUTSIDE_AIR_CIRCULATION;
            }
            if (i == AUTO_INNER_AIR_CIRCULATION.value) {
                return AUTO_INNER_AIR_CIRCULATION;
            }
            if (i == INVALID.value) {
                return INVALID;
            }
            return i == AUTO_OUT_AIR_CIRCULATION.value ? AUTO_OUT_AIR_CIRCULATION : __UNKNOWN__;
        }
    }

    public enum AutoMode {
        MANUAL_MODE(0),
        AUTO_MODE(1),
        __UNKNOWN__(-1);

        public final int value;

        AutoMode(int i) {
            this.value = i;
        }

        public static AutoMode valueOf(int i) {
            if (i == MANUAL_MODE.value) {
                return MANUAL_MODE;
            }
            return i == AUTO_MODE.value ? AUTO_MODE : __UNKNOWN__;
        }
    }

    public enum ComfortCurveMode {
        SOFT(0, 1),
        NORMAL(1, 2),
        FAST(2, 3),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        ComfortCurveMode(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static ComfortCurveMode valueOf(int i) {
            if (i == SOFT.rx) {
                return SOFT;
            }
            if (i == NORMAL.rx) {
                return NORMAL;
            }
            return i == FAST.rx ? FAST : __UNKNOWN__;
        }
    }

    public enum CupholderAutoMode {
        AUTO_MODE_OFF(0),
        AUTO_MODE_ON(1),
        __UNKNOWN__(-1);

        public final int value;

        CupholderAutoMode(int i) {
            this.value = i;
        }

        public static CupholderAutoMode valueOf(int i) {
            if (i == AUTO_MODE_OFF.value) {
                return AUTO_MODE_OFF;
            }
            return i == AUTO_MODE_ON.value ? AUTO_MODE_ON : __UNKNOWN__;
        }
    }

    public enum CupholderHeatCoolMode {
        OFF(0),
        HEAT_MODE(1),
        COOL_MODE(2),
        NO_COMMAND(3),
        __UNKNOWN__(-1);

        public final int value;

        CupholderHeatCoolMode(int i) {
            this.value = i;
        }

        public static CupholderHeatCoolMode valueOf(int i) {
            if (i == OFF.value) {
                return OFF;
            }
            if (i == HEAT_MODE.value) {
                return HEAT_MODE;
            }
            if (i == COOL_MODE.value) {
                return COOL_MODE;
            }
            return i == NO_COMMAND.value ? NO_COMMAND : __UNKNOWN__;
        }
    }

    public enum EcoMode {
        COMFORT(0, 1),
        ECONOMY(1, 2),
        VENT(2, 3),
        INVALID(3, -1),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        EcoMode(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static EcoMode valueOf(int i) {
            if (i == COMFORT.rx) {
                return COMFORT;
            }
            if (i == ECONOMY.rx) {
                return ECONOMY;
            }
            if (i == VENT.rx) {
                return VENT;
            }
            return i == INVALID.rx ? INVALID : __UNKNOWN__;
        }
    }

    public enum FanSpeed {
        LOW(0, 1),
        NORMAL(1, 2),
        HIGH(2, 3),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        FanSpeed(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static FanSpeed valueOf(int i) {
            if (i == LOW.rx) {
                return LOW;
            }
            if (i == NORMAL.rx) {
                return NORMAL;
            }
            return i == HIGH.rx ? HIGH : __UNKNOWN__;
        }
    }

    public enum OnOffByVrState {
        ON(2),
        OFF(1),
        __UNKNOWN__(-1);

        public final int value;

        OnOffByVrState(int i) {
            this.value = i;
        }

        public static OnOffByVrState valueOf(int i) {
            if (i == ON.value) {
                return ON;
            }
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }

    public enum WindMode {
        FACE(1),
        FACE_FOOT(2),
        FOOT(3),
        FOOT_DEFROST(4),
        DEFROST(5),
        INVALID(0),
        __UNKNOWN__(-1);

        public final int value;

        WindMode(int i) {
            this.value = i;
        }

        public static WindMode valueOf(int i) {
            if (i == FACE.value) {
                return FACE;
            }
            if (i == FACE_FOOT.value) {
                return FACE_FOOT;
            }
            if (i == FOOT.value) {
                return FOOT;
            }
            if (i == FOOT_DEFROST.value) {
                return FOOT_DEFROST;
            }
            if (i == DEFROST.value) {
                return DEFROST;
            }
            return i == INVALID.value ? INVALID : __UNKNOWN__;
        }
    }

    public enum WindSpeed {
        LEVEL_1(1),
        LEVEL_2(2),
        LEVEL_3(3),
        LEVEL_4(4),
        LEVEL_5(5),
        LEVEL_6(6),
        LEVEL_7(7),
        OFF(0),
        __UNKNOWN__(-1);

        public final int value;

        WindSpeed(int i) {
            this.value = i;
        }

        public static WindSpeed valueOf(int i) {
            if (i == LEVEL_1.value) {
                return LEVEL_1;
            }
            if (i == LEVEL_2.value) {
                return LEVEL_2;
            }
            if (i == LEVEL_3.value) {
                return LEVEL_3;
            }
            if (i == LEVEL_4.value) {
                return LEVEL_4;
            }
            if (i == LEVEL_5.value) {
                return LEVEL_5;
            }
            if (i == LEVEL_6.value) {
                return LEVEL_6;
            }
            if (i == LEVEL_7.value) {
                return LEVEL_7;
            }
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }
}
