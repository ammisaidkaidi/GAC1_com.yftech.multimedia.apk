package android.media;

import android.opengl.GLES30;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.NioUtils;
import java.util.Iterator;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class AudioTrack extends PlayerBase implements AudioRouting, VolumeAutomation {
    private static final int AUDIO_OUTPUT_FLAG_DEEP_BUFFER = 8;
    private static final int AUDIO_OUTPUT_FLAG_FAST = 4;
    public static final int ERROR = -1;
    public static final int ERROR_BAD_VALUE = -2;
    public static final int ERROR_DEAD_OBJECT = -6;
    public static final int ERROR_INVALID_OPERATION = -3;
    private static final int ERROR_NATIVESETUP_AUDIOSYSTEM = -16;
    private static final int ERROR_NATIVESETUP_INVALIDCHANNELMASK = -17;
    private static final int ERROR_NATIVESETUP_INVALIDFORMAT = -18;
    private static final int ERROR_NATIVESETUP_INVALIDSTREAMTYPE = -19;
    private static final int ERROR_NATIVESETUP_NATIVEINITFAILED = -20;
    public static final int ERROR_WOULD_BLOCK = -7;
    private static final float GAIN_MAX = 1.0f;
    private static final float GAIN_MIN = 0.0f;
    private static final float HEADER_V2_SIZE_BYTES = 20.0f;
    public static final int MODE_STATIC = 0;
    public static final int MODE_STREAM = 1;
    private static final int NATIVE_EVENT_MARKER = 3;
    private static final int NATIVE_EVENT_MORE_DATA = 0;
    private static final int NATIVE_EVENT_NEW_IAUDIOTRACK = 6;
    private static final int NATIVE_EVENT_NEW_POS = 4;
    private static final int NATIVE_EVENT_STREAM_END = 7;
    public static final int PERFORMANCE_MODE_LOW_LATENCY = 1;
    public static final int PERFORMANCE_MODE_NONE = 0;
    public static final int PERFORMANCE_MODE_POWER_SAVING = 2;
    public static final int PLAYSTATE_PAUSED = 2;
    public static final int PLAYSTATE_PLAYING = 3;
    public static final int PLAYSTATE_STOPPED = 1;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_NO_STATIC_DATA = 2;
    public static final int STATE_UNINITIALIZED = 0;
    public static final int SUCCESS = 0;
    private static final int SUPPORTED_OUT_CHANNELS = 7420;
    private static final String TAG = "android.media.AudioTrack";
    public static final int WRITE_BLOCKING = 0;
    public static final int WRITE_NON_BLOCKING = 1;
    private int mAudioFormat;
    private int mAvSyncBytesRemaining;
    private ByteBuffer mAvSyncHeader;
    private int mChannelConfiguration;
    private int mChannelCount;
    private int mChannelIndexMask;
    private int mChannelMask;
    private int mDataLoadMode;
    private NativePositionEventHandlerDelegate mEventHandlerDelegate;
    private final Looper mInitializationLooper;
    private long mJniData;
    private int mNativeBufferSizeInBytes;
    private int mNativeBufferSizeInFrames;
    protected long mNativeTrackInJavaObj;
    private int mOffset;
    private int mPlayState;
    private final Object mPlayStateLock;
    private AudioDeviceInfo mPreferredDevice;

    @GuardedBy("mRoutingChangeListeners")
    private ArrayMap<AudioRouting.OnRoutingChangedListener, NativeRoutingEventHandlerDelegate> mRoutingChangeListeners;
    private int mSampleRate;
    private int mSessionId;
    private int mState;
    private StreamEventCallback mStreamEventCb;
    private final Object mStreamEventCbLock;
    private Executor mStreamEventExec;
    private int mStreamType;
    private static boolean ATC_AOSP_ENHANCEMENT_AUDIO = SystemProperties.getBoolean("ro.atc.aosp_enhancement_audio", false);
    public static final int CHANNEL_COUNT_MAX = native_get_FCC_8();

    public static class Builder {
        private AudioAttributes mAttributes;
        private int mBufferSizeInBytes;
        private AudioFormat mFormat;
        private int mSessionId = 0;
        private int mMode = 1;
        private int mPerformanceMode = 0;
        private boolean mOffload = false;

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Code duplicated, block: B:12:0x0045  */
        public AudioTrack build() throws UnsupportedOperationException {
            if (this.mAttributes == null) {
                this.mAttributes = new AudioAttributes.Builder().setUsage(1).build();
            }
            switch (this.mPerformanceMode) {
                case 0:
                    if (AudioTrack.shouldEnablePowerSaving(this.mAttributes, this.mFormat, this.mBufferSizeInBytes, this.mMode)) {
                        this.mAttributes = new AudioAttributes.Builder(this.mAttributes).replaceFlags((this.mAttributes.getAllFlags() | 512) & (-257)).build();
                    }
                    break;
                case 1:
                    this.mAttributes = new AudioAttributes.Builder(this.mAttributes).replaceFlags((this.mAttributes.getAllFlags() | 256) & (-513)).build();
                    break;
                case 2:
                    this.mAttributes = new AudioAttributes.Builder(this.mAttributes).replaceFlags((this.mAttributes.getAllFlags() | 512) & (-257)).build();
                    break;
            }
            if (this.mFormat == null) {
                this.mFormat = new AudioFormat.Builder().setChannelMask(12).setEncoding(1).build();
            }
            if (this.mOffload) {
                if (this.mAttributes.getUsage() != 1) {
                    throw new UnsupportedOperationException("Cannot create AudioTrack, offload requires USAGE_MEDIA");
                }
                if (!AudioSystem.isOffloadSupported(this.mFormat)) {
                    throw new UnsupportedOperationException("Cannot create AudioTrack, offload format not supported");
                }
            }
            try {
                if (this.mMode == 1 && this.mBufferSizeInBytes == 0) {
                    int channelCount = this.mFormat.getChannelCount();
                    AudioFormat audioFormat = this.mFormat;
                    this.mBufferSizeInBytes = channelCount * AudioFormat.getBytesPerSample(this.mFormat.getEncoding());
                }
                AudioTrack audioTrack = new AudioTrack(this.mAttributes, this.mFormat, this.mBufferSizeInBytes, this.mMode, this.mSessionId, this.mOffload);
                if (audioTrack.getState() != 0) {
                    return audioTrack;
                }
                throw new UnsupportedOperationException("Cannot create AudioTrack");
            } catch (IllegalArgumentException e) {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }

        public Builder setAudioAttributes(AudioAttributes audioAttributes) throws IllegalArgumentException {
            if (audioAttributes == null) {
                throw new IllegalArgumentException("Illegal null AudioAttributes argument");
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

        public Builder setBufferSizeInBytes(int i) throws IllegalArgumentException {
            if (i > 0) {
                this.mBufferSizeInBytes = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid buffer size " + i);
        }

        public Builder setOffloadedPlayback(boolean z) {
            this.mOffload = z;
            return this;
        }

        public Builder setPerformanceMode(int i) {
            switch (i) {
                case 0:
                case 1:
                case 2:
                    this.mPerformanceMode = i;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid performance mode " + i);
            }
        }

        public Builder setSessionId(int i) throws IllegalArgumentException {
            if (i == 0 || i >= 1) {
                this.mSessionId = i;
                return this;
            }
            throw new IllegalArgumentException("Invalid audio session ID " + i);
        }

        public Builder setTransferMode(int i) throws IllegalArgumentException {
            switch (i) {
                case 0:
                case 1:
                    this.mMode = i;
                    return this;
                default:
                    throw new IllegalArgumentException("Invalid transfer mode " + i);
            }
        }
    }

    public static final class MetricsConstants {
        public static final String CHANNELMASK = "android.media.audiorecord.channelmask";
        public static final String CONTENTTYPE = "android.media.audiotrack.type";
        public static final String SAMPLERATE = "android.media.audiorecord.samplerate";
        public static final String STREAMTYPE = "android.media.audiotrack.streamtype";
        public static final String USAGE = "android.media.audiotrack.usage";

        private MetricsConstants() {
        }
    }

    private class NativePositionEventHandlerDelegate {
        private final Handler mHandler;

        NativePositionEventHandlerDelegate(final AudioTrack audioTrack, final OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener, Handler handler) {
            Looper looper = handler != null ? handler.getLooper() : AudioTrack.this.mInitializationLooper;
            if (looper != null) {
                this.mHandler = new Handler(looper) { // from class: android.media.AudioTrack.NativePositionEventHandlerDelegate.1
                    @Override // android.os.Handler
                    public void handleMessage(Message message) {
                        if (audioTrack == null) {
                        }
                        switch (message.what) {
                            case 3:
                                if (onPlaybackPositionUpdateListener != null) {
                                    onPlaybackPositionUpdateListener.onMarkerReached(audioTrack);
                                }
                                break;
                            case 4:
                                if (onPlaybackPositionUpdateListener != null) {
                                    onPlaybackPositionUpdateListener.onPeriodicNotification(audioTrack);
                                }
                                break;
                            default:
                                AudioTrack.loge("Unknown native event type: " + message.what);
                                break;
                        }
                    }
                };
            } else {
                this.mHandler = null;
            }
        }

        Handler getHandler() {
            return this.mHandler;
        }
    }

    public interface OnPlaybackPositionUpdateListener {
        void onMarkerReached(AudioTrack audioTrack);

        void onPeriodicNotification(AudioTrack audioTrack);
    }

    @Deprecated
    public interface OnRoutingChangedListener extends AudioRouting.OnRoutingChangedListener {
        @Override // android.media.AudioRouting.OnRoutingChangedListener
        default void onRoutingChanged(AudioRouting audioRouting) {
            if (audioRouting instanceof AudioTrack) {
                onRoutingChanged((AudioTrack) audioRouting);
            }
        }

        void onRoutingChanged(AudioTrack audioTrack);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface PerformanceMode {
    }

    public static abstract class StreamEventCallback {
        public void onStreamDataRequest(AudioTrack audioTrack) {
        }

        public void onStreamPresentationEnd(AudioTrack audioTrack) {
        }

        public void onTearDown(AudioTrack audioTrack) {
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TransferMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface WriteMode {
    }

    public AudioTrack(int i, int i2, int i3, int i4, int i5, int i6) throws IllegalArgumentException {
        this(i, i2, i3, i4, i5, i6, 0);
    }

    public AudioTrack(int i, int i2, int i3, int i4, int i5, int i6, int i7) throws IllegalArgumentException {
        this(new AudioAttributes.Builder().setLegacyStreamType(i).build(), new AudioFormat.Builder().setChannelMask(i3).setEncoding(i4).setSampleRate(i2).build(), i5, i6, i7);
        deprecateStreamTypeForPlayback(i, "AudioTrack", "AudioTrack()");
    }

    AudioTrack(long j) {
        super(new AudioAttributes.Builder().build(), 1);
        this.mState = 0;
        this.mPlayState = 1;
        this.mPlayStateLock = new Object();
        this.mNativeBufferSizeInBytes = 0;
        this.mNativeBufferSizeInFrames = 0;
        this.mChannelCount = 1;
        this.mChannelMask = 4;
        this.mStreamType = 3;
        this.mDataLoadMode = 1;
        this.mChannelConfiguration = 4;
        this.mChannelIndexMask = 0;
        this.mSessionId = 0;
        this.mAvSyncHeader = null;
        this.mAvSyncBytesRemaining = 0;
        this.mOffset = 0;
        this.mPreferredDevice = null;
        this.mRoutingChangeListeners = new ArrayMap<>();
        this.mStreamEventCbLock = new Object();
        this.mNativeTrackInJavaObj = 0L;
        this.mJniData = 0L;
        Looper looperMyLooper = Looper.myLooper();
        this.mInitializationLooper = looperMyLooper == null ? Looper.getMainLooper() : looperMyLooper;
        if (j == 0) {
            this.mState = 0;
        } else {
            baseRegisterPlayer();
            deferred_connect(j);
        }
    }

    public AudioTrack(AudioAttributes audioAttributes, AudioFormat audioFormat, int i, int i2, int i3) throws IllegalArgumentException {
        this(audioAttributes, audioFormat, i, i2, i3, false);
    }

    /* JADX WARN: Code duplicated, block: B:34:0x00eb  */
    /* JADX WARN: Code duplicated, block: B:35:0x00f1  */
    /* JADX WARN: Code duplicated, block: B:38:0x0106  */
    /* JADX WARN: Code duplicated, block: B:40:0x0135  */
    /* JADX WARN: Code duplicated, block: B:42:0x014f  */
    /* JADX WARN: Code duplicated, block: B:44:0x0161  */
    /* JADX WARN: Code duplicated, block: B:46:0x0169  */
    /* JADX WARN: Code duplicated, block: B:47:0x0173  */
    /* JADX WARN: Code duplicated, block: B:51:0x0185  */
    /* JADX WARN: Code duplicated, block: B:52:0x0189  */
    /* JADX WARN: Code duplicated, block: B:55:0x018f  */
    /* JADX WARN: Instruction removed from duplicated block: B:40:0x0135, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:55:0x018f, please report this as an issue */
    private AudioTrack(AudioAttributes audioAttributes, AudioFormat audioFormat, int i, int i2, int i3, boolean z) throws IllegalArgumentException {
        int i4;
        int channelMask;
        int encoding;
        int[] iArr;
        int[] iArr2;
        int iNative_setup;
        int bytesPerSample;
        super(audioAttributes, 1);
        this.mState = 0;
        this.mPlayState = 1;
        this.mPlayStateLock = new Object();
        this.mNativeBufferSizeInBytes = 0;
        this.mNativeBufferSizeInFrames = 0;
        this.mChannelCount = 1;
        this.mChannelMask = 4;
        this.mStreamType = 3;
        this.mDataLoadMode = 1;
        this.mChannelConfiguration = 4;
        this.mChannelIndexMask = 0;
        this.mSessionId = 0;
        this.mAvSyncHeader = null;
        this.mAvSyncBytesRemaining = 0;
        this.mOffset = 0;
        this.mPreferredDevice = null;
        this.mRoutingChangeListeners = new ArrayMap<>();
        this.mStreamEventCbLock = new Object();
        if (audioFormat == null) {
            throw new IllegalArgumentException("Illegal null AudioFormat");
        }
        if (ATC_AOSP_ENHANCEMENT_AUDIO) {
            if (AudioSystem.isNaviProcType() == 1) {
                this.mAttributes = new AudioAttributes.Builder().setContentType(5).setUsage(17).build();
            } else if (shouldEnablePowerSaving(this.mAttributes, audioFormat, i, i2)) {
                this.mAttributes = new AudioAttributes.Builder(this.mAttributes).replaceFlags((this.mAttributes.getAllFlags() | 512) & (-257)).build();
            }
        } else if (shouldEnablePowerSaving(this.mAttributes, audioFormat, i, i2)) {
            this.mAttributes = new AudioAttributes.Builder(this.mAttributes).replaceFlags((this.mAttributes.getAllFlags() | 512) & (-257)).build();
        }
        Looper looperMyLooper = Looper.myLooper();
        Looper mainLooper = looperMyLooper == null ? Looper.getMainLooper() : looperMyLooper;
        int sampleRate = audioFormat.getSampleRate();
        sampleRate = sampleRate == 0 ? 0 : sampleRate;
        int channelIndexMask = (audioFormat.getPropertySetMask() & 8) != 0 ? audioFormat.getChannelIndexMask() : 0;
        if ((4 & audioFormat.getPropertySetMask()) == 0) {
            if (channelIndexMask == 0) {
                channelMask = 12;
            } else {
                i4 = 0;
            }
            if ((audioFormat.getPropertySetMask() & 1) != 0) {
                encoding = audioFormat.getEncoding();
            } else {
                encoding = 1;
            }
            audioParamCheck(sampleRate, i4, channelIndexMask, encoding, i2);
            this.mStreamType = -1;
            audioBuffSizeCheck(i);
            this.mInitializationLooper = mainLooper;
            if (i3 >= 0) {
                throw new IllegalArgumentException("Invalid audio session ID: " + i3);
            }
            iArr = new int[]{this.mSampleRate};
            iArr2 = new int[]{i3};
            iNative_setup = native_setup(new WeakReference(this), this.mAttributes, iArr, this.mChannelMask, this.mChannelIndexMask, this.mAudioFormat, this.mNativeBufferSizeInBytes, this.mDataLoadMode, iArr2, 0L, z);
            if (iNative_setup != 0) {
                loge("Error code " + iNative_setup + " when initializing AudioTrack.");
                return;
            }
            this.mSampleRate = iArr[0];
            this.mSessionId = iArr2[0];
            if ((this.mAttributes.getFlags() & 16) != 0) {
                if (AudioFormat.isEncodingLinearFrames(this.mAudioFormat)) {
                    bytesPerSample = this.mChannelCount * AudioFormat.getBytesPerSample(this.mAudioFormat);
                } else {
                    bytesPerSample = 1;
                }
                this.mOffset = ((int) Math.ceil(HEADER_V2_SIZE_BYTES / bytesPerSample)) * bytesPerSample;
            }
            if (this.mDataLoadMode == 0) {
                this.mState = 2;
            } else {
                this.mState = 1;
            }
            baseRegisterPlayer();
        }
        channelMask = audioFormat.getChannelMask();
        i4 = channelMask;
        if ((audioFormat.getPropertySetMask() & 1) != 0) {
            encoding = audioFormat.getEncoding();
        } else {
            encoding = 1;
        }
        audioParamCheck(sampleRate, i4, channelIndexMask, encoding, i2);
        this.mStreamType = -1;
        audioBuffSizeCheck(i);
        this.mInitializationLooper = mainLooper;
        if (i3 >= 0) {
            throw new IllegalArgumentException("Invalid audio session ID: " + i3);
        }
        iArr = new int[]{this.mSampleRate};
        iArr2 = new int[]{i3};
        iNative_setup = native_setup(new WeakReference(this), this.mAttributes, iArr, this.mChannelMask, this.mChannelIndexMask, this.mAudioFormat, this.mNativeBufferSizeInBytes, this.mDataLoadMode, iArr2, 0L, z);
        if (iNative_setup != 0) {
            loge("Error code " + iNative_setup + " when initializing AudioTrack.");
            return;
        }
        this.mSampleRate = iArr[0];
        this.mSessionId = iArr2[0];
        if ((this.mAttributes.getFlags() & 16) != 0) {
            if (AudioFormat.isEncodingLinearFrames(this.mAudioFormat)) {
                bytesPerSample = this.mChannelCount * AudioFormat.getBytesPerSample(this.mAudioFormat);
            } else {
                bytesPerSample = 1;
            }
            this.mOffset = ((int) Math.ceil(HEADER_V2_SIZE_BYTES / bytesPerSample)) * bytesPerSample;
        }
        if (this.mDataLoadMode == 0) {
            this.mState = 2;
        } else {
            this.mState = 1;
        }
        baseRegisterPlayer();
    }

    private void audioBuffSizeCheck(int i) {
        int bytesPerSample = AudioFormat.isEncodingLinearFrames(this.mAudioFormat) ? this.mChannelCount * AudioFormat.getBytesPerSample(this.mAudioFormat) : 1;
        if (i % bytesPerSample != 0 || i < 1) {
            throw new IllegalArgumentException("Invalid audio buffer size.");
        }
        this.mNativeBufferSizeInBytes = i;
        this.mNativeBufferSizeInFrames = i / bytesPerSample;
    }

    /* JADX WARN: Code duplicated, block: B:25:0x004c  */
    private void audioParamCheck(int i, int i2, int i3, int i4, int i5) {
        if ((i < 4000 || i > 192000) && i != 0) {
            throw new IllegalArgumentException(i + "Hz is not a supported sample rate.");
        }
        this.mSampleRate = i;
        if (i4 == 13 && i2 != 12) {
            throw new IllegalArgumentException("ENCODING_IEC61937 must be configured as CHANNEL_OUT_STEREO");
        }
        this.mChannelConfiguration = i2;
        if (i2 != 12) {
            switch (i2) {
                case 1:
                case 2:
                case 4:
                    this.mChannelCount = 1;
                    this.mChannelMask = 4;
                    break;
                case 3:
                    this.mChannelCount = 2;
                    this.mChannelMask = 12;
                    break;
                default:
                    if (i2 == 0 && i3 != 0) {
                        this.mChannelCount = 0;
                    } else {
                        if (!isMultichannelConfigSupported(i2)) {
                            throw new IllegalArgumentException("Unsupported channel configuration.");
                        }
                        this.mChannelMask = i2;
                        this.mChannelCount = AudioFormat.channelCountFromOutChannelMask(i2);
                    }
                    break;
            }
        } else {
            this.mChannelCount = 2;
            this.mChannelMask = 12;
        }
        this.mChannelIndexMask = i3;
        if (this.mChannelIndexMask != 0) {
            if (((~((1 << CHANNEL_COUNT_MAX) - 1)) & i3) != 0) {
                throw new IllegalArgumentException("Unsupported channel index configuration " + i3);
            }
            int iBitCount = Integer.bitCount(i3);
            if (this.mChannelCount == 0) {
                this.mChannelCount = iBitCount;
            } else if (this.mChannelCount != iBitCount) {
                throw new IllegalArgumentException("Channel count must match");
            }
        }
        int i6 = i4 != 1 ? i4 : 2;
        if (!AudioFormat.isPublicEncoding(i6)) {
            throw new IllegalArgumentException("Unsupported audio encoding.");
        }
        this.mAudioFormat = i6;
        if ((i5 != 1 && i5 != 0) || (i5 != 1 && !AudioFormat.isEncodingLinearPcm(this.mAudioFormat))) {
            throw new IllegalArgumentException("Invalid mode.");
        }
        this.mDataLoadMode = i5;
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

    private static float clampGainOrLevel(float f) {
        if (Float.isNaN(f)) {
            throw new IllegalArgumentException();
        }
        if (f < 0.0f) {
            return 0.0f;
        }
        if (f > 1.0f) {
            return 1.0f;
        }
        return f;
    }

    public static float getMaxVolume() {
        return 1.0f;
    }

    /* JADX WARN: Code duplicated, block: B:11:0x001b  */
    public static int getMinBufferSize(int i, int i2, int i3) {
        int iChannelCountFromOutChannelMask;
        if (i2 != 12) {
            switch (i2) {
                case 2:
                case 4:
                    iChannelCountFromOutChannelMask = 1;
                    break;
                case 3:
                    iChannelCountFromOutChannelMask = 2;
                    break;
                default:
                    if (!isMultichannelConfigSupported(i2)) {
                        loge("getMinBufferSize(): Invalid channel configuration.");
                        return -2;
                    }
                    iChannelCountFromOutChannelMask = AudioFormat.channelCountFromOutChannelMask(i2);
                    break;
                    break;
            }
        } else {
            iChannelCountFromOutChannelMask = 2;
        }
        if (!AudioFormat.isPublicEncoding(i3)) {
            loge("getMinBufferSize(): Invalid audio format.");
            return -2;
        }
        if (i >= 4000 && i <= 192000) {
            int iNative_get_min_buff_size = native_get_min_buff_size(i, iChannelCountFromOutChannelMask, i3);
            if (iNative_get_min_buff_size > 0) {
                return iNative_get_min_buff_size;
            }
            loge("getMinBufferSize(): error querying hardware");
            return -1;
        }
        loge("getMinBufferSize(): " + i + " Hz is not a supported sample rate.");
        return -2;
    }

    public static float getMinVolume() {
        return 0.0f;
    }

    public static int getNativeOutputSampleRate(int i) {
        return native_get_output_sample_rate(i);
    }

    private static boolean isMultichannelConfigSupported(int i) {
        if ((i & SUPPORTED_OUT_CHANNELS) != i) {
            loge("Channel configuration features unsupported channels");
            return false;
        }
        int iChannelCountFromOutChannelMask = AudioFormat.channelCountFromOutChannelMask(i);
        if (iChannelCountFromOutChannelMask > CHANNEL_COUNT_MAX) {
            loge("Channel configuration contains too many channels " + iChannelCountFromOutChannelMask + ">" + CHANNEL_COUNT_MAX);
            return false;
        }
        if ((i & 12) != 12) {
            loge("Front channels must be present in multichannel configurations");
            return false;
        }
        int i2 = i & 192;
        if (i2 != 0 && i2 != 192) {
            loge("Rear channels can't be used independently");
            return false;
        }
        int i3 = i & GLES30.GL_COLOR;
        if (i3 == 0 || i3 == 6144) {
            return true;
        }
        loge("Side channels can't be used independently");
        return false;
    }

    private static void logd(String str) {
        Log.d(TAG, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void loge(String str) {
        Log.e(TAG, str);
    }

    private native int native_applyVolumeShaper(VolumeShaper.Configuration configuration, VolumeShaper.Operation operation);

    private final native int native_attachAuxEffect(int i);

    private final native void native_disableDeviceCallback();

    private final native void native_enableDeviceCallback();

    private final native void native_finalize();

    private final native void native_flush();

    private native PersistableBundle native_getMetrics();

    private final native int native_getRoutedDeviceId();

    private native VolumeShaper.State native_getVolumeShaperState(int i);

    private static native int native_get_FCC_8();

    private final native int native_get_buffer_capacity_frames();

    private final native int native_get_buffer_size_frames();

    private final native int native_get_flags();

    private final native int native_get_latency();

    private final native int native_get_marker_pos();

    private static final native int native_get_min_buff_size(int i, int i2, int i3);

    private static final native int native_get_output_sample_rate(int i);

    private final native PlaybackParams native_get_playback_params();

    private final native int native_get_playback_rate();

    private final native int native_get_pos_update_period();

    private final native int native_get_position();

    private final native int native_get_timestamp(long[] jArr);

    private final native int native_get_underrun_count();

    private final native void native_pause();

    private final native int native_reload_static();

    private final native int native_setAuxEffectSendLevel(float f);

    private final native boolean native_setOutputDevice(int i);

    private final native int native_setPresentation(int i, int i2);

    private final native void native_setVolume(float f, float f2);

    private final native int native_set_buffer_size_frames(int i);

    private final native int native_set_loop(int i, int i2, int i3);

    private final native int native_set_marker_pos(int i);

    private final native void native_set_playback_params(PlaybackParams playbackParams);

    private final native int native_set_playback_rate(int i);

    private final native int native_set_pos_update_period(int i);

    private final native int native_set_position(int i);

    private final native int native_setup(Object obj, Object obj2, int[] iArr, int i, int i2, int i3, int i4, int i5, int[] iArr2, long j, boolean z);

    private final native void native_start();

    private final native void native_stop();

    private final native int native_write_byte(byte[] bArr, int i, int i2, int i3, boolean z);

    private final native int native_write_float(float[] fArr, int i, int i2, int i3, boolean z);

    private final native int native_write_native_bytes(Object obj, int i, int i2, int i3, boolean z);

    private final native int native_write_short(short[] sArr, int i, int i2, int i3, boolean z);

    private static void postEventFromNative(Object obj, int i, int i2, int i3, Object obj2) {
        Executor executor;
        final StreamEventCallback streamEventCallback;
        Handler handler;
        final AudioTrack audioTrack = (AudioTrack) ((WeakReference) obj).get();
        if (audioTrack == null) {
            return;
        }
        if (i == 1000) {
            audioTrack.broadcastRoutingChange();
            return;
        }
        if (i == 0 || i == 6 || i == 7) {
            synchronized (audioTrack.mStreamEventCbLock) {
                executor = audioTrack.mStreamEventExec;
                streamEventCallback = audioTrack.mStreamEventCb;
            }
            if (executor == null || streamEventCallback == null) {
                return;
            }
            if (i == 0) {
                executor.execute(new Runnable() { // from class: android.media.-$$Lambda$AudioTrack$RYzHLsveZX4qW27TDViuZeb3nTQ
                    @Override // java.lang.Runnable
                    public final void run() {
                        streamEventCallback.onStreamDataRequest(audioTrack);
                    }
                });
                return;
            }
            switch (i) {
                case 6:
                    executor.execute(new Runnable() { // from class: android.media.-$$Lambda$AudioTrack$m_q5GeJNFuHKP4bKA5zNcUJmptg
                        @Override // java.lang.Runnable
                        public final void run() {
                            streamEventCallback.onTearDown(audioTrack);
                        }
                    });
                    return;
                case 7:
                    executor.execute(new Runnable() { // from class: android.media.-$$Lambda$AudioTrack$om39tqtuoUKWEwKYDHE7uiykjxw
                        @Override // java.lang.Runnable
                        public final void run() {
                            streamEventCallback.onStreamPresentationEnd(audioTrack);
                        }
                    });
                    return;
            }
        }
        NativePositionEventHandlerDelegate nativePositionEventHandlerDelegate = audioTrack.mEventHandlerDelegate;
        if (nativePositionEventHandlerDelegate == null || (handler = nativePositionEventHandlerDelegate.getHandler()) == null) {
            return;
        }
        handler.sendMessage(handler.obtainMessage(i, i2, i3, obj2));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean shouldEnablePowerSaving(AudioAttributes audioAttributes, AudioFormat audioFormat, int i, int i2) {
        if ((audioAttributes == null || (audioAttributes.getAllFlags() == 0 && audioAttributes.getUsage() == 1 && (audioAttributes.getContentType() == 0 || audioAttributes.getContentType() == 2 || audioAttributes.getContentType() == 3))) && audioFormat != null && audioFormat.getSampleRate() != 0 && AudioFormat.isEncodingLinearPcm(audioFormat.getEncoding()) && AudioFormat.isValidEncoding(audioFormat.getEncoding()) && audioFormat.getChannelCount() >= 1 && i2 == 1) {
            return i == 0 || ((long) i) >= (((((long) audioFormat.getChannelCount()) * 100) * ((long) AudioFormat.getBytesPerSample(audioFormat.getEncoding()))) * ((long) audioFormat.getSampleRate())) / 1000;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startImpl() {
        synchronized (this.mPlayStateLock) {
            baseStart();
            native_start();
            this.mPlayState = 3;
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

    @Deprecated
    public void addOnRoutingChangedListener(OnRoutingChangedListener onRoutingChangedListener, Handler handler) {
        addOnRoutingChangedListener((AudioRouting.OnRoutingChangedListener) onRoutingChangedListener, handler);
    }

    public int attachAuxEffect(int i) {
        if (this.mState == 0) {
            return -3;
        }
        return native_attachAuxEffect(i);
    }

    @Override // android.media.VolumeAutomation
    public VolumeShaper createVolumeShaper(VolumeShaper.Configuration configuration) {
        return new VolumeShaper(configuration, this);
    }

    void deferred_connect(long j) {
        if (this.mState != 1) {
            int[] iArr = {0};
            int iNative_setup = native_setup(new WeakReference(this), null, new int[]{0}, 0, 0, 0, 0, 0, iArr, j, false);
            if (iNative_setup == 0) {
                this.mSessionId = iArr[0];
                this.mState = 1;
                return;
            }
            loge("Error code " + iNative_setup + " when initializing AudioTrack.");
        }
    }

    protected void finalize() {
        baseRelease();
        native_finalize();
    }

    public void flush() {
        if (this.mState == 1) {
            native_flush();
            this.mAvSyncHeader = null;
            this.mAvSyncBytesRemaining = 0;
        }
    }

    public int getAudioFormat() {
        return this.mAudioFormat;
    }

    public int getAudioSessionId() {
        return this.mSessionId;
    }

    public int getBufferCapacityInFrames() {
        return native_get_buffer_capacity_frames();
    }

    public int getBufferSizeInFrames() {
        return native_get_buffer_size_frames();
    }

    public int getChannelConfiguration() {
        return this.mChannelConfiguration;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public AudioFormat getFormat() {
        AudioFormat.Builder encoding = new AudioFormat.Builder().setSampleRate(this.mSampleRate).setEncoding(this.mAudioFormat);
        if (this.mChannelConfiguration != 0) {
            encoding.setChannelMask(this.mChannelConfiguration);
        }
        if (this.mChannelIndexMask != 0) {
            encoding.setChannelIndexMask(this.mChannelIndexMask);
        }
        return encoding.build();
    }

    public int getLatency() {
        return native_get_latency();
    }

    public PersistableBundle getMetrics() {
        return native_getMetrics();
    }

    @Deprecated
    protected int getNativeFrameCount() {
        return native_get_buffer_capacity_frames();
    }

    public int getNotificationMarkerPosition() {
        return native_get_marker_pos();
    }

    public int getPerformanceMode() {
        int iNative_get_flags = native_get_flags();
        if ((iNative_get_flags & 4) != 0) {
            return 1;
        }
        return (iNative_get_flags & 8) != 0 ? 2 : 0;
    }

    public int getPlayState() {
        int i;
        synchronized (this.mPlayStateLock) {
            i = this.mPlayState;
        }
        return i;
    }

    public int getPlaybackHeadPosition() {
        return native_get_position();
    }

    public PlaybackParams getPlaybackParams() {
        return native_get_playback_params();
    }

    public int getPlaybackRate() {
        return native_get_playback_rate();
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

    @Override // android.media.AudioRouting
    public AudioDeviceInfo getRoutedDevice() {
        int iNative_getRoutedDeviceId = native_getRoutedDeviceId();
        if (iNative_getRoutedDeviceId == 0) {
            return null;
        }
        AudioDeviceInfo[] devicesStatic = AudioManager.getDevicesStatic(2);
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

    public int getStreamType() {
        return this.mStreamType;
    }

    public boolean getTimestamp(AudioTimestamp audioTimestamp) {
        if (audioTimestamp == null) {
            throw new IllegalArgumentException();
        }
        long[] jArr = new long[2];
        if (native_get_timestamp(jArr) != 0) {
            return false;
        }
        audioTimestamp.framePosition = jArr[0];
        audioTimestamp.nanoTime = jArr[1];
        return true;
    }

    public int getTimestampWithStatus(AudioTimestamp audioTimestamp) {
        if (audioTimestamp == null) {
            throw new IllegalArgumentException();
        }
        long[] jArr = new long[2];
        int iNative_get_timestamp = native_get_timestamp(jArr);
        audioTimestamp.framePosition = jArr[0];
        audioTimestamp.nanoTime = jArr[1];
        return iNative_get_timestamp;
    }

    public int getUnderrunCount() {
        return native_get_underrun_count();
    }

    public final native void native_release();

    public void pause() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("pause() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_pause();
            basePause();
            this.mPlayState = 2;
        }
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [android.media.AudioTrack$1] */
    public void play() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("play() called on uninitialized AudioTrack.");
        }
        final int startDelayMs = getStartDelayMs();
        if (startDelayMs == 0) {
            startImpl();
        } else {
            new Thread() { // from class: android.media.AudioTrack.1
                @Override // java.lang.Thread, java.lang.Runnable
                public void run() {
                    try {
                        Thread.sleep(startDelayMs);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                    AudioTrack.this.baseSetStartDelayMs(0);
                    try {
                        AudioTrack.this.startImpl();
                    } catch (IllegalStateException unused) {
                    }
                }
            }.start();
        }
    }

    @Override // android.media.PlayerBase
    int playerApplyVolumeShaper(VolumeShaper.Configuration configuration, VolumeShaper.Operation operation) {
        return native_applyVolumeShaper(configuration, operation);
    }

    @Override // android.media.PlayerBase
    VolumeShaper.State playerGetVolumeShaperState(int i) {
        return native_getVolumeShaperState(i);
    }

    @Override // android.media.PlayerBase
    void playerPause() {
        pause();
    }

    @Override // android.media.PlayerBase
    int playerSetAuxEffectSendLevel(boolean z, float f) {
        if (z) {
            f = 0.0f;
        }
        return native_setAuxEffectSendLevel(clampGainOrLevel(f)) == 0 ? 0 : -1;
    }

    @Override // android.media.PlayerBase
    void playerSetVolume(boolean z, float f, float f2) {
        if (z) {
            f = 0.0f;
        }
        float fClampGainOrLevel = clampGainOrLevel(f);
        if (z) {
            f2 = 0.0f;
        }
        native_setVolume(fClampGainOrLevel, clampGainOrLevel(f2));
    }

    @Override // android.media.PlayerBase
    void playerStart() {
        play();
    }

    @Override // android.media.PlayerBase
    void playerStop() {
        stop();
    }

    public void release() {
        try {
            stop();
        } catch (IllegalStateException unused) {
        }
        baseRelease();
        native_release();
        this.mState = 0;
    }

    public int reloadStaticData() {
        if (this.mDataLoadMode == 1 || this.mState != 1) {
            return -3;
        }
        return native_reload_static();
    }

    @Override // android.media.AudioRouting
    public void removeOnRoutingChangedListener(AudioRouting.OnRoutingChangedListener onRoutingChangedListener) {
        synchronized (this.mRoutingChangeListeners) {
            if (this.mRoutingChangeListeners.containsKey(onRoutingChangedListener)) {
                this.mRoutingChangeListeners.remove(onRoutingChangedListener);
            }
            testDisableNativeRoutingCallbacksLocked();
        }
    }

    @Deprecated
    public void removeOnRoutingChangedListener(OnRoutingChangedListener onRoutingChangedListener) {
        removeOnRoutingChangedListener((AudioRouting.OnRoutingChangedListener) onRoutingChangedListener);
    }

    public void removeStreamEventCallback() {
        synchronized (this.mStreamEventCbLock) {
            this.mStreamEventExec = null;
            this.mStreamEventCb = null;
        }
    }

    public int setAuxEffectSendLevel(float f) {
        if (this.mState == 0) {
            return -3;
        }
        return baseSetAuxEffectSendLevel(f);
    }

    public int setBufferSizeInFrames(int i) {
        if (this.mDataLoadMode == 0 || this.mState == 0) {
            return -3;
        }
        if (i < 0) {
            return -2;
        }
        return native_set_buffer_size_frames(i);
    }

    public int setLoopPoints(int i, int i2, int i3) {
        if (this.mDataLoadMode == 1 || this.mState == 0 || getPlayState() == 3) {
            return -3;
        }
        if (i3 != 0 && (i < 0 || i >= this.mNativeBufferSizeInFrames || i >= i2 || i2 > this.mNativeBufferSizeInFrames)) {
            return -2;
        }
        return native_set_loop(i, i2, i3);
    }

    public int setNotificationMarkerPosition(int i) {
        if (this.mState == 0) {
            return -3;
        }
        return native_set_marker_pos(i);
    }

    public int setPlaybackHeadPosition(int i) {
        if (this.mDataLoadMode == 1 || this.mState == 0 || getPlayState() == 3) {
            return -3;
        }
        if (i < 0 || i > this.mNativeBufferSizeInFrames) {
            return -2;
        }
        return native_set_position(i);
    }

    public void setPlaybackParams(PlaybackParams playbackParams) {
        if (playbackParams == null) {
            throw new IllegalArgumentException("params is null");
        }
        native_set_playback_params(playbackParams);
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener) {
        setPlaybackPositionUpdateListener(onPlaybackPositionUpdateListener, null);
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener, Handler handler) {
        if (onPlaybackPositionUpdateListener != null) {
            this.mEventHandlerDelegate = new NativePositionEventHandlerDelegate(this, onPlaybackPositionUpdateListener, handler);
        } else {
            this.mEventHandlerDelegate = null;
        }
    }

    public int setPlaybackRate(int i) {
        if (this.mState != 1) {
            return -3;
        }
        if (i <= 0) {
            return -2;
        }
        return native_set_playback_rate(i);
    }

    public int setPositionNotificationPeriod(int i) {
        if (this.mState == 0) {
            return -3;
        }
        return native_set_pos_update_period(i);
    }

    @Override // android.media.AudioRouting
    public boolean setPreferredDevice(AudioDeviceInfo audioDeviceInfo) {
        if (audioDeviceInfo != null && !audioDeviceInfo.isSink()) {
            return false;
        }
        boolean zNative_setOutputDevice = native_setOutputDevice(audioDeviceInfo != null ? audioDeviceInfo.getId() : 0);
        if (zNative_setOutputDevice) {
            synchronized (this) {
                this.mPreferredDevice = audioDeviceInfo;
            }
        }
        return zNative_setOutputDevice;
    }

    public int setPresentation(AudioPresentation audioPresentation) {
        if (audioPresentation != null) {
            return native_setPresentation(audioPresentation.getPresentationId(), audioPresentation.getProgramId());
        }
        throw new IllegalArgumentException("audio presentation is null");
    }

    @Deprecated
    protected void setState(int i) {
        this.mState = i;
    }

    @Deprecated
    public int setStereoVolume(float f, float f2) {
        if (this.mState == 0) {
            return -3;
        }
        baseSetVolume(f, f2);
        return 0;
    }

    public void setStreamEventCallback(Executor executor, StreamEventCallback streamEventCallback) {
        if (streamEventCallback == null) {
            throw new IllegalArgumentException("Illegal null StreamEventCallback");
        }
        if (executor == null) {
            throw new IllegalArgumentException("Illegal null Executor for the StreamEventCallback");
        }
        synchronized (this.mStreamEventCbLock) {
            this.mStreamEventExec = executor;
            this.mStreamEventCb = streamEventCallback;
        }
    }

    public int setVolume(float f) {
        return setStereoVolume(f, f);
    }

    public void stop() throws IllegalStateException {
        if (this.mState != 1) {
            throw new IllegalStateException("stop() called on uninitialized AudioTrack.");
        }
        synchronized (this.mPlayStateLock) {
            native_stop();
            baseStop();
            this.mPlayState = 1;
            this.mAvSyncHeader = null;
            this.mAvSyncBytesRemaining = 0;
        }
    }

    public int write(ByteBuffer byteBuffer, int i, int i2) {
        int iNative_write_byte;
        if (this.mState == 0) {
            Log.e(TAG, "AudioTrack.write() called in invalid state STATE_UNINITIALIZED");
            return -3;
        }
        if (i2 != 0 && i2 != 1) {
            Log.e(TAG, "AudioTrack.write() called with invalid blocking mode");
            return -2;
        }
        if (byteBuffer == null || i < 0 || i > byteBuffer.remaining()) {
            Log.e(TAG, "AudioTrack.write() called with invalid size (" + i + ") value");
            return -2;
        }
        if (byteBuffer.isDirect()) {
            iNative_write_byte = native_write_native_bytes(byteBuffer, byteBuffer.position(), i, this.mAudioFormat, i2 == 0);
        } else {
            iNative_write_byte = native_write_byte(NioUtils.unsafeArray(byteBuffer), byteBuffer.position() + NioUtils.unsafeArrayOffset(byteBuffer), i, this.mAudioFormat, i2 == 0);
        }
        if (this.mDataLoadMode == 0 && this.mState == 2 && iNative_write_byte > 0) {
            this.mState = 1;
        }
        if (iNative_write_byte > 0) {
            byteBuffer.position(byteBuffer.position() + iNative_write_byte);
        }
        return iNative_write_byte;
    }

    public int write(ByteBuffer byteBuffer, int i, int i2, long j) {
        if (this.mState == 0) {
            Log.e(TAG, "AudioTrack.write() called in invalid state STATE_UNINITIALIZED");
            return -3;
        }
        if (i2 != 0 && i2 != 1) {
            Log.e(TAG, "AudioTrack.write() called with invalid blocking mode");
            return -2;
        }
        if (this.mDataLoadMode != 1) {
            Log.e(TAG, "AudioTrack.write() with timestamp called for non-streaming mode track");
            return -3;
        }
        if ((this.mAttributes.getFlags() & 16) == 0) {
            Log.d(TAG, "AudioTrack.write() called on a regular AudioTrack. Ignoring pts...");
            return write(byteBuffer, i, i2);
        }
        if (byteBuffer == null || i < 0 || i > byteBuffer.remaining()) {
            Log.e(TAG, "AudioTrack.write() called with invalid size (" + i + ") value");
            return -2;
        }
        if (this.mAvSyncHeader == null) {
            this.mAvSyncHeader = ByteBuffer.allocate(this.mOffset);
            this.mAvSyncHeader.order(ByteOrder.BIG_ENDIAN);
            this.mAvSyncHeader.putInt(1431633922);
        }
        if (this.mAvSyncBytesRemaining == 0) {
            this.mAvSyncHeader.putInt(4, i);
            this.mAvSyncHeader.putLong(8, j);
            this.mAvSyncHeader.putInt(16, this.mOffset);
            this.mAvSyncHeader.position(0);
            this.mAvSyncBytesRemaining = i;
        }
        if (this.mAvSyncHeader.remaining() != 0) {
            int iWrite = write(this.mAvSyncHeader, this.mAvSyncHeader.remaining(), i2);
            if (iWrite < 0) {
                Log.e(TAG, "AudioTrack.write() could not write timestamp header!");
                this.mAvSyncHeader = null;
                this.mAvSyncBytesRemaining = 0;
                return iWrite;
            }
            if (this.mAvSyncHeader.remaining() > 0) {
                Log.v(TAG, "AudioTrack.write() partial timestamp header written.");
                return 0;
            }
        }
        int iWrite2 = write(byteBuffer, Math.min(this.mAvSyncBytesRemaining, i), i2);
        if (iWrite2 >= 0) {
            this.mAvSyncBytesRemaining -= iWrite2;
            return iWrite2;
        }
        Log.e(TAG, "AudioTrack.write() could not write audio data!");
        this.mAvSyncHeader = null;
        this.mAvSyncBytesRemaining = 0;
        return iWrite2;
    }

    public int write(byte[] bArr, int i, int i2) {
        return write(bArr, i, i2, 0);
    }

    public int write(byte[] bArr, int i, int i2, int i3) {
        int i4;
        if (this.mState == 0 || this.mAudioFormat == 4) {
            return -3;
        }
        if (i3 != 0 && i3 != 1) {
            Log.e(TAG, "AudioTrack.write() called with invalid blocking mode");
            return -2;
        }
        if (bArr == null || i < 0 || i2 < 0 || (i4 = i + i2) < 0 || i4 > bArr.length) {
            return -2;
        }
        int iNative_write_byte = native_write_byte(bArr, i, i2, this.mAudioFormat, i3 == 0);
        if (this.mDataLoadMode == 0 && this.mState == 2 && iNative_write_byte > 0) {
            this.mState = 1;
        }
        return iNative_write_byte;
    }

    public int write(float[] fArr, int i, int i2, int i3) {
        int i4;
        if (this.mState == 0) {
            Log.e(TAG, "AudioTrack.write() called in invalid state STATE_UNINITIALIZED");
            return -3;
        }
        if (this.mAudioFormat != 4) {
            Log.e(TAG, "AudioTrack.write(float[] ...) requires format ENCODING_PCM_FLOAT");
            return -3;
        }
        if (i3 != 0 && i3 != 1) {
            Log.e(TAG, "AudioTrack.write() called with invalid blocking mode");
            return -2;
        }
        if (fArr == null || i < 0 || i2 < 0 || (i4 = i + i2) < 0 || i4 > fArr.length) {
            Log.e(TAG, "AudioTrack.write() called with invalid array, offset, or size");
            return -2;
        }
        int iNative_write_float = native_write_float(fArr, i, i2, this.mAudioFormat, i3 == 0);
        if (this.mDataLoadMode == 0 && this.mState == 2 && iNative_write_float > 0) {
            this.mState = 1;
        }
        return iNative_write_float;
    }

    public int write(short[] sArr, int i, int i2) {
        return write(sArr, i, i2, 0);
    }

    public int write(short[] sArr, int i, int i2, int i3) {
        int i4;
        if (this.mState == 0 || this.mAudioFormat == 4) {
            return -3;
        }
        if (i3 != 0 && i3 != 1) {
            Log.e(TAG, "AudioTrack.write() called with invalid blocking mode");
            return -2;
        }
        if (sArr == null || i < 0 || i2 < 0 || (i4 = i + i2) < 0 || i4 > sArr.length) {
            return -2;
        }
        int iNative_write_short = native_write_short(sArr, i, i2, this.mAudioFormat, i3 == 0);
        if (this.mDataLoadMode == 0 && this.mState == 2 && iNative_write_short > 0) {
            this.mState = 1;
        }
        return iNative_write_short;
    }
}
