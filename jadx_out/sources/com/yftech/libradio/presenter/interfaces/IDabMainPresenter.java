package com.yftech.libradio.presenter.interfaces;

import com.yftech.car.media.radio.dab.entity.DabEnsemble;

/* JADX INFO: loaded from: classes4.dex */
public interface IDabMainPresenter {
    void pause();

    void play();

    void setFreq(DabEnsemble dabEnsemble, long j);

    void startAllScan();

    void stopAllScan();

    void stopSearch();

    void updateEnsembleList();
}
