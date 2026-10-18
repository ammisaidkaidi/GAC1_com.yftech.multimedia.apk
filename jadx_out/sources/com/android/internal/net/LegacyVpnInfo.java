package com.android.internal.net;

import android.app.PendingIntent;
import android.net.NetworkInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/* JADX INFO: loaded from: classes3.dex */
public class LegacyVpnInfo implements Parcelable {
    public static final Parcelable.Creator<LegacyVpnInfo> CREATOR = new Parcelable.Creator<LegacyVpnInfo>() { // from class: com.android.internal.net.LegacyVpnInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LegacyVpnInfo createFromParcel(Parcel parcel) {
            LegacyVpnInfo legacyVpnInfo = new LegacyVpnInfo();
            legacyVpnInfo.key = parcel.readString();
            legacyVpnInfo.state = parcel.readInt();
            legacyVpnInfo.intent = (PendingIntent) parcel.readParcelable(null);
            return legacyVpnInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public LegacyVpnInfo[] newArray(int i) {
            return new LegacyVpnInfo[i];
        }
    };
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_FAILED = 5;
    public static final int STATE_INITIALIZING = 1;
    public static final int STATE_TIMEOUT = 4;
    private static final String TAG = "LegacyVpnInfo";
    public PendingIntent intent;
    public String key;
    public int state = -1;

    public static int stateFromNetworkInfo(NetworkInfo networkInfo) {
        switch (networkInfo.getDetailedState()) {
            case CONNECTING:
                return 2;
            case CONNECTED:
                return 3;
            case DISCONNECTED:
                return 0;
            case FAILED:
                return 5;
            default:
                Log.w(TAG, "Unhandled state " + networkInfo.getDetailedState() + " ; treating as disconnected");
                return 0;
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.key);
        parcel.writeInt(this.state);
        parcel.writeParcelable(this.intent, i);
    }
}
