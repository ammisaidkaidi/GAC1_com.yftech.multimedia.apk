package com.yftech.libradio.dialog;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.internal.logging.nano.MetricsProto;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.media.common.util.FastScroller;
import com.media.common.util.LogUtil;
import com.media.common.util.ToastUtils;
import com.yftech.car.media.radio.CarRadioManager;
import com.yftech.car.media.radio.entity.DataOfRadio;
import com.yftech.libmvpmodule.mvp.BaseDialogFragment;
import com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment;
import com.yftech.libradio.R;
import com.yftech.libradio.bean.RadioChannelInfo;
import com.yftech.libradio.dialog.adapter.RadioFavorAdapter;
import com.yftech.libradio.presenter.RadioFavorPresenter;
import com.yftech.libradio.view.IRadioFavorDialogView;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public class RadioFavorDialog extends BaseMvpDialogFragment<RadioFavorPresenter> implements IRadioFavorDialogView, View.OnClickListener {
    private static final String ARG_PARAM1 = "band";
    private int band;
    private ImageView iv_toBoard;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar pb_list;
    private RadioFavorAdapter radioFavorAdapter;
    private RelativeLayout rl_search_fail;
    private RecyclerView rv;
    private TextView tv_num;
    private boolean pullUp = false;
    private boolean isFirstFlag = false;
    private final CarRadioManager.CarRadioControlCallback mCarRadioControlCallback = new CarRadioManager.CarRadioControlCallback() { // from class: com.yftech.libradio.dialog.RadioFavorDialog.5
        public void onAudioFocusChange(int i) {
        }

        public void onFavorChange(int i, DataOfRadio dataOfRadio) {
        }

        public void onFreqChanage(int i, DataOfRadio dataOfRadio, DataOfRadio dataOfRadio2) {
            LogUtil.d("onFreqChanage() newChannel:" + dataOfRadio.toString());
            RadioFavorDialog.this.getPresenter().getFavors(RadioFavorDialog.this.band);
        }

        public void onFreqSignal(int i, DataOfRadio dataOfRadio, int i2) {
        }

        public void onPlayPause(boolean z) {
            LogUtil.d("onPlayPause() isPlaying:" + z);
            RadioFavorDialog.this.getPresenter().getFavors(RadioFavorDialog.this.band);
        }

        public void onSeekStatusChange(int i, int i2, int i3) {
        }
    };

    public static RadioFavorDialog newInstance(int i, BaseDialogFragment.OnDestotyListener onDestotyListener) {
        RadioFavorDialog radioFavorDialog = new RadioFavorDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("band", i);
        radioFavorDialog.setArguments(bundle);
        radioFavorDialog.setOnDestotyListener(onDestotyListener);
        return radioFavorDialog;
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
            this.iv_toBoard.animate().alpha(0.0f).setStartDelay(4000L).setDuration(1000L).setListener(new AnimatorListenerAdapter() { // from class: com.yftech.libradio.dialog.RadioFavorDialog.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    RadioFavorDialog.this.iv_toBoard.setVisibility(4);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment
    public RadioFavorPresenter createPresenter() {
        return new RadioFavorPresenter(getActivity());
    }

    @Override // com.yftech.libradio.view.IRadioFavorDialogView
    public void displayFavorList(int i, List<RadioChannelInfo> list) {
        this.pb_list.setVisibility(8);
        if (list == null || list.isEmpty()) {
            this.rl_search_fail.setVisibility(0);
            this.tv_num.setText(String.format(Locale.getDefault(), "(%d)", 0));
            return;
        }
        this.tv_num.setText(String.format(Locale.getDefault(), "(%d)", Integer.valueOf(list.size())));
        this.rl_search_fail.setVisibility(8);
        if (this.radioFavorAdapter != null) {
            this.radioFavorAdapter.setPlayingIndex(i, false);
            this.radioFavorAdapter.setNewData(list);
            if (i < 0 || !this.isFirstFlag) {
                return;
            }
            this.isFirstFlag = false;
            if (i == 0 || list.size() - i <= 4) {
                ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPositionWithOffset(i, 0);
            } else {
                ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPositionWithOffset(i - 1, 0);
            }
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment
    protected void initView(View view) {
        this.tv_num = (TextView) view.findViewById(R.id.tv_num);
        view.findViewById(R.id.iv_close).setOnClickListener(this);
        this.isFirstFlag = true;
        this.rl_search_fail = (RelativeLayout) view.findViewById(R.id.rl_search_fail);
        this.iv_toBoard = (ImageView) view.findViewById(R.id.iv_toBoard);
        this.iv_toBoard.setOnClickListener(this);
        this.pb_list = (ProgressBar) view.findViewById(R.id.pb_list);
        this.rv = (RecyclerView) view.findViewById(R.id.rv);
        this.linearLayoutManager = new LinearLayoutManager(getContext());
        this.rv.setLayoutManager(this.linearLayoutManager);
        StateListDrawable stateListDrawable = (StateListDrawable) ContextCompat.getDrawable((Context) Objects.requireNonNull(getContext()), R.drawable.thumb_drawable);
        Drawable drawable = ContextCompat.getDrawable((Context) Objects.requireNonNull(getContext()), R.drawable.line_drawable);
        if (stateListDrawable != null && drawable != null) {
            new FastScroller(this.rv, stateListDrawable, drawable, stateListDrawable, drawable, 20, 100, 20);
        }
        this.rv.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.yftech.libradio.dialog.RadioFavorDialog.1
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(@NonNull RecyclerView recyclerView, int i, int i2) {
                if (RadioFavorDialog.this.pullUp != (i2 < 0)) {
                    RadioFavorDialog.this.pullUp = i2 < 0;
                }
                RadioFavorDialog.this.pullUp = i2 < 0;
                LogUtil.d("onScrolled:pullUp=" + RadioFavorDialog.this.pullUp);
                int iFindFirstVisibleItemPosition = RadioFavorDialog.this.linearLayoutManager.findFirstVisibleItemPosition();
                int iFindLastVisibleItemPosition = RadioFavorDialog.this.linearLayoutManager.findLastVisibleItemPosition();
                LogUtil.d("firstVisibleItemPosition=" + iFindFirstVisibleItemPosition);
                LogUtil.d("lastVisibleItemPosition=" + iFindLastVisibleItemPosition);
                if (iFindFirstVisibleItemPosition <= 9 || iFindLastVisibleItemPosition >= RadioFavorDialog.this.radioFavorAdapter.getItemCount() - 1) {
                    RadioFavorDialog.this.setBoardVisible(false);
                } else if (RadioFavorDialog.this.pullUp) {
                    RadioFavorDialog.this.iv_toBoard.setImageResource(R.mipmap.radio_btn_reach_top);
                    RadioFavorDialog.this.setBoardVisible(true);
                } else {
                    RadioFavorDialog.this.iv_toBoard.setImageResource(R.mipmap.radio_btn_reach_bottom);
                    RadioFavorDialog.this.setBoardVisible(true);
                }
            }
        });
        this.radioFavorAdapter = new RadioFavorAdapter(getContext());
        this.radioFavorAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() { // from class: com.yftech.libradio.dialog.RadioFavorDialog.2
            @Override // com.chad.library.adapter.base.BaseQuickAdapter.OnItemChildClickListener
            public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view2, int i) {
                RadioChannelInfo item;
                if (view2.getId() != R.id.iv_like || (item = RadioFavorDialog.this.radioFavorAdapter.getItem(i)) == null) {
                    return;
                }
                if (!CarRadioManager.getInstance().removeFavor(item)) {
                    ToastUtils.showFailMessage(R.string.radio_channel_favor_dialog_remove_favor_failure, 1);
                    return;
                }
                ToastUtils.showNormalMessage(R.string.radio_channel_favor_dialog_remove_favor_successful, 1);
                int farqPosition = RadioFavorDialog.this.radioFavorAdapter.getFarqPosition();
                if (i < farqPosition) {
                    int i2 = farqPosition - 1;
                    RadioFavorDialog.this.radioFavorAdapter.setPlayingIndex(i2, i2 >= 0);
                } else if (i == RadioFavorDialog.this.radioFavorAdapter.getFarqPosition()) {
                    RadioFavorDialog.this.radioFavorAdapter.setPlayingIndex(-1, false);
                }
                RadioFavorDialog.this.radioFavorAdapter.remove(i);
                RadioFavorDialog.this.tv_num.setText(String.format(Locale.getDefault(), "(%d)", Integer.valueOf(RadioFavorDialog.this.radioFavorAdapter.getItemCount())));
                if (RadioFavorDialog.this.radioFavorAdapter.getItemCount() <= 0) {
                    RadioFavorDialog.this.rl_search_fail.setVisibility(0);
                }
            }
        });
        this.radioFavorAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() { // from class: com.yftech.libradio.dialog.RadioFavorDialog.3
            @Override // com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view2, int i) {
                RadioChannelInfo item = RadioFavorDialog.this.radioFavorAdapter.getItem(i);
                if (item == null || !item.isValid) {
                    return;
                }
                CarRadioManager.getInstance().setFreq(item.getFreq(), i);
                RadioFavorDialog.this.radioFavorAdapter.setPlayingIndex(i, true);
            }
        });
        this.rv.setAdapter(this.radioFavorAdapter);
        this.pb_list.setVisibility(0);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpDialogFragment
    protected View onBindView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.dialog_radio_favor, viewGroup, false);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.iv_close) {
            dismiss();
            return;
        }
        if (view.getId() == R.id.iv_toBoard) {
            if (this.pullUp) {
                this.rv.scrollToPosition(0);
                return;
            }
            int itemCount = this.radioFavorAdapter.getItemCount() - 1;
            if (itemCount >= 0) {
                this.rv.scrollToPosition(itemCount);
            }
        }
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (getArguments() != null) {
            this.band = getArguments().getInt("band");
        }
        LogUtil.d("onCreate() band:" + this.band);
        CarRadioManager.registerRadioControlCallback(this.mCarRadioControlCallback);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseDialogFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy()");
        CarRadioManager.unregisterRadioControlCallback(this.mCarRadioControlCallback);
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        LogUtil.d("onPause()");
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        LogUtil.d("onResume()");
        getPresenter().getFavors(this.band);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStart() {
        Window window;
        super.onStart();
        LogUtil.d("onStart()");
        Dialog dialog = getDialog();
        if (dialog == null || (window = dialog.getWindow()) == null) {
            return;
        }
        window.setBackgroundDrawableResource(17170445);
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.dimAmount = 0.0f;
        attributes.windowAnimations = R.style.DialogRightAnimation;
        attributes.width = MetricsProto.MetricsEvent.DIALOG_USER_ENABLE_CALLING;
        attributes.height = MetricsProto.MetricsEvent.ACTION_PERMISSION_DENIED_ADD_VOICEMAIL;
        attributes.gravity = BadgeDrawable.TOP_START;
        attributes.x = 158;
        attributes.y = 62;
        window.setAttributes(attributes);
    }

    @Override // androidx.fragment.app.DialogFragment, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        LogUtil.d("onStop()");
    }
}
