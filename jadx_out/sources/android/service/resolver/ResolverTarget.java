package android.service.resolver;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class ResolverTarget implements Parcelable {
    public static final Parcelable.Creator<ResolverTarget> CREATOR = new Parcelable.Creator<ResolverTarget>() { // from class: android.service.resolver.ResolverTarget.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResolverTarget createFromParcel(Parcel parcel) {
            return new ResolverTarget(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ResolverTarget[] newArray(int i) {
            return new ResolverTarget[i];
        }
    };
    private static final String TAG = "ResolverTarget";
    private float mChooserScore;
    private float mLaunchScore;
    private float mRecencyScore;
    private float mSelectProbability;
    private float mTimeSpentScore;

    public ResolverTarget() {
    }

    ResolverTarget(Parcel parcel) {
        this.mRecencyScore = parcel.readFloat();
        this.mTimeSpentScore = parcel.readFloat();
        this.mLaunchScore = parcel.readFloat();
        this.mChooserScore = parcel.readFloat();
        this.mSelectProbability = parcel.readFloat();
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public float getChooserScore() {
        return this.mChooserScore;
    }

    public float getLaunchScore() {
        return this.mLaunchScore;
    }

    public float getRecencyScore() {
        return this.mRecencyScore;
    }

    public float getSelectProbability() {
        return this.mSelectProbability;
    }

    public float getTimeSpentScore() {
        return this.mTimeSpentScore;
    }

    public void setChooserScore(float f) {
        this.mChooserScore = f;
    }

    public void setLaunchScore(float f) {
        this.mLaunchScore = f;
    }

    public void setRecencyScore(float f) {
        this.mRecencyScore = f;
    }

    public void setSelectProbability(float f) {
        this.mSelectProbability = f;
    }

    public void setTimeSpentScore(float f) {
        this.mTimeSpentScore = f;
    }

    public String toString() {
        return "ResolverTarget{" + this.mRecencyScore + ", " + this.mTimeSpentScore + ", " + this.mLaunchScore + ", " + this.mChooserScore + ", " + this.mSelectProbability + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.mRecencyScore);
        parcel.writeFloat(this.mTimeSpentScore);
        parcel.writeFloat(this.mLaunchScore);
        parcel.writeFloat(this.mChooserScore);
        parcel.writeFloat(this.mSelectProbability);
    }
}
