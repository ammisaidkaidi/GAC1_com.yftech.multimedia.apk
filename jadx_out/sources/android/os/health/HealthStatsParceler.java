package android.os.health;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes2.dex */
public class HealthStatsParceler implements Parcelable {
    public static final Parcelable.Creator<HealthStatsParceler> CREATOR = new Parcelable.Creator<HealthStatsParceler>() { // from class: android.os.health.HealthStatsParceler.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HealthStatsParceler createFromParcel(Parcel parcel) {
            return new HealthStatsParceler(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public HealthStatsParceler[] newArray(int i) {
            return new HealthStatsParceler[i];
        }
    };
    private HealthStats mHealthStats;
    private HealthStatsWriter mWriter;

    public HealthStatsParceler(Parcel parcel) {
        this.mHealthStats = new HealthStats(parcel);
    }

    public HealthStatsParceler(HealthStatsWriter healthStatsWriter) {
        this.mWriter = healthStatsWriter;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public HealthStats getHealthStats() {
        if (this.mWriter != null) {
            Parcel parcelObtain = Parcel.obtain();
            this.mWriter.flattenToParcel(parcelObtain);
            parcelObtain.setDataPosition(0);
            this.mHealthStats = new HealthStats(parcelObtain);
            parcelObtain.recycle();
        }
        return this.mHealthStats;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.mWriter == null) {
            throw new RuntimeException("Can not re-parcel HealthStatsParceler that was constructed from a Parcel");
        }
        this.mWriter.flattenToParcel(parcel);
    }
}
