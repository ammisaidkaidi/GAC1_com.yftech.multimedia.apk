package com.android.internal.telephony.gsm;

/* JADX INFO: loaded from: classes3.dex */
public final class SmsBroadcastConfigInfo {
    private int mFromCodeScheme;
    private int mFromServiceId;
    private boolean mSelected;
    private int mToCodeScheme;
    private int mToServiceId;

    public SmsBroadcastConfigInfo(int i, int i2, int i3, int i4, boolean z) {
        this.mFromServiceId = i;
        this.mToServiceId = i2;
        this.mFromCodeScheme = i3;
        this.mToCodeScheme = i4;
        this.mSelected = z;
    }

    public int getFromCodeScheme() {
        return this.mFromCodeScheme;
    }

    public int getFromServiceId() {
        return this.mFromServiceId;
    }

    public int getToCodeScheme() {
        return this.mToCodeScheme;
    }

    public int getToServiceId() {
        return this.mToServiceId;
    }

    public boolean isSelected() {
        return this.mSelected;
    }

    public void setFromCodeScheme(int i) {
        this.mFromCodeScheme = i;
    }

    public void setFromServiceId(int i) {
        this.mFromServiceId = i;
    }

    public void setSelected(boolean z) {
        this.mSelected = z;
    }

    public void setToCodeScheme(int i) {
        this.mToCodeScheme = i;
    }

    public void setToServiceId(int i) {
        this.mToServiceId = i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SmsBroadcastConfigInfo: Id [");
        sb.append(this.mFromServiceId);
        sb.append(',');
        sb.append(this.mToServiceId);
        sb.append("] Code [");
        sb.append(this.mFromCodeScheme);
        sb.append(',');
        sb.append(this.mToCodeScheme);
        sb.append("] ");
        sb.append(this.mSelected ? "ENABLED" : "DISABLED");
        return sb.toString();
    }
}
