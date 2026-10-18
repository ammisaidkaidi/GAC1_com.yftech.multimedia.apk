package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarInfo extends IBaseApi {
    public static final int BATTERY_VOLTAGE = 124;
    public static final int DAY_NIGHT_MODE = 187;
    public static final int ENGINE_CRANK_STATE = 407;
    public static final int ENGINE_IGN_ANGLE_STATE = 443;
    public static final int ENGINE_SPEED = 121;
    public static final int GEAR_POSITION = 185;
    public static final int GEAR_POSITION_A9E = 415;
    public static final int LOW_BATTERY_WARNING_LEVEL = 125;
    public static final int PARKING_BRAKE_STATE = 201;
    public static final int POWER_MODE = 182;
    public static final int PRESSURE_ATMOSPHERIC_STATE = 437;
    public static final int PRESSURE_OIL_STATE = 438;
    public static final int PRESSURE_TURBINE_STATE = 436;
    public static final int REAR_GEAR_STATE = 184;
    public static final int SLEEP_WAKEUP_COMMAND = -1;
    public static final int TEMP_ENGINE_WATER_STATE = 439;
    public static final int TEMP_EXHAUST_STATE = 440;
    public static final int TEMP_INTAKE_AIR_STATE = 441;
    public static final int TEMP_TRANSMISSION_OIL_STATE = 442;
    public static final int TOTAL_ODOMETER = 123;
    public static final int TRANSPORTATION_MODE = 369;
    public static final int VEHICLE_MODE_BROADCAST = 368;
    public static final int VEHICLE_SPEED = 122;
    public static final int VEHICLE_STATE = 186;

    public enum DayNightMode {
        DAY(0),
        TWILIGHT(1),
        NIGHT(2),
        __UNKNOWN__(-1);

        public final int value;

        DayNightMode(int i) {
            this.value = i;
        }

        public static DayNightMode valueOf(int i) {
            if (i == DAY.value) {
                return DAY;
            }
            if (i == TWILIGHT.value) {
                return TWILIGHT;
            }
            return i == NIGHT.value ? NIGHT : __UNKNOWN__;
        }
    }

    public enum EngineCrankState {
        NOT_ON_CRANK(0),
        CRANK(1),
        __UNKNOWN__(-1);

        public final int value;

        EngineCrankState(int i) {
            this.value = i;
        }

        public static EngineCrankState valueOf(int i) {
            if (i == NOT_ON_CRANK.value) {
                return NOT_ON_CRANK;
            }
            return i == CRANK.value ? CRANK : __UNKNOWN__;
        }
    }

    public enum GearPosition {
        GEAR_FIRST(1),
        GEAR_TOW(2),
        GEAR_THIRD(3),
        GEAR_FOUR(4),
        GEAR_FIVE(5),
        GEAR_SIX(6),
        GEAR_SEVEN(7),
        GEAR_EIGHT(8),
        GEAR_NINE(9),
        SPORT(11),
        PARKING(12),
        REVERSE(13),
        NEUTRAL(14),
        DRIVE(15),
        MANUAL_MODE(16),
        L(17),
        DS(18),
        CONFIRMED(19),
        BRAKE(20),
        NOT_AVAILABLE(21),
        __UNKNOWN__(-1);

        public final int value;

        GearPosition(int i) {
            this.value = i;
        }

        public static GearPosition valueOf(int i) {
            if (i == GEAR_FIRST.value) {
                return GEAR_FIRST;
            }
            if (i == GEAR_TOW.value) {
                return GEAR_TOW;
            }
            if (i == GEAR_THIRD.value) {
                return GEAR_THIRD;
            }
            if (i == GEAR_FOUR.value) {
                return GEAR_FOUR;
            }
            if (i == GEAR_FIVE.value) {
                return GEAR_FIVE;
            }
            if (i == GEAR_SIX.value) {
                return GEAR_SIX;
            }
            if (i == GEAR_SEVEN.value) {
                return GEAR_SEVEN;
            }
            if (i == GEAR_EIGHT.value) {
                return GEAR_EIGHT;
            }
            if (i == GEAR_NINE.value) {
                return GEAR_NINE;
            }
            if (i == SPORT.value) {
                return SPORT;
            }
            if (i == PARKING.value) {
                return PARKING;
            }
            if (i == REVERSE.value) {
                return REVERSE;
            }
            if (i == NEUTRAL.value) {
                return NEUTRAL;
            }
            if (i == DRIVE.value) {
                return DRIVE;
            }
            if (i == MANUAL_MODE.value) {
                return MANUAL_MODE;
            }
            if (i == L.value) {
                return L;
            }
            if (i == DS.value) {
                return DS;
            }
            if (i == CONFIRMED.value) {
                return CONFIRMED;
            }
            if (i == BRAKE.value) {
                return BRAKE;
            }
            return i == NOT_AVAILABLE.value ? NOT_AVAILABLE : __UNKNOWN__;
        }
    }

    public enum GearPositionA9E {
        INVALID(0),
        DRIVE(1),
        NEUTRAL(2),
        REVERSE(3),
        PARKING(4),
        __UNKNOWN__(-1);

        public final int value;

        GearPositionA9E(int i) {
            this.value = i;
        }

        public static GearPositionA9E valueOf(int i) {
            if (i == INVALID.value) {
                return INVALID;
            }
            if (i == DRIVE.value) {
                return DRIVE;
            }
            if (i == NEUTRAL.value) {
                return NEUTRAL;
            }
            if (i == REVERSE.value) {
                return REVERSE;
            }
            return i == PARKING.value ? PARKING : __UNKNOWN__;
        }
    }

    public enum LowBatteryWarningLevel {
        LEVEL_1(1),
        LEVEL_2(2),
        LEVEL_3(3),
        LEVEL_4(4),
        LEVEL_5(5),
        OFF(0),
        __UNKNOWN__(-1);

        public final int value;

        LowBatteryWarningLevel(int i) {
            this.value = i;
        }

        public static LowBatteryWarningLevel valueOf(int i) {
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
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }

    public enum ParkingBrakeState {
        PARKING_BRAKE_NOT_APPLIED(1),
        PARKING_BRAKE_APPLIED(2),
        UNAVAILABLE(3),
        __UNKNOWN__(-1);

        public final int value;

        ParkingBrakeState(int i) {
            this.value = i;
        }

        public static ParkingBrakeState valueOf(int i) {
            if (i == PARKING_BRAKE_NOT_APPLIED.value) {
                return PARKING_BRAKE_NOT_APPLIED;
            }
            if (i == PARKING_BRAKE_APPLIED.value) {
                return PARKING_BRAKE_APPLIED;
            }
            return i == UNAVAILABLE.value ? UNAVAILABLE : __UNKNOWN__;
        }
    }

    public enum PowerMode {
        ACC(1),
        ON(2),
        CRANK(3),
        OFF(0),
        __UNKNOWN__(-1);

        public final int value;

        PowerMode(int i) {
            this.value = i;
        }

        public static PowerMode valueOf(int i) {
            if (i == ACC.value) {
                return ACC;
            }
            if (i == ON.value) {
                return ON;
            }
            if (i == CRANK.value) {
                return CRANK;
            }
            return i == OFF.value ? OFF : __UNKNOWN__;
        }
    }

    public enum RearGearState {
        NOT_ENGAGED(1),
        ENGAGED(2),
        __UNKNOWN__(-1);

        public final int value;

        RearGearState(int i) {
            this.value = i;
        }

        public static RearGearState valueOf(int i) {
            if (i == NOT_ENGAGED.value) {
                return NOT_ENGAGED;
            }
            return i == ENGAGED.value ? ENGAGED : __UNKNOWN__;
        }
    }

    public enum SleepWakeUpCommand {
        GO_TO_SLEEP(0),
        WAKEUP(3),
        __UNKNOWN__(-1);

        public final int value;

        SleepWakeUpCommand(int i) {
            this.value = i;
        }

        public static SleepWakeUpCommand valueOf(int i) {
            if (i == GO_TO_SLEEP.value) {
                return GO_TO_SLEEP;
            }
            return i == WAKEUP.value ? WAKEUP : __UNKNOWN__;
        }
    }

    public enum VehicleModeBroadcast {
        NORMAL(0),
        OTA(1),
        __UNKNOWN__(-1);

        public final int value;

        VehicleModeBroadcast(int i) {
            this.value = i;
        }

        public static VehicleModeBroadcast valueOf(int i) {
            if (i == NORMAL.value) {
                return NORMAL;
            }
            return i == OTA.value ? OTA : __UNKNOWN__;
        }
    }

    public enum VehicleState {
        SLEEPING(0),
        CUTOFF_PENDING(2),
        AUTO_ACC(3),
        IGNITION_LEVEL(5),
        STARTING_IN_PROGRESS(6),
        POWER_TRAIN_RUNNING(7),
        AUTO_START(8),
        ENGINE_SYSTEM_STOP(9),
        UNAVAILABLE(15),
        __UNKNOWN__(-1);

        public final int value;

        VehicleState(int i) {
            this.value = i;
        }

        public static VehicleState valueOf(int i) {
            if (i == SLEEPING.value) {
                return SLEEPING;
            }
            if (i == CUTOFF_PENDING.value) {
                return CUTOFF_PENDING;
            }
            if (i == AUTO_ACC.value) {
                return AUTO_ACC;
            }
            if (i == IGNITION_LEVEL.value) {
                return IGNITION_LEVEL;
            }
            if (i == STARTING_IN_PROGRESS.value) {
                return STARTING_IN_PROGRESS;
            }
            if (i == POWER_TRAIN_RUNNING.value) {
                return POWER_TRAIN_RUNNING;
            }
            if (i == AUTO_START.value) {
                return AUTO_START;
            }
            if (i == ENGINE_SYSTEM_STOP.value) {
                return ENGINE_SYSTEM_STOP;
            }
            return i == UNAVAILABLE.value ? UNAVAILABLE : __UNKNOWN__;
        }
    }
}
