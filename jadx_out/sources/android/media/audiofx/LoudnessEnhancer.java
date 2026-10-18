package android.media.audiofx;

import android.util.Log;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: classes.dex */
public class LoudnessEnhancer extends AudioEffect {
    public static final int PARAM_TARGET_GAIN_MB = 0;
    private static final String TAG = "LoudnessEnhancer";
    private BaseParameterListener mBaseParamListener;
    private OnParameterChangeListener mParamListener;
    private final Object mParamListenerLock;

    private class BaseParameterListener implements AudioEffect.OnParameterChangeListener {
        private BaseParameterListener() {
        }

        @Override // android.media.audiofx.AudioEffect.OnParameterChangeListener
        public void onParameterChange(AudioEffect audioEffect, int i, byte[] bArr, byte[] bArr2) {
            OnParameterChangeListener onParameterChangeListener;
            if (i != 0) {
                return;
            }
            synchronized (LoudnessEnhancer.this.mParamListenerLock) {
                onParameterChangeListener = LoudnessEnhancer.this.mParamListener != null ? LoudnessEnhancer.this.mParamListener : null;
            }
            if (onParameterChangeListener != null) {
                int iByteArrayToInt = bArr.length == 4 ? AudioEffect.byteArrayToInt(bArr, 0) : -1;
                int iByteArrayToInt2 = bArr2.length == 4 ? AudioEffect.byteArrayToInt(bArr2, 0) : Integer.MIN_VALUE;
                if (iByteArrayToInt == -1 || iByteArrayToInt2 == Integer.MIN_VALUE) {
                    return;
                }
                onParameterChangeListener.onParameterChange(LoudnessEnhancer.this, iByteArrayToInt, iByteArrayToInt2);
            }
        }
    }

    public interface OnParameterChangeListener {
        void onParameterChange(LoudnessEnhancer loudnessEnhancer, int i, int i2);
    }

    public static class Settings {
        public int targetGainmB;

        public Settings() {
        }

        public Settings(String str) {
            String strNextToken;
            StringTokenizer stringTokenizer = new StringTokenizer(str, "=;");
            if (stringTokenizer.countTokens() != 3) {
                throw new IllegalArgumentException("settings: " + str);
            }
            String strNextToken2 = stringTokenizer.nextToken();
            if (!strNextToken2.equals(LoudnessEnhancer.TAG)) {
                throw new IllegalArgumentException("invalid settings for LoudnessEnhancer: " + strNextToken2);
            }
            try {
                strNextToken = stringTokenizer.nextToken();
                try {
                    if (strNextToken.equals("targetGainmB")) {
                        this.targetGainmB = Integer.parseInt(stringTokenizer.nextToken());
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
            return new String("LoudnessEnhancer;targetGainmB=" + Integer.toString(this.targetGainmB));
        }
    }

    public LoudnessEnhancer(int i) throws RuntimeException {
        super(EFFECT_TYPE_LOUDNESS_ENHANCER, EFFECT_TYPE_NULL, 0, i);
        this.mParamListener = null;
        this.mBaseParamListener = null;
        this.mParamListenerLock = new Object();
        if (i == 0) {
            Log.w(TAG, "WARNING: attaching a LoudnessEnhancer to global output mix is deprecated!");
        }
    }

    public LoudnessEnhancer(int i, int i2) throws RuntimeException {
        super(EFFECT_TYPE_LOUDNESS_ENHANCER, EFFECT_TYPE_NULL, i, i2);
        this.mParamListener = null;
        this.mBaseParamListener = null;
        this.mParamListenerLock = new Object();
        if (i2 == 0) {
            Log.w(TAG, "WARNING: attaching a LoudnessEnhancer to global output mix is deprecated!");
        }
    }

    public Settings getProperties() throws IllegalStateException, UnsupportedOperationException, IllegalArgumentException {
        Settings settings = new Settings();
        int[] iArr = new int[1];
        checkStatus(getParameter(0, iArr));
        settings.targetGainmB = iArr[0];
        return settings;
    }

    public float getTargetGain() throws IllegalStateException, UnsupportedOperationException, IllegalArgumentException {
        int[] iArr = new int[1];
        checkStatus(getParameter(0, iArr));
        return iArr[0];
    }

    public void setParameterListener(OnParameterChangeListener onParameterChangeListener) {
        synchronized (this.mParamListenerLock) {
            if (this.mParamListener == null) {
                this.mBaseParamListener = new BaseParameterListener();
                super.setParameterListener(this.mBaseParamListener);
            }
            this.mParamListener = onParameterChangeListener;
        }
    }

    public void setProperties(Settings settings) throws IllegalStateException, UnsupportedOperationException, IllegalArgumentException {
        checkStatus(setParameter(0, settings.targetGainmB));
    }

    public void setTargetGain(int i) throws IllegalStateException, UnsupportedOperationException, IllegalArgumentException {
        checkStatus(setParameter(0, i));
    }
}
