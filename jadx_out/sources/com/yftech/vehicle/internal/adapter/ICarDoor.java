package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarDoor extends IBaseApi {
    public static final int AUTO_UNLOCK_SETTING = 53;
    public static final int AUTO_UNLOCK_STATE = 148;
    public static final int DRIVER_DOOR_LOCK_STATE = 402;
    public static final int DRIVER_DOOR_STATE = 188;
    public static final int ENGINE_HOOD_STATE = 283;
    public static final int HOOD_DOOR_STATE = 210;
    public static final int LOCK_UNLOCK_SOUND_SETTING = 55;
    public static final int LOCK_UNLOCK_SOUND_STATE = 150;
    public static final int PASSENGER_DOOR_LOCK_STATE = 403;
    public static final int PASSENGER_DOOR_STATE = 211;
    public static final int PEPS_AUTO_TRUNK_OPEN_SETTING = 58;
    public static final int PEPS_AUTO_TRUNK_OPEN_STATE = 153;
    public static final int PEPS_UNLOCK_SETTING = 56;
    public static final int PEPS_UNLOCK_STATE = 151;
    public static final int PEPS_WALK_AWAY_LOCK_SETTING = 57;
    public static final int PEPS_WALK_AWAY_LOCK_STATE = 152;
    public static final int PLGM_CONTROL = -1;
    public static final int PLGM_FUNCTION_SETTING = 61;
    public static final int PLGM_MAX_LEVEL_SETTING = 60;
    public static final int PSD_DOOR_SPEED_L = 158;
    public static final int PSD_DOOR_SPEED_L_SETTING = 229;
    public static final int PSD_DOOR_SPEED_R = 159;
    public static final int PSD_DOOR_SPEED_R_SETTING = 230;
    public static final int REAR_LEFT_DOOR_STATE = 212;
    public static final int REAR_RIGHT_DOOR_STATE = 213;
    public static final int REMOTE_UNLOCK_SETTING = 52;
    public static final int REMOTE_UNLOCK_STATE = 147;
    public static final int SLIDING_DOOR_CMD_L_SETTING = 241;
    public static final int SLIDING_DOOR_CMD_R_SETTING = 240;
    public static final int SLIDING_DOOR_MODE = 227;
    public static final int SLIDING_DOOR_MODE_SETTING = 228;
    public static final int SLIDING_DOOR_OPERATION_L = 243;
    public static final int SLIDING_DOOR_OPERATION_R = 245;
    public static final int SLIDING_DOOR_STATE_L = 242;
    public static final int SLIDING_DOOR_STATE_R = 244;
    public static final int SPEED_AUTO_LOCK_SETTING = 54;
    public static final int SPEED_AUTO_LOCK_STATE = 149;
    public static final int TAIL_DOOR_CMD_SETTING = 59;
    public static final int TAIL_DOOR_MAX_POSITION_MEMORY_SETTING = 289;
    public static final int TAIL_DOOR_MODE = 157;
    public static final int TAIL_DOOR_PERCENT = 154;
    public static final int TAIL_DOOR_POSITION_MEMORY_CLEAR_SETTING = 286;
    public static final int TAIL_DOOR_STATE = 156;
    public static final int TAIL_DOOR_TRAVEL_PERCENT = 155;
    public static final int TRUNK_DOOR_STATE = 214;

    public enum AutoUnlockState {
        NOT_ACTIVE(0, 1),
        ACTIVE(1, 2),
        NOT_USED(2, 3),
        NO_COMMAND(3, 0),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        AutoUnlockState(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static AutoUnlockState valueOf(int i) {
            if (i == NOT_ACTIVE.rx) {
                return NOT_ACTIVE;
            }
            if (i == ACTIVE.rx) {
                return ACTIVE;
            }
            if (i == NOT_USED.rx) {
                return NOT_USED;
            }
            return i == NO_COMMAND.rx ? NO_COMMAND : __UNKNOWN__;
        }
    }

    public enum DoorLockState {
        UNLOCK(0),
        LOCK(1),
        __UNKNOWN__(-1);

        public final int value;

        DoorLockState(int i) {
            this.value = i;
        }

        public static DoorLockState valueOf(int i) {
            if (i == UNLOCK.value) {
                return UNLOCK;
            }
            return i == LOCK.value ? LOCK : __UNKNOWN__;
        }
    }

    public enum DoorState {
        CLOSED(1),
        OPEN(2),
        __UNKNOWN__(-1);

        public final int value;

        DoorState(int i) {
            this.value = i;
        }

        public static DoorState valueOf(int i) {
            if (i == CLOSED.value) {
                return CLOSED;
            }
            return i == OPEN.value ? OPEN : __UNKNOWN__;
        }
    }

    public enum EngineHoodState {
        CLOSED(2),
        OPEN(1),
        __UNKNOWN__(-1);

        public final int value;

        EngineHoodState(int i) {
            this.value = i;
        }

        public static EngineHoodState valueOf(int i) {
            if (i == CLOSED.value) {
                return CLOSED;
            }
            return i == OPEN.value ? OPEN : __UNKNOWN__;
        }
    }

    public enum PEPSAutoTrunkOpenState {
        NOT_ACTIVE(0, 1),
        SATO_ACTIVE(1, 2),
        SATO_AND_SOUNDREMINDER_ACTIVE(2, 3),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        PEPSAutoTrunkOpenState(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static PEPSAutoTrunkOpenState valueOf(int i) {
            if (i == NOT_ACTIVE.rx) {
                return NOT_ACTIVE;
            }
            if (i == SATO_ACTIVE.rx) {
                return SATO_ACTIVE;
            }
            return i == SATO_AND_SOUNDREMINDER_ACTIVE.rx ? SATO_AND_SOUNDREMINDER_ACTIVE : __UNKNOWN__;
        }
    }

    public enum PLGMControl {
        CONTROL_1(0),
        __UNKNOWN__(-1);

        public final int value;

        PLGMControl(int i) {
            this.value = i;
        }

        public static PLGMControl valueOf(int i) {
            return i == CONTROL_1.value ? CONTROL_1 : __UNKNOWN__;
        }
    }

    public enum PLGMFunction {
        FUNCTION_1(0),
        ENABLE(2),
        DISABLE(1),
        __UNKNOWN__(-1);

        public final int value;

        PLGMFunction(int i) {
            this.value = i;
        }

        public static PLGMFunction valueOf(int i) {
            if (i == FUNCTION_1.value) {
                return FUNCTION_1;
            }
            if (i == ENABLE.value) {
                return ENABLE;
            }
            return i == DISABLE.value ? DISABLE : __UNKNOWN__;
        }
    }

    public enum PLGMMaxLevel {
        LEVEL_1(0),
        __UNKNOWN__(-1);

        public final int value;

        PLGMMaxLevel(int i) {
            this.value = i;
        }

        public static PLGMMaxLevel valueOf(int i) {
            return i == LEVEL_1.value ? LEVEL_1 : __UNKNOWN__;
        }
    }

    public enum PSDDoorSpeed {
        LOW_SPEED(1),
        HIGH_SPEED(2),
        __UNKNOWN__(-1);

        public final int value;

        PSDDoorSpeed(int i) {
            this.value = i;
        }

        public static PSDDoorSpeed valueOf(int i) {
            if (i == LOW_SPEED.value) {
                return LOW_SPEED;
            }
            return i == HIGH_SPEED.value ? HIGH_SPEED : __UNKNOWN__;
        }
    }

    public enum RemoteUnlockMode {
        ALL_DOORS(0, 1),
        DRIVER_DOOR(1, 2),
        __UNKNOWN__(-1, -1);

        private final int rx;
        private final int tx;
        public final int value;

        RemoteUnlockMode(int i, int i2) {
            this.rx = i;
            this.tx = i2;
            this.value = this.tx;
        }

        public static RemoteUnlockMode valueOf(int i) {
            if (i == ALL_DOORS.rx) {
                return ALL_DOORS;
            }
            return i == DRIVER_DOOR.rx ? DRIVER_DOOR : __UNKNOWN__;
        }
    }

    public enum SlidingDoorCmd {
        STOP(1),
        OPEN(2),
        CLOSE(3),
        __UNKNOWN__(-1);

        public final int value;

        SlidingDoorCmd(int i) {
            this.value = i;
        }

        public static SlidingDoorCmd valueOf(int i) {
            if (i == STOP.value) {
                return STOP;
            }
            if (i == OPEN.value) {
                return OPEN;
            }
            return i == CLOSE.value ? CLOSE : __UNKNOWN__;
        }
    }

    public enum SlidingDoorOperation {
        INITIAL_MODE(0),
        STOP(1),
        OPENNING(2),
        CLOSING(3),
        __UNKNOWN__(-1);

        public final int value;

        SlidingDoorOperation(int i) {
            this.value = i;
        }

        public static SlidingDoorOperation valueOf(int i) {
            if (i == INITIAL_MODE.value) {
                return INITIAL_MODE;
            }
            if (i == STOP.value) {
                return STOP;
            }
            if (i == OPENNING.value) {
                return OPENNING;
            }
            return i == CLOSING.value ? CLOSING : __UNKNOWN__;
        }
    }

    public enum SlidingDoorState {
        FULL_LATCH(0),
        HALF_LATCH(1),
        LATCH_OPEN(2),
        FULL_OPEN(3),
        __UNKNOWN__(-1);

        public final int value;

        SlidingDoorState(int i) {
            this.value = i;
        }

        public static SlidingDoorState valueOf(int i) {
            if (i == FULL_LATCH.value) {
                return FULL_LATCH;
            }
            if (i == HALF_LATCH.value) {
                return HALF_LATCH;
            }
            if (i == LATCH_OPEN.value) {
                return LATCH_OPEN;
            }
            return i == FULL_OPEN.value ? FULL_OPEN : __UNKNOWN__;
        }
    }

    public enum TailDoorCmd {
        STOP(1),
        FULLY_OPEN(2),
        FULLY_CLOSE(3),
        OPEN_TWENTY_PERCENT(4),
        OPEN_FORTY_PERCENT(5),
        OPEN_SIXTY_PERCENT(6),
        OPEN_EIGHTY_PERCENT(7),
        __UNKNOWN__(-1);

        public final int value;

        TailDoorCmd(int i) {
            this.value = i;
        }

        public static TailDoorCmd valueOf(int i) {
            if (i == STOP.value) {
                return STOP;
            }
            if (i == FULLY_OPEN.value) {
                return FULLY_OPEN;
            }
            if (i == FULLY_CLOSE.value) {
                return FULLY_CLOSE;
            }
            if (i == OPEN_TWENTY_PERCENT.value) {
                return OPEN_TWENTY_PERCENT;
            }
            if (i == OPEN_FORTY_PERCENT.value) {
                return OPEN_FORTY_PERCENT;
            }
            if (i == OPEN_SIXTY_PERCENT.value) {
                return OPEN_SIXTY_PERCENT;
            }
            return i == OPEN_EIGHTY_PERCENT.value ? OPEN_EIGHTY_PERCENT : __UNKNOWN__;
        }
    }

    public enum TailDoorState {
        FULLY_OPENED(1),
        FULLY_CLOSED(2),
        OPENING(3),
        CLOSING(4),
        HOLD(5),
        TG_BRAKE(6),
        __UNKNOWN__(-1);

        public final int value;

        TailDoorState(int i) {
            this.value = i;
        }

        public static TailDoorState valueOf(int i) {
            if (i == FULLY_OPENED.value) {
                return FULLY_OPENED;
            }
            if (i == FULLY_CLOSED.value) {
                return FULLY_CLOSED;
            }
            if (i == OPENING.value) {
                return OPENING;
            }
            if (i == CLOSING.value) {
                return CLOSING;
            }
            if (i == HOLD.value) {
                return HOLD;
            }
            return i == TG_BRAKE.value ? TG_BRAKE : __UNKNOWN__;
        }
    }
}
