package android.media.audiofx;

import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes.dex */
public class PresetReverb extends AudioEffect {
    public static final int PARAM_PRESET = 0;
    public static final short PRESET_LARGEHALL = 5;
    public static final short PRESET_LARGEROOM = 3;
    public static final short PRESET_MEDIUMHALL = 4;
    public static final short PRESET_MEDIUMROOM = 2;
    public static final short PRESET_NONE = 0;
    public static final short PRESET_PLATE = 6;
    public static final short PRESET_SMALLROOM = 1;
    private static final String TAG = "PresetReverb";
    private BaseParameterListener mBaseParamListener;
    private OnParameterChangeListener mParamListener;
    private final Object mParamListenerLock;

    private class BaseParameterListener implements AudioEffect.OnParameterChangeListener {
        private BaseParameterListener() {
        }

        @Override // android.media.audiofx.AudioEffect.OnParameterChangeListener
        public void onParameterChange(AudioEffect audioEffect, int i, byte[] bArr, byte[] bArr2) {
            OnParameterChangeListener onParameterChangeListener;
            synchronized (PresetReverb.this.mParamListenerLock) {
                onParameterChangeListener = PresetReverb.this.mParamListener != null ? PresetReverb.this.mParamListener : null;
            }
            if (onParameterChangeListener != null) {
                int iByteArrayToInt = bArr.length == 4 ? AudioEffect.byteArrayToInt(bArr, 0) : -1;
                short sByteArrayToShort = bArr2.length == 2 ? AudioEffect.byteArrayToShort(bArr2, 0) : (short) -1;
                if (iByteArrayToInt == -1 || sByteArrayToShort == -1) {
                    return;
                }
                onParameterChangeListener.onParameterChange(PresetReverb.this, i, iByteArrayToInt, sByteArrayToShort);
            }
        }
    }

    public interface OnParameterChangeListener {
        void onParameterChange(PresetReverb presetReverb, int i, int i2, short s);
    }

    public static class Settings {
        public short preset;

        public Settings() {
        }

        public Settings(String str) {
            String strNextToken;
            StringTokenizer stringTokenizer = new StringTokenizer(str, "=;");
            stringTokenizer.countTokens();
            if (stringTokenizer.countTokens() != 3) {
                throw new IllegalArgumentException("settings: " + str);
            }
            String strNextToken2 = stringTokenizer.nextToken();
            if (!strNextToken2.equals(PresetReverb.TAG)) {
                throw new IllegalArgumentException("invalid settings for PresetReverb: " + strNextToken2);
            }
            try {
                strNextToken = stringTokenizer.nextToken();
                try {
                    if (strNextToken.equals("preset")) {
                        this.preset = Short.parseShort(stringTokenizer.nextToken());
                        return;
                    }
                    throw new IllegalArgumentException("invalid key name: " + strNextToken);
                } catch (NumberFormatException unused) {
                    throw new IllegalArgumentException("invalid value for key: " + strNextToken);
                }
            } catch (NumberFormatException unused2) {
                strNextToken = strNextToken2;
            }
        }

        public String toString() {
            return new String("PresetReverb;preset=" + Short.toString(this.preset));
        }
    }

    public PresetReverb(int i, int i2) throws RuntimeException {
        super(EFFECT_TYPE_PRESET_REVERB, EFFECT_TYPE_NULL, i, i2);
        this.mParamListener = null;
        this.mBaseParamListener = null;
        this.mParamListenerLock = new Object();
    }

    public short getPreset() throws IllegalStateException, UnsupportedOperationException, IllegalArgumentException {
        short[] sArr = new short[1];
        checkStatus(getParameter(0, sArr));
        return sArr[0];
    }

    public Settings getProperties() throws IllegalStateException, UnsupportedOperationException, IllegalArgumentException {
        Settings settings = new Settings();
        short[] sArr = new short[1];
        checkStatus(getParameter(0, sArr));
        settings.preset = sArr[0];
        return settings;
    }

    public void setParameterListener(OnParameterChangeListener onParameterChangeListener) {
        synchronized (this.mParamListenerLock) {
            if (this.mParamListener == null) {
                this.mParamListener = onParameterChangeListener;
                this.mBaseParamListener = new BaseParameterListener();
                super.setParameterListener(this.mBaseParamListener);
            }
        }
    }

    public void setPreset(short s) throws IllegalStateException, UnsupportedOperationException, IllegalArgumentException {
        checkStatus(setParameter(0, s));
    }

    public void setProperties(Settings settings) throws IllegalStateException, UnsupportedOperationException, IllegalArgumentException {
        checkStatus(setParameter(0, settings.preset));
    }
}
