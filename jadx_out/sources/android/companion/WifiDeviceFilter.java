package android.companion;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.OneTimeUseBuilder;
import java.util.Objects;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public final class WifiDeviceFilter implements DeviceFilter<ScanResult> {
    public static final Parcelable.Creator<WifiDeviceFilter> CREATOR = new Parcelable.Creator<WifiDeviceFilter>() { // from class: android.companion.WifiDeviceFilter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiDeviceFilter createFromParcel(Parcel parcel) {
            return new WifiDeviceFilter(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WifiDeviceFilter[] newArray(int i) {
            return new WifiDeviceFilter[i];
        }
    };
    private final Pattern mNamePattern;

    public static final class Builder extends OneTimeUseBuilder<WifiDeviceFilter> {
        private Pattern mNamePattern;

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.provider.OneTimeUseBuilder
        public WifiDeviceFilter build() {
            markUsed();
            return new WifiDeviceFilter(this.mNamePattern);
        }

        public Builder setNamePattern(Pattern pattern) {
            checkNotUsed();
            this.mNamePattern = pattern;
            return this;
        }
    }

    @SuppressLint({"ParcelClassLoader"})
    private WifiDeviceFilter(Parcel parcel) {
        this(BluetoothDeviceFilterUtils.patternFromString(parcel.readString()));
    }

    private WifiDeviceFilter(Pattern pattern) {
        this.mNamePattern = pattern;
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
        return Objects.equals(this.mNamePattern, ((WifiDeviceFilter) obj).mNamePattern);
    }

    @Override // android.companion.DeviceFilter
    public String getDeviceDisplayName(ScanResult scanResult) {
        return BluetoothDeviceFilterUtils.getDeviceDisplayNameInternal(scanResult);
    }

    @Override // android.companion.DeviceFilter
    public int getMediumType() {
        return 2;
    }

    public Pattern getNamePattern() {
        return this.mNamePattern;
    }

    public int hashCode() {
        return Objects.hash(this.mNamePattern);
    }

    @Override // android.companion.DeviceFilter
    public boolean matches(ScanResult scanResult) {
        return BluetoothDeviceFilterUtils.matchesName(getNamePattern(), scanResult);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(BluetoothDeviceFilterUtils.patternToString(getNamePattern()));
    }
}
