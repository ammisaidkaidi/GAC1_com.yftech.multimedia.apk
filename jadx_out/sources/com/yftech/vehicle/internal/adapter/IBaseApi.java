package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface IBaseApi {
    public static final byte BUTTON_ACTION_PRESS = 1;
    public static final byte BUTTON_ACTION_RELEASE = 0;
    public static final byte STATE_DISABLED = 0;
    public static final byte STATE_ENABLED = 1;
    public static final byte SWITCH_TURN_OFF = 1;
    public static final byte SWITCH_TURN_ON = 2;

    public enum EnableState {
        DISABLED(0),
        ENABLED(1),
        __UNKNOWN__(-1);

        public final int value;

        EnableState(int i) {
            this.value = i;
        }

        public static EnableState valueOf(int i) {
            if (i == DISABLED.value) {
                return DISABLED;
            }
            return i == ENABLED.value ? ENABLED : __UNKNOWN__;
        }
    }

    public enum EnableStateOp {
        DISABLED(1),
        ENABLED(0),
        __UNKNOWN__(-1);

        public final int value;

        EnableStateOp(int i) {
            this.value = i;
        }

        public static EnableStateOp valueOf(int i) {
            if (i == DISABLED.value) {
                return DISABLED;
            }
            return i == ENABLED.value ? ENABLED : __UNKNOWN__;
        }
    }

    public enum SwitchState {
        TURN_ON(2),
        TURN_OFF(1),
        __UNKNOWN__(-1);

        public final int value;

        SwitchState(int i) {
            this.value = i;
        }

        public static SwitchState valueOf(int i) {
            if (i == TURN_ON.value) {
                return TURN_ON;
            }
            return i == TURN_OFF.value ? TURN_OFF : __UNKNOWN__;
        }
    }
}
