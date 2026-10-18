package android.service.gatekeeper;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.annotations.VisibleForTesting;

/* JADX INFO: loaded from: classes2.dex */
public final class GateKeeperResponse implements Parcelable {
    public static final Parcelable.Creator<GateKeeperResponse> CREATOR = new Parcelable.Creator<GateKeeperResponse>() { // from class: android.service.gatekeeper.GateKeeperResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GateKeeperResponse createFromParcel(Parcel parcel) {
            int i = parcel.readInt();
            if (i == 1) {
                return GateKeeperResponse.createRetryResponse(parcel.readInt());
            }
            if (i != 0) {
                return GateKeeperResponse.createGenericResponse(i);
            }
            boolean z = parcel.readInt() == 1;
            byte[] bArr = null;
            int i2 = parcel.readInt();
            if (i2 > 0) {
                bArr = new byte[i2];
                parcel.readByteArray(bArr);
            }
            return GateKeeperResponse.createOkResponse(bArr, z);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public GateKeeperResponse[] newArray(int i) {
            return new GateKeeperResponse[i];
        }
    };
    public static final int RESPONSE_ERROR = -1;
    public static final int RESPONSE_OK = 0;
    public static final int RESPONSE_RETRY = 1;
    private byte[] mPayload;
    private final int mResponseCode;
    private boolean mShouldReEnroll;
    private int mTimeout;

    private GateKeeperResponse(int i) {
        this.mResponseCode = i;
    }

    @VisibleForTesting
    public static GateKeeperResponse createGenericResponse(int i) {
        return new GateKeeperResponse(i);
    }

    @VisibleForTesting
    public static GateKeeperResponse createOkResponse(byte[] bArr, boolean z) {
        GateKeeperResponse gateKeeperResponse = new GateKeeperResponse(0);
        gateKeeperResponse.mPayload = bArr;
        gateKeeperResponse.mShouldReEnroll = z;
        return gateKeeperResponse;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static GateKeeperResponse createRetryResponse(int i) {
        GateKeeperResponse gateKeeperResponse = new GateKeeperResponse(1);
        gateKeeperResponse.mTimeout = i;
        return gateKeeperResponse;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public byte[] getPayload() {
        return this.mPayload;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }

    public boolean getShouldReEnroll() {
        return this.mShouldReEnroll;
    }

    public int getTimeout() {
        return this.mTimeout;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mResponseCode);
        if (this.mResponseCode == 1) {
            parcel.writeInt(this.mTimeout);
            return;
        }
        if (this.mResponseCode == 0) {
            parcel.writeInt(this.mShouldReEnroll ? 1 : 0);
            if (this.mPayload == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(this.mPayload.length);
                parcel.writeByteArray(this.mPayload);
            }
        }
    }
}
