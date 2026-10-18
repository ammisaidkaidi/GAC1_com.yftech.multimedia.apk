package android.media.audiopolicy;

import android.app.backup.FullBackup;
import android.media.AudioFormat;
import android.media.TtmlUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.SettingsStringUtil;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class AudioPolicyConfig implements Parcelable {
    public static final Parcelable.Creator<AudioPolicyConfig> CREATOR = new Parcelable.Creator<AudioPolicyConfig>() { // from class: android.media.audiopolicy.AudioPolicyConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AudioPolicyConfig createFromParcel(Parcel parcel) {
            return new AudioPolicyConfig(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AudioPolicyConfig[] newArray(int i) {
            return new AudioPolicyConfig[i];
        }
    };
    private static final String TAG = "AudioPolicyConfig";
    protected int mDuckingPolicy;
    private int mMixCounter;
    protected final ArrayList<AudioMix> mMixes;
    private String mRegistrationId;

    protected AudioPolicyConfig(AudioPolicyConfig audioPolicyConfig) {
        this.mDuckingPolicy = 0;
        this.mRegistrationId = null;
        this.mMixCounter = 0;
        this.mMixes = audioPolicyConfig.mMixes;
    }

    private AudioPolicyConfig(Parcel parcel) {
        this.mDuckingPolicy = 0;
        this.mRegistrationId = null;
        this.mMixCounter = 0;
        this.mMixes = new ArrayList<>();
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            AudioMix.Builder builder = new AudioMix.Builder();
            builder.setRouteFlags(parcel.readInt());
            builder.setCallbackFlags(parcel.readInt());
            builder.setDevice(parcel.readInt(), parcel.readString());
            builder.setFormat(new AudioFormat.Builder().setSampleRate(parcel.readInt()).setChannelMask(parcel.readInt()).setEncoding(parcel.readInt()).build());
            int i3 = parcel.readInt();
            AudioMixingRule.Builder builder2 = new AudioMixingRule.Builder();
            for (int i4 = 0; i4 < i3; i4++) {
                builder2.addRuleFromParcel(parcel);
            }
            builder.setMixingRule(builder2.build());
            this.mMixes.add(builder.build());
        }
    }

    AudioPolicyConfig(ArrayList<AudioMix> arrayList) {
        this.mDuckingPolicy = 0;
        this.mRegistrationId = null;
        this.mMixCounter = 0;
        this.mMixes = arrayList;
    }

    private static String mixTypeId(int i) {
        if (i == 0) {
            return TtmlUtils.TAG_P;
        }
        return i == 1 ? FullBackup.ROOT_TREE_TOKEN : "i";
    }

    private void setMixRegistration(AudioMix audioMix) {
        if (this.mRegistrationId.isEmpty()) {
            audioMix.setRegistration("");
        } else if ((audioMix.getRouteFlags() & 2) == 2) {
            audioMix.setRegistration(this.mRegistrationId + "mix" + mixTypeId(audioMix.getMixType()) + SettingsStringUtil.DELIMITER + this.mMixCounter);
        } else if ((audioMix.getRouteFlags() & 1) == 1) {
            audioMix.setRegistration(audioMix.mDeviceAddress);
        }
        this.mMixCounter++;
    }

    @GuardedBy("mMixes")
    protected void add(ArrayList<AudioMix> arrayList) {
        for (AudioMix audioMix : arrayList) {
            setMixRegistration(audioMix);
            this.mMixes.add(audioMix);
        }
    }

    public void addMix(AudioMix audioMix) throws IllegalArgumentException {
        if (audioMix == null) {
            throw new IllegalArgumentException("Illegal null AudioMix argument");
        }
        this.mMixes.add(audioMix);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public ArrayList<AudioMix> getMixes() {
        return this.mMixes;
    }

    protected String getRegistration() {
        return this.mRegistrationId;
    }

    public int hashCode() {
        return Objects.hash(this.mMixes);
    }

    @GuardedBy("mMixes")
    protected void remove(ArrayList<AudioMix> arrayList) {
        Iterator<AudioMix> it = arrayList.iterator();
        while (it.hasNext()) {
            this.mMixes.remove(it.next());
        }
    }

    protected void setRegistration(String str) {
        boolean z = this.mRegistrationId == null || this.mRegistrationId.isEmpty();
        boolean z2 = str == null || str.isEmpty();
        if (z || z2 || this.mRegistrationId.equals(str)) {
            if (str == null) {
                str = "";
            }
            this.mRegistrationId = str;
            Iterator<AudioMix> it = this.mMixes.iterator();
            while (it.hasNext()) {
                setMixRegistration(it.next());
            }
            return;
        }
        Log.e(TAG, "Invalid registration transition from " + this.mRegistrationId + " to " + str);
    }

    public String toLogFriendlyString() {
        String str;
        String str2 = new String("android.media.audiopolicy.AudioPolicyConfig:\n") + this.mMixes.size() + " AudioMix: " + this.mRegistrationId + "\n";
        for (AudioMix audioMix : this.mMixes) {
            str2 = ((((str2 + "* route flags=0x" + Integer.toHexString(audioMix.getRouteFlags()) + "\n") + "  rate=" + audioMix.getFormat().getSampleRate() + "Hz\n") + "  encoding=" + audioMix.getFormat().getEncoding() + "\n") + "  channels=0x") + Integer.toHexString(audioMix.getFormat().getChannelMask()).toUpperCase() + "\n";
            for (AudioMixingRule.AudioMixMatchCriterion audioMixMatchCriterion : audioMix.getRule().getCriteria()) {
                switch (audioMixMatchCriterion.mRule) {
                    case 1:
                        str = (str2 + "  match usage ") + audioMixMatchCriterion.mAttr.usageToString();
                        break;
                    case 2:
                        str = (str2 + "  match capture preset ") + audioMixMatchCriterion.mAttr.getCapturePreset();
                        break;
                    case 4:
                        str = (str2 + "  match UID ") + audioMixMatchCriterion.mIntProp;
                        break;
                    case 32769:
                        str = (str2 + "  exclude usage ") + audioMixMatchCriterion.mAttr.usageToString();
                        break;
                    case 32770:
                        str = (str2 + "  exclude capture preset ") + audioMixMatchCriterion.mAttr.getCapturePreset();
                        break;
                    case 32772:
                        str = (str2 + "  exclude UID ") + audioMixMatchCriterion.mIntProp;
                        break;
                    default:
                        str = str2 + "invalid rule!";
                        break;
                }
                str2 = str + "\n";
            }
        }
        return str2;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mMixes.size());
        for (AudioMix audioMix : this.mMixes) {
            parcel.writeInt(audioMix.getRouteFlags());
            parcel.writeInt(audioMix.mCallbackFlags);
            parcel.writeInt(audioMix.mDeviceSystemType);
            parcel.writeString(audioMix.mDeviceAddress);
            parcel.writeInt(audioMix.getFormat().getSampleRate());
            parcel.writeInt(audioMix.getFormat().getEncoding());
            parcel.writeInt(audioMix.getFormat().getChannelMask());
            ArrayList<AudioMixingRule.AudioMixMatchCriterion> criteria = audioMix.getRule().getCriteria();
            parcel.writeInt(criteria.size());
            Iterator<AudioMixingRule.AudioMixMatchCriterion> it = criteria.iterator();
            while (it.hasNext()) {
                it.next().writeToParcel(parcel);
            }
        }
    }
}
