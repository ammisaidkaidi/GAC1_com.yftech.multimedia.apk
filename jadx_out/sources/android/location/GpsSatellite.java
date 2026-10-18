package android.location;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class GpsSatellite {
    float mAzimuth;
    float mElevation;
    boolean mHasAlmanac;
    boolean mHasEphemeris;
    int mPrn;
    float mSnr;
    boolean mUsedInFix;
    boolean mValid;

    GpsSatellite(int i) {
        this.mPrn = i;
    }

    public float getAzimuth() {
        return this.mAzimuth;
    }

    public float getElevation() {
        return this.mElevation;
    }

    public int getPrn() {
        return this.mPrn;
    }

    public float getSnr() {
        return this.mSnr;
    }

    public boolean hasAlmanac() {
        return this.mHasAlmanac;
    }

    public boolean hasEphemeris() {
        return this.mHasEphemeris;
    }

    void setStatus(GpsSatellite gpsSatellite) {
        if (gpsSatellite == null) {
            this.mValid = false;
            return;
        }
        this.mValid = gpsSatellite.mValid;
        this.mHasEphemeris = gpsSatellite.mHasEphemeris;
        this.mHasAlmanac = gpsSatellite.mHasAlmanac;
        this.mUsedInFix = gpsSatellite.mUsedInFix;
        this.mSnr = gpsSatellite.mSnr;
        this.mElevation = gpsSatellite.mElevation;
        this.mAzimuth = gpsSatellite.mAzimuth;
    }

    public boolean usedInFix() {
        return this.mUsedInFix;
    }
}
