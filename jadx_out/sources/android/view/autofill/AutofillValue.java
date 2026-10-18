package android.view.autofill;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public final class AutofillValue implements Parcelable {
    public static final Parcelable.Creator<AutofillValue> CREATOR = new Parcelable.Creator<AutofillValue>() { // from class: android.view.autofill.AutofillValue.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AutofillValue createFromParcel(Parcel parcel) {
            return new AutofillValue(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AutofillValue[] newArray(int i) {
            return new AutofillValue[i];
        }
    };
    private final int mType;
    private final Object mValue;

    private AutofillValue(int i, Object obj) {
        this.mType = i;
        this.mValue = obj;
    }

    private AutofillValue(Parcel parcel) {
        this.mType = parcel.readInt();
        switch (this.mType) {
            case 1:
                this.mValue = parcel.readCharSequence();
                return;
            case 2:
                this.mValue = Boolean.valueOf(parcel.readInt() != 0);
                return;
            case 3:
                this.mValue = Integer.valueOf(parcel.readInt());
                return;
            case 4:
                this.mValue = Long.valueOf(parcel.readLong());
                return;
            default:
                throw new IllegalArgumentException("type=" + this.mType + " not valid");
        }
    }

    public static AutofillValue forDate(long j) {
        return new AutofillValue(4, Long.valueOf(j));
    }

    public static AutofillValue forList(int i) {
        return new AutofillValue(3, Integer.valueOf(i));
    }

    public static AutofillValue forText(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        return new AutofillValue(1, TextUtils.trimNoCopySpans(charSequence));
    }

    public static AutofillValue forToggle(boolean z) {
        return new AutofillValue(2, Boolean.valueOf(z));
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AutofillValue autofillValue = (AutofillValue) obj;
        if (this.mType != autofillValue.mType) {
            return false;
        }
        return isText() ? this.mValue.toString().equals(autofillValue.mValue.toString()) : Objects.equals(this.mValue, autofillValue.mValue);
    }

    public long getDateValue() {
        Preconditions.checkState(isDate(), "value must be a date value, not type=" + this.mType);
        return ((Long) this.mValue).longValue();
    }

    public int getListValue() {
        Preconditions.checkState(isList(), "value must be a list value, not type=" + this.mType);
        return ((Integer) this.mValue).intValue();
    }

    public CharSequence getTextValue() {
        Preconditions.checkState(isText(), "value must be a text value, not type=" + this.mType);
        return (CharSequence) this.mValue;
    }

    public boolean getToggleValue() {
        Preconditions.checkState(isToggle(), "value must be a toggle value, not type=" + this.mType);
        return ((Boolean) this.mValue).booleanValue();
    }

    public int hashCode() {
        return this.mType + this.mValue.hashCode();
    }

    public boolean isDate() {
        return this.mType == 4;
    }

    public boolean isEmpty() {
        return isText() && ((CharSequence) this.mValue).length() == 0;
    }

    public boolean isList() {
        return this.mType == 3;
    }

    public boolean isText() {
        return this.mType == 1;
    }

    public boolean isToggle() {
        return this.mType == 2;
    }

    public String toString() {
        if (!Helper.sDebug) {
            return super.toString();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[type=");
        sb.append(this.mType);
        sb.append(", value=");
        if (isText()) {
            Helper.appendRedacted(sb, (CharSequence) this.mValue);
        } else {
            sb.append(this.mValue);
        }
        sb.append(']');
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mType);
        switch (this.mType) {
            case 1:
                parcel.writeCharSequence((CharSequence) this.mValue);
                break;
            case 2:
                parcel.writeInt(((Boolean) this.mValue).booleanValue() ? 1 : 0);
                break;
            case 3:
                parcel.writeInt(((Integer) this.mValue).intValue());
                break;
            case 4:
                parcel.writeLong(((Long) this.mValue).longValue());
                break;
        }
    }
}
