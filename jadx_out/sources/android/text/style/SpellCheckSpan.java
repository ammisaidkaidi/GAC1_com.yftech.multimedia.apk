package android.text.style;

import android.os.Parcel;
import android.text.ParcelableSpan;

/* JADX INFO: loaded from: classes2.dex */
public class SpellCheckSpan implements ParcelableSpan {
    private boolean mSpellCheckInProgress;

    public SpellCheckSpan() {
        this.mSpellCheckInProgress = false;
    }

    public SpellCheckSpan(Parcel parcel) {
        this.mSpellCheckInProgress = parcel.readInt() != 0;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 20;
    }

    public boolean isSpellCheckInProgress() {
        return this.mSpellCheckInProgress;
    }

    public void setSpellCheckInProgress(boolean z) {
        this.mSpellCheckInProgress = z;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        writeToParcelInternal(parcel, i);
    }

    @Override // android.text.ParcelableSpan
    public void writeToParcelInternal(Parcel parcel, int i) {
        parcel.writeInt(this.mSpellCheckInProgress ? 1 : 0);
    }
}
