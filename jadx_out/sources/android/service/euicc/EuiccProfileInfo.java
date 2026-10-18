package android.service.euicc;

import android.annotation.SystemApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.carrier.CarrierIdentifier;
import android.telephony.UiccAccessRule;
import android.text.TextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
@SystemApi
public final class EuiccProfileInfo implements Parcelable {
    public static final Parcelable.Creator<EuiccProfileInfo> CREATOR = new Parcelable.Creator<EuiccProfileInfo>() { // from class: android.service.euicc.EuiccProfileInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EuiccProfileInfo createFromParcel(Parcel parcel) {
            return new EuiccProfileInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public EuiccProfileInfo[] newArray(int i) {
            return new EuiccProfileInfo[i];
        }
    };
    public static final int POLICY_RULE_DELETE_AFTER_DISABLING = 4;
    public static final int POLICY_RULE_DO_NOT_DELETE = 2;
    public static final int POLICY_RULE_DO_NOT_DISABLE = 1;
    public static final int PROFILE_CLASS_OPERATIONAL = 2;
    public static final int PROFILE_CLASS_PROVISIONING = 1;
    public static final int PROFILE_CLASS_TESTING = 0;
    public static final int PROFILE_CLASS_UNSET = -1;
    public static final int PROFILE_STATE_DISABLED = 0;
    public static final int PROFILE_STATE_ENABLED = 1;
    public static final int PROFILE_STATE_UNSET = -1;
    private final UiccAccessRule[] mAccessRules;
    private final CarrierIdentifier mCarrierIdentifier;
    private final String mIccid;
    private final String mNickname;
    private final int mPolicyRules;
    private final int mProfileClass;
    private final String mProfileName;
    private final String mServiceProviderName;
    private final int mState;

    public static final class Builder {
        private List<UiccAccessRule> mAccessRules;
        private CarrierIdentifier mCarrierIdentifier;
        private String mIccid;
        private String mNickname;
        private int mPolicyRules;
        private int mProfileClass;
        private String mProfileName;
        private String mServiceProviderName;
        private int mState;

        public Builder(EuiccProfileInfo euiccProfileInfo) {
            this.mIccid = euiccProfileInfo.mIccid;
            this.mNickname = euiccProfileInfo.mNickname;
            this.mServiceProviderName = euiccProfileInfo.mServiceProviderName;
            this.mProfileName = euiccProfileInfo.mProfileName;
            this.mProfileClass = euiccProfileInfo.mProfileClass;
            this.mState = euiccProfileInfo.mState;
            this.mCarrierIdentifier = euiccProfileInfo.mCarrierIdentifier;
            this.mPolicyRules = euiccProfileInfo.mPolicyRules;
            this.mAccessRules = Arrays.asList(euiccProfileInfo.mAccessRules);
        }

        public Builder(String str) {
            if (TextUtils.isDigitsOnly(str)) {
                this.mIccid = str;
                return;
            }
            throw new IllegalArgumentException("iccid contains invalid characters: " + str);
        }

        public EuiccProfileInfo build() {
            if (this.mIccid != null) {
                return new EuiccProfileInfo(this.mIccid, this.mNickname, this.mServiceProviderName, this.mProfileName, this.mProfileClass, this.mState, this.mCarrierIdentifier, this.mPolicyRules, this.mAccessRules);
            }
            throw new IllegalStateException("ICCID must be set for a profile.");
        }

        public Builder setCarrierIdentifier(CarrierIdentifier carrierIdentifier) {
            this.mCarrierIdentifier = carrierIdentifier;
            return this;
        }

        public Builder setIccid(String str) {
            if (TextUtils.isDigitsOnly(str)) {
                this.mIccid = str;
                return this;
            }
            throw new IllegalArgumentException("iccid contains invalid characters: " + str);
        }

        public Builder setNickname(String str) {
            this.mNickname = str;
            return this;
        }

        public Builder setPolicyRules(int i) {
            this.mPolicyRules = i;
            return this;
        }

        public Builder setProfileClass(int i) {
            this.mProfileClass = i;
            return this;
        }

        public Builder setProfileName(String str) {
            this.mProfileName = str;
            return this;
        }

        public Builder setServiceProviderName(String str) {
            this.mServiceProviderName = str;
            return this;
        }

        public Builder setState(int i) {
            this.mState = i;
            return this;
        }

        public Builder setUiccAccessRule(List<UiccAccessRule> list) {
            this.mAccessRules = list;
            return this;
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PolicyRule {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ProfileClass {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ProfileState {
    }

    private EuiccProfileInfo(Parcel parcel) {
        this.mIccid = parcel.readString();
        this.mNickname = parcel.readString();
        this.mServiceProviderName = parcel.readString();
        this.mProfileName = parcel.readString();
        this.mProfileClass = parcel.readInt();
        this.mState = parcel.readInt();
        if (parcel.readByte() == 1) {
            this.mCarrierIdentifier = CarrierIdentifier.CREATOR.createFromParcel(parcel);
        } else {
            this.mCarrierIdentifier = null;
        }
        this.mPolicyRules = parcel.readInt();
        this.mAccessRules = (UiccAccessRule[]) parcel.createTypedArray(UiccAccessRule.CREATOR);
    }

    private EuiccProfileInfo(String str, String str2, String str3, String str4, int i, int i2, CarrierIdentifier carrierIdentifier, int i3, List<UiccAccessRule> list) {
        this.mIccid = str;
        this.mNickname = str2;
        this.mServiceProviderName = str3;
        this.mProfileName = str4;
        this.mProfileClass = i;
        this.mState = i2;
        this.mCarrierIdentifier = carrierIdentifier;
        this.mPolicyRules = i3;
        if (list == null || list.size() <= 0) {
            this.mAccessRules = null;
        } else {
            this.mAccessRules = (UiccAccessRule[]) list.toArray(new UiccAccessRule[list.size()]);
        }
    }

    @Deprecated
    public EuiccProfileInfo(String str, UiccAccessRule[] uiccAccessRuleArr, String str2) {
        if (!TextUtils.isDigitsOnly(str)) {
            throw new IllegalArgumentException("iccid contains invalid characters: " + str);
        }
        this.mIccid = str;
        this.mAccessRules = uiccAccessRuleArr;
        this.mNickname = str2;
        this.mServiceProviderName = null;
        this.mProfileName = null;
        this.mProfileClass = -1;
        this.mState = -1;
        this.mCarrierIdentifier = null;
        this.mPolicyRules = 0;
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
        EuiccProfileInfo euiccProfileInfo = (EuiccProfileInfo) obj;
        return Objects.equals(this.mIccid, euiccProfileInfo.mIccid) && Objects.equals(this.mNickname, euiccProfileInfo.mNickname) && Objects.equals(this.mServiceProviderName, euiccProfileInfo.mServiceProviderName) && Objects.equals(this.mProfileName, euiccProfileInfo.mProfileName) && this.mProfileClass == euiccProfileInfo.mProfileClass && this.mState == euiccProfileInfo.mState && Objects.equals(this.mCarrierIdentifier, euiccProfileInfo.mCarrierIdentifier) && this.mPolicyRules == euiccProfileInfo.mPolicyRules && Arrays.equals(this.mAccessRules, euiccProfileInfo.mAccessRules);
    }

    public CarrierIdentifier getCarrierIdentifier() {
        return this.mCarrierIdentifier;
    }

    public String getIccid() {
        return this.mIccid;
    }

    public String getNickname() {
        return this.mNickname;
    }

    public int getPolicyRules() {
        return this.mPolicyRules;
    }

    public int getProfileClass() {
        return this.mProfileClass;
    }

    public String getProfileName() {
        return this.mProfileName;
    }

    public String getServiceProviderName() {
        return this.mServiceProviderName;
    }

    public int getState() {
        return this.mState;
    }

    public List<UiccAccessRule> getUiccAccessRules() {
        if (this.mAccessRules == null) {
            return null;
        }
        return Arrays.asList(this.mAccessRules);
    }

    public boolean hasPolicyRule(int i) {
        return (this.mPolicyRules & i) != 0;
    }

    public boolean hasPolicyRules() {
        return this.mPolicyRules != 0;
    }

    public int hashCode() {
        return ((((((((((((((((Objects.hashCode(this.mIccid) + 31) * 31) + Objects.hashCode(this.mNickname)) * 31) + Objects.hashCode(this.mServiceProviderName)) * 31) + Objects.hashCode(this.mProfileName)) * 31) + this.mProfileClass) * 31) + this.mState) * 31) + Objects.hashCode(this.mCarrierIdentifier)) * 31) + this.mPolicyRules) * 31) + Arrays.hashCode(this.mAccessRules);
    }

    public String toString() {
        return "EuiccProfileInfo (nickname=" + this.mNickname + ", serviceProviderName=" + this.mServiceProviderName + ", profileName=" + this.mProfileName + ", profileClass=" + this.mProfileClass + ", state=" + this.mState + ", CarrierIdentifier=" + this.mCarrierIdentifier + ", policyRules=" + this.mPolicyRules + ", accessRules=" + Arrays.toString(this.mAccessRules) + ")";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mIccid);
        parcel.writeString(this.mNickname);
        parcel.writeString(this.mServiceProviderName);
        parcel.writeString(this.mProfileName);
        parcel.writeInt(this.mProfileClass);
        parcel.writeInt(this.mState);
        if (this.mCarrierIdentifier != null) {
            parcel.writeByte((byte) 1);
            this.mCarrierIdentifier.writeToParcel(parcel, i);
        } else {
            parcel.writeByte((byte) 0);
        }
        parcel.writeInt(this.mPolicyRules);
        parcel.writeTypedArray(this.mAccessRules, i);
    }
}
