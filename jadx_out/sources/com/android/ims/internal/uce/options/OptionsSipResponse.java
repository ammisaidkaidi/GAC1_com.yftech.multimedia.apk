package com.android.ims.internal.uce.options;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
public class OptionsSipResponse implements Parcelable {
    public static final Parcelable.Creator<OptionsSipResponse> CREATOR = new Parcelable.Creator<OptionsSipResponse>() { // from class: com.android.ims.internal.uce.options.OptionsSipResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public OptionsSipResponse createFromParcel(Parcel parcel) {
            return new OptionsSipResponse(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public OptionsSipResponse[] newArray(int i) {
            return new OptionsSipResponse[i];
        }
    };
    private OptionsCmdId mCmdId;
    private String mReasonPhrase;
    private int mRequestId;
    private int mRetryAfter;
    private int mSipResponseCode;

    public OptionsSipResponse() {
        this.mRequestId = 0;
        this.mSipResponseCode = 0;
        this.mRetryAfter = 0;
        this.mReasonPhrase = "";
        this.mCmdId = new OptionsCmdId();
    }

    private OptionsSipResponse(Parcel parcel) {
        this.mRequestId = 0;
        this.mSipResponseCode = 0;
        this.mRetryAfter = 0;
        this.mReasonPhrase = "";
        readFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public OptionsCmdId getCmdId() {
        return this.mCmdId;
    }

    public String getReasonPhrase() {
        return this.mReasonPhrase;
    }

    public int getRequestId() {
        return this.mRequestId;
    }

    public int getRetryAfter() {
        return this.mRetryAfter;
    }

    public int getSipResponseCode() {
        return this.mSipResponseCode;
    }

    public void readFromParcel(Parcel parcel) {
        this.mRequestId = parcel.readInt();
        this.mSipResponseCode = parcel.readInt();
        this.mReasonPhrase = parcel.readString();
        this.mCmdId = (OptionsCmdId) parcel.readParcelable(OptionsCmdId.class.getClassLoader());
        this.mRetryAfter = parcel.readInt();
    }

    public void setCmdId(OptionsCmdId optionsCmdId) {
        this.mCmdId = optionsCmdId;
    }

    public void setReasonPhrase(String str) {
        this.mReasonPhrase = str;
    }

    public void setRequestId(int i) {
        this.mRequestId = i;
    }

    public void setRetryAfter(int i) {
        this.mRetryAfter = i;
    }

    public void setSipResponseCode(int i) {
        this.mSipResponseCode = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mRequestId);
        parcel.writeInt(this.mSipResponseCode);
        parcel.writeString(this.mReasonPhrase);
        parcel.writeParcelable(this.mCmdId, i);
        parcel.writeInt(this.mRetryAfter);
    }
}
