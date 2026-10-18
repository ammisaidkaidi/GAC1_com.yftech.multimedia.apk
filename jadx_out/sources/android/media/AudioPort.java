package android.media;

import android.security.keystore.KeyProperties;

/* JADX INFO: loaded from: classes.dex */
public class AudioPort {
    public static final int ROLE_NONE = 0;
    public static final int ROLE_SINK = 2;
    public static final int ROLE_SOURCE = 1;
    private static final String TAG = "AudioPort";
    public static final int TYPE_DEVICE = 1;
    public static final int TYPE_NONE = 0;
    public static final int TYPE_SESSION = 3;
    public static final int TYPE_SUBMIX = 2;
    private AudioPortConfig mActiveConfig;
    private final int[] mChannelIndexMasks;
    private final int[] mChannelMasks;
    private final int[] mFormats;
    private final AudioGain[] mGains;
    AudioHandle mHandle;
    private final String mName;
    protected final int mRole;
    private final int[] mSamplingRates;

    AudioPort(AudioHandle audioHandle, int i, String str, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, AudioGain[] audioGainArr) {
        this.mHandle = audioHandle;
        this.mRole = i;
        this.mName = str;
        this.mSamplingRates = iArr;
        this.mChannelMasks = iArr2;
        this.mChannelIndexMasks = iArr3;
        this.mFormats = iArr4;
        this.mGains = audioGainArr;
    }

    public AudioPortConfig activeConfig() {
        return this.mActiveConfig;
    }

    public AudioPortConfig buildConfig(int i, int i2, int i3, AudioGainConfig audioGainConfig) {
        return new AudioPortConfig(this, i, i2, i3, audioGainConfig);
    }

    public int[] channelIndexMasks() {
        return this.mChannelIndexMasks;
    }

    public int[] channelMasks() {
        return this.mChannelMasks;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AudioPort)) {
            return false;
        }
        return this.mHandle.equals(((AudioPort) obj).handle());
    }

    public int[] formats() {
        return this.mFormats;
    }

    AudioGain gain(int i) {
        if (i < 0 || i >= this.mGains.length) {
            return null;
        }
        return this.mGains[i];
    }

    public AudioGain[] gains() {
        return this.mGains;
    }

    AudioHandle handle() {
        return this.mHandle;
    }

    public int hashCode() {
        return this.mHandle.hashCode();
    }

    public int id() {
        return this.mHandle.id();
    }

    public String name() {
        return this.mName;
    }

    public int role() {
        return this.mRole;
    }

    public int[] samplingRates() {
        return this.mSamplingRates;
    }

    public String toString() {
        String string = Integer.toString(this.mRole);
        switch (this.mRole) {
            case 0:
                string = KeyProperties.DIGEST_NONE;
                break;
            case 1:
                string = "SOURCE";
                break;
            case 2:
                string = "SINK";
                break;
        }
        return "{mHandle: " + this.mHandle + ", mRole: " + string + "}";
    }
}
