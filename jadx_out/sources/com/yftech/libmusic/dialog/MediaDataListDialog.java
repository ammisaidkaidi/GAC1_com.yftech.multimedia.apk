package com.yftech.libmusic.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.android.internal.logging.nano.MetricsProto;
import com.google.android.material.badge.BadgeDrawable;
import com.media.common.util.LogUtil;
import com.media.common.util.MediaDataFastScroller;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.media.music.CarMusicManager;
import com.yftech.car.widget.media.list.MediaDataList;
import com.yftech.car.widget.media.recyclerview.widget.LinearLayoutManager;
import com.yftech.car.widget.media.recyclerview.widget.RecyclerView;
import com.yftech.libmusic.R;
import com.yftech.libmusic.bean.MediaFileItem;
import com.yftech.libmusic.event.MusicPlayStatusEvent;
import com.yftech.libmusic.event.MusicSourceChangedEvent;
import com.yftech.libmusic.event.MusicSwitchEvent;
import com.yftech.libmusic.presenter.MusicSongListPresenter;
import com.yftech.libmusic.view.IMusicSongListDialogView;
import com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/* JADX INFO: loaded from: classes4.dex */
public class MediaDataListDialog extends BaseMvpDialogFragment<MusicSongListPresenter> implements IMusicSongListDialogView, View.OnClickListener {
    private static final String ARG_PARAM1 = "index";
    private int index;
    private ImageView iv_toBoard;
    private MediaDataList rv;
    private TextView tv_num;
    private boolean pullUp = false;
    private boolean isPlaying = false;
    private boolean dismissOutOfView = false;
    private final int RC_DISMISS = 10;
    private final int DISMISS_INTERVAL = 15000;
    private final int RC_REFRESH = 20;
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.libmusic.dialog.MediaDataListDialog.1
        @Override // android.os.Handler
        public void handleMessage(@NonNull Message message) {
            if (message.what == 10) {
                MediaDataListDialog.this.dismissOutOfView = false;
                MediaDataListDialog.this.dismissAllowingStateLoss();
            } else if (message.what == 20) {
                LogUtil.d("RC_REFRESH:position=" + message.arg1);
            }
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() { // from class: com.yftech.libmusic.dialog.MediaDataListDialog.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("test".equals(intent.getAction())) {
                MediaDataListDialog.this.getPresenter().getCurrentPlayList();
            }
        }
    };

    public static MediaDataListDialog newInstance(int i) {
        MediaDataListDialog mediaDataListDialog = new MediaDataListDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_PARAM1, i);
        mediaDataListDialog.setArguments(bundle);
        return mediaDataListDialog;
    }

    private void registerTestReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("test");
        this.mActivity.registerReceiver(this.receiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBoardVisible(boolean z) {
        if (!z) {
            this.iv_toBoard.animate().cancel();
            this.iv_toBoard.setVisibility(4);
        } else {
            this.iv_toBoard.animate().cancel();
            this.iv_toBoard.setVisibility(0);
            this.iv_toBoard.setAlpha(1.0f);
            this.iv_toBoard.animate().alpha(0.0f).setStartDelay(4000L).setDuration(1000L).setListener(new AnimatorListenerAdapter() { // from class: com.yftech.libmusic.dialog.MediaDataListDialog.5
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    MediaDataListDialog.this.iv_toBoard.setVisibility(4);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment
    public MusicSongListPresenter createPresenter() {
        return new MusicSongListPresenter(getActivity());
    }

    public void dismissCustom() {
        LogUtil.d("dismissCustom");
        this.dismissOutOfView = false;
        dismissAllowingStateLoss();
    }

    @Override // com.yftech.libmusic.view.IMusicSongListDialogView
    public void displayCurrentPlayList(int i, List<MediaFileItem> list) {
        LogUtil.d("displayCurrentPlayList:playingIndex=" + i);
    }

    @Override // com.yftech.libmusic.view.IMusicSongListDialogView
    public void displayMusicPlayPosition(int i) {
        LogUtil.d("displayMusicPlayPosition:position=" + i);
    }

    @Override // com.yftech.libmusic.view.IMusicSongListDialogView
    public void displayPlayListSize(int i) {
        if (isAdded()) {
            this.tv_num.setText(String.format(Locale.getDefault(), "(%d)", Integer.valueOf(i)));
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment
    protected void initView(View view) {
        ((ImageView) view.findViewById(R.id.iv_close)).setOnClickListener(this);
        this.tv_num = (TextView) view.findViewById(R.id.tv_num);
        this.iv_toBoard = (ImageView) view.findViewById(R.id.iv_toBoard);
        this.iv_toBoard.setOnClickListener(this);
        this.rv = (MediaDataList) view.findViewById(R.id.rv);
        StateListDrawable stateListDrawable = (StateListDrawable) ContextCompat.getDrawable((Context) Objects.requireNonNull(getContext()), R.drawable.thumb_drawable);
        Drawable drawable = ContextCompat.getDrawable((Context) Objects.requireNonNull(getContext()), R.drawable.line_drawable);
        if (stateListDrawable != null && drawable != null) {
            new MediaDataFastScroller(this.rv, stateListDrawable, drawable, stateListDrawable, drawable, 20, 100, 20);
        }
        this.rv.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.yftech.libmusic.dialog.MediaDataListDialog.3
            @Override // com.yftech.car.widget.media.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(@NonNull RecyclerView recyclerView, int i, int i2) {
                if (MediaDataListDialog.this.mHandler.hasMessages(10)) {
                    MediaDataListDialog.this.mHandler.removeMessages(10);
                }
                MediaDataListDialog.this.mHandler.sendEmptyMessageDelayed(10, 15000L);
                if (MediaDataListDialog.this.pullUp != (i2 < 0)) {
                    MediaDataListDialog.this.pullUp = i2 < 0;
                }
                MediaDataListDialog.this.pullUp = i2 < 0;
                LogUtil.d("onScrolled:pullUp=" + MediaDataListDialog.this.pullUp);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) MediaDataListDialog.this.rv.getLayoutManager();
                int iFindFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                int iFindLastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                LogUtil.d("firstVisibleItemPosition=" + iFindFirstVisibleItemPosition);
                LogUtil.d("lastVisibleItemPosition=" + iFindLastVisibleItemPosition);
                if (iFindFirstVisibleItemPosition <= 7 || iFindLastVisibleItemPosition >= MediaDataListDialog.this.rv.getAdapter().getItemCount() - 1) {
                    MediaDataListDialog.this.setBoardVisible(false);
                } else if (MediaDataListDialog.this.pullUp) {
                    MediaDataListDialog.this.iv_toBoard.setImageResource(R.mipmap.music_btn_reach_top);
                    MediaDataListDialog.this.setBoardVisible(true);
                } else {
                    MediaDataListDialog.this.iv_toBoard.setImageResource(R.mipmap.music_btn_reach_bottom);
                    MediaDataListDialog.this.setBoardVisible(true);
                }
            }
        });
        this.rv.setOnItemChangeListener(new MediaDataList.OnItemChangeListener() { // from class: com.yftech.libmusic.dialog.MediaDataListDialog.4
            @Override // com.yftech.car.widget.media.list.MediaDataList.OnItemChangeListener
            public void onItemFile(MediaFile mediaFile) {
                if (MediaDataListDialog.this.mHandler.hasMessages(10)) {
                    MediaDataListDialog.this.mHandler.removeMessages(10);
                }
                MediaDataListDialog.this.mHandler.sendEmptyMessageDelayed(10, 15000L);
            }
        });
        getPresenter().getPlayListSize();
        getPresenter().getCurrentPlayList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void musicPlayStatusChanged(MusicPlayStatusEvent musicPlayStatusEvent) {
        LogUtil.d("musicPlayStatusChanged:isPlaying=" + musicPlayStatusEvent.isPlaying);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void musicSwitch(MusicSwitchEvent musicSwitchEvent) {
        LogUtil.d("musicSwitch");
        if (isAdded()) {
            getPresenter().getPlayPosition();
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment
    protected View onBindView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.dialog_media_data_list, viewGroup, false);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnCancelListener
    public void onCancel(@NonNull DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        this.dismissOutOfView = true;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mHandler.hasMessages(10)) {
            this.mHandler.removeMessages(10);
        }
        this.mHandler.sendEmptyMessageDelayed(10, 15000L);
        if (view.getId() == R.id.iv_toBoard) {
            if (this.pullUp) {
                this.rv.scrollToPosition(0);
                return;
            } else {
                this.rv.scrollToPosition(this.rv.getAdapter().getItemCount() - 1);
                return;
            }
        }
        if (view.getId() == R.id.iv_close) {
            this.dismissOutOfView = false;
            dismiss();
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.index = getArguments().getInt(ARG_PARAM1);
        }
        this.isPlaying = CarMusicManager.getInstance().isPlaying();
        EventBus.getDefault().register(this);
        registerTestReceiver();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseDialogFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy");
        this.mHandler.removeMessages(10);
        EventBus.getDefault().unregister(this);
        this.mActivity.unregisterReceiver(this.receiver);
    }

    @Override // androidx.fragment.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(@NonNull DialogInterface dialogInterface) {
        super.onDismiss(dialogInterface);
        this.dismissOutOfView = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMusicSourceChanged(MusicSourceChangedEvent musicSourceChangedEvent) {
        if (isAdded()) {
            getPresenter().getPlayListSize();
            getPresenter().getCurrentPlayList();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mHandler.hasMessages(10)) {
            this.mHandler.removeMessages(10);
        }
        this.mHandler.sendEmptyMessageDelayed(10, 15000L);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        Window window;
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null || (window = dialog.getWindow()) == null) {
            return;
        }
        window.setBackgroundDrawableResource(17170445);
        WindowManager.LayoutParams attributes = window.getAttributes();
        if ("ar".equals(Locale.getDefault().getLanguage())) {
            attributes.gravity = BadgeDrawable.TOP_END;
            attributes.windowAnimations = R.style.DialogLeftAnimation;
        } else {
            attributes.gravity = BadgeDrawable.TOP_START;
            attributes.windowAnimations = R.style.DialogRightAnimation;
        }
        attributes.dimAmount = 0.0f;
        attributes.width = MetricsProto.MetricsEvent.DIALOG_DREAM_START_DELAY;
        attributes.height = MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_PROCESS_OUTGOING_CALLS;
        attributes.y = 60;
        attributes.x = 180;
        window.setAttributes(attributes);
    }
}
