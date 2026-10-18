package com.yftech.vehicle.internal;

import android.net.wifi.WifiScanner;
import com.android.internal.midi.MidiConstants;

/* JADX INFO: loaded from: classes4.dex */
class CarData {
    private final byte DATA_TYPE_BYTE;
    private final byte DATA_TYPE_DATA;
    private final byte DATA_TYPE_DOUBLE;
    private final byte DATA_TYPE_FLOAT;
    private final byte DATA_TYPE_INT;
    private final byte DATA_TYPE_LONG;
    private final byte DATA_TYPE_SHORT;
    private byte[] data;
    private int offset;

    public CarData(byte b) {
        this.DATA_TYPE_BYTE = (byte) 31;
        this.DATA_TYPE_SHORT = (byte) 30;
        this.DATA_TYPE_INT = (byte) 28;
        this.DATA_TYPE_LONG = (byte) 24;
        this.DATA_TYPE_FLOAT = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
        this.DATA_TYPE_DOUBLE = (byte) 17;
        this.DATA_TYPE_DATA = MidiConstants.STATUS_PITCH_BEND;
        this.offset = 0;
        this.data = new byte[2];
        this.data[0] = 31;
        this.data[1] = (byte) (b & 255);
    }

    public CarData(double d) {
        this.DATA_TYPE_BYTE = (byte) 31;
        this.DATA_TYPE_SHORT = (byte) 30;
        this.DATA_TYPE_INT = (byte) 28;
        this.DATA_TYPE_LONG = (byte) 24;
        this.DATA_TYPE_FLOAT = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
        this.DATA_TYPE_DOUBLE = (byte) 17;
        this.DATA_TYPE_DATA = MidiConstants.STATUS_PITCH_BEND;
        this.offset = 0;
        this.data = new byte[9];
        this.data[0] = 17;
        long jDoubleToLongBits = Double.doubleToLongBits(d);
        this.data[1] = (byte) (jDoubleToLongBits & 255);
        this.data[2] = (byte) ((jDoubleToLongBits >> 8) & 255);
        this.data[3] = (byte) ((jDoubleToLongBits >> 16) & 255);
        this.data[4] = (byte) ((jDoubleToLongBits >> 24) & 255);
        this.data[5] = (byte) ((jDoubleToLongBits >> 32) & 255);
        this.data[6] = (byte) ((jDoubleToLongBits >> 40) & 255);
        this.data[7] = (byte) ((jDoubleToLongBits >> 48) & 255);
        this.data[8] = (byte) ((jDoubleToLongBits >> 56) & 255);
    }

    public CarData(float f) {
        this.DATA_TYPE_BYTE = (byte) 31;
        this.DATA_TYPE_SHORT = (byte) 30;
        this.DATA_TYPE_INT = (byte) 28;
        this.DATA_TYPE_LONG = (byte) 24;
        this.DATA_TYPE_FLOAT = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
        this.DATA_TYPE_DOUBLE = (byte) 17;
        this.DATA_TYPE_DATA = MidiConstants.STATUS_PITCH_BEND;
        this.offset = 0;
        int iFloatToIntBits = Float.floatToIntBits(f);
        this.data = new byte[5];
        this.data[0] = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
        this.data[1] = (byte) (iFloatToIntBits & 255);
        this.data[2] = (byte) ((iFloatToIntBits >> 8) & 255);
        this.data[3] = (byte) ((iFloatToIntBits >> 16) & 255);
        this.data[4] = (byte) ((iFloatToIntBits >> 24) & 255);
    }

    public CarData(int i) {
        this.DATA_TYPE_BYTE = (byte) 31;
        this.DATA_TYPE_SHORT = (byte) 30;
        this.DATA_TYPE_INT = (byte) 28;
        this.DATA_TYPE_LONG = (byte) 24;
        this.DATA_TYPE_FLOAT = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
        this.DATA_TYPE_DOUBLE = (byte) 17;
        this.DATA_TYPE_DATA = MidiConstants.STATUS_PITCH_BEND;
        this.offset = 0;
        this.data = new byte[5];
        this.data[0] = 28;
        this.data[1] = (byte) (i & 255);
        this.data[2] = (byte) ((i >> 8) & 255);
        this.data[3] = (byte) ((i >> 16) & 255);
        this.data[4] = (byte) ((i >> 24) & 255);
    }

    public CarData(long j) {
        this.DATA_TYPE_BYTE = (byte) 31;
        this.DATA_TYPE_SHORT = (byte) 30;
        this.DATA_TYPE_INT = (byte) 28;
        this.DATA_TYPE_LONG = (byte) 24;
        this.DATA_TYPE_FLOAT = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
        this.DATA_TYPE_DOUBLE = (byte) 17;
        this.DATA_TYPE_DATA = MidiConstants.STATUS_PITCH_BEND;
        this.offset = 0;
        this.data = new byte[9];
        this.data[0] = 24;
        this.data[1] = (byte) (j & 255);
        this.data[2] = (byte) ((j >> 8) & 255);
        this.data[3] = (byte) ((j >> 16) & 255);
        this.data[4] = (byte) ((j >> 24) & 255);
        this.data[5] = (byte) ((j >> 32) & 255);
        this.data[6] = (byte) ((j >> 40) & 255);
        this.data[7] = (byte) ((j >> 48) & 255);
        this.data[8] = (byte) ((j >> 56) & 255);
    }

    public CarData(short s) {
        this.DATA_TYPE_BYTE = (byte) 31;
        this.DATA_TYPE_SHORT = (byte) 30;
        this.DATA_TYPE_INT = (byte) 28;
        this.DATA_TYPE_LONG = (byte) 24;
        this.DATA_TYPE_FLOAT = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
        this.DATA_TYPE_DOUBLE = (byte) 17;
        this.DATA_TYPE_DATA = MidiConstants.STATUS_PITCH_BEND;
        this.offset = 0;
        this.data = new byte[3];
        this.data[0] = 30;
        this.data[1] = (byte) (s & 255);
        this.data[2] = (byte) ((s >> 8) & 255);
    }

    public CarData(byte[] bArr) {
        this(bArr, true);
    }

    public CarData(byte[] bArr, boolean z) {
        this.DATA_TYPE_BYTE = (byte) 31;
        this.DATA_TYPE_SHORT = (byte) 30;
        this.DATA_TYPE_INT = (byte) 28;
        this.DATA_TYPE_LONG = (byte) 24;
        this.DATA_TYPE_FLOAT = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
        this.DATA_TYPE_DOUBLE = (byte) 17;
        this.DATA_TYPE_DATA = MidiConstants.STATUS_PITCH_BEND;
        this.offset = 0;
        if (z) {
            this.data = bArr;
            return;
        }
        this.data = new byte[bArr.length + 2];
        this.data[0] = MidiConstants.STATUS_PITCH_BEND;
        this.data[1] = (byte) bArr.length;
        System.arraycopy(bArr, 0, this.data, 2, bArr.length);
    }

    private byte byteValue() {
        if (this.data.length - this.offset > 1) {
            return this.data[this.offset + 1];
        }
        return (byte) -1;
    }

    private double doubleValue() {
        if (this.data.length - this.offset > 8) {
            return Double.longBitsToDouble(longValue());
        }
        return -1.0d;
    }

    private float floatValue() {
        if (this.data.length - this.offset > 4) {
            return Float.intBitsToFloat(intValue());
        }
        return -1.0f;
    }

    private int intValue() {
        if (this.data.length - this.offset <= 4) {
            return -1;
        }
        return (this.data[this.offset + 4] << 24) | (this.data[this.offset + 1] & 255) | ((this.data[this.offset + 2] & 255) << 8) | ((this.data[this.offset + 3] & 255) << 16);
    }

    private long longValue() {
        if (this.data.length - this.offset <= 8) {
            return -1L;
        }
        return (((long) this.data[this.offset + 8]) << 56) | (((long) this.data[this.offset + 1]) & 255) | ((((long) this.data[this.offset + 2]) & 255) << 8) | ((((long) this.data[this.offset + 3]) & 255) << 16) | ((((long) this.data[this.offset + 4]) & 255) << 24) | ((((long) this.data[this.offset + 5]) & 255) << 32) | ((((long) this.data[this.offset + 6]) & 255) << 40) | ((255 & ((long) this.data[this.offset + 7])) << 48);
    }

    private short shortValue() {
        if (this.data.length - this.offset <= 2) {
            return (short) -1;
        }
        return (short) ((this.data[this.offset + 2] << 8) | (this.data[this.offset + 1] & 255));
    }

    public byte[] dataValue() {
        int i;
        if (this.data == null) {
            return new byte[0];
        }
        if (this.data.length - this.offset < 2) {
            return new byte[0];
        }
        if (this.data[this.offset] != -32 || (this.data.length - this.offset) - 1 <= (i = this.data[this.offset + 1] & 255)) {
            return new byte[0];
        }
        byte[] bArr = new byte[i];
        System.arraycopy(this.data, this.offset + 2, bArr, 0, i);
        return bArr;
    }

    public byte[] getBytes() {
        return this.data;
    }

    public boolean isDataValue() {
        if (this.data == null) {
            return false;
        }
        try {
            return this.data[this.offset] == -32;
        } catch (Exception unused) {
            return false;
        }
    }

    /* JADX WARN: Code duplicated, block: B:16:0x003a  */
    /* JADX WARN: Code duplicated, block: B:19:0x0046  */
    public CarData next() {
        if (this.data == null || this.data.length - this.offset < 1) {
            return null;
        }
        int i = Integer.MAX_VALUE;
        if (this.data[this.offset] == 31) {
            i = 1;
        } else if (this.data[this.offset] == 30) {
            i = 2;
        } else if (this.data[this.offset] == 28) {
            i = 4;
        } else if (this.data[this.offset] == 24) {
            i = 8;
        } else if (this.data[this.offset] == 16) {
            i = 4;
        } else if (this.data[this.offset] == 17) {
            i = 8;
        } else if (this.data[this.offset] == -32 && (this.data.length - this.offset) - 1 > 1) {
            i = this.data[this.offset + 1] & 255;
        }
        this.offset = this.offset + i + 1;
        if (this.data[this.offset] == -32) {
            this.offset++;
        }
        if (this.offset < this.data.length - 1) {
            return this;
        }
        return null;
    }

    public Number numberValue() {
        if (this.data != null && this.data.length - this.offset >= 1) {
            if (this.data[this.offset] == 31) {
                return Byte.valueOf(byteValue());
            }
            if (this.data[this.offset] == 30) {
                return Short.valueOf(shortValue());
            }
            if (this.data[this.offset] == 28) {
                return Integer.valueOf(intValue());
            }
            if (this.data[this.offset] == 24) {
                return Long.valueOf(longValue());
            }
            if (this.data[this.offset] == 16) {
                return Float.valueOf(floatValue());
            }
            if (this.data[this.offset] == 17) {
                return Double.valueOf(doubleValue());
            }
            return -1;
        }
        return -1;
    }
}
