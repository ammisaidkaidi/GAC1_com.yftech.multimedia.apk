package android.telephony;

import android.net.wifi.WifiEnterpriseConfig;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public final class CellInfoGsm extends CellInfo implements Parcelable {
    public static final Parcelable.Creator<CellInfoGsm> CREATOR = new Parcelable.Creator<CellInfoGsm>() { // from class: android.telephony.CellInfoGsm.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellInfoGsm createFromParcel(Parcel parcel) {
            parcel.readInt();
            return CellInfoGsm.createFromParcelBody(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CellInfoGsm[] newArray(int i) {
            return new CellInfoGsm[i];
        }
    };
    private static final boolean DBG = false;
    private static final String LOG_TAG = "CellInfoGsm";
    private CellIdentityGsm mCellIdentityGsm;
    private CellSignalStrengthGsm mCellSignalStrengthGsm;

    public CellInfoGsm() {
        this.mCellIdentityGsm = new CellIdentityGsm();
        this.mCellSignalStrengthGsm = new CellSignalStrengthGsm();
    }

    private CellInfoGsm(Parcel parcel) {
        super(parcel);
        this.mCellIdentityGsm = CellIdentityGsm.CREATOR.createFromParcel(parcel);
        this.mCellSignalStrengthGsm = CellSignalStrengthGsm.CREATOR.createFromParcel(parcel);
    }

    public CellInfoGsm(CellInfoGsm cellInfoGsm) {
        super(cellInfoGsm);
        this.mCellIdentityGsm = cellInfoGsm.mCellIdentityGsm.copy();
        this.mCellSignalStrengthGsm = cellInfoGsm.mCellSignalStrengthGsm.copy();
    }

    protected static CellInfoGsm createFromParcelBody(Parcel parcel) {
        return new CellInfoGsm(parcel);
    }

    private static void log(String str) {
        Rlog.w(LOG_TAG, str);
    }

    @Override // android.telephony.CellInfo, android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.telephony.CellInfo
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        try {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) obj;
            return this.mCellIdentityGsm.equals(cellInfoGsm.mCellIdentityGsm) && this.mCellSignalStrengthGsm.equals(cellInfoGsm.mCellSignalStrengthGsm);
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public CellIdentityGsm getCellIdentity() {
        return this.mCellIdentityGsm;
    }

    public CellSignalStrengthGsm getCellSignalStrength() {
        return this.mCellSignalStrengthGsm;
    }

    @Override // android.telephony.CellInfo
    public int hashCode() {
        return super.hashCode() + this.mCellIdentityGsm.hashCode() + this.mCellSignalStrengthGsm.hashCode();
    }

    public void setCellIdentity(CellIdentityGsm cellIdentityGsm) {
        this.mCellIdentityGsm = cellIdentityGsm;
    }

    public void setCellSignalStrength(CellSignalStrengthGsm cellSignalStrengthGsm) {
        this.mCellSignalStrengthGsm = cellSignalStrengthGsm;
    }

    @Override // android.telephony.CellInfo
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("CellInfoGsm:{");
        stringBuffer.append(super.toString());
        stringBuffer.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        stringBuffer.append(this.mCellIdentityGsm);
        stringBuffer.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        stringBuffer.append(this.mCellSignalStrengthGsm);
        stringBuffer.append("}");
        return stringBuffer.toString();
    }

    @Override // android.telephony.CellInfo, android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i, 1);
        this.mCellIdentityGsm.writeToParcel(parcel, i);
        this.mCellSignalStrengthGsm.writeToParcel(parcel, i);
    }
}
