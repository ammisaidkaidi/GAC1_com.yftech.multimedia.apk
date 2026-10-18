package com.yftech.libradio.dabui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.media.common.util.AppConstants;
import com.media.common.util.LogUtil;
import com.yftech.car.media.radio.dab.CarDabRadioManager;
import com.yftech.car.media.radio.dab.entity.DabPlayInfo;
import com.yftech.car.media.radio.dab.entity.DabScanStatus;
import com.yftech.libradio.DabDataManager;
import com.yftech.libradio.R;
import com.yftech.libradio.Utils;
import com.yftech.libradio.bean.DabChannelInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes4.dex */
public abstract class DabStationFilteredListFragment extends DabPlayerFragment {
    private static final String DAB_SP_NAME = "com.yf.dab.preferences";
    private DabCallback callback;
    private DabPlayInfo currentPlayInfo;
    private View emptyView;
    private ImageView iv_toBoard;
    protected SharedPreferences preferences;
    protected RecyclerView rvStationList;
    protected TextView tvLableName;
    private View viewLableSet;
    private boolean pullUp = false;
    protected Object mLock = new Object();
    protected List<DabChannelInfo> mDabChannelInfoList = new ArrayList();
    private List<DabChannelInfo> mFilterDabChannelInfoList = new ArrayList();
    private Handler mHanlder = new Handler(Looper.getMainLooper());
    private DabDataManager.DabDataManagerCallback dabDataManagerCallback = new DabDataManager.DabDataManagerCallback() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.1
        @Override // com.yftech.libradio.DabDataManager.DabDataManagerCallback
        public void onDataListChanged(List<DabChannelInfo> list) {
            if (list.isEmpty()) {
                LogUtil.d("refreshData,ChannelInfoList is empty ----");
                return;
            }
            DabStationFilteredListFragment.this.mDabChannelInfoList.clear();
            DabStationFilteredListFragment.this.mDabChannelInfoList.addAll(list);
            DabStationFilteredListFragment.this.filterDabChannleList();
        }

        @Override // com.yftech.libradio.DabDataManager.DabDataManagerCallback
        public void onSlideShowImageChanged(byte[] bArr) {
            if (bArr == null) {
                return;
            }
            for (DabChannelInfo dabChannelInfo : DabStationFilteredListFragment.this.mFilterDabChannelInfoList) {
                if (DabStationFilteredListFragment.this.currentPlayInfo != null && dabChannelInfo.getFrequency() == DabStationFilteredListFragment.this.currentPlayInfo.getFrequency() && dabChannelInfo.getServiceId() == DabStationFilteredListFragment.this.currentPlayInfo.getServiceId() && dabChannelInfo.getEnsembleId() == DabStationFilteredListFragment.this.currentPlayInfo.getEnsembleId()) {
                    dabChannelInfo.setImageData(bArr);
                    DabStationFilteredListFragment.this.mHanlder.post(new Runnable() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.1.1
                        @Override // java.lang.Runnable
                        public void run() {
                            DabStationFilteredListFragment.this.rvStationList.getAdapter().notifyDataSetChanged();
                        }
                    });
                    return;
                }
            }
        }
    };

    private class DabCallback extends CarDabRadioManager.CarDabRadioControlCallback {
        private DabCallback() {
        }

        public void onEnsembleListChange(int i) {
            super.onEnsembleListChange(i);
        }

        public void onPlayPause(boolean z) {
            super.onPlayPause(z);
        }

        public void onPlayStatusChange(DabPlayInfo dabPlayInfo) {
            super.onPlayStatusChange(dabPlayInfo);
            DabStationFilteredListFragment.this.updatePlayInfoView(dabPlayInfo);
        }

        public void onScanStatusChange(DabScanStatus dabScanStatus, DabScanStatus dabScanStatus2) {
            super.onScanStatusChange(dabScanStatus, dabScanStatus2);
            if (dabScanStatus == null || dabScanStatus.isRuning()) {
                return;
            }
            LogUtil.d("onScanStatusChange--------");
        }
    }

    private class FilteredStationAdapter extends RecyclerView.Adapter<FilteredStationViewHolder> {
        private FilteredStationAdapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return DabStationFilteredListFragment.this.mFilterDabChannelInfoList.size();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(@NonNull FilteredStationViewHolder filteredStationViewHolder, int i) {
            filteredStationViewHolder.position = i;
            DabChannelInfo dabChannelInfo = (DabChannelInfo) DabStationFilteredListFragment.this.mFilterDabChannelInfoList.get(i);
            filteredStationViewHolder.ivStationIcon.setTag(dabChannelInfo);
            if (DabStationFilteredListFragment.this.currentPlayInfo != null && DabStationFilteredListFragment.this.currentPlayInfo.getFrequency() == dabChannelInfo.getFrequency() && DabStationFilteredListFragment.this.currentPlayInfo.getServiceId() == dabChannelInfo.getServiceId() && DabStationFilteredListFragment.this.currentPlayInfo.getEnsembleId() == dabChannelInfo.getEnsembleId()) {
                filteredStationViewHolder.imgFrame.setVisibility(0);
                filteredStationViewHolder.playIcon.setVisibility(0);
                filteredStationViewHolder.tvStationName.setTextColor(DabStationFilteredListFragment.this.getContext().getColor(R.color.colorDD9D09));
                filteredStationViewHolder.tvStationName.setTypeface(Typeface.create(AppConstants.SourceHanSansCN_Medium, 0));
            } else {
                filteredStationViewHolder.imgFrame.setVisibility(8);
                filteredStationViewHolder.playIcon.setVisibility(8);
                filteredStationViewHolder.tvStationName.setTextColor(DabStationFilteredListFragment.this.getContext().getColor(R.color.colorB3FFFFFF));
                filteredStationViewHolder.tvStationName.setTypeface(Typeface.create("sourcehan-sans-regular", 0));
            }
            if (dabChannelInfo.getImageData() != null) {
                Utils.loadDabSliderShowDataNoCache(filteredStationViewHolder.ivStationIcon, DabStationFilteredListFragment.this.getResources().getDrawable(R.mipmap.pic_list_large_default, DabStationFilteredListFragment.this.getContext().getTheme()), 18, dabChannelInfo.getImageData());
            } else {
                filteredStationViewHolder.ivStationIcon.setImageResource(R.mipmap.pic_list_large_default);
            }
            filteredStationViewHolder.tvStationName.setText(Utils.convertStationName(Utils.getDabLableString(dabChannelInfo.getPropertieslabel(), DabStationFilteredListFragment.this.getString(R.string.radio_unknown))));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        @NonNull
        public FilteredStationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return DabStationFilteredListFragment.this.new FilteredStationViewHolder(LayoutInflater.from(DabStationFilteredListFragment.this.getContext()).inflate(R.layout.item_filtered_station_list, viewGroup, false));
        }
    }

    private class FilteredStationViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgFrame;
        private ImageView ivStationIcon;
        private ImageView playIcon;
        private int position;
        private TextView tvStationName;

        public FilteredStationViewHolder(View view) {
            super(view);
            this.imgFrame = (ImageView) view.findViewById(R.id.iv_filtered_list_frame);
            this.playIcon = (ImageView) view.findViewById(R.id.iv_filtered_list_play_status);
            this.tvStationName = (TextView) view.findViewById(R.id.tv_filtered_list_name);
            this.ivStationIcon = (ImageView) view.findViewById(R.id.iv_filtered_list_background);
            view.setOnClickListener(new View.OnClickListener() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.FilteredStationViewHolder.1
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    DabChannelInfo dabChannelInfo = (DabChannelInfo) DabStationFilteredListFragment.this.mFilterDabChannelInfoList.get(FilteredStationViewHolder.this.position);
                    if (dabChannelInfo == null || Utils.isQuickClick()) {
                        return;
                    }
                    CarDabRadioManager.getInstance().setFreq(dabChannelInfo.getFrequency(), dabChannelInfo.getEnsembleId(), dabChannelInfo.getServiceId());
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isRTL() {
        return Locale.getDefault().getLanguage().endsWith("ar");
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
            this.iv_toBoard.animate().alpha(0.0f).setStartDelay(4000L).setDuration(1000L).setListener(new AnimatorListenerAdapter() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.6
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    DabStationFilteredListFragment.this.iv_toBoard.setVisibility(4);
                }
            }).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePlayInfoView(DabPlayInfo dabPlayInfo) {
        if (dabPlayInfo == null) {
            Log.i("TAG", "updatePlayInfoView: dabPlayInfo null");
        } else {
            if (isSameDabPlayInfo(this.currentPlayInfo, dabPlayInfo)) {
                return;
            }
            this.currentPlayInfo = dabPlayInfo;
            this.mHanlder.post(new Runnable() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.8
                @Override // java.lang.Runnable
                public void run() {
                    for (int i = 0; i < DabStationFilteredListFragment.this.mFilterDabChannelInfoList.size(); i++) {
                        DabChannelInfo dabChannelInfo = (DabChannelInfo) DabStationFilteredListFragment.this.mFilterDabChannelInfoList.get(i);
                        if (DabStationFilteredListFragment.this.currentPlayInfo.getFrequency() == dabChannelInfo.getFrequency() && DabStationFilteredListFragment.this.currentPlayInfo.getServiceId() == dabChannelInfo.getServiceId() && DabStationFilteredListFragment.this.currentPlayInfo.getEnsembleId() == dabChannelInfo.getEnsembleId()) {
                            DabStationFilteredListFragment.this.rvStationList.getAdapter().notifyDataSetChanged();
                            DabStationFilteredListFragment.this.rvStationList.scrollToPosition(i);
                            if (TextUtils.isEmpty(DabStationFilteredListFragment.this.getFilterString())) {
                                DabStationFilteredListFragment.this.tvLableName.setText(dabChannelInfo.getEnsembleLabel().getLabelString());
                                return;
                            }
                            return;
                        }
                    }
                }
            });
        }
    }

    protected abstract void filterDabChannleList();

    protected DabChannelInfo getDabChannelInfo(DabPlayInfo dabPlayInfo) {
        if (dabPlayInfo == null) {
            return null;
        }
        for (int i = 0; i < this.mFilterDabChannelInfoList.size(); i++) {
            DabChannelInfo dabChannelInfo = this.mFilterDabChannelInfoList.get(i);
            if (dabPlayInfo.getFrequency() == dabChannelInfo.getFrequency() && dabPlayInfo.getServiceId() == dabChannelInfo.getServiceId() && dabPlayInfo.getEnsembleId() == dabChannelInfo.getEnsembleId()) {
                return dabChannelInfo;
            }
        }
        return null;
    }

    protected abstract String getFilterString();

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    protected void initView(View view) {
        this.rvStationList = (RecyclerView) view.findViewById(R.id.rv_station_list_list);
        this.viewLableSet = view.findViewById(R.id.layout_lable_set);
        this.tvLableName = (TextView) view.findViewById(R.id.tv_list_lable);
        this.emptyView = view.findViewById(R.id.layout_no_content);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 6, 1, false);
        this.rvStationList.setLayoutManager(gridLayoutManager);
        this.rvStationList.setAdapter(new FilteredStationAdapter());
        this.rvStationList.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.2
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(@NonNull Rect rect, @NonNull View view2, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.State state) {
                if (DabStationFilteredListFragment.this.isRTL()) {
                    rect.set(48, 0, 0, 34);
                } else {
                    rect.set(0, 0, 48, 34);
                }
            }
        });
        this.viewLableSet.setOnClickListener(new View.OnClickListener() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                DabStationFilteredListFragment.this.onLabelButtonCliked();
            }
        });
        this.iv_toBoard = (ImageView) view.findViewById(R.id.iv_toBoard);
        this.iv_toBoard.setOnClickListener(new View.OnClickListener() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view2) {
                if (DabStationFilteredListFragment.this.pullUp) {
                    DabStationFilteredListFragment.this.rvStationList.scrollToPosition(0);
                    return;
                }
                int itemCount = DabStationFilteredListFragment.this.rvStationList.getAdapter().getItemCount() - 1;
                if (itemCount >= 0) {
                    DabStationFilteredListFragment.this.rvStationList.scrollToPosition(itemCount);
                }
            }
        });
        this.rvStationList.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.5
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(@NonNull RecyclerView recyclerView, int i, int i2) {
                if (DabStationFilteredListFragment.this.pullUp != (i2 < 0)) {
                    DabStationFilteredListFragment.this.pullUp = i2 < 0;
                }
                DabStationFilteredListFragment.this.pullUp = i2 < 0;
                int iFindFirstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                int iFindLastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition();
                if (iFindFirstVisibleItemPosition <= 11 || iFindLastVisibleItemPosition >= DabStationFilteredListFragment.this.rvStationList.getAdapter().getItemCount() - 1) {
                    DabStationFilteredListFragment.this.setBoardVisible(false);
                } else if (DabStationFilteredListFragment.this.pullUp) {
                    DabStationFilteredListFragment.this.iv_toBoard.setImageResource(R.mipmap.radio_btn_reach_top);
                    DabStationFilteredListFragment.this.setBoardVisible(true);
                } else {
                    DabStationFilteredListFragment.this.iv_toBoard.setImageResource(R.mipmap.radio_btn_reach_bottom);
                    DabStationFilteredListFragment.this.setBoardVisible(true);
                }
            }
        });
    }

    protected boolean isSameDabPlayInfo(DabPlayInfo dabPlayInfo, DabPlayInfo dabPlayInfo2) {
        return dabPlayInfo != null && dabPlayInfo2 != null && dabPlayInfo != null && dabPlayInfo.getFrequency() == dabPlayInfo2.getFrequency() && dabPlayInfo.getEnsembleId() == dabPlayInfo2.getEnsembleId() && dabPlayInfo.getServiceId() == dabPlayInfo2.getServiceId();
    }

    @Override // com.yftech.libmvpmodule.mvp.BaseMvpFragment
    protected View onBindView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.fragment_dab_list_filtered_list, viewGroup, false);
    }

    @Override // com.yftech.libradio.dabui.DabPlayerFragment, com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        this.preferences = getContext().getSharedPreferences(DAB_SP_NAME, 0);
    }

    protected abstract void onLabelButtonCliked();

    @Override // com.yftech.libradio.dabui.DabPlayerFragment, com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        LogUtil.d("onPause------");
        CarDabRadioManager.getInstance().unregisterRadioControlCallback(this.callback);
        this.callback = null;
        DabDataManager.getInstance().unregisterObservor(this.dabDataManagerCallback);
    }

    @Override // com.yftech.libradio.dabui.DabPlayerFragment, com.yftech.libmvpmodule.mvp.BaseMvpFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        LogUtil.d("onResume------");
        if (!DabDataManager.getInstance().isInitialized()) {
            DabDataManager.getInstance().init();
        }
        String filterString = getFilterString();
        this.tvLableName.setText(filterString == null ? "" : Utils.convertStationName(filterString));
        List<DabChannelInfo> dabChannelInfoList = DabDataManager.getInstance().getDabChannelInfoList();
        if (dabChannelInfoList == null || dabChannelInfoList.isEmpty()) {
            this.rvStationList.setVisibility(8);
            this.emptyView.setVisibility(0);
        } else {
            synchronized (this.mLock) {
                this.mDabChannelInfoList.clear();
                this.mDabChannelInfoList.addAll(dabChannelInfoList);
            }
            filterDabChannleList();
        }
        DabDataManager.getInstance().registerObservor(this.dabDataManagerCallback);
        this.callback = new DabCallback();
        CarDabRadioManager.getInstance().registerRadioControlCallback(this.callback, this.mHanlder);
    }

    protected void updateLabelText(DabChannelInfo dabChannelInfo) {
    }

    protected void updateLayoutWithData(final List<DabChannelInfo> list) {
        this.mHanlder.post(new Runnable() { // from class: com.yftech.libradio.dabui.DabStationFilteredListFragment.7
            @Override // java.lang.Runnable
            public void run() {
                DabStationFilteredListFragment.this.mFilterDabChannelInfoList.clear();
                DabStationFilteredListFragment.this.mFilterDabChannelInfoList.addAll(list);
                if (DabStationFilteredListFragment.this.mFilterDabChannelInfoList.isEmpty()) {
                    DabStationFilteredListFragment.this.rvStationList.setVisibility(8);
                    DabStationFilteredListFragment.this.emptyView.setVisibility(0);
                    return;
                }
                DabPlayInfo playInfo = CarDabRadioManager.getInstance().getPlayInfo();
                DabStationFilteredListFragment.this.rvStationList.setVisibility(0);
                DabStationFilteredListFragment.this.emptyView.setVisibility(8);
                if (playInfo == null) {
                    DabStationFilteredListFragment.this.rvStationList.getAdapter().notifyDataSetChanged();
                    LogUtil.d("dabPlayInfo is null----");
                } else {
                    DabStationFilteredListFragment.this.rvStationList.getAdapter().notifyDataSetChanged();
                    DabStationFilteredListFragment.this.updatePlayInfoView(playInfo);
                }
            }
        });
    }
}
