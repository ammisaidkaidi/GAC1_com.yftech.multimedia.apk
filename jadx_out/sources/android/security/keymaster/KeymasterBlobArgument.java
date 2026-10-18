package android.security.keymaster;

import android.os.Parcel;

/* JADX INFO: loaded from: classes2.dex */
class KeymasterBlobArgument extends KeymasterArgument {
    public final byte[] blob;

    public KeymasterBlobArgument(int i, Parcel parcel) {
        super(i);
        this.blob = parcel.createByteArray();
    }

    public KeymasterBlobArgument(int i, byte[] bArr) {
        super(i);
        int tagType = KeymasterDefs.getTagType(i);
        if (tagType == Integer.MIN_VALUE || tagType == -1879048192) {
            this.blob = bArr;
            return;
        }
        throw new IllegalArgumentException("Bad blob tag " + i);
    }

    @Override // android.security.keymaster.KeymasterArgument
    public void writeValue(Parcel parcel) {
        parcel.writeByteArray(this.blob);
    }
}
