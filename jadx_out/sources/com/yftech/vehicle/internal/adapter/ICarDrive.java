package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarDrive extends IBaseApi {
    public static final int ASSIST_PARA_SWITCH_REQ_STATE = 444;
    public static final int AUTO_HOLD_SETTING = 288;
    public static final int AUTO_HOLD_STATE = 287;
    public static final int AVDC_MODE = 222;
    public static final int AVDC_MODE_SETTING = 223;
    public static final int BCS_VDC_OFF_STATE = 405;
    public static final int DRIVE_MODE = 179;
    public static final int DRIVE_MODE_HEV = 406;
    public static final int DRIVE_MODE_SETTING = 71;
    public static final int DRIVE_SYSTEM_MODE = 261;
    public static final int EMS_ENGINE_WAVE_SETTING = 73;
    public static final int EMS_ENGINE_WAVE_STATE = 181;
    public static final int EPS_MODE_STATE = 291;
    public static final int EPS_STYLE = 180;
    public static final int EPS_STYLE_SETTING = 72;
    public static final int ESP_STATE = 215;
    public static final int ESP_STATE_SETTING = 216;
    public static final int ESP_STYLE = 290;
    public static final int GAS_PEDAL_LEVEL = 224;
    public static final int GAS_PEDAL_LEVEL_SETTING = 225;
    public static final int HDC_STATE = 217;
    public static final int HDC_STATE_SETTING = 218;
    public static final int MEMORY_DRIVE_MODE = 207;
    public static final int MEMORY_DRIVE_MODE_A9E = 422;
    public static final int MEMORY_DRIVE_MODE_SETTING = 208;
    public static final int MRR_ACC_MODE = 421;
    public static final int TCU_DRIVE_MODE = 260;

    public enum AutoHoldState {
        OFF(0),
        ON(1),
        STANDBY(2),
        FAILURE(3),
        __UNKNOWN__(-1);

        public final int value;

        AutoHoldState(int i) {
            this.value = i;
        }

        public static AutoHoldState valueOf(int i) {
            if (i == OFF.value) {
                return OFF;
            }
            if (i == ON.value) {
                return ON;
            }
            if (i == STANDBY.value) {
                return STANDBY;
            }
            return i == FAILURE.value ? FAILURE : __UNKNOWN__;
        }
    }

    public enum AvdcMode {
        INVALID(0),
        MODEST_MODEL(1),
        RADICAL_MODEL(2),
        ON_LOW_ADHESION_ROAD(3),
        ADAPTIVE_MODEL(4),
        OFF(5),
        __UNKNOWN__(-1);

        public final int value;

        AvdcMode(int i) {
            this.value = i;
        }

        public static AvdcMode valueOf(int i) {
            if (i == INVALID.value) {
                return INVALID;
            }
            if (i == MODEST_MODEL.value) {
                return MODEST_MODEL;
            }
            if (i == RADICAL_MODEL.value) {
                return RADICAL_MODEL;
            }
            if (i == ON_LOW_ADHESION_ROAD.value) {
                return ON_LOW_ADHESION_ROAD;
            }
            if (i == ADAPTIVE_MODEL.value) {
                return ADAPTIVE_MODEL;
            }
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }

    public enum DriveMode {
        NOT_VALID(0),
        ECO_PRO_MODE(1),
        COMFORT_MODE(2),
        SPORT_MODE(3),
        INDIVIDUAL_MODE(4),
        SPORT_PLUS_MODE(5),
        AI_MODE(6),
        OFFROAD_MODE(7),
        ATS_SAND(8),
        ATS_MUD(9),
        ATS_SNOW(10),
        __UNKNOWN__(-1);

        public final int value;

        DriveMode(int i) {
            this.value = i;
        }

        public static DriveMode valueOf(int i) {
            if (i == NOT_VALID.value) {
                return NOT_VALID;
            }
            if (i == ECO_PRO_MODE.value) {
                return ECO_PRO_MODE;
            }
            if (i == COMFORT_MODE.value) {
                return COMFORT_MODE;
            }
            if (i == SPORT_MODE.value) {
                return SPORT_MODE;
            }
            if (i == INDIVIDUAL_MODE.value) {
                return INDIVIDUAL_MODE;
            }
            if (i == SPORT_PLUS_MODE.value) {
                return SPORT_PLUS_MODE;
            }
            if (i == AI_MODE.value) {
                return AI_MODE;
            }
            if (i == OFFROAD_MODE.value) {
                return OFFROAD_MODE;
            }
            if (i == ATS_SAND.value) {
                return ATS_SAND;
            }
            if (i == ATS_MUD.value) {
                return ATS_MUD;
            }
            return i == ATS_SNOW.value ? ATS_SNOW : __UNKNOWN__;
        }
    }

    public enum DriveModeHev {
        NORMAL_MODE(0),
        ECO_MODE(1),
        SPORT_MODE(2),
        __UNKNOWN__(-1);

        public final int value;

        DriveModeHev(int i) {
            this.value = i;
        }

        public static DriveModeHev valueOf(int i) {
            if (i == NORMAL_MODE.value) {
                return NORMAL_MODE;
            }
            if (i == ECO_MODE.value) {
                return ECO_MODE;
            }
            return i == SPORT_MODE.value ? SPORT_MODE : __UNKNOWN__;
        }
    }

    public enum DriveSystemMode {
        NORMAL(0),
        ECO(1),
        SPORT(2),
        WINTER(3),
        MANUAL(4),
        SPORT_PLUS(5),
        __UNKNOWN__(-1);

        public final int value;

        DriveSystemMode(int i) {
            this.value = i;
        }

        public static DriveSystemMode valueOf(int i) {
            if (i == NORMAL.value) {
                return NORMAL;
            }
            if (i == ECO.value) {
                return ECO;
            }
            if (i == SPORT.value) {
                return SPORT;
            }
            if (i == WINTER.value) {
                return WINTER;
            }
            if (i == MANUAL.value) {
                return MANUAL;
            }
            return i == SPORT_PLUS.value ? SPORT_PLUS : __UNKNOWN__;
        }
    }

    public enum EPSStyle {
        COMFORT(1),
        LIGHT(2),
        SPORT(3),
        __UNKNOWN__(-1);

        public final int value;

        EPSStyle(int i) {
            this.value = i;
        }

        public static EPSStyle valueOf(int i) {
            if (i == COMFORT.value) {
                return COMFORT;
            }
            if (i == LIGHT.value) {
                return LIGHT;
            }
            return i == SPORT.value ? SPORT : __UNKNOWN__;
        }
    }

    public enum EpsModeState {
        NOT_READY(0),
        READY(1),
        IN_TRANSITION(2),
        FAULT(3),
        __UNKNOWN__(-1);

        public final int value;

        EpsModeState(int i) {
            this.value = i;
        }

        public static EpsModeState valueOf(int i) {
            if (i == NOT_READY.value) {
                return NOT_READY;
            }
            if (i == READY.value) {
                return READY;
            }
            if (i == IN_TRANSITION.value) {
                return IN_TRANSITION;
            }
            return i == FAULT.value ? FAULT : __UNKNOWN__;
        }
    }

    public enum EspState {
        ESP_ON(0, 1),
        TCS_OFF(1, 2),
        ESP_OFF(2, -1),
        ESP_FAILURE(3, -1),
        TCS_OFF_VDC_OFF(-2, 3),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        EspState(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static EspState valueOf(int i) {
            if (i == ESP_ON.rx) {
                return ESP_ON;
            }
            if (i == TCS_OFF.rx) {
                return TCS_OFF;
            }
            if (i == ESP_OFF.rx) {
                return ESP_OFF;
            }
            if (i == ESP_FAILURE.rx) {
                return ESP_FAILURE;
            }
            return i == TCS_OFF_VDC_OFF.rx ? TCS_OFF_VDC_OFF : __UNKNOWN__;
        }
    }

    public enum EspStyle {
        NORMAL(0),
        SNOW(1),
        MUD(2),
        SAND(3),
        SPORT(4),
        __UNKNOWN__(-1);

        public final int value;

        EspStyle(int i) {
            this.value = i;
        }

        public static EspStyle valueOf(int i) {
            if (i == NORMAL.value) {
                return NORMAL;
            }
            if (i == SNOW.value) {
                return SNOW;
            }
            if (i == MUD.value) {
                return MUD;
            }
            if (i == SAND.value) {
                return SAND;
            }
            return i == SPORT.value ? SPORT : __UNKNOWN__;
        }
    }

    public enum GasPedalLevel {
        INVALID(0),
        LEVEL_1(1),
        LEVEL_2(2),
        LEVEL_3(3),
        LEVEL_4(4),
        LEVEL_5(5),
        __UNKNOWN__(-1);

        public final int value;

        GasPedalLevel(int i) {
            this.value = i;
        }

        public static GasPedalLevel valueOf(int i) {
            if (i == INVALID.value) {
                return INVALID;
            }
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
            return i == LEVEL_5.value ? LEVEL_5 : __UNKNOWN__;
        }
    }

    public enum HdcState {
        HDC_OFF(0, 1),
        HDC_ON(1, 2),
        HDC_FAILURE(2, -1),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        HdcState(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static HdcState valueOf(int i) {
            if (i == HDC_OFF.rx) {
                return HDC_OFF;
            }
            if (i == HDC_ON.rx) {
                return HDC_ON;
            }
            return i == HDC_FAILURE.rx ? HDC_FAILURE : __UNKNOWN__;
        }
    }

    public enum MemoryDriveMode {
        NOT_MEMORY(1, 0),
        MEMORY(2, 1),
        NOT_USED(3, -1),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        MemoryDriveMode(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static MemoryDriveMode valueOf(int i) {
            if (i == NOT_MEMORY.rx) {
                return NOT_MEMORY;
            }
            if (i == MEMORY.rx) {
                return MEMORY;
            }
            return i == NOT_USED.rx ? NOT_USED : __UNKNOWN__;
        }
    }

    public enum MemoryDriveMode_A9E {
        MEMORY(2, 2),
        NOT_MEMORY(1, 1),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        MemoryDriveMode_A9E(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static MemoryDriveMode_A9E valueOf(int i) {
            if (i == MEMORY.rx) {
                return MEMORY;
            }
            return i == NOT_MEMORY.rx ? NOT_MEMORY : __UNKNOWN__;
        }
    }

    public enum MrrAccMode {
        OFF(0),
        PASSIVE(1),
        STANDBY(2),
        ACTIVE_CONTROL(3),
        BRAKE_ONLY(4),
        OVER_RIDE(5),
        STAND_WAIT(6),
        FAILURE(7),
        __UNKNOWN__(-1);

        public final int value;

        MrrAccMode(int i) {
            this.value = i;
        }

        public static MrrAccMode valueOf(int i) {
            if (i == OFF.value) {
                return OFF;
            }
            if (i == PASSIVE.value) {
                return PASSIVE;
            }
            if (i == STANDBY.value) {
                return STANDBY;
            }
            if (i == ACTIVE_CONTROL.value) {
                return ACTIVE_CONTROL;
            }
            if (i == BRAKE_ONLY.value) {
                return BRAKE_ONLY;
            }
            if (i == OVER_RIDE.value) {
                return OVER_RIDE;
            }
            if (i == STAND_WAIT.value) {
                return STAND_WAIT;
            }
            return i == FAILURE.value ? FAILURE : __UNKNOWN__;
        }
    }

    public enum TcuDriveMode {
        NORMAL(0),
        SPORT(1),
        WINTER(2),
        UPSLOPE_ONE(3),
        HOT_ONE(4),
        UPSLOPE_TWO(5),
        HOT_TWO(6),
        WUSP(7),
        CRUISE(8),
        TIPTRONIC(9),
        ECO(10),
        HIGH_ALTITUDE(11),
        SPORT_PLUS(12),
        OFF_ROAD(13),
        __UNKNOWN__(-1);

        public final int value;

        TcuDriveMode(int i) {
            this.value = i;
        }

        public static TcuDriveMode valueOf(int i) {
            if (i == NORMAL.value) {
                return NORMAL;
            }
            if (i == SPORT.value) {
                return SPORT;
            }
            if (i == WINTER.value) {
                return WINTER;
            }
            if (i == UPSLOPE_ONE.value) {
                return UPSLOPE_ONE;
            }
            if (i == HOT_ONE.value) {
                return HOT_ONE;
            }
            if (i == UPSLOPE_TWO.value) {
                return UPSLOPE_TWO;
            }
            if (i == HOT_TWO.value) {
                return HOT_TWO;
            }
            if (i == WUSP.value) {
                return WUSP;
            }
            if (i == CRUISE.value) {
                return CRUISE;
            }
            if (i == TIPTRONIC.value) {
                return TIPTRONIC;
            }
            if (i == ECO.value) {
                return ECO;
            }
            if (i == HIGH_ALTITUDE.value) {
                return HIGH_ALTITUDE;
            }
            if (i == SPORT_PLUS.value) {
                return SPORT_PLUS;
            }
            return i == OFF_ROAD.value ? OFF_ROAD : __UNKNOWN__;
        }
    }

    public enum VdcOffState {
        OFF(1),
        ON(0),
        __UNKNOWN__(-1);

        public final int value;

        VdcOffState(int i) {
            this.value = i;
        }

        public static VdcOffState valueOf(int i) {
            if (i == OFF.value) {
                return OFF;
            }
            return i == ON.value ? ON : __UNKNOWN__;
        }
    }
}
