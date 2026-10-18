package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.content.NativeLibraryHelper;

/* JADX INFO: loaded from: classes2.dex */
public class NeighboringCellInfo implements Parcelable {
    public static final Parcelable.Creator<NeighboringCellInfo> CREATOR = new Parcelable.Creator<NeighboringCellInfo>() { // from class: android.telephony.NeighboringCellInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NeighboringCellInfo createFromParcel(Parcel parcel) {
            return new NeighboringCellInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NeighboringCellInfo[] newArray(int i) {
            return new NeighboringCellInfo[i];
        }
    };
    public static final int UNKNOWN_CID = -1;
    public static final int UNKNOWN_RSSI = 99;
    private int mCid;
    private int mLac;
    private int mNetworkType;
    private int mPsc;
    private int mRssi;

    @Deprecated
    public NeighboringCellInfo() {
        this.mRssi = 99;
        this.mLac = -1;
        this.mCid = -1;
        this.mPsc = -1;
        this.mNetworkType = 0;
    }

    @Deprecated
    public NeighboringCellInfo(int i, int i2) {
        this.mRssi = i;
        this.mCid = i2;
    }

    /*  JADX ERROR: UnsupportedOperationException in pass: RegionMakerVisitor
        java.lang.UnsupportedOperationException
        	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1092)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker$1.leaveRegion(SwitchRegionMaker.java:419)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:91)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:31)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaksForCase(SwitchRegionMaker.java:399)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaks(SwitchRegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.leaveRegion(PostProcessRegions.java:31)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:91)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.process(PostProcessRegions.java:21)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:31)
        */
    public NeighboringCellInfo(int r7, java.lang.String r8, int r9) {
        /*
            r6 = this;
            r6.<init>()
            r6.mRssi = r7
            r7 = 0
            r6.mNetworkType = r7
            r0 = -1
            r6.mPsc = r0
            r6.mLac = r0
            r6.mCid = r0
            int r1 = r8.length()
            r2 = 8
            if (r1 <= r2) goto L18
            return
        L18:
            if (r1 >= r2) goto L35
            r3 = r8
            r8 = r7
        L1c:
            int r4 = 8 - r1
            if (r8 >= r4) goto L34
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "0"
            r4.append(r5)
            r4.append(r3)
            java.lang.String r3 = r4.toString()
            int r8 = r8 + 1
            goto L1c
        L34:
            r8 = r3
        L35:
            r1 = 16
            switch(r9) {
                case 1: goto L47;
                case 2: goto L47;
                case 3: goto L3e;
                default: goto L3a;
            }
        L3a:
            switch(r9) {
                case 8: goto L3e;
                case 9: goto L3e;
                case 10: goto L3e;
                default: goto L3d;
            }
        L3d:
            goto L6f
        L3e:
            r6.mNetworkType = r9     // Catch: java.lang.NumberFormatException -> L67
            int r8 = java.lang.Integer.parseInt(r8, r1)     // Catch: java.lang.NumberFormatException -> L67
            r6.mPsc = r8     // Catch: java.lang.NumberFormatException -> L67
            goto L6f
        L47:
            r6.mNetworkType = r9     // Catch: java.lang.NumberFormatException -> L67
            java.lang.String r9 = "FFFFFFFF"
            boolean r9 = r8.equalsIgnoreCase(r9)     // Catch: java.lang.NumberFormatException -> L67
            if (r9 != 0) goto L6f
            r9 = 4
            java.lang.String r2 = r8.substring(r9)     // Catch: java.lang.NumberFormatException -> L67
            int r2 = java.lang.Integer.parseInt(r2, r1)     // Catch: java.lang.NumberFormatException -> L67
            r6.mCid = r2     // Catch: java.lang.NumberFormatException -> L67
            java.lang.String r8 = r8.substring(r7, r9)     // Catch: java.lang.NumberFormatException -> L67
            int r8 = java.lang.Integer.parseInt(r8, r1)     // Catch: java.lang.NumberFormatException -> L67
            r6.mLac = r8     // Catch: java.lang.NumberFormatException -> L67
            goto L6f
        L67:
            r6.mPsc = r0
            r6.mLac = r0
            r6.mCid = r0
            r6.mNetworkType = r7
        L6f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.telephony.NeighboringCellInfo.<init>(int, java.lang.String, int):void");
    }

    public NeighboringCellInfo(Parcel parcel) {
        this.mRssi = parcel.readInt();
        this.mLac = parcel.readInt();
        this.mCid = parcel.readInt();
        this.mPsc = parcel.readInt();
        this.mNetworkType = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getCid() {
        return this.mCid;
    }

    public int getLac() {
        return this.mLac;
    }

    public int getNetworkType() {
        return this.mNetworkType;
    }

    public int getPsc() {
        return this.mPsc;
    }

    public int getRssi() {
        return this.mRssi;
    }

    @Deprecated
    public void setCid(int i) {
        this.mCid = i;
    }

    @Deprecated
    public void setRssi(int i) {
        this.mRssi = i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (this.mPsc != -1) {
            sb.append(Integer.toHexString(this.mPsc));
            sb.append("@");
            sb.append(this.mRssi == 99 ? NativeLibraryHelper.CLEAR_ABI_OVERRIDE : Integer.valueOf(this.mRssi));
        } else if (this.mLac != -1 && this.mCid != -1) {
            sb.append(Integer.toHexString(this.mLac));
            sb.append(Integer.toHexString(this.mCid));
            sb.append("@");
            sb.append(this.mRssi == 99 ? NativeLibraryHelper.CLEAR_ABI_OVERRIDE : Integer.valueOf(this.mRssi));
        }
        sb.append("]");
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRssi);
        parcel.writeInt(this.mLac);
        parcel.writeInt(this.mCid);
        parcel.writeInt(this.mPsc);
        parcel.writeInt(this.mNetworkType);
    }
}
