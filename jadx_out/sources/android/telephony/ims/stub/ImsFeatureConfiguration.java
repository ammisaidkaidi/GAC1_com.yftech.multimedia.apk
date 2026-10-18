package android.telephony.ims.stub;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArraySet;
import java.util.Set;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class ImsFeatureConfiguration implements Parcelable {
    public static final Parcelable.Creator<ImsFeatureConfiguration> CREATOR = new Parcelable.Creator<ImsFeatureConfiguration>() { // from class: android.telephony.ims.stub.ImsFeatureConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ImsFeatureConfiguration createFromParcel(Parcel parcel) {
            return new ImsFeatureConfiguration(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ImsFeatureConfiguration[] newArray(int i) {
            return new ImsFeatureConfiguration[i];
        }
    };
    private final Set<FeatureSlotPair> mFeatures;

    public static class Builder {
        ImsFeatureConfiguration mConfig = new ImsFeatureConfiguration();

        public Builder addFeature(int i, int i2) {
            this.mConfig.addFeature(i, i2);
            return this;
        }

        public ImsFeatureConfiguration build() {
            return this.mConfig;
        }
    }

    public static final class FeatureSlotPair {
        public final int featureType;
        public final int slotId;

        public FeatureSlotPair(int i, int i2) {
            this.slotId = i;
            this.featureType = i2;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            FeatureSlotPair featureSlotPair = (FeatureSlotPair) obj;
            return this.slotId == featureSlotPair.slotId && this.featureType == featureSlotPair.featureType;
        }

        public int hashCode() {
            return (this.slotId * 31) + this.featureType;
        }

        public String toString() {
            return "{s=" + this.slotId + ", f=" + this.featureType + "}";
        }
    }

    public ImsFeatureConfiguration() {
        this.mFeatures = new ArraySet();
    }

    protected ImsFeatureConfiguration(Parcel parcel) {
        int i = parcel.readInt();
        this.mFeatures = new ArraySet(i);
        for (int i2 = 0; i2 < i; i2++) {
            this.mFeatures.add(new FeatureSlotPair(parcel.readInt(), parcel.readInt()));
        }
    }

    public ImsFeatureConfiguration(Set<FeatureSlotPair> set) {
        this.mFeatures = new ArraySet();
        if (set != null) {
            this.mFeatures.addAll(set);
        }
    }

    void addFeature(int i, int i2) {
        this.mFeatures.add(new FeatureSlotPair(i, i2));
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ImsFeatureConfiguration) {
            return this.mFeatures.equals(((ImsFeatureConfiguration) obj).mFeatures);
        }
        return false;
    }

    public Set<FeatureSlotPair> getServiceFeatures() {
        return new ArraySet(this.mFeatures);
    }

    public int hashCode() {
        return this.mFeatures.hashCode();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        FeatureSlotPair[] featureSlotPairArr = new FeatureSlotPair[this.mFeatures.size()];
        this.mFeatures.toArray(featureSlotPairArr);
        parcel.writeInt(featureSlotPairArr.length);
        for (FeatureSlotPair featureSlotPair : featureSlotPairArr) {
            parcel.writeInt(featureSlotPair.slotId);
            parcel.writeInt(featureSlotPair.featureType);
        }
    }
}
