package com.yftech.libmusic.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import com.media.common.event.USBStateEvent;
import com.media.common.util.AppConstants;
import com.media.common.util.AudioSettingUtil;
import com.media.common.util.DateUtil;
import com.media.common.util.DebugLog;
import com.media.common.util.LogUtil;
import com.media.common.util.SharedPrefsUtil;
import com.media.common.util.SystemUtil;
import com.yftech.car.audio.CarAudioManager;
import com.yftech.car.bluetooth.CarBluetoothManager;
import com.yftech.car.bluetooth.callback.CarBluetoothMusicCallback;
import com.yftech.car.bluetooth.callback.CarBluetoothPhoneCallback;
import com.yftech.car.bluetooth.model.PhoneCall;
import com.yftech.car.media.data.CarMediaDataManager;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.media.music.CarMusicManager;
import com.yftech.car.phonelink.CarPhoneLinkManager;
import com.yftech.car.widget.media.music.lyrics.LyricView;
import com.yftech.libmusic.MusicMainActivity;
import com.yftech.libmusic.R;
import com.yftech.libmusic.bean.LyricObject;
import com.yftech.libmusic.dialog.MediaDataListDialog;
import com.yftech.libmusic.dialog.MusicSongListDialog;
import com.yftech.libmusic.dialog.MusicSoundEffectsDialog;
import com.yftech.libmusic.dialog.MusicSourceDialog;
import com.yftech.libmusic.event.BluetoothStateEvent;
import com.yftech.libmusic.event.CarLifeStatusEvent;
import com.yftech.libmusic.event.CarPlayStateEvent;
import com.yftech.libmusic.event.MusicPlayStatusEvent;
import com.yftech.libmusic.event.MusicSourceChangedEvent;
import com.yftech.libmusic.event.MusicSourceDialogOpenEvent;
import com.yftech.libmusic.event.MusicSwitchEvent;
import com.yftech.libmusic.presenter.MusicPlayerPresenter;
import com.yftech.libmusic.utils.CarLifeMusicManager;
import com.yftech.libmusic.utils.CarPlayMusicManager;
import com.yftech.libmusic.view.IMusicPlayerFragmentView;
import com.yftech.libmvpmodule.mvp.BaseMvpActivity;
import com.yftech.libmvpmodule.mvp.BaseMvpFragment;
import com.yftech.vehicle.sdk.CarInfoManager;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* JADX INFO: loaded from: classes4.dex */
public class MusicPlayerFragment extends BaseMvpFragment<MusicPlayerPresenter> implements IMusicPlayerFragmentView, View.OnClickListener {
    private static final String ARG_PARAM1 = "MUSIC_SOURCE";
    private boolean isRightStart;
    private ImageView iv_func_1;
    private ImageView iv_func_2;
    private ImageView iv_func_3;
    private ImageView iv_last;
    private ImageView iv_next;
    private ImageView iv_play;
    private MusicLyricFragment musicLyricFragment;
    private MediaDataListDialog musicSongListDialog;
    private MusicSourceDialog musicSourceDialog;
    private LyricView music_play_lyrics;
    private String music_source;
    private FrameLayout progress_index;
    private List<LyricObject> result;
    private SeekBar sb_music;
    private TextView tv_artist;
    private TextView tv_blue_tips;
    private TextView tv_music_duration;
    private TextView tv_music_progress;
    private TextView tv_music_source;
    private TextView tv_seekBar_max;
    private TextView tv_seekBar_progress;
    private TextView tv_title;
    private View v_cover;
    private boolean isPressing = false;
    private final int RC_DURATION_BAR = 10;
    private final int INTERVAL = 2000;
    private final int RC_PLAY_ERROR = 20;
    private final int RC_LYRIC = 30;
    private final int interval = 300;
    private final int RC_LYRIC_POSITION = 40;
    private final int itemPlaceHolder = 2;
    public boolean isNeedToPlay = true;
    private final int RC_HICar = 80;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.1
        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            if (message.what == 10) {
                MusicPlayerFragment.this.isPressing = false;
                if (MusicPlayerFragment.this.progress_index != null) {
                    MusicPlayerFragment.this.progress_index.setVisibility(8);
                }
                if (MusicPlayerFragment.this.tv_music_progress != null) {
                    MusicPlayerFragment.this.tv_music_progress.setVisibility(0);
                }
                if (MusicPlayerFragment.this.tv_music_duration != null) {
                    MusicPlayerFragment.this.tv_music_duration.setVisibility(0);
                    return;
                }
                return;
            }
            if (message.what == 20) {
                MusicPlayerFragment.this.iv_play.setImageResource(R.drawable.music_main_icon_playing);
                MusicPlayerFragment.this.iv_play.setEnabled(false);
                MusicPlayerFragment.this.sb_music.setVisibility(4);
                MusicPlayerFragment.this.tv_music_progress.setVisibility(4);
                MusicPlayerFragment.this.tv_music_duration.setVisibility(4);
                return;
            }
            if (message.what == 30) {
                MusicPlayerFragment.this.music_play_lyrics.setVisibility(0);
                return;
            }
            if (message.what == 80) {
                LogUtil.d("RC_HICar->isShow=" + ((Boolean) message.obj).booleanValue());
            }
        }
    };
    private Runnable setMusicProgressRunnable = new Runnable() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.2
        @Override // java.lang.Runnable
        public void run() {
            int currentPlayProgress = CarMusicManager.getInstance().getCurrentPlayProgress();
            if (MusicPlayerFragment.this.musicLyricFragment == null || !MusicPlayerFragment.this.musicLyricFragment.isVisible()) {
                return;
            }
            MusicPlayerFragment.this.musicLyricFragment.setMusicProgress(currentPlayProgress, true);
            MusicPlayerFragment.this.mHandler.postDelayed(this, 300L);
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.3
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("test".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("value", -1);
                if (intExtra == 0) {
                    MusicPlayerFragment.this.carLifeDataChangedCallback.onMusicDurationChanged(CarLifeMusicManager.getInstance().getCarLifeMusicDuration());
                    return;
                }
                if (intExtra == 1) {
                    MusicPlayerFragment.this.carLifeDataChangedCallback.onMusicDurationChanged(-100);
                } else if (intExtra == 2) {
                    MusicPlayerFragment.this.iv_next.callOnClick();
                } else if (intExtra == 3) {
                    MusicPlayerFragment.this.iv_last.callOnClick();
                }
            }
        }
    };
    private BroadcastReceiver hiCarReceiver = new BroadcastReceiver() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.4
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (AppConstants.ACTION_HICAR_SHOW_STATE.equals(intent.getAction())) {
                boolean booleanExtra = intent.getBooleanExtra("isShow", false);
                LogUtil.d("com.yftech.hicar.show:isShow=" + booleanExtra);
                int vehicleSpeed = CarInfoManager.get().getVehicleSpeed();
                LogUtil.d("com.yftech.hicar.show:speed=" + vehicleSpeed);
                boolean z = MusicPlayerFragment.this.getActivity() instanceof BaseMvpActivity ? ((BaseMvpActivity) MusicPlayerFragment.this.getActivity()).isDisplaying : false;
                LogUtil.d("com.yftech.hicar.show:isDisplaying=" + z);
                if (vehicleSpeed < 1 && z) {
                    Message messageObtainMessage = MusicPlayerFragment.this.mHandler.obtainMessage(80);
                    messageObtainMessage.obj = Boolean.valueOf(booleanExtra);
                    MusicPlayerFragment.this.mHandler.sendMessage(messageObtainMessage);
                }
            }
        }
    };
    private final CarBluetoothPhoneCallback bluetoothPhoneCallback = new CarBluetoothPhoneCallback() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.5
        public void onBluetoothPhoneCallStateChanged(List<PhoneCall> list) {
            if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(MusicPlayerFragment.this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(MusicPlayerFragment.this.music_source) || list == null || list.isEmpty()) {
                return;
            }
            PhoneCall phoneCall = list.get(0);
            boolean z = MusicPlayerFragment.this.getActivity() instanceof MusicMainActivity ? ((MusicMainActivity) MusicPlayerFragment.this.getActivity()).isDisplaying : true;
            if (z && phoneCall.getCallState() == 2) {
                MusicPlayerFragment.this.onPause();
            } else if (z && phoneCall.getCallState() == 7) {
                MusicPlayerFragment.this.onResume();
            }
        }
    };
    private final CarMusicManager.CarMusicControlCallback callback = new CarMusicManager.CarMusicControlCallback() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.6
        public void onChangePlayMode(int i) {
            LogUtil.d("onChangePlayMode=" + i);
            if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(MusicPlayerFragment.this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(MusicPlayerFragment.this.music_source)) {
                switch (i) {
                    case 0:
                        MusicPlayerFragment.this.iv_func_2.setImageResource(R.drawable.music_main_icon_loop);
                        break;
                    case 1:
                        MusicPlayerFragment.this.iv_func_2.setImageResource(R.drawable.music_main_icon_single);
                        break;
                    case 2:
                        MusicPlayerFragment.this.iv_func_2.setImageResource(R.drawable.music_main_icon_random);
                        break;
                }
            }
        }

        public void onChangePlayMusic(MediaFile mediaFile) {
            LogUtil.d("onChangePlayMusic:path=" + mediaFile.getData() + ", title=" + mediaFile.getTitle() + ", artist=" + mediaFile.getArtist());
            if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(MusicPlayerFragment.this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(MusicPlayerFragment.this.music_source)) {
                EventBus.getDefault().post(new MusicSwitchEvent(mediaFile));
                MusicPlayerFragment.this.getPresenter().getThumbDataByPath(mediaFile.getData());
                if (MusicPlayerFragment.this.isAdded()) {
                    MusicPlayerFragment.this.tv_title.setText(mediaFile.getTitle());
                    MusicPlayerFragment.this.tv_artist.setText(mediaFile.getArtist());
                }
                if (MusicPlayerFragment.this.musicLyricFragment != null) {
                    MusicPlayerFragment.this.musicLyricFragment.setMediaFile(mediaFile);
                }
            }
        }

        public void onChangePlaying(boolean z) {
            LogUtil.d("onChangePlaying:isPlaying=" + z);
            if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(MusicPlayerFragment.this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(MusicPlayerFragment.this.music_source)) {
                if (MusicPlayerFragment.this.isAdded()) {
                    MusicPlayerFragment.this.iv_play.setImageResource(!z ? R.drawable.music_main_icon_pause : R.drawable.music_main_icon_playing);
                }
                EventBus.getDefault().post(new MusicPlayStatusEvent(z));
                MusicPlayerFragment.this.mHandler.removeCallbacks(MusicPlayerFragment.this.setMusicProgressRunnable);
                if (z) {
                    MusicPlayerFragment.this.mHandler.post(MusicPlayerFragment.this.setMusicProgressRunnable);
                }
            }
        }

        public void onChangeProgress(int i) {
            if (MusicPlayerFragment.this.isAdded()) {
                if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(MusicPlayerFragment.this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(MusicPlayerFragment.this.music_source)) {
                    MusicPlayerFragment.this.tv_music_progress.setText(DateUtil.getMusicTime(i));
                    if (!MusicPlayerFragment.this.isPressing) {
                        MusicPlayerFragment.this.sb_music.setProgress(i / 1000);
                    }
                    if (i > 0) {
                        MusicPlayerFragment.this.iv_play.setEnabled(true);
                        if (MusicPlayerFragment.this.sb_music.getVisibility() != 0) {
                            MusicPlayerFragment.this.sb_music.setVisibility(0);
                        }
                        if (MusicPlayerFragment.this.tv_music_progress.getVisibility() != 0 && !MusicPlayerFragment.this.isPressing) {
                            MusicPlayerFragment.this.tv_music_progress.setVisibility(0);
                        }
                        if (MusicPlayerFragment.this.tv_music_duration.getVisibility() != 0 && !MusicPlayerFragment.this.isPressing) {
                            MusicPlayerFragment.this.tv_music_duration.setVisibility(0);
                        }
                    }
                    MusicPlayerFragment.this.setLyricProgress(i);
                }
            }
        }

        public void onCollectChange(int i) {
            LogUtil.d("onCollectChange");
        }

        public boolean onPlayError(int i) {
            if (!AppConstants.MUSIC_SOURCE_OF_USB1.equals(MusicPlayerFragment.this.music_source) && !AppConstants.MUSIC_SOURCE_OF_USB2.equals(MusicPlayerFragment.this.music_source)) {
                return false;
            }
            LogUtil.d("onPlayError:status=" + i);
            MusicPlayerFragment.this.mHandler.sendEmptyMessage(20);
            return false;
        }

        public void onPlaybackParams(float f) {
            LogUtil.d("onPlaybackParams");
        }

        public void onPrepared(int i) {
            LogUtil.d("onPrepared" + i);
            if ((AppConstants.MUSIC_SOURCE_OF_USB1.equals(MusicPlayerFragment.this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(MusicPlayerFragment.this.music_source)) && MusicPlayerFragment.this.isAdded()) {
                MusicPlayerFragment.this.tv_music_duration.setText(DateUtil.getMusicTime(i));
                MusicPlayerFragment.this.sb_music.setMax(i / 1000);
                MusicPlayerFragment.this.sb_music.setProgress(CarMusicManager.getInstance().getCurrentPlayProgress() / 1000);
            }
        }

        public void onStopPlay(boolean z) {
            LogUtil.d("onStopPlay");
        }
    };
    private Runnable mCheckBtMusicStateRunable = new Runnable() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.7
        @Override // java.lang.Runnable
        public void run() {
            if (AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(MusicPlayerFragment.this.music_source)) {
                MusicPlayerFragment.this.tv_blue_tips.setVisibility(0);
                MusicPlayerFragment.this.tv_artist.setVisibility(4);
                MusicPlayerFragment.this.tv_title.setVisibility(4);
                MusicPlayerFragment.this.sb_music.setVisibility(4);
                MusicPlayerFragment.this.tv_music_progress.setVisibility(4);
                MusicPlayerFragment.this.tv_music_duration.setVisibility(4);
                MusicPlayerFragment.this.iv_last.setEnabled(false);
                MusicPlayerFragment.this.iv_next.setEnabled(false);
                MusicPlayerFragment.this.iv_play.setEnabled(false);
                EventBus.getDefault().post(new MusicPlayStatusEvent(false));
            }
        }
    };
    private final CarBluetoothMusicCallback musicCallback = new CarBluetoothMusicCallback() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.8
        public void onBluetoothA2dpConnectStateChanged(BluetoothDevice bluetoothDevice, int i) {
            LogUtil.d("onBluetoothA2dpConnectStateChanged:state:" + i);
        }

        public void onBluetoothAvrcpConnectStateChanged(BluetoothDevice bluetoothDevice, int i) {
            LogUtil.d("onBluetoothAvrcpConnectStateChanged:state:" + i);
        }

        public void onBluetoothMediaExit() {
            DebugLog.d("onBluetoothMediaExit");
        }

        public void onBluetoothMediaPlaybackStateChanged(boolean z, int i, int i2) {
            LogUtil.d("onBluetoothMediaPlaybackStateChanged:isDisplaying=" + (MusicPlayerFragment.this.getActivity() instanceof MusicMainActivity ? ((MusicMainActivity) MusicPlayerFragment.this.getActivity()).isDisplaying : true) + ", isPlaying=" + z + ", music_source=" + MusicPlayerFragment.this.music_source + ", songLen=" + i + ", songPos=" + i2);
            if (MusicPlayerFragment.this.isAdded() && AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(MusicPlayerFragment.this.music_source)) {
                if (i < 0 && i2 <= 0 && !z) {
                    MusicPlayerFragment.this.mHandler.postDelayed(MusicPlayerFragment.this.mCheckBtMusicStateRunable, 800L);
                    return;
                }
                MusicPlayerFragment.this.mHandler.removeCallbacks(MusicPlayerFragment.this.mCheckBtMusicStateRunable);
                MusicPlayerFragment.this.tv_blue_tips.setVisibility(8);
                MusicPlayerFragment.this.tv_artist.setVisibility(0);
                MusicPlayerFragment.this.tv_title.setVisibility(0);
                MusicPlayerFragment.this.sb_music.setVisibility(0);
                MusicPlayerFragment.this.tv_music_progress.setVisibility(0);
                MusicPlayerFragment.this.tv_music_duration.setVisibility(0);
                MusicPlayerFragment.this.iv_last.setEnabled(true);
                MusicPlayerFragment.this.iv_next.setEnabled(true);
                MusicPlayerFragment.this.iv_play.setEnabled(true);
                MusicPlayerFragment.this.iv_play.setImageResource(!z ? R.drawable.music_main_icon_pause : R.drawable.music_main_icon_playing);
                if (i < 0) {
                    i = 0;
                    i2 = 0;
                }
                MusicPlayerFragment.this.sb_music.setMax(i / 1000);
                MusicPlayerFragment.this.tv_music_progress.setText(DateUtil.getMusicTime(i2));
                MusicPlayerFragment.this.tv_music_duration.setText(DateUtil.getMusicTime(i));
                if (!MusicPlayerFragment.this.isPressing) {
                    MusicPlayerFragment.this.sb_music.setProgress(i2 / 1000);
                }
                EventBus.getDefault().post(new MusicPlayStatusEvent(z));
            }
        }

        public void onBluetoothMediaRepeatModeChanged(int i) {
            DebugLog.d("onBluetoothMediaRepeatModeChanged:repeatMode:" + i);
        }

        public void onBluetoothMediaTrackInfoChanged(MediaMetadata mediaMetadata) {
            Activity activity;
            int i;
            LogUtil.d("onBluetoothMediaTrackInfoChanged:title=" + mediaMetadata.getString("android.media.metadata.TITLE") + " artist=" + mediaMetadata.getString("android.media.metadata.ARTIST"));
            if (AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(MusicPlayerFragment.this.music_source) && MusicPlayerFragment.this.isAdded()) {
                String string = CarBluetoothManager.getInstance().getBluetoothMusicInfo().getString("android.media.metadata.TITLE");
                String string2 = CarBluetoothManager.getInstance().getBluetoothMusicInfo().getString("android.media.metadata.ARTIST");
                LogUtil.d("title=" + string + ", artist=" + string2);
                LogUtil.d("titleAct=" + mediaMetadata.getString("android.media.metadata.TITLE") + ", artistAct=" + mediaMetadata.getString("android.media.metadata.ARTIST"));
                boolean z = TextUtils.isEmpty(string) || string.trim().length() == 0;
                MusicPlayerFragment.this.tv_title.setTextSize(z ? 36.0f : 56.0f);
                TextView textView = MusicPlayerFragment.this.tv_title;
                if (z) {
                    activity = MusicPlayerFragment.this.mActivity;
                    i = R.color.colorB3FFFFFF;
                } else {
                    activity = MusicPlayerFragment.this.mActivity;
                    i = R.color.colorFFFFFF;
                }
                textView.setTextColor(activity.getColor(i));
                ((LinearLayout.LayoutParams) MusicPlayerFragment.this.tv_title.getLayoutParams()).topMargin = z ? 75 : 0;
                MusicPlayerFragment.this.tv_title.requestLayout();
                TextView textView2 = MusicPlayerFragment.this.tv_title;
                if (z) {
                    string = MusicPlayerFragment.this.getString(R.string.no_content_play);
                }
                textView2.setText(string);
                TextView textView3 = MusicPlayerFragment.this.tv_artist;
                if (z) {
                    string2 = MusicPlayerFragment.this.getString(R.string.select_play_on_bt);
                }
                textView3.setText(string2);
                MusicPlayerFragment.this.iv_last.setEnabled(!z);
                MusicPlayerFragment.this.iv_next.setEnabled(!z);
                MusicPlayerFragment.this.iv_play.setEnabled(!z);
                MusicPlayerFragment.this.sb_music.setVisibility(!z ? 0 : 4);
                MusicPlayerFragment.this.tv_music_progress.setVisibility(!z ? 0 : 4);
                MusicPlayerFragment.this.tv_music_duration.setVisibility(z ? 4 : 0);
                if (MusicPlayerFragment.this.getActivity() instanceof MusicMainActivity) {
                    ((MusicMainActivity) MusicPlayerFragment.this.getActivity()).setAlbumCover(R.mipmap.music_main_pic_cover);
                }
            }
        }
    };
    private final CarLifeMusicManager.OnCarLifeDataChangedCallback carLifeDataChangedCallback = new CarLifeMusicManager.OnCarLifeDataChangedCallback() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.9
        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onConnectionStatusChanged(boolean z) {
        }

        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onMusicDurationChanged(int i) {
            if (AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(MusicPlayerFragment.this.music_source)) {
                if (i > 0) {
                    MusicPlayerFragment.this.sb_music.setMax(i);
                    MusicPlayerFragment.this.tv_music_duration.setText(DateUtil.getMusicTime(i * 1000));
                } else {
                    MusicPlayerFragment.this.sb_music.setMax(0);
                    MusicPlayerFragment.this.tv_music_duration.setText(DateUtil.getMusicTime(0L));
                }
            }
        }

        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onMusicInformationChanged(CarLifeMusicManager.OnCarLifeDataChangedCallback.MusicInfo musicInfo) {
            Activity activity;
            int i;
            if (AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(MusicPlayerFragment.this.music_source) && MusicPlayerFragment.this.isAdded() && musicInfo != null) {
                boolean z = TextUtils.isEmpty(musicInfo.title) || musicInfo.title.trim().length() == 0;
                MusicPlayerFragment.this.tv_title.setTextSize(z ? 36.0f : 56.0f);
                TextView textView = MusicPlayerFragment.this.tv_title;
                if (z) {
                    activity = MusicPlayerFragment.this.mActivity;
                    i = R.color.colorB3FFFFFF;
                } else {
                    activity = MusicPlayerFragment.this.mActivity;
                    i = R.color.colorFFFFFF;
                }
                textView.setTextColor(activity.getColor(i));
                ((LinearLayout.LayoutParams) MusicPlayerFragment.this.tv_title.getLayoutParams()).topMargin = z ? 75 : 0;
                MusicPlayerFragment.this.tv_title.requestLayout();
                MusicPlayerFragment.this.tv_title.setText(z ? MusicPlayerFragment.this.getString(R.string.no_content_play) : musicInfo.title);
                MusicPlayerFragment.this.tv_artist.setText(z ? MusicPlayerFragment.this.getString(R.string.select_music_play_phone) : musicInfo.artist);
            }
        }

        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onMusicPlayStatusChanged(int i) {
            LogUtil.d("onMusicPlayStatusChanged:status=" + i);
            if (AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(MusicPlayerFragment.this.music_source)) {
                if (MusicPlayerFragment.this.isAdded()) {
                    MusicPlayerFragment.this.iv_play.setImageResource(i == 1 ? R.drawable.music_main_icon_playing : R.drawable.music_main_icon_pause);
                }
                EventBus.getDefault().post(new MusicPlayStatusEvent(i == 1));
            }
        }

        @Override // com.yftech.libmusic.utils.CarLifeMusicManager.OnCarLifeDataChangedCallback
        public void onMusicProgressChanged(int i) {
            if (AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(MusicPlayerFragment.this.music_source)) {
                MusicPlayerFragment.this.tv_music_progress.setText(DateUtil.getMusicTime(i * 1000));
                if (MusicPlayerFragment.this.isPressing) {
                    return;
                }
                MusicPlayerFragment.this.sb_music.setProgress(i);
            }
        }
    };
    private final CarPlayMusicManager.CarPlayMusicListener carPlayMusicListener = new CarPlayMusicManager.CarPlayMusicListener() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.10
        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayConnectChanged(int i) {
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayMusicID3Changed(String str, String str2) {
            Activity activity;
            int i;
            LogUtil.d("onCarplayMusicID3Changed:title=" + str + "artist=" + str2);
            if (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(MusicPlayerFragment.this.music_source)) {
                boolean z = true;
                MusicPlayerFragment.this.iv_last.setEnabled(!TextUtils.isEmpty(str));
                MusicPlayerFragment.this.iv_next.setEnabled(!TextUtils.isEmpty(str));
                MusicPlayerFragment.this.iv_play.setEnabled(!TextUtils.isEmpty(str));
                MusicPlayerFragment.this.sb_music.setVisibility(!TextUtils.isEmpty(str) ? 0 : 4);
                MusicPlayerFragment.this.tv_music_progress.setVisibility(!TextUtils.isEmpty(str) ? 0 : 4);
                MusicPlayerFragment.this.tv_music_duration.setVisibility(TextUtils.isEmpty(str) ? 4 : 0);
                if (MusicPlayerFragment.this.isAdded()) {
                    if (!TextUtils.isEmpty(str) && str.trim().length() != 0) {
                        z = false;
                    }
                    MusicPlayerFragment.this.tv_title.setTextSize(z ? 36.0f : 56.0f);
                    TextView textView = MusicPlayerFragment.this.tv_title;
                    if (z) {
                        activity = MusicPlayerFragment.this.mActivity;
                        i = R.color.colorB3FFFFFF;
                    } else {
                        activity = MusicPlayerFragment.this.mActivity;
                        i = R.color.colorFFFFFF;
                    }
                    textView.setTextColor(activity.getColor(i));
                    ((LinearLayout.LayoutParams) MusicPlayerFragment.this.tv_title.getLayoutParams()).topMargin = z ? 75 : 0;
                    MusicPlayerFragment.this.tv_title.requestLayout();
                    TextView textView2 = MusicPlayerFragment.this.tv_title;
                    if (z) {
                        str = MusicPlayerFragment.this.getString(R.string.no_content_play);
                    }
                    textView2.setText(str);
                    TextView textView3 = MusicPlayerFragment.this.tv_artist;
                    if (z) {
                        str2 = MusicPlayerFragment.this.getString(R.string.select_music_play_phone);
                    }
                    textView3.setText(str2);
                }
            }
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayPlayingChanged(boolean z) {
            LogUtil.d("onCarplayPlayingChanged:isPlaying=" + z);
            if (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(MusicPlayerFragment.this.music_source)) {
                if (MusicPlayerFragment.this.isAdded()) {
                    MusicPlayerFragment.this.iv_play.setImageResource(!z ? R.drawable.music_main_icon_pause : R.drawable.music_main_icon_playing);
                }
                EventBus.getDefault().post(new MusicPlayStatusEvent(z));
            }
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayProgressChanged(int i) {
            LogUtil.d("onCarplayProgressChanged:progress=" + i);
            if (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(MusicPlayerFragment.this.music_source) && MusicPlayerFragment.this.isAdded()) {
                MusicPlayerFragment.this.tv_music_progress.setText(DateUtil.getMusicTime(i));
                if (MusicPlayerFragment.this.isPressing) {
                    return;
                }
                MusicPlayerFragment.this.sb_music.setProgress(i / 1000);
            }
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onCarplayTotalDurationChanged(int i) {
            if (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(MusicPlayerFragment.this.music_source) && MusicPlayerFragment.this.isAdded()) {
                MusicPlayerFragment.this.tv_music_duration.setText(DateUtil.getMusicTime(i));
                MusicPlayerFragment.this.sb_music.setMax(i / 1000);
            }
        }

        @Override // com.yftech.libmusic.utils.CarPlayMusicManager.CarPlayMusicListener
        public void onResourceStateChanged(boolean z) {
            if (MusicPlayerFragment.this.getActivity() instanceof BaseMvpActivity ? ((BaseMvpActivity) MusicPlayerFragment.this.getActivity()).isDisplaying : false) {
                if (z) {
                    MusicPlayerFragment.this.onPause();
                } else {
                    MusicPlayerFragment.this.onResume();
                }
            }
        }
    };
    private final CarAudioManager.CarAudioCallback carAudioCallback = new CarAudioManager.CarAudioCallback() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.11
        public void onChangedMediaSource(int i, String str, String str2) throws Throwable {
            LogUtil.d("onChangedMediaSource streamType:" + i + ", packageName:" + str + ", clientId:" + str2);
            if (i == -1 && AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(MusicPlayerFragment.this.music_source) && "carplay".equals(CarPhoneLinkManager.getInstance().getConnectedLinkStatus())) {
                return;
            }
            String currentSourceByClientId = AudioSettingUtil.getCurrentSourceByClientId(str2);
            if ((!MusicPlayerFragment.this.music_source.equals(currentSourceByClientId) || (!AppConstants.MUSIC_SOURCE_OF_USB1.equals(currentSourceByClientId) && !AppConstants.MUSIC_SOURCE_OF_USB2.equals(currentSourceByClientId))) && MusicPlayerFragment.this.musicSongListDialog != null && MusicPlayerFragment.this.musicSongListDialog.isVisible()) {
                MusicPlayerFragment.this.musicSongListDialog.dismiss();
            }
            if (AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(currentSourceByClientId) && CarBluetoothManager.getInstance().isConnected(12)) {
                MusicPlayerFragment.this.music_source = AppConstants.MUSIC_SOURCE_OF_BLUETOOTH;
                MusicPlayerFragment.this.setMusicSourceStyleWithoutPlay();
                return;
            }
            if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(currentSourceByClientId) && CarMediaDataManager.getInstance().isUsbExists(0)) {
                MusicPlayerFragment.this.music_source = AppConstants.MUSIC_SOURCE_OF_USB1;
                MusicPlayerFragment.this.setMusicSourceStyleWithoutPlay();
                return;
            }
            if (AppConstants.MUSIC_SOURCE_OF_USB2.equals(currentSourceByClientId) && CarMediaDataManager.getInstance().isUsbExists(1)) {
                MusicPlayerFragment.this.music_source = AppConstants.MUSIC_SOURCE_OF_USB2;
                MusicPlayerFragment.this.setMusicSourceStyleWithoutPlay();
                return;
            }
            if (AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(currentSourceByClientId) && CarLifeMusicManager.getInstance().getCarLifeConnectionStatus()) {
                MusicPlayerFragment.this.music_source = AppConstants.MUSIC_SOURCE_OF_CARLIFE;
                MusicPlayerFragment.this.setMusicSourceStyleWithoutPlay();
            } else if (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(currentSourceByClientId) && CarPlayMusicManager.getInstance(MusicPlayerFragment.this.getActivity()).getConnectStatus() == 2) {
                MusicPlayerFragment.this.music_source = AppConstants.MUSIC_SOURCE_OF_CARPLAY;
                MusicPlayerFragment.this.setMusicSourceStyleWithoutPlay();
            } else if (TextUtils.isEmpty(currentSourceByClientId)) {
                MusicPlayerFragment.this.isNeedToPlay = false;
            }
        }
    };

    private void closeMusicSoundEffectsDialog() {
        if (getActivity() instanceof MusicMainActivity) {
            Fragment fragmentFindFragmentByTag = ((MusicMainActivity) getActivity()).getSupportFragmentManager().findFragmentByTag(MusicSoundEffectsDialog.class.getSimpleName());
            if (fragmentFindFragmentByTag instanceof DialogFragment) {
                ((DialogFragment) fragmentFindFragmentByTag).dismissAllowingStateLoss();
            }
        }
    }

    private int getIndexByProgress(int i) {
        if (this.result == null || this.result.isEmpty()) {
            return -1;
        }
        int size = this.result.size() / 2;
        int i2 = 0;
        int size2 = this.result.size() - 1;
        while (size != i2 && size != size2) {
            if (this.result.get(size).begintime + this.result.get(size).timeline > i) {
                size2 = size;
            } else {
                i2 = size;
            }
            size = (i2 + size2) / 2;
        }
        return (this.result.get(size).begintime > i || this.result.get(size).begintime + this.result.get(size).timeline <= i) ? size2 : i2;
    }

    public static /* synthetic */ void lambda$setMusicSourceStyle$0(MusicPlayerFragment musicPlayerFragment, boolean z) {
        if (z) {
            return;
        }
        CarPlayMusicManager.getInstance(musicPlayerFragment.getActivity()).play();
    }

    public static MusicPlayerFragment newInstance(String str) {
        LogUtil.d("newInstance:music_source=" + str);
        MusicPlayerFragment musicPlayerFragment = new MusicPlayerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_PARAM1, str);
        musicPlayerFragment.setArguments(bundle);
        return musicPlayerFragment;
    }

    private void registerHiCarReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(AppConstants.ACTION_HICAR_SHOW_STATE);
        if (getContext() != null) {
            getContext().registerReceiver(this.hiCarReceiver, intentFilter);
        }
    }

    private void registerTestReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("test");
        this.mActivity.registerReceiver(this.receiver, intentFilter);
    }

    private void setLyricData(MediaFile mediaFile) {
        getPresenter().getLyricFromFile(this.music_source, mediaFile);
    }

    private void setMusicSourceStyle() throws Throwable {
        byte b;
        int i;
        Activity activity;
        int i2;
        Activity activity2;
        int i3;
        Activity activity3;
        int i4;
        Drawable drawable;
        LogUtil.d("setMusicSourceStyle:music_source=" + this.music_source);
        if (!AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(this.music_source)) {
            this.tv_blue_tips.setVisibility(8);
            this.tv_artist.setVisibility(0);
            this.tv_title.setVisibility(0);
            this.sb_music.setVisibility(0);
            this.tv_music_progress.setVisibility(0);
            this.tv_music_duration.setVisibility(0);
            this.iv_last.setEnabled(true);
            this.iv_next.setEnabled(true);
            this.iv_play.setEnabled(true);
        }
        SharedPrefsUtil.getInstance(getContext()).save(SharedPrefsUtil.TAG_MUSIC_SOURCE_PLAYING, this.music_source);
        switch (this.music_source) {
            case "MUSIC_SOURCE_OF_BLUETOOTH":
                b = 0;
                break;
            case "MUSIC_SOURCE_OF_CARLIFE":
                b = 4;
                break;
            case "MUSIC_SOURCE_OF_CARPLAY":
                b = 3;
                break;
            case "MUSIC_SOURCE_OF_USB1":
                b = 1;
                break;
            case "MUSIC_SOURCE_OF_USB2":
                b = 2;
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
                i = R.mipmap.audio_icon_bluetooth_36;
                MediaMetadata bluetoothMusicInfo = CarBluetoothManager.getInstance().getBluetoothMusicInfo();
                if (bluetoothMusicInfo != null) {
                    String string = bluetoothMusicInfo.getString("android.media.metadata.TITLE");
                    String string2 = bluetoothMusicInfo.getString("android.media.metadata.ARTIST");
                    boolean z = TextUtils.isEmpty(string) || string.trim().length() == 0;
                    this.tv_title.setTextSize(z ? 36.0f : 56.0f);
                    TextView textView = this.tv_title;
                    if (z) {
                        activity = this.mActivity;
                        i2 = R.color.colorB3FFFFFF;
                    } else {
                        activity = this.mActivity;
                        i2 = R.color.colorFFFFFF;
                    }
                    textView.setTextColor(activity.getColor(i2));
                    ((LinearLayout.LayoutParams) this.tv_title.getLayoutParams()).topMargin = z ? 75 : 0;
                    this.tv_title.requestLayout();
                    TextView textView2 = this.tv_title;
                    if (z) {
                        string = getString(R.string.no_content_play);
                    }
                    textView2.setText(string);
                    TextView textView3 = this.tv_artist;
                    if (z) {
                        string2 = getString(R.string.select_play_on_bt);
                    }
                    textView3.setText(string2);
                    this.tv_music_duration.setText(DateUtil.getMusicTime(bluetoothMusicInfo.getLong("android.media.metadata.DURATION")));
                    this.sb_music.setMax(Math.round(bluetoothMusicInfo.getLong("android.media.metadata.DURATION")));
                    this.iv_last.setEnabled(!z);
                    this.iv_next.setEnabled(!z);
                    this.iv_play.setEnabled(!z);
                    this.sb_music.setVisibility(!z ? 0 : 4);
                    this.tv_music_progress.setVisibility(!z ? 0 : 4);
                    this.tv_music_duration.setVisibility(!z ? 0 : 4);
                    if (getActivity() instanceof MusicMainActivity) {
                        ((MusicMainActivity) getActivity()).setAlbumCover(R.mipmap.music_main_pic_cover);
                    }
                } else {
                    this.tv_blue_tips.setVisibility(0);
                    this.tv_artist.setVisibility(4);
                    this.tv_title.setVisibility(4);
                    this.sb_music.setVisibility(4);
                    this.tv_music_progress.setVisibility(4);
                    this.tv_music_duration.setVisibility(4);
                    this.iv_last.setEnabled(false);
                    this.iv_next.setEnabled(false);
                    this.iv_play.setEnabled(false);
                }
                if (getActivity() instanceof MusicMainActivity) {
                    ((MusicMainActivity) getActivity()).setAlbumCover(R.mipmap.music_main_pic_cover);
                }
                boolean zIsPlayingBluetoothMusic = CarBluetoothManager.getInstance().isPlayingBluetoothMusic();
                this.iv_play.setImageResource(!zIsPlayingBluetoothMusic ? R.drawable.music_main_icon_pause : R.drawable.music_main_icon_playing);
                EventBus.getDefault().post(new MusicPlayStatusEvent(zIsPlayingBluetoothMusic));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.14
                    @Override // java.lang.Runnable
                    public void run() {
                        CarBluetoothManager.getInstance().startBluetoothMusic(true);
                    }
                }, 200L);
                break;
            case 1:
                i = R.mipmap.audio_icon_usb_36_36;
                CarMusicManager.getInstance().setCurrentUSB((byte) 0, true);
                MediaFile playMusic = CarMusicManager.getInstance().getPlayMusic();
                long currentPlayDuration = CarMusicManager.getInstance().getCurrentPlayDuration();
                if (playMusic != null) {
                    this.tv_title.setTextSize(56.0f);
                    this.tv_title.setTextColor(this.mActivity.getColor(R.color.colorFFFFFF));
                    this.tv_title.setText((TextUtils.isEmpty(playMusic.getTitle()) || playMusic.getTitle().trim().length() == 0) ? getString(R.string.common_unknown) : playMusic.getTitle());
                    this.tv_artist.setText((TextUtils.isEmpty(playMusic.getArtist()) || playMusic.getArtist().trim().length() == 0) ? getString(R.string.common_unknown) : playMusic.getArtist());
                    if (currentPlayDuration == 0) {
                        this.iv_play.setEnabled(false);
                        this.sb_music.setVisibility(4);
                        this.tv_music_progress.setVisibility(4);
                        this.tv_music_duration.setVisibility(4);
                    } else {
                        this.sb_music.setMax(Math.round(currentPlayDuration / 1000.0f));
                        this.tv_music_duration.setText(DateUtil.getMusicTime(currentPlayDuration));
                    }
                    getPresenter().getThumbDataByPath(playMusic.getData());
                }
                this.iv_play.setImageResource(CarMusicManager.getInstance().isPlaying() ? R.drawable.music_main_icon_playing : R.drawable.music_main_icon_pause);
                EventBus.getDefault().post(new MusicPlayStatusEvent(CarMusicManager.getInstance().isPlaying()));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.15
                    @Override // java.lang.Runnable
                    public void run() {
                        CarMusicManager.getInstance().play();
                    }
                }, 200L);
                break;
            case 2:
                i = R.mipmap.audio_icon_usb2;
                CarMusicManager.getInstance().setCurrentUSB((byte) 1, true);
                MediaFile playMusic2 = CarMusicManager.getInstance().getPlayMusic();
                if (playMusic2 != null) {
                    ((LinearLayout.LayoutParams) this.tv_title.getLayoutParams()).topMargin = 0;
                    this.tv_title.requestLayout();
                    this.tv_title.setTextSize(56.0f);
                    this.tv_title.setTextColor(this.mActivity.getColor(R.color.colorFFFFFF));
                    this.tv_title.setText((TextUtils.isEmpty(playMusic2.getTitle()) || playMusic2.getTitle().trim().length() == 0) ? getString(R.string.common_unknown) : playMusic2.getTitle());
                    this.tv_artist.setText((TextUtils.isEmpty(playMusic2.getArtist()) || playMusic2.getArtist().trim().length() == 0) ? getString(R.string.common_unknown) : playMusic2.getArtist());
                    if (playMusic2.getDuration() == 0) {
                        this.iv_play.setEnabled(false);
                        this.sb_music.setVisibility(4);
                        this.tv_music_progress.setVisibility(4);
                        this.tv_music_duration.setVisibility(4);
                    } else {
                        this.sb_music.setMax(Math.round(playMusic2.getDuration() / 1000.0f));
                        this.tv_music_duration.setText(DateUtil.getMusicTime(playMusic2.getDuration()));
                    }
                    getPresenter().getThumbDataByPath(playMusic2.getData());
                }
                this.iv_play.setImageResource(CarMusicManager.getInstance().isPlaying() ? R.drawable.music_main_icon_playing : R.drawable.music_main_icon_pause);
                EventBus.getDefault().post(new MusicPlayStatusEvent(CarMusicManager.getInstance().isPlaying()));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.16
                    @Override // java.lang.Runnable
                    public void run() {
                        CarMusicManager.getInstance().play();
                    }
                }, 200L);
                break;
            case 3:
                i = R.mipmap.audio_icon_carplay_36;
                String title = CarPlayMusicManager.getInstance(getActivity()).getTitle();
                String artist = CarPlayMusicManager.getInstance(getActivity()).getArtist();
                boolean z2 = TextUtils.isEmpty(title) || title.trim().length() == 0;
                this.tv_title.setTextSize(z2 ? 36.0f : 56.0f);
                TextView textView4 = this.tv_title;
                if (z2) {
                    activity2 = this.mActivity;
                    i3 = R.color.colorB3FFFFFF;
                } else {
                    activity2 = this.mActivity;
                    i3 = R.color.colorFFFFFF;
                }
                textView4.setTextColor(activity2.getColor(i3));
                ((LinearLayout.LayoutParams) this.tv_title.getLayoutParams()).topMargin = z2 ? 75 : 0;
                this.tv_title.requestLayout();
                this.tv_title.setText(z2 ? getString(R.string.no_content_play) : title);
                TextView textView5 = this.tv_artist;
                if (z2) {
                    artist = getString(R.string.select_music_play_phone);
                }
                textView5.setText(artist);
                this.iv_last.setEnabled(!TextUtils.isEmpty(title));
                this.iv_next.setEnabled(!TextUtils.isEmpty(title));
                this.iv_play.setEnabled(!TextUtils.isEmpty(title));
                this.sb_music.setVisibility(!TextUtils.isEmpty(title) ? 0 : 4);
                this.tv_music_progress.setVisibility(!TextUtils.isEmpty(title) ? 0 : 4);
                this.tv_music_duration.setVisibility(!TextUtils.isEmpty(title) ? 0 : 4);
                int progress = CarPlayMusicManager.getInstance(getActivity()).getProgress();
                int totalDuration = CarPlayMusicManager.getInstance(getActivity()).getTotalDuration();
                this.sb_music.setMax(totalDuration / 1000);
                this.sb_music.setProgress(progress / 1000);
                this.tv_music_duration.setText(DateUtil.getMusicTime(totalDuration));
                this.tv_music_progress.setText(DateUtil.getMusicTime(progress));
                if (getActivity() instanceof MusicMainActivity) {
                    ((MusicMainActivity) getActivity()).setAlbumCover(R.mipmap.music_main_pic_cover);
                }
                final boolean zIsPlaying = CarPlayMusicManager.getInstance(getActivity()).isPlaying();
                this.iv_play.setImageResource(zIsPlaying ? R.drawable.music_main_icon_playing : R.drawable.music_main_icon_pause);
                EventBus.getDefault().post(new MusicPlayStatusEvent(zIsPlaying));
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.yftech.libmusic.ui.-$$Lambda$MusicPlayerFragment$jWVcBTo48BJ2EaePvEE9AZfeawo
                    @Override // java.lang.Runnable
                    public final void run() {
                        MusicPlayerFragment.lambda$setMusicSourceStyle$0(this.f$0, zIsPlaying);
                    }
                }, 200L);
                break;
            case 4:
                i = R.mipmap.audio_icon_carlife_36;
                CarLifeMusicManager.OnCarLifeDataChangedCallback.MusicInfo carLifeMusicInformation = CarLifeMusicManager.getInstance().getCarLifeMusicInformation();
                boolean z3 = TextUtils.isEmpty(carLifeMusicInformation.title) || carLifeMusicInformation.title.trim().length() == 0;
                this.tv_title.setTextSize(z3 ? 36.0f : 56.0f);
                TextView textView6 = this.tv_title;
                if (z3) {
                    activity3 = this.mActivity;
                    i4 = R.color.colorB3FFFFFF;
                } else {
                    activity3 = this.mActivity;
                    i4 = R.color.colorFFFFFF;
                }
                textView6.setTextColor(activity3.getColor(i4));
                ((LinearLayout.LayoutParams) this.tv_title.getLayoutParams()).topMargin = z3 ? 75 : 0;
                this.tv_title.requestLayout();
                this.tv_title.setText(z3 ? getString(R.string.no_content_play) : carLifeMusicInformation.title);
                this.tv_artist.setText(z3 ? getString(R.string.select_music_play_phone) : carLifeMusicInformation.artist);
                int carLifeMusicDuration = CarLifeMusicManager.getInstance().getCarLifeMusicDuration();
                int carLifeMusicProgress = CarLifeMusicManager.getInstance().getCarLifeMusicProgress();
                this.tv_music_duration.setText(DateUtil.getMusicTime(carLifeMusicDuration * 1000));
                this.tv_music_progress.setText(DateUtil.getMusicTime(carLifeMusicProgress * 1000));
                if (carLifeMusicDuration > 0) {
                    this.sb_music.setMax(carLifeMusicDuration);
                    this.sb_music.setProgress(carLifeMusicProgress);
                } else {
                    this.sb_music.setMax(0);
                    this.sb_music.setProgress(0);
                }
                if (CarLifeMusicManager.getInstance().getCarLifeMusicPlayStatus() != 1) {
                    CarLifeMusicManager.getInstance().setMusicControl(1);
                } else {
                    this.iv_play.setImageResource(R.drawable.music_main_icon_playing);
                }
                if (getActivity() instanceof MusicMainActivity) {
                    ((MusicMainActivity) getActivity()).setAlbumCover(R.mipmap.music_main_pic_cover);
                }
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.17
                    @Override // java.lang.Runnable
                    public void run() {
                        CarLifeMusicManager.getInstance().setMusicControl(1);
                    }
                }, 200L);
                EventBus.getDefault().post(new MusicPlayStatusEvent(CarLifeMusicManager.getInstance().getCarLifeMusicPlayStatus() == 1));
                break;
            default:
                i = 0;
                break;
        }
        if (i != 0 && (drawable = ContextCompat.getDrawable((Context) Objects.requireNonNull(getContext()), i)) != null) {
            drawable.setBounds(0, 0, 40, 40);
            this.tv_music_source.setCompoundDrawables(null, null, drawable, null);
        }
        if (this.music_source.equals(AppConstants.MUSIC_SOURCE_OF_BLUETOOTH) || this.music_source.equals(AppConstants.MUSIC_SOURCE_OF_CARLIFE) || this.music_source.equals(AppConstants.MUSIC_SOURCE_OF_CARPLAY)) {
            this.iv_func_1.setVisibility(4);
            this.iv_func_2.setVisibility(4);
            this.iv_func_3.setVisibility(4);
            this.music_play_lyrics.setVisibility(4);
            this.v_cover.setOnClickListener(null);
            this.tv_title.setOnClickListener(null);
            this.sb_music.setEnabled(false);
            this.sb_music.setThumb(getContext().getDrawable(R.color.transparent));
            return;
        }
        this.iv_func_1.setVisibility(0);
        this.iv_func_2.setVisibility(0);
        this.iv_func_3.setVisibility(0);
        this.music_play_lyrics.setVisibility(0);
        this.v_cover.setOnClickListener(this);
        this.tv_title.setOnClickListener(this);
        this.sb_music.setEnabled(true);
        this.sb_music.setThumb(getContext().getDrawable(R.drawable.seekbar_for_music_thumb));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMusicSourceStyleWithoutPlay() throws Throwable {
        byte b;
        int i;
        Activity activity;
        int i2;
        Activity activity2;
        int i3;
        Activity activity3;
        int i4;
        Drawable drawable;
        LogUtil.d("setMusicSourceStyleWithoutPlay:music_source=" + this.music_source);
        EventBus.getDefault().post(new MusicSourceChangedEvent(this.music_source));
        if (!AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(this.music_source)) {
            this.tv_blue_tips.setVisibility(8);
            this.tv_artist.setVisibility(0);
            this.tv_title.setVisibility(0);
            this.sb_music.setVisibility(0);
            this.tv_music_progress.setVisibility(0);
            this.tv_music_duration.setVisibility(0);
            this.iv_last.setEnabled(true);
            this.iv_next.setEnabled(true);
            this.iv_play.setEnabled(true);
        }
        SharedPrefsUtil.getInstance(getContext()).save(SharedPrefsUtil.TAG_MUSIC_SOURCE_PLAYING, this.music_source);
        switch (this.music_source) {
            case "MUSIC_SOURCE_OF_BLUETOOTH":
                b = 0;
                break;
            case "MUSIC_SOURCE_OF_CARLIFE":
                b = 4;
                break;
            case "MUSIC_SOURCE_OF_CARPLAY":
                b = 3;
                break;
            case "MUSIC_SOURCE_OF_USB1":
                b = 1;
                break;
            case "MUSIC_SOURCE_OF_USB2":
                b = 2;
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
                i = R.mipmap.audio_icon_bluetooth_36;
                MediaMetadata bluetoothMusicInfo = CarBluetoothManager.getInstance().getBluetoothMusicInfo();
                if (bluetoothMusicInfo != null) {
                    String string = bluetoothMusicInfo.getString("android.media.metadata.TITLE");
                    String string2 = bluetoothMusicInfo.getString("android.media.metadata.ARTIST");
                    LogUtil.d("setMusicSourceStyleWithoutPlay: MUSIC_SOURCE_OF_BLUETOOTH title==" + string + ",artist==" + string2);
                    boolean z = TextUtils.isEmpty(string) || string.trim().length() == 0;
                    this.tv_title.setTextSize(z ? 36.0f : 56.0f);
                    TextView textView = this.tv_title;
                    if (z) {
                        activity = this.mActivity;
                        i2 = R.color.colorB3FFFFFF;
                    } else {
                        activity = this.mActivity;
                        i2 = R.color.colorFFFFFF;
                    }
                    textView.setTextColor(activity.getColor(i2));
                    ((LinearLayout.LayoutParams) this.tv_title.getLayoutParams()).topMargin = z ? 75 : 0;
                    this.tv_title.requestLayout();
                    TextView textView2 = this.tv_title;
                    if (z) {
                        string = getString(R.string.no_content_play);
                    }
                    textView2.setText(string);
                    TextView textView3 = this.tv_artist;
                    if (z) {
                        string2 = getString(R.string.select_play_on_bt);
                    }
                    textView3.setText(string2);
                    this.tv_music_duration.setText(DateUtil.getMusicTime(bluetoothMusicInfo.getLong("android.media.metadata.DURATION")));
                    this.sb_music.setMax(Math.round(bluetoothMusicInfo.getLong("android.media.metadata.DURATION") / 1000.0f));
                    this.iv_last.setEnabled(!z);
                    this.iv_next.setEnabled(!z);
                    this.iv_play.setEnabled(!z);
                    this.sb_music.setVisibility(!z ? 0 : 4);
                    this.tv_music_progress.setVisibility(!z ? 0 : 4);
                    this.tv_music_duration.setVisibility(!z ? 0 : 4);
                } else {
                    this.tv_blue_tips.setVisibility(0);
                    this.tv_artist.setVisibility(4);
                    this.tv_title.setVisibility(4);
                    this.sb_music.setVisibility(4);
                    this.tv_music_progress.setVisibility(4);
                    this.tv_music_duration.setVisibility(4);
                    this.iv_last.setEnabled(false);
                    this.iv_next.setEnabled(false);
                    this.iv_play.setEnabled(false);
                }
                if (getActivity() instanceof MusicMainActivity) {
                    MusicMainActivity musicMainActivity = (MusicMainActivity) getActivity();
                    musicMainActivity.setAlbumCover(R.mipmap.music_main_pic_cover);
                    if (this.musicLyricFragment != null && this.musicLyricFragment.isVisible()) {
                        musicMainActivity.switchContent(this.musicLyricFragment.getFrontFragment());
                    }
                    musicMainActivity.setSoundEffectVisible(true);
                }
                boolean zIsPlayingBluetoothMusic = CarBluetoothManager.getInstance().isPlayingBluetoothMusic();
                this.iv_play.setImageResource(!zIsPlayingBluetoothMusic ? R.drawable.music_main_icon_pause : R.drawable.music_main_icon_playing);
                EventBus.getDefault().post(new MusicPlayStatusEvent(zIsPlayingBluetoothMusic));
                break;
            case 1:
                i = R.mipmap.audio_icon_usb_36_36;
                String string3 = "";
                String string4 = "";
                MediaFile playMusic = CarMusicManager.getInstance().getPlayMusic();
                long currentPlayDuration = CarMusicManager.getInstance().getCurrentPlayDuration();
                if (playMusic != null && currentPlayDuration == 0) {
                    this.iv_play.setEnabled(false);
                    this.sb_music.setVisibility(4);
                    this.tv_music_progress.setVisibility(4);
                    this.tv_music_duration.setVisibility(4);
                    getPresenter().getThumbDataByPath(playMusic.getData());
                }
                if (playMusic != null) {
                    string3 = playMusic.getTitle();
                    string4 = playMusic.getArtist();
                }
                LogUtil.i("USB1 title = " + string3 + "   artist = " + string4);
                ((LinearLayout.LayoutParams) this.tv_title.getLayoutParams()).topMargin = 0;
                this.tv_title.requestLayout();
                TextView textView4 = this.tv_title;
                if (TextUtils.isEmpty(string3) || string3.trim().length() == 0) {
                    string3 = getString(R.string.common_unknown);
                }
                textView4.setText(string3);
                TextView textView5 = this.tv_artist;
                if (TextUtils.isEmpty(string4) || string4.trim().length() == 0) {
                    string4 = getString(R.string.common_unknown);
                }
                textView5.setText(string4);
                this.sb_music.setMax(Math.round(currentPlayDuration / 1000.0f));
                this.tv_music_duration.setText(DateUtil.getMusicTime(currentPlayDuration));
                this.sb_music.setProgress(CarMusicManager.getInstance().getCurrentPlayProgress() / 1000);
                if (getActivity() instanceof MusicMainActivity) {
                    ((MusicMainActivity) getActivity()).setSoundEffectVisible(true);
                }
                boolean zIsPlaying = CarMusicManager.getInstance().isPlaying();
                this.iv_play.setImageResource(!zIsPlaying ? R.drawable.music_main_icon_pause : R.drawable.music_main_icon_playing);
                EventBus.getDefault().post(new MusicPlayStatusEvent(zIsPlaying));
                break;
            case 2:
                i = R.mipmap.audio_icon_usb2;
                String string5 = "";
                String string6 = "";
                MediaFile playMusic2 = CarMusicManager.getInstance().getPlayMusic();
                if (playMusic2 != null && playMusic2.getDuration() == 0) {
                    this.iv_play.setEnabled(false);
                    this.sb_music.setVisibility(4);
                    this.tv_music_progress.setVisibility(4);
                    this.tv_music_duration.setVisibility(4);
                    getPresenter().getThumbDataByPath(playMusic2.getData());
                }
                if (playMusic2 != null) {
                    string5 = playMusic2.getTitle();
                    string6 = playMusic2.getArtist();
                }
                LogUtil.i("USB2 title = " + string5 + "   artist = " + string6);
                ((LinearLayout.LayoutParams) this.tv_title.getLayoutParams()).topMargin = 0;
                this.tv_title.requestLayout();
                TextView textView6 = this.tv_title;
                if (TextUtils.isEmpty(string5) || string5.trim().length() == 0) {
                    string5 = getString(R.string.common_unknown);
                }
                textView6.setText(string5);
                TextView textView7 = this.tv_artist;
                if (TextUtils.isEmpty(string6) || string6.trim().length() == 0) {
                    string6 = getString(R.string.common_unknown);
                }
                textView7.setText(string6);
                this.sb_music.setMax(Math.round(playMusic2.getDuration() / 1000.0f));
                this.tv_music_duration.setText(DateUtil.getMusicTime(playMusic2.getDuration()));
                if (getActivity() instanceof MusicMainActivity) {
                    ((MusicMainActivity) getActivity()).setSoundEffectVisible(true);
                }
                boolean zIsPlaying2 = CarMusicManager.getInstance().isPlaying();
                this.iv_play.setImageResource(!zIsPlaying2 ? R.drawable.music_main_icon_pause : R.drawable.music_main_icon_playing);
                EventBus.getDefault().post(new MusicPlayStatusEvent(zIsPlaying2));
                break;
            case 3:
                i = R.mipmap.audio_icon_carplay_36;
                String title = CarPlayMusicManager.getInstance(getActivity()).getTitle();
                String artist = CarPlayMusicManager.getInstance(getActivity()).getArtist();
                boolean z2 = TextUtils.isEmpty(title) || title.trim().length() == 0;
                this.tv_title.setTextSize(z2 ? 36.0f : 56.0f);
                TextView textView8 = this.tv_title;
                if (z2) {
                    activity2 = this.mActivity;
                    i3 = R.color.colorB3FFFFFF;
                } else {
                    activity2 = this.mActivity;
                    i3 = R.color.colorFFFFFF;
                }
                textView8.setTextColor(activity2.getColor(i3));
                ((LinearLayout.LayoutParams) this.tv_title.getLayoutParams()).topMargin = z2 ? 75 : 0;
                this.tv_title.requestLayout();
                this.tv_title.setText(z2 ? getString(R.string.no_content_play) : title);
                TextView textView9 = this.tv_artist;
                if (z2) {
                    artist = getString(R.string.select_music_play_phone);
                }
                textView9.setText(artist);
                boolean z3 = !TextUtils.isEmpty(title);
                this.iv_last.setEnabled(z3);
                this.iv_next.setEnabled(z3);
                this.iv_play.setEnabled(z3);
                this.sb_music.setVisibility(z3 ? 0 : 4);
                this.tv_music_progress.setVisibility(z3 ? 0 : 4);
                this.tv_music_duration.setVisibility(z3 ? 0 : 4);
                int progress = CarPlayMusicManager.getInstance(getActivity()).getProgress();
                int totalDuration = CarPlayMusicManager.getInstance(getActivity()).getTotalDuration();
                this.sb_music.setMax(totalDuration / 1000);
                this.sb_music.setProgress(progress / 1000);
                this.tv_music_duration.setText(DateUtil.getMusicTime(totalDuration));
                this.tv_music_progress.setText(DateUtil.getMusicTime(progress));
                if (getActivity() instanceof MusicMainActivity) {
                    MusicMainActivity musicMainActivity2 = (MusicMainActivity) getActivity();
                    musicMainActivity2.setAlbumCover(R.mipmap.music_main_pic_cover);
                    if (this.musicLyricFragment != null && this.musicLyricFragment.isVisible()) {
                        musicMainActivity2.switchContent(this.musicLyricFragment.getFrontFragment());
                    }
                    musicMainActivity2.setSoundEffectVisible(true);
                }
                boolean zIsPlaying3 = CarPlayMusicManager.getInstance(getActivity()).isPlaying();
                this.iv_play.setImageResource(!zIsPlaying3 ? R.drawable.music_main_icon_pause : R.drawable.music_main_icon_playing);
                EventBus.getDefault().post(new MusicPlayStatusEvent(zIsPlaying3));
                break;
            case 4:
                i = R.mipmap.audio_icon_carlife_36;
                CarLifeMusicManager.OnCarLifeDataChangedCallback.MusicInfo carLifeMusicInformation = CarLifeMusicManager.getInstance().getCarLifeMusicInformation();
                boolean z4 = TextUtils.isEmpty(carLifeMusicInformation.title) || carLifeMusicInformation.title.trim().length() == 0;
                this.tv_title.setTextSize(z4 ? 36.0f : 56.0f);
                TextView textView10 = this.tv_title;
                if (z4) {
                    activity3 = this.mActivity;
                    i4 = R.color.colorB3FFFFFF;
                } else {
                    activity3 = this.mActivity;
                    i4 = R.color.colorFFFFFF;
                }
                textView10.setTextColor(activity3.getColor(i4));
                ((LinearLayout.LayoutParams) this.tv_title.getLayoutParams()).topMargin = z4 ? 75 : 0;
                this.tv_title.requestLayout();
                this.tv_title.setText(z4 ? getString(R.string.no_content_play) : carLifeMusicInformation.title);
                this.tv_artist.setText(z4 ? getString(R.string.select_music_play_phone) : carLifeMusicInformation.artist);
                int carLifeMusicDuration = CarLifeMusicManager.getInstance().getCarLifeMusicDuration();
                int carLifeMusicProgress = CarLifeMusicManager.getInstance().getCarLifeMusicProgress();
                this.tv_music_duration.setText(DateUtil.getMusicTime(carLifeMusicDuration * 1000));
                this.tv_music_progress.setText(DateUtil.getMusicTime(carLifeMusicProgress * 1000));
                if (carLifeMusicDuration > 0) {
                    this.sb_music.setMax(carLifeMusicDuration);
                    this.sb_music.setProgress(carLifeMusicProgress);
                } else {
                    this.sb_music.setMax(0);
                    this.sb_music.setProgress(0);
                }
                if (getActivity() instanceof MusicMainActivity) {
                    MusicMainActivity musicMainActivity3 = (MusicMainActivity) getActivity();
                    musicMainActivity3.setAlbumCover(R.mipmap.music_main_pic_cover);
                    if (this.musicLyricFragment != null && this.musicLyricFragment.isVisible()) {
                        musicMainActivity3.switchContent(this.musicLyricFragment.getFrontFragment());
                    }
                    musicMainActivity3.setSoundEffectVisible(true);
                }
                boolean z5 = CarLifeMusicManager.getInstance().getCarLifeMusicPlayStatus() == 1;
                this.iv_play.setImageResource(!z5 ? R.drawable.music_main_icon_pause : R.drawable.music_main_icon_playing);
                EventBus.getDefault().post(new MusicPlayStatusEvent(z5));
                break;
            default:
                i = 0;
                break;
        }
        if (i != 0 && (drawable = ContextCompat.getDrawable((Context) Objects.requireNonNull(getContext()), i)) != null) {
            drawable.setBounds(0, 0, 40, 40);
            this.tv_music_source.setCompoundDrawables(null, null, drawable, null);
        }
        if (this.music_source.equals(AppConstants.MUSIC_SOURCE_OF_BLUETOOTH) || this.music_source.equals(AppConstants.MUSIC_SOURCE_OF_CARLIFE) || this.music_source.equals(AppConstants.MUSIC_SOURCE_OF_CARPLAY)) {
            this.iv_func_1.setVisibility(4);
            this.iv_func_2.setVisibility(4);
            this.iv_func_3.setVisibility(4);
            this.music_play_lyrics.setVisibility(4);
            this.v_cover.setOnClickListener(null);
            this.tv_title.setOnClickListener(null);
            this.sb_music.setEnabled(false);
            this.sb_music.setThumb(getContext().getDrawable(R.color.transparent));
            return;
        }
        this.iv_func_1.setVisibility(0);
        this.iv_func_2.setVisibility(0);
        this.iv_func_3.setVisibility(0);
        this.music_play_lyrics.setVisibility(0);
        this.v_cover.setOnClickListener(this);
        this.tv_title.setOnClickListener(this);
        this.sb_music.setEnabled(true);
        this.sb_music.setThumb(getContext().getDrawable(R.drawable.seekbar_for_music_thumb));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    public MusicPlayerPresenter createPresenter() {
        return new MusicPlayerPresenter(getActivity());
    }

    @Override // com.yftech.libmusic.view.IMusicPlayerFragmentView
    public void displayMusicFiles(List<MediaFile> list) {
    }

    @Override // com.yftech.libmusic.view.IMusicPlayerFragmentView
    public void displayMusicLyricPath(List<LyricObject> list) {
    }

    @Override // com.yftech.libmusic.view.IMusicPlayerFragmentView
    public void displayThumbDataByPath(String str, byte[] bArr) {
        if (getActivity() instanceof MusicMainActivity) {
            ((MusicMainActivity) getActivity()).setAlbumCover(str, bArr);
        }
    }

    public String getMusic_source() {
        return this.music_source;
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    protected void initListener() {
        EventBus.getDefault().register(this);
        CarMusicManager.registerMusicControlCallback(this.callback);
        CarBluetoothManager.getInstance().registerBluetoothMusicCallback(this.musicCallback, (Handler) null);
        CarLifeMusicManager.getInstance().registerMusicControlCallback(this.carLifeDataChangedCallback);
        CarPlayMusicManager.getInstance(getActivity()).registerCarPlayMusicListener(this.carPlayMusicListener);
        CarAudioManager.getInstance().registerCarAudioCallback(this.carAudioCallback, (Handler) null);
        registerTestReceiver();
        registerHiCarReceiver();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    @SuppressLint({"ClickableViewAccessibility"})
    protected void initView(View view) throws Throwable {
        this.tv_music_progress = (TextView) view.findViewById(R.id.tv_music_progress);
        this.tv_music_duration = (TextView) view.findViewById(R.id.tv_music_duration);
        this.tv_seekBar_progress = (TextView) view.findViewById(R.id.tv_seekBar_progress);
        this.tv_seekBar_max = (TextView) view.findViewById(R.id.tv_seekBar_max);
        this.progress_index = (FrameLayout) view.findViewById(R.id.progress_index);
        this.tv_title = (TextView) view.findViewById(R.id.tv_title);
        this.tv_title.setOnClickListener(this);
        this.tv_artist = (TextView) view.findViewById(R.id.tv_artist);
        this.tv_blue_tips = (TextView) view.findViewById(R.id.tv_blue_tips);
        this.iv_play = (ImageView) view.findViewById(R.id.iv_music_play);
        this.iv_play.setOnClickListener(this);
        this.iv_last = (ImageView) view.findViewById(R.id.iv_last);
        this.iv_next = (ImageView) view.findViewById(R.id.iv_next);
        this.iv_func_1 = (ImageView) view.findViewById(R.id.iv_func_1);
        this.iv_func_2 = (ImageView) view.findViewById(R.id.iv_func_2);
        this.iv_func_3 = (ImageView) view.findViewById(R.id.iv_func_3);
        this.iv_last.setOnClickListener(this);
        this.iv_next.setOnClickListener(this);
        this.iv_func_1.setOnClickListener(this);
        this.iv_func_2.setOnClickListener(this);
        this.iv_func_3.setOnClickListener(this);
        this.music_play_lyrics = (LyricView) view.findViewById(R.id.music_play_lyrics);
        this.v_cover = view.findViewById(R.id.v_cover);
        this.sb_music = (SeekBar) view.findViewById(R.id.sb_music);
        this.sb_music.setOnTouchListener(new View.OnTouchListener() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.12
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                if (motionEvent.getAction() != 0) {
                    return false;
                }
                AudioSettingUtil.playSystemClickSoundEffect((Context) Objects.requireNonNull(MusicPlayerFragment.this.getContext()));
                return false;
            }
        });
        this.sb_music.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() { // from class: com.yftech.libmusic.ui.MusicPlayerFragment.13
            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                if (z) {
                    int[] iArr = new int[2];
                    seekBar.getLocationInWindow(iArr);
                    int max = seekBar.getMax();
                    int i2 = iArr[0];
                    int paddingStart = seekBar.getPaddingStart();
                    int iRound = Math.round((((((seekBar.getWidth() - paddingStart) - seekBar.getPaddingEnd()) * i) * 1.0f) / max) + i2 + paddingStart);
                    if (MusicPlayerFragment.this.progress_index != null) {
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) MusicPlayerFragment.this.progress_index.getLayoutParams();
                        if (MusicPlayerFragment.this.isRightStart) {
                            layoutParams.rightMargin = iRound - 1079;
                        } else {
                            layoutParams.leftMargin = (iRound - 120) - 140;
                        }
                        MusicPlayerFragment.this.progress_index.setLayoutParams(layoutParams);
                    }
                    if (MusicPlayerFragment.this.tv_seekBar_progress != null) {
                        MusicPlayerFragment.this.tv_seekBar_progress.setText(DateUtil.getMusicTime(i * 1000, false));
                    }
                    if (MusicPlayerFragment.this.tv_seekBar_max != null) {
                        MusicPlayerFragment.this.tv_seekBar_max.setText(DateUtil.getMusicTime(max * 1000, false));
                    }
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (MusicPlayerFragment.this.mHandler.hasMessages(10)) {
                    MusicPlayerFragment.this.mHandler.removeMessages(10);
                }
                MusicPlayerFragment.this.isPressing = true;
                if (MusicPlayerFragment.this.progress_index != null) {
                    MusicPlayerFragment.this.progress_index.setVisibility(0);
                }
                if (MusicPlayerFragment.this.tv_music_progress != null) {
                    MusicPlayerFragment.this.tv_music_progress.setVisibility(4);
                }
                if (MusicPlayerFragment.this.tv_music_duration != null) {
                    MusicPlayerFragment.this.tv_music_duration.setVisibility(4);
                }
            }

            @Override // android.widget.SeekBar.OnSeekBarChangeListener
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicPlayerFragment.this.isPressing = false;
                CarMusicManager.getInstance().setPlayProgress(seekBar.getProgress() * 1000);
                if (MusicPlayerFragment.this.mHandler.hasMessages(10)) {
                    MusicPlayerFragment.this.mHandler.removeMessages(10);
                }
                MusicPlayerFragment.this.mHandler.sendEmptyMessageDelayed(10, 2000L);
            }
        });
        switch (CarMusicManager.getInstance().getPlayMode()) {
            case 0:
                this.iv_func_2.setImageResource(R.drawable.music_main_icon_loop);
                break;
            case 1:
                this.iv_func_2.setImageResource(R.drawable.music_main_icon_single);
                break;
            case 2:
                this.iv_func_2.setImageResource(R.drawable.music_main_icon_random);
                break;
        }
        this.tv_music_source = (TextView) view.findViewById(R.id.tv_music_source);
        this.tv_music_source.setOnClickListener(this);
        setMusicSourceStyle();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    protected View onBindView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_music_player, viewGroup, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothStatusChange(BluetoothStateEvent bluetoothStateEvent) throws Throwable {
        LogUtil.e("onBluetoothStatusChange:event=" + bluetoothStateEvent.toString() + ", music_source=" + this.music_source);
        if (bluetoothStateEvent.state == 2 || !AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(this.music_source)) {
            return;
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof MusicMainActivity) {
            MusicMainActivity musicMainActivity = (MusicMainActivity) activity;
            musicMainActivity.setSoundEffectVisible(false);
            Fragment fragmentFindFragmentByTag = musicMainActivity.getSupportFragmentManager().findFragmentByTag(MusicMainFragment.class.getSimpleName());
            if (fragmentFindFragmentByTag == null) {
                fragmentFindFragmentByTag = MusicMainFragment.newInstance(this.music_source);
            }
            musicMainActivity.getSupportFragmentManager().beginTransaction().hide(this).show(fragmentFindFragmentByTag).commitAllowingStateLoss();
            musicMainActivity.currentFragment = fragmentFindFragmentByTag;
            musicMainActivity.setAlbumCover(R.mipmap.music_main_pic_cover);
        }
        EventBus.getDefault().post(new MusicPlayStatusEvent(false));
        SharedPrefsUtil.getInstance(getContext()).save(SharedPrefsUtil.TAG_MUSIC_SOURCE_PLAYING, "");
        closeMusicSoundEffectsDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCarPlayStatusChange(CarPlayStateEvent carPlayStateEvent) throws Throwable {
        LogUtil.e("onCarPlayStatusChange:receiveBroadCast");
        if (carPlayStateEvent.status == 0 && AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(this.music_source)) {
            FragmentActivity activity = getActivity();
            if (activity instanceof MusicMainActivity) {
                MusicMainActivity musicMainActivity = (MusicMainActivity) activity;
                musicMainActivity.getSupportFragmentManager().beginTransaction().hide(this).commitAllowingStateLoss();
                musicMainActivity.currentFragment = this;
                musicMainActivity.setSoundEffectVisible(false);
                Fragment fragmentFindFragmentByTag = musicMainActivity.getSupportFragmentManager().findFragmentByTag(MusicMainFragment.class.getSimpleName());
                if (fragmentFindFragmentByTag != null) {
                    musicMainActivity.switchContent(fragmentFindFragmentByTag);
                }
                musicMainActivity.setAlbumCover(R.mipmap.music_main_pic_cover);
            }
            EventBus.getDefault().post(new MusicPlayStatusEvent(false));
            SharedPrefsUtil.getInstance(getContext()).save(SharedPrefsUtil.TAG_MUSIC_SOURCE_PLAYING, "");
            closeMusicSoundEffectsDialog();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCarlifeStatusChange(CarLifeStatusEvent carLifeStatusEvent) throws Throwable {
        LogUtil.e("onCarlifeStatusChange:receiveBroadCast");
        if (carLifeStatusEvent.mIsCarLifeConnect || !AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(this.music_source)) {
            return;
        }
        FragmentActivity activity = getActivity();
        if (activity instanceof MusicMainActivity) {
            MusicMainActivity musicMainActivity = (MusicMainActivity) activity;
            Fragment fragmentFindFragmentByTag = musicMainActivity.getSupportFragmentManager().findFragmentByTag(MusicPlayerFragment.class.getSimpleName());
            if (fragmentFindFragmentByTag != null) {
                musicMainActivity.currentFragment = fragmentFindFragmentByTag;
            }
            Fragment fragmentFindFragmentByTag2 = musicMainActivity.getSupportFragmentManager().findFragmentByTag(MusicMainFragment.class.getSimpleName());
            if (fragmentFindFragmentByTag2 != null) {
                musicMainActivity.switchContent(fragmentFindFragmentByTag2);
            }
            musicMainActivity.setSoundEffectVisible(false);
            musicMainActivity.setAlbumCover(R.mipmap.music_main_pic_cover);
        }
        EventBus.getDefault().post(new MusicPlayStatusEvent(false));
        SharedPrefsUtil.getInstance(getContext()).save(SharedPrefsUtil.TAG_MUSIC_SOURCE_PLAYING, "");
        closeMusicSoundEffectsDialog();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (SystemUtil.isFastDoubleClick()) {
            return;
        }
        if (view.getId() == R.id.tv_music_source) {
            EventBus.getDefault().post(new MusicSourceDialogOpenEvent(this.music_source));
            return;
        }
        if (view.getId() == R.id.iv_music_play) {
            if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(this.music_source)) {
                String currentMediaSource = CarAudioManager.getInstance().getCurrentMediaSource();
                if (TextUtils.isEmpty(currentMediaSource)) {
                    return;
                }
                String[] strArrSplit = currentMediaSource.split(",");
                if (strArrSplit.length >= 3) {
                    String currentSourceByClientId = AudioSettingUtil.getCurrentSourceByClientId(strArrSplit[2]);
                    if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(currentSourceByClientId) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(currentSourceByClientId)) {
                        CarMusicManager.getInstance().switchPlayPause();
                        return;
                    } else {
                        CarMusicManager.getInstance().play();
                        return;
                    }
                }
                return;
            }
            if (AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(this.music_source)) {
                if (CarBluetoothManager.getInstance().isPlayingBluetoothMusic()) {
                    CarBluetoothManager.getInstance().pauseBluetoothMusic();
                    return;
                } else {
                    CarBluetoothManager.getInstance().startBluetoothMusic(true);
                    return;
                }
            }
            if (AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(this.music_source)) {
                CarLifeMusicManager.getInstance().setMusicControl((CarLifeMusicManager.getInstance().getCarLifeMusicPlayStatus() == 1 ? 1 : 0) ^ 1);
                return;
            } else {
                if (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(this.music_source)) {
                    if (CarPlayMusicManager.getInstance(getActivity()).isPlaying()) {
                        CarPlayMusicManager.getInstance(getActivity()).pause();
                        return;
                    } else {
                        CarPlayMusicManager.getInstance(getActivity()).play();
                        return;
                    }
                }
                return;
            }
        }
        if (view.getId() == R.id.iv_last) {
            if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(this.music_source)) {
                CarMusicManager.getInstance().previous();
                return;
            }
            if (AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(this.music_source)) {
                CarBluetoothManager.getInstance().previousBluetoothMusic();
                return;
            } else if (AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(this.music_source)) {
                CarLifeMusicManager.getInstance().setMusicControl(3);
                return;
            } else {
                if (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(this.music_source)) {
                    CarPlayMusicManager.getInstance(getActivity()).previous();
                    return;
                }
                return;
            }
        }
        if (view.getId() == R.id.iv_next) {
            if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(this.music_source)) {
                CarMusicManager.getInstance().next();
                return;
            }
            if (AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(this.music_source)) {
                CarBluetoothManager.getInstance().nextBluetoothMusic();
                return;
            } else if (AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(this.music_source)) {
                CarLifeMusicManager.getInstance().setMusicControl(2);
                return;
            } else {
                if (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(this.music_source)) {
                    CarPlayMusicManager.getInstance(getActivity()).next();
                    return;
                }
                return;
            }
        }
        if (view.getId() == R.id.iv_func_1) {
            if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(this.music_source) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(this.music_source)) {
                if (this.musicSongListDialog == null) {
                    this.musicSongListDialog = MediaDataListDialog.newInstance(!AppConstants.MUSIC_SOURCE_OF_USB1.equals(this.music_source) ? 1 : 0);
                }
                if (this.musicSongListDialog.isAdded()) {
                    return;
                }
                this.musicSongListDialog.show(getChildFragmentManager(), MusicSongListDialog.class.getSimpleName());
                return;
            }
            return;
        }
        if (view.getId() == R.id.iv_func_2) {
            CarMusicManager.getInstance().switchPlayMode();
            return;
        }
        if (view.getId() == R.id.iv_func_3) {
            ContextCompat.startActivity((Context) Objects.requireNonNull(getContext()), new Intent(getContext(), (Class<?>) MusicListFromHardDiskActivity.class), null);
            return;
        }
        if ((view.getId() == R.id.v_cover || view.getId() == R.id.tv_title) && (getActivity() instanceof MusicMainActivity)) {
            MusicMainActivity musicMainActivity = (MusicMainActivity) getActivity();
            MediaFile playMusic = CarMusicManager.getInstance().getPlayMusic();
            if (this.musicLyricFragment == null) {
                this.musicLyricFragment = MusicLyricFragment.newInstance(playMusic, this.music_source);
            } else {
                this.musicLyricFragment.setMediaFile(playMusic);
            }
            this.musicLyricFragment.setFrontFragment(this);
            if (CarMusicManager.getInstance().isPlaying()) {
                this.mHandler.removeCallbacks(this.setMusicProgressRunnable);
                this.mHandler.postDelayed(this.setMusicProgressRunnable, 100L);
            }
            musicMainActivity.currentFragment = this;
            musicMainActivity.switchContent(this.musicLyricFragment);
            musicMainActivity.setSoundEffectVisible(false);
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.music_source = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.d("onDestroyView");
        EventBus.getDefault().unregister(this);
        CarMusicManager.unregisterMusicControlCallback(this.callback);
        CarBluetoothManager.getInstance().unregisterBluetoothMusicCallback(this.musicCallback);
        CarLifeMusicManager.getInstance().unregisterMusicControlCallback(this.carLifeDataChangedCallback);
        CarPlayMusicManager.getInstance(getActivity()).unregisterCarPlayMusicListener(this.carPlayMusicListener);
        CarAudioManager.getInstance().unregisterCarAudioCallback(this.carAudioCallback);
        this.mHandler.removeCallbacks(this.setMusicProgressRunnable);
        this.mActivity.unregisterReceiver(this.receiver);
        this.mActivity.unregisterReceiver(this.hiCarReceiver);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        String currentMediaSource = CarAudioManager.getInstance().getCurrentMediaSource();
        LogUtil.d("MusicPlayer onResume:music_source=" + this.music_source + ", needPlay=" + this.isNeedToPlay + ",currentMediaSource==" + currentMediaSource);
        if (currentMediaSource != null && currentMediaSource.contains("com.yftech.media.radio")) {
            this.isNeedToPlay = true;
        }
        Locale locale = Locale.getDefault();
        this.isRightStart = locale.getLanguage().equals("ar") || locale.getLanguage().equals("he");
        this.tv_title.setSelected(true);
        this.tv_artist.setSelected(true);
        if (TextUtils.isEmpty(this.music_source)) {
            return;
        }
        if ((AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(this.music_source) && !CarBluetoothManager.getInstance().isConnected(12)) || ((AppConstants.MUSIC_SOURCE_OF_USB1.equals(this.music_source) && (!CarMediaDataManager.getInstance().isUsbExists(0) || CarMediaDataManager.getInstance().getMusicFileSize(0) <= 0)) || ((AppConstants.MUSIC_SOURCE_OF_USB2.equals(this.music_source) && (!CarMediaDataManager.getInstance().isUsbExists(1) || CarMediaDataManager.getInstance().getMusicFileSize(1) <= 0)) || ((AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(this.music_source) && !CarLifeMusicManager.getInstance().getCarLifeConnectionStatus()) || (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(this.music_source) && CarPlayMusicManager.getInstance(getActivity()).getConnectStatus() != 2))))) {
            LogUtil.e("the source is not available!!!!!!");
            return;
        }
        if (getActivity() instanceof MusicMainActivity) {
            MusicMainActivity musicMainActivity = (MusicMainActivity) getActivity();
            if (this.musicLyricFragment != null && this.musicLyricFragment.isVisible()) {
                musicMainActivity.getSupportFragmentManager().beginTransaction().remove(this.musicLyricFragment).show(this.musicLyricFragment.getFrontFragment()).commitAllowingStateLoss();
                musicMainActivity.currentFragment = this.musicLyricFragment.getFrontFragment();
                this.musicLyricFragment = null;
            }
        }
        if (!this.isNeedToPlay) {
            setMusicSourceStyleWithoutPlay();
        } else {
            if (isResumeByPowerOn()) {
                return;
            }
            setMusicSourceStyle();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        LogUtil.d("onStop");
        if (getActivity() instanceof MusicMainActivity) {
            MusicMainActivity musicMainActivity = (MusicMainActivity) getActivity();
            if (this.musicLyricFragment != null && this.musicLyricFragment.isVisible()) {
                musicMainActivity.getSupportFragmentManager().beginTransaction().remove(this.musicLyricFragment).show(this.musicLyricFragment.getFrontFragment()).commitAllowingStateLoss();
                musicMainActivity.currentFragment = this.musicLyricFragment.getFrontFragment();
                this.musicLyricFragment = null;
            }
        }
        if (this.musicSongListDialog == null || !this.musicSongListDialog.isVisible()) {
            return;
        }
        this.musicSongListDialog.dismissCustom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUSBStatusChange(USBStateEvent uSBStateEvent) throws Throwable {
        LogUtil.e("onUSBStatusChange:event=" + uSBStateEvent.toString());
        if (uSBStateEvent.isUsbExists) {
            return;
        }
        if (this.musicSongListDialog != null && this.musicSongListDialog.isVisible()) {
            this.musicSongListDialog.dismissCustom();
        }
        int i = -1;
        if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(this.music_source)) {
            i = 0;
        } else if (AppConstants.MUSIC_SOURCE_OF_USB2.equals(this.music_source)) {
            i = 1;
        }
        if (i == uSBStateEvent.usbIndex) {
            FragmentActivity activity = getActivity();
            if (activity instanceof MusicMainActivity) {
                MusicMainActivity musicMainActivity = (MusicMainActivity) activity;
                musicMainActivity.getSupportFragmentManager().beginTransaction().hide(this).commitAllowingStateLoss();
                musicMainActivity.currentFragment = this;
                musicMainActivity.setSoundEffectVisible(false);
                Fragment fragmentFindFragmentByTag = musicMainActivity.getSupportFragmentManager().findFragmentByTag(MusicLyricFragment.class.getSimpleName());
                if (fragmentFindFragmentByTag != null) {
                    activity.getSupportFragmentManager().beginTransaction().remove(fragmentFindFragmentByTag).commitNowAllowingStateLoss();
                }
                Fragment fragmentFindFragmentByTag2 = musicMainActivity.getSupportFragmentManager().findFragmentByTag(MusicMainFragment.class.getSimpleName());
                if (fragmentFindFragmentByTag2 != null) {
                    musicMainActivity.switchContent(fragmentFindFragmentByTag2);
                }
                musicMainActivity.setAlbumCover(R.mipmap.music_main_pic_cover);
            }
            EventBus.getDefault().post(new MusicPlayStatusEvent(false));
            SharedPrefsUtil.getInstance(getContext()).save(SharedPrefsUtil.TAG_MUSIC_SOURCE_PLAYING, "");
            closeMusicSoundEffectsDialog();
        }
    }

    public void setLyricProgress(int i) {
        int indexByProgress = getIndexByProgress(i);
        if (indexByProgress < 0) {
            return;
        }
        LogUtil.d("setLyricProgress:progress=" + i);
        Message messageObtainMessage = this.mHandler.obtainMessage(40);
        messageObtainMessage.arg1 = indexByProgress;
        messageObtainMessage.arg2 = 1;
        this.mHandler.sendMessageDelayed(messageObtainMessage, 200L);
    }

    public void setMusic_source(String str) {
        LogUtil.d("setMusic_source=" + str + ", isNeedToPlay=" + this.isNeedToPlay);
        this.music_source = str;
        this.isNeedToPlay = false;
    }

    public void setMusic_source(String str, boolean z) {
        LogUtil.d("setMusic_source=" + str + ", isNeedToPlay=" + z);
        this.music_source = str;
        this.isNeedToPlay = z;
    }
}
