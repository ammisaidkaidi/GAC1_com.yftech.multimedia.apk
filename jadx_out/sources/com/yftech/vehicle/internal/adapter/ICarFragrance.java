package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarFragrance extends IBaseApi {
    public static final int FRAGRANCE1_WORKSTATE = 547;
    public static final int FRAGRANCE2_WORKSTATE = 548;
    public static final int FRAGRANCE3_WORKSTATE = 549;
    public static final int FRAGRANCE_TASTE1_FRAG = 550;
    public static final int FRAGRANCE_TASTE1_PERCENT = 544;
    public static final int FRAGRANCE_TASTE2_FRAG = 551;
    public static final int FRAGRANCE_TASTE2_PERCENT = 545;
    public static final int FRAGRANCE_TASTE3_FRAG = 552;
    public static final int FRAGRANCE_TASTE3_PERCENT = 546;
    public static final int FRAGRANCE_TASTE_STATE = 542;
    public static final int FragranceConcentration_STATE = -1;

    public enum FragranceConcentration {
        OFF(0),
        LOW(1),
        MID(2),
        HIGH(3),
        __UNKNOWN__(-1);

        public final int value;

        FragranceConcentration(int i) {
            this.value = i;
        }

        public static FragranceConcentration valueOf(int i) {
            if (i == OFF.value) {
                return OFF;
            }
            if (i == LOW.value) {
                return LOW;
            }
            if (i == MID.value) {
                return MID;
            }
            return i == HIGH.value ? HIGH : __UNKNOWN__;
        }
    }

    public enum FragranceTaste {
        NO_COMMAND(0),
        TASTE_A(1),
        TASTE_B(2),
        TASTE_C(3),
        __UNKNOWN__(-1);

        public final int value;

        FragranceTaste(int i) {
            this.value = i;
        }

        public static FragranceTaste valueOf(int i) {
            if (i == NO_COMMAND.value) {
                return NO_COMMAND;
            }
            if (i == TASTE_A.value) {
                return TASTE_A;
            }
            if (i == TASTE_B.value) {
                return TASTE_B;
            }
            return i == TASTE_C.value ? TASTE_C : __UNKNOWN__;
        }
    }

    public enum FragranceWorkState {
        RUN(0),
        NOT_RUN(1),
        __UNKNOWN__(-1);

        public final int value;

        FragranceWorkState(int i) {
            this.value = i;
        }

        public static FragranceWorkState valueOf(int i) {
            if (i == RUN.value) {
                return RUN;
            }
            return i == NOT_RUN.value ? NOT_RUN : __UNKNOWN__;
        }
    }
}
