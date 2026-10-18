package android.service.euicc;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class GetEuiccProfileInfoListResult implements Parcelable {
    public static final Parcelable.Creator<GetEuiccProfileInfoListResult> CREATOR = new Parcelable.Creator<GetEuiccProfileInfoListResult>() { // from class: android.service.euicc.GetEuiccProfileInfoListResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GetEuiccProfileInfoListResult createFromParcel(Parcel parcel) {
            return new GetEuiccProfileInfoListResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GetEuiccProfileInfoListResult[] newArray(int i) {
            return new GetEuiccProfileInfoListResult[i];
        }
    };
    private final boolean mIsRemovable;
    private final EuiccProfileInfo[] mProfiles;

    @Deprecated
    public final int result;

    public GetEuiccProfileInfoListResult(int i, EuiccProfileInfo[] euiccProfileInfoArr, boolean z) {
        this.result = i;
        this.mIsRemovable = z;
        if (this.result == 0) {
            this.mProfiles = euiccProfileInfoArr;
        } else {
            if (euiccProfileInfoArr == null) {
                this.mProfiles = null;
                return;
            }
            throw new IllegalArgumentException("Error result with non-null profiles: " + i);
        }
    }

    private GetEuiccProfileInfoListResult(Parcel parcel) {
        this.result = parcel.readInt();
        this.mProfiles = (EuiccProfileInfo[]) parcel.createTypedArray(EuiccProfileInfo.CREATOR);
        this.mIsRemovable = parcel.readBoolean();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean getIsRemovable() {
        return this.mIsRemovable;
    }

    public List<EuiccProfileInfo> getProfiles() {
        if (this.mProfiles == null) {
            return null;
        }
        return Arrays.asList(this.mProfiles);
    }

    public int getResult() {
        return this.result;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.result);
        parcel.writeTypedArray(this.mProfiles, i);
        parcel.writeBoolean(this.mIsRemovable);
    }
}
