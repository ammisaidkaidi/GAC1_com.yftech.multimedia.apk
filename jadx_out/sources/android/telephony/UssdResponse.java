package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/* JADX INFO: loaded from: classes2.dex */
public final class UssdResponse implements Parcelable {
    public static final Parcelable.Creator<UssdResponse> CREATOR = new Parcelable.Creator<UssdResponse>() { // from class: android.telephony.UssdResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UssdResponse createFromParcel(Parcel parcel) {
            return new UssdResponse(parcel.readString(), TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public UssdResponse[] newArray(int i) {
            return new UssdResponse[i];
        }
    };
    private CharSequence mReturnMessage;
    private String mUssdRequest;

    public UssdResponse(String str, CharSequence charSequence) {
        this.mUssdRequest = str;
        this.mReturnMessage = charSequence;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public CharSequence getReturnMessage() {
        return this.mReturnMessage;
    }

    public String getUssdRequest() {
        return this.mUssdRequest;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mUssdRequest);
        TextUtils.writeToParcel(this.mReturnMessage, parcel, 0);
    }
}
