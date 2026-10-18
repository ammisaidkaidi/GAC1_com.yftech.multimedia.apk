package com.yftech.libmusic.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.media.common.event.USBStateEvent;
import com.media.common.util.AppConstants;
import com.media.common.util.AudioSettingUtil;
import com.media.common.util.LogUtil;
import com.yftech.car.audio.CarAudioManager;
import com.yftech.car.bluetooth.CarBluetoothManager;
import com.yftech.car.media.data.CarMediaDataManager;
import com.yftech.car.media.music.CarMusicManager;
import com.yftech.libmusic.MusicMainActivity;
import com.yftech.libmusic.R;
import com.yftech.libmusic.dialog.MusicSourceDialog;
import com.yftech.libmusic.event.BluetoothStateEvent;
import com.yftech.libmusic.event.CarLifeStatusEvent;
import com.yftech.libmusic.event.CarPlayStateEvent;
import com.yftech.libmusic.event.MusicPlayStatusEvent;
import com.yftech.libmusic.event.MusicSourceChangedEvent;
import com.yftech.libmusic.event.MusicSourceDialogOpenEvent;
import com.yftech.libmusic.utils.CarLifeMusicManager;
import com.yftech.libmusic.utils.CarPlayMusicManager;
import com.yftech.libmvpmodule.mvp.BaseMvpFragment;
import com.yftech.libmvpmodule.mvp.BaseMvpPresenter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* JADX INFO: loaded from: classes4.dex */
public class MusicMainFragment extends BaseMvpFragment implements View.OnClickListener {
    private static final String MUSIC_SOURCE = "MUSIC_SOURCE";
    private MusicSourceDialog musicSourceDialog;
    private TextView tv_music_source;
    private final int RC_AUDIO = 10;
    private String musicSource = null;
    private Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.libmusic.ui.MusicMainFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message message) throws Throwable {
            if (message.what == 10) {
                String string = message.obj == null ? "" : message.obj.toString();
                String currentSourceByClientId = AudioSettingUtil.getCurrentSourceByClientId(string);
                if (!TextUtils.isEmpty(currentSourceByClientId) && (MusicMainFragment.this.getActivity() instanceof MusicMainActivity)) {
                    MusicMainActivity musicMainActivity = (MusicMainActivity) MusicMainFragment.this.getActivity();
                    Fragment fragmentFindFragmentByTag = MusicMainFragment.this.getActivity().getSupportFragmentManager().findFragmentByTag(MusicPlayerFragment.class.getSimpleName());
                    StringBuilder sb = new StringBuilder();
                    sb.append("onChangedMediaSource:isPlayingMusic=");
                    sb.append(fragmentFindFragmentByTag != null);
                    sb.append(",clientId:");
                    sb.append(string);
                    LogUtil.d(sb.toString());
                    if (TextUtils.isEmpty(string)) {
                        if (fragmentFindFragmentByTag == null) {
                            musicMainActivity.getSupportFragmentManager().beginTransaction().show(MusicMainFragment.this).commitNowAllowingStateLoss();
                            musicMainActivity.currentFragment = MusicMainFragment.this;
                        }
                        musicMainActivity.setSoundEffectVisible(false);
                        return;
                    }
                    if ((AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(currentSourceByClientId) && !CarBluetoothManager.getInstance().isConnected(12)) || ((AppConstants.MUSIC_SOURCE_OF_USB1.equals(currentSourceByClientId) && (!CarMediaDataManager.getInstance().isUsbExists(0) || CarMediaDataManager.getInstance().getMusicFileSize(0) <= 0)) || ((AppConstants.MUSIC_SOURCE_OF_USB2.equals(currentSourceByClientId) && (!CarMediaDataManager.getInstance().isUsbExists(1) || CarMediaDataManager.getInstance().getMusicFileSize(1) <= 0)) || ((AppConstants.MUSIC_SOURCE_OF_CARLIFE.equals(currentSourceByClientId) && !CarLifeMusicManager.getInstance().getCarLifeConnectionStatus()) || (AppConstants.MUSIC_SOURCE_OF_CARPLAY.equals(currentSourceByClientId) && CarPlayMusicManager.getInstance(MusicMainFragment.this.getActivity()).getConnectStatus() != 2))))) {
                        LogUtil.e("the source is not available!!!!!!");
                        return;
                    }
                    musicMainActivity.getSupportFragmentManager().beginTransaction().hide(MusicMainFragment.this).commitNowAllowingStateLoss();
                    musicMainActivity.currentFragment = null;
                    if (fragmentFindFragmentByTag == null) {
                        if (!TextUtils.isEmpty(currentSourceByClientId)) {
                            MusicMainFragment.this.switchMusicSource(currentSourceByClientId);
                        }
                    } else if (fragmentFindFragmentByTag instanceof MusicPlayerFragment) {
                        MusicPlayerFragment musicPlayerFragment = (MusicPlayerFragment) fragmentFindFragmentByTag;
                        musicPlayerFragment.setMusic_source(currentSourceByClientId);
                        musicMainActivity.switchContent(musicPlayerFragment);
                    }
                    musicMainActivity.setSoundEffectVisible(true);
                }
            }
        }
    };
    private final CarAudioManager.CarAudioCallback carAudioCallback = new CarAudioManager.CarAudioCallback() { // from class: com.yftech.libmusic.ui.MusicMainFragment.2
        public void onChangedMediaSource(int i, String str, String str2) {
            if (MusicMainFragment.this.mHandler.hasMessages(10)) {
                MusicMainFragment.this.mHandler.removeMessages(10);
            }
            Message messageObtainMessage = MusicMainFragment.this.mHandler.obtainMessage(10);
            messageObtainMessage.obj = str2;
            MusicMainFragment.this.mHandler.sendMessageDelayed(messageObtainMessage, 300L);
        }
    };

    public static MusicMainFragment newInstance(String str) {
        MusicMainFragment musicMainFragment = new MusicMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString(MUSIC_SOURCE, str);
        musicMainFragment.setArguments(bundle);
        return musicMainFragment;
    }

    private synchronized void openMusicSourceDialog(String str) {
        if (this.musicSourceDialog != null) {
            this.musicSourceDialog.dismissAllowingStateLoss();
            this.musicSourceDialog = null;
        }
        this.musicSourceDialog = MusicSourceDialog.newInstance(str);
        this.musicSourceDialog.setOnMusicSourceSelectedListener(new MusicSourceDialog.OnMusicSourceSelectedListener() { // from class: com.yftech.libmusic.ui.MusicMainFragment.3
            @Override // com.yftech.libmusic.dialog.MusicSourceDialog.OnMusicSourceSelectedListener
            public void onSelected(String str2, int i, final String str3) {
                if (TextUtils.isEmpty(str3)) {
                }
                MusicMainFragment.this.musicSource = str3;
                if (MusicMainFragment.this.getActivity() instanceof MusicMainActivity) {
                    final MusicMainActivity musicMainActivity = (MusicMainActivity) MusicMainFragment.this.getActivity();
                    Fragment fragmentFindFragmentByTag = musicMainActivity.getSupportFragmentManager().findFragmentByTag(MusicPlayerFragment.class.getSimpleName());
                    if (!(fragmentFindFragmentByTag instanceof MusicPlayerFragment)) {
                        if (fragmentFindFragmentByTag == null) {
                            MusicMainFragment.this.mHandler.post(new Runnable() { // from class: com.yftech.libmusic.ui.MusicMainFragment.3.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    musicMainActivity.switchContent(MusicPlayerFragment.newInstance(str3));
                                    musicMainActivity.setSoundEffectVisible(true);
                                }
                            });
                            return;
                        }
                        return;
                    }
                    MusicPlayerFragment musicPlayerFragment = (MusicPlayerFragment) fragmentFindFragmentByTag;
                    LogUtil.d("original music source=" + musicPlayerFragment.getMusic_source() + ", music source=" + str3);
                    switch (musicPlayerFragment.getMusic_source()) {
                        case "MUSIC_SOURCE_OF_USB1":
                        case "MUSIC_SOURCE_OF_USB2":
                            CarMusicManager.getInstance().pause();
                            break;
                        case "MUSIC_SOURCE_OF_BLUETOOTH":
                            CarBluetoothManager.getInstance().pauseBluetoothMusic();
                            break;
                        case "MUSIC_SOURCE_OF_CARLIFE":
                            CarLifeMusicManager.getInstance().setMusicControl(0);
                            break;
                        case "MUSIC_SOURCE_OF_CARPLAY":
                            CarPlayMusicManager.getInstance(MusicMainFragment.this.getActivity()).pause();
                            break;
                    }
                    musicPlayerFragment.setMusic_source(str3, true);
                    musicPlayerFragment.onResume();
                    switch (str3) {
                        case "MUSIC_SOURCE_OF_USB1":
                            CarMusicManager.getInstance().setCurrentUSB((byte) 0, true);
                            break;
                        case "MUSIC_SOURCE_OF_USB2":
                            CarMusicManager.getInstance().setCurrentUSB((byte) 1, true);
                            break;
                        case "MUSIC_SOURCE_OF_BLUETOOTH":
                            CarBluetoothManager.getInstance().startBluetoothMusic(true);
                            break;
                        case "MUSIC_SOURCE_OF_CARLIFE":
                            CarLifeMusicManager.getInstance().setMusicControl(1);
                            break;
                        case "MUSIC_SOURCE_OF_CARPLAY":
                            CarPlayMusicManager.getInstance(MusicMainFragment.this.getActivity()).play();
                            break;
                    }
                }
            }
        });
        this.musicSourceDialog.show(getChildFragmentManager(), MusicSourceDialog.class.getSimpleName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void switchMusicSource(String str) {
        LogUtil.d("switchMusicSource:music_source=" + str);
        this.musicSource = str;
        if (getActivity() instanceof MusicMainActivity) {
            MusicMainActivity musicMainActivity = (MusicMainActivity) getActivity();
            Fragment fragmentFindFragmentByTag = musicMainActivity.getSupportFragmentManager().findFragmentByTag(MusicPlayerFragment.class.getSimpleName());
            if (fragmentFindFragmentByTag == null) {
                musicMainActivity.switchContent(MusicPlayerFragment.newInstance(str));
            } else if (fragmentFindFragmentByTag instanceof MusicPlayerFragment) {
                MusicPlayerFragment musicPlayerFragment = (MusicPlayerFragment) fragmentFindFragmentByTag;
                musicPlayerFragment.setMusic_source(str);
                musicMainActivity.switchContent(musicPlayerFragment);
            }
            musicMainActivity.setSoundEffectVisible(true);
            EventBus.getDefault().post(new MusicSourceChangedEvent(str));
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    protected BaseMvpPresenter createPresenter() {
        return null;
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    protected void initView(View view) {
        this.tv_music_source = (TextView) view.findViewById(R.id.tv_music_source);
        this.tv_music_source.setOnClickListener(this);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    protected View onBindView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_music_main, viewGroup, false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBluetoothStatusChange(BluetoothStateEvent bluetoothStateEvent) throws Throwable {
        LogUtil.e("onBluetoothStatusChange:event=" + bluetoothStateEvent.toString());
        if (bluetoothStateEvent.state == 2) {
            String currentMediaSource = CarAudioManager.getInstance().getCurrentMediaSource();
            if (TextUtils.isEmpty(currentMediaSource)) {
                return;
            }
            String[] strArrSplit = currentMediaSource.split(",");
            if (strArrSplit.length >= 3) {
                String currentSourceByClientId = AudioSettingUtil.getCurrentSourceByClientId(strArrSplit[2]);
                LogUtil.d("onBluetoothStatusChange:sourceByClientId=" + currentSourceByClientId);
                this.musicSource = currentSourceByClientId;
                if (AppConstants.MUSIC_SOURCE_OF_BLUETOOTH.equals(currentSourceByClientId) && (getActivity() instanceof MusicMainActivity)) {
                    MusicMainActivity musicMainActivity = (MusicMainActivity) getActivity();
                    Fragment fragmentFindFragmentByTag = musicMainActivity.getSupportFragmentManager().findFragmentByTag(MusicPlayerFragment.class.getSimpleName());
                    if (fragmentFindFragmentByTag == null) {
                        musicMainActivity.switchContent(MusicPlayerFragment.newInstance(AppConstants.MUSIC_SOURCE_OF_BLUETOOTH));
                    } else if (fragmentFindFragmentByTag instanceof MusicPlayerFragment) {
                        MusicPlayerFragment musicPlayerFragment = (MusicPlayerFragment) fragmentFindFragmentByTag;
                        musicPlayerFragment.setMusic_source(AppConstants.MUSIC_SOURCE_OF_BLUETOOTH);
                        musicMainActivity.switchContent(musicPlayerFragment);
                    }
                    musicMainActivity.setSoundEffectVisible(true);
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCarPlayStatusChange(CarPlayStateEvent carPlayStateEvent) {
        LogUtil.d("onCarPlayStatusChange:event=" + carPlayStateEvent.toString());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCarlifeStatusChange(CarLifeStatusEvent carLifeStatusEvent) {
        LogUtil.d("onCarlifeStatusChange:event=" + carLifeStatusEvent.toString());
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.tv_music_source) {
            openMusicSourceDialog("");
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.musicSource = getArguments().getString(MUSIC_SOURCE);
        }
        CarAudioManager.getInstance().registerCarAudioCallback(this.carAudioCallback, (Handler) null);
        EventBus.getDefault().register(this);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        if (this.musicSourceDialog != null) {
            this.musicSourceDialog.onDestroy();
            this.musicSourceDialog = null;
        }
        CarAudioManager.getInstance().unregisterCarAudioCallback(this.carAudioCallback);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMusicSourceDialogOpen(MusicSourceDialogOpenEvent musicSourceDialogOpenEvent) {
        if (this.tv_music_source != null) {
            openMusicSourceDialog(musicSourceDialogOpenEvent.music_source);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        LogUtil.d("onStop");
        if (this.musicSourceDialog == null || !this.musicSourceDialog.isVisible()) {
            return;
        }
        this.musicSourceDialog.dismissCustom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUSBStatusChange(USBStateEvent uSBStateEvent) throws Throwable {
        LogUtil.e("onUSBStatusChange:event=" + uSBStateEvent.toString() + ",get music source:" + this.musicSource);
        if (uSBStateEvent.isUsbExists) {
            return;
        }
        if (this.musicSource == null || AppConstants.MUSIC_SOURCE_OF_USB1.equals(this.musicSource) || AppConstants.MUSIC_SOURCE_OF_USB2.equals(this.musicSource)) {
            EventBus.getDefault().post(new MusicPlayStatusEvent(false));
        }
    }
}
