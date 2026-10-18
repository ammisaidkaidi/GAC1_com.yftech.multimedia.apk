package com.android.internal.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class CellNetworkScanResult implements Parcelable {
    public static final Parcelable.Creator<CellNetworkScanResult> CREATOR = new Parcelable.Creator<CellNetworkScanResult>() { // from class: com.android.internal.telephony.CellNetworkScanResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellNetworkScanResult createFromParcel(Parcel parcel) {
            return new CellNetworkScanResult(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellNetworkScanResult[] newArray(int i) {
            return new CellNetworkScanResult[i];
        }
    };
    public static final int STATUS_RADIO_GENERIC_FAILURE = 3;
    public static final int STATUS_RADIO_NOT_AVAILABLE = 2;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_UNKNOWN_ERROR = 4;
    private final List<OperatorInfo> mOperators;
    private final int mStatus;

    public CellNetworkScanResult(int i, List<OperatorInfo> list) {
        this.mStatus = i;
        this.mOperators = list;
    }

    private CellNetworkScanResult(Parcel parcel) {
        this.mStatus = parcel.readInt();
        int i = parcel.readInt();
        if (i <= 0) {
            this.mOperators = null;
            return;
        }
        this.mOperators = new ArrayList();
        for (int i2 = 0; i2 < i; i2++) {
            this.mOperators.add(OperatorInfo.CREATOR.createFromParcel(parcel));
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public List<OperatorInfo> getOperators() {
        return this.mOperators;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CellNetworkScanResult: {");
        stringBuffer.append(" status:");
        stringBuffer.append(this.mStatus);
        if (this.mOperators != null) {
            for (OperatorInfo operatorInfo : this.mOperators) {
                stringBuffer.append(" network:");
                stringBuffer.append(operatorInfo);
            }
        }
        stringBuffer.append("}");
        return stringBuffer.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mStatus);
        if (this.mOperators == null || this.mOperators.size() <= 0) {
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(this.mOperators.size());
        Iterator<OperatorInfo> it = this.mOperators.iterator();
        while (it.hasNext()) {
            it.next().writeToParcel(parcel, i);
        }
    }
}
