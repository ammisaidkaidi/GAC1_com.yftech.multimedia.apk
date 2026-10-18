package com.yftech.libradio.model.interfaces;

import com.yftech.car.media.radio.entity.DataOfRadio;
import com.yftech.libmvpmodule.BasePresenter;
import com.yftech.libmvpmodule.mvp.IMvpView;
import com.yftech.libradio.bean.RadioChannelInfo;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface IRadioModel {

    public interface OnFreqListLoadListener {
        void onComplete(List<String> list, int i);
    }

    public interface OnRadioChannelScanResultLoadListener {
        void onComplete(boolean z);
    }

    public interface OnRadioChannelSearchListLoadListener {
        void onComplete(int i, List<DataOfRadio> list);
    }

    public interface OnRadioFavorListLoadListener {
        void onComplete(int i, List<RadioChannelInfo> list);
    }

    void getFavors(int i, BasePresenter<? extends IMvpView> basePresenter, OnRadioFavorListLoadListener onRadioFavorListLoadListener);

    void getSearchList(int i, BasePresenter<? extends IMvpView> basePresenter, OnRadioChannelSearchListLoadListener onRadioChannelSearchListLoadListener);

    void scanSave(int i, BasePresenter<? extends IMvpView> basePresenter, OnRadioChannelScanResultLoadListener onRadioChannelScanResultLoadListener);

    void setFreqList(int i, BasePresenter<? extends IMvpView> basePresenter, OnFreqListLoadListener onFreqListLoadListener);
}
