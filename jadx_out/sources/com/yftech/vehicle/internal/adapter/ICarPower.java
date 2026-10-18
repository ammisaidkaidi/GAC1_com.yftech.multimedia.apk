package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarPower extends IBaseCmd {

    public enum PowerMode {
        OFF(0),
        ACC(1),
        ON(2),
        CRANK(3),
        __UNKNOWN__(-1);

        public final int value;

        PowerMode(int i) {
            this.value = i;
        }

        public static PowerMode valueOf(int i) {
            if (i == OFF.value) {
                return OFF;
            }
            if (i == ACC.value) {
                return ACC;
            }
            if (i == ON.value) {
                return ON;
            }
            return i == CRANK.value ? CRANK : __UNKNOWN__;
        }
    }

    public enum PowerState {
        STANDBY(1),
        SYSTEMON(2),
        SLEEP(3),
        VOLFAULTSTBY(4),
        TEST(5),
        TEN_MIN(6),
        PRE_SLEEP(7),
        __UNKNOWN__(-1);

        public final int value;

        PowerState(int i) {
            this.value = i;
        }

        public static PowerState valueOf(int i) {
            if (i == STANDBY.value) {
                return STANDBY;
            }
            if (i == SYSTEMON.value) {
                return SYSTEMON;
            }
            if (i == SLEEP.value) {
                return SLEEP;
            }
            if (i == VOLFAULTSTBY.value) {
                return VOLFAULTSTBY;
            }
            if (i == TEST.value) {
                return TEST;
            }
            if (i == TEN_MIN.value) {
                return TEN_MIN;
            }
            return i == PRE_SLEEP.value ? PRE_SLEEP : __UNKNOWN__;
        }
    }
}
