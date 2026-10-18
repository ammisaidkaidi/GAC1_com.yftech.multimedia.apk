package com.android.internal.telephony.uicc.asn1;

/* JADX INFO: loaded from: classes3.dex */
public class TagNotFoundException extends Exception {
    private final int mTag;

    public TagNotFoundException(int i) {
        this.mTag = i;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return super.getMessage() + " (tag=" + this.mTag + ")";
    }

    public int getTag() {
        return this.mTag;
    }
}
