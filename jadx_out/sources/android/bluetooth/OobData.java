package android.bluetooth;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class OobData implements Parcelable {
    public static final Parcelable.Creator<OobData> CREATOR = new Parcelable.Creator<OobData>() { // from class: android.bluetooth.OobData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public OobData createFromParcel(Parcel parcel) {
            return new OobData(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public OobData[] newArray(int i) {
            return new OobData[i];
        }
    };
    private byte[] mLeBluetoothDeviceAddress;
    private byte[] mLeSecureConnectionsConfirmation;
    private byte[] mLeSecureConnectionsRandom;
    private byte[] mSecurityManagerTk;

    public OobData() {
    }

    private OobData(Parcel parcel) {
        this.mLeBluetoothDeviceAddress = parcel.createByteArray();
        this.mSecurityManagerTk = parcel.createByteArray();
        this.mLeSecureConnectionsConfirmation = parcel.createByteArray();
        this.mLeSecureConnectionsRandom = parcel.createByteArray();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public byte[] getLeBluetoothDeviceAddress() {
        return this.mLeBluetoothDeviceAddress;
    }

    public byte[] getLeSecureConnectionsConfirmation() {
        return this.mLeSecureConnectionsConfirmation;
    }

    public byte[] getLeSecureConnectionsRandom() {
        return this.mLeSecureConnectionsRandom;
    }

    public byte[] getSecurityManagerTk() {
        return this.mSecurityManagerTk;
    }

    public void setLeBluetoothDeviceAddress(byte[] bArr) {
        this.mLeBluetoothDeviceAddress = bArr;
    }

    public void setLeSecureConnectionsConfirmation(byte[] bArr) {
        this.mLeSecureConnectionsConfirmation = bArr;
    }

    public void setLeSecureConnectionsRandom(byte[] bArr) {
        this.mLeSecureConnectionsRandom = bArr;
    }

    public void setSecurityManagerTk(byte[] bArr) {
        this.mSecurityManagerTk = bArr;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByteArray(this.mLeBluetoothDeviceAddress);
        parcel.writeByteArray(this.mSecurityManagerTk);
        parcel.writeByteArray(this.mLeSecureConnectionsConfirmation);
        parcel.writeByteArray(this.mLeSecureConnectionsRandom);
    }
}
