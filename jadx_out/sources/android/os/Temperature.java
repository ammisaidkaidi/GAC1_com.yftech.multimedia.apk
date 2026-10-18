package android.os;

/* JADX INFO: loaded from: classes2.dex */
public class Temperature implements Parcelable {
    public static final Parcelable.Creator<Temperature> CREATOR = new Parcelable.Creator<Temperature>() { // from class: android.os.Temperature.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Temperature createFromParcel(Parcel parcel) {
            return new Temperature(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Temperature[] newArray(int i) {
            return new Temperature[i];
        }
    };
    private int mType;
    private float mValue;

    public Temperature() {
        this(-3.4028235E38f, Integer.MIN_VALUE);
    }

    public Temperature(float f, int i) {
        this.mValue = f;
        this.mType = i;
    }

    private Temperature(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int getType() {
        return this.mType;
    }

    public float getValue() {
        return this.mValue;
    }

    public void readFromParcel(Parcel parcel) {
        this.mValue = parcel.readFloat();
        this.mType = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.mValue);
        parcel.writeInt(this.mType);
    }
}
