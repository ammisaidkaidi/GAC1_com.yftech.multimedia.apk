package com.android.ims.internal.uce.common;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
public class CapInfo implements Parcelable {
    public static final Parcelable.Creator<CapInfo> CREATOR = new Parcelable.Creator<CapInfo>() { // from class: com.android.ims.internal.uce.common.CapInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CapInfo createFromParcel(Parcel parcel) {
            return new CapInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public CapInfo[] newArray(int i) {
            return new CapInfo[i];
        }
    };
    private long mCapTimestamp;
    private boolean mCdViaPresenceSupported;
    private String[] mExts;
    private boolean mFtHttpSupported;
    private boolean mFtSnFSupported;
    private boolean mFtSupported;
    private boolean mFtThumbSupported;
    private boolean mFullSnFGroupChatSupported;
    private boolean mGeoPullFtSupported;
    private boolean mGeoPullSupported;
    private boolean mGeoPushSupported;
    private boolean mImSupported;
    private boolean mIpVideoSupported;
    private boolean mIpVoiceSupported;
    private boolean mIsSupported;
    private boolean mRcsIpVideoCallSupported;
    private boolean mRcsIpVideoOnlyCallSupported;
    private boolean mRcsIpVoiceCallSupported;
    private boolean mSmSupported;
    private boolean mSpSupported;
    private boolean mVsDuringCSSupported;
    private boolean mVsSupported;

    public CapInfo() {
        this.mImSupported = false;
        this.mFtSupported = false;
        this.mFtThumbSupported = false;
        this.mFtSnFSupported = false;
        this.mFtHttpSupported = false;
        this.mIsSupported = false;
        this.mVsDuringCSSupported = false;
        this.mVsSupported = false;
        this.mSpSupported = false;
        this.mCdViaPresenceSupported = false;
        this.mIpVoiceSupported = false;
        this.mIpVideoSupported = false;
        this.mGeoPullFtSupported = false;
        this.mGeoPullSupported = false;
        this.mGeoPushSupported = false;
        this.mSmSupported = false;
        this.mFullSnFGroupChatSupported = false;
        this.mRcsIpVoiceCallSupported = false;
        this.mRcsIpVideoCallSupported = false;
        this.mRcsIpVideoOnlyCallSupported = false;
        this.mExts = new String[10];
        this.mCapTimestamp = 0L;
    }

    private CapInfo(Parcel parcel) {
        this.mImSupported = false;
        this.mFtSupported = false;
        this.mFtThumbSupported = false;
        this.mFtSnFSupported = false;
        this.mFtHttpSupported = false;
        this.mIsSupported = false;
        this.mVsDuringCSSupported = false;
        this.mVsSupported = false;
        this.mSpSupported = false;
        this.mCdViaPresenceSupported = false;
        this.mIpVoiceSupported = false;
        this.mIpVideoSupported = false;
        this.mGeoPullFtSupported = false;
        this.mGeoPullSupported = false;
        this.mGeoPushSupported = false;
        this.mSmSupported = false;
        this.mFullSnFGroupChatSupported = false;
        this.mRcsIpVoiceCallSupported = false;
        this.mRcsIpVideoCallSupported = false;
        this.mRcsIpVideoOnlyCallSupported = false;
        this.mExts = new String[10];
        this.mCapTimestamp = 0L;
        readFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public long getCapTimestamp() {
        return this.mCapTimestamp;
    }

    public String[] getExts() {
        return this.mExts;
    }

    public boolean isCdViaPresenceSupported() {
        return this.mCdViaPresenceSupported;
    }

    public boolean isFtHttpSupported() {
        return this.mFtHttpSupported;
    }

    public boolean isFtSnFSupported() {
        return this.mFtSnFSupported;
    }

    public boolean isFtSupported() {
        return this.mFtSupported;
    }

    public boolean isFtThumbSupported() {
        return this.mFtThumbSupported;
    }

    public boolean isFullSnFGroupChatSupported() {
        return this.mFullSnFGroupChatSupported;
    }

    public boolean isGeoPullFtSupported() {
        return this.mGeoPullFtSupported;
    }

    public boolean isGeoPullSupported() {
        return this.mGeoPullSupported;
    }

    public boolean isGeoPushSupported() {
        return this.mGeoPushSupported;
    }

    public boolean isImSupported() {
        return this.mImSupported;
    }

    public boolean isIpVideoSupported() {
        return this.mIpVideoSupported;
    }

    public boolean isIpVoiceSupported() {
        return this.mIpVoiceSupported;
    }

    public boolean isIsSupported() {
        return this.mIsSupported;
    }

    public boolean isRcsIpVideoCallSupported() {
        return this.mRcsIpVideoCallSupported;
    }

    public boolean isRcsIpVideoOnlyCallSupported() {
        return this.mRcsIpVideoOnlyCallSupported;
    }

    public boolean isRcsIpVoiceCallSupported() {
        return this.mRcsIpVoiceCallSupported;
    }

    public boolean isSmSupported() {
        return this.mSmSupported;
    }

    public boolean isSpSupported() {
        return this.mSpSupported;
    }

    public boolean isVsDuringCSSupported() {
        return this.mVsDuringCSSupported;
    }

    public boolean isVsSupported() {
        return this.mVsSupported;
    }

    public void readFromParcel(Parcel parcel) {
        this.mImSupported = parcel.readInt() != 0;
        this.mFtSupported = parcel.readInt() != 0;
        this.mFtThumbSupported = parcel.readInt() != 0;
        this.mFtSnFSupported = parcel.readInt() != 0;
        this.mFtHttpSupported = parcel.readInt() != 0;
        this.mIsSupported = parcel.readInt() != 0;
        this.mVsDuringCSSupported = parcel.readInt() != 0;
        this.mVsSupported = parcel.readInt() != 0;
        this.mSpSupported = parcel.readInt() != 0;
        this.mCdViaPresenceSupported = parcel.readInt() != 0;
        this.mIpVoiceSupported = parcel.readInt() != 0;
        this.mIpVideoSupported = parcel.readInt() != 0;
        this.mGeoPullFtSupported = parcel.readInt() != 0;
        this.mGeoPullSupported = parcel.readInt() != 0;
        this.mGeoPushSupported = parcel.readInt() != 0;
        this.mSmSupported = parcel.readInt() != 0;
        this.mFullSnFGroupChatSupported = parcel.readInt() != 0;
        this.mRcsIpVoiceCallSupported = parcel.readInt() != 0;
        this.mRcsIpVideoCallSupported = parcel.readInt() != 0;
        this.mRcsIpVideoOnlyCallSupported = parcel.readInt() != 0;
        this.mExts = parcel.createStringArray();
        this.mCapTimestamp = parcel.readLong();
    }

    public void setCapTimestamp(long j) {
        this.mCapTimestamp = j;
    }

    public void setCdViaPresenceSupported(boolean z) {
        this.mCdViaPresenceSupported = z;
    }

    public void setExts(String[] strArr) {
        this.mExts = strArr;
    }

    public void setFtHttpSupported(boolean z) {
        this.mFtHttpSupported = z;
    }

    public void setFtSnFSupported(boolean z) {
        this.mFtSnFSupported = z;
    }

    public void setFtSupported(boolean z) {
        this.mFtSupported = z;
    }

    public void setFtThumbSupported(boolean z) {
        this.mFtThumbSupported = z;
    }

    public void setFullSnFGroupChatSupported(boolean z) {
        this.mFullSnFGroupChatSupported = z;
    }

    public void setGeoPullFtSupported(boolean z) {
        this.mGeoPullFtSupported = z;
    }

    public void setGeoPullSupported(boolean z) {
        this.mGeoPullSupported = z;
    }

    public void setGeoPushSupported(boolean z) {
        this.mGeoPushSupported = z;
    }

    public void setImSupported(boolean z) {
        this.mImSupported = z;
    }

    public void setIpVideoSupported(boolean z) {
        this.mIpVideoSupported = z;
    }

    public void setIpVoiceSupported(boolean z) {
        this.mIpVoiceSupported = z;
    }

    public void setIsSupported(boolean z) {
        this.mIsSupported = z;
    }

    public void setRcsIpVideoCallSupported(boolean z) {
        this.mRcsIpVideoCallSupported = z;
    }

    public void setRcsIpVideoOnlyCallSupported(boolean z) {
        this.mRcsIpVideoOnlyCallSupported = z;
    }

    public void setRcsIpVoiceCallSupported(boolean z) {
        this.mRcsIpVoiceCallSupported = z;
    }

    public void setSmSupported(boolean z) {
        this.mSmSupported = z;
    }

    public void setSpSupported(boolean z) {
        this.mSpSupported = z;
    }

    public void setVsDuringCSSupported(boolean z) {
        this.mVsDuringCSSupported = z;
    }

    public void setVsSupported(boolean z) {
        this.mVsSupported = z;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mImSupported ? 1 : 0);
        parcel.writeInt(this.mFtSupported ? 1 : 0);
        parcel.writeInt(this.mFtThumbSupported ? 1 : 0);
        parcel.writeInt(this.mFtSnFSupported ? 1 : 0);
        parcel.writeInt(this.mFtHttpSupported ? 1 : 0);
        parcel.writeInt(this.mIsSupported ? 1 : 0);
        parcel.writeInt(this.mVsDuringCSSupported ? 1 : 0);
        parcel.writeInt(this.mVsSupported ? 1 : 0);
        parcel.writeInt(this.mSpSupported ? 1 : 0);
        parcel.writeInt(this.mCdViaPresenceSupported ? 1 : 0);
        parcel.writeInt(this.mIpVoiceSupported ? 1 : 0);
        parcel.writeInt(this.mIpVideoSupported ? 1 : 0);
        parcel.writeInt(this.mGeoPullFtSupported ? 1 : 0);
        parcel.writeInt(this.mGeoPullSupported ? 1 : 0);
        parcel.writeInt(this.mGeoPushSupported ? 1 : 0);
        parcel.writeInt(this.mSmSupported ? 1 : 0);
        parcel.writeInt(this.mFullSnFGroupChatSupported ? 1 : 0);
        parcel.writeInt(this.mRcsIpVoiceCallSupported ? 1 : 0);
        parcel.writeInt(this.mRcsIpVideoCallSupported ? 1 : 0);
        parcel.writeInt(this.mRcsIpVideoOnlyCallSupported ? 1 : 0);
        parcel.writeStringArray(this.mExts);
        parcel.writeLong(this.mCapTimestamp);
    }
}
