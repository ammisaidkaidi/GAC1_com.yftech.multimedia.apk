package com.yftech.vehicle.internal.adapter;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarWindow extends IBaseApi {
    public static final int SUNROOF_WINDOW_AUTO_CLOSE_SETTING = 63;
    public static final int SUNROOF_WINDOW_AUTO_CLOSE_STATE = 160;
    public static final int WINDOW_OPEN_PERCENT_FL = 162;
    public static final int WINDOW_OPEN_PERCENT_FL_SETTING = 64;
    public static final int WINDOW_OPEN_PERCENT_FL_VD_SETTING = 364;
    public static final int WINDOW_OPEN_PERCENT_FR = 166;
    public static final int WINDOW_OPEN_PERCENT_FR_SETTING = 65;
    public static final int WINDOW_OPEN_PERCENT_FR_VD_SETTING = 365;
    public static final int WINDOW_OPEN_PERCENT_RL = 170;
    public static final int WINDOW_OPEN_PERCENT_RL_SETTING = 66;
    public static final int WINDOW_OPEN_PERCENT_RL_VD_SETTING = 366;
    public static final int WINDOW_OPEN_PERCENT_RR = 174;
    public static final int WINDOW_OPEN_PERCENT_RR_SETTING = 67;
    public static final int WINDOW_OPEN_PERCENT_RR_VD_SETTING = 367;
    public static final int WINDOW_OPERATION_STATE_FL = 163;
    public static final int WINDOW_OPERATION_STATE_FR = 167;
    public static final int WINDOW_OPERATION_STATE_RL = 171;
    public static final int WINDOW_OPERATION_STATE_RR = 175;
    public static final int WINDOW_OVERHEAT_STATE_FL = 164;
    public static final int WINDOW_OVERHEAT_STATE_FR = 168;
    public static final int WINDOW_OVERHEAT_STATE_RL = 172;
    public static final int WINDOW_OVERHEAT_STATE_RR = 176;
    public static final int WINDOW_ROOF_OPEN_OPERTION = 247;
    public static final int WINDOW_ROOF_OPEN_PERCENT = 246;
    public static final int WINDOW_ROOF_OPEN_STATE_SETTING = 68;
    public static final int WINDOW_ROOF_SHADE_OPEN_OPERTION = 250;
    public static final int WINDOW_ROOF_SHADE_OPEN_PERCENT = 249;
    public static final int WINDOW_ROOF_SHADE_OPEN_PERCENT_SETTING = 248;
    public static final int WINDOW_ROOF_SHADE_OPEN_STATE_SETTING = 248;
    public static final int WINDOW_STATE_FL = 161;
    public static final int WINDOW_STATE_FL_SETTING = 202;
    public static final int WINDOW_STATE_FR = 165;
    public static final int WINDOW_STATE_FR_SETTING = 203;
    public static final int WINDOW_STATE_RL = 169;
    public static final int WINDOW_STATE_RL_SETTING = 204;
    public static final int WINDOW_STATE_RR = 173;
    public static final int WINDOW_STATE_RR_SETTING = 205;

    public enum WindowOpenPercent {
        STOP(1),
        FULLY_OPEN(2),
        FULLY_CLOSE(3),
        TEN_PERCENT_OPEN(4),
        TWENTY_PERCENT_OPEN(5),
        THIRTY_PERCENT_OPEN(6),
        FORTY_PERCENT_OPEN(7),
        FIFTY_PERCENT_OPEN(8),
        SIXTY_PERCENT_OPEN(9),
        SEVENTY_PERCENT_OPEN(10),
        EIGHTY_PERCENT_OPEN(11),
        NINETY_PERCENT_OPEN(12),
        CHANGE_OPEN(13),
        CHANGE_CLOSE(14),
        WIND_BREATHE(105),
        __UNKNOWN__(-1);

        public final int value;

        WindowOpenPercent(int i) {
            this.value = i;
        }

        public static WindowOpenPercent valueOf(int i) {
            if (i == STOP.value) {
                return STOP;
            }
            if (i == FULLY_OPEN.value) {
                return FULLY_OPEN;
            }
            if (i == FULLY_CLOSE.value) {
                return FULLY_CLOSE;
            }
            if (i == TEN_PERCENT_OPEN.value) {
                return TEN_PERCENT_OPEN;
            }
            if (i == TWENTY_PERCENT_OPEN.value) {
                return TWENTY_PERCENT_OPEN;
            }
            if (i == THIRTY_PERCENT_OPEN.value) {
                return THIRTY_PERCENT_OPEN;
            }
            if (i == FORTY_PERCENT_OPEN.value) {
                return FORTY_PERCENT_OPEN;
            }
            if (i == FIFTY_PERCENT_OPEN.value) {
                return FIFTY_PERCENT_OPEN;
            }
            if (i == SIXTY_PERCENT_OPEN.value) {
                return SIXTY_PERCENT_OPEN;
            }
            if (i == SEVENTY_PERCENT_OPEN.value) {
                return SEVENTY_PERCENT_OPEN;
            }
            if (i == EIGHTY_PERCENT_OPEN.value) {
                return EIGHTY_PERCENT_OPEN;
            }
            if (i == NINETY_PERCENT_OPEN.value) {
                return NINETY_PERCENT_OPEN;
            }
            if (i == CHANGE_OPEN.value) {
                return CHANGE_OPEN;
            }
            if (i == CHANGE_CLOSE.value) {
                return CHANGE_CLOSE;
            }
            return i == WIND_BREATHE.value ? WIND_BREATHE : __UNKNOWN__;
        }
    }

    public enum WindowOperationState {
        STOPED(0),
        CLOSING(1),
        OPENING(2),
        ANTIPINCH_REVERSING(3),
        __UNKNOWN__(-1);

        public final int value;

        WindowOperationState(int i) {
            this.value = i;
        }

        public static WindowOperationState valueOf(int i) {
            if (i == STOPED.value) {
                return STOPED;
            }
            if (i == CLOSING.value) {
                return CLOSING;
            }
            if (i == OPENING.value) {
                return OPENING;
            }
            return i == ANTIPINCH_REVERSING.value ? ANTIPINCH_REVERSING : __UNKNOWN__;
        }
    }

    public enum WindowRoofOpenOpertion {
        FULLY_CLOSED(0),
        SLIDE_OPEN(1),
        TILT_UP(2),
        OPENING(3),
        CLOSING(4),
        REVERSING(5),
        POSITION_NOT_TRUSTED(6),
        FULLY_OPENED(7),
        COMFORT_POSITION(8),
        EXPANDED_SLIDE_OPEN(9),
        __UNKNOWN__(-1);

        public final int value;

        WindowRoofOpenOpertion(int i) {
            this.value = i;
        }

        public static WindowRoofOpenOpertion valueOf(int i) {
            if (i == FULLY_CLOSED.value) {
                return FULLY_CLOSED;
            }
            if (i == SLIDE_OPEN.value) {
                return SLIDE_OPEN;
            }
            if (i == TILT_UP.value) {
                return TILT_UP;
            }
            if (i == OPENING.value) {
                return OPENING;
            }
            if (i == CLOSING.value) {
                return CLOSING;
            }
            if (i == REVERSING.value) {
                return REVERSING;
            }
            if (i == POSITION_NOT_TRUSTED.value) {
                return POSITION_NOT_TRUSTED;
            }
            if (i == FULLY_OPENED.value) {
                return FULLY_OPENED;
            }
            if (i == COMFORT_POSITION.value) {
                return COMFORT_POSITION;
            }
            return i == EXPANDED_SLIDE_OPEN.value ? EXPANDED_SLIDE_OPEN : __UNKNOWN__;
        }
    }

    public enum WindowRoofOpenState {
        STOP(1),
        FULLY_OPEN(2),
        FULLY_CLOSE(3),
        TILT_UP(4),
        SHADE_OPEN(5),
        SHADE_CLOS(6),
        TWENTY_PERCENT_OPEN(7),
        FORTY_PERCENT_OPEN(8),
        SIXTY_PERCENT_OPEN(9),
        EIGHTY_PERCENT_OPEN(10),
        ONE_HUNDRED_PERCENT_OPEN(11),
        CHANGE_OPEN(12),
        CHANGE_CLOSE(13),
        __UNKNOWN__(-1);

        public final int value;

        WindowRoofOpenState(int i) {
            this.value = i;
        }

        public static WindowRoofOpenState valueOf(int i) {
            if (i == STOP.value) {
                return STOP;
            }
            if (i == FULLY_OPEN.value) {
                return FULLY_OPEN;
            }
            if (i == FULLY_CLOSE.value) {
                return FULLY_CLOSE;
            }
            if (i == TILT_UP.value) {
                return TILT_UP;
            }
            if (i == SHADE_OPEN.value) {
                return SHADE_OPEN;
            }
            if (i == SHADE_CLOS.value) {
                return SHADE_CLOS;
            }
            if (i == TWENTY_PERCENT_OPEN.value) {
                return TWENTY_PERCENT_OPEN;
            }
            if (i == FORTY_PERCENT_OPEN.value) {
                return FORTY_PERCENT_OPEN;
            }
            if (i == SIXTY_PERCENT_OPEN.value) {
                return SIXTY_PERCENT_OPEN;
            }
            if (i == EIGHTY_PERCENT_OPEN.value) {
                return EIGHTY_PERCENT_OPEN;
            }
            if (i == ONE_HUNDRED_PERCENT_OPEN.value) {
                return ONE_HUNDRED_PERCENT_OPEN;
            }
            if (i == CHANGE_OPEN.value) {
                return CHANGE_OPEN;
            }
            return i == CHANGE_CLOSE.value ? CHANGE_CLOSE : __UNKNOWN__;
        }
    }

    public enum WindowRoofShadeOpenOpertion {
        FULLY_CLOSE(0),
        SLIDE_OPEN(1),
        NO_USED(2),
        OPENING(3),
        CLOSING(4),
        REVERSING(5),
        POSITION_NOT_TRUSTED(6),
        FULLY_OPEN(7),
        __UNKNOWN__(-1);

        public final int value;

        WindowRoofShadeOpenOpertion(int i) {
            this.value = i;
        }

        public static WindowRoofShadeOpenOpertion valueOf(int i) {
            if (i == FULLY_CLOSE.value) {
                return FULLY_CLOSE;
            }
            if (i == SLIDE_OPEN.value) {
                return SLIDE_OPEN;
            }
            if (i == NO_USED.value) {
                return NO_USED;
            }
            if (i == OPENING.value) {
                return OPENING;
            }
            if (i == CLOSING.value) {
                return CLOSING;
            }
            if (i == REVERSING.value) {
                return REVERSING;
            }
            if (i == POSITION_NOT_TRUSTED.value) {
                return POSITION_NOT_TRUSTED;
            }
            return i == FULLY_OPEN.value ? FULLY_OPEN : __UNKNOWN__;
        }
    }

    public enum WindowRoofShadeOpenState {
        ROOF_FULLY_CLOSE(101),
        ROOF_TILT_UP(102),
        ROOF_COMFORT_POSITION(103),
        ROOF_FULLY_OPEN(104),
        SHADE_CLOSE(105),
        SHADE_OPEN(106),
        CHANGE_OPEN_TWENTY_PERCENT(107),
        CHANGE_CLOSE_TWENTY_PERCENT(108),
        STOP(109),
        __UNKNOWN__(-1);

        public final int value;

        WindowRoofShadeOpenState(int i) {
            this.value = i;
        }

        public static WindowRoofShadeOpenState valueOf(int i) {
            if (i == ROOF_FULLY_CLOSE.value) {
                return ROOF_FULLY_CLOSE;
            }
            if (i == ROOF_TILT_UP.value) {
                return ROOF_TILT_UP;
            }
            if (i == ROOF_COMFORT_POSITION.value) {
                return ROOF_COMFORT_POSITION;
            }
            if (i == ROOF_FULLY_OPEN.value) {
                return ROOF_FULLY_OPEN;
            }
            if (i == SHADE_CLOSE.value) {
                return SHADE_CLOSE;
            }
            if (i == SHADE_OPEN.value) {
                return SHADE_OPEN;
            }
            if (i == CHANGE_OPEN_TWENTY_PERCENT.value) {
                return CHANGE_OPEN_TWENTY_PERCENT;
            }
            if (i == CHANGE_CLOSE_TWENTY_PERCENT.value) {
                return CHANGE_CLOSE_TWENTY_PERCENT;
            }
            return i == STOP.value ? STOP : __UNKNOWN__;
        }
    }

    public enum WindowState {
        UNDEFINED_POSITION(0),
        FULLY_CLOSED(1),
        BETWEEN(2),
        FULLY_OPENED(3),
        __UNKNOWN__(-1);

        public final int value;

        WindowState(int i) {
            this.value = i;
        }

        public static WindowState valueOf(int i) {
            if (i == UNDEFINED_POSITION.value) {
                return UNDEFINED_POSITION;
            }
            if (i == FULLY_CLOSED.value) {
                return FULLY_CLOSED;
            }
            if (i == BETWEEN.value) {
                return BETWEEN;
            }
            return i == FULLY_OPENED.value ? FULLY_OPENED : __UNKNOWN__;
        }
    }
}
