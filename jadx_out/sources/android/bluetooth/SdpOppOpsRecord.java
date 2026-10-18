package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class SdpOppOpsRecord implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() { // from class: android.bluetooth.SdpOppOpsRecord.1
        @Override // android.os.Parcelable.Creator
        public SdpOppOpsRecord createFromParcel(Parcel parcel) {
            return new SdpOppOpsRecord(parcel);
        }

        @Override // android.os.Parcelable.Creator
        public SdpOppOpsRecord[] newArray(int i) {
            return new SdpOppOpsRecord[i];
        }
    };
    private final byte[] mFormatsList;
    private final int mL2capPsm;
    private final int mProfileVersion;
    private final int mRfcommChannel;
    private final String mServiceName;

    public SdpOppOpsRecord(Parcel parcel) {
        this.mRfcommChannel = parcel.readInt();
        this.mL2capPsm = parcel.readInt();
        this.mProfileVersion = parcel.readInt();
        this.mServiceName = parcel.readString();
        int i = parcel.readInt();
        if (i <= 0) {
            this.mFormatsList = null;
            return;
        }
        byte[] bArr = new byte[i];
        parcel.readByteArray(bArr);
        this.mFormatsList = bArr;
    }

    public SdpOppOpsRecord(String str, int i, int i2, int i3, byte[] bArr) {
        this.mServiceName = str;
        this.mRfcommChannel = i;
        this.mL2capPsm = i2;
        this.mProfileVersion = i3;
        this.mFormatsList = bArr;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public byte[] getFormatsList() {
        return this.mFormatsList;
    }

    public int getL2capPsm() {
        return this.mL2capPsm;
    }

    public int getProfileVersion() {
        return this.mProfileVersion;
    }

    public int getRfcommChannel() {
        return this.mRfcommChannel;
    }

    public String getServiceName() {
        return this.mServiceName;
    }

    public String toString() {
        return "Bluetooth OPP Server SDP Record:\n  RFCOMM Chan Number: " + this.mRfcommChannel + "\n  L2CAP PSM: " + this.mL2capPsm + "\n  Profile version: " + this.mProfileVersion + "\n  Service Name: " + this.mServiceName + "\n  Formats List: " + Arrays.toString(this.mFormatsList);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRfcommChannel);
        parcel.writeInt(this.mL2capPsm);
        parcel.writeInt(this.mProfileVersion);
        parcel.writeString(this.mServiceName);
        if (this.mFormatsList == null || this.mFormatsList.length <= 0) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(this.mFormatsList.length);
            parcel.writeByteArray(this.mFormatsList);
        }
    }
}
