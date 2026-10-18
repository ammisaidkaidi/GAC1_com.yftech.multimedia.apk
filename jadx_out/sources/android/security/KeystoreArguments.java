package android.security;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class KeystoreArguments implements Parcelable {
    public static final Parcelable.Creator<KeystoreArguments> CREATOR = new Parcelable.Creator<KeystoreArguments>() { // from class: android.security.KeystoreArguments.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeystoreArguments createFromParcel(Parcel parcel) {
            return new KeystoreArguments(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public KeystoreArguments[] newArray(int i) {
            return new KeystoreArguments[i];
        }
    };
    public byte[][] args;

    public KeystoreArguments() {
        this.args = null;
    }

    private KeystoreArguments(Parcel parcel) {
        readFromParcel(parcel);
    }

    public KeystoreArguments(byte[][] bArr) {
        this.args = bArr;
    }

    private void readFromParcel(Parcel parcel) {
        int i = parcel.readInt();
        this.args = new byte[i][];
        for (int i2 = 0; i2 < i; i2++) {
            this.args[i2] = parcel.createByteArray();
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.args == null) {
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(this.args.length);
        for (byte[] bArr : this.args) {
            parcel.writeByteArray(bArr);
        }
    }
}
