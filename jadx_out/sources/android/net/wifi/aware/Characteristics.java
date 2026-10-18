package android.net.wifi.aware;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public final class Characteristics implements Parcelable {
    public static final Parcelable.Creator<Characteristics> CREATOR = new Parcelable.Creator<Characteristics>() { // from class: android.net.wifi.aware.Characteristics.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Characteristics createFromParcel(Parcel parcel) {
            return new Characteristics(parcel.readBundle());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Characteristics[] newArray(int i) {
            return new Characteristics[i];
        }
    };
    public static final String KEY_MAX_MATCH_FILTER_LENGTH = "key_max_match_filter_length";
    public static final String KEY_MAX_SERVICE_NAME_LENGTH = "key_max_service_name_length";
    public static final String KEY_MAX_SERVICE_SPECIFIC_INFO_LENGTH = "key_max_service_specific_info_length";
    private Bundle mCharacteristics;

    public Characteristics(Bundle bundle) {
        this.mCharacteristics = new Bundle();
        this.mCharacteristics = bundle;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getMaxMatchFilterLength() {
        return this.mCharacteristics.getInt(KEY_MAX_MATCH_FILTER_LENGTH);
    }

    public int getMaxServiceNameLength() {
        return this.mCharacteristics.getInt(KEY_MAX_SERVICE_NAME_LENGTH);
    }

    public int getMaxServiceSpecificInfoLength() {
        return this.mCharacteristics.getInt(KEY_MAX_SERVICE_SPECIFIC_INFO_LENGTH);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeBundle(this.mCharacteristics);
    }
}
