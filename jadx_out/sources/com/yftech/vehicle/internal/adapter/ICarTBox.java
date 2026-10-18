package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarTBox extends IBaseApi {
    public static final int PHONE_CALL_STATE = 329;
    public static final int PHONE_KEY_ACCEPT_SETTING = 326;
    public static final int PHONE_KEY_CANCEL_SETTING = 327;
    public static final int PHONE_MODE = 328;
    public static final int PHONE_TYPE_SETTING = 514;
    public static final int TCU_MUTE_RADIO_ORDER = 190;

    public enum MuteRadioOrder {
        NO_MUTE_RADIO_ORDER(0),
        MUTE_RADIO_ORDER(1),
        __UNKNOWN__(-1);

        public final int value;

        MuteRadioOrder(int i) {
            this.value = i;
        }

        public static MuteRadioOrder valueOf(int i) {
            if (i == NO_MUTE_RADIO_ORDER.value) {
                return NO_MUTE_RADIO_ORDER;
            }
            return i == MUTE_RADIO_ORDER.value ? MUTE_RADIO_ORDER : __UNKNOWN__;
        }
    }

    public enum PhoneCallState {
        NOT_ACTIVE(0),
        INCOMING_CALL(1),
        CONNECTING(2),
        ANSWER(3),
        ON_THE_PHONE(4),
        HANG_UP(5),
        CALL_OVER(6),
        __UNKNOWN__(-1);

        public final int value;

        PhoneCallState(int i) {
            this.value = i;
        }

        public static PhoneCallState valueOf(int i) {
            if (i == NOT_ACTIVE.value) {
                return NOT_ACTIVE;
            }
            if (i == INCOMING_CALL.value) {
                return INCOMING_CALL;
            }
            if (i == CONNECTING.value) {
                return CONNECTING;
            }
            if (i == ANSWER.value) {
                return ANSWER;
            }
            if (i == ON_THE_PHONE.value) {
                return ON_THE_PHONE;
            }
            if (i == HANG_UP.value) {
                return HANG_UP;
            }
            return i == CALL_OVER.value ? CALL_OVER : __UNKNOWN__;
        }
    }

    public enum PhoneKeyAction {
        NOT_ACTIVE(0),
        SHORT_RELEASE(1),
        LONG_RELEASE(2),
        FAILURE(3),
        __UNKNOWN__(-1);

        public final int value;

        PhoneKeyAction(int i) {
            this.value = i;
        }

        public static PhoneKeyAction valueOf(int i) {
            if (i == NOT_ACTIVE.value) {
                return NOT_ACTIVE;
            }
            if (i == SHORT_RELEASE.value) {
                return SHORT_RELEASE;
            }
            if (i == LONG_RELEASE.value) {
                return LONG_RELEASE;
            }
            return i == FAILURE.value ? FAILURE : __UNKNOWN__;
        }
    }

    public enum PhoneMode {
        NOT_ACTIVE(0),
        TBOX_MODE(1),
        BLUETOOTH_MODE(2),
        BCALL_MODE(3),
        ECALL_MODE(4),
        CALLBACK_MODE1(5),
        CALLBACK_MODE2(6),
        __UNKNOWN__(-1);

        public final int value;

        PhoneMode(int i) {
            this.value = i;
        }

        public static PhoneMode valueOf(int i) {
            if (i == NOT_ACTIVE.value) {
                return NOT_ACTIVE;
            }
            if (i == TBOX_MODE.value) {
                return TBOX_MODE;
            }
            if (i == BLUETOOTH_MODE.value) {
                return BLUETOOTH_MODE;
            }
            if (i == BCALL_MODE.value) {
                return BCALL_MODE;
            }
            if (i == ECALL_MODE.value) {
                return ECALL_MODE;
            }
            if (i == CALLBACK_MODE1.value) {
                return CALLBACK_MODE1;
            }
            return i == CALLBACK_MODE2.value ? CALLBACK_MODE2 : __UNKNOWN__;
        }
    }

    public enum PhoneType {
        NOT_ACTIVE(0),
        BT_PHONE(1),
        E_CALL(2),
        B_CALL(3),
        __UNKNOWN__(-1);

        public final int value;

        PhoneType(int i) {
            this.value = i;
        }

        public static PhoneType valueOf(int i) {
            if (i == NOT_ACTIVE.value) {
                return NOT_ACTIVE;
            }
            if (i == BT_PHONE.value) {
                return BT_PHONE;
            }
            if (i == E_CALL.value) {
                return E_CALL;
            }
            return i == B_CALL.value ? B_CALL : __UNKNOWN__;
        }
    }
}
