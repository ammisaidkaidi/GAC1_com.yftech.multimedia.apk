package com.yftech.libmusic.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.wifi.WifiEnterpriseConfig;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.media.common.event.USBStateEvent;
import com.media.common.ui.widget.ScrollableTextView;
import com.media.common.util.AppConstants;
import com.media.common.util.FastScroller;
import com.media.common.util.LogUtil;
import com.media.common.util.ToastUtils;
import com.yftech.car.media.data.CarMediaDataManager;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.media.music.CarMusicManager;
import com.yftech.libmusic.R;
import com.yftech.libmusic.bean.MusicListItem;
import com.yftech.libmusic.event.MusicPlayStatusEvent;
import com.yftech.libmusic.event.MusicSourceChangedEvent;
import com.yftech.libmusic.event.MusicSwitchEvent;
import com.yftech.libmusic.presenter.MusicListPresenter;
import com.yftech.libmusic.ui.adapter.MusicListForHardDiskAdapter;
import com.yftech.libmusic.view.IMusicListActivityView;
import com.yftech.libmvpmodule.mvp.BaseMvpActivity;
import java.util.List;
import java.util.Locale;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* JADX INFO: loaded from: classes4.dex */
public class MusicListFromHardDiskActivity extends BaseMvpActivity<MusicListPresenter> implements IMusicListActivityView, View.OnClickListener {
    private TextView btn_playAll;
    private int index;
    private ImageView iv_start;
    private ImageView iv_tag;
    private LinearLayoutManager linearLayoutManager;
    private MusicListForHardDiskAdapter musicListForHardDiskAdapter;
    private RecyclerView rv;
    private TextView tv_actionbar_title;
    private TextView tv_file_name;
    private TextView tv_music_count;
    private ScrollableTextView tv_subtitle;
    private boolean isUserSwitch = false;
    private String indexFolder = null;
    private final int RC_REFRESH = 10;
    private final int RC_TEST = 20;
    private Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.libmusic.ui.MusicListFromHardDiskActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 10) {
                if (message.what == 20) {
                    if (message.arg1 == 0) {
                        CarMusicManager.getInstance().next();
                        return;
                    } else {
                        CarMusicManager.getInstance().previous();
                        return;
                    }
                }
                return;
            }
            int i = message.arg1;
            String string = message.obj.toString();
            LogUtil.d("RC_REFRESH:position=" + i + ",path" + string);
            MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.notifyItemChanged(i, string);
        }
    };
    private BroadcastReceiver testReceiver = new BroadcastReceiver() { // from class: com.yftech.libmusic.ui.MusicListFromHardDiskActivity.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("test".equals(intent.getAction())) {
                int intExtra = intent.getIntExtra("value", 0);
                Message messageObtainMessage = MusicListFromHardDiskActivity.this.mHandler.obtainMessage(20);
                messageObtainMessage.arg1 = intExtra;
                MusicListFromHardDiskActivity.this.mHandler.sendMessage(messageObtainMessage);
            }
        }
    };
    private CarMusicManager.CarMusicControlCallback callback = new CarMusicManager.CarMusicControlCallback() { // from class: com.yftech.libmusic.ui.MusicListFromHardDiskActivity.3
        public void onChangePlayMusic(MediaFile mediaFile) throws Throwable {
            if (MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter == null || MusicListFromHardDiskActivity.this.isUserSwitch) {
                MusicListFromHardDiskActivity.this.isUserSwitch = false;
            } else {
                MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setPlaying(mediaFile.getData(), true);
            }
        }

        public void onChangePlaying(boolean z) {
            if (MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter != null) {
                MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setPlayStatus(z);
            }
            MusicListFromHardDiskActivity.this.iv_start.setSelected(z);
        }
    };

    private void checkParentList() {
        List<MusicListItem> parentList = this.musicListForHardDiskAdapter.getParentList();
        if (parentList == null) {
            onBackPressed();
            return;
        }
        this.musicListForHardDiskAdapter.setNewData(parentList);
        this.musicListForHardDiskAdapter.setParentList(null);
        this.tv_file_name.setText(getString(this.index == 0 ? R.string.music_source_selection_1 : R.string.music_source_selection_2));
        this.tv_subtitle.setContent(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        this.btn_playAll.setVisibility(0);
        this.indexFolder = null;
        this.tv_music_count.setText(String.format(Locale.getDefault(), "(%d)", Integer.valueOf(CarMediaDataManager.getInstance().getMusicFileSize(this.index))));
    }

    private void initData() {
        MediaFile playMusic = CarMusicManager.getInstance().getPlayMusic();
        if (playMusic == null || TextUtils.isEmpty(playMusic.getData())) {
            this.index = -1;
        } else {
            int usbIndex = CarMediaDataManager.getInstance().getUsbIndex(playMusic.getData());
            LogUtil.d("usbIndex=" + usbIndex);
            this.index = usbIndex;
        }
        LogUtil.d("onResume:index=" + this.index);
        if (this.index < 0) {
            this.tv_actionbar_title.setText(".");
            return;
        }
        this.musicListForHardDiskAdapter.setParentList(null);
        this.tv_actionbar_title.setText(getString(this.index == 0 ? R.string.music_source_selection_1 : R.string.music_source_selection_2));
        this.tv_file_name.setText(getString(this.index == 0 ? R.string.music_source_selection_1 : R.string.music_source_selection_2));
        this.tv_subtitle.setContent(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        this.btn_playAll.setVisibility(0);
        this.indexFolder = null;
        getPresenter().getMusicListData(this.index);
    }

    private void initView() {
        this.tv_actionbar_title = (TextView) findViewById(R.id.tv_actionbar_title);
        this.tv_actionbar_title.setOnClickListener(this);
        this.tv_subtitle = (ScrollableTextView) findViewById(R.id.tv_subtitle);
        this.tv_subtitle.setContent("");
        this.tv_file_name = (TextView) findViewById(R.id.tv_file_name);
        this.tv_music_count = (TextView) findViewById(R.id.tv_music_count);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_search).setOnClickListener(this);
        this.btn_playAll = (TextView) findViewById(R.id.btn_playAll);
        this.btn_playAll.setOnClickListener(this);
        this.iv_tag = (ImageView) findViewById(R.id.iv_tag);
        this.iv_tag.setOnClickListener(this);
        this.iv_start = (ImageView) findViewById(R.id.iv_start);
        this.iv_start.setOnClickListener(this);
        this.iv_start.setSelected(CarMusicManager.getInstance().isPlaying());
        this.rv = (RecyclerView) findViewById(R.id.rv);
        this.linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.rv.setLayoutManager(this.linearLayoutManager);
        this.rv.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: com.yftech.libmusic.ui.MusicListFromHardDiskActivity.4
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(@NonNull Rect rect, @NonNull View view, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state) {
                rect.bottom = 40;
            }
        });
        StateListDrawable stateListDrawable = (StateListDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.thumb_drawable);
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.line_drawable);
        if (stateListDrawable != null && drawable != null) {
            new FastScroller(this.rv, stateListDrawable, drawable, stateListDrawable, drawable, 20, 100, 20);
        }
        this.musicListForHardDiskAdapter = new MusicListForHardDiskAdapter(getApplicationContext());
        this.musicListForHardDiskAdapter.setPlayStatus(CarMusicManager.getInstance().isPlaying());
        this.musicListForHardDiskAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() { // from class: com.yftech.libmusic.ui.MusicListFromHardDiskActivity.5
            @Override // com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) throws Throwable {
                MusicListItem item = MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getItem(i);
                if (item == null || !item.isDir()) {
                    if (item != null) {
                        MusicListFromHardDiskActivity.this.isUserSwitch = true;
                        CarMusicManager.getInstance().start(item.getFoloder(), item.getFilePath(), 1);
                        if (!TextUtils.isEmpty(MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getPlayingPath()) && !MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getPlayingPath().equals(item.getFilePath())) {
                            MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setPlaying(item.getFilePath(), true);
                        }
                        EventBus.getDefault().post(new MusicSourceChangedEvent(MusicListFromHardDiskActivity.this.index == 0 ? AppConstants.MUSIC_SOURCE_OF_USB1 : AppConstants.MUSIC_SOURCE_OF_USB2));
                        return;
                    }
                    return;
                }
                MusicListFromHardDiskActivity.this.tv_file_name.setText(item.getTitle());
                MusicListFromHardDiskActivity.this.tv_subtitle.setContent(item.getTitle());
                MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setParentList(MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getData());
                MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setNewData(item.getData());
                MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setPlaying(MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getPlayingPath(), false);
                MusicListFromHardDiskActivity.this.rv.scrollToPosition(MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getPosition());
                MusicListFromHardDiskActivity.this.btn_playAll.setVisibility(4);
                if (item.getData() != null) {
                    MusicListFromHardDiskActivity.this.tv_music_count.setText(String.format(Locale.getDefault(), "(%d)", Integer.valueOf(item.getData().size())));
                } else {
                    MusicListFromHardDiskActivity.this.tv_music_count.setText(String.format(Locale.getDefault(), "(%d)", 0));
                }
                MusicListFromHardDiskActivity.this.indexFolder = item.getFoloder();
            }
        });
        this.musicListForHardDiskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() { // from class: com.yftech.libmusic.ui.MusicListFromHardDiskActivity.6
            @Override // com.chad.library.adapter.base.BaseQuickAdapter.OnItemChildClickListener
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view, int i) throws Throwable {
                MusicListItem item = MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getItem(i);
                if (item == null || !item.isDir()) {
                    if (item != null) {
                        MusicListFromHardDiskActivity.this.isUserSwitch = true;
                        CarMusicManager.getInstance().start(item.getFoloder(), item.getFilePath(), 1);
                        if (!TextUtils.isEmpty(MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getPlayingPath()) && !MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getPlayingPath().equals(item.getFilePath())) {
                            MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setPlaying(item.getFilePath(), true);
                        }
                        EventBus.getDefault().post(new MusicSourceChangedEvent(MusicListFromHardDiskActivity.this.index == 0 ? AppConstants.MUSIC_SOURCE_OF_USB1 : AppConstants.MUSIC_SOURCE_OF_USB2));
                        return;
                    }
                    return;
                }
                MusicListFromHardDiskActivity.this.tv_file_name.setText(item.getTitle());
                MusicListFromHardDiskActivity.this.tv_subtitle.setContent(item.getTitle());
                MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setParentList(MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getData());
                MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setNewData(item.getData());
                MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.setPlaying(MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getPlayingPath(), false);
                MusicListFromHardDiskActivity.this.rv.scrollToPosition(MusicListFromHardDiskActivity.this.musicListForHardDiskAdapter.getPosition());
                MusicListFromHardDiskActivity.this.btn_playAll.setVisibility(4);
                if (item.getData() != null) {
                    MusicListFromHardDiskActivity.this.tv_music_count.setText(String.format(Locale.getDefault(), "(%d)", Integer.valueOf(item.getData().size())));
                } else {
                    MusicListFromHardDiskActivity.this.tv_music_count.setText(String.format(Locale.getDefault(), "(%d)", 0));
                }
                MusicListFromHardDiskActivity.this.indexFolder = item.getFoloder();
            }
        });
        this.rv.setAdapter(this.musicListForHardDiskAdapter);
    }

    private void registerTestReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("test");
        registerReceiver(this.testReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity
    public MusicListPresenter createPresenter() {
        return new MusicListPresenter(this);
    }

    @Override // com.yftech.libmusic.view.IMusicListActivityView
    public void displayMusicList(MediaFile mediaFile, List<MusicListItem> list) throws Throwable {
        if (list == null || mediaFile == null) {
            return;
        }
        this.musicListForHardDiskAdapter.setPlaying(mediaFile.getData(), false);
        this.musicListForHardDiskAdapter.setNewData(list);
        this.indexFolder = null;
        this.tv_music_count.setText(String.format(Locale.getDefault(), "(%d)", Integer.valueOf(CarMediaDataManager.getInstance().getMusicFileSize(this.index))));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void musicPlayStatusChanged(MusicPlayStatusEvent musicPlayStatusEvent) {
        LogUtil.d("musicPlayStatusChanged:isPlaying=" + musicPlayStatusEvent.isPlaying);
        if (!this.isDisplaying || TextUtils.isEmpty(this.indexFolder) || this.musicListForHardDiskAdapter == null) {
            return;
        }
        this.musicListForHardDiskAdapter.notifyItemChanged(this.musicListForHardDiskAdapter.getPosition(), this.musicListForHardDiskAdapter.getPlayingPath());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void musicSwitch(MusicSwitchEvent musicSwitchEvent) {
        if (TextUtils.isEmpty(this.indexFolder) || this.musicListForHardDiskAdapter == null) {
            return;
        }
        int positionByPath = this.musicListForHardDiskAdapter.getPositionByPath(musicSwitchEvent.mediaFile.getData());
        String data = musicSwitchEvent.mediaFile.getData();
        int iFindFirstVisibleItemPosition = this.linearLayoutManager.findFirstVisibleItemPosition();
        int iFindLastVisibleItemPosition = this.linearLayoutManager.findLastVisibleItemPosition();
        LogUtil.d("musicSwitch:firstVisibleItemPosition=" + iFindFirstVisibleItemPosition + ", lastVisibleItemPosition=" + iFindLastVisibleItemPosition + ", position=" + positionByPath);
        if (positionByPath > iFindFirstVisibleItemPosition && positionByPath < iFindLastVisibleItemPosition) {
            this.musicListForHardDiskAdapter.notifyItemChanged(positionByPath, data);
            return;
        }
        this.rv.smoothScrollToPosition(positionByPath);
        if (this.mHandler.hasMessages(10)) {
            this.mHandler.removeMessages(10);
        }
        Message messageObtainMessage = this.mHandler.obtainMessage(10);
        messageObtainMessage.arg1 = positionByPath;
        messageObtainMessage.obj = data;
        this.mHandler.sendMessageDelayed(messageObtainMessage, 300L);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        MediaFile playMusic;
        if (view.getId() == R.id.iv_back || view.getId() == R.id.tv_actionbar_title || view.getId() == R.id.tv_subtitle) {
            checkParentList();
            return;
        }
        if (view.getId() == R.id.iv_search) {
            Intent intent = new Intent(this, (Class<?>) MusicSearchFromHardDiskActivity.class);
            intent.putExtra("index", this.index);
            ContextCompat.startActivity(this, intent, null);
            return;
        }
        if (view.getId() != R.id.iv_start && view.getId() != R.id.iv_tag) {
            if (view.getId() != R.id.btn_playAll || (playMusic = CarMusicManager.getInstance().getPlayMusic()) == null || TextUtils.isEmpty(playMusic.getData())) {
                return;
            }
            List fileMusic = CarMediaDataManager.getInstance().getFileMusic(playMusic.getData());
            if (fileMusic.isEmpty()) {
                return;
            }
            CarMusicManager.getInstance().start((MediaFile) fileMusic.get(0), 2);
            if (CarMusicManager.getInstance().getPlayMode() != 0) {
                CarMusicManager.getInstance().setPlayMode(0);
            }
            ToastUtils.showNormalMessage(getString(R.string.music_play_all_tips), 1);
            return;
        }
        if (CarMusicManager.getInstance().isPlaying()) {
            CarMusicManager.getInstance().pause();
            return;
        }
        MediaFile playMusic2 = CarMusicManager.getInstance().getPlayMusic();
        String folder = playMusic2.getFolder();
        if (TextUtils.isEmpty(this.indexFolder)) {
            CarMusicManager.getInstance().start(playMusic2, 2);
            return;
        }
        if (!this.indexFolder.equals(folder)) {
            List<MusicListItem> data = this.musicListForHardDiskAdapter.getData();
            if (!data.isEmpty()) {
                MusicListItem musicListItem = data.get(0);
                if (!musicListItem.isDir()) {
                    MediaFile mediaFile = new MediaFile(0);
                    mediaFile.setTitle(musicListItem.getTitle());
                    mediaFile.setAlbum(musicListItem.getAlbum());
                    mediaFile.setArtist(musicListItem.getArtist());
                    mediaFile.setData(musicListItem.getFilePath());
                    mediaFile.setDuration(musicListItem.getDuration());
                    CarMusicManager.getInstance().start(mediaFile, 1);
                }
            }
            this.rv.smoothScrollToPosition(0);
            return;
        }
        CarMusicManager.getInstance().start(playMusic2, 1);
        List<MusicListItem> data2 = this.musicListForHardDiskAdapter.getData();
        int i = -1;
        if (!data2.isEmpty()) {
            for (int i2 = 0; i2 < data2.size(); i2++) {
                MusicListItem musicListItem2 = data2.get(i2);
                if (!TextUtils.isEmpty(musicListItem2.getFilePath()) && musicListItem2.getFilePath().equals(playMusic2.getData())) {
                    i = i2;
                    break;
                }
            }
        }
        int iFindFirstVisibleItemPosition = this.linearLayoutManager.findFirstVisibleItemPosition();
        int iFindLastVisibleItemPosition = this.linearLayoutManager.findLastVisibleItemPosition();
        if (i >= 0) {
            if (i <= iFindFirstVisibleItemPosition || i >= iFindLastVisibleItemPosition) {
                this.rv.smoothScrollToPosition(i);
            }
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_music_list_from_hard_disk);
        initView();
        EventBus.getDefault().register(this);
        registerTestReceiver();
        CarMusicManager.registerMusicControlCallback(this.callback);
        initData();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        CarMusicManager.unregisterMusicControlCallback(this.callback);
        unregisterReceiver(this.testReceiver);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4 || keyEvent.getRepeatCount() != 0) {
            return super.onKeyDown(i, keyEvent);
        }
        LogUtil.d("onKeyDown");
        checkParentList();
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMusicSourceChanged(MusicSourceChangedEvent musicSourceChangedEvent) {
        if (AppConstants.MUSIC_SOURCE_OF_USB1.equals(musicSourceChangedEvent.music_source) && this.index == 0) {
            return;
        }
        if (AppConstants.MUSIC_SOURCE_OF_USB2.equals(musicSourceChangedEvent.music_source) && this.index == 1) {
            return;
        }
        onBackPressed();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUSBStatusChange(USBStateEvent uSBStateEvent) {
        LogUtil.d("onUSBStatusChange:receiveBroadCast");
        if (uSBStateEvent.isUsbExists || this.index != uSBStateEvent.usbIndex) {
            return;
        }
        onBackPressed();
    }
}
