package com.yftech.libradio;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.media.common.util.LogUtil;
import com.yftech.car.media.radio.CarRadioManager;
import com.yftech.car.media.radio.entity.DataOfRadio;
import com.yftech.car.media.radio.rds.entity.RdsInfo;
import com.yftech.libmvpmodule.mvp.BaseMvpActivity;
import com.yftech.libradio.dialog.RadioSearchingDialog;
import com.yftech.libradio.dialog.RdsSettingsDialog;
import com.yftech.libradio.presenter.RadioSearchChannelPresenter;
import com.yftech.libradio.rds.RdsDataManager;
import com.yftech.libradio.view.IRadioSearchChannelDialogView;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: classes4.dex */
public class RadioListActivity extends BaseMvpActivity<RadioSearchChannelPresenter> implements IRadioSearchChannelDialogView, View.OnClickListener {
    public static final String ARG_PARAM1 = "band";
    private boolean afEnable;
    private int band;
    private String currentPsString;
    private String currentPtyStr;
    private boolean hasScrolledWithOffset;
    private ImageView iv_toBoard;
    private View layoutTitle;
    private LinearLayoutManager linearLayoutManager;
    private Map<Integer, RdsInfo> mRdsInfoMap;
    private boolean mTpEnable;
    private ProgressBar pb_list;
    private boolean ptyEnable;
    private String[] ptyStrs;
    private RadioListAdapter radioSearchChannelAdapter;
    private boolean rdsEnable;
    private RelativeLayout rl_search_fail;
    private RecyclerView rv;
    private boolean taValid;
    private TextView tvRadioName;
    private Handler mHanlder = new Handler(Looper.getMainLooper());
    private boolean pullUp = false;
    private final CarRadioManager.CarRadioControlCallback mCarRadioControlCallback = new CarRadioManager.CarRadioControlCallback() { // from class: com.yftech.libradio.RadioListActivity.5
        public void onAudioFocusChange(int i) {
        }

        public void onFavorChange(int i, DataOfRadio dataOfRadio) {
        }

        public void onFreqChanage(int i, DataOfRadio dataOfRadio, DataOfRadio dataOfRadio2) {
            LogUtil.d("onFreqChanage() newChannel:" + dataOfRadio.toString());
            RadioListActivity.this.getPresenter().getSearchList(RadioListActivity.this.band);
        }

        public void onFreqSignal(int i, DataOfRadio dataOfRadio, int i2) {
        }

        public void onPlayPause(boolean z) {
            LogUtil.d("onPlayPause() isPlaying:" + z);
            RadioListActivity.this.getPresenter().getSearchList(RadioListActivity.this.band);
        }

        public void onRdsCommonStatusChange(int i, boolean z) {
            super.onRdsCommonStatusChange(i, z);
            LogUtil.d("type: " + i + " , value: " + z);
            if (i == 0) {
                RadioListActivity.this.rdsEnable = z;
            } else if (i == 2) {
                RadioListActivity.this.afEnable = z;
            } else if (i == 7) {
                RadioListActivity.this.taValid = z;
            }
            RadioListActivity.this.mHanlder.post(new Runnable() { // from class: com.yftech.libradio.RadioListActivity.5.3
                @Override // java.lang.Runnable
                public void run() {
                    RadioListActivity.this.radioSearchChannelAdapter.notifyDataSetChanged();
                }
            });
        }

        public void onRdsCommonStringChange(int i, String str) {
            super.onRdsCommonStringChange(i, str);
            DataOfRadio freq = CarRadioManager.getInstance().getFreq();
            LogUtil.d("type: " + i + " , value: " + str + " , fre: " + freq.getFreq());
            if (i == 8) {
                RadioListActivity.this.currentPsString = str;
                if (RadioListActivity.this.mRdsInfoMap != null && !RadioListActivity.this.mRdsInfoMap.isEmpty()) {
                    for (RdsInfo rdsInfo : RadioListActivity.this.mRdsInfoMap.values()) {
                        if (rdsInfo.getFreq() == freq.getFreq()) {
                            rdsInfo.setPs(RadioListActivity.this.currentPsString);
                        }
                    }
                }
                RadioListActivity.this.mHanlder.post(new Runnable() { // from class: com.yftech.libradio.RadioListActivity.5.1
                    @Override // java.lang.Runnable
                    public void run() {
                        RadioListActivity.this.rv.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        }

        public void onRdsPtyStatusChange(int i) {
            super.onRdsPtyStatusChange(i);
            DataOfRadio freq = CarRadioManager.getInstance().getFreq();
            LogUtil.d("type: " + i + " ,fre: " + freq.getFreq());
            if (i < 0 || i >= RadioListActivity.this.ptyStrs.length) {
                return;
            }
            RadioListActivity.this.currentPtyStr = RadioListActivity.this.ptyStrs[i];
            if (RadioListActivity.this.mRdsInfoMap != null && !RadioListActivity.this.mRdsInfoMap.isEmpty()) {
                for (RdsInfo rdsInfo : RadioListActivity.this.mRdsInfoMap.values()) {
                    if (rdsInfo.getFreq() == freq.getFreq()) {
                        rdsInfo.setPty(i);
                    }
                }
            }
            RadioListActivity.this.mHanlder.post(new Runnable() { // from class: com.yftech.libradio.RadioListActivity.5.2
                @Override // java.lang.Runnable
                public void run() {
                    RadioListActivity.this.rv.getAdapter().notifyDataSetChanged();
                }
            });
        }

        public void onSeekStatusChange(int i, int i2, int i3) {
            LogUtil.d("onSeekStatusChange() scanType:" + i + " status:" + i2 + " operation:" + i3);
            if (i == 3 && i2 == 0 && i3 == 1) {
                RadioListActivity.this.getPresenter().getSearchList(RadioListActivity.this.band);
            }
        }
    };
    private RdsDataManager.RdsDataManagerCallback rdsDataManagerCallback = new RdsDataManager.RdsDataManagerCallback() { // from class: com.yftech.libradio.RadioListActivity.6
        @Override // com.yftech.libradio.rds.RdsDataManager.RdsDataManagerCallback
        public void onRdsInfoListChanged(Map<Integer, RdsInfo> map) {
            RadioListActivity.this.mRdsInfoMap = map;
            RadioListActivity.this.mHanlder.post(new Runnable() { // from class: com.yftech.libradio.RadioListActivity.6.1
                @Override // java.lang.Runnable
                public void run() {
                    RadioListActivity.this.radioSearchChannelAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private class RadioListAdapter extends BaseQuickAdapter<DataOfRadio, BaseViewHolder> {
        private int position;

        public RadioListAdapter() {
            super(R.layout.item_radio_list);
            this.position = -1;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Code duplicated, block: B:57:0x018f  */
        /* JADX WARN: Code duplicated, block: B:63:0x01a0  */
        /* JADX WARN: Code duplicated, block: B:66:0x01aa  */
        /* JADX WARN: Code duplicated, block: B:67:0x01b8  */
        /* JADX WARN: Code duplicated, block: B:70:0x01ce  */
        /* JADX WARN: Code duplicated, block: B:72:0x01da  */
        /* JADX WARN: Code duplicated, block: B:75:0x01f5  */
        /* JADX WARN: Code duplicated, block: B:77:0x0214  */
        @Override // com.chad.library.adapter.base.BaseQuickAdapter
        public void convert(BaseViewHolder baseViewHolder, DataOfRadio dataOfRadio) {
            Context context;
            int i;
            String ps;
            boolean zIsTpValid;
            boolean zIsAfValid;
            boolean z;
            baseViewHolder.setTextColor(R.id.tv_station_name, ContextCompat.getColor(this.mContext, this.position == baseViewHolder.getLayoutPosition() ? R.color.colorDD9D09 : R.color.colorFFFFFF)).setTextColor(R.id.tv_station_type, ContextCompat.getColor(this.mContext, this.position == baseViewHolder.getLayoutPosition() ? R.color.colorB3DD9D09 : R.color.colorB3FFFFFF)).setTextColor(R.id.tv_station_index, ContextCompat.getColor(this.mContext, this.position == baseViewHolder.getLayoutPosition() ? R.color.colorDD9D09 : R.color.colorFFFFFF)).setGone(R.id.iv_play, this.position == baseViewHolder.getLayoutPosition());
            baseViewHolder.setText(R.id.tv_station_index, String.valueOf(baseViewHolder.getLayoutPosition() + 1));
            AnimationDrawable animationDrawable = (AnimationDrawable) ((ImageView) baseViewHolder.getView(R.id.iv_play)).getBackground();
            if (this.position == baseViewHolder.getLayoutPosition()) {
                boolean zIsPlaying = CarRadioManager.getInstance().isPlaying();
                LogUtil.d("isPlaying:" + zIsPlaying);
                if (zIsPlaying) {
                    animationDrawable.start();
                } else {
                    animationDrawable.stop();
                }
            } else {
                animationDrawable.stop();
            }
            Locale locale = Locale.ENGLISH;
            Object[] objArr = new Object[3];
            objArr[0] = dataOfRadio.getType() == 2 ? "AM" : "FM";
            objArr[1] = Utils.getInstance().formatFreq(dataOfRadio.Freq, dataOfRadio.getType());
            if (dataOfRadio.getType() == 2) {
                context = this.mContext;
                i = R.string.radio_band_am;
            } else {
                context = this.mContext;
                i = R.string.radio_band_fm;
            }
            objArr[2] = context.getString(i);
            String str = String.format(locale, "%s %s %s", objArr);
            if (dataOfRadio.getType() != 0 || !RadioListActivity.this.rdsEnable) {
                baseViewHolder.setText(R.id.tv_station_name, str);
                baseViewHolder.setGone(R.id.layout_ta_tp_af, false);
                baseViewHolder.setGone(R.id.tv_station_type, false);
                return;
            }
            baseViewHolder.setGone(R.id.layout_ta_tp_af, true);
            baseViewHolder.setGone(R.id.tv_station_type, RadioListActivity.this.ptyEnable);
            String string = RadioListActivity.this.getString(R.string.radio_unknown);
            if (RadioListActivity.this.mRdsInfoMap != null && !RadioListActivity.this.mRdsInfoMap.isEmpty()) {
                RdsInfo rdsInfo = (RdsInfo) RadioListActivity.this.mRdsInfoMap.get(Integer.valueOf(dataOfRadio.getFreq()));
                if (rdsInfo != null) {
                    LogUtil.d("rdsInfo: " + rdsInfo);
                    zIsTpValid = rdsInfo.isTpValid();
                    zIsAfValid = rdsInfo.isAfValid();
                    ps = rdsInfo.getPs();
                    int pty = rdsInfo.getPty();
                    if (pty >= 0 && pty < RadioListActivity.this.ptyStrs.length) {
                        string = RadioListActivity.this.ptyStrs[pty];
                    }
                } else {
                    LogUtil.d("rdsInfo is null ----");
                }
                int i2 = R.id.iv_tp;
                if (zIsTpValid || !RadioListActivity.this.mTpEnable) {
                    z = false;
                } else {
                    z = true;
                }
                baseViewHolder.setGone(i2, z);
                baseViewHolder.setGone(R.id.iv_af, !zIsAfValid && RadioListActivity.this.afEnable);
                if (TextUtils.isEmpty(ps)) {
                    baseViewHolder.setText(R.id.tv_station_name, str);
                } else {
                    baseViewHolder.setText(R.id.tv_station_name, Utils.convertStationName(ps.trim()));
                }
                baseViewHolder.setText(R.id.tv_station_type, Utils.convertStationName(string));
                if (this.position == baseViewHolder.getLayoutPosition()) {
                    baseViewHolder.setGone(R.id.iv_ta, false);
                    return;
                }
                if (!TextUtils.isEmpty(RadioListActivity.this.currentPtyStr)) {
                    baseViewHolder.setText(R.id.tv_station_type, Utils.convertStationName(RadioListActivity.this.currentPtyStr));
                }
                if (!TextUtils.isEmpty(RadioListActivity.this.currentPsString)) {
                    baseViewHolder.setText(R.id.tv_station_name, Utils.convertStationName(RadioListActivity.this.currentPsString.trim()));
                }
                baseViewHolder.setGone(R.id.iv_ta, RadioListActivity.this.taValid);
            }
            LogUtil.d("rdsInfoMap is empty ----");
            ps = "";
            zIsTpValid = false;
            zIsAfValid = false;
            int i3 = R.id.iv_tp;
            if (zIsTpValid) {
                z = false;
            } else {
                z = false;
            }
            baseViewHolder.setGone(i3, z);
            baseViewHolder.setGone(R.id.iv_af, !zIsAfValid && RadioListActivity.this.afEnable);
            if (TextUtils.isEmpty(ps)) {
                baseViewHolder.setText(R.id.tv_station_name, Utils.convertStationName(ps.trim()));
            } else {
                baseViewHolder.setText(R.id.tv_station_name, str);
            }
            baseViewHolder.setText(R.id.tv_station_type, Utils.convertStationName(string));
            if (this.position == baseViewHolder.getLayoutPosition()) {
                baseViewHolder.setGone(R.id.iv_ta, false);
                return;
            }
            if (!TextUtils.isEmpty(RadioListActivity.this.currentPtyStr)) {
                baseViewHolder.setText(R.id.tv_station_type, Utils.convertStationName(RadioListActivity.this.currentPtyStr));
            }
            if (!TextUtils.isEmpty(RadioListActivity.this.currentPsString)) {
                baseViewHolder.setText(R.id.tv_station_name, Utils.convertStationName(RadioListActivity.this.currentPsString.trim()));
            }
            baseViewHolder.setGone(R.id.iv_ta, RadioListActivity.this.taValid);
        }

        public void setPlayingIndex(int i, boolean z) {
            LogUtil.d("setPlayingIndex() position:" + i + " isNotifyChanged:" + z);
            if (!z) {
                this.position = i;
            } else {
                this.position = i;
                notifyDataSetChanged();
            }
        }
    }

    private void initView() {
        findViewById(R.id.layout_radio_search).setOnClickListener(this);
        this.layoutTitle = findViewById(R.id.layout_title);
        this.rl_search_fail = (RelativeLayout) findViewById(R.id.layout_no_content);
        this.pb_list = (ProgressBar) findViewById(R.id.pb_list);
        this.rv = (RecyclerView) findViewById(R.id.rv_radio_list);
        this.linearLayoutManager = new LinearLayoutManager(this);
        this.rv.setLayoutManager(this.linearLayoutManager);
        this.radioSearchChannelAdapter = new RadioListAdapter();
        this.radioSearchChannelAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() { // from class: com.yftech.libradio.RadioListActivity.1
            @Override // com.chad.library.adapter.base.BaseQuickAdapter.OnItemClickListener
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                DataOfRadio item;
                if (RadioListActivity.this.radioSearchChannelAdapter == null || (item = RadioListActivity.this.radioSearchChannelAdapter.getItem(i)) == null) {
                    return;
                }
                CarRadioManager.getInstance().setFreq(item.getFreq(), i);
                RadioListActivity.this.radioSearchChannelAdapter.setPlayingIndex(i, true);
            }
        });
        this.rv.setAdapter(this.radioSearchChannelAdapter);
        this.tvRadioName = (TextView) findViewById(R.id.tv_station_name_num);
        this.iv_toBoard = (ImageView) findViewById(R.id.iv_toBoard);
        this.iv_toBoard.setOnClickListener(new View.OnClickListener() { // from class: com.yftech.libradio.RadioListActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (RadioListActivity.this.pullUp) {
                    RadioListActivity.this.rv.scrollToPosition(0);
                    return;
                }
                int itemCount = RadioListActivity.this.rv.getAdapter().getItemCount() - 1;
                if (itemCount >= 0) {
                    RadioListActivity.this.rv.scrollToPosition(itemCount);
                }
            }
        });
        this.rv.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.yftech.libradio.RadioListActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(@NonNull RecyclerView recyclerView, int i, int i2) {
                if (RadioListActivity.this.pullUp != (i2 < 0)) {
                    RadioListActivity.this.pullUp = i2 < 0;
                }
                RadioListActivity.this.pullUp = i2 < 0;
                int iFindFirstVisibleItemPosition = RadioListActivity.this.linearLayoutManager.findFirstVisibleItemPosition();
                int iFindLastVisibleItemPosition = RadioListActivity.this.linearLayoutManager.findLastVisibleItemPosition();
                if (iFindFirstVisibleItemPosition <= 7 || iFindLastVisibleItemPosition >= RadioListActivity.this.rv.getAdapter().getItemCount() - 1) {
                    RadioListActivity.this.setBoardVisible(false);
                } else if (RadioListActivity.this.pullUp) {
                    RadioListActivity.this.iv_toBoard.setImageResource(R.mipmap.radio_btn_reach_top);
                    RadioListActivity.this.setBoardVisible(true);
                } else {
                    RadioListActivity.this.iv_toBoard.setImageResource(R.mipmap.radio_btn_reach_bottom);
                    RadioListActivity.this.setBoardVisible(true);
                }
            }
        });
    }

    private void scrollToPlayingPosition(int i) {
        if (this.hasScrolledWithOffset) {
            ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPosition(i);
            return;
        }
        if (i == 0 || this.radioSearchChannelAdapter.getItemCount() - i <= 5) {
            ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPositionWithOffset(i, 0);
        } else if (i == 1) {
            ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPositionWithOffset(i - 1, 0);
        } else {
            ((LinearLayoutManager) this.rv.getLayoutManager()).scrollToPositionWithOffset(i - 2, 0);
        }
        this.hasScrolledWithOffset = true;
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
            this.iv_toBoard.animate().alpha(0.0f).setStartDelay(4000L).setDuration(1000L).setListener(new AnimatorListenerAdapter() { // from class: com.yftech.libradio.RadioListActivity.4
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    RadioListActivity.this.iv_toBoard.setVisibility(4);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity
    public RadioSearchChannelPresenter createPresenter() {
        return new RadioSearchChannelPresenter(this);
    }

    @Override // com.yftech.libradio.view.IRadioSearchChannelDialogView
    public void displaySearchList(int i, List<DataOfRadio> list) {
        this.pb_list.setVisibility(8);
        this.currentPsString = CarRadioManager.getInstance().getRdsPS();
        this.currentPtyStr = getString(R.string.radio_unknown);
        int rdsPty = CarRadioManager.getInstance().getRdsPty();
        LogUtil.d("pty: " + rdsPty + " ,psString: " + this.currentPsString);
        if (rdsPty >= 0 && rdsPty < this.ptyStrs.length) {
            this.currentPtyStr = this.ptyStrs[rdsPty];
        }
        if (list == null || list.isEmpty()) {
            if (this.rv.getAdapter().getItemCount() > 0) {
                return;
            }
            this.rl_search_fail.setVisibility(0);
            this.rv.setVisibility(4);
            this.layoutTitle.setVisibility(4);
            this.tvRadioName.setText(getString(R.string.station_name_with_count, String.valueOf(0)));
            return;
        }
        this.mRdsInfoMap = RdsDataManager.getInstance().getRdsInfoList();
        this.tvRadioName.setText(getString(R.string.station_name_with_count, String.valueOf(list.size())));
        this.rl_search_fail.setVisibility(8);
        this.rv.setVisibility(0);
        this.layoutTitle.setVisibility(0);
        if (this.radioSearchChannelAdapter != null) {
            this.radioSearchChannelAdapter.setPlayingIndex(i, false);
            this.radioSearchChannelAdapter.setNewData(list);
            if (i >= 0) {
                scrollToPlayingPosition(i);
            }
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (view.getId() == R.id.layout_radio_search) {
            if (Utils.isQuickClick()) {
                LogUtil.i("iv_search or tv_title quick click");
            } else {
                RadioSearchingDialog.newInstance(getString(R.string.radio_channel_search_dialog_search_scanning), this.band).show(getSupportFragmentManager(), RadioSearchingDialog.class.getSimpleName());
            }
        }
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_radio_list);
        this.band = getIntent().getIntExtra(ARG_PARAM1, 0);
        LogUtil.d("onCreate() band:" + this.band);
        this.ptyStrs = getResources().getStringArray(R.array.dab_ptys);
        initView();
        if (!Utils.isRdsPresent()) {
            this.rdsEnable = false;
            return;
        }
        this.rdsEnable = CarRadioManager.getInstance().isRdsCommonStatusEnable(0);
        this.mTpEnable = getSharedPreferences(Utils.SHARED_RADIO_DATA_NAME, 0).getBoolean(RdsSettingsDialog.SP_KEY_TP_SHOW, true);
        this.ptyEnable = getSharedPreferences(Utils.SHARED_RADIO_DATA_NAME, 0).getBoolean(RdsSettingsDialog.SP_KEY_PTY_SHOW, false);
        this.afEnable = CarRadioManager.getInstance().isRdsCommonStatusEnable(2);
        this.taValid = CarRadioManager.getInstance().isRdsCommonStatusEnable(7);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy()");
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.band = intent.getIntExtra(ARG_PARAM1, 0);
        LogUtil.d("band:" + this.band);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        LogUtil.d("onPause()");
        CarRadioManager.unregisterRadioControlCallback(this.mCarRadioControlCallback);
        RdsDataManager.getInstance().unregisterObservor(this.rdsDataManagerCallback);
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        LogUtil.d("onResume()");
        CarRadioManager.registerRadioControlCallback(this.mCarRadioControlCallback);
        RdsDataManager.getInstance().registerObservor(this.rdsDataManagerCallback);
        findViewById(R.id.tv_pty_title).setVisibility((this.ptyEnable && this.band == 0) ? 0 : 8);
        this.pb_list.setVisibility(0);
        getPresenter().getSearchList(this.band);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        LogUtil.d("onStop()");
    }
}
