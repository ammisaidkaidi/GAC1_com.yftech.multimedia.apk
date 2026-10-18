package android.media;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.GuardedBy;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AudioRecord implements AudioRouting {
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDCHANNELMASK = -17;
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDFORMAT = -18;
    private static final int AUDIORECORD_ERROR_SETUP_INVALIDSOURCE = -19;
    private static final int AUDIORECORD_ERROR_SETUP_NATIVEINITFAILED = -20;
    private static final int AUDIORECORD_ERROR_SETUP_ZEROFRAMECOUNT = -16;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -2;
    public static final int ERROR_DEAD_OBJECT = -6;
    public static final int ERROR_INVALID_OPERATION = -3;
    private static final int NATIVE_EVENT_MARKER = 2;
    private static final int NATIVE_EVENT_NEW_POS = 3;
    public static final int READ_BLOCKING = 0;
    public static final int READ_NON_BLOCKING = 1;
    public static final int RECORDSTATE_RECORDING = 3;
    public static final int RECORDSTATE_STOPPED = 1;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_UNINITIALIZED = 0;
    public static final String SUBMIX_FIXED_VOLUME = "fixedVolume";
    public static final int SUCCESS = 0;
    private static final String TAG = "android.media.AudioRecord";
    private AudioAttributes mAudioAttributes;
    private int mAudioFormat;
    private int mChannelCount;
    private int mChannelIndexMask;
    private int mChannelMask;
    private NativeEventHandler mEventHandler;
    private final IBinder mICallBack;
    private Looper mInitializationLooper;
    private boolean mIsSubmixFullVolume;
    private int mNativeBufferSizeInBytes;
    private long mNativeCallbackCookie;
    private long mNativeDeviceCallback;
    private long mNativeRecorderInJavaObj;
    private OnRecordPositionUpdateListener mPositionListener;
    private final Object mPositionListenerLock;
    private AudioDeviceInfo mPreferredDevice;
    private int mRecordSource;
    private int mRecordingState;
    private final Object mRecordingStateLock;

    @GuardedBy("mRoutingChangeListeners")
    private ArrayMap<AudioRouting.OnRoutingChangedListener, NativeRoutingEventHandlerDelegate> mRoutingChangeListeners;
    private int mSampleRate;
    private int mSessionId;
    private int mState;

    public static class Builder {
        private AudioAttributes mAttributes;
        private int mBufferSizeInBytes;
        private AudioFormat mFormat;
        private int mSessionId = 0;

        public AudioRecord build() throws UnsupportedOperationException {
            if (this.mFormat == null) {
                this.mFormat = new AudioFormat.Builder().setEncoding(2).setChannelMask(16).build();
            } else {
                if (this.mFormat.getEncoding() == 0) {
                    this.mFormat = new AudioFormat.Builder(this.mFormat).setEncoding(2).build();
                }
                if (this.mFormat.getChannelMask() == 0 && this.mFormat.getChannelIndexMask() == 0) {
                    this.mFormat = new AudioFormat.Builder(this.mFormat).setChannelMask(16).build();
                }
            }
            if (this.mAttributes == null) {
                this.mAttributes = new AudioAttributes.Builder().setInternalCapturePreset(0).build();
            }
            try {
                if (this.mBufferSizeInBytes == 0) {
                    int channelCount = this.mFormat.getChannelCount();
                    AudioFormat audioFormat = this.mFormat;
                    this.mBufferSizeInBytes = channelCount * AudioFormat.getBytesPerSample(this.mFormat.getEncoding());
                }
                AudioRecord audioRecord = new AudioRecord(this.mAttributes, this.mFormat, this.mBufferSizeInBytes, this.mSessionId);
                if (audioRecord.getState() != 0) {
                    return audioRecord;
                }
                throw new UnsupportedOperationException("Cannot create AudioRecord");
            } catch (IllegalArgumentException e) {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }

        @SystemApi
        public Builder setAudioAttributes(AudioAttributes audioAttributes) throws IllegalArgumentException {
            if (audioAttributes == null) {
                throw new IllegalArgumentException("Illegal null AudioAttributes argument");
            }
            if (audioAttributes.getCapturePreset() == -1) {
                throw new IllegalArgumentException("No valid capture preset in AudioAttributes argument");
            }
            this.mAttributes = audioAttributes;
            return this;
        }

        public Builder setAudioFormat(AudioFormat audioFormat) throws IllegalArgumentException {
            if (audioFormat == null) {
                throw new IllegalArgumentException("Illegal null AudioFormat argument");
            }
            this.mFormat = audioFormat;
            return this;
        }

        public Builder setAudioSource(int i) throws IllegalArgumentException {
            if (i >= 0 && i <= MediaRecorder.getAudioSourceMax()) {
                this.mAttributes = new AudioAttributes.Builder().setInternalCapturePreset(i).build();
                return this;
            }
            throw new IllegalArgumentException("Invalid audio source " + i);
        }

        public Builder setBufferSizeInBytes(int i) throws IllegalArgumentException {
            if (i > 0) {
                this.mBufferSizeInBytes = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid buffer size " + i);
        }

        @SystemApi
        public Builder setSessionId(int i) throws IllegalArgumentException {
            if (i >= 0) {
                this.mSessionId = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid session ID " + i);
        }
    }

    public static final class MetricsConstants {
        public static final String CHANNELS = "android.media.audiorecord.channels";
        public static final String ENCODING = "android.media.audiorecord.encoding";
        public static final String LATENCY = "android.media.audiorecord.latency";
        public static final String SAMPLERATE = "android.media.audiorecord.samplerate";
        public static final String SOURCE = "android.media.audiorecord.source";

        private MetricsConstants() {
        }
    }

    private class NativeEventHandler extends Handler {
        private final AudioRecord mAudioRecord;

        NativeEventHandler(AudioRecord audioRecord, Looper looper) {
            super(looper);
            this.mAudioRecord = audioRecord;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            OnRecordPositionUpdateListener onRecordPositionUpdateListener;
            synchronized (AudioRecord.this.mPositionListenerLock) {
                onRecordPositionUpdateListener = this.mAudioRecord.mPositionListener;
            }
            switch (message.what) {
                case 2:
                    if (onRecordPositionUpdateListener != null) {
                        onRecordPositionUpdateListener.onMarkerReached(this.mAudioRecord);
                        return;
                    }
                    return;
                case 3:
                    if (onRecordPositionUpdateListener != null) {
                        onRecordPositionUpdateListener.onPeriodicNotification(this.mAudioRecord);
                        return;
                    }
                    return;
                default:
                    AudioRecord.loge("Unknown native event type: " + message.what);
                    return;
            }
        }
    }

    public interface OnRecordPositionUpdateListener {
        void onMarkerReached(AudioRecord audioRecord);

        void onPeriodicNotification(AudioRecord audioRecord);
    }

    @Deprecated
    public interface OnRoutingChangedListener extends AudioRouting.OnRoutingChangedListener {
        void onRoutingChanged(AudioRecord audioRecord);

        @Override // android.media.AudioRouting.OnRoutingChangedListener
        default void onRoutingChanged(AudioRouting audioRouting) {
            if (audioRouting instanceof AudioRecord) {
                onRoutingChanged((AudioRecord) audioRouting);
            }
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface ReadMode {
    }

    public AudioRecord(int i, int i2, int i3, int i4, int i5) throws IllegalArgumentException {
        this(new AudioAttributes.Builder().setInternalCapturePreset(i).build(), new AudioFormat.Builder().setChannelMask(getChannelMaskFromLegacyConfig(i3, true)).setEncoding(i4).setSampleRate(i2).build(), i5, 0);
    }

    AudioRecord(long j) {
        this.mState = 0;
        this.mRecordingState = 1;
        this.mRecordingStateLock = new Object();
        this.mPositionListener = null;
        this.mPositionListenerLock = new Object();
        this.mEventHandler = null;
        this.mInitializationLooper = null;
        this.mNativeBufferSizeInBytes = 0;
        this.mSessionId = 0;
        this.mIsSubmixFullVolume = false;
        this.mICallBack = new Binder();
        this.mRoutingChangeListeners = new ArrayMap<>();
        this.mPreferredDevice = null;
        this.mNativeRecorderInJavaObj = 0L;
        this.mNativeCallbackCookie = 0L;
        this.mNativeDeviceCallback = 0L;
        if (j != 0) {
            deferred_connect(j);
        } else {
            this.mState = 0;
        }
    }

    @SystemApi
    public AudioRecord(AudioAttributes audioAttributes, AudioFormat audioFormat, int i, int i2) throws IllegalArgumentException {
        this.mState = 0;
        this.mRecordingState = 1;
        this.mRecordingStateLock = new Object();
        this.mPositionListener = null;
        this.mPositionListenerLock = new Object();
        this.mEventHandler = null;
        this.mInitializationLooper = null;
        this.mNativeBufferSizeInBytes = 0;
        this.mSessionId = 0;
        this.mIsSubmixFullVolume = false;
        this.mICallBack = new Binder();
        this.mRoutingChangeListeners = new ArrayMap<>();
        this.mPreferredDevice = null;
        this.mRecordingState = 1;
        if (audioAttributes == null) {
            throw new IllegalArgumentException("Illegal null AudioAttributes");
        }
        if (audioFormat == null) {
            throw new IllegalArgumentException("Illegal null AudioFormat");
        }
        Looper looperMyLooper = Looper.myLooper();
        this.mInitializationLooper = looperMyLooper;
        if (looperMyLooper == null) {
            this.mInitializationLooper = Looper.getMainLooper();
        }
        if (audioAttributes.getCapturePreset() == 8) {
            AudioAttributes.Builder builder = new AudioAttributes.Builder();
            for (String str : audioAttributes.getTags()) {
                if (str.equalsIgnoreCase(SUBMIX_FIXED_VOLUME)) {
                    this.mIsSubmixFullVolume = true;
                    Log.v(TAG, "Will record from REMOTE_SUBMIX at full fixed volume");
                } else {
                    builder.addTag(str);
                }
            }
            builder.setInternalCapturePreset(audioAttributes.getCapturePreset());
            this.mAudioAttributes = builder.build();
        } else {
            this.mAudioAttributes = audioAttributes;
        }
        int sampleRate = audioFormat.getSampleRate();
        audioParamCheck(audioAttributes.getCapturePreset(), sampleRate == 0 ? 0 : sampleRate, (audioFormat.getPropertySetMask() & 1) != 0 ? audioFormat.getEncoding() : 1);
        if ((audioFormat.getPropertySetMask() & 8) != 0) {
            this.mChannelIndexMask = audioFormat.getChannelIndexMask();
            this.mChannelCount = audioFormat.getChannelCount();
        }
        if ((audioFormat.getPropertySetMask() & 4) != 0) {
            this.mChannelMask = getChannelMaskFromLegacyConfig(audioFormat.getChannelMask(), false);
            this.mChannelCount = audioFormat.getChannelCount();
        } else if (this.mChannelIndexMask == 0) {
            this.mChannelMask = getChannelMaskFromLegacyConfig(1, false);
            this.mChannelCount = AudioFormat.channelCountFromInChannelMask(this.mChannelMask);
        }
        audioBuffSizeCheck(i);
        int[] iArr = {this.mSampleRate};
        int[] iArr2 = {i2};
        int iNative_setup = native_setup(new WeakReference(this), this.mAudioAttributes, iArr, this.mChannelMask, this.mChannelIndexMask, this.mAudioFormat, this.mNativeBufferSizeInBytes, iArr2, ActivityThread.currentOpPackageName(), 0L);
        if (iNative_setup == 0) {
            this.mSampleRate = iArr[0];
            this.mSessionId = iArr2[0];
            this.mState = 1;
        } else {
            loge("Error code " + iNative_setup + " when initializing native AudioRecord object.");
        }
    }

    private void audioBuffSizeCheck(int i) throws IllegalArgumentException {
        int bytesPerSample = this.mChannelCount * AudioFormat.getBytesPerSample(this.mAudioFormat);
        if (i % bytesPerSample == 0 && i >= 1) {
            this.mNativeBufferSizeInBytes = i;
            return;
        }
        throw new IllegalArgumentException("Invalid audio buffer size " + i + " (frame size " + bytesPerSample + ")");
    }

    private void audioParamCheck(int i, int i2, int i3) throws IllegalArgumentException {
        if (i < 0 || !(i <= MediaRecorder.getAudioSourceMax() || i == 1998 || i == 1999)) {
            throw new IllegalArgumentException("Invalid audio source " + i);
        }
        this.mRecordSource = i;
        if ((i2 < 4000 || i2 > 192000) && i2 != 0) {
            throw new IllegalArgumentException(i2 + "Hz is not a supported sample rate.");
        }
        this.mSampleRate = i2;
        switch (i3) {
            case 1:
                this.mAudioFormat = 2;
                return;
            case 2:
            case 3:
            case 4:
                this.mAudioFormat = i3;
                return;
            default:
                throw new IllegalArgumentException("Unsupported sample encoding " + i3 + ". Should be ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, or ENCODING_PCM_FLOAT.");
        }
    }

    private void broadcastRoutingChange() {
        AudioManager.resetAudioPortGeneration();
        synchronized (this.mRoutingChangeListeners) {
            Iterator<NativeRoutingEventHandlerDelegate> it = this.mRoutingChangeListeners.values().iterator();
            while (it.hasNext()) {
                it.next().notifyClient();
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:11:0x0019  */
    private static int getChannelMaskFromLegacyConfig(int i, boolean z) {
        int i2 = 16;
        if (i == 12) {
            i2 = 12;
        } else if (i != 16) {
            if (i != 48) {
                switch (i) {
                    case 1:
                    case 2:
                        break;
                    case 3:
                        i2 = 12;
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported channel configuration.");
                }
            } else {
                i2 = i;
            }
        }
        if (z || !(i == 2 || i == 3)) {
            return i2;
        }
        throw new IllegalArgumentException("Unsupported deprecated configuration.");
    }

    public static int getMinBufferSize(int i, int i2, int i3) {
        int i4;
        if (i2 == 12) {
            i4 = 2;
        } else {
            if (i2 != 16) {
                if (i2 != 48) {
                    switch (i2) {
                        case 1:
                        case 2:
                            break;
                        case 3:
                            break;
                        default:
                            loge("getMinBufferSize(): Invalid channel configuration.");
                            return -2;
                    }
                }
                i4 = 2;
            }
            i4 = 1;
        }
        int iNative_get_min_buff_size = native_get_min_buff_size(i, i4, i3);
        if (iNative_get_min_buff_size == 0) {
            return -2;
        }
        if (iNative_get_min_buff_size == -1) {
            return -1;
        }
        return iNative_get_min_buff_size;
    }

    private void handleFullVolumeRec(boolean z) {
        if (this.mIsSubmixFullVolume) {
            try {
                IAudioService.Stub.asInterface(ServiceManager.getService("audio")).forceRemoteSubmixFullVolume(z, this.mICallBack);
            } catch (RemoteException e) {
                Log.e(TAG, "Error talking to AudioService when handling full submix volume", e);
            }
        }
    }

    private static void logd(String str) {
        Log.d(TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Log.e(TAG, str);
    }

    private final native void native_disableDeviceCallback();

    private final native void native_enableDeviceCallback();

    private final native void native_finalize();

    private native PersistableBundle native_getMetrics();

    private final native int native_getRoutedDeviceId();

    private final native int native_get_active_microphones(ArrayList<MicrophoneInfo> arrayList);

    private final native int native_get_buffer_size_in_frames();

    private final native int native_get_marker_pos();

    private static final native int native_get_min_buff_size(int i, int i2, int i3);

    private final native int native_get_pos_update_period();

    private final native int native_get_timestamp(AudioTimestamp audioTimestamp, int i);

    private final native int native_read_in_byte_array(byte[] bArr, int i, int i2, boolean z);

    private final native int native_read_in_direct_buffer(Object obj, int i, boolean z);

    private final native int native_read_in_float_array(float[] fArr, int i, int i2, boolean z);

    private final native int native_read_in_short_array(short[] sArr, int i, int i2, boolean z);

    private final native boolean native_setInputDevice(int i);

    private final native int native_set_marker_pos(int i);

    private final native int native_set_pos_update_period(int i);

    private final native int native_setup(Object obj, Object obj2, int[] iArr, int i, int i2, int i3, int i4, int[] iArr2, String str, long j);

    private final native int native_start(int i, int i2);

    private final native void native_stop();

    private static void postEventFromNative(Object obj, int i, int i2, int i3, Object obj2) {
        AudioRecord audioRecord = (AudioRecord) ((WeakReference) obj).get();
        if (audioRecord == null) {
            return;
        }
        if (i == 1000) {
            audioRecord.broadcastRoutingChange();
        } else if (audioRecord.mEventHandler != null) {
            audioRecord.mEventHandler.sendMessage(audioRecord.mEventHandler.obtainMessage(i, i2, i3, obj2));
        }
    }

    @GuardedBy("mRoutingChangeListeners")
    private void testDisableNativeRoutingCallbacksLocked() {
        if (this.mRoutingChangeListeners.size() == 0) {
            native_disableDeviceCallback();
        }
    }

    @GuardedBy("mRoutingChangeListeners")
    private void testEnableNativeRoutingCallbacksLocked() {
        if (this.mRoutingChangeListeners.size() == 0) {
            native_enableDeviceCallback();
        }
    }

    @Deprecated
    public void addOnRoutingChangedListener(OnRoutingChangedListener onRoutingChangedListener, Handler handler) {
        addOnRoutingChangedListener((AudioRouting.OnRoutingChangedListener) onRoutingChangedListener, handler);
    }

    @Override // android.media.AudioRouting
    public void addOnRoutingChangedListener(AudioRouting.OnRoutingChangedListener onRoutingChangedListener, Handler handler) {
        synchronized (this.mRoutingChangeListeners) {
            if (onRoutingChangedListener != null) {
                try {
                    if (!this.mRoutingChangeListeners.containsKey(onRoutingChangedListener)) {
                        testEnableNativeRoutingCallbacksLocked();
                        ArrayMap<AudioRouting.OnRoutingChangedListener, NativeRoutingEventHandlerDelegate> arrayMap = this.mRoutingChangeListeners;
                        if (handler == null) {
                            handler = new Handler(this.mInitializationLooper);
                        }
                        arrayMap.put(onRoutingChangedListener, new NativeRoutingEventHandlerDelegate(this, onRoutingChangedListener, handler));
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    void deferred_connect(long j) {
        if (this.mState != 1) {
            int[] iArr = {0};
            int iNative_setup = native_setup(new WeakReference(this), null, new int[]{0}, 0, 0, 0, 0, iArr, ActivityThread.currentOpPackageName(), j);
            if (iNative_setup == 0) {
                this.mSessionId = iArr[0];
                this.mState = 1;
                return;
            }
            loge("Error code " + iNative_setup + " when initializing native AudioRecord object.");
        }
    }

    protected void finalize() {
        release();
    }

    public List<MicrophoneInfo> getActiveMicrophones() throws IOException {
        AudioDeviceInfo routedDevice;
        ArrayList<MicrophoneInfo> arrayList = new ArrayList<>();
        int iNative_get_active_microphones = native_get_active_microphones(arrayList);
        if (iNative_get_active_microphones != 0) {
            if (iNative_get_active_microphones != -3) {
                Log.e(TAG, "getActiveMicrophones failed:" + iNative_get_active_microphones);
            }
            Log.i(TAG, "getActiveMicrophones failed, fallback on routed device info");
        }
        AudioManager.setPortIdForMicrophones(arrayList);
        if (arrayList.size() == 0 && (routedDevice = getRoutedDevice()) != null) {
            MicrophoneInfo microphoneInfoMicrophoneInfoFromAudioDeviceInfo = AudioManager.microphoneInfoFromAudioDeviceInfo(routedDevice);
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < this.mChannelCount; i++) {
                arrayList2.add(new Pair(Integer.valueOf(i), 1));
            }
            microphoneInfoMicrophoneInfoFromAudioDeviceInfo.setChannelMapping(arrayList2);
            arrayList.add(microphoneInfoMicrophoneInfoFromAudioDeviceInfo);
        }
        return arrayList;
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }

    public int getAudioSessionId() {
        return this.mSessionId;
    }

    public int getAudioSource() {
        return this.mRecordSource;
    }

    public int getBufferSizeInFrames() {
        return native_get_buffer_size_in_frames();
    }

    public int getChannelConfiguration() {
        return this.mChannelMask;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public AudioFormat getFormat() {
        AudioFormat.Builder encoding = new AudioFormat.Builder().setSampleRate(this.mSampleRate).setEncoding(this.mAudioFormat);
        if (this.mChannelMask != 0) {
            encoding.setChannelMask(this.mChannelMask);
        }
        if (this.mChannelIndexMask != 0) {
            encoding.setChannelIndexMask(this.mChannelIndexMask);
        }
        return encoding.build();
    }

    public PersistableBundle getMetrics() {
        return native_getMetrics();
    }

    public int getNotificationMarkerPosition() {
        return native_get_marker_pos();
    }

    public int getPositionNotificationPeriod() {
        return native_get_pos_update_period();
    }

    @Override // android.media.AudioRouting
    public AudioDeviceInfo getPreferredDevice() {
        AudioDeviceInfo audioDeviceInfo;
        synchronized (this) {
            audioDeviceInfo = this.mPreferredDevice;
        }
        return audioDeviceInfo;
    }

    public int getRecordingState() {
        int i;
        synchronized (this.mRecordingStateLock) {
            i = this.mRecordingState;
        }
        return i;
    }

    @Override // android.media.AudioRouting
    public AudioDeviceInfo getRoutedDevice() {
        int iNative_getRoutedDeviceId = native_getRoutedDeviceId();
        if (iNative_getRoutedDeviceId == 0) {
            return null;
        }
        AudioDeviceInfo[] devicesStatic = AudioManager.getDevicesStatic(1);
        for (int i = 0; i < devicesStatic.length; i++) {
            if (devicesStatic[i].getId() == iNative_getRoutedDeviceId) {
                return devicesStatic[i];
            }
        }
        return null;
    }

    public int getSampleRate() {
        return this.mSampleRate;
    }

    public int getState() {
        return this.mState;
    }

    public int getTimestamp(AudioTimestamp audioTimestamp, int i) {
        if (audioTimestamp == null || !(i == 1 || i == 0)) {
            throw new IllegalArgumentException();
        }
        return native_get_timestamp(audioTimestamp, i);
    }

    public final native void native_release();

    public int read(ByteBuffer byteBuffer, int i) {
        return read(byteBuffer, i, 0);
    }

    public int read(ByteBuffer byteBuffer, int i, int i2) {
        if (this.mState != 1) {
            return -3;
        }
        if (i2 != 0 && i2 != 1) {
            Log.e(TAG, "AudioRecord.read() called with invalid blocking mode");
            return -2;
        }
        if (byteBuffer == null || i < 0) {
            return -2;
        }
        return native_read_in_direct_buffer(byteBuffer, i, i2 == 0);
    }

    public int read(byte[] bArr, int i, int i2) {
        return read(bArr, i, i2, 0);
    }

    public int read(byte[] bArr, int i, int i2, int i3) {
        int i4;
        if (this.mState != 1 || this.mAudioFormat == 4) {
            return -3;
        }
        if (i3 != 0 && i3 != 1) {
            Log.e(TAG, "AudioRecord.read() called with invalid blocking mode");
            return -2;
        }
        if (bArr == null || i < 0 || i2 < 0 || (i4 = i + i2) < 0 || i4 > bArr.length) {
            return -2;
        }
        return native_read_in_byte_array(bArr, i, i2, i3 == 0);
    }

    public int read(float[] fArr, int i, int i2, int i3) {
        int i4;
        if (this.mState == 0) {
            Log.e(TAG, "AudioRecord.read() called in invalid state STATE_UNINITIALIZED");
            return -3;
        }
        if (this.mAudioFormat != 4) {
            Log.e(TAG, "AudioRecord.read(float[] ...) requires format ENCODING_PCM_FLOAT");
            return -3;
        }
        if (i3 != 0 && i3 != 1) {
            Log.e(TAG, "AudioRecord.read() called with invalid blocking mode");
            return -2;
        }
        if (fArr == null || i < 0 || i2 < 0 || (i4 = i + i2) < 0 || i4 > fArr.length) {
            return -2;
        }
        return native_read_in_float_array(fArr, i, i2, i3 == 0);
    }

    public int read(short[] sArr, int i, int i2) {
        return read(sArr, i, i2, 0);
    }

    public int read(short[] sArr, int i, int i2, int i3) {
        int i4;
        if (this.mState != 1 || this.mAudioFormat == 4) {
            return -3;
        }
        if (i3 != 0 && i3 != 1) {
            Log.e(TAG, "AudioRecord.read() called with invalid blocking mode");
            return -2;
        }
        if (sArr == null || i < 0 || i2 < 0 || (i4 = i + i2) < 0 || i4 > sArr.length) {
            return -2;
        }
        return native_read_in_short_array(sArr, i, i2, i3 == 0);
    }

    public void release() {
        try {
            stop();
        } catch (IllegalStateException unused) {
        }
        native_release();
        this.mState = 0;
    }

    @Deprecated
    public void removeOnRoutingChangedListener(OnRoutingChangedListener onRoutingChangedListener) {
        removeOnRoutingChangedListener((AudioRouting.OnRoutingChangedListener) onRoutingChangedListener);
    }

    @Override // android.media.AudioRouting
    public void removeOnRoutingChangedListener(AudioRouting.OnRoutingChangedListener onRoutingChangedListener) {
        synchronized (this.mRoutingChangeListeners) {
            if (this.mRoutingChangeListeners.containsKey(onRoutingChangedListener)) {
                this.mRoutingChangeListeners.remove(onRoutingChangedListener);
                testDisableNativeRoutingCallbacksLocked();
            }
        }
    }

    public int setNotificationMarkerPosition(int i) {
        if (this.mState == 0) {
            return -3;
        }
        return native_set_marker_pos(i);
    }

    public int setPositionNotificationPeriod(int i) {
        if (this.mState == 0) {
            return -3;
        }
        return native_set_pos_update_period(i);
    }

    @Override // android.media.AudioRouting
    public boolean setPreferredDevice(AudioDeviceInfo audioDeviceInfo) {
        if (audioDeviceInfo != null && !audioDeviceInfo.isSource()) {
            return false;
        }
        boolean zNative_setInputDevice = native_setInputDevice(audioDeviceInfo != null ? audioDeviceInfo.getId() : 0);
        if (zNative_setInputDevice) {
            synchronized (this) {
                this.mPreferredDevice = audioDeviceInfo;
            }
        }
        return zNative_setInputDevice;
    }

    public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener onRecordPositionUpdateListener) {
        setRecordPositionUpdateListener(onRecordPositionUpdateListener, null);
    }

    public void setRecordPositionUpdateListener(OnRecordPositionUpdateListener onRecordPositionUpdateListener, Handler handler) {
        synchronized (this.mPositionListenerLock) {
            this.mPositionListener = onRecordPositionUpdateListener;
            if (onRecordPositionUpdateListener == null) {
                this.mEventHandler = null;
            } else if (handler != null) {
                this.mEventHandler = new NativeEventHandler(this, handler.getLooper());
            } else {
                this.mEventHandler = new NativeEventHandler(this, this.mInitializationLooper);
            }
        }
    }

    public void startRecording() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            if (native_start(0, 0) == 0) {
                handleFullVolumeRec(true);
                this.mRecordingState = 3;
            }
        }
    }

    public void startRecording(MediaSyncEvent mediaSyncEvent) throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("startRecording() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            if (native_start(mediaSyncEvent.getType(), mediaSyncEvent.getAudioSessionId()) == 0) {
                handleFullVolumeRec(true);
                this.mRecordingState = 3;
            }
        }
    }

    public void stop() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("stop() called on an uninitialized AudioRecord.");
        }
        synchronized (this.mRecordingStateLock) {
            handleFullVolumeRec(false);
            native_stop();
            this.mRecordingState = 1;
        }
    }
}
