package android.media;

/* JADX INFO: loaded from: classes.dex */
public class AudioDevicePort extends AudioPort {
    private final String mAddress;
    private final int mType;

    AudioDevicePort(AudioHandle audioHandle, String str, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, AudioGain[] audioGainArr, int i, String str2) {
        super(audioHandle, AudioManager.isInputDevice(i) ? 1 : 2, str, iArr, iArr2, iArr3, iArr4, audioGainArr);
        this.mType = i;
        this.mAddress = str2;
    }

    public String address() {
        return this.mAddress;
    }

    @Override // android.media.AudioPort
    public AudioDevicePortConfig buildConfig(int i, int i2, int i3, AudioGainConfig audioGainConfig) {
        return new AudioDevicePortConfig(this, i, i2, i3, audioGainConfig);
    }

    @Override // android.media.AudioPort
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AudioDevicePort)) {
            return false;
        }
        AudioDevicePort audioDevicePort = (AudioDevicePort) obj;
        if (this.mType == audioDevicePort.type() && this.mAddress != null && this.mAddress.equals(audioDevicePort.address())) {
            return super.equals(obj);
        }
        return false;
    }

    @Override // android.media.AudioPort
    public int hashCode() {
        return super.hashCode();
    }

    @Override // android.media.AudioPort
    public String toString() {
        return "{" + super.toString() + ", mType: " + (this.mRole == 1 ? AudioSystem.getInputDeviceName(this.mType) : AudioSystem.getOutputDeviceName(this.mType)) + ", mAddress: " + this.mAddress + "}";
    }

    public int type() {
        return this.mType;
    }
}
