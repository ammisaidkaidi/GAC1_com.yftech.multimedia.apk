package com.yftech.vehicle.internal.adapter;

import android.net.wifi.WifiScanner;

/* JADX INFO: loaded from: classes4.dex */
public interface ICarMisc extends IBaseCmd {

    public enum AteCarType {
        IHU12_A88_A1(0),
        IHU12_A58_B1(1),
        IHU12_A79_C1(2),
        IHU12_A79_C2(3),
        IHU12_A58G_B2(4),
        IHU12_A8E_A2(5),
        IHU12_A79G_C3C4(6),
        IHU12_A60_D1(7),
        IHU12_A60_D2(8),
        IHU12_A60_E1(9),
        IHU12_A60_E2(10),
        __UNKNOWN__(-1);

        public final int value;

        AteCarType(int i) {
            this.value = i;
        }

        public static AteCarType valueOf(int i) {
            if (i == IHU12_A88_A1.value) {
                return IHU12_A88_A1;
            }
            if (i == IHU12_A58_B1.value) {
                return IHU12_A58_B1;
            }
            if (i == IHU12_A79_C1.value) {
                return IHU12_A79_C1;
            }
            if (i == IHU12_A79_C2.value) {
                return IHU12_A79_C2;
            }
            if (i == IHU12_A58G_B2.value) {
                return IHU12_A58G_B2;
            }
            if (i == IHU12_A8E_A2.value) {
                return IHU12_A8E_A2;
            }
            if (i == IHU12_A79G_C3C4.value) {
                return IHU12_A79G_C3C4;
            }
            if (i == IHU12_A60_D1.value) {
                return IHU12_A60_D1;
            }
            if (i == IHU12_A60_D2.value) {
                return IHU12_A60_D2;
            }
            if (i == IHU12_A60_E1.value) {
                return IHU12_A60_E1;
            }
            return i == IHU12_A60_E2.value ? IHU12_A60_E2 : __UNKNOWN__;
        }
    }

    public interface CarDtcParam {
        public static final byte DTC_INDEX_AMP_TEMP_HEIGH = 4;
        public static final byte DTC_INDEX_CAMERA_FRONT_SIGNAL_FAULT = 6;
        public static final byte DTC_INDEX_CAMERA_LEFT_SIGNAL_FAULT = 8;
        public static final byte DTC_INDEX_CAMERA_REAR_SIGNAL_FAULT = 7;
        public static final byte DTC_INDEX_CAMERA_RIGHT_LVDS_FAULT = 10;
        public static final byte DTC_INDEX_CAMERA_RIGHT_SIGNAL_FAULT = 9;
        public static final byte DTC_INDEX_FRONT_SCREEN_CONN_FAULT = 1;
        public static final byte DTC_INDEX_MAIN_BOARD_TEMP_HEIGH = 3;
        public static final byte DTC_INDEX_MAIN_BOARD_TEMP_LOW = 2;
        public static final byte DTC_INDEX_REAR_VIEW_CONN_FAULT = 0;
        public static final byte DTC_STATE_VALUE_FAULT = 0;
        public static final byte DTC_STATE_VALUE_NORMAL = 1;
    }

    public static class DeviceNode {
        public boolean apaConn;
        public boolean avmConn;
        public boolean hvAcConn;

        private DeviceNode(boolean z, boolean z2, boolean z3) {
            this.hvAcConn = z;
            this.avmConn = z2;
            this.apaConn = z3;
        }

        public static DeviceNode valueOf(int i) {
            return new DeviceNode((i & 1) == 0, (i & 2) == 0, (i & 4) == 0);
        }

        public String toString() {
            return "DeviceNode{hvAcConn=" + this.hvAcConn + "avmConn=" + this.avmConn + "apaConn=" + this.apaConn + '}';
        }
    }

    public enum ECallState {
        NO_MESSAGE(0),
        CALL_LAUNCH(1),
        CALL_GOING(2),
        CALL_END(4),
        FAILURE(8),
        MALFUNCTION(16),
        __UNKNOWN__(-1);

        public final int value;

        ECallState(int i) {
            this.value = i;
        }

        public static ECallState valueOf(int i) {
            if (i == NO_MESSAGE.value) {
                return NO_MESSAGE;
            }
            if (i == CALL_LAUNCH.value) {
                return CALL_LAUNCH;
            }
            if (i == CALL_GOING.value) {
                return CALL_GOING;
            }
            if (i == CALL_END.value) {
                return CALL_END;
            }
            if (i == FAILURE.value) {
                return FAILURE;
            }
            return i == MALFUNCTION.value ? MALFUNCTION : __UNKNOWN__;
        }
    }

    public enum EngineerMode {
        START(1),
        STOP(2),
        RESULT(3),
        __UNKNOWN__(-1);

        public final int value;

        EngineerMode(int i) {
            this.value = i;
        }

        public static EngineerMode valueOf(int i) {
            if (i == START.value) {
                return START;
            }
            if (i == STOP.value) {
                return STOP;
            }
            return i == RESULT.value ? RESULT : __UNKNOWN__;
        }
    }

    public static class ErrorCodeInfo {
        public int day;
        public int dtc_index;
        public int dtc_state;
        public int hour;
        public int minute;
        public int month;
        public int motorSpeed;
        public int odoMeter;
        public int sec;
        public int vehicleSpeed;
        public int voltage;
        public int year;

        private ErrorCodeInfo(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12) {
            this.dtc_index = i;
            this.dtc_state = i2;
            this.vehicleSpeed = i3;
            this.motorSpeed = i4;
            this.voltage = i5;
            this.odoMeter = i6;
            this.year = i7;
            this.month = i8;
            this.day = i9;
            this.hour = i10;
            this.minute = i11;
            this.sec = i12;
        }

        public static ErrorCodeInfo valueOf(byte[] bArr) {
            return new ErrorCodeInfo(bArr[0], bArr[1], (bArr[3] & 255) | (bArr[2] << 8), (bArr[5] & 255) | (bArr[4] << 8), bArr[6] & 255, (bArr[7] << WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK) | (bArr[8] << 8) | bArr[9], bArr[10] & 255, bArr[11] & 255, bArr[12] & 255, bArr[13] & 255, bArr[14] & 255, bArr[15] & 255);
        }

        public String toString() {
            return "ErrorCodeInfo{dtc_index=" + this.dtc_index + ", dtc_state=" + this.dtc_state + ", vehicleSpeed=" + this.vehicleSpeed + ", motorSpeed=" + this.motorSpeed + ", voltage=" + this.voltage + ", odoMeter=" + this.odoMeter + ", year=" + this.year + ", month=" + this.month + ", day=" + this.day + ", hour=" + this.hour + ", minute=" + this.minute + ", sec=" + this.sec + '}';
        }
    }

    public enum HmiMode {
        ALL_OFF(0),
        VR(1),
        PHONE(2),
        NAVI(3),
        VEHICLE_SETTING(4),
        SYSTEM_SETTING(5),
        DRIVING_RECORDER(6),
        CARPLAY(7),
        CARLIFE(8),
        __UNKNOWN__(-1);

        public final int value;

        HmiMode(int i) {
            this.value = i;
        }

        public static HmiMode valueOf(int i) {
            if (i == ALL_OFF.value) {
                return ALL_OFF;
            }
            if (i == VR.value) {
                return VR;
            }
            if (i == PHONE.value) {
                return PHONE;
            }
            if (i == NAVI.value) {
                return NAVI;
            }
            if (i == VEHICLE_SETTING.value) {
                return VEHICLE_SETTING;
            }
            if (i == SYSTEM_SETTING.value) {
                return SYSTEM_SETTING;
            }
            if (i == DRIVING_RECORDER.value) {
                return DRIVING_RECORDER;
            }
            if (i == CARPLAY.value) {
                return CARPLAY;
            }
            return i == CARLIFE.value ? CARLIFE : __UNKNOWN__;
        }
    }

    public static class KeyTestEvent {
        public static final int ACTION_LONG_PRESS = 3;
        public static final int ACTION_LONG_RELEASED = 2;
        public static final int ACTION_PRESS = 1;
        public static final int ACTION_RELEASED = 0;
        public static final int KEYCODE_AM_FM = 2;
        public static final int KEYCODE_BACK = 15;
        public static final int KEYCODE_DISPLAY = 19;
        public static final int KEYCODE_EJECT = 1;
        public static final int KEYCODE_FM_ROLLING = 16;
        public static final int KEYCODE_MENU = 7;
        public static final int KEYCODE_MODE = 8;
        public static final int KEYCODE_MUTE = 14;
        public static final int KEYCODE_NAVI = 12;
        public static final int KEYCODE_NEXT = 3;
        public static final int KEYCODE_NONE = -1;
        public static final int KEYCODE_PHONE = 10;
        public static final int KEYCODE_POWER = 0;
        public static final int KEYCODE_PREVIOUS = 4;
        public static final int KEYCODE_RANDOM = 6;
        public static final int KEYCODE_REPEAT = 5;
        public static final int KEYCODE_RESEVE = 9;
        public static final int KEYCODE_SELECT = 17;
        public static final int KEYCODE_SET = 13;
        public static final int KEYCODE_VOLUME = 18;
        public static final int KEYCODE_VR = 11;
        public final int action;
        public final int keyCode;

        private KeyTestEvent(int i, int i2) {
            this.action = i2;
            this.keyCode = i;
        }

        public static KeyTestEvent valueOf(int i) {
            return new KeyTestEvent((i >> 8) & 255, i & 255);
        }
    }

    public enum ProgramSource {
        ALL_OFF(0),
        AM(1),
        FM(2),
        USB1(3),
        USB2(4),
        CD(5),
        SD(6),
        BT(7),
        AUX(8),
        USB3(9),
        IPOD(10),
        __UNKNOWN__(-1);

        public int radioHz;
        public final int value;

        ProgramSource(int i) {
            this.value = i;
        }

        public static ProgramSource valueOf(byte[] bArr) {
            byte b = bArr[0];
            int i = (bArr[2] & 255) | ((bArr[1] & 255) << 8);
            if (b == ALL_OFF.value) {
                return ALL_OFF.setRadioHz(i);
            }
            if (b == AM.value) {
                return AM.setRadioHz(i);
            }
            if (b == FM.value) {
                return FM.setRadioHz(i);
            }
            if (b == USB1.value) {
                return USB1.setRadioHz(i);
            }
            if (b == USB2.value) {
                return USB2.setRadioHz(i);
            }
            if (b == CD.value) {
                return CD.setRadioHz(i);
            }
            if (b == SD.value) {
                return SD.setRadioHz(i);
            }
            if (b == BT.value) {
                return BT.setRadioHz(i);
            }
            if (b == AUX.value) {
                return AUX.setRadioHz(i);
            }
            if (b == USB3.value) {
                return USB3.setRadioHz(i);
            }
            return b == IPOD.value ? IPOD.setRadioHz(i) : __UNKNOWN__;
        }

        public ProgramSource setRadioHz(int i) {
            this.radioHz = i;
            return this;
        }
    }

    public enum SpeakerCmd {
        ALL_SPEAKER(0),
        ONLY_FR_SPEAKER(1),
        ONLY_FL_SPEAKER(2),
        ONLY_RR_SPEAKER(3),
        ONLY_RL_SPEAKER(4),
        STOP_SPEAKER(5),
        __UNKNOWN__(-1);

        public final int value;

        SpeakerCmd(int i) {
            this.value = i;
        }

        public static SpeakerCmd valueOf(int i) {
            if (i == ALL_SPEAKER.value) {
                return ALL_SPEAKER;
            }
            if (i == ONLY_FR_SPEAKER.value) {
                return ONLY_FR_SPEAKER;
            }
            if (i == ONLY_FL_SPEAKER.value) {
                return ONLY_FL_SPEAKER;
            }
            if (i == ONLY_RR_SPEAKER.value) {
                return ONLY_RR_SPEAKER;
            }
            if (i == ONLY_RL_SPEAKER.value) {
                return ONLY_RL_SPEAKER;
            }
            return i == STOP_SPEAKER.value ? STOP_SPEAKER : __UNKNOWN__;
        }
    }

    public enum TftCmd {
        OPEN(0),
        CLOSE(1),
        STOP(2),
        __UNKNOWN__(-1);

        public final int value;

        TftCmd(int i) {
            this.value = i;
        }

        public static TftCmd valueOf(int i) {
            if (i == OPEN.value) {
                return OPEN;
            }
            if (i == CLOSE.value) {
                return CLOSE;
            }
            return i == STOP.value ? STOP : __UNKNOWN__;
        }
    }

    public enum TftColorState {
        STOP_CTL(0),
        RED(1),
        GREEN(2),
        BLUE(3),
        BLACK(4),
        WHITE(5),
        __UNKNOWN__(-1);

        public boolean isOpened;
        public final int value;

        TftColorState(int i) {
            this.value = i;
        }

        public static TftColorState valueOf(int i) {
            int i2 = (i >> 8) & 255;
            boolean z = (i & 255) == 1;
            if (i2 == STOP_CTL.value) {
                return STOP_CTL.setIsOpened(z);
            }
            if (i2 == RED.value) {
                return RED.setIsOpened(z);
            }
            if (i2 == GREEN.value) {
                return GREEN.setIsOpened(z);
            }
            if (i2 == BLUE.value) {
                return BLUE.setIsOpened(z);
            }
            if (i2 == BLACK.value) {
                return BLACK.setIsOpened(z);
            }
            return i2 == WHITE.value ? WHITE.setIsOpened(z) : __UNKNOWN__;
        }

        public TftColorState setIsOpened(boolean z) {
            this.isOpened = z;
            return this;
        }
    }

    public enum TransportMode {
        TRANSPORT(1),
        NOT_TRANSPORT(2),
        FAULT(3),
        __UNKNOWN__(-1);

        public final int value;

        TransportMode(int i) {
            this.value = i;
        }

        public static TransportMode valueOf(int i) {
            if (i == TRANSPORT.value) {
                return TRANSPORT;
            }
            if (i == NOT_TRANSPORT.value) {
                return NOT_TRANSPORT;
            }
            return i == FAULT.value ? FAULT : __UNKNOWN__;
        }
    }
}
