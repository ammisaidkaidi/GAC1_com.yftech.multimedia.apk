package android.service.autofill;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class FillRequest implements Parcelable {
    public static final Parcelable.Creator<FillRequest> CREATOR = new Parcelable.Creator<FillRequest>() { // from class: android.service.autofill.FillRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public FillRequest createFromParcel(Parcel parcel) {
            return new FillRequest(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public FillRequest[] newArray(int i) {
            return new FillRequest[i];
        }
    };
    public static final int FLAG_MANUAL_REQUEST = 1;
    public static final int INVALID_REQUEST_ID = Integer.MIN_VALUE;
    private final Bundle mClientState;
    private final ArrayList<FillContext> mContexts;
    private final int mFlags;
    private final int mId;

    @Retention(RetentionPolicy.SOURCE)
    @interface RequestFlags {
    }

    public FillRequest(int i, ArrayList<FillContext> arrayList, Bundle bundle, int i2) {
        this.mId = i;
        this.mFlags = Preconditions.checkFlagsArgument(i2, 1);
        this.mContexts = (ArrayList) Preconditions.checkCollectionElementsNotNull(arrayList, "contexts");
        this.mClientState = bundle;
    }

    private FillRequest(Parcel parcel) {
        this.mId = parcel.readInt();
        this.mContexts = new ArrayList<>();
        parcel.readParcelableList(this.mContexts, null);
        this.mClientState = parcel.readBundle();
        this.mFlags = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public Bundle getClientState() {
        return this.mClientState;
    }

    public List<FillContext> getFillContexts() {
        return this.mContexts;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getId() {
        return this.mId;
    }

    public String toString() {
        return "FillRequest: [id=" + this.mId + ", flags=" + this.mFlags + ", ctxts= " + this.mContexts + "]";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mId);
        parcel.writeParcelableList(this.mContexts, i);
        parcel.writeBundle(this.mClientState);
        parcel.writeInt(this.mFlags);
    }
}
