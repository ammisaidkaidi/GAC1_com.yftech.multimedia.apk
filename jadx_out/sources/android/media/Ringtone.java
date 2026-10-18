package android.media;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.MediaStore;
import android.util.Log;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class Ringtone {
    private static final boolean LOGD = true;
    private static final String MEDIA_SELECTION = "mime_type LIKE 'audio/%' OR mime_type IN ('application/ogg', 'application/x-flac')";
    private static final String TAG = "Ringtone";
    private final boolean mAllowRemote;
    private final AudioManager mAudioManager;
    private final Context mContext;
    private MediaPlayer mLocalPlayer;
    private final IRingtonePlayer mRemotePlayer;
    private final Binder mRemoteToken;
    private String mTitle;
    private Uri mUri;
    private static final String[] MEDIA_COLUMNS = {"_id", "_data", "title"};
    private static final ArrayList<Ringtone> sActiveRingtones = new ArrayList<>();
    private static boolean ATC_AOSP_ENHANCEMENT_AUDIO = SystemProperties.getBoolean("ro.atc.aosp_enhancement_audio", false);
    private final MyOnCompletionListener mCompletionListener = new MyOnCompletionListener();
    private AudioAttributes mAudioAttributes = new AudioAttributes.Builder().setUsage(6).setContentType(4).build();
    private boolean mIsLooping = false;
    private float mVolume = 1.0f;
    private final Object mPlaybackSettingsLock = new Object();

    class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        MyOnCompletionListener() {
        }

        @Override // android.media.MediaPlayer.OnCompletionListener
        public void onCompletion(MediaPlayer mediaPlayer) {
            synchronized (Ringtone.sActiveRingtones) {
                Ringtone.sActiveRingtones.remove(Ringtone.this);
            }
            mediaPlayer.setOnCompletionListener(null);
        }
    }

    public Ringtone(Context context, boolean z) {
        this.mContext = context;
        this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
        this.mAllowRemote = z;
        this.mRemotePlayer = z ? this.mAudioManager.getRingtonePlayer() : null;
        this.mRemoteToken = z ? new Binder() : null;
    }

    private void applyPlaybackProperties_sync() {
        if (this.mLocalPlayer != null) {
            this.mLocalPlayer.setVolume(this.mVolume);
            this.mLocalPlayer.setLooping(this.mIsLooping);
        } else {
            if (!this.mAllowRemote || this.mRemotePlayer == null) {
                Log.w(TAG, "Neither local nor remote player available when applying playback properties");
                return;
            }
            try {
                this.mRemotePlayer.setPlaybackProperties(this.mRemoteToken, this.mVolume, this.mIsLooping);
            } catch (RemoteException e) {
                Log.w(TAG, "Problem setting playback properties: ", e);
            }
        }
    }

    private void destroyLocalPlayer() {
        if (this.mLocalPlayer != null) {
            this.mLocalPlayer.setOnCompletionListener(null);
            this.mLocalPlayer.reset();
            this.mLocalPlayer.release();
            this.mLocalPlayer = null;
            synchronized (sActiveRingtones) {
                sActiveRingtones.remove(this);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:41:0x0092  */
    /* JADX WARN: Code duplicated, block: B:44:0x0098  */
    /* JADX WARN: Code duplicated, block: B:47:0x00a6  */
    /* JADX WARN: Code duplicated, block: B:49:0x00af A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Code duplicated, block: B:60:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:61:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r10v0, types: [boolean] */
    /* JADX WARN: Type inference failed for: r10v1, types: [android.database.Cursor] */
    /* JADX WARN: Type inference failed for: r10v2 */
    public static String getTitle(Context context, Uri uri, boolean z, boolean z2) throws Throwable {
        String string;
        String string2;
        Cursor cursorQuery;
        String title;
        ContentResolver contentResolver = context.getContentResolver();
        if (uri != null) {
            String authorityWithoutUserId = ContentProvider.getAuthorityWithoutUserId(uri.getAuthority());
            string = null;
            try {
                if (!"settings".equals(authorityWithoutUserId)) {
                    try {
                        if (MediaStore.AUTHORITY.equals(authorityWithoutUserId)) {
                            cursorQuery = contentResolver.query(uri, MEDIA_COLUMNS, z2 ? null : MEDIA_SELECTION, null, null);
                            if (cursorQuery != null) {
                                try {
                                    if (cursorQuery.getCount() == 1) {
                                        cursorQuery.moveToFirst();
                                        String string3 = cursorQuery.getString(2);
                                        if (cursorQuery != null) {
                                            cursorQuery.close();
                                        }
                                        return string3;
                                    }
                                } catch (SecurityException unused) {
                                    IRingtonePlayer ringtonePlayer = z2 ? ((AudioManager) context.getSystemService("audio")).getRingtonePlayer() : null;
                                    if (ringtonePlayer != null) {
                                        try {
                                            title = ringtonePlayer.getTitle(uri);
                                        } catch (RemoteException unused2) {
                                            title = null;
                                            if (cursorQuery != null) {
                                                cursorQuery.close();
                                            }
                                            string = title;
                                            if (string == null) {
                                                string = uri.getLastPathSegment();
                                            }
                                            if (string == null) {
                                                return string;
                                            }
                                            string2 = context.getString(R.string.ringtone_unknown);
                                            if (string2 == null) {
                                                return "";
                                            }
                                            return string2;
                                        }
                                    } else {
                                        title = null;
                                    }
                                    if (cursorQuery != null) {
                                        cursorQuery.close();
                                    }
                                    string = title;
                                }
                            }
                        } else {
                            cursorQuery = null;
                        }
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    } catch (SecurityException unused3) {
                        cursorQuery = null;
                    } catch (Throwable th) {
                        th = th;
                        z = 0;
                        if (z != 0) {
                            z.close();
                        }
                        throw th;
                    }
                    if (string == null) {
                        string = uri.getLastPathSegment();
                    }
                } else if (z != 0) {
                    string = context.getString(R.string.ringtone_default_with_actual, getTitle(context, RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.getDefaultType(uri)), false, z2));
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } else {
            string = context.getString(R.string.ringtone_silent);
        }
        if (string == null) {
            return string;
        }
        string2 = context.getString(R.string.ringtone_unknown);
        if (string2 == null) {
            return "";
        }
        return string2;
    }

    private boolean playFallbackRingtone() {
        if (this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttributes)) == 0) {
            return false;
        }
        int defaultType = RingtoneManager.getDefaultType(this.mUri);
        if (defaultType != -1 && RingtoneManager.getActualDefaultRingtoneUri(this.mContext, defaultType) == null) {
            Log.w(TAG, "not playing fallback for " + this.mUri);
            return false;
        }
        try {
            AssetFileDescriptor assetFileDescriptorOpenRawResourceFd = this.mContext.getResources().openRawResourceFd(R.raw.fallbackring);
            if (assetFileDescriptorOpenRawResourceFd == null) {
                Log.e(TAG, "Could not load fallback ringtone");
                return false;
            }
            this.mLocalPlayer = new MediaPlayer();
            if (assetFileDescriptorOpenRawResourceFd.getDeclaredLength() < 0) {
                this.mLocalPlayer.setDataSource(assetFileDescriptorOpenRawResourceFd.getFileDescriptor());
            } else {
                this.mLocalPlayer.setDataSource(assetFileDescriptorOpenRawResourceFd.getFileDescriptor(), assetFileDescriptorOpenRawResourceFd.getStartOffset(), assetFileDescriptorOpenRawResourceFd.getDeclaredLength());
            }
            this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
            synchronized (this.mPlaybackSettingsLock) {
                applyPlaybackProperties_sync();
            }
            this.mLocalPlayer.prepare();
            startLocalPlayer();
            assetFileDescriptorOpenRawResourceFd.close();
            return true;
        } catch (Resources.NotFoundException unused) {
            Log.e(TAG, "Fallback ringtone does not exist");
            return false;
        } catch (IOException unused2) {
            destroyLocalPlayer();
            Log.e(TAG, "Failed to open fallback ringtone");
            return false;
        }
    }

    private void startLocalPlayer() {
        if (this.mLocalPlayer == null) {
            return;
        }
        synchronized (sActiveRingtones) {
            sActiveRingtones.add(this);
        }
        this.mLocalPlayer.setOnCompletionListener(this.mCompletionListener);
        this.mLocalPlayer.start();
    }

    protected void finalize() {
        if (this.mLocalPlayer != null) {
            this.mLocalPlayer.release();
        }
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAudioAttributes;
    }

    @Deprecated
    public int getStreamType() {
        return AudioAttributes.toLegacyStreamType(this.mAudioAttributes);
    }

    public String getTitle(Context context) {
        if (this.mTitle == null) {
            this.mTitle = getTitle(context, this.mUri, true, this.mAllowRemote);
        }
        return this.mTitle;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public float getVolume() {
        float f;
        synchronized (this.mPlaybackSettingsLock) {
            f = this.mVolume;
        }
        return f;
    }

    public boolean isLooping() {
        boolean z;
        synchronized (this.mPlaybackSettingsLock) {
            z = this.mIsLooping;
        }
        return z;
    }

    public boolean isPlaying() {
        if (this.mLocalPlayer != null) {
            return this.mLocalPlayer.isPlaying();
        }
        if (!this.mAllowRemote || this.mRemotePlayer == null) {
            Log.w(TAG, "Neither local nor remote playback available");
            return false;
        }
        try {
            return this.mRemotePlayer.isPlaying(this.mRemoteToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Problem checking ringtone: " + e);
            return false;
        }
    }

    public void play() {
        boolean z;
        float f;
        if (this.mLocalPlayer != null) {
            if (this.mAudioManager.getStreamVolume(AudioAttributes.toLegacyStreamType(this.mAudioAttributes)) != 0) {
                startLocalPlayer();
                return;
            }
            return;
        }
        if (!this.mAllowRemote || this.mRemotePlayer == null) {
            if (playFallbackRingtone()) {
                return;
            }
            Log.w(TAG, "Neither local nor remote playback available");
            return;
        }
        if (ATC_AOSP_ENHANCEMENT_AUDIO && this.mUri == null) {
            Log.e(TAG, "mUri is null");
            return;
        }
        Uri canonicalUri = this.mUri.getCanonicalUri();
        synchronized (this.mPlaybackSettingsLock) {
            z = this.mIsLooping;
            f = this.mVolume;
        }
        try {
            this.mRemotePlayer.play(this.mRemoteToken, canonicalUri, this.mAudioAttributes, f, z);
        } catch (RemoteException e) {
            if (playFallbackRingtone()) {
                return;
            }
            Log.w(TAG, "Problem playing ringtone: " + e);
        }
    }

    public void setAudioAttributes(AudioAttributes audioAttributes) throws IllegalArgumentException {
        if (audioAttributes == null) {
            throw new IllegalArgumentException("Invalid null AudioAttributes for Ringtone");
        }
        this.mAudioAttributes = audioAttributes;
        setUri(this.mUri);
    }

    public void setLooping(boolean z) {
        synchronized (this.mPlaybackSettingsLock) {
            this.mIsLooping = z;
            applyPlaybackProperties_sync();
        }
    }

    @Deprecated
    public void setStreamType(int i) {
        PlayerBase.deprecateStreamTypeForPlayback(i, TAG, "setStreamType()");
        setAudioAttributes(new AudioAttributes.Builder().setInternalLegacyStreamType(i).build());
    }

    void setTitle(String str) {
        this.mTitle = str;
    }

    public void setUri(Uri uri) {
        destroyLocalPlayer();
        this.mUri = uri;
        if (this.mUri == null) {
            return;
        }
        this.mLocalPlayer = new MediaPlayer();
        try {
            this.mLocalPlayer.setDataSource(this.mContext, this.mUri);
            this.mLocalPlayer.setAudioAttributes(this.mAudioAttributes);
            synchronized (this.mPlaybackSettingsLock) {
                applyPlaybackProperties_sync();
            }
            this.mLocalPlayer.prepare();
        } catch (IOException | SecurityException e) {
            destroyLocalPlayer();
            if (!this.mAllowRemote) {
                Log.w(TAG, "Remote playback not allowed: " + e);
            }
        }
        if (this.mLocalPlayer != null) {
            Log.d(TAG, "Successfully created local player");
        } else {
            Log.d(TAG, "Problem opening; delegating to remote player");
        }
    }

    public void setVolume(float f) {
        synchronized (this.mPlaybackSettingsLock) {
            if (f < 0.0f) {
                f = 0.0f;
            }
            if (f > 1.0f) {
                f = 1.0f;
            }
            this.mVolume = f;
            applyPlaybackProperties_sync();
        }
    }

    public void stop() {
        if (this.mLocalPlayer != null) {
            destroyLocalPlayer();
            return;
        }
        if (!this.mAllowRemote || this.mRemotePlayer == null) {
            return;
        }
        try {
            this.mRemotePlayer.stop(this.mRemoteToken);
        } catch (RemoteException e) {
            Log.w(TAG, "Problem stopping ringtone: " + e);
        }
    }
}
