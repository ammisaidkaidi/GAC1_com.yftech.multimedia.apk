package android.security.keymaster;

import android.os.Parcel;
import java.util.Date;

/* JADX INFO: loaded from: classes2.dex */
class KeymasterDateArgument extends KeymasterArgument {
    public final Date date;

    public KeymasterDateArgument(int i, Parcel parcel) {
        super(i);
        this.date = new Date(parcel.readLong());
    }

    public KeymasterDateArgument(int i, Date date) {
        super(i);
        if (KeymasterDefs.getTagType(i) == 1610612736) {
            this.date = date;
            return;
        }
        throw new IllegalArgumentException("Bad date tag " + i);
    }

    @Override // android.security.keymaster.KeymasterArgument
    public void writeValue(Parcel parcel) {
        parcel.writeLong(this.date.getTime());
    }
}
