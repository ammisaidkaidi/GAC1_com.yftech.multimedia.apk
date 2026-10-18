package com.yftech.vehicle.internal.project;

import com.yftech.vehicle.internal.adapter.IBaseCmd;

/* JADX INFO: loaded from: classes4.dex */
public interface IProject extends IBaseCmd {

    public enum AccountCfg {
        ACCOUNT_1(1),
        ACCOUNT_2(2),
        ACCOUNT_3(3),
        ACCOUNT_4(4),
        ACCOUNT_5(5),
        ACCOUNT_6(6),
        ACCOUNT_7(7),
        ACCOUNT_8(8),
        __UNKNOWN__(-1);

        public final int value;

        AccountCfg(int i) {
            this.value = i;
        }

        public static AccountCfg valueOf(int i) {
            if (i == ACCOUNT_1.value) {
                return ACCOUNT_1;
            }
            if (i == ACCOUNT_2.value) {
                return ACCOUNT_2;
            }
            if (i == ACCOUNT_3.value) {
                return ACCOUNT_3;
            }
            if (i == ACCOUNT_4.value) {
                return ACCOUNT_4;
            }
            if (i == ACCOUNT_5.value) {
                return ACCOUNT_5;
            }
            if (i == ACCOUNT_6.value) {
                return ACCOUNT_6;
            }
            if (i == ACCOUNT_7.value) {
                return ACCOUNT_7;
            }
            return i == ACCOUNT_8.value ? ACCOUNT_8 : __UNKNOWN__;
        }
    }

    public static class CarSeartMemoryInfo {
        public AccountCfg accountCfg;
        public DriverSeatPostionCmd cmd;
        public byte puid;

        public CarSeartMemoryInfo(DriverSeatPostionCmd driverSeatPostionCmd, AccountCfg accountCfg) {
            this.cmd = DriverSeatPostionCmd.__UNKNOWN__;
            this.accountCfg = AccountCfg.__UNKNOWN__;
            this.cmd = driverSeatPostionCmd;
            this.accountCfg = accountCfg;
            this.puid = this.puid;
        }

        public CarSeartMemoryInfo(DriverSeatPostionCmd driverSeatPostionCmd, AccountCfg accountCfg, byte b) {
            this.cmd = DriverSeatPostionCmd.__UNKNOWN__;
            this.accountCfg = AccountCfg.__UNKNOWN__;
            this.cmd = driverSeatPostionCmd;
            this.accountCfg = accountCfg;
            this.puid = b;
        }

        public byte[] parseByte(CarSeartMemoryInfo carSeartMemoryInfo) {
            byte[] bArr = new byte[6];
            bArr[0] = -122;
            bArr[1] = 25;
            bArr[2] = 1;
            if (carSeartMemoryInfo.cmd == DriverSeatPostionCmd.__UNKNOWN__) {
                bArr[3] = 0;
            } else {
                bArr[3] = (byte) carSeartMemoryInfo.cmd.value;
            }
            bArr[4] = (byte) carSeartMemoryInfo.accountCfg.value;
            bArr[5] = 0;
            return bArr;
        }
    }

    public enum DriverSeatPostionCmd {
        SAVE(1),
        RECALL(2),
        __UNKNOWN__(-1);

        public final int value;

        DriverSeatPostionCmd(int i) {
            this.value = i;
        }

        public static DriverSeatPostionCmd valueOf(int i) {
            if (i == SAVE.value) {
                return SAVE;
            }
            return i == RECALL.value ? RECALL : __UNKNOWN__;
        }
    }

    public enum OpertionObject {
        SEAT_POSITION(1),
        __UNKNOWN__(-1);

        public final int value;

        OpertionObject(int i) {
            this.value = i;
        }

        public static OpertionObject valueOf(int i) {
            return i == SEAT_POSITION.value ? SEAT_POSITION : __UNKNOWN__;
        }
    }

    public enum OpertionType {
        MEMORY(0),
        SEARCH(1),
        DELETE(2),
        __UNKNOWN__(-1);

        public final int value;

        OpertionType(int i) {
            this.value = i;
        }

        public static OpertionType valueOf(int i) {
            if (i == MEMORY.value) {
                return MEMORY;
            }
            if (i == SEARCH.value) {
                return SEARCH;
            }
            return i == DELETE.value ? DELETE : __UNKNOWN__;
        }
    }

    public static class PersonalMemoryInfo {
        public AccountCfg accountCfg;
        public byte currentPuid;
        public OpertionObject opObj;
        public OpertionType opType;
        public byte puid;

        public PersonalMemoryInfo(OpertionType opertionType, AccountCfg accountCfg) {
            this.opObj = OpertionObject.SEAT_POSITION;
            this.opType = OpertionType.__UNKNOWN__;
            this.accountCfg = AccountCfg.__UNKNOWN__;
            this.puid = (byte) 0;
            this.currentPuid = (byte) 0;
            this.opType = opertionType;
            this.accountCfg = accountCfg;
            this.puid = this.puid;
        }

        public PersonalMemoryInfo(OpertionType opertionType, AccountCfg accountCfg, byte b) {
            this.opObj = OpertionObject.SEAT_POSITION;
            this.opType = OpertionType.__UNKNOWN__;
            this.accountCfg = AccountCfg.__UNKNOWN__;
            this.puid = (byte) 0;
            this.currentPuid = (byte) 0;
            this.opType = opertionType;
            this.accountCfg = accountCfg;
            this.puid = b;
        }

        public PersonalMemoryInfo(OpertionType opertionType, AccountCfg accountCfg, byte b, byte b2) {
            this.opObj = OpertionObject.SEAT_POSITION;
            this.opType = OpertionType.__UNKNOWN__;
            this.accountCfg = AccountCfg.__UNKNOWN__;
            this.puid = (byte) 0;
            this.currentPuid = (byte) 0;
            this.opType = opertionType;
            this.accountCfg = accountCfg;
            this.puid = b;
            this.currentPuid = b2;
        }

        public static PersonalMemoryInfo valueOf(byte[] bArr) {
            byte b = bArr[0];
            return new PersonalMemoryInfo(OpertionType.valueOf(bArr[1] & 255), AccountCfg.valueOf(bArr[2] & 255), (byte) (bArr[3] & 255), (byte) (bArr[4] & 255));
        }

        public byte[] parseByte(PersonalMemoryInfo personalMemoryInfo) {
            return new byte[]{-122, 24, (byte) personalMemoryInfo.opObj.value, (byte) personalMemoryInfo.opType.value, (byte) personalMemoryInfo.accountCfg.value};
        }
    }
}
