package android.service.euicc;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.euicc.DownloadableSubscription;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class GetDownloadableSubscriptionMetadataResult implements Parcelable {
    public static final Parcelable.Creator<GetDownloadableSubscriptionMetadataResult> CREATOR = new Parcelable.Creator<GetDownloadableSubscriptionMetadataResult>() { // from class: android.service.euicc.GetDownloadableSubscriptionMetadataResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GetDownloadableSubscriptionMetadataResult createFromParcel(Parcel parcel) {
            return new GetDownloadableSubscriptionMetadataResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GetDownloadableSubscriptionMetadataResult[] newArray(int i) {
            return new GetDownloadableSubscriptionMetadataResult[i];
        }
    };
    private final DownloadableSubscription mSubscription;

    @Deprecated
    public final int result;

    public GetDownloadableSubscriptionMetadataResult(int i, DownloadableSubscription downloadableSubscription) {
        this.result = i;
        if (this.result == 0) {
            this.mSubscription = downloadableSubscription;
        } else {
            if (downloadableSubscription == null) {
                this.mSubscription = null;
                return;
            }
            throw new IllegalArgumentException("Error result with non-null subscription: " + i);
        }
    }

    private GetDownloadableSubscriptionMetadataResult(Parcel parcel) {
        this.result = parcel.readInt();
        this.mSubscription = (DownloadableSubscription) parcel.readTypedObject(DownloadableSubscription.CREATOR);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public DownloadableSubscription getDownloadableSubscription() {
        return this.mSubscription;
    }

    public int getResult() {
        return this.result;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.result);
        parcel.writeTypedObject(this.mSubscription, i);
    }
}
