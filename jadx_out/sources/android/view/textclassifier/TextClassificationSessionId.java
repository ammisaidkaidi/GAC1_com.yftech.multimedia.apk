package android.view.textclassifier;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.Locale;
import java.util.UUID;

/* JADX INFO: loaded from: classes2.dex */
public final class TextClassificationSessionId implements Parcelable {
    public static final Parcelable.Creator<TextClassificationSessionId> CREATOR = new Parcelable.Creator<TextClassificationSessionId>() { // from class: android.view.textclassifier.TextClassificationSessionId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextClassificationSessionId createFromParcel(Parcel parcel) {
            return new TextClassificationSessionId((String) Preconditions.checkNotNull(parcel.readString()));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public TextClassificationSessionId[] newArray(int i) {
            return new TextClassificationSessionId[i];
        }
    };
    private final String mValue;

    public TextClassificationSessionId() {
        this(UUID.randomUUID().toString());
    }

    public TextClassificationSessionId(String str) {
        this.mValue = str;
    }

    public static TextClassificationSessionId unflattenFromString(String str) {
        return new TextClassificationSessionId(str);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj != null && getClass() == obj.getClass() && this.mValue.equals(((TextClassificationSessionId) obj).mValue);
    }

    public String flattenToString() {
        return this.mValue;
    }

    public int hashCode() {
        return 31 + this.mValue.hashCode();
    }

    public String toString() {
        return String.format(Locale.US, "TextClassificationSessionId {%s}", this.mValue);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mValue);
    }
}
